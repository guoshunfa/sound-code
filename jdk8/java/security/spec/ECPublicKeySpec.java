package java.security.spec;

public class ECPublicKeySpec implements KeySpec {
   private ECPoint w;
   private ECParameterSpec params;

   public ECPublicKeySpec(ECPoint var1, ECParameterSpec var2) {
      if (var1 == null) {
         throw new NullPointerException("w is null");
      } else if (var2 == null) {
         throw new NullPointerException("params is null");
      } else if (var1 == ECPoint.POINT_INFINITY) {
         throw new IllegalArgumentException("w is ECPoint.POINT_INFINITY");
      } else {
         this.w = var1;
         this.params = var2;
      }
   }

   public ECPoint getW() {
      return this.w;
   }

   public ECParameterSpec getParams() {
      return this.params;
   }
}
