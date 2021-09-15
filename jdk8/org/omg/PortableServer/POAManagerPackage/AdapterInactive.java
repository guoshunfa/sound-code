package org.omg.PortableServer.POAManagerPackage;

import org.omg.CORBA.UserException;

public final class AdapterInactive extends UserException {
   public AdapterInactive() {
      super(AdapterInactiveHelper.id());
   }

   public AdapterInactive(String var1) {
      super(AdapterInactiveHelper.id() + "  " + var1);
   }
}
