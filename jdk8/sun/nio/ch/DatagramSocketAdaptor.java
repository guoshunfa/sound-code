package sun.nio.ch;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;

public class DatagramSocketAdaptor extends DatagramSocket {
   private final DatagramChannelImpl dc;
   private volatile int timeout = 0;
   private static final DatagramSocketImpl dummyDatagramSocket = new DatagramSocketImpl() {
      protected void create() throws SocketException {
      }

      protected void bind(int var1, InetAddress var2) throws SocketException {
      }

      protected void send(DatagramPacket var1) throws IOException {
      }

      protected int peek(InetAddress var1) throws IOException {
         return 0;
      }

      protected int peekData(DatagramPacket var1) throws IOException {
         return 0;
      }

      protected void receive(DatagramPacket var1) throws IOException {
      }

      /** @deprecated */
      @Deprecated
      protected void setTTL(byte var1) throws IOException {
      }

      /** @deprecated */
      @Deprecated
      protected byte getTTL() throws IOException {
         return 0;
      }

      protected void setTimeToLive(int var1) throws IOException {
      }

      protected int getTimeToLive() throws IOException {
         return 0;
      }

      protected void join(InetAddress var1) throws IOException {
      }

      protected void leave(InetAddress var1) throws IOException {
      }

      protected void joinGroup(SocketAddress var1, NetworkInterface var2) throws IOException {
      }

      protected void leaveGroup(SocketAddress var1, NetworkInterface var2) throws IOException {
      }

      protected void close() {
      }

      public Object getOption(int var1) throws SocketException {
         return null;
      }

      public void setOption(int var1, Object var2) throws SocketException {
      }
   };

   private DatagramSocketAdaptor(DatagramChannelImpl var1) throws IOException {
      super(dummyDatagramSocket);
      this.dc = var1;
   }

   public static DatagramSocket create(DatagramChannelImpl var0) {
      try {
         return new DatagramSocketAdaptor(var0);
      } catch (IOException var2) {
         throw new Error(var2);
      }
   }

   private void connectInternal(SocketAddress var1) throws SocketException {
      InetSocketAddress var2 = Net.asInetSocketAddress(var1);
      int var3 = var2.getPort();
      if (var3 >= 0 && var3 <= 65535) {
         if (var1 == null) {
            throw new IllegalArgumentException("connect: null address");
         } else if (!this.isClosed()) {
            try {
               this.dc.connect(var1);
            } catch (Exception var5) {
               Net.translateToSocketException(var5);
            }

         }
      } else {
         throw new IllegalArgumentException("connect: " + var3);
      }
   }

   public void bind(SocketAddress var1) throws SocketException {
      try {
         if (var1 == null) {
            var1 = new InetSocketAddress(0);
         }

         this.dc.bind((SocketAddress)var1);
      } catch (Exception var3) {
         Net.translateToSocketException(var3);
      }

   }

   public void connect(InetAddress var1, int var2) {
      try {
         this.connectInternal(new InetSocketAddress(var1, var2));
      } catch (SocketException var4) {
      }

   }

   public void connect(SocketAddress var1) throws SocketException {
      if (var1 == null) {
         throw new IllegalArgumentException("Address can't be null");
      } else {
         this.connectInternal(var1);
      }
   }

   public void disconnect() {
      try {
         this.dc.disconnect();
      } catch (IOException var2) {
         throw new Error(var2);
      }
   }

   public boolean isBound() {
      return this.dc.localAddress() != null;
   }

   public boolean isConnected() {
      return this.dc.remoteAddress() != null;
   }

   public InetAddress getInetAddress() {
      return this.isConnected() ? Net.asInetSocketAddress(this.dc.remoteAddress()).getAddress() : null;
   }

   public int getPort() {
      return this.isConnected() ? Net.asInetSocketAddress(this.dc.remoteAddress()).getPort() : -1;
   }

   public void send(DatagramPacket var1) throws IOException {
      synchronized(this.dc.blockingLock()) {
         if (!this.dc.isBlocking()) {
            throw new IllegalBlockingModeException();
         } else {
            try {
               synchronized(var1) {
                  ByteBuffer var4 = ByteBuffer.wrap(var1.getData(), var1.getOffset(), var1.getLength());
                  if (this.dc.isConnected()) {
                     if (var1.getAddress() == null) {
                        InetSocketAddress var5 = (InetSocketAddress)this.dc.remoteAddress();
                        var1.setPort(var5.getPort());
                        var1.setAddress(var5.getAddress());
                        this.dc.write(var4);
                     } else {
                        this.dc.send(var4, var1.getSocketAddress());
                     }
                  } else {
                     this.dc.send(var4, var1.getSocketAddress());
                  }
               }
            } catch (IOException var9) {
               Net.translateException(var9);
            }

         }
      }
   }

