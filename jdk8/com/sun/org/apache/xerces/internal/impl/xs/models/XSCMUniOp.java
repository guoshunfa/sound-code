package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;

public class XSCMUniOp extends CMNode {
   private CMNode fChild;

   public XSCMUniOp(int type, CMNode childNode) {
      super(type);
      if (this.type() != 5 && this.type() != 4 && this.type() != 6) {
         throw new RuntimeException("ImplementationMessages.VAL_UST");
      } else {
         this.fChild = childNode;
      }
   }

   final CMNode getChild() {
      return this.fChild;
   }

   public boolean isNullable() {
      return this.type() == 6 ? this.fChild.isNullable() : true;
   }

   protected void calcFirstPos(CMStateSet toSet) {
      toSet.setTo(this.fChild.firstPos());
   }

   protected void calcLastPos(CMStateSet toSet) {
      toSet.setTo(this.fChild.lastPos());
   }

   public void setUserData(Object userData) {
      super.setUserData(userData);
      this.fChild.setUserData(userData);
   }
}
