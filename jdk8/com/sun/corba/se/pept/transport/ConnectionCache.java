package com.sun.corba.se.pept.transport;

public interface ConnectionCache {
   String getCacheType();

   void stampTime(Connection var1);

   long numberOfConnections();

   long numberOfIdleConnections();

   long numberOfBusyConnections();

   boolean reclaim();

   void close();
}
