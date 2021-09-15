package java.net;

import java.io.IOException;
import java.util.List;
import sun.security.util.SecurityConstants;

public abstract class ProxySelector {
   private static ProxySelector theProxySelector;

   public static ProxySelector getDefault() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(SecurityConstants.GET_PROXYSELECTOR_PERMISSION);
      }

      return theProxySelector;
   }

   public static void setDefault(ProxySelector var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(SecurityConstants.SET_PROXYSELECTOR_PERMISSION);
      }

      theProxySelector = var0;
   }

   public abstract List<Proxy> select(URI var1);

   public abstract void connectFailed(URI var1, SocketAddress var2, IOException var3);

   static {
      try {
         Class var0 = Class.forName("sun.net.spi.DefaultProxySelector");
         if (var0 != null && ProxySelector.class.isAssignableFrom(var0)) {
            theProxySelector = (ProxySelector)var0.newInstance();
         }
      } catch (Exception var1) {
         theProxySelector = null;
      }

   }
}
