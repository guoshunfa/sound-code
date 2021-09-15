package com.sun.net.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

final class X509KeyManagerComSunWrapper implements X509KeyManager {
   private javax.net.ssl.X509KeyManager theX509KeyManager;

   X509KeyManagerComSunWrapper(javax.net.ssl.X509KeyManager var1) {
      this.theX509KeyManager = var1;
   }

   public String[] getClientAliases(String var1, Principal[] var2) {
      return this.theX509KeyManager.getClientAliases(var1, var2);
   }

   public String chooseClientAlias(String var1, Principal[] var2) {
      String[] var3 = new String[]{var1};
      return this.theX509KeyManager.chooseClientAlias(var3, var2, (Socket)null);
   }

   public String[] getServerAliases(String var1, Principal[] var2) {
      return this.theX509KeyManager.getServerAliases(var1, var2);
   }

   public String chooseServerAlias(String var1, Principal[] var2) {
      return this.theX509KeyManager.chooseServerAlias(var1, var2, (Socket)null);
   }

   public X509Certificate[] getCertificateChain(String var1) {
      return this.theX509KeyManager.getCertificateChain(var1);
   }

   public PrivateKey getPrivateKey(String var1) {
      return this.theX509KeyManager.getPrivateKey(var1);
   }
}
