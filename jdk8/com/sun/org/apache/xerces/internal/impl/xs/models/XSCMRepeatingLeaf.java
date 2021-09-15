package com.sun.org.apache.xerces.internal.impl.xs.models;

public final class XSCMRepeatingLeaf extends XSCMLeaf {
   private final int fMinOccurs;
   private final int fMaxOccurs;

   public XSCMRepeatingLeaf(int type, Object leaf, int minOccurs, int maxOccurs, int id, int position) {
      super(type, leaf, id, position);
      this.fMinOccurs = minOccurs;
      this.fMaxOccurs = maxOccurs;
   }

   final int getMinOccurs() {
      return this.fMinOccurs;
   }

   final int getMaxOccurs() {
      return this.fMaxOccurs;
   }
}
