package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.nio.channels.spi.AbstractInterruptibleChannel")
public final class AbstractInterruptibleChannelRMHooks {
   private final Object closeLock = new Object();
   private volatile boolean open = true;

   @InstrumentationMethod
   public final void close() throws IOException {
      synchronized(this.closeLock) {
         if (!this.open) {
            return;
         }
      }

      ResourceIdImpl var1 = null;
      SocketAddress var2 = null;
      if (DatagramChannel.class.isInstance(this)) {
         DatagramChannel var3 = (DatagramChannel)this;
         var2 = var3.getLocalAddress();
         var1 = ResourceIdImpl.of((Object)var2);
      } else if (SocketChannel.class.isInstance(this)) {
         SocketChannel var11 = (SocketChannel)this;
         var2 = var11.getLocalAddress();
         var1 = ResourceIdImpl.of((Object)var2);
      } else if (ServerSocketChannel.class.isInstance(this)) {
         ServerSocketChannel var12 = (ServerSocketChannel)this;
         var2 = var12.getLocalAddress();
         var1 = ResourceIdImpl.of((Object)var2);
      }

      boolean var8 = false;

      try {
         var8 = true;
         this.close();
         var8 = false;
      } finally {
         if (var8) {
            if (var2 != null) {
               ResourceRequest var5;
               if (DatagramChannel.class.isInstance(this)) {
                  var5 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
                  var5.request(-1L, var1);
               } else if (SocketChannel.class.isInstance(this) || ServerSocketChannel.class.isInstance(this)) {
                  var5 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
                  var5.request(-1L, var1);
               }
            }

         }
      }

      if (var2 != null) {
         ResourceRequest var13;
         if (DatagramChannel.class.isInstance(this)) {
            var13 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
            var13.request(-1L, var1);
         } else if (SocketChannel.class.isInstance(this) || ServerSocketChannel.class.isInstance(this)) {
            var13 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
            var13.request(-1L, var1);
         }
      }

   }
}
