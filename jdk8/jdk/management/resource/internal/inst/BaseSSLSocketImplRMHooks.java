package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.BaseSSLSocketImpl")
abstract class BaseSSLSocketImplRMHooks {
   @InstrumentationMethod
   boolean isLayered() {
      return this.isLayered();
   }

   @InstrumentationMethod
   public final InetAddress getLocalAddress() {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public final boolean isBound() {
      return this.isBound();
   }

   @InstrumentationMethod
   public synchronized void close() throws IOException {
      if (this.isLayered() && this.isBound()) {
         ResourceIdImpl var1 = ResourceIdImpl.of((Object)this.getLocalAddress());
         ResourceRequest var2 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
         var2.request(-1L, var1);
      }

      this.close();
   }
}
