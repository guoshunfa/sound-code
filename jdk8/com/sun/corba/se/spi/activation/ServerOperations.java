package com.sun.corba.se.spi.activation;

public interface ServerOperations {
   void shutdown();

   void install();

   void uninstall();
}
