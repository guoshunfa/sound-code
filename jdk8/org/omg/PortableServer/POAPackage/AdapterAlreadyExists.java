package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class AdapterAlreadyExists extends UserException {
   public AdapterAlreadyExists() {
      super(AdapterAlreadyExistsHelper.id());
   }

   public AdapterAlreadyExists(String var1) {
      super(AdapterAlreadyExistsHelper.id() + "  " + var1);
   }
}
