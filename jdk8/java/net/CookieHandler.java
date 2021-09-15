package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import sun.security.util.SecurityConstants;

public abstract class CookieHandler {
   private static CookieHandler cookieHandler;

   public static synchronized CookieHandler getDefault() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(SecurityConstants.GET_COOKIEHANDLER_PERMISSION);
      }

      return cookieHandler;
   }

   public static synchronized void setDefault(CookieHandler var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.SET_COOKIEHANDLER_PERMISSION);
      }

      cookieHandler = var0;
   }

   public abstract Map<String, List<String>> get(URI var1, Map<String, List<String>> var2) throws IOException;

   public abstract void put(URI var1, Map<String, List<String>> var2) throws IOException;
}
