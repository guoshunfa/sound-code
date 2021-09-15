package sun.security.krb5.internal.crypto.dk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.Cipher;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.KeyUsage;

public abstract class DkCrypto {
   protected static final boolean debug = false;
   static final byte[] KERBEROS_CONSTANT = new byte[]{107, 101, 114, 98, 101, 114, 111, 115};

   protected abstract int getKeySeedLength();

   protected abstract byte[] randomToKey(byte[] var1);

   protected abstract Cipher getCipher(byte[] var1, byte[] var2, int var3) throws GeneralSecurityException;

   public abstract int getChecksumLength();

   protected abstract byte[] getHmac(byte[] var1, byte[] var2) throws GeneralSecurityException;

   public byte[] encrypt(byte[] var1, int var2, byte[] var3, byte[] var4, byte[] var5, int var6, int var7) throws GeneralSecurityException, KrbCryptoException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var8 = null;
         byte[] var9 = null;

         byte[] var20;
         try {
            byte[] var10 = new byte[]{(byte)(var2 >> 24 & 255), (byte)(var2 >> 16 & 255), (byte)(var2 >> 8 & 255), (byte)(var2 & 255), -86};
            var8 = this.dk(var1, var10);
            Cipher var11 = this.getCipher(var8, var3, 1);
            int var12 = var11.getBlockSize();
            byte[] var13 = Confounder.bytes(var12);
            int var14 = this.roundup(var13.length + var7, var12);
            byte[] var15 = new byte[var14];
            System.arraycopy(var13, 0, var15, 0, var13.length);
            System.arraycopy(var5, var6, var15, var13.length, var7);
            Arrays.fill((byte[])var15, var13.length + var7, var14, (byte)0);
            int var16 = var11.getOutputSize(var14);
            int var17 = var16 + this.getChecksumLength();
            byte[] var18 = new byte[var17];
            var11.doFinal(var15, 0, var14, var18, 0);
            if (var4 != null && var4.length == var12) {
               System.arraycopy(var18, var16 - var12, var4, 0, var12);
            }

            var10[4] = 85;
            var9 = this.dk(var1, var10);
            byte[] var19 = this.getHmac(var9, var15);
            System.arraycopy(var19, 0, var18, var16, this.getChecksumLength());
            var20 = var18;
         } finally {
            if (var8 != null) {
               Arrays.fill((byte[])var8, 0, var8.length, (byte)0);
            }

            if (var9 != null) {
               Arrays.fill((byte[])var9, 0, var9.length, (byte)0);
            }

         }

