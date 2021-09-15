package sun.security.krb5.internal.crypto.dk;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.KeyUsage;
import sun.security.provider.MD4;

public class ArcFourCrypto extends DkCrypto {
   private static final boolean debug = false;
   private static final int confounderSize = 8;
   private static final byte[] ZERO_IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
   private static final int hashSize = 16;
   private final int keyLength;

   public ArcFourCrypto(int var1) {
      this.keyLength = var1;
   }

   protected int getKeySeedLength() {
      return this.keyLength;
   }

   protected byte[] randomToKey(byte[] var1) {
      return var1;
   }

   public byte[] stringToKey(char[] var1) throws GeneralSecurityException {
      return this.stringToKey(var1, (byte[])null);
   }

   private byte[] stringToKey(char[] var1, byte[] var2) throws GeneralSecurityException {
      if (var2 != null && var2.length > 0) {
         throw new RuntimeException("Invalid parameter to stringToKey");
      } else {
         byte[] var3 = null;
         Object var4 = null;

         Object var6;
         try {
            var3 = charToUtf16(var1);
            MessageDigest var5 = MD4.getInstance();
            var5.update(var3);
            byte[] var12 = var5.digest();
            return var12;
         } catch (Exception var10) {
            var6 = null;
         } finally {
            if (var3 != null) {
               Arrays.fill((byte[])var3, (byte)0);
            }

         }

         return (byte[])var6;
      }
   }

   protected Cipher getCipher(byte[] var1, byte[] var2, int var3) throws GeneralSecurityException {
      if (var2 == null) {
         var2 = ZERO_IV;
      }

      SecretKeySpec var4 = new SecretKeySpec(var1, "ARCFOUR");
      Cipher var5 = Cipher.getInstance("ARCFOUR");
      IvParameterSpec var6 = new IvParameterSpec(var2, 0, var2.length);
      var5.init(var3, var4, var6);
      return var5;
   }

   public int getChecksumLength() {
      return 16;
   }

   protected byte[] getHmac(byte[] var1, byte[] var2) throws GeneralSecurityException {
      SecretKeySpec var3 = new SecretKeySpec(var1, "HmacMD5");
      Mac var4 = Mac.getInstance("HmacMD5");
      var4.init(var3);
      byte[] var5 = var4.doFinal(var2);
      return var5;
   }

   public byte[] calculateChecksum(byte[] var1, int var2, byte[] var3, int var4, int var5) throws GeneralSecurityException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         Object var6 = null;

         byte[] var7;
         GeneralSecurityException var8;
         byte[] var14;
         try {
            var7 = "signaturekey".getBytes();
            byte[] var15 = new byte[var7.length + 1];
            System.arraycopy(var7, 0, var15, 0, var7.length);
            var14 = this.getHmac(var1, var15);
         } catch (Exception var13) {
            var8 = new GeneralSecurityException("Calculate Checkum Failed!");
            var8.initCause(var13);
            throw var8;
         }

         var7 = this.getSalt(var2);
         var8 = null;

         MessageDigest var16;
         try {
            var16 = MessageDigest.getInstance("MD5");
         } catch (NoSuchAlgorithmException var12) {
            GeneralSecurityException var10 = new GeneralSecurityException("Calculate Checkum Failed!");
            var10.initCause(var12);
            throw var10;
         }

