package com.sun.management;

import jdk.Exported;

@Exported
public interface OperatingSystemMXBean extends java.lang.management.OperatingSystemMXBean {
   long getCommittedVirtualMemorySize();

   long getTotalSwapSpaceSize();

   long getFreeSwapSpaceSize();

   long getProcessCpuTime();

   long getFreePhysicalMemorySize();

   long getTotalPhysicalMemorySize();

   double getSystemCpuLoad();

   double getProcessCpuLoad();
}
