package sun.security.validator;

import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.Timestamp;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.action.GetBooleanAction;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.PKIXExtendedParameters;

public final class PKIXValidator extends Validator {
   private static final boolean checkTLSRevocation = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("com.sun.net.ssl.checkRevocation")));
   private static final boolean TRY_VALIDATOR = true;
   private final Set<X509Certificate> trustedCerts;
   private final PKIXBuilderParameters parameterTemplate;
   private int certPathLength = -1;
   private final Map<X500Principal, List<PublicKey>> trustedSubjects;
   private final CertificateFactory factory;
   private final boolean plugin;

   PKIXValidator(String var1, Collection<X509Certificate> var2) {
      super("PKIX", var1);
      if (var2 instanceof Set) {
         this.trustedCerts = (Set)var2;
      } else {
         this.trustedCerts = new HashSet(var2);
      }

      HashSet var3 = new HashSet();
      Iterator var4 = var2.iterator();

      X509Certificate var5;
      while(var4.hasNext()) {
         var5 = (X509Certificate)var4.next();
         var3.add(new TrustAnchor(var5, (byte[])null));
      }

      try {
         this.parameterTemplate = new PKIXBuilderParameters(var3, (CertSelector)null);
      } catch (InvalidAlgorithmParameterException var9) {
         throw new RuntimeException("Unexpected error: " + var9.toString(), var9);
      }

      this.setDefaultParameters(var1);
      this.trustedSubjects = new HashMap();

      Object var7;
      for(var4 = var2.iterator(); var4.hasNext(); ((List)var7).add(var5.getPublicKey())) {
         var5 = (X509Certificate)var4.next();
         X500Principal var6 = var5.getSubjectX500Principal();
         if (this.trustedSubjects.containsKey(var6)) {
            var7 = (List)this.trustedSubjects.get(var6);
         } else {
            var7 = new ArrayList();
            this.trustedSubjects.put(var6, var7);
         }
      }

      try {
         this.factory = CertificateFactory.getInstance("X.509");
      } catch (CertificateException var8) {
         throw new RuntimeException("Internal error", var8);
      }

      this.plugin = var1.equals("plugin code signing");
   }

   PKIXValidator(String var1, PKIXBuilderParameters var2) {
      super("PKIX", var1);
      this.trustedCerts = new HashSet();
      Iterator var3 = var2.getTrustAnchors().iterator();

      while(var3.hasNext()) {
         TrustAnchor var4 = (TrustAnchor)var3.next();
         X509Certificate var5 = var4.getTrustedCert();
         if (var5 != null) {
            this.trustedCerts.add(var5);
         }
      }

      this.parameterTemplate = var2;
      this.trustedSubjects = new HashMap();

      Object var6;
      X509Certificate var8;
      for(var3 = this.trustedCerts.iterator(); var3.hasNext(); ((List)var6).add(var8.getPublicKey())) {
         var8 = (X509Certificate)var3.next();
         X500Principal var9 = var8.getSubjectX500Principal();
         if (this.trustedSubjects.containsKey(var9)) {
            var6 = (List)this.trustedSubjects.get(var9);
         } else {
            var6 = new ArrayList();
            this.trustedSubjects.put(var9, var6);
         }
      }

      try {
         this.factory = CertificateFactory.getInstance("X.509");
      } catch (CertificateException var7) {
         throw new RuntimeException("Internal error", var7);
      }

      this.plugin = var1.equals("plugin code signing");
   }

   public Collection<X509Certificate> getTrustedCertificates() {
      return this.trustedCerts;
   }

   public int getCertPathLength() {
      return this.certPathLength;
   }

   private void setDefaultParameters(String var1) {
      if (var1 != "tls server" && var1 != "tls client") {
         this.parameterTemplate.setRevocationEnabled(false);
      } else {
         this.parameterTemplate.setRevocationEnabled(checkTLSRevocation);
      }

   }

   public PKIXBuilderParameters getParameters() {
      return this.parameterTemplate;
   }

   X509Certificate[] engineValidate(X509Certificate[] var1, Collection<X509Certificate> var2, AlgorithmConstraints var3, Object var4) throws CertificateException {
      if (var1 != null && var1.length != 0) {
         PKIXExtendedParameters var5 = null;

         try {
            var5 = new PKIXExtendedParameters((PKIXBuilderParameters)this.parameterTemplate.clone(), var4 instanceof Timestamp ? (Timestamp)var4 : null, this.variant);
         } catch (InvalidAlgorithmParameterException var13) {
         }

         if (var3 != null) {
            var5.addCertPathChecker(new AlgorithmChecker(var3, (Timestamp)null, this.variant));
         }

         X500Principal var6 = null;

         X500Principal var9;
         X509Certificate[] var10;
         for(int var7 = 0; var7 < var1.length; ++var7) {
            X509Certificate var8 = var1[var7];
            var9 = var8.getSubjectX500Principal();
            if (var7 != 0 && !var9.equals(var6)) {
               return this.doBuild(var1, var2, var5);
            }

            if (this.trustedCerts.contains(var8) || this.trustedSubjects.containsKey(var9) && ((List)this.trustedSubjects.get(var9)).contains(var8.getPublicKey())) {
               if (var7 == 0) {
                  return new X509Certificate[]{var1[0]};
               } else {
                  var10 = new X509Certificate[var7];
                  System.arraycopy(var1, 0, var10, 0, var7);
                  return this.doValidate(var10, var5);
               }
            }

            var6 = var8.getIssuerX500Principal();
         }

         X509Certificate var14 = var1[var1.length - 1];
         X500Principal var15 = var14.getIssuerX500Principal();
         var9 = var14.getSubjectX500Principal();
         if (this.trustedSubjects.containsKey(var15) && this.isSignatureValid((List)this.trustedSubjects.get(var15), var14)) {
            return this.doValidate(var1, var5);
         } else if (this.plugin) {
            if (var1.length > 1) {
               var10 = new X509Certificate[var1.length - 1];
               System.arraycopy(var1, 0, var10, 0, var10.length);

               try {
                  var5.setTrustAnchors(Collections.singleton(new TrustAnchor(var1[var1.length - 1], (byte[])null)));
               } catch (InvalidAlgorithmParameterException var12) {
                  throw new CertificateException(var12);
               }

               this.doValidate(var10, var5);
            }

            throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
         } else {
            return this.doBuild(var1, var2, var5);
         }
      } else {
         throw new CertificateException("null or zero-length certificate chain");
      }
   }

   private boolean isSignatureValid(List<PublicKey> var1, X509Certificate var2) {
      if (!this.plugin) {
         return true;
      } else {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            PublicKey var4 = (PublicKey)var3.next();

            try {
               var2.verify(var4);
               return true;
            } catch (Exception var6) {
            }
         }

         return false;
      }
   }

   private static X509Certificate[] toArray(CertPath var0, TrustAnchor var1) throws CertificateException {
      List var2 = var0.getCertificates();
      X509Certificate[] var3 = new X509Certificate[var2.size() + 1];
      var2.toArray(var3);
      X509Certificate var4 = var1.getTrustedCert();
      if (var4 == null) {
         throw new ValidatorException("TrustAnchor must be specified as certificate");
      } else {
         var3[var3.length - 1] = var4;
         return var3;
      }
   }

   private void setDate(PKIXBuilderParameters var1) {
      Date var2 = this.validationDate;
      if (var2 != null) {
         var1.setDate(var2);
      }

   }

   private X509Certificate[] doValidate(X509Certificate[] var1, PKIXBuilderParameters var2) throws CertificateException {
      try {
         this.setDate(var2);
         CertPathValidator var3 = CertPathValidator.getInstance("PKIX");
         CertPath var4 = this.factory.generateCertPath(Arrays.asList(var1));
         this.certPathLength = var1.length;
         PKIXCertPathValidatorResult var5 = (PKIXCertPathValidatorResult)var3.validate(var4, var2);
         return toArray(var4, var5.getTrustAnchor());
      } catch (GeneralSecurityException var6) {
         throw new ValidatorException("PKIX path validation failed: " + var6.toString(), var6);
      }
   }

   private X509Certificate[] doBuild(X509Certificate[] var1, Collection<X509Certificate> var2, PKIXBuilderParameters var3) throws CertificateException {
      try {
         this.setDate(var3);
         X509CertSelector var4 = new X509CertSelector();
         var4.setCertificate(var1[0]);
         var3.setTargetCertConstraints(var4);
         ArrayList var5 = new ArrayList();
         var5.addAll(Arrays.asList(var1));
         if (var2 != null) {
            var5.addAll(var2);
         }

         CertStore var6 = CertStore.getInstance("Collection", new CollectionCertStoreParameters(var5));
         var3.addCertStore(var6);
         CertPathBuilder var7 = CertPathBuilder.getInstance("PKIX");
         PKIXCertPathBuilderResult var8 = (PKIXCertPathBuilderResult)var7.build(var3);
         return toArray(var8.getCertPath(), var8.getTrustAnchor());
      } catch (GeneralSecurityException var9) {
         throw new ValidatorException("PKIX path building failed: " + var9.toString(), var9);
      }
   }
}
