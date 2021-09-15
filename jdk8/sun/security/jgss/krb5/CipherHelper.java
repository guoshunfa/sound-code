package sun.security.jgss.krb5;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.ietf.jgss.GSSException;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.internal.crypto.Aes128;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.ArcFourHmac;
import sun.security.krb5.internal.crypto.Des3;

class CipherHelper {
   private static final int KG_USAGE_SEAL = 22;
   private static final int KG_USAGE_SIGN = 23;
   private static final int KG_USAGE_SEQ = 24;
   private static final int DES_CHECKSUM_SIZE = 8;
   private static final int DES_IV_SIZE = 8;
   private static final int AES_IV_SIZE = 16;
   private static final int HMAC_CHECKSUM_SIZE = 8;
   private static final int KG_USAGE_SIGN_MS = 15;
   private static final boolean DEBUG;
   private static final byte[] ZERO_IV;
   private static final byte[] ZERO_IV_AES;
   private int etype;
   private int sgnAlg;
   private int sealAlg;
   private byte[] keybytes;
   private int proto = 0;

   CipherHelper(EncryptionKey var1) throws GSSException {
      this.etype = var1.getEType();
      this.keybytes = var1.getBytes();
      switch(this.etype) {
      case 1:
      case 3:
         this.sgnAlg = 0;
         this.sealAlg = 0;
         break;
      case 16:
         this.sgnAlg = 1024;
         this.sealAlg = 512;
         break;
      case 17:
      case 18:
         this.sgnAlg = -1;
         this.sealAlg = -1;
         this.proto = 1;
         break;
      case 23:
         this.sgnAlg = 4352;
         this.sealAlg = 4096;
         break;
      default:
         throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
      }

   }

   int getSgnAlg() {
      return this.sgnAlg;
   }

   int getSealAlg() {
      return this.sealAlg;
   }

   int getProto() {
      return this.proto;
   }

   int getEType() {
      return this.etype;
   }

   boolean isArcFour() {
      boolean var1 = false;
      if (this.etype == 23) {
         var1 = true;
      }

      return var1;
   }

   byte[] calculateChecksum(int var1, byte[] var2, byte[] var3, byte[] var4, int var5, int var6, int var7) throws GSSException {
      byte[] var11;
      switch(var1) {
      case 0:
         try {
            MessageDigest var21 = MessageDigest.getInstance("MD5");
            var21.update(var2);
            var21.update(var4, var5, var6);
            if (var3 != null) {
               var21.update(var3);
            }

            var4 = var21.digest();
            var5 = 0;
            var6 = var4.length;
            var2 = null;
            Object var20 = null;
         } catch (NoSuchAlgorithmException var19) {
            GSSException var22 = new GSSException(11, -1, "Could not get MD5 Message Digest - " + var19.getMessage());
            var22.initCause(var19);
            throw var22;
         }
      case 512:
         return this.getDesCbcChecksum(this.keybytes, var2, var4, var5, var6);
      case 1024:
         byte[] var8;
         int var9;
         int var10;
         if (var2 == null && var3 == null) {
            var8 = var4;
            var10 = var6;
            var9 = var5;
         } else {
            var10 = (var2 != null ? var2.length : 0) + var6 + (var3 != null ? var3.length : 0);
            var8 = new byte[var10];
            int var23 = 0;
            if (var2 != null) {
               System.arraycopy(var2, 0, var8, 0, var2.length);
               var23 = var2.length;
            }

            System.arraycopy(var4, var5, var8, var23, var6);
            var23 += var6;
            if (var3 != null) {
               System.arraycopy(var3, 0, var8, var23, var3.length);
            }

            var9 = 0;
         }

         try {
            var11 = Des3.calculateChecksum(this.keybytes, 23, var8, var9, var10);
            return var11;
         } catch (GeneralSecurityException var18) {
            GSSException var24 = new GSSException(11, -1, "Could not use HMAC-SHA1-DES3-KD signing algorithm - " + var18.getMessage());
            var24.initCause(var18);
            throw var24;
         }
      case 4352:
         int var12;
         int var13;
         if (var2 == null && var3 == null) {
            var11 = var4;
            var13 = var6;
            var12 = var5;
         } else {
            var13 = (var2 != null ? var2.length : 0) + var6 + (var3 != null ? var3.length : 0);
            var11 = new byte[var13];
            int var14 = 0;
            if (var2 != null) {
               System.arraycopy(var2, 0, var11, 0, var2.length);
               var14 = var2.length;
            }

            System.arraycopy(var4, var5, var11, var14, var6);
            var14 += var6;
            if (var3 != null) {
               System.arraycopy(var3, 0, var11, var14, var3.length);
            }

            var12 = 0;
         }

         try {
            byte var25 = 23;
            if (var7 == 257) {
               var25 = 15;
            }

            byte[] var26 = ArcFourHmac.calculateChecksum(this.keybytes, var25, var11, var12, var13);
            byte[] var16 = new byte[this.getChecksumLength()];
            System.arraycopy(var26, 0, var16, 0, var16.length);
            return var16;
         } catch (GeneralSecurityException var17) {
            GSSException var15 = new GSSException(11, -1, "Could not use HMAC_MD5_ARCFOUR signing algorithm - " + var17.getMessage());
            var15.initCause(var17);
            throw var15;
         }
      default:
         throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
      }
   }

