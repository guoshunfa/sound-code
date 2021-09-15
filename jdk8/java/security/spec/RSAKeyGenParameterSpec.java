package java.security.spec;

import java.math.BigInteger;

public class RSAKeyGenParameterSpec implements AlgorithmParameterSpec {
   private int keysize;
   private BigInteger publicExponent;
   public static final BigInteger F0 = BigInteger.valueOf(3L);
   public static final BigInteger F4 = BigInteger.valueOf(65537L);

   public RSAKeyGenParameterSpec(int var1, BigInteger var2) {
      this.keysize = var1;
      this.publicExponent = var2;
   }

   public int getKeysize() {
      return this.keysize;
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }
}
