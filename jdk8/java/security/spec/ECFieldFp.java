package java.security.spec;

import java.math.BigInteger;

public class ECFieldFp implements ECField {
   private BigInteger p;

   public ECFieldFp(BigInteger var1) {
      if (var1.signum() != 1) {
         throw new IllegalArgumentException("p is not positive");
      } else {
         this.p = var1;
      }
   }

   public int getFieldSize() {
      return this.p.bitLength();
   }

   public BigInteger getP() {
      return this.p;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof ECFieldFp ? this.p.equals(((ECFieldFp)var1).p) : false;
      }
   }

   public int hashCode() {
      return this.p.hashCode();
   }
}
