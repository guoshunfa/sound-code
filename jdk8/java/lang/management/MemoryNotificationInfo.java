package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.MemoryNotifInfoCompositeData;

public class MemoryNotificationInfo {
   private final String poolName;
   private final MemoryUsage usage;
   private final long count;
   public static final String MEMORY_THRESHOLD_EXCEEDED = "java.management.memory.threshold.exceeded";
   public static final String MEMORY_COLLECTION_THRESHOLD_EXCEEDED = "java.management.memory.collection.threshold.exceeded";

   public MemoryNotificationInfo(String var1, MemoryUsage var2, long var3) {
      if (var1 == null) {
         throw new NullPointerException("Null poolName");
      } else if (var2 == null) {
         throw new NullPointerException("Null usage");
      } else {
         this.poolName = var1;
         this.usage = var2;
         this.count = var3;
      }
   }

   MemoryNotificationInfo(CompositeData var1) {
      MemoryNotifInfoCompositeData.validateCompositeData(var1);
      this.poolName = MemoryNotifInfoCompositeData.getPoolName(var1);
      this.usage = MemoryNotifInfoCompositeData.getUsage(var1);
      this.count = MemoryNotifInfoCompositeData.getCount(var1);
   }

   public String getPoolName() {
      return this.poolName;
   }

   public MemoryUsage getUsage() {
      return this.usage;
   }

   public long getCount() {
      return this.count;
   }

   public static MemoryNotificationInfo from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof MemoryNotifInfoCompositeData ? ((MemoryNotifInfoCompositeData)var0).getMemoryNotifInfo() : new MemoryNotificationInfo(var0);
      }
   }
}
