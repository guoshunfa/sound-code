package javax.management.remote;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import sun.reflect.misc.ReflectUtil;

public class JMXConnectorFactory {
   public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
   public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
   public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
   private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorFactory");

   private JMXConnectorFactory() {
   }

   public static JMXConnector connect(JMXServiceURL var0) throws IOException {
      return connect(var0, (Map)null);
   }

   public static JMXConnector connect(JMXServiceURL var0, Map<String, ?> var1) throws IOException {
      if (var0 == null) {
         throw new NullPointerException("Null JMXServiceURL");
      } else {
         JMXConnector var2 = newJMXConnector(var0, var1);
         var2.connect(var1);
         return var2;
      }
   }

   private static <K, V> Map<K, V> newHashMap() {
      return new HashMap();
   }

   private static <K> Map<K, Object> newHashMap(Map<K, ?> var0) {
      return new HashMap(var0);
   }

   public static JMXConnector newJMXConnector(JMXServiceURL var0, Map<String, ?> var1) throws IOException {
      Map var2;
      if (var1 == null) {
         var2 = newHashMap();
      } else {
         EnvHelp.checkAttributes(var1);
         var2 = newHashMap(var1);
      }

      ClassLoader var3 = resolveClassLoader(var2);
      Class var4 = JMXConnectorProvider.class;
      String var5 = var0.getProtocol();
      JMXServiceURL var7 = var0;
      JMXConnectorProvider var8 = (JMXConnectorProvider)getProvider(var0, var2, "ClientProvider", var4, var3);
      IOException var9 = null;
      if (var8 == null) {
         if (var3 != null) {
            try {
               JMXConnector var10 = getConnectorAsService(var3, var7, var2);
               if (var10 != null) {
                  return var10;
               }
            } catch (JMXProviderException var11) {
               throw var11;
            } catch (IOException var12) {
               var9 = var12;
            }
         }

         var8 = (JMXConnectorProvider)getProvider(var5, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ClientProvider", var4);
      }

      if (var8 == null) {
         MalformedURLException var14 = new MalformedURLException("Unsupported protocol: " + var5);
         if (var9 == null) {
            throw var14;
         } else {
            throw (MalformedURLException)EnvHelp.initCause(var14, var9);
         }
      } else {
         Map var13 = Collections.unmodifiableMap(var2);
         return var8.newJMXConnector(var0, var13);
      }
   }

   private static String resolvePkgs(Map<String, ?> var0) throws JMXProviderException {
      Object var1 = null;
      if (var0 != null) {
         var1 = var0.get("jmx.remote.protocol.provider.pkgs");
      }

      if (var1 == null) {
         var1 = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return System.getProperty("jmx.remote.protocol.provider.pkgs");
            }
         });
      }

      if (var1 == null) {
         return null;
      } else {
         String var2;
         if (!(var1 instanceof String)) {
            var2 = "Value of jmx.remote.protocol.provider.pkgs parameter is not a String: " + var1.getClass().getName();
            throw new JMXProviderException(var2);
         } else {
            var2 = (String)var1;
            if (var2.trim().equals("")) {
               return null;
            } else if (!var2.startsWith("|") && !var2.endsWith("|") && var2.indexOf("||") < 0) {
               return var2;
            } else {
               String var3 = "Value of jmx.remote.protocol.provider.pkgs contains an empty element: " + var2;
               throw new JMXProviderException(var3);
            }
         }
      }
   }

   static <T> T getProvider(JMXServiceURL var0, Map<String, Object> var1, String var2, Class<T> var3, ClassLoader var4) throws IOException {
      String var5 = var0.getProtocol();
      String var6 = resolvePkgs(var1);
      Object var7 = null;
      if (var6 != null) {
         var7 = getProvider(var5, var6, var4, var2, var3);
         if (var7 != null) {
            boolean var8 = var4 != var7.getClass().getClassLoader();
            var1.put("jmx.remote.protocol.provider.class.loader", var8 ? wrap(var4) : var4);
         }
      }

      return var7;
   }

   static <T> Iterator<T> getProviderIterator(Class<T> var0, ClassLoader var1) {
      ServiceLoader var2 = ServiceLoader.load(var0, var1);
      return var2.iterator();
   }

   private static ClassLoader wrap(final ClassLoader var0) {
      return var0 != null ? (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return new ClassLoader(var0) {
               protected Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
                  ReflectUtil.checkPackageAccess(var1);
                  return super.loadClass(var1, var2);
               }
            };
         }
      }) : null;
   }

   private static JMXConnector getConnectorAsService(ClassLoader var0, JMXServiceURL var1, Map<String, ?> var2) throws IOException {
      Iterator var3 = getProviderIterator(JMXConnectorProvider.class, var0);
      IOException var5 = null;

      while(var3.hasNext()) {
         JMXConnectorProvider var6 = (JMXConnectorProvider)var3.next();

         try {
            JMXConnector var4 = var6.newJMXConnector(var1, var2);
            return var4;
         } catch (JMXProviderException var8) {
            throw var8;
         } catch (Exception var9) {
            if (logger.traceOn()) {
               logger.trace("getConnectorAsService", "URL[" + var1 + "] Service provider exception: " + var9);
            }

            if (!(var9 instanceof MalformedURLException) && var5 == null) {
               if (var9 instanceof IOException) {
                  var5 = (IOException)var9;
               } else {
                  var5 = (IOException)EnvHelp.initCause(new IOException(var9.getMessage()), var9);
               }
            }
         }
      }

      if (var5 == null) {
         return null;
      } else {
         throw var5;
      }
   }

   static <T> T getProvider(String var0, String var1, ClassLoader var2, String var3, Class<T> var4) throws IOException {
      StringTokenizer var5 = new StringTokenizer(var1, "|");

      while(true) {
         if (var5.hasMoreTokens()) {
            String var6 = var5.nextToken();
            String var7 = var6 + "." + protocol2package(var0) + "." + var3;

            Class var8;
            try {
               var8 = Class.forName(var7, true, var2);
            } catch (ClassNotFoundException var13) {
               continue;
            }

            if (!var4.isAssignableFrom(var8)) {
               String var14 = "Provider class does not implement " + var4.getName() + ": " + var8.getName();
               throw new JMXProviderException(var14);
            }

            Class var9 = (Class)Util.cast(var8);

            try {
               return var9.newInstance();
            } catch (Exception var12) {
               String var11 = "Exception when instantiating provider [" + var7 + "]";
               throw new JMXProviderException(var11, var12);
            }
         }

         return null;
      }
   }

   static ClassLoader resolveClassLoader(Map<String, ?> var0) {
      ClassLoader var1 = null;
      if (var0 != null) {
         try {
            var1 = (ClassLoader)var0.get("jmx.remote.protocol.provider.class.loader");
         } catch (ClassCastException var4) {
            throw new IllegalArgumentException("The ClassLoader supplied in the environment map using the jmx.remote.protocol.provider.class.loader attribute is not an instance of java.lang.ClassLoader");
         }
      }

      if (var1 == null) {
         var1 = Thread.currentThread().getContextClassLoader();
      }

      return var1;
   }

   private static String protocol2package(String var0) {
      return var0.replace('+', '.').replace('-', '_');
   }
}
