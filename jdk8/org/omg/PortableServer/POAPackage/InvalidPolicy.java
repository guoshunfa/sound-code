package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class InvalidPolicy extends UserException {
   public short index = 0;

   public InvalidPolicy() {
      super(InvalidPolicyHelper.id());
   }

   public InvalidPolicy(short var1) {
      super(InvalidPolicyHelper.id());
      this.index = var1;
   }

   public InvalidPolicy(String var1, short var2) {
      super(InvalidPolicyHelper.id() + "  " + var1);
      this.index = var2;
   }
}
