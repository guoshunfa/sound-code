package sun.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.SecureCacheResponse;
import java.net.URL;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import javax.security.cert.X509Certificate;
import sun.net.www.http.HttpClient;
import sun.net.www.protocol.http.HttpURLConnection;

public abstract class AbstractDelegateHttpsURLConnection extends HttpURLConnection {
   protected AbstractDelegateHttpsURLConnection(URL var1, sun.net.www.protocol.http.Handler var2) throws IOException {
      this(var1, (Proxy)null, var2);
   }

   protected AbstractDelegateHttpsURLConnection(URL var1, Proxy var2, sun.net.www.protocol.http.Handler var3) throws IOException {
      super(var1, var2, var3);
   }

   protected abstract SSLSocketFactory getSSLSocketFactory();

   protected abstract HostnameVerifier getHostnameVerifier();

   public void setNewClient(URL var1) throws IOException {
      this.setNewClient(var1, false);
   }

   public void setNewClient(URL var1, boolean var2) throws IOException {
      this.http = HttpsClient.New(this.getSSLSocketFactory(), var1, this.getHostnameVerifier(), var2, this);
      ((HttpsClient)this.http).afterConnect();
   }

   public void setProxiedClient(URL var1, String var2, int var3) throws IOException {
      this.setProxiedClient(var1, var2, var3, false);
   }

   public void setProxiedClient(URL var1, String var2, int var3, boolean var4) throws IOException {
      this.proxiedConnect(var1, var2, var3, var4);
      if (!this.http.isCachedConnection()) {
         this.doTunneling();
      }

      ((HttpsClient)this.http).afterConnect();
   }

   protected void proxiedConnect(URL var1, String var2, int var3, boolean var4) throws IOException {
      if (!this.connected) {
         this.http = HttpsClient.New(this.getSSLSocketFactory(), var1, this.getHostnameVerifier(), var2, var3, var4, this);
         this.connected = true;
      }
   }

   public boolean isConnected() {
      return this.connected;
   }

   public void setConnected(boolean var1) {
      this.connected = var1;
   }

   public void connect() throws IOException {
      if (!this.connected) {
         this.plainConnect();
         if (this.cachedResponse == null) {
            if (!this.http.isCachedConnection() && this.http.needsTunneling()) {
               this.doTunneling();
            }

            ((HttpsClient)this.http).afterConnect();
         }
      }
   }

   protected HttpClient getNewHttpClient(URL var1, Proxy var2, int var3) throws IOException {
      return HttpsClient.New(this.getSSLSocketFactory(), var1, this.getHostnameVerifier(), var2, true, var3, this);
   }

   protected HttpClient getNewHttpClient(URL var1, Proxy var2, int var3, boolean var4) throws IOException {
      return HttpsClient.New(this.getSSLSocketFactory(), var1, this.getHostnameVerifier(), var2, var4, var3, this);
   }

   public String getCipherSuite() {
      if (this.cachedResponse != null) {
         return ((SecureCacheResponse)this.cachedResponse).getCipherSuite();
      } else if (this.http == null) {
         throw new IllegalStateException("connection not yet open");
      } else {
         return ((HttpsClient)this.http).getCipherSuite();
      }
   }

   public Certificate[] getLocalCertificates() {
      if (this.cachedResponse != null) {
         List var1 = ((SecureCacheResponse)this.cachedResponse).getLocalCertificateChain();
         return var1 == null ? null : (Certificate[])var1.toArray(new Certificate[0]);
      } else if (this.http == null) {
         throw new IllegalStateException("connection not yet open");
      } else {
         return ((HttpsClient)this.http).getLocalCertificates();
      }
   }

   public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
      if (this.cachedResponse != null) {
         List var1 = ((SecureCacheResponse)this.cachedResponse).getServerCertificateChain();
         return var1 == null ? null : (Certificate[])var1.toArray(new Certificate[0]);
      } else if (this.http == null) {
         throw new IllegalStateException("connection not yet open");
      } else {
         return ((HttpsClient)this.http).getServerCertificates();
      }
   }

   public X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
      if (this.cachedResponse != null) {
         throw new UnsupportedOperationException("this method is not supported when using cache");
      } else if (this.http == null) {
         throw new IllegalStateException("connection not yet open");
      } else {
         return ((HttpsClient)this.http).getServerCertificateChain();
      }
   }

   Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      if (this.cachedResponse != null) {
         return ((SecureCacheResponse)this.cachedResponse).getPeerPrincipal();
      } else if (this.http == null) {
         throw new IllegalStateException("connection not yet open");
      } else {
         return ((HttpsClient)this.http).getPeerPrincipal();
      }
   }

   Principal getLocalPrincipal() {
      if (this.cachedResponse != null) {
         return ((SecureCacheResponse)this.cachedResponse).getLocalPrincipal();
      } else if (this.http == null) {
         throw new IllegalStateException("connection not yet open");
      } else {
         return ((HttpsClient)this.http).getLocalPrincipal();
      }
   }
}
