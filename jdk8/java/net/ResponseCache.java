package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import sun.security.util.SecurityConstants;

public abstract class ResponseCache {
   private static ResponseCache theResponseCache;

   public static synchronized ResponseCache getDefault() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(SecurityConstants.GET_RESPONSECACHE_PERMISSION);
      }

      return theResponseCache;
   }

   public static synchronized void setDefault(ResponseCache var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.SET_RESPONSECACHE_PERMISSION);
      }

      theResponseCache = var0;
   }

   public abstract CacheResponse get(URI var1, String var2, Map<String, List<String>> var3) throws IOException;

   public abstract CacheRequest put(URI var1, URLConnection var2) throws IOException;
}
