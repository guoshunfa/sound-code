package sun.security.provider.certpath;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.PKIXExtensions;

class KeyChecker extends PKIXCertPathChecker {
   private static final Debug debug = Debug.getInstance("certpath");
   private final int certPathLen;
   private final CertSelector targetConstraints;
   private int remainingCerts;
   private Set<String> supportedExts;
   private static final int KEY_CERT_SIGN = 5;

   KeyChecker(int var1, CertSelector var2) {
      this.certPathLen = var1;
      this.targetConstraints = var2;
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (!var1) {
         this.remainingCerts = this.certPathLen;
      } else {
         throw new CertPathValidatorException("forward checking not supported");
      }
   }

   public boolean isForwardCheckingSupported() {
      return false;
   }

   public Set<String> getSupportedExtensions() {
      if (this.supportedExts == null) {
         this.supportedExts = new HashSet(3);
         this.supportedExts.add(PKIXExtensions.KeyUsage_Id.toString());
         this.supportedExts.add(PKIXExtensions.ExtendedKeyUsage_Id.toString());
         this.supportedExts.add(PKIXExtensions.SubjectAlternativeName_Id.toString());
         this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
      }

      return this.supportedExts;
   }

   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      X509Certificate var3 = (X509Certificate)var1;
      --this.remainingCerts;
      if (this.remainingCerts == 0) {
         if (this.targetConstraints != null && !this.targetConstraints.match(var3)) {
            throw new CertPathValidatorException("target certificate constraints check failed");
         }
      } else {
         verifyCAKeyUsage(var3);
      }

      if (var2 != null && !var2.isEmpty()) {
         var2.remove(PKIXExtensions.KeyUsage_Id.toString());
         var2.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
         var2.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
      }

   }

   static void verifyCAKeyUsage(X509Certificate var0) throws CertPathValidatorException {
      String var1 = "CA key usage";
      if (debug != null) {
         debug.println("KeyChecker.verifyCAKeyUsage() ---checking " + var1 + "...");
      }

      boolean[] var2 = var0.getKeyUsage();
      if (var2 != null) {
         if (!var2[5]) {
            throw new CertPathValidatorException(var1 + " check failed: keyCertSign bit is not set", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_KEY_USAGE);
         } else {
            if (debug != null) {
               debug.println("KeyChecker.verifyCAKeyUsage() " + var1 + " verified.");
            }

         }
      }
   }
}
