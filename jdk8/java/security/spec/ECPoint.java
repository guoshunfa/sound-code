package java.security.spec;

import java.math.BigInteger;

public class ECPoint {
   private final BigInteger x;
   private final BigInteger y;
   public static final ECPoint POINT_INFINITY = new ECPoint();

   private ECPoint() {
      this.x = null;
      this.y = null;
   }

   public ECPoint(BigInteger var1, BigInteger var2) {
      if (var1 != null && var2 != null) {
         this.x = var1;
         this.y = var2;
      } else {
         throw new NullPointerException("affine coordinate x or y is null");
      }
   }

   public BigInteger getAffineX() {
      return this.x;
   }

   public BigInteger getAffineY() {
      return this.y;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (this == POINT_INFINITY) {
         return false;
      } else if (!(var1 instanceof ECPoint)) {
         return false;
      } else {
         return this.x.equals(((ECPoint)var1).x) && this.y.equals(((ECPoint)var1).y);
      }
   }

   public int hashCode() {
      return this == POINT_INFINITY ? 0 : this.x.hashCode() << 5 + this.y.hashCode();
   }
}
