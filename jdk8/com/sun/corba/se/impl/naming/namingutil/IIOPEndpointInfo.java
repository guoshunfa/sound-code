package com.sun.corba.se.impl.naming.namingutil;

public class IIOPEndpointInfo {
   private int major = 1;
   private int minor = 0;
   private String host = "localhost";
   private int port = 2089;

   IIOPEndpointInfo() {
   }

   public void setHost(String var1) {
      this.host = var1;
   }

   public String getHost() {
      return this.host;
   }

   public void setPort(int var1) {
      this.port = var1;
   }

   public int getPort() {
      return this.port;
   }

   public void setVersion(int var1, int var2) {
      this.major = var1;
      this.minor = var2;
   }

   public int getMajor() {
      return this.major;
   }

   public int getMinor() {
      return this.minor;
   }

   public void dump() {
      System.out.println(" Major -> " + this.major + " Minor -> " + this.minor);
      System.out.println("host -> " + this.host);
      System.out.println("port -> " + this.port);
   }
}
