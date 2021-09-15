package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.transport.SocketInfo;

public class EndPointInfoImpl implements SocketInfo, LegacyServerSocketEndPointInfo {
   protected String type;
   protected String hostname;
   protected int port;
   protected int locatorPort;
   protected String name;

   public EndPointInfoImpl(String var1, int var2, String var3) {
      this.type = var1;
      this.port = var2;
      this.hostname = var3;
      this.locatorPort = -1;
      this.name = "NO_NAME";
   }

   public String getType() {
      return this.type;
   }

   public String getHost() {
      return this.hostname;
   }

   public String getHostName() {
      return this.hostname;
   }

   public int getPort() {
      return this.port;
   }

   public int getLocatorPort() {
      return this.locatorPort;
   }

   public void setLocatorPort(int var1) {
      this.locatorPort = var1;
   }

   public String getName() {
      return this.name;
   }

   public int hashCode() {
      return this.type.hashCode() ^ this.hostname.hashCode() ^ this.port;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof EndPointInfoImpl)) {
         return false;
      } else {
         EndPointInfoImpl var2 = (EndPointInfoImpl)var1;
         if (this.type == null) {
            if (var2.type != null) {
               return false;
            }
         } else if (!this.type.equals(var2.type)) {
            return false;
         }

         if (this.port != var2.port) {
            return false;
         } else {
            return this.hostname.equals(var2.hostname);
         }
      }
   }

   public String toString() {
      return this.type + " " + this.name + " " + this.hostname + " " + this.port;
   }
}
