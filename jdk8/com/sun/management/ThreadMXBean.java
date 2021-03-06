package com.sun.management;

import jdk.Exported;

@Exported
public interface ThreadMXBean extends java.lang.management.ThreadMXBean {
   long[] getThreadCpuTime(long[] var1);

   long[] getThreadUserTime(long[] var1);

   long getThreadAllocatedBytes(long var1);

   long[] getThreadAllocatedBytes(long[] var1);

   boolean isThreadAllocatedMemorySupported();

   boolean isThreadAllocatedMemoryEnabled();

   void setThreadAllocatedMemoryEnabled(boolean var1);
}
