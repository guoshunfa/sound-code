package sun.security.provider;

import java.math.BigInteger;
import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.ProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAGenParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.SecurityProviderConstants;

public class DSAParameterGenerator extends AlgorithmParameterGeneratorSpi {
   private int valueL = -1;
   private int valueN = -1;
   private int seedLen = -1;
   private java.security.SecureRandom random;
   private static final BigInteger TWO = BigInteger.valueOf(2L);

   protected void engineInit(int var1, java.security.SecureRandom var2) {
      if (var1 == 2048 || var1 == 3072 || var1 >= 512 && var1 <= 1024 && var1 % 64 == 0) {
         this.valueL = var1;
         this.valueN = SecurityProviderConstants.getDefDSASubprimeSize(var1);
         this.seedLen = this.valueN;
         this.random = var2;
      } else {
         throw new InvalidParameterException("Unexpected strength (size of prime): " + var1 + ". Prime size should be 512-1024, 2048, or 3072");
      }
   }

   protected void engineInit(AlgorithmParameterSpec var1, java.security.SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof DSAGenParameterSpec)) {
         throw new InvalidAlgorithmParameterException("Invalid parameter");
      } else {
         DSAGenParameterSpec var3 = (DSAGenParameterSpec)var1;
         this.valueL = var3.getPrimePLength();
         this.valueN = var3.getSubprimeQLength();
         this.seedLen = var3.getSeedLength();
         this.random = var2;
      }
   }

   protected AlgorithmParameters engineGenerateParameters() {
      AlgorithmParameters var1 = null;

      try {
         if (this.random == null) {
            this.random = new java.security.SecureRandom();
         }

         if (this.valueL == -1) {
            this.engineInit(SecurityProviderConstants.DEF_DSA_KEY_SIZE, this.random);
         }

         BigInteger[] var2 = generatePandQ(this.random, this.valueL, this.valueN, this.seedLen);
         BigInteger var3 = var2[0];
         BigInteger var4 = var2[1];
         BigInteger var5 = generateG(var3, var4);
         DSAParameterSpec var6 = new DSAParameterSpec(var3, var4, var5);
         var1 = AlgorithmParameters.getInstance("DSA", "SUN");
         var1.init((AlgorithmParameterSpec)var6);
         return var1;
      } catch (InvalidParameterSpecException var7) {
         throw new RuntimeException(var7.getMessage());
      } catch (NoSuchAlgorithmException var8) {
         throw new RuntimeException(var8.getMessage());
      } catch (NoSuchProviderException var9) {
         throw new RuntimeException(var9.getMessage());
      }
   }

   private static BigInteger[] generatePandQ(java.security.SecureRandom var0, int var1, int var2, int var3) {
      String var4 = null;
      if (var2 == 160) {
         var4 = "SHA";
      } else if (var2 == 224) {
         var4 = "SHA-224";
      } else if (var2 == 256) {
         var4 = "SHA-256";
      }

      MessageDigest var5 = null;

      try {
         var5 = MessageDigest.getInstance(var4);
      } catch (NoSuchAlgorithmException var23) {
         var23.printStackTrace();
      }

      int var6 = var5.getDigestLength() * 8;
      int var7 = (var1 - 1) / var6;
      int var8 = (var1 - 1) % var6;
      byte[] var9 = new byte[var3 / 8];
      BigInteger var10 = TWO.pow(var3);
      short var11 = -1;
      if (var1 <= 1024) {
         var11 = 80;
      } else if (var1 == 2048) {
         var11 = 112;
      } else if (var1 == 3072) {
         var11 = 128;
      }

      if (var11 < 0) {
         throw new ProviderException("Invalid valueL: " + var1);
      } else {
         BigInteger var14 = null;

         while(true) {
            BigInteger var13;
            BigInteger var16;
            do {
               var0.nextBytes(var9);
               var14 = new BigInteger(1, var9);
               var16 = (new BigInteger(1, var5.digest(var9))).mod(TWO.pow(var2 - 1));
               var13 = TWO.pow(var2 - 1).add(var16).add(BigInteger.ONE).subtract(var16.mod(TWO));
            } while(!var13.isProbablePrime(var11));

            var16 = BigInteger.ONE;

            for(int var15 = 0; var15 < 4 * var1; ++var15) {
               BigInteger[] var17 = new BigInteger[var7 + 1];

               BigInteger var19;
               BigInteger var20;
               for(int var18 = 0; var18 <= var7; ++var18) {
                  var19 = BigInteger.valueOf((long)var18);
                  var20 = var14.add(var16).add(var19).mod(var10);
                  byte[] var21 = var5.digest(toByteArray(var20));
                  var17[var18] = new BigInteger(1, var21);
               }

               BigInteger var24 = var17[0];

               for(int var25 = 1; var25 < var7; ++var25) {
                  var24 = var24.add(var17[var25].multiply(TWO.pow(var25 * var6)));
               }

               var24 = var24.add(var17[var7].mod(TWO.pow(var8)).multiply(TWO.pow(var7 * var6)));
               var19 = TWO.pow(var1 - 1);
               var20 = var24.add(var19);
               BigInteger var26 = var20.mod(var13.multiply(TWO));
               BigInteger var12 = var20.subtract(var26.subtract(BigInteger.ONE));
               if (var12.compareTo(var19) > -1 && var12.isProbablePrime(var11)) {
                  BigInteger[] var22 = new BigInteger[]{var12, var13, var14, BigInteger.valueOf((long)var15)};
                  return var22;
               }

               var16 = var16.add(BigInteger.valueOf((long)var7)).add(BigInteger.ONE);
            }
         }
      }
   }

   private static BigInteger generateG(BigInteger var0, BigInteger var1) {
      BigInteger var2 = BigInteger.ONE;
      BigInteger var3 = var0.subtract(BigInteger.ONE).divide(var1);

      BigInteger var4;
      for(var4 = BigInteger.ONE; var4.compareTo(TWO) < 0; var2 = var2.add(BigInteger.ONE)) {
         var4 = var2.modPow(var3, var0);
      }

      return var4;
   }

   private static byte[] toByteArray(BigInteger var0) {
      byte[] var1 = var0.toByteArray();
      if (var1[0] == 0) {
         byte[] var2 = new byte[var1.length - 1];
         System.arraycopy(var1, 1, var2, 0, var2.length);
         var1 = var2;
      }

      return var1;
   }
}
