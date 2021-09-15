package sun.net.www.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.CacheRequest;
import java.net.CookieHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import sun.net.NetworkClient;
import sun.net.ProgressSource;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;
import sun.net.www.protocol.http.HttpURLConnection;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class HttpClient extends NetworkClient {
   protected boolean cachedHttpClient;
   protected boolean inCache;
   MessageHeader requests;
   PosterOutputStream poster;
   boolean streaming;
   boolean failedOnce;
   private boolean ignoreContinue;
   private static final int HTTP_CONTINUE = 100;
   static final int httpPortNumber = 80;
   protected boolean proxyDisabled;
   public boolean usingProxy;
   protected String host;
   protected int port;
   protected static KeepAliveCache kac = new KeepAliveCache();
   private static boolean keepAliveProp = true;
   private static boolean retryPostProp = true;
   private static final boolean cacheNTLMProp;
   private static final boolean cacheSPNEGOProp;
   volatile boolean keepingAlive;
   volatile boolean disableKeepAlive;
   int keepAliveConnections;
   int keepAliveTimeout;
   private CacheRequest cacheRequest;
   protected URL url;
   public boolean reuse;
   private HttpCapture capture;
   private static final PlatformLogger logger = HttpURLConnection.getHttpLogger();

   protected int getDefaultPort() {
      return 80;
   }

   private static int getDefaultPort(String var0) {
      if ("http".equalsIgnoreCase(var0)) {
         return 80;
      } else {
         return "https".equalsIgnoreCase(var0) ? 443 : -1;
      }
   }

   private static void logFinest(String var0) {
      if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
         logger.finest(var0);
      }

   }

   /** @deprecated */
   @Deprecated
   public static synchronized void resetProperties() {
   }

   int getKeepAliveTimeout() {
      return this.keepAliveTimeout;
   }

   public boolean getHttpKeepAliveSet() {
      return keepAliveProp;
   }

   protected HttpClient() {
      this.cachedHttpClient = false;
      this.poster = null;
      this.failedOnce = false;
      this.ignoreContinue = true;
      this.usingProxy = false;
      this.keepingAlive = false;
      this.keepAliveConnections = -1;
      this.keepAliveTimeout = 0;
      this.cacheRequest = null;
      this.reuse = false;
      this.capture = null;
   }

   private HttpClient(URL var1) throws IOException {
      this(var1, (String)null, -1, false);
   }

   protected HttpClient(URL var1, boolean var2) throws IOException {
      this(var1, (String)null, -1, var2);
   }

   public HttpClient(URL var1, String var2, int var3) throws IOException {
      this(var1, var2, var3, false);
   }

   protected HttpClient(URL var1, Proxy var2, int var3) throws IOException {
      this.cachedHttpClient = false;
      this.poster = null;
      this.failedOnce = false;
      this.ignoreContinue = true;
      this.usingProxy = false;
      this.keepingAlive = false;
      this.keepAliveConnections = -1;
      this.keepAliveTimeout = 0;
      this.cacheRequest = null;
      this.reuse = false;
      this.capture = null;
      this.proxy = var2 == null ? Proxy.NO_PROXY : var2;
      this.host = var1.getHost();
      this.url = var1;
      this.port = var1.getPort();
      if (this.port == -1) {
         this.port = this.getDefaultPort();
      }

      this.setConnectTimeout(var3);
      this.capture = HttpCapture.getCapture(var1);
      this.openServer();
   }

   protected static Proxy newHttpProxy(String var0, int var1, String var2) {
      if (var0 != null && var2 != null) {
         int var3 = var1 < 0 ? getDefaultPort(var2) : var1;
         InetSocketAddress var4 = InetSocketAddress.createUnresolved(var0, var3);
         return new Proxy(Proxy.Type.HTTP, var4);
      } else {
         return Proxy.NO_PROXY;
      }
   }

   private HttpClient(URL var1, String var2, int var3, boolean var4) throws IOException {
      this(var1, (Proxy)(var4 ? Proxy.NO_PROXY : newHttpProxy(var2, var3, "http")), -1);
   }

   public HttpClient(URL var1, String var2, int var3, boolean var4, int var5) throws IOException {
      this(var1, var4 ? Proxy.NO_PROXY : newHttpProxy(var2, var3, "http"), var5);
   }

   public static HttpClient New(URL var0) throws IOException {
      return New(var0, Proxy.NO_PROXY, -1, true, (HttpURLConnection)null);
   }

   public static HttpClient New(URL var0, boolean var1) throws IOException {
      return New(var0, Proxy.NO_PROXY, -1, var1, (HttpURLConnection)null);
   }

   public static HttpClient New(URL var0, Proxy var1, int var2, boolean var3, HttpURLConnection var4) throws IOException {
      if (var1 == null) {
         var1 = Proxy.NO_PROXY;
      }

      HttpClient var5 = null;
      if (var3) {
         var5 = kac.get(var0, (Object)null);
         if (var5 != null && var4 != null && var4.streaming() && var4.getRequestMethod() == "POST" && !var5.available()) {
            var5.inCache = false;
            var5.closeServer();
            var5 = null;
         }

         if (var5 != null) {
            if (var5.proxy != null && var5.proxy.equals(var1) || var5.proxy == null && var1 == null) {
               synchronized(var5) {
                  var5.cachedHttpClient = true;

                  assert var5.inCache;

                  var5.inCache = false;
                  if (var4 != null && var5.needsTunneling()) {
                     var4.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
                  }

                  logFinest("KeepAlive stream retrieved from the cache, " + var5);
               }
            } else {
               synchronized(var5) {
                  var5.inCache = false;
                  var5.closeServer();
               }

               var5 = null;
            }
         }
      }

      if (var5 == null) {
         var5 = new HttpClient(var0, var1, var2);
      } else {
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            if (var5.proxy != Proxy.NO_PROXY && var5.proxy != null) {
               var6.checkConnect(var0.getHost(), var0.getPort());
            } else {
               var6.checkConnect(InetAddress.getByName(var0.getHost()).getHostAddress(), var0.getPort());
            }
         }

         var5.url = var0;
      }

      return var5;
   }

   public static HttpClient New(URL var0, Proxy var1, int var2, HttpURLConnection var3) throws IOException {
      return New(var0, var1, var2, true, var3);
   }

   public static HttpClient New(URL var0, String var1, int var2, boolean var3) throws IOException {
      return New(var0, newHttpProxy(var1, var2, "http"), -1, var3, (HttpURLConnection)null);
   }

   public static HttpClient New(URL var0, String var1, int var2, boolean var3, int var4, HttpURLConnection var5) throws IOException {
      return New(var0, newHttpProxy(var1, var2, "http"), var4, var3, var5);
   }

   public void finished() {
      if (!this.reuse) {
         --this.keepAliveConnections;
         this.poster = null;
         if (this.keepAliveConnections > 0 && this.isKeepingAlive() && !this.serverOutput.checkError()) {
            this.putInKeepAliveCache();
         } else {
            this.closeServer();
         }

      }
   }

   protected synchronized boolean available() {
      boolean var1 = true;
      int var2 = -1;

      try {
         try {
            var2 = this.serverSocket.getSoTimeout();
            this.serverSocket.setSoTimeout(1);
            BufferedInputStream var3 = new BufferedInputStream(this.serverSocket.getInputStream());
            int var4 = var3.read();
            if (var4 == -1) {
               logFinest("HttpClient.available(): read returned -1: not available");
               var1 = false;
            }
         } catch (SocketTimeoutException var9) {
            logFinest("HttpClient.available(): SocketTimeout: its available");
         } finally {
            if (var2 != -1) {
               this.serverSocket.setSoTimeout(var2);
            }

         }
      } catch (IOException var11) {
         logFinest("HttpClient.available(): SocketException: not available");
         var1 = false;
      }

      return var1;
   }

   protected synchronized void putInKeepAliveCache() {
      if (this.inCache) {
         assert false : "Duplicate put to keep alive cache";

      } else {
         this.inCache = true;
         kac.put(this.url, (Object)null, this);
      }
   }

   protected synchronized boolean isInKeepAliveCache() {
      return this.inCache;
   }

   public void closeIdleConnection() {
      HttpClient var1 = kac.get(this.url, (Object)null);
      if (var1 != null) {
         var1.closeServer();
      }

   }

   public void openServer(String var1, int var2) throws IOException {
      this.serverSocket = this.doConnect(var1, var2);

      try {
         Object var3 = this.serverSocket.getOutputStream();
         if (this.capture != null) {
            var3 = new HttpCaptureOutputStream((OutputStream)var3, this.capture);
         }

         this.serverOutput = new PrintStream(new BufferedOutputStream((OutputStream)var3), false, encoding);
      } catch (UnsupportedEncodingException var4) {
         throw new InternalError(encoding + " encoding not found", var4);
      }

      this.serverSocket.setTcpNoDelay(true);
   }

   public boolean needsTunneling() {
      return false;
   }

   public synchronized boolean isCachedConnection() {
      return this.cachedHttpClient;
   }

   public void afterConnect() throws IOException, UnknownHostException {
   }

   private synchronized void privilegedOpenServer(final InetSocketAddress var1) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               HttpClient.this.openServer(var1.getHostString(), var1.getPort());
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getException();
      }
   }

   private void superOpenServer(String var1, int var2) throws IOException, UnknownHostException {
      super.openServer(var1, var2);
   }

   protected synchronized void openServer() throws IOException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkConnect(this.host, this.port);
      }

      if (!this.keepingAlive) {
         if (!this.url.getProtocol().equals("http") && !this.url.getProtocol().equals("https")) {
            if (this.proxy != null && this.proxy.type() == Proxy.Type.HTTP) {
               URLConnection.setProxiedHost(this.host);
               this.privilegedOpenServer((InetSocketAddress)this.proxy.address());
               this.usingProxy = true;
            } else {
               super.openServer(this.host, this.port);
               this.usingProxy = false;
            }
         } else if (this.proxy != null && this.proxy.type() == Proxy.Type.HTTP) {
            URLConnection.setProxiedHost(this.host);
            this.privilegedOpenServer((InetSocketAddress)this.proxy.address());
            this.usingProxy = true;
         } else {
            this.openServer(this.host, this.port);
            this.usingProxy = false;
         }
      }
   }

   public String getURLFile() throws IOException {
      String var1;
      if (this.usingProxy && !this.proxyDisabled) {
         StringBuffer var2 = new StringBuffer(128);
         var2.append(this.url.getProtocol());
         var2.append(":");
         if (this.url.getAuthority() != null && this.url.getAuthority().length() > 0) {
            var2.append("//");
            var2.append(this.url.getAuthority());
         }

         if (this.url.getPath() != null) {
            var2.append(this.url.getPath());
         }

         if (this.url.getQuery() != null) {
            var2.append('?');
            var2.append(this.url.getQuery());
         }

         var1 = var2.toString();
      } else {
         var1 = this.url.getFile();
         if (var1 != null && var1.length() != 0) {
            if (var1.charAt(0) == '?') {
               var1 = "/" + var1;
            }
         } else {
            var1 = "/";
         }
      }

      if (var1.indexOf(10) == -1) {
         return var1;
      } else {
         throw new MalformedURLException("Illegal character in URL");
      }
   }

   /** @deprecated */
   @Deprecated
   public void writeRequests(MessageHeader var1) {
      this.requests = var1;
      this.requests.print(this.serverOutput);
      this.serverOutput.flush();
   }

   public void writeRequests(MessageHeader var1, PosterOutputStream var2) throws IOException {
      this.requests = var1;
      this.requests.print(this.serverOutput);
      this.poster = var2;
      if (this.poster != null) {
         this.poster.writeTo(this.serverOutput);
      }

      this.serverOutput.flush();
   }

   public void writeRequests(MessageHeader var1, PosterOutputStream var2, boolean var3) throws IOException {
      this.streaming = var3;
      this.writeRequests(var1, var2);
   }

   public boolean parseHTTP(MessageHeader var1, ProgressSource var2, HttpURLConnection var3) throws IOException {
      try {
         this.serverInput = this.serverSocket.getInputStream();
         if (this.capture != null) {
            this.serverInput = new HttpCaptureInputStream(this.serverInput, this.capture);
         }

         this.serverInput = new BufferedInputStream(this.serverInput);
         return this.parseHTTPHeader(var1, var2, var3);
      } catch (SocketTimeoutException var6) {
         if (this.ignoreContinue) {
            this.closeServer();
         }

         throw var6;
      } catch (IOException var7) {
         this.closeServer();
         this.cachedHttpClient = false;
         if (!this.failedOnce && this.requests != null) {
            this.failedOnce = true;
            if (!this.getRequestMethod().equals("CONNECT") && !this.streaming && (!var3.getRequestMethod().equals("POST") || retryPostProp)) {
               this.openServer();
               if (this.needsTunneling()) {
                  MessageHeader var5 = this.requests;
                  var3.doTunneling();
                  this.requests = var5;
               }

               this.afterConnect();
               this.writeRequests(this.requests, this.poster);
               return this.parseHTTP(var1, var2, var3);
            }
         }

         throw var7;
      }
   }

   private boolean parseHTTPHeader(MessageHeader var1, ProgressSource var2, HttpURLConnection var3) throws IOException {
      this.keepAliveConnections = -1;
      this.keepAliveTimeout = 0;
      boolean var4 = false;
      byte[] var5 = new byte[8];

      int var6;
      String var15;
      boolean var20;
      try {
         var6 = 0;
         this.serverInput.mark(10);

         while(var6 < 8) {
            int var7 = this.serverInput.read(var5, var6, 8 - var6);
            if (var7 < 0) {
               break;
            }

            var6 += var7;
         }

         var15 = null;
         String var8 = null;
         var4 = var5[0] == 72 && var5[1] == 84 && var5[2] == 84 && var5[3] == 80 && var5[4] == 47 && var5[5] == 49 && var5[6] == 46;
         this.serverInput.reset();
         if (var4) {
            var1.parseHeader(this.serverInput);
            CookieHandler var9 = var3.getCookieHandler();
            if (var9 != null) {
               URI var10 = ParseUtil.toURI(this.url);
               if (var10 != null) {
                  var9.put(var10, var1.getHeaders());
               }
            }

            if (this.usingProxy) {
               var15 = var1.findValue("Proxy-Connection");
               var8 = var1.findValue("Proxy-Authenticate");
            }

            if (var15 == null) {
               var15 = var1.findValue("Connection");
               var8 = var1.findValue("WWW-Authenticate");
            }

            var20 = !this.disableKeepAlive;
            if (var20 && (!cacheNTLMProp || !cacheSPNEGOProp) && var8 != null) {
               var8 = var8.toLowerCase(Locale.US);
               if (!cacheNTLMProp) {
                  var20 &= !var8.startsWith("ntlm ");
               }

               if (!cacheSPNEGOProp) {
                  var20 &= !var8.startsWith("negotiate ");
                  var20 &= !var8.startsWith("kerberos ");
               }
            }

            this.disableKeepAlive |= !var20;
            if (var15 != null && var15.toLowerCase(Locale.US).equals("keep-alive")) {
               if (this.disableKeepAlive) {
                  this.keepAliveConnections = 1;
               } else {
                  HeaderParser var11 = new HeaderParser(var1.findValue("Keep-Alive"));
                  this.keepAliveConnections = var11.findInt("max", this.usingProxy ? 50 : 5);
                  this.keepAliveTimeout = var11.findInt("timeout", this.usingProxy ? 60 : 5);
               }
            } else if (var5[7] != 48) {
               if (var15 == null && !this.disableKeepAlive) {
                  this.keepAliveConnections = 5;
               } else {
                  this.keepAliveConnections = 1;
               }
            }
         } else {
            if (var6 != 8) {
               if (!this.failedOnce && this.requests != null) {
                  this.failedOnce = true;
                  if (!this.getRequestMethod().equals("CONNECT") && !this.streaming && (!var3.getRequestMethod().equals("POST") || retryPostProp)) {
                     this.closeServer();
                     this.cachedHttpClient = false;
                     this.openServer();
                     if (this.needsTunneling()) {
                        MessageHeader var19 = this.requests;
                        var3.doTunneling();
                        this.requests = var19;
                     }

                     this.afterConnect();
                     this.writeRequests(this.requests, this.poster);
                     return this.parseHTTP(var1, var2, var3);
                  }
               }

               throw new SocketException("Unexpected end of file from server");
            }

            var1.set("Content-type", "unknown/unknown");
         }
      } catch (IOException var14) {
         throw var14;
      }

      var6 = -1;

      try {
         var15 = var1.getValue(0);

         int var18;
         for(var18 = var15.indexOf(32); var15.charAt(var18) == ' '; ++var18) {
         }

         var6 = Integer.parseInt(var15.substring(var18, var18 + 3));
      } catch (Exception var13) {
      }

      if (var6 == 100 && this.ignoreContinue) {
         var1.reset();
         return this.parseHTTPHeader(var1, var2, var3);
      } else {
         long var16 = -1L;
         String var17 = var1.findValue("Transfer-Encoding");
         if (var17 != null && var17.equalsIgnoreCase("chunked")) {
            this.serverInput = new ChunkedInputStream(this.serverInput, this, var1);
            if (this.keepAliveConnections <= 1) {
               this.keepAliveConnections = 1;
               this.keepingAlive = false;
            } else {
               this.keepingAlive = !this.disableKeepAlive;
            }

            this.failedOnce = false;
         } else {
            String var22 = var1.findValue("content-length");
            if (var22 != null) {
               try {
                  var16 = Long.parseLong(var22);
               } catch (NumberFormatException var12) {
                  var16 = -1L;
               }
            }

            String var21 = this.requests.getKey(0);
            if (var21 != null && var21.startsWith("HEAD") || var6 == 304 || var6 == 204) {
               var16 = 0L;
            }

            if (this.keepAliveConnections > 1 && (var16 >= 0L || var6 == 304 || var6 == 204)) {
               this.keepingAlive = !this.disableKeepAlive;
               this.failedOnce = false;
            } else if (this.keepingAlive) {
               this.keepingAlive = false;
            }
         }

         if (var16 > 0L) {
            if (var2 != null) {
               var2.setContentType(var1.findValue("content-type"));
            }

            var20 = this.isKeepingAlive() || this.disableKeepAlive;
            if (var20) {
               logFinest("KeepAlive stream used: " + this.url);
               this.serverInput = new KeepAliveStream(this.serverInput, var2, var16, this);
               this.failedOnce = false;
            } else {
               this.serverInput = new MeteredStream(this.serverInput, var2, var16);
            }
         } else if (var16 == -1L) {
            if (var2 != null) {
               var2.setContentType(var1.findValue("content-type"));
               this.serverInput = new MeteredStream(this.serverInput, var2, var16);
            }
         } else if (var2 != null) {
            var2.finishTracking();
         }

         return var4;
      }
   }

   public synchronized InputStream getInputStream() {
      return this.serverInput;
   }

   public OutputStream getOutputStream() {
      return this.serverOutput;
   }

   public String toString() {
      return this.getClass().getName() + "(" + this.url + ")";
   }

   public final boolean isKeepingAlive() {
      return this.getHttpKeepAliveSet() && this.keepingAlive;
   }

   public void setCacheRequest(CacheRequest var1) {
      this.cacheRequest = var1;
   }

   CacheRequest getCacheRequest() {
      return this.cacheRequest;
   }

   String getRequestMethod() {
      if (this.requests != null) {
         String var1 = this.requests.getKey(0);
         if (var1 != null) {
            return var1.split("\\s+")[0];
         }
      }

      return "";
   }

   protected void finalize() throws Throwable {
   }

   public void setDoNotRetry(boolean var1) {
      this.failedOnce = var1;
   }

   public void setIgnoreContinue(boolean var1) {
      this.ignoreContinue = var1;
   }

   public void closeServer() {
      try {
         this.keepingAlive = false;
         this.serverSocket.close();
      } catch (Exception var2) {
      }

   }

   public String getProxyHostUsed() {
      return !this.usingProxy ? null : ((InetSocketAddress)this.proxy.address()).getHostString();
   }

   public int getProxyPortUsed() {
      return this.usingProxy ? ((InetSocketAddress)this.proxy.address()).getPort() : -1;
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("http.keepAlive")));
      String var1 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.net.http.retryPost")));
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jdk.ntlm.cache")));
      String var3 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jdk.spnego.cache")));
      if (var0 != null) {
         keepAliveProp = Boolean.valueOf(var0);
      } else {
         keepAliveProp = true;
      }

      if (var1 != null) {
         retryPostProp = Boolean.valueOf(var1);
      } else {
         retryPostProp = true;
      }

      if (var2 != null) {
         cacheNTLMProp = Boolean.parseBoolean(var2);
      } else {
         cacheNTLMProp = true;
      }

      if (var3 != null) {
         cacheSPNEGOProp = Boolean.parseBoolean(var3);
      } else {
         cacheSPNEGOProp = true;
      }

   }
}
