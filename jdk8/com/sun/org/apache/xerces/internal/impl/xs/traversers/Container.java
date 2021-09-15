package com.sun.org.apache.xerces.internal.impl.xs.traversers;

abstract class Container {
   static final int THRESHOLD = 5;
   OneAttr[] values;
   int pos = 0;

   static Container getContainer(int size) {
      return (Container)(size > 5 ? new LargeContainer(size) : new SmallContainer(size));
   }

   abstract void put(String var1, OneAttr var2);

   abstract OneAttr get(String var1);
}
