package sun.security.provider;

import java.security.AccessController;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import sun.security.action.GetPropertyAction;

public class DSAKeyFactory extends KeyFactorySpi {
   static final boolean SERIAL_INTEROP;
   private static final String SERIAL_PROP = "sun.security.key.serial.interop";

   protected PublicKey engineGeneratePublic(KeySpec var1) throws InvalidKeySpecException {
      try {
         if (var1 instanceof DSAPublicKeySpec) {
            DSAPublicKeySpec var2 = (DSAPublicKeySpec)var1;
            return (PublicKey)(SERIAL_INTEROP ? new DSAPublicKey(var2.getY(), var2.getP(), var2.getQ(), var2.getG()) : new DSAPublicKeyImpl(var2.getY(), var2.getP(), var2.getQ(), var2.getG()));
         } else if (var1 instanceof X509EncodedKeySpec) {
            return (PublicKey)(SERIAL_INTEROP ? new DSAPublicKey(((X509EncodedKeySpec)var1).getEncoded()) : new DSAPublicKeyImpl(((X509EncodedKeySpec)var1).getEncoded()));
         } else {
            throw new InvalidKeySpecException("Inappropriate key specification");
         }
      } catch (InvalidKeyException var3) {
         throw new InvalidKeySpecException("Inappropriate key specification: " + var3.getMessage());
      }
   }

   protected PrivateKey engineGeneratePrivate(KeySpec var1) throws InvalidKeySpecException {
      try {
         if (var1 instanceof DSAPrivateKeySpec) {
            DSAPrivateKeySpec var2 = (DSAPrivateKeySpec)var1;
            return new DSAPrivateKey(var2.getX(), var2.getP(), var2.getQ(), var2.getG());
         } else if (var1 instanceof PKCS8EncodedKeySpec) {
            return new DSAPrivateKey(((PKCS8EncodedKeySpec)var1).getEncoded());
         } else {
            throw new InvalidKeySpecException("Inappropriate key specification");
         }
      } catch (InvalidKeyException var3) {
         throw new InvalidKeySpecException("Inappropriate key specification: " + var3.getMessage());
      }
   }

   protected <T extends KeySpec> T engineGetKeySpec(Key var1, Class<T> var2) throws InvalidKeySpecException {
      try {
         DSAParams var3;
         Class var4;
         Class var5;
         if (var1 instanceof java.security.interfaces.DSAPublicKey) {
            var4 = Class.forName("java.security.spec.DSAPublicKeySpec");
            var5 = Class.forName("java.security.spec.X509EncodedKeySpec");
            if (var4.isAssignableFrom(var2)) {
               java.security.interfaces.DSAPublicKey var8 = (java.security.interfaces.DSAPublicKey)var1;
               var3 = var8.getParams();
               return (KeySpec)var2.cast(new DSAPublicKeySpec(var8.getY(), var3.getP(), var3.getQ(), var3.getG()));
            } else if (var5.isAssignableFrom(var2)) {
               return (KeySpec)var2.cast(new X509EncodedKeySpec(var1.getEncoded()));
            } else {
               throw new InvalidKeySpecException("Inappropriate key specification");
            }
         } else if (var1 instanceof java.security.interfaces.DSAPrivateKey) {
            var4 = Class.forName("java.security.spec.DSAPrivateKeySpec");
            var5 = Class.forName("java.security.spec.PKCS8EncodedKeySpec");
            if (var4.isAssignableFrom(var2)) {
               java.security.interfaces.DSAPrivateKey var6 = (java.security.interfaces.DSAPrivateKey)var1;
               var3 = var6.getParams();
               return (KeySpec)var2.cast(new DSAPrivateKeySpec(var6.getX(), var3.getP(), var3.getQ(), var3.getG()));
            } else if (var5.isAssignableFrom(var2)) {
               return (KeySpec)var2.cast(new PKCS8EncodedKeySpec(var1.getEncoded()));
            } else {
               throw new InvalidKeySpecException("Inappropriate key specification");
            }
         } else {
            throw new InvalidKeySpecException("Inappropriate key type");
         }
      } catch (ClassNotFoundException var7) {
         throw new InvalidKeySpecException("Unsupported key specification: " + var7.getMessage());
      }
   }

   protected Key engineTranslateKey(Key var1) throws InvalidKeyException {
      try {
         if (var1 instanceof java.security.interfaces.DSAPublicKey) {
            if (var1 instanceof DSAPublicKey) {
               return var1;
            } else {
               DSAPublicKeySpec var4 = (DSAPublicKeySpec)this.engineGetKeySpec(var1, DSAPublicKeySpec.class);
               return this.engineGeneratePublic(var4);
            }
         } else if (var1 instanceof java.security.interfaces.DSAPrivateKey) {
            if (var1 instanceof DSAPrivateKey) {
               return var1;
            } else {
               DSAPrivateKeySpec var2 = (DSAPrivateKeySpec)this.engineGetKeySpec(var1, DSAPrivateKeySpec.class);
               return this.engineGeneratePrivate(var2);
            }
         } else {
            throw new InvalidKeyException("Wrong algorithm type");
         }
      } catch (InvalidKeySpecException var3) {
         throw new InvalidKeyException("Cannot translate key: " + var3.getMessage());
      }
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.security.key.serial.interop", (String)null)));
      SERIAL_INTEROP = "true".equalsIgnoreCase(var0);
   }
}
