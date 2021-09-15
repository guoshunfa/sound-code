package javax.management.remote;

import java.io.IOException;
import java.util.Map;

public interface JMXConnectorProvider {
   JMXConnector newJMXConnector(JMXServiceURL var1, Map<String, ?> var2) throws IOException;
}
