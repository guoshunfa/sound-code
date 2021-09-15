package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CheckedOutputStream extends FilterOutputStream {
   private Checksum cksum;

   public CheckedOutputStream(OutputStream var1, Checksum var2) {
      super(var1);
      this.cksum = var2;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
      this.cksum.update(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      this.cksum.update(var1, var2, var3);
   }

   public Checksum getChecksum() {
      return this.cksum;
   }
}
