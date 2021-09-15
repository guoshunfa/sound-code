package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class NoServant extends UserException {
   public NoServant() {
      super(NoServantHelper.id());
   }

   public NoServant(String var1) {
      super(NoServantHelper.id() + "  " + var1);
   }
}
