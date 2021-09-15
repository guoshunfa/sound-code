package com.sun.jmx.snmp.daemon;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.agent.SnmpMibAgent;
import java.util.Enumeration;
import java.util.Vector;

final class SnmpMibTree {
   private SnmpMibAgent defaultAgent = null;
   private SnmpMibTree.TreeNode root = new SnmpMibTree.TreeNode(-1L, (SnmpMibAgent)null, (SnmpMibTree.TreeNode)null);

   public SnmpMibTree() {
   }

   public void setDefaultAgent(SnmpMibAgent var1) {
      this.defaultAgent = var1;
      this.root.agent = var1;
   }

   public SnmpMibAgent getDefaultAgent() {
      return this.defaultAgent;
   }

   public void register(SnmpMibAgent var1) {
      this.root.registerNode(var1);
   }

   public void register(SnmpMibAgent var1, long[] var2) {
      this.root.registerNode(var2, 0, var1);
   }

   public SnmpMibAgent getAgentMib(SnmpOid var1) {
      SnmpMibTree.TreeNode var2 = this.root.retrieveMatchingBranch(var1.longValue(), 0);
      if (var2 == null) {
         return this.defaultAgent;
      } else {
         return var2.getAgentMib() == null ? this.defaultAgent : var2.getAgentMib();
      }
   }

   public void unregister(SnmpMibAgent var1, SnmpOid[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         long[] var4 = var2[var3].longValue();
         SnmpMibTree.TreeNode var5 = this.root.retrieveMatchingBranch(var4, 0);
         if (var5 != null) {
            var5.removeAgent(var1);
         }
      }

   }

   public void unregister(SnmpMibAgent var1) {
      this.root.removeAgentFully(var1);
   }

   public void printTree() {
      this.root.printTree(">");
   }

   final class TreeNode {
      private Vector<SnmpMibTree.TreeNode> children;
      private Vector<SnmpMibAgent> agents;
      private long nodeValue;
      private SnmpMibAgent agent;
      private SnmpMibTree.TreeNode parent;

      void registerNode(SnmpMibAgent var1) {
         long[] var2 = var1.getRootOid();
         this.registerNode(var2, 0, var1);
      }

      SnmpMibTree.TreeNode retrieveMatchingBranch(long[] var1, int var2) {
         SnmpMibTree.TreeNode var3 = this.retrieveChild(var1, var2);
         if (var3 == null) {
            return this;
         } else if (this.children.isEmpty()) {
            return var3;
         } else if (var2 + 1 == var1.length) {
            return var3;
         } else {
            SnmpMibTree.TreeNode var4 = var3.retrieveMatchingBranch(var1, var2 + 1);
            return var4.agent == null ? this : var4;
         }
      }

      SnmpMibAgent getAgentMib() {
         return this.agent;
      }

      public void printTree(String var1) {
         StringBuilder var2 = new StringBuilder();
         if (this.agents != null) {
            Enumeration var3 = this.agents.elements();

            while(var3.hasMoreElements()) {
               SnmpMibAgent var4 = (SnmpMibAgent)var3.nextElement();
               if (var4 == null) {
                  var2.append("empty ");
               } else {
                  var2.append(var4.getMibName()).append(" ");
               }
            }

            var1 = var1 + " ";
            if (this.children != null) {
               var3 = this.children.elements();

               while(var3.hasMoreElements()) {
                  SnmpMibTree.TreeNode var5 = (SnmpMibTree.TreeNode)var3.nextElement();
                  var5.printTree(var1);
               }

            }
         }
      }

      private TreeNode(long var2, SnmpMibAgent var4, SnmpMibTree.TreeNode var5) {
         this.children = new Vector();
         this.agents = new Vector();
         this.nodeValue = var2;
         this.parent = var5;
         this.agents.addElement(var4);
      }

      private void removeAgentFully(SnmpMibAgent var1) {
         Vector var2 = new Vector();
         Enumeration var3 = this.children.elements();

         while(var3.hasMoreElements()) {
            SnmpMibTree.TreeNode var4 = (SnmpMibTree.TreeNode)var3.nextElement();
            var4.removeAgentFully(var1);
            if (var4.agents.isEmpty()) {
               var2.add(var4);
            }
         }

         var3 = var2.elements();

         while(var3.hasMoreElements()) {
            this.children.removeElement(var3.nextElement());
         }

         this.removeAgent(var1);
      }

      private void removeAgent(SnmpMibAgent var1) {
         if (this.agents.contains(var1)) {
            this.agents.removeElement(var1);
            if (!this.agents.isEmpty()) {
               this.agent = (SnmpMibAgent)this.agents.firstElement();
            }

         }
      }

      private void setAgent(SnmpMibAgent var1) {
         this.agent = var1;
      }

      private void registerNode(long[] var1, int var2, SnmpMibAgent var3) {
         if (var2 < var1.length) {
            SnmpMibTree.TreeNode var4 = this.retrieveChild(var1, var2);
            if (var4 == null) {
               long var5 = var1[var2];
               var4 = SnmpMibTree.this.new TreeNode(var5, var3, this);
               this.children.addElement(var4);
            } else if (!this.agents.contains(var3)) {
               this.agents.addElement(var3);
            }

            if (var2 == var1.length - 1) {
               var4.setAgent(var3);
            } else {
               var4.registerNode(var1, var2 + 1, var3);
            }

         }
      }

      private SnmpMibTree.TreeNode retrieveChild(long[] var1, int var2) {
         long var3 = var1[var2];
         Enumeration var5 = this.children.elements();

         SnmpMibTree.TreeNode var6;
         do {
            if (!var5.hasMoreElements()) {
               return null;
            }

            var6 = (SnmpMibTree.TreeNode)var5.nextElement();
         } while(!var6.match(var3));

         return var6;
      }

      private boolean match(long var1) {
         return this.nodeValue == var1;
      }

      // $FF: synthetic method
      TreeNode(long var2, SnmpMibAgent var4, SnmpMibTree.TreeNode var5, Object var6) {
         this(var2, var4, var5);
      }
   }
}
