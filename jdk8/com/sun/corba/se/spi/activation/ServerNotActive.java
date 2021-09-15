package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotActive extends UserException {
   public int serverId = 0;

   public ServerNotActive() {
      super(ServerNotActiveHelper.id());
   }

   public ServerNotActive(int var1) {
      super(ServerNotActiveHelper.id());
      this.serverId = var1;
   }

   public ServerNotActive(String var1, int var2) {
      super(ServerNotActiveHelper.id() + "  " + var1);
      this.serverId = var2;
   }
}
