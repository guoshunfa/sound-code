package javax.xml.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

class FactoryFinder {
   private static Object newInstance(String className, ClassLoader classLoader) throws SOAPException {
      try {
         Class spiClass = safeLoadClass(className, classLoader);
         return spiClass.newInstance();
      } catch (ClassNotFoundException var3) {
         throw new SOAPException("Provider " + className + " not found", var3);
      } catch (Exception var4) {
         throw new SOAPException("Provider " + className + " could not be instantiated: " + var4, var4);
      }
   }

   static Object find(String factoryId) throws SOAPException {
      return find(factoryId, (String)null, false);
   }

   static Object find(String factoryId, String fallbackClassName) throws SOAPException {
      return find(factoryId, fallbackClassName, true);
   }

   static Object find(String factoryId, String defaultClassName, boolean tryFallback) throws SOAPException {
      ClassLoader classLoader;
      try {
         classLoader = Thread.currentThread().getContextClassLoader();
      } catch (Exception var12) {
         throw new SOAPException(var12.toString(), var12);
      }

      String serviceId;
      try {
         serviceId = System.getProperty(factoryId);
         if (serviceId != null) {
            return newInstance(serviceId, classLoader);
         }
      } catch (SecurityException var11) {
      }

      String is;
      try {
         serviceId = System.getProperty("java.home");
         is = serviceId + File.separator + "lib" + File.separator + "jaxm.properties";
         File f = new File(is);
         if (f.exists()) {
            Properties props = new Properties();
            props.load((InputStream)(new FileInputStream(f)));
            String factoryClassName = props.getProperty(factoryId);
            return newInstance(factoryClassName, classLoader);
         }
      } catch (Exception var10) {
      }

      serviceId = "META-INF/services/" + factoryId;

      try {
         is = null;
         InputStream is;
         if (classLoader == null) {
            is = ClassLoader.getSystemResourceAsStream(serviceId);
         } else {
            is = classLoader.getResourceAsStream(serviceId);
         }

         if (is != null) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String factoryClassName = rd.readLine();
            rd.close();
            if (factoryClassName != null && !"".equals(factoryClassName)) {
               return newInstance(factoryClassName, classLoader);
            }
         }
      } catch (Exception var9) {
      }

      if (!tryFallback) {
         return null;
      } else if (defaultClassName == null) {
         throw new SOAPException("Provider for " + factoryId + " cannot be found", (Throwable)null);
      } else {
         return newInstance(defaultClassName, classLoader);
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
         if (isDefaultImplementation(className)) {
            return Class.forName(className);
         } else {
            throw var4;
         }
      }
   }

   private static boolean isDefaultImplementation(String className) {
      return "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl".equals(className) || "com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl".equals(className) || "com.sun.xml.internal.messaging.saaj.client.p2p.HttpSOAPConnectionFactory".equals(className) || "com.sun.xml.internal.messaging.saaj.soap.SAAJMetaFactoryImpl".equals(className);
   }
}
