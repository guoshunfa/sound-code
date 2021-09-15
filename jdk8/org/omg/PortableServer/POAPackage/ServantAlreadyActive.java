package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ServantAlreadyActive extends UserException {
   public ServantAlreadyActive() {
      super(ServantAlreadyActiveHelper.id());
   }

   public ServantAlreadyActive(String var1) {
      super(ServantAlreadyActiveHelper.id() + "  " + var1);
   }
}
