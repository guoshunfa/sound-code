package com.sun.corba.se.spi.activation;

import org.omg.CORBA.portable.IDLEntity;

public final class EndPointInfo implements IDLEntity {
   public String endpointType = null;
   public int port = 0;

   public EndPointInfo() {
   }

   public EndPointInfo(String var1, int var2) {
      this.endpointType = var1;
      this.port = var2;
   }
}
