package javax.management;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.Util;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MBeanServerDelegate implements MBeanServerDelegateMBean, NotificationEmitter {
   private String mbeanServerId;
   private final NotificationBroadcasterSupport broadcaster = new NotificationBroadcasterSupport();
   private static long oldStamp = 0L;
   private final long stamp = getStamp();
   private long sequenceNumber = 1L;
   private static final MBeanNotificationInfo[] notifsInfo;
   public static final ObjectName DELEGATE_NAME;

   public synchronized String getMBeanServerId() {
      if (this.mbeanServerId == null) {
         String var1;
         try {
            var1 = InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException var3) {
            JmxProperties.MISC_LOGGER.finest("Can't get local host name, using \"localhost\" instead. Cause is: " + var3);
            var1 = "localhost";
         }

         this.mbeanServerId = var1 + "_" + this.stamp;
      }

      return this.mbeanServerId;
   }

   public String getSpecificationName() {
      return "Java Management Extensions";
   }

   public String getSpecificationVersion() {
      return "1.4";
   }

   public String getSpecificationVendor() {
      return "Oracle Corporation";
   }

   public String getImplementationName() {
      return "JMX";
   }

   public String getImplementationVersion() {
      try {
         return System.getProperty("java.runtime.version");
      } catch (SecurityException var2) {
         return "";
      }
   }

   public String getImplementationVendor() {
      return "Oracle Corporation";
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      int var1 = notifsInfo.length;
      MBeanNotificationInfo[] var2 = new MBeanNotificationInfo[var1];
      System.arraycopy(notifsInfo, 0, var2, 0, var1);
      return var2;
   }

   public synchronized void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws IllegalArgumentException {
      this.broadcaster.addNotificationListener(var1, var2, var3);
   }

   public synchronized void removeNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      this.broadcaster.removeNotificationListener(var1, var2, var3);
   }

   public synchronized void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      this.broadcaster.removeNotificationListener(var1);
   }

   public void sendNotification(Notification var1) {
      if (var1.getSequenceNumber() < 1L) {
         synchronized(this) {
            var1.setSequenceNumber((long)(this.sequenceNumber++));
         }
      }

      this.broadcaster.sendNotification(var1);
   }

   private static synchronized long getStamp() {
      long var0 = System.currentTimeMillis();
      if (oldStamp >= var0) {
         var0 = oldStamp + 1L;
      }

      oldStamp = var0;
      return var0;
   }

   static {
      String[] var0 = new String[]{"JMX.mbean.unregistered", "JMX.mbean.registered"};
      notifsInfo = new MBeanNotificationInfo[1];
      notifsInfo[0] = new MBeanNotificationInfo(var0, "javax.management.MBeanServerNotification", "Notifications sent by the MBeanServerDelegate MBean");
      DELEGATE_NAME = Util.newObjectName("JMImplementation:type=MBeanServerDelegate");
   }
}
