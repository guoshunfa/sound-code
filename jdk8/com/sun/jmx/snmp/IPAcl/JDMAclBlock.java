package com.sun.jmx.snmp.IPAcl;

import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Vector;

class JDMAclBlock extends SimpleNode {
   JDMAclBlock(int var1) {
      super(var1);
   }

   JDMAclBlock(Parser var1, int var2) {
      super(var1, var2);
   }

   public static Node jjtCreate(int var0) {
      return new JDMAclBlock(var0);
   }

   public static Node jjtCreate(Parser var0, int var1) {
      return new JDMAclBlock(var0, var1);
   }

   public void buildTrapEntries(Hashtable<InetAddress, Vector<String>> var1) {
   }

   public void buildInformEntries(Hashtable<InetAddress, Vector<String>> var1) {
   }
}
