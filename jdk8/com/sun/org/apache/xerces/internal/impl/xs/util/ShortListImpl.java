package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.ShortList;
import com.sun.org.apache.xerces.internal.xs.XSException;
import java.util.AbstractList;

public final class ShortListImpl extends AbstractList implements ShortList {
   public static final ShortListImpl EMPTY_LIST = new ShortListImpl(new short[0], 0);
   private final short[] fArray;
   private final int fLength;

   public ShortListImpl(short[] array, int length) {
      this.fArray = array;
      this.fLength = length;
   }

   public int getLength() {
      return this.fLength;
   }

   public boolean contains(short item) {
      for(int i = 0; i < this.fLength; ++i) {
         if (this.fArray[i] == item) {
            return true;
         }
      }

      return false;
   }

   public short item(int index) throws XSException {
      if (index >= 0 && index < this.fLength) {
         return this.fArray[index];
      } else {
         throw new XSException((short)2, (String)null);
      }
   }

   public boolean equals(Object obj) {
      if (obj != null && obj instanceof ShortList) {
         ShortList rhs = (ShortList)obj;
         if (this.fLength != rhs.getLength()) {
            return false;
         } else {
            for(int i = 0; i < this.fLength; ++i) {
               if (this.fArray[i] != rhs.item(i)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public Object get(int index) {
      if (index >= 0 && index < this.fLength) {
         return new Short(this.fArray[index]);
      } else {
         throw new IndexOutOfBoundsException("Index: " + index);
      }
   }

   public int size() {
      return this.getLength();
   }
}