   byte[] calculateChecksum(byte[] var1, byte[] var2, int var3, int var4, int var5) throws GSSException {
      int var6 = (var1 != null ? var1.length : 0) + var4;
      byte[] var7 = new byte[var6];
      System.arraycopy(var2, var3, var7, 0, var4);
      if (var1 != null) {
         System.arraycopy(var1, 0, var7, var4, var1.length);
      }

      byte[] var8;
      GSSException var9;
      switch(this.etype) {
      case 17:
         try {
            var8 = Aes128.calculateChecksum(this.keybytes, var5, var7, 0, var6);
            return var8;
         } catch (GeneralSecurityException var11) {
            var9 = new GSSException(11, -1, "Could not use AES128 signing algorithm - " + var11.getMessage());
            var9.initCause(var11);
            throw var9;
         }
      case 18:
         try {
            var8 = Aes256.calculateChecksum(this.keybytes, var5, var7, 0, var6);
            return var8;
         } catch (GeneralSecurityException var10) {
            var9 = new GSSException(11, -1, "Could not use AES256 signing algorithm - " + var10.getMessage());
            var9.initCause(var10);
            throw var9;
         }
      default:
         throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
      }
   }

   byte[] encryptSeq(byte[] var1, byte[] var2, int var3, int var4) throws GSSException {
      switch(this.sgnAlg) {
      case 0:
      case 512:
         try {
            Cipher var12 = this.getInitializedDes(true, this.keybytes, var1);
            return var12.doFinal(var2, var3, var4);
         } catch (GeneralSecurityException var11) {
            GSSException var13 = new GSSException(11, -1, "Could not encrypt sequence number using DES - " + var11.getMessage());
            var13.initCause(var11);
            throw var13;
         }
      case 1024:
         byte[] var5;
         if (var1.length == 8) {
            var5 = var1;
         } else {
            var5 = new byte[8];
            System.arraycopy(var1, 0, var5, 0, 8);
         }

         try {
            return Des3.encryptRaw(this.keybytes, 24, var5, var2, var3, var4);
         } catch (Exception var10) {
            GSSException var7 = new GSSException(11, -1, "Could not encrypt sequence number using DES3-KD - " + var10.getMessage());
            var7.initCause(var10);
            throw var7;
         }
      case 4352:
         byte[] var6;
         if (var1.length == 8) {
            var6 = var1;
         } else {
            var6 = new byte[8];
            System.arraycopy(var1, 0, var6, 0, 8);
         }

         try {
            return ArcFourHmac.encryptSeq(this.keybytes, 24, var6, var2, var3, var4);
         } catch (Exception var9) {
            GSSException var8 = new GSSException(11, -1, "Could not encrypt sequence number using RC4-HMAC - " + var9.getMessage());
            var8.initCause(var9);
            throw var8;
         }
      default:
         throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
      }
   }

