package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import java.util.AbstractList;

public class ListDV extends TypeValidator {
   public short getAllowedFacets() {
      return 2079;
   }

   public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
      return content;
   }

   public int getDataLength(Object value) {
      return ((ListDV.ListData)value).getLength();
   }

   static final class ListData extends AbstractList implements ObjectList {
      final Object[] data;
      private String canonical;

      public ListData(Object[] data) {
         this.data = data;
      }

      public synchronized String toString() {
         if (this.canonical == null) {
            int len = this.data.length;
            StringBuffer buf = new StringBuffer();
            if (len > 0) {
               buf.append(this.data[0].toString());
            }

            for(int i = 1; i < len; ++i) {
               buf.append(' ');
               buf.append(this.data[i].toString());
            }

            this.canonical = buf.toString();
         }

         return this.canonical;
      }

      public int getLength() {
         return this.data.length;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof ListDV.ListData)) {
            return false;
         } else {
            Object[] odata = ((ListDV.ListData)obj).data;
            int count = this.data.length;
            if (count != odata.length) {
               return false;
            } else {
               for(int i = 0; i < count; ++i) {
                  if (!this.data[i].equals(odata[i])) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public int hashCode() {
         int hash = 0;

         for(int i = 0; i < this.data.length; ++i) {
            hash ^= this.data[i].hashCode();
         }

         return hash;
      }

      public boolean contains(Object item) {
         for(int i = 0; i < this.data.length; ++i) {
            if (item == this.data[i]) {
               return true;
            }
         }

         return false;
      }

      public Object item(int index) {
         return index >= 0 && index < this.data.length ? this.data[index] : null;
      }

      public Object get(int index) {
         if (index >= 0 && index < this.data.length) {
            return this.data[index];
         } else {
            throw new IndexOutOfBoundsException("Index: " + index);
         }
      }

      public int size() {
         return this.getLength();
      }
   }
}
