package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyUninstalled extends UserException {
   public int serverId = 0;

   public ServerAlreadyUninstalled() {
      super(ServerAlreadyUninstalledHelper.id());
   }

   public ServerAlreadyUninstalled(int var1) {
      super(ServerAlreadyUninstalledHelper.id());
      this.serverId = var1;
   }

   public ServerAlreadyUninstalled(String var1, int var2) {
      super(ServerAlreadyUninstalledHelper.id() + "  " + var1);
      this.serverId = var2;
   }
}
