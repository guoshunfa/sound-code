package java.security.spec;

import java.math.BigInteger;

public class DSAPrivateKeySpec implements KeySpec {
   private BigInteger x;
   private BigInteger p;
   private BigInteger q;
   private BigInteger g;

   public DSAPrivateKeySpec(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      this.x = var1;
      this.p = var2;
      this.q = var3;
      this.g = var4;
   }

   public BigInteger getX() {
      return this.x;
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
