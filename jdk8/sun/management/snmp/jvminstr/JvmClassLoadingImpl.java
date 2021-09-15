package sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmClassesVerboseLevel;
import sun.management.snmp.jvmmib.JvmClassLoadingMBean;

public class JvmClassLoadingImpl implements JvmClassLoadingMBean {
   static final EnumJvmClassesVerboseLevel JvmClassesVerboseLevelVerbose = new EnumJvmClassesVerboseLevel("verbose");
   static final EnumJvmClassesVerboseLevel JvmClassesVerboseLevelSilent = new EnumJvmClassesVerboseLevel("silent");

   public JvmClassLoadingImpl(SnmpMib var1) {
   }

   public JvmClassLoadingImpl(SnmpMib var1, MBeanServer var2) {
   }

   static ClassLoadingMXBean getClassLoadingMXBean() {
      return ManagementFactory.getClassLoadingMXBean();
   }

   public EnumJvmClassesVerboseLevel getJvmClassesVerboseLevel() throws SnmpStatusException {
      return getClassLoadingMXBean().isVerbose() ? JvmClassesVerboseLevelVerbose : JvmClassesVerboseLevelSilent;
   }

   public void setJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel var1) throws SnmpStatusException {
      boolean var2;
      if (JvmClassesVerboseLevelVerbose.equals(var1)) {
         var2 = true;
      } else {
         if (!JvmClassesVerboseLevelSilent.equals(var1)) {
            throw new SnmpStatusException(10);
         }

         var2 = false;
      }

      getClassLoadingMXBean().setVerbose(var2);
   }

   public void checkJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel var1) throws SnmpStatusException {
      if (!JvmClassesVerboseLevelVerbose.equals(var1)) {
         if (!JvmClassesVerboseLevelSilent.equals(var1)) {
            throw new SnmpStatusException(10);
         }
      }
   }

   public Long getJvmClassesUnloadedCount() throws SnmpStatusException {
      return new Long(getClassLoadingMXBean().getUnloadedClassCount());
   }

   public Long getJvmClassesTotalLoadedCount() throws SnmpStatusException {
      return new Long(getClassLoadingMXBean().getTotalLoadedClassCount());
   }

   public Long getJvmClassesLoadedCount() throws SnmpStatusException {
      return new Long((long)getClassLoadingMXBean().getLoadedClassCount());
   }
}
