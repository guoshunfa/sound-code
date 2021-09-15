package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class MethodData {
   private int methodType;
   private byte[] methodData = null;

   public MethodData(int var1, byte[] var2) {
      this.methodType = var1;
      if (var2 != null) {
         this.methodData = (byte[])var2.clone();
      }

   }

   public MethodData(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         DerValue var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            BigInteger var3 = var2.getData().getBigInteger();
            this.methodType = var3.intValue();
            if (var1.getData().available() > 0) {
               var2 = var1.getData().getDerValue();
               if ((var2.getTag() & 31) != 1) {
                  throw new Asn1Exception(906);
               }

               this.methodData = var2.getData().getOctetString();
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
      var2.putInteger(BigInteger.valueOf((long)this.methodType));
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      if (this.methodData != null) {
         var2 = new DerOutputStream();
         var2.putOctetString(this.methodData);
         var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      }

      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }
}
