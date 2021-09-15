package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DeflaterInputStream extends FilterInputStream {
   protected final Deflater def;
   protected final byte[] buf;
   private byte[] rbuf;
   private boolean usesDefaultDeflater;
   private boolean reachEOF;

   private void ensureOpen() throws IOException {
      if (this.in == null) {
         throw new IOException("Stream closed");
      }
   }

   public DeflaterInputStream(InputStream var1) {
      this(var1, new Deflater());
      this.usesDefaultDeflater = true;
   }

   public DeflaterInputStream(InputStream var1, Deflater var2) {
      this(var1, var2, 512);
   }

   public DeflaterInputStream(InputStream var1, Deflater var2, int var3) {
      super(var1);
      this.rbuf = new byte[1];
      this.usesDefaultDeflater = false;
      this.reachEOF = false;
      if (var1 == null) {
         throw new NullPointerException("Null input");
      } else if (var2 == null) {
         throw new NullPointerException("Null deflater");
      } else if (var3 < 1) {
         throw new IllegalArgumentException("Buffer size < 1");
      } else {
         this.def = var2;
         this.buf = new byte[var3];
      }
   }

   public void close() throws IOException {
      if (this.in != null) {
         try {
            if (this.usesDefaultDeflater) {
               this.def.end();
            }

            this.in.close();
         } finally {
            this.in = null;
         }
      }

   }

   public int read() throws IOException {
      int var1 = this.read(this.rbuf, 0, 1);
      return var1 <= 0 ? -1 : this.rbuf[0] & 255;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException("Null buffer for read");
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4;
            int var5;
            for(var4 = 0; var3 > 0 && !this.def.finished(); var3 -= var5) {
               if (this.def.needsInput()) {
                  var5 = this.in.read(this.buf, 0, this.buf.length);
                  if (var5 < 0) {
                     this.def.finish();
                  } else if (var5 > 0) {
                     this.def.setInput(this.buf, 0, var5);
                  }
               }

               var5 = this.def.deflate(var1, var2, var3);
               var4 += var5;
               var2 += var5;
            }

            if (var4 == 0 && this.def.finished()) {
               this.reachEOF = true;
               var4 = -1;
            }

            return var4;
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
         if (this.rbuf.length < 512) {
            this.rbuf = new byte[512];
         }

         int var3 = (int)Math.min(var1, 2147483647L);

         long var4;
         int var6;
         for(var4 = 0L; var3 > 0; var3 -= var6) {
            var6 = this.read(this.rbuf, 0, var3 <= this.rbuf.length ? var3 : this.rbuf.length);
            if (var6 < 0) {
               break;
            }

            var4 += (long)var6;
         }

         return var4;
      }
   }

   public int available() throws IOException {
      this.ensureOpen();
      return this.reachEOF ? 0 : 1;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int var1) {
   }

   public void reset() throws IOException {
      throw new IOException("mark/reset not supported");
   }
}
