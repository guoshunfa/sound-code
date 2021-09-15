package java.security.cert;

import java.security.PublicKey;

public class PKIXCertPathBuilderResult extends PKIXCertPathValidatorResult implements CertPathBuilderResult {
   private CertPath certPath;

   public PKIXCertPathBuilderResult(CertPath var1, TrustAnchor var2, PolicyNode var3, PublicKey var4) {
      super(var2, var3, var4);
      if (var1 == null) {
         throw new NullPointerException("certPath must be non-null");
      } else {
         this.certPath = var1;
      }
   }

   public CertPath getCertPath() {
      return this.certPath;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("PKIXCertPathBuilderResult: [\n");
      var1.append("  Certification Path: " + this.certPath + "\n");
      var1.append("  Trust Anchor: " + this.getTrustAnchor().toString() + "\n");
      var1.append("  Policy Tree: " + String.valueOf((Object)this.getPolicyTree()) + "\n");
      var1.append("  Subject Public Key: " + this.getPublicKey() + "\n");
      var1.append("]");
      return var1.toString();
   }
}
