package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.orbutil.closure.Closure;

public final class IIOPAddressClosureImpl extends IIOPAddressBase {
   private Closure host;
   private Closure port;

   public IIOPAddressClosureImpl(Closure var1, Closure var2) {
      this.host = var1;
      this.port = var2;
   }

   public String getHost() {
      return (String)((String)this.host.evaluate());
   }

   public int getPort() {
      Integer var1 = (Integer)((Integer)this.port.evaluate());
      return var1;
   }
}
