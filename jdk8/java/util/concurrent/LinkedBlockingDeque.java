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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LinkedBlockingDeque<E> extends AbstractQueue<E> implements BlockingDeque<E>, Serializable {
   private static final long serialVersionUID = -387911632671998426L;
   transient LinkedBlockingDeque.Node<E> first;
   transient LinkedBlockingDeque.Node<E> last;
   private transient int count;
   private final int capacity;
   final ReentrantLock lock;
   private final Condition notEmpty;
   private final Condition notFull;

   public LinkedBlockingDeque() {
      this(Integer.MAX_VALUE);
   }

   public LinkedBlockingDeque(int var1) {
      this.lock = new ReentrantLock();
      this.notEmpty = this.lock.newCondition();
      this.notFull = this.lock.newCondition();
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.capacity = var1;
      }
   }

   public LinkedBlockingDeque(Collection<? extends E> var1) {
      this(Integer.MAX_VALUE);
      ReentrantLock var2 = this.lock;
      var2.lock();

      try {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 == null) {
               throw new NullPointerException();
            }

            if (!this.linkLast(new LinkedBlockingDeque.Node(var4))) {
               throw new IllegalStateException("Deque full");
            }
         }
      } finally {
         var2.unlock();
      }

   }

   private boolean linkFirst(LinkedBlockingDeque.Node<E> var1) {
      if (this.count >= this.capacity) {
         return false;
      } else {
         LinkedBlockingDeque.Node var2 = this.first;
         var1.next = var2;
         this.first = var1;
         if (this.last == null) {
            this.last = var1;
         } else {
            var2.prev = var1;
         }

         ++this.count;
         this.notEmpty.signal();
         return true;
      }
   }

   private boolean linkLast(LinkedBlockingDeque.Node<E> var1) {
      if (this.count >= this.capacity) {
         return false;
      } else {
         LinkedBlockingDeque.Node var2 = this.last;
         var1.prev = var2;
         this.last = var1;
         if (this.first == null) {
            this.first = var1;
         } else {
            var2.next = var1;
         }

         ++this.count;
         this.notEmpty.signal();
         return true;
      }
   }

   private E unlinkFirst() {
      LinkedBlockingDeque.Node var1 = this.first;
      if (var1 == null) {
         return null;
      } else {
         LinkedBlockingDeque.Node var2 = var1.next;
         Object var3 = var1.item;
         var1.item = null;
         var1.next = var1;
         this.first = var2;
         if (var2 == null) {
            this.last = null;
         } else {
            var2.prev = null;
         }

         --this.count;
         this.notFull.signal();
         return var3;
      }
   }

   private E unlinkLast() {
      LinkedBlockingDeque.Node var1 = this.last;
      if (var1 == null) {
         return null;
      } else {
         LinkedBlockingDeque.Node var2 = var1.prev;
         Object var3 = var1.item;
         var1.item = null;
         var1.prev = var1;
         this.last = var2;
         if (var2 == null) {
            this.first = null;
         } else {
            var2.next = null;
         }

         --this.count;
         this.notFull.signal();
         return var3;
      }
   }

   void unlink(LinkedBlockingDeque.Node<E> var1) {
      LinkedBlockingDeque.Node var2 = var1.prev;
      LinkedBlockingDeque.Node var3 = var1.next;
      if (var2 == null) {
         this.unlinkFirst();
      } else if (var3 == null) {
         this.unlinkLast();
      } else {
         var2.next = var3;
         var3.prev = var2;
         var1.item = null;
         --this.count;
         this.notFull.signal();
      }

   }

   public void addFirst(E var1) {
      if (!this.offerFirst(var1)) {
         throw new IllegalStateException("Deque full");
      }
   }

   public void addLast(E var1) {
      if (!this.offerLast(var1)) {
         throw new IllegalStateException("Deque full");
      }
   }

   public boolean offerFirst(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LinkedBlockingDeque.Node var2 = new LinkedBlockingDeque.Node(var1);
         ReentrantLock var3 = this.lock;
         var3.lock();

         boolean var4;
         try {
            var4 = this.linkFirst(var2);
         } finally {
            var3.unlock();
         }

         return var4;
      }
   }

   public boolean offerLast(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LinkedBlockingDeque.Node var2 = new LinkedBlockingDeque.Node(var1);
         ReentrantLock var3 = this.lock;
         var3.lock();

         boolean var4;
         try {
            var4 = this.linkLast(var2);
         } finally {
            var3.unlock();
         }

         return var4;
      }
   }

   public void putFirst(E var1) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LinkedBlockingDeque.Node var2 = new LinkedBlockingDeque.Node(var1);
         ReentrantLock var3 = this.lock;
         var3.lock();

         try {
            while(!this.linkFirst(var2)) {
               this.notFull.await();
            }
         } finally {
            var3.unlock();
         }

      }
   }

   public void putLast(E var1) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LinkedBlockingDeque.Node var2 = new LinkedBlockingDeque.Node(var1);
         ReentrantLock var3 = this.lock;
         var3.lock();

         try {
            while(!this.linkLast(var2)) {
               this.notFull.await();
            }
         } finally {
            var3.unlock();
         }

      }
   }

   public boolean offerFirst(E var1, long var2, TimeUnit var4) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LinkedBlockingDeque.Node var5 = new LinkedBlockingDeque.Node(var1);
         long var6 = var4.toNanos(var2);
         ReentrantLock var8 = this.lock;
         var8.lockInterruptibly();

         try {
            boolean var9;
            while(!this.linkFirst(var5)) {
               if (var6 <= 0L) {
                  var9 = false;
                  return var9;
               }

               var6 = this.notFull.awaitNanos(var6);
            }

            var9 = true;
            return var9;
         } finally {
            var8.unlock();
         }
      }
   }

   public boolean offerLast(E var1, long var2, TimeUnit var4) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         LinkedBlockingDeque.Node var5 = new LinkedBlockingDeque.Node(var1);
         long var6 = var4.toNanos(var2);
         ReentrantLock var8 = this.lock;
         var8.lockInterruptibly();

         try {
            boolean var9;
            while(!this.linkLast(var5)) {
               if (var6 <= 0L) {
                  var9 = false;
                  return var9;
               }

               var6 = this.notFull.awaitNanos(var6);
            }

            var9 = true;
            return var9;
         } finally {
            var8.unlock();
         }
      }
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
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.unlinkFirst();
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public E pollLast() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.unlinkLast();
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public E takeFirst() throws InterruptedException {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var3;
      try {
         Object var2;
         while((var2 = this.unlinkFirst()) == null) {
            this.notEmpty.await();
         }

         var3 = var2;
      } finally {
         var1.unlock();
      }

      return var3;
   }

   public E takeLast() throws InterruptedException {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var3;
      try {
         Object var2;
         while((var2 = this.unlinkLast()) == null) {
            this.notEmpty.await();
         }

         var3 = var2;
      } finally {
         var1.unlock();
      }

      return var3;
   }

   public E pollFirst(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = var3.toNanos(var1);
      ReentrantLock var6 = this.lock;
      var6.lockInterruptibly();

      try {
         Object var7;
         Object var8;
         while((var7 = this.unlinkFirst()) == null) {
            if (var4 <= 0L) {
               var8 = null;
               return var8;
            }

            var4 = this.notEmpty.awaitNanos(var4);
         }

         var8 = var7;
         return var8;
      } finally {
         var6.unlock();
      }
   }

   public E pollLast(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = var3.toNanos(var1);
      ReentrantLock var6 = this.lock;
      var6.lockInterruptibly();

      Object var8;
      try {
         Object var7;
         while((var7 = this.unlinkLast()) == null) {
            if (var4 <= 0L) {
               var8 = null;
               return var8;
            }

            var4 = this.notEmpty.awaitNanos(var4);
         }

         var8 = var7;
      } finally {
         var6.unlock();
      }

      return var8;
   }

   public E getFirst() {
      Object var1 = this.peekFirst();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1;
      }
   }

   public E getLast() {
      Object var1 = this.peekLast();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return var1;
      }
   }

   public E peekFirst() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.first == null ? null : this.first.item;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public E peekLast() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      Object var2;
      try {
         var2 = this.last == null ? null : this.last.item;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public boolean removeFirstOccurrence(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         boolean var8;
         try {
            for(LinkedBlockingDeque.Node var3 = this.first; var3 != null; var3 = var3.next) {
               if (var1.equals(var3.item)) {
                  this.unlink(var3);
                  boolean var4 = true;
                  return var4;
               }
            }

            var8 = false;
         } finally {
            var2.unlock();
         }

         return var8;
      }
   }

   public boolean removeLastOccurrence(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         try {
            for(LinkedBlockingDeque.Node var3 = this.last; var3 != null; var3 = var3.prev) {
               if (var1.equals(var3.item)) {
                  this.unlink(var3);
                  boolean var4 = true;
                  return var4;
               }
            }

            boolean var8 = false;
            return var8;
         } finally {
            var2.unlock();
         }
      }
   }

   public boolean add(E var1) {
      this.addLast(var1);
      return true;
   }

   public boolean offer(E var1) {
      return this.offerLast(var1);
   }

   public void put(E var1) throws InterruptedException {
      this.putLast(var1);
   }

   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.offerLast(var1, var2, var4);
   }

   public E remove() {
      return this.removeFirst();
   }

   public E poll() {
      return this.pollFirst();
   }

   public E take() throws InterruptedException {
      return this.takeFirst();
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      return this.pollFirst(var1, var3);
   }

   public E element() {
      return this.getFirst();
   }

   public E peek() {
      return this.peekFirst();
   }

   public int remainingCapacity() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      int var2;
      try {
         var2 = this.capacity - this.count;
      } finally {
         var1.unlock();
      }

      return var2;
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
            int var4 = Math.min(var2, this.count);

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
               var1.add(this.first.item);
               this.unlinkFirst();
            }

            var5 = var4;
            return var5;
         } finally {
            var3.unlock();
         }
      }
   }

   public void push(E var1) {
      this.addFirst(var1);
   }

   public E pop() {
      return this.removeFirst();
   }

   public boolean remove(Object var1) {
      return this.removeFirstOccurrence(var1);
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

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         ReentrantLock var2 = this.lock;
         var2.lock();

         boolean var8;
         try {
            for(LinkedBlockingDeque.Node var3 = this.first; var3 != null; var3 = var3.next) {
               if (var1.equals(var3.item)) {
                  boolean var4 = true;
                  return var4;
               }
            }

            var8 = false;
         } finally {
            var2.unlock();
         }

         return var8;
      }
   }

   public Object[] toArray() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         Object[] var2 = new Object[this.count];
         int var3 = 0;

         for(LinkedBlockingDeque.Node var4 = this.first; var4 != null; var4 = var4.next) {
            var2[var3++] = var4.item;
         }

         Object[] var8 = var2;
         return var8;
      } finally {
         var1.unlock();
      }
   }

   public <T> T[] toArray(T[] var1) {
      ReentrantLock var2 = this.lock;
      var2.lock();

      Object[] var8;
      try {
         if (var1.length < this.count) {
            var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), this.count));
         }

         int var3 = 0;

         for(LinkedBlockingDeque.Node var4 = this.first; var4 != null; var4 = var4.next) {
            var1[var3++] = var4.item;
         }

         if (var1.length > var3) {
            var1[var3] = null;
         }

         var8 = var1;
      } finally {
         var2.unlock();
      }

      return var8;
   }

   public String toString() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      String var3;
      try {
         LinkedBlockingDeque.Node var2 = this.first;
         if (var2 != null) {
            StringBuilder var9 = new StringBuilder();
            var9.append('[');

            while(true) {
               Object var4 = var2.item;
               var9.append(var4 == this ? "(this Collection)" : var4);
               var2 = var2.next;
               if (var2 == null) {
                  String var5 = var9.append(']').toString();
                  return var5;
               }

               var9.append(',').append(' ');
            }
         }

         var3 = "[]";
      } finally {
         var1.unlock();
      }

      return var3;
   }

   public void clear() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         LinkedBlockingDeque.Node var3;
         for(LinkedBlockingDeque.Node var2 = this.first; var2 != null; var2 = var3) {
            var2.item = null;
            var3 = var2.next;
            var2.prev = null;
            var2.next = null;
         }

         this.first = this.last = null;
         this.count = 0;
         this.notFull.signalAll();
      } finally {
         var1.unlock();
      }
   }

   public Iterator<E> iterator() {
      return new LinkedBlockingDeque.Itr();
   }

   public Iterator<E> descendingIterator() {
      return new LinkedBlockingDeque.DescendingItr();
   }

   public Spliterator<E> spliterator() {
      return new LinkedBlockingDeque.LBDSpliterator(this);
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      ReentrantLock var2 = this.lock;
      var2.lock();

      try {
         var1.defaultWriteObject();

         for(LinkedBlockingDeque.Node var3 = this.first; var3 != null; var3 = var3.next) {
            var1.writeObject(var3.item);
         }

         var1.writeObject((Object)null);
      } finally {
         var2.unlock();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.count = 0;
      this.first = null;
      this.last = null;

      while(true) {
         Object var2 = var1.readObject();
         if (var2 == null) {
            return;
         }

         this.add(var2);
      }
   }

   static final class LBDSpliterator<E> implements Spliterator<E> {
      static final int MAX_BATCH = 33554432;
      final LinkedBlockingDeque<E> queue;
      LinkedBlockingDeque.Node<E> current;
      int batch;
      boolean exhausted;
      long est;

      LBDSpliterator(LinkedBlockingDeque<E> var1) {
         this.queue = var1;
         this.est = (long)var1.size();
      }

      public long estimateSize() {
         return this.est;
      }

      public Spliterator<E> trySplit() {
         LinkedBlockingDeque var2 = this.queue;
         int var3 = this.batch;
         int var4 = var3 <= 0 ? 1 : (var3 >= 33554432 ? 33554432 : var3 + 1);
         LinkedBlockingDeque.Node var1;
         if (!this.exhausted && ((var1 = this.current) != null || (var1 = var2.first) != null) && var1.next != null) {
            Object[] var5 = new Object[var4];
            ReentrantLock var6 = var2.lock;
            int var7 = 0;
            LinkedBlockingDeque.Node var8 = this.current;
            var6.lock();

            try {
               if (var8 != null || (var8 = var2.first) != null) {
                  do {
                     if ((var5[var7] = var8.item) != null) {
                        ++var7;
                     }
                  } while((var8 = var8.next) != null && var7 < var4);
               }
            } finally {
               var6.unlock();
            }

            if ((this.current = var8) == null) {
               this.est = 0L;
               this.exhausted = true;
            } else if ((this.est -= (long)var7) < 0L) {
               this.est = 0L;
            }

            if (var7 > 0) {
               this.batch = var7;
               return Spliterators.spliterator((Object[])var5, 0, var7, 4368);
            }
         }

         return null;
      }

      public void forEachRemaining(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            LinkedBlockingDeque var2 = this.queue;
            ReentrantLock var3 = var2.lock;
            if (!this.exhausted) {
               this.exhausted = true;
               LinkedBlockingDeque.Node var4 = this.current;

               do {
                  Object var5 = null;
                  var3.lock();

                  try {
                     if (var4 == null) {
                        var4 = var2.first;
                     }

                     while(var4 != null) {
                        var5 = var4.item;
                        var4 = var4.next;
                        if (var5 != null) {
                           break;
                        }
                     }
                  } finally {
                     var3.unlock();
                  }

                  if (var5 != null) {
                     var1.accept(var5);
                  }
               } while(var4 != null);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            LinkedBlockingDeque var2 = this.queue;
            ReentrantLock var3 = var2.lock;
            if (!this.exhausted) {
               Object var4 = null;
               var3.lock();

               try {
                  if (this.current == null) {
                     this.current = var2.first;
                  }

                  while(this.current != null) {
                     var4 = this.current.item;
                     this.current = this.current.next;
                     if (var4 != null) {
                        break;
                     }
                  }
               } finally {
                  var3.unlock();
               }

               if (this.current == null) {
                  this.exhausted = true;
               }

               if (var4 != null) {
                  var1.accept(var4);
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

   private class DescendingItr extends LinkedBlockingDeque<E>.AbstractItr {
      private DescendingItr() {
         super();
      }

      LinkedBlockingDeque.Node<E> firstNode() {
         return LinkedBlockingDeque.this.last;
      }

      LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> var1) {
         return var1.prev;
      }

      // $FF: synthetic method
      DescendingItr(Object var2) {
         this();
      }
   }

   private class Itr extends LinkedBlockingDeque<E>.AbstractItr {
      private Itr() {
         super();
      }

      LinkedBlockingDeque.Node<E> firstNode() {
         return LinkedBlockingDeque.this.first;
      }

      LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> var1) {
         return var1.next;
      }

      // $FF: synthetic method
      Itr(Object var2) {
         this();
      }
   }

   private abstract class AbstractItr implements Iterator<E> {
      LinkedBlockingDeque.Node<E> next;
      E nextItem;
      private LinkedBlockingDeque.Node<E> lastRet;

      abstract LinkedBlockingDeque.Node<E> firstNode();

      abstract LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> var1);

      AbstractItr() {
         ReentrantLock var2 = LinkedBlockingDeque.this.lock;
         var2.lock();

         try {
            this.next = this.firstNode();
            this.nextItem = this.next == null ? null : this.next.item;
         } finally {
            var2.unlock();
         }

      }

      private LinkedBlockingDeque.Node<E> succ(LinkedBlockingDeque.Node<E> var1) {
         while(true) {
            LinkedBlockingDeque.Node var2 = this.nextNode(var1);
            if (var2 == null) {
               return null;
            }

            if (var2.item != null) {
               return var2;
            }

            if (var2 == var1) {
               return this.firstNode();
            }

            var1 = var2;
         }
      }

      void advance() {
         ReentrantLock var1 = LinkedBlockingDeque.this.lock;
         var1.lock();

         try {
            this.next = this.succ(this.next);
            this.nextItem = this.next == null ? null : this.next.item;
         } finally {
            var1.unlock();
         }

      }

      public boolean hasNext() {
         return this.next != null;
      }

      public E next() {
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            this.lastRet = this.next;
            Object var1 = this.nextItem;
            this.advance();
            return var1;
         }
      }

      public void remove() {
         LinkedBlockingDeque.Node var1 = this.lastRet;
         if (var1 == null) {
            throw new IllegalStateException();
         } else {
            this.lastRet = null;
            ReentrantLock var2 = LinkedBlockingDeque.this.lock;
            var2.lock();

            try {
               if (var1.item != null) {
                  LinkedBlockingDeque.this.unlink(var1);
               }
            } finally {
               var2.unlock();
            }

         }
      }
   }

   static final class Node<E> {
      E item;
      LinkedBlockingDeque.Node<E> prev;
      LinkedBlockingDeque.Node<E> next;

      Node(E var1) {
         this.item = var1;
      }
   }
}
