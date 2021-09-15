package com.sun.jmx.snmp.IPAcl;

class JDMCommunity extends SimpleNode {
   protected String communityString = "";

   JDMCommunity(int var1) {
      super(var1);
   }

   JDMCommunity(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMCommunity(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMCommunity(var0, var1);
   }

   public String getCommunity() {
      return this.communityString;
   }
}
