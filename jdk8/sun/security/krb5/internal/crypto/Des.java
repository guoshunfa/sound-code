package sun.security.krb5.internal.crypto;

import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.KrbCryptoException;

public final class Des {
   private static final String CHARSET = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.krb5.msinterop.des.s2kcharset")));
   private static final long[] bad_keys = new long[]{72340172838076673L, -72340172838076674L, 2242545357980376863L, -2242545357980376864L, 143554428589179390L, -143554428589179391L, 2296870857142767345L, -2296870857142767346L, 135110050437988849L, -2305315235293957887L, 2305315235293957886L, -135110050437988850L, 80784550989267214L, 2234100979542855169L, -2234100979542855170L, -80784550989267215L};
   private static final byte[] good_parity = new byte[]{1, 1, 2, 2, 4, 4, 7, 7, 8, 8, 11, 11, 13, 13, 14, 14, 16, 16, 19, 19, 21, 21, 22, 22, 25, 25, 26, 26, 28, 28, 31, 31, 32, 32, 35, 35, 37, 37, 38, 38, 41, 41, 42, 42, 44, 44, 47, 47, 49, 49, 50, 50, 52, 52, 55, 55, 56, 56, 59, 59, 61, 61, 62, 62, 64, 64, 67, 67, 69, 69, 70, 70, 73, 73, 74, 74, 76, 76, 79, 79, 81, 81, 82, 82, 84, 84, 87, 87, 88, 88, 91, 91, 93, 93, 94, 94, 97, 97, 98, 98, 100, 100, 103, 103, 104, 104, 107, 107, 109, 109, 110, 110, 112, 112, 115, 115, 117, 117, 118, 118, 121, 121, 122, 122, 124, 124, 127, 127, -128, -128, -125, -125, -123, -123, -122, -122, -119, -119, -118, -118, -116, -116, -113, -113, -111, -111, -110, -110, -108, -108, -105, -105, -104, -104, -101, -101, -99, -99, -98, -98, -95, -95, -94, -94, -92, -92, -89, -89, -88, -88, -85, -85, -83, -83, -82, -82, -80, -80, -77, -77, -75, -75, -74, -74, -71, -71, -70, -70, -68, -68, -65, -65, -63, -63, -62, -62, -60, -60, -57, -57, -56, -56, -53, -53, -51, -51, -50, -50, -48, -48, -45, -45, -43, -43, -42, -42, -39, -39, -38, -38, -36, -36, -33, -33, -32, -32, -29, -29, -27, -27, -26, -26, -23, -23, -22, -22, -20, -20, -17, -17, -15, -15, -14, -14, -12, -12, -9, -9, -8, -8, -5, -5, -3, -3, -2, -2};

   public static final byte[] set_parity(byte[] var0) {
      for(int var1 = 0; var1 < 8; ++var1) {
         var0[var1] = good_parity[var0[var1] & 255];
      }

      return var0;
   }

   public static final long set_parity(long var0) {
      return octet2long(set_parity(long2octet(var0)));
   }

   public static final boolean bad_key(long var0) {
      for(int var2 = 0; var2 < bad_keys.length; ++var2) {
         if (bad_keys[var2] == var0) {
            return true;
         }
      }

      return false;
   }

   public static final boolean bad_key(byte[] var0) {
      return bad_key(octet2long(var0));
   }

   public static long octet2long(byte[] var0) {
      return octet2long(var0, 0);
   }

   public static long octet2long(byte[] var0, int var1) {
      long var2 = 0L;

      for(int var4 = 0; var4 < 8; ++var4) {
         if (var4 + var1 < var0.length) {
            var2 |= ((long)var0[var4 + var1] & 255L) << (7 - var4) * 8;
         }
      }

      return var2;
   }

   public static byte[] long2octet(long var0) {
      byte[] var2 = new byte[8];

      for(int var3 = 0; var3 < 8; ++var3) {
         var2[var3] = (byte)((int)(var0 >>> (7 - var3) * 8 & 255L));
      }

      return var2;
   }

   public static void long2octet(long var0, byte[] var2) {
      long2octet(var0, var2, 0);
   }

   public static void long2octet(long var0, byte[] var2, int var3) {
      for(int var4 = 0; var4 < 8; ++var4) {
         if (var4 + var3 < var2.length) {
            var2[var4 + var3] = (byte)((int)(var0 >>> (7 - var4) * 8 & 255L));
         }
      }

   }

