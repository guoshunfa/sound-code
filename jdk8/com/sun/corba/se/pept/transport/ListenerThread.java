package com.sun.corba.se.pept.transport;

public interface ListenerThread {
   Acceptor getAcceptor();

   void close();
}
