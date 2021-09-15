package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class DefaultSocketFactory implements ORBSocketFactory {
   private ORB orb;
   private static ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");

   public void setORB(ORB var1) {
      this.orb = var1;
   }

   public ServerSocket createServerSocket(String var1, int var2) throws IOException {
      if (!var1.equals("IIOP_CLEAR_TEXT")) {
         throw wrapper.defaultCreateServerSocketGivenNonIiopClearText(var1);
      } else {
         ServerSocket var3;
         if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
            ServerSocketChannel var4 = ServerSocketChannel.open();
            var3 = var4.socket();
         } else {
            var3 = new ServerSocket();
         }

         var3.bind(new InetSocketAddress(var2));
         return var3;
      }
   }

   public SocketInfo getEndPointInfo(org.omg.CORBA.ORB var1, IOR var2, SocketInfo var3) {
      IIOPProfileTemplate var4 = (IIOPProfileTemplate)var2.getProfile().getTaggedProfileTemplate();
      IIOPAddress var5 = var4.getPrimaryAddress();
      return new EndPointInfoImpl("IIOP_CLEAR_TEXT", var5.getPort(), var5.getHost().toLowerCase());
   }

   public Socket createSocket(SocketInfo var1) throws IOException, GetEndPointInfoAgainException {
      Socket var2;
      if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
         InetSocketAddress var3 = new InetSocketAddress(var1.getHost(), var1.getPort());
         SocketChannel var4 = SocketChannel.open(var3);
         var2 = var4.socket();
      } else {
         var2 = new Socket(var1.getHost(), var1.getPort());
      }

      try {
         var2.setTcpNoDelay(true);
      } catch (Exception var5) {
      }

      return var2;
   }
}
