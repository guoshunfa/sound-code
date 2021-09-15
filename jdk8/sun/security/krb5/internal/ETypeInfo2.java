package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.internal.util.KerberosString;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class ETypeInfo2 {
   private int etype;
   private String saltStr = null;
   private byte[] s2kparams = null;
   private static final byte TAG_TYPE = 0;
   private static final byte TAG_VALUE1 = 1;
   private static final byte TAG_VALUE2 = 2;

   private ETypeInfo2() {
   }

   public ETypeInfo2(int var1, String var2, byte[] var3) {
      this.etype = var1;
      this.saltStr = var2;
      if (var3 != null) {
         this.s2kparams = (byte[])var3.clone();
      }

   }

   public Object clone() {
      ETypeInfo2 var1 = new ETypeInfo2();
      var1.etype = this.etype;
      var1.saltStr = this.saltStr;
      if (this.s2kparams != null) {
         var1.s2kparams = new byte[this.s2kparams.length];
         System.arraycopy(this.s2kparams, 0, var1.s2kparams, 0, this.s2kparams.length);
      }

      return var1;
   }

   public ETypeInfo2(DerValue var1) throws Asn1Exception, IOException {
      DerValue var2 = null;
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.etype = var2.getData().getBigInteger().intValue();
            if (var1.getData().available() > 0 && (var1.getData().peekByte() & 31) == 1) {
               var2 = var1.getData().getDerValue();
               this.saltStr = (new KerberosString(var2.getData().getDerValue())).toString();
            }

            if (var1.getData().available() > 0 && (var1.getData().peekByte() & 31) == 2) {
               var2 = var1.getData().getDerValue();
               this.s2kparams = var2.getData().getOctetString();
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
      if (this.saltStr != null) {
         var2 = new DerOutputStream();
         var2.putDerValue((new KerberosString(this.saltStr)).toDerValue());
         var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      }

      if (this.s2kparams != null) {
         var2 = new DerOutputStream();
         var2.putOctetString(this.s2kparams);
         var1.write(DerValue.createTag((byte)-128, true, (byte)2), var2);
      }

      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public int getEType() {
      return this.etype;
   }

   public String getSalt() {
      return this.saltStr;
   }

   public byte[] getParams() {
      return this.s2kparams == null ? null : (byte[])this.s2kparams.clone();
   }
}
