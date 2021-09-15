package com.sun.jmx.snmp.IPAcl;

class JDMInformCommunity extends SimpleNode {
   protected String community = "";

   JDMInformCommunity(int var1) {
      super(var1);
   }

   JDMInformCommunity(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMInformCommunity(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMInformCommunity(var0, var1);
   }

   public String getCommunity() {
      return this.community;
   }
}
