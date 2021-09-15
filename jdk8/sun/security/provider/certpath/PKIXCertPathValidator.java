package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathChecker;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertPathValidatorSpi;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXReason;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.X509CertImpl;

public final class PKIXCertPathValidator extends CertPathValidatorSpi {
   private static final Debug debug = Debug.getInstance("certpath");

   public CertPathChecker engineGetRevocationChecker() {
      return new RevocationChecker();
   }

   public CertPathValidatorResult engineValidate(CertPath var1, CertPathParameters var2) throws CertPathValidatorException, InvalidAlgorithmParameterException {
      PKIX.ValidatorParams var3 = PKIX.checkParams(var1, var2);
      return validate(var3);
   }

   private static PKIXCertPathValidatorResult validate(PKIX.ValidatorParams var0) throws CertPathValidatorException {
      if (debug != null) {
         debug.println("PKIXCertPathValidator.engineValidate()...");
      }

      AdaptableX509CertSelector var1 = null;
      List var2 = var0.certificates();
      if (!var2.isEmpty()) {
         var1 = new AdaptableX509CertSelector();
         X509Certificate var3 = (X509Certificate)var2.get(0);
         var1.setSubject(var3.getIssuerX500Principal());

         try {
            X509CertImpl var4 = X509CertImpl.toImpl(var3);
            var1.setSkiAndSerialNumber(var4.getAuthorityKeyIdentifierExtension());
         } catch (IOException | CertificateException var8) {
         }
      }

      CertPathValidatorException var10 = null;
      Iterator var11 = var0.trustAnchors().iterator();

      while(true) {
         TrustAnchor var5;
         while(true) {
            if (!var11.hasNext()) {
               if (var10 != null) {
                  throw var10;
               }

               throw new CertPathValidatorException("Path does not chain with any of the trust anchors", (Throwable)null, (CertPath)null, -1, PKIXReason.NO_TRUST_ANCHOR);
            }

            var5 = (TrustAnchor)var11.next();
            X509Certificate var6 = var5.getTrustedCert();
            if (var6 != null) {
               if (var1 != null && !var1.match(var6)) {
                  if (debug != null) {
                     debug.println("NO - don't try this trustedCert");
                  }
                  continue;
               }

               if (debug != null) {
                  debug.println("YES - try this trustedCert");
                  debug.println("anchor.getTrustedCert().getSubjectX500Principal() = " + var6.getSubjectX500Principal());
               }
               break;
            }

            if (debug != null) {
               debug.println("PKIXCertPathValidator.engineValidate(): anchor.getTrustedCert() == null");
            }
            break;
         }

         try {
            return validate(var5, var0);
         } catch (CertPathValidatorException var9) {
            var10 = var9;
         }
      }
   }

   private static PKIXCertPathValidatorResult validate(TrustAnchor var0, PKIX.ValidatorParams var1) throws CertPathValidatorException {
      UntrustedChecker var2 = new UntrustedChecker();
      X509Certificate var3 = var0.getTrustedCert();
      if (var3 != null) {
         var2.check(var3);
      }

      int var4 = var1.certificates().size();
      ArrayList var5 = new ArrayList();
      var5.add(var2);
      var5.add(new AlgorithmChecker(var0, (AlgorithmConstraints)null, var1.date(), var1.timestamp(), var1.variant()));
      var5.add(new KeyChecker(var4, var1.targetCertConstraints()));
      var5.add(new ConstraintsChecker(var4));
      PolicyNodeImpl var6 = new PolicyNodeImpl((PolicyNodeImpl)null, "2.5.29.32.0", (Set)null, false, Collections.singleton("2.5.29.32.0"), false);
      PolicyChecker var7 = new PolicyChecker(var1.initialPolicies(), var4, var1.explicitPolicyRequired(), var1.policyMappingInhibited(), var1.anyPolicyInhibited(), var1.policyQualifiersRejected(), var6);
      var5.add(var7);
      Date var8 = null;
      if ((var1.variant() == "code signing" || var1.variant() == "plugin code signing") && var1.timestamp() != null) {
         var8 = var1.timestamp().getTimestamp();
      } else {
         var8 = var1.date();
      }

      BasicChecker var9 = new BasicChecker(var0, var8, var1.sigProvider(), false);
      var5.add(var9);
      boolean var10 = false;
      List var11 = var1.certPathCheckers();
      Iterator var12 = var11.iterator();

      while(var12.hasNext()) {
         PKIXCertPathChecker var13 = (PKIXCertPathChecker)var12.next();
         if (var13 instanceof PKIXRevocationChecker) {
            if (var10) {
               throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified");
            }

            var10 = true;
            if (var13 instanceof RevocationChecker) {
               ((RevocationChecker)var13).init(var0, var1);
            }
         }
      }

      if (var1.revocationEnabled() && !var10) {
         var5.add(new RevocationChecker(var0, var1));
      }

      var5.addAll(var11);
      PKIXMasterCertPathValidator.validate(var1.certPath(), var1.certificates(), var5);
      return new PKIXCertPathValidatorResult(var0, var7.getPolicyTree(), var9.getPublicKey());
   }
}
