package com.sun.org.apache.xerces.internal.impl.dtd.models;

public abstract class CMNode {
   private int fType;
   private CMStateSet fFirstPos = null;
   private CMStateSet fFollowPos = null;
   private CMStateSet fLastPos = null;
   private int fMaxStates = -1;
   private Object fUserData = null;

   public CMNode(int type) {
      this.fType = type;
   }

   public abstract boolean isNullable();

   public final int type() {
      return this.fType;
   }

   public final CMStateSet firstPos() {
      if (this.fFirstPos == null) {
         this.fFirstPos = new CMStateSet(this.fMaxStates);
         this.calcFirstPos(this.fFirstPos);
      }

      return this.fFirstPos;
   }

   public final CMStateSet lastPos() {
      if (this.fLastPos == null) {
         this.fLastPos = new CMStateSet(this.fMaxStates);
         this.calcLastPos(this.fLastPos);
      }

      return this.fLastPos;
   }

   final void setFollowPos(CMStateSet setToAdopt) {
      this.fFollowPos = setToAdopt;
   }

   public final void setMaxStates(int maxStates) {
      this.fMaxStates = maxStates;
   }

   public void setUserData(Object userData) {
      this.fUserData = userData;
   }

   public Object getUserData() {
      return this.fUserData;
   }

   protected abstract void calcFirstPos(CMStateSet var1);

   protected abstract void calcLastPos(CMStateSet var1);
}
