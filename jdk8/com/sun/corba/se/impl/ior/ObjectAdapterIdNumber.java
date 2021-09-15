package com.sun.corba.se.impl.ior;

public class ObjectAdapterIdNumber extends ObjectAdapterIdArray {
   private int poaid;

   public ObjectAdapterIdNumber(int var1) {
      super("OldRootPOA", Integer.toString(var1));
      this.poaid = var1;
   }

   public int getOldPOAId() {
      return this.poaid;
   }
}
