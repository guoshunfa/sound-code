package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantNotActive extends UserException {
   public ServantNotActive() {
      super(ServantNotActiveHelper.id());
   }

   public ServantNotActive(String var1) {
      super(ServantNotActiveHelper.id() + "  " + var1);
   }
}
