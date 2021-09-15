package jdk.management.resource.internal;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;

public class CompletionHandlerWrapper<V, A> implements CompletionHandler<V, A> {
   private final CompletionHandler<V, ? super A> handler;
   private final ResourceId id;
   private final ResourceRequest ra;
   private final long approved;
   private Object clientChannel;

   public CompletionHandlerWrapper(CompletionHandler<V, ? super A> var1, ResourceId var2, ResourceRequest var3, long var4) {
      this.handler = var1;
      this.id = var2;
      this.ra = var3;
      this.approved = var4;
   }

   public CompletionHandlerWrapper(CompletionHandler<V, ? super A> var1) {
      this(var1, (ResourceId)null, (ResourceRequest)null, 0L);
   }

   public CompletionHandlerWrapper(CompletionHandler<V, ? super A> var1, Object var2) {
      this(var1, (ResourceId)null, (ResourceRequest)null, 0L);
      this.clientChannel = var2;
   }

   public void completed(V var1, A var2) {
      if (var1 instanceof Number) {
         int var3 = ((Number)var1).intValue();
         if (var3 == -1) {
            this.ra.request(-this.approved, this.id);
         } else {
            this.ra.request(-(this.approved - (long)var3), this.id);
         }
      } else if (var1 instanceof AsynchronousSocketChannel || this.clientChannel != null) {
         AsynchronousSocketChannel var13;
         if (var1 != null) {
            var13 = (AsynchronousSocketChannel)var1;
         } else {
            var13 = (AsynchronousSocketChannel)this.clientChannel;
         }

         ResourceIdImpl var4 = null;

         try {
            var4 = ResourceIdImpl.of((Object)var13.getLocalAddress());
         } catch (IOException var12) {
         }

         ResourceRequest var5 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(var13);
         long var6 = 0L;
         ResourceRequestDeniedException var8 = null;

         try {
            var6 = var5.request(1L, var4);
            if (var6 < 1L) {
               var8 = new ResourceRequestDeniedException("Resource limited: too many open server socket channels");
            }
         } catch (ResourceRequestDeniedException var11) {
            var8 = var11;
         }

         if (var8 != null) {
            var5.request(-var6, var4);

            try {
               var13.close();
            } catch (IOException var10) {
            }

            if (this.handler != null) {
               this.handler.failed(var8, var2);
            }

            return;
         }

         var5.request(-(var6 - 1L), var4);
      }

      if (this.handler != null) {
         this.handler.completed(var1, var2);
      }

   }

   public void failed(Throwable var1, A var2) {
      if (this.ra != null && this.id != null) {
         this.ra.request(-this.approved, this.id);
      }

      if (this.handler != null) {
         this.handler.failed(var1, var2);
      }

   }
}
