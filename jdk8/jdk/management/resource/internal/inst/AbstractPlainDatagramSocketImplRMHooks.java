package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.net.SocketException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.net.AbstractPlainDatagramSocketImpl")
public class AbstractPlainDatagramSocketImplRMHooks {
   protected FileDescriptor fd;

   @InstrumentationMethod
   protected synchronized void create() throws SocketException {
      this.create();
      JavaIOFileDescriptorAccess var1 = SharedSecrets.getJavaIOFileDescriptorAccess();

      long var2;
      try {
         var2 = var1.getHandle(this.fd);
         if (var2 == -1L) {
            var2 = (long)var1.get(this.fd);
         }
      } catch (UnsupportedOperationException var10) {
         var2 = (long)var1.get(this.fd);
      }

      ResourceIdImpl var4 = ResourceIdImpl.of((Object)var2);
      ResourceRequest var5 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
      long var6 = 0L;

      try {
         var6 = var5.request(1L, var4);
         if (var6 < 1L) {
            throw new SocketException("Resource limited: too many open file descriptors");
         }
      } catch (ResourceRequestDeniedException var11) {
         var5.request(-var6, var4);
         SocketException var9 = new SocketException("Resource limited: too many open file descriptors");
         var9.initCause(var11);
         throw var9;
      }

      var5.request(-(var6 - 1L), var4);
   }

   @InstrumentationMethod
   protected void close() {
      if (this.fd != null) {
         JavaIOFileDescriptorAccess var1 = SharedSecrets.getJavaIOFileDescriptorAccess();

         long var2;
         try {
            var2 = var1.getHandle(this.fd);
            if (var2 == -1L) {
               var2 = (long)var1.get(this.fd);
            }
         } catch (UnsupportedOperationException var6) {
            var2 = (long)var1.get(this.fd);
         }

         if (var2 != -1L) {
            ResourceIdImpl var4 = ResourceIdImpl.of((Object)var2);
            ResourceRequest var5 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
            var5.request(-1L, var4);
         }
      }

      this.close();
   }
}
