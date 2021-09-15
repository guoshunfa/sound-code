package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmRTClassPathEntryMBean {
   String getJvmRTClassPathItem() throws SnmpStatusException;

   Integer getJvmRTClassPathIndex() throws SnmpStatusException;
}
