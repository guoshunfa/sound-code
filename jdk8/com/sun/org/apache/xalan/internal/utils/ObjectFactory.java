package com.sun.org.apache.xalan.internal.utils;

public class ObjectFactory {
   private static final String JAXP_INTERNAL = "com.sun.org.apache";
   private static final String STAX_INTERNAL = "com.sun.xml.internal";
   private static final boolean DEBUG = false;

   private static void debugPrintln(String msg) {
   }

   public static ClassLoader findClassLoader() {
      if (System.getSecurityManager() != null) {
         return null;
      } else {
         ClassLoader context = SecuritySupport.getContextClassLoader();
         ClassLoader system = SecuritySupport.getSystemClassLoader();

         ClassLoader chain;
         for(chain = system; context != chain; chain = SecuritySupport.getParentClassLoader(chain)) {
            if (chain == null) {
               return context;
            }
         }

         ClassLoader current = ObjectFactory.class.getClassLoader();

         for(chain = system; current != chain; chain = SecuritySupport.getParentClassLoader(chain)) {
            if (chain == null) {
               return current;
            }
         }

         return system;
      }
   }

   public static Object newInstance(String className, boolean doFallback) throws ConfigurationError {
      return System.getSecurityManager() != null ? newInstance(className, (ClassLoader)null, doFallback) : newInstance(className, findClassLoader(), doFallback);
   }

   static Object newInstance(String className, ClassLoader cl, boolean doFallback) throws ConfigurationError {
      try {
         Class providerClass = findProviderClass(className, cl, doFallback);
         Object instance = providerClass.newInstance();
         return instance;
      } catch (ClassNotFoundException var5) {
         throw new ConfigurationError("Provider " + className + " not found", var5);
      } catch (Exception var6) {
         throw new ConfigurationError("Provider " + className + " could not be instantiated: " + var6, var6);
      }
   }

   public static Class<?> findProviderClass(String className, boolean doFallback) throws ClassNotFoundException, ConfigurationError {
      return findProviderClass(className, findClassLoader(), doFallback);
   }

   private static Class<?> findProviderClass(String className, ClassLoader cl, boolean doFallback) throws ClassNotFoundException, ConfigurationError {
      SecurityManager security = System.getSecurityManager();

      try {
         if (security != null) {
            if (!className.startsWith("com.sun.org.apache") && !className.startsWith("com.sun.xml.internal")) {
               int lastDot = className.lastIndexOf(".");
               String packageName = className;
               if (lastDot != -1) {
                  packageName = className.substring(0, lastDot);
               }

               security.checkPackageAccess(packageName);
            } else {
               cl = null;
            }
         }
      } catch (SecurityException var8) {
         throw var8;
      }

      Class providerClass;
      if (cl == null) {
         providerClass = Class.forName(className, false, ObjectFactory.class.getClassLoader());
      } else {
         try {
            providerClass = cl.loadClass(className);
         } catch (ClassNotFoundException var7) {
            if (!doFallback) {
               throw var7;
            }

            ClassLoader current = ObjectFactory.class.getClassLoader();
            if (current == null) {
               providerClass = Class.forName(className);
            } else {
               if (cl == current) {
                  throw var7;
               }

               providerClass = current.loadClass(className);
            }
         }
      }

      return providerClass;
   }
}
