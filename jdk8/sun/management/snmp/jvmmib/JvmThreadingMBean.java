package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmThreadingMBean {
   EnumJvmThreadCpuTimeMonitoring getJvmThreadCpuTimeMonitoring() throws SnmpStatusException;

   void setJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring var1) throws SnmpStatusException;

   void checkJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring var1) throws SnmpStatusException;

   EnumJvmThreadContentionMonitoring getJvmThreadContentionMonitoring() throws SnmpStatusException;

   void setJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring var1) throws SnmpStatusException;

   void checkJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring var1) throws SnmpStatusException;

   Long getJvmThreadTotalStartedCount() throws SnmpStatusException;

   Long getJvmThreadPeakCount() throws SnmpStatusException;

   Long getJvmThreadDaemonCount() throws SnmpStatusException;

   Long getJvmThreadCount() throws SnmpStatusException;

   Long getJvmThreadPeakCountReset() throws SnmpStatusException;

   void setJvmThreadPeakCountReset(Long var1) throws SnmpStatusException;

   void checkJvmThreadPeakCountReset(Long var1) throws SnmpStatusException;
}
