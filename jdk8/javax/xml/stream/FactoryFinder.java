package javax.xml.stream;

import java.io.File;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class FactoryFinder {
   private static final String DEFAULT_PACKAGE = "com.sun.xml.internal.";
   private static boolean debug = false;
   private static final Properties cacheProps = new Properties();
   private static volatile boolean firstTime = true;
   private static final SecuritySupport ss = new SecuritySupport();

   private static void dPrint(String msg) {
      if (debug) {
         System.err.println("JAXP: " + msg);
      }

   }

   private static Class getProviderClass(String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader) throws ClassNotFoundException {
      try {
         if (cl == null) {
            if (useBSClsLoader) {
               return Class.forName(className, false, FactoryFinder.class.getClassLoader());
            } else {
               cl = ss.getContextClassLoader();
               if (cl == null) {
                  throw new ClassNotFoundException();
               } else {
                  return Class.forName(className, false, cl);
               }
            }
         } else {
            return Class.forName(className, false, cl);
         }
      } catch (ClassNotFoundException var5) {
         if (doFallback) {
            return Class.forName(className, false, FactoryFinder.class.getClassLoader());
         } else {
            throw var5;
         }
      }
   }

   static <T> T newInstance(Class<T> type, String className, ClassLoader cl, boolean doFallback) throws FactoryConfigurationError {
      return newInstance(type, className, cl, doFallback, false);
   }

   static <T> T newInstance(Class<T> type, String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader) throws FactoryConfigurationError {
      assert type != null;

      if (System.getSecurityManager() != null && className != null && className.startsWith("com.sun.xml.internal.")) {
         cl = null;
         useBSClsLoader = true;
      }

      try {
         Class<?> providerClass = getProviderClass(className, cl, doFallback, useBSClsLoader);
         if (!type.isAssignableFrom(providerClass)) {
            throw new ClassCastException(className + " cannot be cast to " + type.getName());
         } else {
            Object instance = providerClass.newInstance();
            if (debug) {
               dPrint("created new instance of " + providerClass + " using ClassLoader: " + cl);
            }

            return type.cast(instance);
         }
      } catch (ClassNotFoundException var7) {
         throw new FactoryConfigurationError("Provider " + className + " not found", var7);
      } catch (Exception var8) {
         throw new FactoryConfigurationError("Provider " + className + " could not be instantiated: " + var8, var8);
      }
   }

   static <T> T find(Class<T> type, String fallbackClassName) throws FactoryConfigurationError {
      return find(type, type.getName(), (ClassLoader)null, fallbackClassName);
   }

   static <T> T find(Class<T> type, String factoryId, ClassLoader cl, String fallbackClassName) throws FactoryConfigurationError {
      dPrint("find factoryId =" + factoryId);

      String configFile;
      try {
         if (type.getName().equals(factoryId)) {
            configFile = ss.getSystemProperty(factoryId);
         } else {
            configFile = System.getProperty(factoryId);
         }

         if (configFile != null) {
            dPrint("found system property, value=" + configFile);
            return newInstance(type, configFile, cl, true);
         }
      } catch (SecurityException var10) {
         throw new FactoryConfigurationError("Failed to read factoryId '" + factoryId + "'", var10);
      }

      configFile = null;

      try {
         if (firstTime) {
            synchronized(cacheProps) {
               if (firstTime) {
                  configFile = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "stax.properties";
                  File f = new File(configFile);
                  firstTime = false;
                  if (ss.doesFileExist(f)) {
                     dPrint("Read properties file " + f);
                     cacheProps.load((InputStream)ss.getFileInputStream(f));
                  } else {
                     configFile = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
                     f = new File(configFile);
                     if (ss.doesFileExist(f)) {
                        dPrint("Read properties file " + f);
                        cacheProps.load((InputStream)ss.getFileInputStream(f));
                     }
                  }
               }
            }
         }

         String factoryClassName = cacheProps.getProperty(factoryId);
         if (factoryClassName != null) {
            dPrint("found in " + configFile + " value=" + factoryClassName);
            return newInstance(type, factoryClassName, cl, true);
         }
      } catch (Exception var9) {
         if (debug) {
            var9.printStackTrace();
         }
      }

      if (type.getName().equals(factoryId)) {
         T provider = findServiceProvider(type, cl);
         if (provider != null) {
            return provider;
         }
      } else {
         assert fallbackClassName == null;
      }

      if (fallbackClassName == null) {
         throw new FactoryConfigurationError("Provider for " + factoryId + " cannot be found", (Exception)null);
      } else {
         dPrint("loaded from fallback value: " + fallbackClassName);
         return newInstance(type, fallbackClassName, cl, true);
      }
   }

   private static <T> T findServiceProvider(final Class<T> type, final ClassLoader cl) {
      try {
         return AccessController.doPrivileged(new PrivilegedAction<T>() {
            public T run() {
               ServiceLoader serviceLoader;
               if (cl == null) {
                  serviceLoader = ServiceLoader.load(type);
               } else {
                  serviceLoader = ServiceLoader.load(type, cl);
               }

               Iterator<T> iterator = serviceLoader.iterator();
               return iterator.hasNext() ? iterator.next() : null;
            }
         });
      } catch (ServiceConfigurationError var5) {
         RuntimeException x = new RuntimeException("Provider for " + type + " cannot be created", var5);
         FactoryConfigurationError error = new FactoryConfigurationError(x, x.getMessage());
         throw error;
      }
   }

   static {
      try {
         String val = ss.getSystemProperty("jaxp.debug");
         debug = val != null && !"false".equals(val);
      } catch (SecurityException var1) {
         debug = false;
      }

   }
}
