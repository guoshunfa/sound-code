package java.security;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;

public abstract class KeyPairGenerator extends KeyPairGeneratorSpi {
   private static final Debug pdebug = Debug.getInstance("provider", "Provider");
   private static final boolean skipDebug = Debug.isOn("engine=") && !Debug.isOn("keypairgenerator");
   private final String algorithm;
   Provider provider;

   protected KeyPairGenerator(String var1) {
      this.algorithm = var1;
   }

   public String getAlgorithm() {
      return this.algorithm;
   }

   private static KeyPairGenerator getInstance(GetInstance.Instance var0, String var1) {
      Object var2;
      if (var0.impl instanceof KeyPairGenerator) {
         var2 = (KeyPairGenerator)var0.impl;
      } else {
         KeyPairGeneratorSpi var3 = (KeyPairGeneratorSpi)var0.impl;
         var2 = new KeyPairGenerator.Delegate(var3, var1);
      }

      ((KeyPairGenerator)var2).provider = var0.provider;
      if (!skipDebug && pdebug != null) {
         pdebug.println("KeyPairGenerator." + var1 + " algorithm from: " + ((KeyPairGenerator)var2).provider.getName());
      }

      return (KeyPairGenerator)var2;
   }

   public static KeyPairGenerator getInstance(String var0) throws NoSuchAlgorithmException {
      List var1 = GetInstance.getServices("KeyPairGenerator", var0);
      Iterator var2 = var1.iterator();
      if (!var2.hasNext()) {
         throw new NoSuchAlgorithmException(var0 + " KeyPairGenerator not available");
      } else {
         NoSuchAlgorithmException var3 = null;

         while(true) {
            Provider.Service var4 = (Provider.Service)var2.next();

            try {
               GetInstance.Instance var5 = GetInstance.getInstance(var4, KeyPairGeneratorSpi.class);
               if (var5.impl instanceof KeyPairGenerator) {
                  return getInstance(var5, var0);
               }

               return new KeyPairGenerator.Delegate(var5, var2, var0);
            } catch (NoSuchAlgorithmException var6) {
               if (var3 == null) {
                  var3 = var6;
               }

               if (!var2.hasNext()) {
                  throw var3;
               }
            }
         }
      }
   }

