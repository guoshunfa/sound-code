package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import sun.rmi.transport.proxy.RMIMasterSocketFactory;

public abstract class RMISocketFactory implements RMIClientSocketFactory, RMIServerSocketFactory {
   private static RMISocketFactory factory = null;
   private static RMISocketFactory defaultSocketFactory;
   private static RMIFailureHandler handler = null;

   public abstract Socket createSocket(String var1, int var2) throws IOException;

   public abstract ServerSocket createServerSocket(int var1) throws IOException;

   public static synchronized void setSocketFactory(RMISocketFactory var0) throws IOException {
      if (factory != null) {
         throw new SocketException("factory already defined");
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkSetFactory();
         }

         factory = var0;
      }
   }

   public static synchronized RMISocketFactory getSocketFactory() {
      return factory;
   }

   public static synchronized RMISocketFactory getDefaultSocketFactory() {
      if (defaultSocketFactory == null) {
         defaultSocketFactory = new RMIMasterSocketFactory();
      }

      return defaultSocketFactory;
   }

   public static synchronized void setFailureHandler(RMIFailureHandler var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkSetFactory();
      }

      handler = var0;
   }

   public static synchronized RMIFailureHandler getFailureHandler() {
      return handler;
   }
}
