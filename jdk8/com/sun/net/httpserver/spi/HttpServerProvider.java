package com.sun.net.httpserver.spi;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import jdk.Exported;
import sun.net.httpserver.DefaultHttpServerProvider;

@Exported
public abstract class HttpServerProvider {
   private static final Object lock = new Object();
   private static HttpServerProvider provider = null;

   public abstract HttpServer createHttpServer(InetSocketAddress var1, int var2) throws IOException;

   public abstract HttpsServer createHttpsServer(InetSocketAddress var1, int var2) throws IOException;

   protected HttpServerProvider() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("httpServerProvider"));
      }

   }

   private static boolean loadProviderFromProperty() {
      String var0 = System.getProperty("com.sun.net.httpserver.HttpServerProvider");
      if (var0 == null) {
         return false;
      } else {
         try {
            Class var1 = Class.forName(var0, true, ClassLoader.getSystemClassLoader());
            provider = (HttpServerProvider)var1.newInstance();
            return true;
         } catch (IllegalAccessException | InstantiationException | SecurityException | ClassNotFoundException var2) {
            throw new ServiceConfigurationError((String)null, var2);
         }
      }
   }

   private static boolean loadProviderAsService() {
      Iterator var0 = ServiceLoader.load(HttpServerProvider.class, ClassLoader.getSystemClassLoader()).iterator();

      while(true) {
         try {
            if (!var0.hasNext()) {
               return false;
            }

            provider = (HttpServerProvider)var0.next();
            return true;
         } catch (ServiceConfigurationError var2) {
            if (!(var2.getCause() instanceof SecurityException)) {
               throw var2;
            }
         }
      }
   }

   public static HttpServerProvider provider() {
      synchronized(lock) {
         return provider != null ? provider : (HttpServerProvider)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               if (HttpServerProvider.loadProviderFromProperty()) {
                  return HttpServerProvider.provider;
               } else if (HttpServerProvider.loadProviderAsService()) {
                  return HttpServerProvider.provider;
               } else {
                  HttpServerProvider.provider = new DefaultHttpServerProvider();
                  return HttpServerProvider.provider;
               }
            }
         });
      }
   }
}
