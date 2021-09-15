package javax.xml.ws.spi;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.ws.WebServiceException;

class FactoryFinder {
   private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader";

   private static Object newInstance(String className, ClassLoader classLoader) {
      try {
         Class spiClass = safeLoadClass(className, classLoader);
         return spiClass.newInstance();
      } catch (ClassNotFoundException var3) {
         throw new WebServiceException("Provider " + className + " not found", var3);
      } catch (Exception var4) {
         throw new WebServiceException("Provider " + className + " could not be instantiated: " + var4, var4);
      }
   }

   static Object find(String factoryId, String fallbackClassName) {
      if (isOsgi()) {
         return lookupUsingOSGiServiceLoader(factoryId);
      } else {
         ClassLoader classLoader;
         try {
            classLoader = Thread.currentThread().getContextClassLoader();
         } catch (Exception var26) {
            throw new WebServiceException(var26.toString(), var26);
         }

         String serviceId = "META-INF/services/" + factoryId;
         BufferedReader rd = null;

         String factoryClassName;
         label224: {
            Object var7;
            try {
               InputStream is;
               if (classLoader == null) {
                  is = ClassLoader.getSystemResourceAsStream(serviceId);
               } else {
                  is = classLoader.getResourceAsStream(serviceId);
               }

               if (is == null) {
                  break label224;
               }

               rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
               factoryClassName = rd.readLine();
               if (factoryClassName == null || "".equals(factoryClassName)) {
                  break label224;
               }

               var7 = newInstance(factoryClassName, classLoader);
            } catch (Exception var29) {
               break label224;
            } finally {
               close(rd);
            }

            return var7;
         }

         FileInputStream inStream = null;

         label210: {
            Object var11;
            try {
               factoryClassName = System.getProperty("java.home");
               String configFile = factoryClassName + File.separator + "lib" + File.separator + "jaxws.properties";
               File f = new File(configFile);
               if (!f.exists()) {
                  break label210;
               }

               Properties props = new Properties();
               inStream = new FileInputStream(f);
               props.load((InputStream)inStream);
               String factoryClassName = props.getProperty(factoryId);
               var11 = newInstance(factoryClassName, classLoader);
            } catch (Exception var27) {
               break label210;
            } finally {
               close(inStream);
            }

            return var11;
         }

         try {
            factoryClassName = System.getProperty(factoryId);
            if (factoryClassName != null) {
               return newInstance(factoryClassName, classLoader);
            }
         } catch (SecurityException var25) {
         }

         if (fallbackClassName == null) {
            throw new WebServiceException("Provider for " + factoryId + " cannot be found", (Throwable)null);
         } else {
            return newInstance(fallbackClassName, classLoader);
         }
      }
   }

   private static void close(Closeable closeable) {
      if (closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
         }
      }

   }

   private static Class safeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
      try {
         SecurityManager s = System.getSecurityManager();
         if (s != null) {
            int i = className.lastIndexOf(46);
            if (i != -1) {
               s.checkPackageAccess(className.substring(0, i));
            }
         }

         return classLoader == null ? Class.forName(className) : classLoader.loadClass(className);
      } catch (SecurityException var4) {
         if ("com.sun.xml.internal.ws.spi.ProviderImpl".equals(className)) {
            return Class.forName(className);
         } else {
            throw var4;
         }
      }
   }

   private static boolean isOsgi() {
      try {
         Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   private static Object lookupUsingOSGiServiceLoader(String factoryId) {
      try {
         Class serviceClass = Class.forName(factoryId);
         Class[] args = new Class[]{serviceClass};
         Class target = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         Method m = target.getMethod("lookupProviderInstances", Class.class);
         Iterator iter = ((Iterable)m.invoke((Object)null, (Object[])args)).iterator();
         return iter.hasNext() ? iter.next() : null;
      } catch (Exception var6) {
         return null;
      }
   }
}
