package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;

public class HmacMd5ArcFourCksumType extends CksumType {
   public int confounderSize() {
      return 8;
   }

   public int cksumType() {
      return -138;
   }

   public boolean isSafe() {
      return true;
   }

   public int cksumSize() {
      return 16;
   }

   public int keyType() {
      return 4;
   }

   public int keySize() {
      return 16;
   }

   public byte[] calculateChecksum(byte[] var1, int var2) {
      return null;
   }

   public byte[] calculateKeyedChecksum(byte[] var1, int var2, byte[] var3, int var4) throws KrbCryptoException {
      try {
         return ArcFourHmac.calculateChecksum(var3, var4, var1, 0, var2);
      } catch (GeneralSecurityException var7) {
         KrbCryptoException var6 = new KrbCryptoException(var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }
   }

   public boolean verifyKeyedChecksum(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) throws KrbCryptoException {
      try {
         byte[] var6 = ArcFourHmac.calculateChecksum(var3, var5, var1, 0, var2);
         return isChecksumEqual(var4, var6);
      } catch (GeneralSecurityException var8) {
         KrbCryptoException var7 = new KrbCryptoException(var8.getMessage());
         var7.initCause(var8);
         throw var7;
      }
   }
}
