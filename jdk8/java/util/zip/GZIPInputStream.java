package java.util.zip;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

public class GZIPInputStream extends InflaterInputStream {
   protected CRC32 crc;
   protected boolean eos;
   private boolean closed;
   public static final int GZIP_MAGIC = 35615;
   private static final int FTEXT = 1;
   private static final int FHCRC = 2;
   private static final int FEXTRA = 4;
   private static final int FNAME = 8;
   private static final int FCOMMENT = 16;
   private byte[] tmpbuf;

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }

   public GZIPInputStream(InputStream var1, int var2) throws IOException {
      super(var1, new Inflater(true), var2);
      this.crc = new CRC32();
      this.closed = false;
      this.tmpbuf = new byte[128];
      this.usesDefaultInflater = true;
      this.readHeader(var1);
   }

   public GZIPInputStream(InputStream var1) throws IOException {
      this(var1, 512);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (this.eos) {
         return -1;
      } else {
         int var4 = super.read(var1, var2, var3);
         if (var4 == -1) {
            if (!this.readTrailer()) {
               return this.read(var1, var2, var3);
            }

            this.eos = true;
         } else {
            this.crc.update(var1, var2, var4);
         }

         return var4;
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         super.close();
         this.eos = true;
         this.closed = true;
      }

   }

   private int readHeader(InputStream var1) throws IOException {
      CheckedInputStream var2 = new CheckedInputStream(var1, this.crc);
      this.crc.reset();
      if (this.readUShort(var2) != 35615) {
         throw new ZipException("Not in GZIP format");
      } else if (this.readUByte(var2) != 8) {
         throw new ZipException("Unsupported compression method");
      } else {
         int var3 = this.readUByte(var2);
         this.skipBytes(var2, 6);
         int var4 = 10;
         int var5;
         if ((var3 & 4) == 4) {
            var5 = this.readUShort(var2);
            this.skipBytes(var2, var5);
            var4 += var5 + 2;
         }

         if ((var3 & 8) == 8) {
            do {
               ++var4;
            } while(this.readUByte(var2) != 0);
         }

         if ((var3 & 16) == 16) {
            do {
               ++var4;
            } while(this.readUByte(var2) != 0);
         }

         if ((var3 & 2) == 2) {
            var5 = (int)this.crc.getValue() & '\uffff';
            if (this.readUShort(var2) != var5) {
               throw new ZipException("Corrupt GZIP header");
            }

            var4 += 2;
         }

         this.crc.reset();
         return var4;
      }
   }

   private boolean readTrailer() throws IOException {
      Object var1 = this.in;
      int var2 = this.inf.getRemaining();
      if (var2 > 0) {
         var1 = new SequenceInputStream(new ByteArrayInputStream(this.buf, this.len - var2, var2), new FilterInputStream((InputStream)var1) {
            public void close() throws IOException {
            }
         });
      }

      if (this.readUInt((InputStream)var1) == this.crc.getValue() && this.readUInt((InputStream)var1) == (this.inf.getBytesWritten() & 4294967295L)) {
         if (this.in.available() <= 0 && var2 <= 26) {
            return true;
         } else {
            byte var3 = 8;

            int var6;
            try {
               var6 = var3 + this.readHeader((InputStream)var1);
            } catch (IOException var5) {
               return true;
            }

            this.inf.reset();
            if (var2 > var6) {
               this.inf.setInput(this.buf, this.len - var2 + var6, var2 - var6);
            }

            return false;
         }
      } else {
         throw new ZipException("Corrupt GZIP trailer");
      }
   }

   private long readUInt(InputStream var1) throws IOException {
      long var2 = (long)this.readUShort(var1);
      return (long)this.readUShort(var1) << 16 | var2;
   }

   private int readUShort(InputStream var1) throws IOException {
      int var2 = this.readUByte(var1);
      return this.readUByte(var1) << 8 | var2;
   }

   private int readUByte(InputStream var1) throws IOException {
      int var2 = var1.read();
      if (var2 == -1) {
         throw new EOFException();
      } else if (var2 >= -1 && var2 <= 255) {
         return var2;
      } else {
         throw new IOException(this.in.getClass().getName() + ".read() returned value out of range -1..255: " + var2);
      }
   }

   private void skipBytes(InputStream var1, int var2) throws IOException {
      while(var2 > 0) {
         int var3 = var1.read(this.tmpbuf, 0, var2 < this.tmpbuf.length ? var2 : this.tmpbuf.length);
         if (var3 == -1) {
            throw new EOFException();
         }

         var2 -= var3;
      }

   }
}
