package javax.net.ssl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public abstract class HttpsURLConnection extends HttpURLConnection {
   private static HostnameVerifier defaultHostnameVerifier = new HttpsURLConnection.DefaultHostnameVerifier();
   protected HostnameVerifier hostnameVerifier;
   private static SSLSocketFactory defaultSSLSocketFactory = null;
   private SSLSocketFactory sslSocketFactory;

   protected HttpsURLConnection(URL var1) {
      super(var1);
      this.hostnameVerifier = defaultHostnameVerifier;
      this.sslSocketFactory = getDefaultSSLSocketFactory();
   }

   public abstract String getCipherSuite();

   public abstract Certificate[] getLocalCertificates();

   public abstract Certificate[] getServerCertificates() throws SSLPeerUnverifiedException;

   public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      Certificate[] var1 = this.getServerCertificates();
      return ((X509Certificate)var1[0]).getSubjectX500Principal();
   }

   public Principal getLocalPrincipal() {
      Certificate[] var1 = this.getLocalCertificates();
      return var1 != null ? ((X509Certificate)var1[0]).getSubjectX500Principal() : null;
   }

   public static void setDefaultHostnameVerifier(HostnameVerifier var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("no default HostnameVerifier specified");
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(new SSLPermission("setHostnameVerifier"));
         }

         defaultHostnameVerifier = var0;
      }
   }

   public static HostnameVerifier getDefaultHostnameVerifier() {
      return defaultHostnameVerifier;
   }

   public void setHostnameVerifier(HostnameVerifier var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("no HostnameVerifier specified");
      } else {
         this.hostnameVerifier = var1;
      }
   }

   public HostnameVerifier getHostnameVerifier() {
      return this.hostnameVerifier;
   }

   public static void setDefaultSSLSocketFactory(SSLSocketFactory var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("no default SSLSocketFactory specified");
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkSetFactory();
         }

         defaultSSLSocketFactory = var0;
      }
   }

   public static SSLSocketFactory getDefaultSSLSocketFactory() {
      if (defaultSSLSocketFactory == null) {
         defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
      }

      return defaultSSLSocketFactory;
   }

   public void setSSLSocketFactory(SSLSocketFactory var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("no SSLSocketFactory specified");
      } else {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkSetFactory();
         }

         this.sslSocketFactory = var1;
      }
   }

   public SSLSocketFactory getSSLSocketFactory() {
      return this.sslSocketFactory;
   }

   private static class DefaultHostnameVerifier implements HostnameVerifier {
      private DefaultHostnameVerifier() {
      }

      public boolean verify(String var1, SSLSession var2) {
         return false;
      }

      // $FF: synthetic method
      DefaultHostnameVerifier(Object var1) {
         this();
      }
   }
}
