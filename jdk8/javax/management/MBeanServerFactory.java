package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javax.management.loading.ClassLoaderRepository;
import sun.reflect.misc.ReflectUtil;

public class MBeanServerFactory {
   private static MBeanServerBuilder builder = null;
   private static final ArrayList<MBeanServer> mBeanServerList = new ArrayList();

   private MBeanServerFactory() {
   }

   public static void releaseMBeanServer(MBeanServer var0) {
      checkPermission("releaseMBeanServer");
      removeMBeanServer(var0);
   }

   public static MBeanServer createMBeanServer() {
      return createMBeanServer((String)null);
   }

   public static MBeanServer createMBeanServer(String var0) {
      checkPermission("createMBeanServer");
      MBeanServer var1 = newMBeanServer(var0);
      addMBeanServer(var1);
      return var1;
   }

   public static MBeanServer newMBeanServer() {
      return newMBeanServer((String)null);
   }

   public static MBeanServer newMBeanServer(String var0) {
      checkPermission("newMBeanServer");
      MBeanServerBuilder var1 = getNewMBeanServerBuilder();
      synchronized(var1) {
         MBeanServerDelegate var3 = var1.newMBeanServerDelegate();
         if (var3 == null) {
            throw new JMRuntimeException("MBeanServerBuilder.newMBeanServerDelegate() returned null");
         } else {
            MBeanServer var4 = var1.newMBeanServer(var0, (MBeanServer)null, var3);
            if (var4 == null) {
               throw new JMRuntimeException("MBeanServerBuilder.newMBeanServer() returned null");
            } else {
               return var4;
            }
         }
      }
   }

   public static synchronized ArrayList<MBeanServer> findMBeanServer(String var0) {
      checkPermission("findMBeanServer");
      if (var0 == null) {
         return new ArrayList(mBeanServerList);
      } else {
         ArrayList var1 = new ArrayList();
         Iterator var2 = mBeanServerList.iterator();

         while(var2.hasNext()) {
            MBeanServer var3 = (MBeanServer)var2.next();
            String var4 = mBeanServerId(var3);
            if (var0.equals(var4)) {
               var1.add(var3);
            }
         }

         return var1;
      }
   }

   public static ClassLoaderRepository getClassLoaderRepository(MBeanServer var0) {
      return var0.getClassLoaderRepository();
   }

   private static String mBeanServerId(MBeanServer var0) {
      try {
         return (String)var0.getAttribute(MBeanServerDelegate.DELEGATE_NAME, "MBeanServerId");
      } catch (JMException var2) {
         JmxProperties.MISC_LOGGER.finest("Ignoring exception while getting MBeanServerId: " + var2);
         return null;
      }
   }

   private static void checkPermission(String var0) throws SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         MBeanServerPermission var2 = new MBeanServerPermission(var0);
         var1.checkPermission(var2);
      }

   }

   private static synchronized void addMBeanServer(MBeanServer var0) {
      mBeanServerList.add(var0);
   }

   private static synchronized void removeMBeanServer(MBeanServer var0) {
      boolean var1 = mBeanServerList.remove(var0);
      if (!var1) {
         JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "removeMBeanServer(MBeanServer)", "MBeanServer was not in list!");
         throw new IllegalArgumentException("MBeanServer was not in list!");
      }
   }

   private static Class<?> loadBuilderClass(String var0) throws ClassNotFoundException {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();
      return var1 != null ? var1.loadClass(var0) : ReflectUtil.forName(var0);
   }

   private static MBeanServerBuilder newBuilder(Class<?> var0) {
      try {
         Object var1 = var0.newInstance();
         return (MBeanServerBuilder)var1;
      } catch (RuntimeException var3) {
         throw var3;
      } catch (Exception var4) {
         String var2 = "Failed to instantiate a MBeanServerBuilder from " + var0 + ": " + var4;
         throw new JMRuntimeException(var2, var4);
      }
   }

   private static synchronized void checkMBeanServerBuilder() {
      try {
         GetPropertyAction var0 = new GetPropertyAction("javax.management.builder.initial");
         String var6 = (String)AccessController.doPrivileged((PrivilegedAction)var0);

         try {
            Class var2;
            if (var6 != null && var6.length() != 0) {
               var2 = loadBuilderClass(var6);
            } else {
               var2 = MBeanServerBuilder.class;
            }

            if (builder != null) {
               Class var7 = builder.getClass();
               if (var2 == var7) {
                  return;
               }
            }

            builder = newBuilder(var2);
         } catch (ClassNotFoundException var4) {
            String var3 = "Failed to load MBeanServerBuilder class " + var6 + ": " + var4;
            throw new JMRuntimeException(var3, var4);
         }
      } catch (RuntimeException var5) {
         if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
            StringBuilder var1 = (new StringBuilder()).append("Failed to instantiate MBeanServerBuilder: ").append((Object)var5).append("\n\t\tCheck the value of the ").append("javax.management.builder.initial").append(" property.");
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "checkMBeanServerBuilder", var1.toString());
         }

         throw var5;
      }
   }

   private static synchronized MBeanServerBuilder getNewMBeanServerBuilder() {
      checkMBeanServerBuilder();
      return builder;
   }
}
