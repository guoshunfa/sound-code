package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.EndPointInfo;
import org.omg.CORBA.portable.IDLEntity;

public final class ServerLocationPerORB implements IDLEntity {
   public String hostname = null;
   public EndPointInfo[] ports = null;

   public ServerLocationPerORB() {
   }

   public ServerLocationPerORB(String var1, EndPointInfo[] var2) {
      this.hostname = var1;
      this.ports = var2;
   }
}
