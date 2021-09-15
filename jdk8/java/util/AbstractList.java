package java.util;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
   protected transient int modCount = 0;

   protected AbstractList() {
   }

   public boolean add(E var1) {
      this.add(this.size(), var1);
      return true;
   }

   public abstract E get(int var1);

   public E set(int var1, E var2) {
      throw new UnsupportedOperationException();
   }

   public void add(int var1, E var2) {
      throw new UnsupportedOperationException();
   }

   public E remove(int var1) {
      throw new UnsupportedOperationException();
   }

   public int indexOf(Object var1) {
      ListIterator var2 = this.listIterator();
      if (var1 == null) {
         while(var2.hasNext()) {
            if (var2.next() == null) {
               return var2.previousIndex();
            }
         }
      } else {
         while(var2.hasNext()) {
            if (var1.equals(var2.next())) {
               return var2.previousIndex();
            }
         }
      }

      return -1;
   }

   public int lastIndexOf(Object var1) {
      ListIterator var2 = this.listIterator(this.size());
      if (var1 == null) {
         while(var2.hasPrevious()) {
            if (var2.previous() == null) {
               return var2.nextIndex();
            }
         }
      } else {
         while(var2.hasPrevious()) {
            if (var1.equals(var2.previous())) {
               return var2.nextIndex();
            }
         }
      }

      return -1;
   }

   public void clear() {
      this.removeRange(0, this.size());
   }

   public boolean addAll(int var1, Collection<? extends E> var2) {
      this.rangeCheckForAdd(var1);
      boolean var3 = false;

      for(Iterator var4 = var2.iterator(); var4.hasNext(); var3 = true) {
         Object var5 = var4.next();
         this.add(var1++, var5);
      }

      return var3;
   }

   public Iterator<E> iterator() {
      return new AbstractList.Itr();
   }

   public ListIterator<E> listIterator() {
      return this.listIterator(0);
   }

   public ListIterator<E> listIterator(int var1) {
      this.rangeCheckForAdd(var1);
      return new AbstractList.ListItr(var1);
   }

   public List<E> subList(int var1, int var2) {
      return (List)(this instanceof RandomAccess ? new RandomAccessSubList(this, var1, var2) : new SubList(this, var1, var2));
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof List)) {
         return false;
      } else {
         ListIterator var2 = this.listIterator();
         ListIterator var3 = ((List)var1).listIterator();

         while(true) {
            if (var2.hasNext() && var3.hasNext()) {
               Object var4 = var2.next();
               Object var5 = var3.next();
               if (var4 == null) {
                  if (var5 == null) {
                     continue;
                  }
               } else if (var4.equals(var5)) {
                  continue;
               }

               return false;
            }

            return !var2.hasNext() && !var3.hasNext();
         }
      }
   }

   public int hashCode() {
      int var1 = 1;

      Object var3;
      for(Iterator var2 = this.iterator(); var2.hasNext(); var1 = 31 * var1 + (var3 == null ? 0 : var3.hashCode())) {
         var3 = var2.next();
      }

      return var1;
   }

   protected void removeRange(int var1, int var2) {
      ListIterator var3 = this.listIterator(var1);
      int var4 = 0;

      for(int var5 = var2 - var1; var4 < var5; ++var4) {
         var3.next();
         var3.remove();
      }

   }

   private void rangeCheckForAdd(int var1) {
      if (var1 < 0 || var1 > this.size()) {
         throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
      }
   }

   private String outOfBoundsMsg(int var1) {
      return "Index: " + var1 + ", Size: " + this.size();
   }

   private class ListItr extends AbstractList<E>.Itr implements ListIterator<E> {
      ListItr(int var2) {
         super(null);
         this.cursor = var2;
      }

      public boolean hasPrevious() {
         return this.cursor != 0;
      }

      public E previous() {
         this.checkForComodification();

         try {
            int var1 = this.cursor - 1;
            Object var2 = AbstractList.this.get(var1);
            this.lastRet = this.cursor = var1;
            return var2;
         } catch (IndexOutOfBoundsException var3) {
            this.checkForComodification();
            throw new NoSuchElementException();
         }
      }

      public int nextIndex() {
         return this.cursor;
      }

      public int previousIndex() {
         return this.cursor - 1;
      }

      public void set(E var1) {
         if (this.lastRet < 0) {
            throw new IllegalStateException();
         } else {
            this.checkForComodification();

            try {
               AbstractList.this.set(this.lastRet, var1);
               this.expectedModCount = AbstractList.this.modCount;
            } catch (IndexOutOfBoundsException var3) {
               throw new ConcurrentModificationException();
            }
         }
      }

      public void add(E var1) {
         this.checkForComodification();

         try {
            int var2 = this.cursor;
            AbstractList.this.add(var2, var1);
            this.lastRet = -1;
            this.cursor = var2 + 1;
            this.expectedModCount = AbstractList.this.modCount;
         } catch (IndexOutOfBoundsException var3) {
            throw new ConcurrentModificationException();
         }
      }
   }

   private class Itr implements Iterator<E> {
      int cursor;
      int lastRet;
      int expectedModCount;

      private Itr() {
         this.cursor = 0;
         this.lastRet = -1;
         this.expectedModCount = AbstractList.this.modCount;
      }

      public boolean hasNext() {
         return this.cursor != AbstractList.this.size();
      }

      public E next() {
         this.checkForComodification();

         try {
            int var1 = this.cursor;
            Object var2 = AbstractList.this.get(var1);
            this.lastRet = var1;
            this.cursor = var1 + 1;
            return var2;
         } catch (IndexOutOfBoundsException var3) {
            this.checkForComodification();
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         if (this.lastRet < 0) {
            throw new IllegalStateException();
         } else {
            this.checkForComodification();

            try {
               AbstractList.this.remove(this.lastRet);
               if (this.lastRet < this.cursor) {
                  --this.cursor;
               }

               this.lastRet = -1;
               this.expectedModCount = AbstractList.this.modCount;
            } catch (IndexOutOfBoundsException var2) {
               throw new ConcurrentModificationException();
            }
         }
      }

      final void checkForComodification() {
         if (AbstractList.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      // $FF: synthetic method
      Itr(Object var2) {
         this();
      }
   }
}
