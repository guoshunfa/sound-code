package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Map;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmMemoryGCCall;
import sun.management.snmp.jvmmib.EnumJvmMemoryGCVerboseLevel;
import sun.management.snmp.jvmmib.JvmMemoryMBean;
import sun.management.snmp.util.JvmContextFactory;
import sun.management.snmp.util.MibLogger;

public class JvmMemoryImpl implements JvmMemoryMBean {
   static final EnumJvmMemoryGCCall JvmMemoryGCCallSupported = new EnumJvmMemoryGCCall("supported");
   static final EnumJvmMemoryGCCall JvmMemoryGCCallStart = new EnumJvmMemoryGCCall("start");
   static final EnumJvmMemoryGCCall JvmMemoryGCCallFailed = new EnumJvmMemoryGCCall("failed");
   static final EnumJvmMemoryGCCall JvmMemoryGCCallStarted = new EnumJvmMemoryGCCall("started");
   static final EnumJvmMemoryGCVerboseLevel JvmMemoryGCVerboseLevelVerbose = new EnumJvmMemoryGCVerboseLevel("verbose");
   static final EnumJvmMemoryGCVerboseLevel JvmMemoryGCVerboseLevelSilent = new EnumJvmMemoryGCVerboseLevel("silent");
   static final String heapMemoryTag = "jvmMemory.getHeapMemoryUsage";
   static final String nonHeapMemoryTag = "jvmMemory.getNonHeapMemoryUsage";
   static final Long Long0 = new Long(0L);
   static final MibLogger log = new MibLogger(JvmMemoryImpl.class);

   public JvmMemoryImpl(SnmpMib var1) {
   }

   public JvmMemoryImpl(SnmpMib var1, MBeanServer var2) {
   }

   private MemoryUsage getMemoryUsage(MemoryType var1) {
      return var1 == MemoryType.HEAP ? ManagementFactory.getMemoryMXBean().getHeapMemoryUsage() : ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
   }

   MemoryUsage getNonHeapMemoryUsage() {
      try {
         Map var1 = JvmContextFactory.getUserData();
         if (var1 != null) {
            MemoryUsage var2 = (MemoryUsage)var1.get("jvmMemory.getNonHeapMemoryUsage");
            if (var2 != null) {
               log.debug("getNonHeapMemoryUsage", "jvmMemory.getNonHeapMemoryUsage found in cache.");
               return var2;
            } else {
               MemoryUsage var3 = this.getMemoryUsage(MemoryType.NON_HEAP);
               var1.put("jvmMemory.getNonHeapMemoryUsage", var3);
               return var3;
            }
         } else {
            log.trace("getNonHeapMemoryUsage", "ERROR: should never come here!");
            return this.getMemoryUsage(MemoryType.NON_HEAP);
         }
      } catch (RuntimeException var4) {
         log.trace("getNonHeapMemoryUsage", "Failed to get NonHeapMemoryUsage: " + var4);
         log.debug("getNonHeapMemoryUsage", (Throwable)var4);
         throw var4;
      }
   }

   MemoryUsage getHeapMemoryUsage() {
      try {
         Map var1 = JvmContextFactory.getUserData();
         if (var1 != null) {
            MemoryUsage var2 = (MemoryUsage)var1.get("jvmMemory.getHeapMemoryUsage");
            if (var2 != null) {
               log.debug("getHeapMemoryUsage", "jvmMemory.getHeapMemoryUsage found in cache.");
               return var2;
            } else {
               MemoryUsage var3 = this.getMemoryUsage(MemoryType.HEAP);
               var1.put("jvmMemory.getHeapMemoryUsage", var3);
               return var3;
            }
         } else {
            log.trace("getHeapMemoryUsage", "ERROR: should never come here!");
            return this.getMemoryUsage(MemoryType.HEAP);
         }
      } catch (RuntimeException var4) {
         log.trace("getHeapMemoryUsage", "Failed to get HeapMemoryUsage: " + var4);
         log.debug("getHeapMemoryUsage", (Throwable)var4);
         throw var4;
      }
   }

   public Long getJvmMemoryNonHeapMaxSize() throws SnmpStatusException {
      long var1 = this.getNonHeapMemoryUsage().getMax();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public Long getJvmMemoryNonHeapCommitted() throws SnmpStatusException {
      long var1 = this.getNonHeapMemoryUsage().getCommitted();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public Long getJvmMemoryNonHeapUsed() throws SnmpStatusException {
      long var1 = this.getNonHeapMemoryUsage().getUsed();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public Long getJvmMemoryNonHeapInitSize() throws SnmpStatusException {
      long var1 = this.getNonHeapMemoryUsage().getInit();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public Long getJvmMemoryHeapMaxSize() throws SnmpStatusException {
      long var1 = this.getHeapMemoryUsage().getMax();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public EnumJvmMemoryGCCall getJvmMemoryGCCall() throws SnmpStatusException {
      Map var1 = JvmContextFactory.getUserData();
      if (var1 != null) {
         EnumJvmMemoryGCCall var2 = (EnumJvmMemoryGCCall)var1.get("jvmMemory.getJvmMemoryGCCall");
         if (var2 != null) {
            return var2;
         }
      }

      return JvmMemoryGCCallSupported;
   }

   public void setJvmMemoryGCCall(EnumJvmMemoryGCCall var1) throws SnmpStatusException {
      if (var1.intValue() == JvmMemoryGCCallStart.intValue()) {
         Map var2 = JvmContextFactory.getUserData();

         try {
            ManagementFactory.getMemoryMXBean().gc();
            if (var2 != null) {
               var2.put("jvmMemory.getJvmMemoryGCCall", JvmMemoryGCCallStarted);
            }
         } catch (Exception var4) {
            if (var2 != null) {
               var2.put("jvmMemory.getJvmMemoryGCCall", JvmMemoryGCCallFailed);
            }
         }

      } else {
         throw new SnmpStatusException(10);
      }
   }

   public void checkJvmMemoryGCCall(EnumJvmMemoryGCCall var1) throws SnmpStatusException {
      if (var1.intValue() != JvmMemoryGCCallStart.intValue()) {
         throw new SnmpStatusException(10);
      }
   }

   public Long getJvmMemoryHeapCommitted() throws SnmpStatusException {
      long var1 = this.getHeapMemoryUsage().getCommitted();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public EnumJvmMemoryGCVerboseLevel getJvmMemoryGCVerboseLevel() throws SnmpStatusException {
      return ManagementFactory.getMemoryMXBean().isVerbose() ? JvmMemoryGCVerboseLevelVerbose : JvmMemoryGCVerboseLevelSilent;
   }

   public void setJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel var1) throws SnmpStatusException {
      if (JvmMemoryGCVerboseLevelVerbose.intValue() == var1.intValue()) {
         ManagementFactory.getMemoryMXBean().setVerbose(true);
      } else {
         ManagementFactory.getMemoryMXBean().setVerbose(false);
      }

   }

   public void checkJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel var1) throws SnmpStatusException {
   }

   public Long getJvmMemoryHeapUsed() throws SnmpStatusException {
      long var1 = this.getHeapMemoryUsage().getUsed();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public Long getJvmMemoryHeapInitSize() throws SnmpStatusException {
      long var1 = this.getHeapMemoryUsage().getInit();
      return var1 > -1L ? new Long(var1) : Long0;
   }

   public Long getJvmMemoryPendingFinalCount() throws SnmpStatusException {
      long var1 = (long)ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount();
      return var1 > -1L ? new Long((long)((int)var1)) : new Long(0L);
   }
}
