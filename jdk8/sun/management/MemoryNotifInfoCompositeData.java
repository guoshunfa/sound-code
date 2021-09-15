package sun.management;

import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class MemoryNotifInfoCompositeData extends LazyCompositeData {
   private final MemoryNotificationInfo memoryNotifInfo;
   private static final CompositeType memoryNotifInfoCompositeType;
   private static final String POOL_NAME = "poolName";
   private static final String USAGE = "usage";
   private static final String COUNT = "count";
   private static final String[] memoryNotifInfoItemNames;
   private static final long serialVersionUID = -1805123446483771291L;

   private MemoryNotifInfoCompositeData(MemoryNotificationInfo var1) {
      this.memoryNotifInfo = var1;
   }

   public MemoryNotificationInfo getMemoryNotifInfo() {
      return this.memoryNotifInfo;
   }

   public static CompositeData toCompositeData(MemoryNotificationInfo var0) {
      MemoryNotifInfoCompositeData var1 = new MemoryNotifInfoCompositeData(var0);
      return var1.getCompositeData();
   }

   protected CompositeData getCompositeData() {
      Object[] var1 = new Object[]{this.memoryNotifInfo.getPoolName(), MemoryUsageCompositeData.toCompositeData(this.memoryNotifInfo.getUsage()), new Long(this.memoryNotifInfo.getCount())};

      try {
         return new CompositeDataSupport(memoryNotifInfoCompositeType, memoryNotifInfoItemNames, var1);
      } catch (OpenDataException var3) {
         throw new AssertionError(var3);
      }
   }

   public static String getPoolName(CompositeData var0) {
      String var1 = getString(var0, "poolName");
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid composite data: Attribute poolName has null value");
      } else {
         return var1;
      }
   }

   public static MemoryUsage getUsage(CompositeData var0) {
      CompositeData var1 = (CompositeData)var0.get("usage");
      return MemoryUsage.from(var1);
   }

   public static long getCount(CompositeData var0) {
      return getLong(var0, "count");
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else if (!isTypeMatched(memoryNotifInfoCompositeType, var0.getCompositeType())) {
         throw new IllegalArgumentException("Unexpected composite type for MemoryNotificationInfo");
      }
   }

   static {
      try {
         memoryNotifInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MemoryNotificationInfo.class);
      } catch (OpenDataException var1) {
         throw new AssertionError(var1);
      }

      memoryNotifInfoItemNames = new String[]{"poolName", "usage", "count"};
   }
}
