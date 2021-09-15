package javax.management;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

public class MBeanServerBuilder {
   public MBeanServerDelegate newMBeanServerDelegate() {
      return JmxMBeanServer.newMBeanServerDelegate();
   }

   public MBeanServer newMBeanServer(String var1, MBeanServer var2, MBeanServerDelegate var3) {
      return JmxMBeanServer.newMBeanServer(var1, var2, var3, false);
   }
}
