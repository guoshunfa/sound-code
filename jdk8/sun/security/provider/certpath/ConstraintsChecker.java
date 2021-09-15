package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X509CertImpl;

class ConstraintsChecker extends PKIXCertPathChecker {
   private static final Debug debug = Debug.getInstance("certpath");
   private final int certPathLength;
   private int maxPathLength;
   private int i;
   private NameConstraintsExtension prevNC;
   private Set<String> supportedExts;

   ConstraintsChecker(int var1) {
      this.certPathLength = var1;
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (!var1) {
         this.i = 0;
         this.maxPathLength = this.certPathLength;
         this.prevNC = null;
      } else {
         throw new CertPathValidatorException("forward checking not supported");
      }
   }

   public boolean isForwardCheckingSupported() {
      return false;
   }

   public Set<String> getSupportedExtensions() {
      if (this.supportedExts == null) {
         this.supportedExts = new HashSet(2);
         this.supportedExts.add(PKIXExtensions.BasicConstraints_Id.toString());
         this.supportedExts.add(PKIXExtensions.NameConstraints_Id.toString());
         this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
      }

      return this.supportedExts;
   }

   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      X509Certificate var3 = (X509Certificate)var1;
      ++this.i;
      this.checkBasicConstraints(var3);
      this.verifyNameConstraints(var3);
      if (var2 != null && !var2.isEmpty()) {
         var2.remove(PKIXExtensions.BasicConstraints_Id.toString());
         var2.remove(PKIXExtensions.NameConstraints_Id.toString());
      }

   }

   private void verifyNameConstraints(X509Certificate var1) throws CertPathValidatorException {
      String var2 = "name constraints";
      if (debug != null) {
         debug.println("---checking " + var2 + "...");
      }

      if (this.prevNC != null && (this.i == this.certPathLength || !X509CertImpl.isSelfIssued(var1))) {
         if (debug != null) {
            debug.println("prevNC = " + this.prevNC + ", currDN = " + var1.getSubjectX500Principal());
         }

         try {
            if (!this.prevNC.verify(var1)) {
               throw new CertPathValidatorException(var2 + " check failed", (Throwable)null, (CertPath)null, -1, PKIXReason.INVALID_NAME);
            }
         } catch (IOException var4) {
            throw new CertPathValidatorException(var4);
         }
      }

      this.prevNC = mergeNameConstraints(var1, this.prevNC);
      if (debug != null) {
         debug.println(var2 + " verified.");
      }

   }

   static NameConstraintsExtension mergeNameConstraints(X509Certificate var0, NameConstraintsExtension var1) throws CertPathValidatorException {
      X509CertImpl var2;
      try {
         var2 = X509CertImpl.toImpl(var0);
      } catch (CertificateException var6) {
         throw new CertPathValidatorException(var6);
      }

      NameConstraintsExtension var3 = var2.getNameConstraintsExtension();
      if (debug != null) {
         debug.println("prevNC = " + var1 + ", newNC = " + var3);
      }

      if (var1 == null) {
         if (debug != null) {
            debug.println("mergedNC = " + String.valueOf((Object)var3));
         }

         return var3 == null ? var3 : (NameConstraintsExtension)var3.clone();
      } else {
         try {
            var1.merge(var3);
         } catch (IOException var5) {
            throw new CertPathValidatorException(var5);
         }

         if (debug != null) {
            debug.println("mergedNC = " + var1);
         }

         return var1;
      }
   }

   private void checkBasicConstraints(X509Certificate var1) throws CertPathValidatorException {
      String var2 = "basic constraints";
      if (debug != null) {
         debug.println("---checking " + var2 + "...");
         debug.println("i = " + this.i + ", maxPathLength = " + this.maxPathLength);
      }

      if (this.i < this.certPathLength) {
         int var3 = -1;
         if (var1.getVersion() < 3) {
            if (this.i == 1 && X509CertImpl.isSelfIssued(var1)) {
               var3 = Integer.MAX_VALUE;
            }
         } else {
            var3 = var1.getBasicConstraints();
         }

         if (var3 == -1) {
            throw new CertPathValidatorException(var2 + " check failed: this is not a CA certificate", (Throwable)null, (CertPath)null, -1, PKIXReason.NOT_CA_CERT);
         }

         if (!X509CertImpl.isSelfIssued(var1)) {
            if (this.maxPathLength <= 0) {
               throw new CertPathValidatorException(var2 + " check failed: pathLenConstraint violated - this cert must be the last cert in the certification path", (Throwable)null, (CertPath)null, -1, PKIXReason.PATH_TOO_LONG);
            }

            --this.maxPathLength;
         }

         if (var3 < this.maxPathLength) {
            this.maxPathLength = var3;
         }
      }

      if (debug != null) {
         debug.println("after processing, maxPathLength = " + this.maxPathLength);
         debug.println(var2 + " verified.");
      }

   }

   static int mergeBasicConstraints(X509Certificate var0, int var1) {
      int var2 = var0.getBasicConstraints();
      if (!X509CertImpl.isSelfIssued(var0)) {
         --var1;
      }

      if (var2 < var1) {
         var1 = var2;
      }

      return var1;
   }
}
