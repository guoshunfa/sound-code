package javax.naming;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

final class NameImplEnumerator implements Enumeration<String> {
   Vector<String> vector;
   int count;
   int limit;

   NameImplEnumerator(Vector<String> var1, int var2, int var3) {
      this.vector = var1;
      this.count = var2;
      this.limit = var3;
   }

   public boolean hasMoreElements() {
      return this.count < this.limit;
   }

   public String nextElement() {
      if (this.count < this.limit) {
         return (String)this.vector.elementAt(this.count++);
      } else {
         throw new NoSuchElementException("NameImplEnumerator");
      }
   }
}
