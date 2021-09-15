package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CheckedInputStream extends FilterInputStream {
   private Checksum cksum;

   public CheckedInputStream(InputStream var1, Checksum var2) {
      super(var1);
      this.cksum = var2;
   }

   public int read() throws IOException {
      int var1 = this.in.read();
      if (var1 != -1) {
         this.cksum.update(var1);
      }

      return var1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      var3 = this.in.read(var1, var2, var3);
      if (var3 != -1) {
         this.cksum.update(var1, var2, var3);
      }

      return var3;
   }

   public long skip(long var1) throws IOException {
      byte[] var3 = new byte[512];

      long var4;
      long var6;
      for(var4 = 0L; var4 < var1; var4 += var6) {
         var6 = var1 - var4;
         var6 = (long)this.read(var3, 0, var6 < (long)var3.length ? (int)var6 : var3.length);
         if (var6 == -1L) {
            return var4;
         }
      }

      return var4;
   }

   public Checksum getChecksum() {
      return this.cksum;
   }
}
