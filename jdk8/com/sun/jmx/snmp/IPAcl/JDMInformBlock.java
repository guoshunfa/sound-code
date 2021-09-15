package com.sun.jmx.snmp.IPAcl;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

class JDMInformBlock extends SimpleNode {
   JDMInformBlock(int var1) {
      super(var1);
   }

   JDMInformBlock(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMInformBlock(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMInformBlock(var0, var1);
   }

   public void buildAclEntries(PrincipalImpl var1, AclImpl var2) {
   }

   public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> var1) {
   }
}
