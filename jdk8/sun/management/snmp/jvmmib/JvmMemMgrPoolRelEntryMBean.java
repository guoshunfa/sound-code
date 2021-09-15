package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemMgrPoolRelEntryMBean {
   String getJvmMemMgrRelPoolName() throws SnmpStatusException;

   String getJvmMemMgrRelManagerName() throws SnmpStatusException;

   Integer getJvmMemManagerIndex() throws SnmpStatusException;

   Integer getJvmMemPoolIndex() throws SnmpStatusException;
}
