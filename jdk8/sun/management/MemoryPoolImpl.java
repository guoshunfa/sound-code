package sun.management;

import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import javax.management.ObjectName;

class MemoryPoolImpl implements MemoryPoolMXBean {
   private final String name;
   private final boolean isHeap;
   private final boolean isValid;
   private final boolean collectionThresholdSupported;
   private final boolean usageThresholdSupported;
   private MemoryManagerMXBean[] managers;
   private long usageThreshold;
   private long collectionThreshold;
   private boolean usageSensorRegistered;
   private boolean gcSensorRegistered;
   private Sensor usageSensor;
   private Sensor gcSensor;

   MemoryPoolImpl(String var1, boolean var2, long var3, long var5) {
      this.name = var1;
      this.isHeap = var2;
      this.isValid = true;
      this.managers = null;
      this.usageThreshold = var3;
      this.collectionThreshold = var5;
      this.usageThresholdSupported = var3 >= 0L;
      this.collectionThresholdSupported = var5 >= 0L;
      this.usageSensor = new MemoryPoolImpl.PoolSensor(this, var1 + " usage sensor");
      this.gcSensor = new MemoryPoolImpl.CollectionSensor(this, var1 + " collection sensor");
      this.usageSensorRegistered = false;
      this.gcSensorRegistered = false;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid() {
      return this.isValid;
   }

   public MemoryType getType() {
      return this.isHeap ? MemoryType.HEAP : MemoryType.NON_HEAP;
   }

   public MemoryUsage getUsage() {
      return this.getUsage0();
   }

   public synchronized MemoryUsage getPeakUsage() {
      return this.getPeakUsage0();
   }

   public synchronized long getUsageThreshold() {
      if (!this.isUsageThresholdSupported()) {
         throw new UnsupportedOperationException("Usage threshold is not supported");
      } else {
         return this.usageThreshold;
      }
   }

   public void setUsageThreshold(long var1) {
      if (!this.isUsageThresholdSupported()) {
         throw new UnsupportedOperationException("Usage threshold is not supported");
      } else {
         Util.checkControlAccess();
         MemoryUsage var3 = this.getUsage0();
         if (var1 < 0L) {
            throw new IllegalArgumentException("Invalid threshold: " + var1);
         } else if (var3.getMax() != -1L && var1 > var3.getMax()) {
            throw new IllegalArgumentException("Invalid threshold: " + var1 + " must be <= maxSize. Committed = " + var3.getCommitted() + " Max = " + var3.getMax());
         } else {
            synchronized(this) {
               if (!this.usageSensorRegistered) {
                  this.usageSensorRegistered = true;
                  this.setPoolUsageSensor(this.usageSensor);
               }

               this.setUsageThreshold0(this.usageThreshold, var1);
               this.usageThreshold = var1;
            }
         }
      }
   }

   private synchronized MemoryManagerMXBean[] getMemoryManagers() {
      if (this.managers == null) {
         this.managers = this.getMemoryManagers0();
      }

      return this.managers;
   }

   public String[] getMemoryManagerNames() {
      MemoryManagerMXBean[] var1 = this.getMemoryManagers();
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = var1[var3].getName();
      }

      return var2;
   }

   public void resetPeakUsage() {
      Util.checkControlAccess();
      synchronized(this) {
         this.resetPeakUsage0();
      }
   }

   public boolean isUsageThresholdExceeded() {
      if (!this.isUsageThresholdSupported()) {
         throw new UnsupportedOperationException("Usage threshold is not supported");
      } else if (this.usageThreshold == 0L) {
         return false;
      } else {
         MemoryUsage var1 = this.getUsage0();
         return var1.getUsed() >= this.usageThreshold || this.usageSensor.isOn();
      }
   }

   public long getUsageThresholdCount() {
      if (!this.isUsageThresholdSupported()) {
         throw new UnsupportedOperationException("Usage threshold is not supported");
      } else {
         return this.usageSensor.getCount();
      }
   }

   public boolean isUsageThresholdSupported() {
      return this.usageThresholdSupported;
   }

   public synchronized long getCollectionUsageThreshold() {
      if (!this.isCollectionUsageThresholdSupported()) {
         throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
      } else {
         return this.collectionThreshold;
      }
   }

   public void setCollectionUsageThreshold(long var1) {
      if (!this.isCollectionUsageThresholdSupported()) {
         throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
      } else {
         Util.checkControlAccess();
         MemoryUsage var3 = this.getUsage0();
         if (var1 < 0L) {
            throw new IllegalArgumentException("Invalid threshold: " + var1);
         } else if (var3.getMax() != -1L && var1 > var3.getMax()) {
            throw new IllegalArgumentException("Invalid threshold: " + var1 + " > max (" + var3.getMax() + ").");
         } else {
            synchronized(this) {
               if (!this.gcSensorRegistered) {
                  this.gcSensorRegistered = true;
                  this.setPoolCollectionSensor(this.gcSensor);
               }

               this.setCollectionThreshold0(this.collectionThreshold, var1);
               this.collectionThreshold = var1;
            }
         }
      }
   }

   public boolean isCollectionUsageThresholdExceeded() {
      if (!this.isCollectionUsageThresholdSupported()) {
         throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
      } else if (this.collectionThreshold == 0L) {
         return false;
      } else {
         MemoryUsage var1 = this.getCollectionUsage0();
         return this.gcSensor.isOn() || var1 != null && var1.getUsed() >= this.collectionThreshold;
      }
   }

   public long getCollectionUsageThresholdCount() {
      if (!this.isCollectionUsageThresholdSupported()) {
         throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
      } else {
         return this.gcSensor.getCount();
      }
   }

   public MemoryUsage getCollectionUsage() {
      return this.getCollectionUsage0();
   }

   public boolean isCollectionUsageThresholdSupported() {
      return this.collectionThresholdSupported;
   }

   private native MemoryUsage getUsage0();

   private native MemoryUsage getPeakUsage0();

   private native MemoryUsage getCollectionUsage0();

   private native void setUsageThreshold0(long var1, long var3);

   private native void setCollectionThreshold0(long var1, long var3);

   private native void resetPeakUsage0();

   private native MemoryManagerMXBean[] getMemoryManagers0();

   private native void setPoolUsageSensor(Sensor var1);

   private native void setPoolCollectionSensor(Sensor var1);

   public ObjectName getObjectName() {
      return Util.newObjectName("java.lang:type=MemoryPool", this.getName());
   }

   class CollectionSensor extends Sensor {
      MemoryPoolImpl pool;

      CollectionSensor(MemoryPoolImpl var2, String var3) {
         super(var3);
         this.pool = var2;
      }

      void triggerAction(MemoryUsage var1) {
         MemoryImpl.createNotification("java.management.memory.collection.threshold.exceeded", this.pool.getName(), var1, MemoryPoolImpl.this.gcSensor.getCount());
      }

      void triggerAction() {
      }

      void clearAction() {
      }
   }

   class PoolSensor extends Sensor {
      MemoryPoolImpl pool;

      PoolSensor(MemoryPoolImpl var2, String var3) {
         super(var3);
         this.pool = var2;
      }

      void triggerAction(MemoryUsage var1) {
         MemoryImpl.createNotification("java.management.memory.threshold.exceeded", this.pool.getName(), var1, this.getCount());
      }

      void triggerAction() {
      }

      void clearAction() {
      }
   }
}
