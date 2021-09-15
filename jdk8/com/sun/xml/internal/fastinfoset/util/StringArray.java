package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class StringArray extends ValueArray {
   public String[] _array;
   private StringArray _readOnlyArray;
   private boolean _clear;

   public StringArray(int initialCapacity, int maximumCapacity, boolean clear) {
      this._array = new String[initialCapacity];
      this._maximumCapacity = maximumCapacity;
      this._clear = clear;
   }

   public StringArray() {
      this(10, Integer.MAX_VALUE, false);
   }

   public final void clear() {
      if (this._clear) {
         for(int i = this._readOnlyArraySize; i < this._size; ++i) {
            this._array[i] = null;
         }
      }

      this._size = this._readOnlyArraySize;
   }

   public final String[] getArray() {
      if (this._array == null) {
         return null;
      } else {
         String[] clonedArray = new String[this._array.length];
         System.arraycopy(this._array, 0, clonedArray, 0, this._array.length);
         return clonedArray;
      }
   }

   public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
      if (!(readOnlyArray instanceof StringArray)) {
         throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyArray}));
      } else {
         this.setReadOnlyArray((StringArray)readOnlyArray, clear);
      }
   }

   public final void setReadOnlyArray(StringArray readOnlyArray, boolean clear) {
      if (readOnlyArray != null) {
         this._readOnlyArray = readOnlyArray;
         this._readOnlyArraySize = readOnlyArray.getSize();
         if (clear) {
            this.clear();
         }

         this._array = this.getCompleteArray();
         this._size = this._readOnlyArraySize;
      }

   }

   public final String[] getCompleteArray() {
      if (this._readOnlyArray == null) {
         return this.getArray();
      } else {
         String[] ra = this._readOnlyArray.getCompleteArray();
         String[] a = new String[this._readOnlyArraySize + this._array.length];
         System.arraycopy(ra, 0, a, 0, this._readOnlyArraySize);
         return a;
      }
   }

   public final String get(int i) {
      return this._array[i];
   }

   public final int add(String s) {
      if (this._size == this._array.length) {
         this.resize();
      }

      this._array[this._size++] = s;
      return this._size;
   }

   protected final void resize() {
      if (this._size == this._maximumCapacity) {
         throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
      } else {
         int newSize = this._size * 3 / 2 + 1;
         if (newSize > this._maximumCapacity) {
            newSize = this._maximumCapacity;
         }

         String[] newArray = new String[newSize];
         System.arraycopy(this._array, 0, newArray, 0, this._size);
         this._array = newArray;
      }
   }
}
