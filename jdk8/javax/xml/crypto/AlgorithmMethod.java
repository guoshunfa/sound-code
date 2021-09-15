package javax.xml.crypto;

import java.security.spec.AlgorithmParameterSpec;

public interface AlgorithmMethod {
   String getAlgorithm();

   AlgorithmParameterSpec getParameterSpec();
}
