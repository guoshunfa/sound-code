package java.util.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ZipInputStream extends InflaterInputStream implements ZipConstants {
   private ZipEntry entry;
   private int flag;
   private CRC32 crc;
   private long remaining;
   private byte[] tmpbuf;
   private static final int STORED = 0;
   private static final int DEFLATED = 8;
   private boolean closed;
   private boolean entryEOF;
   private ZipCoder zc;
   private byte[] b;

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }

   public ZipInputStream(InputStream var1) {
      this(var1, StandardCharsets.UTF_8);
   }

   public ZipInputStream(InputStream var1, Charset var2) {
      super(new PushbackInputStream(var1, 512), new Inflater(true), 512);
      this.crc = new CRC32();
      this.tmpbuf = new byte[512];
      this.closed = false;
      this.entryEOF = false;
      this.b = new byte[256];
      this.usesDefaultInflater = true;
      if (var1 == null) {
         throw new NullPointerException("in is null");
      } else if (var2 == null) {
         throw new NullPointerException("charset is null");
      } else {
         this.zc = ZipCoder.get(var2);
      }
   }

   public ZipEntry getNextEntry() throws IOException {
      this.ensureOpen();
      if (this.entry != null) {
         this.closeEntry();
      }

      this.crc.reset();
      this.inf.reset();
      if ((this.entry = this.readLOC()) == null) {
         return null;
      } else {
         if (this.entry.method == 0) {
            this.remaining = this.entry.size;
         }

         this.entryEOF = false;
         return this.entry;
      }
   }

   public void closeEntry() throws IOException {
      this.ensureOpen();

      while(this.read(this.tmpbuf, 0, this.tmpbuf.length) != -1) {
      }

      this.entryEOF = true;
   }

   public int available() throws IOException {
      this.ensureOpen();
      return this.entryEOF ? 0 : 1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         if (var3 == 0) {
            return 0;
         } else if (this.entry == null) {
            return -1;
         } else {
            switch(this.entry.method) {
            case 0:
               if (this.remaining <= 0L) {
                  this.entryEOF = true;
                  this.entry = null;
                  return -1;
               } else {
                  if ((long)var3 > this.remaining) {
                     var3 = (int)this.remaining;
                  }

                  var3 = this.in.read(var1, var2, var3);
                  if (var3 == -1) {
                     throw new ZipException("unexpected EOF");
                  } else {
                     this.crc.update(var1, var2, var3);
                     this.remaining -= (long)var3;
                     if (this.remaining == 0L && this.entry.crc != this.crc.getValue()) {
                        throw new ZipException("invalid entry CRC (expected 0x" + Long.toHexString(this.entry.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")");
                     }

                     return var3;
                  }
               }
            case 8:
               var3 = super.read(var1, var2, var3);
               if (var3 == -1) {
                  this.readEnd(this.entry);
                  this.entryEOF = true;
                  this.entry = null;
               } else {
                  this.crc.update(var1, var2, var3);
               }

               return var3;
            default:
               throw new ZipException("invalid compression method");
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("negative skip length");
      } else {
         this.ensureOpen();
         int var3 = (int)Math.min(var1, 2147483647L);

         int var4;
         int var5;
         for(var4 = 0; var4 < var3; var4 += var5) {
            var5 = var3 - var4;
            if (var5 > this.tmpbuf.length) {
               var5 = this.tmpbuf.length;
            }

            var5 = this.read(this.tmpbuf, 0, var5);
            if (var5 == -1) {
               this.entryEOF = true;
               break;
            }
         }

         return (long)var4;
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         super.close();
         this.closed = true;
      }

   }

   private ZipEntry readLOC() throws IOException {
      try {
         this.readFully(this.tmpbuf, 0, 30);
      } catch (EOFException var5) {
         return null;
      }

      if (ZipUtils.get32(this.tmpbuf, 0) != 67324752L) {
         return null;
      } else {
         this.flag = ZipUtils.get16(this.tmpbuf, 6);
         int var1 = ZipUtils.get16(this.tmpbuf, 26);
         int var2 = this.b.length;
         if (var1 > var2) {
            do {
               var2 *= 2;
            } while(var1 > var2);

            this.b = new byte[var2];
         }

         this.readFully(this.b, 0, var1);
         ZipEntry var3 = this.createZipEntry((this.flag & 2048) != 0 ? this.zc.toStringUTF8(this.b, var1) : this.zc.toString(this.b, var1));
         if ((this.flag & 1) == 1) {
            throw new ZipException("encrypted ZIP entry not supported");
         } else {
            var3.method = ZipUtils.get16(this.tmpbuf, 8);
            var3.xdostime = ZipUtils.get32(this.tmpbuf, 10);
            if ((this.flag & 8) == 8) {
               if (var3.method != 8) {
                  throw new ZipException("only DEFLATED entries can have EXT descriptor");
               }
            } else {
               var3.crc = ZipUtils.get32(this.tmpbuf, 14);
               var3.csize = ZipUtils.get32(this.tmpbuf, 18);
               var3.size = ZipUtils.get32(this.tmpbuf, 22);
            }

            var1 = ZipUtils.get16(this.tmpbuf, 28);
            if (var1 > 0) {
               byte[] var4 = new byte[var1];
               this.readFully(var4, 0, var1);
               var3.setExtra0(var4, var3.csize == 4294967295L || var3.size == 4294967295L);
            }

            return var3;
         }
      }
   }

   protected ZipEntry createZipEntry(String var1) {
      return new ZipEntry(var1);
   }

   private void readEnd(ZipEntry var1) throws IOException {
      int var2 = this.inf.getRemaining();
      if (var2 > 0) {
         ((PushbackInputStream)this.in).unread(this.buf, this.len - var2, var2);
      }

      if ((this.flag & 8) == 8) {
         long var3;
         if (this.inf.getBytesWritten() <= 4294967295L && this.inf.getBytesRead() <= 4294967295L) {
            this.readFully(this.tmpbuf, 0, 16);
            var3 = ZipUtils.get32(this.tmpbuf, 0);
            if (var3 != 134695760L) {
               var1.crc = var3;
               var1.csize = ZipUtils.get32(this.tmpbuf, 4);
               var1.size = ZipUtils.get32(this.tmpbuf, 8);
               ((PushbackInputStream)this.in).unread(this.tmpbuf, 11, 4);
            } else {
               var1.crc = ZipUtils.get32(this.tmpbuf, 4);
               var1.csize = ZipUtils.get32(this.tmpbuf, 8);
               var1.size = ZipUtils.get32(this.tmpbuf, 12);
            }
         } else {
            this.readFully(this.tmpbuf, 0, 24);
            var3 = ZipUtils.get32(this.tmpbuf, 0);
            if (var3 != 134695760L) {
               var1.crc = var3;
               var1.csize = ZipUtils.get64(this.tmpbuf, 4);
               var1.size = ZipUtils.get64(this.tmpbuf, 12);
               ((PushbackInputStream)this.in).unread(this.tmpbuf, 19, 4);
            } else {
               var1.crc = ZipUtils.get32(this.tmpbuf, 4);
               var1.csize = ZipUtils.get64(this.tmpbuf, 8);
               var1.size = ZipUtils.get64(this.tmpbuf, 16);
            }
         }
      }

      if (var1.size != this.inf.getBytesWritten()) {
         throw new ZipException("invalid entry size (expected " + var1.size + " but got " + this.inf.getBytesWritten() + " bytes)");
      } else if (var1.csize != this.inf.getBytesRead()) {
         throw new ZipException("invalid entry compressed size (expected " + var1.csize + " but got " + this.inf.getBytesRead() + " bytes)");
      } else if (var1.crc != this.crc.getValue()) {
         throw new ZipException("invalid entry CRC (expected 0x" + Long.toHexString(var1.crc) + " but got 0x" + Long.toHexString(this.crc.getValue()) + ")");
      }
   }

   private void readFully(byte[] var1, int var2, int var3) throws IOException {
      while(var3 > 0) {
         int var4 = this.in.read(var1, var2, var3);
         if (var4 == -1) {
            throw new EOFException();
         }

         var2 += var4;
         var3 -= var4;
      }

   }
}
