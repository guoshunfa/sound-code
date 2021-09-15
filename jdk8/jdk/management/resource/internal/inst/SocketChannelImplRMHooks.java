package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.SocketChannelImpl")
public final class SocketChannelImplRMHooks {
   @InstrumentationMethod
   public SocketAddress getLocalAddress() throws IOException {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public SocketChannel bind(SocketAddress var1) throws IOException {
      ResourceIdImpl var2 = null;
      ResourceRequest var3 = null;
      long var4 = 0L;
      if (this.getLocalAddress() == null) {
         var2 = ResourceIdImpl.of((Object)var1);
         var3 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);

         try {
            var4 = var3.request(1L, var2);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many open socket channels");
            }
         } catch (ResourceRequestDeniedException var11) {
            throw new IOException("Resource limited: too many open socket channels", var11);
         }
      }

      byte var6 = 0;
      SocketChannel var7 = null;

      try {
         var7 = this.bind(var1);
         var6 = 1;
      } finally {
         if (var3 != null) {
            var3.request(-(var4 - (long)var6), var2);
         }

      }

      return var7;
   }

   @InstrumentationMethod
   public boolean connect(SocketAddress var1) throws IOException {
      ResourceIdImpl var2 = null;
      ResourceRequest var3 = null;
      long var4 = 0L;
      if (this.getLocalAddress() == null) {
         var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
         var3 = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);

         try {
            var4 = var3.request(1L, var2);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many open sockets");
            }
         } catch (ResourceRequestDeniedException var11) {
            throw new IOException("Resource limited: too many open sockets", var11);
         }
      }

      byte var6 = 0;
      boolean var7 = false;

      try {
         var7 = this.connect(var1);
         var6 = 1;
      } finally {
         if (var3 != null) {
            var3.request(-(var4 - (long)var6), var2);
         }

      }

      return var7;
   }

   @InstrumentationMethod
   public int read(ByteBuffer var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
      ResourceRequest var3 = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
      long var4 = 0L;
      int var6 = var1.remaining();

      try {
         var4 = Math.max(var3.request((long)var6, var2), 0L);
         if (var4 < (long)var6) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var13) {
         throw new IOException("Resource limited", var13);
      }

      int var7 = 0;
      boolean var8 = false;

      int var14;
      try {
         var14 = this.read(var1);
         var7 = Math.max(var14, 0);
      } finally {
         var3.request(-(var4 - (long)var7), var2);
      }

      return var14;
   }

   @InstrumentationMethod
   public long read(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.getLocalAddress());
         ResourceRequest var5 = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
         long var6 = 0L;
         int var8 = 0;

         for(int var9 = var2; var9 < var2 + var3; ++var9) {
            var8 += var1[var9].remaining();
         }

         try {
            var6 = Math.max(var5.request((long)var8, var4), 0L);
            if (var6 < (long)var8) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var17) {
            throw new IOException("Resource limited", var17);
         }

         long var18 = 0L;
         long var11 = 0L;

         try {
            var11 = this.read(var1, var2, var3);
            var18 = Math.max(var11, 0L);
         } finally {
            var5.request(-(var6 - var18), var4);
         }

         return var11;
      } else {
         return this.read(var1, var2, var3);
      }
   }

   @InstrumentationMethod
   public int write(ByteBuffer var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
      ResourceRequest var3 = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
      long var4 = 0L;
      int var6 = var1.remaining();

      try {
         var4 = Math.max(var3.request((long)var6, var2), 0L);
         if (var4 < (long)var6) {
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var12) {
         throw new IOException("Resource limited", var12);
      }

      int var7 = 0;

      try {
         var7 = this.write(var1);
      } finally {
         var3.request(-(var4 - (long)var7), var2);
      }

      return var7;
   }

   @InstrumentationMethod
   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.getLocalAddress());
         ResourceRequest var5 = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
         long var6 = 0L;
         int var8 = 0;

         for(int var9 = var2; var9 < var2 + var3; ++var9) {
            var8 += var1[var9].remaining();
         }

         try {
            var6 = Math.max(var5.request((long)var8, var4), 0L);
            if (var6 < (long)var8) {
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var15) {
            throw new IOException("Resource limited", var15);
         }

         long var16 = 0L;

         try {
            var16 = this.write(var1, var2, var3);
         } finally {
            var5.request(-(var6 - var16), var4);
         }

         return var16;
      } else {
         return this.write(var1, var2, var3);
      }
   }
}
