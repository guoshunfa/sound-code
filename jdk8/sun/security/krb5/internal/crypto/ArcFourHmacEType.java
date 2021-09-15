package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KrbApErrException;

public final class ArcFourHmacEType extends EType {
   public int eType() {
      return 23;
   }

   public int minimumPadSize() {
      return 1;
   }

   public int confounderSize() {
      return 8;
   }

   public int checksumType() {
      return -138;
   }

   public int checksumSize() {
      return ArcFourHmac.getChecksumLength();
   }

   public int blockSize() {
      return 1;
   }

   public int keyType() {
      return 4;
   }

   public int keySize() {
      return 16;
   }

   public byte[] encrypt(byte[] var1, byte[] var2, int var3) throws KrbCryptoException {
      byte[] var4 = new byte[this.blockSize()];
      return this.encrypt(var1, var2, var4, var3);
   }

   public byte[] encrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbCryptoException {
      try {
         return ArcFourHmac.encrypt(var2, var4, var3, var1, 0, var1.length);
      } catch (GeneralSecurityException var7) {
         KrbCryptoException var6 = new KrbCryptoException(var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }
   }

   public byte[] decrypt(byte[] var1, byte[] var2, int var3) throws KrbApErrException, KrbCryptoException {
      byte[] var4 = new byte[this.blockSize()];
      return this.decrypt(var1, var2, var4, var3);
   }

   public byte[] decrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbApErrException, KrbCryptoException {
      try {
         return ArcFourHmac.decrypt(var2, var4, var3, var1, 0, var1.length);
      } catch (GeneralSecurityException var7) {
         KrbCryptoException var6 = new KrbCryptoException(var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }
   }

   public byte[] decryptedData(byte[] var1) {
      return var1;
   }
}
