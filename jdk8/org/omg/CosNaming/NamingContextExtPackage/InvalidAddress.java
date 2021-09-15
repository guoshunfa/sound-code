package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.UserException;

public final class InvalidAddress extends UserException {
   public InvalidAddress() {
      super(InvalidAddressHelper.id());
   }

   public InvalidAddress(String var1) {
      super(InvalidAddressHelper.id() + "  " + var1);
   }
}
