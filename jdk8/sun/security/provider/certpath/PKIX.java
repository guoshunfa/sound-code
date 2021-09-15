package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;

class PKIX {
   private static final Debug debug = Debug.getInstance("certpath");

   private PKIX() {
   }

   static boolean isDSAPublicKeyWithoutParams(PublicKey var0) {
      return var0 instanceof DSAPublicKey && ((DSAPublicKey)var0).getParams() == null;
   }

   static PKIX.ValidatorParams checkParams(CertPath var0, CertPathParameters var1) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof PKIXParameters)) {
         throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXParameters");
      } else {
         return new PKIX.ValidatorParams(var0, (PKIXParameters)var1);
      }
   }

   static PKIX.BuilderParams checkBuilderParams(CertPathParameters var0) throws InvalidAlgorithmParameterException {
      if (!(var0 instanceof PKIXBuilderParameters)) {
         throw new InvalidAlgorithmParameterException("inappropriate params, must be an instance of PKIXBuilderParameters");
      } else {
         return new PKIX.BuilderParams((PKIXBuilderParameters)var0);
      }
   }

   private static class CertStoreComparator implements Comparator<CertStore> {
      private CertStoreComparator() {
      }

      public int compare(CertStore var1, CertStore var2) {
         return !var1.getType().equals("Collection") && !(var1.getCertStoreParameters() instanceof CollectionCertStoreParameters) ? 1 : -1;
      }

      // $FF: synthetic method
      CertStoreComparator(Object var1) {
         this();
      }
   }

   static class CertStoreTypeException extends CertStoreException {
      private static final long serialVersionUID = 7463352639238322556L;
      private final String type;

      CertStoreTypeException(String var1, CertStoreException var2) {
         super(var2.getMessage(), var2.getCause());
         this.type = var1;
      }

      String getType() {
         return this.type;
      }
   }

   static class BuilderParams extends PKIX.ValidatorParams {
      private PKIXBuilderParameters params;
      private List<CertStore> stores;
      private X500Principal targetSubject;

      BuilderParams(PKIXBuilderParameters var1) throws InvalidAlgorithmParameterException {
         super(var1);
         this.checkParams(var1);
      }

      private void checkParams(PKIXBuilderParameters var1) throws InvalidAlgorithmParameterException {
         CertSelector var2 = this.targetCertConstraints();
         if (!(var2 instanceof X509CertSelector)) {
            throw new InvalidAlgorithmParameterException("the targetCertConstraints parameter must be an X509CertSelector");
         } else {
            this.params = var1;
            this.targetSubject = getTargetSubject(this.certStores(), (X509CertSelector)this.targetCertConstraints());
         }
      }

      List<CertStore> certStores() {
         if (this.stores == null) {
            this.stores = new ArrayList(this.params.getCertStores());
            Collections.sort(this.stores, new PKIX.CertStoreComparator());
         }

         return this.stores;
      }

      int maxPathLength() {
         return this.params.getMaxPathLength();
      }

      PKIXBuilderParameters params() {
         return this.params;
      }

      X500Principal targetSubject() {
         return this.targetSubject;
      }

      private static X500Principal getTargetSubject(List<CertStore> var0, X509CertSelector var1) throws InvalidAlgorithmParameterException {
         X500Principal var2 = var1.getSubject();
         if (var2 != null) {
            return var2;
         } else {
            X509Certificate var3 = var1.getCertificate();
            if (var3 != null) {
               var2 = var3.getSubjectX500Principal();
            }

            if (var2 != null) {
               return var2;
            } else {
               Iterator var4 = var0.iterator();

               while(var4.hasNext()) {
                  CertStore var5 = (CertStore)var4.next();

                  try {
                     Collection var6 = var5.getCertificates(var1);
                     if (!var6.isEmpty()) {
                        X509Certificate var7 = (X509Certificate)var6.iterator().next();
                        return var7.getSubjectX500Principal();
                     }
                  } catch (CertStoreException var8) {
                     if (PKIX.debug != null) {
                        PKIX.debug.println("BuilderParams.getTargetSubjectDN: non-fatal exception retrieving certs: " + var8);
                        var8.printStackTrace();
                     }
                  }
               }

               throw new InvalidAlgorithmParameterException("Could not determine unique target subject");
            }
         }
      }
   }

   static class ValidatorParams {
      private final PKIXParameters params;
      private CertPath certPath;
      private List<PKIXCertPathChecker> checkers;
      private List<CertStore> stores;
      private boolean gotDate;
      private Date date;
      private Set<String> policies;
      private boolean gotConstraints;
      private CertSelector constraints;
      private Set<TrustAnchor> anchors;
      private List<X509Certificate> certs;
      private Timestamp timestamp;
      private String variant;

      ValidatorParams(CertPath var1, PKIXParameters var2) throws InvalidAlgorithmParameterException {
         this(var2);
         if (!var1.getType().equals("X.509") && !var1.getType().equals("X509")) {
            throw new InvalidAlgorithmParameterException("inappropriate CertPath type specified, must be X.509 or X509");
         } else {
            this.certPath = var1;
         }
      }

      ValidatorParams(PKIXParameters var1) throws InvalidAlgorithmParameterException {
         if (var1 instanceof PKIXExtendedParameters) {
            this.timestamp = ((PKIXExtendedParameters)var1).getTimestamp();
            this.variant = ((PKIXExtendedParameters)var1).getVariant();
         }

         this.anchors = var1.getTrustAnchors();
         Iterator var2 = this.anchors.iterator();

         TrustAnchor var3;
         do {
            if (!var2.hasNext()) {
               this.params = var1;
               return;
            }

            var3 = (TrustAnchor)var2.next();
         } while(var3.getNameConstraints() == null);

         throw new InvalidAlgorithmParameterException("name constraints in trust anchor not supported");
      }

      CertPath certPath() {
         return this.certPath;
      }

      void setCertPath(CertPath var1) {
         this.certPath = var1;
      }

      List<X509Certificate> certificates() {
         if (this.certs == null) {
            if (this.certPath == null) {
               this.certs = Collections.emptyList();
            } else {
               ArrayList var1 = new ArrayList(this.certPath.getCertificates());
               Collections.reverse(var1);
               this.certs = var1;
            }
         }

         return this.certs;
      }

      List<PKIXCertPathChecker> certPathCheckers() {
         if (this.checkers == null) {
            this.checkers = this.params.getCertPathCheckers();
         }

         return this.checkers;
      }

      List<CertStore> certStores() {
         if (this.stores == null) {
            this.stores = this.params.getCertStores();
         }

         return this.stores;
      }

      Date date() {
         if (!this.gotDate) {
            this.date = this.params.getDate();
            if (this.date == null) {
               this.date = new Date();
            }

            this.gotDate = true;
         }

         return this.date;
      }

      Set<String> initialPolicies() {
         if (this.policies == null) {
            this.policies = this.params.getInitialPolicies();
         }

         return this.policies;
      }

      CertSelector targetCertConstraints() {
         if (!this.gotConstraints) {
            this.constraints = this.params.getTargetCertConstraints();
            this.gotConstraints = true;
         }

         return this.constraints;
      }

      Set<TrustAnchor> trustAnchors() {
         return this.anchors;
      }

      boolean revocationEnabled() {
         return this.params.isRevocationEnabled();
      }

      boolean policyMappingInhibited() {
         return this.params.isPolicyMappingInhibited();
      }

      boolean explicitPolicyRequired() {
         return this.params.isExplicitPolicyRequired();
      }

      boolean policyQualifiersRejected() {
         return this.params.getPolicyQualifiersRejected();
      }

      String sigProvider() {
         return this.params.getSigProvider();
      }

      boolean anyPolicyInhibited() {
         return this.params.isAnyPolicyInhibited();
      }

      PKIXParameters getPKIXParameters() {
         return this.params;
      }

      Timestamp timestamp() {
         return this.timestamp;
      }

      String variant() {
         return this.variant;
      }
   }
}
