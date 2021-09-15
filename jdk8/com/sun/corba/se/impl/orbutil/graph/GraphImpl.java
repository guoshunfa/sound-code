package com.sun.corba.se.impl.orbutil.graph;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GraphImpl extends AbstractSet implements Graph {
   private Map nodeToData;

   public GraphImpl() {
      this.nodeToData = new HashMap();
   }

   public GraphImpl(Collection var1) {
      this();
      this.addAll(var1);
   }

   public boolean add(Object var1) {
      if (!(var1 instanceof Node)) {
         throw new IllegalArgumentException("Graphs must contain only Node instances");
      } else {
         Node var2 = (Node)var1;
         boolean var3 = this.nodeToData.keySet().contains(var1);
         if (!var3) {
            NodeData var4 = new NodeData();
            this.nodeToData.put(var2, var4);
         }

         return !var3;
      }
   }

   public Iterator iterator() {
      return this.nodeToData.keySet().iterator();
   }

   public int size() {
      return this.nodeToData.keySet().size();
   }

   public NodeData getNodeData(Node var1) {
      return (NodeData)this.nodeToData.get(var1);
   }

   private void clearNodeData() {
      Iterator var1 = this.nodeToData.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry var2 = (Map.Entry)var1.next();
         NodeData var3 = (NodeData)((NodeData)var2.getValue());
         var3.clear();
      }

   }

   void visitAll(GraphImpl.NodeVisitor var1) {
      boolean var2 = false;

      do {
         var2 = true;
         Map.Entry[] var3 = (Map.Entry[])((Map.Entry[])this.nodeToData.entrySet().toArray(new Map.Entry[0]));

         for(int var4 = 0; var4 < var3.length; ++var4) {
            Map.Entry var5 = var3[var4];
            Node var6 = (Node)var5.getKey();
            NodeData var7 = (NodeData)var5.getValue();
            if (!var7.isVisited()) {
               var7.visited();
               var2 = false;
               var1.visit(this, var6, var7);
            }
         }
      } while(!var2);

   }

   private void markNonRoots() {
      this.visitAll(new GraphImpl.NodeVisitor() {
         public void visit(Graph var1, Node var2, NodeData var3) {
            Iterator var4 = var2.getChildren().iterator();

            while(var4.hasNext()) {
               Node var5 = (Node)var4.next();
               var1.add(var5);
               NodeData var6 = var1.getNodeData(var5);
               var6.notRoot();
            }

         }
      });
   }

   private Set collectRootSet() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.nodeToData.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         Node var4 = (Node)var3.getKey();
         NodeData var5 = (NodeData)var3.getValue();
         if (var5.isRoot()) {
            var1.add(var4);
         }
      }

      return var1;
   }

   public Set getRoots() {
      this.clearNodeData();
      this.markNonRoots();
      return this.collectRootSet();
   }

   interface NodeVisitor {
      void visit(Graph var1, Node var2, NodeData var3);
   }
}
