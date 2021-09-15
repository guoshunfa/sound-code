package java.io;

/** @deprecated */
@Deprecated
public class StringBufferInputStream extends InputStream {
   protected String buffer;
   protected int pos;
   protected int count;

   public StringBufferInputStream(String var1) {
      this.buffer = var1;
      this.count = var1.length();
   }

   public synchronized int read() {
      return this.pos < this.count ? this.buffer.charAt(this.pos++) & 255 : -1;
   }

   public synchronized int read(byte[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
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
               String var5 = this.buffer;
               int var6 = var3;

               while(true) {
                  --var6;
                  if (var6 < 0) {
                     return var3;
                  }

                  var1[var2++] = (byte)var5.charAt(this.pos++);
               }
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized long skip(long var1) {
      if (var1 < 0L) {
         return 0L;
      } else {
         if (var1 > (long)(this.count - this.pos)) {
            var1 = (long)(this.count - this.pos);
         }

         this.pos = (int)((long)this.pos + var1);
         return var1;
      }
   }

   public synchronized int available() {
      return this.count - this.pos;
   }

   public synchronized void reset() {
      this.pos = 0;
   }
}
