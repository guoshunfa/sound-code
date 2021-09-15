package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

public class PriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
   private static final long serialVersionUID = 5595510919245408276L;
   private static final int DEFAULT_INITIAL_CAPACITY = 11;
   private static final int MAX_ARRAY_SIZE = 2147483639;
   private transient Object[] queue;
   private transient int size;
   private transient Comparator<? super E> comparator;
   private final ReentrantLock lock;
   private final Condition notEmpty;
   private transient volatile int allocationSpinLock;
   private PriorityQueue<E> q;
   private static final Unsafe UNSAFE;
   private static final long allocationSpinLockOffset;

   public PriorityBlockingQueue() {
      this(11, (Comparator)null);
   }

   public PriorityBlockingQueue(int var1) {
      this(var1, (Comparator)null);
   }

   public PriorityBlockingQueue(int var1, Comparator<? super E> var2) {
      if (var1 < 1) {
         throw new IllegalArgumentException();
      } else {
         this.lock = new ReentrantLock();
         this.notEmpty = this.lock.newCondition();
         this.comparator = var2;
         this.queue = new Object[var1];
      }
   }

   public PriorityBlockingQueue(Collection<? extends E> var1) {
      this.lock = new ReentrantLock();
      this.notEmpty = this.lock.newCondition();
      boolean var2 = true;
      boolean var3 = true;
      if (var1 instanceof SortedSet) {
         SortedSet var4 = (SortedSet)var1;
         this.comparator = var4.comparator();
         var2 = false;
      } else if (var1 instanceof PriorityBlockingQueue) {
         PriorityBlockingQueue var7 = (PriorityBlockingQueue)var1;
         this.comparator = var7.comparator();
         var3 = false;
         if (var7.getClass() == PriorityBlockingQueue.class) {
            var2 = false;
         }
      }

      Object[] var8 = var1.toArray();
      int var5 = var8.length;
      if (var8.getClass() != Object[].class) {
         var8 = Arrays.copyOf(var8, var5, Object[].class);
      }

      if (var3 && (var5 == 1 || this.comparator != null)) {
         for(int var6 = 0; var6 < var5; ++var6) {
            if (var8[var6] == null) {
               throw new NullPointerException();
            }
         }
      }

      this.queue = var8;
      this.size = var5;
      if (var2) {
         this.heapify();
      }

   }

   private void tryGrow(Object[] var1, int var2) {
      this.lock.unlock();
      Object[] var3 = null;
      if (this.allocationSpinLock == 0 && UNSAFE.compareAndSwapInt(this, allocationSpinLockOffset, 0, 1)) {
         try {
            int var4 = var2 + (var2 < 64 ? var2 + 2 : var2 >> 1);
            if (var4 - 2147483639 > 0) {
               int var5 = var2 + 1;
               if (var5 < 0 || var5 > 2147483639) {
                  throw new OutOfMemoryError();
               }

               var4 = 2147483639;
            }

            if (var4 > var2 && this.queue == var1) {
               var3 = new Object[var4];
            }
         } finally {
            this.allocationSpinLock = 0;
         }
      }

      if (var3 == null) {
         Thread.yield();
      }

      this.lock.lock();
      if (var3 != null && this.queue == var1) {
         this.queue = var3;
         System.arraycopy(var1, 0, var3, 0, var2);
      }

   }

   private E dequeue() {
      int var1 = this.size - 1;
      if (var1 < 0) {
         return null;
      } else {
         Object[] var2 = this.queue;
         Object var3 = var2[0];
         Object var4 = var2[var1];
         var2[var1] = null;
         Comparator var5 = this.comparator;
         if (var5 == null) {
            siftDownComparable(0, var4, var2, var1);
         } else {
            siftDownUsingComparator(0, var4, var2, var1, var5);
         }

         this.size = var1;
         return var3;
      }
   }

   private static <T> void siftUpComparable(int var0, T var1, Object[] var2) {
      Comparable var3;
      int var4;
      for(var3 = (Comparable)var1; var0 > 0; var0 = var4) {
         var4 = var0 - 1 >>> 1;
         Object var5 = var2[var4];
         if (var3.compareTo(var5) >= 0) {
            break;
         }

         var2[var0] = var5;
      }

      var2[var0] = var3;
   }

   private static <T> void siftUpUsingComparator(int var0, T var1, Object[] var2, Comparator<? super T> var3) {
      while(true) {
         if (var0 > 0) {
            int var4 = var0 - 1 >>> 1;
            Object var5 = var2[var4];
            if (var3.compare(var1, var5) < 0) {
               var2[var0] = var5;
               var0 = var4;
               continue;
            }
         }

         var2[var0] = var1;
         return;
      }
   }

   private static <T> void siftDownComparable(int var0, T var1, Object[] var2, int var3) {
      if (var3 > 0) {
         Comparable var4 = (Comparable)var1;

         int var6;
         for(int var5 = var3 >>> 1; var0 < var5; var0 = var6) {
            var6 = (var0 << 1) + 1;
            Object var7 = var2[var6];
            int var8 = var6 + 1;
            if (var8 < var3 && ((Comparable)var7).compareTo(var2[var8]) > 0) {
               var6 = var8;
               var7 = var2[var8];
            }

            if (var4.compareTo(var7) <= 0) {
               break;
            }

            var2[var0] = var7;
         }

         var2[var0] = var4;
      }

   }

   private static <T> void siftDownUsingComparator(int var0, T var1, Object[] var2, int var3, Comparator<? super T> var4) {
      if (var3 > 0) {
         int var6;
         for(int var5 = var3 >>> 1; var0 < var5; var0 = var6) {
            var6 = (var0 << 1) + 1;
            Object var7 = var2[var6];
            int var8 = var6 + 1;
            if (var8 < var3 && var4.compare(var7, var2[var8]) > 0) {
               var6 = var8;
               var7 = var2[var8];
            }

            if (var4.compare(var1, var7) <= 0) {
               break;
            }

            var2[var0] = var7;
         }

         var2[var0] = var1;
      }

   }

   private void heapify() {
      Object[] var1 = this.queue;
      int var2 = this.size;
      int var3 = (var2 >>> 1) - 1;
      Comparator var4 = this.comparator;
      int var5;
      if (var4 == null) {
         for(var5 = var3; var5 >= 0; --var5) {
            siftDownComparable(var5, var1[var5], var1, var2);
         }
      } else {
         for(var5 = var3; var5 >= 0; --var5) {
            siftDownUsingComparator(var5, var1[var5], var1, var2, var4);
         }
      }

   }

   public boolean add(E var1) {
      return this.offer(var1);
   }

   public boolean offer(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         int var3;
         int var4;
         Object[] var5;
         while((var3 = this.size) >= (var4 = (var5 = this.queue).length)) {
            this.tryGrow(var5, var4);
         }

         try {
            Comparator var6 = this.comparator;
            if (var6 == null) {
               siftUpComparable(var3, var1, var5);
            } else {
               siftUpUsingComparator(var3, var1, var5, var6);
            }

            this.size = var3 + 1;
            this.notEmpty.signal();
         } finally {
            var2.unlock();
         }

         return true;
      }
   }

   public void put(E var1) {
      this.offer(var1);
   }

   public boolean offer(E var1, long var2, TimeUnit var4) {
      return this.offer(var1);
   }

   public E poll() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.dequeue();
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public E take() throws InterruptedException {
      ReentrantLock var1 = this.lock;
      var1.lockInterruptibly();

      Object var2;
      try {
         while((var2 = this.dequeue()) == null) {
            this.notEmpty.await();
         }
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = var3.toNanos(var1);
      ReentrantLock var6 = this.lock;
      var6.lockInterruptibly();

      Object var7;
      try {
         while((var7 = this.dequeue()) == null && var4 > 0L) {
            var4 = this.notEmpty.awaitNanos(var4);
         }
      } finally {
         var6.unlock();
      }

      return var7;
   }

   public E peek() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.size == 0 ? null : this.queue[0];
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public Comparator<? super E> comparator() {
      return this.comparator;
   }

   public int size() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      int var2;
      try {
         var2 = this.size;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public int remainingCapacity() {
      return Integer.MAX_VALUE;
   }

   private int indexOf(Object var1) {
      if (var1 != null) {
         Object[] var2 = this.queue;
         int var3 = this.size;

         for(int var4 = 0; var4 < var3; ++var4) {
            if (var1.equals(var2[var4])) {
               return var4;
            }
         }
      }

      return -1;
   }

   private void removeAt(int var1) {
      Object[] var2 = this.queue;
      int var3 = this.size - 1;
      if (var3 == var1) {
         var2[var1] = null;
      } else {
         Object var4 = var2[var3];
         var2[var3] = null;
         Comparator var5 = this.comparator;
         if (var5 == null) {
            siftDownComparable(var1, var4, var2, var3);
         } else {
            siftDownUsingComparator(var1, var4, var2, var3, var5);
         }

         if (var2[var1] == var4) {
            if (var5 == null) {
               siftUpComparable(var1, var4, var2);
            } else {
               siftUpUsingComparator(var1, var4, var2, var5);
            }
         }
      }

      this.size = var3;
   }

   public boolean remove(Object var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      boolean var4;
      try {
         int var3 = this.indexOf(var1);
         if (var3 != -1) {
            this.removeAt(var3);
            var4 = true;
            return var4;
         }

         var4 = false;
      } finally {
         var2.unlock();
      }

      return var4;
   }

   void removeEQ(Object var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      try {
         Object[] var3 = this.queue;
         int var4 = 0;

         for(int var5 = this.size; var4 < var5; ++var4) {
            if (var1 == var3[var4]) {
               this.removeAt(var4);
               break;
            }
         }
      } finally {
         var2.unlock();
      }

   }

   public boolean contains(Object var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      boolean var3;
      try {
         var3 = this.indexOf(var1) != -1;
      } finally {
         var2.unlock();
      }

      return var3;
   }

   public Object[] toArray() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object[] var2;
      try {
         var2 = Arrays.copyOf(this.queue, this.size);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public String toString() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         int var2 = this.size;
         if (var2 == 0) {
            String var9 = "[]";
            return var9;
         } else {
            StringBuilder var3 = new StringBuilder();
            var3.append('[');

            for(int var4 = 0; var4 < var2; ++var4) {
               Object var5 = this.queue[var4];
               var3.append(var5 == this ? "(this Collection)" : var5);
               if (var4 != var2 - 1) {
                  var3.append(',').append(' ');
               }
            }

            String var10 = var3.append(']').toString();
            return var10;
         }
      } finally {
         var1.unlock();
      }
   }

   public int drainTo(Collection<? super E> var1) {
      return this.drainTo(var1, Integer.MAX_VALUE);
   }

   public int drainTo(Collection<? super E> var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 == this) {
         throw new IllegalArgumentException();
      } else if (var2 <= 0) {
         return 0;
      } else {
         ReentrantLock var3 = this.lock;
         var3.lock();

         try {
            int var4 = Math.min(this.size, var2);

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
               var1.add(this.queue[0]);
               this.dequeue();
            }

            var5 = var4;
            return var5;
         } finally {
            var3.unlock();
         }
      }
   }

   public void clear() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         Object[] var2 = this.queue;
         int var3 = this.size;
         this.size = 0;

         for(int var4 = 0; var4 < var3; ++var4) {
            var2[var4] = null;
         }
      } finally {
         var1.unlock();
      }

   }

   public <T> T[] toArray(T[] var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      Object[] var4;
      try {
         int var3 = this.size;
         if (var1.length < var3) {
            var4 = (Object[])Arrays.copyOf(this.queue, this.size, var1.getClass());
            return var4;
         }

         System.arraycopy(this.queue, 0, var1, 0, var3);
         if (var1.length > var3) {
            var1[var3] = null;
         }

         var4 = var1;
      } finally {
         var2.unlock();
      }

      return var4;
   }

   public Iterator<E> iterator() {
      return new PriorityBlockingQueue.Itr(this.toArray());
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.lock.lock();

      try {
         this.q = new PriorityQueue(Math.max(this.size, 1), this.comparator);
         this.q.addAll(this);
         var1.defaultWriteObject();
      } finally {
         this.q = null;
         this.lock.unlock();
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      try {
         var1.defaultReadObject();
         int var2 = this.q.size();
         SharedSecrets.getJavaOISAccess().checkArray(var1, Object[].class, var2);
         this.queue = new Object[var2];
         this.comparator = this.q.comparator();
         this.addAll(this.q);
      } finally {
         this.q = null;
      }

   }

   public Spliterator<E> spliterator() {
      return new PriorityBlockingQueue.PBQSpliterator(this, (Object[])null, 0, -1);
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = PriorityBlockingQueue.class;
         allocationSpinLockOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("allocationSpinLock"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class PBQSpliterator<E> implements Spliterator<E> {
      final PriorityBlockingQueue<E> queue;
      Object[] array;
      int index;
      int fence;

      PBQSpliterator(PriorityBlockingQueue<E> var1, Object[] var2, int var3, int var4) {
         this.queue = var1;
         this.array = var2;
         this.index = var3;
         this.fence = var4;
      }

      final int getFence() {
         int var1;
         if ((var1 = this.fence) < 0) {
            var1 = this.fence = (this.array = this.queue.toArray()).length;
         }

         return var1;
      }

      public Spliterator<E> trySplit() {
         int var1 = this.getFence();
         int var2 = this.index;
         int var3 = var2 + var1 >>> 1;
         return var2 >= var3 ? null : new PriorityBlockingQueue.PBQSpliterator(this.queue, this.array, var2, this.index = var3);
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            Object[] var2;
            if ((var2 = this.array) == null) {
               this.fence = (var2 = this.queue.toArray()).length;
            }

            int var3;
            int var4;
            if ((var4 = this.fence) <= var2.length && (var3 = this.index) >= 0 && var3 < (this.index = var4)) {
               do {
                  var1.accept(var2[var3]);
                  ++var3;
               } while(var3 < var4);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (this.getFence() > this.index && this.index >= 0) {
            Object var2 = this.array[this.index++];
            var1.accept(var2);
            return true;
         } else {
            return false;
         }
      }

      public long estimateSize() {
         return (long)(this.getFence() - this.index);
      }

      public int characteristics() {
         return 16704;
      }
   }

   final class Itr implements Iterator<E> {
      final Object[] array;
      int cursor;
      int lastRet = -1;

      Itr(Object[] var2) {
         this.array = var2;
      }

      public boolean hasNext() {
         return this.cursor < this.array.length;
      }

      public E next() {
         if (this.cursor >= this.array.length) {
            throw new NoSuchElementException();
         } else {
            this.lastRet = this.cursor;
            return this.array[this.cursor++];
         }
      }

      public void remove() {
         if (this.lastRet < 0) {
            throw new IllegalStateException();
         } else {
            PriorityBlockingQueue.this.removeEQ(this.array[this.lastRet]);
            this.lastRet = -1;
         }
      }
   }
}
