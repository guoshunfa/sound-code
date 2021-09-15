package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;

public class XSCMLeaf extends CMNode {
   private Object fLeaf = null;
   private int fParticleId = -1;
   private int fPosition = -1;

   public XSCMLeaf(int type, Object leaf, int id, int position) {
      super(type);
      this.fLeaf = leaf;
      this.fParticleId = id;
      this.fPosition = position;
   }

   final Object getLeaf() {
      return this.fLeaf;
   }

   final int getParticleId() {
      return this.fParticleId;
   }

   final int getPosition() {
      return this.fPosition;
   }

   final void setPosition(int newPosition) {
      this.fPosition = newPosition;
   }

   public boolean isNullable() {
      return this.fPosition == -1;
   }

   public String toString() {
      StringBuffer strRet = new StringBuffer(this.fLeaf.toString());
      if (this.fPosition >= 0) {
         strRet.append(" (Pos:" + Integer.toString(this.fPosition) + ")");
      }

      return strRet.toString();
   }

   protected void calcFirstPos(CMStateSet toSet) {
      if (this.fPosition == -1) {
         toSet.zeroBits();
      } else {
         toSet.setBit(this.fPosition);
      }

   }

   protected void calcLastPos(CMStateSet toSet) {
      if (this.fPosition == -1) {
         toSet.zeroBits();
      } else {
         toSet.setBit(this.fPosition);
      }

   }
}
