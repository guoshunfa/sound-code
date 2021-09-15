package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class CacheEnumerator implements Enumeration {
   boolean keys;
   int index;
   CacheEntry[] table;
   CacheEntry entry;

   CacheEnumerator(CacheEntry[] var1, boolean var2) {
      this.table = var1;
      this.keys = var2;
      this.index = var1.length;
   }

   public boolean hasMoreElements() {
      label24:
      while(true) {
         if (this.index >= 0) {
            while(this.entry != null) {
               if (this.entry.check() != null) {
                  return true;
               }

               this.entry = this.entry.next;
            }

            while(true) {
               if (--this.index < 0 || (this.entry = this.table[this.index]) != null) {
                  continue label24;
               }
            }
         }

         return false;
      }
   }

   public Object nextElement() {
      while(this.index >= 0) {
         if (this.entry == null) {
            while(--this.index >= 0 && (this.entry = this.table[this.index]) == null) {
            }
         }

         if (this.entry != null) {
            CacheEntry var1 = this.entry;
            this.entry = var1.next;
            if (var1.check() != null) {
               return this.keys ? var1.key : var1.check();
            }
         }
      }

      throw new NoSuchElementException("CacheEnumerator");
   }
}
