package java.security;

import java.util.Set;

public interface AlgorithmConstraints {
   boolean permits(Set<CryptoPrimitive> var1, String var2, AlgorithmParameters var3);

   boolean permits(Set<CryptoPrimitive> var1, Key var2);

   boolean permits(Set<CryptoPrimitive> var1, String var2, Key var3, AlgorithmParameters var4);
}
