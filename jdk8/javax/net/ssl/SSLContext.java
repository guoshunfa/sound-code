package javax.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import sun.security.jca.GetInstance;

public class SSLContext {
   private final Provider provider;
   private final SSLContextSpi contextSpi;
   private final String protocol;
   private static SSLContext defaultContext;

   protected SSLContext(SSLContextSpi var1, Provider var2, String var3) {
      this.contextSpi = var1;
      this.provider = var2;
      this.protocol = var3;
   }

   public static synchronized SSLContext getDefault() throws NoSuchAlgorithmException {
      if (defaultContext == null) {
         defaultContext = getInstance("Default");
      }

      return defaultContext;
   }

   public static synchronized void setDefault(SSLContext var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(new SSLPermission("setDefaultSSLContext"));
         }

         defaultContext = var0;
      }
   }

   public static SSLContext getInstance(String var0) throws NoSuchAlgorithmException {
      GetInstance.Instance var1 = GetInstance.getInstance("SSLContext", SSLContextSpi.class, var0);
      return new SSLContext((SSLContextSpi)var1.impl, var1.provider, var0);
   }

   public static SSLContext getInstance(String var0, String var1) throws NoSuchAlgorithmException, NoSuchProviderException {
      GetInstance.Instance var2 = GetInstance.getInstance("SSLContext", SSLContextSpi.class, var0, var1);
      return new SSLContext((SSLContextSpi)var2.impl, var2.provider, var0);
   }

   public static SSLContext getInstance(String var0, Provider var1) throws NoSuchAlgorithmException {
      GetInstance.Instance var2 = GetInstance.getInstance("SSLContext", SSLContextSpi.class, var0, var1);
      return new SSLContext((SSLContextSpi)var2.impl, var2.provider, var0);
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

   public final SSLEngine createSSLEngine() {
      try {
         return this.contextSpi.engineCreateSSLEngine();
      } catch (AbstractMethodError var3) {
         UnsupportedOperationException var2 = new UnsupportedOperationException("Provider: " + this.getProvider() + " doesn't support this operation");
         var2.initCause(var3);
         throw var2;
      }
   }

   public final SSLEngine createSSLEngine(String var1, int var2) {
      try {
         return this.contextSpi.engineCreateSSLEngine(var1, var2);
      } catch (AbstractMethodError var5) {
         UnsupportedOperationException var4 = new UnsupportedOperationException("Provider: " + this.getProvider() + " does not support this operation");
         var4.initCause(var5);
         throw var4;
      }
   }

   public final SSLSessionContext getServerSessionContext() {
      return this.contextSpi.engineGetServerSessionContext();
   }

   public final SSLSessionContext getClientSessionContext() {
      return this.contextSpi.engineGetClientSessionContext();
   }

   public final SSLParameters getDefaultSSLParameters() {
      return this.contextSpi.engineGetDefaultSSLParameters();
   }

   public final SSLParameters getSupportedSSLParameters() {
      return this.contextSpi.engineGetSupportedSSLParameters();
   }
}
