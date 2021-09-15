package java.security.spec;

import java.math.BigInteger;

public class RSAPrivateKeySpec implements KeySpec {
   private BigInteger modulus;
   private BigInteger privateExponent;

   public RSAPrivateKeySpec(BigInteger var1, BigInteger var2) {
      this.modulus = var1;
      this.privateExponent = var2;
   }

   public BigInteger getModulus() {
      return this.modulus;
   }

   public BigInteger getPrivateExponent() {
      return this.privateExponent;
   }
}
