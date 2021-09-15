package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class NoSuchEndPoint extends UserException {
   public NoSuchEndPoint() {
      super(NoSuchEndPointHelper.id());
   }

   public NoSuchEndPoint(String var1) {
      super(NoSuchEndPointHelper.id() + "  " + var1);
   }
}
