package sun.security.jgss.krb5;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import javax.security.auth.DestroyFailedException;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.PrincipalName;

public class Krb5AcceptCredential implements Krb5CredElement {
   private final Krb5NameElement name;
   private final ServiceCreds screds;

   private Krb5AcceptCredential(Krb5NameElement var1, ServiceCreds var2) {
      this.name = var1;
      this.screds = var2;
   }

   static Krb5AcceptCredential getInstance(final GSSCaller var0, Krb5NameElement var1) throws GSSException {
      final String var2 = var1 == null ? null : var1.getKrb5PrincipalName().getName();
      final AccessControlContext var3 = AccessController.getContext();
      ServiceCreds var4 = null;

      try {
         var4 = (ServiceCreds)AccessController.doPrivileged(new PrivilegedExceptionAction<ServiceCreds>() {
            public ServiceCreds run() throws Exception {
               return Krb5Util.getServiceCreds(var0 == GSSCaller.CALLER_UNKNOWN ? GSSCaller.CALLER_ACCEPT : var0, var2, var3);
            }
         });
      } catch (PrivilegedActionException var7) {
         GSSException var6 = new GSSException(13, -1, "Attempt to obtain new ACCEPT credentials failed!");
         var6.initCause(var7.getException());
         throw var6;
      }

      if (var4 == null) {
         throw new GSSException(13, -1, "Failed to find any Kerberos credentails");
      } else {
         if (var1 == null) {
            String var5 = var4.getName();
            if (var5 != null) {
               var1 = Krb5NameElement.getInstance(var5, Krb5MechFactory.NT_GSS_KRB5_PRINCIPAL);
            }
         }

         return new Krb5AcceptCredential(var1, var4);
      }
   }

   public final GSSNameSpi getName() throws GSSException {
      return this.name;
   }

   public int getInitLifetime() throws GSSException {
      return 0;
   }

   public int getAcceptLifetime() throws GSSException {
      return Integer.MAX_VALUE;
   }

   public boolean isInitiatorCredential() throws GSSException {
      return false;
   }

   public boolean isAcceptorCredential() throws GSSException {
      return true;
   }

   public final Oid getMechanism() {
      return Krb5MechFactory.GSS_KRB5_MECH_OID;
   }

   public final Provider getProvider() {
      return Krb5MechFactory.PROVIDER;
   }

   public EncryptionKey[] getKrb5EncryptionKeys(PrincipalName var1) {
      return this.screds.getEKeys(var1);
   }

   public void dispose() throws GSSException {
      try {
         this.destroy();
      } catch (DestroyFailedException var3) {
         GSSException var2 = new GSSException(11, -1, "Could not destroy credentials - " + var3.getMessage());
         var2.initCause(var3);
      }

   }

   public void destroy() throws DestroyFailedException {
      this.screds.destroy();
   }

   public GSSCredentialSpi impersonate(GSSNameSpi var1) throws GSSException {
      Credentials var2 = this.screds.getInitCred();
      if (var2 != null) {
         return Krb5InitCredential.getInstance(this.name, var2).impersonate(var1);
      } else {
         throw new GSSException(11, -1, "Only an initiate credentials can impersonate");
      }
   }
}
