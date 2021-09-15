package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Consumer;
import sun.misc.SharedSecrets;

public class PriorityQueue<E> extends AbstractQueue<E> implements Serializable {
   private static final long serialVersionUID = -7720805057305804111L;
   private static final int DEFAULT_INITIAL_CAPACITY = 11;
   transient Object[] queue;
   private int size;
   private final Comparator<? super E> comparator;
   transient int modCount;
   private static final int MAX_ARRAY_SIZE = 2147483639;

   public PriorityQueue() {
      this(11, (Comparator)null);
   }

   public PriorityQueue(int var1) {
      this(var1, (Comparator)null);
   }

   public PriorityQueue(Comparator<? super E> var1) {
      this(11, var1);
   }

   public PriorityQueue(int var1, Comparator<? super E> var2) {
      this.size = 0;
      this.modCount = 0;
      if (var1 < 1) {
         throw new IllegalArgumentException();
      } else {
         this.queue = new Object[var1];
         this.comparator = var2;
      }
   }

   public PriorityQueue(Collection<? extends E> var1) {
      this.size = 0;
      this.modCount = 0;
      if (var1 instanceof SortedSet) {
         SortedSet var2 = (SortedSet)var1;
         this.comparator = var2.comparator();
         this.initElementsFromCollection(var2);
      } else if (var1 instanceof PriorityQueue) {
         PriorityQueue var3 = (PriorityQueue)var1;
         this.comparator = var3.comparator();
         this.initFromPriorityQueue(var3);
      } else {
         this.comparator = null;
         this.initFromCollection(var1);
      }

   }

   public PriorityQueue(PriorityQueue<? extends E> var1) {
      this.size = 0;
      this.modCount = 0;
      this.comparator = var1.comparator();
      this.initFromPriorityQueue(var1);
   }

   public PriorityQueue(SortedSet<? extends E> var1) {
      this.size = 0;
      this.modCount = 0;
      this.comparator = var1.comparator();
      this.initElementsFromCollection(var1);
   }

   private void initFromPriorityQueue(PriorityQueue<? extends E> var1) {
      if (var1.getClass() == PriorityQueue.class) {
         this.queue = var1.toArray();
         this.size = var1.size();
      } else {
         this.initFromCollection(var1);
      }

   }

   private void initElementsFromCollection(Collection<? extends E> var1) {
      Object[] var2 = var1.toArray();
      if (var2.getClass() != Object[].class) {
         var2 = Arrays.copyOf(var2, var2.length, Object[].class);
      }

      int var3 = var2.length;
      if (var3 == 1 || this.comparator != null) {
         for(int var4 = 0; var4 < var3; ++var4) {
            if (var2[var4] == null) {
               throw new NullPointerException();
            }
         }
      }

      this.queue = var2;
      this.size = var2.length;
   }

   private void initFromCollection(Collection<? extends E> var1) {
      this.initElementsFromCollection(var1);
      this.heapify();
   }

   private void grow(int var1) {
      int var2 = this.queue.length;
      int var3 = var2 + (var2 < 64 ? var2 + 2 : var2 >> 1);
      if (var3 - 2147483639 > 0) {
         var3 = hugeCapacity(var1);
      }

      this.queue = Arrays.copyOf(this.queue, var3);
   }

   private static int hugeCapacity(int var0) {
      if (var0 < 0) {
         throw new OutOfMemoryError();
      } else {
         return var0 > 2147483639 ? Integer.MAX_VALUE : 2147483639;
      }
   }

   public boolean add(E var1) {
      return this.offer(var1);
   }

