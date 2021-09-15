package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;
import sun.misc.SharedSecrets;

public class ArrayDeque<E> extends AbstractCollection<E> implements Deque<E>, Cloneable, Serializable {
   transient Object[] elements;
   transient int head;
   transient int tail;
   private static final int MIN_INITIAL_CAPACITY = 8;
   private static final long serialVersionUID = 2340985798034038923L;

   private static int calculateSize(int var0) {
      int var1 = 8;
      if (var0 >= var1) {
         var1 = var0 | var0 >>> 1;
         var1 |= var1 >>> 2;
         var1 |= var1 >>> 4;
         var1 |= var1 >>> 8;
         var1 |= var1 >>> 16;
         ++var1;
         if (var1 < 0) {
            var1 >>>= 1;
         }
      }

      return var1;
   }

   private void allocateElements(int var1) {
      this.elements = new Object[calculateSize(var1)];
   }

   private void doubleCapacity() {
      assert this.head == this.tail;

      int var1 = this.head;
      int var2 = this.elements.length;
      int var3 = var2 - var1;
      int var4 = var2 << 1;
      if (var4 < 0) {
         throw new IllegalStateException("Sorry, deque too big");
      } else {
         Object[] var5 = new Object[var4];
         System.arraycopy(this.elements, var1, var5, 0, var3);
         System.arraycopy(this.elements, 0, var5, var3, var1);
         this.elements = var5;
         this.head = 0;
         this.tail = var2;
      }
   }

   private <T> T[] copyElements(T[] var1) {
      if (this.head < this.tail) {
         System.arraycopy(this.elements, this.head, var1, 0, this.size());
      } else if (this.head > this.tail) {
         int var2 = this.elements.length - this.head;
         System.arraycopy(this.elements, this.head, var1, 0, var2);
         System.arraycopy(this.elements, 0, var1, var2, this.tail);
      }

      return var1;
   }

   public ArrayDeque() {
      this.elements = new Object[16];
   }

   public ArrayDeque(int var1) {
      this.allocateElements(var1);
   }

   public ArrayDeque(Collection<? extends E> var1) {
      this.allocateElements(var1.size());
      this.addAll(var1);
   }

