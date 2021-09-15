package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemPoolEntryMBean {
   Long getJvmMemPoolCollectMaxSize() throws SnmpStatusException;

   Long getJvmMemPoolCollectCommitted() throws SnmpStatusException;

   Long getJvmMemPoolCollectUsed() throws SnmpStatusException;

   EnumJvmMemPoolCollectThreshdSupport getJvmMemPoolCollectThreshdSupport() throws SnmpStatusException;

   Long getJvmMemPoolCollectThreshdCount() throws SnmpStatusException;

   Long getJvmMemPoolCollectThreshold() throws SnmpStatusException;

   void setJvmMemPoolCollectThreshold(Long var1) throws SnmpStatusException;

   void checkJvmMemPoolCollectThreshold(Long var1) throws SnmpStatusException;

   Long getJvmMemPoolMaxSize() throws SnmpStatusException;

   Long getJvmMemPoolCommitted() throws SnmpStatusException;

   Long getJvmMemPoolUsed() throws SnmpStatusException;

   Long getJvmMemPoolInitSize() throws SnmpStatusException;

   EnumJvmMemPoolThreshdSupport getJvmMemPoolThreshdSupport() throws SnmpStatusException;

   Long getJvmMemPoolThreshdCount() throws SnmpStatusException;

   Long getJvmMemPoolThreshold() throws SnmpStatusException;

   void setJvmMemPoolThreshold(Long var1) throws SnmpStatusException;

   void checkJvmMemPoolThreshold(Long var1) throws SnmpStatusException;

   Long getJvmMemPoolPeakReset() throws SnmpStatusException;

   void setJvmMemPoolPeakReset(Long var1) throws SnmpStatusException;

   void checkJvmMemPoolPeakReset(Long var1) throws SnmpStatusException;

   EnumJvmMemPoolState getJvmMemPoolState() throws SnmpStatusException;

   EnumJvmMemPoolType getJvmMemPoolType() throws SnmpStatusException;

   String getJvmMemPoolName() throws SnmpStatusException;

   Long getJvmMemPoolPeakMaxSize() throws SnmpStatusException;

   Integer getJvmMemPoolIndex() throws SnmpStatusException;

   Long getJvmMemPoolPeakCommitted() throws SnmpStatusException;

   Long getJvmMemPoolPeakUsed() throws SnmpStatusException;
}
