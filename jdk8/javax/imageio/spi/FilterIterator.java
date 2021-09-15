package javax.imageio.spi;

import java.util.Iterator;
import java.util.NoSuchElementException;

class FilterIterator<T> implements Iterator<T> {
   private Iterator<T> iter;
   private ServiceRegistry.Filter filter;
   private T next = null;

   public FilterIterator(Iterator<T> var1, ServiceRegistry.Filter var2) {
      this.iter = var1;
      this.filter = var2;
      this.advance();
   }

   private void advance() {
      while(true) {
         if (this.iter.hasNext()) {
            Object var1 = this.iter.next();
            if (!this.filter.filter(var1)) {
               continue;
            }

            this.next = var1;
            return;
         }

         this.next = null;
         return;
      }
   }

   public boolean hasNext() {
      return this.next != null;
   }

   public T next() {
      if (this.next == null) {
         throw new NoSuchElementException();
      } else {
         Object var1 = this.next;
         this.advance();
         return var1;
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
