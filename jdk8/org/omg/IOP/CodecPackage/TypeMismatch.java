package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class TypeMismatch extends UserException {
   public TypeMismatch() {
      super(TypeMismatchHelper.id());
   }

   public TypeMismatch(String var1) {
      super(TypeMismatchHelper.id() + "  " + var1);
   }
}
