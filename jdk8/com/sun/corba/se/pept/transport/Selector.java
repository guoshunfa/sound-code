package com.sun.corba.se.pept.transport;

public interface Selector {
   void setTimeout(long var1);

   long getTimeout();

   void registerInterestOps(EventHandler var1);

   void registerForEvent(EventHandler var1);

   void unregisterForEvent(EventHandler var1);

   void close();
}
