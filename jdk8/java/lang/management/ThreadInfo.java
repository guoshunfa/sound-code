package java.lang.management;

import javax.management.openmbean.CompositeData;
import sun.management.ManagementFactoryHelper;
import sun.management.ThreadInfoCompositeData;

public class ThreadInfo {
   private String threadName;
   private long threadId;
   private long blockedTime;
   private long blockedCount;
   private long waitedTime;
   private long waitedCount;
   private LockInfo lock;
   private String lockName;
   private long lockOwnerId;
   private String lockOwnerName;
   private boolean inNative;
   private boolean suspended;
   private Thread.State threadState;
   private StackTraceElement[] stackTrace;
   private MonitorInfo[] lockedMonitors;
   private LockInfo[] lockedSynchronizers;
   private static MonitorInfo[] EMPTY_MONITORS = new MonitorInfo[0];
   private static LockInfo[] EMPTY_SYNCS = new LockInfo[0];
   private static final int MAX_FRAMES = 8;
   private static final StackTraceElement[] NO_STACK_TRACE = new StackTraceElement[0];

   private ThreadInfo(Thread var1, int var2, Object var3, Thread var4, long var5, long var7, long var9, long var11, StackTraceElement[] var13) {
      this.initialize(var1, var2, var3, var4, var5, var7, var9, var11, var13, EMPTY_MONITORS, EMPTY_SYNCS);
   }

   private ThreadInfo(Thread var1, int var2, Object var3, Thread var4, long var5, long var7, long var9, long var11, StackTraceElement[] var13, Object[] var14, int[] var15, Object[] var16) {
      int var17 = var14 == null ? 0 : var14.length;
      MonitorInfo[] var18;
      int var19;
      if (var17 == 0) {
         var18 = EMPTY_MONITORS;
      } else {
         var18 = new MonitorInfo[var17];

         for(var19 = 0; var19 < var17; ++var19) {
            Object var20 = var14[var19];
            String var21 = var20.getClass().getName();
            int var22 = System.identityHashCode(var20);
            int var23 = var15[var19];
            StackTraceElement var24 = var23 >= 0 ? var13[var23] : null;
            var18[var19] = new MonitorInfo(var21, var22, var23, var24);
         }
      }

      var19 = var16 == null ? 0 : var16.length;
      LockInfo[] var29;
      if (var19 == 0) {
         var29 = EMPTY_SYNCS;
      } else {
         var29 = new LockInfo[var19];

         for(int var25 = 0; var25 < var19; ++var25) {
            Object var26 = var16[var25];
            String var27 = var26.getClass().getName();
            int var28 = System.identityHashCode(var26);
            var29[var25] = new LockInfo(var27, var28);
         }
      }

      this.initialize(var1, var2, var3, var4, var5, var7, var9, var11, var13, var18, var29);
   }

   private void initialize(Thread var1, int var2, Object var3, Thread var4, long var5, long var7, long var9, long var11, StackTraceElement[] var13, MonitorInfo[] var14, LockInfo[] var15) {
      this.threadId = var1.getId();
      this.threadName = var1.getName();
      this.threadState = ManagementFactoryHelper.toThreadState(var2);
      this.suspended = ManagementFactoryHelper.isThreadSuspended(var2);
      this.inNative = ManagementFactoryHelper.isThreadRunningNative(var2);
      this.blockedCount = var5;
      this.blockedTime = var7;
      this.waitedCount = var9;
      this.waitedTime = var11;
      if (var3 == null) {
         this.lock = null;
         this.lockName = null;
      } else {
         this.lock = new LockInfo(var3);
         this.lockName = this.lock.getClassName() + '@' + Integer.toHexString(this.lock.getIdentityHashCode());
      }

      if (var4 == null) {
         this.lockOwnerId = -1L;
         this.lockOwnerName = null;
      } else {
         this.lockOwnerId = var4.getId();
         this.lockOwnerName = var4.getName();
      }

      if (var13 == null) {
         this.stackTrace = NO_STACK_TRACE;
      } else {
         this.stackTrace = var13;
      }

      this.lockedMonitors = var14;
      this.lockedSynchronizers = var15;
   }

