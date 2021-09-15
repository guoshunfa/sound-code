package com.sun.corba.se.impl.ior;

import java.util.Arrays;
import java.util.Iterator;

public class ObjectAdapterIdArray extends ObjectAdapterIdBase {
   private final String[] objectAdapterId;

   public ObjectAdapterIdArray(String[] var1) {
      this.objectAdapterId = var1;
   }

   public ObjectAdapterIdArray(String var1, String var2) {
      this.objectAdapterId = new String[2];
      this.objectAdapterId[0] = var1;
      this.objectAdapterId[1] = var2;
   }

   public int getNumLevels() {
      return this.objectAdapterId.length;
   }

   public Iterator iterator() {
      return Arrays.asList(this.objectAdapterId).iterator();
   }

   public String[] getAdapterName() {
      return (String[])((String[])this.objectAdapterId.clone());
   }
}
