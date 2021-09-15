package sun.security.krb5.internal.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class KrbDataOutputStream extends BufferedOutputStream {
   public KrbDataOutputStream(OutputStream var1) {
      super(var1);
   }

   public void write32(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)((var1 & -16777216) >> 24 & 255), (byte)((var1 & 16711680) >> 16 & 255), (byte)((var1 & '\uff00') >> 8 & 255), (byte)(var1 & 255)};
      this.write(var2, 0, 4);
   }

   public void write16(int var1) throws IOException {
      byte[] var2 = new byte[]{(byte)((var1 & '\uff00') >> 8 & 255), (byte)(var1 & 255)};
      this.write(var2, 0, 2);
   }

   public void write8(int var1) throws IOException {
      this.write(var1 & 255);
   }
}
