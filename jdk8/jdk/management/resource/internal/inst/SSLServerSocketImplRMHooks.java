package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.Socket;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.SSLServerSocketImpl")
final class SSLServerSocketImplRMHooks {
   @InstrumentationMethod
   public Socket accept() throws IOException {
      long var1 = 0L;
      long var3 = 0L;
      Socket var5 = null;
      ResourceIdImpl var6 = null;
      ResourceRequest var7 = null;

      try {
         var5 = this.accept();
         var3 = 1L;
         var7 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(var5);
         var6 = ResourceIdImpl.of((Object)var5.getLocalAddress());

         try {
            var1 = var7.request(1L, var6);
            if (var1 < 1L) {
               try {
                  var5.close();
               } catch (IOException var16) {
               }

               throw new IOException("Resource limited: too many open sockets");
            }
         } catch (ResourceRequestDeniedException var17) {
            try {
               var5.close();
            } catch (IOException var15) {
            }

            throw new IOException("Resource limited: too many open sockets", var17);
         }

         var3 = 1L;
      } finally {
         if (var7 != null) {
            var7.request(-(var1 - var3), var6);
         }

      }

      return var5;
   }
}
