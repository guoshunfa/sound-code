package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathBuilderSpi {
   public abstract CertPathBuilderResult engineBuild(CertPathParameters var1) throws CertPathBuilderException, InvalidAlgorithmParameterException;

   public CertPathChecker engineGetRevocationChecker() {
      throw new UnsupportedOperationException();
   }
}
