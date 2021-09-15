package sun.security.krb5;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.crypto.EType;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncryptedData implements Cloneable {
   int eType;
   Integer kvno;
   byte[] cipher;
   byte[] plain;
   public static final int ETYPE_NULL = 0;
   public static final int ETYPE_DES_CBC_CRC = 1;
   public static final int ETYPE_DES_CBC_MD4 = 2;
   public static final int ETYPE_DES_CBC_MD5 = 3;
   public static final int ETYPE_ARCFOUR_HMAC = 23;
   public static final int ETYPE_ARCFOUR_HMAC_EXP = 24;
   public static final int ETYPE_DES3_CBC_HMAC_SHA1_KD = 16;
   public static final int ETYPE_AES128_CTS_HMAC_SHA1_96 = 17;
   public static final int ETYPE_AES256_CTS_HMAC_SHA1_96 = 18;

   private EncryptedData() {
   }

   public Object clone() {
      EncryptedData var1 = new EncryptedData();
      var1.eType = this.eType;
      if (this.kvno != null) {
         var1.kvno = new Integer(this.kvno);
      }

      if (this.cipher != null) {
         var1.cipher = new byte[this.cipher.length];
         System.arraycopy(this.cipher, 0, var1.cipher, 0, this.cipher.length);
      }

      return var1;
   }

   public EncryptedData(int var1, Integer var2, byte[] var3) {
      this.eType = var1;
      this.kvno = var2;
      this.cipher = var3;
   }

   public EncryptedData(EncryptionKey var1, byte[] var2, int var3) throws KdcErrException, KrbCryptoException {
      EType var4 = EType.getInstance(var1.getEType());
      this.cipher = var4.encrypt(var2, var1.getBytes(), var3);
      this.eType = var1.getEType();
      this.kvno = var1.getKeyVersionNumber();
   }

   public byte[] decrypt(EncryptionKey var1, int var2) throws KdcErrException, KrbApErrException, KrbCryptoException {
      if (this.eType != var1.getEType()) {
         throw new KrbCryptoException("EncryptedData is encrypted using keytype " + EType.toString(this.eType) + " but decryption key is of type " + EType.toString(var1.getEType()));
      } else {
         EType var3 = EType.getInstance(this.eType);
         this.plain = var3.decrypt(this.cipher, var1.getBytes(), var2);
         return var3.decryptedData(this.plain);
      }
   }

   private byte[] decryptedData() throws KdcErrException {
      if (this.plain != null) {
         EType var1 = EType.getInstance(this.eType);
         return var1.decryptedData(this.plain);
      } else {
         return null;
      }
   }

   private EncryptedData(DerValue var1) throws Asn1Exception, IOException {
      DerValue var2 = null;
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.eType = var2.getData().getBigInteger().intValue();
            if ((var1.getData().peekByte() & 31) == 1) {
               var2 = var1.getData().getDerValue();
               int var3 = var2.getData().getBigInteger().intValue();
               this.kvno = new Integer(var3);
            } else {
               this.kvno = null;
            }

            var2 = var1.getData().getDerValue();
            if ((var2.getTag() & 31) == 2) {
               this.cipher = var2.getData().getOctetString();
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
      var2.putInteger(BigInteger.valueOf((long)this.eType));
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      if (this.kvno != null) {
         var2.putInteger(BigInteger.valueOf(this.kvno.longValue()));
         var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
         var2 = new DerOutputStream();
      }

      var2.putOctetString(this.cipher);
      var1.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public static EncryptedData parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new EncryptedData(var4);
         }
      }
   }

   public byte[] reset(byte[] var1) {
      byte[] var2 = null;
      if ((var1[1] & 255) < 128) {
         var2 = new byte[var1[1] + 2];
         System.arraycopy(var1, 0, var2, 0, var1[1] + 2);
      } else if ((var1[1] & 255) > 128) {
         int var3 = var1[1] & 127;
         int var4 = 0;

         for(int var5 = 0; var5 < var3; ++var5) {
            var4 |= (var1[var5 + 2] & 255) << 8 * (var3 - var5 - 1);
         }

         var2 = new byte[var4 + var3 + 2];
         System.arraycopy(var1, 0, var2, 0, var4 + var3 + 2);
      }

      return var2;
   }

   public int getEType() {
      return this.eType;
   }

   public Integer getKeyVersionNumber() {
      return this.kvno;
   }

   public byte[] getBytes() {
      return this.cipher;
   }
}
