package sun.security.rsa;

import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.PSource.PSpecified;
import sun.security.jca.JCAUtil;

public final class RSAPadding {
   public static final int PAD_BLOCKTYPE_1 = 1;
   public static final int PAD_BLOCKTYPE_2 = 2;
   public static final int PAD_NONE = 3;
   public static final int PAD_OAEP_MGF1 = 4;
   private final int type;
   private final int paddedSize;
   private SecureRandom random;
   private final int maxDataSize;
   private MessageDigest md;
   private MessageDigest mgfMd;
   private byte[] lHash;
   private static final Map<String, byte[]> emptyHashes = Collections.synchronizedMap(new HashMap());

   public static RSAPadding getInstance(int var0, int var1) throws InvalidKeyException, InvalidAlgorithmParameterException {
      return new RSAPadding(var0, var1, (SecureRandom)null, (OAEPParameterSpec)null);
   }

   public static RSAPadding getInstance(int var0, int var1, SecureRandom var2) throws InvalidKeyException, InvalidAlgorithmParameterException {
      return new RSAPadding(var0, var1, var2, (OAEPParameterSpec)null);
   }

   public static RSAPadding getInstance(int var0, int var1, SecureRandom var2, OAEPParameterSpec var3) throws InvalidKeyException, InvalidAlgorithmParameterException {
      return new RSAPadding(var0, var1, var2, var3);
   }

   private RSAPadding(int var1, int var2, SecureRandom var3, OAEPParameterSpec var4) throws InvalidKeyException, InvalidAlgorithmParameterException {
      this.type = var1;
      this.paddedSize = var2;
      this.random = var3;
      if (var2 < 64) {
         throw new InvalidKeyException("Padded size must be at least 64");
      } else {
         switch(var1) {
         case 1:
         case 2:
            this.maxDataSize = var2 - 11;
            break;
         case 3:
            this.maxDataSize = var2;
            break;
         case 4:
            String var5 = "SHA-1";
            String var6 = "SHA-1";
            byte[] var7 = null;

            try {
               if (var4 != null) {
                  var5 = var4.getDigestAlgorithm();
                  String var8 = var4.getMGFAlgorithm();
                  if (!var8.equalsIgnoreCase("MGF1")) {
                     throw new InvalidAlgorithmParameterException("Unsupported MGF algo: " + var8);
                  }

                  var6 = ((MGF1ParameterSpec)var4.getMGFParameters()).getDigestAlgorithm();
                  PSource var9 = var4.getPSource();
                  String var10 = var9.getAlgorithm();
                  if (!var10.equalsIgnoreCase("PSpecified")) {
                     throw new InvalidAlgorithmParameterException("Unsupported pSource algo: " + var10);
                  }

                  var7 = ((PSpecified)var9).getValue();
               }

               this.md = MessageDigest.getInstance(var5);
               this.mgfMd = MessageDigest.getInstance(var6);
            } catch (NoSuchAlgorithmException var11) {
               throw new InvalidKeyException("Digest " + var5 + " not available", var11);
            }

            this.lHash = getInitialHash(this.md, var7);
            int var12 = this.lHash.length;
            this.maxDataSize = var2 - 2 - 2 * var12;
            if (this.maxDataSize <= 0) {
               throw new InvalidKeyException("Key is too short for encryption using OAEPPadding with " + var5 + " and MGF1" + var6);
            }
            break;
         default:
            throw new InvalidKeyException("Invalid padding: " + var1);
         }

      }
   }

   private static byte[] getInitialHash(MessageDigest var0, byte[] var1) {
      byte[] var2;
      if (var1 != null && var1.length != 0) {
         var2 = var0.digest(var1);
      } else {
         String var3 = var0.getAlgorithm();
         var2 = (byte[])emptyHashes.get(var3);
         if (var2 == null) {
            var2 = var0.digest();
            emptyHashes.put(var3, var2);
         }
      }

      return var2;
   }

   public int getMaxDataSize() {
      return this.maxDataSize;
   }

   public byte[] pad(byte[] var1, int var2, int var3) throws BadPaddingException {
      return this.pad(RSACore.convert(var1, var2, var3));
   }

   public byte[] pad(byte[] var1) throws BadPaddingException {
      if (var1.length > this.maxDataSize) {
         throw new BadPaddingException("Data must be shorter than " + (this.maxDataSize + 1) + " bytes but received " + var1.length + " bytes.");
      } else {
         switch(this.type) {
         case 1:
         case 2:
            return this.padV15(var1);
         case 3:
            return var1;
         case 4:
            return this.padOAEP(var1);
         default:
            throw new AssertionError();
         }
      }
   }

   public byte[] unpad(byte[] var1, int var2, int var3) throws BadPaddingException {
      return this.unpad(RSACore.convert(var1, var2, var3));
   }

   public byte[] unpad(byte[] var1) throws BadPaddingException {
      if (var1.length != this.paddedSize) {
         throw new BadPaddingException("Decryption error.The padded array length (" + var1.length + ") is not the specified padded size (" + this.paddedSize + ")");
      } else {
         switch(this.type) {
         case 1:
         case 2:
            return this.unpadV15(var1);
         case 3:
            return var1;
         case 4:
            return this.unpadOAEP(var1);
         default:
            throw new AssertionError();
         }
      }
   }

