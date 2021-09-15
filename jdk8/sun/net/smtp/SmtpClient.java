package sun.net.smtp;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.TransferProtocolClient;
import sun.security.action.GetPropertyAction;

public class SmtpClient extends TransferProtocolClient {
   private static int DEFAULT_SMTP_PORT = 25;
   String mailhost;
   SmtpPrintStream message;

   public void closeServer() throws IOException {
      if (this.serverIsOpen()) {
         this.closeMessage();
         this.issueCommand("QUIT\r\n", 221);
         super.closeServer();
      }

   }

   void issueCommand(String var1, int var2) throws IOException {
      this.sendServer(var1);

      int var3;
      do {
         if ((var3 = this.readServerResponse()) == var2) {
            return;
         }
      } while(var3 == 220);

      throw new SmtpProtocolException(this.getResponseString());
   }

   private void toCanonical(String var1) throws IOException {
      if (var1.startsWith("<")) {
         this.issueCommand("rcpt to: " + var1 + "\r\n", 250);
      } else {
         this.issueCommand("rcpt to: <" + var1 + ">\r\n", 250);
      }

   }

   public void to(String var1) throws IOException {
      if (var1.indexOf(10) != -1) {
         throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
      } else {
         int var2 = 0;
         int var3 = var1.length();
         int var4 = 0;
         int var5 = 0;
         int var6 = 0;

         for(boolean var7 = false; var4 < var3; ++var4) {
            char var8 = var1.charAt(var4);
            if (var6 > 0) {
               if (var8 == '(') {
                  ++var6;
               } else if (var8 == ')') {
                  --var6;
               }

               if (var6 == 0) {
                  if (var5 > var2) {
                     var7 = true;
                  } else {
                     var2 = var4 + 1;
                  }
               }
            } else if (var8 == '(') {
               ++var6;
            } else if (var8 == '<') {
               var2 = var5 = var4 + 1;
            } else if (var8 == '>') {
               var7 = true;
            } else if (var8 == ',') {
               if (var5 > var2) {
                  this.toCanonical(var1.substring(var2, var5));
               }

               var2 = var4 + 1;
               var7 = false;
            } else if (var8 > ' ' && !var7) {
               var5 = var4 + 1;
            } else if (var2 == var4) {
               ++var2;
            }
         }

         if (var5 > var2) {
            this.toCanonical(var1.substring(var2, var5));
         }

      }
   }

   public void from(String var1) throws IOException {
      if (var1.indexOf(10) != -1) {
         throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
      } else {
         if (var1.startsWith("<")) {
            this.issueCommand("mail from: " + var1 + "\r\n", 250);
         } else {
            this.issueCommand("mail from: <" + var1 + ">\r\n", 250);
         }

      }
   }

   private void openServer(String var1) throws IOException {
      this.mailhost = var1;
      this.openServer(this.mailhost, DEFAULT_SMTP_PORT);
      this.issueCommand("helo " + InetAddress.getLocalHost().getHostName() + "\r\n", 250);
   }

   public PrintStream startMessage() throws IOException {
      this.issueCommand("data\r\n", 354);

      try {
         this.message = new SmtpPrintStream(this.serverOutput, this);
      } catch (UnsupportedEncodingException var2) {
         throw new InternalError(encoding + " encoding not found", var2);
      }

      return this.message;
   }

   void closeMessage() throws IOException {
      if (this.message != null) {
         this.message.close();
      }

   }

   public SmtpClient(String var1) throws IOException {
      if (var1 != null) {
         try {
            this.openServer(var1);
            this.mailhost = var1;
            return;
         } catch (Exception var5) {
         }
      }

      try {
         this.mailhost = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("mail.host")));
         if (this.mailhost != null) {
            this.openServer(this.mailhost);
            return;
         }
      } catch (Exception var4) {
      }

      try {
         this.mailhost = "localhost";
         this.openServer(this.mailhost);
      } catch (Exception var3) {
         this.mailhost = "mailhost";
         this.openServer(this.mailhost);
      }

   }

   public SmtpClient() throws IOException {
      this((String)null);
   }

   public SmtpClient(int var1) throws IOException {
      this.setConnectTimeout(var1);

      try {
         this.mailhost = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("mail.host")));
         if (this.mailhost != null) {
            this.openServer(this.mailhost);
            return;
         }
      } catch (Exception var4) {
      }

      try {
         this.mailhost = "localhost";
         this.openServer(this.mailhost);
      } catch (Exception var3) {
         this.mailhost = "mailhost";
         this.openServer(this.mailhost);
      }

   }

   public String getMailHost() {
      return this.mailhost;
   }

   String getEncoding() {
      return encoding;
   }
}
