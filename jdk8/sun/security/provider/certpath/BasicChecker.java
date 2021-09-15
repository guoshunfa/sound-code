package sun.security.provider.certpath;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.X500Name;

class BasicChecker extends PKIXCertPathChecker {
   private static final Debug debug = Debug.getInstance("certpath");
   private final PublicKey trustedPubKey;
   private final X500Principal caName;
   private final Date date;
   private final String sigProvider;
   private final boolean sigOnly;
   private X500Principal prevSubject;
   private PublicKey prevPubKey;

   BasicChecker(TrustAnchor var1, Date var2, String var3, boolean var4) {
      if (var1.getTrustedCert() != null) {
         this.trustedPubKey = var1.getTrustedCert().getPublicKey();
         this.caName = var1.getTrustedCert().getSubjectX500Principal();
      } else {
         this.trustedPubKey = var1.getCAPublicKey();
         this.caName = var1.getCA();
      }

      this.date = var2;
      this.sigProvider = var3;
      this.sigOnly = var4;
      this.prevPubKey = this.trustedPubKey;
   }

   public void init(boolean var1) throws CertPathValidatorException {
      if (!var1) {
         this.prevPubKey = this.trustedPubKey;
         if (PKIX.isDSAPublicKeyWithoutParams(this.prevPubKey)) {
            throw new CertPathValidatorException("Key parameters missing");
         } else {
            this.prevSubject = this.caName;
         }
      } else {
         throw new CertPathValidatorException("forward checking not supported");
      }
   }

   public boolean isForwardCheckingSupported() {
      return false;
   }

   public Set<String> getSupportedExtensions() {
      return null;
   }

   public void check(Certificate var1, Collection<String> var2) throws CertPathValidatorException {
      X509Certificate var3 = (X509Certificate)var1;
      if (!this.sigOnly) {
         this.verifyValidity(var3);
         this.verifyNameChaining(var3);
      }

      this.verifySignature(var3);
      this.updateState(var3);
   }

   private void verifySignature(X509Certificate var1) throws CertPathValidatorException {
      String var2 = "signature";
      if (debug != null) {
         debug.println("---checking " + var2 + "...");
      }

      try {
         var1.verify(this.prevPubKey, this.sigProvider);
      } catch (SignatureException var4) {
         throw new CertPathValidatorException(var2 + " check failed", var4, (CertPath)null, -1, CertPathValidatorException.BasicReason.INVALID_SIGNATURE);
      } catch (GeneralSecurityException var5) {
         throw new CertPathValidatorException(var2 + " check failed", var5);
      }

      if (debug != null) {
         debug.println(var2 + " verified.");
      }

   }

   private void verifyValidity(X509Certificate var1) throws CertPathValidatorException {
      String var2 = "validity";
      if (debug != null) {
         debug.println("---checking " + var2 + ":" + this.date.toString() + "...");
      }

      try {
         var1.checkValidity(this.date);
      } catch (CertificateExpiredException var4) {
         throw new CertPathValidatorException(var2 + " check failed", var4, (CertPath)null, -1, CertPathValidatorException.BasicReason.EXPIRED);
      } catch (CertificateNotYetValidException var5) {
         throw new CertPathValidatorException(var2 + " check failed", var5, (CertPath)null, -1, CertPathValidatorException.BasicReason.NOT_YET_VALID);
      }

      if (debug != null) {
         debug.println(var2 + " verified.");
      }

   }

   private void verifyNameChaining(X509Certificate var1) throws CertPathValidatorException {
      if (this.prevSubject != null) {
         String var2 = "subject/issuer name chaining";
         if (debug != null) {
            debug.println("---checking " + var2 + "...");
         }

         X500Principal var3 = var1.getIssuerX500Principal();
         if (X500Name.asX500Name(var3).isEmpty()) {
            throw new CertPathValidatorException(var2 + " check failed: empty/null issuer DN in certificate is invalid", (Throwable)null, (CertPath)null, -1, PKIXReason.NAME_CHAINING);
         }

         if (!var3.equals(this.prevSubject)) {
            throw new CertPathValidatorException(var2 + " check failed", (Throwable)null, (CertPath)null, -1, PKIXReason.NAME_CHAINING);
         }

         if (debug != null) {
            debug.println(var2 + " verified.");
         }
      }

   }

   private void updateState(X509Certificate var1) throws CertPathValidatorException {
      PublicKey var2 = var1.getPublicKey();
      if (debug != null) {
         debug.println("BasicChecker.updateState issuer: " + var1.getIssuerX500Principal().toString() + "; subject: " + var1.getSubjectX500Principal() + "; serial#: " + var1.getSerialNumber().toString());
      }

      if (PKIX.isDSAPublicKeyWithoutParams(var2)) {
         var2 = makeInheritedParamsKey(var2, this.prevPubKey);
         if (debug != null) {
            debug.println("BasicChecker.updateState Made key with inherited params");
         }
      }

      this.prevPubKey = var2;
      this.prevSubject = var1.getSubjectX500Principal();
   }

   static PublicKey makeInheritedParamsKey(PublicKey var0, PublicKey var1) throws CertPathValidatorException {
      if (var0 instanceof DSAPublicKey && var1 instanceof DSAPublicKey) {
         DSAParams var2 = ((DSAPublicKey)var1).getParams();
         if (var2 == null) {
            throw new CertPathValidatorException("Key parameters missing");
         } else {
            try {
               BigInteger var3 = ((DSAPublicKey)var0).getY();
               KeyFactory var4 = KeyFactory.getInstance("DSA");
               DSAPublicKeySpec var5 = new DSAPublicKeySpec(var3, var2.getP(), var2.getQ(), var2.getG());
               return var4.generatePublic(var5);
            } catch (GeneralSecurityException var6) {
               throw new CertPathValidatorException("Unable to generate key with inherited parameters: " + var6.getMessage(), var6);
            }
         }
      } else {
         throw new CertPathValidatorException("Input key is not appropriate type for inheriting parameters");
      }
   }

   PublicKey getPublicKey() {
      return this.prevPubKey;
   }
}
