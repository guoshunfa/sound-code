package javax.net.ssl;

import java.security.NoSuchAlgorithmException;
import javax.net.ServerSocketFactory;

public abstract class SSLServerSocketFactory extends ServerSocketFactory {
   private static SSLServerSocketFactory theFactory;
   private static boolean propertyChecked;

   private static void log(String var0) {
      if (SSLSocketFactory.DEBUG) {
         System.out.println(var0);
      }

   }

   protected SSLServerSocketFactory() {
   }

   public static synchronized ServerSocketFactory getDefault() {
      if (theFactory != null) {
         return theFactory;
      } else {
         if (!propertyChecked) {
            propertyChecked = true;
            String var0 = SSLSocketFactory.getSecurityProperty("ssl.ServerSocketFactory.provider");
            if (var0 != null) {
               log("setting up default SSLServerSocketFactory");

               try {
                  Class var1 = null;

                  try {
                     var1 = Class.forName(var0);
                  } catch (ClassNotFoundException var5) {
                     ClassLoader var3 = ClassLoader.getSystemClassLoader();
                     if (var3 != null) {
                        var1 = var3.loadClass(var0);
                     }
                  }

                  log("class " + var0 + " is loaded");
                  SSLServerSocketFactory var2 = (SSLServerSocketFactory)var1.newInstance();
                  log("instantiated an instance of class " + var0);
                  theFactory = var2;
                  return var2;
               } catch (Exception var6) {
                  log("SSLServerSocketFactory instantiation failed: " + var6);
                  theFactory = new DefaultSSLServerSocketFactory(var6);
                  return theFactory;
               }
            }
         }

         try {
            return SSLContext.getDefault().getServerSocketFactory();
         } catch (NoSuchAlgorithmException var4) {
            return new DefaultSSLServerSocketFactory(var4);
         }
      }
   }

   public abstract String[] getDefaultCipherSuites();

   public abstract String[] getSupportedCipherSuites();
}
