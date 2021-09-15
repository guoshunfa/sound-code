package sun.security.krb5.internal.crypto.dk;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Des3DkCrypto extends DkCrypto {
   private static final byte[] ZERO_IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

   protected int getKeySeedLength() {
      return 168;
   }

   public byte[] stringToKey(char[] var1) throws GeneralSecurityException {
      byte[] var2 = null;

      byte[] var3;
      try {
         var2 = charToUtf8(var1);
         var3 = this.stringToKey(var2, (byte[])null);
      } finally {
         if (var2 != null) {
            Arrays.fill((byte[])var2, (byte)0);
         }

      }

      return var3;
   }

   private byte[] stringToKey(byte[] var1, byte[] var2) throws GeneralSecurityException {
      if (var2 != null && var2.length > 0) {
         throw new RuntimeException("Invalid parameter to stringToKey");
      } else {
         byte[] var3 = this.randomToKey(nfold(var1, this.getKeySeedLength()));
         return this.dk(var3, KERBEROS_CONSTANT);
      }
   }

   public byte[] parityFix(byte[] var1) throws GeneralSecurityException {
      setParityBit(var1);
      return var1;
   }

   protected byte[] randomToKey(byte[] var1) {
      if (var1.length != 21) {
         throw new IllegalArgumentException("input must be 168 bits");
      } else {
         byte[] var2 = keyCorrection(des3Expand(var1, 0, 7));
         byte[] var3 = keyCorrection(des3Expand(var1, 7, 14));
         byte[] var4 = keyCorrection(des3Expand(var1, 14, 21));
         byte[] var5 = new byte[24];
         System.arraycopy(var2, 0, var5, 0, 8);
         System.arraycopy(var3, 0, var5, 8, 8);
         System.arraycopy(var4, 0, var5, 16, 8);
         return var5;
      }
   }

   private static byte[] keyCorrection(byte[] var0) {
      try {
         if (DESKeySpec.isWeak(var0, 0)) {
            var0[7] = (byte)(var0[7] ^ 240);
         }
      } catch (InvalidKeyException var2) {
      }

      return var0;
   }

   private static byte[] des3Expand(byte[] var0, int var1, int var2) {
      if (var2 - var1 != 7) {
         throw new IllegalArgumentException("Invalid length of DES Key Value:" + var1 + "," + var2);
      } else {
         byte[] var3 = new byte[8];
         byte var4 = 0;
         System.arraycopy(var0, var1, var3, 0, 7);
         byte var5 = 0;

         for(int var6 = var1; var6 < var2; ++var6) {
            byte var7 = (byte)(var0[var6] & 1);
            ++var5;
            if (var7 != 0) {
               var4 = (byte)(var4 | var7 << var5);
            }
         }

         var3[7] = var4;
         setParityBit(var3);
         return var3;
      }
   }

   private static void setParityBit(byte[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         int var2 = var0[var1] & 254;
         var2 |= Integer.bitCount(var2) & 1 ^ 1;
         var0[var1] = (byte)var2;
      }

   }

   protected Cipher getCipher(byte[] var1, byte[] var2, int var3) throws GeneralSecurityException {
      SecretKeyFactory var4 = SecretKeyFactory.getInstance("desede");
      DESedeKeySpec var5 = new DESedeKeySpec(var1, 0);
      SecretKey var6 = var4.generateSecret(var5);
      if (var2 == null) {
         var2 = ZERO_IV;
      }

      Cipher var7 = Cipher.getInstance("DESede/CBC/NoPadding");
      IvParameterSpec var8 = new IvParameterSpec(var2, 0, var2.length);
      var7.init(var3, var6, var8);
      return var7;
   }

   public int getChecksumLength() {
      return 20;
   }

   protected byte[] getHmac(byte[] var1, byte[] var2) throws GeneralSecurityException {
      SecretKeySpec var3 = new SecretKeySpec(var1, "HmacSHA1");
      Mac var4 = Mac.getInstance("HmacSHA1");
      var4.init(var3);
      return var4.doFinal(var2);
   }
}
