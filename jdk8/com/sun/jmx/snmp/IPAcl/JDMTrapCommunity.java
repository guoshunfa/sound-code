package com.sun.jmx.snmp.IPAcl;

class JDMTrapCommunity extends SimpleNode {
   protected String community = "";

   JDMTrapCommunity(int var1) {
      super(var1);
   }

   JDMTrapCommunity(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMTrapCommunity(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMTrapCommunity(var0, var1);
   }

   public String getCommunity() {
      return this.community;
   }
}
