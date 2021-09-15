package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemoryMBean {
   Long getJvmMemoryNonHeapMaxSize() throws SnmpStatusException;

   Long getJvmMemoryNonHeapCommitted() throws SnmpStatusException;

   Long getJvmMemoryNonHeapUsed() throws SnmpStatusException;

   Long getJvmMemoryNonHeapInitSize() throws SnmpStatusException;

   Long getJvmMemoryHeapMaxSize() throws SnmpStatusException;

   Long getJvmMemoryHeapCommitted() throws SnmpStatusException;

   EnumJvmMemoryGCCall getJvmMemoryGCCall() throws SnmpStatusException;

   void setJvmMemoryGCCall(EnumJvmMemoryGCCall var1) throws SnmpStatusException;

   void checkJvmMemoryGCCall(EnumJvmMemoryGCCall var1) throws SnmpStatusException;

   Long getJvmMemoryHeapUsed() throws SnmpStatusException;

   EnumJvmMemoryGCVerboseLevel getJvmMemoryGCVerboseLevel() throws SnmpStatusException;

   void setJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel var1) throws SnmpStatusException;

   void checkJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel var1) throws SnmpStatusException;

   Long getJvmMemoryHeapInitSize() throws SnmpStatusException;

   Long getJvmMemoryPendingFinalCount() throws SnmpStatusException;
}
