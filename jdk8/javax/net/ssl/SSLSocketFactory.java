package javax.net.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.AccessController;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Locale;
import javax.net.SocketFactory;
import sun.security.action.GetPropertyAction;

public abstract class SSLSocketFactory extends SocketFactory {
   private static SSLSocketFactory theFactory;
   private static boolean propertyChecked;
   static final boolean DEBUG;

   private static void log(String var0) {
      if (DEBUG) {
         System.out.println(var0);
      }

   }

   public static synchronized SocketFactory getDefault() {
      if (theFactory != null) {
         return theFactory;
      } else {
         if (!propertyChecked) {
            propertyChecked = true;
            String var0 = getSecurityProperty("ssl.SocketFactory.provider");
            if (var0 != null) {
               log("setting up default SSLSocketFactory");

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
                  SSLSocketFactory var2 = (SSLSocketFactory)var1.newInstance();
                  log("instantiated an instance of class " + var0);
                  theFactory = var2;
                  return var2;
               } catch (Exception var6) {
                  log("SSLSocketFactory instantiation failed: " + var6.toString());
                  theFactory = new DefaultSSLSocketFactory(var6);
                  return theFactory;
               }
            }
         }

         try {
            return SSLContext.getDefault().getSocketFactory();
         } catch (NoSuchAlgorithmException var4) {
            return new DefaultSSLSocketFactory(var4);
         }
      }
   }

   static String getSecurityProperty(final String var0) {
      return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            String var1 = Security.getProperty(var0);
            if (var1 != null) {
               var1 = var1.trim();
               if (var1.length() == 0) {
                  var1 = null;
               }
            }

            return var1;
         }
      });
   }

   public abstract String[] getDefaultCipherSuites();

   public abstract String[] getSupportedCipherSuites();

   public abstract Socket createSocket(Socket var1, String var2, int var3, boolean var4) throws IOException;

   public Socket createSocket(Socket var1, InputStream var2, boolean var3) throws IOException {
      throw new UnsupportedOperationException();
   }

   static {
      String var0 = ((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("javax.net.debug", "")))).toLowerCase(Locale.ENGLISH);
      DEBUG = var0.contains("all") || var0.contains("ssl");
   }
}
