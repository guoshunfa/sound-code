package sun.management;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import sun.misc.VM;

class MemoryImpl extends NotificationEmitterSupport implements MemoryMXBean {
   private final VMManagement jvm;
   private static MemoryPoolMXBean[] pools = null;
   private static MemoryManagerMXBean[] mgrs = null;
   private static final String notifName = "javax.management.Notification";
   private static final String[] notifTypes = new String[]{"java.management.memory.threshold.exceeded", "java.management.memory.collection.threshold.exceeded"};
   private static final String[] notifMsgs = new String[]{"Memory usage exceeds usage threshold", "Memory usage exceeds collection usage threshold"};
   private static long seqNumber = 0L;

   MemoryImpl(VMManagement var1) {
      this.jvm = var1;
   }

   public int getObjectPendingFinalizationCount() {
      return VM.getFinalRefCount();
   }

   public void gc() {
      Runtime.getRuntime().gc();
   }

   public MemoryUsage getHeapMemoryUsage() {
      return this.getMemoryUsage0(true);
   }

   public MemoryUsage getNonHeapMemoryUsage() {
      return this.getMemoryUsage0(false);
   }

   public boolean isVerbose() {
      return this.jvm.getVerboseGC();
   }

   public void setVerbose(boolean var1) {
      Util.checkControlAccess();
      this.setVerboseGC(var1);
   }

   static synchronized MemoryPoolMXBean[] getMemoryPools() {
      if (pools == null) {
         pools = getMemoryPools0();
      }

      return pools;
   }

   static synchronized MemoryManagerMXBean[] getMemoryManagers() {
      if (mgrs == null) {
         mgrs = getMemoryManagers0();
      }

      return mgrs;
   }

   private static native MemoryPoolMXBean[] getMemoryPools0();

   private static native MemoryManagerMXBean[] getMemoryManagers0();

   private native MemoryUsage getMemoryUsage0(boolean var1);

   private native void setVerboseGC(boolean var1);

   public MBeanNotificationInfo[] getNotificationInfo() {
      return new MBeanNotificationInfo[]{new MBeanNotificationInfo(notifTypes, "javax.management.Notification", "Memory Notification")};
   }

   private static String getNotifMsg(String var0) {
      for(int var1 = 0; var1 < notifTypes.length; ++var1) {
         if (var0 == notifTypes[var1]) {
            return notifMsgs[var1];
         }
      }

      return "Unknown message";
   }

   private static long getNextSeqNumber() {
      return ++seqNumber;
   }

   static void createNotification(String var0, String var1, MemoryUsage var2, long var3) {
      MemoryImpl var5 = (MemoryImpl)java.lang.management.ManagementFactory.getMemoryMXBean();
      if (var5.hasListeners()) {
         long var6 = System.currentTimeMillis();
         String var8 = getNotifMsg(var0);
         Notification var9 = new Notification(var0, var5.getObjectName(), getNextSeqNumber(), var6, var8);
         MemoryNotificationInfo var10 = new MemoryNotificationInfo(var1, var2, var3);
         CompositeData var11 = MemoryNotifInfoCompositeData.toCompositeData(var10);
         var9.setUserData(var11);
         var5.sendNotification(var9);
      }
   }

   public ObjectName getObjectName() {
      return Util.newObjectName("java.lang:type=Memory");
   }
}
