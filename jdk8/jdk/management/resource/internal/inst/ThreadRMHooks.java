package jdk.management.resource.internal.inst;

import java.security.AccessControlContext;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import jdk.management.resource.internal.SimpleResourceContext;

@InstrumentationTarget("java.lang.Thread")
public final class ThreadRMHooks {
   private long tid;

   private static synchronized long nextThreadID() {
      return 0L;
   }

   @InstrumentationMethod
   private void init(ThreadGroup var1, Runnable var2, String var3, long var4, AccessControlContext var6, boolean var7) {
      long var8 = nextThreadID();
      ResourceIdImpl var10 = ResourceIdImpl.of((Object)var8);
      ResourceRequest var11 = ApproverGroup.THREAD_CREATED_GROUP.getApprover(this);
      long var12 = 1L;
      long var14 = 0L;

      try {
         var14 = var11.request(var12, var10);
         if (var14 == 0L) {
            throw new ResourceRequestDeniedException("Resource limited: thread creation denied by resource manager");
         }

         this.init(var1, var2, var3, var4, var6, var7);
         SimpleResourceContext var16 = (SimpleResourceContext)SimpleResourceContext.getThreadContext(Thread.currentThread());
         var16.bindNewThreadContext((Thread)this);
      } finally {
         var11.request(-(var14 - var12), var10);
      }

      this.tid = var8;
   }

   @InstrumentationMethod
   private void exit() {
      ResourceIdImpl var1 = ResourceIdImpl.of((Object)this.tid);
      ResourceRequest var2 = ApproverGroup.THREAD_CREATED_GROUP.getApprover(this);

      try {
         var2.request(-1L, var1);
         SimpleResourceContext.removeThreadContext();
      } finally {
         this.exit();
      }

   }
}