   byte[] decryptSeq(byte[] var1, byte[] var2, int var3, int var4) throws GSSException {
      switch(this.sgnAlg) {
      case 0:
      case 512:
         try {
            Cipher var12 = this.getInitializedDes(false, this.keybytes, var1);
            return var12.doFinal(var2, var3, var4);
         } catch (GeneralSecurityException var11) {
            GSSException var13 = new GSSException(11, -1, "Could not decrypt sequence number using DES - " + var11.getMessage());
            var13.initCause(var11);
            throw var13;
         }
      case 1024:
         byte[] var5;
         if (var1.length == 8) {
            var5 = var1;
         } else {
            var5 = new byte[8];
            System.arraycopy(var1, 0, var5, 0, 8);
         }

         try {
            return Des3.decryptRaw(this.keybytes, 24, var5, var2, var3, var4);
         } catch (Exception var10) {
            GSSException var7 = new GSSException(11, -1, "Could not decrypt sequence number using DES3-KD - " + var10.getMessage());
            var7.initCause(var10);
            throw var7;
         }
      case 4352:
         byte[] var6;
         if (var1.length == 8) {
            var6 = var1;
         } else {
            var6 = new byte[8];
            System.arraycopy(var1, 0, var6, 0, 8);
         }

         try {
            return ArcFourHmac.decryptSeq(this.keybytes, 24, var6, var2, var3, var4);
         } catch (Exception var9) {
            GSSException var8 = new GSSException(11, -1, "Could not decrypt sequence number using RC4-HMAC - " + var9.getMessage());
            var8.initCause(var9);
            throw var8;
         }
      default:
         throw new GSSException(11, -1, "Unsupported signing algorithm: " + this.sgnAlg);
      }
   }

   int getChecksumLength() throws GSSException {
      switch(this.etype) {
      case 1:
      case 3:
         return 8;
      case 16:
         return Des3.getChecksumLength();
      case 17:
         return Aes128.getChecksumLength();
      case 18:
         return Aes256.getChecksumLength();
      case 23:
         return 8;
      default:
         throw new GSSException(11, -1, "Unsupported encryption type: " + this.etype);
      }
   }

   void decryptData(WrapToken var1, byte[] var2, int var3, int var4, byte[] var5, int var6) throws GSSException {
      switch(this.sealAlg) {
      case 0:
         this.desCbcDecrypt(var1, getDesEncryptionKey(this.keybytes), var2, var3, var4, var5, var6);
         break;
      case 512:
         this.des3KdDecrypt(var1, var2, var3, var4, var5, var6);
         break;
      case 4096:
         this.arcFourDecrypt(var1, var2, var3, var4, var5, var6);
         break;
      default:
         throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
      }

   }

