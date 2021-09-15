package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {
   private final transient ReentrantLock lock = new ReentrantLock();
   private final PriorityQueue<E> q = new PriorityQueue();
   private Thread leader = null;
   private final Condition available;

   public DelayQueue() {
      this.available = this.lock.newCondition();
   }

   public DelayQueue(Collection<? extends E> var1) {
      this.available = this.lock.newCondition();
      this.addAll(var1);
   }

   public boolean add(E var1) {
      return this.offer(var1);
   }

   public boolean offer(E var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      boolean var3;
      try {
         this.q.offer(var1);
         if (this.q.peek() == var1) {
            this.leader = null;
            this.available.signal();
         }

         var3 = true;
      } finally {
         var2.unlock();
      }

      return var3;
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

      Delayed var3;
      try {
         Delayed var2 = (Delayed)this.q.peek();
         if (var2 != null && var2.getDelay(TimeUnit.NANOSECONDS) <= 0L) {
            var3 = (Delayed)this.q.poll();
            return var3;
         }

         var3 = null;
      } finally {
         var1.unlock();
      }

      return var3;
   }

   public E take() throws InterruptedException {
      ReentrantLock var1 = this.lock;
      var1.lockInterruptibly();

      try {
         while(true) {
            while(true) {
               Delayed var2 = (Delayed)this.q.peek();
               if (var2 != null) {
                  long var3 = var2.getDelay(TimeUnit.NANOSECONDS);
                  if (var3 <= 0L) {
                     Delayed var14 = (Delayed)this.q.poll();
                     return var14;
                  }

                  var2 = null;
                  if (this.leader != null) {
                     this.available.await();
                  } else {
                     Thread var5 = Thread.currentThread();
                     this.leader = var5;

                     try {
                        this.available.awaitNanos(var3);
                     } finally {
                        if (this.leader == var5) {
                           this.leader = null;
                        }

                     }
                  }
               } else {
                  this.available.await();
               }
            }
         }
      } finally {
         if (this.leader == null && this.q.peek() != null) {
            this.available.signal();
         }

         var1.unlock();
      }
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = var3.toNanos(var1);
      ReentrantLock var6 = this.lock;
      var6.lockInterruptibly();

      try {
         while(true) {
            Delayed var7 = (Delayed)this.q.peek();
            if (var7 != null) {
               long var22 = var7.getDelay(TimeUnit.NANOSECONDS);
               if (var22 <= 0L) {
                  Delayed var21 = (Delayed)this.q.poll();
                  return var21;
               }

               Thread var10;
               if (var4 <= 0L) {
                  var10 = null;
                  return var10;
               }

               var7 = null;
               if (var4 >= var22 && this.leader == null) {
                  var10 = Thread.currentThread();
                  this.leader = var10;

                  try {
                     long var11 = this.available.awaitNanos(var22);
                     var4 -= var22 - var11;
                  } finally {
                     if (this.leader == var10) {
                        this.leader = null;
                     }

                  }
               } else {
                  var4 = this.available.awaitNanos(var4);
               }
            } else {
               if (var4 <= 0L) {
                  Object var8 = null;
                  return (Delayed)var8;
               }

               var4 = this.available.awaitNanos(var4);
            }
         }
      } finally {
         if (this.leader == null && this.q.peek() != null) {
            this.available.signal();
         }

         var6.unlock();
      }
   }

   public E peek() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Delayed var2;
      try {
         var2 = (Delayed)this.q.peek();
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
         var2 = this.q.size();
      } finally {
         var1.unlock();
      }

      return var2;
   }

   private E peekExpired() {
      Delayed var1 = (Delayed)this.q.peek();
      return var1 != null && var1.getDelay(TimeUnit.NANOSECONDS) <= 0L ? var1 : null;
   }

   public int drainTo(Collection<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 == this) {
         throw new IllegalArgumentException();
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         try {
            int var3;
            Delayed var4;
            for(var3 = 0; (var4 = this.peekExpired()) != null; ++var3) {
               var1.add(var4);
               this.q.poll();
            }

            int var8 = var3;
            return var8;
         } finally {
            var2.unlock();
         }
      }
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
            int var4;
            Delayed var5;
            for(var4 = 0; var4 < var2 && (var5 = this.peekExpired()) != null; ++var4) {
               var1.add(var5);
               this.q.poll();
            }

            int var9 = var4;
            return var9;
         } finally {
            var3.unlock();
         }
      }
   }

   public void clear() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         this.q.clear();
      } finally {
         var1.unlock();
      }

   }

   public int remainingCapacity() {
      return Integer.MAX_VALUE;
   }

   public Object[] toArray() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object[] var2;
      try {
         var2 = this.q.toArray();
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public <T> T[] toArray(T[] var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      Object[] var3;
      try {
         var3 = this.q.toArray(var1);
      } finally {
         var2.unlock();
      }

      return var3;
   }

   public boolean remove(Object var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      boolean var3;
      try {
         var3 = this.q.remove(var1);
      } finally {
         var2.unlock();
      }

      return var3;
   }

   void removeEQ(Object var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      try {
         Iterator var3 = this.q.iterator();

         while(var3.hasNext()) {
            if (var1 == var3.next()) {
               var3.remove();
               break;
            }
         }
      } finally {
         var2.unlock();
      }

   }

   public Iterator<E> iterator() {
      return new DelayQueue.Itr(this.toArray());
   }

   private class Itr implements Iterator<E> {
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
            return (Delayed)this.array[this.cursor++];
         }
      }

      public void remove() {
         if (this.lastRet < 0) {
            throw new IllegalStateException();
         } else {
            DelayQueue.this.removeEQ(this.array[this.lastRet]);
            this.lastRet = -1;
         }
      }
   }
}
