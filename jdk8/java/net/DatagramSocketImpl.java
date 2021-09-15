package java.net;

import java.io.FileDescriptor;
import java.io.IOException;

public abstract class DatagramSocketImpl implements SocketOptions {
   protected int localPort;
   protected FileDescriptor fd;
   DatagramSocket socket;

   int dataAvailable() {
      return 0;
   }

   void setDatagramSocket(DatagramSocket var1) {
      this.socket = var1;
   }

   DatagramSocket getDatagramSocket() {
      return this.socket;
   }

   protected abstract void create() throws SocketException;

   protected abstract void bind(int var1, InetAddress var2) throws SocketException;

   protected abstract void send(DatagramPacket var1) throws IOException;

   protected void connect(InetAddress var1, int var2) throws SocketException {
   }

   protected void disconnect() {
   }

   protected abstract int peek(InetAddress var1) throws IOException;

   protected abstract int peekData(DatagramPacket var1) throws IOException;

   protected abstract void receive(DatagramPacket var1) throws IOException;

   /** @deprecated */
   @Deprecated
   protected abstract void setTTL(byte var1) throws IOException;

   /** @deprecated */
   @Deprecated
   protected abstract byte getTTL() throws IOException;

   protected abstract void setTimeToLive(int var1) throws IOException;

   protected abstract int getTimeToLive() throws IOException;

   protected abstract void join(InetAddress var1) throws IOException;

   protected abstract void leave(InetAddress var1) throws IOException;

   protected abstract void joinGroup(SocketAddress var1, NetworkInterface var2) throws IOException;

   protected abstract void leaveGroup(SocketAddress var1, NetworkInterface var2) throws IOException;

   protected abstract void close();

   protected int getLocalPort() {
      return this.localPort;
   }

   <T> void setOption(SocketOption<T> var1, T var2) throws IOException {
      if (var1 == StandardSocketOptions.SO_SNDBUF) {
         this.setOption(4097, var2);
      } else if (var1 == StandardSocketOptions.SO_RCVBUF) {
         this.setOption(4098, var2);
      } else if (var1 == StandardSocketOptions.SO_REUSEADDR) {
         this.setOption(4, var2);
      } else if (var1 == StandardSocketOptions.IP_TOS) {
         this.setOption(3, var2);
      } else if (var1 == StandardSocketOptions.IP_MULTICAST_IF && this.getDatagramSocket() instanceof MulticastSocket) {
         this.setOption(31, var2);
      } else if (var1 == StandardSocketOptions.IP_MULTICAST_TTL && this.getDatagramSocket() instanceof MulticastSocket) {
         if (!(var2 instanceof Integer)) {
            throw new IllegalArgumentException("not an integer");
         }

         this.setTimeToLive((Integer)var2);
      } else {
         if (var1 != StandardSocketOptions.IP_MULTICAST_LOOP || !(this.getDatagramSocket() instanceof MulticastSocket)) {
            throw new UnsupportedOperationException("unsupported option");
         }

         this.setOption(18, var2);
      }

   }

   <T> T getOption(SocketOption<T> var1) throws IOException {
      if (var1 == StandardSocketOptions.SO_SNDBUF) {
         return this.getOption(4097);
      } else if (var1 == StandardSocketOptions.SO_RCVBUF) {
         return this.getOption(4098);
      } else if (var1 == StandardSocketOptions.SO_REUSEADDR) {
         return this.getOption(4);
      } else if (var1 == StandardSocketOptions.IP_TOS) {
         return this.getOption(3);
      } else if (var1 == StandardSocketOptions.IP_MULTICAST_IF && this.getDatagramSocket() instanceof MulticastSocket) {
         return this.getOption(31);
      } else if (var1 == StandardSocketOptions.IP_MULTICAST_TTL && this.getDatagramSocket() instanceof MulticastSocket) {
         Integer var2 = this.getTimeToLive();
         return var2;
      } else if (var1 == StandardSocketOptions.IP_MULTICAST_LOOP && this.getDatagramSocket() instanceof MulticastSocket) {
         return this.getOption(18);
      } else {
         throw new UnsupportedOperationException("unsupported option");
      }
   }

   protected FileDescriptor getFileDescriptor() {
      return this.fd;
   }
}
