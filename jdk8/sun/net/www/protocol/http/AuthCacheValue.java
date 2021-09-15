package sun.net.www.protocol.http;

import java.io.Serializable;
import java.net.PasswordAuthentication;

public abstract class AuthCacheValue implements Serializable {
   static final long serialVersionUID = 735249334068211611L;
   protected static AuthCache cache = new AuthCacheImpl();

   public static void setAuthCache(AuthCache var0) {
      cache = var0;
   }

   AuthCacheValue() {
   }

   abstract AuthCacheValue.Type getAuthType();

   abstract AuthScheme getAuthScheme();

   abstract String getHost();

   abstract int getPort();

   abstract String getRealm();

   abstract String getPath();

   abstract String getProtocolScheme();

   abstract PasswordAuthentication credentials();

   public static enum Type {
      Proxy,
      Server;
   }
}
