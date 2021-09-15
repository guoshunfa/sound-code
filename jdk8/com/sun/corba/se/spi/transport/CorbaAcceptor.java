package com.sun.corba.se.spi.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.spi.ior.IORTemplate;

public interface CorbaAcceptor extends Acceptor {
   String getObjectAdapterId();

   String getObjectAdapterManagerId();

   void addToIORTemplate(IORTemplate var1, Policies var2, String var3);

   String getMonitoringName();
}
