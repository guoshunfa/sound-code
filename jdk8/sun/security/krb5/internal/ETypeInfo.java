package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ETypeInfo {
   private int etype;
   private String salt = null;
   private static final byte TAG_TYPE = 0;
   private static final byte TAG_VALUE = 1;

   private ETypeInfo() {
   }

   public ETypeInfo(int var1, String var2) {
      this.etype = var1;
      this.salt = var2;
   }

   public Object clone() {
      return new ETypeInfo(this.etype, this.salt);
   }

   public ETypeInfo(DerValue var1) throws Asn1Exception, IOException {
      DerValue var2 = null;
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.etype = var2.getData().getBigInteger().intValue();
            if (var1.getData().available() > 0) {
               var2 = var1.getData().getDerValue();
               if ((var2.getTag() & 31) == 1) {
                  byte[] var3 = var2.getData().getOctetString();
                  if (KerberosString.MSNAME) {
                     this.salt = new String(var3, "UTF8");
                  } else {
                     this.salt = new String(var3);
                  }
               }
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
      var2.putInteger(this.etype);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      if (this.salt != null) {
         var2 = new DerOutputStream();
         if (KerberosString.MSNAME) {
            var2.putOctetString(this.salt.getBytes("UTF8"));
         } else {
            var2.putOctetString(this.salt.getBytes());
         }

         var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      }

      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public int getEType() {
      return this.etype;
   }

   public String getSalt() {
      return this.salt;
   }
}
