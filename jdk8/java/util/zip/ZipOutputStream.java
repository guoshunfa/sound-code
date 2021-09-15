package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import sun.security.action.GetPropertyAction;

public class ZipOutputStream extends DeflaterOutputStream implements ZipConstants {
   private static final boolean inhibitZip64 = Boolean.parseBoolean((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jdk.util.zip.inhibitZip64", "false"))));
   private ZipOutputStream.XEntry current;
   private Vector<ZipOutputStream.XEntry> xentries;
   private HashSet<String> names;
   private CRC32 crc;
   private long written;
   private long locoff;
   private byte[] comment;
   private int method;
   private boolean finished;
   private boolean closed;
   private final ZipCoder zc;
   public static final int STORED = 0;
   public static final int DEFLATED = 8;

   private static int version(ZipEntry var0) throws ZipException {
      switch(var0.method) {
      case 0:
         return 10;
      case 8:
         return 20;
      default:
         throw new ZipException("unsupported compression method");
      }
   }

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }

   public ZipOutputStream(OutputStream var1) {
      this(var1, StandardCharsets.UTF_8);
   }

   public ZipOutputStream(OutputStream var1, Charset var2) {
      super(var1, new Deflater(-1, true));
      this.xentries = new Vector();
      this.names = new HashSet();
      this.crc = new CRC32();
      this.written = 0L;
      this.locoff = 0L;
      this.method = 8;
      this.closed = false;
      if (var2 == null) {
         throw new NullPointerException("charset is null");
      } else {
         this.zc = ZipCoder.get(var2);
         this.usesDefaultDeflater = true;
      }
   }

   public void setComment(String var1) {
      if (var1 != null) {
         this.comment = this.zc.getBytes(var1);
         if (this.comment.length > 65535) {
            throw new IllegalArgumentException("ZIP file comment too long.");
         }
      }

   }

   public void setMethod(int var1) {
      if (var1 != 8 && var1 != 0) {
         throw new IllegalArgumentException("invalid compression method");
      } else {
         this.method = var1;
      }
   }

   public void setLevel(int var1) {
      this.def.setLevel(var1);
   }

   public void putNextEntry(ZipEntry var1) throws IOException {
      this.ensureOpen();
      if (this.current != null) {
         this.closeEntry();
      }

      if (var1.xdostime == -1L) {
         var1.setTime(System.currentTimeMillis());
      }

      if (var1.method == -1) {
         var1.method = this.method;
      }

      var1.flag = 0;
      switch(var1.method) {
      case 0:
         if (var1.size == -1L) {
            var1.size = var1.csize;
         } else if (var1.csize == -1L) {
            var1.csize = var1.size;
         } else if (var1.size != var1.csize) {
            throw new ZipException("STORED entry where compressed != uncompressed size");
         }

         if (var1.size != -1L && var1.crc != -1L) {
            break;
         }

         throw new ZipException("STORED entry missing size, compressed size, or crc-32");
      case 8:
         if (var1.size == -1L || var1.csize == -1L || var1.crc == -1L) {
            var1.flag = 8;
         }
         break;
      default:
         throw new ZipException("unsupported compression method");
      }

      if (!this.names.add(var1.name)) {
         throw new ZipException("duplicate entry: " + var1.name);
      } else {
         if (this.zc.isUTF8()) {
            var1.flag |= 2048;
         }

         this.current = new ZipOutputStream.XEntry(var1, this.written);
         this.xentries.add(this.current);
         this.writeLOC(this.current);
      }
   }

   public void closeEntry() throws IOException {
      this.ensureOpen();
      if (this.current != null) {
         ZipEntry var1 = this.current.entry;
         switch(var1.method) {
         case 0:
            if (var1.size != this.written - this.locoff) {
               throw new ZipException("invalid entry size (expected " + var1.size + " but got " + (this.written - this.locoff) + " bytes)");
            }

            if (var1.crc != this.crc.getValue()) {
               throw new ZipException("invalid entry crc-32 (expected 0x" + Long.toHexString(var1.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")");
            }
            break;
         case 8:
            this.def.finish();

            while(!this.def.finished()) {
               this.deflate();
            }

            if ((var1.flag & 8) == 0) {
               if (var1.size != this.def.getBytesRead()) {
                  throw new ZipException("invalid entry size (expected " + var1.size + " but got " + this.def.getBytesRead() + " bytes)");
               }

               if (var1.csize != this.def.getBytesWritten()) {
                  throw new ZipException("invalid entry compressed size (expected " + var1.csize + " but got " + this.def.getBytesWritten() + " bytes)");
               }

               if (var1.crc != this.crc.getValue()) {
                  throw new ZipException("invalid entry CRC-32 (expected 0x" + Long.toHexString(var1.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")");
               }
            } else {
               var1.size = this.def.getBytesRead();
               var1.csize = this.def.getBytesWritten();
               var1.crc = this.crc.getValue();
               this.writeEXT(var1);
            }

            this.def.reset();
            this.written += var1.csize;
            break;
         default:
            throw new ZipException("invalid compression method");
         }

         this.crc.reset();
         this.current = null;
      }

   }

   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         if (var3 != 0) {
            if (this.current == null) {
               throw new ZipException("no current ZIP entry");
            } else {
               ZipEntry var4 = this.current.entry;
               switch(var4.method) {
               case 0:
                  this.written += (long)var3;
                  if (this.written - this.locoff > var4.size) {
                     throw new ZipException("attempt to write past end of STORED entry");
                  }

                  this.out.write(var1, var2, var3);
                  break;
               case 8:
                  super.write(var1, var2, var3);
                  break;
               default:
                  throw new ZipException("invalid compression method");
               }

               this.crc.update(var1, var2, var3);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void finish() throws IOException {
      this.ensureOpen();
      if (!this.finished) {
         if (this.current != null) {
            this.closeEntry();
         }

         long var1 = this.written;
         Iterator var3 = this.xentries.iterator();

         while(var3.hasNext()) {
            ZipOutputStream.XEntry var4 = (ZipOutputStream.XEntry)var3.next();
            this.writeCEN(var4);
         }

         this.writeEND(var1, this.written - var1);
         this.finished = true;
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         super.close();
         this.closed = true;
      }

   }

   private void writeLOC(ZipOutputStream.XEntry var1) throws IOException {
      ZipEntry var2 = var1.entry;
      int var3 = var2.flag;
      boolean var4 = false;
      int var5 = this.getExtraLen(var2.extra);
      this.writeInt(67324752L);
      if ((var3 & 8) == 8) {
         this.writeShort(version(var2));
         this.writeShort(var3);
         this.writeShort(var2.method);
         this.writeInt(var2.xdostime);
         this.writeInt(0L);
         this.writeInt(0L);
         this.writeInt(0L);
      } else {
         if (var2.csize < 4294967295L && var2.size < 4294967295L) {
            this.writeShort(version(var2));
         } else {
            var4 = true;
            this.writeShort(45);
         }

         this.writeShort(var3);
         this.writeShort(var2.method);
         this.writeInt(var2.xdostime);
         this.writeInt(var2.crc);
         if (var4) {
            this.writeInt(4294967295L);
            this.writeInt(4294967295L);
            var5 += 20;
         } else {
            this.writeInt(var2.csize);
            this.writeInt(var2.size);
         }
      }

      byte[] var6 = this.zc.getBytes(var2.name);
      this.writeShort(var6.length);
      int var7 = 0;
      int var8 = 0;
      if (var2.mtime != null) {
         var7 += 4;
         var8 |= 1;
      }

      if (var2.atime != null) {
         var7 += 4;
         var8 |= 2;
      }

      if (var2.ctime != null) {
         var7 += 4;
         var8 |= 4;
      }

      if (var8 != 0) {
         var5 += var7 + 5;
      }

      this.writeShort(var5);
      this.writeBytes(var6, 0, var6.length);
      if (var4) {
         this.writeShort(1);
         this.writeShort(16);
         this.writeLong(var2.size);
         this.writeLong(var2.csize);
      }

      if (var8 != 0) {
         this.writeShort(21589);
         this.writeShort(var7 + 1);
         this.writeByte(var8);
         if (var2.mtime != null) {
            this.writeInt(ZipUtils.fileTimeToUnixTime(var2.mtime));
         }

         if (var2.atime != null) {
            this.writeInt(ZipUtils.fileTimeToUnixTime(var2.atime));
         }

         if (var2.ctime != null) {
            this.writeInt(ZipUtils.fileTimeToUnixTime(var2.ctime));
         }
      }

      this.writeExtra(var2.extra);
      this.locoff = this.written;
   }

   private void writeEXT(ZipEntry var1) throws IOException {
      this.writeInt(134695760L);
      this.writeInt(var1.crc);
      if (var1.csize < 4294967295L && var1.size < 4294967295L) {
         this.writeInt(var1.csize);
         this.writeInt(var1.size);
      } else {
         this.writeLong(var1.csize);
         this.writeLong(var1.size);
      }

   }

   private void writeCEN(ZipOutputStream.XEntry var1) throws IOException {
      ZipEntry var2 = var1.entry;
      int var3 = var2.flag;
      int var4 = version(var2);
      long var5 = var2.csize;
      long var7 = var2.size;
      long var9 = var1.offset;
      int var11 = 0;
      boolean var12 = false;
      if (var2.csize >= 4294967295L) {
         var5 = 4294967295L;
         var11 += 8;
         var12 = true;
      }

      if (var2.size >= 4294967295L) {
         var7 = 4294967295L;
         var11 += 8;
         var12 = true;
      }

      if (var1.offset >= 4294967295L) {
         var9 = 4294967295L;
         var11 += 8;
         var12 = true;
      }

      this.writeInt(33639248L);
      if (var12) {
         this.writeShort(45);
         this.writeShort(45);
      } else {
         this.writeShort(var4);
         this.writeShort(var4);
      }

      this.writeShort(var3);
      this.writeShort(var2.method);
      this.writeInt(var2.xdostime);
      this.writeInt(var2.crc);
      this.writeInt(var5);
      this.writeInt(var7);
      byte[] var13 = this.zc.getBytes(var2.name);
      this.writeShort(var13.length);
      int var14 = this.getExtraLen(var2.extra);
      if (var12) {
         var14 += var11 + 4;
      }

      int var15 = 0;
      if (var2.mtime != null) {
         var14 += 4;
         var15 |= 1;
      }

      if (var2.atime != null) {
         var15 |= 2;
      }

      if (var2.ctime != null) {
         var15 |= 4;
      }

      if (var15 != 0) {
         var14 += 5;
      }

      this.writeShort(var14);
      byte[] var16;
      if (var2.comment != null) {
         var16 = this.zc.getBytes(var2.comment);
         this.writeShort(Math.min(var16.length, 65535));
      } else {
         var16 = null;
         this.writeShort(0);
      }

      this.writeShort(0);
      this.writeShort(0);
      this.writeInt(0L);
      this.writeInt(var9);
      this.writeBytes(var13, 0, var13.length);
      if (var12) {
         this.writeShort(1);
         this.writeShort(var11);
         if (var7 == 4294967295L) {
            this.writeLong(var2.size);
         }

         if (var5 == 4294967295L) {
            this.writeLong(var2.csize);
         }

         if (var9 == 4294967295L) {
            this.writeLong(var1.offset);
         }
      }

      if (var15 != 0) {
         this.writeShort(21589);
         if (var2.mtime != null) {
            this.writeShort(5);
            this.writeByte(var15);
            this.writeInt(ZipUtils.fileTimeToUnixTime(var2.mtime));
         } else {
            this.writeShort(1);
            this.writeByte(var15);
         }
      }

      this.writeExtra(var2.extra);
      if (var16 != null) {
         this.writeBytes(var16, 0, Math.min(var16.length, 65535));
      }

   }

   private void writeEND(long var1, long var3) throws IOException {
      boolean var5 = false;
      long var6 = var3;
      long var8 = var1;
      if (var3 >= 4294967295L) {
         var6 = 4294967295L;
         var5 = true;
      }

      if (var1 >= 4294967295L) {
         var8 = 4294967295L;
         var5 = true;
      }

      int var10 = this.xentries.size();
      if (var10 >= 65535) {
         var5 |= !inhibitZip64;
         if (var5) {
            var10 = 65535;
         }
      }

      if (var5) {
         long var11 = this.written;
         this.writeInt(101075792L);
         this.writeLong(44L);
         this.writeShort(45);
         this.writeShort(45);
         this.writeInt(0L);
         this.writeInt(0L);
         this.writeLong((long)this.xentries.size());
         this.writeLong((long)this.xentries.size());
         this.writeLong(var3);
         this.writeLong(var1);
         this.writeInt(117853008L);
         this.writeInt(0L);
         this.writeLong(var11);
         this.writeInt(1L);
      }

      this.writeInt(101010256L);
      this.writeShort(0);
      this.writeShort(0);
      this.writeShort(var10);
      this.writeShort(var10);
      this.writeInt(var6);
      this.writeInt(var8);
      if (this.comment != null) {
         this.writeShort(this.comment.length);
         this.writeBytes(this.comment, 0, this.comment.length);
      } else {
         this.writeShort(0);
      }

   }

   private int getExtraLen(byte[] var1) {
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;
         int var3 = var1.length;

         int var6;
         for(int var4 = 0; var4 + 4 <= var3; var4 += var6 + 4) {
            int var5 = ZipUtils.get16(var1, var4);
            var6 = ZipUtils.get16(var1, var4 + 2);
            if (var6 < 0 || var4 + 4 + var6 > var3) {
               break;
            }

            if (var5 == 21589 || var5 == 1) {
               var2 += var6 + 4;
            }
         }

         return var3 - var2;
      }
   }

   private void writeExtra(byte[] var1) throws IOException {
      if (var1 != null) {
         int var2 = var1.length;
         int var3 = 0;

         while(true) {
            if (var3 + 4 > var2) {
               if (var3 < var2) {
                  this.writeBytes(var1, var3, var2 - var3);
               }
               break;
            }

            int var4 = ZipUtils.get16(var1, var3);
            int var5 = ZipUtils.get16(var1, var3 + 2);
            if (var5 < 0 || var3 + 4 + var5 > var2) {
               this.writeBytes(var1, var3, var2 - var3);
               return;
            }

            if (var4 != 21589 && var4 != 1) {
               this.writeBytes(var1, var3, var5 + 4);
            }

            var3 += var5 + 4;
         }
      }

   }

   private void writeByte(int var1) throws IOException {
      OutputStream var2 = this.out;
      var2.write(var1 & 255);
      ++this.written;
   }

   private void writeShort(int var1) throws IOException {
      OutputStream var2 = this.out;
      var2.write(var1 >>> 0 & 255);
      var2.write(var1 >>> 8 & 255);
      this.written += 2L;
   }

   private void writeInt(long var1) throws IOException {
      OutputStream var3 = this.out;
      var3.write((int)(var1 >>> 0 & 255L));
      var3.write((int)(var1 >>> 8 & 255L));
      var3.write((int)(var1 >>> 16 & 255L));
      var3.write((int)(var1 >>> 24 & 255L));
      this.written += 4L;
   }

   private void writeLong(long var1) throws IOException {
      OutputStream var3 = this.out;
      var3.write((int)(var1 >>> 0 & 255L));
      var3.write((int)(var1 >>> 8 & 255L));
      var3.write((int)(var1 >>> 16 & 255L));
      var3.write((int)(var1 >>> 24 & 255L));
      var3.write((int)(var1 >>> 32 & 255L));
      var3.write((int)(var1 >>> 40 & 255L));
      var3.write((int)(var1 >>> 48 & 255L));
      var3.write((int)(var1 >>> 56 & 255L));
      this.written += 8L;
   }

   private void writeBytes(byte[] var1, int var2, int var3) throws IOException {
      super.out.write(var1, var2, var3);
      this.written += (long)var3;
   }

   private static class XEntry {
      final ZipEntry entry;
      final long offset;

      public XEntry(ZipEntry var1, long var2) {
         this.entry = var1;
         this.offset = var2;
      }
   }
}
