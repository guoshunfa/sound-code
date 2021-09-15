package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.portable.IDLEntity;

public final class ServerDef implements IDLEntity {
   public String applicationName = null;
   public String serverName = null;
   public String serverClassPath = null;
   public String serverArgs = null;
   public String serverVmArgs = null;

   public ServerDef() {
   }

   public ServerDef(String var1, String var2, String var3, String var4, String var5) {
      this.applicationName = var1;
      this.serverName = var2;
      this.serverClassPath = var3;
      this.serverArgs = var4;
      this.serverVmArgs = var5;
   }
}