   private byte[] padV15(byte[] var1) throws BadPaddingException {
      byte[] var2 = new byte[this.paddedSize];
      System.arraycopy(var1, 0, var2, this.paddedSize - var1.length, var1.length);
      int var3 = this.paddedSize - 3 - var1.length;
      byte var4 = 0;
      int var8 = var4 + 1;
      var2[var4] = 0;
      var2[var8++] = (byte)this.type;
      if (this.type == 1) {
         while(var3-- > 0) {
            var2[var8++] = -1;
         }
      } else {
         if (this.random == null) {
            this.random = JCAUtil.getSecureRandom();
         }

         byte[] var5 = new byte[64];

         int var7;
         for(int var6 = -1; var3-- > 0; var2[var8++] = (byte)var7) {
            do {
               if (var6 < 0) {
                  this.random.nextBytes(var5);
                  var6 = var5.length - 1;
               }

               var7 = var5[var6--] & 255;
            } while(var7 == 0);
         }
      }

      return var2;
   }

   private byte[] unpadV15(byte[] var1) throws BadPaddingException {
      byte var2 = 0;
      boolean var3 = false;
      int var9 = var2 + 1;
      if (var1[var2] != 0) {
         var3 = true;
      }

      if (var1[var9++] != this.type) {
         var3 = true;
      }

      int var4 = 0;

      int var5;
      while(var9 < var1.length) {
         var5 = var1[var9++] & 255;
         if (var5 == 0 && var4 == 0) {
            var4 = var9;
         }

         if (var9 == var1.length && var4 == 0) {
            var3 = true;
         }

         if (this.type == 1 && var5 != 255 && var4 == 0) {
            var3 = true;
         }
      }

      var5 = var1.length - var4;
      if (var5 > this.maxDataSize) {
         var3 = true;
      }

      byte[] var6 = new byte[var4];
      System.arraycopy(var1, 0, var6, 0, var4);
      byte[] var7 = new byte[var5];
      System.arraycopy(var1, var4, var7, 0, var5);
      BadPaddingException var8 = new BadPaddingException("Decryption error");
      if (var3) {
         throw var8;
      } else {
         return var7;
      }
   }

   private byte[] padOAEP(byte[] var1) throws BadPaddingException {
      if (this.random == null) {
         this.random = JCAUtil.getSecureRandom();
      }

      int var2 = this.lHash.length;
      byte[] var3 = new byte[var2];
      this.random.nextBytes(var3);
      byte[] var4 = new byte[this.paddedSize];
      byte var5 = 1;
      System.arraycopy(var3, 0, var4, var5, var2);
      int var7 = var2 + 1;
      int var8 = var4.length - var7;
      int var9 = this.paddedSize - var1.length;
      System.arraycopy(this.lHash, 0, var4, var7, var2);
      var4[var9 - 1] = 1;
      System.arraycopy(var1, 0, var4, var9, var1.length);
      this.mgf1(var4, var5, var2, var4, var7, var8);
      this.mgf1(var4, var7, var8, var4, var5, var2);
      return var4;
   }

   private byte[] unpadOAEP(byte[] var1) throws BadPaddingException {
      byte[] var2 = var1;
      boolean var3 = false;
      int var4 = this.lHash.length;
      if (var1[0] != 0) {
         var3 = true;
      }

      byte var5 = 1;
      int var7 = var4 + 1;
      int var8 = var1.length - var7;
      this.mgf1(var1, var7, var8, var1, var5, var4);
      this.mgf1(var1, var5, var4, var1, var7, var8);

      int var9;
      for(var9 = 0; var9 < var4; ++var9) {
         if (this.lHash[var9] != var2[var7 + var9]) {
            var3 = true;
         }
      }

      var9 = var7 + var4;
      int var10 = -1;

      int var11;
      for(var11 = var9; var11 < var2.length; ++var11) {
         byte var12 = var2[var11];
         if (var10 == -1 && var12 != 0) {
            if (var12 == 1) {
               var10 = var11;
            } else {
               var3 = true;
            }
         }
      }

      if (var10 == -1) {
         var3 = true;
         var10 = var2.length - 1;
      }

      var11 = var10 + 1;
      byte[] var15 = new byte[var11 - var9];
      System.arraycopy(var2, var9, var15, 0, var15.length);
      byte[] var13 = new byte[var2.length - var11];
      System.arraycopy(var2, var11, var13, 0, var13.length);
      BadPaddingException var14 = new BadPaddingException("Decryption error");
      if (var3) {
         throw var14;
      } else {
         return var13;
      }
   }

   private void mgf1(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) throws BadPaddingException {
      byte[] var7 = new byte[4];
      byte[] var8 = new byte[this.mgfMd.getDigestLength()];

      while(true) {
         int var9;
         do {
            if (var6 <= 0) {
               return;
            }

            this.mgfMd.update(var1, var2, var3);
            this.mgfMd.update(var7);

            try {
               this.mgfMd.digest(var8, 0, var8.length);
            } catch (DigestException var10) {
               throw new BadPaddingException(var10.toString());
            }

            for(var9 = 0; var9 < var8.length && var6 > 0; --var6) {
               int var10001 = var5++;
               var4[var10001] ^= var8[var9++];
            }
         } while(var6 <= 0);

         for(var9 = var7.length - 1; ++var7[var9] == 0 && var9 > 0; --var9) {
         }
      }
   }
}
