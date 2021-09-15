package com.sun.corba.se.spi.protocol;

import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.spi.oa.ObjectAdapterFactory;
import java.util.Set;

public interface RequestDispatcherRegistry {
   void registerClientRequestDispatcher(ClientRequestDispatcher var1, int var2);

   ClientRequestDispatcher getClientRequestDispatcher(int var1);

   void registerLocalClientRequestDispatcherFactory(LocalClientRequestDispatcherFactory var1, int var2);

   LocalClientRequestDispatcherFactory getLocalClientRequestDispatcherFactory(int var1);

   void registerServerRequestDispatcher(CorbaServerRequestDispatcher var1, int var2);

   CorbaServerRequestDispatcher getServerRequestDispatcher(int var1);

   void registerServerRequestDispatcher(CorbaServerRequestDispatcher var1, String var2);

   CorbaServerRequestDispatcher getServerRequestDispatcher(String var1);

   void registerObjectAdapterFactory(ObjectAdapterFactory var1, int var2);

   ObjectAdapterFactory getObjectAdapterFactory(int var1);

   Set<ObjectAdapterFactory> getObjectAdapterFactories();
}
