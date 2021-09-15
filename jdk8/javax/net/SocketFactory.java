package javax.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public abstract class SocketFactory {
   private static SocketFactory theFactory;

   protected SocketFactory() {
   }

   public static SocketFactory getDefault() {
      Class var0 = SocketFactory.class;
      synchronized(SocketFactory.class) {
         if (theFactory == null) {
            theFactory = new DefaultSocketFactory();
         }
      }

      return theFactory;
   }

   public Socket createSocket() throws IOException {
      UnsupportedOperationException var1 = new UnsupportedOperationException();
      SocketException var2 = new SocketException("Unconnected sockets not implemented");
      var2.initCause(var1);
      throw var2;
   }

   public abstract Socket createSocket(String var1, int var2) throws IOException, UnknownHostException;

   public abstract Socket createSocket(String var1, int var2, InetAddress var3, int var4) throws IOException, UnknownHostException;

   public abstract Socket createSocket(InetAddress var1, int var2) throws IOException;

   public abstract Socket createSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException;
}
