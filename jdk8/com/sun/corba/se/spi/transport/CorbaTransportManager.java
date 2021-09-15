package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import java.util.Collection;

public interface CorbaTransportManager extends TransportManager {
   String SOCKET_OR_CHANNEL_CONNECTION_CACHE = "SocketOrChannelConnectionCache";

   Collection getAcceptors(String var1, ObjectAdapterId var2);

   void addToIORTemplate(IORTemplate var1, Policies var2, String var3, String var4, ObjectAdapterId var5);
}
