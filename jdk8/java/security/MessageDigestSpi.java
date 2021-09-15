package java.security;

import java.nio.ByteBuffer;
import sun.security.jca.JCAUtil;

public abstract class MessageDigestSpi {
   private byte[] tempArray;

   protected int engineGetDigestLength() {
      return 0;
   }

   protected abstract void engineUpdate(byte var1);

   protected abstract void engineUpdate(byte[] var1, int var2, int var3);

   protected void engineUpdate(ByteBuffer var1) {
      if (var1.hasRemaining()) {
         int var3;
         int var4;
         if (var1.hasArray()) {
            byte[] var2 = var1.array();
            var3 = var1.arrayOffset();
            var4 = var1.position();
            int var5 = var1.limit();
            this.engineUpdate(var2, var3 + var4, var5 - var4);
            var1.position(var5);
         } else {
            int var6 = var1.remaining();
            var3 = JCAUtil.getTempArraySize(var6);
            if (this.tempArray == null || var3 > this.tempArray.length) {
               this.tempArray = new byte[var3];
            }

            while(var6 > 0) {
               var4 = Math.min(var6, this.tempArray.length);
               var1.get(this.tempArray, 0, var4);
               this.engineUpdate(this.tempArray, 0, var4);
               var6 -= var4;
            }
         }

      }
   }

   protected abstract byte[] engineDigest();

   protected int engineDigest(byte[] var1, int var2, int var3) throws DigestException {
      byte[] var4 = this.engineDigest();
      if (var3 < var4.length) {
         throw new DigestException("partial digests not returned");
      } else if (var1.length - var2 < var4.length) {
         throw new DigestException("insufficient space in the output buffer to store the digest");
      } else {
         System.arraycopy(var4, 0, var1, var2, var4.length);
         return var4.length;
      }
   }

   protected abstract void engineReset();

   public Object clone() throws CloneNotSupportedException {
      if (this instanceof Cloneable) {
         return super.clone();
      } else {
         throw new CloneNotSupportedException();
      }
   }
}