   void decryptData(WrapToken_v2 var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws GSSException {
      switch(this.etype) {
      case 17:
         this.aes128Decrypt(var1, var2, var3, var4, var5, var6, var7);
         break;
      case 18:
         this.aes256Decrypt(var1, var2, var3, var4, var5, var6, var7);
         break;
      default:
         throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
      }

   }

   void decryptData(WrapToken var1, InputStream var2, int var3, byte[] var4, int var5) throws GSSException, IOException {
      switch(this.sealAlg) {
      case 0:
         this.desCbcDecrypt(var1, getDesEncryptionKey(this.keybytes), var2, var3, var4, var5);
         break;
      case 512:
         byte[] var6 = new byte[var3];

         try {
            Krb5Token.readFully(var2, var6, 0, var3);
         } catch (IOException var11) {
            GSSException var8 = new GSSException(10, -1, "Cannot read complete token");
            var8.initCause(var11);
            throw var8;
         }

         this.des3KdDecrypt(var1, var6, 0, var3, var4, var5);
         break;
      case 4096:
         byte[] var7 = new byte[var3];

         try {
            Krb5Token.readFully(var2, var7, 0, var3);
         } catch (IOException var10) {
            GSSException var9 = new GSSException(10, -1, "Cannot read complete token");
            var9.initCause(var10);
            throw var9;
         }

         this.arcFourDecrypt(var1, var7, 0, var3, var4, var5);
         break;
      default:
         throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
      }

   }

   void decryptData(WrapToken_v2 var1, InputStream var2, int var3, byte[] var4, int var5, int var6) throws GSSException, IOException {
      byte[] var7 = new byte[var3];

      try {
         Krb5Token.readFully(var2, var7, 0, var3);
      } catch (IOException var10) {
         GSSException var9 = new GSSException(10, -1, "Cannot read complete token");
         var9.initCause(var10);
         throw var9;
      }

      switch(this.etype) {
      case 17:
         this.aes128Decrypt(var1, var7, 0, var3, var4, var5, var6);
         break;
      case 18:
         this.aes256Decrypt(var1, var7, 0, var3, var4, var5, var6);
         break;
      default:
         throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
      }

   }

   void encryptData(WrapToken var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6, OutputStream var7) throws GSSException, IOException {
      switch(this.sealAlg) {
      case 0:
         Cipher var8 = this.getInitializedDes(true, getDesEncryptionKey(this.keybytes), ZERO_IV);
         CipherOutputStream var9 = new CipherOutputStream(var7, var8);
         var9.write(var2);
         var9.write(var3, var4, var5);
         var9.write(var6);
         break;
      case 512:
         byte[] var10 = this.des3KdEncrypt(var2, var3, var4, var5, var6);
         var7.write(var10);
         break;
      case 4096:
         byte[] var11 = this.arcFourEncrypt(var1, var2, var3, var4, var5, var6);
         var7.write(var11);
         break;
      default:
         throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
      }

   }

   byte[] encryptData(WrapToken_v2 var1, byte[] var2, byte[] var3, byte[] var4, int var5, int var6, int var7) throws GSSException {
      switch(this.etype) {
      case 17:
         return this.aes128Encrypt(var2, var3, var4, var5, var6, var7);
      case 18:
         return this.aes256Encrypt(var2, var3, var4, var5, var6, var7);
      default:
         throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
      }
   }

   void encryptData(WrapToken var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6, byte[] var7, int var8) throws GSSException {
      switch(this.sealAlg) {
      case 0:
         int var9 = var8;
         Cipher var10 = this.getInitializedDes(true, getDesEncryptionKey(this.keybytes), ZERO_IV);

         try {
            var9 += var10.update(var2, 0, var2.length, var7, var9);
            var9 += var10.update(var3, var4, var5, var7, var9);
            var10.update(var6, 0, var6.length, var7, var9);
            var10.doFinal();
            break;
         } catch (GeneralSecurityException var13) {
            GSSException var14 = new GSSException(11, -1, "Could not use DES Cipher - " + var13.getMessage());
            var14.initCause(var13);
            throw var14;
         }
      case 512:
         byte[] var11 = this.des3KdEncrypt(var2, var3, var4, var5, var6);
         System.arraycopy(var11, 0, var7, var8, var11.length);
         break;
      case 4096:
         byte[] var12 = this.arcFourEncrypt(var1, var2, var3, var4, var5, var6);
         System.arraycopy(var12, 0, var7, var8, var12.length);
         break;
      default:
         throw new GSSException(11, -1, "Unsupported seal algorithm: " + this.sealAlg);
      }

   }

   int encryptData(WrapToken_v2 var1, byte[] var2, byte[] var3, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9) throws GSSException {
      Object var10 = null;
      byte[] var11;
      switch(this.etype) {
      case 17:
         var11 = this.aes128Encrypt(var2, var3, var4, var5, var6, var9);
         break;
      case 18:
         var11 = this.aes256Encrypt(var2, var3, var4, var5, var6, var9);
         break;
      default:
         throw new GSSException(11, -1, "Unsupported etype: " + this.etype);
      }

      System.arraycopy(var11, 0, var7, var8, var11.length);
      return var11.length;
   }

   private byte[] getDesCbcChecksum(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) throws GSSException {
      Cipher var6 = this.getInitializedDes(true, var1, ZERO_IV);
      int var7 = var6.getBlockSize();
      byte[] var8 = new byte[var7];
      int var9 = var5 / var7;
      int var10 = var5 % var7;
      if (var10 == 0) {
         --var9;
         System.arraycopy(var3, var4 + var9 * var7, var8, 0, var7);
      } else {
         System.arraycopy(var3, var4 + var9 * var7, var8, 0, var10);
      }

      try {
         byte[] var11 = new byte[Math.max(var7, var2 == null ? var7 : var2.length)];
         if (var2 != null) {
            var6.update(var2, 0, var2.length, var11, 0);
         }

         for(int var14 = 0; var14 < var9; ++var14) {
            var6.update(var3, var4, var7, var11, 0);
            var4 += var7;
         }

         byte[] var15 = new byte[var7];
         var6.update(var8, 0, var7, var15, 0);
         var6.doFinal();
         return var15;
      } catch (GeneralSecurityException var13) {
         GSSException var12 = new GSSException(11, -1, "Could not use DES Cipher - " + var13.getMessage());
         var12.initCause(var13);
         throw var12;
      }
   }

   private final Cipher getInitializedDes(boolean var1, byte[] var2, byte[] var3) throws GSSException {
      try {
         IvParameterSpec var4 = new IvParameterSpec(var3);
         SecretKeySpec var8 = new SecretKeySpec(var2, "DES");
         Cipher var6 = Cipher.getInstance("DES/CBC/NoPadding");
         var6.init(var1 ? 1 : 2, var8, var4);
         return var6;
      } catch (GeneralSecurityException var7) {
         GSSException var5 = new GSSException(11, -1, var7.getMessage());
         var5.initCause(var7);
         throw var5;
      }
   }

   private void desCbcDecrypt(WrapToken var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6, int var7) throws GSSException {
      try {
         boolean var8 = false;
         Cipher var15 = this.getInitializedDes(false, var2, ZERO_IV);
         var15.update(var3, var4, 8, var1.confounder);
         var4 += 8;
         var5 -= 8;
         int var10 = var15.getBlockSize();
         int var11 = var5 / var10 - 1;

         for(int var12 = 0; var12 < var11; ++var12) {
            var15.update(var3, var4, var10, var6, var7);
            var4 += var10;
            var7 += var10;
         }

         byte[] var16 = new byte[var10];
         var15.update(var3, var4, var10, var16);
         var15.doFinal();
         byte var13 = var16[var10 - 1];
         if (var13 >= 1 && var13 <= 8) {
            var1.padding = WrapToken.pads[var13];
            var10 -= var13;
            System.arraycopy(var16, 0, var6, var7, var10);
         } else {
            throw new GSSException(10, -1, "Invalid padding on Wrap Token");
         }
      } catch (GeneralSecurityException var14) {
         GSSException var9 = new GSSException(11, -1, "Could not use DES cipher - " + var14.getMessage());
         var9.initCause(var14);
         throw var9;
      }
   }

   private void desCbcDecrypt(WrapToken var1, byte[] var2, InputStream var3, int var4, byte[] var5, int var6) throws GSSException, IOException {
      boolean var7 = false;
      Cipher var8 = this.getInitializedDes(false, var2, ZERO_IV);
      CipherHelper.WrapTokenInputStream var9 = new CipherHelper.WrapTokenInputStream(var3, var4);
      CipherInputStream var10 = new CipherInputStream(var9, var8);
      int var17 = var10.read(var1.confounder);
      var4 -= var17;
      int var11 = var8.getBlockSize();
      int var12 = var4 / var11 - 1;

      for(int var13 = 0; var13 < var12; ++var13) {
         var10.read(var5, var6, var11);
         var6 += var11;
      }

      byte[] var18 = new byte[var11];
      var10.read(var18);

      try {
         var8.doFinal();
      } catch (GeneralSecurityException var16) {
         GSSException var15 = new GSSException(11, -1, "Could not use DES cipher - " + var16.getMessage());
         var15.initCause(var16);
         throw var15;
      }

      byte var14 = var18[var11 - 1];
      if (var14 >= 1 && var14 <= 8) {
         var1.padding = WrapToken.pads[var14];
         var11 -= var14;
         System.arraycopy(var18, 0, var5, var6, var11);
      } else {
         throw new GSSException(10, -1, "Invalid padding on Wrap Token");
      }
   }

   private static byte[] getDesEncryptionKey(byte[] var0) throws GSSException {
      if (var0.length > 8) {
         throw new GSSException(11, -100, "Invalid DES Key!");
      } else {
         byte[] var1 = new byte[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = (byte)(var0[var2] ^ 240);
         }

         return var1;
      }
   }

   private void des3KdDecrypt(WrapToken var1, byte[] var2, int var3, int var4, byte[] var5, int var6) throws GSSException {
      byte[] var7;
      try {
         var7 = Des3.decryptRaw(this.keybytes, 22, ZERO_IV, var2, var3, var4);
      } catch (GeneralSecurityException var10) {
         GSSException var9 = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + var10.getMessage());
         var9.initCause(var10);
         throw var9;
      }

      byte var8 = var7[var7.length - 1];
      if (var8 >= 1 && var8 <= 8) {
         var1.padding = WrapToken.pads[var8];
         int var11 = var7.length - 8 - var8;
         System.arraycopy(var7, 8, var5, var6, var11);
         System.arraycopy(var7, 0, var1.confounder, 0, 8);
      } else {
         throw new GSSException(10, -1, "Invalid padding on Wrap Token");
      }
   }

