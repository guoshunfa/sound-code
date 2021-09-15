package sun.awt.util;

import java.lang.reflect.Array;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IdentityLinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E> {
   private transient IdentityLinkedList.Entry<E> header;
   private transient int size;

   public IdentityLinkedList() {
      this.header = new IdentityLinkedList.Entry((Object)null, (IdentityLinkedList.Entry)null, (IdentityLinkedList.Entry)null);
      this.size = 0;
      this.header.next = this.header.previous = this.header;
   }

   public IdentityLinkedList(Collection<? extends E> var1) {
      this();
      this.addAll(var1);
   }

   public E getFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.header.next.element;
      }
   }

   public E getLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.header.previous.element;
      }
   }

   public E removeFirst() {
      return this.remove(this.header.next);
   }

   public E removeLast() {
      return this.remove(this.header.previous);
   }

   public void addFirst(E var1) {
      this.addBefore(var1, this.header.next);
   }

   public void addLast(E var1) {
      this.addBefore(var1, this.header);
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1) != -1;
   }

   public int size() {
      return this.size;
   }

   public boolean add(E var1) {
      this.addBefore(var1, this.header);
      return true;
   }

   public boolean remove(Object var1) {
      for(IdentityLinkedList.Entry var2 = this.header.next; var2 != this.header; var2 = var2.next) {
         if (var1 == var2.element) {
            this.remove(var2);
            return true;
         }
      }

      return false;
   }

   public boolean addAll(Collection<? extends E> var1) {
      return this.addAll(this.size, var1);
   }

   public boolean addAll(int var1, Collection<? extends E> var2) {
      if (var1 >= 0 && var1 <= this.size) {
         Object[] var3 = var2.toArray();
         int var4 = var3.length;
         if (var4 == 0) {
            return false;
         } else {
            ++this.modCount;
            IdentityLinkedList.Entry var5 = var1 == this.size ? this.header : this.entry(var1);
            IdentityLinkedList.Entry var6 = var5.previous;

            for(int var7 = 0; var7 < var4; ++var7) {
               IdentityLinkedList.Entry var8 = new IdentityLinkedList.Entry(var3[var7], var5, var6);
               var6.next = var8;
               var6 = var8;
            }

            var5.previous = var6;
            this.size += var4;
            return true;
         }
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.size);
      }
   }

   public void clear() {
      IdentityLinkedList.Entry var2;
      for(IdentityLinkedList.Entry var1 = this.header.next; var1 != this.header; var1 = var2) {
         var2 = var1.next;
         var1.next = var1.previous = null;
         var1.element = null;
      }

      this.header.next = this.header.previous = this.header;
      this.size = 0;
      ++this.modCount;
   }

   public E get(int var1) {
      return this.entry(var1).element;
   }

   public E set(int var1, E var2) {
      IdentityLinkedList.Entry var3 = this.entry(var1);
      Object var4 = var3.element;
      var3.element = var2;
      return var4;
   }

   public void add(int var1, E var2) {
      this.addBefore(var2, var1 == this.size ? this.header : this.entry(var1));
   }

   public E remove(int var1) {
      return this.remove(this.entry(var1));
   }

   private IdentityLinkedList.Entry<E> entry(int var1) {
      if (var1 >= 0 && var1 < this.size) {
         IdentityLinkedList.Entry var2 = this.header;
         int var3;
         if (var1 < this.size >> 1) {
            for(var3 = 0; var3 <= var1; ++var3) {
               var2 = var2.next;
            }
         } else {
            for(var3 = this.size; var3 > var1; --var3) {
               var2 = var2.previous;
            }
         }

         return var2;
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.size);
      }
   }

   public int indexOf(Object var1) {
      int var2 = 0;

      for(IdentityLinkedList.Entry var3 = this.header.next; var3 != this.header; var3 = var3.next) {
         if (var1 == var3.element) {
            return var2;
         }

         ++var2;
      }

      return -1;
   }

   public int lastIndexOf(Object var1) {
      int var2 = this.size;

      for(IdentityLinkedList.Entry var3 = this.header.previous; var3 != this.header; var3 = var3.previous) {
         --var2;
         if (var1 == var3.element) {
            return var2;
         }
      }

      return -1;
   }

   public E peek() {
      return this.size == 0 ? null : this.getFirst();
   }

   public E element() {
      return this.getFirst();
   }

   public E poll() {
      return this.size == 0 ? null : this.removeFirst();
   }

   public E remove() {
      return this.removeFirst();
   }

   public boolean offer(E var1) {
      return this.add(var1);
   }

   public boolean offerFirst(E var1) {
      this.addFirst(var1);
      return true;
   }

   public boolean offerLast(E var1) {
      this.addLast(var1);
      return true;
   }

   public E peekFirst() {
      return this.size == 0 ? null : this.getFirst();
   }

   public E peekLast() {
      return this.size == 0 ? null : this.getLast();
   }

   public E pollFirst() {
      return this.size == 0 ? null : this.removeFirst();
   }

   public E pollLast() {
      return this.size == 0 ? null : this.removeLast();
   }

   public void push(E var1) {
      this.addFirst(var1);
   }

   public E pop() {
      return this.removeFirst();
   }

   public boolean removeFirstOccurrence(Object var1) {
      return this.remove(var1);
   }

   public boolean removeLastOccurrence(Object var1) {
      for(IdentityLinkedList.Entry var2 = this.header.previous; var2 != this.header; var2 = var2.previous) {
         if (var1 == var2.element) {
            this.remove(var2);
            return true;
         }
      }

      return false;
   }

   public ListIterator<E> listIterator(int var1) {
      return new IdentityLinkedList.ListItr(var1);
   }

   private IdentityLinkedList.Entry<E> addBefore(E var1, IdentityLinkedList.Entry<E> var2) {
      IdentityLinkedList.Entry var3 = new IdentityLinkedList.Entry(var1, var2, var2.previous);
      var3.previous.next = var3;
      var3.next.previous = var3;
      ++this.size;
      ++this.modCount;
      return var3;
   }

   private E remove(IdentityLinkedList.Entry<E> var1) {
      if (var1 == this.header) {
         throw new NoSuchElementException();
      } else {
         Object var2 = var1.element;
         var1.previous.next = var1.next;
         var1.next.previous = var1.previous;
         var1.next = var1.previous = null;
         var1.element = null;
         --this.size;
         ++this.modCount;
         return var2;
      }
   }

   public Iterator<E> descendingIterator() {
      return new IdentityLinkedList.DescendingIterator();
   }

   public Object[] toArray() {
      Object[] var1 = new Object[this.size];
      int var2 = 0;

      for(IdentityLinkedList.Entry var3 = this.header.next; var3 != this.header; var3 = var3.next) {
         var1[var2++] = var3.element;
      }

      return var1;
   }

   public <T> T[] toArray(T[] var1) {
      if (var1.length < this.size) {
         var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), this.size));
      }

      int var2 = 0;
      Object[] var3 = var1;

      for(IdentityLinkedList.Entry var4 = this.header.next; var4 != this.header; var4 = var4.next) {
         var3[var2++] = var4.element;
      }

      if (var1.length > this.size) {
         var1[this.size] = null;
      }

      return var1;
   }

   private class DescendingIterator implements Iterator {
      final IdentityLinkedList<E>.ListItr itr;

      private DescendingIterator() {
         this.itr = IdentityLinkedList.this.new ListItr(IdentityLinkedList.this.size());
      }

      public boolean hasNext() {
         return this.itr.hasPrevious();
      }

      public E next() {
         return this.itr.previous();
      }

      public void remove() {
         this.itr.remove();
      }

      // $FF: synthetic method
      DescendingIterator(Object var2) {
         this();
      }
   }

   private static class Entry<E> {
      E element;
      IdentityLinkedList.Entry<E> next;
      IdentityLinkedList.Entry<E> previous;

      Entry(E var1, IdentityLinkedList.Entry<E> var2, IdentityLinkedList.Entry<E> var3) {
         this.element = var1;
         this.next = var2;
         this.previous = var3;
      }
   }

   private class ListItr implements ListIterator<E> {
      private IdentityLinkedList.Entry<E> lastReturned;
      private IdentityLinkedList.Entry<E> next;
      private int nextIndex;
      private int expectedModCount;

      ListItr(int var2) {
         this.lastReturned = IdentityLinkedList.this.header;
         this.expectedModCount = IdentityLinkedList.this.modCount;
         if (var2 >= 0 && var2 <= IdentityLinkedList.this.size) {
            if (var2 < IdentityLinkedList.this.size >> 1) {
               this.next = IdentityLinkedList.this.header.next;

               for(this.nextIndex = 0; this.nextIndex < var2; ++this.nextIndex) {
                  this.next = this.next.next;
               }
            } else {
               this.next = IdentityLinkedList.this.header;

               for(this.nextIndex = IdentityLinkedList.this.size; this.nextIndex > var2; --this.nextIndex) {
                  this.next = this.next.previous;
               }
            }

         } else {
            throw new IndexOutOfBoundsException("Index: " + var2 + ", Size: " + IdentityLinkedList.this.size);
         }
      }

      public boolean hasNext() {
         return this.nextIndex != IdentityLinkedList.this.size;
      }

      public E next() {
         this.checkForComodification();
         if (this.nextIndex == IdentityLinkedList.this.size) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.next;
            this.next = this.next.next;
            ++this.nextIndex;
            return this.lastReturned.element;
         }
      }

      public boolean hasPrevious() {
         return this.nextIndex != 0;
      }

      public E previous() {
         if (this.nextIndex == 0) {
            throw new NoSuchElementException();
         } else {
            this.lastReturned = this.next = this.next.previous;
            --this.nextIndex;
            this.checkForComodification();
            return this.lastReturned.element;
         }
      }

      public int nextIndex() {
         return this.nextIndex;
      }

      public int previousIndex() {
         return this.nextIndex - 1;
      }

      public void remove() {
         this.checkForComodification();
         IdentityLinkedList.Entry var1 = this.lastReturned.next;

         try {
            IdentityLinkedList.this.remove(this.lastReturned);
         } catch (NoSuchElementException var3) {
            throw new IllegalStateException();
         }

         if (this.next == this.lastReturned) {
            this.next = var1;
         } else {
            --this.nextIndex;
         }

         this.lastReturned = IdentityLinkedList.this.header;
         ++this.expectedModCount;
      }

      public void set(E var1) {
         if (this.lastReturned == IdentityLinkedList.this.header) {
            throw new IllegalStateException();
         } else {
            this.checkForComodification();
            this.lastReturned.element = var1;
         }
      }

      public void add(E var1) {
         this.checkForComodification();
         this.lastReturned = IdentityLinkedList.this.header;
         IdentityLinkedList.this.addBefore(var1, this.next);
         ++this.nextIndex;
         ++this.expectedModCount;
      }

      final void checkForComodification() {
         if (IdentityLinkedList.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }
   }
}
