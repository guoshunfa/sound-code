package com.sun.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;

final class KeyManagerFactorySpiWrapper extends KeyManagerFactorySpi {
   private javax.net.ssl.KeyManagerFactory theKeyManagerFactory;

   KeyManagerFactorySpiWrapper(String var1, Provider var2) throws NoSuchAlgorithmException {
      this.theKeyManagerFactory = javax.net.ssl.KeyManagerFactory.getInstance(var1, var2);
   }

   protected void engineInit(KeyStore var1, char[] var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      this.theKeyManagerFactory.init(var1, var2);
   }

   protected KeyManager[] engineGetKeyManagers() {
      javax.net.ssl.KeyManager[] var3 = this.theKeyManagerFactory.getKeyManagers();
      KeyManager[] var4 = new KeyManager[var3.length];
      int var2 = 0;

      int var1;
      for(var1 = 0; var2 < var3.length; ++var2) {
         if (!(var3[var2] instanceof KeyManager)) {
            if (var3[var2] instanceof javax.net.ssl.X509KeyManager) {
               var4[var1] = new X509KeyManagerComSunWrapper((javax.net.ssl.X509KeyManager)var3[var2]);
               ++var1;
            }
         } else {
            var4[var1] = (KeyManager)var3[var2];
            ++var1;
         }
      }

      if (var1 != var2) {
         var4 = (KeyManager[])((KeyManager[])SSLSecurity.truncateArray(var4, new KeyManager[var1]));
      }

      return var4;
   }
}
