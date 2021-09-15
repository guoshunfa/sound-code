package sun.rmi.transport.proxy;

import java.net.InetAddress;
import java.net.UnknownHostException;

final class CGITryHostnameCommand implements CGICommandHandler {
   public String getName() {
      return "tryhostname";
   }

   public void execute(String var1) {
      System.out.println("Status: 200 OK");
      System.out.println("Content-type: text/html");
      System.out.println("");
      System.out.println("<HTML><HEAD><TITLE>Java RMI Server Hostname Info</TITLE></HEAD><BODY>");
      System.out.println("<H1>Java RMI Server Hostname Info</H1>");
      System.out.println("<H2>Local host name available to Java VM:</H2>");
      System.out.print("<P>InetAddress.getLocalHost().getHostName()");

      try {
         String var2 = InetAddress.getLocalHost().getHostName();
         System.out.println(" = " + var2);
      } catch (UnknownHostException var3) {
         System.out.println(" threw java.net.UnknownHostException");
      }

      System.out.println("<H2>Server host information obtained through CGI interface from HTTP server:</H2>");
      System.out.println("<P>SERVER_NAME = " + CGIHandler.ServerName);
      System.out.println("<P>SERVER_PORT = " + CGIHandler.ServerPort);
      System.out.println("</BODY></HTML>");
   }
}
