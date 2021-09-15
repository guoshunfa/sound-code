package sun.security.provider.certpath;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.util.UntrustedCertificates;

public final class UntrustedChecker extends PKIXCertPathChecker {
   private static final Debug debug = Debug.getInstance("certpath");

   public void init(boolean var1) throws CertPathValidatorException {
   }

   public boolean isForwardCheckingSupported() {
      return true;
   }

   public Set<String> getSupportedExtensions() {
      return null;
   }

   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      X509Certificate var3 = (X509Certificate)var1;
      if (UntrustedCertificates.isUntrusted(var3)) {
         if (debug != null) {
            debug.println("UntrustedChecker: untrusted certificate " + var3.getSubjectX500Principal());
         }

         throw new CertPathValidatorException("Untrusted certificate: " + var3.getSubjectX500Principal());
      }
   }
}
