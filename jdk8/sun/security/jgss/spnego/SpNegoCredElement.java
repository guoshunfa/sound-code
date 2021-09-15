package sun.security.jgss.spnego;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;

public class SpNegoCredElement implements GSSCredentialSpi {
   private GSSCredentialSpi cred = null;

   public SpNegoCredElement(GSSCredentialSpi var1) throws GSSException {
      this.cred = var1;
   }

   Oid getInternalMech() {
      return this.cred.getMechanism();
   }

   public GSSCredentialSpi getInternalCred() {
      return this.cred;
   }

   public Provider getProvider() {
      return SpNegoMechFactory.PROVIDER;
   }

   public void dispose() throws GSSException {
      this.cred.dispose();
   }

   public GSSNameSpi getName() throws GSSException {
      return this.cred.getName();
   }

   public int getInitLifetime() throws GSSException {
      return this.cred.getInitLifetime();
   }

   public int getAcceptLifetime() throws GSSException {
      return this.cred.getAcceptLifetime();
   }

   public boolean isInitiatorCredential() throws GSSException {
      return this.cred.isInitiatorCredential();
   }

   public boolean isAcceptorCredential() throws GSSException {
      return this.cred.isAcceptorCredential();
   }

   public Oid getMechanism() {
      return GSSUtil.GSS_SPNEGO_MECH_OID;
   }

   public GSSCredentialSpi impersonate(GSSNameSpi var1) throws GSSException {
      return this.cred.impersonate(var1);
   }
}
