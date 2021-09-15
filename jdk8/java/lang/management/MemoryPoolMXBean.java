package java.lang.management;

public interface MemoryPoolMXBean extends PlatformManagedObject {
   String getName();

   MemoryType getType();

   MemoryUsage getUsage();

   MemoryUsage getPeakUsage();

   void resetPeakUsage();

   boolean isValid();

   String[] getMemoryManagerNames();

   long getUsageThreshold();

   void setUsageThreshold(long var1);

   boolean isUsageThresholdExceeded();

   long getUsageThresholdCount();

   boolean isUsageThresholdSupported();

   long getCollectionUsageThreshold();

   void setCollectionUsageThreshold(long var1);

   boolean isCollectionUsageThresholdExceeded();

   long getCollectionUsageThresholdCount();

   MemoryUsage getCollectionUsage();

   boolean isCollectionUsageThresholdSupported();
}
