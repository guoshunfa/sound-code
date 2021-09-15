package sun.security.krb5.internal;

import java.io.IOException;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAData {
   private int pADataType;
   private byte[] pADataValue = null;
   private static final byte TAG_PATYPE = 1;
   private static final byte TAG_PAVALUE = 2;

   private PAData() {
   }

   public PAData(int var1, byte[] var2) {
      this.pADataType = var1;
      if (var2 != null) {
         this.pADataValue = (byte[])var2.clone();
      }

   }

   public Object clone() {
      PAData var1 = new PAData();
      var1.pADataType = this.pADataType;
      if (this.pADataValue != null) {
         var1.pADataValue = new byte[this.pADataValue.length];
         System.arraycopy(this.pADataValue, 0, var1.pADataValue, 0, this.pADataValue.length);
      }

      return var1;
   }

   public PAData(DerValue var1) throws Asn1Exception, IOException {
      DerValue var2 = null;
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 1) {
            this.pADataType = var2.getData().getBigInteger().intValue();
            var2 = var1.getData().getDerValue();
            if ((var2.getTag() & 31) == 2) {
               this.pADataValue = var2.getData().getOctetString();
            }

            if (var1.getData().available() > 0) {
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
      var2.putInteger(this.pADataType);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      var2 = new DerOutputStream();
      var2.putOctetString(this.pADataValue);
      var1.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public int getType() {
      return this.pADataType;
   }

   public byte[] getValue() {
      return this.pADataValue == null ? null : (byte[])this.pADataValue.clone();
   }

   public static int getPreferredEType(PAData[] var0, int var1) throws IOException, Asn1Exception {
      if (var0 == null) {
         return var1;
      } else {
         DerValue var2 = null;
         DerValue var3 = null;
         PAData[] var4 = var0;
         int var5 = var0.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            PAData var7 = var4[var6];
            if (var7.getValue() != null) {
               switch(var7.getType()) {
               case 11:
                  var2 = new DerValue(var7.getValue());
                  break;
               case 19:
                  var3 = new DerValue(var7.getValue());
               }
            }
         }

         DerValue var8;
         if (var3 != null) {
            while(var3.data.available() > 0) {
               var8 = var3.data.getDerValue();
               ETypeInfo2 var9 = new ETypeInfo2(var8);
               if (var9.getParams() == null) {
                  return var9.getEType();
               }
            }
         }

         if (var2 != null && var2.data.available() > 0) {
            var8 = var2.data.getDerValue();
            ETypeInfo var10 = new ETypeInfo(var8);
            return var10.getEType();
         } else {
            return var1;
         }
      }
   }

   public static PAData.SaltAndParams getSaltAndParams(int var0, PAData[] var1) throws Asn1Exception, IOException {
      if (var1 == null) {
         return null;
      } else {
         DerValue var2 = null;
         DerValue var3 = null;
         String var4 = null;
         PAData[] var5 = var1;
         int var6 = var1.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            PAData var8 = var5[var7];
            if (var8.getValue() != null) {
               switch(var8.getType()) {
               case 3:
                  var4 = new String(var8.getValue(), KerberosString.MSNAME ? "UTF8" : "8859_1");
                  break;
               case 11:
                  var2 = new DerValue(var8.getValue());
                  break;
               case 19:
                  var3 = new DerValue(var8.getValue());
               }
            }
         }

         DerValue var9;
         if (var3 != null) {
            while(var3.data.available() > 0) {
               var9 = var3.data.getDerValue();
               ETypeInfo2 var10 = new ETypeInfo2(var9);
               if (var10.getParams() == null && var10.getEType() == var0) {
                  return new PAData.SaltAndParams(var10.getSalt(), var10.getParams());
               }
            }
         }

         if (var2 != null) {
            while(var2.data.available() > 0) {
               var9 = var2.data.getDerValue();
               ETypeInfo var11 = new ETypeInfo(var9);
               if (var11.getEType() == var0) {
                  return new PAData.SaltAndParams(var11.getSalt(), (byte[])null);
               }
            }
         }

         return var4 != null ? new PAData.SaltAndParams(var4, (byte[])null) : null;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(">>>Pre-Authentication Data:\n\t PA-DATA type = ").append(this.pADataType).append('\n');
      DerValue var2;
      DerValue var3;
      switch(this.pADataType) {
      case 2:
         var1.append("\t PA-ENC-TIMESTAMP");
         break;
      case 11:
         if (this.pADataValue != null) {
            try {
               var2 = new DerValue(this.pADataValue);

               while(var2.data.available() > 0) {
                  var3 = var2.data.getDerValue();
                  ETypeInfo var8 = new ETypeInfo(var3);
                  var1.append("\t PA-ETYPE-INFO etype = ").append(var8.getEType()).append(", salt = ").append(var8.getSalt()).append('\n');
               }
            } catch (Asn1Exception | IOException var7) {
               var1.append("\t <Unparseable PA-ETYPE-INFO>\n");
            }
         }
         break;
      case 19:
         if (this.pADataValue != null) {
            try {
               var2 = new DerValue(this.pADataValue);

               while(var2.data.available() > 0) {
                  var3 = var2.data.getDerValue();
                  ETypeInfo2 var4 = new ETypeInfo2(var3);
                  var1.append("\t PA-ETYPE-INFO2 etype = ").append(var4.getEType()).append(", salt = ").append(var4.getSalt()).append(", s2kparams = ");
                  byte[] var5 = var4.getParams();
                  if (var5 == null) {
                     var1.append("null\n");
                  } else if (var5.length == 0) {
                     var1.append("empty\n");
                  } else {
                     var1.append((new HexDumpEncoder()).encodeBuffer(var5));
                  }
               }
            } catch (Asn1Exception | IOException var6) {
               var1.append("\t <Unparseable PA-ETYPE-INFO>\n");
            }
         }
         break;
      case 129:
         var1.append("\t PA-FOR-USER\n");
      }

      return var1.toString();
   }

   public static class SaltAndParams {
      public final String salt;
      public final byte[] params;

      public SaltAndParams(String var1, byte[] var2) {
         if (var1 != null && var1.isEmpty()) {
            var1 = null;
         }

         this.salt = var1;
         this.params = var2;
      }
   }
}
