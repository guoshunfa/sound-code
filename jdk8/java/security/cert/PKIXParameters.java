package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PKIXParameters implements CertPathParameters {
   private Set<TrustAnchor> unmodTrustAnchors;
   private Date date;
   private List<PKIXCertPathChecker> certPathCheckers;
   private String sigProvider;
   private boolean revocationEnabled = true;
   private Set<String> unmodInitialPolicies;
   private boolean explicitPolicyRequired = false;
   private boolean policyMappingInhibited = false;
   private boolean anyPolicyInhibited = false;
   private boolean policyQualifiersRejected = true;
   private List<CertStore> certStores;
   private CertSelector certSelector;

   public PKIXParameters(Set<TrustAnchor> var1) throws InvalidAlgorithmParameterException {
      this.setTrustAnchors(var1);
      this.unmodInitialPolicies = Collections.emptySet();
      this.certPathCheckers = new ArrayList();
      this.certStores = new ArrayList();
   }

   public PKIXParameters(KeyStore var1) throws KeyStoreException, InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new NullPointerException("the keystore parameter must be non-null");
      } else {
         HashSet var2 = new HashSet();
         Enumeration var3 = var1.aliases();

         while(var3.hasMoreElements()) {
            String var4 = (String)var3.nextElement();
            if (var1.isCertificateEntry(var4)) {
               Certificate var5 = var1.getCertificate(var4);
               if (var5 instanceof X509Certificate) {
                  var2.add(new TrustAnchor((X509Certificate)var5, (byte[])null));
               }
            }
         }

         this.setTrustAnchors(var2);
         this.unmodInitialPolicies = Collections.emptySet();
         this.certPathCheckers = new ArrayList();
         this.certStores = new ArrayList();
      }
   }

   public Set<TrustAnchor> getTrustAnchors() {
      return this.unmodTrustAnchors;
   }

   public void setTrustAnchors(Set<TrustAnchor> var1) throws InvalidAlgorithmParameterException {
      if (var1 == null) {
         throw new NullPointerException("the trustAnchors parameters must be non-null");
      } else if (var1.isEmpty()) {
         throw new InvalidAlgorithmParameterException("the trustAnchors parameter must be non-empty");
      } else {
         Iterator var2 = var1.iterator();

         do {
            if (!var2.hasNext()) {
               this.unmodTrustAnchors = Collections.unmodifiableSet(new HashSet(var1));
               return;
            }
         } while(var2.next() instanceof TrustAnchor);

         throw new ClassCastException("all elements of set must be of type java.security.cert.TrustAnchor");
      }
   }

   public Set<String> getInitialPolicies() {
      return this.unmodInitialPolicies;
   }

   public void setInitialPolicies(Set<String> var1) {
      if (var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            if (!(var2.next() instanceof String)) {
               throw new ClassCastException("all elements of set must be of type java.lang.String");
            }
         }

         this.unmodInitialPolicies = Collections.unmodifiableSet(new HashSet(var1));
      } else {
         this.unmodInitialPolicies = Collections.emptySet();
      }

   }

   public void setCertStores(List<CertStore> var1) {
      if (var1 == null) {
         this.certStores = new ArrayList();
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            if (!(var2.next() instanceof CertStore)) {
               throw new ClassCastException("all elements of list must be of type java.security.cert.CertStore");
            }
         }

         this.certStores = new ArrayList(var1);
      }

   }

   public void addCertStore(CertStore var1) {
      if (var1 != null) {
         this.certStores.add(var1);
      }

   }

   public List<CertStore> getCertStores() {
      return Collections.unmodifiableList(new ArrayList(this.certStores));
   }

   public void setRevocationEnabled(boolean var1) {
      this.revocationEnabled = var1;
   }

   public boolean isRevocationEnabled() {
      return this.revocationEnabled;
   }

   public void setExplicitPolicyRequired(boolean var1) {
      this.explicitPolicyRequired = var1;
   }

   public boolean isExplicitPolicyRequired() {
      return this.explicitPolicyRequired;
   }

   public void setPolicyMappingInhibited(boolean var1) {
      this.policyMappingInhibited = var1;
   }

   public boolean isPolicyMappingInhibited() {
      return this.policyMappingInhibited;
   }

   public void setAnyPolicyInhibited(boolean var1) {
      this.anyPolicyInhibited = var1;
   }

   public boolean isAnyPolicyInhibited() {
      return this.anyPolicyInhibited;
   }

   public void setPolicyQualifiersRejected(boolean var1) {
      this.policyQualifiersRejected = var1;
   }

   public boolean getPolicyQualifiersRejected() {
      return this.policyQualifiersRejected;
   }

   public Date getDate() {
      return this.date == null ? null : (Date)this.date.clone();
   }

   public void setDate(Date var1) {
      if (var1 != null) {
         this.date = (Date)var1.clone();
      } else {
         var1 = null;
      }

   }

   public void setCertPathCheckers(List<PKIXCertPathChecker> var1) {
      if (var1 != null) {
         ArrayList var2 = new ArrayList();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            PKIXCertPathChecker var4 = (PKIXCertPathChecker)var3.next();
            var2.add((PKIXCertPathChecker)var4.clone());
         }

         this.certPathCheckers = var2;
      } else {
         this.certPathCheckers = new ArrayList();
      }

   }

   public List<PKIXCertPathChecker> getCertPathCheckers() {
      ArrayList var1 = new ArrayList();
      Iterator var2 = this.certPathCheckers.iterator();

      while(var2.hasNext()) {
         PKIXCertPathChecker var3 = (PKIXCertPathChecker)var2.next();
         var1.add((PKIXCertPathChecker)var3.clone());
      }

      return Collections.unmodifiableList(var1);
   }

   public void addCertPathChecker(PKIXCertPathChecker var1) {
      if (var1 != null) {
         this.certPathCheckers.add((PKIXCertPathChecker)var1.clone());
      }

   }

   public String getSigProvider() {
      return this.sigProvider;
   }

   public void setSigProvider(String var1) {
      this.sigProvider = var1;
   }

   public CertSelector getTargetCertConstraints() {
      return this.certSelector != null ? (CertSelector)this.certSelector.clone() : null;
   }

   public void setTargetCertConstraints(CertSelector var1) {
      if (var1 != null) {
         this.certSelector = (CertSelector)var1.clone();
      } else {
         this.certSelector = null;
      }

   }

   public Object clone() {
      try {
         PKIXParameters var1 = (PKIXParameters)super.clone();
         if (this.certStores != null) {
            var1.certStores = new ArrayList(this.certStores);
         }

         if (this.certPathCheckers != null) {
            var1.certPathCheckers = new ArrayList(this.certPathCheckers.size());
            Iterator var2 = this.certPathCheckers.iterator();

            while(var2.hasNext()) {
               PKIXCertPathChecker var3 = (PKIXCertPathChecker)var2.next();
               var1.certPathCheckers.add((PKIXCertPathChecker)var3.clone());
            }
         }

         return var1;
      } catch (CloneNotSupportedException var4) {
         throw new InternalError(var4.toString(), var4);
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("[\n");
      if (this.unmodTrustAnchors != null) {
         var1.append("  Trust Anchors: " + this.unmodTrustAnchors.toString() + "\n");
      }

      if (this.unmodInitialPolicies != null) {
         if (this.unmodInitialPolicies.isEmpty()) {
            var1.append("  Initial Policy OIDs: any\n");
         } else {
            var1.append("  Initial Policy OIDs: [" + this.unmodInitialPolicies.toString() + "]\n");
         }
      }

      var1.append("  Validity Date: " + String.valueOf((Object)this.date) + "\n");
      var1.append("  Signature Provider: " + this.sigProvider + "\n");
      var1.append("  Default Revocation Enabled: " + this.revocationEnabled + "\n");
      var1.append("  Explicit Policy Required: " + this.explicitPolicyRequired + "\n");
      var1.append("  Policy Mapping Inhibited: " + this.policyMappingInhibited + "\n");
      var1.append("  Any Policy Inhibited: " + this.anyPolicyInhibited + "\n");
      var1.append("  Policy Qualifiers Rejected: " + this.policyQualifiersRejected + "\n");
      var1.append("  Target Cert Constraints: " + String.valueOf((Object)this.certSelector) + "\n");
      if (this.certPathCheckers != null) {
         var1.append("  Certification Path Checkers: [" + this.certPathCheckers.toString() + "]\n");
      }

      if (this.certStores != null) {
         var1.append("  CertStores: [" + this.certStores.toString() + "]\n");
      }

      var1.append("]");
      return var1.toString();
   }
}
