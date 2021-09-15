package com.sun.corba.se.impl.orbutil.graph;

public class NodeData {
   private boolean visited;
   private boolean root;

   public NodeData() {
      this.clear();
   }

   public void clear() {
      this.visited = false;
      this.root = true;
   }

   boolean isVisited() {
      return this.visited;
   }

   void visited() {
      this.visited = true;
   }

   boolean isRoot() {
      return this.root;
   }

   void notRoot() {
      this.root = false;
   }
}
