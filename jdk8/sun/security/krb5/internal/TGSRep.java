package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptedData;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;
import sun.security.util.DerValue;

public class TGSRep extends KDCRep {
   public TGSRep(PAData[] var1, PrincipalName var2, Ticket var3, EncryptedData var4) throws IOException {
      super(var1, var2, var3, var4, 13);
   }

   public TGSRep(byte[] var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(new DerValue(var1));
   }

   public TGSRep(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, RealmException, KrbApErrException, IOException {
      this.init(var1, 13);
   }
}
