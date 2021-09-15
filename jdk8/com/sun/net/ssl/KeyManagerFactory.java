package com.sun.net.ssl;

import java.security.AccessController;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.security.UnrecoverableKeyException;

/** @deprecated */
@Deprecated
public class KeyManagerFactory {
   private Provider provider;
   private KeyManagerFactorySpi factorySpi;
   private String algorithm;

   public static final String getDefaultAlgorithm() {
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return Security.getProperty("sun.ssl.keymanager.type");
         }
      });
      if (var0 == null) {
         var0 = "SunX509";
      }

      return var0;
   }

   protected KeyManagerFactory(KeyManagerFactorySpi var1, Provider var2, String var3) {
      this.factorySpi = var1;
      this.provider = var2;
      this.algorithm = var3;
   }

   public final String getAlgorithm() {
      return this.algorithm;
   }

   public static final KeyManagerFactory getInstance(String var0) throws NoSuchAlgorithmException {
      try {
         Object[] var1 = SSLSecurity.getImpl(var0, "KeyManagerFactory", (String)null);
         return new KeyManagerFactory((KeyManagerFactorySpi)var1[0], (Provider)var1[1], var0);
      } catch (NoSuchProviderException var2) {
         throw new NoSuchAlgorithmException(var0 + " not found");
      }
   }

   public static final KeyManagerFactory getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var1 != null && var1.length() != 0) {
         Object[] var2 = SSLSecurity.getImpl(var0, "KeyManagerFactory", var1);
         return new KeyManagerFactory((KeyManagerFactorySpi)var2[0], (Provider)var2[1], var0);
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static final KeyManagerFactory getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         Object[] var2 = SSLSecurity.getImpl(var0, "KeyManagerFactory", var1);
         return new KeyManagerFactory((KeyManagerFactorySpi)var2[0], (Provider)var2[1], var0);
      }
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public void init(KeyStore var1, char[] var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      this.factorySpi.engineInit(var1, var2);
   }

   public KeyManager[] getKeyManagers() {
      return this.factorySpi.engineGetKeyManagers();
   }
}
