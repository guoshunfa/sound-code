package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.DatagramChannelImpl")
public final class DatagramChannelImplRMHooks {
   @InstrumentationMethod
   public SocketAddress getLocalAddress() throws IOException {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public boolean isConnected() {
      return this.isConnected();
   }

   @InstrumentationMethod
   public DatagramChannel bind(SocketAddress var1) throws IOException {
      ResourceIdImpl var2 = null;
      ResourceRequest var3 = null;
      long var4 = 0L;
      if (this.getLocalAddress() == null) {
         var2 = ResourceIdImpl.of((Object)var1);
         var3 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);

         try {
            var4 = var3.request(1L, var2);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many open datagram channels");
            }
         } catch (ResourceRequestDeniedException var11) {
            throw new IOException("Resource limited: too many open datagram channels", var11);
         }
      }

      byte var6 = 0;

      DatagramChannel var7;
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
   public DatagramChannel connect(SocketAddress var1) throws IOException {
      ResourceIdImpl var2 = null;
      ResourceRequest var3 = null;
      long var4 = 0L;
      if (this.getLocalAddress() == null) {
         var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
         var3 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);

         try {
            var4 = var3.request(1L, var2);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many open datagram channels");
            }
         } catch (ResourceRequestDeniedException var11) {
            throw new IOException("Resource limited: too many open datagram channels", var11);
         }
      }

      byte var6 = 0;

      DatagramChannel var7;
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
   public SocketAddress receive(ByteBuffer var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
      ResourceRequest var3 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
      long var4 = 0L;

      try {
         var4 = Math.max(var3.request(1L, var2), 0L);
         if (var4 < 1L) {
            throw new IOException("Resource limited: too many received datagrams");
         }
      } catch (ResourceRequestDeniedException var17) {
         throw new IOException("Resource limited: too many received datagrams", var17);
      }

      var3.request(-(var4 - 1L), var2);
      int var6 = var1.remaining();
      ResourceRequest var7 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);

      try {
         var4 = Math.max(var7.request((long)var6, var2), 0L);
         if (var4 < (long)var6) {
            var3.request(-1L, var2);
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var16) {
         var3.request(-1L, var2);
         throw new IOException("Resource limited: insufficient bytes approved", var16);
      }

      int var8 = 0;
      SocketAddress var9 = null;

      try {
         int var10 = var1.position();
         var9 = this.receive(var1);
         var8 = var1.position() - var10;
      } finally {
         if (var9 == null) {
            var3.request(-1L, var2);
         }

         var7.request(-(var4 - (long)var8), var2);
      }

      return var9;
   }

   @InstrumentationMethod
   public int send(ByteBuffer var1, SocketAddress var2) throws IOException {
      ResourceIdImpl var3 = ResourceIdImpl.of((Object)this.getLocalAddress());
      long var4 = 0L;
      if (this.getLocalAddress() == null) {
         ResourceRequest var6 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);

         try {
            var4 = var6.request(1L, var3);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many open datagram channels");
            }
         } catch (ResourceRequestDeniedException var19) {
            throw new IOException("Resource limited: too many open datagram channels", var19);
         }

         var6.request(-(var4 - 1L), var3);
      }

      int var20;
      if (this.isConnected()) {
         var20 = this.send(var1, var2);
      } else {
         ResourceRequest var7 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
         var4 = 0L;

         try {
            var4 = Math.max(var7.request(1L, var3), 0L);
            if (var4 < 1L) {
               throw new IOException("Resource limited: too many sent datagrams");
            }
         } catch (ResourceRequestDeniedException var18) {
            throw new IOException("Resource limited: too many sent datagrams", var18);
         }

         var7.request(-(var4 - 1L), var3);
         int var8 = var1.remaining();
         ResourceRequest var9 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);

         try {
            var4 = Math.max(var9.request((long)var8, var3), 0L);
            if (var4 < (long)var8) {
               var7.request(-1L, var3);
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var17) {
            var7.request(-1L, var3);
            throw new IOException("Resource limited: insufficient bytes approved", var17);
         }

         var20 = 0;

         try {
            var20 = this.send(var1, var2);
         } finally {
            if (var20 == 0) {
               var7.request(-1L, var3);
            }

            var9.request(-(var4 - (long)var20), var3);
         }
      }

      return var20;
   }

   @InstrumentationMethod
   public int read(ByteBuffer var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
      ResourceRequest var3 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
      long var4 = 0L;

      try {
         var4 = Math.max(var3.request(1L, var2), 0L);
         if (var4 < 1L) {
            throw new IOException("Resource limited: too many received datagrams");
         }
      } catch (ResourceRequestDeniedException var16) {
         throw new IOException("Resource limited: too many received datagrams", var16);
      }

      var3.request(-(var4 - 1L), var2);
      ResourceRequest var6 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
      var4 = 0L;
      int var7 = var1.remaining();

      try {
         var4 = Math.max(var6.request((long)var7, var2), 0L);
         if (var4 < (long)var7) {
            var3.request(-1L, var2);
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var15) {
         var3.request(-1L, var2);
         throw new IOException("Resource limited: insufficient bytes approved", var15);
      }

      int var8 = 0;
      boolean var9 = false;

      int var17;
      try {
         var17 = this.read(var1);
         var8 = Math.max(var17, 0);
      } finally {
         var6.request(-(var4 - (long)var8), var2);
         if (var8 == 0) {
            var3.request(-1L, var2);
         }

      }

      return var17;
   }

   @InstrumentationMethod
   public long read(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.getLocalAddress());
         ResourceRequest var5 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
         long var6 = 0L;

         try {
            var6 = Math.max(var5.request(1L, var4), 0L);
            if (var6 < 1L) {
               throw new IOException("Resource limited: too many received datagrams");
            }
         } catch (ResourceRequestDeniedException var20) {
            throw new IOException("Resource limited: too many received datagrams", var20);
         }

         var5.request(-(var6 - 1L), var4);
         ResourceRequest var8 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
         var6 = 0L;
         int var9 = 0;

         for(int var10 = var2; var10 < var2 + var3; ++var10) {
            var9 += var1[var10].remaining();
         }

         try {
            var6 = Math.max(var8.request((long)var9, var4), 0L);
            if (var6 < (long)var9) {
               var5.request(-1L, var4);
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var19) {
            var5.request(-1L, var4);
            throw new IOException("Resource limited: insufficient bytes approved", var19);
         }

         long var21 = 0L;
         long var12 = 0L;

         try {
            var12 = this.read(var1, var2, var3);
            var21 = Math.max(var12, 0L);
         } finally {
            var8.request(-(var6 - var21), var4);
            if (var21 == 0L) {
               var5.request(-1L, var4);
            }

         }

         return var12;
      } else {
         return this.read(var1, var2, var3);
      }
   }

   @InstrumentationMethod
   public int write(ByteBuffer var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
      ResourceRequest var3 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
      long var4 = 0L;

      try {
         var4 = Math.max(var3.request(1L, var2), 0L);
         if (var4 < 1L) {
            throw new IOException("Resource limited: too many sent datagrams");
         }
      } catch (ResourceRequestDeniedException var15) {
         throw new IOException("Resource limited: too many sent datagrams", var15);
      }

      var3.request(-(var4 - 1L), var2);
      ResourceRequest var6 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
      var4 = 0L;
      int var7 = var1.remaining();

      try {
         var4 = Math.max(var6.request((long)var7, var2), 0L);
         if (var4 < (long)var7) {
            var3.request(-1L, var2);
            throw new IOException("Resource limited: insufficient bytes approved");
         }
      } catch (ResourceRequestDeniedException var14) {
         var3.request(-1L, var2);
         throw new IOException("Resource limited: insufficient bytes approved", var14);
      }

      int var8 = 0;

      try {
         var8 = this.write(var1);
      } finally {
         var6.request(-(var4 - (long)var8), var2);
         if (var8 == 0) {
            var3.request(-1L, var2);
         }

      }

      return var8;
   }

   @InstrumentationMethod
   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         ResourceIdImpl var4 = ResourceIdImpl.of((Object)this.getLocalAddress());
         ResourceRequest var5 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
         long var6 = 0L;

         try {
            var6 = Math.max(var5.request(1L, var4), 0L);
            if (var6 < 1L) {
               throw new IOException("Resource limited: too many sent datagrams");
            }
         } catch (ResourceRequestDeniedException var18) {
            throw new IOException("Resource limited: too many sent datagrams", var18);
         }

         var5.request(-(var6 - 1L), var4);
         ResourceRequest var8 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
         var6 = 0L;
         int var9 = 0;

         for(int var10 = var2; var10 < var2 + var3; ++var10) {
            var9 += var1[var10].remaining();
         }

         try {
            var6 = Math.max(var8.request((long)var9, var4), 0L);
            if (var6 < (long)var9) {
               var5.request(-1L, var4);
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var17) {
            var5.request(-1L, var4);
            throw new IOException("Resource limited: insufficient bytes approved", var17);
         }

         long var19 = 0L;

         try {
            var19 = this.write(var1, var2, var3);
         } finally {
            var8.request(-(var6 - var19), var4);
            if (var19 == 0L) {
               var5.request(-1L, var4);
            }

         }

         return var19;
      } else {
         return this.write(var1, var2, var3);
      }
   }
}
