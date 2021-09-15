package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.security.AccessController;
import java.security.PrivilegedAction;

class WrappedSocket extends Socket {
   protected Socket socket;
   protected InputStream in = null;
   protected OutputStream out = null;

   public WrappedSocket(Socket var1, InputStream var2, OutputStream var3) throws IOException {
      super((SocketImpl)null);
      this.socket = var1;
      this.in = var2;
      this.out = var3;
   }

   public InetAddress getInetAddress() {
      return this.socket.getInetAddress();
   }

   public InetAddress getLocalAddress() {
      return (InetAddress)AccessController.doPrivileged(new PrivilegedAction<InetAddress>() {
         public InetAddress run() {
            return WrappedSocket.this.socket.getLocalAddress();
         }
      });
   }

   public int getPort() {
      return this.socket.getPort();
   }

   public int getLocalPort() {
      return this.socket.getLocalPort();
   }

   public InputStream getInputStream() throws IOException {
      if (this.in == null) {
         this.in = this.socket.getInputStream();
      }

      return this.in;
   }

   public OutputStream getOutputStream() throws IOException {
      if (this.out == null) {
         this.out = this.socket.getOutputStream();
      }

      return this.out;
   }

   public void setTcpNoDelay(boolean var1) throws SocketException {
      this.socket.setTcpNoDelay(var1);
   }

   public boolean getTcpNoDelay() throws SocketException {
      return this.socket.getTcpNoDelay();
   }

   public void setSoLinger(boolean var1, int var2) throws SocketException {
      this.socket.setSoLinger(var1, var2);
   }

   public int getSoLinger() throws SocketException {
      return this.socket.getSoLinger();
   }

   public synchronized void setSoTimeout(int var1) throws SocketException {
      this.socket.setSoTimeout(var1);
   }

   public synchronized int getSoTimeout() throws SocketException {
      return this.socket.getSoTimeout();
   }

   public synchronized void close() throws IOException {
      this.socket.close();
   }

   public String toString() {
      return "Wrapped" + this.socket.toString();
   }
}
