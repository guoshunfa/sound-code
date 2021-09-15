package java.util.zip;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import sun.misc.JavaUtilZipFileAccess;
import sun.misc.PerfCounter;
import sun.misc.SharedSecrets;
import sun.misc.VM;

public class ZipFile implements ZipConstants, Closeable {
   private long jzfile;
   private final String name;
   private final int total;
   private final boolean locsig;
   private volatile boolean closeRequested;
   private static final int STORED = 0;
   private static final int DEFLATED = 8;
   public static final int OPEN_READ = 1;
   public static final int OPEN_DELETE = 4;
   private static final boolean usemmap;
   private static final boolean ensuretrailingslash;
   private ZipCoder zc;
   private final Map<InputStream, Inflater> streams;
   private Deque<Inflater> inflaterCache;
   private static final int JZENTRY_NAME = 0;
   private static final int JZENTRY_EXTRA = 1;
   private static final int JZENTRY_COMMENT = 2;

   private static native void initIDs();

   public ZipFile(String var1) throws IOException {
      this(new File(var1), 1);
   }

   public ZipFile(File var1, int var2) throws IOException {
      this(var1, var2, StandardCharsets.UTF_8);
   }

   public ZipFile(File var1) throws ZipException, IOException {
      this(var1, 1);
   }

   public ZipFile(File var1, int var2, Charset var3) throws IOException {
      this.closeRequested = false;
      this.streams = new WeakHashMap();
      this.inflaterCache = new ArrayDeque();
      if ((var2 & 1) != 0 && (var2 & -6) == 0) {
         String var4 = var1.getPath();
         SecurityManager var5 = System.getSecurityManager();
         if (var5 != null) {
            var5.checkRead(var4);
            if ((var2 & 4) != 0) {
               var5.checkDelete(var4);
            }
         }

         if (var3 == null) {
            throw new NullPointerException("charset is null");
         } else {
            this.zc = ZipCoder.get(var3);
            long var6 = System.nanoTime();
            this.jzfile = open(var4, var2, var1.lastModified(), usemmap);
            PerfCounter.getZipFileOpenTime().addElapsedTimeFrom(var6);
            PerfCounter.getZipFileCount().increment();
            this.name = var4;
            this.total = getTotal(this.jzfile);
            this.locsig = startsWithLOC(this.jzfile);
         }
      } else {
         throw new IllegalArgumentException("Illegal mode: 0x" + Integer.toHexString(var2));
      }
   }

   public ZipFile(String var1, Charset var2) throws IOException {
      this(new File(var1), 1, var2);
   }

   public ZipFile(File var1, Charset var2) throws IOException {
      this(var1, 1, var2);
   }

   public String getComment() {
      synchronized(this) {
         this.ensureOpen();
         byte[] var2 = getCommentBytes(this.jzfile);
         return var2 == null ? null : this.zc.toString(var2, var2.length);
      }
   }

   public ZipEntry getEntry(String var1) {
      if (var1 == null) {
         throw new NullPointerException("name");
      } else {
         long var2 = 0L;
         synchronized(this) {
            this.ensureOpen();
            var2 = getEntry(this.jzfile, this.zc.getBytes(var1), true);
            if (var2 != 0L) {
               ZipEntry var5 = ensuretrailingslash ? this.getZipEntry((String)null, var2) : this.getZipEntry(var1, var2);
               freeEntry(this.jzfile, var2);
               return var5;
            } else {
               return null;
            }
         }
      }
   }

   private static native long getEntry(long var0, byte[] var2, boolean var3);

   private static native void freeEntry(long var0, long var2);

