package com.sun.corba.se.spi.legacy.connection;

public interface LegacyServerSocketManager {
   int legacyGetTransientServerPort(String var1);

   int legacyGetPersistentServerPort(String var1);

   int legacyGetTransientOrPersistentServerPort(String var1);

   LegacyServerSocketEndPointInfo legacyGetEndpoint(String var1);

   boolean legacyIsLocalServerPort(int var1);
}
