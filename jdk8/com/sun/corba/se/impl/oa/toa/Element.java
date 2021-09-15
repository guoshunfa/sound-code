package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;

final class Element {
   Object servant = null;
   Object servantData = null;
   int index = -1;
   int counter = 0;
   boolean valid = false;

   Element(int var1, Object var2) {
      this.servant = var2;
      this.index = var1;
   }

   byte[] getKey(Object var1, Object var2) {
      this.servant = var1;
      this.servantData = var2;
      this.valid = true;
      return this.toBytes();
   }

   byte[] toBytes() {
      byte[] var1 = new byte[8];
      ORBUtility.intToBytes(this.index, var1, 0);
      ORBUtility.intToBytes(this.counter, var1, 4);
      return var1;
   }

   void delete(Element var1) {
      if (this.valid) {
         ++this.counter;
         this.servantData = null;
         this.valid = false;
         this.servant = var1;
      }
   }

   public String toString() {
      return "Element[" + this.index + ", " + this.counter + "]";
   }
}
