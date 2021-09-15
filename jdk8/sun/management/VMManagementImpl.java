package sun.management;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import sun.management.counter.Counter;
import sun.management.counter.perf.PerfInstrumentation;
import sun.misc.Perf;
import sun.security.action.GetPropertyAction;

class VMManagementImpl implements VMManagement {
   private static String version = getVersion0();
   private static boolean compTimeMonitoringSupport;
   private static boolean threadContentionMonitoringSupport;
   private static boolean currentThreadCpuTimeSupport;
   private static boolean otherThreadCpuTimeSupport;
   private static boolean bootClassPathSupport;
   private static boolean objectMonitorUsageSupport;
   private static boolean synchronizerUsageSupport;
   private static boolean threadAllocatedMemorySupport;
   private static boolean gcNotificationSupport;
   private static boolean remoteDiagnosticCommandsSupport;
   private List<String> vmArgs = null;
   private PerfInstrumentation perfInstr = null;
   private boolean noPerfData = false;

   private static native String getVersion0();

   private static native void initOptionalSupportFields();

   public boolean isCompilationTimeMonitoringSupported() {
      return compTimeMonitoringSupport;
   }

   public boolean isThreadContentionMonitoringSupported() {
      return threadContentionMonitoringSupport;
   }

   public boolean isCurrentThreadCpuTimeSupported() {
      return currentThreadCpuTimeSupport;
   }

   public boolean isOtherThreadCpuTimeSupported() {
      return otherThreadCpuTimeSupport;
   }

   public boolean isBootClassPathSupported() {
      return bootClassPathSupport;
   }

   public boolean isObjectMonitorUsageSupported() {
      return objectMonitorUsageSupport;
   }

   public boolean isSynchronizerUsageSupported() {
      return synchronizerUsageSupport;
   }

   public boolean isThreadAllocatedMemorySupported() {
      return threadAllocatedMemorySupport;
   }

   public boolean isGcNotificationSupported() {
      return gcNotificationSupport;
   }

   public boolean isRemoteDiagnosticCommandsSupported() {
      return remoteDiagnosticCommandsSupport;
   }

   public native boolean isThreadContentionMonitoringEnabled();

   public native boolean isThreadCpuTimeEnabled();

   public native boolean isThreadAllocatedMemoryEnabled();

   public int getLoadedClassCount() {
      long var1 = this.getTotalClassCount() - this.getUnloadedClassCount();
      return (int)var1;
   }

   public native long getTotalClassCount();

   public native long getUnloadedClassCount();

   public native boolean getVerboseClass();

   public native boolean getVerboseGC();

   public String getManagementVersion() {
      return version;
   }

   public String getVmId() {
      int var1 = this.getProcessId();
      String var2 = "localhost";

      try {
         var2 = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException var4) {
      }

      return var1 + "@" + var2;
   }

   private native int getProcessId();

   public String getVmName() {
      return System.getProperty("java.vm.name");
   }

   public String getVmVendor() {
      return System.getProperty("java.vm.vendor");
   }

   public String getVmVersion() {
      return System.getProperty("java.vm.version");
   }

   public String getVmSpecName() {
      return System.getProperty("java.vm.specification.name");
   }

   public String getVmSpecVendor() {
      return System.getProperty("java.vm.specification.vendor");
   }

   public String getVmSpecVersion() {
      return System.getProperty("java.vm.specification.version");
   }

   public String getClassPath() {
      return System.getProperty("java.class.path");
   }

   public String getLibraryPath() {
      return System.getProperty("java.library.path");
   }

   public String getBootClassPath() {
      GetPropertyAction var1 = new GetPropertyAction("sun.boot.class.path");
      String var2 = (String)AccessController.doPrivileged((PrivilegedAction)var1);
      return var2;
   }

   public long getUptime() {
      return this.getUptime0();
   }

   public synchronized List<String> getVmArguments() {
      if (this.vmArgs == null) {
         String[] var1 = this.getVmArguments0();
         List var2 = var1 != null && var1.length != 0 ? Arrays.asList(var1) : Collections.emptyList();
         this.vmArgs = Collections.unmodifiableList(var2);
      }

      return this.vmArgs;
   }

   public native String[] getVmArguments0();

   public native long getStartupTime();

   private native long getUptime0();

   public native int getAvailableProcessors();

   public String getCompilerName() {
      String var1 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty("sun.management.compiler");
         }
      });
      return var1;
   }

   public native long getTotalCompileTime();

   public native long getTotalThreadCount();

   public native int getLiveThreadCount();

   public native int getPeakThreadCount();

   public native int getDaemonThreadCount();

   public String getOsName() {
      return System.getProperty("os.name");
   }

   public String getOsArch() {
      return System.getProperty("os.arch");
   }

   public String getOsVersion() {
      return System.getProperty("os.version");
   }

   public native long getSafepointCount();

   public native long getTotalSafepointTime();

   public native long getSafepointSyncTime();

   public native long getTotalApplicationNonStoppedTime();

   public native long getLoadedClassSize();

   public native long getUnloadedClassSize();

   public native long getClassLoadingTime();

   public native long getMethodDataSize();

   public native long getInitializedClassCount();

   public native long getClassInitializationTime();

   public native long getClassVerificationTime();

   private synchronized PerfInstrumentation getPerfInstrumentation() {
      if (!this.noPerfData && this.perfInstr == null) {
         Perf var1 = (Perf)AccessController.doPrivileged((PrivilegedAction)(new Perf.GetPerfAction()));

         try {
            ByteBuffer var2 = var1.attach(0, "r");
            if (var2.capacity() == 0) {
               this.noPerfData = true;
               return null;
            }

            this.perfInstr = new PerfInstrumentation(var2);
         } catch (IllegalArgumentException var3) {
            this.noPerfData = true;
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }

         return this.perfInstr;
      } else {
         return this.perfInstr;
      }
   }

   public List<Counter> getInternalCounters(String var1) {
      PerfInstrumentation var2 = this.getPerfInstrumentation();
      return var2 != null ? var2.findByPattern(var1) : Collections.emptyList();
   }

   static {
      if (version == null) {
         throw new AssertionError("Invalid Management Version");
      } else {
         initOptionalSupportFields();
      }
   }
}
