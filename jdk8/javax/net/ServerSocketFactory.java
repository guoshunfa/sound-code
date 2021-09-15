package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;

public abstract class ServerSocketFactory {
   private static ServerSocketFactory theFactory;

   protected ServerSocketFactory() {
   }

   public static ServerSocketFactory getDefault() {
      Class var0 = ServerSocketFactory.class;
      synchronized(ServerSocketFactory.class) {
         if (theFactory == null) {
            theFactory = new DefaultServerSocketFactory();
         }
      }

      return theFactory;
   }

   public ServerSocket createServerSocket() throws IOException {
      throw new SocketException("Unbound server sockets not implemented");
   }

   public abstract ServerSocket createServerSocket(int var1) throws IOException;

   public abstract ServerSocket createServerSocket(int var1, int var2) throws IOException;

   public abstract ServerSocket createServerSocket(int var1, int var2, InetAddress var3) throws IOException;
}
