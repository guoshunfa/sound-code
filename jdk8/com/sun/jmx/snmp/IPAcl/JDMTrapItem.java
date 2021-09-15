package com.sun.jmx.snmp.IPAcl;

class JDMTrapItem extends SimpleNode {
   protected JDMTrapCommunity comm = null;

   JDMTrapItem(int var1) {
      super(var1);
   }

   JDMTrapItem(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMTrapItem(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMTrapItem(var0, var1);
   }

   public JDMTrapCommunity getCommunity() {
      return this.comm;
   }
}
