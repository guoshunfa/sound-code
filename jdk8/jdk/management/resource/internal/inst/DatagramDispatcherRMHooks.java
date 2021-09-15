package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.Exported;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@Exported(false)
@InstrumentationTarget("sun.nio.ch.DatagramDispatcher")
public class DatagramDispatcherRMHooks {
   @InstrumentationMethod
   void close(FileDescriptor var1) throws IOException {
      long var2 = 0L;
      ResourceIdImpl var4 = ResourceIdImpl.of(var1);
      boolean var9 = false;

      try {
         var9 = true;
         this.close(var1);
         var2 = 1L;
         var9 = false;
      } finally {
         if (var9) {
            ResourceRequest var7 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var1);
            var7.request(-var2, var4);
         }
      }

      ResourceRequest var5 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var1);
      var5.request(-var2, var4);
   }
}
