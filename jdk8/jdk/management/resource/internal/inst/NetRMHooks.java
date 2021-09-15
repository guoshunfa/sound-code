package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.ProtocolFamily;
import jdk.Exported;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@Exported(false)
@InstrumentationTarget("sun.nio.ch.Net")
public class NetRMHooks {
   @InstrumentationMethod
   static FileDescriptor socket(ProtocolFamily var0, boolean var1) throws IOException {
      FileDescriptor var2 = socket(var0, var1);
      ResourceIdImpl var3 = ResourceIdImpl.of(var2);
      ResourceRequest var4 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var2);
      long var5 = 0L;
      long var7 = 0L;

      try {
         try {
            var5 = var4.request(1L, var3);
            if (var5 < 1L) {
               throw new IOException("Resource limited: too many open file descriptors");
            }
         } catch (ResourceRequestDeniedException var13) {
            throw new IOException("Resource limited: too many open file descriptors", var13);
         }

         var7 = 1L;
      } finally {
         var4.request(-(var5 - var7), var3);
      }

      return var2;
   }

   @InstrumentationMethod
   static FileDescriptor serverSocket(boolean var0) {
      FileDescriptor var1 = serverSocket(var0);
      ResourceIdImpl var2 = ResourceIdImpl.of(var1);
      ResourceRequest var3 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var1);
      long var4 = 0L;
      long var6 = 0L;

      try {
         var4 = var3.request(1L, var2);
         if (var4 < 1L) {
            throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors");
         }

         var6 = 1L;
      } finally {
         var3.request(-(var4 - var6), var2);
      }

      return var1;
   }
}
