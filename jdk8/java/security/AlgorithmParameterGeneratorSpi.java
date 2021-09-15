package java.security;

import java.security.spec.AlgorithmParameterSpec;

public abstract class AlgorithmParameterGeneratorSpi {
   protected abstract void engineInit(int var1, SecureRandom var2);

   protected abstract void engineInit(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException;

   protected abstract AlgorithmParameters engineGenerateParameters();
}
