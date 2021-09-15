package java.util;

class SubList<E> extends AbstractList<E> {
   private final AbstractList<E> l;
   private final int offset;
   private int size;

   SubList(AbstractList<E> var1, int var2, int var3) {
      if (var2 < 0) {
         throw new IndexOutOfBoundsException("fromIndex = " + var2);
      } else if (var3 > var1.size()) {
         throw new IndexOutOfBoundsException("toIndex = " + var3);
      } else if (var2 > var3) {
         throw new IllegalArgumentException("fromIndex(" + var2 + ") > toIndex(" + var3 + ")");
      } else {
         this.l = var1;
         this.offset = var2;
         this.size = var3 - var2;
         this.modCount = this.l.modCount;
      }
   }

   public E set(int var1, E var2) {
      this.rangeCheck(var1);
      this.checkForComodification();
      return this.l.set(var1 + this.offset, var2);
   }

   public E get(int var1) {
      this.rangeCheck(var1);
      this.checkForComodification();
      return this.l.get(var1 + this.offset);
   }

   public int size() {
      this.checkForComodification();
      return this.size;
   }

   public void add(int var1, E var2) {
      this.rangeCheckForAdd(var1);
      this.checkForComodification();
      this.l.add(var1 + this.offset, var2);
      this.modCount = this.l.modCount;
      ++this.size;
   }

   public E remove(int var1) {
      this.rangeCheck(var1);
      this.checkForComodification();
      Object var2 = this.l.remove(var1 + this.offset);
      this.modCount = this.l.modCount;
      --this.size;
      return var2;
   }

   protected void removeRange(int var1, int var2) {
      this.checkForComodification();
      this.l.removeRange(var1 + this.offset, var2 + this.offset);
      this.modCount = this.l.modCount;
      this.size -= var2 - var1;
   }

   public boolean addAll(Collection<? extends E> var1) {
      return this.addAll(this.size, var1);
   }

   public boolean addAll(int var1, Collection<? extends E> var2) {
      this.rangeCheckForAdd(var1);
      int var3 = var2.size();
      if (var3 == 0) {
         return false;
      } else {
         this.checkForComodification();
         this.l.addAll(this.offset + var1, var2);
         this.modCount = this.l.modCount;
         this.size += var3;
         return true;
      }
   }

   public Iterator<E> iterator() {
      return this.listIterator();
   }

   public ListIterator<E> listIterator(final int var1) {
      this.checkForComodification();
      this.rangeCheckForAdd(var1);
      return new ListIterator<E>() {
         private final ListIterator<E> i;

         {
            this.i = SubList.this.l.listIterator(var1 + SubList.this.offset);
         }

         public boolean hasNext() {
            return this.nextIndex() < SubList.this.size;
         }

         public E next() {
            if (this.hasNext()) {
               return this.i.next();
            } else {
               throw new NoSuchElementException();
            }
         }

         public boolean hasPrevious() {
            return this.previousIndex() >= 0;
         }

         public E previous() {
            if (this.hasPrevious()) {
               return this.i.previous();
            } else {
               throw new NoSuchElementException();
            }
         }

         public int nextIndex() {
            return this.i.nextIndex() - SubList.this.offset;
         }

         public int previousIndex() {
            return this.i.previousIndex() - SubList.this.offset;
         }

         public void remove() {
            this.i.remove();
            SubList.this.modCount = SubList.this.l.modCount;
            SubList.this.size--;
         }

         public void set(E var1x) {
            this.i.set(var1x);
         }

         public void add(E var1x) {
            this.i.add(var1x);
            SubList.this.modCount = SubList.this.l.modCount;
            SubList.this.size++;
         }
      };
   }

   public List<E> subList(int var1, int var2) {
      return new SubList(this, var1, var2);
   }

   private void rangeCheck(int var1) {
      if (var1 < 0 || var1 >= this.size) {
         throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
      }
   }

   private void rangeCheckForAdd(int var1) {
      if (var1 < 0 || var1 > this.size) {
         throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
      }
   }

   private String outOfBoundsMsg(int var1) {
      return "Index: " + var1 + ", Size: " + this.size;
   }

   private void checkForComodification() {
      if (this.modCount != this.l.modCount) {
         throw new ConcurrentModificationException();
      }
   }
}
