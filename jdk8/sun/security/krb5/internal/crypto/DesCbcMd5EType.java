package sun.security.krb5.internal.crypto;

import java.security.MessageDigest;
import sun.security.krb5.KrbCryptoException;

public final class DesCbcMd5EType extends DesCbcEType {
   public int eType() {
      return 3;
   }

   public int minimumPadSize() {
      return 0;
   }

   public int confounderSize() {
      return 8;
   }

   public int checksumType() {
      return 7;
   }

   public int checksumSize() {
      return 16;
   }

   protected byte[] calculateChecksum(byte[] var1, int var2) throws KrbCryptoException {
      MessageDigest var3 = null;

      try {
         var3 = MessageDigest.getInstance("MD5");
      } catch (Exception var6) {
         throw new KrbCryptoException("JCE provider may not be installed. " + var6.getMessage());
      }

      try {
         var3.update(var1);
         return var3.digest();
      } catch (Exception var5) {
         throw new KrbCryptoException(var5.getMessage());
      }
   }
}
