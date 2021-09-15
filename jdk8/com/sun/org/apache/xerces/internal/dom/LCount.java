package com.sun.org.apache.xerces.internal.dom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class LCount {
   static final Map<String, LCount> lCounts = new ConcurrentHashMap();
   public int captures = 0;
   public int bubbles = 0;
   public int defaults;
   public int total = 0;

   static LCount lookup(String evtName) {
      LCount lc = (LCount)lCounts.get(evtName);
      if (lc == null) {
         lCounts.put(evtName, lc = new LCount());
      }

      return lc;
   }
}
