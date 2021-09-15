package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.UserException;

public final class WrongAdapter extends UserException {
   public WrongAdapter() {
      super(WrongAdapterHelper.id());
   }

   public WrongAdapter(String var1) {
      super(WrongAdapterHelper.id() + "  " + var1);
   }
}
