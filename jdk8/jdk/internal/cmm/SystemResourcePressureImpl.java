package jdk.internal.cmm;

import java.lang.management.ManagementPermission;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import jdk.management.cmm.SystemResourcePressureMXBean;

public final class SystemResourcePressureImpl extends NotificationBroadcasterSupport implements SystemResourcePressureMXBean {
   private static final int MIN_PRESSURE_LEVEL = 0;
   private static final int MAX_PRESSURE_LEVEL = 10;
   public static final String RESOURCE_PRESSURE_MXBEAN_NAME = "com.oracle.management:type=ResourcePressureMBean";
   private static final String MEM_PRESSURE_ATTRIBUTE_NAME = "MemoryPressure";
   private long notifSeqNum;
   private static ManagementPermission controlPermission = new ManagementPermission("control");

   public SystemResourcePressureImpl() {
      super(new MBeanNotificationInfo(new String[]{"jmx.attribute.change"}, AttributeChangeNotification.class.getName(), "Notification that Memory pressure level has changed"));
   }

   public synchronized int getMemoryPressure() {
      return this.getVmMemoryPressure();
   }

   public void setMemoryPressure(int var1) {
      if (var1 >= 0 && var1 <= 10) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(controlPermission);
         }

         AttributeChangeNotification var3;
         synchronized(this) {
            int var5 = this.setVmMemoryPressure(var1);
            if (var1 == var5) {
               return;
            }

            var3 = new AttributeChangeNotification(this, ++this.notifSeqNum, System.currentTimeMillis(), "Memory pressure level change detected", "MemoryPressure", "int", var5, var1);
         }

         this.sendNotification(var3);
      } else {
         throw new IllegalArgumentException("Invalid pressure level: " + var1);
      }
   }

   private native int setVmMemoryPressure(int var1);

   private native int getVmMemoryPressure();

   public ObjectName getObjectName() {
      try {
         return ObjectName.getInstance("com.oracle.management:type=ResourcePressureMBean");
      } catch (MalformedObjectNameException var2) {
         throw new InternalError();
      }
   }
}
