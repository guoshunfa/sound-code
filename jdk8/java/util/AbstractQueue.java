package java.util;

public abstract class AbstractQueue<E> extends AbstractCollection<E> implements Queue<E> {
   protected AbstractQueue() {
   }

   public boolean add(E var1) {
      if (this.offer(var1)) {
         return true;
      } else {
         throw new IllegalStateException("Queue full");
      }
   }

   public E remove() {
      Object var1 = this.poll();
      if (var1 != null) {
         return var1;
      } else {
         throw new NoSuchElementException();
      }
   }

   public E element() {
      Object var1 = this.peek();
      if (var1 != null) {
         return var1;
      } else {
         throw new NoSuchElementException();
      }
   }

   public void clear() {
      while(this.poll() != null) {
      }

   }

   public boolean addAll(Collection<? extends E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 == this) {
         throw new IllegalArgumentException();
      } else {
         boolean var2 = false;
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (this.add(var4)) {
               var2 = true;
            }
         }

         return var2;
      }
   }
}
