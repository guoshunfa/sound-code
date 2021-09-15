package com.sun.org.apache.xerces.internal.impl.xs.util;

public final class XIntPool {
   private static final short POOL_SIZE = 10;
   private static final XInt[] fXIntPool = new XInt[10];

   public final XInt getXInt(int value) {
      return value >= 0 && value < fXIntPool.length ? fXIntPool[value] : new XInt(value);
   }

   static {
      for(int i = 0; i < 10; ++i) {
         fXIntPool[i] = new XInt(i);
      }

   }
}
