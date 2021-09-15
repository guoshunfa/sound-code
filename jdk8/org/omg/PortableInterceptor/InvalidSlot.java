package org.omg.PortableInterceptor;

import org.omg.CORBA.UserException;

public final class InvalidSlot extends UserException {
   public InvalidSlot() {
      super(InvalidSlotHelper.id());
   }

   public InvalidSlot(String var1) {
      super(InvalidSlotHelper.id() + "  " + var1);
   }
}
