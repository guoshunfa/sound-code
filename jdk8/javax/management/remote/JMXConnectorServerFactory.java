package javax.management.remote;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanServer;

public class JMXConnectorServerFactory {
   public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
   public static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
   public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
   public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
   private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
   private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorServerFactory");

   private JMXConnectorServerFactory() {
   }

   private static JMXConnectorServer getConnectorServerAsService(ClassLoader var0, JMXServiceURL var1, Map<String, ?> var2, MBeanServer var3) throws IOException {
      Iterator var4 = JMXConnectorFactory.getProviderIterator(JMXConnectorServerProvider.class, var0);
      IOException var5 = null;

      while(var4.hasNext()) {
         try {
            return ((JMXConnectorServerProvider)var4.next()).newJMXConnectorServer(var1, var2, var3);
         } catch (JMXProviderException var7) {
            throw var7;
         } catch (Exception var8) {
            if (logger.traceOn()) {
               logger.trace("getConnectorAsService", "URL[" + var1 + "] Service provider exception: " + var8);
            }

            if (!(var8 instanceof MalformedURLException) && var5 == null) {
               if (var8 instanceof IOException) {
                  var5 = (IOException)var8;
               } else {
                  var5 = (IOException)EnvHelp.initCause(new IOException(var8.getMessage()), var8);
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

   public static JMXConnectorServer newJMXConnectorServer(JMXServiceURL var0, Map<String, ?> var1, MBeanServer var2) throws IOException {
      HashMap var3;
      if (var1 == null) {
         var3 = new HashMap();
      } else {
         EnvHelp.checkAttributes(var1);
         var3 = new HashMap(var1);
      }

      Class var4 = JMXConnectorServerProvider.class;
      ClassLoader var5 = JMXConnectorFactory.resolveClassLoader(var3);
      String var6 = var0.getProtocol();
      JMXConnectorServerProvider var8 = (JMXConnectorServerProvider)JMXConnectorFactory.getProvider((JMXServiceURL)var0, (Map)var3, (String)"ServerProvider", (Class)var4, (ClassLoader)var5);
      IOException var9 = null;
      if (var8 == null) {
         if (var5 != null) {
            try {
               JMXConnectorServer var10 = getConnectorServerAsService(var5, var0, var3, var2);
               if (var10 != null) {
                  return var10;
               }
            } catch (JMXProviderException var11) {
               throw var11;
            } catch (IOException var12) {
               var9 = var12;
            }
         }

         var8 = (JMXConnectorServerProvider)JMXConnectorFactory.getProvider(var6, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ServerProvider", var4);
      }

      if (var8 == null) {
         MalformedURLException var13 = new MalformedURLException("Unsupported protocol: " + var6);
         if (var9 == null) {
            throw var13;
         } else {
            throw (MalformedURLException)EnvHelp.initCause(var13, var9);
         }
      } else {
         Map var14 = Collections.unmodifiableMap(var3);
         return var8.newJMXConnectorServer(var0, var14, var2);
      }
   }
}
