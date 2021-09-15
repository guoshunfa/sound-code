package org.omg.PortableServer;

import org.omg.PortableServer.CurrentPackage.NoContext;

public interface CurrentOperations extends org.omg.CORBA.CurrentOperations {
   POA get_POA() throws NoContext;

   byte[] get_object_id() throws NoContext;
}
