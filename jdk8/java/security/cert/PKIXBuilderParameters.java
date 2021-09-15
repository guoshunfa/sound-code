package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Set;

public class PKIXBuilderParameters extends PKIXParameters {
   private int maxPathLength = 5;

   public PKIXBuilderParameters(Set<TrustAnchor> var1, CertSelector var2) throws InvalidAlgorithmParameterException {
      super(var1);
      this.setTargetCertConstraints(var2);
   }

   public PKIXBuilderParameters(KeyStore var1, CertSelector var2) throws KeyStoreException, InvalidAlgorithmParameterException {
      super(var1);
      this.setTargetCertConstraints(var2);
   }

   public void setMaxPathLength(int var1) {
      if (var1 < -1) {
         throw new InvalidParameterException("the maximum path length parameter can not be less than -1");
      } else {
         this.maxPathLength = var1;
      }
   }

   public int getMaxPathLength() {
      return this.maxPathLength;
   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("[\n");
      var1.append(super.toString());
      var1.append("  Maximum Path Length: " + this.maxPathLength + "\n");
      var1.append("]\n");
      return var1.toString();
   }
}
