package com.sun.corba.se.impl.transport;

import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.ORBSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class DefaultSocketFactoryImpl implements ORBSocketFactory {
   private ORB orb;
   private static final boolean keepAlive = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         String var1 = System.getProperty("com.sun.CORBA.transport.enableTcpKeepAlive");
         return var1 != null ? new Boolean(!"false".equalsIgnoreCase(var1)) : Boolean.FALSE;
      }
   });

   public void setORB(ORB var1) {
      this.orb = var1;
   }

   public ServerSocket createServerSocket(String var1, InetSocketAddress var2) throws IOException {
      ServerSocketChannel var3 = null;
      ServerSocket var4 = null;
      if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
         var3 = ServerSocketChannel.open();
         var4 = var3.socket();
      } else {
         var4 = new ServerSocket();
      }

      var4.bind(var2);
      return var4;
   }

   public Socket createSocket(String var1, InetSocketAddress var2) throws IOException {
      SocketChannel var3 = null;
      Socket var4 = null;
      if (this.orb.getORBData().connectionSocketType().equals("SocketChannel")) {
         var3 = SocketChannel.open(var2);
         var4 = var3.socket();
      } else {
         var4 = new Socket(var2.getHostName(), var2.getPort());
      }

      var4.setTcpNoDelay(true);
      if (keepAlive) {
         var4.setKeepAlive(true);
      }

      return var4;
   }

   public void setAcceptedSocketOptions(Acceptor var1, ServerSocket var2, Socket var3) throws SocketException {
      var3.setTcpNoDelay(true);
      if (keepAlive) {
         var3.setKeepAlive(true);
      }

   }
}
