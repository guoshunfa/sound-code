package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.SocketInputStream")
@TypeMapping(
   from = "jdk.management.resource.internal.inst.SocketInputStreamRMHooks$AbstractPlainSocketImpl",
   to = "java.net.AbstractPlainSocketImpl"
)
public final class SocketInputStreamRMHooks {
   private SocketInputStreamRMHooks.AbstractPlainSocketImpl impl = null;

   @InstrumentationMethod
   private int socketRead(FileDescriptor var1, byte[] var2, int var3, int var4, int var5) throws IOException {
      if (var4 < 0) {
         return this.socketRead(var1, var2, var3, var4, var5);
      } else {
         ResourceIdImpl var6 = ResourceIdImpl.of((Object)this.impl.localport);
         ResourceRequest var7 = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
         long var8 = 0L;

         try {
            var8 = Math.max(var7.request((long)var4, var6), 0L);
         } catch (ResourceRequestDeniedException var16) {
            throw new IOException("Resource limited", var16);
         }

         var4 = Math.min(var4, (int)var8);
         int var10 = 0;

         int var11;
         try {
            var11 = this.socketRead(var1, var2, var3, var4, var5);
            var10 = Math.max(var11, 0);
         } finally {
            var7.request(-(var8 - (long)var10), var6);
         }

         return var11;
      }
   }

   static class AbstractPlainSocketImpl {
      protected int localport;
   }
}
