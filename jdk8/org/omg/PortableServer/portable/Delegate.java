package org.omg.PortableServer.portable;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public interface Delegate {
   ORB orb(Servant var1);

   Object this_object(Servant var1);

   POA poa(Servant var1);

   byte[] object_id(Servant var1);

   POA default_POA(Servant var1);

   boolean is_a(Servant var1, String var2);

   boolean non_existent(Servant var1);

   Object get_interface_def(Servant var1);
}
