package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class ORBPortInfo implements IDLEntity {
   public String orbId = null;
   public int port = 0;

   public ORBPortInfo() {
   }

   public ORBPortInfo(String var1, int var2) {
      this.orbId = var1;
      this.port = var2;
   }
}
