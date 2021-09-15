package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class ObjectAlreadyActive extends UserException {
   public ObjectAlreadyActive() {
      super(ObjectAlreadyActiveHelper.id());
   }

   public ObjectAlreadyActive(String var1) {
      super(ObjectAlreadyActiveHelper.id() + "  " + var1);
   }
}