         var16.update(var7);
         var16.update(var3, var4, var5);
         byte[] var9 = var16.digest();
         byte[] var17 = this.getHmac(var14, var9);
         if (var17.length == this.getChecksumLength()) {
            return var17;
         } else if (var17.length > this.getChecksumLength()) {
            byte[] var11 = new byte[this.getChecksumLength()];
            System.arraycopy(var17, 0, var11, 0, var11.length);
            return var11;
         } else {
            throw new GeneralSecurityException("checksum size too short: " + var17.length + "; expecting : " + this.getChecksumLength());
         }
      }
   }

   public byte[] encryptSeq(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException, KrbCryptoException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = new byte[4];
         byte[] var8 = this.getHmac(var1, var7);
         var8 = this.getHmac(var8, var3);
         Cipher var9 = Cipher.getInstance("ARCFOUR");
         SecretKeySpec var10 = new SecretKeySpec(var8, "ARCFOUR");
         var9.init(1, var10);
         byte[] var11 = var9.doFinal(var4, var5, var6);
         return var11;
      }
   }

   public byte[] decryptSeq(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException, KrbCryptoException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = new byte[4];
         byte[] var8 = this.getHmac(var1, var7);
         var8 = this.getHmac(var8, var3);
         Cipher var9 = Cipher.getInstance("ARCFOUR");
         SecretKeySpec var10 = new SecretKeySpec(var8, "ARCFOUR");
         var9.init(2, var10);
         byte[] var11 = var9.doFinal(var4, var5, var6);
         return var11;
      }
   }

   public byte[] encrypt(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, int var6, int var7) throws GeneralSecurityException, KrbCryptoException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var8 = Confounder.bytes(8);
         int var9 = this.roundup(var8.length + var7, 1);
         byte[] var10 = new byte[var9];
         System.arraycopy(var8, 0, var10, 0, var8.length);
         System.arraycopy(var5, var6, var10, var8.length, var7);
         byte[] var11 = new byte[var1.length];
         System.arraycopy(var1, 0, var11, 0, var1.length);
         byte[] var12 = this.getSalt(var2);
         byte[] var13 = this.getHmac(var11, var12);
         byte[] var14 = this.getHmac(var13, var10);
         byte[] var15 = this.getHmac(var13, var14);
         Cipher var16 = Cipher.getInstance("ARCFOUR");
         SecretKeySpec var17 = new SecretKeySpec(var15, "ARCFOUR");
         var16.init(1, var17);
         byte[] var18 = var16.doFinal(var10, 0, var10.length);
         byte[] var19 = new byte[16 + var18.length];
         System.arraycopy(var14, 0, var19, 0, 16);
         System.arraycopy(var18, 0, var19, 16, var18.length);
         return var19;
      }
   }

   public byte[] encryptRaw(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException, KrbCryptoException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = new byte[var1.length];

         for(int var8 = 0; var8 <= 15; ++var8) {
            var7[var8] = (byte)(var1[var8] ^ 240);
         }

         byte[] var13 = new byte[4];
         byte[] var9 = this.getHmac(var7, var13);
         var9 = this.getHmac(var9, var3);
         Cipher var10 = Cipher.getInstance("ARCFOUR");
         SecretKeySpec var11 = new SecretKeySpec(var9, "ARCFOUR");
         var10.init(1, var11);
         byte[] var12 = var10.doFinal(var4, var5, var6);
         return var12;
      }
   }

   public byte[] decrypt(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = new byte[var1.length];
         System.arraycopy(var1, 0, var7, 0, var1.length);
         byte[] var8 = this.getSalt(var2);
         byte[] var9 = this.getHmac(var7, var8);
         byte[] var10 = new byte[16];
         System.arraycopy(var4, var5, var10, 0, 16);
         byte[] var11 = this.getHmac(var9, var10);
         Cipher var12 = Cipher.getInstance("ARCFOUR");
         SecretKeySpec var13 = new SecretKeySpec(var11, "ARCFOUR");
         var12.init(2, var13);
         byte[] var14 = var12.doFinal(var4, var5 + 16, var6 - 16);
         byte[] var15 = this.getHmac(var9, var14);
         boolean var16 = false;
         if (var15.length >= 16) {
            for(int var17 = 0; var17 < 16; ++var17) {
               if (var15[var17] != var4[var17]) {
                  var16 = true;
                  break;
               }
            }
         }

         if (var16) {
            throw new GeneralSecurityException("Checksum failed");
         } else {
            byte[] var18 = new byte[var14.length - 8];
            System.arraycopy(var14, 8, var18, 0, var18.length);
            return var18;
         }
      }
   }

   public byte[] decryptRaw(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6, byte[] var7) throws GeneralSecurityException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var8 = new byte[var1.length];

         for(int var9 = 0; var9 <= 15; ++var9) {
            var8[var9] = (byte)(var1[var9] ^ 240);
         }

         byte[] var15 = new byte[4];
         byte[] var10 = this.getHmac(var8, var15);
         byte[] var11 = new byte[4];
         System.arraycopy(var7, 0, var11, 0, var11.length);
         var10 = this.getHmac(var10, var11);
         Cipher var12 = Cipher.getInstance("ARCFOUR");
         SecretKeySpec var13 = new SecretKeySpec(var10, "ARCFOUR");
         var12.init(2, var13);
         byte[] var14 = var12.doFinal(var4, var5, var6);
         return var14;
      }
   }

   private byte[] getSalt(int var1) {
      int var2 = this.arcfour_translate_usage(var1);
      byte[] var3 = new byte[]{(byte)(var2 & 255), (byte)(var2 >> 8 & 255), (byte)(var2 >> 16 & 255), (byte)(var2 >> 24 & 255)};
      return var3;
   }

   private int arcfour_translate_usage(int var1) {
      switch(var1) {
      case 3:
         return 8;
      case 9:
         return 8;
      case 23:
         return 13;
      default:
         return var1;
      }
   }
}
