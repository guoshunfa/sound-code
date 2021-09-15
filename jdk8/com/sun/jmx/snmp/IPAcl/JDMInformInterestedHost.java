package com.sun.jmx.snmp.IPAcl;

class JDMInformInterestedHost extends SimpleNode {
   JDMInformInterestedHost(int var1) {
      super(var1);
   }

   JDMInformInterestedHost(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMInformInterestedHost(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMInformInterestedHost(var0, var1);
   }
}
