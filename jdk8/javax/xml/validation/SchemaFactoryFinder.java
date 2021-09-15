package javax.xml.validation;

import com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory;
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

class SchemaFactoryFinder {
   private static boolean debug = false;
   private static final SecuritySupport ss = new SecuritySupport();
   private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";
   private static final Properties cacheProps = new Properties();
   private static volatile boolean firstTime = true;
   private final ClassLoader classLoader;
   private static final Class<SchemaFactory> SERVICE_CLASS;

   private static void debugPrintln(String msg) {
      if (debug) {
         System.err.println("JAXP: " + msg);
      }

   }

   public SchemaFactoryFinder(ClassLoader loader) {
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

   public SchemaFactory newFactory(String schemaLanguage) {
      if (schemaLanguage == null) {
         throw new NullPointerException();
      } else {
         SchemaFactory f = this._newFactory(schemaLanguage);
         if (f != null) {
            debugPrintln("factory '" + f.getClass().getName() + "' was found for " + schemaLanguage);
         } else {
            debugPrintln("unable to find a factory for " + schemaLanguage);
         }

         return f;
      }
   }

   private SchemaFactory _newFactory(String schemaLanguage) {
      String propertyName = SERVICE_CLASS.getName() + ":" + schemaLanguage;

      SchemaFactory sf;
      String javah;
      try {
         debugPrintln("Looking up system property '" + propertyName + "'");
         javah = ss.getSystemProperty(propertyName);
         if (javah != null) {
            debugPrintln("The value is '" + javah + "'");
            sf = this.createInstance(javah);
            if (sf != null) {
               return sf;
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
            sf = this.createInstance(factoryClassName);
            if (sf != null) {
               return sf;
            }
         }
      } catch (Exception var10) {
         if (debug) {
            var10.printStackTrace();
         }
      }

      SchemaFactory factoryImpl = this.findServiceProvider(schemaLanguage);
      if (factoryImpl != null) {
         return factoryImpl;
      } else if (schemaLanguage.equals("http://www.w3.org/2001/XMLSchema")) {
         debugPrintln("attempting to use the platform default XML Schema validator");
         return new XMLSchemaFactory();
      } else {
         debugPrintln("all things were tried, but none was found. bailing out.");
         return null;
      }
   }

   private Class<?> createClass(String className) {
      boolean internal = false;
      if (System.getSecurityManager() != null && className != null && className.startsWith("com.sun.org.apache.xerces.internal")) {
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

   SchemaFactory createInstance(String className) {
      SchemaFactory schemaFactory = null;
      debugPrintln("createInstance(" + className + ")");
      Class<?> clazz = this.createClass(className);
      if (clazz == null) {
         debugPrintln("failed to getClass(" + className + ")");
         return null;
      } else {
         debugPrintln("loaded " + className + " from " + which(clazz));

         try {
            if (!SchemaFactory.class.isAssignableFrom(clazz)) {
               throw new ClassCastException(clazz.getName() + " cannot be cast to " + SchemaFactory.class);
            } else {
               schemaFactory = (SchemaFactory)clazz.newInstance();
               return schemaFactory;
            }
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

   private boolean isSchemaLanguageSupportedBy(final SchemaFactory factory, final String schemaLanguage, AccessControlContext acc) {
      return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            return factory.isSchemaLanguageSupported(schemaLanguage);
         }
      }, acc);
   }

   private SchemaFactory findServiceProvider(final String schemaLanguage) {
      assert schemaLanguage != null;

      final AccessControlContext acc = AccessController.getContext();

      try {
         return (SchemaFactory)AccessController.doPrivileged(new PrivilegedAction<SchemaFactory>() {
            public SchemaFactory run() {
               ServiceLoader<SchemaFactory> loader = ServiceLoader.load(SchemaFactoryFinder.SERVICE_CLASS);
               Iterator var2 = loader.iterator();

               SchemaFactory factory;
               do {
                  if (!var2.hasNext()) {
                     return null;
                  }

                  factory = (SchemaFactory)var2.next();
               } while(!SchemaFactoryFinder.this.isSchemaLanguageSupportedBy(factory, schemaLanguage, acc));

               return factory;
            }
         });
      } catch (ServiceConfigurationError var4) {
         throw new SchemaFactoryConfigurationError("Provider for " + SERVICE_CLASS + " cannot be created", var4);
      }
   }

   private static String which(Class<?> clazz) {
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

      SERVICE_CLASS = SchemaFactory.class;
   }
}
