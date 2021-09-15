package java.security.cert;

import java.security.PublicKey;

public class PKIXCertPathValidatorResult implements CertPathValidatorResult {
   private TrustAnchor trustAnchor;
   private PolicyNode policyTree;
   private PublicKey subjectPublicKey;

   public PKIXCertPathValidatorResult(TrustAnchor var1, PolicyNode var2, PublicKey var3) {
      if (var3 == null) {
         throw new NullPointerException("subjectPublicKey must be non-null");
      } else if (var1 == null) {
         throw new NullPointerException("trustAnchor must be non-null");
      } else {
         this.trustAnchor = var1;
         this.policyTree = var2;
         this.subjectPublicKey = var3;
      }
   }

   public TrustAnchor getTrustAnchor() {
      return this.trustAnchor;
   }

   public PolicyNode getPolicyTree() {
      return this.policyTree;
   }

   public PublicKey getPublicKey() {
      return this.subjectPublicKey;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("PKIXCertPathValidatorResult: [\n");
      var1.append("  Trust Anchor: " + this.trustAnchor.toString() + "\n");
      var1.append("  Policy Tree: " + String.valueOf((Object)this.policyTree) + "\n");
      var1.append("  Subject Public Key: " + this.subjectPublicKey + "\n");
      var1.append("]");
      return var1.toString();
   }
}
