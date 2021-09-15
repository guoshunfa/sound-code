package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerHeldDown extends UserException {
   public int serverId = 0;

   public ServerHeldDown() {
      super(ServerHeldDownHelper.id());
   }

   public ServerHeldDown(int var1) {
      super(ServerHeldDownHelper.id());
      this.serverId = var1;
   }

   public ServerHeldDown(String var1, int var2) {
      super(ServerHeldDownHelper.id() + "  " + var1);
      this.serverId = var2;
   }
}
