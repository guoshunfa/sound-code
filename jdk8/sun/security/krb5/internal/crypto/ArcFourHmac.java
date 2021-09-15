package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.dk.ArcFourCrypto;

public class ArcFourHmac {
   private static final ArcFourCrypto CRYPTO = new ArcFourCrypto(128);

   private ArcFourHmac() {
   }

   public static byte[] stringToKey(char[] var0) throws GeneralSecurityException {
      return CRYPTO.stringToKey(var0);
   }

   public static int getChecksumLength() {
      return CRYPTO.getChecksumLength();
   }

   public static byte[] calculateChecksum(byte[] var0, int var1, byte[] var2, int var3, int var4) throws GeneralSecurityException {
      return CRYPTO.calculateChecksum(var0, var1, var2, var3, var4);
   }

   public static byte[] encryptSeq(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5) throws GeneralSecurityException, KrbCryptoException {
      return CRYPTO.encryptSeq(var0, var1, var2, var3, var4, var5);
   }

   public static byte[] decryptSeq(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5) throws GeneralSecurityException, KrbCryptoException {
      return CRYPTO.decryptSeq(var0, var1, var2, var3, var4, var5);
   }

   public static byte[] encrypt(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5) throws GeneralSecurityException, KrbCryptoException {
      return CRYPTO.encrypt(var0, var1, var2, (byte[])null, var3, var4, var5);
   }

   public static byte[] encryptRaw(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5) throws GeneralSecurityException, KrbCryptoException {
      return CRYPTO.encryptRaw(var0, var1, var2, var3, var4, var5);
   }

   public static byte[] decrypt(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5) throws GeneralSecurityException {
      return CRYPTO.decrypt(var0, var1, var2, var3, var4, var5);
   }

   public static byte[] decryptRaw(byte[] var0, int var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws GeneralSecurityException {
      return CRYPTO.decryptRaw(var0, var1, var2, var3, var4, var5, var6);
   }
}
