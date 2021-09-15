package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;

public interface RepositoryOperations {
   int registerServer(ServerDef var1) throws ServerAlreadyRegistered, BadServerDefinition;

   void unregisterServer(int var1) throws ServerNotRegistered;

   ServerDef getServer(int var1) throws ServerNotRegistered;

   boolean isInstalled(int var1) throws ServerNotRegistered;

   void install(int var1) throws ServerNotRegistered, ServerAlreadyInstalled;

   void uninstall(int var1) throws ServerNotRegistered, ServerAlreadyUninstalled;

   int[] listRegisteredServers();

   String[] getApplicationNames();

   int getServerID(String var1) throws ServerNotRegistered;
}
