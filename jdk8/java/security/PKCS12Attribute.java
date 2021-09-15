package java.security;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public final class PKCS12Attribute implements KeyStore.Entry.Attribute {
   private static final Pattern COLON_SEPARATED_HEX_PAIRS = Pattern.compile("^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2})+$");
   private String name;
   private String value;
   private byte[] encoded;
   private int hashValue = -1;

   public PKCS12Attribute(String var1, String var2) {
      if (var1 != null && var2 != null) {
         ObjectIdentifier var3;
         try {
            var3 = new ObjectIdentifier(var1);
         } catch (IOException var8) {
            throw new IllegalArgumentException("Incorrect format: name", var8);
         }

         this.name = var1;
         int var4 = var2.length();
         String[] var5;
         if (var2.charAt(0) == '[' && var2.charAt(var4 - 1) == ']') {
            var5 = var2.substring(1, var4 - 1).split(", ");
         } else {
            var5 = new String[]{var2};
         }

         this.value = var2;

         try {
            this.encoded = this.encode(var3, var5);
         } catch (IOException var7) {
            throw new IllegalArgumentException("Incorrect format: value", var7);
         }
      } else {
         throw new NullPointerException();
      }
   }

   public PKCS12Attribute(byte[] var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.encoded = (byte[])var1.clone();

         try {
            this.parse(var1);
         } catch (IOException var3) {
            throw new IllegalArgumentException("Incorrect format: encoded", var3);
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public byte[] getEncoded() {
      return (byte[])this.encoded.clone();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof PKCS12Attribute) ? false : Arrays.equals(this.encoded, ((PKCS12Attribute)var1).getEncoded());
      }
   }

   public int hashCode() {
      if (this.hashValue == -1) {
         Arrays.hashCode(this.encoded);
      }

      return this.hashValue;
   }

   public String toString() {
      return this.name + "=" + this.value;
   }

   private byte[] encode(ObjectIdentifier var1, String[] var2) throws IOException {
      DerOutputStream var3 = new DerOutputStream();
      var3.putOID(var1);
      DerOutputStream var4 = new DerOutputStream();
      String[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         if (COLON_SEPARATED_HEX_PAIRS.matcher(var8).matches()) {
            byte[] var9 = (new BigInteger(var8.replace(":", ""), 16)).toByteArray();
            if (var9[0] == 0) {
               var9 = Arrays.copyOfRange((byte[])var9, 1, var9.length);
            }

            var4.putOctetString(var9);
         } else {
            var4.putUTF8String(var8);
         }
      }

      var3.write((byte)49, (DerOutputStream)var4);
      DerOutputStream var10 = new DerOutputStream();
      var10.write((byte)48, (DerOutputStream)var3);
      return var10.toByteArray();
   }

   private void parse(byte[] var1) throws IOException {
      DerInputStream var2 = new DerInputStream(var1);
      DerValue[] var3 = var2.getSequence(2);
      ObjectIdentifier var4 = var3[0].getOID();
      DerInputStream var5 = new DerInputStream(var3[1].toByteArray());
      DerValue[] var6 = var5.getSet(1);
      String[] var7 = new String[var6.length];

      for(int var9 = 0; var9 < var6.length; ++var9) {
         if (var6[var9].tag == 4) {
            var7[var9] = Debug.toString(var6[var9].getOctetString());
         } else {
            String var8;
            if ((var8 = var6[var9].getAsString()) != null) {
               var7[var9] = var8;
            } else if (var6[var9].tag == 6) {
               var7[var9] = var6[var9].getOID().toString();
            } else if (var6[var9].tag == 24) {
               var7[var9] = var6[var9].getGeneralizedTime().toString();
            } else if (var6[var9].tag == 23) {
               var7[var9] = var6[var9].getUTCTime().toString();
            } else if (var6[var9].tag == 2) {
               var7[var9] = var6[var9].getBigInteger().toString();
            } else if (var6[var9].tag == 1) {
               var7[var9] = String.valueOf(var6[var9].getBoolean());
            } else {
               var7[var9] = Debug.toString(var6[var9].getDataBytes());
            }
         }
      }

      this.name = var4.toString();
      this.value = var7.length == 1 ? var7[0] : Arrays.toString((Object[])var7);
   }
}
