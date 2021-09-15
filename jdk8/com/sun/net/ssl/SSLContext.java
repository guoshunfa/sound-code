package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/** @deprecated */
@Deprecated
public class SSLContext {
   private Provider provider;
   private SSLContextSpi contextSpi;
   private String protocol;

   protected SSLContext(SSLContextSpi var1, Provider var2, String var3) {
      this.contextSpi = var1;
      this.provider = var2;
      this.protocol = var3;
   }

   public static SSLContext getInstance(String var0) throws NoSuchAlgorithmException {
      try {
         Object[] var1 = SSLSecurity.getImpl(var0, "SSLContext", (String)null);
         return new SSLContext((SSLContextSpi)var1[0], (Provider)var1[1], var0);
      } catch (NoSuchProviderException var2) {
         throw new NoSuchAlgorithmException(var0 + " not found");
      }
   }

   public static SSLContext getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var1 != null && var1.length() != 0) {
         Object[] var2 = SSLSecurity.getImpl(var0, "SSLContext", var1);
         return new SSLContext((SSLContextSpi)var2[0], (Provider)var2[1], var0);
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static SSLContext getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      if (var1 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         Object[] var2 = SSLSecurity.getImpl(var0, "SSLContext", var1);
         return new SSLContext((SSLContextSpi)var2[0], (Provider)var2[1], var0);
      }
   }

   public final String getProtocol() {
      return this.protocol;
   }

   public final Provider getProvider() {
      return this.provider;
   }

   public final void init(KeyManager[] var1, TrustManager[] var2, SecureRandom var3) throws KeyManagementException {
      this.contextSpi.engineInit(var1, var2, var3);
   }

   public final SSLSocketFactory getSocketFactory() {
      return this.contextSpi.engineGetSocketFactory();
   }

   public final SSLServerSocketFactory getServerSocketFactory() {
      return this.contextSpi.engineGetServerSocketFactory();
   }
}
