package com.sun.net.ssl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

final class SSLContextSpiWrapper extends SSLContextSpi {
   private javax.net.ssl.SSLContext theSSLContext;

   SSLContextSpiWrapper(String var1, Provider var2) throws NoSuchAlgorithmException {
      this.theSSLContext = javax.net.ssl.SSLContext.getInstance(var1, var2);
   }

   protected void engineInit(KeyManager[] var1, TrustManager[] var2, SecureRandom var3) throws KeyManagementException {
      int var4;
      int var5;
      javax.net.ssl.KeyManager[] var6;
      if (var1 != null) {
         var6 = new javax.net.ssl.KeyManager[var1.length];
         var5 = 0;

         for(var4 = 0; var5 < var1.length; ++var5) {
            if (!(var1[var5] instanceof javax.net.ssl.KeyManager)) {
               if (var1[var5] instanceof X509KeyManager) {
                  var6[var4] = new X509KeyManagerJavaxWrapper((X509KeyManager)var1[var5]);
                  ++var4;
               }
            } else {
               var6[var4] = (javax.net.ssl.KeyManager)var1[var5];
               ++var4;
            }
         }

         if (var4 != var5) {
            var6 = (javax.net.ssl.KeyManager[])((javax.net.ssl.KeyManager[])SSLSecurity.truncateArray(var6, new javax.net.ssl.KeyManager[var4]));
         }
      } else {
         var6 = null;
      }

      javax.net.ssl.TrustManager[] var7;
      if (var2 != null) {
         var7 = new javax.net.ssl.TrustManager[var2.length];
         var5 = 0;

         for(var4 = 0; var5 < var2.length; ++var5) {
            if (!(var2[var5] instanceof javax.net.ssl.TrustManager)) {
               if (var2[var5] instanceof X509TrustManager) {
                  var7[var4] = new X509TrustManagerJavaxWrapper((X509TrustManager)var2[var5]);
                  ++var4;
               }
            } else {
               var7[var4] = (javax.net.ssl.TrustManager)var2[var5];
               ++var4;
            }
         }

         if (var4 != var5) {
            var7 = (javax.net.ssl.TrustManager[])((javax.net.ssl.TrustManager[])SSLSecurity.truncateArray(var7, new javax.net.ssl.TrustManager[var4]));
         }
      } else {
         var7 = null;
      }

      this.theSSLContext.init(var6, var7, var3);
   }

   protected SSLSocketFactory engineGetSocketFactory() {
      return this.theSSLContext.getSocketFactory();
   }

   protected SSLServerSocketFactory engineGetServerSocketFactory() {
      return this.theSSLContext.getServerSocketFactory();
   }
}
