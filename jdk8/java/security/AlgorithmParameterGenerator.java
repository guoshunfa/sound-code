package java.security;

import java.security.spec.AlgorithmParameterSpec;

public class AlgorithmParameterGenerator {
   private Provider provider;
   private AlgorithmParameterGeneratorSpi paramGenSpi;
   private String algorithm;

   protected AlgorithmParameterGenerator(AlgorithmParameterGeneratorSpi var1, Provider var2, String var3) {
      this.paramGenSpi = var1;
      this.provider = var2;
      this.algorithm = var3;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public static AlgorithmParameterGenerator getInstance(String var0) throws NoSuchAlgorithmException {
      try {
         Object[] var1 = Security.getImpl(var0, "AlgorithmParameterGenerator", (String)null);
         return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)var1[0], (Provider)var1[1], var0);
      } catch (NoSuchProviderException var2) {
         throw new NoSuchAlgorithmException(var0 + " not found");
      }
   }

   public static AlgorithmParameterGenerator getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var1 != null && var1.length() != 0) {
         Object[] var2 = Security.getImpl(var0, "AlgorithmParameterGenerator", var1);
         return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)var2[0], (Provider)var2[1], var0);
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static AlgorithmParameterGenerator getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         Object[] var2 = Security.getImpl(var0, "AlgorithmParameterGenerator", var1);
         return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)var2[0], (Provider)var2[1], var0);
      }
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final void init(int var1) {
      this.paramGenSpi.engineInit(var1, new SecureRandom());
   }

   public final void init(int var1, SecureRandom var2) {
      this.paramGenSpi.engineInit(var1, var2);
   }

   public final void init(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      this.paramGenSpi.engineInit(var1, new SecureRandom());
   }

   public final void init(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      this.paramGenSpi.engineInit(var1, var2);
   }

   public final AlgorithmParameters generateParameters() {
      return this.paramGenSpi.engineGenerateParameters();
   }
}
