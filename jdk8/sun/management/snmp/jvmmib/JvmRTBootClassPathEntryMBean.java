package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmRTBootClassPathEntryMBean {
   String getJvmRTBootClassPathItem() throws SnmpStatusException;

   Integer getJvmRTBootClassPathIndex() throws SnmpStatusException;
}
