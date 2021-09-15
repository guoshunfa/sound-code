package sun.net.www.protocol.http;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import sun.net.www.HeaderParser;
import sun.security.action.GetBooleanAction;

public abstract class AuthenticationInfo extends AuthCacheValue implements Cloneable {
   static final long serialVersionUID = -2588378268010453259L;
   public static final char SERVER_AUTHENTICATION = 's';
   public static final char PROXY_AUTHENTICATION = 'p';
   static final boolean serializeAuth = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("http.auth.serializeRequests")));
   protected transient PasswordAuthentication pw;
   private static HashMap<String, Thread> requests = new HashMap();
   char type;
   AuthScheme authScheme;
   String protocol;
   String host;
   int port;
   String realm;
   String path;
   String s1;
   String s2;

   public PasswordAuthentication credentials() {
      return this.pw;
   }

   public AuthCacheValue.Type getAuthType() {
      return this.type == 's' ? AuthCacheValue.Type.Server : AuthCacheValue.Type.Proxy;
   }

   AuthScheme getAuthScheme() {
      return this.authScheme;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public String getRealm() {
      return this.realm;
   }

   public String getPath() {
      return this.path;
   }

   public String getProtocolScheme() {
      return this.protocol;
   }

   protected boolean useAuthCache() {
      return true;
   }

   private static boolean requestIsInProgress(String var0) {
      if (!serializeAuth) {
         return false;
      } else {
         synchronized(requests) {
            Thread var3 = Thread.currentThread();
            Thread var2;
            if ((var2 = (Thread)requests.get(var0)) == null) {
               requests.put(var0, var3);
               return false;
            } else if (var2 == var3) {
               return false;
            } else {
               while(requests.containsKey(var0)) {
                  try {
                     requests.wait();
                  } catch (InterruptedException var6) {
                  }
               }

               return true;
            }
         }
      }
   }

   private static void requestCompleted(String var0) {
      synchronized(requests) {
         Thread var2 = (Thread)requests.get(var0);
         if (var2 != null && var2 == Thread.currentThread()) {
            boolean var3 = requests.remove(var0) != null;

            assert var3;
         }

         requests.notifyAll();
      }
   }

   public AuthenticationInfo(char var1, AuthScheme var2, String var3, int var4, String var5) {
      this.type = var1;
      this.authScheme = var2;
      this.protocol = "";
      this.host = var3.toLowerCase();
      this.port = var4;
      this.realm = var5;
      this.path = null;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public AuthenticationInfo(char var1, AuthScheme var2, URL var3, String var4) {
      this.type = var1;
      this.authScheme = var2;
      this.protocol = var3.getProtocol().toLowerCase();
      this.host = var3.getHost().toLowerCase();
      this.port = var3.getPort();
      if (this.port == -1) {
         this.port = var3.getDefaultPort();
      }

      this.realm = var4;
      String var5 = var3.getPath();
      if (var5.length() == 0) {
         this.path = var5;
      } else {
         this.path = reducePath(var5);
      }

   }

   static String reducePath(String var0) {
      int var1 = var0.lastIndexOf(47);
      int var2 = var0.lastIndexOf(46);
      if (var1 != -1) {
         return var1 < var2 ? var0.substring(0, var1 + 1) : var0;
      } else {
         return var0;
      }
   }

   static AuthenticationInfo getServerAuth(URL var0) {
      int var1 = var0.getPort();
      if (var1 == -1) {
         var1 = var0.getDefaultPort();
      }

      String var2 = "s:" + var0.getProtocol().toLowerCase() + ":" + var0.getHost().toLowerCase() + ":" + var1;
      return getAuth(var2, var0);
   }

   static String getServerAuthKey(URL var0, String var1, AuthScheme var2) {
      int var3 = var0.getPort();
      if (var3 == -1) {
         var3 = var0.getDefaultPort();
      }

      String var4 = "s:" + var2 + ":" + var0.getProtocol().toLowerCase() + ":" + var0.getHost().toLowerCase() + ":" + var3 + ":" + var1;
      return var4;
   }

   static AuthenticationInfo getServerAuth(String var0) {
      AuthenticationInfo var1 = getAuth(var0, (URL)null);
      if (var1 == null && requestIsInProgress(var0)) {
         var1 = getAuth(var0, (URL)null);
      }

      return var1;
   }

   static AuthenticationInfo getAuth(String var0, URL var1) {
      return var1 == null ? (AuthenticationInfo)cache.get(var0, (String)null) : (AuthenticationInfo)cache.get(var0, var1.getPath());
   }

   static AuthenticationInfo getProxyAuth(String var0, int var1) {
      String var2 = "p::" + var0.toLowerCase() + ":" + var1;
      AuthenticationInfo var3 = (AuthenticationInfo)cache.get(var2, (String)null);
      return var3;
   }

   static String getProxyAuthKey(String var0, int var1, String var2, AuthScheme var3) {
      String var4 = "p:" + var3 + "::" + var0.toLowerCase() + ":" + var1 + ":" + var2;
      return var4;
   }

   static AuthenticationInfo getProxyAuth(String var0) {
      AuthenticationInfo var1 = (AuthenticationInfo)cache.get(var0, (String)null);
      if (var1 == null && requestIsInProgress(var0)) {
         var1 = (AuthenticationInfo)cache.get(var0, (String)null);
      }

      return var1;
   }

   void addToCache() {
      String var1 = this.cacheKey(true);
      if (this.useAuthCache()) {
         cache.put(var1, this);
         if (this.supportsPreemptiveAuthorization()) {
            cache.put(this.cacheKey(false), this);
         }
      }

      endAuthRequest(var1);
   }

   static void endAuthRequest(String var0) {
      if (serializeAuth) {
         synchronized(requests) {
            requestCompleted(var0);
         }
      }
   }

   void removeFromCache() {
      cache.remove(this.cacheKey(true), this);
      if (this.supportsPreemptiveAuthorization()) {
         cache.remove(this.cacheKey(false), this);
      }

   }

   public abstract boolean supportsPreemptiveAuthorization();

   public String getHeaderName() {
      return this.type == 's' ? "Authorization" : "Proxy-authorization";
   }

   public abstract String getHeaderValue(URL var1, String var2);

   public abstract boolean setHeaders(HttpURLConnection var1, HeaderParser var2, String var3);

   public abstract boolean isAuthorizationStale(String var1);

   String cacheKey(boolean var1) {
      return var1 ? this.type + ":" + this.authScheme + ":" + this.protocol + ":" + this.host + ":" + this.port + ":" + this.realm : this.type + ":" + this.protocol + ":" + this.host + ":" + this.port;
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.pw = new PasswordAuthentication(this.s1, this.s2.toCharArray());
      this.s1 = null;
      this.s2 = null;
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      this.s1 = this.pw.getUserName();
      this.s2 = new String(this.pw.getPassword());
      var1.defaultWriteObject();
   }
}
