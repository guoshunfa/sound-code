package com.sun.jmx.remote.protocol.iiop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;

public class ClientProvider implements JMXConnectorProvider {
   public JMXConnector newJMXConnector(JMXServiceURL var1, Map<String, ?> var2) throws IOException {
      if (!var1.getProtocol().equals("iiop")) {
         throw new MalformedURLException("Protocol not iiop: " + var1.getProtocol());
      } else {
         return new RMIConnector(var1, var2);
      }
   }
}
