package sun.rmi.transport.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class RMIDirectSocketFactory extends RMISocketFactory {
   public Socket createSocket(String var1, int var2) throws IOException {
      return new Socket(var1, var2);
   }

   public ServerSocket createServerSocket(int var1) throws IOException {
      return new ServerSocket(var1);
   }
}