         return var20;
      }
   }

   public byte[] encryptRaw(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException, KrbCryptoException {
      Cipher var7 = this.getCipher(var1, var3, 1);
      int var8 = var7.getBlockSize();
      if (var6 % var8 != 0) {
         throw new GeneralSecurityException("length of data to be encrypted (" + var6 + ") is not a multiple of the blocksize (" + var8 + ")");
      } else {
         int var9 = var7.getOutputSize(var6);
         byte[] var10 = new byte[var9];
         var7.doFinal(var4, 0, var6, var10, 0);
         return var10;
      }
   }

   public byte[] decryptRaw(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException {
      Cipher var7 = this.getCipher(var1, var3, 2);
      int var8 = var7.getBlockSize();
      if (var6 % var8 != 0) {
         throw new GeneralSecurityException("length of data to be decrypted (" + var6 + ") is not a multiple of the blocksize (" + var8 + ")");
      } else {
         byte[] var9 = var7.doFinal(var4, var5, var6);
         return var9;
      }
   }

   public byte[] decrypt(byte[] var1, int var2, byte[] var3, byte[] var4, int var5, int var6) throws GeneralSecurityException {
      if (!KeyUsage.isValid(var2)) {
         throw new GeneralSecurityException("Invalid key usage number: " + var2);
      } else {
         byte[] var7 = null;
         byte[] var8 = null;

         byte[] var18;
         try {
            byte[] var9 = new byte[]{(byte)(var2 >> 24 & 255), (byte)(var2 >> 16 & 255), (byte)(var2 >> 8 & 255), (byte)(var2 & 255), -86};
            var7 = this.dk(var1, var9);
            Cipher var10 = this.getCipher(var7, var3, 2);
            int var11 = var10.getBlockSize();
            int var12 = this.getChecksumLength();
            int var13 = var6 - var12;
            byte[] var14 = var10.doFinal(var4, var5, var13);
            var9[4] = 85;
            var8 = this.dk(var1, var9);
            byte[] var15 = this.getHmac(var8, var14);
            boolean var16 = false;
            if (var15.length >= var12) {
               for(int var17 = 0; var17 < var12; ++var17) {
                  if (var15[var17] != var4[var13 + var17]) {
                     var16 = true;
                     break;
                  }
               }
            }

            if (var16) {
               throw new GeneralSecurityException("Checksum failed");
            }

            if (var3 != null && var3.length == var11) {
               System.arraycopy(var4, var5 + var13 - var11, var3, 0, var11);
            }

            byte[] var22 = new byte[var14.length - var11];
            System.arraycopy(var14, var11, var22, 0, var22.length);
            var18 = var22;
         } finally {
            if (var7 != null) {
               Arrays.fill((byte[])var7, 0, var7.length, (byte)0);
            }

            if (var8 != null) {
               Arrays.fill((byte[])var8, 0, var8.length, (byte)0);
            }

         }

         return var18;
      }
   }

   int roundup(int var1, int var2) {
      return (var1 + var2 - 1) / var2 * var2;
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

   byte[] dk(byte[] var1, byte[] var2) throws GeneralSecurityException {
      return this.randomToKey(this.dr(var1, var2));
   }

   private byte[] dr(byte[] var1, byte[] var2) throws GeneralSecurityException {
      Cipher var3 = this.getCipher(var1, (byte[])null, 1);
      int var4 = var3.getBlockSize();
      if (var2.length != var4) {
         var2 = nfold(var2, var4 * 8);
      }

      byte[] var5 = var2;
      int var6 = this.getKeySeedLength() >> 3;
      byte[] var7 = new byte[var6];
      boolean var8 = false;

      byte[] var11;
      for(int var9 = 0; var9 < var6; var5 = var11) {
         var11 = var3.doFinal(var5);
         int var10 = var6 - var9 <= var11.length ? var6 - var9 : var11.length;
         System.arraycopy(var11, 0, var7, var9, var10);
         var9 += var10;
      }

      return var7;
   }

   static byte[] nfold(byte[] var0, int var1) {
      int var2 = var0.length;
      var1 >>= 3;
      int var3 = var1;

      int var5;
      for(int var4 = var2; var4 != 0; var3 = var5) {
         var5 = var4;
         var4 = var3 % var4;
      }

      int var6 = var1 * var2 / var3;
      byte[] var7 = new byte[var1];
      Arrays.fill((byte[])var7, (byte)0);
      int var8 = 0;

      int var10;
      for(var10 = var6 - 1; var10 >= 0; --var10) {
         int var9 = ((var2 << 3) - 1 + ((var2 << 3) + 13) * (var10 / var2) + (var2 - var10 % var2 << 3)) % (var2 << 3);
         int var11 = ((var0[(var2 - 1 - (var9 >>> 3)) % var2] & 255) << 8 | var0[(var2 - (var9 >>> 3)) % var2] & 255) >>> (var9 & 7) + 1 & 255;
         var8 += var11;
         int var12 = var7[var10 % var1] & 255;
         var8 += var12;
         var7[var10 % var1] = (byte)(var8 & 255);
         var8 >>>= 8;
      }

      if (var8 != 0) {
         for(var10 = var1 - 1; var10 >= 0; --var10) {
            var8 += var7[var10] & 255;
            var7[var10] = (byte)(var8 & 255);
            var8 >>>= 8;
         }
      }

      return var7;
   }

   static String bytesToString(byte[] var0) {
      StringBuffer var1 = new StringBuffer();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if ((var0[var2] & 255) < 16) {
            var1.append("0" + Integer.toHexString(var0[var2] & 255));
         } else {
            var1.append(Integer.toHexString(var0[var2] & 255));
         }
      }

      return var1.toString();
   }

   private static byte[] binaryStringToBytes(String var0) {
      char[] var1 = var0.toCharArray();
      byte[] var2 = new byte[var1.length / 2];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         byte var4 = Byte.parseByte(new String(var1, var3 * 2, 1), 16);
         byte var5 = Byte.parseByte(new String(var1, var3 * 2 + 1, 1), 16);
         var2[var3] = (byte)(var4 << 4 | var5);
      }

      return var2;
   }

   static void traceOutput(String var0, byte[] var1, int var2, int var3) {
      try {
         ByteArrayOutputStream var4 = new ByteArrayOutputStream(var3);
         (new HexDumpEncoder()).encodeBuffer(new ByteArrayInputStream(var1, var2, var3), var4);
         System.err.println(var0 + ":" + var4.toString());
      } catch (Exception var5) {
      }

   }

   static byte[] charToUtf8(char[] var0) {
      Charset var1 = Charset.forName("UTF-8");
      CharBuffer var2 = CharBuffer.wrap(var0);
      ByteBuffer var3 = var1.encode(var2);
      int var4 = var3.limit();
      byte[] var5 = new byte[var4];
      var3.get(var5, 0, var4);
      return var5;
   }

   static byte[] charToUtf16(char[] var0) {
      Charset var1 = Charset.forName("UTF-16LE");
      CharBuffer var2 = CharBuffer.wrap(var0);
      ByteBuffer var3 = var1.encode(var2);
      int var4 = var3.limit();
      byte[] var5 = new byte[var4];
      var3.get(var5, 0, var4);
      return var5;
   }
}
