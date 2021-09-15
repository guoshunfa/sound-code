package sun.security.krb5.internal.crypto;

import sun.security.krb5.Config;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.KrbException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.Krb5;

public abstract class CksumType {
   private static boolean DEBUG;

   public static CksumType getInstance(int var0) throws KdcErrException {
      Object var1 = null;
      String var2 = null;
      switch(var0) {
      case -138:
         var1 = new HmacMd5ArcFourCksumType();
         var2 = "sun.security.krb5.internal.crypto.HmacMd5ArcFourCksumType";
         break;
      case 1:
         var1 = new Crc32CksumType();
         var2 = "sun.security.krb5.internal.crypto.Crc32CksumType";
         break;
      case 2:
      case 3:
      case 6:
      default:
         throw new KdcErrException(15);
      case 4:
         var1 = new DesMacCksumType();
         var2 = "sun.security.krb5.internal.crypto.DesMacCksumType";
         break;
      case 5:
         var1 = new DesMacKCksumType();
         var2 = "sun.security.krb5.internal.crypto.DesMacKCksumType";
         break;
      case 7:
         var1 = new RsaMd5CksumType();
         var2 = "sun.security.krb5.internal.crypto.RsaMd5CksumType";
         break;
      case 8:
         var1 = new RsaMd5DesCksumType();
         var2 = "sun.security.krb5.internal.crypto.RsaMd5DesCksumType";
         break;
      case 12:
         var1 = new HmacSha1Des3KdCksumType();
         var2 = "sun.security.krb5.internal.crypto.HmacSha1Des3KdCksumType";
         break;
      case 15:
         var1 = new HmacSha1Aes128CksumType();
         var2 = "sun.security.krb5.internal.crypto.HmacSha1Aes128CksumType";
         break;
      case 16:
         var1 = new HmacSha1Aes256CksumType();
         var2 = "sun.security.krb5.internal.crypto.HmacSha1Aes256CksumType";
      }

      if (DEBUG) {
         System.out.println(">>> CksumType: " + var2);
      }

      return (CksumType)var1;
   }

   public static CksumType getInstance() throws KdcErrException {
      int var0 = 7;

      try {
         Config var1 = Config.getInstance();
         if ((var0 = Config.getType(var1.get("libdefaults", "ap_req_checksum_type"))) == -1 && (var0 = Config.getType(var1.get("libdefaults", "checksum_type"))) == -1) {
            var0 = 7;
         }
      } catch (KrbException var2) {
      }

      return getInstance(var0);
   }

   public abstract int confounderSize();

   public abstract int cksumType();

   public abstract boolean isSafe();

   public abstract int cksumSize();

   public abstract int keyType();

   public abstract int keySize();

   public abstract byte[] calculateChecksum(byte[] var1, int var2) throws KrbCryptoException;

   public abstract byte[] calculateKeyedChecksum(byte[] var1, int var2, byte[] var3, int var4) throws KrbCryptoException;

   public abstract boolean verifyKeyedChecksum(byte[] var1, int var2, byte[] var3, byte[] var4, int var5) throws KrbCryptoException;

   public static boolean isChecksumEqual(byte[] var0, byte[] var1) {
      if (var0 == var1) {
         return true;
      } else if ((var0 != null || var1 == null) && (var0 == null || var1 != null)) {
         if (var0.length != var1.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < var0.length; ++var2) {
               if (var0[var2] != var1[var2]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
