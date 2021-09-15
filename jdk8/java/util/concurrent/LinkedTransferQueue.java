package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class LinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, Serializable {
   private static final long serialVersionUID = -3223113410248163686L;
   private static final boolean MP = Runtime.getRuntime().availableProcessors() > 1;
   private static final int FRONT_SPINS = 128;
   private static final int CHAINED_SPINS = 64;
   static final int SWEEP_THRESHOLD = 32;
   transient volatile LinkedTransferQueue.Node head;
   private transient volatile LinkedTransferQueue.Node tail;
   private transient volatile int sweepVotes;
   private static final int NOW = 0;
   private static final int ASYNC = 1;
   private static final int SYNC = 2;
   private static final int TIMED = 3;
   private static final Unsafe UNSAFE;
   private static final long headOffset;
   private static final long tailOffset;
   private static final long sweepVotesOffset;

   private boolean casTail(LinkedTransferQueue.Node var1, LinkedTransferQueue.Node var2) {
      return UNSAFE.compareAndSwapObject(this, tailOffset, var1, var2);
   }

   private boolean casHead(LinkedTransferQueue.Node var1, LinkedTransferQueue.Node var2) {
      return UNSAFE.compareAndSwapObject(this, headOffset, var1, var2);
   }

   private boolean casSweepVotes(int var1, int var2) {
      return UNSAFE.compareAndSwapInt(this, sweepVotesOffset, var1, var2);
   }

   static <E> E cast(Object var0) {
      return var0;
   }

   private E xfer(E var1, boolean var2, int var3, long var4) {
      if (var2 && var1 == null) {
         throw new NullPointerException();
      } else {
         LinkedTransferQueue.Node var6 = null;

         while(true) {
            LinkedTransferQueue.Node var7 = this.head;

            LinkedTransferQueue.Node var11;
            for(LinkedTransferQueue.Node var8 = var7; var8 != null; var8 = var8 != var11 ? var11 : (var7 = this.head)) {
               boolean var9 = var8.isData;
               Object var10 = var8.item;
               if (var10 != var8 && var10 != null == var9) {
                  if (var9 == var2) {
                     break;
                  }

                  if (var8.casItem(var10, var1)) {
                     var11 = var8;

                     while(var11 != var7) {
                        LinkedTransferQueue.Node var12 = var11.next;
                        if (this.head == var7 && this.casHead(var7, var12 == null ? var11 : var12)) {
                           var7.forgetNext();
                           break;
                        }

                        if ((var7 = this.head) == null || (var11 = var7.next) == null || !var11.isMatched()) {
                           break;
                        }
                     }

                     LockSupport.unpark(var8.waiter);
                     return cast(var10);
                  }
               }

               var11 = var8.next;
            }

            if (var3 == 0) {
               break;
            }

            if (var6 == null) {
               var6 = new LinkedTransferQueue.Node(var1, var2);
            }

            var7 = this.tryAppend(var6, var2);
            if (var7 != null) {
               if (var3 != 1) {
                  return this.awaitMatch(var6, var7, var1, var3 == 3, var4);
               }
               break;
            }
         }

         return var1;
      }
   }

   private LinkedTransferQueue.Node tryAppend(LinkedTransferQueue.Node var1, boolean var2) {
      LinkedTransferQueue.Node var3 = this.tail;
      LinkedTransferQueue.Node var4 = var3;

      do {
         while(var4 != null || (var4 = this.head) != null) {
            if (var4.cannotPrecede(var2)) {
               return null;
            }

            LinkedTransferQueue.Node var5;
            if ((var5 = var4.next) == null) {
               if (var4.casNext((LinkedTransferQueue.Node)null, var1)) {
                  if (var4 != var3) {
                     while((this.tail != var3 || !this.casTail(var3, var1)) && (var3 = this.tail) != null && (var1 = var3.next) != null && (var1 = var1.next) != null && var1 != var3) {
                     }
                  }

                  return var4;
               }

               var4 = var4.next;
            } else {
               LinkedTransferQueue.Node var6;
               var4 = var4 != var3 && var3 != (var6 = this.tail) ? (var3 = var6) : (var4 != var5 ? var5 : null);
            }
         }
      } while(!this.casHead((LinkedTransferQueue.Node)null, var1));

      return var1;
   }

   private E awaitMatch(LinkedTransferQueue.Node var1, LinkedTransferQueue.Node var2, E var3, boolean var4, long var5) {
      long var7 = var4 ? System.nanoTime() + var5 : 0L;
      Thread var9 = Thread.currentThread();
      int var10 = -1;
      ThreadLocalRandom var11 = null;

      while(true) {
         Object var12 = var1.item;
         if (var12 != var3) {
            var1.forgetContents();
            return cast(var12);
         }

         if ((var9.isInterrupted() || var4 && var5 <= 0L) && var1.casItem(var3, var1)) {
            this.unsplice(var2, var1);
            return var3;
         }

         if (var10 < 0) {
            if ((var10 = spinsFor(var2, var1.isData)) > 0) {
               var11 = ThreadLocalRandom.current();
            }
         } else if (var10 > 0) {
            --var10;
            if (var11.nextInt(64) == 0) {
               Thread.yield();
            }
         } else if (var1.waiter == null) {
            var1.waiter = var9;
         } else if (var4) {
            var5 = var7 - System.nanoTime();
            if (var5 > 0L) {
               LockSupport.parkNanos(this, var5);
            }
         } else {
            LockSupport.park(this);
         }
      }
   }

   private static int spinsFor(LinkedTransferQueue.Node var0, boolean var1) {
      if (MP && var0 != null) {
         if (var0.isData != var1) {
            return 192;
         }

         if (var0.isMatched()) {
            return 128;
         }

         if (var0.waiter == null) {
            return 64;
         }
      }

      return 0;
   }

   final LinkedTransferQueue.Node succ(LinkedTransferQueue.Node var1) {
      LinkedTransferQueue.Node var2 = var1.next;
      return var1 == var2 ? this.head : var2;
   }

   private LinkedTransferQueue.Node firstOfMode(boolean var1) {
      for(LinkedTransferQueue.Node var2 = this.head; var2 != null; var2 = this.succ(var2)) {
         if (!var2.isMatched()) {
            return var2.isData == var1 ? var2 : null;
         }
      }

      return null;
   }

   final LinkedTransferQueue.Node firstDataNode() {
      LinkedTransferQueue.Node var1 = this.head;

      while(var1 != null) {
         Object var2 = var1.item;
         if (var1.isData) {
            if (var2 != null && var2 != var1) {
               return var1;
            }
         } else if (var2 == null) {
            break;
         }

         if (var1 == (var1 = var1.next)) {
            var1 = this.head;
         }
      }

      return null;
   }

   private E firstDataItem() {
      for(LinkedTransferQueue.Node var1 = this.head; var1 != null; var1 = this.succ(var1)) {
         Object var2 = var1.item;
         if (var1.isData) {
            if (var2 != null && var2 != var1) {
               return cast(var2);
            }
         } else if (var2 == null) {
            return null;
         }
      }

      return null;
   }

   private int countOfMode(boolean var1) {
      int var2 = 0;
      LinkedTransferQueue.Node var3 = this.head;

      while(var3 != null) {
         if (!var3.isMatched()) {
            if (var3.isData != var1) {
               return 0;
            }

            ++var2;
            if (var2 == Integer.MAX_VALUE) {
               break;
            }
         }

         LinkedTransferQueue.Node var4 = var3.next;
         if (var4 != var3) {
            var3 = var4;
         } else {
            var2 = 0;
            var3 = this.head;
         }
      }

      return var2;
   }

   public Spliterator<E> spliterator() {
      return new LinkedTransferQueue.LTQSpliterator(this);
   }

   final void unsplice(LinkedTransferQueue.Node var1, LinkedTransferQueue.Node var2) {
      var2.forgetContents();
      if (var1 != null && var1 != var2 && var1.next == var2) {
         LinkedTransferQueue.Node var3 = var2.next;
         if (var3 == null || var3 != var2 && var1.casNext(var2, var3) && var1.isMatched()) {
            while(true) {
               LinkedTransferQueue.Node var4 = this.head;
               if (var4 == var1 || var4 == var2 || var4 == null) {
                  return;
               }

               if (!var4.isMatched()) {
                  int var6;
                  if (var1.next != var1 && var2.next != var2) {
                     label45:
                     do {
                        do {
                           var6 = this.sweepVotes;
                           if (var6 < 32) {
                              continue label45;
                           }
                        } while(!this.casSweepVotes(var6, 0));

                        this.sweep();
                        return;
                     } while(!this.casSweepVotes(var6, var6 + 1));
                  }
                  break;
               }

               LinkedTransferQueue.Node var5 = var4.next;
               if (var5 == null) {
                  return;
               }

               if (var5 != var4 && this.casHead(var4, var5)) {
                  var4.forgetNext();
               }
            }
         }
      }

   }

   private void sweep() {
      LinkedTransferQueue.Node var1 = this.head;

      LinkedTransferQueue.Node var2;
      while(var1 != null && (var2 = var1.next) != null) {
         if (!var2.isMatched()) {
            var1 = var2;
         } else {
            LinkedTransferQueue.Node var3;
            if ((var3 = var2.next) == null) {
               break;
            }

            if (var2 == var3) {
               var1 = this.head;
            } else {
               var1.casNext(var2, var3);
            }
         }
      }

   }

   private boolean findAndRemove(Object var1) {
      if (var1 != null) {
         LinkedTransferQueue.Node var2 = null;
         LinkedTransferQueue.Node var3 = this.head;

         while(var3 != null) {
            Object var4 = var3.item;
            if (var3.isData) {
               if (var4 != null && var4 != var3 && var1.equals(var4) && var3.tryMatchData()) {
                  this.unsplice(var2, var3);
                  return true;
               }
            } else if (var4 == null) {
               break;
            }

            var2 = var3;
            if ((var3 = var3.next) == var2) {
               var2 = null;
               var3 = this.head;
            }
         }
      }

      return false;
   }

   public LinkedTransferQueue() {
   }

   public LinkedTransferQueue(Collection<? extends E> var1) {
      this();
      this.addAll(var1);
   }

   public void put(E var1) {
      this.xfer(var1, true, 1, 0L);
   }

   public boolean offer(E var1, long var2, TimeUnit var4) {
      this.xfer(var1, true, 1, 0L);
      return true;
   }

   public boolean offer(E var1) {
      this.xfer(var1, true, 1, 0L);
      return true;
   }

   public boolean add(E var1) {
      this.xfer(var1, true, 1, 0L);
      return true;
   }

   public boolean tryTransfer(E var1) {
      return this.xfer(var1, true, 0, 0L) == null;
   }

   public void transfer(E var1) throws InterruptedException {
      if (this.xfer(var1, true, 2, 0L) != null) {
         Thread.interrupted();
         throw new InterruptedException();
      }
   }

   public boolean tryTransfer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      if (this.xfer(var1, true, 3, var4.toNanos(var2)) == null) {
         return true;
      } else if (!Thread.interrupted()) {
         return false;
      } else {
         throw new InterruptedException();
      }
   }

   public E take() throws InterruptedException {
      Object var1 = this.xfer((Object)null, false, 2, 0L);
      if (var1 != null) {
         return var1;
      } else {
         Thread.interrupted();
         throw new InterruptedException();
      }
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      Object var4 = this.xfer((Object)null, false, 3, var3.toNanos(var1));
      if (var4 == null && Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return var4;
      }
   }

   public E poll() {
      return this.xfer((Object)null, false, 0, 0L);
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

   public Iterator<E> iterator() {
      return new LinkedTransferQueue.Itr();
   }

   public E peek() {
      return this.firstDataItem();
   }

   public boolean isEmpty() {
      for(LinkedTransferQueue.Node var1 = this.head; var1 != null; var1 = this.succ(var1)) {
         if (!var1.isMatched()) {
            return !var1.isData;
         }
      }

      return true;
   }

   public boolean hasWaitingConsumer() {
      return this.firstOfMode(false) != null;
   }

   public int size() {
      return this.countOfMode(true);
   }

   public int getWaitingConsumerCount() {
      return this.countOfMode(false);
   }

   public boolean remove(Object var1) {
      return this.findAndRemove(var1);
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         for(LinkedTransferQueue.Node var2 = this.head; var2 != null; var2 = this.succ(var2)) {
            Object var3 = var2.item;
            if (var2.isData) {
               if (var3 != null && var3 != var2 && var1.equals(var3)) {
                  return true;
               }
            } else if (var3 == null) {
               break;
            }
         }

         return false;
      }
   }

   public int remainingCapacity() {
      return Integer.MAX_VALUE;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      Iterator var2 = this.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.writeObject(var3);
      }

      var1.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();

      while(true) {
         Object var2 = var1.readObject();
         if (var2 == null) {
            return;
         }

         this.offer(var2);
      }
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = LinkedTransferQueue.class;
         headOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("head"));
         tailOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("tail"));
         sweepVotesOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("sweepVotes"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class LTQSpliterator<E> implements Spliterator<E> {
      static final int MAX_BATCH = 33554432;
      final LinkedTransferQueue<E> queue;
      LinkedTransferQueue.Node current;
      int batch;
      boolean exhausted;

      LTQSpliterator(LinkedTransferQueue<E> var1) {
         this.queue = var1;
      }

      public Spliterator<E> trySplit() {
         LinkedTransferQueue var2 = this.queue;
         int var3 = this.batch;
         int var4 = var3 <= 0 ? 1 : (var3 >= 33554432 ? 33554432 : var3 + 1);
         LinkedTransferQueue.Node var1;
         if (!this.exhausted && ((var1 = this.current) != null || (var1 = var2.firstDataNode()) != null) && var1.next != null) {
            Object[] var5 = new Object[var4];
            int var6 = 0;

            do {
               Object var7 = var1.item;
               if (var7 != var1 && (var5[var6] = var7) != null) {
                  ++var6;
               }

               if (var1 == (var1 = var1.next)) {
                  var1 = var2.firstDataNode();
               }
            } while(var1 != null && var6 < var4 && var1.isData);

            if ((this.current = var1) == null) {
               this.exhausted = true;
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
            LinkedTransferQueue var3 = this.queue;
            LinkedTransferQueue.Node var2;
            if (!this.exhausted && ((var2 = this.current) != null || (var2 = var3.firstDataNode()) != null)) {
               this.exhausted = true;

               do {
                  Object var4 = var2.item;
                  if (var4 != null && var4 != var2) {
                     var1.accept(var4);
                  }

                  if (var2 == (var2 = var2.next)) {
                     var2 = var3.firstDataNode();
                  }
               } while(var2 != null && var2.isData);
            }

         }
      }

      public boolean tryAdvance(Consumer<? super E> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            LinkedTransferQueue var3 = this.queue;
            LinkedTransferQueue.Node var2;
            if (!this.exhausted && ((var2 = this.current) != null || (var2 = var3.firstDataNode()) != null)) {
               Object var4;
               do {
                  if ((var4 = var2.item) == var2) {
                     var4 = null;
                  }

                  if (var2 == (var2 = var2.next)) {
                     var2 = var3.firstDataNode();
                  }
               } while(var4 == null && var2 != null && var2.isData);

               if ((this.current = var2) == null) {
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

      public long estimateSize() {
         return Long.MAX_VALUE;
      }

      public int characteristics() {
         return 4368;
      }
   }

   final class Itr implements Iterator<E> {
      private LinkedTransferQueue.Node nextNode;
      private E nextItem;
      private LinkedTransferQueue.Node lastRet;
      private LinkedTransferQueue.Node lastPred;

      private void advance(LinkedTransferQueue.Node var1) {
         LinkedTransferQueue.Node var2;
         LinkedTransferQueue.Node var4;
         LinkedTransferQueue.Node var5;
         if ((var2 = this.lastRet) != null && !var2.isMatched()) {
            this.lastPred = var2;
         } else {
            LinkedTransferQueue.Node var3;
            if ((var3 = this.lastPred) != null && !var3.isMatched()) {
               while((var4 = var3.next) != null && var4 != var3 && var4.isMatched() && (var5 = var4.next) != null && var5 != var4) {
                  var3.casNext(var4, var5);
               }
            } else {
               this.lastPred = null;
            }
         }

         this.lastRet = var1;
         var4 = var1;

         while(true) {
            var5 = var4 == null ? LinkedTransferQueue.this.head : var4.next;
            if (var5 == null) {
               break;
            }

            if (var5 == var4) {
               var4 = null;
            } else {
               Object var7 = var5.item;
               if (var5.isData) {
                  if (var7 != null && var7 != var5) {
                     this.nextItem = LinkedTransferQueue.cast(var7);
                     this.nextNode = var5;
                     return;
                  }
               } else if (var7 == null) {
                  break;
               }

               if (var4 == null) {
                  var4 = var5;
               } else {
                  LinkedTransferQueue.Node var6;
                  if ((var6 = var5.next) == null) {
                     break;
                  }

                  if (var5 == var6) {
                     var4 = null;
                  } else {
                     var4.casNext(var5, var6);
                  }
               }
            }
         }

         this.nextNode = null;
         this.nextItem = null;
      }

      Itr() {
         this.advance((LinkedTransferQueue.Node)null);
      }

      public final boolean hasNext() {
         return this.nextNode != null;
      }

      public final E next() {
         LinkedTransferQueue.Node var1 = this.nextNode;
         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            Object var2 = this.nextItem;
            this.advance(var1);
            return var2;
         }
      }

      public final void remove() {
         LinkedTransferQueue.Node var1 = this.lastRet;
         if (var1 == null) {
            throw new IllegalStateException();
         } else {
            this.lastRet = null;
            if (var1.tryMatchData()) {
               LinkedTransferQueue.this.unsplice(this.lastPred, var1);
            }

         }
      }
   }

   static final class Node {
      final boolean isData;
      volatile Object item;
      volatile LinkedTransferQueue.Node next;
      volatile Thread waiter;
      private static final long serialVersionUID = -3375979862319811754L;
      private static final Unsafe UNSAFE;
      private static final long itemOffset;
      private static final long nextOffset;
      private static final long waiterOffset;

      final boolean casNext(LinkedTransferQueue.Node var1, LinkedTransferQueue.Node var2) {
         return UNSAFE.compareAndSwapObject(this, nextOffset, var1, var2);
      }

      final boolean casItem(Object var1, Object var2) {
         return UNSAFE.compareAndSwapObject(this, itemOffset, var1, var2);
      }

      Node(Object var1, boolean var2) {
         UNSAFE.putObject(this, itemOffset, var1);
         this.isData = var2;
      }

      final void forgetNext() {
         UNSAFE.putObject(this, nextOffset, this);
      }

      final void forgetContents() {
         UNSAFE.putObject(this, itemOffset, this);
         UNSAFE.putObject(this, waiterOffset, (Object)null);
      }

      final boolean isMatched() {
         Object var1 = this.item;
         return var1 == this || var1 == null == this.isData;
      }

      final boolean isUnmatchedRequest() {
         return !this.isData && this.item == null;
      }

      final boolean cannotPrecede(boolean var1) {
         boolean var2 = this.isData;
         Object var3;
         return var2 != var1 && (var3 = this.item) != this && var3 != null == var2;
      }

      final boolean tryMatchData() {
         Object var1 = this.item;
         if (var1 != null && var1 != this && this.casItem(var1, (Object)null)) {
            LockSupport.unpark(this.waiter);
            return true;
         } else {
            return false;
         }
      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = LinkedTransferQueue.Node.class;
            itemOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("item"));
            nextOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("next"));
            waiterOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("waiter"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }
}
