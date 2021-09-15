package java.security;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public class AlgorithmParameters {
   private Provider provider;
   private AlgorithmParametersSpi paramSpi;
   private String algorithm;
   private boolean initialized = false;

   protected AlgorithmParameters(AlgorithmParametersSpi var1, Provider var2, String var3) {
      this.paramSpi = var1;
      this.provider = var2;
      this.algorithm = var3;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public static AlgorithmParameters getInstance(String var0) throws NoSuchAlgorithmException {
      try {
         Object[] var1 = Security.getImpl(var0, "AlgorithmParameters", (String)null);
         return new AlgorithmParameters((AlgorithmParametersSpi)var1[0], (Provider)var1[1], var0);
      } catch (NoSuchProviderException var2) {
         throw new NoSuchAlgorithmException(var0 + " not found");
      }
   }

   public static AlgorithmParameters getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var1 != null && var1.length() != 0) {
         Object[] var2 = Security.getImpl(var0, "AlgorithmParameters", var1);
         return new AlgorithmParameters((AlgorithmParametersSpi)var2[0], (Provider)var2[1], var0);
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static AlgorithmParameters getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         Object[] var2 = Security.getImpl(var0, "AlgorithmParameters", var1);
         return new AlgorithmParameters((AlgorithmParametersSpi)var2[0], (Provider)var2[1], var0);
      }
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final void init(AlgorithmParameterSpec var1) throws InvalidParameterSpecException {
      if (this.initialized) {
         throw new InvalidParameterSpecException("already initialized");
      } else {
         this.paramSpi.engineInit(var1);
         this.initialized = true;
      }
   }

   public final void init(byte[] var1) throws IOException {
      if (this.initialized) {
         throw new IOException("already initialized");
      } else {
         this.paramSpi.engineInit(var1);
         this.initialized = true;
      }
   }

   public final void init(byte[] var1, String var2) throws IOException {
      if (this.initialized) {
         throw new IOException("already initialized");
      } else {
         this.paramSpi.engineInit(var1, var2);
         this.initialized = true;
      }
   }

   public final <T extends AlgorithmParameterSpec> T getParameterSpec(Class<T> var1) throws InvalidParameterSpecException {
      if (!this.initialized) {
         throw new InvalidParameterSpecException("not initialized");
      } else {
         return this.paramSpi.engineGetParameterSpec(var1);
      }
   }

   public final byte[] getEncoded() throws IOException {
      if (!this.initialized) {
         throw new IOException("not initialized");
      } else {
         return this.paramSpi.engineGetEncoded();
      }
   }

   public final byte[] getEncoded(String var1) throws IOException {
      if (!this.initialized) {
         throw new IOException("not initialized");
      } else {
         return this.paramSpi.engineGetEncoded(var1);
      }
   }

   public final String toString() {
      return !this.initialized ? null : this.paramSpi.engineToString();
   }
}
