package com.sun.jmx.snmp.IPAcl;

class JDMHostTrap extends SimpleNode {
   protected String name = "";

   JDMHostTrap(int var1) {
      super(var1);
   }

   JDMHostTrap(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMHostTrap(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMHostTrap(var0, var1);
   }
}
