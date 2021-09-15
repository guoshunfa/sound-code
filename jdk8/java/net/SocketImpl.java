package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class SocketImpl implements SocketOptions {
   Socket socket = null;
   ServerSocket serverSocket = null;
   protected FileDescriptor fd;
   protected InetAddress address;
   protected int port;
   protected int localport;

   protected abstract void create(boolean var1) throws IOException;

   protected abstract void connect(String var1, int var2) throws IOException;

   protected abstract void connect(InetAddress var1, int var2) throws IOException;

   protected abstract void connect(SocketAddress var1, int var2) throws IOException;

   protected abstract void bind(InetAddress var1, int var2) throws IOException;

   protected abstract void listen(int var1) throws IOException;

   protected abstract void accept(SocketImpl var1) throws IOException;

   protected abstract InputStream getInputStream() throws IOException;

   protected abstract OutputStream getOutputStream() throws IOException;

   protected abstract int available() throws IOException;

   protected abstract void close() throws IOException;

   protected void shutdownInput() throws IOException {
      throw new IOException("Method not implemented!");
   }

   protected void shutdownOutput() throws IOException {
      throw new IOException("Method not implemented!");
   }

   protected FileDescriptor getFileDescriptor() {
      return this.fd;
   }

   protected InetAddress getInetAddress() {
      return this.address;
   }

   protected int getPort() {
      return this.port;
   }

   protected boolean supportsUrgentData() {
      return false;
   }

   protected abstract void sendUrgentData(int var1) throws IOException;

   protected int getLocalPort() {
      return this.localport;
   }

   void setSocket(Socket var1) {
      this.socket = var1;
   }

   Socket getSocket() {
      return this.socket;
   }

   void setServerSocket(ServerSocket var1) {
      this.serverSocket = var1;
   }

   ServerSocket getServerSocket() {
      return this.serverSocket;
   }

   public String toString() {
      return "Socket[addr=" + this.getInetAddress() + ",port=" + this.getPort() + ",localport=" + this.getLocalPort() + "]";
   }

   void reset() throws IOException {
      this.address = null;
      this.port = 0;
      this.localport = 0;
   }

   protected void setPerformancePreferences(int var1, int var2, int var3) {
   }

   <T> void setOption(SocketOption<T> var1, T var2) throws IOException {
      if (var1 == StandardSocketOptions.SO_KEEPALIVE) {
         this.setOption(8, var2);
      } else if (var1 == StandardSocketOptions.SO_SNDBUF) {
         this.setOption(4097, var2);
      } else if (var1 == StandardSocketOptions.SO_RCVBUF) {
         this.setOption(4098, var2);
      } else if (var1 == StandardSocketOptions.SO_REUSEADDR) {
         this.setOption(4, var2);
      } else if (var1 == StandardSocketOptions.SO_LINGER) {
         this.setOption(128, var2);
      } else if (var1 == StandardSocketOptions.IP_TOS) {
         this.setOption(3, var2);
      } else {
         if (var1 != StandardSocketOptions.TCP_NODELAY) {
            throw new UnsupportedOperationException("unsupported option");
         }

         this.setOption(1, var2);
      }

   }

   <T> T getOption(SocketOption<T> var1) throws IOException {
      if (var1 == StandardSocketOptions.SO_KEEPALIVE) {
         return this.getOption(8);
      } else if (var1 == StandardSocketOptions.SO_SNDBUF) {
         return this.getOption(4097);
      } else if (var1 == StandardSocketOptions.SO_RCVBUF) {
         return this.getOption(4098);
      } else if (var1 == StandardSocketOptions.SO_REUSEADDR) {
         return this.getOption(4);
      } else if (var1 == StandardSocketOptions.SO_LINGER) {
         return this.getOption(128);
      } else if (var1 == StandardSocketOptions.IP_TOS) {
         return this.getOption(3);
      } else if (var1 == StandardSocketOptions.TCP_NODELAY) {
         return this.getOption(1);
      } else {
         throw new UnsupportedOperationException("unsupported option");
      }
   }
}
