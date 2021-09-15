package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyRegistered extends UserException {
   public int serverId = 0;

   public ServerAlreadyRegistered() {
      super(ServerAlreadyRegisteredHelper.id());
   }

   public ServerAlreadyRegistered(int var1) {
      super(ServerAlreadyRegisteredHelper.id());
      this.serverId = var1;
   }

   public ServerAlreadyRegistered(String var1, int var2) {
      super(ServerAlreadyRegisteredHelper.id() + "  " + var1);
      this.serverId = var2;
   }
}
