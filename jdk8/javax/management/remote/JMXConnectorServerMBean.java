package javax.management.remote;

import java.io.IOException;
import java.util.Map;

public interface JMXConnectorServerMBean {
   void start() throws IOException;

   void stop() throws IOException;

   boolean isActive();

   void setMBeanServerForwarder(MBeanServerForwarder var1);

   String[] getConnectionIds();

   JMXServiceURL getAddress();

   Map<String, ?> getAttributes();

   JMXConnector toJMXConnector(Map<String, ?> var1) throws IOException;
}
