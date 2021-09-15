package com.sun.jmx.snmp.IPAcl;

class JDMAclItem extends SimpleNode {
   protected JDMAccess access = null;
   protected JDMCommunities com = null;

   JDMAclItem(int var1) {
      super(var1);
   }

   JDMAclItem(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMAclItem(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMAclItem(var0, var1);
   }

   public JDMAccess getAccess() {
      return this.access;
   }

   public JDMCommunities getCommunities() {
      return this.com;
   }
}
