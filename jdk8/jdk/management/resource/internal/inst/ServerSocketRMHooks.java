package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.ServerSocket")
final class ServerSocketRMHooks {
   private Object closeLock;

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

   @InstrumentationMethod
   public InetAddress getInetAddress() {
      return this.getInetAddress();
   }

   @InstrumentationMethod
   public void bind(SocketAddress var1, int var2) throws IOException {
      ResourceIdImpl var3 = null;
      ResourceRequest var4 = null;
      long var5 = 0L;
      if (!this.isBound()) {
         var3 = ResourceIdImpl.of((Object)var1);
         var4 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
         var5 = var4.request(1L, var3);
         if (var5 < 1L) {
            throw new ResourceRequestDeniedException("Resource limited: too many open sockets");
         }
      }

      byte var7 = 0;

      try {
         this.bind(var1, var2);
         var7 = 1;
      } finally {
         if (var4 != null) {
            var4.request(-(var5 - (long)var7), var3);
         }

      }

   }

   @InstrumentationMethod
   public boolean isBound() {
      return this.isBound();
   }

   @InstrumentationMethod
   public boolean isClosed() {
      return this.isClosed();
   }

   @InstrumentationMethod
   public void close() throws IOException {
      synchronized(this.closeLock) {
         if (this.isClosed()) {
            return;
         }
      }

      boolean var1 = this.isBound();
      InetAddress var2 = this.getInetAddress();
      boolean var10 = false;

      try {
         var10 = true;
         this.close();
         var10 = false;
      } finally {
         if (var10) {
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
