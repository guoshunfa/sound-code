package sun.net.www.protocol.https;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.net.www.http.HttpClient;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;
import sun.security.ssl.SSLSocketImpl;
import sun.security.util.HostnameChecker;
import sun.util.logging.PlatformLogger;

final class HttpsClient extends HttpClient implements HandshakeCompletedListener {
   private static final int httpsPortNumber = 443;
   private static final String defaultHVCanonicalName = "javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier";
   private HostnameVerifier hv;
   private SSLSocketFactory sslSocketFactory;
   private SSLSession session;

   protected int getDefaultPort() {
      return 443;
   }

   private String[] getCipherSuites() {
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("https.cipherSuites")));
      String[] var1;
      if (var2 != null && !"".equals(var2)) {
         Vector var4 = new Vector();
         StringTokenizer var3 = new StringTokenizer(var2, ",");

         while(var3.hasMoreTokens()) {
            var4.addElement(var3.nextToken());
         }

         var1 = new String[var4.size()];

         for(int var5 = 0; var5 < var1.length; ++var5) {
            var1[var5] = (String)var4.elementAt(var5);
         }
      } else {
         var1 = null;
      }

      return var1;
   }

   private String[] getProtocols() {
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("https.protocols")));
      String[] var1;
      if (var2 != null && !"".equals(var2)) {
         Vector var4 = new Vector();
         StringTokenizer var3 = new StringTokenizer(var2, ",");

         while(var3.hasMoreTokens()) {
            var4.addElement(var3.nextToken());
         }

         var1 = new String[var4.size()];

         for(int var5 = 0; var5 < var1.length; ++var5) {
            var1[var5] = (String)var4.elementAt(var5);
         }
      } else {
         var1 = null;
      }

      return var1;
   }

   private String getUserAgent() {
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("https.agent")));
      if (var1 == null || var1.length() == 0) {
         var1 = "JSSE";
      }

      return var1;
   }

   private HttpsClient(SSLSocketFactory var1, URL var2) throws IOException {
      this(var1, var2, (String)((String)null), -1);
   }

   HttpsClient(SSLSocketFactory var1, URL var2, String var3, int var4) throws IOException {
      this(var1, var2, var3, var4, -1);
   }

   HttpsClient(SSLSocketFactory var1, URL var2, String var3, int var4, int var5) throws IOException {
      this(var1, var2, var3 == null ? null : HttpClient.newHttpProxy(var3, var4, "https"), var5);
   }

   HttpsClient(SSLSocketFactory var1, URL var2, Proxy var3, int var4) throws IOException {
      PlatformLogger var5 = HttpURLConnection.getHttpLogger();
      if (var5.isLoggable(PlatformLogger.Level.FINEST)) {
         var5.finest("Creating new HttpsClient with url:" + var2 + " and proxy:" + var3 + " with connect timeout:" + var4);
      }

      this.proxy = var3;
      this.setSSLSocketFactory(var1);
      this.proxyDisabled = true;
      this.host = var2.getHost();
      this.url = var2;
      this.port = var2.getPort();
      if (this.port == -1) {
         this.port = this.getDefaultPort();
      }

      this.setConnectTimeout(var4);
      this.openServer();
   }

   static HttpClient New(SSLSocketFactory var0, URL var1, HostnameVerifier var2, HttpURLConnection var3) throws IOException {
      return New(var0, var1, var2, true, var3);
   }

   static HttpClient New(SSLSocketFactory var0, URL var1, HostnameVerifier var2, boolean var3, HttpURLConnection var4) throws IOException {
      return New(var0, var1, var2, (String)null, -1, var3, var4);
   }

   static HttpClient New(SSLSocketFactory var0, URL var1, HostnameVerifier var2, String var3, int var4, HttpURLConnection var5) throws IOException {
      return New(var0, var1, var2, var3, var4, true, var5);
   }

   static HttpClient New(SSLSocketFactory var0, URL var1, HostnameVerifier var2, String var3, int var4, boolean var5, HttpURLConnection var6) throws IOException {
      return New(var0, var1, var2, var3, var4, var5, -1, var6);
   }

   static HttpClient New(SSLSocketFactory var0, URL var1, HostnameVerifier var2, String var3, int var4, boolean var5, int var6, HttpURLConnection var7) throws IOException {
      return New(var0, var1, var2, var3 == null ? null : HttpClient.newHttpProxy(var3, var4, "https"), var5, var6, var7);
   }

   static HttpClient New(SSLSocketFactory var0, URL var1, HostnameVerifier var2, Proxy var3, boolean var4, int var5, HttpURLConnection var6) throws IOException {
      if (var3 == null) {
         var3 = Proxy.NO_PROXY;
      }

      PlatformLogger var7 = HttpURLConnection.getHttpLogger();
      if (var7.isLoggable(PlatformLogger.Level.FINEST)) {
         var7.finest("Looking for HttpClient for URL " + var1 + " and proxy value of " + var3);
      }

      HttpsClient var8 = null;
      if (var4) {
         var8 = (HttpsClient)kac.get(var1, var0);
         if (var8 != null && var6 != null && var6.streaming() && var6.getRequestMethod() == "POST" && !var8.available()) {
            var8 = null;
         }

         if (var8 != null) {
            if (var8.proxy != null && var8.proxy.equals(var3) || var8.proxy == null && var3 == Proxy.NO_PROXY) {
               synchronized(var8) {
                  var8.cachedHttpClient = true;

                  assert var8.inCache;

                  var8.inCache = false;
                  if (var6 != null && var8.needsTunneling()) {
                     var6.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
                  }

                  if (var7.isLoggable(PlatformLogger.Level.FINEST)) {
                     var7.finest("KeepAlive stream retrieved from the cache, " + var8);
                  }
               }
            } else {
               synchronized(var8) {
                  if (var7.isLoggable(PlatformLogger.Level.FINEST)) {
                     var7.finest("Not returning this connection to cache: " + var8);
                  }

                  var8.inCache = false;
                  var8.closeServer();
               }

               var8 = null;
            }
         }
      }

      if (var8 == null) {
         var8 = new HttpsClient(var0, var1, var3, var5);
      } else {
         SecurityManager var9 = System.getSecurityManager();
         if (var9 != null) {
            if (var8.proxy != Proxy.NO_PROXY && var8.proxy != null) {
               var9.checkConnect(var1.getHost(), var1.getPort());
            } else {
               var9.checkConnect(InetAddress.getByName(var1.getHost()).getHostAddress(), var1.getPort());
            }
         }

         var8.url = var1;
      }

      var8.setHostnameVerifier(var2);
      return var8;
   }

   void setHostnameVerifier(HostnameVerifier var1) {
      this.hv = var1;
   }

   void setSSLSocketFactory(SSLSocketFactory var1) {
      this.sslSocketFactory = var1;
   }

   SSLSocketFactory getSSLSocketFactory() {
      return this.sslSocketFactory;
   }

   protected Socket createSocket() throws IOException {
      try {
         return this.sslSocketFactory.createSocket();
      } catch (SocketException var3) {
         Throwable var2 = var3.getCause();
         if (var2 != null && var2 instanceof UnsupportedOperationException) {
            return super.createSocket();
         } else {
            throw var3;
         }
      }
   }

   public boolean needsTunneling() {
      return this.proxy != null && this.proxy.type() != Proxy.Type.DIRECT && this.proxy.type() != Proxy.Type.SOCKS;
   }

   public void afterConnect() throws IOException, UnknownHostException {
      if (!this.isCachedConnection()) {
         SSLSocket var1 = null;
         SSLSocketFactory var2 = this.sslSocketFactory;

         try {
            if (!(this.serverSocket instanceof SSLSocket)) {
               var1 = (SSLSocket)var2.createSocket(this.serverSocket, this.host, this.port, true);
            } else {
               var1 = (SSLSocket)this.serverSocket;
               if (var1 instanceof SSLSocketImpl) {
                  ((SSLSocketImpl)var1).setHost(this.host);
               }
            }
         } catch (IOException var11) {
            try {
               var1 = (SSLSocket)var2.createSocket(this.host, this.port);
            } catch (IOException var10) {
               throw var11;
            }
         }

         String[] var3 = this.getProtocols();
         String[] var4 = this.getCipherSuites();
         if (var3 != null) {
            var1.setEnabledProtocols(var3);
         }

         if (var4 != null) {
            var1.setEnabledCipherSuites(var4);
         }

         var1.addHandshakeCompletedListener(this);
         boolean var5 = true;
         String var6 = var1.getSSLParameters().getEndpointIdentificationAlgorithm();
         if (var6 != null && var6.length() != 0) {
            if (var6.equalsIgnoreCase("HTTPS")) {
               var5 = false;
            }
         } else {
            boolean var7 = false;
            if (this.hv != null) {
               String var8 = this.hv.getClass().getCanonicalName();
               if (var8 != null && var8.equalsIgnoreCase("javax.net.ssl.HttpsURLConnection.DefaultHostnameVerifier")) {
                  var7 = true;
               }
            } else {
               var7 = true;
            }

            if (var7) {
               SSLParameters var12 = var1.getSSLParameters();
               var12.setEndpointIdentificationAlgorithm("HTTPS");
               var1.setSSLParameters(var12);
               var5 = false;
            }
         }

         var1.startHandshake();
         this.session = var1.getSession();
         this.serverSocket = var1;

         try {
            this.serverOutput = new PrintStream(new BufferedOutputStream(this.serverSocket.getOutputStream()), false, encoding);
         } catch (UnsupportedEncodingException var9) {
            throw new InternalError(encoding + " encoding not found");
         }

         if (var5) {
            this.checkURLSpoofing(this.hv);
         }
      } else {
         this.session = ((SSLSocket)this.serverSocket).getSession();
      }

   }

   private void checkURLSpoofing(HostnameVerifier var1) throws IOException {
      String var2 = this.url.getHost();
      if (var2 != null && var2.startsWith("[") && var2.endsWith("]")) {
         var2 = var2.substring(1, var2.length() - 1);
      }

      Certificate[] var3 = null;
      String var4 = this.session.getCipherSuite();

      try {
         HostnameChecker var5 = HostnameChecker.getInstance((byte)1);
         if (var4.startsWith("TLS_KRB5")) {
            if (!HostnameChecker.match(var2, this.getPeerPrincipal())) {
               throw new SSLPeerUnverifiedException("Hostname checker failed for Kerberos");
            }
         } else {
            var3 = this.session.getPeerCertificates();
            if (!(var3[0] instanceof X509Certificate)) {
               throw new SSLPeerUnverifiedException("");
            }

            X509Certificate var6 = (X509Certificate)var3[0];
            var5.match(var2, var6);
         }

         return;
      } catch (SSLPeerUnverifiedException var7) {
      } catch (CertificateException var8) {
      }

      if (var4 == null || var4.indexOf("_anon_") == -1) {
         if (var1 == null || !var1.verify(var2, this.session)) {
            this.serverSocket.close();
            this.session.invalidate();
            throw new IOException("HTTPS hostname wrong:  should be <" + this.url.getHost() + ">");
         }
      }
   }

   protected void putInKeepAliveCache() {
      if (this.inCache) {
         assert false : "Duplicate put to keep alive cache";

      } else {
         this.inCache = true;
         kac.put(this.url, this.sslSocketFactory, this);
      }
   }

   public void closeIdleConnection() {
      HttpClient var1 = kac.get(this.url, this.sslSocketFactory);
      if (var1 != null) {
         var1.closeServer();
      }

   }

   String getCipherSuite() {
      return this.session.getCipherSuite();
   }

   public Certificate[] getLocalCertificates() {
      return this.session.getLocalCertificates();
   }

   Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
      return this.session.getPeerCertificates();
   }

   javax.security.cert.X509Certificate[] getServerCertificateChain() throws SSLPeerUnverifiedException {
      return this.session.getPeerCertificateChain();
   }

   Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      Object var1;
      try {
         var1 = this.session.getPeerPrincipal();
      } catch (AbstractMethodError var4) {
         Certificate[] var3 = this.session.getPeerCertificates();
         var1 = ((X509Certificate)var3[0]).getSubjectX500Principal();
      }

      return (Principal)var1;
   }

   Principal getLocalPrincipal() {
      Object var1;
      try {
         var1 = this.session.getLocalPrincipal();
      } catch (AbstractMethodError var4) {
         var1 = null;
         Certificate[] var3 = this.session.getLocalCertificates();
         if (var3 != null) {
            var1 = ((X509Certificate)var3[0]).getSubjectX500Principal();
         }
      }

      return (Principal)var1;
   }

   public void handshakeCompleted(HandshakeCompletedEvent var1) {
      this.session = var1.getSession();
   }

   public String getProxyHostUsed() {
      return !this.needsTunneling() ? null : super.getProxyHostUsed();
   }

   public int getProxyPortUsed() {
      return this.proxy != null && this.proxy.type() != Proxy.Type.DIRECT && this.proxy.type() != Proxy.Type.SOCKS ? ((InetSocketAddress)this.proxy.address()).getPort() : -1;
   }
}
