package com.sun.jmx.snmp.IPAcl;

class JDMManagers extends SimpleNode {
   JDMManagers(int var1) {
      super(var1);
   }

   JDMManagers(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMManagers(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMManagers(var0, var1);
   }
}
