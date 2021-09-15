package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Map;
import sun.management.snmp.jvmmib.EnumJvmMemPoolCollectThreshdSupport;
import sun.management.snmp.jvmmib.EnumJvmMemPoolState;
import sun.management.snmp.jvmmib.EnumJvmMemPoolThreshdSupport;
import sun.management.snmp.jvmmib.EnumJvmMemPoolType;
import sun.management.snmp.jvmmib.JvmMemPoolEntryMBean;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;

public class JvmMemPoolEntryImpl implements JvmMemPoolEntryMBean {
   protected final int jvmMemPoolIndex;
   static final String memoryTag = "jvmMemPoolEntry.getUsage";
   static final String peakMemoryTag = "jvmMemPoolEntry.getPeakUsage";
   static final String collectMemoryTag = "jvmMemPoolEntry.getCollectionUsage";
   static final MemoryUsage ZEROS = new MemoryUsage(0L, 0L, 0L, 0L);
   final String entryMemoryTag;
   final String entryPeakMemoryTag;
   final String entryCollectMemoryTag;
   final MemoryPoolMXBean pool;
   private long jvmMemPoolPeakReset = 0L;
   private static final EnumJvmMemPoolState JvmMemPoolStateValid = new EnumJvmMemPoolState("valid");
   private static final EnumJvmMemPoolState JvmMemPoolStateInvalid = new EnumJvmMemPoolState("invalid");
   private static final EnumJvmMemPoolType EnumJvmMemPoolTypeHeap = new EnumJvmMemPoolType("heap");
   private static final EnumJvmMemPoolType EnumJvmMemPoolTypeNonHeap = new EnumJvmMemPoolType("nonheap");
   private static final EnumJvmMemPoolThreshdSupport EnumJvmMemPoolThreshdSupported = new EnumJvmMemPoolThreshdSupport("supported");
   private static final EnumJvmMemPoolThreshdSupport EnumJvmMemPoolThreshdUnsupported = new EnumJvmMemPoolThreshdSupport("unsupported");
   private static final EnumJvmMemPoolCollectThreshdSupport EnumJvmMemPoolCollectThreshdSupported = new EnumJvmMemPoolCollectThreshdSupport("supported");
   private static final EnumJvmMemPoolCollectThreshdSupport EnumJvmMemPoolCollectThreshdUnsupported = new EnumJvmMemPoolCollectThreshdSupport("unsupported");
   static final MibLogger log = new MibLogger(JvmMemPoolEntryImpl.class);

   MemoryUsage getMemoryUsage() {
      try {
         Map var1 = JvmContextFactory.getUserData();
         if (var1 != null) {
            MemoryUsage var2 = (MemoryUsage)var1.get(this.entryMemoryTag);
            if (var2 != null) {
               log.debug("getMemoryUsage", this.entryMemoryTag + " found in cache.");
               return var2;
            } else {
               MemoryUsage var3 = this.pool.getUsage();
               if (var3 == null) {
                  var3 = ZEROS;
               }

               var1.put(this.entryMemoryTag, var3);
               return var3;
            }
         } else {
            log.trace("getMemoryUsage", "ERROR: should never come here!");
            return this.pool.getUsage();
         }
      } catch (RuntimeException var4) {
         log.trace("getMemoryUsage", "Failed to get MemoryUsage: " + var4);
         log.debug("getMemoryUsage", (Throwable)var4);
         throw var4;
      }
   }

   MemoryUsage getPeakMemoryUsage() {
      try {
         Map var1 = JvmContextFactory.getUserData();
         if (var1 != null) {
            MemoryUsage var2 = (MemoryUsage)var1.get(this.entryPeakMemoryTag);
            if (var2 != null) {
               if (log.isDebugOn()) {
                  log.debug("getPeakMemoryUsage", this.entryPeakMemoryTag + " found in cache.");
               }

               return var2;
            } else {
               MemoryUsage var3 = this.pool.getPeakUsage();
               if (var3 == null) {
                  var3 = ZEROS;
               }

               var1.put(this.entryPeakMemoryTag, var3);
               return var3;
            }
         } else {
            log.trace("getPeakMemoryUsage", "ERROR: should never come here!");
            return ZEROS;
         }
      } catch (RuntimeException var4) {
         log.trace("getPeakMemoryUsage", "Failed to get MemoryUsage: " + var4);
         log.debug("getPeakMemoryUsage", (Throwable)var4);
         throw var4;
      }
   }

