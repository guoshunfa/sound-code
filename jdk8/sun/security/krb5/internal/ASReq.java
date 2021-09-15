package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.KrbException;
import sun.security.util.DerValue;

public class ASReq extends KDCReq {
   public ASReq(PAData[] var1, KDCReqBody var2) throws IOException {
      super(var1, var2, 10);
   }

   public ASReq(byte[] var1) throws Asn1Exception, KrbException, IOException {
      this.init(new DerValue(var1));
   }

   public ASReq(DerValue var1) throws Asn1Exception, KrbException, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, KrbException {
      super.init(var1, 10);
   }
}
