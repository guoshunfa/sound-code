package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathValidatorSpi {
   public abstract CertPathValidatorResult engineValidate(CertPath var1, CertPathParameters var2) throws CertPathValidatorException, InvalidAlgorithmParameterException;

   public CertPathChecker engineGetRevocationChecker() {
      throw new UnsupportedOperationException();
   }
}
