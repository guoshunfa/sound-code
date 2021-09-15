package javax.management.remote;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.security.auth.Subject;

public interface JMXConnector extends Closeable {
   String CREDENTIALS = "jmx.remote.credentials";

   void connect() throws IOException;

   void connect(Map<String, ?> var1) throws IOException;

   MBeanServerConnection getMBeanServerConnection() throws IOException;

   MBeanServerConnection getMBeanServerConnection(Subject var1) throws IOException;

   void close() throws IOException;

   void addConnectionNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3);

   void removeConnectionNotificationListener(NotificationListener var1) throws ListenerNotFoundException;

   void removeConnectionNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException;

   String getConnectionId() throws IOException;
}
