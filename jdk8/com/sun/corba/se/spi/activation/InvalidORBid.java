package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class InvalidORBid extends UserException {
   public InvalidORBid() {
      super(InvalidORBidHelper.id());
   }

   public InvalidORBid(String var1) {
      super(InvalidORBidHelper.id() + "  " + var1);
   }
}
