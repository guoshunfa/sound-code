package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMBinOp extends CMNode {
   private CMNode fLeftChild;
   private CMNode fRightChild;

   public CMBinOp(int type, CMNode leftNode, CMNode rightNode) {
      super(type);
      if (this.type() != 4 && this.type() != 5) {
         throw new RuntimeException("ImplementationMessages.VAL_BST");
      } else {
         this.fLeftChild = leftNode;
         this.fRightChild = rightNode;
      }
   }

   final CMNode getLeft() {
      return this.fLeftChild;
   }

   final CMNode getRight() {
      return this.fRightChild;
   }

   public boolean isNullable() {
      if (this.type() == 4) {
         return this.fLeftChild.isNullable() || this.fRightChild.isNullable();
      } else if (this.type() != 5) {
         throw new RuntimeException("ImplementationMessages.VAL_BST");
      } else {
         return this.fLeftChild.isNullable() && this.fRightChild.isNullable();
      }
   }

   protected void calcFirstPos(CMStateSet toSet) {
      if (this.type() == 4) {
         toSet.setTo(this.fLeftChild.firstPos());
         toSet.union(this.fRightChild.firstPos());
      } else {
         if (this.type() != 5) {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
         }

         toSet.setTo(this.fLeftChild.firstPos());
         if (this.fLeftChild.isNullable()) {
            toSet.union(this.fRightChild.firstPos());
         }
      }

   }

   protected void calcLastPos(CMStateSet toSet) {
      if (this.type() == 4) {
         toSet.setTo(this.fLeftChild.lastPos());
         toSet.union(this.fRightChild.lastPos());
      } else {
         if (this.type() != 5) {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
         }

         toSet.setTo(this.fRightChild.lastPos());
         if (this.fRightChild.isNullable()) {
            toSet.union(this.fLeftChild.lastPos());
         }
      }

   }
}
