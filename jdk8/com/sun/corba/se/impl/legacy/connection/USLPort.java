package com.sun.corba.se.impl.legacy.connection;

public class USLPort {
   private String type;
   private int port;

   public USLPort(String var1, int var2) {
      this.type = var1;
      this.port = var2;
   }

   public String getType() {
      return this.type;
   }

   public int getPort() {
      return this.port;
   }

   public String toString() {
      return this.type + ":" + this.port;
   }
}
