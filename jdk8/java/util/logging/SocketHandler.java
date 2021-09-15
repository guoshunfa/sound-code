package java.util.logging;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketHandler extends StreamHandler {
   private Socket sock;
   private String host;
   private int port;

   private void configure() {
      LogManager var1 = LogManager.getLogManager();
      String var2 = this.getClass().getName();
      this.setLevel(var1.getLevelProperty(var2 + ".level", Level.ALL));
      this.setFilter(var1.getFilterProperty(var2 + ".filter", (Filter)null));
      this.setFormatter(var1.getFormatterProperty(var2 + ".formatter", new XMLFormatter()));

      try {
         this.setEncoding(var1.getStringProperty(var2 + ".encoding", (String)null));
      } catch (Exception var6) {
         try {
            this.setEncoding((String)null);
         } catch (Exception var5) {
         }
      }

      this.port = var1.getIntProperty(var2 + ".port", 0);
      this.host = var1.getStringProperty(var2 + ".host", (String)null);
   }

   public SocketHandler() throws IOException {
      this.sealed = false;
      this.configure();

      try {
         this.connect();
      } catch (IOException var2) {
         System.err.println("SocketHandler: connect failed to " + this.host + ":" + this.port);
         throw var2;
      }

      this.sealed = true;
   }

   public SocketHandler(String var1, int var2) throws IOException {
      this.sealed = false;
      this.configure();
      this.sealed = true;
      this.port = var2;
      this.host = var1;
      this.connect();
   }

   private void connect() throws IOException {
      if (this.port == 0) {
         throw new IllegalArgumentException("Bad port: " + this.port);
      } else if (this.host == null) {
         throw new IllegalArgumentException("Null host name: " + this.host);
      } else {
         this.sock = new Socket(this.host, this.port);
         OutputStream var1 = this.sock.getOutputStream();
         BufferedOutputStream var2 = new BufferedOutputStream(var1);
         this.setOutputStream(var2);
      }
   }

   public synchronized void close() throws SecurityException {
      super.close();
      if (this.sock != null) {
         try {
            this.sock.close();
         } catch (IOException var2) {
         }
      }

      this.sock = null;
   }

   public synchronized void publish(LogRecord var1) {
      if (this.isLoggable(var1)) {
         super.publish(var1);
         this.flush();
      }
   }
}
