package sun.security.krb5.internal.crypto.dk;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.KeyUsage;

public class AesDkCrypto extends DkCrypto {
   private static final boolean debug = false;
   private static final int BLOCK_SIZE = 16;
   private static final int DEFAULT_ITERATION_COUNT = 4096;
   private static final byte[] ZERO_IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int hashSize = 12;
   private final int keyLength;

   public AesDkCrypto(int var1) {
      this.keyLength = var1;
   }

   protected int getKeySeedLength() {
      return this.keyLength;
   }

   public byte[] stringToKey(char[] var1, String var2, byte[] var3) throws GeneralSecurityException {
      byte[] var4 = null;

      Object var6;
      try {
         var4 = var2.getBytes("UTF-8");
         byte[] var5 = this.stringToKey(var1, var4, var3);
         return var5;
      } catch (Exception var10) {
         var6 = null;
      } finally {
         if (var4 != null) {
            Arrays.fill((byte[])var4, (byte)0);
         }

      }

      return (byte[])var6;
   }

   private byte[] stringToKey(char[] var1, byte[] var2, byte[] var3) throws GeneralSecurityException {
      int var4 = 4096;
      if (var3 != null) {
         if (var3.length != 4) {
            throw new RuntimeException("Invalid parameter to stringToKey");
         }

         var4 = readBigEndian(var3, 0, 4);
      }

      byte[] var5 = this.randomToKey(PBKDF2(var1, var2, var4, this.getKeySeedLength()));
      byte[] var6 = this.dk(var5, KERBEROS_CONSTANT);
      return var6;
   }

   protected byte[] randomToKey(byte[] var1) {
      return var1;
   }

   protected Cipher getCipher(byte[] var1, byte[] var2, int var3) throws GeneralSecurityException {
      if (var2 == null) {
         var2 = ZERO_IV;
      }

      SecretKeySpec var4 = new SecretKeySpec(var1, "AES");
      Cipher var5 = Cipher.getInstance("AES/CBC/NoPadding");
      IvParameterSpec var6 = new IvParameterSpec(var2, 0, var2.length);
      var5.init(var3, var4, var6);
      return var5;
   }

   public int getChecksumLength() {
      return 12;
   }

   protected byte[] getHmac(byte[] var1, byte[] var2) throws GeneralSecurityException {
      SecretKeySpec var3 = new SecretKeySpec(var1, "HMAC");
      Mac var4 = Mac.getInstance("HmacSHA1");
      var4.init(var3);
      byte[] var5 = var4.doFinal(var2);
      byte[] var6 = new byte[12];
      System.arraycopy(var5, 0, var6, 0, 12);
      return var6;
   }

   public byte[] calculateChecksum(byte[] var1, int var2, byte[] var3, int var4, int var5) throws GeneralSecurityException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var6 = new byte[]{(byte)(var2 >> 24 & 255), (byte)(var2 >> 16 & 255), (byte)(var2 >> 8 & 255), (byte)(var2 & 255), -103};
         byte[] var7 = this.dk(var1, var6);

         byte[] var9;
         try {
            byte[] var8 = this.getHmac(var7, var3);
            if (var8.length != this.getChecksumLength()) {
               if (var8.length > this.getChecksumLength()) {
                  var9 = new byte[this.getChecksumLength()];
                  System.arraycopy(var8, 0, var9, 0, var9.length);
                  byte[] var10 = var9;
                  return var10;
               }

               throw new GeneralSecurityException("checksum size too short: " + var8.length + "; expecting : " + this.getChecksumLength());
            }

            var9 = var8;
         } finally {
            Arrays.fill((byte[])var7, 0, var7.length, (byte)0);
         }

