package org.omg.PortableServer.CurrentPackage;

import org.omg.CORBA.UserException;

public final class NoContext extends UserException {
   public NoContext() {
      super(NoContextHelper.id());
   }

   public NoContext(String var1) {
      super(NoContextHelper.id() + "  " + var1);
   }
}
