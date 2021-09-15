package sun.security.provider.certpath.ldap;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import javax.naming.CommunicationException;
import javax.naming.ServiceUnavailableException;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.certpath.CertStoreHelper;

public final class LDAPCertStoreHelper extends CertStoreHelper {
   public CertStore getCertStore(URI var1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      return LDAPCertStore.getInstance(LDAPCertStore.getParameters(var1));
   }

   public X509CertSelector wrap(X509CertSelector var1, X500Principal var2, String var3) throws IOException {
      return new LDAPCertStore.LDAPCertSelector(var1, var2, var3);
   }

   public X509CRLSelector wrap(X509CRLSelector var1, Collection<X500Principal> var2, String var3) throws IOException {
      return new LDAPCertStore.LDAPCRLSelector(var1, var2, var3);
   }

   public boolean isCausedByNetworkIssue(CertStoreException var1) {
      Throwable var2 = var1.getCause();
      return var2 != null && (var2 instanceof ServiceUnavailableException || var2 instanceof CommunicationException);
   }
}
