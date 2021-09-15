package sun.net.www.protocol.mailto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketPermission;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Permission;
import sun.net.smtp.SmtpClient;
import sun.net.www.MessageHeader;
import sun.net.www.ParseUtil;
import sun.net.www.URLConnection;

public class MailToURLConnection extends URLConnection {
   InputStream is = null;
   OutputStream os = null;
   SmtpClient client;
   Permission permission;
   private int connectTimeout = -1;
   private int readTimeout = -1;

   MailToURLConnection(URL var1) {
      super(var1);
      MessageHeader var2 = new MessageHeader();
      var2.add("content-type", "text/html");
      this.setProperties(var2);
   }

   String getFromAddress() {
      String var1 = System.getProperty("user.fromaddr");
      if (var1 == null) {
         var1 = System.getProperty("user.name");
         if (var1 != null) {
            String var2 = System.getProperty("mail.host");
            if (var2 == null) {
               try {
                  var2 = InetAddress.getLocalHost().getHostName();
               } catch (UnknownHostException var4) {
               }
            }

            var1 = var1 + "@" + var2;
         } else {
            var1 = "";
         }
      }

      return var1;
   }

   public void connect() throws IOException {
      this.client = new SmtpClient(this.connectTimeout);
      this.client.setReadTimeout(this.readTimeout);
   }

   public synchronized OutputStream getOutputStream() throws IOException {
      if (this.os != null) {
         return this.os;
      } else if (this.is != null) {
         throw new IOException("Cannot write output after reading input.");
      } else {
         this.connect();
         String var1 = ParseUtil.decode(this.url.getPath());
         this.client.from(this.getFromAddress());
         this.client.to(var1);
         this.os = this.client.startMessage();
         return this.os;
      }
   }

   public Permission getPermission() throws IOException {
      if (this.permission == null) {
         this.connect();
         String var1 = this.client.getMailHost() + ":" + 25;
         this.permission = new SocketPermission(var1, "connect");
      }

      return this.permission;
   }

   public void setConnectTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeouts can't be negative");
      } else {
         this.connectTimeout = var1;
      }
   }

   public int getConnectTimeout() {
      return this.connectTimeout < 0 ? 0 : this.connectTimeout;
   }

   public void setReadTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeouts can't be negative");
      } else {
         this.readTimeout = var1;
      }
   }

   public int getReadTimeout() {
      return this.readTimeout < 0 ? 0 : this.readTimeout;
   }
}
