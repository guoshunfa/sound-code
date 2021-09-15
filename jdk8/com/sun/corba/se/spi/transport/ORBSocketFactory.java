package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public interface ORBSocketFactory {
   void setORB(ORB var1);

   ServerSocket createServerSocket(String var1, InetSocketAddress var2) throws IOException;

   Socket createSocket(String var1, InetSocketAddress var2) throws IOException;

   void setAcceptedSocketOptions(Acceptor var1, ServerSocket var2, Socket var3) throws SocketException;
}
