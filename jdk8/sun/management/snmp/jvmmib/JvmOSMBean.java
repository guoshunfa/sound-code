package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmOSMBean {
   Integer getJvmOSProcessorCount() throws SnmpStatusException;

   String getJvmOSVersion() throws SnmpStatusException;

   String getJvmOSArch() throws SnmpStatusException;

   String getJvmOSName() throws SnmpStatusException;
}
