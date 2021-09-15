package com.sun.jndi.dns;

import java.util.Hashtable;

class NameNode {
   private String label;
   private Hashtable<String, NameNode> children = null;
   private boolean isZoneCut = false;
   private int depth = 0;

   NameNode(String var1) {
      this.label = var1;
   }

   protected NameNode newNameNode(String var1) {
      return new NameNode(var1);
   }

   String getLabel() {
      return this.label;
   }

   int depth() {
      return this.depth;
   }

   boolean isZoneCut() {
      return this.isZoneCut;
   }

   void setZoneCut(boolean var1) {
      this.isZoneCut = var1;
   }

   Hashtable<String, NameNode> getChildren() {
      return this.children;
   }

   NameNode get(String var1) {
      return this.children != null ? (NameNode)this.children.get(var1) : null;
   }

   NameNode get(DnsName var1, int var2) {
      NameNode var3 = this;

      for(int var4 = var2; var4 < var1.size() && var3 != null; ++var4) {
         var3 = var3.get(var1.getKey(var4));
      }

      return var3;
   }

   NameNode add(DnsName var1, int var2) {
      NameNode var3 = this;

      for(int var4 = var2; var4 < var1.size(); ++var4) {
         String var5 = var1.get(var4);
         String var6 = var1.getKey(var4);
         NameNode var7 = null;
         if (var3.children == null) {
            var3.children = new Hashtable();
         } else {
            var7 = (NameNode)var3.children.get(var6);
         }

         if (var7 == null) {
            var7 = this.newNameNode(var5);
            var7.depth = var3.depth + 1;
            var3.children.put(var6, var7);
         }

         var3 = var7;
      }

      return var3;
   }
}
