package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.AsynchronousSocketChannelImpl")
public class AsynchronousSocketChannelImplRMHooks {
   @InstrumentationMethod
   public final SocketAddress getLocalAddress() throws IOException {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public final AsynchronousSocketChannel bind(SocketAddress var1) throws IOException {
      ResourceIdImpl var2 = null;
      ResourceRequest var3 = null;
      long var4 = 0L;
      if (this.getLocalAddress() == null) {
         var2 = ResourceIdImpl.of((Object)var1);
         var3 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);

         try {
            var4 = var3.request(1L, var2);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many open socket channels");
            }
         } catch (ResourceRequestDeniedException var11) {
            throw new IOException("Resource limited: too many open socket channels", var11);
         }
      }

      byte var6 = 0;
      AsynchronousSocketChannel var7 = null;

      try {
         var7 = this.bind(var1);
         var6 = 1;
      } finally {
         if (var3 != null) {
            var3.request(-(var4 - (long)var6), var2);
         }

      }

      return var7;
   }
}
