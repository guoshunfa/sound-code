package com.sun.corba.se.spi.legacy.connection;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.transport.SocketInfo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.omg.CORBA.ORB;

public interface ORBSocketFactory {
   String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";

   ServerSocket createServerSocket(String var1, int var2) throws IOException;

   SocketInfo getEndPointInfo(ORB var1, IOR var2, SocketInfo var3);

   Socket createSocket(SocketInfo var1) throws IOException, GetEndPointInfoAgainException;
}
