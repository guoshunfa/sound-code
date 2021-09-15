package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;

public class RMIClassLoader {
   private static final RMIClassLoaderSpi defaultProvider = newDefaultProviderInstance();
   private static final RMIClassLoaderSpi provider = (RMIClassLoaderSpi)AccessController.doPrivileged(new PrivilegedAction<RMIClassLoaderSpi>() {
      public RMIClassLoaderSpi run() {
         return RMIClassLoader.initializeProvider();
      }
   });

   private RMIClassLoader() {
   }

   /** @deprecated */
   @Deprecated
   public static Class<?> loadClass(String var0) throws MalformedURLException, ClassNotFoundException {
      return loadClass((String)null, var0);
   }

   public static Class<?> loadClass(URL var0, String var1) throws MalformedURLException, ClassNotFoundException {
      return provider.loadClass(var0 != null ? var0.toString() : null, var1, (ClassLoader)null);
   }

   public static Class<?> loadClass(String var0, String var1) throws MalformedURLException, ClassNotFoundException {
      return provider.loadClass(var0, var1, (ClassLoader)null);
   }

   public static Class<?> loadClass(String var0, String var1, ClassLoader var2) throws MalformedURLException, ClassNotFoundException {
      return provider.loadClass(var0, var1, var2);
   }

   public static Class<?> loadProxyClass(String var0, String[] var1, ClassLoader var2) throws ClassNotFoundException, MalformedURLException {
      return provider.loadProxyClass(var0, var1, var2);
   }

   public static ClassLoader getClassLoader(String var0) throws MalformedURLException, SecurityException {
      return provider.getClassLoader(var0);
   }

   public static String getClassAnnotation(Class<?> var0) {
      return provider.getClassAnnotation(var0);
   }

   public static RMIClassLoaderSpi getDefaultProviderInstance() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("setFactory"));
      }

      return defaultProvider;
   }

   /** @deprecated */
   @Deprecated
   public static Object getSecurityContext(ClassLoader var0) {
      return sun.rmi.server.LoaderHandler.getSecurityContext(var0);
   }

   private static RMIClassLoaderSpi newDefaultProviderInstance() {
      return new RMIClassLoaderSpi() {
         public Class<?> loadClass(String var1, String var2, ClassLoader var3) throws MalformedURLException, ClassNotFoundException {
            return sun.rmi.server.LoaderHandler.loadClass(var1, var2, var3);
         }

         public Class<?> loadProxyClass(String var1, String[] var2, ClassLoader var3) throws MalformedURLException, ClassNotFoundException {
            return sun.rmi.server.LoaderHandler.loadProxyClass(var1, var2, var3);
         }

         public ClassLoader getClassLoader(String var1) throws MalformedURLException {
            return sun.rmi.server.LoaderHandler.getClassLoader(var1);
         }

         public String getClassAnnotation(Class<?> var1) {
            return sun.rmi.server.LoaderHandler.getClassAnnotation(var1);
         }
      };
   }

   private static RMIClassLoaderSpi initializeProvider() {
      String var0 = System.getProperty("java.rmi.server.RMIClassLoaderSpi");
      if (var0 != null) {
         if (var0.equals("default")) {
            return defaultProvider;
         } else {
            try {
               Class var9 = Class.forName(var0, false, ClassLoader.getSystemClassLoader()).asSubclass(RMIClassLoaderSpi.class);
               return (RMIClassLoaderSpi)var9.newInstance();
            } catch (ClassNotFoundException var4) {
               throw new NoClassDefFoundError(var4.getMessage());
            } catch (IllegalAccessException var5) {
               throw new IllegalAccessError(var5.getMessage());
            } catch (InstantiationException var6) {
               throw new InstantiationError(var6.getMessage());
            } catch (ClassCastException var7) {
               LinkageError var2 = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
               var2.initCause(var7);
               throw var2;
            }
         }
      } else {
         Iterator var1 = ServiceLoader.load(RMIClassLoaderSpi.class, ClassLoader.getSystemClassLoader()).iterator();
         if (var1.hasNext()) {
            try {
               return (RMIClassLoaderSpi)var1.next();
            } catch (ClassCastException var8) {
               LinkageError var3 = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
               var3.initCause(var8);
               throw var3;
            }
         } else {
            return defaultProvider;
         }
      }
   }
}
