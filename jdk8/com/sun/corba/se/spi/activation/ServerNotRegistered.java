package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotRegistered extends UserException {
   public int serverId = 0;

   public ServerNotRegistered() {
      super(ServerNotRegisteredHelper.id());
   }

   public ServerNotRegistered(int var1) {
      super(ServerNotRegisteredHelper.id());
      this.serverId = var1;
   }

   public ServerNotRegistered(String var1, int var2) {
      super(ServerNotRegisteredHelper.id() + "  " + var1);
      this.serverId = var2;
   }
}
