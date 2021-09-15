package sun.security.jgss.wrapper;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;

public class GSSCredElement implements GSSCredentialSpi {
   private int usage;
   long pCred;
   private GSSNameElement name = null;
   private GSSLibStub cStub;

   void doServicePermCheck() throws GSSException {
      if (GSSUtil.isKerberosMech(this.cStub.getMech()) && System.getSecurityManager() != null) {
         String var1;
         if (this.isInitiatorCredential()) {
            var1 = Krb5Util.getTGSName(this.name);
            Krb5Util.checkServicePermission(var1, "initiate");
         }

         if (this.isAcceptorCredential() && this.name != GSSNameElement.DEF_ACCEPTOR) {
            var1 = this.name.getKrbName();
            Krb5Util.checkServicePermission(var1, "accept");
         }
      }

   }

   GSSCredElement(long var1, GSSNameElement var3, Oid var4) throws GSSException {
      this.pCred = var1;
      this.cStub = GSSLibStub.getInstance(var4);
      this.usage = 1;
      this.name = var3;
   }

   GSSCredElement(GSSNameElement var1, int var2, int var3, GSSLibStub var4) throws GSSException {
      this.cStub = var4;
      this.usage = var3;
      if (var1 != null) {
         this.name = var1;
         this.doServicePermCheck();
         this.pCred = this.cStub.acquireCred(this.name.pName, var2, var3);
      } else {
         this.pCred = this.cStub.acquireCred(0L, var2, var3);
         this.name = new GSSNameElement(this.cStub.getCredName(this.pCred), this.cStub);
         this.doServicePermCheck();
      }

   }

   public Provider getProvider() {
      return SunNativeProvider.INSTANCE;
   }

   public void dispose() throws GSSException {
      this.name = null;
      if (this.pCred != 0L) {
         this.pCred = this.cStub.releaseCred(this.pCred);
      }

   }

   public GSSNameElement getName() throws GSSException {
      return this.name == GSSNameElement.DEF_ACCEPTOR ? null : this.name;
   }

   public int getInitLifetime() throws GSSException {
      return this.isInitiatorCredential() ? this.cStub.getCredTime(this.pCred) : 0;
   }

   public int getAcceptLifetime() throws GSSException {
      return this.isAcceptorCredential() ? this.cStub.getCredTime(this.pCred) : 0;
   }

   public boolean isInitiatorCredential() {
      return this.usage != 2;
   }

   public boolean isAcceptorCredential() {
      return this.usage != 1;
   }

   public Oid getMechanism() {
      return this.cStub.getMech();
   }

   public String toString() {
      return "N/A";
   }

   protected void finalize() throws Throwable {
      this.dispose();
   }

   public GSSCredentialSpi impersonate(GSSNameSpi var1) throws GSSException {
      throw new GSSException(11, -1, "Not supported yet");
   }
}
