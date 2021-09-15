package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMUniOp extends CMNode {
   private CMNode fChild;

   public CMUniOp(int type, CMNode childNode) {
      super(type);
      if (this.type() != 1 && this.type() != 2 && this.type() != 3) {
         throw new RuntimeException("ImplementationMessages.VAL_UST");
      } else {
         this.fChild = childNode;
      }
   }

   final CMNode getChild() {
      return this.fChild;
   }

   public boolean isNullable() {
      return this.type() == 3 ? this.fChild.isNullable() : true;
   }

   protected void calcFirstPos(CMStateSet toSet) {
      toSet.setTo(this.fChild.firstPos());
   }

   protected void calcLastPos(CMStateSet toSet) {
      toSet.setTo(this.fChild.lastPos());
   }
}