   public static KeyPairGenerator getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      GetInstance.Instance var2 = GetInstance.getInstance("KeyPairGenerator", KeyPairGeneratorSpi.class, var0, var1);
      return getInstance(var2, var0);
   }

   public static KeyPairGenerator getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      GetInstance.Instance var2 = GetInstance.getInstance("KeyPairGenerator", KeyPairGeneratorSpi.class, var0, var1);
      return getInstance(var2, var0);
   }

   public final Provider getProvider() {
      this.disableFailover();
      return this.provider;
   }

   void disableFailover() {
   }

   public void initialize(int var1) {
      this.initialize(var1, JCAUtil.getSecureRandom());
   }

   public void initialize(int var1, SecureRandom var2) {
   }

   public void initialize(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      this.initialize(var1, JCAUtil.getSecureRandom());
   }

   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
   }

   public final KeyPair genKeyPair() {
      return this.generateKeyPair();
   }

   public KeyPair generateKeyPair() {
      return null;
   }

   private static final class Delegate extends KeyPairGenerator {
      private volatile KeyPairGeneratorSpi spi;
      private final Object lock = new Object();
      private Iterator<Provider.Service> serviceIterator;
      private static final int I_NONE = 1;
      private static final int I_SIZE = 2;
      private static final int I_PARAMS = 3;
      private int initType;
      private int initKeySize;
      private AlgorithmParameterSpec initParams;
      private SecureRandom initRandom;

      Delegate(KeyPairGeneratorSpi var1, String var2) {
         super(var2);
         this.spi = var1;
      }

      Delegate(GetInstance.Instance var1, Iterator<Provider.Service> var2, String var3) {
         super(var3);
         this.spi = (KeyPairGeneratorSpi)var1.impl;
         this.provider = var1.provider;
         this.serviceIterator = var2;
         this.initType = 1;
         if (!KeyPairGenerator.skipDebug && KeyPairGenerator.pdebug != null) {
            KeyPairGenerator.pdebug.println("KeyPairGenerator." + var3 + " algorithm from: " + this.provider.getName());
         }

      }

      private KeyPairGeneratorSpi nextSpi(KeyPairGeneratorSpi var1, boolean var2) {
         synchronized(this.lock) {
            if (var1 != null && var1 != this.spi) {
               return this.spi;
            } else if (this.serviceIterator == null) {
               return null;
            } else {
               while(true) {
                  if (this.serviceIterator.hasNext()) {
                     Provider.Service var4 = (Provider.Service)this.serviceIterator.next();

                     KeyPairGeneratorSpi var10000;
                     try {
                        Object var5 = var4.newInstance((Object)null);
                        if (!(var5 instanceof KeyPairGeneratorSpi) || var5 instanceof KeyPairGenerator) {
                           continue;
                        }

                        KeyPairGeneratorSpi var6 = (KeyPairGeneratorSpi)var5;
                        if (var2) {
                           if (this.initType == 2) {
                              var6.initialize(this.initKeySize, this.initRandom);
                           } else if (this.initType == 3) {
                              var6.initialize(this.initParams, this.initRandom);
                           } else if (this.initType != 1) {
                              throw new AssertionError("KeyPairGenerator initType: " + this.initType);
                           }
                        }

                        this.provider = var4.getProvider();
                        this.spi = var6;
                        var10000 = var6;
                     } catch (Exception var8) {
                        continue;
                     }

                     return var10000;
                  }

                  this.disableFailover();
                  return null;
               }
            }
         }
      }

      void disableFailover() {
         this.serviceIterator = null;
         this.initType = 0;
         this.initParams = null;
         this.initRandom = null;
      }

      public void initialize(int var1, SecureRandom var2) {
         if (this.serviceIterator == null) {
            this.spi.initialize(var1, var2);
         } else {
            RuntimeException var3 = null;
            KeyPairGeneratorSpi var4 = this.spi;

            while(true) {
               try {
                  var4.initialize(var1, var2);
                  this.initType = 2;
                  this.initKeySize = var1;
                  this.initParams = null;
                  this.initRandom = var2;
                  return;
               } catch (RuntimeException var6) {
                  if (var3 == null) {
                     var3 = var6;
                  }

                  var4 = this.nextSpi(var4, false);
                  if (var4 == null) {
                     throw var3;
                  }
               }
            }
         }
      }

      public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
         if (this.serviceIterator == null) {
            this.spi.initialize(var1, var2);
         } else {
            Exception var3 = null;
            KeyPairGeneratorSpi var4 = this.spi;

            while(true) {
               try {
                  var4.initialize(var1, var2);
                  this.initType = 3;
                  this.initKeySize = 0;
                  this.initParams = var1;
                  this.initRandom = var2;
                  return;
               } catch (Exception var6) {
                  if (var3 == null) {
                     var3 = var6;
                  }

                  var4 = this.nextSpi(var4, false);
                  if (var4 == null) {
                     if (var3 instanceof RuntimeException) {
                        throw (RuntimeException)var3;
                     }

                     throw (InvalidAlgorithmParameterException)var3;
                  }
               }
            }
         }
      }

      public KeyPair generateKeyPair() {
         if (this.serviceIterator == null) {
            return this.spi.generateKeyPair();
         } else {
            RuntimeException var1 = null;
            KeyPairGeneratorSpi var2 = this.spi;

            while(true) {
               try {
                  return var2.generateKeyPair();
               } catch (RuntimeException var4) {
                  if (var1 == null) {
                     var1 = var4;
                  }

                  var2 = this.nextSpi(var2, true);
                  if (var2 == null) {
                     throw var1;
                  }
               }
            }
         }
      }
   }
}
