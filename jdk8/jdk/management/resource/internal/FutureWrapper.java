package jdk.management.resource.internal;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;

public class FutureWrapper<T> implements Future<T> {
   private final Future<T> future;
   private final ResourceId id;
   private final ResourceRequest ra;
   private final long approved;
   private Object clientChannel;
   private boolean isInvoked;

   public FutureWrapper(Future<T> var1, ResourceId var2, ResourceRequest var3, long var4) {
      this.isInvoked = false;
      this.future = var1;
      this.id = var2;
      this.ra = var3;
      this.approved = var4;
   }

   public FutureWrapper(Future<T> var1) {
      this(var1, (ResourceId)null, (ResourceRequest)null, 0L);
   }

   public FutureWrapper(Future<T> var1, Object var2) {
      this(var1, (ResourceId)null, (ResourceRequest)null, 0L);
      this.clientChannel = var2;
   }

   public boolean cancel(boolean var1) {
      return this.future.cancel(var1);
   }

   public boolean isCancelled() {
      return this.future.isCancelled();
   }

   public boolean isDone() {
      return this.future.isDone();
   }

   public T get() throws InterruptedException, ExecutionException {
      Object var1 = this.future.get();
      this.processResult(var1);
      return var1;
   }

   public T get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      Object var4 = this.future.get(var1, var3);
      this.processResult(var4);
      return var4;
   }

   private synchronized void processResult(T var1) {
      if (!this.isInvoked) {
         this.isInvoked = true;
         if (var1 instanceof Number) {
            int var2 = ((Number)var1).intValue();
            if (var2 == -1) {
               this.ra.request(-this.approved, this.id);
            } else {
               this.ra.request(-(this.approved - (long)var2), this.id);
            }
         } else if (var1 instanceof AsynchronousSocketChannel || this.clientChannel != null) {
            AsynchronousSocketChannel var12 = (AsynchronousSocketChannel)var1;
            if (var1 != null) {
               var12 = (AsynchronousSocketChannel)var1;
            } else {
               var12 = (AsynchronousSocketChannel)this.clientChannel;
            }

            ResourceIdImpl var3 = null;

            try {
               var3 = ResourceIdImpl.of((Object)var12.getLocalAddress());
            } catch (IOException var11) {
            }

            ResourceRequest var4 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(var12);
            long var5 = 0L;
            ResourceRequestDeniedException var7 = null;

            try {
               var5 = var4.request(1L, var3);
               if (var5 < 1L) {
                  var7 = new ResourceRequestDeniedException("Resource limited: too many open server socket channels");
               }
            } catch (ResourceRequestDeniedException var10) {
               var7 = var10;
            }

            if (var7 == null) {
               var4.request(-(var5 - 1L), var3);
            } else {
               var4.request(-var5, var3);

               try {
                  var12.close();
               } catch (IOException var9) {
               }
            }
         }

      }
   }
}
