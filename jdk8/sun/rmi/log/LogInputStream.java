package sun.rmi.log;

import java.io.IOException;
import java.io.InputStream;

public class LogInputStream extends InputStream {
   private InputStream in;
   private int length;

   public LogInputStream(InputStream var1, int var2) throws IOException {
      this.in = var1;
      this.length = var2;
   }

   public int read() throws IOException {
      if (this.length == 0) {
         return -1;
      } else {
         int var1 = this.in.read();
         this.length = var1 != -1 ? this.length - 1 : 0;
         return var1;
      }
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.length == 0) {
         return -1;
      } else {
         var3 = this.length < var3 ? this.length : var3;
         int var4 = this.in.read(var1, var2, var3);
         this.length = var4 != -1 ? this.length - var4 : 0;
         return var4;
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 > 2147483647L) {
         throw new IOException("Too many bytes to skip - " + var1);
      } else if (this.length == 0) {
         return 0L;
      } else {
         var1 = (long)this.length < var1 ? (long)this.length : var1;
         var1 = this.in.skip(var1);
         this.length = (int)((long)this.length - var1);
         return var1;
      }
   }

   public int available() throws IOException {
      int var1 = this.in.available();
      return this.length < var1 ? this.length : var1;
   }

   public void close() {
      this.length = 0;
   }

   protected void finalize() throws IOException {
      this.close();
   }
}
