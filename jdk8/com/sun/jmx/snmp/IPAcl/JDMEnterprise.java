package com.sun.jmx.snmp.IPAcl;

class JDMEnterprise extends SimpleNode {
   protected String enterprise = "";

   JDMEnterprise(int var1) {
      super(var1);
   }

   JDMEnterprise(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMEnterprise(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMEnterprise(var0, var1);
   }
}
