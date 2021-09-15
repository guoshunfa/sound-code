package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmThreadContentionMonitoring;
import sun.management.snmp.jvmmib.EnumJvmThreadCpuTimeMonitoring;
import sun.management.snmp.jvmmib.JvmThreadingMBean;
import sun.management.snmp.util.MibLogger;

public class JvmThreadingImpl implements JvmThreadingMBean {
   static final EnumJvmThreadCpuTimeMonitoring JvmThreadCpuTimeMonitoringUnsupported = new EnumJvmThreadCpuTimeMonitoring("unsupported");
   static final EnumJvmThreadCpuTimeMonitoring JvmThreadCpuTimeMonitoringEnabled = new EnumJvmThreadCpuTimeMonitoring("enabled");
   static final EnumJvmThreadCpuTimeMonitoring JvmThreadCpuTimeMonitoringDisabled = new EnumJvmThreadCpuTimeMonitoring("disabled");
   static final EnumJvmThreadContentionMonitoring JvmThreadContentionMonitoringUnsupported = new EnumJvmThreadContentionMonitoring("unsupported");
   static final EnumJvmThreadContentionMonitoring JvmThreadContentionMonitoringEnabled = new EnumJvmThreadContentionMonitoring("enabled");
   static final EnumJvmThreadContentionMonitoring JvmThreadContentionMonitoringDisabled = new EnumJvmThreadContentionMonitoring("disabled");
   private long jvmThreadPeakCountReset = 0L;
   static final MibLogger log = new MibLogger(JvmThreadingImpl.class);

   public JvmThreadingImpl(SnmpMib var1) {
      log.debug("JvmThreadingImpl", "Constructor");
   }

   public JvmThreadingImpl(SnmpMib var1, MBeanServer var2) {
      log.debug("JvmThreadingImpl", "Constructor with server");
   }

   static ThreadMXBean getThreadMXBean() {
      return ManagementFactory.getThreadMXBean();
   }

   public EnumJvmThreadCpuTimeMonitoring getJvmThreadCpuTimeMonitoring() throws SnmpStatusException {
      ThreadMXBean var1 = getThreadMXBean();
      if (!var1.isThreadCpuTimeSupported()) {
         log.debug("getJvmThreadCpuTimeMonitoring", "Unsupported ThreadCpuTimeMonitoring");
         return JvmThreadCpuTimeMonitoringUnsupported;
      } else {
         try {
            if (var1.isThreadCpuTimeEnabled()) {
               log.debug("getJvmThreadCpuTimeMonitoring", "Enabled ThreadCpuTimeMonitoring");
               return JvmThreadCpuTimeMonitoringEnabled;
            } else {
               log.debug("getJvmThreadCpuTimeMonitoring", "Disabled ThreadCpuTimeMonitoring");
               return JvmThreadCpuTimeMonitoringDisabled;
            }
         } catch (UnsupportedOperationException var3) {
            log.debug("getJvmThreadCpuTimeMonitoring", "Newly unsupported ThreadCpuTimeMonitoring");
            return JvmThreadCpuTimeMonitoringUnsupported;
         }
      }
   }

