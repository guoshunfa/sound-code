package java.security.spec;

public class ECGenParameterSpec implements AlgorithmParameterSpec {
   private String name;

   public ECGenParameterSpec(String var1) {
      if (var1 == null) {
         throw new NullPointerException("stdName is null");
      } else {
         this.name = var1;
      }
   }

   public String getName() {
      return this.name;
   }
}
