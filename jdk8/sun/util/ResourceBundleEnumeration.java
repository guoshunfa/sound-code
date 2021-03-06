package sun.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class ResourceBundleEnumeration implements Enumeration<String> {
   Set<String> set;
   Iterator<String> iterator;
   Enumeration<String> enumeration;
   String next = null;

   public ResourceBundleEnumeration(Set<String> var1, Enumeration<String> var2) {
      this.set = var1;
      this.iterator = var1.iterator();
      this.enumeration = var2;
   }

   public boolean hasMoreElements() {
      if (this.next == null) {
         if (this.iterator.hasNext()) {
            this.next = (String)this.iterator.next();
         } else if (this.enumeration != null) {
            while(this.next == null && this.enumeration.hasMoreElements()) {
               this.next = (String)this.enumeration.nextElement();
               if (this.set.contains(this.next)) {
                  this.next = null;
               }
            }
         }
      }

      return this.next != null;
   }

   public String nextElement() {
      if (this.hasMoreElements()) {
         String var1 = this.next;
         this.next = null;
         return var1;
      } else {
         throw new NoSuchElementException();
      }
   }
}
