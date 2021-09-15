package sun.net.www.protocol.http;

import java.io.IOException;
import java.net.Authenticator;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Base64;
import java.util.HashMap;
import sun.net.www.HeaderParser;
import sun.security.action.GetPropertyAction;
import sun.util.logging.PlatformLogger;

class NegotiateAuthentication extends AuthenticationInfo {
   private static final long serialVersionUID = 100L;
   private static final PlatformLogger logger = HttpURLConnection.getHttpLogger();
   private final HttpCallerInfo hci;
   static HashMap<String, Boolean> supported = null;
   static ThreadLocal<HashMap<String, Negotiator>> cache = null;
   private static final boolean cacheSPNEGO;
   private Negotiator negotiator = null;

   public NegotiateAuthentication(HttpCallerInfo var1) {
      super((char)(Authenticator.RequestorType.PROXY == var1.authType ? 'p' : 's'), var1.scheme.equalsIgnoreCase("Negotiate") ? AuthScheme.NEGOTIATE : AuthScheme.KERBEROS, var1.url, "");
      this.hci = var1;
   }

   public boolean supportsPreemptiveAuthorization() {
      return false;
   }

   public static boolean isSupported(HttpCallerInfo var0) {
      ClassLoader var1 = null;

      try {
         var1 = Thread.currentThread().getContextClassLoader();
      } catch (SecurityException var5) {
         if (logger.isLoggable(PlatformLogger.Level.FINER)) {
            logger.finer("NegotiateAuthentication: Attempt to get the context class loader failed - " + var5);
         }
      }

      if (var1 != null) {
         synchronized(var1) {
            return isSupportedImpl(var0);
         }
      } else {
         return isSupportedImpl(var0);
      }
   }

   private static synchronized boolean isSupportedImpl(HttpCallerInfo var0) {
      if (supported == null) {
         supported = new HashMap();
      }

      String var1 = var0.host;
      var1 = var1.toLowerCase();
      if (supported.containsKey(var1)) {
         return (Boolean)supported.get(var1);
      } else {
         Negotiator var2 = Negotiator.getNegotiator(var0);
         if (var2 != null) {
            supported.put(var1, true);
            if (cache == null) {
               cache = new ThreadLocal<HashMap<String, Negotiator>>() {
                  protected HashMap<String, Negotiator> initialValue() {
                     return new HashMap();
                  }
               };
            }

            ((HashMap)cache.get()).put(var1, var2);
            return true;
         } else {
            supported.put(var1, false);
            return false;
         }
      }
   }

   private static synchronized HashMap<String, Negotiator> getCache() {
      return cache == null ? null : (HashMap)cache.get();
   }

   protected boolean useAuthCache() {
      return super.useAuthCache() && cacheSPNEGO;
   }

   public String getHeaderValue(URL var1, String var2) {
      throw new RuntimeException("getHeaderValue not supported");
   }

   public boolean isAuthorizationStale(String var1) {
      return false;
   }

   public synchronized boolean setHeaders(HttpURLConnection var1, HeaderParser var2, String var3) {
      try {
         byte[] var5 = null;
         String[] var6 = var3.split("\\s+");
         if (var6.length > 1) {
            var5 = Base64.getDecoder().decode(var6[1]);
         }

         String var4 = this.hci.scheme + " " + Base64.getEncoder().encodeToString(var5 == null ? this.firstToken() : this.nextToken(var5));
         var1.setAuthenticationProperty(this.getHeaderName(), var4);
         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   private byte[] firstToken() throws IOException {
      this.negotiator = null;
      HashMap var1 = getCache();
      if (var1 != null) {
         this.negotiator = (Negotiator)var1.get(this.getHost());
         if (this.negotiator != null) {
            var1.remove(this.getHost());
         }
      }

      if (this.negotiator == null) {
         this.negotiator = Negotiator.getNegotiator(this.hci);
         if (this.negotiator == null) {
            IOException var2 = new IOException("Cannot initialize Negotiator");
            throw var2;
         }
      }

      return this.negotiator.firstToken();
   }

   private byte[] nextToken(byte[] var1) throws IOException {
      return this.negotiator.nextToken(var1);
   }

   static {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jdk.spnego.cache", "true")));
      cacheSPNEGO = Boolean.parseBoolean(var0);
   }
}
