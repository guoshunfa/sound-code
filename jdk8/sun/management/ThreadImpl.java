package sun.management;

import com.sun.management.ThreadMXBean;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import javax.management.ObjectName;

class ThreadImpl implements ThreadMXBean {
   private final VMManagement jvm;
   private boolean contentionMonitoringEnabled = false;
   private boolean cpuTimeEnabled;
   private boolean allocatedMemoryEnabled;

   ThreadImpl(VMManagement var1) {
      this.jvm = var1;
      this.cpuTimeEnabled = this.jvm.isThreadCpuTimeEnabled();
      this.allocatedMemoryEnabled = this.jvm.isThreadAllocatedMemoryEnabled();
   }

   public int getThreadCount() {
      return this.jvm.getLiveThreadCount();
   }

   public int getPeakThreadCount() {
      return this.jvm.getPeakThreadCount();
   }

   public long getTotalStartedThreadCount() {
      return this.jvm.getTotalThreadCount();
   }

   public int getDaemonThreadCount() {
      return this.jvm.getDaemonThreadCount();
   }

   public boolean isThreadContentionMonitoringSupported() {
      return this.jvm.isThreadContentionMonitoringSupported();
   }

   public synchronized boolean isThreadContentionMonitoringEnabled() {
      if (!this.isThreadContentionMonitoringSupported()) {
         throw new UnsupportedOperationException("Thread contention monitoring is not supported.");
      } else {
         return this.contentionMonitoringEnabled;
      }
   }

   public boolean isThreadCpuTimeSupported() {
      return this.jvm.isOtherThreadCpuTimeSupported();
   }

   public boolean isCurrentThreadCpuTimeSupported() {
      return this.jvm.isCurrentThreadCpuTimeSupported();
   }

   public boolean isThreadAllocatedMemorySupported() {
      return this.jvm.isThreadAllocatedMemorySupported();
   }

   public boolean isThreadCpuTimeEnabled() {
      if (!this.isThreadCpuTimeSupported() && !this.isCurrentThreadCpuTimeSupported()) {
         throw new UnsupportedOperationException("Thread CPU time measurement is not supported");
      } else {
         return this.cpuTimeEnabled;
      }
   }

   public boolean isThreadAllocatedMemoryEnabled() {
      if (!this.isThreadAllocatedMemorySupported()) {
         throw new UnsupportedOperationException("Thread allocated memory measurement is not supported");
      } else {
         return this.allocatedMemoryEnabled;
      }
   }

   public long[] getAllThreadIds() {
      Util.checkMonitorAccess();
      Thread[] var1 = getThreads();
      int var2 = var1.length;
      long[] var3 = new long[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         Thread var5 = var1[var4];
         var3[var4] = var5.getId();
      }

      return var3;
   }

   public ThreadInfo getThreadInfo(long var1) {
      long[] var3 = new long[]{var1};
      ThreadInfo[] var4 = this.getThreadInfo(var3, 0);
      return var4[0];
   }

   public ThreadInfo getThreadInfo(long var1, int var3) {
      long[] var4 = new long[]{var1};
      ThreadInfo[] var5 = this.getThreadInfo(var4, var3);
      return var5[0];
   }

   public ThreadInfo[] getThreadInfo(long[] var1) {
      return this.getThreadInfo(var1, 0);
   }

