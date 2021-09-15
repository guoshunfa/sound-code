package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ObjectNotActive extends UserException {
   public ObjectNotActive() {
      super(ObjectNotActiveHelper.id());
   }

   public ObjectNotActive(String var1) {
      super(ObjectNotActiveHelper.id() + "  " + var1);
   }
}
