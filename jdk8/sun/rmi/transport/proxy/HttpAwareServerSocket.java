package sun.rmi.transport.proxy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import sun.rmi.runtime.Log;

class HttpAwareServerSocket extends ServerSocket {
   public HttpAwareServerSocket(int var1) throws IOException {
      super(var1);
   }

   public HttpAwareServerSocket(int var1, int var2) throws IOException {
      super(var1, var2);
   }

   public Socket accept() throws IOException {
      Socket var1 = super.accept();
      BufferedInputStream var2 = new BufferedInputStream(var1.getInputStream());
      RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "socket accepted (checking for POST)");
      var2.mark(4);
      boolean var3 = var2.read() == 80 && var2.read() == 79 && var2.read() == 83 && var2.read() == 84;
      var2.reset();
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
         RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, var3 ? "POST found, HTTP socket returned" : "POST not found, direct socket returned");
      }

      return (Socket)(var3 ? new HttpReceiveSocket(var1, var2, (OutputStream)null) : new WrappedSocket(var1, var2, (OutputStream)null));
   }

   public String toString() {
      return "HttpAware" + super.toString();
   }
}