   MemoryUsage getCollectMemoryUsage() {
      try {
         Map var1 = JvmContextFactory.getUserData();
         if (var1 != null) {
            MemoryUsage var2 = (MemoryUsage)var1.get(this.entryCollectMemoryTag);
            if (var2 != null) {
               if (log.isDebugOn()) {
                  log.debug("getCollectMemoryUsage", this.entryCollectMemoryTag + " found in cache.");
               }

               return var2;
            } else {
               MemoryUsage var3 = this.pool.getCollectionUsage();
               if (var3 == null) {
                  var3 = ZEROS;
               }

               var1.put(this.entryCollectMemoryTag, var3);
               return var3;
            }
         } else {
            log.trace("getCollectMemoryUsage", "ERROR: should never come here!");
            return ZEROS;
         }
      } catch (RuntimeException var4) {
         log.trace("getPeakMemoryUsage", "Failed to get MemoryUsage: " + var4);
         log.debug("getPeakMemoryUsage", (Throwable)var4);
         throw var4;
      }
   }

   public JvmMemPoolEntryImpl(MemoryPoolMXBean var1, int var2) {
      this.pool = var1;
      this.jvmMemPoolIndex = var2;
      this.entryMemoryTag = "jvmMemPoolEntry.getUsage." + var2;
      this.entryPeakMemoryTag = "jvmMemPoolEntry.getPeakUsage." + var2;
      this.entryCollectMemoryTag = "jvmMemPoolEntry.getCollectionUsage." + var2;
   }

