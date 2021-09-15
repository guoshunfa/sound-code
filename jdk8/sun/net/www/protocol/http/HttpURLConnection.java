package sun.net.www.protocol.http;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpRetryException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.ResponseCache;
import java.net.SecureCacheResponse;
import java.net.SocketPermission;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLPermission;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import sun.misc.JavaNetHttpCookieAccess;
import sun.misc.SharedSecrets;
import sun.net.ApplicationProxy;
import sun.net.NetProperties;
import sun.net.ProgressMonitor;
import sun.net.ProgressSource;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.ParseUtil;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.http.ChunkedOutputStream;
import sun.net.www.http.HttpClient;
import sun.net.www.http.PosterOutputStream;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetIntegerAction;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

public class HttpURLConnection extends java.net.HttpURLConnection {
   static String HTTP_CONNECT = "CONNECT";
   static final String version = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.version")));
   public static final String userAgent;
   static final int defaultmaxRedirects = 20;
   static final int maxRedirects = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("http.maxRedirects", 20)));
   static final boolean validateProxy;
   static final boolean validateServer;
   static final Set<String> disabledProxyingSchemes;
   static final Set<String> disabledTunnelingSchemes;
   private HttpURLConnection.StreamingOutputStream strOutputStream;
   private static final String RETRY_MSG1 = "cannot retry due to proxy authentication, in streaming mode";
   private static final String RETRY_MSG2 = "cannot retry due to server authentication, in streaming mode";
   private static final String RETRY_MSG3 = "cannot retry due to redirection, in streaming mode";
   private static boolean enableESBuffer = false;
   private static int timeout4ESBuffer = 0;
   private static int bufSize4ES = 0;
   private static final boolean allowRestrictedHeaders;
   private static final Set<String> restrictedHeaderSet;
   private static final String[] restrictedHeaders = new String[]{"Access-Control-Request-Headers", "Access-Control-Request-Method", "Connection", "Content-Length", "Content-Transfer-Encoding", "Host", "Keep-Alive", "Origin", "Trailer", "Transfer-Encoding", "Upgrade", "Via"};
   static final String httpVersion = "HTTP/1.1";
   static final String acceptString = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
   private static final String[] EXCLUDE_HEADERS;
   private static final String[] EXCLUDE_HEADERS2;
   protected HttpClient http;
   protected Handler handler;
   protected Proxy instProxy;
   private CookieHandler cookieHandler;
   private final ResponseCache cacheHandler;
   protected CacheResponse cachedResponse;
   private MessageHeader cachedHeaders;
   private InputStream cachedInputStream;
   protected PrintStream ps;
   private InputStream errorStream;
   private boolean setUserCookies;
   private String userCookies;
   private String userCookies2;
   /** @deprecated */
   @Deprecated
   private static HttpAuthenticator defaultAuth;
   private MessageHeader requests;
   private MessageHeader userHeaders;
   private boolean connecting;
   String domain;
   DigestAuthentication.Parameters digestparams;
   AuthenticationInfo currentProxyCredentials;
   AuthenticationInfo currentServerCredentials;
   boolean needToCheck;
   private boolean doingNTLM2ndStage;
   private boolean doingNTLMp2ndStage;
   private boolean tryTransparentNTLMServer;
   private boolean tryTransparentNTLMProxy;
   private boolean useProxyResponseCode;
   private Object authObj;
   boolean isUserServerAuth;
   boolean isUserProxyAuth;
   String serverAuthKey;
   String proxyAuthKey;
   protected ProgressSource pi;
   private MessageHeader responses;
   private InputStream inputStream;
   private PosterOutputStream poster;
   private boolean setRequests;
   private boolean failedOnce;
   private Exception rememberedException;
   private HttpClient reuseClient;
   private HttpURLConnection.TunnelState tunnelState;
   private int connectTimeout;
   private int readTimeout;
   private SocketPermission socketPermission;
   private static final PlatformLogger logger;
   String requestURI;
   byte[] cdata;
   private static final String SET_COOKIE = "set-cookie";
   private static final String SET_COOKIE2 = "set-cookie2";
   private Map<String, List<String>> filteredHeaders;

   private static String getNetProperty(String var0) {
      PrivilegedAction var1 = () -> {
         return NetProperties.get(var0);
      };
      return (String)AccessController.doPrivileged(var1);
   }

   private static Set<String> schemesListToSet(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         HashSet var1 = new HashSet();
         String[] var2 = var0.split("\\s*,\\s*");
         String[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            var1.add(var6.toLowerCase(Locale.ROOT));
         }

         return var1;
      } else {
         return Collections.emptySet();
      }
   }

   private static PasswordAuthentication privilegedRequestPasswordAuthentication(final String var0, final InetAddress var1, final int var2, final String var3, final String var4, final String var5, final URL var6, final Authenticator.RequestorType var7) {
      return (PasswordAuthentication)AccessController.doPrivileged(new PrivilegedAction<PasswordAuthentication>() {
         public PasswordAuthentication run() {
            if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
               HttpURLConnection.logger.finest("Requesting Authentication: host =" + var0 + " url = " + var6);
            }

            PasswordAuthentication var1x = Authenticator.requestPasswordAuthentication(var0, var1, var2, var3, var4, var5, var6, var7);
            if (HttpURLConnection.logger.isLoggable(PlatformLogger.Level.FINEST)) {
               HttpURLConnection.logger.finest("Authentication returned: " + (var1x != null ? var1x.toString() : "null"));
            }

            return var1x;
         }
      });
   }

   private boolean isRestrictedHeader(String var1, String var2) {
      if (allowRestrictedHeaders) {
         return false;
      } else {
         var1 = var1.toLowerCase();
         if (restrictedHeaderSet.contains(var1)) {
            return !var1.equals("connection") || !var2.equalsIgnoreCase("close");
         } else {
            return var1.startsWith("sec-");
         }
      }
   }

   private boolean isExternalMessageHeaderAllowed(String var1, String var2) {
      this.checkMessageHeader(var1, var2);
      return !this.isRestrictedHeader(var1, var2);
   }

   public static PlatformLogger getHttpLogger() {
      return logger;
   }

   public Object authObj() {
      return this.authObj;
   }

   public void authObj(Object var1) {
      this.authObj = var1;
   }

   private void checkMessageHeader(String var1, String var2) {
      byte var3 = 10;
      int var4 = var1.indexOf(var3);
      int var5 = var1.indexOf(58);
      if (var4 == -1 && var5 == -1) {
         if (var2 != null) {
            var4 = var2.indexOf(var3);

            while(true) {
               if (var4 == -1) {
                  return;
               }

               ++var4;
               if (var4 >= var2.length()) {
                  break;
               }

               char var6 = var2.charAt(var4);
               if (var6 != ' ' && var6 != '\t') {
                  break;
               }

               var4 = var2.indexOf(var3, var4);
            }

            throw new IllegalArgumentException("Illegal character(s) in message header value: " + var2);
         }
      } else {
         throw new IllegalArgumentException("Illegal character(s) in message header field: " + var1);
      }
   }

   public synchronized void setRequestMethod(String var1) throws ProtocolException {
      if (this.connecting) {
         throw new IllegalStateException("connect in progress");
      } else {
         super.setRequestMethod(var1);
      }
   }

   private void writeRequests() throws IOException {
      if (this.http.usingProxy && this.tunnelState() != HttpURLConnection.TunnelState.TUNNELING) {
         this.setPreemptiveProxyAuthentication(this.requests);
      }

      if (!this.setRequests) {
         if (!this.failedOnce) {
            this.checkURLFile();
            this.requests.prepend(this.method + " " + this.getRequestURI() + " " + "HTTP/1.1", (String)null);
         }

         if (!this.getUseCaches()) {
            this.requests.setIfNotSet("Cache-Control", "no-cache");
            this.requests.setIfNotSet("Pragma", "no-cache");
         }

         this.requests.setIfNotSet("User-Agent", userAgent);
         int var1 = this.url.getPort();
         String var2 = this.url.getHost();
         if (var1 != -1 && var1 != this.url.getDefaultPort()) {
            var2 = var2 + ":" + var1;
         }

         String var3 = this.requests.findValue("Host");
         if (var3 == null || !var3.equalsIgnoreCase(var2) && !this.checkSetHost()) {
            this.requests.set("Host", var2);
         }

         this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
         if (!this.failedOnce && this.http.getHttpKeepAliveSet()) {
            if (this.http.usingProxy && this.tunnelState() != HttpURLConnection.TunnelState.TUNNELING) {
               this.requests.setIfNotSet("Proxy-Connection", "keep-alive");
            } else {
               this.requests.setIfNotSet("Connection", "keep-alive");
            }
         } else {
            this.requests.setIfNotSet("Connection", "close");
         }

         long var4 = this.getIfModifiedSince();
         if (var4 != 0L) {
            Date var6 = new Date(var4);
            SimpleDateFormat var7 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            var7.setTimeZone(TimeZone.getTimeZone("GMT"));
            this.requests.setIfNotSet("If-Modified-Since", var7.format(var6));
         }

         AuthenticationInfo var13 = AuthenticationInfo.getServerAuth(this.url);
         if (var13 != null && var13.supportsPreemptiveAuthorization()) {
            this.requests.setIfNotSet(var13.getHeaderName(), var13.getHeaderValue(this.url, this.method));
            this.currentServerCredentials = var13;
         }

         if (!this.method.equals("PUT") && (this.poster != null || this.streaming())) {
            this.requests.setIfNotSet("Content-type", "application/x-www-form-urlencoded");
         }

         boolean var14 = false;
         if (this.streaming()) {
            if (this.chunkLength != -1) {
               this.requests.set("Transfer-Encoding", "chunked");
               var14 = true;
            } else if (this.fixedContentLengthLong != -1L) {
               this.requests.set("Content-Length", String.valueOf(this.fixedContentLengthLong));
            } else if (this.fixedContentLength != -1) {
               this.requests.set("Content-Length", String.valueOf(this.fixedContentLength));
            }
         } else if (this.poster != null) {
            synchronized(this.poster) {
               this.poster.close();
               this.requests.set("Content-Length", String.valueOf(this.poster.size()));
            }
         }

         if (!var14 && this.requests.findValue("Transfer-Encoding") != null) {
            this.requests.remove("Transfer-Encoding");
            if (logger.isLoggable(PlatformLogger.Level.WARNING)) {
               logger.warning("use streaming mode for chunked encoding");
            }
         }

         this.setCookieHeader();
         this.setRequests = true;
      }

      if (logger.isLoggable(PlatformLogger.Level.FINE)) {
         logger.fine(this.requests.toString());
      }

      this.http.writeRequests(this.requests, this.poster, this.streaming());
      if (this.ps.checkError()) {
         String var11 = this.http.getProxyHostUsed();
         int var12 = this.http.getProxyPortUsed();
         this.disconnectInternal();
         if (this.failedOnce) {
            throw new IOException("Error writing to server");
         }

         this.failedOnce = true;
         if (var11 != null) {
            this.setProxiedClient(this.url, var11, var12);
         } else {
            this.setNewClient(this.url);
         }

         this.ps = (PrintStream)this.http.getOutputStream();
         this.connected = true;
         this.responses = new MessageHeader();
         this.setRequests = false;
         this.writeRequests();
      }

   }

   private boolean checkSetHost() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         String var2 = var1.getClass().getName();
         if (var2.equals("sun.plugin2.applet.AWTAppletSecurityManager") || var2.equals("sun.plugin2.applet.FXAppletSecurityManager") || var2.equals("com.sun.javaws.security.JavaWebStartSecurity") || var2.equals("sun.plugin.security.ActivatorSecurityManager")) {
            byte var3 = -2;

            try {
               var1.checkConnect(this.url.toExternalForm(), var3);
            } catch (SecurityException var5) {
               return false;
            }
         }
      }

      return true;
   }

   private void checkURLFile() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         String var2 = var1.getClass().getName();
         if (var2.equals("sun.plugin2.applet.AWTAppletSecurityManager") || var2.equals("sun.plugin2.applet.FXAppletSecurityManager") || var2.equals("com.sun.javaws.security.JavaWebStartSecurity") || var2.equals("sun.plugin.security.ActivatorSecurityManager")) {
            byte var3 = -3;

            try {
               var1.checkConnect(this.url.toExternalForm(), var3);
            } catch (SecurityException var5) {
               throw new SecurityException("denied access outside a permitted URL subpath", var5);
            }
         }
      }

   }

   protected void setNewClient(URL var1) throws IOException {
      this.setNewClient(var1, false);
   }

   protected void setNewClient(URL var1, boolean var2) throws IOException {
      this.http = HttpClient.New(var1, (String)null, -1, var2, this.connectTimeout, this);
      this.http.setReadTimeout(this.readTimeout);
   }

   protected void setProxiedClient(URL var1, String var2, int var3) throws IOException {
      this.setProxiedClient(var1, var2, var3, false);
   }

   protected void setProxiedClient(URL var1, String var2, int var3, boolean var4) throws IOException {
      this.proxiedConnect(var1, var2, var3, var4);
   }

   protected void proxiedConnect(URL var1, String var2, int var3, boolean var4) throws IOException {
      this.http = HttpClient.New(var1, var2, var3, var4, this.connectTimeout, this);
      this.http.setReadTimeout(this.readTimeout);
   }

   protected HttpURLConnection(URL var1, Handler var2) throws IOException {
      this(var1, (Proxy)null, var2);
   }

   private static String checkHost(String var0) throws IOException {
      if (var0 != null && var0.indexOf(10) > -1) {
         throw new MalformedURLException("Illegal character in host");
      } else {
         return var0;
      }
   }

   public HttpURLConnection(URL var1, String var2, int var3) throws IOException {
      this(var1, new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(checkHost(var2), var3)));
   }

   public HttpURLConnection(URL var1, Proxy var2) throws IOException {
      this(var1, var2, new Handler());
   }

   private static URL checkURL(URL var0) throws IOException {
      if (var0 != null && var0.toExternalForm().indexOf(10) > -1) {
         throw new MalformedURLException("Illegal character in URL");
      } else {
         return var0;
      }
   }

   protected HttpURLConnection(URL var1, Proxy var2, Handler var3) throws IOException {
      super(checkURL(var1));
      this.ps = null;
      this.errorStream = null;
      this.setUserCookies = true;
      this.userCookies = null;
      this.userCookies2 = null;
      this.connecting = false;
      this.currentProxyCredentials = null;
      this.currentServerCredentials = null;
      this.needToCheck = true;
      this.doingNTLM2ndStage = false;
      this.doingNTLMp2ndStage = false;
      this.tryTransparentNTLMServer = true;
      this.tryTransparentNTLMProxy = true;
      this.useProxyResponseCode = false;
      this.inputStream = null;
      this.poster = null;
      this.setRequests = false;
      this.failedOnce = false;
      this.rememberedException = null;
      this.reuseClient = null;
      this.tunnelState = HttpURLConnection.TunnelState.NONE;
      this.connectTimeout = -1;
      this.readTimeout = -1;
      this.requestURI = null;
      this.cdata = new byte[128];
      this.requests = new MessageHeader();
      this.responses = new MessageHeader();
      this.userHeaders = new MessageHeader();
      this.handler = var3;
      this.instProxy = var2;
      if (this.instProxy instanceof ApplicationProxy) {
         try {
            this.cookieHandler = CookieHandler.getDefault();
         } catch (SecurityException var5) {
         }
      } else {
         this.cookieHandler = (CookieHandler)AccessController.doPrivileged(new PrivilegedAction<CookieHandler>() {
            public CookieHandler run() {
               return CookieHandler.getDefault();
            }
         });
      }

      this.cacheHandler = (ResponseCache)AccessController.doPrivileged(new PrivilegedAction<ResponseCache>() {
         public ResponseCache run() {
            return ResponseCache.getDefault();
         }
      });
   }

   /** @deprecated */
   @Deprecated
   public static void setDefaultAuthenticator(HttpAuthenticator var0) {
      defaultAuth = var0;
   }

   public static InputStream openConnectionCheckRedirects(URLConnection var0) throws IOException {
      int var2 = 0;

      boolean var1;
      InputStream var3;
      do {
         if (var0 instanceof HttpURLConnection) {
            ((HttpURLConnection)var0).setInstanceFollowRedirects(false);
         }

         var3 = var0.getInputStream();
         var1 = false;
         if (var0 instanceof HttpURLConnection) {
            HttpURLConnection var4 = (HttpURLConnection)var0;
            int var5 = var4.getResponseCode();
            if (var5 >= 300 && var5 <= 307 && var5 != 306 && var5 != 304) {
               URL var6 = var4.getURL();
               String var7 = var4.getHeaderField("Location");
               URL var8 = null;
               if (var7 != null) {
                  var8 = new URL(var6, var7);
               }

               var4.disconnect();
               if (var8 == null || !var6.getProtocol().equals(var8.getProtocol()) || var6.getPort() != var8.getPort() || !hostsEqual(var6, var8) || var2 >= 5) {
                  throw new SecurityException("illegal URL redirect");
               }

               var1 = true;
               var0 = var8.openConnection();
               ++var2;
            }
         }
      } while(var1);

      return var3;
   }

   private static boolean hostsEqual(URL var0, URL var1) {
      final String var2 = var0.getHost();
      final String var3 = var1.getHost();
      if (var2 == null) {
         return var3 == null;
      } else if (var3 == null) {
         return false;
      } else if (var2.equalsIgnoreCase(var3)) {
         return true;
      } else {
         final boolean[] var4 = new boolean[]{false};
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               try {
                  InetAddress var1 = InetAddress.getByName(var2);
                  InetAddress var2x = InetAddress.getByName(var3);
                  var4[0] = var1.equals(var2x);
               } catch (SecurityException | UnknownHostException var3x) {
               }

               return null;
            }
         });
         return var4[0];
      }
   }

   public void connect() throws IOException {
      synchronized(this) {
         this.connecting = true;
      }

      this.plainConnect();
   }

   private boolean checkReuseConnection() {
      if (this.connected) {
         return true;
      } else if (this.reuseClient != null) {
         this.http = this.reuseClient;
         this.http.setReadTimeout(this.getReadTimeout());
         this.http.reuse = false;
         this.reuseClient = null;
         this.connected = true;
         return true;
      } else {
         return false;
      }
   }

   private String getHostAndPort(URL var1) {
      String var2 = var1.getHost();
      final String var3 = var2;

      try {
         var2 = (String)AccessController.doPrivileged(new PrivilegedExceptionAction<String>() {
            public String run() throws IOException {
               InetAddress var1 = InetAddress.getByName(var3);
               return var1.getHostAddress();
            }
         });
      } catch (PrivilegedActionException var6) {
      }

      int var4 = var1.getPort();
      if (var4 == -1) {
         String var5 = var1.getProtocol();
         return "http".equals(var5) ? var2 + ":80" : var2 + ":443";
      } else {
         return var2 + ":" + Integer.toString(var4);
      }
   }

   protected void plainConnect() throws IOException {
      synchronized(this) {
         if (this.connected) {
            return;
         }
      }

      SocketPermission var1 = this.URLtoSocketPermission(this.url);
      if (var1 != null) {
         try {
            AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction)(new PrivilegedExceptionAction<Void>() {
               public Void run() throws IOException {
                  HttpURLConnection.this.plainConnect0();
                  return null;
               }
            }), (AccessControlContext)null, var1);
         } catch (PrivilegedActionException var3) {
            throw (IOException)var3.getException();
         }
      } else {
         this.plainConnect0();
      }

   }

   SocketPermission URLtoSocketPermission(URL var1) throws IOException {
      if (this.socketPermission != null) {
         return this.socketPermission;
      } else {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 == null) {
            return null;
         } else {
            SocketPermission var3 = new SocketPermission(this.getHostAndPort(var1), "connect");
            String var4 = this.getRequestMethod() + ":" + this.getUserSetHeaders().getHeaderNamesInList();
            String var5 = var1.getProtocol() + "://" + var1.getAuthority() + var1.getPath();
            URLPermission var6 = new URLPermission(var5, var4);

            try {
               var2.checkPermission(var6);
               this.socketPermission = var3;
               return this.socketPermission;
            } catch (SecurityException var8) {
               return null;
            }
         }
      }
   }

   protected void plainConnect0() throws IOException {
      if (this.cacheHandler != null && this.getUseCaches()) {
         try {
            URI var1 = ParseUtil.toURI(this.url);
            if (var1 != null) {
               this.cachedResponse = this.cacheHandler.get(var1, this.getRequestMethod(), this.getUserSetHeaders().getHeaders());
               if ("https".equalsIgnoreCase(var1.getScheme()) && !(this.cachedResponse instanceof SecureCacheResponse)) {
                  this.cachedResponse = null;
               }

               if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                  logger.finest("Cache Request for " + var1 + " / " + this.getRequestMethod());
                  logger.finest("From cache: " + (this.cachedResponse != null ? this.cachedResponse.toString() : "null"));
               }

               if (this.cachedResponse != null) {
                  this.cachedHeaders = this.mapToMessageHeader(this.cachedResponse.getHeaders());
                  this.cachedInputStream = this.cachedResponse.getBody();
               }
            }
         } catch (IOException var6) {
         }

         if (this.cachedHeaders != null && this.cachedInputStream != null) {
            this.connected = true;
            return;
         }

         this.cachedResponse = null;
      }

      try {
         if (this.instProxy != null) {
            if (!this.failedOnce) {
               this.http = this.getNewHttpClient(this.url, this.instProxy, this.connectTimeout);
               this.http.setReadTimeout(this.readTimeout);
            } else {
               this.http = this.getNewHttpClient(this.url, this.instProxy, this.connectTimeout, false);
               this.http.setReadTimeout(this.readTimeout);
            }
         } else {
            ProxySelector var9 = (ProxySelector)AccessController.doPrivileged(new PrivilegedAction<ProxySelector>() {
               public ProxySelector run() {
                  return ProxySelector.getDefault();
               }
            });
            if (var9 == null) {
               if (!this.failedOnce) {
                  this.http = this.getNewHttpClient(this.url, (Proxy)null, this.connectTimeout);
                  this.http.setReadTimeout(this.readTimeout);
               } else {
                  this.http = this.getNewHttpClient(this.url, (Proxy)null, this.connectTimeout, false);
                  this.http.setReadTimeout(this.readTimeout);
               }
            } else {
               URI var2 = ParseUtil.toURI(this.url);
               if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                  logger.finest("ProxySelector Request for " + var2);
               }

               Iterator var3 = var9.select(var2).iterator();

               while(var3.hasNext()) {
                  Proxy var4 = (Proxy)var3.next();

                  try {
                     if (!this.failedOnce) {
                        this.http = this.getNewHttpClient(this.url, var4, this.connectTimeout);
                        this.http.setReadTimeout(this.readTimeout);
                     } else {
                        this.http = this.getNewHttpClient(this.url, var4, this.connectTimeout, false);
                        this.http.setReadTimeout(this.readTimeout);
                     }

                     if (logger.isLoggable(PlatformLogger.Level.FINEST) && var4 != null) {
                        logger.finest("Proxy used: " + var4.toString());
                     }
                     break;
                  } catch (IOException var7) {
                     if (var4 == Proxy.NO_PROXY) {
                        throw var7;
                     }

                     var9.connectFailed(var2, var4.address(), var7);
                     if (!var3.hasNext()) {
                        this.http = this.getNewHttpClient(this.url, (Proxy)null, this.connectTimeout, false);
                        this.http.setReadTimeout(this.readTimeout);
                        break;
                     }
                  }
               }
            }
         }

         this.ps = (PrintStream)this.http.getOutputStream();
      } catch (IOException var8) {
         throw var8;
      }

      this.connected = true;
   }

   protected HttpClient getNewHttpClient(URL var1, Proxy var2, int var3) throws IOException {
      return HttpClient.New(var1, var2, var3, this);
   }

   protected HttpClient getNewHttpClient(URL var1, Proxy var2, int var3, boolean var4) throws IOException {
      return HttpClient.New(var1, var2, var3, var4, this);
   }

   private void expect100Continue() throws IOException {
      int var1 = this.http.getReadTimeout();
      boolean var2 = false;
      boolean var3 = false;
      if (var1 <= 0) {
         this.http.setReadTimeout(5000);
         var2 = true;
      }

      try {
         this.http.parseHTTP(this.responses, this.pi, this);
      } catch (SocketTimeoutException var8) {
         if (!var2) {
            throw var8;
         }

         var3 = true;
         this.http.setIgnoreContinue(true);
      }

      if (!var3) {
         String var4 = this.responses.getValue(0);
         if (var4 != null && var4.startsWith("HTTP/")) {
            String[] var5 = var4.split("\\s+");
            this.responseCode = -1;

            try {
               if (var5.length > 1) {
                  this.responseCode = Integer.parseInt(var5[1]);
               }
            } catch (NumberFormatException var7) {
            }
         }

         if (this.responseCode != 100) {
            throw new ProtocolException("Server rejected operation");
         }
      }

      this.http.setReadTimeout(var1);
      this.responseCode = -1;
      this.responses.reset();
   }

   public synchronized OutputStream getOutputStream() throws IOException {
      this.connecting = true;
      SocketPermission var1 = this.URLtoSocketPermission(this.url);
      if (var1 != null) {
         try {
            return (OutputStream)AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction)(new PrivilegedExceptionAction<OutputStream>() {
               public OutputStream run() throws IOException {
                  return HttpURLConnection.this.getOutputStream0();
               }
            }), (AccessControlContext)null, var1);
         } catch (PrivilegedActionException var3) {
            throw (IOException)var3.getException();
         }
      } else {
         return this.getOutputStream0();
      }
   }

   private synchronized OutputStream getOutputStream0() throws IOException {
      try {
         if (!this.doOutput) {
            throw new ProtocolException("cannot write to a URLConnection if doOutput=false - call setDoOutput(true)");
         } else {
            if (this.method.equals("GET")) {
               this.method = "POST";
            }

            if ("TRACE".equals(this.method) && "http".equals(this.url.getProtocol())) {
               throw new ProtocolException("HTTP method TRACE doesn't support output");
            } else if (this.inputStream != null) {
               throw new ProtocolException("Cannot write output after reading input.");
            } else {
               if (!this.checkReuseConnection()) {
                  this.connect();
               }

               boolean var1 = false;
               String var8 = this.requests.findValue("Expect");
               if ("100-Continue".equalsIgnoreCase(var8) && this.streaming()) {
                  this.http.setIgnoreContinue(false);
                  var1 = true;
               }

               if (this.streaming() && this.strOutputStream == null) {
                  this.writeRequests();
               }

               if (var1) {
                  this.expect100Continue();
               }

               this.ps = (PrintStream)this.http.getOutputStream();
               if (this.streaming()) {
                  if (this.strOutputStream == null) {
                     if (this.chunkLength != -1) {
                        this.strOutputStream = new HttpURLConnection.StreamingOutputStream(new ChunkedOutputStream(this.ps, this.chunkLength), -1L);
                     } else {
                        long var3 = 0L;
                        if (this.fixedContentLengthLong != -1L) {
                           var3 = this.fixedContentLengthLong;
                        } else if (this.fixedContentLength != -1) {
                           var3 = (long)this.fixedContentLength;
                        }

                        this.strOutputStream = new HttpURLConnection.StreamingOutputStream(this.ps, var3);
                     }
                  }

                  return this.strOutputStream;
               } else {
                  if (this.poster == null) {
                     this.poster = new PosterOutputStream();
                  }

                  return this.poster;
               }
            }
         }
      } catch (RuntimeException var5) {
         this.disconnectInternal();
         throw var5;
      } catch (ProtocolException var6) {
         int var2 = this.responseCode;
         this.disconnectInternal();
         this.responseCode = var2;
         throw var6;
      } catch (IOException var7) {
         this.disconnectInternal();
         throw var7;
      }
   }

   public boolean streaming() {
      return this.fixedContentLength != -1 || this.fixedContentLengthLong != -1L || this.chunkLength != -1;
   }

   private void setCookieHeader() throws IOException {
      if (this.cookieHandler != null) {
         int var2;
         synchronized(this) {
            if (this.setUserCookies) {
               var2 = this.requests.getKey("Cookie");
               if (var2 != -1) {
                  this.userCookies = this.requests.getValue(var2);
               }

               var2 = this.requests.getKey("Cookie2");
               if (var2 != -1) {
                  this.userCookies2 = this.requests.getValue(var2);
               }

               this.setUserCookies = false;
            }
         }

         this.requests.remove("Cookie");
         this.requests.remove("Cookie2");
         URI var1 = ParseUtil.toURI(this.url);
         if (var1 != null) {
            if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
               logger.finest("CookieHandler request for " + var1);
            }

            Map var12 = this.cookieHandler.get(var1, this.requests.getHeaders(EXCLUDE_HEADERS));
            if (!var12.isEmpty()) {
               if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                  logger.finest("Cookies retrieved: " + var12.toString());
               }

               Iterator var3 = var12.entrySet().iterator();

               label89:
               while(true) {
                  String var5;
                  List var6;
                  do {
                     do {
                        Map.Entry var4;
                        do {
                           if (!var3.hasNext()) {
                              break label89;
                           }

                           var4 = (Map.Entry)var3.next();
                           var5 = (String)var4.getKey();
                        } while(!"Cookie".equalsIgnoreCase(var5) && !"Cookie2".equalsIgnoreCase(var5));

                        var6 = (List)var4.getValue();
                     } while(var6 == null);
                  } while(var6.isEmpty());

                  StringBuilder var7 = new StringBuilder();
                  Iterator var8 = var6.iterator();

                  while(var8.hasNext()) {
                     String var9 = (String)var8.next();
                     var7.append(var9).append("; ");
                  }

                  try {
                     this.requests.add(var5, var7.substring(0, var7.length() - 2));
                  } catch (StringIndexOutOfBoundsException var10) {
                  }
               }
            }
         }

         if (this.userCookies != null) {
            if ((var2 = this.requests.getKey("Cookie")) != -1) {
               this.requests.set("Cookie", this.requests.getValue(var2) + ";" + this.userCookies);
            } else {
               this.requests.set("Cookie", this.userCookies);
            }
         }

         if (this.userCookies2 != null) {
            if ((var2 = this.requests.getKey("Cookie2")) != -1) {
               this.requests.set("Cookie2", this.requests.getValue(var2) + ";" + this.userCookies2);
            } else {
               this.requests.set("Cookie2", this.userCookies2);
            }
         }
      }

   }

   public synchronized InputStream getInputStream() throws IOException {
      this.connecting = true;
      SocketPermission var1 = this.URLtoSocketPermission(this.url);
      if (var1 != null) {
         try {
            return (InputStream)AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction)(new PrivilegedExceptionAction<InputStream>() {
               public InputStream run() throws IOException {
                  return HttpURLConnection.this.getInputStream0();
               }
            }), (AccessControlContext)null, var1);
         } catch (PrivilegedActionException var3) {
            throw (IOException)var3.getException();
         }
      } else {
         return this.getInputStream0();
      }
   }

   private synchronized InputStream getInputStream0() throws IOException {
      if (!this.doInput) {
         throw new ProtocolException("Cannot read from URLConnection if doInput=false (call setDoInput(true))");
      } else if (this.rememberedException != null) {
         if (this.rememberedException instanceof RuntimeException) {
            throw new RuntimeException(this.rememberedException);
         } else {
            throw this.getChainedException((IOException)this.rememberedException);
         }
      } else if (this.inputStream != null) {
         return this.inputStream;
      } else {
         if (this.streaming()) {
            if (this.strOutputStream == null) {
               this.getOutputStream();
            }

            this.strOutputStream.close();
            if (!this.strOutputStream.writtenOK()) {
               throw new IOException("Incomplete output stream");
            }
         }

         int var1 = 0;
         boolean var2 = false;
         long var3 = -1L;
         Object var5 = null;
         AuthenticationInfo var6 = null;
         AuthenticationHeader var7 = null;
         boolean var8 = false;
         boolean var9 = false;
         this.isUserServerAuth = this.requests.getKey("Authorization") != -1;
         this.isUserProxyAuth = this.requests.getKey("Proxy-Authorization") != -1;

         InputStream var45;
         try {
            int var33;
            while(true) {
               if (!this.checkReuseConnection()) {
                  this.connect();
               }

               if (this.cachedInputStream != null) {
                  InputStream var34 = this.cachedInputStream;
                  return var34;
               }

               boolean var10 = ProgressMonitor.getDefault().shouldMeterInput(this.url, this.method);
               if (var10) {
                  this.pi = new ProgressSource(this.url, this.method);
                  this.pi.beginTracking();
               }

               this.ps = (PrintStream)this.http.getOutputStream();
               if (!this.streaming()) {
                  this.writeRequests();
               }

               this.http.parseHTTP(this.responses, this.pi, this);
               if (logger.isLoggable(PlatformLogger.Level.FINE)) {
                  logger.fine(this.responses.toString());
               }

               boolean var35 = this.responses.filterNTLMResponses("WWW-Authenticate");
               boolean var12 = this.responses.filterNTLMResponses("Proxy-Authenticate");
               if ((var35 || var12) && logger.isLoggable(PlatformLogger.Level.FINE)) {
                  logger.fine(">>>> Headers are filtered");
                  logger.fine(this.responses.toString());
               }

               this.inputStream = this.http.getInputStream();
               var33 = this.getResponseCode();
               if (var33 == -1) {
                  this.disconnectInternal();
                  throw new IOException("Invalid Http response");
               }

               label763: {
                  boolean var13;
                  Iterator var14;
                  String var15;
                  if (var33 == 407) {
                     if (this.streaming()) {
                        this.disconnectInternal();
                        throw new HttpRetryException("cannot retry due to proxy authentication, in streaming mode", 407);
                     }

                     var13 = false;
                     var14 = this.responses.multiValueIterator("Proxy-Authenticate");

                     label711: {
                        do {
                           if (!var14.hasNext()) {
                              break label711;
                           }

                           var15 = ((String)var14.next()).trim();
                        } while(!var15.equalsIgnoreCase("Negotiate") && !var15.equalsIgnoreCase("Kerberos"));

                        if (!var9) {
                           var9 = true;
                        } else {
                           var13 = true;
                           this.doingNTLMp2ndStage = false;
                           var6 = null;
                        }
                     }

                     AuthenticationHeader var39 = new AuthenticationHeader("Proxy-Authenticate", this.responses, new HttpCallerInfo(this.url, this.http.getProxyHostUsed(), this.http.getProxyPortUsed()), var13, disabledProxyingSchemes);
                     if (this.doingNTLMp2ndStage) {
                        String var16 = this.responses.findValue("Proxy-Authenticate");
                        this.reset();
                        if (!var6.setHeaders(this, var39.headerParser(), var16)) {
                           this.disconnectInternal();
                           throw new IOException("Authentication failure");
                        }

                        if (var5 != null && var7 != null && !((AuthenticationInfo)var5).setHeaders(this, var7.headerParser(), var16)) {
                           this.disconnectInternal();
                           throw new IOException("Authentication failure");
                        }

                        this.authObj = null;
                        this.doingNTLMp2ndStage = false;
                        break label763;
                     }

                     var6 = this.resetProxyAuthentication(var6, var39);
                     if (var6 != null) {
                        ++var1;
                        this.disconnectInternal();
                        break label763;
                     }
                  } else {
                     var9 = false;
                     this.doingNTLMp2ndStage = false;
                     if (!this.isUserProxyAuth) {
                        this.requests.remove("Proxy-Authorization");
                     }
                  }

                  if (var6 != null) {
                     var6.addToCache();
                  }

                  if (var33 == 401) {
                     if (this.streaming()) {
                        this.disconnectInternal();
                        throw new HttpRetryException("cannot retry due to server authentication, in streaming mode", 401);
                     }

                     var13 = false;
                     var14 = this.responses.multiValueIterator("WWW-Authenticate");

                     label693: {
                        do {
                           if (!var14.hasNext()) {
                              break label693;
                           }

                           var15 = ((String)var14.next()).trim();
                        } while(!var15.equalsIgnoreCase("Negotiate") && !var15.equalsIgnoreCase("Kerberos"));

                        if (!var8) {
                           var8 = true;
                        } else {
                           var13 = true;
                           this.doingNTLM2ndStage = false;
                           var5 = null;
                        }
                     }

                     var7 = new AuthenticationHeader("WWW-Authenticate", this.responses, new HttpCallerInfo(this.url), var13);
                     var15 = var7.raw();
                     if (this.doingNTLM2ndStage) {
                        this.reset();
                        if (!((AuthenticationInfo)var5).setHeaders(this, (HeaderParser)null, var15)) {
                           this.disconnectWeb();
                           throw new IOException("Authentication failure");
                        }

                        this.doingNTLM2ndStage = false;
                        this.authObj = null;
                        this.setCookieHeader();
                        break label763;
                     }

                     if (var5 != null && ((AuthenticationInfo)var5).getAuthScheme() != AuthScheme.NTLM) {
                        if (((AuthenticationInfo)var5).isAuthorizationStale(var15)) {
                           this.disconnectWeb();
                           ++var1;
                           this.requests.set(((AuthenticationInfo)var5).getHeaderName(), ((AuthenticationInfo)var5).getHeaderValue(this.url, this.method));
                           this.currentServerCredentials = (AuthenticationInfo)var5;
                           this.setCookieHeader();
                           break label763;
                        }

                        ((AuthenticationInfo)var5).removeFromCache();
                     }

                     var5 = this.getServerAuthentication(var7);
                     this.currentServerCredentials = (AuthenticationInfo)var5;
                     if (var5 != null) {
                        this.disconnectWeb();
                        ++var1;
                        this.setCookieHeader();
                        break label763;
                     }
                  }

                  if (var5 != null) {
                     if (var5 instanceof DigestAuthentication && this.domain != null) {
                        DigestAuthentication var41 = (DigestAuthentication)var5;
                        StringTokenizer var37 = new StringTokenizer(this.domain, " ");
                        var15 = var41.realm;
                        PasswordAuthentication var42 = var41.pw;
                        this.digestparams = var41.params;

                        while(var37.hasMoreTokens()) {
                           String var17 = var37.nextToken();

                           try {
                              URL var18 = new URL(this.url, var17);
                              DigestAuthentication var19 = new DigestAuthentication(false, var18, var15, "Digest", var42, this.digestparams);
                              var19.addToCache();
                           } catch (Exception var29) {
                           }
                        }
                     } else {
                        if (var5 instanceof BasicAuthentication) {
                           String var38 = AuthenticationInfo.reducePath(this.url.getPath());
                           String var36 = ((AuthenticationInfo)var5).path;
                           if (!var36.startsWith(var38) || var38.length() >= var36.length()) {
                              var38 = BasicAuthentication.getRootPath(var36, var38);
                           }

                           BasicAuthentication var43 = (BasicAuthentication)((AuthenticationInfo)var5).clone();
                           ((AuthenticationInfo)var5).removeFromCache();
                           var43.path = var38;
                           var5 = var43;
                        }

                        ((AuthenticationInfo)var5).addToCache();
                     }
                  }

                  var8 = false;
                  var9 = false;
                  this.doingNTLMp2ndStage = false;
                  this.doingNTLM2ndStage = false;
                  if (!this.isUserServerAuth) {
                     this.requests.remove("Authorization");
                  }

                  if (!this.isUserProxyAuth) {
                     this.requests.remove("Proxy-Authorization");
                  }

                  if (var33 == 200) {
                     this.checkResponseCredentials(false);
                  } else {
                     this.needToCheck = false;
                  }

                  this.needToCheck = true;
                  if (!this.followRedirect()) {
                     try {
                        var3 = Long.parseLong(this.responses.findValue("content-length"));
                     } catch (Exception var28) {
                     }
                     break;
                  }

                  ++var1;
                  this.setCookieHeader();
               }

               if (var1 >= maxRedirects) {
                  throw new ProtocolException("Server redirected too many  times (" + var1 + ")");
               }
            }

            if (this.method.equals("HEAD") || var3 == 0L || var33 == 304 || var33 == 204) {
               if (this.pi != null) {
                  this.pi.finishTracking();
                  this.pi = null;
               }

               this.http.finished();
               this.http = null;
               this.inputStream = new EmptyInputStream();
               this.connected = false;
            }

            if ((var33 == 200 || var33 == 203 || var33 == 206 || var33 == 300 || var33 == 301 || var33 == 410) && this.cacheHandler != null && this.getUseCaches()) {
               URI var44 = ParseUtil.toURI(this.url);
               if (var44 != null) {
                  Object var40 = this;
                  if ("https".equalsIgnoreCase(var44.getScheme())) {
                     try {
                        var40 = (URLConnection)this.getClass().getField("httpsURLConnection").get(this);
                     } catch (NoSuchFieldException | IllegalAccessException var27) {
                     }
                  }

                  CacheRequest var46 = this.cacheHandler.put(var44, (URLConnection)var40);
                  if (var46 != null && this.http != null) {
                     this.http.setCacheRequest(var46);
                     this.inputStream = new HttpURLConnection.HttpInputStream(this.inputStream, var46);
                  }
               }
            }

            if (!(this.inputStream instanceof HttpURLConnection.HttpInputStream)) {
               this.inputStream = new HttpURLConnection.HttpInputStream(this.inputStream);
            }

            if (var33 >= 400) {
               if (var33 != 404 && var33 != 410) {
                  throw new IOException("Server returned HTTP response code: " + var33 + " for URL: " + this.url.toString());
               }

               throw new FileNotFoundException(this.url.toString());
            }

            this.poster = null;
            this.strOutputStream = null;
            var45 = this.inputStream;
         } catch (RuntimeException var30) {
            this.disconnectInternal();
            this.rememberedException = var30;
            throw var30;
         } catch (IOException var31) {
            this.rememberedException = var31;
            String var11 = this.responses.findValue("Transfer-Encoding");
            if (this.http != null && this.http.isKeepingAlive() && enableESBuffer && (var3 > 0L || var11 != null && var11.equalsIgnoreCase("chunked"))) {
               this.errorStream = HttpURLConnection.ErrorStream.getErrorStream(this.inputStream, var3, this.http);
            }

            throw var31;
         } finally {
            if (this.proxyAuthKey != null) {
               AuthenticationInfo.endAuthRequest(this.proxyAuthKey);
            }

            if (this.serverAuthKey != null) {
               AuthenticationInfo.endAuthRequest(this.serverAuthKey);
            }

         }

         return var45;
      }
   }

   private IOException getChainedException(final IOException var1) {
      try {
         final Object[] var2 = new Object[]{var1.getMessage()};
         IOException var3 = (IOException)AccessController.doPrivileged(new PrivilegedExceptionAction<IOException>() {
            public IOException run() throws Exception {
               return (IOException)var1.getClass().getConstructor(String.class).newInstance(var2);
            }
         });
         var3.initCause(var1);
         return var3;
      } catch (Exception var4) {
         return var1;
      }
   }

   public InputStream getErrorStream() {
      if (this.connected && this.responseCode >= 400) {
         if (this.errorStream != null) {
            return this.errorStream;
         }

         if (this.inputStream != null) {
            return this.inputStream;
         }
      }

      return null;
   }

   private AuthenticationInfo resetProxyAuthentication(AuthenticationInfo var1, AuthenticationHeader var2) throws IOException {
      if (var1 != null && var1.getAuthScheme() != AuthScheme.NTLM) {
         String var3 = var2.raw();
         if (var1.isAuthorizationStale(var3)) {
            String var4;
            if (var1 instanceof DigestAuthentication) {
               DigestAuthentication var5 = (DigestAuthentication)var1;
               if (this.tunnelState() == HttpURLConnection.TunnelState.SETUP) {
                  var4 = var5.getHeaderValue(connectRequestURI(this.url), HTTP_CONNECT);
               } else {
                  var4 = var5.getHeaderValue(this.getRequestURI(), this.method);
               }
            } else {
               var4 = var1.getHeaderValue(this.url, this.method);
            }

            this.requests.set(var1.getHeaderName(), var4);
            this.currentProxyCredentials = var1;
            return var1;
         }

         var1.removeFromCache();
      }

      var1 = this.getHttpProxyAuthentication(var2);
      this.currentProxyCredentials = var1;
      return var1;
   }

   HttpURLConnection.TunnelState tunnelState() {
      return this.tunnelState;
   }

   public void setTunnelState(HttpURLConnection.TunnelState var1) {
      this.tunnelState = var1;
   }

   public synchronized void doTunneling() throws IOException {
      int var1 = 0;
      String var2 = "";
      boolean var3 = false;
      AuthenticationInfo var4 = null;
      String var5 = null;
      int var6 = -1;
      MessageHeader var7 = this.requests;
      this.requests = new MessageHeader();
      boolean var8 = false;

      try {
         this.setTunnelState(HttpURLConnection.TunnelState.SETUP);

         int var17;
         label192: {
            while(true) {
               if (!this.checkReuseConnection()) {
                  this.proxiedConnect(this.url, var5, var6, false);
               }

               this.sendCONNECTRequest();
               this.responses.reset();
               this.http.parseHTTP(this.responses, (ProgressSource)null, this);
               if (logger.isLoggable(PlatformLogger.Level.FINE)) {
                  logger.fine(this.responses.toString());
               }

               if (this.responses.filterNTLMResponses("Proxy-Authenticate") && logger.isLoggable(PlatformLogger.Level.FINE)) {
                  logger.fine(">>>> Headers are filtered");
                  logger.fine(this.responses.toString());
               }

               var2 = this.responses.getValue(0);
               StringTokenizer var9 = new StringTokenizer(var2);
               var9.nextToken();
               var17 = Integer.parseInt(var9.nextToken().trim());
               if (var17 != 407) {
                  break;
               }

               boolean var10 = false;
               Iterator var11 = this.responses.multiValueIterator("Proxy-Authenticate");

               while(var11.hasNext()) {
                  String var12 = ((String)var11.next()).trim();
                  if (var12.equalsIgnoreCase("Negotiate") || var12.equalsIgnoreCase("Kerberos")) {
                     if (!var8) {
                        var8 = true;
                     } else {
                        var10 = true;
                        this.doingNTLMp2ndStage = false;
                        var4 = null;
                     }
                     break;
                  }
               }

               AuthenticationHeader var18 = new AuthenticationHeader("Proxy-Authenticate", this.responses, new HttpCallerInfo(this.url, this.http.getProxyHostUsed(), this.http.getProxyPortUsed()), var10, disabledTunnelingSchemes);
               if (!this.doingNTLMp2ndStage) {
                  var4 = this.resetProxyAuthentication(var4, var18);
                  if (var4 == null) {
                     break;
                  }

                  var5 = this.http.getProxyHostUsed();
                  var6 = this.http.getProxyPortUsed();
                  this.disconnectInternal();
                  ++var1;
               } else {
                  String var13 = this.responses.findValue("Proxy-Authenticate");
                  this.reset();
                  if (!var4.setHeaders(this, var18.headerParser(), var13)) {
                     this.disconnectInternal();
                     throw new IOException("Authentication failure");
                  }

                  this.authObj = null;
                  this.doingNTLMp2ndStage = false;
               }

               if (var1 >= maxRedirects) {
                  break label192;
               }
            }

            if (var4 != null) {
               var4.addToCache();
            }

            if (var17 == 200) {
               this.setTunnelState(HttpURLConnection.TunnelState.TUNNELING);
            } else {
               this.disconnectInternal();
               this.setTunnelState(HttpURLConnection.TunnelState.NONE);
            }
         }

         if (var1 >= maxRedirects || var17 != 200) {
            throw new IOException("Unable to tunnel through proxy. Proxy returns \"" + var2 + "\"");
         }
      } finally {
         if (this.proxyAuthKey != null) {
            AuthenticationInfo.endAuthRequest(this.proxyAuthKey);
         }

      }

      this.requests = var7;
      this.responses.reset();
   }

   static String connectRequestURI(URL var0) {
      String var1 = var0.getHost();
      int var2 = var0.getPort();
      var2 = var2 != -1 ? var2 : var0.getDefaultPort();
      return var1 + ":" + var2;
   }

   private void sendCONNECTRequest() throws IOException {
      int var1 = this.url.getPort();
      this.requests.set(0, HTTP_CONNECT + " " + connectRequestURI(this.url) + " " + "HTTP/1.1", (String)null);
      this.requests.setIfNotSet("User-Agent", userAgent);
      String var2 = this.url.getHost();
      if (var1 != -1 && var1 != this.url.getDefaultPort()) {
         var2 = var2 + ":" + var1;
      }

      this.requests.setIfNotSet("Host", var2);
      this.requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
      if (this.http.getHttpKeepAliveSet()) {
         this.requests.setIfNotSet("Proxy-Connection", "keep-alive");
      }

      this.setPreemptiveProxyAuthentication(this.requests);
      if (logger.isLoggable(PlatformLogger.Level.FINE)) {
         logger.fine(this.requests.toString());
      }

      this.http.writeRequests(this.requests, (PosterOutputStream)null);
   }

   private void setPreemptiveProxyAuthentication(MessageHeader var1) throws IOException {
      AuthenticationInfo var2 = AuthenticationInfo.getProxyAuth(this.http.getProxyHostUsed(), this.http.getProxyPortUsed());
      if (var2 != null && var2.supportsPreemptiveAuthorization()) {
         String var3;
         if (var2 instanceof DigestAuthentication) {
            DigestAuthentication var4 = (DigestAuthentication)var2;
            if (this.tunnelState() == HttpURLConnection.TunnelState.SETUP) {
               var3 = var4.getHeaderValue(connectRequestURI(this.url), HTTP_CONNECT);
            } else {
               var3 = var4.getHeaderValue(this.getRequestURI(), this.method);
            }
         } else {
            var3 = var2.getHeaderValue(this.url, this.method);
         }

         var1.set(var2.getHeaderName(), var3);
         this.currentProxyCredentials = var2;
      }

   }

   private AuthenticationInfo getHttpProxyAuthentication(AuthenticationHeader var1) {
      Object var2 = null;
      String var3 = var1.raw();
      final String var4 = this.http.getProxyHostUsed();
      int var5 = this.http.getProxyPortUsed();
      if (var4 != null && var1.isPresent()) {
         HeaderParser var6 = var1.headerParser();
         String var7 = var6.findValue("realm");
         String var8 = var1.scheme();
         AuthScheme var9 = AuthScheme.UNKNOWN;
         if ("basic".equalsIgnoreCase(var8)) {
            var9 = AuthScheme.BASIC;
         } else if ("digest".equalsIgnoreCase(var8)) {
            var9 = AuthScheme.DIGEST;
         } else if ("ntlm".equalsIgnoreCase(var8)) {
            var9 = AuthScheme.NTLM;
            this.doingNTLMp2ndStage = true;
         } else if ("Kerberos".equalsIgnoreCase(var8)) {
            var9 = AuthScheme.KERBEROS;
            this.doingNTLMp2ndStage = true;
         } else if ("Negotiate".equalsIgnoreCase(var8)) {
            var9 = AuthScheme.NEGOTIATE;
            this.doingNTLMp2ndStage = true;
         }

         if (var7 == null) {
            var7 = "";
         }

         this.proxyAuthKey = AuthenticationInfo.getProxyAuthKey(var4, var5, var7, var9);
         var2 = AuthenticationInfo.getProxyAuth(this.proxyAuthKey);
         if (var2 == null) {
            PasswordAuthentication var11;
            switch(var9) {
            case BASIC:
               InetAddress var10 = null;

               try {
                  var10 = (InetAddress)AccessController.doPrivileged(new PrivilegedExceptionAction<InetAddress>() {
                     public InetAddress run() throws UnknownHostException {
                        return InetAddress.getByName(var4);
                     }
                  });
               } catch (PrivilegedActionException var14) {
               }

               var11 = privilegedRequestPasswordAuthentication(var4, var10, var5, "http", var7, var8, this.url, Authenticator.RequestorType.PROXY);
               if (var11 != null) {
                  var2 = new BasicAuthentication(true, var4, var5, var7, var11);
               }
               break;
            case DIGEST:
               var11 = privilegedRequestPasswordAuthentication(var4, (InetAddress)null, var5, this.url.getProtocol(), var7, var8, this.url, Authenticator.RequestorType.PROXY);
               if (var11 != null) {
                  DigestAuthentication.Parameters var12 = new DigestAuthentication.Parameters();
                  var2 = new DigestAuthentication(true, var4, var5, var7, var8, var11, var12);
               }
               break;
            case NTLM:
               if (NTLMAuthenticationProxy.supported) {
                  if (this.tryTransparentNTLMProxy) {
                     this.tryTransparentNTLMProxy = NTLMAuthenticationProxy.supportsTransparentAuth;
                     if (this.tryTransparentNTLMProxy && this.useProxyResponseCode) {
                        this.tryTransparentNTLMProxy = false;
                     }
                  }

                  var11 = null;
                  if (this.tryTransparentNTLMProxy) {
                     logger.finest("Trying Transparent NTLM authentication");
                  } else {
                     var11 = privilegedRequestPasswordAuthentication(var4, (InetAddress)null, var5, this.url.getProtocol(), "", var8, this.url, Authenticator.RequestorType.PROXY);
                  }

                  if (this.tryTransparentNTLMProxy || !this.tryTransparentNTLMProxy && var11 != null) {
                     var2 = NTLMAuthenticationProxy.proxy.create(true, var4, var5, var11);
                  }

                  this.tryTransparentNTLMProxy = false;
               }
               break;
            case NEGOTIATE:
               var2 = new NegotiateAuthentication(new HttpCallerInfo(var1.getHttpCallerInfo(), "Negotiate"));
               break;
            case KERBEROS:
               var2 = new NegotiateAuthentication(new HttpCallerInfo(var1.getHttpCallerInfo(), "Kerberos"));
               break;
            case UNKNOWN:
               if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                  logger.finest("Unknown/Unsupported authentication scheme: " + var8);
               }
            default:
               throw new AssertionError("should not reach here");
            }
         }

         if (var2 == null && defaultAuth != null && defaultAuth.schemeSupported(var8)) {
            try {
               URL var15 = new URL("http", var4, var5, "/");
               String var16 = defaultAuth.authString(var15, var8, var7);
               if (var16 != null) {
                  var2 = new BasicAuthentication(true, var4, var5, var7, var16);
               }
            } catch (MalformedURLException var13) {
            }
         }

         if (var2 != null && !((AuthenticationInfo)var2).setHeaders(this, var6, var3)) {
            var2 = null;
         }
      }

      if (logger.isLoggable(PlatformLogger.Level.FINER)) {
         logger.finer("Proxy Authentication for " + var1.toString() + " returned " + (var2 != null ? var2.toString() : "null"));
      }

      return (AuthenticationInfo)var2;
   }

   private AuthenticationInfo getServerAuthentication(AuthenticationHeader var1) {
      Object var2 = null;
      String var3 = var1.raw();
      if (var1.isPresent()) {
         HeaderParser var4 = var1.headerParser();
         String var5 = var4.findValue("realm");
         String var6 = var1.scheme();
         AuthScheme var7 = AuthScheme.UNKNOWN;
         if ("basic".equalsIgnoreCase(var6)) {
            var7 = AuthScheme.BASIC;
         } else if ("digest".equalsIgnoreCase(var6)) {
            var7 = AuthScheme.DIGEST;
         } else if ("ntlm".equalsIgnoreCase(var6)) {
            var7 = AuthScheme.NTLM;
            this.doingNTLM2ndStage = true;
         } else if ("Kerberos".equalsIgnoreCase(var6)) {
            var7 = AuthScheme.KERBEROS;
            this.doingNTLM2ndStage = true;
         } else if ("Negotiate".equalsIgnoreCase(var6)) {
            var7 = AuthScheme.NEGOTIATE;
            this.doingNTLM2ndStage = true;
         }

         this.domain = var4.findValue("domain");
         if (var5 == null) {
            var5 = "";
         }

         this.serverAuthKey = AuthenticationInfo.getServerAuthKey(this.url, var5, var7);
         var2 = AuthenticationInfo.getServerAuth(this.serverAuthKey);
         InetAddress var8 = null;
         if (var2 == null) {
            try {
               var8 = InetAddress.getByName(this.url.getHost());
            } catch (UnknownHostException var14) {
            }
         }

         int var9 = this.url.getPort();
         if (var9 == -1) {
            var9 = this.url.getDefaultPort();
         }

         if (var2 == null) {
            PasswordAuthentication var10;
            switch(var7) {
            case BASIC:
               var10 = privilegedRequestPasswordAuthentication(this.url.getHost(), var8, var9, this.url.getProtocol(), var5, var6, this.url, Authenticator.RequestorType.SERVER);
               if (var10 != null) {
                  var2 = new BasicAuthentication(false, this.url, var5, var10);
               }
               break;
            case DIGEST:
               var10 = privilegedRequestPasswordAuthentication(this.url.getHost(), var8, var9, this.url.getProtocol(), var5, var6, this.url, Authenticator.RequestorType.SERVER);
               if (var10 != null) {
                  this.digestparams = new DigestAuthentication.Parameters();
                  var2 = new DigestAuthentication(false, this.url, var5, var6, var10, this.digestparams);
               }
               break;
            case NTLM:
               if (NTLMAuthenticationProxy.supported) {
                  URL var11;
                  try {
                     var11 = new URL(this.url, "/");
                  } catch (Exception var13) {
                     var11 = this.url;
                  }

                  if (this.tryTransparentNTLMServer) {
                     this.tryTransparentNTLMServer = NTLMAuthenticationProxy.supportsTransparentAuth;
                     if (this.tryTransparentNTLMServer) {
                        this.tryTransparentNTLMServer = NTLMAuthenticationProxy.isTrustedSite(this.url);
                     }
                  }

                  var10 = null;
                  if (this.tryTransparentNTLMServer) {
                     logger.finest("Trying Transparent NTLM authentication");
                  } else {
                     var10 = privilegedRequestPasswordAuthentication(this.url.getHost(), var8, var9, this.url.getProtocol(), "", var6, this.url, Authenticator.RequestorType.SERVER);
                  }

                  if (this.tryTransparentNTLMServer || !this.tryTransparentNTLMServer && var10 != null) {
                     var2 = NTLMAuthenticationProxy.proxy.create(false, var11, var10);
                  }

                  this.tryTransparentNTLMServer = false;
               }
               break;
            case NEGOTIATE:
               var2 = new NegotiateAuthentication(new HttpCallerInfo(var1.getHttpCallerInfo(), "Negotiate"));
               break;
            case KERBEROS:
               var2 = new NegotiateAuthentication(new HttpCallerInfo(var1.getHttpCallerInfo(), "Kerberos"));
               break;
            case UNKNOWN:
               if (logger.isLoggable(PlatformLogger.Level.FINEST)) {
                  logger.finest("Unknown/Unsupported authentication scheme: " + var6);
               }
            default:
               throw new AssertionError("should not reach here");
            }
         }

         if (var2 == null && defaultAuth != null && defaultAuth.schemeSupported(var6)) {
            String var15 = defaultAuth.authString(this.url, var6, var5);
            if (var15 != null) {
               var2 = new BasicAuthentication(false, this.url, var5, var15);
            }
         }

         if (var2 != null && !((AuthenticationInfo)var2).setHeaders(this, var4, var3)) {
            var2 = null;
         }
      }

      if (logger.isLoggable(PlatformLogger.Level.FINER)) {
         logger.finer("Server Authentication for " + var1.toString() + " returned " + (var2 != null ? var2.toString() : "null"));
      }

      return (AuthenticationInfo)var2;
   }

   private void checkResponseCredentials(boolean var1) throws IOException {
      try {
         if (this.needToCheck) {
            String var2;
            DigestAuthentication var3;
            if (validateProxy && this.currentProxyCredentials != null && this.currentProxyCredentials instanceof DigestAuthentication) {
               var2 = this.responses.findValue("Proxy-Authentication-Info");
               if (var1 || var2 != null) {
                  var3 = (DigestAuthentication)this.currentProxyCredentials;
                  var3.checkResponse(var2, this.method, this.getRequestURI());
                  this.currentProxyCredentials = null;
               }
            }

            if (validateServer && this.currentServerCredentials != null && this.currentServerCredentials instanceof DigestAuthentication) {
               var2 = this.responses.findValue("Authentication-Info");
               if (var1 || var2 != null) {
                  var3 = (DigestAuthentication)this.currentServerCredentials;
                  var3.checkResponse(var2, this.method, this.url);
                  this.currentServerCredentials = null;
               }
            }

            if (this.currentServerCredentials == null && this.currentProxyCredentials == null) {
               this.needToCheck = false;
            }

         }
      } catch (IOException var4) {
         this.disconnectInternal();
         this.connected = false;
         throw var4;
      }
   }

   String getRequestURI() throws IOException {
      if (this.requestURI == null) {
         this.requestURI = this.http.getURLFile();
      }

      return this.requestURI;
   }

   private boolean followRedirect() throws IOException {
      if (!this.getInstanceFollowRedirects()) {
         return false;
      } else {
         final int var1 = this.getResponseCode();
         if (var1 >= 300 && var1 <= 307 && var1 != 306 && var1 != 304) {
            final String var2 = this.getHeaderField("Location");
            if (var2 == null) {
               return false;
            } else {
               URL var3;
               try {
                  var3 = new URL(var2);
                  if (!this.url.getProtocol().equalsIgnoreCase(var3.getProtocol())) {
                     return false;
                  }
               } catch (MalformedURLException var8) {
                  var3 = new URL(this.url, var2);
               }

               final URL var4 = var3;
               this.socketPermission = null;
               SocketPermission var5 = this.URLtoSocketPermission(var3);
               if (var5 != null) {
                  try {
                     return (Boolean)AccessController.doPrivilegedWithCombiner((PrivilegedExceptionAction)(new PrivilegedExceptionAction<Boolean>() {
                        public Boolean run() throws IOException {
                           return HttpURLConnection.this.followRedirect0(var2, var1, var4);
                        }
                     }), (AccessControlContext)null, var5);
                  } catch (PrivilegedActionException var7) {
                     throw (IOException)var7.getException();
                  }
               } else {
                  return this.followRedirect0(var2, var1, var3);
               }
            }
         } else {
            return false;
         }
      }
   }

   private boolean followRedirect0(String var1, int var2, URL var3) throws IOException {
      this.disconnectInternal();
      if (this.streaming()) {
         throw new HttpRetryException("cannot retry due to redirection, in streaming mode", var2, var1);
      } else {
         if (logger.isLoggable(PlatformLogger.Level.FINE)) {
            logger.fine("Redirected from " + this.url + " to " + var3);
         }

         this.responses = new MessageHeader();
         if (var2 == 305) {
            String var4 = var3.getHost();
            int var5 = var3.getPort();
            SecurityManager var6 = System.getSecurityManager();
            if (var6 != null) {
               var6.checkConnect(var4, var5);
            }

            this.setProxiedClient(this.url, var4, var5);
            this.requests.set(0, this.method + " " + this.getRequestURI() + " " + "HTTP/1.1", (String)null);
            this.connected = true;
            this.useProxyResponseCode = true;
         } else {
            this.url = var3;
            this.requestURI = null;
            if (this.method.equals("POST") && !Boolean.getBoolean("http.strictPostRedirect") && var2 != 307) {
               this.requests = new MessageHeader();
               this.setRequests = false;
               super.setRequestMethod("GET");
               this.poster = null;
               if (!this.checkReuseConnection()) {
                  this.connect();
               }
            } else {
               if (!this.checkReuseConnection()) {
                  this.connect();
               }

               if (this.http != null) {
                  this.requests.set(0, this.method + " " + this.getRequestURI() + " " + "HTTP/1.1", (String)null);
                  int var7 = this.url.getPort();
                  String var8 = this.url.getHost();
                  if (var7 != -1 && var7 != this.url.getDefaultPort()) {
                     var8 = var8 + ":" + var7;
                  }

                  this.requests.set("Host", var8);
               }
            }
         }

         return true;
      }
   }

   private void reset() throws IOException {
      this.http.reuse = true;
      this.reuseClient = this.http;
      InputStream var1 = this.http.getInputStream();
      if (!this.method.equals("HEAD")) {
         try {
            if (!(var1 instanceof ChunkedInputStream) && !(var1 instanceof MeteredStream)) {
               long var2 = 0L;
               boolean var4 = false;
               String var5 = this.responses.findValue("Content-Length");
               if (var5 != null) {
                  try {
                     var2 = Long.parseLong(var5);
                  } catch (NumberFormatException var9) {
                     var2 = 0L;
                  }
               }

               int var11;
               for(long var6 = 0L; var6 < var2 && (var11 = var1.read(this.cdata)) != -1; var6 += (long)var11) {
               }
            } else {
               while(var1.read(this.cdata) > 0) {
               }
            }
         } catch (IOException var10) {
            this.http.reuse = false;
            this.reuseClient = null;
            this.disconnectInternal();
            return;
         }

         try {
            if (var1 instanceof MeteredStream) {
               var1.close();
            }
         } catch (IOException var8) {
         }
      }

      this.responseCode = -1;
      this.responses = new MessageHeader();
      this.connected = false;
   }

   private void disconnectWeb() throws IOException {
      if (this.usingProxy() && this.http.isKeepingAlive()) {
         this.responseCode = -1;
         this.reset();
      } else {
         this.disconnectInternal();
      }

   }

   private void disconnectInternal() {
      this.responseCode = -1;
      this.inputStream = null;
      if (this.pi != null) {
         this.pi.finishTracking();
         this.pi = null;
      }

      if (this.http != null) {
         this.http.closeServer();
         this.http = null;
         this.connected = false;
      }

   }

   public void disconnect() {
      this.responseCode = -1;
      if (this.pi != null) {
         this.pi.finishTracking();
         this.pi = null;
      }

      if (this.http != null) {
         if (this.inputStream != null) {
            HttpClient var1 = this.http;
            boolean var2 = var1.isKeepingAlive();

            try {
               this.inputStream.close();
            } catch (IOException var4) {
            }

            if (var2) {
               var1.closeIdleConnection();
            }
         } else {
            this.http.setDoNotRetry(true);
            this.http.closeServer();
         }

         this.http = null;
         this.connected = false;
      }

      this.cachedInputStream = null;
      if (this.cachedHeaders != null) {
         this.cachedHeaders.reset();
      }

   }

   public boolean usingProxy() {
      if (this.http != null) {
         return this.http.getProxyHostUsed() != null;
      } else {
         return false;
      }
   }

   private String filterHeaderField(String var1, String var2) {
      if (var2 == null) {
         return null;
      } else if (!"set-cookie".equalsIgnoreCase(var1) && !"set-cookie2".equalsIgnoreCase(var1)) {
         return var2;
      } else if (this.cookieHandler != null && var2.length() != 0) {
         JavaNetHttpCookieAccess var3 = SharedSecrets.getJavaNetHttpCookieAccess();
         StringBuilder var4 = new StringBuilder();
         List var5 = var3.parse(var2);
         boolean var6 = false;
         Iterator var7 = var5.iterator();

         while(var7.hasNext()) {
            HttpCookie var8 = (HttpCookie)var7.next();
            if (!var8.isHttpOnly()) {
               if (var6) {
                  var4.append(',');
               }

               var4.append(var3.header(var8));
               var6 = true;
            }
         }

         return var4.length() == 0 ? "" : var4.toString();
      } else {
         return var2;
      }
   }

   private Map<String, List<String>> getFilteredHeaderFields() {
      if (this.filteredHeaders != null) {
         return this.filteredHeaders;
      } else {
         HashMap var2 = new HashMap();
         Map var1;
         if (this.cachedHeaders != null) {
            var1 = this.cachedHeaders.getHeaders();
         } else {
            var1 = this.responses.getHeaders();
         }

         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            String var5 = (String)var4.getKey();
            List var6 = (List)var4.getValue();
            ArrayList var7 = new ArrayList();
            Iterator var8 = var6.iterator();

            while(var8.hasNext()) {
               String var9 = (String)var8.next();
               String var10 = this.filterHeaderField(var5, var9);
               if (var10 != null) {
                  var7.add(var10);
               }
            }

            if (!var7.isEmpty()) {
               var2.put(var5, Collections.unmodifiableList(var7));
            }
         }

         return this.filteredHeaders = Collections.unmodifiableMap(var2);
      }
   }

   public String getHeaderField(String var1) {
      try {
         this.getInputStream();
      } catch (IOException var3) {
      }

      return this.cachedHeaders != null ? this.filterHeaderField(var1, this.cachedHeaders.findValue(var1)) : this.filterHeaderField(var1, this.responses.findValue(var1));
   }

   public Map<String, List<String>> getHeaderFields() {
      try {
         this.getInputStream();
      } catch (IOException var2) {
      }

      return this.getFilteredHeaderFields();
   }

   public String getHeaderField(int var1) {
      try {
         this.getInputStream();
      } catch (IOException var3) {
      }

      return this.cachedHeaders != null ? this.filterHeaderField(this.cachedHeaders.getKey(var1), this.cachedHeaders.getValue(var1)) : this.filterHeaderField(this.responses.getKey(var1), this.responses.getValue(var1));
   }

   public String getHeaderFieldKey(int var1) {
      try {
         this.getInputStream();
      } catch (IOException var3) {
      }

      return this.cachedHeaders != null ? this.cachedHeaders.getKey(var1) : this.responses.getKey(var1);
   }

   public synchronized void setRequestProperty(String var1, String var2) {
      if (!this.connected && !this.connecting) {
         if (var1 == null) {
            throw new NullPointerException("key is null");
         } else {
            if (this.isExternalMessageHeaderAllowed(var1, var2)) {
               this.requests.set(var1, var2);
               if (!var1.equalsIgnoreCase("Content-Type")) {
                  this.userHeaders.set(var1, var2);
               }
            }

         }
      } else {
         throw new IllegalStateException("Already connected");
      }
   }

   MessageHeader getUserSetHeaders() {
      return this.userHeaders;
   }

   public synchronized void addRequestProperty(String var1, String var2) {
      if (!this.connected && !this.connecting) {
         if (var1 == null) {
            throw new NullPointerException("key is null");
         } else {
            if (this.isExternalMessageHeaderAllowed(var1, var2)) {
               this.requests.add(var1, var2);
               if (!var1.equalsIgnoreCase("Content-Type")) {
                  this.userHeaders.add(var1, var2);
               }
            }

         }
      } else {
         throw new IllegalStateException("Already connected");
      }
   }

   public void setAuthenticationProperty(String var1, String var2) {
      this.checkMessageHeader(var1, var2);
      this.requests.set(var1, var2);
   }

   public synchronized String getRequestProperty(String var1) {
      if (var1 == null) {
         return null;
      } else {
         for(int var2 = 0; var2 < EXCLUDE_HEADERS.length; ++var2) {
            if (var1.equalsIgnoreCase(EXCLUDE_HEADERS[var2])) {
               return null;
            }
         }

         if (!this.setUserCookies) {
            if (var1.equalsIgnoreCase("Cookie")) {
               return this.userCookies;
            }

            if (var1.equalsIgnoreCase("Cookie2")) {
               return this.userCookies2;
            }
         }

         return this.requests.findValue(var1);
      }
   }

   public synchronized Map<String, List<String>> getRequestProperties() {
      if (this.connected) {
         throw new IllegalStateException("Already connected");
      } else if (this.setUserCookies) {
         return this.requests.getHeaders(EXCLUDE_HEADERS);
      } else {
         HashMap var1 = null;
         if (this.userCookies != null || this.userCookies2 != null) {
            var1 = new HashMap();
            if (this.userCookies != null) {
               var1.put("Cookie", Arrays.asList(this.userCookies));
            }

            if (this.userCookies2 != null) {
               var1.put("Cookie2", Arrays.asList(this.userCookies2));
            }
         }

         return this.requests.filterAndAddHeaders(EXCLUDE_HEADERS2, var1);
      }
   }

   public void setConnectTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeouts can't be negative");
      } else {
         this.connectTimeout = var1;
      }
   }

   public int getConnectTimeout() {
      return this.connectTimeout < 0 ? 0 : this.connectTimeout;
   }

   public void setReadTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeouts can't be negative");
      } else {
         this.readTimeout = var1;
      }
   }

   public int getReadTimeout() {
      return this.readTimeout < 0 ? 0 : this.readTimeout;
   }

   public CookieHandler getCookieHandler() {
      return this.cookieHandler;
   }

   String getMethod() {
      return this.method;
   }

   private MessageHeader mapToMessageHeader(Map<String, List<String>> var1) {
      MessageHeader var2 = new MessageHeader();
      if (var1 != null && !var1.isEmpty()) {
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            String var5 = (String)var4.getKey();
            List var6 = (List)var4.getValue();
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               if (var5 == null) {
                  var2.prepend(var5, var8);
               } else {
                  var2.add(var5, var8);
               }
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("http.agent")));
      if (var0 == null) {
         var0 = "Java/" + version;
      } else {
         var0 = var0 + " Java/" + version;
      }

      userAgent = var0;
      String var1 = getNetProperty("jdk.http.auth.tunneling.disabledSchemes");
      disabledTunnelingSchemes = schemesListToSet(var1);
      var1 = getNetProperty("jdk.http.auth.proxying.disabledSchemes");
      disabledProxyingSchemes = schemesListToSet(var1);
      validateProxy = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("http.auth.digest.validateProxy")));
      validateServer = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("http.auth.digest.validateServer")));
      enableESBuffer = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.net.http.errorstream.enableBuffering")));
      timeout4ESBuffer = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("sun.net.http.errorstream.timeout", 300)));
      if (timeout4ESBuffer <= 0) {
         timeout4ESBuffer = 300;
      }

      bufSize4ES = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("sun.net.http.errorstream.bufferSize", 4096)));
      if (bufSize4ES <= 0) {
         bufSize4ES = 4096;
      }

      allowRestrictedHeaders = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("sun.net.http.allowRestrictedHeaders")));
      if (!allowRestrictedHeaders) {
         restrictedHeaderSet = new HashSet(restrictedHeaders.length);

         for(int var2 = 0; var2 < restrictedHeaders.length; ++var2) {
            restrictedHeaderSet.add(restrictedHeaders[var2].toLowerCase());
         }
      } else {
         restrictedHeaderSet = null;
      }

      EXCLUDE_HEADERS = new String[]{"Proxy-Authorization", "Authorization"};
      EXCLUDE_HEADERS2 = new String[]{"Proxy-Authorization", "Authorization", "Cookie", "Cookie2"};
      logger = PlatformLogger.getLogger("sun.net.www.protocol.http.HttpURLConnection");
   }

   static class ErrorStream extends InputStream {
      ByteBuffer buffer;
      InputStream is;

      private ErrorStream(ByteBuffer var1) {
         this.buffer = var1;
         this.is = null;
      }

      private ErrorStream(ByteBuffer var1, InputStream var2) {
         this.buffer = var1;
         this.is = var2;
      }

      public static InputStream getErrorStream(InputStream var0, long var1, HttpClient var3) {
         if (var1 == 0L) {
            return null;
         } else {
            try {
               int var4 = var3.getReadTimeout();
               var3.setReadTimeout(HttpURLConnection.timeout4ESBuffer / 5);
               long var5 = 0L;
               boolean var7 = false;
               if (var1 < 0L) {
                  var5 = (long)HttpURLConnection.bufSize4ES;
                  var7 = true;
               } else {
                  var5 = var1;
               }

               if (var5 > (long)HttpURLConnection.bufSize4ES) {
                  return null;
               } else {
                  int var8 = (int)var5;
                  byte[] var9 = new byte[var8];
                  int var10 = 0;
                  int var11 = 0;
                  int var12 = 0;

                  do {
                     try {
                        var12 = var0.read(var9, var10, var9.length - var10);
                        if (var12 < 0) {
                           if (!var7) {
                              throw new IOException("the server closes before sending " + var1 + " bytes of data");
                           }
                           break;
                        }

                        var10 += var12;
                     } catch (SocketTimeoutException var14) {
                        var11 += HttpURLConnection.timeout4ESBuffer / 5;
                     }
                  } while(var10 < var8 && var11 < HttpURLConnection.timeout4ESBuffer);

                  var3.setReadTimeout(var4);
                  if (var10 == 0) {
                     return null;
                  } else if (((long)var10 != var5 || var7) && (!var7 || var12 >= 0)) {
                     return new HttpURLConnection.ErrorStream(ByteBuffer.wrap(var9, 0, var10), var0);
                  } else {
                     var0.close();
                     return new HttpURLConnection.ErrorStream(ByteBuffer.wrap(var9, 0, var10));
                  }
               }
            } catch (IOException var15) {
               return null;
            }
         }
      }

      public int available() throws IOException {
         return this.is == null ? this.buffer.remaining() : this.buffer.remaining() + this.is.available();
      }

      public int read() throws IOException {
         byte[] var1 = new byte[1];
         int var2 = this.read(var1);
         return var2 == -1 ? var2 : var1[0] & 255;
      }

      public int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         int var4 = this.buffer.remaining();
         if (var4 > 0) {
            int var5 = var4 < var3 ? var4 : var3;
            this.buffer.get(var1, var2, var5);
            return var5;
         } else {
            return this.is == null ? -1 : this.is.read(var1, var2, var3);
         }
      }

      public void close() throws IOException {
         this.buffer = null;
         if (this.is != null) {
            this.is.close();
         }

      }
   }

   class StreamingOutputStream extends FilterOutputStream {
      long expected;
      long written;
      boolean closed;
      boolean error;
      IOException errorExcp;

      StreamingOutputStream(OutputStream var2, long var3) {
         super(var2);
         this.expected = var3;
         this.written = 0L;
         this.closed = false;
         this.error = false;
      }

      public void write(int var1) throws IOException {
         this.checkError();
         ++this.written;
         if (this.expected != -1L && this.written > this.expected) {
            throw new IOException("too many bytes written");
         } else {
            this.out.write(var1);
         }
      }

      public void write(byte[] var1) throws IOException {
         this.write(var1, 0, var1.length);
      }

      public void write(byte[] var1, int var2, int var3) throws IOException {
         this.checkError();
         this.written += (long)var3;
         if (this.expected != -1L && this.written > this.expected) {
            this.out.close();
            throw new IOException("too many bytes written");
         } else {
            this.out.write(var1, var2, var3);
         }
      }

      void checkError() throws IOException {
         if (this.closed) {
            throw new IOException("Stream is closed");
         } else if (this.error) {
            throw this.errorExcp;
         } else if (((PrintStream)this.out).checkError()) {
            throw new IOException("Error writing request body to server");
         }
      }

      boolean writtenOK() {
         return this.closed && !this.error;
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            if (this.expected != -1L) {
               if (this.written != this.expected) {
                  this.error = true;
                  this.errorExcp = new IOException("insufficient data written");
                  this.out.close();
                  throw this.errorExcp;
               }

               super.flush();
            } else {
               super.close();
               OutputStream var1 = HttpURLConnection.this.http.getOutputStream();
               var1.write(13);
               var1.write(10);
               var1.flush();
            }

         }
      }
   }

   class HttpInputStream extends FilterInputStream {
      private CacheRequest cacheRequest;
      private OutputStream outputStream;
      private boolean marked = false;
      private int inCache = 0;
      private int markCount = 0;
      private boolean closed;
      private byte[] skipBuffer;
      private static final int SKIP_BUFFER_SIZE = 8096;

      public HttpInputStream(InputStream var2) {
         super(var2);
         this.cacheRequest = null;
         this.outputStream = null;
      }

      public HttpInputStream(InputStream var2, CacheRequest var3) {
         super(var2);
         this.cacheRequest = var3;

         try {
            this.outputStream = var3.getBody();
         } catch (IOException var5) {
            this.cacheRequest.abort();
            this.cacheRequest = null;
            this.outputStream = null;
         }

      }

      public synchronized void mark(int var1) {
         super.mark(var1);
         if (this.cacheRequest != null) {
            this.marked = true;
            this.markCount = 0;
         }

      }

      public synchronized void reset() throws IOException {
         super.reset();
         if (this.cacheRequest != null) {
            this.marked = false;
            this.inCache += this.markCount;
         }

      }

      private void ensureOpen() throws IOException {
         if (this.closed) {
            throw new IOException("stream is closed");
         }
      }

      public int read() throws IOException {
         this.ensureOpen();

         try {
            byte[] var1 = new byte[1];
            int var2 = this.read(var1);
            return var2 == -1 ? var2 : var1[0] & 255;
         } catch (IOException var3) {
            if (this.cacheRequest != null) {
               this.cacheRequest.abort();
            }

            throw var3;
         }
      }

      public int read(byte[] var1) throws IOException {
         return this.read(var1, 0, var1.length);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         this.ensureOpen();

         try {
            int var4 = super.read(var1, var2, var3);
            int var5;
            if (this.inCache > 0) {
               if (this.inCache >= var4) {
                  this.inCache -= var4;
                  var5 = 0;
               } else {
                  var5 = var4 - this.inCache;
                  this.inCache = 0;
               }
            } else {
               var5 = var4;
            }

            if (var5 > 0 && this.outputStream != null) {
               this.outputStream.write(var1, var2 + (var4 - var5), var5);
            }

            if (this.marked) {
               this.markCount += var4;
            }

            return var4;
         } catch (IOException var6) {
            if (this.cacheRequest != null) {
               this.cacheRequest.abort();
            }

            throw var6;
         }
      }

      public long skip(long var1) throws IOException {
         this.ensureOpen();
         long var3 = var1;
         if (this.skipBuffer == null) {
            this.skipBuffer = new byte[8096];
         }

         byte[] var6 = this.skipBuffer;
         if (var1 <= 0L) {
            return 0L;
         } else {
            while(var3 > 0L) {
               int var5 = this.read(var6, 0, (int)Math.min(8096L, var3));
               if (var5 < 0) {
                  break;
               }

               var3 -= (long)var5;
            }

            return var1 - var3;
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            try {
               if (this.outputStream != null) {
                  if (this.read() != -1) {
                     this.cacheRequest.abort();
                  } else {
                     this.outputStream.close();
                  }
               }

               super.close();
            } catch (IOException var5) {
               if (this.cacheRequest != null) {
                  this.cacheRequest.abort();
               }

               throw var5;
            } finally {
               this.closed = true;
               HttpURLConnection.this.http = null;
               HttpURLConnection.this.checkResponseCredentials(true);
            }

         }
      }
   }

   public static enum TunnelState {
      NONE,
      SETUP,
      TUNNELING;
   }
}
