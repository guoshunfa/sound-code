package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

class DefaultSSLServerSocketFactory extends SSLServerSocketFactory {
   private final Exception reason;

   DefaultSSLServerSocketFactory(Exception var1) {
      this.reason = var1;
   }

   private ServerSocket throwException() throws SocketException {
      throw (SocketException)(new SocketException(this.reason.toString())).initCause(this.reason);
   }

   public ServerSocket createServerSocket() throws IOException {
      return this.throwException();
   }

   public ServerSocket createServerSocket(int var1) throws IOException {
      return this.throwException();
   }

   public ServerSocket createServerSocket(int var1, int var2) throws IOException {
      return this.throwException();
   }

   public ServerSocket createServerSocket(int var1, int var2, InetAddress var3) throws IOException {
      return this.throwException();
   }

   public String[] getDefaultCipherSuites() {
      return new String[0];
   }

   public String[] getSupportedCipherSuites() {
      return new String[0];
   }
}
