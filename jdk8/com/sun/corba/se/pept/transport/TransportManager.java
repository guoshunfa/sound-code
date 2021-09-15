package com.sun.corba.se.pept.transport;

import java.util.Collection;

public interface TransportManager {
   ByteBufferPool getByteBufferPool(int var1);

   OutboundConnectionCache getOutboundConnectionCache(ContactInfo var1);

   Collection getOutboundConnectionCaches();

   InboundConnectionCache getInboundConnectionCache(Acceptor var1);

   Collection getInboundConnectionCaches();

   Selector getSelector(int var1);

   void registerAcceptor(Acceptor var1);

   Collection getAcceptors();

   void unregisterAcceptor(Acceptor var1);

   void close();
}
