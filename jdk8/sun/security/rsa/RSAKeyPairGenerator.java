package sun.security.rsa;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.util.SecurityProviderConstants;

public final class RSAKeyPairGenerator extends KeyPairGeneratorSpi {
   private BigInteger publicExponent;
   private int keySize;
   private SecureRandom random;

   public RSAKeyPairGenerator() {
      this.initialize(SecurityProviderConstants.DEF_RSA_KEY_SIZE, (SecureRandom)null);
   }

   public void initialize(int var1, SecureRandom var2) {
      try {
         RSAKeyFactory.checkKeyLengths(var1, RSAKeyGenParameterSpec.F4, 512, 65536);
      } catch (InvalidKeyException var4) {
         throw new InvalidParameterException(var4.getMessage());
      }

      this.keySize = var1;
      this.random = var2;
      this.publicExponent = RSAKeyGenParameterSpec.F4;
   }

   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof RSAKeyGenParameterSpec)) {
         throw new InvalidAlgorithmParameterException("Params must be instance of RSAKeyGenParameterSpec");
      } else {
         RSAKeyGenParameterSpec var3 = (RSAKeyGenParameterSpec)var1;
         int var4 = var3.getKeysize();
         BigInteger var5 = var3.getPublicExponent();
         if (var5 == null) {
            var5 = RSAKeyGenParameterSpec.F4;
         } else {
            if (var5.compareTo(RSAKeyGenParameterSpec.F0) < 0) {
               throw new InvalidAlgorithmParameterException("Public exponent must be 3 or larger");
            }

            if (var5.bitLength() > var4) {
               throw new InvalidAlgorithmParameterException("Public exponent must be smaller than key size");
            }
         }

         try {
            RSAKeyFactory.checkKeyLengths(var4, var5, 512, 65536);
         } catch (InvalidKeyException var7) {
            throw new InvalidAlgorithmParameterException("Invalid key sizes", var7);
         }

         this.keySize = var4;
         this.publicExponent = var5;
         this.random = var2;
      }
   }

   public KeyPair generateKeyPair() {
      int var1 = this.keySize + 1 >> 1;
      int var2 = this.keySize - var1;
      if (this.random == null) {
         this.random = JCAUtil.getSecureRandom();
      }

      BigInteger var3 = this.publicExponent;

      BigInteger var4;
      BigInteger var5;
      BigInteger var6;
      BigInteger var7;
      BigInteger var8;
      BigInteger var9;
      do {
         var4 = BigInteger.probablePrime(var1, this.random);

         do {
            var5 = BigInteger.probablePrime(var2, this.random);
            if (var4.compareTo(var5) < 0) {
               var7 = var4;
               var4 = var5;
               var5 = var7;
            }

            var6 = var4.multiply(var5);
         } while(var6.bitLength() < this.keySize);

         var7 = var4.subtract(BigInteger.ONE);
         var8 = var5.subtract(BigInteger.ONE);
         var9 = var7.multiply(var8);
      } while(!var3.gcd(var9).equals(BigInteger.ONE));

      BigInteger var10 = var3.modInverse(var9);
      BigInteger var11 = var10.mod(var7);
      BigInteger var12 = var10.mod(var8);
      BigInteger var13 = var5.modInverse(var4);

      try {
         RSAPublicKeyImpl var14 = new RSAPublicKeyImpl(var6, var3);
         RSAPrivateCrtKeyImpl var15 = new RSAPrivateCrtKeyImpl(var6, var3, var10, var4, var5, var11, var12, var13);
         return new KeyPair(var14, var15);
      } catch (InvalidKeyException var16) {
         throw new RuntimeException(var16);
      }
   }
}
