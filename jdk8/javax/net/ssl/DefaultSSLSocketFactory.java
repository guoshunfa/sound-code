package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

class DefaultSSLSocketFactory extends SSLSocketFactory {
   private Exception reason;

   DefaultSSLSocketFactory(Exception var1) {
      this.reason = var1;
   }

   private Socket throwException() throws SocketException {
      throw (SocketException)(new SocketException(this.reason.toString())).initCause(this.reason);
   }

   public Socket createSocket() throws IOException {
      return this.throwException();
   }

   public Socket createSocket(String var1, int var2) throws IOException {
      return this.throwException();
   }

   public Socket createSocket(Socket var1, String var2, int var3, boolean var4) throws IOException {
      return this.throwException();
   }

   public Socket createSocket(InetAddress var1, int var2) throws IOException {
      return this.throwException();
   }

   public Socket createSocket(String var1, int var2, InetAddress var3, int var4) throws IOException {
      return this.throwException();
   }

   public Socket createSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException {
      return this.throwException();
   }

   public String[] getDefaultCipherSuites() {
      return new String[0];
   }

   public String[] getSupportedCipherSuites() {
      return new String[0];
   }
}