   private SocketAddress receive(ByteBuffer var1) throws IOException {
      if (this.timeout == 0) {
         return this.dc.receive(var1);
      } else {
         this.dc.configureBlocking(false);

         try {
            SocketAddress var3;
            if ((var3 = this.dc.receive(var1)) != null) {
               SocketAddress var13 = var3;
               return var13;
            } else {
               long var4 = (long)this.timeout;

               do {
                  if (!this.dc.isOpen()) {
                     throw new ClosedChannelException();
                  }

                  long var6 = System.currentTimeMillis();
                  int var8 = this.dc.poll(Net.POLLIN, var4);
                  if (var8 > 0 && (var8 & Net.POLLIN) != 0 && (var3 = this.dc.receive(var1)) != null) {
                     SocketAddress var9 = var3;
                     return var9;
                  }

                  var4 -= System.currentTimeMillis() - var6;
               } while(var4 > 0L);

               throw new SocketTimeoutException();
            }
         } finally {
            if (this.dc.isOpen()) {
               this.dc.configureBlocking(true);
            }

         }
      }
   }

   public void receive(DatagramPacket var1) throws IOException {
      synchronized(this.dc.blockingLock()) {
         if (!this.dc.isBlocking()) {
            throw new IllegalBlockingModeException();
         } else {
            try {
               synchronized(var1) {
                  ByteBuffer var4 = ByteBuffer.wrap(var1.getData(), var1.getOffset(), var1.getLength());
                  SocketAddress var5 = this.receive(var4);
                  var1.setSocketAddress(var5);
                  var1.setLength(var4.position() - var1.getOffset());
               }
            } catch (IOException var9) {
               Net.translateException(var9);
            }

         }
      }
   }

   public InetAddress getLocalAddress() {
      if (this.isClosed()) {
         return null;
      } else {
         Object var1 = this.dc.localAddress();
         if (var1 == null) {
            var1 = new InetSocketAddress(0);
         }

         InetAddress var2 = ((InetSocketAddress)var1).getAddress();
         SecurityManager var3 = System.getSecurityManager();
         if (var3 != null) {
            try {
               var3.checkConnect(var2.getHostAddress(), -1);
            } catch (SecurityException var5) {
               return (new InetSocketAddress(0)).getAddress();
            }
         }

         return var2;
      }
   }

   public int getLocalPort() {
      if (this.isClosed()) {
         return -1;
      } else {
         try {
            SocketAddress var1 = this.dc.getLocalAddress();
            if (var1 != null) {
               return ((InetSocketAddress)var1).getPort();
            }
         } catch (Exception var2) {
         }

         return 0;
      }
   }

   public void setSoTimeout(int var1) throws SocketException {
      this.timeout = var1;
   }

   public int getSoTimeout() throws SocketException {
      return this.timeout;
   }

   private void setBooleanOption(SocketOption<Boolean> var1, boolean var2) throws SocketException {
      try {
         this.dc.setOption(var1, var2);
      } catch (IOException var4) {
         Net.translateToSocketException(var4);
      }

   }

   private void setIntOption(SocketOption<Integer> var1, int var2) throws SocketException {
      try {
         this.dc.setOption(var1, var2);
      } catch (IOException var4) {
         Net.translateToSocketException(var4);
      }

   }

   private boolean getBooleanOption(SocketOption<Boolean> var1) throws SocketException {
      try {
         return (Boolean)this.dc.getOption(var1);
      } catch (IOException var3) {
         Net.translateToSocketException(var3);
         return false;
      }
   }

   private int getIntOption(SocketOption<Integer> var1) throws SocketException {
      try {
         return (Integer)this.dc.getOption(var1);
      } catch (IOException var3) {
         Net.translateToSocketException(var3);
         return -1;
      }
   }

   public void setSendBufferSize(int var1) throws SocketException {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Invalid send size");
      } else {
         this.setIntOption(StandardSocketOptions.SO_SNDBUF, var1);
      }
   }

   public int getSendBufferSize() throws SocketException {
      return this.getIntOption(StandardSocketOptions.SO_SNDBUF);
   }

   public void setReceiveBufferSize(int var1) throws SocketException {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Invalid receive size");
      } else {
         this.setIntOption(StandardSocketOptions.SO_RCVBUF, var1);
      }
   }

   public int getReceiveBufferSize() throws SocketException {
      return this.getIntOption(StandardSocketOptions.SO_RCVBUF);
   }

   public void setReuseAddress(boolean var1) throws SocketException {
      this.setBooleanOption(StandardSocketOptions.SO_REUSEADDR, var1);
   }

   public boolean getReuseAddress() throws SocketException {
      return this.getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
   }

   public void setBroadcast(boolean var1) throws SocketException {
      this.setBooleanOption(StandardSocketOptions.SO_BROADCAST, var1);
   }

   public boolean getBroadcast() throws SocketException {
      return this.getBooleanOption(StandardSocketOptions.SO_BROADCAST);
   }

   public void setTrafficClass(int var1) throws SocketException {
      this.setIntOption(StandardSocketOptions.IP_TOS, var1);
   }

   public int getTrafficClass() throws SocketException {
      return this.getIntOption(StandardSocketOptions.IP_TOS);
   }

   public void close() {
      try {
         this.dc.close();
      } catch (IOException var2) {
         throw new Error(var2);
      }
   }

   public boolean isClosed() {
      return !this.dc.isOpen();
   }

   public DatagramChannel getChannel() {
      return this.dc;
   }
}
