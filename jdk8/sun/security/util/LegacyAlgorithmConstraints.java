package sun.security.util;

import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.util.Set;

public class LegacyAlgorithmConstraints extends AbstractAlgorithmConstraints {
   public static final String PROPERTY_TLS_LEGACY_ALGS = "jdk.tls.legacyAlgorithms";
   private final String[] legacyAlgorithms;

   public LegacyAlgorithmConstraints(String var1, AlgorithmDecomposer var2) {
      super(var2);
      this.legacyAlgorithms = getAlgorithms(var1);
   }

   public final boolean permits(Set<CryptoPrimitive> var1, String var2, AlgorithmParameters var3) {
      return checkAlgorithm(this.legacyAlgorithms, var2, this.decomposer);
   }

   public final boolean permits(Set<CryptoPrimitive> var1, Key var2) {
      return true;
   }

   public final boolean permits(Set<CryptoPrimitive> var1, String var2, Key var3, AlgorithmParameters var4) {
      return checkAlgorithm(this.legacyAlgorithms, var2, this.decomposer);
   }
}
