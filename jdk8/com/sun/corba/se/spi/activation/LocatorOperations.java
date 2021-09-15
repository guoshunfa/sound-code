package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;

public interface LocatorOperations {
   ServerLocation locateServer(int var1, String var2) throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown;

   ServerLocationPerORB locateServerForORB(int var1, String var2) throws InvalidORBid, ServerNotRegistered, ServerHeldDown;

   int getEndpoint(String var1) throws NoSuchEndPoint;

   int getServerPortForType(ServerLocationPerORB var1, String var2) throws NoSuchEndPoint;
}
