package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.ORBPortInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocation implements IDLEntity {
   public String hostname = null;
   public ORBPortInfo[] ports = null;

   public ServerLocation() {
   }

   public ServerLocation(String var1, ORBPortInfo[] var2) {
      this.hostname = var1;
      this.ports = var2;
   }
}
