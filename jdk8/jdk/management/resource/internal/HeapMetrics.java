package jdk.management.resource.internal;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import jdk.management.resource.ResourceAccuracy;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceMeter;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

class HeapMetrics implements Runnable {
   private static final HeapMetrics instance = new HeapMetrics();
   private static volatile Thread thread;
   private static ResourceIdImpl[] idWithAccuracy;
   private static ResourceIdImpl[] idWithAccuracyForced;

   private HeapMetrics() {
   }

   static void init() {
      synchronized(instance) {
         if (thread != null) {
            if (thread.isAlive()) {
               return;
            }

            terminate();
         }

         thread = (Thread)AccessController.doPrivileged((PrivilegedAction)(() -> {
            ThreadGroup var0;
            for(var0 = Thread.currentThread().getThreadGroup(); var0.getParent() != null; var0 = var0.getParent()) {
            }

            ResourceContext var1 = SimpleResourceContext.getThreadContext(Thread.currentThread());
            UnassignedContext.getSystemContext().bindThreadContext();
            thread = new Thread(var0, instance, "HeapMetrics");
            thread.setDaemon(true);
            var1.bindThreadContext();
            return thread;
         }), (AccessControlContext)null, new RuntimePermission("modifyThreadGroup"), new RuntimePermission("modifyThread"));
         thread.start();
      }
   }

   static void terminate() {
      synchronized(instance) {
         if (thread != null) {
            Thread var1 = thread;
            thread = null;
            var1.interrupt();
         }

      }
   }

   private ResourceId selectId(int var1, boolean var2) {
      return var2 ? idWithAccuracyForced[var1] : idWithAccuracy[var1];
   }

   public void run() {
      Object var1 = new Object();
      ResourceNatives.setRetainedMemoryNotificationEnabled(var1);
      UnassignedContext.getSystemContext().bindThreadContext();
      UnassignedContext var2 = UnassignedContext.getUnassignedContext();
      long[] var3 = new long[1];
      int[] var4 = new int[1];
      byte[] var5 = new byte[1];
      synchronized(var1) {
         while(Thread.currentThread().equals(thread)) {
            try {
               boolean var7 = false;

               do {
                  SimpleResourceContext[] var8 = (SimpleResourceContext[])SimpleResourceContext.getContexts().values().toArray(new SimpleResourceContext[0]);
                  int var9 = var8.length;
                  if (var9 + 1 != var3.length) {
                     var3 = new long[var9 + 1];
                     var4 = new int[var9 + 1];
                     var5 = new byte[var9 + 1];
                  }

                  int var10;
                  for(var10 = 0; var10 < var9; ++var10) {
                     var4[var10] = var8[var10].nativeThreadContext();
                  }

                  var4[var9] = var2.nativeThreadContext();
                  Arrays.fill(var3, -1L);
                  var7 = ResourceNatives.getContextsRetainedMemory(var4, var3, var5);

                  for(var10 = 0; var10 <= var9; ++var10) {
                     if (var3[var10] != -1L) {
                        Object var11 = var10 < var9 ? var8[var10] : var2;
                        ResourceRequest var12 = ((ResourceContext)var11).getResourceRequest(ResourceType.HEAP_RETAINED);
                        if (var12 != null) {
                           long var13 = var3[var10] - ((ResourceMeter)var12).getValue();
                           boolean var15 = var5[var10] >= ResourceAccuracy.HIGH.ordinal();
                           if (var13 != 0L || var15) {
                              var12.request(var13, this.selectId(var5[var10], var15));
                           }
                        }
                     }
                  }
               } while(var7);

               var1.wait();
            } catch (InterruptedException var17) {
            }
         }

      }
   }

   static {
      idWithAccuracy = new ResourceIdImpl[]{ResourceIdImpl.of("Heap", ResourceAccuracy.LOW), ResourceIdImpl.of("Heap", ResourceAccuracy.MEDIUM), ResourceIdImpl.of("Heap", ResourceAccuracy.HIGH), ResourceIdImpl.of("Heap", ResourceAccuracy.HIGHEST)};
      idWithAccuracyForced = new ResourceIdImpl[]{ResourceIdImpl.of("Heap", ResourceAccuracy.LOW, true), ResourceIdImpl.of("Heap", ResourceAccuracy.MEDIUM, true), ResourceIdImpl.of("Heap", ResourceAccuracy.HIGH, true), ResourceIdImpl.of("Heap", ResourceAccuracy.HIGHEST, true)};
   }
}
