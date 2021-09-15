package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.lang.reflect.Array;
import java.util.AbstractList;

public final class ObjectListImpl extends AbstractList implements ObjectList {
   public static final ObjectListImpl EMPTY_LIST = new ObjectListImpl(new Object[0], 0);
   private final Object[] fArray;
   private final int fLength;

   public ObjectListImpl(Object[] array, int length) {
      this.fArray = array;
      this.fLength = length;
   }

   public int getLength() {
      return this.fLength;
   }

   public boolean contains(Object item) {
      int i;
      if (item == null) {
         for(i = 0; i < this.fLength; ++i) {
            if (this.fArray[i] == null) {
               return true;
            }
         }
      } else {
         for(i = 0; i < this.fLength; ++i) {
            if (item.equals(this.fArray[i])) {
               return true;
            }
         }
      }

      return false;
   }

   public Object item(int index) {
      return index >= 0 && index < this.fLength ? this.fArray[index] : null;
   }

   public Object get(int index) {
      if (index >= 0 && index < this.fLength) {
         return this.fArray[index];
      } else {
         throw new IndexOutOfBoundsException("Index: " + index);
      }
   }

   public int size() {
      return this.getLength();
   }

   public Object[] toArray() {
      Object[] a = new Object[this.fLength];
      this.toArray0(a);
      return a;
   }

   public Object[] toArray(Object[] a) {
      if (a.length < this.fLength) {
         Class arrayClass = a.getClass();
         Class componentType = arrayClass.getComponentType();
         a = (Object[])((Object[])Array.newInstance(componentType, this.fLength));
      }

      this.toArray0(a);
      if (a.length > this.fLength) {
         a[this.fLength] = null;
      }

      return a;
   }

   private void toArray0(Object[] a) {
      if (this.fLength > 0) {
         System.arraycopy(this.fArray, 0, a, 0, this.fLength);
      }

   }
}