   public boolean offer(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ++this.modCount;
         int var2 = this.size;
         if (var2 >= this.queue.length) {
            this.grow(var2 + 1);
         }

         this.size = var2 + 1;
         if (var2 == 0) {
            this.queue[0] = var1;
         } else {
            this.siftUp(var2, var1);
         }

         return true;
      }
   }

   public E peek() {
      return this.size == 0 ? null : this.queue[0];
   }

   private int indexOf(Object var1) {
      if (var1 != null) {
         for(int var2 = 0; var2 < this.size; ++var2) {
            if (var1.equals(this.queue[var2])) {
               return var2;
            }
         }
      }

      return -1;
   }

   public boolean remove(Object var1) {
      int var2 = this.indexOf(var1);
      if (var2 == -1) {
         return false;
      } else {
         this.removeAt(var2);
         return true;
      }
   }

   boolean removeEq(Object var1) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         if (var1 == this.queue[var2]) {
            this.removeAt(var2);
            return true;
         }
      }

      return false;
   }

   public boolean contains(Object var1) {
      return this.indexOf(var1) != -1;
   }

   public Object[] toArray() {
      return Arrays.copyOf(this.queue, this.size);
   }

   public <T> T[] toArray(T[] var1) {
      int var2 = this.size;
      if (var1.length < var2) {
         return (Object[])Arrays.copyOf(this.queue, var2, var1.getClass());
      } else {
         System.arraycopy(this.queue, 0, var1, 0, var2);
         if (var1.length > var2) {
            var1[var2] = null;
         }

         return var1;
      }
   }

   public Iterator<E> iterator() {
      return new PriorityQueue.Itr();
   }

   public int size() {
      return this.size;
   }

   public void clear() {
      ++this.modCount;

      for(int var1 = 0; var1 < this.size; ++var1) {
         this.queue[var1] = null;
      }

      this.size = 0;
   }

   public E poll() {
      if (this.size == 0) {
         return null;
      } else {
         int var1 = --this.size;
         ++this.modCount;
         Object var2 = this.queue[0];
         Object var3 = this.queue[var1];
         this.queue[var1] = null;
         if (var1 != 0) {
            this.siftDown(0, var3);
         }

         return var2;
      }
   }

   private E removeAt(int var1) {
      ++this.modCount;
      int var2 = --this.size;
      if (var2 == var1) {
         this.queue[var1] = null;
      } else {
         Object var3 = this.queue[var2];
         this.queue[var2] = null;
         this.siftDown(var1, var3);
         if (this.queue[var1] == var3) {
            this.siftUp(var1, var3);
            if (this.queue[var1] != var3) {
               return var3;
            }
         }
      }

      return null;
   }

   private void siftUp(int var1, E var2) {
      if (this.comparator != null) {
         this.siftUpUsingComparator(var1, var2);
      } else {
         this.siftUpComparable(var1, var2);
      }

   }

   private void siftUpComparable(int var1, E var2) {
      Comparable var3;
      int var4;
      for(var3 = (Comparable)var2; var1 > 0; var1 = var4) {
         var4 = var1 - 1 >>> 1;
         Object var5 = this.queue[var4];
         if (var3.compareTo(var5) >= 0) {
            break;
         }

         this.queue[var1] = var5;
      }

      this.queue[var1] = var3;
   }

   private void siftUpUsingComparator(int var1, E var2) {
      while(true) {
         if (var1 > 0) {
            int var3 = var1 - 1 >>> 1;
            Object var4 = this.queue[var3];
            if (this.comparator.compare(var2, var4) < 0) {
               this.queue[var1] = var4;
               var1 = var3;
               continue;
            }
         }

         this.queue[var1] = var2;
         return;
      }
   }

   private void siftDown(int var1, E var2) {
      if (this.comparator != null) {
         this.siftDownUsingComparator(var1, var2);
      } else {
         this.siftDownComparable(var1, var2);
      }

   }

   private void siftDownComparable(int var1, E var2) {
      Comparable var3 = (Comparable)var2;

      int var5;
      for(int var4 = this.size >>> 1; var1 < var4; var1 = var5) {
         var5 = (var1 << 1) + 1;
         Object var6 = this.queue[var5];
         int var7 = var5 + 1;
         if (var7 < this.size && ((Comparable)var6).compareTo(this.queue[var7]) > 0) {
            var5 = var7;
            var6 = this.queue[var7];
         }

         if (var3.compareTo(var6) <= 0) {
            break;
         }

         this.queue[var1] = var6;
      }

      this.queue[var1] = var3;
   }

   private void siftDownUsingComparator(int var1, E var2) {
      int var4;
      for(int var3 = this.size >>> 1; var1 < var3; var1 = var4) {
         var4 = (var1 << 1) + 1;
         Object var5 = this.queue[var4];
         int var6 = var4 + 1;
         if (var6 < this.size && this.comparator.compare(var5, this.queue[var6]) > 0) {
            var4 = var6;
            var5 = this.queue[var6];
         }

         if (this.comparator.compare(var2, var5) <= 0) {
            break;
         }

         this.queue[var1] = var5;
      }

      this.queue[var1] = var2;
   }

   private void heapify() {
      for(int var1 = (this.size >>> 1) - 1; var1 >= 0; --var1) {
         this.siftDown(var1, this.queue[var1]);
      }

   }

   public Comparator<? super E> comparator() {
      return this.comparator;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeInt(Math.max(2, this.size + 1));

      for(int var2 = 0; var2 < this.size; ++var2) {
         var1.writeObject(this.queue[var2]);
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      var1.readInt();
      SharedSecrets.getJavaOISAccess().checkArray(var1, Object[].class, this.size);
      this.queue = new Object[this.size];

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.queue[var2] = var1.readObject();
      }

      this.heapify();
   }

   public final Spliterator<E> spliterator() {
      return new PriorityQueue.PriorityQueueSpliterator(this, 0, -1, 0);
   }

   static final class PriorityQueueSpliterator<E> implements Spliterator<E> {
      private final PriorityQueue<E> pq;
      private int index;
      private int fence;
      private int expectedModCount;

      PriorityQueueSpliterator(PriorityQueue<E> var1, int var2, int var3, int var4) {
         this.pq = var1;
         this.index = var2;
         this.fence = var3;
         this.expectedModCount = var4;
      }

      private int getFence() {
         int var1;
         if ((var1 = this.fence) < 0) {
            this.expectedModCount = this.pq.modCount;
            var1 = this.fence = this.pq.size;
         }

         return var1;
      }

      public PriorityQueue.PriorityQueueSpliterator<E> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1;
         return var2 >= var3 ? null : new PriorityQueue.PriorityQueueSpliterator(this.pq, var2, this.index = var3, this.expectedModCount);
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            PriorityQueue var5;
            Object[] var6;
            if ((var5 = this.pq) != null && (var6 = var5.queue) != null) {
               int var3;
               int var4;
               if ((var3 = this.fence) < 0) {
                  var4 = var5.modCount;
                  var3 = var5.size;
               } else {
                  var4 = this.expectedModCount;
               }

               int var2;
               if ((var2 = this.index) >= 0 && (this.index = var3) <= var6.length) {
                  while(true) {
                     if (var2 >= var3) {
                        if (var5.modCount == var4) {
                           return;
                        }
                        break;
                     }

                     Object var7;
                     if ((var7 = var6[var2]) == null) {
                        break;
                     }

                     var1.accept(var7);
                     ++var2;
                  }
               }
            }

            throw new ConcurrentModificationException();
         }
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            int var2 = this.getFence();
            int var3 = this.index;
            if (var3 >= 0 && var3 < var2) {
               this.index = var3 + 1;
               Object var4 = this.pq.queue[var3];
               if (var4 == null) {
                  throw new ConcurrentModificationException();
               } else {
                  var1.accept(var4);
                  if (this.pq.modCount != this.expectedModCount) {
                     throw new ConcurrentModificationException();
                  } else {
                     return true;
                  }
               }
            } else {
               return false;
            }
         }
      }

      public long estimateSize() {
         return (long)(this.getFence() - this.index);
      }

      public int characteristics() {
         return 16704;
      }
   }

   private final class Itr implements Iterator<E> {
      private int cursor;
      private int lastRet;
      private ArrayDeque<E> forgetMeNot;
      private E lastRetElt;
      private int expectedModCount;

      private Itr() {
         this.cursor = 0;
         this.lastRet = -1;
         this.forgetMeNot = null;
         this.lastRetElt = null;
         this.expectedModCount = PriorityQueue.this.modCount;
      }

      public boolean hasNext() {
         return this.cursor < PriorityQueue.this.size || this.forgetMeNot != null && !this.forgetMeNot.isEmpty();
      }

      public E next() {
         if (this.expectedModCount != PriorityQueue.this.modCount) {
            throw new ConcurrentModificationException();
         } else if (this.cursor < PriorityQueue.this.size) {
            return PriorityQueue.this.queue[this.lastRet = this.cursor++];
         } else {
            if (this.forgetMeNot != null) {
               this.lastRet = -1;
               this.lastRetElt = this.forgetMeNot.poll();
               if (this.lastRetElt != null) {
                  return this.lastRetElt;
               }
            }

            throw new NoSuchElementException();
         }
      }

      public void remove() {
         if (this.expectedModCount != PriorityQueue.this.modCount) {
            throw new ConcurrentModificationException();
         } else {
            if (this.lastRet != -1) {
               Object var1 = PriorityQueue.this.removeAt(this.lastRet);
               this.lastRet = -1;
               if (var1 == null) {
                  --this.cursor;
               } else {
                  if (this.forgetMeNot == null) {
                     this.forgetMeNot = new ArrayDeque();
                  }

                  this.forgetMeNot.add(var1);
               }
            } else {
               if (this.lastRetElt == null) {
                  throw new IllegalStateException();
               }

               PriorityQueue.this.removeEq(this.lastRetElt);
               this.lastRetElt = null;
            }

            this.expectedModCount = PriorityQueue.this.modCount;
         }
      }

      // $FF: synthetic method
      Itr(Object var2) {
         this();
      }
   }
}
