package java.security;

import java.security.spec.AlgorithmParameterSpec;

public abstract class KeyPairGeneratorSpi {
   public abstract void initialize(int var1, SecureRandom var2);

   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      throw new UnsupportedOperationException();
   }

   public abstract KeyPair generateKeyPair();
}
