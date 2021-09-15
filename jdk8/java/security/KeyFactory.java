package java.security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.util.Debug;

public class KeyFactory {
   private static final Debug debug = Debug.getInstance("jca", "KeyFactory");
   private final String algorithm;
   private Provider provider;
   private volatile KeyFactorySpi spi;
   private final Object lock = new Object();
   private Iterator<Provider.Service> serviceIterator;

   protected KeyFactory(KeyFactorySpi var1, Provider var2, String var3) {
      this.spi = var1;
      this.provider = var2;
      this.algorithm = var3;
   }

   private KeyFactory(String var1) throws NoSuchAlgorithmException {
      this.algorithm = var1;
      List var2 = GetInstance.getServices("KeyFactory", var1);
      this.serviceIterator = var2.iterator();
      if (this.nextSpi((KeyFactorySpi)null) == null) {
         throw new NoSuchAlgorithmException(var1 + " KeyFactory not available");
      }
   }

   public static KeyFactory getInstance(String var0) throws NoSuchAlgorithmException {
      return new KeyFactory(var0);
   }

   public static KeyFactory getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      GetInstance.Instance var2 = GetInstance.getInstance("KeyFactory", KeyFactorySpi.class, var0, var1);
      return new KeyFactory((KeyFactorySpi)var2.impl, var2.provider, var0);
   }

   public static KeyFactory getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      GetInstance.Instance var2 = GetInstance.getInstance("KeyFactory", KeyFactorySpi.class, var0, var1);
      return new KeyFactory((KeyFactorySpi)var2.impl, var2.provider, var0);
   }

   public final Provider getProvider() {
      synchronized(this.lock) {
         this.serviceIterator = null;
         return this.provider;
      }
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   private KeyFactorySpi nextSpi(KeyFactorySpi var1) {
      synchronized(this.lock) {
         if (var1 != null && var1 != this.spi) {
            return this.spi;
         } else if (this.serviceIterator == null) {
            return null;
         } else {
            while(true) {
               if (this.serviceIterator.hasNext()) {
                  Provider.Service var3 = (Provider.Service)this.serviceIterator.next();

                  KeyFactorySpi var10000;
                  try {
                     Object var4 = var3.newInstance((Object)null);
                     if (!(var4 instanceof KeyFactorySpi)) {
                        continue;
                     }

                     KeyFactorySpi var5 = (KeyFactorySpi)var4;
                     this.provider = var3.getProvider();
                     this.spi = var5;
                     var10000 = var5;
                  } catch (NoSuchAlgorithmException var7) {
                     continue;
                  }

                  return var10000;
               }

               this.serviceIterator = null;
               return null;
            }
         }
      }
   }

   public final PublicKey generatePublic(KeySpec var1) throws InvalidKeySpecException {
      if (this.serviceIterator == null) {
         return this.spi.engineGeneratePublic(var1);
      } else {
         Exception var2 = null;
         KeyFactorySpi var3 = this.spi;

         while(true) {
            try {
               return var3.engineGeneratePublic(var1);
            } catch (Exception var5) {
               if (var2 == null) {
                  var2 = var5;
               }

               var3 = this.nextSpi(var3);
               if (var3 == null) {
                  if (var2 instanceof RuntimeException) {
                     throw (RuntimeException)var2;
                  }

                  if (var2 instanceof InvalidKeySpecException) {
                     throw (InvalidKeySpecException)var2;
                  }

                  throw new InvalidKeySpecException("Could not generate public key", var2);
               }
            }
         }
      }
   }

   public final PrivateKey generatePrivate(KeySpec var1) throws InvalidKeySpecException {
      if (this.serviceIterator == null) {
         return this.spi.engineGeneratePrivate(var1);
      } else {
         Exception var2 = null;
         KeyFactorySpi var3 = this.spi;

         while(true) {
            try {
               return var3.engineGeneratePrivate(var1);
            } catch (Exception var5) {
               if (var2 == null) {
                  var2 = var5;
               }

               var3 = this.nextSpi(var3);
               if (var3 == null) {
                  if (var2 instanceof RuntimeException) {
                     throw (RuntimeException)var2;
                  }

                  if (var2 instanceof InvalidKeySpecException) {
                     throw (InvalidKeySpecException)var2;
                  }

                  throw new InvalidKeySpecException("Could not generate private key", var2);
               }
            }
         }
      }
   }

   public final <T extends KeySpec> T getKeySpec(Key var1, Class<T> var2) throws InvalidKeySpecException {
      if (this.serviceIterator == null) {
         return this.spi.engineGetKeySpec(var1, var2);
      } else {
         Exception var3 = null;
         KeyFactorySpi var4 = this.spi;

         while(true) {
            try {
               return var4.engineGetKeySpec(var1, var2);
            } catch (Exception var6) {
               if (var3 == null) {
                  var3 = var6;
               }

               var4 = this.nextSpi(var4);
               if (var4 == null) {
                  if (var3 instanceof RuntimeException) {
                     throw (RuntimeException)var3;
                  }

                  if (var3 instanceof InvalidKeySpecException) {
                     throw (InvalidKeySpecException)var3;
                  }

                  throw new InvalidKeySpecException("Could not get key spec", var3);
               }
            }
         }
      }
   }

   public final Key translateKey(Key var1) throws InvalidKeyException {
      if (this.serviceIterator == null) {
         return this.spi.engineTranslateKey(var1);
      } else {
         Exception var2 = null;
         KeyFactorySpi var3 = this.spi;

         while(true) {
            try {
               return var3.engineTranslateKey(var1);
            } catch (Exception var5) {
               if (var2 == null) {
                  var2 = var5;
               }

               var3 = this.nextSpi(var3);
               if (var3 == null) {
                  if (var2 instanceof RuntimeException) {
                     throw (RuntimeException)var2;
                  }

                  if (var2 instanceof InvalidKeyException) {
                     throw (InvalidKeyException)var2;
                  }

                  throw new InvalidKeyException("Could not translate key", var2);
               }
            }
         }
      }
   }
}
