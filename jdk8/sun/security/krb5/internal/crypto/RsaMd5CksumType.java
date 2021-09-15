package sun.security.krb5.internal.crypto;

import java.security.MessageDigest;
import sun.security.krb5.KrbCryptoException;

public final class RsaMd5CksumType extends CksumType {
   public int confounderSize() {
      return 0;
   }

   public int cksumType() {
      return 7;
   }

   public boolean isSafe() {
      return false;
   }

   public int cksumSize() {
      return 16;
   }

   public int keyType() {
      return 0;
   }

   public int keySize() {
      return 0;
   }

   public byte[] calculateChecksum(byte[] var1, int var2) throws KrbCryptoException {
      Object var4 = null;

      MessageDigest var3;
      try {
         var3 = MessageDigest.getInstance("MD5");
      } catch (Exception var7) {
         throw new KrbCryptoException("JCE provider may not be installed. " + var7.getMessage());
      }

      try {
         var3.update(var1);
         byte[] var8 = var3.digest();
         return var8;
      } catch (Exception var6) {
         throw new KrbCryptoException(var6.getMessage());
      }
   }

   public byte[] calculateKeyedChecksum(byte[] var1, int var2, byte[] var3, int var4) throws KrbCryptoException {
      return null;
   }

   public boolean verifyKeyedChecksum(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) throws KrbCryptoException {
      return false;
   }
}
