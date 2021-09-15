package javax.management.remote;

import javax.management.MBeanServer;

public interface MBeanServerForwarder extends MBeanServer {
   MBeanServer getMBeanServer();

   void setMBeanServer(MBeanServer var1);
}
