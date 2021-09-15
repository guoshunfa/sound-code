package com.sun.org.apache.xerces.internal.impl.xs.traversers;

class SmallContainer extends Container {
   String[] keys;

   SmallContainer(int size) {
      this.keys = new String[size];
      this.values = new OneAttr[size];
   }

   void put(String key, OneAttr value) {
      this.keys[this.pos] = key;
      this.values[this.pos++] = value;
   }

   OneAttr get(String key) {
      for(int i = 0; i < this.pos; ++i) {
         if (this.keys[i].equals(key)) {
            return this.values[i];
         }
      }

      return null;
   }
}
