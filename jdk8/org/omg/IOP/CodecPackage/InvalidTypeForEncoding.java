package org.omg.IOP.CodecPackage;

import org.omg.CORBA.UserException;

public final class InvalidTypeForEncoding extends UserException {
   public InvalidTypeForEncoding() {
      super(InvalidTypeForEncodingHelper.id());
   }

   public InvalidTypeForEncoding(String var1) {
      super(InvalidTypeForEncodingHelper.id() + "  " + var1);
   }
}
