package org.omg.PortableInterceptor.ORBInitInfoPackage;

import org.omg.CORBA.UserException;

public final class DuplicateName extends UserException {
   public String name = null;

   public DuplicateName() {
      super(DuplicateNameHelper.id());
   }

   public DuplicateName(String var1) {
      super(DuplicateNameHelper.id());
      this.name = var1;
   }

   public DuplicateName(String var1, String var2) {
      super(DuplicateNameHelper.id() + "  " + var1);
      this.name = var2;
   }
}
