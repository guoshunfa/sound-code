package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.UnixAsynchronousSocketChannelImpl")
public class UnixAsynchronousSocketChannelImplRMHooks {
   protected volatile InetSocketAddress localAddress = null;

   @InstrumentationMethod
   <A> Future<Void> implConnect(SocketAddress var1, A var2, CompletionHandler<Void, ? super A> var3) {
      boolean var4 = this.localAddress != null;
      if (var3 != null && !var4) {
         var3 = new CompletionHandlerWrapper((CompletionHandler)var3, this);
      }

      Object var5 = this.implConnect(var1, var2, (CompletionHandler)var3);
      if (var5 != null && !var4) {
         if (((Future)var5).isDone()) {
            ResourceIdImpl var6 = ResourceIdImpl.of((Object)this.localAddress);
            ResourceRequest var7 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
            long var8 = 0L;
            ResourceRequestDeniedException var10 = null;

            try {
               var8 = var7.request(1L, var6);
               if (var8 < 1L) {
                  var10 = new ResourceRequestDeniedException("Resource limited: too many open sockets");
               }
            } catch (ResourceRequestDeniedException var14) {
               var10 = var14;
            }

            if (var10 != null) {
               var7.request(-var8, var6);
               CompletableFuture var11 = new CompletableFuture();
               var11.completeExceptionally(var10);
               var5 = var11;

               try {
                  this.implClose();
               } catch (IOException var13) {
               }
            } else {
               var7.request(-(var8 - 1L), var6);
            }
         } else {
            var5 = new FutureWrapper((Future)var5, this);
         }
      }

      return (Future)var5;
   }

   @InstrumentationMethod
   <V extends Number, A> Future<V> implRead(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      ResourceIdImpl var9 = ResourceIdImpl.of((Object)this.localAddress);
      ResourceRequest var10 = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
      long var11 = 0L;
      int var13;
      int var15;
      if (var1) {
         var13 = 0;
         ByteBuffer[] var14 = var3;
         var15 = var3.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            ByteBuffer var17 = var14[var16];
            var13 += var17.remaining();
         }
      } else {
         var13 = var2.remaining();
      }

      try {
         var11 = Math.max(var10.request((long)var13, var9), 0L);
         if (var11 < (long)var13) {
            throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var19) {
         if (var8 != null) {
            ((CompletionHandler)var8).failed(var19, var7);
            return null;
         }

         CompletableFuture var21 = new CompletableFuture();
         var21.completeExceptionally(var19);
         return var21;
      }

      if (var8 != null) {
         var8 = new CompletionHandlerWrapper((CompletionHandler)var8, var9, var10, var11);
      }

      Object var20 = this.implRead(var1, var2, var3, var4, var6, var7, (CompletionHandler)var8);
      if (var8 == null) {
         if (((Future)var20).isDone()) {
            var15 = 0;

            try {
               var15 = ((Number)((Future)var20).get()).intValue();
            } catch (ExecutionException | InterruptedException var18) {
            }

            var15 = Math.max(0, var15);
            var10.request(-(var11 - (long)var15), var9);
         } else {
            var20 = new FutureWrapper((Future)var20, var9, var10, var11);
         }
      }

      return (Future)var20;
   }

   @InstrumentationMethod
   <V extends Number, A> Future<V> implWrite(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      ResourceIdImpl var9 = ResourceIdImpl.of((Object)this.localAddress);
      ResourceRequest var10 = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
      long var11 = 0L;
      int var13;
      int var15;
      if (var1) {
         var13 = 0;
         ByteBuffer[] var14 = var3;
         var15 = var3.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            ByteBuffer var17 = var14[var16];
            var13 += var17.remaining();
         }
      } else {
         var13 = var2.remaining();
      }

      try {
         var11 = Math.max(var10.request((long)var13, var9), 0L);
         if (var11 < (long)var13) {
            throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var19) {
         if (var8 != null) {
            ((CompletionHandler)var8).failed(var19, var7);
            return null;
         }

         CompletableFuture var21 = new CompletableFuture();
         var21.completeExceptionally(var19);
         return var21;
      }

      if (var8 != null) {
         var8 = new CompletionHandlerWrapper((CompletionHandler)var8, var9, var10, var11);
      }

      Object var20 = this.implWrite(var1, var2, var3, var4, var6, var7, (CompletionHandler)var8);
      if (var8 == null) {
         if (((Future)var20).isDone()) {
            var15 = 0;

            try {
               var15 = ((Number)((Future)var20).get()).intValue();
            } catch (ExecutionException | InterruptedException var18) {
            }

            var15 = Math.max(0, var15);
            var10.request(-(var11 - (long)var15), var9);
         } else {
            var20 = new FutureWrapper((Future)var20, var9, var10, var11);
         }
      }

      return (Future)var20;
   }

   @InstrumentationMethod
   void implClose() throws IOException {
      boolean var7 = false;

      try {
         var7 = true;
         this.implClose();
         var7 = false;
      } finally {
         if (var7) {
            if (this.localAddress != null) {
               ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.localAddress);
               ResourceRequest var5 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
               var5.request(-1L, var4);
            }

         }
      }

      if (this.localAddress != null) {
         ResourceIdImpl var1 = ResourceIdImpl.of((Object)this.localAddress);
         ResourceRequest var2 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
         var2.request(-1L, var1);
      }

   }
}
