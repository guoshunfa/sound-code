package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;

class FixedLengthInputStream extends LeftOverInputStream {
   private long remaining;

   FixedLengthInputStream(ExchangeImpl var1, InputStream var2, long var3) {
      super(var1, var2);
      this.remaining = var3;
   }

   protected int readImpl(byte[] var1, int var2, int var3) throws IOException {
      this.eof = this.remaining == 0L;
      if (this.eof) {
         return -1;
      } else {
         if ((long)var3 > this.remaining) {
            var3 = (int)this.remaining;
         }

         int var4 = this.in.read(var1, var2, var3);
         if (var4 > -1) {
            this.remaining -= (long)var4;
            if (this.remaining == 0L) {
               this.t.getServerImpl().requestCompleted(this.t.getConnection());
            }
         }

         return var4;
      }
   }

   public int available() throws IOException {
      if (this.eof) {
         return 0;
      } else {
         int var1 = this.in.available();
         return (long)var1 < this.remaining ? var1 : (int)this.remaining;
      }
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
