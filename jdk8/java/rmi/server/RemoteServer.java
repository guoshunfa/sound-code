package java.rmi.server;

import java.io.OutputStream;
import java.io.PrintStream;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.transport.tcp.TCPTransport;

public abstract class RemoteServer extends RemoteObject {
   private static final long serialVersionUID = -4100238210092549637L;
   private static boolean logNull;

   protected RemoteServer() {
   }

   protected RemoteServer(RemoteRef var1) {
      super(var1);
   }

   public static String getClientHost() throws ServerNotActiveException {
      return TCPTransport.getClientHost();
   }

   public static void setLog(OutputStream var0) {
      logNull = var0 == null;
      UnicastServerRef.callLog.setOutputStream(var0);
   }

   public static PrintStream getLog() {
      return logNull ? null : UnicastServerRef.callLog.getPrintStream();
   }

   static {
      logNull = !UnicastServerRef.logCalls;
   }
}
