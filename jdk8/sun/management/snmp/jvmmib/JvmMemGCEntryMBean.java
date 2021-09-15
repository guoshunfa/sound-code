package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemGCEntryMBean {
   Long getJvmMemGCTimeMs() throws SnmpStatusException;

   Long getJvmMemGCCount() throws SnmpStatusException;

   Integer getJvmMemManagerIndex() throws SnmpStatusException;
}
