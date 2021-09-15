package jdk.management.resource.internal;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ResourceNatives {
   public static final int SYSTEM_RESOURCE_CONTEXT_ID = 0;
   public static final int FEATURE_ENABLED = 1;
   public static final int FEATURE_RETAINED_MEMORY = 2;
   private static int enabledFeatures;

   private ResourceNatives() {
   }

   private static native int featuresEnabled0();

   private static native int sampleInterval0();

   private static native void getThreadStats0(long[] var0, long[] var1, long[] var2);

   private static native long getCurrentThreadCPUTime0();

   private static native long getCurrentThreadAllocatedHeap0();

   public static boolean isEnabled() {
      return (featuresEnabled() & 1) == 1;
   }

   public static boolean isHeapRetainedEnabled() {
      return (featuresEnabled() & 2) == 2;
   }

   public static int featuresEnabled() {
      return enabledFeatures;
   }

   public static int sampleInterval() {
      return sampleInterval0();
   }

   public static void getThreadStats(long[] var0, long[] var1, long[] var2) {
      getThreadStats0(var0, var1, var2);
   }

   public static long getCurrentThreadCPUTime() {
      return getCurrentThreadCPUTime0();
   }

   public static long getCurrentThreadAllocatedHeap() {
      return getCurrentThreadAllocatedHeap0();
   }

   private static native int createResourceContext0(String var0);

   private static native void destroyResourceContext0(int var0, int var1);

   public static native int setThreadResourceContext0(long var0, int var2);

   public static native int getThreadResourceContext0(long var0);

   public static int createResourceContext(String var0) {
      int var1 = createResourceContext0(var0);
      return var1;
   }

   public static void destroyResourceContext(int var0, int var1) {
      destroyResourceContext0(var0, var1);
   }

   public static int setThreadResourceContext(int var0) {
      return setThreadResourceContext0(0L, var0);
   }

   public static int setThreadResourceContext(long var0, int var2) {
      return setThreadResourceContext0(var0, var2);
   }

   public static int getThreadResourceContext() {
      return getThreadResourceContext0(0L);
   }

   private static native boolean getContextsRetainedMemory0(int[] var0, long[] var1, byte[] var2);

   private static native void setRetainedMemoryNotificationEnabled0(Object var0);

   private static native void computeRetainedMemory0(int[] var0, byte var1);

   public static boolean getContextsRetainedMemory(int[] var0, long[] var1, byte[] var2) {
      return getContextsRetainedMemory0(var0, var1, var2);
   }

   public static void setRetainedMemoryNotificationEnabled(Object var0) {
      setRetainedMemoryNotificationEnabled0(var0);
   }

   public static void computeRetainedMemory(int[] var0, int var1) {
      computeRetainedMemory0(var0, (byte)var1);
   }

   static {
      AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("resource");
            return null;
         }
      }), (AccessControlContext)null, new RuntimePermission("loadLibrary.resource"));
      enabledFeatures = featuresEnabled0();
   }
}