         return var9;
      }
   }

   public byte[] encrypt(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, int var6, int var7) throws GeneralSecurityException, KrbCryptoException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var8 = this.encryptCTS(var1, var2, var3, var4, var5, var6, var7, true);
         return var8;
      }
   }

   public byte[] encryptRaw(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException, KrbCryptoException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = this.encryptCTS(var1, var2, var3, (byte[])null, var4, var5, var6, false);
         return var7;
      }
   }

   public byte[] decrypt(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = this.decryptCTS(var1, var2, var3, var4, var5, var6, true);
         return var7;
      }
   }

   public byte[] decryptRaw(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = this.decryptCTS(var1, var2, var3, var4, var5, var6, false);
         return var7;
      }
   }

   private byte[] encryptCTS(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, int var6, int var7, boolean var8) throws GeneralSecurityException, KrbCryptoException {
      byte[] var9 = null;
      byte[] var10 = null;

      byte[] var18;
      try {
         byte[] var11 = new byte[]{(byte)(var2 >> 24 & 255), (byte)(var2 >> 16 & 255), (byte)(var2 >> 8 & 255), (byte)(var2 & 255), -86};
         var9 = this.dk(var1, var11);
         Object var12 = null;
         byte[] var13;
         byte[] var22;
         if (var8) {
            var13 = Confounder.bytes(16);
            var22 = new byte[var13.length + var7];
            System.arraycopy(var13, 0, var22, 0, var13.length);
            System.arraycopy(var5, var6, var22, var13.length, var7);
         } else {
            var22 = new byte[var7];
            System.arraycopy(var5, var6, var22, 0, var7);
         }

         var13 = new byte[var22.length + 12];
         Cipher var14 = Cipher.getInstance("AES/CTS/NoPadding");
         SecretKeySpec var15 = new SecretKeySpec(var9, "AES");
         IvParameterSpec var16 = new IvParameterSpec(var3, 0, var3.length);
         var14.init(1, var15, var16);
         var14.doFinal(var22, 0, var22.length, var13);
         var11[4] = 85;
         var10 = this.dk(var1, var11);
         byte[] var17 = this.getHmac(var10, var22);
         System.arraycopy(var17, 0, var13, var22.length, var17.length);
         var18 = var13;
      } finally {
         if (var9 != null) {
            Arrays.fill((byte[])var9, 0, var9.length, (byte)0);
         }

         if (var10 != null) {
            Arrays.fill((byte[])var10, 0, var10.length, (byte)0);
         }

      }

      return var18;
   }

   private byte[] decryptCTS(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6, boolean var7) throws GeneralSecurityException {
      byte[] var8 = null;
      byte[] var9 = null;

      byte[] var19;
      try {
         byte[] var10 = new byte[]{(byte)(var2 >> 24 & 255), (byte)(var2 >> 16 & 255), (byte)(var2 >> 8 & 255), (byte)(var2 & 255), -86};
         var8 = this.dk(var1, var10);
         Cipher var11 = Cipher.getInstance("AES/CTS/NoPadding");
         SecretKeySpec var12 = new SecretKeySpec(var8, "AES");
         IvParameterSpec var13 = new IvParameterSpec(var3, 0, var3.length);
         var11.init(2, var12, var13);
         byte[] var14 = var11.doFinal(var4, var5, var6 - 12);
         var10[4] = 85;
         var9 = this.dk(var1, var10);
         byte[] var15 = this.getHmac(var9, var14);
         int var16 = var5 + var6 - 12;
         boolean var17 = false;
         if (var15.length >= 12) {
            for(int var18 = 0; var18 < 12; ++var18) {
               if (var15[var18] != var4[var16 + var18]) {
                  var17 = true;
                  break;
               }
            }
         }

         if (var17) {
            throw new GeneralSecurityException("Checksum failed");
         }

         byte[] var23;
         if (!var7) {
            var23 = var14;
            return var23;
         }

         var23 = new byte[var14.length - 16];
         System.arraycopy(var14, 16, var23, 0, var23.length);
         var19 = var23;
      } finally {
         if (var8 != null) {
            Arrays.fill((byte[])var8, 0, var8.length, (byte)0);
         }

         if (var9 != null) {
            Arrays.fill((byte[])var9, 0, var9.length, (byte)0);
         }

      }

      return var19;
   }

   private static byte[] PBKDF2(char[] var0, byte[] var1, int var2, int var3) throws GeneralSecurityException {
      PBEKeySpec var4 = new PBEKeySpec(var0, var1, var2, var3);
      SecretKeyFactory var5 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      SecretKey var6 = var5.generateSecret(var4);
      byte[] var7 = var6.getEncoded();
      return var7;
   }

   public static final int readBigEndian(byte[] var0, int var1, int var2) {
      int var3 = 0;

      for(int var4 = (var2 - 1) * 8; var2 > 0; --var2) {
         var3 += (var0[var1] & 255) << var4;
         var4 -= 8;
         ++var1;
      }

      return var3;
   }
}
