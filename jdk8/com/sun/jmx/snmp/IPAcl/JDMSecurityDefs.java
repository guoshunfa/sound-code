package com.sun.jmx.snmp.IPAcl;

class JDMSecurityDefs extends SimpleNode {
   JDMSecurityDefs(int var1) {
      super(var1);
   }

   JDMSecurityDefs(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMSecurityDefs(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMSecurityDefs(var0, var1);
   }
}
