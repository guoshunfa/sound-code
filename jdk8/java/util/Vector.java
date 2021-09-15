package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
   protected Object[] elementData;
   protected int elementCount;
   protected int capacityIncrement;
   private static final long serialVersionUID = -2767605614048989439L;
   private static final int MAX_ARRAY_SIZE = 2147483639;

   public Vector(int var1, int var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + var1);
      } else {
         this.elementData = new Object[var1];
         this.capacityIncrement = var2;
      }
   }

   public Vector(int var1) {
      this(var1, 0);
   }

   public Vector() {
      this(10);
   }

   public Vector(Collection<? extends E> var1) {
      this.elementData = var1.toArray();
      this.elementCount = this.elementData.length;
      if (this.elementData.getClass() != Object[].class) {
         this.elementData = Arrays.copyOf(this.elementData, this.elementCount, Object[].class);
      }

   }

   public synchronized void copyInto(Object[] var1) {
      System.arraycopy(this.elementData, 0, var1, 0, this.elementCount);
   }

   public synchronized void trimToSize() {
      ++this.modCount;
      int var1 = this.elementData.length;
      if (this.elementCount < var1) {
         this.elementData = Arrays.copyOf(this.elementData, this.elementCount);
      }

   }

   public synchronized void ensureCapacity(int var1) {
      if (var1 > 0) {
         ++this.modCount;
         this.ensureCapacityHelper(var1);
      }

   }

   private void ensureCapacityHelper(int var1) {
      if (var1 - this.elementData.length > 0) {
         this.grow(var1);
      }

   }

   private void grow(int var1) {
      int var2 = this.elementData.length;
      int var3 = var2 + (this.capacityIncrement > 0 ? this.capacityIncrement : var2);
      if (var3 - var1 < 0) {
         var3 = var1;
      }

      if (var3 - 2147483639 > 0) {
         var3 = hugeCapacity(var1);
      }

      this.elementData = Arrays.copyOf(this.elementData, var3);
   }

   private static int hugeCapacity(int var0) {
      if (var0 < 0) {
         throw new OutOfMemoryError();
      } else {
         return var0 > 2147483639 ? Integer.MAX_VALUE : 2147483639;
      }
   }

   public synchronized void setSize(int var1) {
      ++this.modCount;
      if (var1 > this.elementCount) {
         this.ensureCapacityHelper(var1);
      } else {
         for(int var2 = var1; var2 < this.elementCount; ++var2) {
            this.elementData[var2] = null;
         }
      }

      this.elementCount = var1;
   }

   public synchronized int capacity() {
      return this.elementData.length;
   }

   public synchronized int size() {
      return this.elementCount;
   }

   public synchronized boolean isEmpty() {
      return this.elementCount == 0;
   }

   public Enumeration<E> elements() {
      return new Enumeration<E>() {
         int count = 0;

         public boolean hasMoreElements() {
            return this.count < Vector.this.elementCount;
         }

         public E nextElement() {
            synchronized(Vector.this) {
               if (this.count < Vector.this.elementCount) {
                  return Vector.this.elementData(this.count++);
               }
            }

            throw new NoSuchElementException("Vector Enumeration");
         }
      };
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1, 0) >= 0;
   }

   public int indexOf(Object var1) {
      return this.indexOf(var1, 0);
   }

   public synchronized int indexOf(Object var1, int var2) {
      int var3;
      if (var1 == null) {
         for(var3 = var2; var3 < this.elementCount; ++var3) {
            if (this.elementData[var3] == null) {
               return var3;
            }
         }
      } else {
         for(var3 = var2; var3 < this.elementCount; ++var3) {
            if (var1.equals(this.elementData[var3])) {
               return var3;
            }
         }
      }

      return -1;
   }

   public synchronized int lastIndexOf(Object var1) {
      return this.lastIndexOf(var1, this.elementCount - 1);
   }

   public synchronized int lastIndexOf(Object var1, int var2) {
      if (var2 >= this.elementCount) {
         throw new IndexOutOfBoundsException(var2 + " >= " + this.elementCount);
      } else {
         int var3;
         if (var1 == null) {
            for(var3 = var2; var3 >= 0; --var3) {
               if (this.elementData[var3] == null) {
                  return var3;
               }
            }
         } else {
            for(var3 = var2; var3 >= 0; --var3) {
               if (var1.equals(this.elementData[var3])) {
                  return var3;
               }
            }
         }

         return -1;
      }
   }

   public synchronized E elementAt(int var1) {
      if (var1 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var1 + " >= " + this.elementCount);
      } else {
         return this.elementData(var1);
      }
   }

   public synchronized E firstElement() {
      if (this.elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this.elementData(0);
      }
   }

   public synchronized E lastElement() {
      if (this.elementCount == 0) {
         throw new NoSuchElementException();
      } else {
         return this.elementData(this.elementCount - 1);
      }
   }

   public synchronized void setElementAt(E var1, int var2) {
      if (var2 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var2 + " >= " + this.elementCount);
      } else {
         this.elementData[var2] = var1;
      }
   }

   public synchronized void removeElementAt(int var1) {
      ++this.modCount;
      if (var1 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var1 + " >= " + this.elementCount);
      } else if (var1 < 0) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         int var2 = this.elementCount - var1 - 1;
         if (var2 > 0) {
            System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var2);
         }

         --this.elementCount;
         this.elementData[this.elementCount] = null;
      }
   }

   public synchronized void insertElementAt(E var1, int var2) {
      ++this.modCount;
      if (var2 > this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var2 + " > " + this.elementCount);
      } else {
         this.ensureCapacityHelper(this.elementCount + 1);
         System.arraycopy(this.elementData, var2, this.elementData, var2 + 1, this.elementCount - var2);
         this.elementData[var2] = var1;
         ++this.elementCount;
      }
   }

   public synchronized void addElement(E var1) {
      ++this.modCount;
      this.ensureCapacityHelper(this.elementCount + 1);
      this.elementData[this.elementCount++] = var1;
   }

   public synchronized boolean removeElement(Object var1) {
      ++this.modCount;
      int var2 = this.indexOf(var1);
      if (var2 >= 0) {
         this.removeElementAt(var2);
         return true;
      } else {
         return false;
      }
   }

   public synchronized void removeAllElements() {
      ++this.modCount;

      for(int var1 = 0; var1 < this.elementCount; ++var1) {
         this.elementData[var1] = null;
      }

      this.elementCount = 0;
   }

   public synchronized Object clone() {
      try {
         Vector var1 = (Vector)super.clone();
         var1.elementData = Arrays.copyOf(this.elementData, this.elementCount);
         var1.modCount = 0;
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public synchronized Object[] toArray() {
      return Arrays.copyOf(this.elementData, this.elementCount);
   }

   public synchronized <T> T[] toArray(T[] var1) {
      if (var1.length < this.elementCount) {
         return (Object[])Arrays.copyOf(this.elementData, this.elementCount, var1.getClass());
      } else {
         System.arraycopy(this.elementData, 0, var1, 0, this.elementCount);
         if (var1.length > this.elementCount) {
            var1[this.elementCount] = null;
         }

         return var1;
      }
   }

   E elementData(int var1) {
      return this.elementData[var1];
   }

   public synchronized E get(int var1) {
      if (var1 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         return this.elementData(var1);
      }
   }

   public synchronized E set(int var1, E var2) {
      if (var1 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         Object var3 = this.elementData(var1);
         this.elementData[var1] = var2;
         return var3;
      }
   }

   public synchronized boolean add(E var1) {
      ++this.modCount;
      this.ensureCapacityHelper(this.elementCount + 1);
      this.elementData[this.elementCount++] = var1;
      return true;
   }

   public boolean remove(Object var1) {
      return this.removeElement(var1);
   }

   public void add(int var1, E var2) {
      this.insertElementAt(var2, var1);
   }

   public synchronized E remove(int var1) {
      ++this.modCount;
      if (var1 >= this.elementCount) {
         throw new ArrayIndexOutOfBoundsException(var1);
      } else {
         Object var2 = this.elementData(var1);
         int var3 = this.elementCount - var1 - 1;
         if (var3 > 0) {
            System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var3);
         }

         this.elementData[--this.elementCount] = null;
         return var2;
      }
   }

   public void clear() {
      this.removeAllElements();
   }

   public synchronized boolean containsAll(Collection<?> var1) {
      return super.containsAll(var1);
   }

   public synchronized boolean addAll(Collection<? extends E> var1) {
      ++this.modCount;
      Object[] var2 = var1.toArray();
      int var3 = var2.length;
      this.ensureCapacityHelper(this.elementCount + var3);
      System.arraycopy(var2, 0, this.elementData, this.elementCount, var3);
      this.elementCount += var3;
      return var3 != 0;
   }

   public synchronized boolean removeAll(Collection<?> var1) {
      return super.removeAll(var1);
   }

   public synchronized boolean retainAll(Collection<?> var1) {
      return super.retainAll(var1);
   }

   public synchronized boolean addAll(int var1, Collection<? extends E> var2) {
      ++this.modCount;
      if (var1 >= 0 && var1 <= this.elementCount) {
         Object[] var3 = var2.toArray();
         int var4 = var3.length;
         this.ensureCapacityHelper(this.elementCount + var4);
         int var5 = this.elementCount - var1;
         if (var5 > 0) {
            System.arraycopy(this.elementData, var1, this.elementData, var1 + var4, var5);
         }

         System.arraycopy(var3, 0, this.elementData, var1, var4);
         this.elementCount += var4;
         return var4 != 0;
      } else {
         throw new ArrayIndexOutOfBoundsException(var1);
      }
   }

   public synchronized boolean equals(Object var1) {
      return super.equals(var1);
   }

   public synchronized int hashCode() {
      return super.hashCode();
   }

   public synchronized String toString() {
      return super.toString();
   }

   public synchronized List<E> subList(int var1, int var2) {
      return Collections.synchronizedList(super.subList(var1, var2), this);
   }

   protected synchronized void removeRange(int var1, int var2) {
      ++this.modCount;
      int var3 = this.elementCount - var2;
      System.arraycopy(this.elementData, var2, this.elementData, var1, var3);

      for(int var4 = this.elementCount - (var2 - var1); this.elementCount != var4; this.elementData[--this.elementCount] = null) {
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField var2 = var1.readFields();
      int var3 = var2.get("elementCount", (int)0);
      Object[] var4 = (Object[])((Object[])var2.get("elementData", (Object)null));
      if (var3 >= 0 && var4 != null && var3 <= var4.length) {
         this.elementCount = var3;
         this.elementData = (Object[])var4.clone();
      } else {
         throw new StreamCorruptedException("Inconsistent vector internals");
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ObjectOutputStream.PutField var2 = var1.putFields();
      Object[] var3;
      synchronized(this) {
         var2.put("capacityIncrement", this.capacityIncrement);
         var2.put("elementCount", this.elementCount);
         var3 = (Object[])this.elementData.clone();
      }

      var2.put("elementData", var3);
      var1.writeFields();
   }

   public synchronized ListIterator<E> listIterator(int var1) {
      if (var1 >= 0 && var1 <= this.elementCount) {
         return new Vector.ListItr(var1);
      } else {
         throw new IndexOutOfBoundsException("Index: " + var1);
      }
   }

   public synchronized ListIterator<E> listIterator() {
      return new Vector.ListItr(0);
   }

   public synchronized Iterator<E> iterator() {
      return new Vector.Itr();
   }

   public synchronized void forEach(Consumer<? super E> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      Object[] var3 = (Object[])this.elementData;
      int var4 = this.elementCount;

      for(int var5 = 0; this.modCount == var2 && var5 < var4; ++var5) {
         var1.accept(var3[var5]);
      }

      if (this.modCount != var2) {
         throw new ConcurrentModificationException();
      }
   }

   public synchronized boolean removeIf(Predicate<? super E> var1) {
      Objects.requireNonNull(var1);
      int var2 = 0;
      int var3 = this.elementCount;
      BitSet var4 = new BitSet(var3);
      int var5 = this.modCount;

      for(int var6 = 0; this.modCount == var5 && var6 < var3; ++var6) {
         Object var7 = this.elementData[var6];
         if (var1.test(var7)) {
            var4.set(var6);
            ++var2;
         }
      }

      if (this.modCount != var5) {
         throw new ConcurrentModificationException();
      } else {
         boolean var10 = var2 > 0;
         if (var10) {
            int var11 = var3 - var2;
            int var8 = 0;

            for(int var9 = 0; var8 < var3 && var9 < var11; ++var9) {
               var8 = var4.nextClearBit(var8);
               this.elementData[var9] = this.elementData[var8];
               ++var8;
            }

            for(var8 = var11; var8 < var3; ++var8) {
               this.elementData[var8] = null;
            }

            this.elementCount = var11;
            if (this.modCount != var5) {
               throw new ConcurrentModificationException();
            }

            ++this.modCount;
         }

         return var10;
      }
   }

   public synchronized void replaceAll(UnaryOperator<E> var1) {
      Objects.requireNonNull(var1);
      int var2 = this.modCount;
      int var3 = this.elementCount;

      for(int var4 = 0; this.modCount == var2 && var4 < var3; ++var4) {
         this.elementData[var4] = var1.apply(this.elementData[var4]);
      }

      if (this.modCount != var2) {
         throw new ConcurrentModificationException();
      } else {
         ++this.modCount;
      }
   }

   public synchronized void sort(Comparator<? super E> var1) {
      int var2 = this.modCount;
      Arrays.sort((Object[])this.elementData, 0, this.elementCount, var1);
      if (this.modCount != var2) {
         throw new ConcurrentModificationException();
      } else {
         ++this.modCount;
      }
   }

   public Spliterator<E> spliterator() {
      return new Vector.VectorSpliterator(this, (Object[])null, 0, -1, 0);
   }

   static final class VectorSpliterator<E> implements Spliterator<E> {
      private final Vector<E> list;
      private Object[] array;
      private int index;
      private int fence;
      private int expectedModCount;

      VectorSpliterator(Vector<E> var1, Object[] var2, int var3, int var4, int var5) {
         this.list = var1;
         this.array = var2;
         this.index = var3;
         this.fence = var4;
         this.expectedModCount = var5;
      }

      private int getFence() {
         int var1;
         if ((var1 = this.fence) < 0) {
            synchronized(this.list) {
               this.array = this.list.elementData;
               this.expectedModCount = this.list.modCount;
               var1 = this.fence = this.list.elementCount;
            }
         }

         return var1;
      }

      public Spliterator<E> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1;
         return var2 >= var3 ? null : new Vector.VectorSpliterator(this.list, this.array, var2, this.index = var3, this.expectedModCount);
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2;
            if (this.getFence() > (var2 = this.index)) {
               this.index = var2 + 1;
               var1.accept(this.array[var2]);
               if (this.list.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               } else {
                  return true;
               }
            } else {
               return false;
            }
         }
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Vector var4;
            if ((var4 = this.list) != null) {
               int var3;
               Object[] var5;
               if ((var3 = this.fence) < 0) {
                  synchronized(var4) {
                     this.expectedModCount = var4.modCount;
                     var5 = this.array = var4.elementData;
                     var3 = this.fence = var4.elementCount;
                  }
               } else {
                  var5 = this.array;
               }

               int var2;
               if (var5 != null && (var2 = this.index) >= 0 && (this.index = var3) <= var5.length) {
                  while(var2 < var3) {
                     var1.accept(var5[var2++]);
                  }

                  if (var4.modCount == this.expectedModCount) {
                     return;
                  }
               }
            }

            throw new ConcurrentModificationException();
         }
      }

      public long estimateSize() {
         return (long)(this.getFence() - this.index);
      }

      public int characteristics() {
         return 16464;
      }
   }

   final class ListItr extends Vector<E>.Itr implements ListIterator<E> {
      ListItr(int var2) {
         super(null);
         this.cursor = var2;
      }

      public boolean hasPrevious() {
         return this.cursor != 0;
      }

      public int nextIndex() {
         return this.cursor;
      }

      public int previousIndex() {
         return this.cursor - 1;
      }

      public E previous() {
         synchronized(Vector.this) {
            this.checkForComodification();
            int var2 = this.cursor - 1;
            if (var2 < 0) {
               throw new NoSuchElementException();
            } else {
               this.cursor = var2;
               return Vector.this.elementData(this.lastRet = var2);
            }
         }
      }

      public void set(E var1) {
         if (this.lastRet == -1) {
            throw new IllegalStateException();
         } else {
            synchronized(Vector.this) {
               this.checkForComodification();
               Vector.this.set(this.lastRet, var1);
            }
         }
      }

      public void add(E var1) {
         int var2 = this.cursor;
         synchronized(Vector.this) {
            this.checkForComodification();
            Vector.this.add(var2, var1);
            this.expectedModCount = Vector.this.modCount;
         }

         this.cursor = var2 + 1;
         this.lastRet = -1;
      }
   }

   private class Itr implements Iterator<E> {
      int cursor;
      int lastRet;
      int expectedModCount;

      private Itr() {
         this.lastRet = -1;
         this.expectedModCount = Vector.this.modCount;
      }

      public boolean hasNext() {
         return this.cursor != Vector.this.elementCount;
      }

      public E next() {
         synchronized(Vector.this) {
            this.checkForComodification();
            int var2 = this.cursor;
            if (var2 >= Vector.this.elementCount) {
               throw new NoSuchElementException();
            } else {
               this.cursor = var2 + 1;
               return Vector.this.elementData(this.lastRet = var2);
            }
         }
      }

      public void remove() {
         if (this.lastRet == -1) {
            throw new IllegalStateException();
         } else {
            synchronized(Vector.this) {
               this.checkForComodification();
               Vector.this.remove(this.lastRet);
               this.expectedModCount = Vector.this.modCount;
            }

            this.cursor = this.lastRet;
            this.lastRet = -1;
         }
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         Objects.requireNonNull(var1);
         synchronized(Vector.this) {
            int var3 = Vector.this.elementCount;
            int var4 = this.cursor;
            if (var4 < var3) {
               Object[] var5 = (Object[])Vector.this.elementData;
               if (var4 >= var5.length) {
                  throw new ConcurrentModificationException();
               } else {
                  while(var4 != var3 && Vector.this.modCount == this.expectedModCount) {
                     var1.accept(var5[var4++]);
                  }

                  this.cursor = var4;
                  this.lastRet = var4 - 1;
                  this.checkForComodification();
               }
            }
         }
      }

      final void checkForComodification() {
         if (Vector.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      // $FF: synthetic method
      Itr(Object var2) {
         this();
      }
   }
}
