package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.StringList;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Vector;

public final class StringListImpl extends AbstractList implements StringList {
   public static final StringListImpl EMPTY_LIST = new StringListImpl(new String[0], 0);
   private final String[] fArray;
   private final int fLength;
   private final Vector fVector;

   public StringListImpl(Vector v) {
      this.fVector = v;
      this.fLength = v == null ? 0 : v.size();
      this.fArray = null;
   }

   public StringListImpl(String[] array, int length) {
      this.fArray = array;
      this.fLength = length;
      this.fVector = null;
   }

   public int getLength() {
      return this.fLength;
   }

   public boolean contains(String item) {
      if (this.fVector != null) {
         return this.fVector.contains(item);
      } else {
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
   }

   public String item(int index) {
      if (index >= 0 && index < this.fLength) {
         return this.fVector != null ? (String)this.fVector.elementAt(index) : this.fArray[index];
      } else {
         return null;
      }
   }

   public Object get(int index) {
      if (index >= 0 && index < this.fLength) {
         return this.fVector != null ? this.fVector.elementAt(index) : this.fArray[index];
      } else {
         throw new IndexOutOfBoundsException("Index: " + index);
      }
   }

   public int size() {
      return this.getLength();
   }

   public Object[] toArray() {
      if (this.fVector != null) {
         return this.fVector.toArray();
      } else {
         Object[] a = new Object[this.fLength];
         this.toArray0(a);
         return a;
      }
   }

   public Object[] toArray(Object[] a) {
      if (this.fVector != null) {
         return this.fVector.toArray(a);
      } else {
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
   }

   private void toArray0(Object[] a) {
      if (this.fLength > 0) {
         System.arraycopy(this.fArray, 0, a, 0, this.fLength);
      }

   }
}