   public void setJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring var1) throws SnmpStatusException {
      ThreadMXBean var2 = getThreadMXBean();
      if (JvmThreadCpuTimeMonitoringEnabled.intValue() == var1.intValue()) {
         var2.setThreadCpuTimeEnabled(true);
      } else {
         var2.setThreadCpuTimeEnabled(false);
      }

   }

   public void checkJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring var1) throws SnmpStatusException {
      if (JvmThreadCpuTimeMonitoringUnsupported.intValue() == var1.intValue()) {
         log.debug("checkJvmThreadCpuTimeMonitoring", "Try to set to illegal unsupported value");
         throw new SnmpStatusException(10);
      } else if (JvmThreadCpuTimeMonitoringEnabled.intValue() != var1.intValue() && JvmThreadCpuTimeMonitoringDisabled.intValue() != var1.intValue()) {
         log.debug("checkJvmThreadCpuTimeMonitoring", "unknown enum value ");
         throw new SnmpStatusException(10);
      } else {
         ThreadMXBean var2 = getThreadMXBean();
         if (!var2.isThreadCpuTimeSupported()) {
            log.debug("checkJvmThreadCpuTimeMonitoring", "Unsupported operation, can't set state");
            throw new SnmpStatusException(12);
         }
      }
   }

   public EnumJvmThreadContentionMonitoring getJvmThreadContentionMonitoring() throws SnmpStatusException {
      ThreadMXBean var1 = getThreadMXBean();
      if (!var1.isThreadContentionMonitoringSupported()) {
         log.debug("getJvmThreadContentionMonitoring", "Unsupported ThreadContentionMonitoring");
         return JvmThreadContentionMonitoringUnsupported;
      } else if (var1.isThreadContentionMonitoringEnabled()) {
         log.debug("getJvmThreadContentionMonitoring", "Enabled ThreadContentionMonitoring");
         return JvmThreadContentionMonitoringEnabled;
      } else {
         log.debug("getJvmThreadContentionMonitoring", "Disabled ThreadContentionMonitoring");
         return JvmThreadContentionMonitoringDisabled;
      }
   }

   public void setJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring var1) throws SnmpStatusException {
      ThreadMXBean var2 = getThreadMXBean();
      if (JvmThreadContentionMonitoringEnabled.intValue() == var1.intValue()) {
         var2.setThreadContentionMonitoringEnabled(true);
      } else {
         var2.setThreadContentionMonitoringEnabled(false);
      }

   }

   public void checkJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring var1) throws SnmpStatusException {
      if (JvmThreadContentionMonitoringUnsupported.intValue() == var1.intValue()) {
         log.debug("checkJvmThreadContentionMonitoring", "Try to set to illegal unsupported value");
         throw new SnmpStatusException(10);
      } else if (JvmThreadContentionMonitoringEnabled.intValue() != var1.intValue() && JvmThreadContentionMonitoringDisabled.intValue() != var1.intValue()) {
         log.debug("checkJvmThreadContentionMonitoring", "Try to set to unknown value");
         throw new SnmpStatusException(10);
      } else {
         ThreadMXBean var2 = getThreadMXBean();
         if (!var2.isThreadContentionMonitoringSupported()) {
            log.debug("checkJvmThreadContentionMonitoring", "Unsupported operation, can't set state");
            throw new SnmpStatusException(12);
         }
      }
   }

   public Long getJvmThreadTotalStartedCount() throws SnmpStatusException {
      return new Long(getThreadMXBean().getTotalStartedThreadCount());
   }

   public Long getJvmThreadPeakCount() throws SnmpStatusException {
      return new Long((long)getThreadMXBean().getPeakThreadCount());
   }

   public Long getJvmThreadDaemonCount() throws SnmpStatusException {
      return new Long((long)getThreadMXBean().getDaemonThreadCount());
   }

   public Long getJvmThreadCount() throws SnmpStatusException {
      return new Long((long)getThreadMXBean().getThreadCount());
   }

   public synchronized Long getJvmThreadPeakCountReset() throws SnmpStatusException {
      return new Long(this.jvmThreadPeakCountReset);
   }

   public synchronized void setJvmThreadPeakCountReset(Long var1) throws SnmpStatusException {
      long var2 = var1;
      if (var2 > this.jvmThreadPeakCountReset) {
         long var4 = System.currentTimeMillis();
         getThreadMXBean().resetPeakThreadCount();
         this.jvmThreadPeakCountReset = var4;
         log.debug("setJvmThreadPeakCountReset", "jvmThreadPeakCountReset=" + var4);
      }

   }

   public void checkJvmThreadPeakCountReset(Long var1) throws SnmpStatusException {
   }
}
