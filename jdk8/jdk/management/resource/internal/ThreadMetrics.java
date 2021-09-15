package jdk.management.resource.internal;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

final class ThreadMetrics {
   static final WeakKeyConcurrentHashMap<Thread, ThreadMetrics> threadMetrics = new WeakKeyConcurrentHashMap();
   private long cputime = 0L;
   private long allocatedHeap = 0L;

   private ThreadMetrics() {
   }

   private synchronized long usedCputime(long var1) {
      long var3 = this.cputime == 0L ? 0L : var1 - this.cputime;
      this.cputime = var1;
      return var3;
   }

   private synchronized long usedAllocatedHeap(long var1) {
      long var3 = this.allocatedHeap == 0L ? 0L : var1 - this.allocatedHeap;
      this.allocatedHeap = var1;
      return var3;
   }

   static void updateCurrentThreadMetrics(ResourceContext var0) {
      ResourceRequest var1 = var0.getResourceRequest(ResourceType.THREAD_CPU);
      ResourceRequest var2 = var0.getResourceRequest(ResourceType.HEAP_ALLOCATED);
      if (var1 != null || var2 != null) {
         updateCurrentThreadMetrics(var1, var2);
      }
   }

   static void updateThreadMetrics(ResourceContext var0) {
      ResourceRequest var1 = var0.getResourceRequest(ResourceType.THREAD_CPU);
      ResourceRequest var2 = var0.getResourceRequest(ResourceType.HEAP_ALLOCATED);
      if (var1 != null || var2 != null) {
         Thread[] var3 = (Thread[])var0.boundThreads().toArray((var0x) -> {
            return new Thread[var0x];
         });
         if (var3.length > 0) {
            updateThreadMetrics(var3, var1, var2);
         }

      }
   }

   static void updateThreadMetrics(ResourceContext var0, Thread var1) {
      ResourceRequest var2 = var0.getResourceRequest(ResourceType.THREAD_CPU);
      ResourceRequest var3 = var0.getResourceRequest(ResourceType.HEAP_ALLOCATED);
      if (var2 != null || var3 != null) {
         Thread[] var4 = new Thread[]{var1};
         updateThreadMetrics(var4, var2, var3);
      }
   }

   private static void updateCurrentThreadMetrics(ResourceRequest var0, ResourceRequest var1) {
      long var2 = ResourceNatives.getCurrentThreadCPUTime();
      long var4 = ResourceNatives.getCurrentThreadAllocatedHeap();
      Thread var6 = Thread.currentThread();
      ThreadMetrics var7 = getThreadMetrics(var6);
      updateMetrics(var7, var6.getId(), var0, var2, var1, var4);
   }

   private static void updateThreadMetrics(Thread[] var0, ResourceRequest var1, ResourceRequest var2) {
      long[] var3 = new long[var0.length];
      long[] var4 = new long[var0.length];
      long[] var5 = new long[var0.length];

      int var6;
      for(var6 = 0; var6 < var0.length; ++var6) {
         var5[var6] = var0[var6] != null ? var0[var6].getId() : Long.MIN_VALUE;
      }

      ResourceNatives.getThreadStats(var5, var3, var4);

      for(var6 = 0; var6 < var0.length; ++var6) {
         if (var3[var6] != -1L && var4[var6] != -1L) {
            ThreadMetrics var7 = getThreadMetrics(var0[var6]);
            updateMetrics(var7, var5[var6], var1, var3[var6], var2, var4[var6]);
         }
      }

   }

   private static void updateMetrics(ThreadMetrics var0, long var1, ResourceRequest var3, long var4, ResourceRequest var6, long var7) {
      long var9 = var0.usedCputime(var4);
      ResourceIdImpl var11 = null;
      if (var9 > 0L && var3 != null) {
         var11 = ResourceIdImpl.of((Object)var1);

         try {
            var3.request(var9, var11);
         } catch (RuntimeException var16) {
         }
      }

      long var12 = var0.usedAllocatedHeap(var7);
      if (var12 > 0L && var6 != null) {
         if (var11 == null) {
            var11 = ResourceIdImpl.of((Object)var1);
         }

         try {
            var6.request(var12, var11);
         } catch (RuntimeException var15) {
         }
      }

   }

   private static ThreadMetrics getThreadMetrics(Thread var0) {
      return (ThreadMetrics)threadMetrics.computeIfAbsent(var0, (var0x) -> {
         return new ThreadMetrics();
      });
   }

   static synchronized void init() {
      int var0 = ResourceNatives.sampleInterval();
      if (var0 != 0) {
         if (var0 < 0) {
            var0 = 100;
         }

         ThreadMetrics.ThreadSampler.init((long)var0);
      }

   }

   private static class ThreadSampler implements Runnable {
      private static ThreadMetrics.ThreadSampler samplerRunnable = null;
      private static ScheduledFuture<?> samplerFuture = null;
      private final long interval;
      private static final ScheduledExecutorService scheduledExecutor = (ScheduledExecutorService)AccessController.doPrivileged((PrivilegedAction)(() -> {
         ThreadGroup var0;
         for(var0 = Thread.currentThread().getThreadGroup(); var0.getParent() != null; var0 = var0.getParent()) {
         }

         ThreadFactory var2 = (var1) -> {
            ResourceContext var2 = SimpleResourceContext.getThreadContext(Thread.currentThread());
            UnassignedContext.getSystemContext().bindThreadContext();
            Thread var3 = new Thread(var0, var1, "ThreadMetrics");
            var3.setDaemon(true);
            var2.bindThreadContext();
            return var3;
         };
         return Executors.newScheduledThreadPool(1, var2);
      }), (AccessControlContext)null, new RuntimePermission("modifyThreadGroup"), new RuntimePermission("modifyThread"));

      static synchronized void init(long var0) {
         if (samplerRunnable == null || var0 != samplerRunnable.interval) {
            terminate();
            samplerRunnable = new ThreadMetrics.ThreadSampler(var0);
            samplerFuture = scheduledExecutor.scheduleAtFixedRate(samplerRunnable, var0, var0, TimeUnit.MILLISECONDS);
         }

      }

      static synchronized void terminate() {
         samplerRunnable = null;
         if (samplerFuture != null) {
            samplerFuture.cancel(false);
         }

      }

      private ThreadSampler(long var1) {
         this.interval = var1;
      }

      public void run() {
         UnassignedContext.getSystemContext().bindThreadContext();

         try {
            SimpleResourceContext.getContexts().forEachValue(2147483647L, (var0) -> {
               ThreadMetrics.updateThreadMetrics(var0);
            });
         } catch (RuntimeException var2) {
         }

      }
   }
}
