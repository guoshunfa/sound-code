package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public abstract class AbstractQueuedLongSynchronizer extends AbstractOwnableSynchronizer implements Serializable {
   private static final long serialVersionUID = 7373984972572414692L;
   private transient volatile AbstractQueuedLongSynchronizer.Node head;
   private transient volatile AbstractQueuedLongSynchronizer.Node tail;
   private volatile long state;
   static final long spinForTimeoutThreshold = 1000L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final long stateOffset;
   private static final long headOffset;
   private static final long tailOffset;
   private static final long waitStatusOffset;
   private static final long nextOffset;

   protected AbstractQueuedLongSynchronizer() {
   }

   protected final long getState() {
      return this.state;
   }

   protected final void setState(long var1) {
      this.state = var1;
   }

   protected final boolean compareAndSetState(long var1, long var3) {
      return unsafe.compareAndSwapLong(this, stateOffset, var1, var3);
   }

   private AbstractQueuedLongSynchronizer.Node enq(AbstractQueuedLongSynchronizer.Node var1) {
      while(true) {
         AbstractQueuedLongSynchronizer.Node var2 = this.tail;
         if (var2 == null) {
            if (this.compareAndSetHead(new AbstractQueuedLongSynchronizer.Node())) {
               this.tail = this.head;
            }
         } else {
            var1.prev = var2;
            if (this.compareAndSetTail(var2, var1)) {
               var2.next = var1;
               return var2;
            }
         }
      }
   }

   private AbstractQueuedLongSynchronizer.Node addWaiter(AbstractQueuedLongSynchronizer.Node var1) {
      AbstractQueuedLongSynchronizer.Node var2 = new AbstractQueuedLongSynchronizer.Node(Thread.currentThread(), var1);
      AbstractQueuedLongSynchronizer.Node var3 = this.tail;
      if (var3 != null) {
         var2.prev = var3;
         if (this.compareAndSetTail(var3, var2)) {
            var3.next = var2;
            return var2;
         }
      }

      this.enq(var2);
      return var2;
   }

   private void setHead(AbstractQueuedLongSynchronizer.Node var1) {
      this.head = var1;
      var1.thread = null;
      var1.prev = null;
   }

   private void unparkSuccessor(AbstractQueuedLongSynchronizer.Node var1) {
      int var2 = var1.waitStatus;
      if (var2 < 0) {
         compareAndSetWaitStatus(var1, var2, 0);
      }

      AbstractQueuedLongSynchronizer.Node var3 = var1.next;
      if (var3 == null || var3.waitStatus > 0) {
         var3 = null;

         for(AbstractQueuedLongSynchronizer.Node var4 = this.tail; var4 != null && var4 != var1; var4 = var4.prev) {
            if (var4.waitStatus <= 0) {
               var3 = var4;
            }
         }
      }

      if (var3 != null) {
         LockSupport.unpark(var3.thread);
      }

   }

   private void doReleaseShared() {
      while(true) {
         AbstractQueuedLongSynchronizer.Node var1 = this.head;
         if (var1 != null && var1 != this.tail) {
            int var2 = var1.waitStatus;
            if (var2 == -1) {
               if (!compareAndSetWaitStatus(var1, -1, 0)) {
                  continue;
               }

               this.unparkSuccessor(var1);
            } else if (var2 == 0 && !compareAndSetWaitStatus(var1, 0, -3)) {
               continue;
            }
         }

         if (var1 == this.head) {
            return;
         }
      }
   }

   private void setHeadAndPropagate(AbstractQueuedLongSynchronizer.Node var1, long var2) {
      AbstractQueuedLongSynchronizer.Node var4 = this.head;
      this.setHead(var1);
      if (var2 > 0L || var4 == null || var4.waitStatus < 0 || (var4 = this.head) == null || var4.waitStatus < 0) {
         AbstractQueuedLongSynchronizer.Node var5 = var1.next;
         if (var5 == null || var5.isShared()) {
            this.doReleaseShared();
         }
      }

   }

   private void cancelAcquire(AbstractQueuedLongSynchronizer.Node var1) {
      if (var1 != null) {
         var1.thread = null;

         AbstractQueuedLongSynchronizer.Node var2;
         for(var2 = var1.prev; var2.waitStatus > 0; var1.prev = var2 = var2.prev) {
         }

         AbstractQueuedLongSynchronizer.Node var3 = var2.next;
         var1.waitStatus = 1;
         if (var1 == this.tail && this.compareAndSetTail(var1, var2)) {
            compareAndSetNext(var2, var3, (AbstractQueuedLongSynchronizer.Node)null);
         } else {
            int var4;
            if (var2 != this.head && ((var4 = var2.waitStatus) == -1 || var4 <= 0 && compareAndSetWaitStatus(var2, var4, -1)) && var2.thread != null) {
               AbstractQueuedLongSynchronizer.Node var5 = var1.next;
               if (var5 != null && var5.waitStatus <= 0) {
                  compareAndSetNext(var2, var3, var5);
               }
            } else {
               this.unparkSuccessor(var1);
            }

            var1.next = var1;
         }

      }
   }

   private static boolean shouldParkAfterFailedAcquire(AbstractQueuedLongSynchronizer.Node var0, AbstractQueuedLongSynchronizer.Node var1) {
      int var2 = var0.waitStatus;
      if (var2 == -1) {
         return true;
      } else {
         if (var2 > 0) {
            do {
               var1.prev = var0 = var0.prev;
            } while(var0.waitStatus > 0);

            var0.next = var1;
         } else {
            compareAndSetWaitStatus(var0, var2, -1);
         }

         return false;
      }
   }

   static void selfInterrupt() {
      Thread.currentThread().interrupt();
   }

   private final boolean parkAndCheckInterrupt() {
      LockSupport.park(this);
      return Thread.interrupted();
   }

   final boolean acquireQueued(AbstractQueuedLongSynchronizer.Node var1, long var2) {
      boolean var4 = true;

      try {
         boolean var5 = false;

         while(true) {
            AbstractQueuedLongSynchronizer.Node var6 = var1.predecessor();
            if (var6 == this.head && this.tryAcquire(var2)) {
               this.setHead(var1);
               var6.next = null;
               var4 = false;
               boolean var7 = var5;
               return var7;
            }

            if (shouldParkAfterFailedAcquire(var6, var1) && this.parkAndCheckInterrupt()) {
               var5 = true;
            }
         }
      } finally {
         if (var4) {
            this.cancelAcquire(var1);
         }

      }
   }

   private void doAcquireInterruptibly(long var1) throws InterruptedException {
      AbstractQueuedLongSynchronizer.Node var3 = this.addWaiter(AbstractQueuedLongSynchronizer.Node.EXCLUSIVE);
      boolean var4 = true;

      try {
         AbstractQueuedLongSynchronizer.Node var5;
         do {
            var5 = var3.predecessor();
            if (var5 == this.head && this.tryAcquire(var1)) {
               this.setHead(var3);
               var5.next = null;
               var4 = false;
               return;
            }
         } while(!shouldParkAfterFailedAcquire(var5, var3) || !this.parkAndCheckInterrupt());

         throw new InterruptedException();
      } finally {
         if (var4) {
            this.cancelAcquire(var3);
         }

      }
   }

   private boolean doAcquireNanos(long var1, long var3) throws InterruptedException {
      if (var3 <= 0L) {
         return false;
      } else {
         long var5 = System.nanoTime() + var3;
         AbstractQueuedLongSynchronizer.Node var7 = this.addWaiter(AbstractQueuedLongSynchronizer.Node.EXCLUSIVE);
         boolean var8 = true;

         try {
            do {
               AbstractQueuedLongSynchronizer.Node var9 = var7.predecessor();
               boolean var10;
               if (var9 == this.head && this.tryAcquire(var1)) {
                  this.setHead(var7);
                  var9.next = null;
                  var8 = false;
                  var10 = true;
                  return var10;
               }

               var3 = var5 - System.nanoTime();
               if (var3 <= 0L) {
                  var10 = false;
                  return var10;
               }

               if (shouldParkAfterFailedAcquire(var9, var7) && var3 > 1000L) {
                  LockSupport.parkNanos(this, var3);
               }
            } while(!Thread.interrupted());

            throw new InterruptedException();
         } finally {
            if (var8) {
               this.cancelAcquire(var7);
            }

         }
      }
   }

   private void doAcquireShared(long var1) {
      AbstractQueuedLongSynchronizer.Node var3 = this.addWaiter(AbstractQueuedLongSynchronizer.Node.SHARED);
      boolean var4 = true;

      try {
         boolean var5 = false;

         while(true) {
            AbstractQueuedLongSynchronizer.Node var6 = var3.predecessor();
            if (var6 == this.head) {
               long var7 = this.tryAcquireShared(var1);
               if (var7 >= 0L) {
                  this.setHeadAndPropagate(var3, var7);
                  var6.next = null;
                  if (var5) {
                     selfInterrupt();
                  }

                  var4 = false;
                  return;
               }
            }

            if (shouldParkAfterFailedAcquire(var6, var3) && this.parkAndCheckInterrupt()) {
               var5 = true;
            }
         }
      } finally {
         if (var4) {
            this.cancelAcquire(var3);
         }

      }
   }

   private void doAcquireSharedInterruptibly(long var1) throws InterruptedException {
      AbstractQueuedLongSynchronizer.Node var3 = this.addWaiter(AbstractQueuedLongSynchronizer.Node.SHARED);
      boolean var4 = true;

      try {
         AbstractQueuedLongSynchronizer.Node var5;
         do {
            var5 = var3.predecessor();
            if (var5 == this.head) {
               long var6 = this.tryAcquireShared(var1);
               if (var6 >= 0L) {
                  this.setHeadAndPropagate(var3, var6);
                  var5.next = null;
                  var4 = false;
                  return;
               }
            }
         } while(!shouldParkAfterFailedAcquire(var5, var3) || !this.parkAndCheckInterrupt());

         throw new InterruptedException();
      } finally {
         if (var4) {
            this.cancelAcquire(var3);
         }

      }
   }

   private boolean doAcquireSharedNanos(long var1, long var3) throws InterruptedException {
      if (var3 <= 0L) {
         return false;
      } else {
         long var5 = System.nanoTime() + var3;
         AbstractQueuedLongSynchronizer.Node var7 = this.addWaiter(AbstractQueuedLongSynchronizer.Node.SHARED);
         boolean var8 = true;

         while(true) {
            boolean var16;
            try {
               AbstractQueuedLongSynchronizer.Node var9 = var7.predecessor();
               if (var9 == this.head) {
                  long var10 = this.tryAcquireShared(var1);
                  if (var10 >= 0L) {
                     this.setHeadAndPropagate(var7, var10);
                     var9.next = null;
                     var8 = false;
                     boolean var12 = true;
                     return var12;
                  }
               }

               var3 = var5 - System.nanoTime();
               if (var3 > 0L) {
                  if (shouldParkAfterFailedAcquire(var9, var7) && var3 > 1000L) {
                     LockSupport.parkNanos(this, var3);
                  }

                  if (Thread.interrupted()) {
                     throw new InterruptedException();
                  }
                  continue;
               }

               var16 = false;
            } finally {
               if (var8) {
                  this.cancelAcquire(var7);
               }

            }

            return var16;
         }
      }
   }

   protected boolean tryAcquire(long var1) {
      throw new UnsupportedOperationException();
   }

   protected boolean tryRelease(long var1) {
      throw new UnsupportedOperationException();
   }

   protected long tryAcquireShared(long var1) {
      throw new UnsupportedOperationException();
   }

   protected boolean tryReleaseShared(long var1) {
      throw new UnsupportedOperationException();
   }

   protected boolean isHeldExclusively() {
      throw new UnsupportedOperationException();
   }

   public final void acquire(long var1) {
      if (!this.tryAcquire(var1) && this.acquireQueued(this.addWaiter(AbstractQueuedLongSynchronizer.Node.EXCLUSIVE), var1)) {
         selfInterrupt();
      }

   }

   public final void acquireInterruptibly(long var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         if (!this.tryAcquire(var1)) {
            this.doAcquireInterruptibly(var1);
         }

      }
   }

   public final boolean tryAcquireNanos(long var1, long var3) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this.tryAcquire(var1) || this.doAcquireNanos(var1, var3);
      }
   }

   public final boolean release(long var1) {
      if (this.tryRelease(var1)) {
         AbstractQueuedLongSynchronizer.Node var3 = this.head;
         if (var3 != null && var3.waitStatus != 0) {
            this.unparkSuccessor(var3);
         }

         return true;
      } else {
         return false;
      }
   }

   public final void acquireShared(long var1) {
      if (this.tryAcquireShared(var1) < 0L) {
         this.doAcquireShared(var1);
      }

   }

   public final void acquireSharedInterruptibly(long var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         if (this.tryAcquireShared(var1) < 0L) {
            this.doAcquireSharedInterruptibly(var1);
         }

      }
   }

   public final boolean tryAcquireSharedNanos(long var1, long var3) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this.tryAcquireShared(var1) >= 0L || this.doAcquireSharedNanos(var1, var3);
      }
   }

   public final boolean releaseShared(long var1) {
      if (this.tryReleaseShared(var1)) {
         this.doReleaseShared();
         return true;
      } else {
         return false;
      }
   }

   public final boolean hasQueuedThreads() {
      return this.head != this.tail;
   }

   public final boolean hasContended() {
      return this.head != null;
   }

   public final Thread getFirstQueuedThread() {
      return this.head == this.tail ? null : this.fullGetFirstQueuedThread();
   }

   private Thread fullGetFirstQueuedThread() {
      AbstractQueuedLongSynchronizer.Node var1;
      AbstractQueuedLongSynchronizer.Node var2;
      Thread var3;
      if (((var1 = this.head) == null || (var2 = var1.next) == null || var2.prev != this.head || (var3 = var2.thread) == null) && ((var1 = this.head) == null || (var2 = var1.next) == null || var2.prev != this.head || (var3 = var2.thread) == null)) {
         AbstractQueuedLongSynchronizer.Node var4 = this.tail;

         Thread var5;
         for(var5 = null; var4 != null && var4 != this.head; var4 = var4.prev) {
            Thread var6 = var4.thread;
            if (var6 != null) {
               var5 = var6;
            }
         }

         return var5;
      } else {
         return var3;
      }
   }

   public final boolean isQueued(Thread var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         for(AbstractQueuedLongSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
            if (var2.thread == var1) {
               return true;
            }
         }

         return false;
      }
   }

   final boolean apparentlyFirstQueuedIsExclusive() {
      AbstractQueuedLongSynchronizer.Node var1;
      AbstractQueuedLongSynchronizer.Node var2;
      return (var1 = this.head) != null && (var2 = var1.next) != null && !var2.isShared() && var2.thread != null;
   }

   public final boolean hasQueuedPredecessors() {
      AbstractQueuedLongSynchronizer.Node var1 = this.tail;
      AbstractQueuedLongSynchronizer.Node var2 = this.head;
      AbstractQueuedLongSynchronizer.Node var3;
      return var2 != var1 && ((var3 = var2.next) == null || var3.thread != Thread.currentThread());
   }

   public final int getQueueLength() {
      int var1 = 0;

      for(AbstractQueuedLongSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
         if (var2.thread != null) {
            ++var1;
         }
      }

      return var1;
   }

   public final Collection<Thread> getQueuedThreads() {
      ArrayList var1 = new ArrayList();

      for(AbstractQueuedLongSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
         Thread var3 = var2.thread;
         if (var3 != null) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public final Collection<Thread> getExclusiveQueuedThreads() {
      ArrayList var1 = new ArrayList();

      for(AbstractQueuedLongSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
         if (!var2.isShared()) {
            Thread var3 = var2.thread;
            if (var3 != null) {
               var1.add(var3);
            }
         }
      }

      return var1;
   }

   public final Collection<Thread> getSharedQueuedThreads() {
      ArrayList var1 = new ArrayList();

      for(AbstractQueuedLongSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
         if (var2.isShared()) {
            Thread var3 = var2.thread;
            if (var3 != null) {
               var1.add(var3);
            }
         }
      }

      return var1;
   }

   public String toString() {
      long var1 = this.getState();
      String var3 = this.hasQueuedThreads() ? "non" : "";
      return super.toString() + "[State = " + var1 + ", " + var3 + "empty queue]";
   }

   final boolean isOnSyncQueue(AbstractQueuedLongSynchronizer.Node var1) {
      if (var1.waitStatus != -2 && var1.prev != null) {
         return var1.next != null ? true : this.findNodeFromTail(var1);
      } else {
         return false;
      }
   }

   private boolean findNodeFromTail(AbstractQueuedLongSynchronizer.Node var1) {
      for(AbstractQueuedLongSynchronizer.Node var2 = this.tail; var2 != var1; var2 = var2.prev) {
         if (var2 == null) {
            return false;
         }
      }

      return true;
   }

   final boolean transferForSignal(AbstractQueuedLongSynchronizer.Node var1) {
      if (!compareAndSetWaitStatus(var1, -2, 0)) {
         return false;
      } else {
         AbstractQueuedLongSynchronizer.Node var2 = this.enq(var1);
         int var3 = var2.waitStatus;
         if (var3 > 0 || !compareAndSetWaitStatus(var2, var3, -1)) {
            LockSupport.unpark(var1.thread);
         }

         return true;
      }
   }

   final boolean transferAfterCancelledWait(AbstractQueuedLongSynchronizer.Node var1) {
      if (compareAndSetWaitStatus(var1, -2, 0)) {
         this.enq(var1);
         return true;
      } else {
         while(!this.isOnSyncQueue(var1)) {
            Thread.yield();
         }

         return false;
      }
   }

   final long fullyRelease(AbstractQueuedLongSynchronizer.Node var1) {
      boolean var2 = true;

      long var5;
      try {
         long var3 = this.getState();
         if (!this.release(var3)) {
            throw new IllegalMonitorStateException();
         }

         var2 = false;
         var5 = var3;
      } finally {
         if (var2) {
            var1.waitStatus = 1;
         }

      }

      return var5;
   }

   public final boolean owns(AbstractQueuedLongSynchronizer.ConditionObject var1) {
      return var1.isOwnedBy(this);
   }

   public final boolean hasWaiters(AbstractQueuedLongSynchronizer.ConditionObject var1) {
      if (!this.owns(var1)) {
         throw new IllegalArgumentException("Not owner");
      } else {
         return var1.hasWaiters();
      }
   }

   public final int getWaitQueueLength(AbstractQueuedLongSynchronizer.ConditionObject var1) {
      if (!this.owns(var1)) {
         throw new IllegalArgumentException("Not owner");
      } else {
         return var1.getWaitQueueLength();
      }
   }

   public final Collection<Thread> getWaitingThreads(AbstractQueuedLongSynchronizer.ConditionObject var1) {
      if (!this.owns(var1)) {
         throw new IllegalArgumentException("Not owner");
      } else {
         return var1.getWaitingThreads();
      }
   }

   private final boolean compareAndSetHead(AbstractQueuedLongSynchronizer.Node var1) {
      return unsafe.compareAndSwapObject(this, headOffset, (Object)null, var1);
   }

   private final boolean compareAndSetTail(AbstractQueuedLongSynchronizer.Node var1, AbstractQueuedLongSynchronizer.Node var2) {
      return unsafe.compareAndSwapObject(this, tailOffset, var1, var2);
   }

   private static final boolean compareAndSetWaitStatus(AbstractQueuedLongSynchronizer.Node var0, int var1, int var2) {
      return unsafe.compareAndSwapInt(var0, waitStatusOffset, var1, var2);
   }

   private static final boolean compareAndSetNext(AbstractQueuedLongSynchronizer.Node var0, AbstractQueuedLongSynchronizer.Node var1, AbstractQueuedLongSynchronizer.Node var2) {
      return unsafe.compareAndSwapObject(var0, nextOffset, var1, var2);
   }

   static {
      try {
         stateOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("state"));
         headOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("head"));
         tailOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("tail"));
         waitStatusOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.Node.class.getDeclaredField("waitStatus"));
         nextOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.Node.class.getDeclaredField("next"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   public class ConditionObject implements Condition, Serializable {
      private static final long serialVersionUID = 1173984872572414699L;
      private transient AbstractQueuedLongSynchronizer.Node firstWaiter;
      private transient AbstractQueuedLongSynchronizer.Node lastWaiter;
      private static final int REINTERRUPT = 1;
      private static final int THROW_IE = -1;

      private AbstractQueuedLongSynchronizer.Node addConditionWaiter() {
         AbstractQueuedLongSynchronizer.Node var1 = this.lastWaiter;
         if (var1 != null && var1.waitStatus != -2) {
            this.unlinkCancelledWaiters();
            var1 = this.lastWaiter;
         }

         AbstractQueuedLongSynchronizer.Node var2 = new AbstractQueuedLongSynchronizer.Node(Thread.currentThread(), -2);
         if (var1 == null) {
            this.firstWaiter = var2;
         } else {
            var1.nextWaiter = var2;
         }

         this.lastWaiter = var2;
         return var2;
      }

      private void doSignal(AbstractQueuedLongSynchronizer.Node var1) {
         do {
            if ((this.firstWaiter = var1.nextWaiter) == null) {
               this.lastWaiter = null;
            }

            var1.nextWaiter = null;
         } while(!AbstractQueuedLongSynchronizer.this.transferForSignal(var1) && (var1 = this.firstWaiter) != null);

      }

      private void doSignalAll(AbstractQueuedLongSynchronizer.Node var1) {
         this.lastWaiter = this.firstWaiter = null;

         AbstractQueuedLongSynchronizer.Node var2;
         do {
            var2 = var1.nextWaiter;
            var1.nextWaiter = null;
            AbstractQueuedLongSynchronizer.this.transferForSignal(var1);
            var1 = var2;
         } while(var2 != null);

      }

      private void unlinkCancelledWaiters() {
         AbstractQueuedLongSynchronizer.Node var1 = this.firstWaiter;

         AbstractQueuedLongSynchronizer.Node var3;
         for(AbstractQueuedLongSynchronizer.Node var2 = null; var1 != null; var1 = var3) {
            var3 = var1.nextWaiter;
            if (var1.waitStatus != -2) {
               var1.nextWaiter = null;
               if (var2 == null) {
                  this.firstWaiter = var3;
               } else {
                  var2.nextWaiter = var3;
               }

               if (var3 == null) {
                  this.lastWaiter = var2;
               }
            } else {
               var2 = var1;
            }
         }

      }

      public final void signal() {
         if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            AbstractQueuedLongSynchronizer.Node var1 = this.firstWaiter;
            if (var1 != null) {
               this.doSignal(var1);
            }

         }
      }

      public final void signalAll() {
         if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            AbstractQueuedLongSynchronizer.Node var1 = this.firstWaiter;
            if (var1 != null) {
               this.doSignalAll(var1);
            }

         }
      }

      public final void awaitUninterruptibly() {
         AbstractQueuedLongSynchronizer.Node var1 = this.addConditionWaiter();
         long var2 = AbstractQueuedLongSynchronizer.this.fullyRelease(var1);
         boolean var4 = false;

         while(!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(var1)) {
            LockSupport.park(this);
            if (Thread.interrupted()) {
               var4 = true;
            }
         }

         if (AbstractQueuedLongSynchronizer.this.acquireQueued(var1, var2) || var4) {
            AbstractQueuedLongSynchronizer.selfInterrupt();
         }

      }

      private int checkInterruptWhileWaiting(AbstractQueuedLongSynchronizer.Node var1) {
         return Thread.interrupted() ? (AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(var1) ? -1 : 1) : 0;
      }

      private void reportInterruptAfterWait(int var1) throws InterruptedException {
         if (var1 == -1) {
            throw new InterruptedException();
         } else {
            if (var1 == 1) {
               AbstractQueuedLongSynchronizer.selfInterrupt();
            }

         }
      }

      public final void await() throws InterruptedException {
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedLongSynchronizer.Node var1 = this.addConditionWaiter();
            long var2 = AbstractQueuedLongSynchronizer.this.fullyRelease(var1);
            int var4 = 0;

            while(!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(var1)) {
               LockSupport.park(this);
               if ((var4 = this.checkInterruptWhileWaiting(var1)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedLongSynchronizer.this.acquireQueued(var1, var2) && var4 != -1) {
               var4 = 1;
            }

            if (var1.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var4 != 0) {
               this.reportInterruptAfterWait(var4);
            }

         }
      }

      public final long awaitNanos(long var1) throws InterruptedException {
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedLongSynchronizer.Node var3 = this.addConditionWaiter();
            long var4 = AbstractQueuedLongSynchronizer.this.fullyRelease(var3);
            long var6 = System.nanoTime() + var1;

            int var8;
            for(var8 = 0; !AbstractQueuedLongSynchronizer.this.isOnSyncQueue(var3); var1 = var6 - System.nanoTime()) {
               if (var1 <= 0L) {
                  AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(var3);
                  break;
               }

               if (var1 >= 1000L) {
                  LockSupport.parkNanos(this, var1);
               }

               if ((var8 = this.checkInterruptWhileWaiting(var3)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedLongSynchronizer.this.acquireQueued(var3, var4) && var8 != -1) {
               var8 = 1;
            }

            if (var3.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var8 != 0) {
               this.reportInterruptAfterWait(var8);
            }

            return var6 - System.nanoTime();
         }
      }

      public final boolean awaitUntil(Date var1) throws InterruptedException {
         long var2 = var1.getTime();
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedLongSynchronizer.Node var4 = this.addConditionWaiter();
            long var5 = AbstractQueuedLongSynchronizer.this.fullyRelease(var4);
            boolean var7 = false;
            int var8 = 0;

            while(!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(var4)) {
               if (System.currentTimeMillis() > var2) {
                  var7 = AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(var4);
                  break;
               }

               LockSupport.parkUntil(this, var2);
               if ((var8 = this.checkInterruptWhileWaiting(var4)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedLongSynchronizer.this.acquireQueued(var4, var5) && var8 != -1) {
               var8 = 1;
            }

            if (var4.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var8 != 0) {
               this.reportInterruptAfterWait(var8);
            }

            return !var7;
         }
      }

      public final boolean await(long var1, TimeUnit var3) throws InterruptedException {
         long var4 = var3.toNanos(var1);
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedLongSynchronizer.Node var6 = this.addConditionWaiter();
            long var7 = AbstractQueuedLongSynchronizer.this.fullyRelease(var6);
            long var9 = System.nanoTime() + var4;
            boolean var11 = false;

            int var12;
            for(var12 = 0; !AbstractQueuedLongSynchronizer.this.isOnSyncQueue(var6); var4 = var9 - System.nanoTime()) {
               if (var4 <= 0L) {
                  var11 = AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(var6);
                  break;
               }

               if (var4 >= 1000L) {
                  LockSupport.parkNanos(this, var4);
               }

               if ((var12 = this.checkInterruptWhileWaiting(var6)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedLongSynchronizer.this.acquireQueued(var6, var7) && var12 != -1) {
               var12 = 1;
            }

            if (var6.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var12 != 0) {
               this.reportInterruptAfterWait(var12);
            }

            return !var11;
         }
      }

      final boolean isOwnedBy(AbstractQueuedLongSynchronizer var1) {
         return var1 == AbstractQueuedLongSynchronizer.this;
      }

      protected final boolean hasWaiters() {
         if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            for(AbstractQueuedLongSynchronizer.Node var1 = this.firstWaiter; var1 != null; var1 = var1.nextWaiter) {
               if (var1.waitStatus == -2) {
                  return true;
               }
            }

            return false;
         }
      }

      protected final int getWaitQueueLength() {
         if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            int var1 = 0;

            for(AbstractQueuedLongSynchronizer.Node var2 = this.firstWaiter; var2 != null; var2 = var2.nextWaiter) {
               if (var2.waitStatus == -2) {
                  ++var1;
               }
            }

            return var1;
         }
      }

      protected final Collection<Thread> getWaitingThreads() {
         if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            ArrayList var1 = new ArrayList();

            for(AbstractQueuedLongSynchronizer.Node var2 = this.firstWaiter; var2 != null; var2 = var2.nextWaiter) {
               if (var2.waitStatus == -2) {
                  Thread var3 = var2.thread;
                  if (var3 != null) {
                     var1.add(var3);
                  }
               }
            }

            return var1;
         }
      }
   }

   static final class Node {
      static final AbstractQueuedLongSynchronizer.Node SHARED = new AbstractQueuedLongSynchronizer.Node();
      static final AbstractQueuedLongSynchronizer.Node EXCLUSIVE = null;
      static final int CANCELLED = 1;
      static final int SIGNAL = -1;
      static final int CONDITION = -2;
      static final int PROPAGATE = -3;
      volatile int waitStatus;
      volatile AbstractQueuedLongSynchronizer.Node prev;
      volatile AbstractQueuedLongSynchronizer.Node next;
      volatile Thread thread;
      AbstractQueuedLongSynchronizer.Node nextWaiter;

      final boolean isShared() {
         return this.nextWaiter == SHARED;
      }

      final AbstractQueuedLongSynchronizer.Node predecessor() throws NullPointerException {
         AbstractQueuedLongSynchronizer.Node var1 = this.prev;
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return var1;
         }
      }

      Node() {
      }

      Node(Thread var1, AbstractQueuedLongSynchronizer.Node var2) {
         this.nextWaiter = var2;
         this.thread = var1;
      }

      Node(Thread var1, int var2) {
         this.waitStatus = var2;
         this.thread = var1;
      }
   }
}
