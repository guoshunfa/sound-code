package javax.imageio.stream;

import com.sun.imageio.stream.StreamCloser;
import com.sun.imageio.stream.StreamFinalizer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class FileCacheImageInputStream extends ImageInputStreamImpl {
   private InputStream stream;
   private File cacheFile;
   private RandomAccessFile cache;
   private static final int BUFFER_LENGTH = 1024;
   private byte[] buf = new byte[1024];
   private long length = 0L;
   private boolean foundEOF = false;
   private final Object disposerReferent;
   private final DisposerRecord disposerRecord;
   private final StreamCloser.CloseAction closeAction;

   public FileCacheImageInputStream(InputStream var1, File var2) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("stream == null!");
      } else if (var2 != null && !var2.isDirectory()) {
         throw new IllegalArgumentException("Not a directory!");
      } else {
         this.stream = var1;
         if (var2 == null) {
            this.cacheFile = Files.createTempFile("imageio", ".tmp").toFile();
         } else {
            this.cacheFile = Files.createTempFile(var2.toPath(), "imageio", ".tmp").toFile();
         }

         this.cache = new RandomAccessFile(this.cacheFile, "rw");
         this.closeAction = StreamCloser.createCloseAction(this);
         StreamCloser.addToQueue(this.closeAction);
         this.disposerRecord = new FileCacheImageInputStream.StreamDisposerRecord(this.cacheFile, this.cache);
         if (this.getClass() == FileCacheImageInputStream.class) {
            this.disposerReferent = new Object();
            Disposer.addRecord(this.disposerReferent, this.disposerRecord);
         } else {
            this.disposerReferent = new StreamFinalizer(this);
         }

      }
   }

   private long readUntil(long var1) throws IOException {
      if (var1 < this.length) {
         return var1;
      } else if (this.foundEOF) {
         return this.length;
      } else {
         long var3 = var1 - this.length;
         this.cache.seek(this.length);

         while(var3 > 0L) {
            int var5 = this.stream.read(this.buf, 0, (int)Math.min(var3, 1024L));
            if (var5 == -1) {
               this.foundEOF = true;
               return this.length;
            }

            this.cache.write(this.buf, 0, var5);
            var3 -= (long)var5;
            this.length += (long)var5;
         }

         return var1;
      }
   }

   public int read() throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      long var1 = this.streamPos + 1L;
      long var3 = this.readUntil(var1);
      if (var3 >= var1) {
         this.cache.seek((long)(this.streamPos++));
         return this.cache.read();
      } else {
         return -1;
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.checkClosed();
      if (var1 == null) {
         throw new NullPointerException("b == null!");
      } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         this.bitOffset = 0;
         if (var3 == 0) {
            return 0;
         } else {
            long var4 = this.readUntil(this.streamPos + (long)var3);
            var3 = (int)Math.min((long)var3, var4 - this.streamPos);
            if (var3 > 0) {
               this.cache.seek(this.streamPos);
               this.cache.readFully(var1, var2, var3);
               this.streamPos += (long)var3;
               return var3;
            } else {
               return -1;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off+len > b.length || off+len < 0!");
      }
   }

   public boolean isCached() {
      return true;
   }

   public boolean isCachedFile() {
      return true;
   }

   public boolean isCachedMemory() {
      return false;
   }

   public void close() throws IOException {
      super.close();
      this.disposerRecord.dispose();
      this.stream = null;
      this.cache = null;
      this.cacheFile = null;
      StreamCloser.removeFromQueue(this.closeAction);
   }

   protected void finalize() throws Throwable {
   }

   private static class StreamDisposerRecord implements DisposerRecord {
      private File cacheFile;
      private RandomAccessFile cache;

      public StreamDisposerRecord(File var1, RandomAccessFile var2) {
         this.cacheFile = var1;
         this.cache = var2;
      }

      public synchronized void dispose() {
         if (this.cache != null) {
            try {
               this.cache.close();
            } catch (IOException var5) {
            } finally {
               this.cache = null;
            }
         }

         if (this.cacheFile != null) {
            this.cacheFile.delete();
            this.cacheFile = null;
         }

      }
   }
}
