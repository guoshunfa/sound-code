package java.security.spec;

import java.math.BigInteger;

public class DSAPublicKeySpec implements KeySpec {
   private BigInteger y;
   private BigInteger p;
   private BigInteger q;
   private BigInteger g;

   public DSAPublicKeySpec(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      this.y = var1;
      this.p = var2;
      this.q = var3;
      this.g = var4;
   }

   public BigInteger getY() {
      return this.y;
   }

   public BigInteger getP() {
      return this.p;
   }

   public BigInteger getQ() {
      return this.q;
   }

   public BigInteger getG() {
      return this.g;
   }
}
