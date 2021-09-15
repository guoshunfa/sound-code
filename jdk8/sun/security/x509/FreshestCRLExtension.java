package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class FreshestCRLExtension extends CRLDistributionPointsExtension {
   public static final String NAME = "FreshestCRL";

   public FreshestCRLExtension(List<DistributionPoint> var1) throws IOException {
      super(PKIXExtensions.FreshestCRL_Id, false, var1, "FreshestCRL");
   }

   public FreshestCRLExtension(Boolean var1, Object var2) throws IOException {
      super(PKIXExtensions.FreshestCRL_Id, var1, var2, "FreshestCRL");
   }

   public void encode(OutputStream var1) throws IOException {
      super.encode(var1, PKIXExtensions.FreshestCRL_Id, false);
   }
}
