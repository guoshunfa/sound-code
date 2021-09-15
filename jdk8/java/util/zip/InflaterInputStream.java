package java.util.zip;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InflaterInputStream extends FilterInputStream {
   protected Inflater inf;
   protected byte[] buf;
   protected int len;
   private boolean closed;
   private boolean reachEOF;
   boolean usesDefaultInflater;
   private byte[] singleByteBuf;
   private byte[] b;

   private void ensureOpen() throws IOException {
      if (this.closed) {
         throw new IOException("Stream closed");
      }
   }

   public InflaterInputStream(InputStream var1, Inflater var2, int var3) {
      super(var1);
      this.closed = false;
      this.reachEOF = false;
      this.usesDefaultInflater = false;
      this.singleByteBuf = new byte[1];
      this.b = new byte[512];
      if (var1 != null && var2 != null) {
         if (var3 <= 0) {
            throw new IllegalArgumentException("buffer size <= 0");
         } else {
            this.inf = var2;
            this.buf = new byte[var3];
         }
      } else {
         throw new NullPointerException();
      }
   }

   public InflaterInputStream(InputStream var1, Inflater var2) {
      this(var1, var2, 512);
   }

   public InflaterInputStream(InputStream var1) {
      this(var1, new Inflater());
      this.usesDefaultInflater = true;
   }

   public int read() throws IOException {
      this.ensureOpen();
      return this.read(this.singleByteBuf, 0, 1) == -1 ? -1 : Byte.toUnsignedInt(this.singleByteBuf[0]);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (var3 == 0) {
            return 0;
         } else {
            try {
               int var4;
               while((var4 = this.inf.inflate(var1, var2, var3)) == 0) {
                  if (this.inf.finished() || this.inf.needsDictionary()) {
                     this.reachEOF = true;
                     return -1;
                  }

                  if (this.inf.needsInput()) {
                     this.fill();
                  }
               }

               return var4;
            } catch (DataFormatException var6) {
               String var5 = var6.getMessage();
               throw new ZipException(var5 != null ? var5 : "Invalid ZLIB data format");
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public int available() throws IOException {
      this.ensureOpen();
      return this.reachEOF ? 0 : 1;
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
            if (var5 > this.b.length) {
               var5 = this.b.length;
            }

            var5 = this.read(this.b, 0, var5);
            if (var5 == -1) {
               this.reachEOF = true;
               break;
            }
         }

         return (long)var4;
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         if (this.usesDefaultInflater) {
            this.inf.end();
         }

         this.in.close();
         this.closed = true;
      }

   }

   protected void fill() throws IOException {
      this.ensureOpen();
      this.len = this.in.read(this.buf, 0, this.buf.length);
      if (this.len == -1) {
         throw new EOFException("Unexpected end of ZLIB input stream");
      } else {
         this.inf.setInput(this.buf, 0, this.len);
      }
   }

   public boolean markSupported() {
      return false;
   }

   public synchronized void mark(int var1) {
   }

   public synchronized void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }
}
