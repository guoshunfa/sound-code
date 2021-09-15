package sun.security.provider.certpath.ssl;

import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.Collection;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.certpath.CertStoreHelper;

public final class SSLServerCertStoreHelper extends CertStoreHelper {
   public CertStore getCertStore(URI var1) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
      return SSLServerCertStore.getInstance(var1);
   }

   public X509CertSelector wrap(X509CertSelector var1, X500Principal var2, String var3) throws IOException {
      throw new UnsupportedOperationException();
   }

   public X509CRLSelector wrap(X509CRLSelector var1, Collection<X500Principal> var2, String var3) throws IOException {
      throw new UnsupportedOperationException();
   }

   public boolean isCausedByNetworkIssue(CertStoreException var1) {
      Throwable var2 = var1.getCause();
      return var2 != null && var2 instanceof IOException;
   }
}
