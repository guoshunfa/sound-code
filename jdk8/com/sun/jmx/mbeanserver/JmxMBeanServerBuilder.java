package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerDelegate;

public class JmxMBeanServerBuilder extends MBeanServerBuilder {
   public MBeanServerDelegate newMBeanServerDelegate() {
      return JmxMBeanServer.newMBeanServerDelegate();
   }

   public MBeanServer newMBeanServer(String var1, MBeanServer var2, MBeanServerDelegate var3) {
      return JmxMBeanServer.newMBeanServer(var1, var2, var3, true);
   }
}
