package sun.security.krb5.internal;

import java.io.IOException;
import java.math.BigInteger;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PAEncTSEnc {
   public KerberosTime pATimeStamp;
   public Integer pAUSec;

   public PAEncTSEnc(KerberosTime var1, Integer var2) {
      this.pATimeStamp = var1;
      this.pAUSec = var2;
   }

   public PAEncTSEnc() {
      KerberosTime var1 = KerberosTime.now();
      this.pATimeStamp = var1;
      this.pAUSec = new Integer(var1.getMicroSeconds());
   }

   public PAEncTSEnc(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         this.pATimeStamp = KerberosTime.parse(var1.getData(), (byte)0, false);
         if (var1.getData().available() > 0) {
            DerValue var2 = var1.getData().getDerValue();
            if ((var2.getTag() & 31) != 1) {
               throw new Asn1Exception(906);
            }

            this.pAUSec = new Integer(var2.getData().getBigInteger().intValue());
         }

         if (var1.getData().available() > 0) {
            throw new Asn1Exception(906);
         }
      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      new DerOutputStream();
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), this.pATimeStamp.asn1Encode());
      DerOutputStream var2;
      if (this.pAUSec != null) {
         var2 = new DerOutputStream();
         var2.putInteger(BigInteger.valueOf((long)this.pAUSec));
         var1.write(DerValue.createTag((byte)-128, true, (byte)1), var2);
      }

      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }
}
