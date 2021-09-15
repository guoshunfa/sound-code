package javax.management.remote;

import java.io.IOException;
import java.util.Map;
import javax.management.MBeanServer;

public interface JMXConnectorServerProvider {
   JMXConnectorServer newJMXConnectorServer(JMXServiceURL var1, Map<String, ?> var2, MBeanServer var3) throws IOException;
}
