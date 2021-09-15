package org.omg.PortableServer;

import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

public interface POAManagerOperations {
   void activate() throws AdapterInactive;

   void hold_requests(boolean var1) throws AdapterInactive;

   void discard_requests(boolean var1) throws AdapterInactive;

   void deactivate(boolean var1, boolean var2) throws AdapterInactive;

   State get_state();
}
