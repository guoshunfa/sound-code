package com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

final class X509TrustManagerComSunWrapper implements X509TrustManager {
   private javax.net.ssl.X509TrustManager theX509TrustManager;

   X509TrustManagerComSunWrapper(javax.net.ssl.X509TrustManager var1) {
      this.theX509TrustManager = var1;
   }

   public boolean isClientTrusted(X509Certificate[] var1) {
      try {
         this.theX509TrustManager.checkClientTrusted(var1, "UNKNOWN");
         return true;
      } catch (CertificateException var3) {
         return false;
      }
   }

   public boolean isServerTrusted(X509Certificate[] var1) {
      try {
         this.theX509TrustManager.checkServerTrusted(var1, "UNKNOWN");
         return true;
      } catch (CertificateException var3) {
         return false;
      }
   }

   public X509Certificate[] getAcceptedIssuers() {
      return this.theX509TrustManager.getAcceptedIssuers();
   }
}
