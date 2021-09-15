package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyActive extends UserException {
   public int serverId = 0;

   public ServerAlreadyActive() {
      super(ServerAlreadyActiveHelper.id());
   }

   public ServerAlreadyActive(int var1) {
      super(ServerAlreadyActiveHelper.id());
      this.serverId = var1;
   }

   public ServerAlreadyActive(String var1, int var2) {
      super(ServerAlreadyActiveHelper.id() + "  " + var1);
      this.serverId = var2;
   }
}
