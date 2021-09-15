package com.sun.org.apache.xerces.internal.impl.dv.util;

import com.sun.org.apache.xerces.internal.xs.XSException;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;
import java.util.AbstractList;

public class ByteListImpl extends AbstractList implements ByteList {
   protected final byte[] data;
   protected String canonical;

   public ByteListImpl(byte[] data) {
      this.data = data;
   }

   public int getLength() {
      return this.data.length;
   }

   public boolean contains(byte item) {
      for(int i = 0; i < this.data.length; ++i) {
         if (this.data[i] == item) {
            return true;
         }
      }

      return false;
   }

   public byte item(int index) throws XSException {
      if (index >= 0 && index <= this.data.length - 1) {
         return this.data[index];
      } else {
         throw new XSException((short)2, (String)null);
      }
   }

   public Object get(int index) {
      if (index >= 0 && index < this.data.length) {
         return new Byte(this.data[index]);
      } else {
         throw new IndexOutOfBoundsException("Index: " + index);
      }
   }

   public int size() {
      return this.getLength();
   }
}
