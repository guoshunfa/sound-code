package com.sun.jmx.snmp.IPAcl;

class JDMHostInform extends SimpleNode {
   protected String name = "";

   JDMHostInform(int var1) {
      super(var1);
   }

   JDMHostInform(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMHostInform(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMHostInform(var0, var1);
   }
}
