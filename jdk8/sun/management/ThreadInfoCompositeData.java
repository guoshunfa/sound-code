package sun.management;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

public class ThreadInfoCompositeData extends LazyCompositeData {
   private final ThreadInfo threadInfo;
   private final CompositeData cdata;
   private final boolean currentVersion;
   private static final String THREAD_ID = "threadId";
   private static final String THREAD_NAME = "threadName";
   private static final String THREAD_STATE = "threadState";
   private static final String BLOCKED_TIME = "blockedTime";
   private static final String BLOCKED_COUNT = "blockedCount";
   private static final String WAITED_TIME = "waitedTime";
   private static final String WAITED_COUNT = "waitedCount";
   private static final String LOCK_INFO = "lockInfo";
   private static final String LOCK_NAME = "lockName";
   private static final String LOCK_OWNER_ID = "lockOwnerId";
   private static final String LOCK_OWNER_NAME = "lockOwnerName";
   private static final String STACK_TRACE = "stackTrace";
   private static final String SUSPENDED = "suspended";
   private static final String IN_NATIVE = "inNative";
   private static final String LOCKED_MONITORS = "lockedMonitors";
   private static final String LOCKED_SYNCS = "lockedSynchronizers";
   private static final String[] threadInfoItemNames = new String[]{"threadId", "threadName", "threadState", "blockedTime", "blockedCount", "waitedTime", "waitedCount", "lockInfo", "lockName", "lockOwnerId", "lockOwnerName", "stackTrace", "suspended", "inNative", "lockedMonitors", "lockedSynchronizers"};
   private static final String[] threadInfoV6Attributes = new String[]{"lockInfo", "lockedMonitors", "lockedSynchronizers"};
   private static final CompositeType threadInfoCompositeType;
   private static final CompositeType threadInfoV5CompositeType;
   private static final CompositeType lockInfoCompositeType;
   private static final long serialVersionUID = 2464378539119753175L;

   private ThreadInfoCompositeData(ThreadInfo var1) {
      this.threadInfo = var1;
      this.currentVersion = true;
      this.cdata = null;
   }

   private ThreadInfoCompositeData(CompositeData var1) {
      this.threadInfo = null;
      this.currentVersion = isCurrentVersion(var1);
      this.cdata = var1;
   }

   public ThreadInfo getThreadInfo() {
      return this.threadInfo;
   }

   public boolean isCurrentVersion() {
      return this.currentVersion;
   }

   public static ThreadInfoCompositeData getInstance(CompositeData var0) {
      validateCompositeData(var0);
      return new ThreadInfoCompositeData(var0);
   }

   public static CompositeData toCompositeData(ThreadInfo var0) {
      ThreadInfoCompositeData var1 = new ThreadInfoCompositeData(var0);
      return var1.getCompositeData();
   }

   protected CompositeData getCompositeData() {
      StackTraceElement[] var1 = this.threadInfo.getStackTrace();
      CompositeData[] var2 = new CompositeData[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         StackTraceElement var4 = var1[var3];
         var2[var3] = StackTraceElementCompositeData.toCompositeData(var4);
      }

      CompositeData var11 = LockInfoCompositeData.toCompositeData(this.threadInfo.getLockInfo());
      LockInfo[] var12 = this.threadInfo.getLockedSynchronizers();
      CompositeData[] var5 = new CompositeData[var12.length];

      for(int var6 = 0; var6 < var12.length; ++var6) {
         LockInfo var7 = var12[var6];
         var5[var6] = LockInfoCompositeData.toCompositeData(var7);
      }

      MonitorInfo[] var13 = this.threadInfo.getLockedMonitors();
      CompositeData[] var14 = new CompositeData[var13.length];

      for(int var8 = 0; var8 < var13.length; ++var8) {
         MonitorInfo var9 = var13[var8];
         var14[var8] = MonitorInfoCompositeData.toCompositeData(var9);
      }

      Object[] var15 = new Object[]{new Long(this.threadInfo.getThreadId()), this.threadInfo.getThreadName(), this.threadInfo.getThreadState().name(), new Long(this.threadInfo.getBlockedTime()), new Long(this.threadInfo.getBlockedCount()), new Long(this.threadInfo.getWaitedTime()), new Long(this.threadInfo.getWaitedCount()), var11, this.threadInfo.getLockName(), new Long(this.threadInfo.getLockOwnerId()), this.threadInfo.getLockOwnerName(), var2, new Boolean(this.threadInfo.isSuspended()), new Boolean(this.threadInfo.isInNative()), var14, var5};

      try {
         return new CompositeDataSupport(threadInfoCompositeType, threadInfoItemNames, var15);
      } catch (OpenDataException var10) {
         throw new AssertionError(var10);
      }
   }

