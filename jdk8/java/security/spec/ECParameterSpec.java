package java.security.spec;

import java.math.BigInteger;

public class ECParameterSpec implements AlgorithmParameterSpec {
   private final EllipticCurve curve;
   private final ECPoint g;
   private final BigInteger n;
   private final int h;

   public ECParameterSpec(EllipticCurve var1, ECPoint var2, BigInteger var3, int var4) {
      if (var1 == null) {
         throw new NullPointerException("curve is null");
      } else if (var2 == null) {
         throw new NullPointerException("g is null");
      } else if (var3 == null) {
         throw new NullPointerException("n is null");
      } else if (var3.signum() != 1) {
         throw new IllegalArgumentException("n is not positive");
      } else if (var4 <= 0) {
         throw new IllegalArgumentException("h is not positive");
      } else {
         this.curve = var1;
         this.g = var2;
         this.n = var3;
         this.h = var4;
      }
   }

   public EllipticCurve getCurve() {
      return this.curve;
   }

   public ECPoint getGenerator() {
      return this.g;
   }

   public BigInteger getOrder() {
      return this.n;
   }

   public int getCofactor() {
      return this.h;
   }
}
