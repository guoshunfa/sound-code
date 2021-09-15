package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.WindowsAsynchronousServerSocketChannelImpl")
public class WindowsAsynchronousServerSocketChannelImplRMHooks {
   protected final FileDescriptor fd = null;
   protected volatile InetSocketAddress localAddress = null;

   @InstrumentationMethod
   void implClose() throws IOException {
      boolean var7 = false;

      try {
         var7 = true;
         this.implClose();
         var7 = false;
      } finally {
         if (var7) {
            ResourceIdImpl var4 = ResourceIdImpl.of(this.fd);
            ResourceRequest var5;
            if (var4 != null) {
               var5 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
               var5.request(-1L, var4);
            }

            if (this.localAddress != null) {
               var4 = ResourceIdImpl.of((Object)this.localAddress);
               var5 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
               var5.request(-1L, var4);
            }

         }
      }

      ResourceIdImpl var1 = ResourceIdImpl.of(this.fd);
      ResourceRequest var2;
      if (var1 != null) {
         var2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
         var2.request(-1L, var1);
      }

      if (this.localAddress != null) {
         var1 = ResourceIdImpl.of((Object)this.localAddress);
         var2 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
         var2.request(-1L, var1);
      }

   }
}
