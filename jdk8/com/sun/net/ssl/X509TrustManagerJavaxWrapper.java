package com.sun.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

final class X509TrustManagerJavaxWrapper implements javax.net.ssl.X509TrustManager {
   private X509TrustManager theX509TrustManager;

   X509TrustManagerJavaxWrapper(X509TrustManager var1) {
      this.theX509TrustManager = var1;
   }

   public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      if (!this.theX509TrustManager.isClientTrusted(var1)) {
         throw new CertificateException("Untrusted Client Certificate Chain");
      }
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      if (!this.theX509TrustManager.isServerTrusted(var1)) {
         throw new CertificateException("Untrusted Server Certificate Chain");
      }
   }

   public X509Certificate[] getAcceptedIssuers() {
      return this.theX509TrustManager.getAcceptedIssuers();
   }
}
