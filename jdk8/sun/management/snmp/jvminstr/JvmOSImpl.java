package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.JvmOSMBean;

public class JvmOSImpl implements JvmOSMBean, Serializable {
   static final long serialVersionUID = 1839834731763310809L;

   public JvmOSImpl(SnmpMib var1) {
   }

   public JvmOSImpl(SnmpMib var1, MBeanServer var2) {
   }

   static OperatingSystemMXBean getOSMBean() {
      return ManagementFactory.getOperatingSystemMXBean();
   }

   private static String validDisplayStringTC(String var0) {
      return JVM_MANAGEMENT_MIB_IMPL.validDisplayStringTC(var0);
   }

   private static String validJavaObjectNameTC(String var0) {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(var0);
   }

   public Integer getJvmOSProcessorCount() throws SnmpStatusException {
      return new Integer(getOSMBean().getAvailableProcessors());
   }

   public String getJvmOSVersion() throws SnmpStatusException {
      return validDisplayStringTC(getOSMBean().getVersion());
   }

   public String getJvmOSArch() throws SnmpStatusException {
      return validDisplayStringTC(getOSMBean().getArch());
   }

   public String getJvmOSName() throws SnmpStatusException {
      return validJavaObjectNameTC(getOSMBean().getName());
   }
}
