package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmRTLibraryPathEntryMBean {
   String getJvmRTLibraryPathItem() throws SnmpStatusException;

   Integer getJvmRTLibraryPathIndex() throws SnmpStatusException;
}
