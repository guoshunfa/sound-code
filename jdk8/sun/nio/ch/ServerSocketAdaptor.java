package sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerSocketAdaptor extends ServerSocket {
   private final ServerSocketChannelImpl ssc;
   private volatile int timeout = 0;

   public static ServerSocket create(ServerSocketChannelImpl var0) {
      try {
         return new ServerSocketAdaptor(var0);
      } catch (IOException var2) {
         throw new Error(var2);
      }
   }

   private ServerSocketAdaptor(ServerSocketChannelImpl var1) throws IOException {
      this.ssc = var1;
   }

   public void bind(SocketAddress var1) throws IOException {
      this.bind(var1, 50);
   }

   public void bind(SocketAddress var1, int var2) throws IOException {
      if (var1 == null) {
         var1 = new InetSocketAddress(0);
      }

      try {
         this.ssc.bind((SocketAddress)var1, var2);
      } catch (Exception var4) {
         Net.translateException(var4);
      }

   }

   public InetAddress getInetAddress() {
      return !this.ssc.isBound() ? null : Net.getRevealedLocalAddress(this.ssc.localAddress()).getAddress();
   }

   public int getLocalPort() {
      return !this.ssc.isBound() ? -1 : Net.asInetSocketAddress(this.ssc.localAddress()).getPort();
   }

   public Socket accept() throws IOException {
      synchronized(this.ssc.blockingLock()) {
         if (!this.ssc.isBound()) {
            throw new IllegalBlockingModeException();
         } else {
            Socket var10000;
            try {
               SocketChannel var2;
               if (this.timeout != 0) {
                  this.ssc.configureBlocking(false);

                  try {
                     if ((var2 = this.ssc.accept()) != null) {
                        Socket var17 = var2.socket();
                        return var17;
                     }

                     long var3 = (long)this.timeout;

                     do {
                        if (!this.ssc.isOpen()) {
                           throw new ClosedChannelException();
                        }

                        long var5 = System.currentTimeMillis();
                        int var7 = this.ssc.poll(Net.POLLIN, var3);
                        if (var7 > 0 && (var2 = this.ssc.accept()) != null) {
                           Socket var8 = var2.socket();
                           return var8;
                        }

                        var3 -= System.currentTimeMillis() - var5;
                     } while(var3 > 0L);

                     throw new SocketTimeoutException();
                  } finally {
                     if (this.ssc.isOpen()) {
                        this.ssc.configureBlocking(true);
                     }

                  }
               }

               var2 = this.ssc.accept();
               if (var2 == null && !this.ssc.isBlocking()) {
                  throw new IllegalBlockingModeException();
               }

               var10000 = var2.socket();
            } catch (Exception var15) {
               Net.translateException(var15);

               assert false;

               return null;
            }

            return var10000;
         }
      }
   }

   public void close() throws IOException {
      this.ssc.close();
   }

   public ServerSocketChannel getChannel() {
      return this.ssc;
   }

   public boolean isBound() {
      return this.ssc.isBound();
   }

   public boolean isClosed() {
      return !this.ssc.isOpen();
   }

   public void setSoTimeout(int var1) throws SocketException {
      this.timeout = var1;
   }

   public int getSoTimeout() throws SocketException {
      return this.timeout;
   }

   public void setReuseAddress(boolean var1) throws SocketException {
      try {
         this.ssc.setOption(StandardSocketOptions.SO_REUSEADDR, var1);
      } catch (IOException var3) {
         Net.translateToSocketException(var3);
      }

   }

   public boolean getReuseAddress() throws SocketException {
      try {
         return (Boolean)this.ssc.getOption(StandardSocketOptions.SO_REUSEADDR);
      } catch (IOException var2) {
         Net.translateToSocketException(var2);
         return false;
      }
   }

   public String toString() {
      return !this.isBound() ? "ServerSocket[unbound]" : "ServerSocket[addr=" + this.getInetAddress() + ",localport=" + this.getLocalPort() + "]";
   }

   public void setReceiveBufferSize(int var1) throws SocketException {
      if (var1 <= 0) {
         throw new IllegalArgumentException("size cannot be 0 or negative");
      } else {
         try {
            this.ssc.setOption(StandardSocketOptions.SO_RCVBUF, var1);
         } catch (IOException var3) {
            Net.translateToSocketException(var3);
         }

      }
   }

   public int getReceiveBufferSize() throws SocketException {
      try {
         return (Integer)this.ssc.getOption(StandardSocketOptions.SO_RCVBUF);
      } catch (IOException var2) {
         Net.translateToSocketException(var2);
         return -1;
      }
   }
}
