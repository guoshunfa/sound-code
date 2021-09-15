package com.sun.corba.se.pept.transport;

public interface OutboundConnectionCache extends ConnectionCache {
   Connection get(ContactInfo var1);

   void put(ContactInfo var1, Connection var2);

   void remove(ContactInfo var1);
}
