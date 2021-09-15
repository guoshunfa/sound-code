package com.sun.jmx.snmp.IPAcl;

class JDMAccess extends SimpleNode {
   protected int access = -1;

   JDMAccess(int var1) {
      super(var1);
   }

   JDMAccess(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMAccess(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMAccess(var0, var1);
   }

   protected void putPermission(AclEntryImpl var1) {
      if (this.access == 17) {
         var1.addPermission(SnmpAcl.getREAD());
      }

      if (this.access == 18) {
         var1.addPermission(SnmpAcl.getREAD());
         var1.addPermission(SnmpAcl.getWRITE());
      }

   }
}