   private static boolean isV5Attribute(String var0) {
      String[] var1 = threadInfoV6Attributes;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (var0.equals(var4)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isCurrentVersion(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else {
         return isTypeMatched(threadInfoCompositeType, var0.getCompositeType());
      }
   }

   public long threadId() {
      return getLong(this.cdata, "threadId");
   }

   public String threadName() {
      String var1 = getString(this.cdata, "threadName");
      if (var1 == null) {
         throw new IllegalArgumentException("Invalid composite data: Attribute threadName has null value");
      } else {
         return var1;
      }
   }

   public Thread.State threadState() {
      return Thread.State.valueOf(getString(this.cdata, "threadState"));
   }

   public long blockedTime() {
      return getLong(this.cdata, "blockedTime");
   }

   public long blockedCount() {
      return getLong(this.cdata, "blockedCount");
   }

   public long waitedTime() {
      return getLong(this.cdata, "waitedTime");
   }

   public long waitedCount() {
      return getLong(this.cdata, "waitedCount");
   }

   public String lockName() {
      return getString(this.cdata, "lockName");
   }

   public long lockOwnerId() {
      return getLong(this.cdata, "lockOwnerId");
   }

   public String lockOwnerName() {
      return getString(this.cdata, "lockOwnerName");
   }

   public boolean suspended() {
      return getBoolean(this.cdata, "suspended");
   }

   public boolean inNative() {
      return getBoolean(this.cdata, "inNative");
   }

   public StackTraceElement[] stackTrace() {
      CompositeData[] var1 = (CompositeData[])((CompositeData[])this.cdata.get("stackTrace"));
      StackTraceElement[] var2 = new StackTraceElement[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         CompositeData var4 = var1[var3];
         var2[var3] = StackTraceElementCompositeData.from(var4);
      }

      return var2;
   }

   public LockInfo lockInfo() {
      CompositeData var1 = (CompositeData)this.cdata.get("lockInfo");
      return LockInfo.from(var1);
   }

   public MonitorInfo[] lockedMonitors() {
      CompositeData[] var1 = (CompositeData[])((CompositeData[])this.cdata.get("lockedMonitors"));
      MonitorInfo[] var2 = new MonitorInfo[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         CompositeData var4 = var1[var3];
         var2[var3] = MonitorInfo.from(var4);
      }

      return var2;
   }

   public LockInfo[] lockedSynchronizers() {
      CompositeData[] var1 = (CompositeData[])((CompositeData[])this.cdata.get("lockedSynchronizers"));
      LockInfo[] var2 = new LockInfo[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         CompositeData var4 = var1[var3];
         var2[var3] = LockInfo.from(var4);
      }

      return var2;
   }

   public static void validateCompositeData(CompositeData var0) {
      if (var0 == null) {
         throw new NullPointerException("Null CompositeData");
      } else {
         CompositeType var1 = var0.getCompositeType();
         boolean var2 = true;
         if (!isTypeMatched(threadInfoCompositeType, var1)) {
            var2 = false;
            if (!isTypeMatched(threadInfoV5CompositeType, var1)) {
               throw new IllegalArgumentException("Unexpected composite type for ThreadInfo");
            }
         }

         CompositeData[] var3 = (CompositeData[])((CompositeData[])var0.get("stackTrace"));
         if (var3 == null) {
            throw new IllegalArgumentException("StackTraceElement[] is missing");
         } else {
            if (var3.length > 0) {
               StackTraceElementCompositeData.validateCompositeData(var3[0]);
            }

            if (var2) {
               CompositeData var4 = (CompositeData)var0.get("lockInfo");
               if (var4 != null && !isTypeMatched(lockInfoCompositeType, var4.getCompositeType())) {
                  throw new IllegalArgumentException("Unexpected composite type for \"lockInfo\" attribute.");
               }

               CompositeData[] var5 = (CompositeData[])((CompositeData[])var0.get("lockedMonitors"));
               if (var5 == null) {
                  throw new IllegalArgumentException("MonitorInfo[] is null");
               }

               if (var5.length > 0) {
                  MonitorInfoCompositeData.validateCompositeData(var5[0]);
               }

               CompositeData[] var6 = (CompositeData[])((CompositeData[])var0.get("lockedSynchronizers"));
               if (var6 == null) {
                  throw new IllegalArgumentException("LockInfo[] is null");
               }

               if (var6.length > 0 && !isTypeMatched(lockInfoCompositeType, var6[0].getCompositeType())) {
                  throw new IllegalArgumentException("Unexpected composite type for \"lockedSynchronizers\" attribute.");
               }
            }

         }
      }
   }

   static {
      try {
         threadInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(ThreadInfo.class);
         String[] var0 = (String[])threadInfoCompositeType.keySet().toArray(new String[0]);
         int var1 = threadInfoItemNames.length - threadInfoV6Attributes.length;
         String[] var2 = new String[var1];
         String[] var3 = new String[var1];
         OpenType[] var4 = new OpenType[var1];
         int var5 = 0;
         String[] var6 = var0;
         int var7 = var0.length;
         int var8 = 0;

         while(true) {
            if (var8 >= var7) {
               threadInfoV5CompositeType = new CompositeType("java.lang.management.ThreadInfo", "J2SE 5.0 java.lang.management.ThreadInfo", var2, var3, var4);
               break;
            }

            String var9 = var6[var8];
            if (isV5Attribute(var9)) {
               var2[var5] = var9;
               var3[var5] = threadInfoCompositeType.getDescription(var9);
               var4[var5] = threadInfoCompositeType.getType(var9);
               ++var5;
            }

            ++var8;
         }
      } catch (OpenDataException var10) {
         throw new AssertionError(var10);
      }

      Object var11 = new Object();
      LockInfo var12 = new LockInfo(var11.getClass().getName(), System.identityHashCode(var11));
      CompositeData var13 = LockInfoCompositeData.toCompositeData(var12);
      lockInfoCompositeType = var13.getCompositeType();
   }
}
