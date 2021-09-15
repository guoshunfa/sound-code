package sun.security.krb5.internal.crypto;

import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import sun.security.krb5.KrbCryptoException;

public class DesMacKCksumType extends CksumType {
   public int confounderSize() {
      return 0;
   }

   public int cksumType() {
      return 5;
   }

   public boolean isSafe() {
      return true;
   }

   public int cksumSize() {
      return 16;
   }

   public int keyType() {
      return 1;
   }

   public int keySize() {
      return 8;
   }

   public byte[] calculateChecksum(byte[] var1, int var2) {
      return null;
   }

   public byte[] calculateKeyedChecksum(byte[] var1, int var2, byte[] var3, int var4) throws KrbCryptoException {
      try {
         if (DESKeySpec.isWeak(var3, 0)) {
            var3[7] = (byte)(var3[7] ^ 240);
         }
      } catch (InvalidKeyException var7) {
      }

      byte[] var5 = new byte[var3.length];
      System.arraycopy(var3, 0, var5, 0, var3.length);
      byte[] var6 = Des.des_cksum(var5, var1, var3);
      return var6;
   }

   public boolean verifyKeyedChecksum(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) throws KrbCryptoException {
      byte[] var6 = this.calculateKeyedChecksum(var1, var1.length, var3, var5);
      return isChecksumEqual(var4, var6);
   }
}
