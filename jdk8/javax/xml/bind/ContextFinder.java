package javax.xml.bind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class ContextFinder {
   private static final Logger logger = Logger.getLogger("javax.xml.bind");
   private static final String PLATFORM_DEFAULT_FACTORY_CLASS = "com.sun.xml.internal.bind.v2.ContextFactory";

   private static void handleInvocationTargetException(InvocationTargetException x) throws JAXBException {
      Throwable t = x.getTargetException();
      if (t != null) {
         if (t instanceof JAXBException) {
            throw (JAXBException)t;
         }

         if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
         }

         if (t instanceof Error) {
            throw (Error)t;
         }
      }

   }

   private static JAXBException handleClassCastException(Class originalType, Class targetType) {
      URL targetTypeURL = which(targetType);
      return new JAXBException(Messages.format("JAXBContext.IllegalCast", getClassClassLoader(originalType).getResource("javax/xml/bind/JAXBContext.class"), targetTypeURL));
   }

   static JAXBContext newInstance(String contextPath, String className, ClassLoader classLoader, Map properties) throws JAXBException {
      try {
         Class spFactory = safeLoadClass(className, classLoader);
         return newInstance(contextPath, spFactory, classLoader, properties);
      } catch (ClassNotFoundException var5) {
         throw new JAXBException(Messages.format("ContextFinder.ProviderNotFound", (Object)className), var5);
      } catch (RuntimeException var6) {
         throw var6;
      } catch (Exception var7) {
         throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", className, var7), var7);
      }
   }

   static JAXBContext newInstance(String contextPath, Class spFactory, ClassLoader classLoader, Map properties) throws JAXBException {
      try {
         Object context = null;

         Method m;
         try {
            m = spFactory.getMethod("createContext", String.class, ClassLoader.class, Map.class);
            context = m.invoke((Object)null, contextPath, classLoader, properties);
         } catch (NoSuchMethodException var6) {
         }

         if (context == null) {
            m = spFactory.getMethod("createContext", String.class, ClassLoader.class);
            context = m.invoke((Object)null, contextPath, classLoader);
         }

         if (!(context instanceof JAXBContext)) {
            throw handleClassCastException(context.getClass(), JAXBContext.class);
         } else {
            return (JAXBContext)context;
         }
      } catch (InvocationTargetException var7) {
         handleInvocationTargetException(var7);
         Throwable e = var7;
         if (var7.getTargetException() != null) {
            e = var7.getTargetException();
         }

         throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, e), (Throwable)e);
      } catch (RuntimeException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, var9), var9);
      }
   }

   static JAXBContext newInstance(Class[] classes, Map properties, String className) throws JAXBException {
      ClassLoader cl = getContextClassLoader();

      Class spi;
      try {
         spi = safeLoadClass(className, cl);
      } catch (ClassNotFoundException var6) {
         throw new JAXBException(var6);
      }

      if (logger.isLoggable(Level.FINE)) {
         logger.log(Level.FINE, "loaded {0} from {1}", new Object[]{className, which(spi)});
      }

      return newInstance(classes, properties, spi);
   }

   static JAXBContext newInstance(Class[] classes, Map properties, Class spFactory) throws JAXBException {
      Method m;
      try {
         m = spFactory.getMethod("createContext", Class[].class, Map.class);
      } catch (NoSuchMethodException var6) {
         throw new JAXBException(var6);
      }

      try {
         Object context = m.invoke((Object)null, classes, properties);
         if (!(context instanceof JAXBContext)) {
            throw handleClassCastException(context.getClass(), JAXBContext.class);
         } else {
            return (JAXBContext)context;
         }
      } catch (IllegalAccessException var7) {
         throw new JAXBException(var7);
      } catch (InvocationTargetException var8) {
         handleInvocationTargetException(var8);
         Throwable x = var8;
         if (var8.getTargetException() != null) {
            x = var8.getTargetException();
         }

         throw new JAXBException((Throwable)x);
      }
   }

   static JAXBContext find(String factoryId, String contextPath, ClassLoader classLoader, Map properties) throws JAXBException {
      String jaxbContextFQCN = JAXBContext.class.getName();
      StringTokenizer packages = new StringTokenizer(contextPath, ":");
      if (!packages.hasMoreTokens()) {
         throw new JAXBException(Messages.format("ContextFinder.NoPackageInContextPath"));
      } else {
         logger.fine("Searching jaxb.properties");

         String factoryClassName;
         while(packages.hasMoreTokens()) {
            String packageName = packages.nextToken(":").replace('.', '/');
            StringBuilder propFileName = (new StringBuilder()).append(packageName).append("/jaxb.properties");
            Properties props = loadJAXBProperties(classLoader, propFileName.toString());
            if (props != null) {
               if (props.containsKey(factoryId)) {
                  factoryClassName = props.getProperty(factoryId);
                  return newInstance(contextPath, factoryClassName, classLoader, properties);
               }

               throw new JAXBException(Messages.format("ContextFinder.MissingProperty", packageName, factoryId));
            }
         }

         logger.fine("Searching the system property");
         factoryClassName = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("javax.xml.bind.context.factory")));
         if (factoryClassName != null) {
            return newInstance(contextPath, factoryClassName, classLoader, properties);
         } else {
            factoryClassName = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(jaxbContextFQCN)));
            if (factoryClassName != null) {
               return newInstance(contextPath, factoryClassName, classLoader, properties);
            } else {
               Class jaxbContext = lookupJaxbContextUsingOsgiServiceLoader();
               if (jaxbContext != null) {
                  logger.fine("OSGi environment detected");
                  return newInstance(contextPath, jaxbContext, classLoader, properties);
               } else {
                  logger.fine("Searching META-INF/services");
                  BufferedReader r = null;

                  label151: {
                     JAXBContext var12;
                     try {
                        StringBuilder resource = (new StringBuilder()).append("META-INF/services/").append(jaxbContextFQCN);
                        InputStream resourceStream = classLoader.getResourceAsStream(resource.toString());
                        if (resourceStream == null) {
                           logger.log(Level.FINE, (String)"Unable to load:{0}", (Object)resource.toString());
                           break label151;
                        }

                        r = new BufferedReader(new InputStreamReader(resourceStream, "UTF-8"));
                        factoryClassName = r.readLine();
                        if (factoryClassName != null) {
                           factoryClassName = factoryClassName.trim();
                        }

                        r.close();
                        var12 = newInstance(contextPath, factoryClassName, classLoader, properties);
                     } catch (UnsupportedEncodingException var23) {
                        throw new JAXBException(var23);
                     } catch (IOException var24) {
                        throw new JAXBException(var24);
                     } finally {
                        try {
                           if (r != null) {
                              r.close();
                           }
                        } catch (IOException var22) {
                           Logger.getLogger(ContextFinder.class.getName()).log(Level.SEVERE, (String)null, (Throwable)var22);
                        }

                     }

                     return var12;
                  }

                  logger.fine("Trying to create the platform default provider");
                  return newInstance(contextPath, "com.sun.xml.internal.bind.v2.ContextFactory", classLoader, properties);
               }
            }
         }
      }
   }

   static JAXBContext find(Class[] classes, Map properties) throws JAXBException {
      String jaxbContextFQCN = JAXBContext.class.getName();
      Class[] var4 = classes;
      int var5 = classes.length;

      String factoryClassName;
      for(int var6 = 0; var6 < var5; ++var6) {
         Class c = var4[var6];
         ClassLoader classLoader = getClassClassLoader(c);
         Package pkg = c.getPackage();
         if (pkg != null) {
            String packageName = pkg.getName().replace('.', '/');
            String resourceName = packageName + "/jaxb.properties";
            logger.log(Level.FINE, (String)"Trying to locate {0}", (Object)resourceName);
            Properties props = loadJAXBProperties(classLoader, resourceName);
            if (props != null) {
               logger.fine("  found");
               if (props.containsKey("javax.xml.bind.context.factory")) {
                  factoryClassName = props.getProperty("javax.xml.bind.context.factory").trim();
                  return newInstance(classes, properties, factoryClassName);
               }

               throw new JAXBException(Messages.format("ContextFinder.MissingProperty", packageName, "javax.xml.bind.context.factory"));
            }

            logger.fine("  not found");
         }
      }

      logger.log(Level.FINE, (String)"Checking system property {0}", (Object)"javax.xml.bind.context.factory");
      factoryClassName = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("javax.xml.bind.context.factory")));
      if (factoryClassName != null) {
         logger.log(Level.FINE, (String)"  found {0}", (Object)factoryClassName);
         return newInstance(classes, properties, factoryClassName);
      } else {
         logger.fine("  not found");
         logger.log(Level.FINE, (String)"Checking system property {0}", (Object)jaxbContextFQCN);
         factoryClassName = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction(jaxbContextFQCN)));
         if (factoryClassName != null) {
            logger.log(Level.FINE, (String)"  found {0}", (Object)factoryClassName);
            return newInstance(classes, properties, factoryClassName);
         } else {
            logger.fine("  not found");
            Class jaxbContext = lookupJaxbContextUsingOsgiServiceLoader();
            if (jaxbContext != null) {
               logger.fine("OSGi environment detected");
               return newInstance(classes, properties, jaxbContext);
            } else {
               logger.fine("Checking META-INF/services");
               BufferedReader r = null;

               label166: {
                  JAXBContext var30;
                  try {
                     String resource = "META-INF/services/" + jaxbContextFQCN;
                     ClassLoader classLoader = getContextClassLoader();
                     URL resourceURL;
                     if (classLoader == null) {
                        resourceURL = ClassLoader.getSystemResource(resource);
                     } else {
                        resourceURL = classLoader.getResource(resource);
                     }

                     if (resourceURL == null) {
                        logger.log(Level.FINE, (String)"Unable to find: {0}", (Object)resource);
                        break label166;
                     }

                     logger.log(Level.FINE, (String)"Reading {0}", (Object)resourceURL);
                     r = new BufferedReader(new InputStreamReader(resourceURL.openStream(), "UTF-8"));
                     factoryClassName = r.readLine();
                     if (factoryClassName != null) {
                        factoryClassName = factoryClassName.trim();
                     }

                     var30 = newInstance(classes, properties, factoryClassName);
                  } catch (UnsupportedEncodingException var22) {
                     throw new JAXBException(var22);
                  } catch (IOException var23) {
                     throw new JAXBException(var23);
                  } finally {
                     if (r != null) {
                        try {
                           r.close();
                        } catch (IOException var21) {
                           logger.log(Level.FINE, (String)"Unable to close stream", (Throwable)var21);
                        }
                     }

                  }

                  return var30;
               }

               logger.fine("Trying to create the platform default provider");
               return newInstance(classes, properties, "com.sun.xml.internal.bind.v2.ContextFactory");
            }
         }
      }
   }

   private static Class lookupJaxbContextUsingOsgiServiceLoader() {
      try {
         Class target = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
         Method m = target.getMethod("lookupProviderClasses", Class.class);
         Iterator iter = ((Iterable)m.invoke((Object)null, JAXBContext.class)).iterator();
         return iter.hasNext() ? (Class)iter.next() : null;
      } catch (Exception var3) {
         logger.log(Level.FINE, "Unable to find from OSGi: javax.xml.bind.JAXBContext");
         return null;
      }
   }

   private static Properties loadJAXBProperties(ClassLoader classLoader, String propFileName) throws JAXBException {
      Properties props = null;

      try {
         URL url;
         if (classLoader == null) {
            url = ClassLoader.getSystemResource(propFileName);
         } else {
            url = classLoader.getResource(propFileName);
         }

         if (url != null) {
            logger.log(Level.FINE, (String)"loading props from {0}", (Object)url);
            props = new Properties();
            InputStream is = url.openStream();
            props.load(is);
            is.close();
         }

         return props;
      } catch (IOException var5) {
         logger.log(Level.FINE, (String)("Unable to load " + propFileName), (Throwable)var5);
         throw new JAXBException(var5.toString(), var5);
      }
   }

   static URL which(Class clazz, ClassLoader loader) {
      String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
      if (loader == null) {
         loader = getSystemClassLoader();
      }

      return loader.getResource(classnameAsResource);
   }

   static URL which(Class clazz) {
      return which(clazz, getClassClassLoader(clazz));
   }

   private static Class safeLoadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
      logger.log(Level.FINE, (String)"Trying to load {0}", (Object)className);

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
         if ("com.sun.xml.internal.bind.v2.ContextFactory".equals(className)) {
            return Class.forName(className);
         } else {
            throw var4;
         }
      }
   }

   private static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   private static ClassLoader getClassClassLoader(final Class c) {
      return System.getSecurityManager() == null ? c.getClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return c.getClassLoader();
         }
      });
   }

   private static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }

   static {
      try {
         if (AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("jaxb.debug"))) != null) {
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);
            ConsoleHandler handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
            logger.addHandler(handler);
         }
      } catch (Throwable var1) {
      }

   }
}
