package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.ServerSocketChannelImpl")
public final class ServerSocketChannelImplRMHooks {
   private static ServerSocketChannelImplRMHooks.NativeDispatcher nd;

   @InstrumentationMethod
   public SocketAddress getLocalAddress() throws IOException {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public SocketChannel accept() throws IOException {
      long var1 = 0L;
      long var3 = 0L;
      SocketChannel var5 = null;
      ResourceIdImpl var6 = null;
      ResourceRequest var7 = null;

      try {
         var5 = this.accept();
         if (var5 != null) {
            var7 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(var5);
            var6 = ResourceIdImpl.of((Object)this.getLocalAddress());

            try {
               var1 = var7.request(1L, var6);
               if (var1 < 1L) {
                  try {
                     var5.close();
                  } catch (IOException var16) {
                  }

                  throw new IOException("Resource limited: too many open socket channels");
               }
            } catch (ResourceRequestDeniedException var17) {
               try {
                  var5.close();
               } catch (IOException var15) {
               }

               throw new IOException("Resource limited: too many open socket channels", var17);
            }

            var3 = 1L;
         }
      } finally {
         if (var7 != null) {
            var7.request(-(var1 - var3), var6);
         }

      }

      return var5;
   }

   public final void close() throws IOException {
   }

   @InstrumentationMethod
   private int accept(FileDescriptor var1, FileDescriptor var2, InetSocketAddress[] var3) throws IOException {
      int var4 = this.accept(var1, var2, var3);
      ResourceIdImpl var5 = ResourceIdImpl.of(var2);
      if (var5 != null) {
         ResourceRequest var6 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(var2);
         long var7 = 0L;
         long var9 = 0L;

         try {
            try {
               var7 = var6.request(1L, var5);
               if (var7 < 1L) {
                  throw new IOException("Resource limited: too many open file descriptors");
               }
            } catch (ResourceRequestDeniedException var19) {
               throw new IOException("Resource limited: too many open file descriptors", var19);
            }

            var9 = 1L;
         } finally {
            if (var9 == 0L) {
               try {
                  nd.close(var2);
               } catch (IOException var18) {
               }
            } else {
               var6.request(-(var7 - 1L), var5);
            }

         }
      }

      return var4;
   }

   @InstrumentationMethod
   public ServerSocketChannel bind(SocketAddress var1, int var2) throws IOException {
      ResourceIdImpl var3 = null;
      ResourceRequest var4 = null;
      long var5 = 0L;
      if (this.getLocalAddress() == null) {
         var3 = ResourceIdImpl.of((Object)var1);
         var4 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
         var5 = var4.request(1L, var3);
         if (var5 < 1L) {
            throw new ResourceRequestDeniedException("Resource limited: too many open socket channels");
         }
      }

      byte var7 = 0;
      ServerSocketChannel var8 = null;

      try {
         var8 = this.bind(var1, var2);
         var7 = 1;
      } finally {
         if (var4 != null) {
            var4.request(-(var5 - (long)var7), var3);
         }

      }

      return var8;
   }

   abstract class NativeDispatcher {
      abstract void close(FileDescriptor var1) throws IOException;
   }
}
