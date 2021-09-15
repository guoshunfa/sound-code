package sun.security.krb5;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.PAData;
import sun.security.krb5.internal.ccache.CCacheOutputStream;
import sun.security.krb5.internal.crypto.Aes128;
import sun.security.krb5.internal.crypto.Aes256;
import sun.security.krb5.internal.crypto.ArcFourHmac;
import sun.security.krb5.internal.crypto.Des;
import sun.security.krb5.internal.crypto.Des3;
import sun.security.krb5.internal.crypto.EType;
import sun.security.krb5.internal.ktab.KeyTab;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class EncryptionKey implements Cloneable {
   public static final EncryptionKey NULL_KEY = new EncryptionKey(new byte[0], 0, (Integer)null);
   private int keyType;
   private byte[] keyValue;
   private Integer kvno;
   private static final boolean DEBUG;

   public synchronized int getEType() {
      return this.keyType;
   }

   public final Integer getKeyVersionNumber() {
      return this.kvno;
   }

   public final byte[] getBytes() {
      return this.keyValue;
   }

   public synchronized Object clone() {
      return new EncryptionKey(this.keyValue, this.keyType, this.kvno);
   }

   public static EncryptionKey[] acquireSecretKeys(PrincipalName var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException("Cannot have null pricipal name to look in keytab.");
      } else {
         KeyTab var2 = KeyTab.getInstance(var1);
         return var2.readServiceKeys(var0);
      }
   }

   public static EncryptionKey acquireSecretKey(PrincipalName var0, char[] var1, int var2, PAData.SaltAndParams var3) throws KrbException {
      String var4;
      byte[] var5;
      if (var3 != null) {
         var4 = var3.salt != null ? var3.salt : var0.getSalt();
         var5 = var3.params;
      } else {
         var4 = var0.getSalt();
         var5 = null;
      }

      return acquireSecretKey(var1, var4, var2, var5);
   }

   public static EncryptionKey acquireSecretKey(char[] var0, String var1, int var2, byte[] var3) throws KrbException {
      return new EncryptionKey(stringToKey(var0, var1, var3, var2), var2, (Integer)null);
   }

   public static EncryptionKey[] acquireSecretKeys(char[] var0, String var1) throws KrbException {
      int[] var2 = EType.getDefaults("default_tkt_enctypes");
      EncryptionKey[] var3 = new EncryptionKey[var2.length];

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if (EType.isSupported(var2[var4])) {
            var3[var4] = new EncryptionKey(stringToKey(var0, var1, (byte[])null, var2[var4]), var2[var4], (Integer)null);
         } else if (DEBUG) {
            System.out.println("Encryption Type " + EType.toString(var2[var4]) + " is not supported/enabled");
         }
      }

      return var3;
   }

   public EncryptionKey(byte[] var1, int var2, Integer var3) {
      if (var1 != null) {
         this.keyValue = new byte[var1.length];
         System.arraycopy(var1, 0, this.keyValue, 0, var1.length);
         this.keyType = var2;
         this.kvno = var3;
      } else {
         throw new IllegalArgumentException("EncryptionKey: Key bytes cannot be null!");
      }
   }

   public EncryptionKey(int var1, byte[] var2) {
      this(var2, var1, (Integer)null);
   }

   private static byte[] stringToKey(char[] var0, String var1, byte[] var2, int var3) throws KrbCryptoException {
      char[] var4 = var1.toCharArray();
      char[] var5 = new char[var0.length + var4.length];
      System.arraycopy(var0, 0, var5, 0, var0.length);
      System.arraycopy(var4, 0, var5, var0.length, var4.length);
      Arrays.fill(var4, '0');

      try {
         byte[] var6;
         switch(var3) {
         case 1:
         case 3:
            var6 = Des.string_to_key_bytes(var5);
            return var6;
         case 16:
            var6 = Des3.stringToKey(var5);
            return var6;
         case 17:
            var6 = Aes128.stringToKey(var0, var1, var2);
            return var6;
         case 18:
            var6 = Aes256.stringToKey(var0, var1, var2);
            return var6;
         case 23:
            var6 = ArcFourHmac.stringToKey(var0);
            return var6;
         default:
            throw new IllegalArgumentException("encryption type " + EType.toString(var3) + " not supported");
         }
      } catch (GeneralSecurityException var11) {
         KrbCryptoException var7 = new KrbCryptoException(var11.getMessage());
         var7.initCause(var11);
         throw var7;
      } finally {
         Arrays.fill(var5, '0');
      }
   }

   public EncryptionKey(char[] var1, String var2, String var3) throws KrbCryptoException {
      if (var3 != null && !var3.equalsIgnoreCase("DES")) {
         if (var3.equalsIgnoreCase("DESede")) {
            this.keyType = 16;
         } else if (var3.equalsIgnoreCase("AES128")) {
            this.keyType = 17;
         } else if (var3.equalsIgnoreCase("ArcFourHmac")) {
            this.keyType = 23;
         } else {
            if (!var3.equalsIgnoreCase("AES256")) {
               throw new IllegalArgumentException("Algorithm " + var3 + " not supported");
            }

            this.keyType = 18;
            if (!EType.isSupported(this.keyType)) {
               throw new IllegalArgumentException("Algorithm " + var3 + " not enabled");
            }
         }
      } else {
         this.keyType = 3;
      }

      this.keyValue = stringToKey(var1, var2, (byte[])null, this.keyType);
      this.kvno = null;
   }

   public EncryptionKey(EncryptionKey var1) throws KrbCryptoException {
      this.keyValue = Confounder.bytes(var1.keyValue.length);

      for(int var2 = 0; var2 < this.keyValue.length; ++var2) {
         byte[] var10000 = this.keyValue;
         var10000[var2] ^= var1.keyValue[var2];
      }

      this.keyType = var1.keyType;

      try {
         if (this.keyType == 3 || this.keyType == 1) {
            if (!DESKeySpec.isParityAdjusted(this.keyValue, 0)) {
               this.keyValue = Des.set_parity(this.keyValue);
            }

            if (DESKeySpec.isWeak(this.keyValue, 0)) {
               this.keyValue[7] = (byte)(this.keyValue[7] ^ 240);
            }
         }

         if (this.keyType == 16) {
            if (!DESedeKeySpec.isParityAdjusted(this.keyValue, 0)) {
               this.keyValue = Des3.parityFix(this.keyValue);
            }

            byte[] var5 = new byte[8];

            for(int var6 = 0; var6 < this.keyValue.length; var6 += 8) {
               System.arraycopy(this.keyValue, var6, var5, 0, 8);
               if (DESKeySpec.isWeak(var5, 0)) {
                  this.keyValue[var6 + 7] = (byte)(this.keyValue[var6 + 7] ^ 240);
               }
            }
         }

      } catch (GeneralSecurityException var4) {
         KrbCryptoException var3 = new KrbCryptoException(var4.getMessage());
         var3.initCause(var4);
         throw var3;
      }
   }

   public EncryptionKey(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         DerValue var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.keyType = var2.getData().getBigInteger().intValue();
            var2 = var1.getData().getDerValue();
            if ((var2.getTag() & 31) == 1) {
               this.keyValue = var2.getData().getOctetString();
               if (var2.getData().available() > 0) {
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

   public synchronized byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      DerOutputStream var2 = new DerOutputStream();
      var2.putInteger(this.keyType);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var2 = new DerOutputStream();
      var2.putOctetString(this.keyValue);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public synchronized void destroy() {
      if (this.keyValue != null) {
         for(int var1 = 0; var1 < this.keyValue.length; ++var1) {
            this.keyValue[var1] = 0;
         }
      }

   }

   public static EncryptionKey parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new EncryptionKey(var4);
         }
      }
   }

   public synchronized void writeKey(CCacheOutputStream var1) throws IOException {
      var1.write16(this.keyType);
      var1.write16(this.keyType);
      var1.write32(this.keyValue.length);

      for(int var2 = 0; var2 < this.keyValue.length; ++var2) {
         var1.write8(this.keyValue[var2]);
      }

   }

   public String toString() {
      return new String("EncryptionKey: keyType=" + this.keyType + " kvno=" + this.kvno + " keyValue (hex dump)=" + (this.keyValue != null && this.keyValue.length != 0 ? '\n' + Krb5.hexDumper.encodeBuffer(this.keyValue) + '\n' : " Empty Key"));
   }

   public static EncryptionKey findKey(int var0, EncryptionKey[] var1) throws KrbException {
      return findKey(var0, (Integer)null, var1);
   }

   private static boolean versionMatches(Integer var0, Integer var1) {
      return var0 != null && var0 != 0 && var1 != null && var1 != 0 ? var0.equals(var1) : true;
   }

   public static EncryptionKey findKey(int var0, Integer var1, EncryptionKey[] var2) throws KrbException {
      if (!EType.isSupported(var0)) {
         throw new KrbException("Encryption type " + EType.toString(var0) + " is not supported/enabled");
      } else {
         boolean var4 = false;
         int var5 = 0;
         EncryptionKey var6 = null;

         int var3;
         int var7;
         Integer var8;
         for(var7 = 0; var7 < var2.length; ++var7) {
            var3 = var2[var7].getEType();
            if (EType.isSupported(var3)) {
               var8 = var2[var7].getKeyVersionNumber();
               if (var0 == var3) {
                  var4 = true;
                  if (versionMatches(var1, var8)) {
                     return var2[var7];
                  }

                  if (var8 > var5) {
                     var6 = var2[var7];
                     var5 = var8;
                  }
               }
            }
         }

         if (var0 == 1 || var0 == 3) {
            for(var7 = 0; var7 < var2.length; ++var7) {
               var3 = var2[var7].getEType();
               if (var3 == 1 || var3 == 3) {
                  var8 = var2[var7].getKeyVersionNumber();
                  var4 = true;
                  if (versionMatches(var1, var8)) {
                     return new EncryptionKey(var0, var2[var7].getBytes());
                  }

                  if (var8 > var5) {
                     var6 = new EncryptionKey(var0, var2[var7].getBytes());
                     var5 = var8;
                  }
               }
            }
         }

         return var4 ? var6 : null;
      }
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
