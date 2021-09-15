package com.sun.corba.se.spi.legacy.connection;

public interface LegacyServerSocketEndPointInfo {
   String DEFAULT_ENDPOINT = "DEFAULT_ENDPOINT";
   String BOOT_NAMING = "BOOT_NAMING";
   String NO_NAME = "NO_NAME";

   String getType();

   String getHostName();

   int getPort();

   int getLocatorPort();

   void setLocatorPort(int var1);

   String getName();
}
