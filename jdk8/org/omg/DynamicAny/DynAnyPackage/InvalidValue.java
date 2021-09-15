package org.omg.DynamicAny.DynAnyPackage;

import org.omg.CORBA.UserException;

public final class InvalidValue extends UserException {
   public InvalidValue() {
      super(InvalidValueHelper.id());
   }

   public InvalidValue(String var1) {
      super(InvalidValueHelper.id() + "  " + var1);
   }
}
