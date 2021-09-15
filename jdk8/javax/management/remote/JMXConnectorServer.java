package javax.management.remote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public abstract class JMXConnectorServer extends NotificationBroadcasterSupport implements JMXConnectorServerMBean, MBeanRegistration, JMXAddressable {
   public static final String AUTHENTICATOR = "jmx.remote.authenticator";
   private MBeanServer mbeanServer;
   private ObjectName myName;
   private final List<String> connectionIds;
   private static final int[] sequenceNumberLock = new int[0];
   private static long sequenceNumber;

   public JMXConnectorServer() {
      this((MBeanServer)null);
   }

   public JMXConnectorServer(MBeanServer var1) {
      this.mbeanServer = null;
      this.connectionIds = new ArrayList();
      this.mbeanServer = var1;
   }

   public synchronized MBeanServer getMBeanServer() {
      return this.mbeanServer;
   }

   public synchronized void setMBeanServerForwarder(MBeanServerForwarder var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid null argument: mbsf");
      } else {
         if (this.mbeanServer != null) {
            var1.setMBeanServer(this.mbeanServer);
         }

         this.mbeanServer = var1;
      }
   }

   public String[] getConnectionIds() {
      synchronized(this.connectionIds) {
         return (String[])this.connectionIds.toArray(new String[this.connectionIds.size()]);
      }
   }

   public JMXConnector toJMXConnector(Map<String, ?> var1) throws IOException {
      if (!this.isActive()) {
         throw new IllegalStateException("Connector is not active");
      } else {
         JMXServiceURL var2 = this.getAddress();
         return JMXConnectorFactory.newJMXConnector(var2, var1);
      }
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      String[] var1 = new String[]{"jmx.remote.connection.opened", "jmx.remote.connection.closed", "jmx.remote.connection.failed"};
      String var2 = JMXConnectionNotification.class.getName();
      return new MBeanNotificationInfo[]{new MBeanNotificationInfo(var1, var2, "A client connection has been opened or closed")};
   }

   protected void connectionOpened(String var1, String var2, Object var3) {
      if (var1 == null) {
         throw new NullPointerException("Illegal null argument");
      } else {
         synchronized(this.connectionIds) {
            this.connectionIds.add(var1);
         }

         this.sendNotification("jmx.remote.connection.opened", var1, var2, var3);
      }
   }

   protected void connectionClosed(String var1, String var2, Object var3) {
      if (var1 == null) {
         throw new NullPointerException("Illegal null argument");
      } else {
         synchronized(this.connectionIds) {
            this.connectionIds.remove(var1);
         }

         this.sendNotification("jmx.remote.connection.closed", var1, var2, var3);
      }
   }

   protected void connectionFailed(String var1, String var2, Object var3) {
      if (var1 == null) {
         throw new NullPointerException("Illegal null argument");
      } else {
         synchronized(this.connectionIds) {
            this.connectionIds.remove(var1);
         }

         this.sendNotification("jmx.remote.connection.failed", var1, var2, var3);
      }
   }

   private void sendNotification(String var1, String var2, String var3, Object var4) {
      JMXConnectionNotification var5 = new JMXConnectionNotification(var1, this.getNotificationSource(), var2, nextSequenceNumber(), var3, var4);
      this.sendNotification(var5);
   }

   private synchronized Object getNotificationSource() {
      return this.myName != null ? this.myName : this;
   }

   private static long nextSequenceNumber() {
      synchronized(sequenceNumberLock) {
         return (long)(sequenceNumber++);
      }
   }

   public synchronized ObjectName preRegister(MBeanServer var1, ObjectName var2) {
      if (var1 != null && var2 != null) {
         if (this.mbeanServer == null) {
            this.mbeanServer = var1;
            this.myName = var2;
         }

         return var2;
      } else {
         throw new NullPointerException("Null MBeanServer or ObjectName");
      }
   }

   public void postRegister(Boolean var1) {
   }

   public synchronized void preDeregister() throws Exception {
      if (this.myName != null && this.isActive()) {
         this.stop();
         this.myName = null;
      }

   }

   public void postDeregister() {
      this.myName = null;
   }
}
