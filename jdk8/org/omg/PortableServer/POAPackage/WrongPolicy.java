package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class WrongPolicy extends UserException {
   public WrongPolicy() {
      super(WrongPolicyHelper.id());
   }

   public WrongPolicy(String var1) {
      super(WrongPolicyHelper.id() + "  " + var1);
   }
}
