package java.security.spec;

public class PSSParameterSpec implements AlgorithmParameterSpec {
   private String mdName = "SHA-1";
   private String mgfName = "MGF1";
   private AlgorithmParameterSpec mgfSpec;
   private int saltLen;
   private int trailerField;
   public static final PSSParameterSpec DEFAULT = new PSSParameterSpec();

   private PSSParameterSpec() {
      this.mgfSpec = MGF1ParameterSpec.SHA1;
      this.saltLen = 20;
      this.trailerField = 1;
   }

   public PSSParameterSpec(String var1, String var2, AlgorithmParameterSpec var3, int var4, int var5) {
      this.mgfSpec = MGF1ParameterSpec.SHA1;
      this.saltLen = 20;
      this.trailerField = 1;
      if (var1 == null) {
         throw new NullPointerException("digest algorithm is null");
      } else if (var2 == null) {
         throw new NullPointerException("mask generation function algorithm is null");
      } else if (var4 < 0) {
         throw new IllegalArgumentException("negative saltLen value: " + var4);
      } else if (var5 < 0) {
         throw new IllegalArgumentException("negative trailerField: " + var5);
      } else {
         this.mdName = var1;
         this.mgfName = var2;
         this.mgfSpec = var3;
         this.saltLen = var4;
         this.trailerField = var5;
      }
   }

   public PSSParameterSpec(int var1) {
      this.mgfSpec = MGF1ParameterSpec.SHA1;
      this.saltLen = 20;
      this.trailerField = 1;
      if (var1 < 0) {
         throw new IllegalArgumentException("negative saltLen value: " + var1);
      } else {
         this.saltLen = var1;
      }
   }

   public String getDigestAlgorithm() {
      return this.mdName;
   }

   public String getMGFAlgorithm() {
      return this.mgfName;
   }

   public AlgorithmParameterSpec getMGFParameters() {
      return this.mgfSpec;
   }

   public int getSaltLength() {
      return this.saltLen;
   }

   public int getTrailerField() {
      return this.trailerField;
   }
}
