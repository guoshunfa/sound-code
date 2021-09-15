package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

final class TrustManagerFactorySpiWrapper extends TrustManagerFactorySpi {
   private javax.net.ssl.TrustManagerFactory theTrustManagerFactory;

   TrustManagerFactorySpiWrapper(String var1, Provider var2) throws NoSuchAlgorithmException {
      this.theTrustManagerFactory = javax.net.ssl.TrustManagerFactory.getInstance(var1, var2);
   }

   protected void engineInit(KeyStore var1) throws KeyStoreException {
      this.theTrustManagerFactory.init(var1);
   }

   protected TrustManager[] engineGetTrustManagers() {
      javax.net.ssl.TrustManager[] var3 = this.theTrustManagerFactory.getTrustManagers();
      TrustManager[] var4 = new TrustManager[var3.length];
      int var2 = 0;

      int var1;
      for(var1 = 0; var2 < var3.length; ++var2) {
         if (!(var3[var2] instanceof TrustManager)) {
            if (var3[var2] instanceof javax.net.ssl.X509TrustManager) {
               var4[var1] = new X509TrustManagerComSunWrapper((javax.net.ssl.X509TrustManager)var3[var2]);
               ++var1;
            }
         } else {
            var4[var1] = (TrustManager)var3[var2];
            ++var1;
         }
      }

      if (var1 != var2) {
         var4 = (TrustManager[])((TrustManager[])SSLSecurity.truncateArray(var4, new TrustManager[var1]));
      }

      return var4;
   }
}
