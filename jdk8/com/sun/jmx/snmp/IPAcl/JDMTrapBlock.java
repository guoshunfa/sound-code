package com.sun.jmx.snmp.IPAcl;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

class JDMTrapBlock extends SimpleNode {
   JDMTrapBlock(int var1) {
      super(var1);
   }

   JDMTrapBlock(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMTrapBlock(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMTrapBlock(var0, var1);
   }

   public void buildAclEntries(PrincipalImpl var1, AclImpl var2) {
   }

   public void buildInformEntries(Hashtable<InetAddress, Vector<String>> var1) {
   }
}
