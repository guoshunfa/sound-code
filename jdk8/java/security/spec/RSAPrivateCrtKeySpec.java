package java.security.spec;

import java.math.BigInteger;

public class RSAPrivateCrtKeySpec extends RSAPrivateKeySpec {
   private final BigInteger publicExponent;
   private final BigInteger primeP;
   private final BigInteger primeQ;
   private final BigInteger primeExponentP;
   private final BigInteger primeExponentQ;
   private final BigInteger crtCoefficient;

   public RSAPrivateCrtKeySpec(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigInteger var7, BigInteger var8) {
      super(var1, var3);
      this.publicExponent = var2;
      this.primeP = var4;
      this.primeQ = var5;
      this.primeExponentP = var6;
      this.primeExponentQ = var7;
      this.crtCoefficient = var8;
   }

   public BigInteger getPublicExponent() {
      return this.publicExponent;
   }

   public BigInteger getPrimeP() {
      return this.primeP;
   }

   public BigInteger getPrimeQ() {
      return this.primeQ;
   }

   public BigInteger getPrimeExponentP() {
      return this.primeExponentP;
   }

   public BigInteger getPrimeExponentQ() {
      return this.primeExponentQ;
   }

   public BigInteger getCrtCoefficient() {
      return this.crtCoefficient;
   }
}
