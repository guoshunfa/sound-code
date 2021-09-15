package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;

public final class NotFound extends UserException {
   public NotFoundReason why = null;
   public NameComponent[] rest_of_name = null;

   public NotFound() {
      super(NotFoundHelper.id());
   }

   public NotFound(NotFoundReason var1, NameComponent[] var2) {
      super(NotFoundHelper.id());
      this.why = var1;
      this.rest_of_name = var2;
   }

   public NotFound(String var1, NotFoundReason var2, NameComponent[] var3) {
      super(NotFoundHelper.id() + "  " + var1);
      this.why = var2;
      this.rest_of_name = var3;
   }
}
