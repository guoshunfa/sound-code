package sun.rmi.transport.proxy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class HttpReceiveSocket extends WrappedSocket implements RMISocketInfo {
   private boolean headerSent = false;

   public HttpReceiveSocket(Socket var1, InputStream var2, OutputStream var3) throws IOException {
      super(var1, var2, var3);
      this.in = new HttpInputStream(var2 != null ? var2 : var1.getInputStream());
      this.out = var3 != null ? var3 : var1.getOutputStream();
   }

   public boolean isReusable() {
      return false;
   }

   public InetAddress getInetAddress() {
      return null;
   }

   public OutputStream getOutputStream() throws IOException {
      if (!this.headerSent) {
         DataOutputStream var1 = new DataOutputStream(this.out);
         var1.writeBytes("HTTP/1.0 200 OK\r\n");
         var1.flush();
         this.headerSent = true;
         this.out = new HttpOutputStream(this.out);
      }

      return this.out;
   }

   public synchronized void close() throws IOException {
      this.getOutputStream().close();
      this.socket.close();
   }

   public String toString() {
      return "HttpReceive" + this.socket.toString();
   }
}
