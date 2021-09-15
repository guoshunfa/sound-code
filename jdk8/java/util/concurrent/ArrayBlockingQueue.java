package java.util.concurrent;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
   private static final long serialVersionUID = -817911632652898426L;
   final Object[] items;
   int takeIndex;
   int putIndex;
   int count;
   final ReentrantLock lock;
   private final Condition notEmpty;
   private final Condition notFull;
   transient ArrayBlockingQueue<E>.Itrs itrs;

   final int dec(int var1) {
      return (var1 == 0 ? this.items.length : var1) - 1;
   }

   final E itemAt(int var1) {
      return this.items[var1];
   }

   private static void checkNotNull(Object var0) {
      if (var0 == null) {
         throw new NullPointerException();
      }
   }

   private void enqueue(E var1) {
      Object[] var2 = this.items;
      var2[this.putIndex] = var1;
      if (++this.putIndex == var2.length) {
         this.putIndex = 0;
      }

      ++this.count;
      this.notEmpty.signal();
   }

   private E dequeue() {
      Object[] var1 = this.items;
      Object var2 = var1[this.takeIndex];
      var1[this.takeIndex] = null;
      if (++this.takeIndex == var1.length) {
         this.takeIndex = 0;
      }

      --this.count;
      if (this.itrs != null) {
         this.itrs.elementDequeued();
      }

      this.notFull.signal();
      return var2;
   }

   void removeAt(int var1) {
      Object[] var2 = this.items;
      if (var1 == this.takeIndex) {
         var2[this.takeIndex] = null;
         if (++this.takeIndex == var2.length) {
            this.takeIndex = 0;
         }

         --this.count;
         if (this.itrs != null) {
            this.itrs.elementDequeued();
         }
      } else {
         int var3 = this.putIndex;
         int var4 = var1;

         while(true) {
            int var5 = var4 + 1;
            if (var5 == var2.length) {
               var5 = 0;
            }

            if (var5 == var3) {
               var2[var4] = null;
               this.putIndex = var4;
               --this.count;
               if (this.itrs != null) {
                  this.itrs.removedAt(var1);
               }
               break;
            }

            var2[var4] = var2[var5];
            var4 = var5;
         }
      }

      this.notFull.signal();
   }

   public ArrayBlockingQueue(int var1) {
      this(var1, false);
   }

   public ArrayBlockingQueue(int var1, boolean var2) {
      this.itrs = null;
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.items = new Object[var1];
         this.lock = new ReentrantLock(var2);
         this.notEmpty = this.lock.newCondition();
         this.notFull = this.lock.newCondition();
      }
   }

   public ArrayBlockingQueue(int var1, boolean var2, Collection<? extends E> var3) {
      this(var1, var2);
      ReentrantLock var4 = this.lock;
      var4.lock();

      try {
         int var5 = 0;

         Object var7;
         try {
            for(Iterator var6 = var3.iterator(); var6.hasNext(); this.items[var5++] = var7) {
               var7 = var6.next();
               checkNotNull(var7);
            }
         } catch (ArrayIndexOutOfBoundsException var11) {
            throw new IllegalArgumentException();
         }

         this.count = var5;
         this.putIndex = var5 == var1 ? 0 : var5;
      } finally {
         var4.unlock();
      }

   }

   public boolean add(E var1) {
      return super.add(var1);
   }

   public boolean offer(E var1) {
      checkNotNull(var1);
      ReentrantLock var2 = this.lock;
      var2.lock();

      boolean var3;
      try {
         if (this.count == this.items.length) {
            var3 = false;
            return var3;
         }

         this.enqueue(var1);
         var3 = true;
      } finally {
         var2.unlock();
      }

      return var3;
   }

   public void put(E var1) throws InterruptedException {
      checkNotNull(var1);
      ReentrantLock var2 = this.lock;
      var2.lockInterruptibly();

      try {
         while(this.count == this.items.length) {
            this.notFull.await();
         }

         this.enqueue(var1);
      } finally {
         var2.unlock();
      }

   }

   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      checkNotNull(var1);
      long var5 = var4.toNanos(var2);
      ReentrantLock var7 = this.lock;
      var7.lockInterruptibly();

      try {
         boolean var8;
         while(this.count == this.items.length) {
            if (var5 <= 0L) {
               var8 = false;
               return var8;
            }

            var5 = this.notFull.awaitNanos(var5);
         }

         this.enqueue(var1);
         var8 = true;
         return var8;
      } finally {
         var7.unlock();
      }
   }

   public E poll() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.count == 0 ? null : this.dequeue();
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
         while(this.count == 0) {
            this.notEmpty.await();
         }

         var2 = this.dequeue();
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = var3.toNanos(var1);
      ReentrantLock var6 = this.lock;
      var6.lockInterruptibly();

      try {
         Object var7;
         while(this.count == 0) {
            if (var4 <= 0L) {
               var7 = null;
               return var7;
            }

            var4 = this.notEmpty.awaitNanos(var4);
         }

         var7 = this.dequeue();
         return var7;
      } finally {
         var6.unlock();
      }
   }

   public E peek() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.itemAt(this.takeIndex);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public int size() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      int var2;
      try {
         var2 = this.count;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public int remainingCapacity() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      int var2;
      try {
         var2 = this.items.length - this.count;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public boolean remove(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Object[] var2 = this.items;
         ReentrantLock var3 = this.lock;
         var3.lock();

         boolean var10;
         try {
            if (this.count > 0) {
               int var4 = this.putIndex;
               int var5 = this.takeIndex;

               do {
                  if (var1.equals(var2[var5])) {
                     this.removeAt(var5);
                     boolean var6 = true;
                     return var6;
                  }

                  ++var5;
                  if (var5 == var2.length) {
                     var5 = 0;
                  }
               } while(var5 != var4);
            }

            var10 = false;
         } finally {
            var3.unlock();
         }

         return var10;
      }
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         Object[] var2 = this.items;
         ReentrantLock var3 = this.lock;
         var3.lock();

         try {
            if (this.count > 0) {
               int var4 = this.putIndex;
               int var5 = this.takeIndex;

               do {
                  if (var1.equals(var2[var5])) {
                     boolean var6 = true;
                     return var6;
                  }

                  ++var5;
                  if (var5 == var2.length) {
                     var5 = 0;
                  }
               } while(var5 != var4);
            }

            boolean var10 = false;
            return var10;
         } finally {
            var3.unlock();
         }
      }
   }

   public Object[] toArray() {
      ReentrantLock var2 = this.lock;
      var2.lock();

      Object[] var1;
      try {
         int var3 = this.count;
         var1 = new Object[var3];
         int var4 = this.items.length - this.takeIndex;
         if (var3 <= var4) {
            System.arraycopy(this.items, this.takeIndex, var1, 0, var3);
         } else {
            System.arraycopy(this.items, this.takeIndex, var1, 0, var4);
            System.arraycopy(this.items, 0, var1, var4, var3 - var4);
         }
      } finally {
         var2.unlock();
      }

      return var1;
   }

   public <T> T[] toArray(T[] var1) {
      Object[] var2 = this.items;
      ReentrantLock var3 = this.lock;
      var3.lock();

      try {
         int var4 = this.count;
         int var5 = var1.length;
         if (var5 < var4) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var4));
         }

         int var6 = var2.length - this.takeIndex;
         if (var4 <= var6) {
            System.arraycopy(var2, this.takeIndex, var1, 0, var4);
         } else {
            System.arraycopy(var2, this.takeIndex, var1, 0, var6);
            System.arraycopy(var2, 0, var1, var6, var4 - var6);
         }

         if (var5 > var4) {
            var1[var4] = null;
         }
      } finally {
         var3.unlock();
      }

      return var1;
   }

   public String toString() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         int var2 = this.count;
         if (var2 == 0) {
            String var11 = "[]";
            return var11;
         } else {
            Object[] var3 = this.items;
            StringBuilder var4 = new StringBuilder();
            var4.append('[');
            int var5 = this.takeIndex;

            while(true) {
               Object var6 = var3[var5];
               var4.append(var6 == this ? "(this Collection)" : var6);
               --var2;
               if (var2 == 0) {
                  String var7 = var4.append(']').toString();
                  return var7;
               }

               var4.append(',').append(' ');
               ++var5;
               if (var5 == var3.length) {
                  var5 = 0;
               }
            }
         }
      } finally {
         var1.unlock();
      }
   }

   public void clear() {
      Object[] var1 = this.items;
      ReentrantLock var2 = this.lock;
      var2.lock();

      try {
         int var3 = this.count;
         if (var3 > 0) {
            int var4 = this.putIndex;
            int var5 = this.takeIndex;

            do {
               var1[var5] = null;
               ++var5;
               if (var5 == var1.length) {
                  var5 = 0;
               }
            } while(var5 != var4);

            this.takeIndex = var4;
            this.count = 0;
            if (this.itrs != null) {
               this.itrs.queueIsEmpty();
            }

            while(var3 > 0 && var2.hasWaiters(this.notFull)) {
               this.notFull.signal();
               --var3;
            }
         }
      } finally {
         var2.unlock();
      }

   }

   public int drainTo(Collection<? super E> var1) {
      return this.drainTo(var1, Integer.MAX_VALUE);
   }

   public int drainTo(Collection<? super E> var1, int var2) {
      checkNotNull(var1);
      if (var1 == this) {
         throw new IllegalArgumentException();
      } else if (var2 <= 0) {
         return 0;
      } else {
         Object[] var3 = this.items;
         ReentrantLock var4 = this.lock;
         var4.lock();

         int var17;
         try {
            int var5 = Math.min(var2, this.count);
            int var6 = this.takeIndex;
            int var7 = 0;

            try {
               for(; var7 < var5; ++var7) {
                  Object var8 = var3[var6];
                  var1.add(var8);
                  var3[var6] = null;
                  ++var6;
                  if (var6 == var3.length) {
                     var6 = 0;
                  }
               }

               var17 = var5;
            } finally {
               if (var7 > 0) {
                  this.count -= var7;
                  this.takeIndex = var6;
                  if (this.itrs != null) {
                     if (this.count == 0) {
                        this.itrs.queueIsEmpty();
                     } else if (var7 > var6) {
                        this.itrs.takeIndexWrapped();
                     }
                  }

                  while(var7 > 0 && var4.hasWaiters(this.notFull)) {
                     this.notFull.signal();
                     --var7;
                  }
               }

            }
         } finally {
            var4.unlock();
         }

         return var17;
      }
   }

   public Iterator<E> iterator() {
      return new ArrayBlockingQueue.Itr();
   }

   public Spliterator<E> spliterator() {
      return Spliterators.spliterator((Collection)this, 4368);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.items.length == 0 || this.takeIndex < 0 || this.takeIndex >= this.items.length || this.putIndex < 0 || this.putIndex >= this.items.length || this.count < 0 || this.count > this.items.length || Math.floorMod(this.putIndex - this.takeIndex, this.items.length) != Math.floorMod(this.count, this.items.length)) {
         throw new InvalidObjectException("invariants violated");
      }
   }

   private class Itr implements Iterator<E> {
      private int cursor;
      private E nextItem;
      private int nextIndex;
      private E lastItem;
      private int lastRet = -1;
      private int prevTakeIndex;
      private int prevCycles;
      private static final int NONE = -1;
      private static final int REMOVED = -2;
      private static final int DETACHED = -3;

      Itr() {
         ReentrantLock var2 = ArrayBlockingQueue.this.lock;
         var2.lock();

         try {
            if (ArrayBlockingQueue.this.count == 0) {
               this.cursor = -1;
               this.nextIndex = -1;
               this.prevTakeIndex = -3;
            } else {
               int var3 = ArrayBlockingQueue.this.takeIndex;
               this.prevTakeIndex = var3;
               this.nextItem = ArrayBlockingQueue.this.itemAt(this.nextIndex = var3);
               this.cursor = this.incCursor(var3);
               if (ArrayBlockingQueue.this.itrs == null) {
                  ArrayBlockingQueue.this.itrs = ArrayBlockingQueue.this.new Itrs(this);
               } else {
                  ArrayBlockingQueue.this.itrs.register(this);
                  ArrayBlockingQueue.this.itrs.doSomeSweeping(false);
               }

               this.prevCycles = ArrayBlockingQueue.this.itrs.cycles;
            }
         } finally {
            var2.unlock();
         }

      }

      boolean isDetached() {
         return this.prevTakeIndex < 0;
      }

      private int incCursor(int var1) {
         ++var1;
         if (var1 == ArrayBlockingQueue.this.items.length) {
            var1 = 0;
         }

         if (var1 == ArrayBlockingQueue.this.putIndex) {
            var1 = -1;
         }

         return var1;
      }

      private boolean invalidated(int var1, int var2, long var3, int var5) {
         if (var1 < 0) {
            return false;
         } else {
            int var6 = var1 - var2;
            if (var6 < 0) {
               var6 += var5;
            }

            return var3 > (long)var6;
         }
      }

      private void incorporateDequeues() {
         int var1 = ArrayBlockingQueue.this.itrs.cycles;
         int var2 = ArrayBlockingQueue.this.takeIndex;
         int var3 = this.prevCycles;
         int var4 = this.prevTakeIndex;
         if (var1 != var3 || var2 != var4) {
            int var5 = ArrayBlockingQueue.this.items.length;
            long var6 = (long)((var1 - var3) * var5 + (var2 - var4));
            if (this.invalidated(this.lastRet, var4, var6, var5)) {
               this.lastRet = -2;
            }

            if (this.invalidated(this.nextIndex, var4, var6, var5)) {
               this.nextIndex = -2;
            }

            if (this.invalidated(this.cursor, var4, var6, var5)) {
               this.cursor = var2;
            }

            if (this.cursor < 0 && this.nextIndex < 0 && this.lastRet < 0) {
               this.detach();
            } else {
               this.prevCycles = var1;
               this.prevTakeIndex = var2;
            }
         }

      }

      private void detach() {
         if (this.prevTakeIndex >= 0) {
            this.prevTakeIndex = -3;
            ArrayBlockingQueue.this.itrs.doSomeSweeping(true);
         }

      }

      public boolean hasNext() {
         if (this.nextItem != null) {
            return true;
         } else {
            this.noNext();
            return false;
         }
      }

      private void noNext() {
         ReentrantLock var1 = ArrayBlockingQueue.this.lock;
         var1.lock();

         try {
            if (!this.isDetached()) {
               this.incorporateDequeues();
               if (this.lastRet >= 0) {
                  this.lastItem = ArrayBlockingQueue.this.itemAt(this.lastRet);
                  this.detach();
               }
            }
         } finally {
            var1.unlock();
         }

      }

      public E next() {
         Object var1 = this.nextItem;
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            ReentrantLock var2 = ArrayBlockingQueue.this.lock;
            var2.lock();

            try {
               if (!this.isDetached()) {
                  this.incorporateDequeues();
               }

               this.lastRet = this.nextIndex;
               int var3 = this.cursor;
               if (var3 >= 0) {
                  this.nextItem = ArrayBlockingQueue.this.itemAt(this.nextIndex = var3);
                  this.cursor = this.incCursor(var3);
               } else {
                  this.nextIndex = -1;
                  this.nextItem = null;
               }
            } finally {
               var2.unlock();
            }

            return var1;
         }
      }

      public void remove() {
         ReentrantLock var1 = ArrayBlockingQueue.this.lock;
         var1.lock();

         try {
            if (!this.isDetached()) {
               this.incorporateDequeues();
            }

            int var2 = this.lastRet;
            this.lastRet = -1;
            if (var2 >= 0) {
               if (!this.isDetached()) {
                  ArrayBlockingQueue.this.removeAt(var2);
               } else {
                  Object var3 = this.lastItem;
                  this.lastItem = null;
                  if (ArrayBlockingQueue.this.itemAt(var2) == var3) {
                     ArrayBlockingQueue.this.removeAt(var2);
                  }
               }
            } else if (var2 == -1) {
               throw new IllegalStateException();
            }

            if (this.cursor < 0 && this.nextIndex < 0) {
               this.detach();
            }
         } finally {
            var1.unlock();
         }

      }

      void shutdown() {
         this.cursor = -1;
         if (this.nextIndex >= 0) {
            this.nextIndex = -2;
         }

         if (this.lastRet >= 0) {
            this.lastRet = -2;
            this.lastItem = null;
         }

         this.prevTakeIndex = -3;
      }

      private int distance(int var1, int var2, int var3) {
         int var4 = var1 - var2;
         if (var4 < 0) {
            var4 += var3;
         }

         return var4;
      }

      boolean removedAt(int var1) {
         if (this.isDetached()) {
            return true;
         } else {
            int var2 = ArrayBlockingQueue.this.itrs.cycles;
            int var3 = ArrayBlockingQueue.this.takeIndex;
            int var4 = this.prevCycles;
            int var5 = this.prevTakeIndex;
            int var6 = ArrayBlockingQueue.this.items.length;
            int var7 = var2 - var4;
            if (var1 < var3) {
               ++var7;
            }

            int var8 = var7 * var6 + (var1 - var5);
            int var9 = this.cursor;
            int var10;
            if (var9 >= 0) {
               var10 = this.distance(var9, var5, var6);
               if (var10 == var8) {
                  if (var9 == ArrayBlockingQueue.this.putIndex) {
                     var9 = -1;
                     this.cursor = -1;
                  }
               } else if (var10 > var8) {
                  this.cursor = var9 = ArrayBlockingQueue.this.dec(var9);
               }
            }

            var10 = this.lastRet;
            int var11;
            if (var10 >= 0) {
               var11 = this.distance(var10, var5, var6);
               if (var11 == var8) {
                  var10 = -2;
                  this.lastRet = -2;
               } else if (var11 > var8) {
                  this.lastRet = var10 = ArrayBlockingQueue.this.dec(var10);
               }
            }

            var11 = this.nextIndex;
            if (var11 >= 0) {
               int var12 = this.distance(var11, var5, var6);
               if (var12 == var8) {
                  boolean var13 = true;
                  this.nextIndex = -2;
               } else if (var12 > var8) {
                  this.nextIndex = ArrayBlockingQueue.this.dec(var11);
               }
            } else if (var9 < 0 && var11 < 0 && var10 < 0) {
               this.prevTakeIndex = -3;
               return true;
            }

            return false;
         }
      }

      boolean takeIndexWrapped() {
         if (this.isDetached()) {
            return true;
         } else if (ArrayBlockingQueue.this.itrs.cycles - this.prevCycles > 1) {
            this.shutdown();
            return true;
         } else {
            return false;
         }
      }
   }

   class Itrs {
      int cycles = 0;
      private ArrayBlockingQueue<E>.Itrs.Node head;
      private ArrayBlockingQueue<E>.Itrs.Node sweeper = null;
      private static final int SHORT_SWEEP_PROBES = 4;
      private static final int LONG_SWEEP_PROBES = 16;

      Itrs(ArrayBlockingQueue<E>.Itr var2) {
         this.register(var2);
      }

      void doSomeSweeping(boolean var1) {
         int var2 = var1 ? 16 : 4;
         ArrayBlockingQueue.Itrs.Node var5 = this.sweeper;
         ArrayBlockingQueue.Itrs.Node var3;
         ArrayBlockingQueue.Itrs.Node var4;
         boolean var6;
         if (var5 == null) {
            var3 = null;
            var4 = this.head;
            var6 = true;
         } else {
            var3 = var5;
            var4 = var5.next;
            var6 = false;
         }

         while(var2 > 0) {
            if (var4 == null) {
               if (var6) {
                  break;
               }

               var3 = null;
               var4 = this.head;
               var6 = true;
            }

            ArrayBlockingQueue.Itr var7 = (ArrayBlockingQueue.Itr)var4.get();
            ArrayBlockingQueue.Itrs.Node var8 = var4.next;
            if (var7 != null && !var7.isDetached()) {
               var3 = var4;
            } else {
               var2 = 16;
               var4.clear();
               var4.next = null;
               if (var3 == null) {
                  this.head = var8;
                  if (var8 == null) {
                     ArrayBlockingQueue.this.itrs = null;
                     return;
                  }
               } else {
                  var3.next = var8;
               }
            }

            var4 = var8;
            --var2;
         }

         this.sweeper = var4 == null ? null : var3;
      }

      void register(ArrayBlockingQueue<E>.Itr var1) {
         this.head = new ArrayBlockingQueue.Itrs.Node(var1, this.head);
      }

      void takeIndexWrapped() {
         ++this.cycles;
         ArrayBlockingQueue.Itrs.Node var1 = null;

         ArrayBlockingQueue.Itrs.Node var4;
         for(ArrayBlockingQueue.Itrs.Node var2 = this.head; var2 != null; var2 = var4) {
            ArrayBlockingQueue.Itr var3 = (ArrayBlockingQueue.Itr)var2.get();
            var4 = var2.next;
            if (var3 != null && !var3.takeIndexWrapped()) {
               var1 = var2;
            } else {
               var2.clear();
               var2.next = null;
               if (var1 == null) {
                  this.head = var4;
               } else {
                  var1.next = var4;
               }
            }
         }

         if (this.head == null) {
            ArrayBlockingQueue.this.itrs = null;
         }

      }

      void removedAt(int var1) {
         ArrayBlockingQueue.Itrs.Node var2 = null;

         ArrayBlockingQueue.Itrs.Node var5;
         for(ArrayBlockingQueue.Itrs.Node var3 = this.head; var3 != null; var3 = var5) {
            ArrayBlockingQueue.Itr var4 = (ArrayBlockingQueue.Itr)var3.get();
            var5 = var3.next;
            if (var4 != null && !var4.removedAt(var1)) {
               var2 = var3;
            } else {
               var3.clear();
               var3.next = null;
               if (var2 == null) {
                  this.head = var5;
               } else {
                  var2.next = var5;
               }
            }
         }

         if (this.head == null) {
            ArrayBlockingQueue.this.itrs = null;
         }

      }

      void queueIsEmpty() {
         for(ArrayBlockingQueue.Itrs.Node var1 = this.head; var1 != null; var1 = var1.next) {
            ArrayBlockingQueue.Itr var2 = (ArrayBlockingQueue.Itr)var1.get();
            if (var2 != null) {
               var1.clear();
               var2.shutdown();
            }
         }

         this.head = null;
         ArrayBlockingQueue.this.itrs = null;
      }

      void elementDequeued() {
         if (ArrayBlockingQueue.this.count == 0) {
            this.queueIsEmpty();
         } else if (ArrayBlockingQueue.this.takeIndex == 0) {
            this.takeIndexWrapped();
         }

      }

      private class Node extends WeakReference<ArrayBlockingQueue<E>.Itr> {
         ArrayBlockingQueue<E>.Itrs.Node next;

         Node(ArrayBlockingQueue<E>.Itr var2, ArrayBlockingQueue<E>.Itrs.Node var3) {
            super(var2);
            this.next = var3;
         }
      }
   }
}
