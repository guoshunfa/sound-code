package com.sun.corba.se.impl.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class IdentityHashtableEnumerator implements Enumeration {
   boolean keys;
   int index;
   IdentityHashtableEntry[] table;
   IdentityHashtableEntry entry;

   IdentityHashtableEnumerator(IdentityHashtableEntry[] var1, boolean var2) {
      this.table = var1;
      this.keys = var2;
      this.index = var1.length;
   }

   public boolean hasMoreElements() {
      if (this.entry != null) {
         return true;
      } else {
         do {
            if (this.index-- <= 0) {
               return false;
            }
         } while((this.entry = this.table[this.index]) == null);

         return true;
      }
   }

   public Object nextElement() {
      if (this.entry == null) {
         while(this.index-- > 0 && (this.entry = this.table[this.index]) == null) {
         }
      }

      if (this.entry != null) {
         IdentityHashtableEntry var1 = this.entry;
         this.entry = var1.next;
         return this.keys ? var1.key : var1.value;
      } else {
         throw new NoSuchElementException("IdentityHashtableEnumerator");
      }
   }
}
