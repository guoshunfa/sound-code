package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class SynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
   private static final long serialVersionUID = -3223113410248163686L;
   static final int NCPUS = Runtime.getRuntime().availableProcessors();
   static final int maxTimedSpins;
   static final int maxUntimedSpins;
   static final long spinForTimeoutThreshold = 1000L;
   private transient volatile SynchronousQueue.Transferer<E> transferer;
   private ReentrantLock qlock;
   private SynchronousQueue.WaitQueue waitingProducers;
   private SynchronousQueue.WaitQueue waitingConsumers;

   public SynchronousQueue() {
      this(false);
   }

   public SynchronousQueue(boolean var1) {
      this.transferer = (SynchronousQueue.Transferer)(var1 ? new SynchronousQueue.TransferQueue() : new SynchronousQueue.TransferStack());
   }

   public void put(E var1) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.transferer.transfer(var1, false, 0L) == null) {
         Thread.interrupted();
         throw new InterruptedException();
      }
   }

   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.transferer.transfer(var1, true, var4.toNanos(var2)) != null) {
         return true;
      } else if (!Thread.interrupted()) {
         return false;
      } else {
         throw new InterruptedException();
      }
   }

   public boolean offer(E var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return this.transferer.transfer(var1, true, 0L) != null;
      }
   }

   public E take() throws InterruptedException {
      Object var1 = this.transferer.transfer((Object)null, false, 0L);
      if (var1 != null) {
         return var1;
      } else {
         Thread.interrupted();
         throw new InterruptedException();
      }
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      Object var4 = this.transferer.transfer((Object)null, true, var3.toNanos(var1));
      if (var4 == null && Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return var4;
      }
   }

   public E poll() {
      return this.transferer.transfer((Object)null, true, 0L);
   }

   public boolean isEmpty() {
      return true;
   }

   public int size() {
      return 0;
   }

   public int remainingCapacity() {
      return 0;
   }

   public void clear() {
   }

   public boolean contains(Object var1) {
      return false;
   }

   public boolean remove(Object var1) {
      return false;
   }

   public boolean containsAll(Collection<?> var1) {
      return var1.isEmpty();
   }

   public boolean removeAll(Collection<?> var1) {
      return false;
   }

   public boolean retainAll(Collection<?> var1) {
      return false;
   }

   public E peek() {
      return null;
   }

   public Iterator<E> iterator() {
      return Collections.emptyIterator();
   }

   public Spliterator<E> spliterator() {
      return Spliterators.emptySpliterator();
   }

   public Object[] toArray() {
      return new Object[0];
   }

   public <T> T[] toArray(T[] var1) {
      if (var1.length > 0) {
         var1[0] = null;
      }

      return var1;
   }

   public int drainTo(Collection<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 == this) {
         throw new IllegalArgumentException();
      } else {
         int var2;
         Object var3;
         for(var2 = 0; (var3 = this.poll()) != null; ++var2) {
            var1.add(var3);
         }

         return var2;
      }
   }

   public int drainTo(Collection<? super E> var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 == this) {
         throw new IllegalArgumentException();
      } else {
         int var3;
         Object var4;
         for(var3 = 0; var3 < var2 && (var4 = this.poll()) != null; ++var3) {
            var1.add(var4);
         }

         return var3;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      boolean var2 = this.transferer instanceof SynchronousQueue.TransferQueue;
      if (var2) {
         this.qlock = new ReentrantLock(true);
         this.waitingProducers = new SynchronousQueue.FifoWaitQueue();
         this.waitingConsumers = new SynchronousQueue.FifoWaitQueue();
      } else {
         this.qlock = new ReentrantLock();
         this.waitingProducers = new SynchronousQueue.LifoWaitQueue();
         this.waitingConsumers = new SynchronousQueue.LifoWaitQueue();
      }

      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.waitingProducers instanceof SynchronousQueue.FifoWaitQueue) {
         this.transferer = new SynchronousQueue.TransferQueue();
      } else {
         this.transferer = new SynchronousQueue.TransferStack();
      }

   }

   static long objectFieldOffset(Unsafe var0, String var1, Class<?> var2) {
      try {
         return var0.objectFieldOffset(var2.getDeclaredField(var1));
      } catch (NoSuchFieldException var5) {
         NoSuchFieldError var4 = new NoSuchFieldError(var1);
         var4.initCause(var5);
         throw var4;
      }
   }

   static {
      maxTimedSpins = NCPUS < 2 ? 0 : 32;
      maxUntimedSpins = maxTimedSpins * 16;
   }

   static class FifoWaitQueue extends SynchronousQueue.WaitQueue {
      private static final long serialVersionUID = -3623113410248163686L;
   }

   static class LifoWaitQueue extends SynchronousQueue.WaitQueue {
      private static final long serialVersionUID = -3633113410248163686L;
   }

   static class WaitQueue implements Serializable {
   }

   static final class TransferQueue<E> extends SynchronousQueue.Transferer<E> {
      transient volatile SynchronousQueue.TransferQueue.QNode head;
      transient volatile SynchronousQueue.TransferQueue.QNode tail;
      transient volatile SynchronousQueue.TransferQueue.QNode cleanMe;
      private static final Unsafe UNSAFE;
      private static final long headOffset;
      private static final long tailOffset;
      private static final long cleanMeOffset;

      TransferQueue() {
         SynchronousQueue.TransferQueue.QNode var1 = new SynchronousQueue.TransferQueue.QNode((Object)null, false);
         this.head = var1;
         this.tail = var1;
      }

      void advanceHead(SynchronousQueue.TransferQueue.QNode var1, SynchronousQueue.TransferQueue.QNode var2) {
         if (var1 == this.head && UNSAFE.compareAndSwapObject(this, headOffset, var1, var2)) {
            var1.next = var1;
         }

      }

      void advanceTail(SynchronousQueue.TransferQueue.QNode var1, SynchronousQueue.TransferQueue.QNode var2) {
         if (this.tail == var1) {
            UNSAFE.compareAndSwapObject(this, tailOffset, var1, var2);
         }

      }

      boolean casCleanMe(SynchronousQueue.TransferQueue.QNode var1, SynchronousQueue.TransferQueue.QNode var2) {
         return this.cleanMe == var1 && UNSAFE.compareAndSwapObject(this, cleanMeOffset, var1, var2);
      }

      E transfer(E var1, boolean var2, long var3) {
         SynchronousQueue.TransferQueue.QNode var5 = null;
         boolean var6 = var1 != null;

         while(true) {
            while(true) {
               SynchronousQueue.TransferQueue.QNode var7;
               SynchronousQueue.TransferQueue.QNode var8;
               do {
                  do {
                     var7 = this.tail;
                     var8 = this.head;
                  } while(var7 == null);
               } while(var8 == null);

               SynchronousQueue.TransferQueue.QNode var9;
               Object var10;
               if (var8 != var7 && var7.isData != var6) {
                  var9 = var8.next;
                  if (var7 == this.tail && var9 != null && var8 == this.head) {
                     var10 = var9.item;
                     if (var6 != (var10 != null) && var10 != var9 && var9.casItem(var10, var1)) {
                        this.advanceHead(var8, var9);
                        LockSupport.unpark(var9.waiter);
                        return var10 != null ? var10 : var1;
                     }

                     this.advanceHead(var8, var9);
                  }
               } else {
                  var9 = var7.next;
                  if (var7 == this.tail) {
                     if (var9 != null) {
                        this.advanceTail(var7, var9);
                     } else {
                        if (var2 && var3 <= 0L) {
                           return null;
                        }

                        if (var5 == null) {
                           var5 = new SynchronousQueue.TransferQueue.QNode(var1, var6);
                        }

                        if (var7.casNext((SynchronousQueue.TransferQueue.QNode)null, var5)) {
                           this.advanceTail(var7, var5);
                           var10 = this.awaitFulfill(var5, var1, var2, var3);
                           if (var10 == var5) {
                              this.clean(var7, var5);
                              return null;
                           }

                           if (!var5.isOffList()) {
                              this.advanceHead(var7, var5);
                              if (var10 != null) {
                                 var5.item = var5;
                              }

                              var5.waiter = null;
                           }

                           return var10 != null ? var10 : var1;
                        }
                     }
                  }
               }
            }
         }
      }

      Object awaitFulfill(SynchronousQueue.TransferQueue.QNode var1, E var2, boolean var3, long var4) {
         long var6 = var3 ? System.nanoTime() + var4 : 0L;
         Thread var8 = Thread.currentThread();
         int var9 = this.head.next == var1 ? (var3 ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins) : 0;

         while(true) {
            while(true) {
               if (var8.isInterrupted()) {
                  var1.tryCancel(var2);
               }

               Object var10 = var1.item;
               if (var10 != var2) {
                  return var10;
               }

               if (var3) {
                  var4 = var6 - System.nanoTime();
                  if (var4 <= 0L) {
                     var1.tryCancel(var2);
                     continue;
                  }
               }

               if (var9 > 0) {
                  --var9;
               } else if (var1.waiter == null) {
                  var1.waiter = var8;
               } else if (!var3) {
                  LockSupport.park(this);
               } else if (var4 > 1000L) {
                  LockSupport.parkNanos(this, var4);
               }
            }
         }
      }

      void clean(SynchronousQueue.TransferQueue.QNode var1, SynchronousQueue.TransferQueue.QNode var2) {
         var2.waiter = null;

         while(true) {
            while(true) {
               SynchronousQueue.TransferQueue.QNode var5;
               SynchronousQueue.TransferQueue.QNode var6;
               label50:
               do {
                  while(var1.next == var2) {
                     SynchronousQueue.TransferQueue.QNode var3 = this.head;
                     SynchronousQueue.TransferQueue.QNode var4 = var3.next;
                     if (var4 == null || !var4.isCancelled()) {
                        var5 = this.tail;
                        if (var5 == var3) {
                           return;
                        }

                        var6 = var5.next;
                        continue label50;
                     }

                     this.advanceHead(var3, var4);
                  }

                  return;
               } while(var5 != this.tail);

               if (var6 == null) {
                  SynchronousQueue.TransferQueue.QNode var7;
                  if (var2 != var5) {
                     var7 = var2.next;
                     if (var7 == var2 || var1.casNext(var2, var7)) {
                        return;
                     }
                  }

                  var7 = this.cleanMe;
                  if (var7 == null) {
                     if (this.casCleanMe((SynchronousQueue.TransferQueue.QNode)null, var1)) {
                        return;
                     }
                  } else {
                     SynchronousQueue.TransferQueue.QNode var8 = var7.next;
                     SynchronousQueue.TransferQueue.QNode var9;
                     if (var8 == null || var8 == var7 || !var8.isCancelled() || var8 != var5 && (var9 = var8.next) != null && var9 != var8 && var7.casNext(var8, var9)) {
                        this.casCleanMe(var7, (SynchronousQueue.TransferQueue.QNode)null);
                     }

                     if (var7 == var1) {
                        return;
                     }
                  }
               } else {
                  this.advanceTail(var5, var6);
               }
            }
         }
      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = SynchronousQueue.TransferQueue.class;
            headOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("head"));
            tailOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("tail"));
            cleanMeOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("cleanMe"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }

      static final class QNode {
         volatile SynchronousQueue.TransferQueue.QNode next;
         volatile Object item;
         volatile Thread waiter;
         final boolean isData;
         private static final Unsafe UNSAFE;
         private static final long itemOffset;
         private static final long nextOffset;

         QNode(Object var1, boolean var2) {
            this.item = var1;
            this.isData = var2;
         }

         boolean casNext(SynchronousQueue.TransferQueue.QNode var1, SynchronousQueue.TransferQueue.QNode var2) {
            return this.next == var1 && UNSAFE.compareAndSwapObject(this, nextOffset, var1, var2);
         }

         boolean casItem(Object var1, Object var2) {
            return this.item == var1 && UNSAFE.compareAndSwapObject(this, itemOffset, var1, var2);
         }

         void tryCancel(Object var1) {
            UNSAFE.compareAndSwapObject(this, itemOffset, var1, this);
         }

         boolean isCancelled() {
            return this.item == this;
         }

         boolean isOffList() {
            return this.next == this;
         }

         static {
            try {
               UNSAFE = Unsafe.getUnsafe();
               Class var0 = SynchronousQueue.TransferQueue.QNode.class;
               itemOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("item"));
               nextOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("next"));
            } catch (Exception var1) {
               throw new Error(var1);
            }
         }
      }
   }

   static final class TransferStack<E> extends SynchronousQueue.Transferer<E> {
      static final int REQUEST = 0;
      static final int DATA = 1;
      static final int FULFILLING = 2;
      volatile SynchronousQueue.TransferStack.SNode head;
      private static final Unsafe UNSAFE;
      private static final long headOffset;

      static boolean isFulfilling(int var0) {
         return (var0 & 2) != 0;
      }

      boolean casHead(SynchronousQueue.TransferStack.SNode var1, SynchronousQueue.TransferStack.SNode var2) {
         return var1 == this.head && UNSAFE.compareAndSwapObject(this, headOffset, var1, var2);
      }

      static SynchronousQueue.TransferStack.SNode snode(SynchronousQueue.TransferStack.SNode var0, Object var1, SynchronousQueue.TransferStack.SNode var2, int var3) {
         if (var0 == null) {
            var0 = new SynchronousQueue.TransferStack.SNode(var1);
         }

         var0.mode = var3;
         var0.next = var2;
         return var0;
      }

      E transfer(E var1, boolean var2, long var3) {
         SynchronousQueue.TransferStack.SNode var5 = null;
         int var6 = var1 == null ? 0 : 1;

         while(true) {
            while(true) {
               while(true) {
                  SynchronousQueue.TransferStack.SNode var7 = this.head;
                  SynchronousQueue.TransferStack.SNode var8;
                  if (var7 == null || var7.mode == var6) {
                     if (var2 && var3 <= 0L) {
                        if (var7 == null || !var7.isCancelled()) {
                           return null;
                        }

                        this.casHead(var7, var7.next);
                     } else if (this.casHead(var7, var5 = snode(var5, var1, var7, var6))) {
                        var8 = this.awaitFulfill(var5, var2, var3);
                        if (var8 == var5) {
                           this.clean(var5);
                           return null;
                        }

                        if ((var7 = this.head) != null && var7.next == var5) {
                           this.casHead(var7, var5.next);
                        }

                        return var6 == 0 ? var8.item : var5.item;
                     }
                  } else {
                     SynchronousQueue.TransferStack.SNode var9;
                     if (isFulfilling(var7.mode)) {
                        var8 = var7.next;
                        if (var8 == null) {
                           this.casHead(var7, (SynchronousQueue.TransferStack.SNode)null);
                        } else {
                           var9 = var8.next;
                           if (var8.tryMatch(var7)) {
                              this.casHead(var7, var9);
                           } else {
                              var7.casNext(var8, var9);
                           }
                        }
                     } else if (var7.isCancelled()) {
                        this.casHead(var7, var7.next);
                     } else if (this.casHead(var7, var5 = snode(var5, var1, var7, 2 | var6))) {
                        while(true) {
                           var8 = var5.next;
                           if (var8 == null) {
                              this.casHead(var5, (SynchronousQueue.TransferStack.SNode)null);
                              var5 = null;
                              break;
                           }

                           var9 = var8.next;
                           if (var8.tryMatch(var5)) {
                              this.casHead(var5, var9);
                              return var6 == 0 ? var8.item : var5.item;
                           }

                           var5.casNext(var8, var9);
                        }
                     }
                  }
               }
            }
         }
      }

      SynchronousQueue.TransferStack.SNode awaitFulfill(SynchronousQueue.TransferStack.SNode var1, boolean var2, long var3) {
         long var5 = var2 ? System.nanoTime() + var3 : 0L;
         Thread var7 = Thread.currentThread();
         int var8 = this.shouldSpin(var1) ? (var2 ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins) : 0;

         while(true) {
            while(true) {
               if (var7.isInterrupted()) {
                  var1.tryCancel();
               }

               SynchronousQueue.TransferStack.SNode var9 = var1.match;
               if (var9 != null) {
                  return var9;
               }

               if (var2) {
                  var3 = var5 - System.nanoTime();
                  if (var3 <= 0L) {
                     var1.tryCancel();
                     continue;
                  }
               }

               if (var8 > 0) {
                  var8 = this.shouldSpin(var1) ? var8 - 1 : 0;
               } else if (var1.waiter == null) {
                  var1.waiter = var7;
               } else if (!var2) {
                  LockSupport.park(this);
               } else if (var3 > 1000L) {
                  LockSupport.parkNanos(this, var3);
               }
            }
         }
      }

      boolean shouldSpin(SynchronousQueue.TransferStack.SNode var1) {
         SynchronousQueue.TransferStack.SNode var2 = this.head;
         return var2 == var1 || var2 == null || isFulfilling(var2.mode);
      }

      void clean(SynchronousQueue.TransferStack.SNode var1) {
         var1.item = null;
         var1.waiter = null;
         SynchronousQueue.TransferStack.SNode var2 = var1.next;
         if (var2 != null && var2.isCancelled()) {
            var2 = var2.next;
         }

         SynchronousQueue.TransferStack.SNode var3;
         while((var3 = this.head) != null && var3 != var2 && var3.isCancelled()) {
            this.casHead(var3, var3.next);
         }

         while(var3 != null && var3 != var2) {
            SynchronousQueue.TransferStack.SNode var4 = var3.next;
            if (var4 != null && var4.isCancelled()) {
               var3.casNext(var4, var4.next);
            } else {
               var3 = var4;
            }
         }

      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = SynchronousQueue.TransferStack.class;
            headOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("head"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }

      static final class SNode {
         volatile SynchronousQueue.TransferStack.SNode next;
         volatile SynchronousQueue.TransferStack.SNode match;
         volatile Thread waiter;
         Object item;
         int mode;
         private static final Unsafe UNSAFE;
         private static final long matchOffset;
         private static final long nextOffset;

         SNode(Object var1) {
            this.item = var1;
         }

         boolean casNext(SynchronousQueue.TransferStack.SNode var1, SynchronousQueue.TransferStack.SNode var2) {
            return var1 == this.next && UNSAFE.compareAndSwapObject(this, nextOffset, var1, var2);
         }

         boolean tryMatch(SynchronousQueue.TransferStack.SNode var1) {
            if (this.match == null && UNSAFE.compareAndSwapObject(this, matchOffset, (Object)null, var1)) {
               Thread var2 = this.waiter;
               if (var2 != null) {
                  this.waiter = null;
                  LockSupport.unpark(var2);
               }

               return true;
            } else {
               return this.match == var1;
            }
         }

         void tryCancel() {
            UNSAFE.compareAndSwapObject(this, matchOffset, (Object)null, this);
         }

         boolean isCancelled() {
            return this.match == this;
         }

         static {
            try {
               UNSAFE = Unsafe.getUnsafe();
               Class var0 = SynchronousQueue.TransferStack.SNode.class;
               matchOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("match"));
               nextOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("next"));
            } catch (Exception var1) {
               throw new Error(var1);
            }
         }
      }
   }

   abstract static class Transferer<E> {
      abstract E transfer(E var1, boolean var2, long var3);
   }
}
