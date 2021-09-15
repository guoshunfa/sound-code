package java.security.spec;

import java.math.BigInteger;

public class RSAPublicKeySpec implements KeySpec {
   private BigInteger modulus;
   private BigInteger publicExponent;

   public RSAPublicKeySpec(BigInteger var1, BigInteger var2) {
      this.modulus = var1;
      this.publicExponent = var2;
   }

   public BigInteger getModulus() {
      return this.modulus;
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }
}
