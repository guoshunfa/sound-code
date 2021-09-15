package sun.nio.ch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketOption;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class SocketAdaptor extends Socket {
   private final SocketChannelImpl sc;
   private volatile int timeout = 0;
   private InputStream socketInputStream = null;

   private SocketAdaptor(SocketChannelImpl var1) throws SocketException {
      super((SocketImpl)null);
      this.sc = var1;
   }

   public static Socket create(SocketChannelImpl var0) {
      try {
         return new SocketAdaptor(var0);
      } catch (SocketException var2) {
         throw new InternalError("Should not reach here");
      }
   }

   public SocketChannel getChannel() {
      return this.sc;
   }

   public void connect(SocketAddress var1) throws IOException {
      this.connect(var1, 0);
   }

   public void connect(SocketAddress var1, int var2) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("connect: The address can't be null");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("connect: timeout can't be negative");
      } else {
         synchronized(this.sc.blockingLock()) {
            if (!this.sc.isBlocking()) {
               throw new IllegalBlockingModeException();
            } else {
               try {
                  if (var2 != 0) {
                     this.sc.configureBlocking(false);

                     try {
                        if (this.sc.connect(var1)) {
                           return;
                        }

                        long var4 = (long)var2;

                        do {
                           if (!this.sc.isOpen()) {
                              throw new ClosedChannelException();
                           }

                           long var6 = System.currentTimeMillis();
                           int var8 = this.sc.poll(Net.POLLCONN, var4);
                           if (var8 > 0 && this.sc.finishConnect()) {
                              return;
                           }

                           var4 -= System.currentTimeMillis() - var6;
                        } while(var4 > 0L);

                        try {
                           this.sc.close();
                        } catch (IOException var16) {
                        }

                        throw new SocketTimeoutException();
                     } finally {
                        if (this.sc.isOpen()) {
                           this.sc.configureBlocking(true);
                        }

                     }
                  }

                  this.sc.connect(var1);
               } catch (Exception var18) {
                  Net.translateException(var18, true);
                  return;
               }

            }
         }
      }
   }

   public void bind(SocketAddress var1) throws IOException {
      try {
         this.sc.bind(var1);
      } catch (Exception var3) {
         Net.translateException(var3);
      }

   }

   public InetAddress getInetAddress() {
      SocketAddress var1 = this.sc.remoteAddress();
      return var1 == null ? null : ((InetSocketAddress)var1).getAddress();
   }

   public InetAddress getLocalAddress() {
      if (this.sc.isOpen()) {
         InetSocketAddress var1 = this.sc.localAddress();
         if (var1 != null) {
            return Net.getRevealedLocalAddress(var1).getAddress();
         }
      }

      return (new InetSocketAddress(0)).getAddress();
   }

   public int getPort() {
      SocketAddress var1 = this.sc.remoteAddress();
      return var1 == null ? 0 : ((InetSocketAddress)var1).getPort();
   }

   public int getLocalPort() {
      InetSocketAddress var1 = this.sc.localAddress();
      return var1 == null ? -1 : ((InetSocketAddress)var1).getPort();
   }

   public InputStream getInputStream() throws IOException {
      if (!this.sc.isOpen()) {
         throw new SocketException("Socket is closed");
      } else if (!this.sc.isConnected()) {
         throw new SocketException("Socket is not connected");
      } else if (!this.sc.isInputOpen()) {
         throw new SocketException("Socket input is shutdown");
      } else {
         if (this.socketInputStream == null) {
            try {
               this.socketInputStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
                  public InputStream run() throws IOException {
                     return SocketAdaptor.this.new SocketInputStream();
                  }
               });
            } catch (PrivilegedActionException var2) {
               throw (IOException)var2.getException();
            }
         }

         return this.socketInputStream;
      }
   }

   public OutputStream getOutputStream() throws IOException {
      if (!this.sc.isOpen()) {
         throw new SocketException("Socket is closed");
      } else if (!this.sc.isConnected()) {
         throw new SocketException("Socket is not connected");
      } else if (!this.sc.isOutputOpen()) {
         throw new SocketException("Socket output is shutdown");
      } else {
         OutputStream var1 = null;

         try {
            var1 = (OutputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<OutputStream>() {
               public OutputStream run() throws IOException {
                  return Channels.newOutputStream((WritableByteChannel)SocketAdaptor.this.sc);
               }
            });
            return var1;
         } catch (PrivilegedActionException var3) {
            throw (IOException)var3.getException();
         }
      }
   }

   private void setBooleanOption(SocketOption<Boolean> var1, boolean var2) throws SocketException {
      try {
         this.sc.setOption(var1, var2);
      } catch (IOException var4) {
         Net.translateToSocketException(var4);
      }

   }

   private void setIntOption(SocketOption<Integer> var1, int var2) throws SocketException {
      try {
         this.sc.setOption(var1, var2);
      } catch (IOException var4) {
         Net.translateToSocketException(var4);
      }

   }

   private boolean getBooleanOption(SocketOption<Boolean> var1) throws SocketException {
      try {
         return (Boolean)this.sc.getOption(var1);
      } catch (IOException var3) {
         Net.translateToSocketException(var3);
         return false;
      }
   }

   private int getIntOption(SocketOption<Integer> var1) throws SocketException {
      try {
         return (Integer)this.sc.getOption(var1);
      } catch (IOException var3) {
         Net.translateToSocketException(var3);
         return -1;
      }
   }

   public void setTcpNoDelay(boolean var1) throws SocketException {
      this.setBooleanOption(StandardSocketOptions.TCP_NODELAY, var1);
   }

   public boolean getTcpNoDelay() throws SocketException {
      return this.getBooleanOption(StandardSocketOptions.TCP_NODELAY);
   }

   public void setSoLinger(boolean var1, int var2) throws SocketException {
      if (!var1) {
         var2 = -1;
      }

      this.setIntOption(StandardSocketOptions.SO_LINGER, var2);
   }

   public int getSoLinger() throws SocketException {
      return this.getIntOption(StandardSocketOptions.SO_LINGER);
   }

   public void sendUrgentData(int var1) throws IOException {
      int var2 = this.sc.sendOutOfBandData((byte)var1);
      if (var2 == 0) {
         throw new IOException("Socket buffer full");
      }
   }

   public void setOOBInline(boolean var1) throws SocketException {
      this.setBooleanOption(ExtendedSocketOption.SO_OOBINLINE, var1);
   }

   public boolean getOOBInline() throws SocketException {
      return this.getBooleanOption(ExtendedSocketOption.SO_OOBINLINE);
   }

   public void setSoTimeout(int var1) throws SocketException {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeout can't be negative");
      } else {
         this.timeout = var1;
      }
   }

   public int getSoTimeout() throws SocketException {
      return this.timeout;
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

   public void setKeepAlive(boolean var1) throws SocketException {
      this.setBooleanOption(StandardSocketOptions.SO_KEEPALIVE, var1);
   }

   public boolean getKeepAlive() throws SocketException {
      return this.getBooleanOption(StandardSocketOptions.SO_KEEPALIVE);
   }

   public void setTrafficClass(int var1) throws SocketException {
      this.setIntOption(StandardSocketOptions.IP_TOS, var1);
   }

   public int getTrafficClass() throws SocketException {
      return this.getIntOption(StandardSocketOptions.IP_TOS);
   }

   public void setReuseAddress(boolean var1) throws SocketException {
      this.setBooleanOption(StandardSocketOptions.SO_REUSEADDR, var1);
   }

   public boolean getReuseAddress() throws SocketException {
      return this.getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
   }

   public void close() throws IOException {
      this.sc.close();
   }

   public void shutdownInput() throws IOException {
      try {
         this.sc.shutdownInput();
      } catch (Exception var2) {
         Net.translateException(var2);
      }

   }

   public void shutdownOutput() throws IOException {
      try {
         this.sc.shutdownOutput();
      } catch (Exception var2) {
         Net.translateException(var2);
      }

   }

   public String toString() {
      return this.sc.isConnected() ? "Socket[addr=" + this.getInetAddress() + ",port=" + this.getPort() + ",localport=" + this.getLocalPort() + "]" : "Socket[unconnected]";
   }

   public boolean isConnected() {
      return this.sc.isConnected();
   }

   public boolean isBound() {
      return this.sc.localAddress() != null;
   }

   public boolean isClosed() {
      return !this.sc.isOpen();
   }

   public boolean isInputShutdown() {
      return !this.sc.isInputOpen();
   }

   public boolean isOutputShutdown() {
      return !this.sc.isOutputOpen();
   }

   private class SocketInputStream extends ChannelInputStream {
      private SocketInputStream() {
         super(SocketAdaptor.this.sc);
      }

      protected int read(ByteBuffer var1) throws IOException {
         synchronized(SocketAdaptor.this.sc.blockingLock()) {
            if (!SocketAdaptor.this.sc.isBlocking()) {
               throw new IllegalBlockingModeException();
            } else if (SocketAdaptor.this.timeout == 0) {
               return SocketAdaptor.this.sc.read(var1);
            } else {
               SocketAdaptor.this.sc.configureBlocking(false);

               try {
                  int var3;
                  if ((var3 = SocketAdaptor.this.sc.read(var1)) != 0) {
                     int var16 = var3;
                     return var16;
                  } else {
                     long var4 = (long)SocketAdaptor.this.timeout;

                     do {
                        if (!SocketAdaptor.this.sc.isOpen()) {
                           throw new ClosedChannelException();
                        }

                        long var6 = System.currentTimeMillis();
                        int var8 = SocketAdaptor.this.sc.poll(Net.POLLIN, var4);
                        if (var8 > 0 && (var3 = SocketAdaptor.this.sc.read(var1)) != 0) {
                           int var9 = var3;
                           return var9;
                        }

                        var4 -= System.currentTimeMillis() - var6;
                     } while(var4 > 0L);

                     throw new SocketTimeoutException();
                  }
               } finally {
                  if (SocketAdaptor.this.sc.isOpen()) {
                     SocketAdaptor.this.sc.configureBlocking(true);
                  }

               }
            }
         }
      }

      // $FF: synthetic method
      SocketInputStream(Object var2) {
         this();
      }
   }
}
