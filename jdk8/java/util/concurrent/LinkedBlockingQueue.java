package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
   private static final long serialVersionUID = -6903933977591709194L;
   private final int capacity;
   private final AtomicInteger count;
   transient LinkedBlockingQueue.Node<E> head;
   private transient LinkedBlockingQueue.Node<E> last;
   private final ReentrantLock takeLock;
   private final Condition notEmpty;
   private final ReentrantLock putLock;
   private final Condition notFull;

   private void signalNotEmpty() {
      ReentrantLock var1 = this.takeLock;
      var1.lock();

      try {
         this.notEmpty.signal();
      } finally {
         var1.unlock();
      }

   }

   private void signalNotFull() {
      ReentrantLock var1 = this.putLock;
      var1.lock();

      try {
         this.notFull.signal();
      } finally {
         var1.unlock();
      }

   }

   private void enqueue(LinkedBlockingQueue.Node<E> var1) {
      this.last = this.last.next = var1;
   }

   private E dequeue() {
      LinkedBlockingQueue.Node var1 = this.head;
      LinkedBlockingQueue.Node var2 = var1.next;
      var1.next = var1;
      this.head = var2;
      Object var3 = var2.item;
      var2.item = null;
      return var3;
   }

   void fullyLock() {
      this.putLock.lock();
      this.takeLock.lock();
   }

   void fullyUnlock() {
      this.takeLock.unlock();
      this.putLock.unlock();
   }

   public LinkedBlockingQueue() {
      this(Integer.MAX_VALUE);
   }

   public LinkedBlockingQueue(int var1) {
      this.count = new AtomicInteger();
      this.takeLock = new ReentrantLock();
      this.notEmpty = this.takeLock.newCondition();
      this.putLock = new ReentrantLock();
      this.notFull = this.putLock.newCondition();
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.capacity = var1;
         this.last = this.head = new LinkedBlockingQueue.Node((Object)null);
      }
   }

   public LinkedBlockingQueue(Collection<? extends E> var1) {
      this(Integer.MAX_VALUE);
      ReentrantLock var2 = this.putLock;
      var2.lock();

      try {
         int var3 = 0;

         for(Iterator var4 = var1.iterator(); var4.hasNext(); ++var3) {
            Object var5 = var4.next();
            if (var5 == null) {
               throw new NullPointerException();
            }

            if (var3 == this.capacity) {
               throw new IllegalStateException("Queue full");
            }

            this.enqueue(new LinkedBlockingQueue.Node(var5));
         }

         this.count.set(var3);
      } finally {
         var2.unlock();
      }
   }

   public int size() {
      return this.count.get();
   }

   public int remainingCapacity() {
      return this.capacity - this.count.get();
   }

   public void put(E var1) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         boolean var2 = true;
         LinkedBlockingQueue.Node var3 = new LinkedBlockingQueue.Node(var1);
         ReentrantLock var4 = this.putLock;
         AtomicInteger var5 = this.count;
         var4.lockInterruptibly();

         int var9;
         try {
            while(var5.get() == this.capacity) {
               this.notFull.await();
            }

            this.enqueue(var3);
            var9 = var5.getAndIncrement();
            if (var9 + 1 < this.capacity) {
               this.notFull.signal();
            }
         } finally {
            var4.unlock();
         }

         if (var9 == 0) {
            this.signalNotEmpty();
         }

      }
   }

   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         long var5 = var4.toNanos(var2);
         boolean var7 = true;
         ReentrantLock var8 = this.putLock;
         AtomicInteger var9 = this.count;
         var8.lockInterruptibly();

         int var14;
         try {
            while(var9.get() == this.capacity) {
               if (var5 <= 0L) {
                  boolean var10 = false;
                  return var10;
               }

               var5 = this.notFull.awaitNanos(var5);
            }

            this.enqueue(new LinkedBlockingQueue.Node(var1));
            var14 = var9.getAndIncrement();
            if (var14 + 1 < this.capacity) {
               this.notFull.signal();
            }
         } finally {
            var8.unlock();
         }

         if (var14 == 0) {
            this.signalNotEmpty();
         }

         return true;
      }
   }

   public boolean offer(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         AtomicInteger var2 = this.count;
         if (var2.get() == this.capacity) {
            return false;
         } else {
            int var3 = -1;
            LinkedBlockingQueue.Node var4 = new LinkedBlockingQueue.Node(var1);
            ReentrantLock var5 = this.putLock;
            var5.lock();

            try {
               if (var2.get() < this.capacity) {
                  this.enqueue(var4);
                  var3 = var2.getAndIncrement();
                  if (var3 + 1 < this.capacity) {
                     this.notFull.signal();
                  }
               }
            } finally {
               var5.unlock();
            }

            if (var3 == 0) {
               this.signalNotEmpty();
            }

            return var3 >= 0;
         }
      }
   }

   public E take() throws InterruptedException {
      boolean var2 = true;
      AtomicInteger var3 = this.count;
      ReentrantLock var4 = this.takeLock;
      var4.lockInterruptibly();

      Object var1;
      int var8;
      try {
         while(var3.get() == 0) {
            this.notEmpty.await();
         }

         var1 = this.dequeue();
         var8 = var3.getAndDecrement();
         if (var8 > 1) {
            this.notEmpty.signal();
         }
      } finally {
         var4.unlock();
      }

      if (var8 == this.capacity) {
         this.signalNotFull();
      }

      return var1;
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      Object var4 = null;
      boolean var5 = true;
      long var6 = var3.toNanos(var1);
      AtomicInteger var8 = this.count;
      ReentrantLock var9 = this.takeLock;
      var9.lockInterruptibly();

      int var14;
      try {
         while(true) {
            if (var8.get() != 0) {
               var4 = this.dequeue();
               var14 = var8.getAndDecrement();
               if (var14 > 1) {
                  this.notEmpty.signal();
               }
               break;
            }

            if (var6 <= 0L) {
               Object var10 = null;
               return var10;
            }

            var6 = this.notEmpty.awaitNanos(var6);
         }
      } finally {
         var9.unlock();
      }

      if (var14 == this.capacity) {
         this.signalNotFull();
      }

      return var4;
   }

   public E poll() {
      AtomicInteger var1 = this.count;
      if (var1.get() == 0) {
         return null;
      } else {
         Object var2 = null;
         int var3 = -1;
         ReentrantLock var4 = this.takeLock;
         var4.lock();

         try {
            if (var1.get() > 0) {
               var2 = this.dequeue();
               var3 = var1.getAndDecrement();
               if (var3 > 1) {
                  this.notEmpty.signal();
               }
            }
         } finally {
            var4.unlock();
         }

         if (var3 == this.capacity) {
            this.signalNotFull();
         }

         return var2;
      }
   }

   public E peek() {
      if (this.count.get() == 0) {
         return null;
      } else {
         ReentrantLock var1 = this.takeLock;
         var1.lock();

         Object var3;
         try {
            LinkedBlockingQueue.Node var2 = this.head.next;
            if (var2 == null) {
               var3 = null;
               return var3;
            }

            var3 = var2.item;
         } finally {
            var1.unlock();
         }

         return var3;
      }
   }

   void unlink(LinkedBlockingQueue.Node<E> var1, LinkedBlockingQueue.Node<E> var2) {
      var1.item = null;
      var2.next = var1.next;
      if (this.last == var1) {
         this.last = var2;
      }

      if (this.count.getAndDecrement() == this.capacity) {
         this.notFull.signal();
      }

   }

   public boolean remove(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         this.fullyLock();

         boolean var8;
         try {
            LinkedBlockingQueue.Node var2 = this.head;

            for(LinkedBlockingQueue.Node var3 = var2.next; var3 != null; var3 = var3.next) {
               if (var1.equals(var3.item)) {
                  this.unlink(var3, var2);
                  boolean var4 = true;
                  return var4;
               }

               var2 = var3;
            }

            var8 = false;
         } finally {
            this.fullyUnlock();
         }

         return var8;
      }
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         this.fullyLock();

         boolean var7;
         try {
            for(LinkedBlockingQueue.Node var2 = this.head.next; var2 != null; var2 = var2.next) {
               if (var1.equals(var2.item)) {
                  boolean var3 = true;
                  return var3;
               }
            }

            var7 = false;
         } finally {
            this.fullyUnlock();
         }

         return var7;
      }
   }

   public Object[] toArray() {
      this.fullyLock();

      try {
         int var1 = this.count.get();
         Object[] var2 = new Object[var1];
         int var3 = 0;

         for(LinkedBlockingQueue.Node var4 = this.head.next; var4 != null; var4 = var4.next) {
            var2[var3++] = var4.item;
         }

         Object[] var8 = var2;
         return var8;
      } finally {
         this.fullyUnlock();
      }
   }

   public <T> T[] toArray(T[] var1) {
      this.fullyLock();

      Object[] var8;
      try {
         int var2 = this.count.get();
         if (var1.length < var2) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var2));
         }

         int var3 = 0;

         for(LinkedBlockingQueue.Node var4 = this.head.next; var4 != null; var4 = var4.next) {
            var1[var3++] = var4.item;
         }

         if (var1.length > var3) {
            var1[var3] = null;
         }

         var8 = var1;
      } finally {
         this.fullyUnlock();
      }

      return var8;
   }

   public String toString() {
      this.fullyLock();

      try {
         LinkedBlockingQueue.Node var1 = this.head.next;
         if (var1 == null) {
            String var8 = "[]";
            return var8;
         } else {
            StringBuilder var2 = new StringBuilder();
            var2.append('[');

            while(true) {
               Object var3 = var1.item;
               var2.append(var3 == this ? "(this Collection)" : var3);
               var1 = var1.next;
               if (var1 == null) {
                  String var4 = var2.append(']').toString();
                  return var4;
               }

               var2.append(',').append(' ');
            }
         }
      } finally {
         this.fullyUnlock();
      }
   }

   public void clear() {
      this.fullyLock();

      try {
         LinkedBlockingQueue.Node var2 = this.head;

         while(true) {
            LinkedBlockingQueue.Node var1;
            if ((var1 = var2.next) == null) {
               this.head = this.last;
               if (this.count.getAndSet(0) == this.capacity) {
                  this.notFull.signal();
               }
               break;
            }

            var2.next = var2;
            var1.item = null;
            var2 = var1;
         }
      } finally {
         this.fullyUnlock();
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
         boolean var3 = false;
         ReentrantLock var4 = this.takeLock;
         var4.lock();

         int var17;
         try {
            int var5 = Math.min(var2, this.count.get());
            LinkedBlockingQueue.Node var6 = this.head;
            int var7 = 0;

            try {
               while(var7 < var5) {
                  LinkedBlockingQueue.Node var8 = var6.next;
                  var1.add(var8.item);
                  var8.item = null;
                  var6.next = var6;
                  var6 = var8;
                  ++var7;
               }

               var17 = var5;
            } finally {
               if (var7 > 0) {
                  this.head = var6;
                  var3 = this.count.getAndAdd(-var7) == this.capacity;
               }

            }
         } finally {
            var4.unlock();
            if (var3) {
               this.signalNotFull();
            }

         }

         return var17;
      }
   }

   public Iterator<E> iterator() {
      return new LinkedBlockingQueue.Itr();
   }

   public Spliterator<E> spliterator() {
      return new LinkedBlockingQueue.LBQSpliterator(this);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.fullyLock();

      try {
         var1.defaultWriteObject();

         for(LinkedBlockingQueue.Node var2 = this.head.next; var2 != null; var2 = var2.next) {
            var1.writeObject(var2.item);
         }

         var1.writeObject((Object)null);
      } finally {
         this.fullyUnlock();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.count.set(0);
      this.last = this.head = new LinkedBlockingQueue.Node((Object)null);

      while(true) {
         Object var2 = var1.readObject();
         if (var2 == null) {
            return;
         }

         this.add(var2);
      }
   }

   static final class LBQSpliterator<E> implements Spliterator<E> {
      static final int MAX_BATCH = 33554432;
      final LinkedBlockingQueue<E> queue;
      LinkedBlockingQueue.Node<E> current;
      int batch;
      boolean exhausted;
      long est;

      LBQSpliterator(LinkedBlockingQueue<E> var1) {
         this.queue = var1;
         this.est = (long)var1.size();
      }

      public long estimateSize() {
         return this.est;
      }

      public Spliterator<E> trySplit() {
         LinkedBlockingQueue var2 = this.queue;
         int var3 = this.batch;
         int var4 = var3 <= 0 ? 1 : (var3 >= 33554432 ? 33554432 : var3 + 1);
         LinkedBlockingQueue.Node var1;
         if (!this.exhausted && ((var1 = this.current) != null || (var1 = var2.head.next) != null) && var1.next != null) {
            Object[] var5 = new Object[var4];
            int var6 = 0;
            LinkedBlockingQueue.Node var7 = this.current;
            var2.fullyLock();

            try {
               if (var7 != null || (var7 = var2.head.next) != null) {
                  do {
                     if ((var5[var6] = var7.item) != null) {
                        ++var6;
                     }
                  } while((var7 = var7.next) != null && var6 < var4);
               }
            } finally {
               var2.fullyUnlock();
            }

            if ((this.current = var7) == null) {
               this.est = 0L;
               this.exhausted = true;
            } else if ((this.est -= (long)var6) < 0L) {
               this.est = 0L;
            }

            if (var6 > 0) {
               this.batch = var6;
               return Spliterators.spliterator((Object[])var5, 0, var6, 4368);
            }
         }

         return null;
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            LinkedBlockingQueue var2 = this.queue;
            if (!this.exhausted) {
               this.exhausted = true;
               LinkedBlockingQueue.Node var3 = this.current;

               do {
                  Object var4 = null;
                  var2.fullyLock();

                  try {
                     if (var3 == null) {
                        var3 = var2.head.next;
                     }

                     while(var3 != null) {
                        var4 = var3.item;
                        var3 = var3.next;
                        if (var4 != null) {
                           break;
                        }
                     }
                  } finally {
                     var2.fullyUnlock();
                  }

                  if (var4 != null) {
                     var1.accept(var4);
                  }
               } while(var3 != null);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            LinkedBlockingQueue var2 = this.queue;
            if (!this.exhausted) {
               Object var3 = null;
               var2.fullyLock();

               try {
                  if (this.current == null) {
                     this.current = var2.head.next;
                  }

                  while(this.current != null) {
                     var3 = this.current.item;
                     this.current = this.current.next;
                     if (var3 != null) {
                        break;
                     }
                  }
               } finally {
                  var2.fullyUnlock();
               }

               if (this.current == null) {
                  this.exhausted = true;
               }

               if (var3 != null) {
                  var1.accept(var3);
                  return true;
               }
            }

            return false;
         }
      }

      public int characteristics() {
         return 4368;
      }
   }

   private class Itr implements Iterator<E> {
      private LinkedBlockingQueue.Node<E> current;
      private LinkedBlockingQueue.Node<E> lastRet;
      private E currentElement;

      Itr() {
         LinkedBlockingQueue.this.fullyLock();

         try {
            this.current = LinkedBlockingQueue.this.head.next;
            if (this.current != null) {
               this.currentElement = this.current.item;
            }
         } finally {
            LinkedBlockingQueue.this.fullyUnlock();
         }

      }

      public boolean hasNext() {
         return this.current != null;
      }

      private LinkedBlockingQueue.Node<E> nextNode(LinkedBlockingQueue.Node<E> var1) {
         while(true) {
            LinkedBlockingQueue.Node var2 = var1.next;
            if (var2 == var1) {
               return LinkedBlockingQueue.this.head.next;
            }

            if (var2 == null || var2.item != null) {
               return var2;
            }

            var1 = var2;
         }
      }

      public E next() {
         LinkedBlockingQueue.this.fullyLock();

         Object var2;
         try {
            if (this.current == null) {
               throw new NoSuchElementException();
            }

            Object var1 = this.currentElement;
            this.lastRet = this.current;
            this.current = this.nextNode(this.current);
            this.currentElement = this.current == null ? null : this.current.item;
            var2 = var1;
         } finally {
            LinkedBlockingQueue.this.fullyUnlock();
         }

         return var2;
      }

      public void remove() {
         if (this.lastRet == null) {
            throw new IllegalStateException();
         } else {
            LinkedBlockingQueue.this.fullyLock();

            try {
               LinkedBlockingQueue.Node var1 = this.lastRet;
               this.lastRet = null;
               LinkedBlockingQueue.Node var2 = LinkedBlockingQueue.this.head;

               for(LinkedBlockingQueue.Node var3 = var2.next; var3 != null; var3 = var3.next) {
                  if (var3 == var1) {
                     LinkedBlockingQueue.this.unlink(var3, var2);
                     break;
                  }

                  var2 = var3;
               }
            } finally {
               LinkedBlockingQueue.this.fullyUnlock();
            }

         }
      }
   }

   static class Node<E> {
      E item;
      LinkedBlockingQueue.Node<E> next;

      Node(E var1) {
         this.item = var1;
      }
   }
}
