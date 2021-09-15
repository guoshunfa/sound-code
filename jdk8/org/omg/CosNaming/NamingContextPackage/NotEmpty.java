package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;

public final class NotEmpty extends UserException {
   public NotEmpty() {
      super(NotEmptyHelper.id());
   }

   public NotEmpty(String var1) {
      super(NotEmptyHelper.id() + "  " + var1);
   }
}
