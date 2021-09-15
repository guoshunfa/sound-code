package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyInstalled extends UserException {
   public int serverId = 0;

   public ServerAlreadyInstalled() {
      super(ServerAlreadyInstalledHelper.id());
   }

   public ServerAlreadyInstalled(int var1) {
      super(ServerAlreadyInstalledHelper.id());
      this.serverId = var1;
   }

   public ServerAlreadyInstalled(String var1, int var2) {
      super(ServerAlreadyInstalledHelper.id() + "  " + var1);
      this.serverId = var2;
   }
}
