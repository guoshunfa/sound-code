package sun.security.krb5.internal.crypto;

import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.Cipher;
import sun.security.krb5.Config;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;

public abstract class EType {
   private static final boolean DEBUG;
   private static boolean allowWeakCrypto;
   private static final int[] BUILTIN_ETYPES;
   private static final int[] BUILTIN_ETYPES_NOAES256;

   public static void initStatic() {
      boolean var0 = false;

      try {
         Config var1 = Config.getInstance();
         String var2 = var1.get("libdefaults", "allow_weak_crypto");
         if (var2 != null && var2.equals("true")) {
            var0 = true;
         }
      } catch (Exception var3) {
         if (DEBUG) {
            System.out.println("Exception in getting allow_weak_crypto, using default value " + var3.getMessage());
         }
      }

      allowWeakCrypto = var0;
   }

   public static EType getInstance(int var0) throws KdcErrException {
      Object var1 = null;
      String var2 = null;
      switch(var0) {
      case 0:
         var1 = new NullEType();
         var2 = "sun.security.krb5.internal.crypto.NullEType";
         break;
      case 1:
         var1 = new DesCbcCrcEType();
         var2 = "sun.security.krb5.internal.crypto.DesCbcCrcEType";
         break;
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 19:
      case 20:
      case 21:
      case 22:
      default:
         String var3 = "encryption type = " + toString(var0) + " (" + var0 + ")";
         throw new KdcErrException(14, var3);
      case 3:
         var1 = new DesCbcMd5EType();
         var2 = "sun.security.krb5.internal.crypto.DesCbcMd5EType";
         break;
      case 16:
         var1 = new Des3CbcHmacSha1KdEType();
         var2 = "sun.security.krb5.internal.crypto.Des3CbcHmacSha1KdEType";
         break;
      case 17:
         var1 = new Aes128CtsHmacSha1EType();
         var2 = "sun.security.krb5.internal.crypto.Aes128CtsHmacSha1EType";
         break;
      case 18:
         var1 = new Aes256CtsHmacSha1EType();
         var2 = "sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType";
         break;
      case 23:
         var1 = new ArcFourHmacEType();
         var2 = "sun.security.krb5.internal.crypto.ArcFourHmacEType";
      }

      if (DEBUG) {
         System.out.println(">>> EType: " + var2);
      }

      return (EType)var1;
   }

   public abstract int eType();

   public abstract int minimumPadSize();

   public abstract int confounderSize();

   public abstract int checksumType();

   public abstract int checksumSize();

   public abstract int blockSize();

   public abstract int keyType();

   public abstract int keySize();

   public abstract byte[] encrypt(byte[] var1, byte[] var2, int var3) throws KrbCryptoException;

   public abstract byte[] encrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbCryptoException;

   public abstract byte[] decrypt(byte[] var1, byte[] var2, int var3) throws KrbApErrException, KrbCryptoException;

   public abstract byte[] decrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbApErrException, KrbCryptoException;

   public int dataSize(byte[] var1) {
      return var1.length - this.startOfData();
   }

   public int padSize(byte[] var1) {
      return var1.length - this.confounderSize() - this.checksumSize() - this.dataSize(var1);
   }

   public int startOfChecksum() {
      return this.confounderSize();
   }

   public int startOfData() {
      return this.confounderSize() + this.checksumSize();
   }

   public int startOfPad(byte[] var1) {
      return this.confounderSize() + this.checksumSize() + this.dataSize(var1);
   }

   public byte[] decryptedData(byte[] var1) {
      int var2 = this.dataSize(var1);
      byte[] var3 = new byte[var2];
      System.arraycopy(var1, this.startOfData(), var3, 0, var2);
      return var3;
   }

   public static int[] getBuiltInDefaults() {
      int var0 = 0;

      try {
         var0 = Cipher.getMaxAllowedKeyLength("AES");
      } catch (Exception var2) {
      }

      int[] var1;
      if (var0 < 256) {
         var1 = BUILTIN_ETYPES_NOAES256;
      } else {
         var1 = BUILTIN_ETYPES;
      }

      return !allowWeakCrypto ? Arrays.copyOfRange((int[])var1, 0, var1.length - 2) : var1;
   }

   public static int[] getDefaults(String var0) throws KrbException {
      Config var1 = null;

      try {
         var1 = Config.getInstance();
      } catch (KrbException var3) {
         if (DEBUG) {
            System.out.println("Exception while getting " + var0 + var3.getMessage());
            System.out.println("Using default builtin etypes");
         }

         return getBuiltInDefaults();
      }

      return var1.defaultEtype(var0);
   }

   public static int[] getDefaults(String var0, EncryptionKey[] var1) throws KrbException {
      int[] var2 = getDefaults(var0);
      ArrayList var3 = new ArrayList(var2.length);

      int var4;
      for(var4 = 0; var4 < var2.length; ++var4) {
         if (EncryptionKey.findKey(var2[var4], var1) != null) {
            var3.add(var2[var4]);
         }
      }

      var4 = var3.size();
      if (var4 <= 0) {
         StringBuffer var7 = new StringBuffer();

         for(int var6 = 0; var6 < var1.length; ++var6) {
            var7.append(toString(var1[var6].getEType()));
            var7.append(" ");
         }

         throw new KrbException("Do not have keys of types listed in " + var0 + " available; only have keys of following type: " + var7.toString());
      } else {
         var2 = new int[var4];

         for(int var5 = 0; var5 < var4; ++var5) {
            var2[var5] = (Integer)var3.get(var5);
         }

         return var2;
      }
   }

   public static boolean isSupported(int var0, int[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var0 == var1[var2]) {
            return true;
         }
      }

      return false;
   }

   public static boolean isSupported(int var0) {
      int[] var1 = getBuiltInDefaults();
      return isSupported(var0, var1);
   }

   public static String toString(int var0) {
      switch(var0) {
      case 0:
         return "NULL";
      case 1:
         return "DES CBC mode with CRC-32";
      case 2:
         return "DES CBC mode with MD4";
      case 3:
         return "DES CBC mode with MD5";
      case 4:
         return "reserved";
      case 5:
         return "DES3 CBC mode with MD5";
      case 6:
         return "reserved";
      case 7:
         return "DES3 CBC mode with SHA1";
      case 8:
      case 19:
      case 20:
      case 21:
      case 22:
      default:
         return "Unknown (" + var0 + ")";
      case 9:
         return "DSA with SHA1- Cms0ID";
      case 10:
         return "MD5 with RSA encryption - Cms0ID";
      case 11:
         return "SHA1 with RSA encryption - Cms0ID";
      case 12:
         return "RC2 CBC mode with Env0ID";
      case 13:
         return "RSA encryption with Env0ID";
      case 14:
         return "RSAES-0AEP-ENV-0ID";
      case 15:
         return "DES-EDE3-CBC-ENV-0ID";
      case 16:
         return "DES3 CBC mode with SHA1-KD";
      case 17:
         return "AES128 CTS mode with HMAC SHA1-96";
      case 18:
         return "AES256 CTS mode with HMAC SHA1-96";
      case 23:
         return "RC4 with HMAC";
      case 24:
         return "RC4 with HMAC EXP";
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
      initStatic();
      BUILTIN_ETYPES = new int[]{18, 17, 16, 23, 1, 3};
      BUILTIN_ETYPES_NOAES256 = new int[]{17, 16, 23, 1, 3};
   }
}
