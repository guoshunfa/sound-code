package sun.management.snmp.jvminstr;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import sun.management.snmp.jvmmib.EnumJvmRTBootClassPathSupport;
import sun.management.snmp.jvmmib.JvmRuntimeMBean;
import sun.management.snmp.util.JvmContextFactory;

public class JvmRuntimeImpl implements JvmRuntimeMBean {
   static final EnumJvmRTBootClassPathSupport JvmRTBootClassPathSupportSupported = new EnumJvmRTBootClassPathSupport("supported");
   static final EnumJvmRTBootClassPathSupport JvmRTBootClassPathSupportUnSupported = new EnumJvmRTBootClassPathSupport("unsupported");

   public JvmRuntimeImpl(SnmpMib var1) {
   }

   public JvmRuntimeImpl(SnmpMib var1, MBeanServer var2) {
   }

   static RuntimeMXBean getRuntimeMXBean() {
      return ManagementFactory.getRuntimeMXBean();
   }

   private static String validDisplayStringTC(String var0) {
      return JVM_MANAGEMENT_MIB_IMPL.validDisplayStringTC(var0);
   }

   private static String validPathElementTC(String var0) {
      return JVM_MANAGEMENT_MIB_IMPL.validPathElementTC(var0);
   }

   private static String validJavaObjectNameTC(String var0) {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(var0);
   }

   static String[] splitPath(String var0) {
      String[] var1 = var0.split(File.pathSeparator);
      return var1;
   }

   static String[] getClassPath(Object var0) {
      Map var1 = (Map)Util.cast(var0 instanceof Map ? var0 : null);
      String[] var3;
      if (var1 != null) {
         var3 = (String[])((String[])var1.get("JvmRuntime.getClassPath"));
         if (var3 != null) {
            return var3;
         }
      }

      var3 = splitPath(getRuntimeMXBean().getClassPath());
      if (var1 != null) {
         var1.put("JvmRuntime.getClassPath", var3);
      }

      return var3;
   }

   static String[] getBootClassPath(Object var0) {
      if (!getRuntimeMXBean().isBootClassPathSupported()) {
         return new String[0];
      } else {
         Map var1 = (Map)Util.cast(var0 instanceof Map ? var0 : null);
         String[] var3;
         if (var1 != null) {
            var3 = (String[])((String[])var1.get("JvmRuntime.getBootClassPath"));
            if (var3 != null) {
               return var3;
            }
         }

         var3 = splitPath(getRuntimeMXBean().getBootClassPath());
         if (var1 != null) {
            var1.put("JvmRuntime.getBootClassPath", var3);
         }

         return var3;
      }
   }

   static String[] getLibraryPath(Object var0) {
      Map var1 = (Map)Util.cast(var0 instanceof Map ? var0 : null);
      String[] var3;
      if (var1 != null) {
         var3 = (String[])((String[])var1.get("JvmRuntime.getLibraryPath"));
         if (var3 != null) {
            return var3;
         }
      }

      var3 = splitPath(getRuntimeMXBean().getLibraryPath());
      if (var1 != null) {
         var1.put("JvmRuntime.getLibraryPath", var3);
      }

      return var3;
   }

   static String[] getInputArguments(Object var0) {
      Map var1 = (Map)Util.cast(var0 instanceof Map ? var0 : null);
      if (var1 != null) {
         String[] var3 = (String[])((String[])var1.get("JvmRuntime.getInputArguments"));
         if (var3 != null) {
            return var3;
         }
      }

      List var5 = getRuntimeMXBean().getInputArguments();
      String[] var4 = (String[])var5.toArray(new String[0]);
      if (var1 != null) {
         var1.put("JvmRuntime.getInputArguments", var4);
      }

      return var4;
   }

   public String getJvmRTSpecVendor() throws SnmpStatusException {
      return validDisplayStringTC(getRuntimeMXBean().getSpecVendor());
   }

   public String getJvmRTSpecName() throws SnmpStatusException {
      return validDisplayStringTC(getRuntimeMXBean().getSpecName());
   }

   public String getJvmRTVMVersion() throws SnmpStatusException {
      return validDisplayStringTC(getRuntimeMXBean().getVmVersion());
   }

   public String getJvmRTVMVendor() throws SnmpStatusException {
      return validDisplayStringTC(getRuntimeMXBean().getVmVendor());
   }

   public String getJvmRTManagementSpecVersion() throws SnmpStatusException {
      return validDisplayStringTC(getRuntimeMXBean().getManagementSpecVersion());
   }

   public String getJvmRTVMName() throws SnmpStatusException {
      return validJavaObjectNameTC(getRuntimeMXBean().getVmName());
   }

   public Integer getJvmRTInputArgsCount() throws SnmpStatusException {
      String[] var1 = getInputArguments(JvmContextFactory.getUserData());
      return new Integer(var1.length);
   }

   public EnumJvmRTBootClassPathSupport getJvmRTBootClassPathSupport() throws SnmpStatusException {
      return getRuntimeMXBean().isBootClassPathSupported() ? JvmRTBootClassPathSupportSupported : JvmRTBootClassPathSupportUnSupported;
   }

   public Long getJvmRTUptimeMs() throws SnmpStatusException {
      return new Long(getRuntimeMXBean().getUptime());
   }

   public Long getJvmRTStartTimeMs() throws SnmpStatusException {
      return new Long(getRuntimeMXBean().getStartTime());
   }

   public String getJvmRTSpecVersion() throws SnmpStatusException {
      return validDisplayStringTC(getRuntimeMXBean().getSpecVersion());
   }

   public String getJvmRTName() throws SnmpStatusException {
      return validDisplayStringTC(getRuntimeMXBean().getName());
   }
}
