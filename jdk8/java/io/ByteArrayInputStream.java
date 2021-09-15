package java.io;

public class ByteArrayInputStream extends InputStream {
   protected byte[] buf;
   protected int pos;
   protected int mark = 0;
   protected int count;

   public ByteArrayInputStream(byte[] var1) {
      this.buf = var1;
      this.pos = 0;
      this.count = var1.length;
   }

   public ByteArrayInputStream(byte[] var1, int var2, int var3) {
      this.buf = var1;
      this.pos = var2;
      this.count = Math.min(var2 + var3, var1.length);
      this.mark = var2;
   }

   public synchronized int read() {
      return this.pos < this.count ? this.buf[this.pos++] & 255 : -1;
   }

   public synchronized int read(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (this.pos >= this.count) {
            return -1;
         } else {
            int var4 = this.count - this.pos;
            if (var3 > var4) {
               var3 = var4;
            }

            if (var3 <= 0) {
               return 0;
            } else {
               System.arraycopy(this.buf, this.pos, var1, var2, var3);
               this.pos += var3;
               return var3;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized long skip(long var1) {
      long var3 = (long)(this.count - this.pos);
      if (var1 < var3) {
         var3 = var1 < 0L ? 0L : var1;
      }

      this.pos = (int)((long)this.pos + var3);
      return var3;
   }

   public synchronized int available() {
      return this.count - this.pos;
   }

   public boolean markSupported() {
      return true;
   }

   public void mark(int var1) {
      this.mark = this.pos;
   }

   public synchronized void reset() {
      this.pos = this.mark;
   }

   public void close() throws IOException {
   }
}
