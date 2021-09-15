package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class FormatMismatch extends UserException {
   public FormatMismatch() {
      super(FormatMismatchHelper.id());
   }

   public FormatMismatch(String var1) {
      super(FormatMismatchHelper.id() + "  " + var1);
   }
}
