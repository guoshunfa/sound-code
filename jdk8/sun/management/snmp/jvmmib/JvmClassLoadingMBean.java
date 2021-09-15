package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmClassLoadingMBean {
   EnumJvmClassesVerboseLevel getJvmClassesVerboseLevel() throws SnmpStatusException;

   void setJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel var1) throws SnmpStatusException;

   void checkJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel var1) throws SnmpStatusException;

   Long getJvmClassesUnloadedCount() throws SnmpStatusException;

   Long getJvmClassesTotalLoadedCount() throws SnmpStatusException;

   Long getJvmClassesLoadedCount() throws SnmpStatusException;
}
