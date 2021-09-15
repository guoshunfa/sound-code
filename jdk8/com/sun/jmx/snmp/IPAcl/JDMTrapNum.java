package com.sun.jmx.snmp.IPAcl;

class JDMTrapNum extends SimpleNode {
   protected int low = 0;
   protected int high = 0;

   JDMTrapNum(int var1) {
      super(var1);
   }

   JDMTrapNum(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMTrapNum(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMTrapNum(var0, var1);
   }
}
