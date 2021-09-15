package java.io;

public class PushbackInputStream extends FilterInputStream {
   protected byte[] buf;
   protected int pos;

   private void ensureOpen() throws IOException {
      if (this.in == null) {
         throw new IOException("Stream closed");
      }
   }

   public PushbackInputStream(InputStream var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException("size <= 0");
      } else {
         this.buf = new byte[var2];
         this.pos = var2;
      }
   }

   public PushbackInputStream(InputStream var1) {
      this(var1, 1);
   }

   public int read() throws IOException {
      this.ensureOpen();
      return this.pos < this.buf.length ? this.buf[this.pos++] & 255 : super.read();
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.buf.length - this.pos;
            if (var4 > 0) {
               if (var3 < var4) {
                  var4 = var3;
               }

               System.arraycopy(this.buf, this.pos, var1, var2, var4);
               this.pos += var4;
               var2 += var4;
               var3 -= var4;
            }

            if (var3 > 0) {
               var3 = super.read(var1, var2, var3);
               if (var3 == -1) {
                  return var4 == 0 ? -1 : var4;
               } else {
                  return var4 + var3;
               }
            } else {
               return var4;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void unread(int var1) throws IOException {
      this.ensureOpen();
      if (this.pos == 0) {
         throw new IOException("Push back buffer is full");
      } else {
         this.buf[--this.pos] = (byte)var1;
      }
   }

   public void unread(byte[] var1, int var2, int var3) throws IOException {
      this.ensureOpen();
      if (var3 > this.pos) {
         throw new IOException("Push back buffer is full");
      } else {
         this.pos -= var3;
         System.arraycopy(var1, var2, this.buf, this.pos, var3);
      }
   }

   public void unread(byte[] var1) throws IOException {
      this.unread(var1, 0, var1.length);
   }

   public int available() throws IOException {
      this.ensureOpen();
      int var1 = this.buf.length - this.pos;
      int var2 = super.available();
      return var1 > Integer.MAX_VALUE - var2 ? Integer.MAX_VALUE : var1 + var2;
   }

   public long skip(long var1) throws IOException {
      this.ensureOpen();
      if (var1 <= 0L) {
         return 0L;
      } else {
         long var3 = (long)(this.buf.length - this.pos);
         if (var3 > 0L) {
            if (var1 < var3) {
               var3 = var1;
            }

            this.pos = (int)((long)this.pos + var3);
            var1 -= var3;
         }

         if (var1 > 0L) {
            var3 += super.skip(var1);
         }

         return var3;
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

   public synchronized void close() throws IOException {
      if (this.in != null) {
         this.in.close();
         this.in = null;
         this.buf = null;
      }
   }
}
