package sun.security.krb5.internal;

import java.io.IOException;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.util.DerValue;

public class EncTGSRepPart extends EncKDCRepPart {
   public EncTGSRepPart(EncryptionKey var1, LastReq var2, int var3, KerberosTime var4, TicketFlags var5, KerberosTime var6, KerberosTime var7, KerberosTime var8, KerberosTime var9, PrincipalName var10, HostAddresses var11) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, 26);
   }

   public EncTGSRepPart(byte[] var1) throws Asn1Exception, IOException, KrbException {
      this.init(new DerValue(var1));
   }

   public EncTGSRepPart(DerValue var1) throws Asn1Exception, IOException, KrbException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, KrbException {
      this.init(var1, 26);
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      return this.asn1Encode(26);
   }
}
