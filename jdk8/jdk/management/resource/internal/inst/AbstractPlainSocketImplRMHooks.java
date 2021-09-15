package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.AbstractPlainSocketImpl")
abstract class AbstractPlainSocketImplRMHooks {
   protected FileDescriptor fd;

   abstract void socketClose0(boolean var1);

   @InstrumentationMethod
   protected synchronized void create(boolean var1) throws IOException {
      this.create(var1);
      ResourceIdImpl var2 = ResourceIdImpl.of(this.fd);
      ResourceRequest var3 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
      long var4 = 0L;

      try {
         var4 = var3.request(1L, var2);
         if (var4 < 1L) {
            this.socketClose0(false);
            throw new IOException("Resource limited: too many open file descriptors");
         }
      } catch (ResourceRequestDeniedException var7) {
         var3.request(-var4, var2);
         this.socketClose0(false);
         throw new IOException("Resource limited: too many open file descriptors", var7);
      }

      var3.request(-(var4 - 1L), var2);
   }

   @InstrumentationMethod
   protected void close() throws IOException {
      ResourceIdImpl var1 = null;
      ResourceRequest var2 = null;
      boolean var3 = true;
      if (this.fd != null) {
         var1 = ResourceIdImpl.of(this.fd);
         var2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
      }

      try {
         this.close();
      } finally {
         if (var2 != null) {
            var2.request(-1L, var1);
         }

      }

   }
}
