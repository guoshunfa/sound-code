package jdk.management.resource.internal.inst;

import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.SocketOutputStream")
@TypeMapping(
   from = "jdk.management.resource.internal.inst.SocketOutputStreamRMHooks$AbstractPlainSocketImpl",
   to = "java.net.AbstractPlainSocketImpl"
)
public final class SocketOutputStreamRMHooks {
   private SocketOutputStreamRMHooks.AbstractPlainSocketImpl impl = null;

   @InstrumentationMethod
   private void socketWrite(byte[] var1, int var2, int var3) throws IOException {
      if (var3 < 0) {
         this.socketWrite(var1, var2, var3);
      } else {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.impl.localport);
         ResourceRequest var5 = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
         long var6 = 0L;

         try {
            var6 = var5.request((long)var3, var4);
            if (var6 < (long)var3) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var13) {
            throw new IOException("Resource limited", var13);
         }

         int var8 = 0;

         try {
            this.socketWrite(var1, var2, var3);
            var8 = var3;
         } finally {
            var5.request(-(var6 - (long)var8), var4);
         }

      }
   }

   static class AbstractPlainSocketImpl {
      protected int localport;
   }
}