   private byte[] des3KdEncrypt(byte[] var1, byte[] var2, int var3, int var4, byte[] var5) throws GSSException {
      byte[] var6 = new byte[var1.length + var4 + var5.length];
      System.arraycopy(var1, 0, var6, 0, var1.length);
      System.arraycopy(var2, var3, var6, var1.length, var4);
      System.arraycopy(var5, 0, var6, var1.length + var4, var5.length);

      try {
         byte[] var7 = Des3.encryptRaw(this.keybytes, 22, ZERO_IV, var6, 0, var6.length);
         return var7;
      } catch (Exception var9) {
         GSSException var8 = new GSSException(11, -1, "Could not use DES3-KD Cipher - " + var9.getMessage());
         var8.initCause(var9);
         throw var8;
      }
   }

   private void arcFourDecrypt(WrapToken var1, byte[] var2, int var3, int var4, byte[] var5, int var6) throws GSSException {
      byte[] var7 = this.decryptSeq(var1.getChecksum(), var1.getEncSeqNumber(), 0, 8);

      byte[] var8;
      try {
         var8 = ArcFourHmac.decryptRaw(this.keybytes, 22, ZERO_IV, var2, var3, var4, var7);
      } catch (GeneralSecurityException var11) {
         GSSException var10 = new GSSException(11, -1, "Could not use ArcFour Cipher - " + var11.getMessage());
         var10.initCause(var11);
         throw var10;
      }

      byte var9 = var8[var8.length - 1];
      if (var9 < 1) {
         throw new GSSException(10, -1, "Invalid padding on Wrap Token");
      } else {
         var1.padding = WrapToken.pads[var9];
         int var12 = var8.length - 8 - var9;
         System.arraycopy(var8, 8, var5, var6, var12);
         System.arraycopy(var8, 0, var1.confounder, 0, 8);
      }
   }