   public InputStream getInputStream(ZipEntry var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("entry");
      } else {
         long var2 = 0L;
         ZipFile.ZipFileInputStream var4 = null;
         synchronized(this) {
            this.ensureOpen();
            if (!this.zc.isUTF8() && (var1.flag & 2048) != 0) {
               var2 = getEntry(this.jzfile, this.zc.getBytesUTF8(var1.name), false);
            } else {
               var2 = getEntry(this.jzfile, this.zc.getBytes(var1.name), false);
            }

            if (var2 == 0L) {
               return null;
            } else {
               var4 = new ZipFile.ZipFileInputStream(var2);
               switch(getEntryMethod(var2)) {
               case 0:
                  synchronized(this.streams) {
                     this.streams.put(var4, (Object)null);
                  }

                  return var4;
               case 8:
                  long var6 = getEntrySize(var2) + 2L;
                  if (var6 > 65536L) {
                     var6 = 8192L;
                  }

                  if (var6 <= 0L) {
                     var6 = 4096L;
                  }

                  Inflater var8 = this.getInflater();
                  ZipFile.ZipFileInflaterInputStream var9 = new ZipFile.ZipFileInflaterInputStream(var4, var8, (int)var6);
                  synchronized(this.streams) {
                     this.streams.put(var9, var8);
                  }

                  return var9;
               default:
                  throw new ZipException("invalid compression method");
               }
            }
         }
      }
   }

   private Inflater getInflater() {
      Inflater var1;
      synchronized(this.inflaterCache) {
         while(null != (var1 = (Inflater)this.inflaterCache.poll())) {
            if (!var1.ended()) {
               return var1;
            }
         }
      }

      return new Inflater(true);
   }

   private void releaseInflater(Inflater var1) {
      if (!var1.ended()) {
         var1.reset();
         synchronized(this.inflaterCache) {
            this.inflaterCache.add(var1);
         }
      }

   }

   public String getName() {
      return this.name;
   }

   public Enumeration<? extends ZipEntry> entries() {
      return new ZipFile.ZipEntryIterator();
   }

   public Stream<? extends ZipEntry> stream() {
      return StreamSupport.stream(Spliterators.spliterator((Iterator)(new ZipFile.ZipEntryIterator()), (long)this.size(), 1297), false);
   }

   private ZipEntry getZipEntry(String var1, long var2) {
      ZipEntry var4 = new ZipEntry();
      var4.flag = getEntryFlag(var2);
      byte[] var5;
      if (var1 != null) {
         var4.name = var1;
      } else {
         var5 = getEntryBytes(var2, 0);
         if (var5 == null) {
            var4.name = "";
         } else if (!this.zc.isUTF8() && (var4.flag & 2048) != 0) {
            var4.name = this.zc.toStringUTF8(var5, var5.length);
         } else {
            var4.name = this.zc.toString(var5, var5.length);
         }
      }

      var4.xdostime = getEntryTime(var2);
      var4.crc = getEntryCrc(var2);
      var4.size = getEntrySize(var2);
      var4.csize = getEntryCSize(var2);
      var4.method = getEntryMethod(var2);
      var4.setExtra0(getEntryBytes(var2, 1), false);
      var5 = getEntryBytes(var2, 2);
      if (var5 == null) {
         var4.comment = null;
      } else if (!this.zc.isUTF8() && (var4.flag & 2048) != 0) {
         var4.comment = this.zc.toStringUTF8(var5, var5.length);
      } else {
         var4.comment = this.zc.toString(var5, var5.length);
      }

      return var4;
   }

   private static native long getNextEntry(long var0, int var2);

   public int size() {
      this.ensureOpen();
      return this.total;
   }

   public void close() throws IOException {
      if (!this.closeRequested) {
         this.closeRequested = true;
         synchronized(this) {
            synchronized(this.streams) {
               if (!this.streams.isEmpty()) {
                  HashMap var3 = new HashMap(this.streams);
                  this.streams.clear();
                  Iterator var4 = var3.entrySet().iterator();

                  while(var4.hasNext()) {
                     Map.Entry var5 = (Map.Entry)var4.next();
                     ((InputStream)var5.getKey()).close();
                     Inflater var6 = (Inflater)var5.getValue();
                     if (var6 != null) {
                        var6.end();
                     }
                  }
               }
            }

            Inflater var2;
            synchronized(this.inflaterCache) {
               while(null != (var2 = (Inflater)this.inflaterCache.poll())) {
                  var2.end();
               }
            }

            if (this.jzfile != 0L) {
               long var13 = this.jzfile;
               this.jzfile = 0L;
               close(var13);
            }

         }
      }
   }

   protected void finalize() throws IOException {
      this.close();
   }

   private static native void close(long var0);

   private void ensureOpen() {
      if (this.closeRequested) {
         throw new IllegalStateException("zip file closed");
      } else if (this.jzfile == 0L) {
         throw new IllegalStateException("The object is not initialized.");
      }
   }

   private void ensureOpenOrZipException() throws IOException {
      if (this.closeRequested) {
         throw new ZipException("ZipFile closed");
      }
   }

   private boolean startsWithLocHeader() {
      return this.locsig;
   }

   private static native long open(String var0, int var1, long var2, boolean var4) throws IOException;

   private static native int getTotal(long var0);

   private static native boolean startsWithLOC(long var0);

   private static native int read(long var0, long var2, long var4, byte[] var6, int var7, int var8);

   private static native long getEntryTime(long var0);

   private static native long getEntryCrc(long var0);

   private static native long getEntryCSize(long var0);

   private static native long getEntrySize(long var0);

   private static native int getEntryMethod(long var0);

   private static native int getEntryFlag(long var0);

   private static native byte[] getCommentBytes(long var0);

   private static native byte[] getEntryBytes(long var0, int var2);

   private static native String getZipMessage(long var0);

   static {
      initIDs();
      String var0 = VM.getSavedProperty("sun.zip.disableMemoryMapping");
      usemmap = var0 == null || var0.length() != 0 && !var0.equalsIgnoreCase("true");
      var0 = VM.getSavedProperty("jdk.util.zip.ensureTrailingSlash");
      ensuretrailingslash = var0 == null || !var0.equalsIgnoreCase("false");
      SharedSecrets.setJavaUtilZipFileAccess(new JavaUtilZipFileAccess() {
         public boolean startsWithLocHeader(ZipFile var1) {
            return var1.startsWithLocHeader();
         }
      });
   }

   private class ZipFileInputStream extends InputStream {
      private volatile boolean zfisCloseRequested = false;
      protected long jzentry;
      private long pos = 0L;
      protected long rem;
      protected long size;

      ZipFileInputStream(long var2) {
         this.rem = ZipFile.getEntryCSize(var2);
         this.size = ZipFile.getEntrySize(var2);
         this.jzentry = var2;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         synchronized(ZipFile.this) {
            long var5 = this.rem;
            long var7 = this.pos;
            if (var5 == 0L) {
               return -1;
            }

            if (var3 <= 0) {
               return 0;
            }

            if ((long)var3 > var5) {
               var3 = (int)var5;
            }

            ZipFile.this.ensureOpenOrZipException();
            var3 = ZipFile.read(ZipFile.this.jzfile, this.jzentry, var7, var1, var2, var3);
            if (var3 > 0) {
               this.pos = var7 + (long)var3;
               this.rem = var5 - (long)var3;
            }
         }

         if (this.rem == 0L) {
            this.close();
         }

         return var3;
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         return this.read(var1, 0, 1) == 1 ? var1[0] & 255 : -1;
      }

      public long skip(long var1) {
         if (var1 > this.rem) {
            var1 = this.rem;
         }

         this.pos += var1;
         this.rem -= var1;
         if (this.rem == 0L) {
            this.close();
         }

         return var1;
      }

      public int available() {
         return this.rem > 2147483647L ? Integer.MAX_VALUE : (int)this.rem;
      }

      public long size() {
         return this.size;
      }

      public void close() {
         if (!this.zfisCloseRequested) {
            this.zfisCloseRequested = true;
            this.rem = 0L;
            synchronized(ZipFile.this) {
               if (this.jzentry != 0L && ZipFile.this.jzfile != 0L) {
                  ZipFile.freeEntry(ZipFile.this.jzfile, this.jzentry);
                  this.jzentry = 0L;
               }
            }

            synchronized(ZipFile.this.streams) {
               ZipFile.this.streams.remove(this);
            }
         }
      }

      protected void finalize() {
         this.close();
      }
   }

   private class ZipEntryIterator implements Enumeration<ZipEntry>, Iterator<ZipEntry> {
      private int i = 0;

      public ZipEntryIterator() {
         ZipFile.this.ensureOpen();
      }

      public boolean hasMoreElements() {
         return this.hasNext();
      }

      public boolean hasNext() {
         synchronized(ZipFile.this) {
            ZipFile.this.ensureOpen();
            return this.i < ZipFile.this.total;
         }
      }

      public ZipEntry nextElement() {
         return this.next();
      }

      public ZipEntry next() {
         synchronized(ZipFile.this) {
            ZipFile.this.ensureOpen();
            if (this.i >= ZipFile.this.total) {
               throw new NoSuchElementException();
            } else {
               long var2 = ZipFile.getNextEntry(ZipFile.this.jzfile, this.i++);
               if (var2 == 0L) {
                  String var7;
                  if (ZipFile.this.closeRequested) {
                     var7 = "ZipFile concurrently closed";
                  } else {
                     var7 = ZipFile.getZipMessage(ZipFile.this.jzfile);
                  }

                  throw new ZipError("jzentry == 0,\n jzfile = " + ZipFile.this.jzfile + ",\n total = " + ZipFile.this.total + ",\n name = " + ZipFile.this.name + ",\n i = " + this.i + ",\n message = " + var7);
               } else {
                  ZipEntry var4 = ZipFile.this.getZipEntry((String)null, var2);
                  ZipFile.freeEntry(ZipFile.this.jzfile, var2);
                  return var4;
               }
            }
         }
      }
   }

   private class ZipFileInflaterInputStream extends InflaterInputStream {
      private volatile boolean closeRequested = false;
      private boolean eof = false;
      private final ZipFile.ZipFileInputStream zfin;

      ZipFileInflaterInputStream(ZipFile.ZipFileInputStream var2, Inflater var3, int var4) {
         super(var2, var3, var4);
         this.zfin = var2;
      }

      public void close() throws IOException {
         if (!this.closeRequested) {
            this.closeRequested = true;
            super.close();
            Inflater var1;
            synchronized(ZipFile.this.streams) {
               var1 = (Inflater)ZipFile.this.streams.remove(this);
            }

            if (var1 != null) {
               ZipFile.this.releaseInflater(var1);
            }

         }
      }

      protected void fill() throws IOException {
         if (this.eof) {
            throw new EOFException("Unexpected end of ZLIB input stream");
         } else {
            this.len = this.in.read(this.buf, 0, this.buf.length);
            if (this.len == -1) {
               this.buf[0] = 0;
               this.len = 1;
               this.eof = true;
            }

            this.inf.setInput(this.buf, 0, this.len);
         }
      }

      public int available() throws IOException {
         if (this.closeRequested) {
            return 0;
         } else {
            long var1 = this.zfin.size() - this.inf.getBytesWritten();
            return var1 > 2147483647L ? Integer.MAX_VALUE : (int)var1;
         }
      }

      protected void finalize() throws Throwable {
         this.close();
      }
   }
}
