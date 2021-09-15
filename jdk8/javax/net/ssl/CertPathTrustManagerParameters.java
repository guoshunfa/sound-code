package javax.net.ssl;

import java.security.cert.CertPathParameters;

public class CertPathTrustManagerParameters implements ManagerFactoryParameters {
   private final CertPathParameters parameters;

   public CertPathTrustManagerParameters(CertPathParameters var1) {
      this.parameters = (CertPathParameters)var1.clone();
   }

   public CertPathParameters getParameters() {
      return (CertPathParameters)this.parameters.clone();
   }
}
