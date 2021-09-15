package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

/** @deprecated */
@Deprecated
public class DefaultLoaderRepository {
   public static Class<?> loadClass(String var0) throws ClassNotFoundException {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClass", var0);
      return load((ClassLoader)null, var0);
   }

   public static Class<?> loadClassWithout(ClassLoader var0, String var1) throws ClassNotFoundException {
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClassWithout", var1);
      return load(var0, var1);
   }

   private static Class<?> load(ClassLoader var0, String var1) throws ClassNotFoundException {
      ArrayList var2 = MBeanServerFactory.findMBeanServer((String)null);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         MBeanServer var4 = (MBeanServer)var3.next();
         ClassLoaderRepository var5 = var4.getClassLoaderRepository();

         try {
            return var5.loadClassWithout(var0, var1);
         } catch (ClassNotFoundException var7) {
         }
      }

      throw new ClassNotFoundException(var1);
   }
}
