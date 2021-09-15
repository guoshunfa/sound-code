package com.sun.jmx.snmp.IPAcl;

class JDMCommunities extends SimpleNode {
   JDMCommunities(int var1) {
      super(var1);
   }

   JDMCommunities(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMCommunities(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMCommunities(var0, var1);
   }

   public void buildCommunities(AclEntryImpl var1) {
      for(int var2 = 0; var2 < this.children.length; ++var2) {
         var1.addCommunity(((JDMCommunity)this.children[var2]).getCommunity());
      }

   }
}
