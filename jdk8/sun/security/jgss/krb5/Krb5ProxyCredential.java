package sun.security.jgss.krb5;

import java.security.Provider;
import javax.security.auth.DestroyFailedException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.internal.Ticket;

public class Krb5ProxyCredential implements Krb5CredElement {
   public final Krb5InitCredential self;
   private final Krb5NameElement client;
   public final Ticket tkt;

   Krb5ProxyCredential(Krb5InitCredential var1, Krb5NameElement var2, Ticket var3) {
      this.self = var1;
      this.tkt = var3;
      this.client = var2;
   }

   public final Krb5NameElement getName() throws GSSException {
      return this.client;
   }

   public int getInitLifetime() throws GSSException {
      return this.self.getInitLifetime();
   }

   public int getAcceptLifetime() throws GSSException {
      return 0;
   }

   public boolean isInitiatorCredential() throws GSSException {
      return true;
   }

   public boolean isAcceptorCredential() throws GSSException {
      return false;
   }

   public final Oid getMechanism() {
      return Krb5MechFactory.GSS_KRB5_MECH_OID;
   }

   public final Provider getProvider() {
      return Krb5MechFactory.PROVIDER;
   }

   public void dispose() throws GSSException {
      try {
         this.self.destroy();
      } catch (DestroyFailedException var3) {
         GSSException var2 = new GSSException(11, -1, "Could not destroy credentials - " + var3.getMessage());
         var2.initCause(var3);
      }

   }

   public GSSCredentialSpi impersonate(GSSNameSpi var1) throws GSSException {
      throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
   }
}
