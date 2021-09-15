package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.Timestamp;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.TrustAnchor;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PKIXTimestampParameters extends PKIXBuilderParameters {
   private final PKIXBuilderParameters p;
   private Timestamp jarTimestamp;

   public PKIXTimestampParameters(PKIXBuilderParameters var1, Timestamp var2) throws InvalidAlgorithmParameterException {
      super((Set)var1.getTrustAnchors(), (CertSelector)null);
      this.p = var1;
      this.jarTimestamp = var2;
   }

   public Timestamp getTimestamp() {
      return this.jarTimestamp;
   }

   public void setTimestamp(Timestamp var1) {
      this.jarTimestamp = var1;
   }

   public void setDate(Date var1) {
      this.p.setDate(var1);
   }

   public void addCertPathChecker(PKIXCertPathChecker var1) {
      this.p.addCertPathChecker(var1);
   }

   public void setMaxPathLength(int var1) {
      this.p.setMaxPathLength(var1);
   }

   public int getMaxPathLength() {
      return this.p.getMaxPathLength();
   }

   public String toString() {
      return this.p.toString();
   }

   public Set<TrustAnchor> getTrustAnchors() {
      return this.p.getTrustAnchors();
   }

   public void setTrustAnchors(Set<TrustAnchor> var1) throws InvalidAlgorithmParameterException {
      if (this.p != null) {
         this.p.setTrustAnchors(var1);
      }
   }

   public Set<String> getInitialPolicies() {
      return this.p.getInitialPolicies();
   }

   public void setInitialPolicies(Set<String> var1) {
      this.p.setInitialPolicies(var1);
   }

   public void setCertStores(List<CertStore> var1) {
      this.p.setCertStores(var1);
   }

   public void addCertStore(CertStore var1) {
      this.p.addCertStore(var1);
   }

   public List<CertStore> getCertStores() {
      return this.p.getCertStores();
   }

   public void setRevocationEnabled(boolean var1) {
      this.p.setRevocationEnabled(var1);
   }

   public boolean isRevocationEnabled() {
      return this.p.isRevocationEnabled();
   }

   public void setExplicitPolicyRequired(boolean var1) {
      this.p.setExplicitPolicyRequired(var1);
   }

   public boolean isExplicitPolicyRequired() {
      return this.p.isExplicitPolicyRequired();
   }

   public void setPolicyMappingInhibited(boolean var1) {
      this.p.setPolicyMappingInhibited(var1);
   }

   public boolean isPolicyMappingInhibited() {
      return this.p.isPolicyMappingInhibited();
   }

   public void setAnyPolicyInhibited(boolean var1) {
      this.p.setAnyPolicyInhibited(var1);
   }

   public boolean isAnyPolicyInhibited() {
      return this.p.isAnyPolicyInhibited();
   }

   public void setPolicyQualifiersRejected(boolean var1) {
      this.p.setPolicyQualifiersRejected(var1);
   }

   public boolean getPolicyQualifiersRejected() {
      return this.p.getPolicyQualifiersRejected();
   }

   public Date getDate() {
      return this.p.getDate();
   }

   public void setCertPathCheckers(List<PKIXCertPathChecker> var1) {
      this.p.setCertPathCheckers(var1);
   }

   public List<PKIXCertPathChecker> getCertPathCheckers() {
      return this.p.getCertPathCheckers();
   }

   public String getSigProvider() {
      return this.p.getSigProvider();
   }

   public void setSigProvider(String var1) {
      this.p.setSigProvider(var1);
   }

   public CertSelector getTargetCertConstraints() {
      return this.p.getTargetCertConstraints();
   }

   public void setTargetCertConstraints(CertSelector var1) {
      if (this.p != null) {
         this.p.setTargetCertConstraints(var1);
      }
   }
}
