package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class LastReqEntry {
   private int lrType;
   private KerberosTime lrValue;

   private LastReqEntry() {
   }

   public LastReqEntry(int var1, KerberosTime var2) {
      this.lrType = var1;
      this.lrValue = var2;
   }

   public LastReqEntry(DerValue var1) throws Asn1Exception, IOException {
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         DerValue var2 = var1.getData().getDerValue();
         if ((var2.getTag() & 31) == 0) {
            this.lrType = var2.getData().getBigInteger().intValue();
            this.lrValue = KerberosTime.parse(var1.getData(), (byte)1, false);
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
      var2.putInteger(this.lrType);
      var1.write(DerValue.createTag((byte)-128, true, (byte)0), var2);
      var1.write(DerValue.createTag((byte)-128, true, (byte)1), this.lrValue.asn1Encode());
      var2 = new DerOutputStream();
      var2.write((byte)48, (DerOutputStream)var1);
      return var2.toByteArray();
   }

   public Object clone() {
      LastReqEntry var1 = new LastReqEntry();
      var1.lrType = this.lrType;
      var1.lrValue = this.lrValue;
      return var1;
   }
}
