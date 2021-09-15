package sun.net.ftp;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceConfigurationError;
import sun.net.ftp.impl.DefaultFtpClientProvider;

public abstract class FtpClientProvider {
   private static final Object lock = new Object();
   private static FtpClientProvider provider = null;

   public abstract FtpClient createFtpClient();

   protected FtpClientProvider() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("ftpClientProvider"));
      }

   }

   private static boolean loadProviderFromProperty() {
      String var0 = System.getProperty("sun.net.ftpClientProvider");
      if (var0 == null) {
         return false;
      } else {
         try {
            Class var1 = Class.forName(var0, true, (ClassLoader)null);
            provider = (FtpClientProvider)var1.newInstance();
            return true;
         } catch (IllegalAccessException | InstantiationException | SecurityException | ClassNotFoundException var2) {
            throw new ServiceConfigurationError(var2.toString());
         }
      }
   }

   private static boolean loadProviderAsService() {
      return false;
   }

   public static FtpClientProvider provider() {
      synchronized(lock) {
         return provider != null ? provider : (FtpClientProvider)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               if (FtpClientProvider.loadProviderFromProperty()) {
                  return FtpClientProvider.provider;
               } else if (FtpClientProvider.loadProviderAsService()) {
                  return FtpClientProvider.provider;
               } else {
                  FtpClientProvider.provider = new DefaultFtpClientProvider();
                  return FtpClientProvider.provider;
               }
            }
         });
      }
   }
}
