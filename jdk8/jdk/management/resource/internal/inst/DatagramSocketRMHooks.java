package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.DatagramSocket")
public final class DatagramSocketRMHooks {
   @InstrumentationMethod
   public InetAddress getLocalAddress() {
      return this.getLocalAddress();
   }

   @InstrumentationMethod
   public boolean isBound() {
      return this.isBound();
   }

   @InstrumentationMethod
   public synchronized void bind(SocketAddress var1) throws SocketException {
      ResourceIdImpl var2 = null;
      ResourceRequest var3 = null;
      long var4 = 0L;
      if (!this.isBound()) {
         var2 = ResourceIdImpl.of((Object)var1);
         var3 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);

         try {
            var4 = var3.request(1L, var2);
            if (var4 < 1L) {
               throw new SocketException("Resource limited: too many open datagram sockets");
            }
         } catch (ResourceRequestDeniedException var11) {
            SocketException var7 = new SocketException("Resource limited: too many open datagram sockets");
            var7.initCause(var11);
            throw var7;
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
   private synchronized void connectInternal(InetAddress var1, int var2) throws SocketException {
      ResourceIdImpl var3 = null;
      ResourceRequest var4 = null;
      long var5 = 0L;
      if (!this.isBound()) {
         var3 = ResourceIdImpl.of((Object)this.getLocalAddress());
         var4 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);

         try {
            var5 = var4.request(1L, var3);
            if (var5 < 1L) {
               throw new SocketException("Resource limited: too many open datagram sockets");
            }
         } catch (ResourceRequestDeniedException var12) {
            SocketException var8 = new SocketException("Resource limited: too many open datagram sockets");
            var8.initCause(var12);
            throw var8;
         }
      }

      byte var7 = 0;

      try {
         this.connectInternal(var1, var2);
         var7 = 1;
      } finally {
         if (var4 != null) {
            var4.request(-(var5 - (long)var7), var3);
         }

      }

   }

   @InstrumentationMethod
   public synchronized void receive(DatagramPacket var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
      ResourceRequest var3 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
      long var4 = 0L;

      try {
         var4 = Math.max(var3.request(1L, var2), 0L);
         if (var4 < 1L) {
            throw new IOException("Resource limited: too many received datagrams");
         }
      } catch (ResourceRequestDeniedException var15) {
         throw new IOException("Resource limited: too many received datagrams", var15);
      }

      int var6 = Math.max(var1.getLength(), 0);
      if (var6 > 0) {
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
         byte var9 = 0;

         try {
            this.receive(var1);
            var8 = var1.getLength();
            var9 = 1;
         } finally {
            var7.request(-(var4 - (long)var8), var2);
            var3.request((long)(-(1 - var9)), var2);
         }
      }

   }

   @InstrumentationMethod
   public void send(DatagramPacket var1) throws IOException {
      ResourceIdImpl var2 = ResourceIdImpl.of((Object)this.getLocalAddress());
      ResourceRequest var3 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
      long var4 = 0L;

      try {
         var4 = Math.max(var3.request(1L, var2), 0L);
         if (var4 < 1L) {
            throw new IOException("Resource limited: too many sent datagrams");
         }
      } catch (ResourceRequestDeniedException var14) {
         throw new IOException("Resource limited: too many sent datagrams", var14);
      }

      int var6 = Math.max(var1.getLength(), 0);
      if (var6 > 0) {
         ResourceRequest var7 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);

         try {
            var4 = Math.max(var7.request((long)var6, var2), 0L);
            if (var4 < (long)var6) {
               var3.request(-1L, var2);
               throw new IOException("Resource limited: insufficient bytes approved");
            }
         } catch (ResourceRequestDeniedException var15) {
            var3.request(-1L, var2);
            throw new IOException("Resource limited: too many sent datagrams", var15);
         }

         int var8 = 0;

         try {
            this.send(var1);
            var8 = var1.getLength();
         } finally {
            var7.request(-(var4 - (long)var8), var2);
         }
      }

   }

   @InstrumentationMethod
   public boolean isClosed() {
      return this.isClosed();
   }

   @InstrumentationMethod
   public boolean isConnected() {
      return this.isConnected();
   }

   @InstrumentationMethod
   public void close() {
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
                  ResourceRequest var7 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
                  var7.request(-1L, var6);
               }

            }
         }

         if (var1) {
            ResourceIdImpl var3 = ResourceIdImpl.of((Object)var2);
            ResourceRequest var4 = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
            var4.request(-1L, var3);
         }

      }
   }
}
