package java.util;

public abstract class AbstractSequentialList<E> extends AbstractList<E> {
   protected AbstractSequentialList() {
   }

   public E get(int var1) {
      try {
         return this.listIterator(var1).next();
      } catch (NoSuchElementException var3) {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }
   }

   public E set(int var1, E var2) {
      try {
         ListIterator var3 = this.listIterator(var1);
         Object var4 = var3.next();
         var3.set(var2);
         return var4;
      } catch (NoSuchElementException var5) {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }
   }

   public void add(int var1, E var2) {
      try {
         this.listIterator(var1).add(var2);
      } catch (NoSuchElementException var4) {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }
   }

   public E remove(int var1) {
      try {
         ListIterator var2 = this.listIterator(var1);
         Object var3 = var2.next();
         var2.remove();
         return var3;
      } catch (NoSuchElementException var4) {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }
   }

   public boolean addAll(int var1, Collection<? extends E> var2) {
      try {
         boolean var3 = false;
         ListIterator var4 = this.listIterator(var1);

         for(Iterator var5 = var2.iterator(); var5.hasNext(); var3 = true) {
            var4.add(var5.next());
         }

         return var3;
      } catch (NoSuchElementException var6) {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }
   }

   public Iterator<E> iterator() {
      return this.listIterator();
   }

   public abstract ListIterator<E> listIterator(int var1);
}
