package sun.security.krb5;

import java.io.IOException;
import sun.security.krb5.internal.EncTGSRepPart;
import sun.security.krb5.internal.KRBError;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.TGSRep;
import sun.security.krb5.internal.TGSReq;
import sun.security.krb5.internal.Ticket;
import sun.security.util.DerValue;

public class KrbTgsRep extends KrbKdcRep {
   private TGSRep rep;
   private Credentials creds;
   private Ticket secondTicket;
   private static final boolean DEBUG;

   KrbTgsRep(byte[] var1, KrbTgsReq var2) throws KrbException, IOException {
      DerValue var3 = new DerValue(var1);
      TGSReq var4 = var2.getMessage();
      TGSRep var5 = null;

      try {
         var5 = new TGSRep(var3);
      } catch (Asn1Exception var11) {
         var5 = null;
         KRBError var7 = new KRBError(var3);
         String var8 = var7.getErrorString();
         String var9 = null;
         if (var8 != null && var8.length() > 0) {
            if (var8.charAt(var8.length() - 1) == 0) {
               var9 = var8.substring(0, var8.length() - 1);
            } else {
               var9 = var8;
            }
         }

         KrbException var10;
         if (var9 == null) {
            var10 = new KrbException(var7.getErrorCode());
         } else {
            var10 = new KrbException(var7.getErrorCode(), var9);
         }

         var10.initCause(var11);
         throw var10;
      }

      byte[] var6 = var5.encPart.decrypt(var2.tgsReqKey, var2.usedSubkey() ? 9 : 8);
      byte[] var12 = var5.encPart.reset(var6);
      var3 = new DerValue(var12);
      EncTGSRepPart var13 = new EncTGSRepPart(var3);
      var5.encKDCRepPart = var13;
      check(false, var4, var5);
      this.creds = new Credentials(var5.ticket, var5.cname, var13.sname, var13.key, var13.flags, var13.authtime, var13.starttime, var13.endtime, var13.renewTill, var13.caddr);
      this.rep = var5;
      this.secondTicket = var2.getSecondTicket();
   }

   public Credentials getCreds() {
      return this.creds;
   }

   sun.security.krb5.internal.ccache.Credentials setCredentials() {
      return new sun.security.krb5.internal.ccache.Credentials(this.rep, this.secondTicket);
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
