package javax.xml.xpath;

import com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class XPathFactoryFinder {
   private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xpath.internal";
   private static final SecuritySupport ss = new SecuritySupport();
   private static boolean debug = false;
   private static final Properties cacheProps;
   private static volatile boolean firstTime;
   private final ClassLoader classLoader;
   private static final Class<XPathFactory> SERVICE_CLASS;

   private static void debugPrintln(String msg) {
      if (debug) {
         System.err.println("JAXP: " + msg);
      }

   }

   public XPathFactoryFinder(ClassLoader loader) {
      this.classLoader = loader;
      if (debug) {
         this.debugDisplayClassLoader();
      }

   }

   private void debugDisplayClassLoader() {
      try {
         if (this.classLoader == ss.getContextClassLoader()) {
            debugPrintln("using thread context class loader (" + this.classLoader + ") for search");
            return;
         }
      } catch (Throwable var2) {
      }

      if (this.classLoader == ClassLoader.getSystemClassLoader()) {
         debugPrintln("using system class loader (" + this.classLoader + ") for search");
      } else {
         debugPrintln("using class loader (" + this.classLoader + ") for search");
      }
   }

   public XPathFactory newFactory(String uri) throws XPathFactoryConfigurationException {
      if (uri == null) {
         throw new NullPointerException();
      } else {
         XPathFactory f = this._newFactory(uri);
         if (f != null) {
            debugPrintln("factory '" + f.getClass().getName() + "' was found for " + uri);
         } else {
            debugPrintln("unable to find a factory for " + uri);
         }

         return f;
      }
   }

   private XPathFactory _newFactory(String uri) throws XPathFactoryConfigurationException {
      XPathFactory xpathFactory = null;
      String propertyName = SERVICE_CLASS.getName() + ":" + uri;

      String javah;
      try {
         debugPrintln("Looking up system property '" + propertyName + "'");
         javah = ss.getSystemProperty(propertyName);
         if (javah != null) {
            debugPrintln("The value is '" + javah + "'");
            xpathFactory = this.createInstance(javah);
            if (xpathFactory != null) {
               return xpathFactory;
            }
         } else {
            debugPrintln("The property is undefined.");
         }
      } catch (Throwable var11) {
         if (debug) {
            debugPrintln("failed to look up system property '" + propertyName + "'");
            var11.printStackTrace();
         }
      }

      javah = ss.getSystemProperty("java.home");
      String configFile = javah + File.separator + "lib" + File.separator + "jaxp.properties";

      try {
         if (firstTime) {
            synchronized(cacheProps) {
               if (firstTime) {
                  File f = new File(configFile);
                  firstTime = false;
                  if (ss.doesFileExist(f)) {
                     debugPrintln("Read properties file " + f);
                     cacheProps.load((InputStream)ss.getFileInputStream(f));
                  }
               }
            }
         }

         String factoryClassName = cacheProps.getProperty(propertyName);
         debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties");
         if (factoryClassName != null) {
            xpathFactory = this.createInstance(factoryClassName);
            if (xpathFactory != null) {
               return xpathFactory;
            }
         }
      } catch (Exception var10) {
         if (debug) {
            var10.printStackTrace();
         }
      }

      assert xpathFactory == null;

      xpathFactory = this.findServiceProvider(uri);
      if (xpathFactory != null) {
         return xpathFactory;
      } else if (uri.equals("http://java.sun.com/jaxp/xpath/dom")) {
         debugPrintln("attempting to use the platform default W3C DOM XPath lib");
         return new XPathFactoryImpl();
      } else {
         debugPrintln("all things were tried, but none was found. bailing out.");
         return null;
      }
   }

   private Class<?> createClass(String className) {
      boolean internal = false;
      if (System.getSecurityManager() != null && className != null && className.startsWith("com.sun.org.apache.xpath.internal")) {
         internal = true;
      }

      try {
         Class clazz;
         if (this.classLoader != null && !internal) {
            clazz = Class.forName(className, false, this.classLoader);
         } else {
            clazz = Class.forName(className);
         }

         return clazz;
      } catch (Throwable var5) {
         if (debug) {
            var5.printStackTrace();
         }

         return null;
      }
   }

   XPathFactory createInstance(String className) throws XPathFactoryConfigurationException {
      XPathFactory xPathFactory = null;
      debugPrintln("createInstance(" + className + ")");
      Class<?> clazz = this.createClass(className);
      if (clazz == null) {
         debugPrintln("failed to getClass(" + className + ")");
         return null;
      } else {
         debugPrintln("loaded " + className + " from " + which(clazz));

         try {
            xPathFactory = (XPathFactory)clazz.newInstance();
            return xPathFactory;
         } catch (ClassCastException var5) {
            debugPrintln("could not instantiate " + clazz.getName());
            if (debug) {
               var5.printStackTrace();
            }

            return null;
         } catch (IllegalAccessException var6) {
            debugPrintln("could not instantiate " + clazz.getName());
            if (debug) {
               var6.printStackTrace();
            }

            return null;
         } catch (InstantiationException var7) {
            debugPrintln("could not instantiate " + clazz.getName());
            if (debug) {
               var7.printStackTrace();
            }

            return null;
         }
      }
   }

   private boolean isObjectModelSupportedBy(final XPathFactory factory, final String objectModel, AccessControlContext acc) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return factory.isObjectModelSupported(objectModel);
         }
      }, acc);
   }

   private XPathFactory findServiceProvider(final String objectModel) throws XPathFactoryConfigurationException {
      assert objectModel != null;

      final AccessControlContext acc = AccessController.getContext();

      try {
         return (XPathFactory)AccessController.doPrivileged(new PrivilegedAction<XPathFactory>() {
            public XPathFactory run() {
               ServiceLoader<XPathFactory> loader = ServiceLoader.load(XPathFactoryFinder.SERVICE_CLASS);
               Iterator var2 = loader.iterator();

               XPathFactory factory;
               do {
                  if (!var2.hasNext()) {
                     return null;
                  }

                  factory = (XPathFactory)var2.next();
               } while(!XPathFactoryFinder.this.isObjectModelSupportedBy(factory, objectModel, acc));

               return factory;
            }
         });
      } catch (ServiceConfigurationError var4) {
         throw new XPathFactoryConfigurationException(var4);
      }
   }

   private static String which(Class clazz) {
      return which(clazz.getName(), clazz.getClassLoader());
   }

   private static String which(String classname, ClassLoader loader) {
      String classnameAsResource = classname.replace('.', '/') + ".class";
      if (loader == null) {
         loader = ClassLoader.getSystemClassLoader();
      }

      URL it = ss.getResourceAsURL(loader, classnameAsResource);
      return it != null ? it.toString() : null;
   }

   static {
      try {
         debug = ss.getSystemProperty("jaxp.debug") != null;
      } catch (Exception var1) {
         debug = false;
      }

      cacheProps = new Properties();
      firstTime = true;
      SERVICE_CLASS = XPathFactory.class;
   }
}
