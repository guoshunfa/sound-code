package com.sun.corba.se.spi.activation;

public interface ActivatorOperations {
   void active(int var1, Server var2) throws ServerNotRegistered;

   void registerEndpoints(int var1, String var2, EndPointInfo[] var3) throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered;

   int[] getActiveServers();

   void activate(int var1) throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown;

   void shutdown(int var1) throws ServerNotActive, ServerNotRegistered;

   void install(int var1) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled;

   String[] getORBNames(int var1) throws ServerNotRegistered;

   void uninstall(int var1) throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled;
}