   private byte[] arcFourEncrypt(WrapToken var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws GSSException {
      byte[] var7 = new byte[var2.length + var5 + var6.length];
      System.arraycopy(var2, 0, var7, 0, var2.length);
      System.arraycopy(var3, var4, var7, var2.length, var5);
      System.arraycopy(var6, 0, var7, var2.length + var5, var6.length);
      byte[] var8 = new byte[4];
      WrapToken.writeBigEndian(var1.getSequenceNumber(), var8);

      try {
         byte[] var9 = ArcFourHmac.encryptRaw(this.keybytes, 22, var8, var7, 0, var7.length);
         return var9;
      } catch (Exception var11) {
         GSSException var10 = new GSSException(11, -1, "Could not use ArcFour Cipher - " + var11.getMessage());
         var10.initCause(var11);
         throw var10;
      }
   }

   private byte[] aes128Encrypt(byte[] var1, byte[] var2, byte[] var3, int var4, int var5, int var6) throws GSSException {
      byte[] var7 = new byte[var1.length + var5 + var2.length];
      System.arraycopy(var1, 0, var7, 0, var1.length);
      System.arraycopy(var3, var4, var7, var1.length, var5);
      System.arraycopy(var2, 0, var7, var1.length + var5, var2.length);

      try {
         byte[] var8 = Aes128.encryptRaw(this.keybytes, var6, ZERO_IV_AES, var7, 0, var7.length);
         return var8;
      } catch (Exception var10) {
         GSSException var9 = new GSSException(11, -1, "Could not use AES128 Cipher - " + var10.getMessage());
         var9.initCause(var10);
         throw var9;
      }
   }

   private void aes128Decrypt(WrapToken_v2 var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws GSSException {
      Object var8 = null;

      byte[] var12;
      try {
         var12 = Aes128.decryptRaw(this.keybytes, var7, ZERO_IV_AES, var2, var3, var4);
      } catch (GeneralSecurityException var11) {
         GSSException var10 = new GSSException(11, -1, "Could not use AES128 Cipher - " + var11.getMessage());
         var10.initCause(var11);
         throw var10;
      }

      int var9 = var12.length - 16 - 16;
      System.arraycopy(var12, 16, var5, var6, var9);
   }

   private byte[] aes256Encrypt(byte[] var1, byte[] var2, byte[] var3, int var4, int var5, int var6) throws GSSException {
      byte[] var7 = new byte[var1.length + var5 + var2.length];
      System.arraycopy(var1, 0, var7, 0, var1.length);
      System.arraycopy(var3, var4, var7, var1.length, var5);
      System.arraycopy(var2, 0, var7, var1.length + var5, var2.length);

      try {
         byte[] var8 = Aes256.encryptRaw(this.keybytes, var6, ZERO_IV_AES, var7, 0, var7.length);
         return var8;
      } catch (Exception var10) {
         GSSException var9 = new GSSException(11, -1, "Could not use AES256 Cipher - " + var10.getMessage());
         var9.initCause(var10);
         throw var9;
      }
   }

   private void aes256Decrypt(WrapToken_v2 var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws GSSException {
      byte[] var8;
      try {
         var8 = Aes256.decryptRaw(this.keybytes, var7, ZERO_IV_AES, var2, var3, var4);
      } catch (GeneralSecurityException var11) {
         GSSException var10 = new GSSException(11, -1, "Could not use AES128 Cipher - " + var11.getMessage());
         var10.initCause(var11);
         throw var10;
      }

      int var9 = var8.length - 16 - 16;
      System.arraycopy(var8, 16, var5, var6, var9);
   }

   static {
      DEBUG = Krb5Util.DEBUG;
      ZERO_IV = new byte[8];
      ZERO_IV_AES = new byte[16];
   }

   class WrapTokenInputStream extends InputStream {
      private InputStream is;
      private int length;
      private int remaining;
      private int temp;

      public WrapTokenInputStream(InputStream var2, int var3) {
         this.is = var2;
         this.length = var3;
         this.remaining = var3;
      }

      public final int read() throws IOException {
         if (this.remaining == 0) {
            return -1;
         } else {
            this.temp = this.is.read();
            if (this.temp != -1) {
               this.remaining -= this.temp;
            }

            return this.temp;
         }
      }

      public final int read(byte[] var1) throws IOException {
         if (this.remaining == 0) {
            return -1;
         } else {
            this.temp = Math.min(this.remaining, var1.length);
            this.temp = this.is.read(var1, 0, this.temp);
            if (this.temp != -1) {
               this.remaining -= this.temp;
            }

            return this.temp;
         }
      }

      public final int read(byte[] var1, int var2, int var3) throws IOException {
         if (this.remaining == 0) {
            return -1;
         } else {
            this.temp = Math.min(this.remaining, var3);
            this.temp = this.is.read(var1, var2, this.temp);
            if (this.temp != -1) {
               this.remaining -= this.temp;
            }

            return this.temp;
         }
      }

      public final long skip(long var1) throws IOException {
         if (this.remaining == 0) {
            return 0L;
         } else {
            this.temp = (int)Math.min((long)this.remaining, var1);
            this.temp = (int)this.is.skip((long)this.temp);
            this.remaining -= this.temp;
            return (long)this.temp;
         }
      }

      public final int available() throws IOException {
         return Math.min(this.remaining, this.is.available());
      }

      public final void close() throws IOException {
         this.remaining = 0;
      }
   }
}
