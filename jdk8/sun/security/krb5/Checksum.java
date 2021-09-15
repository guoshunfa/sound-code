package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.crypto.CksumType;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class Checksum {
   private int cksumType;
   private byte[] checksum;
   public static final int CKSUMTYPE_NULL = 0;
   public static final int CKSUMTYPE_CRC32 = 1;
   public static final int CKSUMTYPE_RSA_MD4 = 2;
   public static final int CKSUMTYPE_RSA_MD4_DES = 3;
   public static final int CKSUMTYPE_DES_MAC = 4;
   public static final int CKSUMTYPE_DES_MAC_K = 5;
   public static final int CKSUMTYPE_RSA_MD4_DES_K = 6;
   public static final int CKSUMTYPE_RSA_MD5 = 7;
   public static final int CKSUMTYPE_RSA_MD5_DES = 8;
   public static final int CKSUMTYPE_HMAC_SHA1_DES3_KD = 12;
   public static final int CKSUMTYPE_HMAC_SHA1_96_AES128 = 15;
   public static final int CKSUMTYPE_HMAC_SHA1_96_AES256 = 16;
   public static final int CKSUMTYPE_HMAC_MD5_ARCFOUR = -138;
   static int CKSUMTYPE_DEFAULT;
   static int SAFECKSUMTYPE_DEFAULT;
   private static boolean DEBUG;

   public static void initStatic() {
      String var0 = null;
      Config var1 = null;

      try {
         var1 = Config.getInstance();
         var0 = var1.get("libdefaults", "default_checksum");
         if (var0 != null) {
            CKSUMTYPE_DEFAULT = Config.getType(var0);
         } else {
            CKSUMTYPE_DEFAULT = 7;
         }
      } catch (Exception var4) {
         if (DEBUG) {
            System.out.println("Exception in getting default checksum value from the configuration Setting default checksum to be RSA-MD5");
            var4.printStackTrace();
         }

         CKSUMTYPE_DEFAULT = 7;
      }

      try {
         var0 = var1.get("libdefaults", "safe_checksum_type");
         if (var0 != null) {
            SAFECKSUMTYPE_DEFAULT = Config.getType(var0);
         } else {
            SAFECKSUMTYPE_DEFAULT = 8;
         }
      } catch (Exception var3) {
         if (DEBUG) {
            System.out.println("Exception in getting safe default checksum value from the configuration Setting  safe default checksum to be RSA-MD5");
            var3.printStackTrace();
         }

         SAFECKSUMTYPE_DEFAULT = 8;
      }

   }

   public Checksum(byte[] var1, int var2) {
      this.cksumType = var2;
      this.checksum = var1;
   }

   public Checksum(int var1, byte[] var2) throws KdcErrException, KrbCryptoException {
      this.cksumType = var1;
      CksumType var3 = CksumType.getInstance(this.cksumType);
      if (!var3.isSafe()) {
         this.checksum = var3.calculateChecksum(var2, var2.length);
      } else {
         throw new KdcErrException(50);
      }
   }

   public Checksum(int var1, byte[] var2, EncryptionKey var3, int var4) throws KdcErrException, KrbApErrException, KrbCryptoException {
      this.cksumType = var1;
      CksumType var5 = CksumType.getInstance(this.cksumType);
      if (!var5.isSafe()) {
         throw new KrbApErrException(50);
      } else {
         this.checksum = var5.calculateKeyedChecksum(var2, var2.length, var3.getBytes(), var4);
      }
   }

   public boolean verifyKeyedChecksum(byte[] var1, EncryptionKey var2, int var3) throws KdcErrException, KrbApErrException, KrbCryptoException {
      CksumType var4 = CksumType.getInstance(this.cksumType);
      if (!var4.isSafe()) {
         throw new KrbApErrException(50);
      } else {
         return var4.verifyKeyedChecksum(var1, var1.length, var2.getBytes(), this.checksum, var3);
      }
   }

   boolean isEqual(Checksum var1) throws KdcErrException {
      if (this.cksumType != var1.cksumType) {
         return false;
      } else {
         CksumType var2 = CksumType.getInstance(this.cksumType);
         return CksumType.isChecksumEqual(this.checksum, var1.checksum);
      }
   }

   private Checksum(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         DerValue var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.cksumType = var2.getData().getBigInteger().intValue();
            var2 = var1.getData().getDerValue();
            if ((var2.getTag() & 31) == 1) {
               this.checksum = var2.getData().getOctetString();
               if (var1.getData().available() > 0) {
                  throw new Asn1Exception(906);
               }
            } else {
               throw new Asn1Exception(906);
            }
         } else {
            throw new Asn1Exception(906);
         }
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var2.putInteger(BigInteger.valueOf((long)this.cksumType));
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      var2.putOctetString(this.checksum);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public static Checksum parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new Checksum(var4);
         }
      }
   }

   public final byte[] getBytes() {
      return this.checksum;
   }

   public final int getType() {
      return this.cksumType;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Checksum)) {
         return false;
      } else {
         try {
            return this.isEqual((Checksum)var1);
         } catch (KdcErrException var3) {
            return false;
         }
      }
   }

   public int hashCode() {
      byte var1 = 17;
      int var2 = 37 * var1 + this.cksumType;
      if (this.checksum != null) {
         var2 = 37 * var2 + Arrays.hashCode(this.checksum);
      }

      return var2;
   }

   static {
      DEBUG = Krb5.DEBUG;
      initStatic();
   }
}
