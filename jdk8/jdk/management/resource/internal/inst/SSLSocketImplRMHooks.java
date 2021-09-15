package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.SSLSocketImpl")
public final class SSLSocketImplRMHooks {
   public final boolean isBound() {
      return this.isBound();
   }

   boolean isLayered() {
      return this.isLayered();
   }

   public final InetAddress getLocalAddress() {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   void waitForClose(boolean var1) throws IOException {
      InetAddress var2 = null;
      if (this.isLayered()) {
         var2 = this.getLocalAddress();
      }

      if (this.isBound()) {
         ResourceIdImpl var3 = ResourceIdImpl.of((Object)var2);
         ResourceRequest var4 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
         var4.request(-1L, var3);
      }

      this.waitForClose(var1);
   }
}
