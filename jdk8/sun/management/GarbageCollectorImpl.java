package sun.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import java.lang.management.MemoryPoolMXBean;
import java.util.Iterator;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

class GarbageCollectorImpl extends MemoryManagerImpl implements GarbageCollectorMXBean {
   private String[] poolNames = null;
   private GcInfoBuilder gcInfoBuilder;
   private static final String notifName = "javax.management.Notification";
   private static final String[] gcNotifTypes = new String[]{"com.sun.management.gc.notification"};
   private static long seqNumber = 0L;

   GarbageCollectorImpl(String var1) {
      super(var1);
   }

   public native long getCollectionCount();

   public native long getCollectionTime();

   synchronized String[] getAllPoolNames() {
      if (this.poolNames == null) {
         List var1 = java.lang.management.ManagementFactory.getMemoryPoolMXBeans();
         this.poolNames = new String[var1.size()];
         int var2 = 0;

         MemoryPoolMXBean var4;
         for(Iterator var3 = var1.iterator(); var3.hasNext(); this.poolNames[var2++] = var4.getName()) {
            var4 = (MemoryPoolMXBean)var3.next();
         }
      }

      return this.poolNames;
   }

   private synchronized GcInfoBuilder getGcInfoBuilder() {
      if (this.gcInfoBuilder == null) {
         this.gcInfoBuilder = new GcInfoBuilder(this, this.getAllPoolNames());
      }

      return this.gcInfoBuilder;
   }

   public GcInfo getLastGcInfo() {
      GcInfo var1 = this.getGcInfoBuilder().getLastGcInfo();
      return var1;
   }

   public MBeanNotificationInfo[] getNotificationInfo() {
      return new MBeanNotificationInfo[]{new MBeanNotificationInfo(gcNotifTypes, "javax.management.Notification", "GC Notification")};
   }

   private static long getNextSeqNumber() {
      return ++seqNumber;
   }

   void createGCNotification(long var1, String var3, String var4, String var5, GcInfo var6) {
      if (this.hasListeners()) {
         Notification var7 = new Notification("com.sun.management.gc.notification", this.getObjectName(), getNextSeqNumber(), var1, var3);
         GarbageCollectionNotificationInfo var8 = new GarbageCollectionNotificationInfo(var3, var4, var5, var6);
         CompositeData var9 = GarbageCollectionNotifInfoCompositeData.toCompositeData(var8);
         var7.setUserData(var9);
         this.sendNotification(var7);
      }
   }

   public synchronized void addNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) {
      boolean var4 = this.hasListeners();
      super.addNotificationListener(var1, var2, var3);
      boolean var5 = this.hasListeners();
      if (!var4 && var5) {
         this.setNotificationEnabled(this, true);
      }

   }

   public synchronized void removeNotificationListener(NotificationListener var1) throws ListenerNotFoundException {
      boolean var2 = this.hasListeners();
      super.removeNotificationListener(var1);
      boolean var3 = this.hasListeners();
      if (var2 && !var3) {
         this.setNotificationEnabled(this, false);
      }

   }

   public synchronized void removeNotificationListener(NotificationListener var1, NotificationFilter var2, Object var3) throws ListenerNotFoundException {
      boolean var4 = this.hasListeners();
      super.removeNotificationListener(var1, var2, var3);
      boolean var5 = this.hasListeners();
      if (var4 && !var5) {
         this.setNotificationEnabled(this, false);
      }

   }

   public ObjectName getObjectName() {
      return Util.newObjectName("java.lang:type=GarbageCollector", this.getName());
   }

   native void setNotificationEnabled(GarbageCollectorMXBean var1, boolean var2);
}
