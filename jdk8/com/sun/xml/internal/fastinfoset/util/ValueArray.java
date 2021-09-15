package com.sun.xml.internal.fastinfoset.util;

public abstract class ValueArray {
   public static final int DEFAULT_CAPACITY = 10;
   public static final int MAXIMUM_CAPACITY = Integer.MAX_VALUE;
   protected int _size;
   protected int _readOnlyArraySize;
   protected int _maximumCapacity;

   public int getSize() {
      return this._size;
   }

   public int getMaximumCapacity() {
      return this._maximumCapacity;
   }

   public void setMaximumCapacity(int maximumCapacity) {
      this._maximumCapacity = maximumCapacity;
   }

   public abstract void setReadOnlyArray(ValueArray var1, boolean var2);

   public abstract void clear();
}
