package com.apple.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public final class Dispatch {
   static final Dispatch instance = new Dispatch();
   int queueIndex = 0;
   Executor nonBlockingMainQueue = null;
   Executor blockingMainQueue = null;

   public static Dispatch getInstance() {
      checkSecurity();
      return !LibDispatchNative.nativeIsDispatchSupported() ? null : instance;
   }

   private static void checkSecurity() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(new RuntimePermission("canInvokeInSystemThreadGroup"));
      }

   }

   private Dispatch() {
   }

   public Executor getAsyncExecutor(Dispatch.Priority var1) {
      if (var1 == null) {
         var1 = Dispatch.Priority.NORMAL;
      }

      long var2 = LibDispatchNative.nativeCreateConcurrentQueue(var1.nativePriority);
      return var2 == 0L ? null : new LibDispatchConcurrentQueue(var2);
   }

   public ExecutorService createSerialExecutor(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      if (var1.length() > 256) {
         var1 = var1.substring(0, 256);
      }

      String var2 = "com.apple.java.concurrent.";
      if ("".equals(var1)) {
         synchronized(this) {
            var2 = var2 + this.queueIndex++;
         }
      } else {
         var2 = var2 + var1;
      }

      long var3 = LibDispatchNative.nativeCreateSerialQueue(var2);
      return var3 == 0L ? null : new LibDispatchSerialQueue(var3);
   }

   public synchronized Executor getNonBlockingMainQueueExecutor() {
      return this.nonBlockingMainQueue != null ? this.nonBlockingMainQueue : (this.nonBlockingMainQueue = new LibDispatchMainQueue.ASync());
   }

   public synchronized Executor getBlockingMainQueueExecutor() {
      return this.blockingMainQueue != null ? this.blockingMainQueue : (this.blockingMainQueue = new LibDispatchMainQueue.Sync());
   }

   public static enum Priority {
      LOW(-2),
      NORMAL(0),
      HIGH(2);

      final int nativePriority;

      private Priority(int var3) {
         this.nativePriority = var3;
      }
   }
}
