package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class AlreadyBound extends UserException {
   public AlreadyBound() {
      super(AlreadyBoundHelper.id());
   }

   public AlreadyBound(String var1) {
      super(AlreadyBoundHelper.id() + "  " + var1);
   }
}