   public void addFirst(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.elements[this.head = this.head - 1 & this.elements.length - 1] = var1;
         if (this.head == this.tail) {
            this.doubleCapacity();
         }

      }
   }

   public void addLast(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.elements[this.tail] = var1;
         if ((this.tail = this.tail + 1 & this.elements.length - 1) == this.head) {
            this.doubleCapacity();
         }

      }
   }

   public boolean offerFirst(E var1) {
      this.addFirst(var1);
      return true;
   }

   public boolean offerLast(E var1) {
      this.addLast(var1);
      return true;
   }

   public E removeFirst() {
      Object var1 = this.pollFirst();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1;
      }
   }

   public E removeLast() {
      Object var1 = this.pollLast();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1;
      }
   }

   public E pollFirst() {
      int var1 = this.head;
      Object var2 = this.elements[var1];
      if (var2 == null) {
         return null;
      } else {
         this.elements[var1] = null;
         this.head = var1 + 1 & this.elements.length - 1;
         return var2;
      }
   }

   public E pollLast() {
      int var1 = this.tail - 1 & this.elements.length - 1;
      Object var2 = this.elements[var1];
      if (var2 == null) {
         return null;
      } else {
         this.elements[var1] = null;
         this.tail = var1;
         return var2;
      }
   }

   public E getFirst() {
      Object var1 = this.elements[this.head];
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1;
      }
   }

   public E getLast() {
      Object var1 = this.elements[this.tail - 1 & this.elements.length - 1];
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1;
      }
   }

   public E peekFirst() {
      return this.elements[this.head];
   }

   public E peekLast() {
      return this.elements[this.tail - 1 & this.elements.length - 1];
   }

   public boolean removeFirstOccurrence(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.elements.length - 1;

         Object var4;
         for(int var3 = this.head; (var4 = this.elements[var3]) != null; var3 = var3 + 1 & var2) {
            if (var1.equals(var4)) {
               this.delete(var3);
               return true;
            }
         }

         return false;
      }
   }

   public boolean removeLastOccurrence(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.elements.length - 1;

         Object var4;
         for(int var3 = this.tail - 1 & var2; (var4 = this.elements[var3]) != null; var3 = var3 - 1 & var2) {
            if (var1.equals(var4)) {
               this.delete(var3);
               return true;
            }
         }

         return false;
      }
   }

   public boolean add(E var1) {
      this.addLast(var1);
      return true;
   }

   public boolean offer(E var1) {
      return this.offerLast(var1);
   }

   public E remove() {
      return this.removeFirst();
   }

   public E poll() {
      return this.pollFirst();
   }

   public E element() {
      return this.getFirst();
   }

   public E peek() {
      return this.peekFirst();
   }

   public void push(E var1) {
      this.addFirst(var1);
   }

   public E pop() {
      return this.removeFirst();
   }

   private void checkInvariants() {
      assert this.elements[this.tail] == null;

      if (!$assertionsDisabled) {
         label36: {
            if (this.head == this.tail) {
               if (this.elements[this.head] == null) {
                  break label36;
               }
            } else if (this.elements[this.head] != null && this.elements[this.tail - 1 & this.elements.length - 1] != null) {
               break label36;
            }

            throw new AssertionError();
         }
      }

      assert this.elements[this.head - 1 & this.elements.length - 1] == null;

   }

   private boolean delete(int var1) {
      this.checkInvariants();
      Object[] var2 = this.elements;
      int var3 = var2.length - 1;
      int var4 = this.head;
      int var5 = this.tail;
      int var6 = var1 - var4 & var3;
      int var7 = var5 - var1 & var3;
      if (var6 >= (var5 - var4 & var3)) {
         throw new ConcurrentModificationException();
      } else if (var6 < var7) {
         if (var4 <= var1) {
            System.arraycopy(var2, var4, var2, var4 + 1, var6);
         } else {
            System.arraycopy(var2, 0, var2, 1, var1);
            var2[0] = var2[var3];
            System.arraycopy(var2, var4, var2, var4 + 1, var3 - var4);
         }

         var2[var4] = null;
         this.head = var4 + 1 & var3;
         return false;
      } else {
         if (var1 < var5) {
            System.arraycopy(var2, var1 + 1, var2, var1, var7);
            this.tail = var5 - 1;
         } else {
            System.arraycopy(var2, var1 + 1, var2, var1, var3 - var1);
            var2[var3] = var2[0];
            System.arraycopy(var2, 1, var2, 0, var5);
            this.tail = var5 - 1 & var3;
         }

         return true;
      }
   }

   public int size() {
      return this.tail - this.head & this.elements.length - 1;
   }

   public boolean isEmpty() {
      return this.head == this.tail;
   }

   public Iterator<E> iterator() {
      return new ArrayDeque.DeqIterator();
   }

   public Iterator<E> descendingIterator() {
      return new ArrayDeque.DescendingIterator();
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         int var2 = this.elements.length - 1;

         Object var4;
         for(int var3 = this.head; (var4 = this.elements[var3]) != null; var3 = var3 + 1 & var2) {
            if (var1.equals(var4)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean remove(Object var1) {
      return this.removeFirstOccurrence(var1);
   }

   public void clear() {
      int var1 = this.head;
      int var2 = this.tail;
      if (var1 != var2) {
         this.head = this.tail = 0;
         int var3 = var1;
         int var4 = this.elements.length - 1;

         do {
            this.elements[var3] = null;
            var3 = var3 + 1 & var4;
         } while(var3 != var2);
      }

   }

   public Object[] toArray() {
      return this.copyElements(new Object[this.size()]);
   }

   public <T> T[] toArray(T[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var2));
      }

      this.copyElements(var1);
      if (var1.length > var2) {
         var1[var2] = null;
      }

      return var1;
   }

   public ArrayDeque<E> clone() {
      try {
         ArrayDeque var1 = (ArrayDeque)super.clone();
         var1.elements = Arrays.copyOf(this.elements, this.elements.length);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(this.size());
      int var2 = this.elements.length - 1;

      for(int var3 = this.head; var3 != this.tail; var3 = var3 + 1 & var2) {
         var1.writeObject(this.elements[var3]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      int var2 = var1.readInt();
      int var3 = calculateSize(var2);
      SharedSecrets.getJavaOISAccess().checkArray(var1, Object[].class, var3);
      this.allocateElements(var2);
      this.head = 0;
      this.tail = var2;

      for(int var4 = 0; var4 < var2; ++var4) {
         this.elements[var4] = var1.readObject();
      }

   }

   public Spliterator<E> spliterator() {
      return new ArrayDeque.DeqSpliterator(this, -1, -1);
   }

   static final class DeqSpliterator<E> implements Spliterator<E> {
      private final ArrayDeque<E> deq;
      private int fence;
      private int index;

      DeqSpliterator(ArrayDeque<E> var1, int var2, int var3) {
         this.deq = var1;
         this.index = var2;
         this.fence = var3;
      }

      private int getFence() {
         int var1;
         if ((var1 = this.fence) < 0) {
            var1 = this.fence = this.deq.tail;
            this.index = this.deq.head;
         }

         return var1;
      }

      public ArrayDeque.DeqSpliterator<E> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = this.deq.elements.length;
         if (var2 != var1 && (var2 + 1 & var3 - 1) != var1) {
            if (var2 > var1) {
               var1 += var3;
            }

            int var4 = var2 + var1 >>> 1 & var3 - 1;
            return new ArrayDeque.DeqSpliterator(this.deq, var2, this.index = var4);
         } else {
            return null;
         }
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object[] var2 = this.deq.elements;
            int var3 = var2.length - 1;
            int var4 = this.getFence();
            int var5 = this.index;
            this.index = var4;

            while(var5 != var4) {
               Object var6 = var2[var5];
               var5 = var5 + 1 & var3;
               if (var6 == null) {
                  throw new ConcurrentModificationException();
               }

               var1.accept(var6);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object[] var2 = this.deq.elements;
            int var3 = var2.length - 1;
            int var4 = this.getFence();
            int var5 = this.index;
            if (var5 != this.fence) {
               Object var6 = var2[var5];
               this.index = var5 + 1 & var3;
               if (var6 == null) {
                  throw new ConcurrentModificationException();
               } else {
                  var1.accept(var6);
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public long estimateSize() {
         int var1 = this.getFence() - this.index;
         if (var1 < 0) {
            var1 += this.deq.elements.length;
         }

         return (long)var1;
      }

      public int characteristics() {
         return 16720;
      }
   }

   private class DescendingIterator implements Iterator<E> {
      private int cursor;
      private int fence;
      private int lastRet;

      private DescendingIterator() {
         this.cursor = ArrayDeque.this.tail;
         this.fence = ArrayDeque.this.head;
         this.lastRet = -1;
      }

      public boolean hasNext() {
         return this.cursor != this.fence;
      }

      public E next() {
         if (this.cursor == this.fence) {
            throw new NoSuchElementException();
         } else {
            this.cursor = this.cursor - 1 & ArrayDeque.this.elements.length - 1;
            Object var1 = ArrayDeque.this.elements[this.cursor];
            if (ArrayDeque.this.head == this.fence && var1 != null) {
               this.lastRet = this.cursor;
               return var1;
            } else {
               throw new ConcurrentModificationException();
            }
         }
      }

      public void remove() {
         if (this.lastRet < 0) {
            throw new IllegalStateException();
         } else {
            if (!ArrayDeque.this.delete(this.lastRet)) {
               this.cursor = this.cursor + 1 & ArrayDeque.this.elements.length - 1;
               this.fence = ArrayDeque.this.head;
            }

            this.lastRet = -1;
         }
      }

      // $FF: synthetic method
      DescendingIterator(Object var2) {
         this();
      }
   }

   private class DeqIterator implements Iterator<E> {
      private int cursor;
      private int fence;
      private int lastRet;

      private DeqIterator() {
         this.cursor = ArrayDeque.this.head;
         this.fence = ArrayDeque.this.tail;
         this.lastRet = -1;
      }

      public boolean hasNext() {
         return this.cursor != this.fence;
      }

      public E next() {
         if (this.cursor == this.fence) {
            throw new NoSuchElementException();
         } else {
            Object var1 = ArrayDeque.this.elements[this.cursor];
            if (ArrayDeque.this.tail == this.fence && var1 != null) {
               this.lastRet = this.cursor;
               this.cursor = this.cursor + 1 & ArrayDeque.this.elements.length - 1;
               return var1;
            } else {
               throw new ConcurrentModificationException();
            }
         }
      }

      public void remove() {
         if (this.lastRet < 0) {
            throw new IllegalStateException();
         } else {
            if (ArrayDeque.this.delete(this.lastRet)) {
               this.cursor = this.cursor - 1 & ArrayDeque.this.elements.length - 1;
               this.fence = ArrayDeque.this.tail;
            }

            this.lastRet = -1;
         }
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
         Object[] var2 = ArrayDeque.this.elements;
         int var3 = var2.length - 1;
         int var4 = this.fence;
         int var5 = this.cursor;
         this.cursor = var4;

         while(var5 != var4) {
            Object var6 = var2[var5];
            var5 = var5 + 1 & var3;
            if (var6 == null) {
               throw new ConcurrentModificationException();
            }

            var1.accept(var6);
         }

      }

      // $FF: synthetic method
      DeqIterator(Object var2) {
         this();
      }
   }
}
