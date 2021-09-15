package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.AsynchronousServerSocketChannelImpl")
public class AsynchronousServerSocketChannelImplRMHooks {
   @InstrumentationMethod
   public final SocketAddress getLocalAddress() throws IOException {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public final AsynchronousServerSocketChannel bind(SocketAddress var1, int var2) throws IOException {
      ResourceIdImpl var3 = null;
      ResourceRequest var4 = null;
      long var5 = 0L;
      if (this.getLocalAddress() == null) {
         var3 = ResourceIdImpl.of((Object)var1);
         var4 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);

         try {
            var5 = var4.request(1L, var3);
            if (var5 < 1L) {
               throw new IOException("Resource limited: too many open socket channels");
            }
         } catch (ResourceRequestDeniedException var12) {
            throw new IOException("Resource limited: too many open socket channels", var12);
         }
      }

      byte var7 = 0;
      AsynchronousServerSocketChannel var8 = null;

      try {
         var8 = this.bind(var1, var2);
         var7 = 1;
      } finally {
         if (var4 != null) {
            var4.request(-(var5 - (long)var7), var3);
         }

      }

      return var8;
   }

   @InstrumentationMethod
   public final Future<AsynchronousSocketChannel> accept() {
      Object var1 = this.accept();
      if (((Future)var1).isDone()) {
         AsynchronousSocketChannel var2;
         CompletableFuture var4;
         try {
            var2 = (AsynchronousSocketChannel)((Future)var1).get();
         } catch (InterruptedException var12) {
            var4 = new CompletableFuture();
            var4.completeExceptionally(var12);
            return var4;
         } catch (ExecutionException var13) {
            var4 = new CompletableFuture();
            var4.completeExceptionally(var13.getCause());
            return var4;
         }

         ResourceIdImpl var3 = null;

         try {
            var3 = ResourceIdImpl.of((Object)var2.getLocalAddress());
         } catch (IOException var11) {
         }

         ResourceRequest var15 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(var2);
         long var5 = 0L;
         ResourceRequestDeniedException var7 = null;

         try {
            var5 = var15.request(1L, var3);
            if (var5 < 1L) {
               var7 = new ResourceRequestDeniedException("Resource limited: too many open server socket channels");
            }
         } catch (ResourceRequestDeniedException var10) {
            var7 = var10;
         }

         if (var7 != null) {
            var15.request(-var5, var3);

            try {
               var2.close();
            } catch (IOException var9) {
            }

            CompletableFuture var8 = new CompletableFuture();
            var8.completeExceptionally(var7);
            return var8;
         }

         var15.request(-(var5 - 1L), var3);
      } else {
         FutureWrapper var14 = new FutureWrapper((Future)var1);
         var1 = var14;
      }

      return (Future)var1;
   }

   @InstrumentationMethod
   public final <A> void accept(A var1, CompletionHandler<AsynchronousSocketChannel, ? super A> var2) {
      if (var2 == null) {
         throw new NullPointerException("'handler' is null");
      } else {
         CompletionHandlerWrapper var3 = new CompletionHandlerWrapper(var2);
         this.accept(var1, var3);
      }
   }
}