   public static void cbc_encrypt(byte[] var0, byte[] var1, byte[] var2, byte[] var3, boolean var4) throws KrbCryptoException {
      Cipher var5 = null;

      try {
         var5 = Cipher.getInstance("DES/CBC/NoPadding");
      } catch (GeneralSecurityException var12) {
         KrbCryptoException var7 = new KrbCryptoException("JCE provider may not be installed. " + var12.getMessage());
         var7.initCause(var12);
         throw var7;
      }

      IvParameterSpec var6 = new IvParameterSpec(var3);
      SecretKeySpec var13 = new SecretKeySpec(var2, "DES");

      try {
         SecretKeyFactory var8 = SecretKeyFactory.getInstance("DES");
         if (var4) {
            var5.init(1, var13, var6);
         } else {
            var5.init(2, var13, var6);
         }

         byte[] var10 = var5.doFinal(var0);
         System.arraycopy(var10, 0, var1, 0, var10.length);
      } catch (GeneralSecurityException var11) {
         KrbCryptoException var9 = new KrbCryptoException(var11.getMessage());
         var9.initCause(var11);
         throw var9;
      }
   }

   public static long char_to_key(char[] var0) throws KrbCryptoException {
      long var1 = 0L;
      long var7 = 0L;
      Object var9 = null;

      byte[] var16;
      try {
         if (CHARSET == null) {
            var16 = (new String(var0)).getBytes();
         } else {
            var16 = (new String(var0)).getBytes(CHARSET);
         }
      } catch (Exception var15) {
         if (var9 != null) {
            Arrays.fill((byte[])var9, 0, ((Object[])var9).length, (byte)0);
         }

         KrbCryptoException var11 = new KrbCryptoException("Unable to convert passwd, " + var15);
         var11.initCause(var15);
         throw var11;
      }

      byte[] var10 = pad(var16);
      byte[] var17 = new byte[8];
      int var12 = var10.length / 8 + (var10.length % 8 == 0 ? 0 : 1);

      for(int var13 = 0; var13 < var12; ++var13) {
         long var3 = octet2long(var10, var13 * 8) & 9187201950435737471L;
         if (var13 % 2 == 1) {
            long var5 = 0L;

            for(int var14 = 0; var14 < 64; ++var14) {
               var5 |= (var3 & 1L << var14) >>> var14 << 63 - var14;
            }

            var3 = var5 >>> 1;
         }

         var1 ^= var3 << 1;
      }

      var1 = set_parity(var1);
      byte[] var18;
      if (bad_key(var1)) {
         var18 = long2octet(var1);
         var18[7] = (byte)(var18[7] ^ 240);
         var1 = octet2long(var18);
      }

      var17 = des_cksum(long2octet(var1), var10, long2octet(var1));
      var1 = octet2long(set_parity(var17));
      if (bad_key(var1)) {
         var18 = long2octet(var1);
         var18[7] = (byte)(var18[7] ^ 240);
         var1 = octet2long(var18);
      }

      if (var16 != null) {
         Arrays.fill((byte[])var16, 0, var16.length, (byte)0);
      }

      if (var10 != null) {
         Arrays.fill((byte[])var10, 0, var10.length, (byte)0);
      }

      return var1;
   }

   public static byte[] des_cksum(byte[] var0, byte[] var1, byte[] var2) throws KrbCryptoException {
      Cipher var3 = null;
      byte[] var4 = new byte[8];

      try {
         var3 = Cipher.getInstance("DES/CBC/NoPadding");
      } catch (Exception var10) {
         KrbCryptoException var6 = new KrbCryptoException("JCE provider may not be installed. " + var10.getMessage());
         var6.initCause(var10);
         throw var6;
      }

      IvParameterSpec var5 = new IvParameterSpec(var0);
      SecretKeySpec var12 = new SecretKeySpec(var2, "DES");

      try {
         SecretKeyFactory var7 = SecretKeyFactory.getInstance("DES");
         SecretKeySpec var13 = var12;
         var3.init(1, var12, var5);

         for(int var9 = 0; var9 < var1.length / 8; ++var9) {
            var4 = var3.doFinal(var1, var9 * 8, 8);
            var3.init(1, var13, new IvParameterSpec(var4));
         }

         return var4;
      } catch (GeneralSecurityException var11) {
         KrbCryptoException var8 = new KrbCryptoException(var11.getMessage());
         var8.initCause(var11);
         throw var8;
      }
   }

   static byte[] pad(byte[] var0) {
      int var1;
      if (var0.length < 8) {
         var1 = var0.length;
      } else {
         var1 = var0.length % 8;
      }

      if (var1 == 0) {
         return var0;
      } else {
         byte[] var2 = new byte[8 - var1 + var0.length];

         for(int var3 = var2.length - 1; var3 > var0.length - 1; --var3) {
            var2[var3] = 0;
         }

         System.arraycopy(var0, 0, var2, 0, var0.length);
         return var2;
      }
   }

   public static byte[] string_to_key_bytes(char[] var0) throws KrbCryptoException {
      return long2octet(char_to_key(var0));
   }
}
