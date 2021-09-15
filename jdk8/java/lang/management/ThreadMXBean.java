package java.lang.management;

public interface ThreadMXBean extends PlatformManagedObject {
   int getThreadCount();

   int getPeakThreadCount();

   long getTotalStartedThreadCount();

   int getDaemonThreadCount();

   long[] getAllThreadIds();

   ThreadInfo getThreadInfo(long var1);

   ThreadInfo[] getThreadInfo(long[] var1);

   ThreadInfo getThreadInfo(long var1, int var3);

   ThreadInfo[] getThreadInfo(long[] var1, int var2);

   boolean isThreadContentionMonitoringSupported();

   boolean isThreadContentionMonitoringEnabled();

   void setThreadContentionMonitoringEnabled(boolean var1);

   long getCurrentThreadCpuTime();

   long getCurrentThreadUserTime();

   long getThreadCpuTime(long var1);

   long getThreadUserTime(long var1);

   boolean isThreadCpuTimeSupported();

   boolean isCurrentThreadCpuTimeSupported();

   boolean isThreadCpuTimeEnabled();

   void setThreadCpuTimeEnabled(boolean var1);

   long[] findMonitorDeadlockedThreads();

   void resetPeakThreadCount();

   long[] findDeadlockedThreads();

   boolean isObjectMonitorUsageSupported();

   boolean isSynchronizerUsageSupported();

   ThreadInfo[] getThreadInfo(long[] var1, boolean var2, boolean var3);

   ThreadInfo[] dumpAllThreads(boolean var1, boolean var2);
}
