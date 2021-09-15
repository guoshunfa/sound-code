package java.security.spec;

import java.math.BigInteger;

public class RSAOtherPrimeInfo {
   private BigInteger prime;
   private BigInteger primeExponent;
   private BigInteger crtCoefficient;

   public RSAOtherPrimeInfo(BigInteger var1, BigInteger var2, BigInteger var3) {
      if (var1 == null) {
         throw new NullPointerException("the prime parameter must be non-null");
      } else if (var2 == null) {
         throw new NullPointerException("the primeExponent parameter must be non-null");
      } else if (var3 == null) {
         throw new NullPointerException("the crtCoefficient parameter must be non-null");
      } else {
         this.prime = var1;
         this.primeExponent = var2;
         this.crtCoefficient = var3;
      }
   }

   public final BigInteger getPrime() {
      return this.prime;
   }

   public final BigInteger getExponent() {
      return this.primeExponent;
   }

   public final BigInteger getCrtCoefficient() {
      return this.crtCoefficient;
   }
}
