package sun.management;

import com.sun.management.UnixOperatingSystemMXBean;

class OperatingSystemImpl extends BaseOperatingSystemImpl implements UnixOperatingSystemMXBean {
   OperatingSystemImpl(VMManagement var1) {
      super(var1);
   }

   public native long getCommittedVirtualMemorySize();

   public native long getTotalSwapSpaceSize();

   public native long getFreeSwapSpaceSize();

   public native long getProcessCpuTime();

   public native long getFreePhysicalMemorySize();

   public native long getTotalPhysicalMemorySize();

   public native long getOpenFileDescriptorCount();

   public native long getMaxFileDescriptorCount();

   public native double getSystemCpuLoad();

   public native double getProcessCpuLoad();

   private static native void initialize();

   static {
      initialize();
   }
}
