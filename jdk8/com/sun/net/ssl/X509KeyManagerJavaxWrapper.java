package com.sun.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;

final class X509KeyManagerJavaxWrapper implements javax.net.ssl.X509KeyManager {
   private X509KeyManager theX509KeyManager;

   X509KeyManagerJavaxWrapper(X509KeyManager var1) {
      this.theX509KeyManager = var1;
   }

   public String[] getClientAliases(String var1, Principal[] var2) {
      return this.theX509KeyManager.getClientAliases(var1, var2);
   }

   public String chooseClientAlias(String[] var1, Principal[] var2, Socket var3) {
      if (var1 == null) {
         return null;
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            String var4;
            if ((var4 = this.theX509KeyManager.chooseClientAlias(var1[var5], var2)) != null) {
               return var4;
            }
         }

         return null;
      }
   }

   public String chooseEngineClientAlias(String[] var1, Principal[] var2, SSLEngine var3) {
      if (var1 == null) {
         return null;
      } else {
         for(int var5 = 0; var5 < var1.length; ++var5) {
            String var4;
            if ((var4 = this.theX509KeyManager.chooseClientAlias(var1[var5], var2)) != null) {
               return var4;
            }
         }

         return null;
      }
   }

   public String[] getServerAliases(String var1, Principal[] var2) {
      return this.theX509KeyManager.getServerAliases(var1, var2);
   }

   public String chooseServerAlias(String var1, Principal[] var2, Socket var3) {
      return var1 == null ? null : this.theX509KeyManager.chooseServerAlias(var1, var2);
   }

   public String chooseEngineServerAlias(String var1, Principal[] var2, SSLEngine var3) {
      return var1 == null ? null : this.theX509KeyManager.chooseServerAlias(var1, var2);
   }

   public X509Certificate[] getCertificateChain(String var1) {
      return this.theX509KeyManager.getCertificateChain(var1);
   }

   public PrivateKey getPrivateKey(String var1) {
      return this.theX509KeyManager.getPrivateKey(var1);
   }
}
