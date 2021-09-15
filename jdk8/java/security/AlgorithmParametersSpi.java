package java.security;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public abstract class AlgorithmParametersSpi {
   protected abstract void engineInit(AlgorithmParameterSpec var1) throws InvalidParameterSpecException;

   protected abstract void engineInit(byte[] var1) throws IOException;

   protected abstract void engineInit(byte[] var1, String var2) throws IOException;

   protected abstract <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> var1) throws InvalidParameterSpecException;

   protected abstract byte[] engineGetEncoded() throws IOException;

   protected abstract byte[] engineGetEncoded(String var1) throws IOException;

   protected abstract String engineToString();
}