   public Long getJvmMemPoolMaxSize() throws SnmpStatusException {
      long var1 = this.getMemoryUsage().getMax();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolUsed() throws SnmpStatusException {
      long var1 = this.getMemoryUsage().getUsed();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolInitSize() throws SnmpStatusException {
      long var1 = this.getMemoryUsage().getInit();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolCommitted() throws SnmpStatusException {
      long var1 = this.getMemoryUsage().getCommitted();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolPeakMaxSize() throws SnmpStatusException {
      long var1 = this.getPeakMemoryUsage().getMax();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolPeakUsed() throws SnmpStatusException {
      long var1 = this.getPeakMemoryUsage().getUsed();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolPeakCommitted() throws SnmpStatusException {
      long var1 = this.getPeakMemoryUsage().getCommitted();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolCollectMaxSize() throws SnmpStatusException {
      long var1 = this.getCollectMemoryUsage().getMax();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolCollectUsed() throws SnmpStatusException {
      long var1 = this.getCollectMemoryUsage().getUsed();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolCollectCommitted() throws SnmpStatusException {
      long var1 = this.getCollectMemoryUsage().getCommitted();
      return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
   }

   public Long getJvmMemPoolThreshold() throws SnmpStatusException {
      if (!this.pool.isUsageThresholdSupported()) {
         return JvmMemoryImpl.Long0;
      } else {
         long var1 = this.pool.getUsageThreshold();
         return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
      }
   }

   public void setJvmMemPoolThreshold(Long var1) throws SnmpStatusException {
      long var2 = var1;
      if (var2 < 0L) {
         throw new SnmpStatusException(10);
      } else {
         this.pool.setUsageThreshold(var2);
      }
   }

   public void checkJvmMemPoolThreshold(Long var1) throws SnmpStatusException {
      if (!this.pool.isUsageThresholdSupported()) {
         throw new SnmpStatusException(12);
      } else {
         long var2 = var1;
         if (var2 < 0L) {
            throw new SnmpStatusException(10);
         }
      }
   }

   public EnumJvmMemPoolThreshdSupport getJvmMemPoolThreshdSupport() throws SnmpStatusException {
      return this.pool.isUsageThresholdSupported() ? EnumJvmMemPoolThreshdSupported : EnumJvmMemPoolThreshdUnsupported;
   }

   public Long getJvmMemPoolThreshdCount() throws SnmpStatusException {
      if (!this.pool.isUsageThresholdSupported()) {
         return JvmMemoryImpl.Long0;
      } else {
         long var1 = this.pool.getUsageThresholdCount();
         return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
      }
   }

   public Long getJvmMemPoolCollectThreshold() throws SnmpStatusException {
      if (!this.pool.isCollectionUsageThresholdSupported()) {
         return JvmMemoryImpl.Long0;
      } else {
         long var1 = this.pool.getCollectionUsageThreshold();
         return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
      }
   }

   public void setJvmMemPoolCollectThreshold(Long var1) throws SnmpStatusException {
      long var2 = var1;
      if (var2 < 0L) {
         throw new SnmpStatusException(10);
      } else {
         this.pool.setCollectionUsageThreshold(var2);
      }
   }

   public void checkJvmMemPoolCollectThreshold(Long var1) throws SnmpStatusException {
      if (!this.pool.isCollectionUsageThresholdSupported()) {
         throw new SnmpStatusException(12);
      } else {
         long var2 = var1;
         if (var2 < 0L) {
            throw new SnmpStatusException(10);
         }
      }
   }

   public EnumJvmMemPoolCollectThreshdSupport getJvmMemPoolCollectThreshdSupport() throws SnmpStatusException {
      return this.pool.isCollectionUsageThresholdSupported() ? EnumJvmMemPoolCollectThreshdSupported : EnumJvmMemPoolCollectThreshdUnsupported;
   }

   public Long getJvmMemPoolCollectThreshdCount() throws SnmpStatusException {
      if (!this.pool.isCollectionUsageThresholdSupported()) {
         return JvmMemoryImpl.Long0;
      } else {
         long var1 = this.pool.getCollectionUsageThresholdCount();
         return var1 > -1L ? new Long(var1) : JvmMemoryImpl.Long0;
      }
   }

   public static EnumJvmMemPoolType jvmMemPoolType(MemoryType var0) throws SnmpStatusException {
      if (var0.equals(MemoryType.HEAP)) {
         return EnumJvmMemPoolTypeHeap;
      } else if (var0.equals(MemoryType.NON_HEAP)) {
         return EnumJvmMemPoolTypeNonHeap;
      } else {
         throw new SnmpStatusException(10);
      }
   }

   public EnumJvmMemPoolType getJvmMemPoolType() throws SnmpStatusException {
      return jvmMemPoolType(this.pool.getType());
   }

   public String getJvmMemPoolName() throws SnmpStatusException {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(this.pool.getName());
   }

   public Integer getJvmMemPoolIndex() throws SnmpStatusException {
      return new Integer(this.jvmMemPoolIndex);
   }

   public EnumJvmMemPoolState getJvmMemPoolState() throws SnmpStatusException {
      return this.pool.isValid() ? JvmMemPoolStateValid : JvmMemPoolStateInvalid;
   }

   public synchronized Long getJvmMemPoolPeakReset() throws SnmpStatusException {
      return new Long(this.jvmMemPoolPeakReset);
   }

   public synchronized void setJvmMemPoolPeakReset(Long var1) throws SnmpStatusException {
      long var2 = var1;
      if (var2 > this.jvmMemPoolPeakReset) {
         long var4 = System.currentTimeMillis();
         this.pool.resetPeakUsage();
         this.jvmMemPoolPeakReset = var4;
         log.debug("setJvmMemPoolPeakReset", "jvmMemPoolPeakReset=" + var4);
      }

   }

   public void checkJvmMemPoolPeakReset(Long var1) throws SnmpStatusException {
   }
}
