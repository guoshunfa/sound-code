package com.sun.jmx.remote.protocol.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;

public class ServerProvider implements JMXConnectorServerProvider {
   public JMXConnectorServer newJMXConnectorServer(JMXServiceURL var1, Map<String, ?> var2, MBeanServer var3) throws IOException {
      if (!var1.getProtocol().equals("rmi")) {
         throw new MalformedURLException("Protocol not rmi: " + var1.getProtocol());
      } else {
         return new RMIConnectorServer(var1, var2, var3);
      }
   }
}
