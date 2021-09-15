package com.sun.jmx.snmp.IPAcl;

class JDMHost extends SimpleNode {
   JDMHost(int var1) {
      super(var1);
   }

   JDMHost(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMHost(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMHost(var0, var1);
   }
}
