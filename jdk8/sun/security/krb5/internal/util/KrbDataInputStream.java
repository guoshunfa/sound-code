package sun.security.krb5.internal.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class KrbDataInputStream extends BufferedInputStream {
   private boolean bigEndian = true;

   public void setNativeByteOrder() {
      if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
         this.bigEndian = true;
      } else {
         this.bigEndian = false;
      }

   }

   public KrbDataInputStream(InputStream var1) {
      super(var1);
   }

   public final int readLength4() throws IOException {
      int var1 = this.read(4);
      if (var1 < 0) {
         throw new IOException("Invalid encoding");
      } else {
         return var1;
      }
   }

   public int read(int var1) throws IOException {
      byte[] var2 = new byte[var1];
      if (this.read(var2, 0, var1) != var1) {
         throw new IOException("Premature end of stream reached");
      } else {
         int var3 = 0;

         for(int var4 = 0; var4 < var1; ++var4) {
            if (this.bigEndian) {
               var3 |= (var2[var4] & 255) << (var1 - var4 - 1) * 8;
            } else {
               var3 |= (var2[var4] & 255) << var4 * 8;
            }
         }

         return var3;
      }
   }

   public int readVersion() throws IOException {
      int var1 = (this.read() & 255) << 8;
      return var1 | this.read() & 255;
   }
}
