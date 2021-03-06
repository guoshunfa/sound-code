package java.security.spec;

public class MGF1ParameterSpec implements AlgorithmParameterSpec {
   public static final MGF1ParameterSpec SHA1 = new MGF1ParameterSpec("SHA-1");
   public static final MGF1ParameterSpec SHA224 = new MGF1ParameterSpec("SHA-224");
   public static final MGF1ParameterSpec SHA256 = new MGF1ParameterSpec("SHA-256");
   public static final MGF1ParameterSpec SHA384 = new MGF1ParameterSpec("SHA-384");
   public static final MGF1ParameterSpec SHA512 = new MGF1ParameterSpec("SHA-512");
   private String mdName;

   public MGF1ParameterSpec(String var1) {
      if (var1 == null) {
         throw new NullPointerException("digest algorithm is null");
      } else {
         this.mdName = var1;
      }
   }

   public String getDigestAlgorithm() {
      return this.mdName;
   }
}
