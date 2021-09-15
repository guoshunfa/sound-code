package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import java.util.HashMap;
import java.util.Map;

class LargeContainer extends Container {
   Map items;

   LargeContainer(int size) {
      this.items = new HashMap(size * 2 + 1);
      this.values = new OneAttr[size];
   }

   void put(String key, OneAttr value) {
      this.items.put(key, value);
      this.values[this.pos++] = value;
   }

   OneAttr get(String key) {
      OneAttr ret = (OneAttr)this.items.get(key);
      return ret;
   }
}
