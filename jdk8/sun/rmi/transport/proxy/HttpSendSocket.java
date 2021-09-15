package sun.rmi.transport.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.rmi.runtime.Log;
import sun.security.action.GetPropertyAction;

class HttpSendSocket extends Socket implements RMISocketInfo {
   protected String host;
   protected int port;
   protected URL url;
   protected URLConnection conn;
   protected InputStream in;
   protected OutputStream out;
   protected HttpSendInputStream inNotifier;
   protected HttpSendOutputStream outNotifier;
   private String lineSeparator;

   public HttpSendSocket(String var1, int var2, URL var3) throws IOException {
      super((SocketImpl)null);
      this.conn = null;
      this.in = null;
      this.out = null;
      this.lineSeparator = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("line.separator")));
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
         RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "host = " + var1 + ", port = " + var2 + ", url = " + var3);
      }

      this.host = var1;
      this.port = var2;
      this.url = var3;
      this.inNotifier = new HttpSendInputStream((InputStream)null, this);
      this.outNotifier = new HttpSendOutputStream(this.writeNotify(), this);
   }

   public HttpSendSocket(String var1, int var2) throws IOException {
      this(var1, var2, new URL("http", var1, var2, "/"));
   }

   public HttpSendSocket(InetAddress var1, int var2) throws IOException {
      this(var1.getHostName(), var2);
   }

   public boolean isReusable() {
      return false;
   }

   public synchronized OutputStream writeNotify() throws IOException {
      if (this.conn != null) {
         throw new IOException("attempt to write on HttpSendSocket after request has been sent");
      } else {
         this.conn = this.url.openConnection();
         this.conn.setDoOutput(true);
         this.conn.setUseCaches(false);
         this.conn.setRequestProperty("Content-type", "application/octet-stream");
         this.inNotifier.deactivate();
         this.in = null;
         return this.out = this.conn.getOutputStream();
      }
   }

   public synchronized InputStream readNotify() throws IOException {
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "sending request and activating input stream");
      this.outNotifier.deactivate();
      this.out.close();
      this.out = null;

      try {
         this.in = this.conn.getInputStream();
      } catch (IOException var5) {
         RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "failed to get input stream, exception: ", var5);
         throw new IOException("HTTP request failed");
      }

      String var1 = this.conn.getContentType();
      if (var1 != null && this.conn.getContentType().equals("application/octet-stream")) {
         return this.in;
      } else {
         if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
            String var2;
            if (var1 == null) {
               var2 = "missing content type in response" + this.lineSeparator;
            } else {
               var2 = "invalid content type in response: " + var1 + this.lineSeparator;
            }

            var2 = var2 + "HttpSendSocket.readNotify: response body: ";

            String var4;
            try {
               for(BufferedReader var3 = new BufferedReader(new InputStreamReader(this.in)); (var4 = var3.readLine()) != null; var2 = var2 + var4 + this.lineSeparator) {
               }
            } catch (IOException var6) {
            }

            RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, var2);
         }

         throw new IOException("HTTP request failed");
      }
   }

   public InetAddress getInetAddress() {
      try {
         return InetAddress.getByName(this.host);
      } catch (UnknownHostException var2) {
         return null;
      }
   }

   public InetAddress getLocalAddress() {
      try {
         return InetAddress.getLocalHost();
      } catch (UnknownHostException var2) {
         return null;
      }
   }

   public int getPort() {
      return this.port;
   }

   public int getLocalPort() {
      return -1;
   }

   public InputStream getInputStream() throws IOException {
      return this.inNotifier;
   }

   public OutputStream getOutputStream() throws IOException {
      return this.outNotifier;
   }

   public void setTcpNoDelay(boolean var1) throws SocketException {
   }

   public boolean getTcpNoDelay() throws SocketException {
      return false;
   }

   public void setSoLinger(boolean var1, int var2) throws SocketException {
   }

   public int getSoLinger() throws SocketException {
      return -1;
   }

   public synchronized void setSoTimeout(int var1) throws SocketException {
   }

   public synchronized int getSoTimeout() throws SocketException {
      return 0;
   }

   public synchronized void close() throws IOException {
      if (this.out != null) {
         this.out.close();
      }

   }

   public String toString() {
      return "HttpSendSocket[host=" + this.host + ",port=" + this.port + ",url=" + this.url + "]";
   }
}
