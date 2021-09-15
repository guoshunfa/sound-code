package java.security.spec;

import java.math.BigInteger;

public class RSAMultiPrimePrivateCrtKeySpec extends RSAPrivateKeySpec {
   private final BigInteger publicExponent;
   private final BigInteger primeP;
   private final BigInteger primeQ;
   private final BigInteger primeExponentP;
   private final BigInteger primeExponentQ;
   private final BigInteger crtCoefficient;
   private final RSAOtherPrimeInfo[] otherPrimeInfo;

   public RSAMultiPrimePrivateCrtKeySpec(BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, BigInteger var7, BigInteger var8, RSAOtherPrimeInfo[] var9) {
      super(var1, var3);
      if (var1 == null) {
         throw new NullPointerException("the modulus parameter must be non-null");
      } else if (var2 == null) {
         throw new NullPointerException("the publicExponent parameter must be non-null");
      } else if (var3 == null) {
         throw new NullPointerException("the privateExponent parameter must be non-null");
      } else if (var4 == null) {
         throw new NullPointerException("the primeP parameter must be non-null");
      } else if (var5 == null) {
         throw new NullPointerException("the primeQ parameter must be non-null");
      } else if (var6 == null) {
         throw new NullPointerException("the primeExponentP parameter must be non-null");
      } else if (var7 == null) {
         throw new NullPointerException("the primeExponentQ parameter must be non-null");
      } else if (var8 == null) {
         throw new NullPointerException("the crtCoefficient parameter must be non-null");
      } else {
         this.publicExponent = var2;
         this.primeP = var4;
         this.primeQ = var5;
         this.primeExponentP = var6;
         this.primeExponentQ = var7;
         this.crtCoefficient = var8;
         if (var9 == null) {
            this.otherPrimeInfo = null;
         } else {
            if (var9.length == 0) {
               throw new IllegalArgumentException("the otherPrimeInfo parameter must not be empty");
            }

            this.otherPrimeInfo = (RSAOtherPrimeInfo[])var9.clone();
         }

      }
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

   public RSAOtherPrimeInfo[] getOtherPrimeInfo() {
      return this.otherPrimeInfo == null ? null : (RSAOtherPrimeInfo[])this.otherPrimeInfo.clone();
   }
}
