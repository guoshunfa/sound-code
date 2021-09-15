package com.sun.jmx.snmp.IPAcl;

class JDMInformItem extends SimpleNode {
   protected JDMInformCommunity comm = null;

   JDMInformItem(int var1) {
      super(var1);
   }

   JDMInformItem(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMInformItem(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMInformItem(var0, var1);
   }

   public JDMInformCommunity getCommunity() {
      return this.comm;
   }
}
