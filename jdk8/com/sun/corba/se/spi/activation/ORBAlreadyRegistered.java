package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ORBAlreadyRegistered extends UserException {
   public String orbId = null;

   public ORBAlreadyRegistered() {
      super(ORBAlreadyRegisteredHelper.id());
   }

   public ORBAlreadyRegistered(String var1) {
      super(ORBAlreadyRegisteredHelper.id());
      this.orbId = var1;
   }

   public ORBAlreadyRegistered(String var1, String var2) {
      super(ORBAlreadyRegisteredHelper.id() + "  " + var1);
      this.orbId = var2;
   }
}
