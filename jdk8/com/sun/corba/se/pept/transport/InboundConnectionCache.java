package com.sun.corba.se.pept.transport;

public interface InboundConnectionCache extends ConnectionCache {
   Connection get(Acceptor var1);

   void put(Acceptor var1, Connection var2);

   void remove(Connection var1);

   Acceptor getAcceptor();
}
