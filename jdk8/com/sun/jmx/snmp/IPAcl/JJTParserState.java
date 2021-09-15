package com.sun.jmx.snmp.IPAcl;

import java.util.Stack;

class JJTParserState {
   private Stack<Node> nodes = new Stack();
   private Stack<Integer> marks = new Stack();
   private int sp = 0;
   private int mk = 0;
   private boolean node_created;

   boolean nodeCreated() {
      return this.node_created;
   }

   void reset() {
      this.nodes.removeAllElements();
      this.marks.removeAllElements();
      this.sp = 0;
      this.mk = 0;
   }

   Node rootNode() {
      return (Node)this.nodes.elementAt(0);
   }

   void pushNode(Node var1) {
      this.nodes.push(var1);
      ++this.sp;
   }

   Node popNode() {
      if (--this.sp < this.mk) {
         this.mk = (Integer)this.marks.pop();
      }

      return (Node)this.nodes.pop();
   }

   Node peekNode() {
      return (Node)this.nodes.peek();
   }

   int nodeArity() {
      return this.sp - this.mk;
   }

   void clearNodeScope(Node var1) {
      while(this.sp > this.mk) {
         this.popNode();
      }

      this.mk = (Integer)this.marks.pop();
   }

   void openNodeScope(Node var1) {
      this.marks.push(new Integer(this.mk));
      this.mk = this.sp;
      var1.jjtOpen();
   }

   void closeNodeScope(Node var1, int var2) {
      this.mk = (Integer)this.marks.pop();

      while(var2-- > 0) {
         Node var3 = this.popNode();
         var3.jjtSetParent(var1);
         var1.jjtAddChild(var3, var2);
      }

      var1.jjtClose();
      this.pushNode(var1);
      this.node_created = true;
   }

   void closeNodeScope(Node var1, boolean var2) {
      if (var2) {
         int var3 = this.nodeArity();
         this.mk = (Integer)this.marks.pop();

         while(var3-- > 0) {
            Node var4 = this.popNode();
            var4.jjtSetParent(var1);
            var1.jjtAddChild(var4, var3);
         }

         var1.jjtClose();
         this.pushNode(var1);
         this.node_created = true;
      } else {
         this.mk = (Integer)this.marks.pop();
         this.node_created = false;
      }

   }
}
