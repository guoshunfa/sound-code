package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemManagerEntryMBean {
   EnumJvmMemManagerState getJvmMemManagerState() throws SnmpStatusException;

   String getJvmMemManagerName() throws SnmpStatusException;

   Integer getJvmMemManagerIndex() throws SnmpStatusException;
}
