package sun.management;

import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

class MemoryManagerImpl extends NotificationEmitterSupport implements MemoryManagerMXBean {
   private final String name;
   private final boolean isValid;
   private MemoryPoolMXBean[] pools;
   private MBeanNotificationInfo[] notifInfo = null;

   MemoryManagerImpl(String var1) {
      this.name = var1;
      this.isValid = true;
      this.pools = null;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid() {
      return this.isValid;
   }

   public String[] getMemoryPoolNames() {
      MemoryPoolMXBean[] var1 = this.getMemoryPools();
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = var1[var3].getName();
      }

      return var2;
   }

   synchronized MemoryPoolMXBean[] getMemoryPools() {
      if (this.pools == null) {
         this.pools = this.getMemoryPools0();
      }

      return this.pools;
   }

   private native MemoryPoolMXBean[] getMemoryPools0();

   public MBeanNotificationInfo[] getNotificationInfo() {
      synchronized(this) {
         if (this.notifInfo == null) {
            this.notifInfo = new MBeanNotificationInfo[0];
         }
      }

      return this.notifInfo;
   }

   public ObjectName getObjectName() {
      return Util.newObjectName("java.lang:type=MemoryManager", this.getName());
   }
}