   private ThreadInfo(CompositeData var1) {
      ThreadInfoCompositeData var2 = ThreadInfoCompositeData.getInstance(var1);
      this.threadId = var2.threadId();
      this.threadName = var2.threadName();
      this.blockedTime = var2.blockedTime();
      this.blockedCount = var2.blockedCount();
      this.waitedTime = var2.waitedTime();
      this.waitedCount = var2.waitedCount();
      this.lockName = var2.lockName();
      this.lockOwnerId = var2.lockOwnerId();
      this.lockOwnerName = var2.lockOwnerName();
      this.threadState = var2.threadState();
      this.suspended = var2.suspended();
      this.inNative = var2.inNative();
      this.stackTrace = var2.stackTrace();
      if (var2.isCurrentVersion()) {
         this.lock = var2.lockInfo();
         this.lockedMonitors = var2.lockedMonitors();
         this.lockedSynchronizers = var2.lockedSynchronizers();
      } else {
         if (this.lockName != null) {
            String[] var3 = this.lockName.split("@");
            if (var3.length == 2) {
               int var4 = Integer.parseInt(var3[1], 16);
               this.lock = new LockInfo(var3[0], var4);
            } else {
               assert var3.length == 2;

               this.lock = null;
            }
         } else {
            this.lock = null;
         }

         this.lockedMonitors = EMPTY_MONITORS;
         this.lockedSynchronizers = EMPTY_SYNCS;
      }

   }

   public long getThreadId() {
      return this.threadId;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public Thread.State getThreadState() {
      return this.threadState;
   }

   public long getBlockedTime() {
      return this.blockedTime;
   }

   public long getBlockedCount() {
      return this.blockedCount;
   }

   public long getWaitedTime() {
      return this.waitedTime;
   }

   public long getWaitedCount() {
      return this.waitedCount;
   }

   public LockInfo getLockInfo() {
      return this.lock;
   }

   public String getLockName() {
      return this.lockName;
   }

   public long getLockOwnerId() {
      return this.lockOwnerId;
   }

   public String getLockOwnerName() {
      return this.lockOwnerName;
   }

   public StackTraceElement[] getStackTrace() {
      return this.stackTrace;
   }

   public boolean isSuspended() {
      return this.suspended;
   }

   public boolean isInNative() {
      return this.inNative;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("\"" + this.getThreadName() + "\" Id=" + this.getThreadId() + " " + this.getThreadState());
      if (this.getLockName() != null) {
         var1.append(" on " + this.getLockName());
      }

      if (this.getLockOwnerName() != null) {
         var1.append(" owned by \"" + this.getLockOwnerName() + "\" Id=" + this.getLockOwnerId());
      }

      if (this.isSuspended()) {
         var1.append(" (suspended)");
      }

      if (this.isInNative()) {
         var1.append(" (in native)");
      }

      var1.append('\n');

      int var2;
      int var5;
      int var6;
      for(var2 = 0; var2 < this.stackTrace.length && var2 < 8; ++var2) {
         StackTraceElement var3 = this.stackTrace[var2];
         var1.append("\tat " + var3.toString());
         var1.append('\n');
         if (var2 == 0 && this.getLockInfo() != null) {
            Thread.State var4 = this.getThreadState();
            switch(var4) {
            case BLOCKED:
               var1.append("\t-  blocked on " + this.getLockInfo());
               var1.append('\n');
               break;
            case WAITING:
               var1.append("\t-  waiting on " + this.getLockInfo());
               var1.append('\n');
               break;
            case TIMED_WAITING:
               var1.append("\t-  waiting on " + this.getLockInfo());
               var1.append('\n');
            }
         }

         MonitorInfo[] var9 = this.lockedMonitors;
         var5 = var9.length;

         for(var6 = 0; var6 < var5; ++var6) {
            MonitorInfo var7 = var9[var6];
            if (var7.getLockedStackDepth() == var2) {
               var1.append("\t-  locked " + var7);
               var1.append('\n');
            }
         }
      }

      if (var2 < this.stackTrace.length) {
         var1.append("\t...");
         var1.append('\n');
      }

      LockInfo[] var8 = this.getLockedSynchronizers();
      if (var8.length > 0) {
         var1.append("\n\tNumber of locked synchronizers = " + var8.length);
         var1.append('\n');
         LockInfo[] var10 = var8;
         var5 = var8.length;

         for(var6 = 0; var6 < var5; ++var6) {
            LockInfo var11 = var10[var6];
            var1.append("\t- " + var11);
            var1.append('\n');
         }
      }

      var1.append('\n');
      return var1.toString();
   }

   public static ThreadInfo from(CompositeData var0) {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof ThreadInfoCompositeData ? ((ThreadInfoCompositeData)var0).getThreadInfo() : new ThreadInfo(var0);
      }
   }

   public MonitorInfo[] getLockedMonitors() {
      return this.lockedMonitors;
   }

   public LockInfo[] getLockedSynchronizers() {
      return this.lockedSynchronizers;
   }
}
