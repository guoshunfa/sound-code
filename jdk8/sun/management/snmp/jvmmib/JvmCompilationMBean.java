package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmCompilationMBean {
   EnumJvmJITCompilerTimeMonitoring getJvmJITCompilerTimeMonitoring() throws SnmpStatusException;

   Long getJvmJITCompilerTimeMs() throws SnmpStatusException;

   String getJvmJITCompilerName() throws SnmpStatusException;
}
