package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketOptions;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.net.Socket")
@TypeMapping(
   from = "jdk.management.resource.internal.inst.SocketRMHooks$SocketImpl",
   to = "java.net.SocketImpl"
)
public final class SocketRMHooks {
   private boolean created = false;
   SocketRMHooks.SocketImpl impl;

   public InetAddress getLocalAddress() {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public void bind(SocketAddress var1) throws IOException {
      ResourceIdImpl var2 = null;
      ResourceRequest var3 = null;
      long var4 = 0L;
      if (!this.isBound()) {
         var2 = ResourceIdImpl.of((Object)var1);
         var3 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);

         try {
            var4 = var3.request(1L, var2);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many open sockets");
            }
         } catch (ResourceRequestDeniedException var10) {
            throw new IOException("Resource limited: too many open sockets", var10);
         }
      }

      byte var6 = 0;

      try {
         this.bind(var1);
         var6 = 1;
      } finally {
         if (var3 != null) {
            var3.request(-(var4 - (long)var6), var2);
         }

      }

   }

   @InstrumentationMethod
   public boolean isBound() {
      return this.isBound();
   }

   @InstrumentationMethod
   public void connect(SocketAddress var1, int var2) throws IOException {
      ResourceIdImpl var3 = null;
      ResourceRequest var4 = null;
      long var5 = 0L;
      if (!this.isBound()) {
         var3 = ResourceIdImpl.of((Object)this.getLocalAddress());
         var4 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);

         try {
            var5 = var4.request(1L, var3);
            if (var5 < 1L) {
               throw new IOException("Resource limited: too many open sockets");
            }
         } catch (ResourceRequestDeniedException var11) {
            throw new IOException("Resource limited: too many open sockets", var11);
         }
      }

      byte var7 = 0;

      try {
         this.connect(var1, var2);
         var7 = 1;
      } finally {
         if (var4 != null) {
            var4.request(-(var5 - (long)var7), var3);
         }

      }

   }

   @InstrumentationMethod
   final void postAccept() {
      this.postAccept();
      FileDescriptor var1 = this.impl.getFileDescriptor();
      JavaIOFileDescriptorAccess var2 = SharedSecrets.getJavaIOFileDescriptorAccess();

      long var3;
      try {
         var3 = var2.getHandle(var1);
         if (var3 == -1L) {
            var3 = (long)var2.get(var1);
         }
      } catch (UnsupportedOperationException var18) {
         var3 = (long)var2.get(var1);
      }

      ResourceIdImpl var5 = ResourceIdImpl.of((Object)var3);
      ResourceRequest var6 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var1);
      long var7 = 0L;
      boolean var9 = false;

      try {
         var7 = var6.request(1L, var5);
         if (var7 < 1L) {
            throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors");
         }

         var9 = true;
      } finally {
         if (!var9) {
            try {
               this.close();
            } catch (IOException var17) {
            }

            var6.request(-Math.max(0L, var7 - 1L), var5);
         }

      }

   }

   @InstrumentationMethod
   public boolean isClosed() {
      return this.isClosed();
   }

   @InstrumentationMethod
   public synchronized void close() throws IOException {
      if (!this.isClosed()) {
         boolean var1 = this.isBound();
         InetAddress var2 = this.getLocalAddress();
         boolean var9 = false;

         try {
            var9 = true;
            this.close();
            var9 = false;
         } finally {
            if (var9) {
               if (var1) {
                  ResourceIdImpl var6 = ResourceIdImpl.of((Object)var2);
                  ResourceRequest var7 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
                  var7.request(-1L, var6);
               }

            }
         }

         if (var1) {
            ResourceIdImpl var3 = ResourceIdImpl.of((Object)var2);
            ResourceRequest var4 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
            var4.request(-1L, var3);
         }

      }
   }

   abstract class SocketImpl implements SocketOptions {
      protected FileDescriptor fd;

      protected FileDescriptor getFileDescriptor() {
         return this.fd;
      }
   }
}
