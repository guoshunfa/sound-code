package sun.security.rsa;

import java.math.BigInteger;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import sun.security.action.GetPropertyAction;

public final class RSAKeyFactory extends KeyFactorySpi {
   private static final Class<?> rsaPublicKeySpecClass = RSAPublicKeySpec.class;
   private static final Class<?> rsaPrivateKeySpecClass = RSAPrivateKeySpec.class;
   private static final Class<?> rsaPrivateCrtKeySpecClass = RSAPrivateCrtKeySpec.class;
   private static final Class<?> x509KeySpecClass = X509EncodedKeySpec.class;
   private static final Class<?> pkcs8KeySpecClass = PKCS8EncodedKeySpec.class;
   public static final int MIN_MODLEN = 512;
   public static final int MAX_MODLEN = 16384;
   public static final int MAX_MODLEN_RESTRICT_EXP = 3072;
   public static final int MAX_RESTRICTED_EXPLEN = 64;
   private static final boolean restrictExpLen = "true".equalsIgnoreCase((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.rsa.restrictRSAExponent", "true"))));
   private static final RSAKeyFactory INSTANCE = new RSAKeyFactory();

   public static RSAKey toRSAKey(Key var0) throws InvalidKeyException {
      return !(var0 instanceof RSAPrivateKeyImpl) && !(var0 instanceof RSAPrivateCrtKeyImpl) && !(var0 instanceof RSAPublicKeyImpl) ? (RSAKey)INSTANCE.engineTranslateKey(var0) : (RSAKey)var0;
   }

   static void checkRSAProviderKeyLengths(int var0, BigInteger var1) throws InvalidKeyException {
      checkKeyLengths(var0 + 7 & -8, var1, 512, Integer.MAX_VALUE);
   }

   public static void checkKeyLengths(int var0, BigInteger var1, int var2, int var3) throws InvalidKeyException {
      if (var2 > 0 && var0 < var2) {
         throw new InvalidKeyException("RSA keys must be at least " + var2 + " bits long");
      } else {
         int var4 = Math.min(var3, 16384);
         if (var0 > var4) {
            throw new InvalidKeyException("RSA keys must be no longer than " + var4 + " bits");
         } else if (restrictExpLen && var1 != null && var0 > 3072 && var1.bitLength() > 64) {
            throw new InvalidKeyException("RSA exponents can be no longer than 64 bits  if modulus is greater than 3072 bits");
         }
      }
   }

   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      if (var1 == null) {
         throw new InvalidKeyException("Key must not be null");
      } else {
         String var2 = var1.getAlgorithm();
         if (!var2.equals("RSA")) {
            throw new InvalidKeyException("Not an RSA key: " + var2);
         } else if (var1 instanceof PublicKey) {
            return this.translatePublicKey((PublicKey)var1);
         } else if (var1 instanceof PrivateKey) {
            return this.translatePrivateKey((PrivateKey)var1);
         } else {
            throw new InvalidKeyException("Neither a public nor a private key");
         }
      }
   }

   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      try {
         return this.generatePublic(var1);
      } catch (InvalidKeySpecException var3) {
         throw var3;
      } catch (GeneralSecurityException var4) {
         throw new InvalidKeySpecException(var4);
      }
   }

   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      try {
         return this.generatePrivate(var1);
      } catch (InvalidKeySpecException var3) {
         throw var3;
      } catch (GeneralSecurityException var4) {
         throw new InvalidKeySpecException(var4);
      }
   }

   private PublicKey translatePublicKey(PublicKey var1) throws InvalidKeyException {
      if (var1 instanceof RSAPublicKey) {
         if (var1 instanceof RSAPublicKeyImpl) {
            return var1;
         } else {
            RSAPublicKey var5 = (RSAPublicKey)var1;

            try {
               return new RSAPublicKeyImpl(var5.getModulus(), var5.getPublicExponent());
            } catch (RuntimeException var4) {
               throw new InvalidKeyException("Invalid key", var4);
            }
         }
      } else if ("X.509".equals(var1.getFormat())) {
         byte[] var2 = var1.getEncoded();
         return new RSAPublicKeyImpl(var2);
      } else {
         throw new InvalidKeyException("Public keys must be instance of RSAPublicKey or have X.509 encoding");
      }
   }

   private PrivateKey translatePrivateKey(PrivateKey var1) throws InvalidKeyException {
      if (var1 instanceof RSAPrivateCrtKey) {
         if (var1 instanceof RSAPrivateCrtKeyImpl) {
            return var1;
         } else {
            RSAPrivateCrtKey var7 = (RSAPrivateCrtKey)var1;

            try {
               return new RSAPrivateCrtKeyImpl(var7.getModulus(), var7.getPublicExponent(), var7.getPrivateExponent(), var7.getPrimeP(), var7.getPrimeQ(), var7.getPrimeExponentP(), var7.getPrimeExponentQ(), var7.getCrtCoefficient());
            } catch (RuntimeException var4) {
               throw new InvalidKeyException("Invalid key", var4);
            }
         }
      } else if (var1 instanceof RSAPrivateKey) {
         if (var1 instanceof RSAPrivateKeyImpl) {
            return var1;
         } else {
            RSAPrivateKey var6 = (RSAPrivateKey)var1;

            try {
               return new RSAPrivateKeyImpl(var6.getModulus(), var6.getPrivateExponent());
            } catch (RuntimeException var5) {
               throw new InvalidKeyException("Invalid key", var5);
            }
         }
      } else if ("PKCS#8".equals(var1.getFormat())) {
         byte[] var2 = var1.getEncoded();
         return RSAPrivateCrtKeyImpl.newKey(var2);
      } else {
         throw new InvalidKeyException("Private keys must be instance of RSAPrivate(Crt)Key or have PKCS#8 encoding");
      }
   }

   private PublicKey generatePublic(KeySpec var1) throws GeneralSecurityException {
      if (var1 instanceof X509EncodedKeySpec) {
         X509EncodedKeySpec var3 = (X509EncodedKeySpec)var1;
         return new RSAPublicKeyImpl(var3.getEncoded());
      } else if (var1 instanceof RSAPublicKeySpec) {
         RSAPublicKeySpec var2 = (RSAPublicKeySpec)var1;
         return new RSAPublicKeyImpl(var2.getModulus(), var2.getPublicExponent());
      } else {
         throw new InvalidKeySpecException("Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys");
      }
   }

   private PrivateKey generatePrivate(KeySpec var1) throws GeneralSecurityException {
      if (var1 instanceof PKCS8EncodedKeySpec) {
         PKCS8EncodedKeySpec var4 = (PKCS8EncodedKeySpec)var1;
         return RSAPrivateCrtKeyImpl.newKey(var4.getEncoded());
      } else if (var1 instanceof RSAPrivateCrtKeySpec) {
         RSAPrivateCrtKeySpec var3 = (RSAPrivateCrtKeySpec)var1;
         return new RSAPrivateCrtKeyImpl(var3.getModulus(), var3.getPublicExponent(), var3.getPrivateExponent(), var3.getPrimeP(), var3.getPrimeQ(), var3.getPrimeExponentP(), var3.getPrimeExponentQ(), var3.getCrtCoefficient());
      } else if (var1 instanceof RSAPrivateKeySpec) {
         RSAPrivateKeySpec var2 = (RSAPrivateKeySpec)var1;
         return new RSAPrivateKeyImpl(var2.getModulus(), var2.getPrivateExponent());
      } else {
         throw new InvalidKeySpecException("Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys");
      }
   }

   protected <T extends KeySpec> T engineGetKeySpec(Key var1, Class<T> var2) throws InvalidKeySpecException {
      try {
         var1 = this.engineTranslateKey(var1);
      } catch (InvalidKeyException var4) {
         throw new InvalidKeySpecException(var4);
      }

      if (var1 instanceof RSAPublicKey) {
         RSAPublicKey var6 = (RSAPublicKey)var1;
         if (rsaPublicKeySpecClass.isAssignableFrom(var2)) {
            return (KeySpec)var2.cast(new RSAPublicKeySpec(var6.getModulus(), var6.getPublicExponent()));
         } else if (x509KeySpecClass.isAssignableFrom(var2)) {
            return (KeySpec)var2.cast(new X509EncodedKeySpec(var1.getEncoded()));
         } else {
            throw new InvalidKeySpecException("KeySpec must be RSAPublicKeySpec or X509EncodedKeySpec for RSA public keys");
         }
      } else if (var1 instanceof RSAPrivateKey) {
         if (pkcs8KeySpecClass.isAssignableFrom(var2)) {
            return (KeySpec)var2.cast(new PKCS8EncodedKeySpec(var1.getEncoded()));
         } else if (rsaPrivateCrtKeySpecClass.isAssignableFrom(var2)) {
            if (var1 instanceof RSAPrivateCrtKey) {
               RSAPrivateCrtKey var5 = (RSAPrivateCrtKey)var1;
               return (KeySpec)var2.cast(new RSAPrivateCrtKeySpec(var5.getModulus(), var5.getPublicExponent(), var5.getPrivateExponent(), var5.getPrimeP(), var5.getPrimeQ(), var5.getPrimeExponentP(), var5.getPrimeExponentQ(), var5.getCrtCoefficient()));
            } else {
               throw new InvalidKeySpecException("RSAPrivateCrtKeySpec can only be used with CRT keys");
            }
         } else if (rsaPrivateKeySpecClass.isAssignableFrom(var2)) {
            RSAPrivateKey var3 = (RSAPrivateKey)var1;
            return (KeySpec)var2.cast(new RSAPrivateKeySpec(var3.getModulus(), var3.getPrivateExponent()));
         } else {
            throw new InvalidKeySpecException("KeySpec must be RSAPrivate(Crt)KeySpec or PKCS8EncodedKeySpec for RSA private keys");
         }
      } else {
         throw new InvalidKeySpecException("Neither public nor private key");
      }
   }
}
