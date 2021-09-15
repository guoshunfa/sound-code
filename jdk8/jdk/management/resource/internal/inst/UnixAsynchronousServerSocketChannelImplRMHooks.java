package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.UnixAsynchronousServerSocketChannelImpl")
public class UnixAsynchronousServerSocketChannelImplRMHooks {
   private static final UnixAsynchronousServerSocketChannelImplRMHooks.NativeDispatcher nd = null;
   protected volatile InetSocketAddress localAddress = null;

   @InstrumentationMethod
   private int accept(FileDescriptor var1, FileDescriptor var2, InetSocketAddress[] var3) throws IOException {
      int var4 = this.accept(var1, var2, var3);
      ResourceIdImpl var5 = ResourceIdImpl.of(var2);
      if (var5 != null) {
         ResourceRequest var6 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var2);
         long var7 = 0L;
         long var9 = 0L;

         try {
            try {
               var7 = var6.request(1L, var5);
               if (var7 < 1L) {
                  throw new IOException("Resource limited: too many open file descriptors");
               }
            } catch (ResourceRequestDeniedException var19) {
               throw new IOException("Resource limited: too many open file descriptors", var19);
            }

            var9 = 1L;
         } finally {
            if (var9 == 0L) {
               try {
                  nd.close(var2);
               } catch (IOException var18) {
               }
            } else {
               var6.request(-(var7 - 1L), var5);
            }

         }
      }

      return var4;
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

   abstract class NativeDispatcher {
      abstract void close(FileDescriptor var1) throws IOException;
   }
}