   private void verifyThreadIds(long[] var1) {
      if (var1 == null) {
         throw new NullPointerException("Null ids parameter.");
      } else {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] <= 0L) {
               throw new IllegalArgumentException("Invalid thread ID parameter: " + var1[var2]);
            }
         }

      }
   }

   public ThreadInfo[] getThreadInfo(long[] var1, int var2) {
      this.verifyThreadIds(var1);
      if (var2 < 0) {
         throw new IllegalArgumentException("Invalid maxDepth parameter: " + var2);
      } else if (var1.length == 0) {
         return new ThreadInfo[0];
      } else {
         Util.checkMonitorAccess();
         ThreadInfo[] var3 = new ThreadInfo[var1.length];
         if (var2 == Integer.MAX_VALUE) {
            getThreadInfo1(var1, -1, var3);
         } else {
            getThreadInfo1(var1, var2, var3);
         }

         return var3;
      }
   }

   public void setThreadContentionMonitoringEnabled(boolean var1) {
      if (!this.isThreadContentionMonitoringSupported()) {
         throw new UnsupportedOperationException("Thread contention monitoring is not supported");
      } else {
         Util.checkControlAccess();
         synchronized(this) {
            if (this.contentionMonitoringEnabled != var1) {
               if (var1) {
                  resetContentionTimes0(0L);
               }

               setThreadContentionMonitoringEnabled0(var1);
               this.contentionMonitoringEnabled = var1;
            }

         }
      }
   }

   private boolean verifyCurrentThreadCpuTime() {
      if (!this.isCurrentThreadCpuTimeSupported()) {
         throw new UnsupportedOperationException("Current thread CPU time measurement is not supported.");
      } else {
         return this.isThreadCpuTimeEnabled();
      }
   }

   public long getCurrentThreadCpuTime() {
      return this.verifyCurrentThreadCpuTime() ? getThreadTotalCpuTime0(0L) : -1L;
   }

   public long getThreadCpuTime(long var1) {
      long[] var3 = new long[]{var1};
      long[] var4 = this.getThreadCpuTime(var3);
      return var4[0];
   }

   private boolean verifyThreadCpuTime(long[] var1) {
      this.verifyThreadIds(var1);
      if (!this.isThreadCpuTimeSupported() && !this.isCurrentThreadCpuTimeSupported()) {
         throw new UnsupportedOperationException("Thread CPU time measurement is not supported.");
      } else {
         if (!this.isThreadCpuTimeSupported()) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
               if (var1[var2] != Thread.currentThread().getId()) {
                  throw new UnsupportedOperationException("Thread CPU time measurement is only supported for the current thread.");
               }
            }
         }

         return this.isThreadCpuTimeEnabled();
      }
   }

   public long[] getThreadCpuTime(long[] var1) {
      boolean var2 = this.verifyThreadCpuTime(var1);
      int var3 = var1.length;
      long[] var4 = new long[var3];
      Arrays.fill(var4, -1L);
      if (var2) {
         if (var3 == 1) {
            long var5 = var1[0];
            if (var5 == Thread.currentThread().getId()) {
               var5 = 0L;
            }

            var4[0] = getThreadTotalCpuTime0(var5);
         } else {
            getThreadTotalCpuTime1(var1, var4);
         }
      }

      return var4;
   }

   public long getCurrentThreadUserTime() {
      return this.verifyCurrentThreadCpuTime() ? getThreadUserCpuTime0(0L) : -1L;
   }

   public long getThreadUserTime(long var1) {
      long[] var3 = new long[]{var1};
      long[] var4 = this.getThreadUserTime(var3);
      return var4[0];
   }

   public long[] getThreadUserTime(long[] var1) {
      boolean var2 = this.verifyThreadCpuTime(var1);
      int var3 = var1.length;
      long[] var4 = new long[var3];
      Arrays.fill(var4, -1L);
      if (var2) {
         if (var3 == 1) {
            long var5 = var1[0];
            if (var5 == Thread.currentThread().getId()) {
               var5 = 0L;
            }

            var4[0] = getThreadUserCpuTime0(var5);
         } else {
            getThreadUserCpuTime1(var1, var4);
         }
      }

      return var4;
   }

   public void setThreadCpuTimeEnabled(boolean var1) {
      if (!this.isThreadCpuTimeSupported() && !this.isCurrentThreadCpuTimeSupported()) {
         throw new UnsupportedOperationException("Thread CPU time measurement is not supported");
      } else {
         Util.checkControlAccess();
         synchronized(this) {
            if (this.cpuTimeEnabled != var1) {
               setThreadCpuTimeEnabled0(var1);
               this.cpuTimeEnabled = var1;
            }

         }
      }
   }

   public long getThreadAllocatedBytes(long var1) {
      long[] var3 = new long[]{var1};
      long[] var4 = this.getThreadAllocatedBytes(var3);
      return var4[0];
   }

   private boolean verifyThreadAllocatedMemory(long[] var1) {
      this.verifyThreadIds(var1);
      if (!this.isThreadAllocatedMemorySupported()) {
         throw new UnsupportedOperationException("Thread allocated memory measurement is not supported.");
      } else {
         return this.isThreadAllocatedMemoryEnabled();
      }
   }

   public long[] getThreadAllocatedBytes(long[] var1) {
      boolean var2 = this.verifyThreadAllocatedMemory(var1);
      long[] var3 = new long[var1.length];
      Arrays.fill(var3, -1L);
      if (var2) {
         getThreadAllocatedMemory1(var1, var3);
      }

      return var3;
   }

   public void setThreadAllocatedMemoryEnabled(boolean var1) {
      if (!this.isThreadAllocatedMemorySupported()) {
         throw new UnsupportedOperationException("Thread allocated memory measurement is not supported.");
      } else {
         Util.checkControlAccess();
         synchronized(this) {
            if (this.allocatedMemoryEnabled != var1) {
               setThreadAllocatedMemoryEnabled0(var1);
               this.allocatedMemoryEnabled = var1;
            }

         }
      }
   }

   public long[] findMonitorDeadlockedThreads() {
      Util.checkMonitorAccess();
      Thread[] var1 = findMonitorDeadlockedThreads0();
      if (var1 == null) {
         return null;
      } else {
         long[] var2 = new long[var1.length];

         for(int var3 = 0; var3 < var1.length; ++var3) {
            Thread var4 = var1[var3];
            var2[var3] = var4.getId();
         }

         return var2;
      }
   }

   public long[] findDeadlockedThreads() {
      if (!this.isSynchronizerUsageSupported()) {
         throw new UnsupportedOperationException("Monitoring of Synchronizer Usage is not supported.");
      } else {
         Util.checkMonitorAccess();
         Thread[] var1 = findDeadlockedThreads0();
         if (var1 == null) {
            return null;
         } else {
            long[] var2 = new long[var1.length];

            for(int var3 = 0; var3 < var1.length; ++var3) {
               Thread var4 = var1[var3];
               var2[var3] = var4.getId();
            }

            return var2;
         }
      }
   }

   public void resetPeakThreadCount() {
      Util.checkControlAccess();
      resetPeakThreadCount0();
   }

   public boolean isObjectMonitorUsageSupported() {
      return this.jvm.isObjectMonitorUsageSupported();
   }

   public boolean isSynchronizerUsageSupported() {
      return this.jvm.isSynchronizerUsageSupported();
   }

   private void verifyDumpThreads(boolean var1, boolean var2) {
      if (var1 && !this.isObjectMonitorUsageSupported()) {
         throw new UnsupportedOperationException("Monitoring of Object Monitor Usage is not supported.");
      } else if (var2 && !this.isSynchronizerUsageSupported()) {
         throw new UnsupportedOperationException("Monitoring of Synchronizer Usage is not supported.");
      } else {
         Util.checkMonitorAccess();
      }
   }

   public ThreadInfo[] getThreadInfo(long[] var1, boolean var2, boolean var3) {
      this.verifyThreadIds(var1);
      if (var1.length == 0) {
         return new ThreadInfo[0];
      } else {
         this.verifyDumpThreads(var2, var3);
         return dumpThreads0(var1, var2, var3);
      }
   }

   public ThreadInfo[] dumpAllThreads(boolean var1, boolean var2) {
      this.verifyDumpThreads(var1, var2);
      return dumpThreads0((long[])null, var1, var2);
   }

   private static native Thread[] getThreads();

   private static native void getThreadInfo1(long[] var0, int var1, ThreadInfo[] var2);

   private static native long getThreadTotalCpuTime0(long var0);

   private static native void getThreadTotalCpuTime1(long[] var0, long[] var1);

   private static native long getThreadUserCpuTime0(long var0);

   private static native void getThreadUserCpuTime1(long[] var0, long[] var1);

   private static native void getThreadAllocatedMemory1(long[] var0, long[] var1);

   private static native void setThreadCpuTimeEnabled0(boolean var0);

   private static native void setThreadAllocatedMemoryEnabled0(boolean var0);

   private static native void setThreadContentionMonitoringEnabled0(boolean var0);

   private static native Thread[] findMonitorDeadlockedThreads0();

   private static native Thread[] findDeadlockedThreads0();

   private static native void resetPeakThreadCount0();

   private static native ThreadInfo[] dumpThreads0(long[] var0, boolean var1, boolean var2);

   private static native void resetContentionTimes0(long var0);

   public ObjectName getObjectName() {
      return Util.newObjectName("java.lang:type=Threading");
   }
}
