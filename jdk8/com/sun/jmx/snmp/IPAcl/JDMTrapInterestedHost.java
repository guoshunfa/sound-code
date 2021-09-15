package com.sun.jmx.snmp.IPAcl;

class JDMTrapInterestedHost extends SimpleNode {
   JDMTrapInterestedHost(int var1) {
      super(var1);
   }

   JDMTrapInterestedHost(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMTrapInterestedHost(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMTrapInterestedHost(var0, var1);
   }
}
