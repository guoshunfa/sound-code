package javax.xml.datatype;

import java.io.File;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class FactoryFinder {
   private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
   private static boolean debug = false;
   private static final Properties cacheProps = new Properties();
   private static volatile boolean firstTime = true;
   private static final SecuritySupport ss = new SecuritySupport();

   private static void dPrint(String msg) {
      if (debug) {
         System.err.println("JAXP: " + msg);
      }

   }

   private static Class<?> getProviderClass(String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader) throws ClassNotFoundException {
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

   static <T> T newInstance(Class<T> type, String className, ClassLoader cl, boolean doFallback) throws DatatypeConfigurationException {
      return newInstance(type, className, cl, doFallback, false);
   }

   static <T> T newInstance(Class<T> type, String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader) throws DatatypeConfigurationException {
      assert type != null;

      if (System.getSecurityManager() != null && className != null && className.startsWith("com.sun.org.apache.xerces.internal")) {
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
         throw new DatatypeConfigurationException("Provider " + className + " not found", var7);
      } catch (Exception var8) {
         throw new DatatypeConfigurationException("Provider " + className + " could not be instantiated: " + var8, var8);
      }
   }

   static <T> T find(Class<T> type, String fallbackClassName) throws DatatypeConfigurationException {
      String factoryId = type.getName();
      dPrint("find factoryId =" + factoryId);

      String factoryClassName;
      try {
         factoryClassName = ss.getSystemProperty(factoryId);
         if (factoryClassName != null) {
            dPrint("found system property, value=" + factoryClassName);
            return newInstance(type, factoryClassName, (ClassLoader)null, true);
         }
      } catch (SecurityException var9) {
         if (debug) {
            var9.printStackTrace();
         }
      }

      try {
         if (firstTime) {
            synchronized(cacheProps) {
               if (firstTime) {
                  String configFile = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
                  File f = new File(configFile);
                  firstTime = false;
                  if (ss.doesFileExist(f)) {
                     dPrint("Read properties file " + f);
                     cacheProps.load((InputStream)ss.getFileInputStream(f));
                  }
               }
            }
         }

         factoryClassName = cacheProps.getProperty(factoryId);
         if (factoryClassName != null) {
            dPrint("found in $java.home/jaxp.properties, value=" + factoryClassName);
            return newInstance(type, factoryClassName, (ClassLoader)null, true);
         }
      } catch (Exception var8) {
         if (debug) {
            var8.printStackTrace();
         }
      }

      T provider = findServiceProvider(type);
      if (provider != null) {
         return provider;
      } else if (fallbackClassName == null) {
         throw new DatatypeConfigurationException("Provider for " + factoryId + " cannot be found");
      } else {
         dPrint("loaded from fallback value: " + fallbackClassName);
         return newInstance(type, fallbackClassName, (ClassLoader)null, true);
      }
   }

   private static <T> T findServiceProvider(final Class<T> type) throws DatatypeConfigurationException {
      try {
         return AccessController.doPrivileged(new PrivilegedAction<T>() {
            public T run() {
               ServiceLoader<T> serviceLoader = ServiceLoader.load(type);
               Iterator<T> iterator = serviceLoader.iterator();
               return iterator.hasNext() ? iterator.next() : null;
            }
         });
      } catch (ServiceConfigurationError var3) {
         DatatypeConfigurationException error = new DatatypeConfigurationException("Provider for " + type + " cannot be found", var3);
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
