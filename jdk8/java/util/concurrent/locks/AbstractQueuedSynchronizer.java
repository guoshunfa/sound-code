package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements Serializable {
   private static final long serialVersionUID = 7373984972572414691L;
   private transient volatile AbstractQueuedSynchronizer.Node head;
   private transient volatile AbstractQueuedSynchronizer.Node tail;
   private volatile int state;
   static final long spinForTimeoutThreshold = 1000L;
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final long stateOffset;
   private static final long headOffset;
   private static final long tailOffset;
   private static final long waitStatusOffset;
   private static final long nextOffset;

   protected AbstractQueuedSynchronizer() {
   }

   protected final int getState() {
      return this.state;
   }

   protected final void setState(int var1) {
      this.state = var1;
   }

   protected final boolean compareAndSetState(int var1, int var2) {
      return unsafe.compareAndSwapInt(this, stateOffset, var1, var2);
   }

   private AbstractQueuedSynchronizer.Node enq(AbstractQueuedSynchronizer.Node var1) {
      while(true) {
         AbstractQueuedSynchronizer.Node var2 = this.tail;
         if (var2 == null) {
            if (this.compareAndSetHead(new AbstractQueuedSynchronizer.Node())) {
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

   private AbstractQueuedSynchronizer.Node addWaiter(AbstractQueuedSynchronizer.Node var1) {
      AbstractQueuedSynchronizer.Node var2 = new AbstractQueuedSynchronizer.Node(Thread.currentThread(), var1);
      AbstractQueuedSynchronizer.Node var3 = this.tail;
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

   private void setHead(AbstractQueuedSynchronizer.Node var1) {
      this.head = var1;
      var1.thread = null;
      var1.prev = null;
   }

   private void unparkSuccessor(AbstractQueuedSynchronizer.Node var1) {
      int var2 = var1.waitStatus;
      if (var2 < 0) {
         compareAndSetWaitStatus(var1, var2, 0);
      }

      AbstractQueuedSynchronizer.Node var3 = var1.next;
      if (var3 == null || var3.waitStatus > 0) {
         var3 = null;

         for(AbstractQueuedSynchronizer.Node var4 = this.tail; var4 != null && var4 != var1; var4 = var4.prev) {
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
         AbstractQueuedSynchronizer.Node var1 = this.head;
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

   private void setHeadAndPropagate(AbstractQueuedSynchronizer.Node var1, int var2) {
      AbstractQueuedSynchronizer.Node var3 = this.head;
      this.setHead(var1);
      if (var2 > 0 || var3 == null || var3.waitStatus < 0 || (var3 = this.head) == null || var3.waitStatus < 0) {
         AbstractQueuedSynchronizer.Node var4 = var1.next;
         if (var4 == null || var4.isShared()) {
            this.doReleaseShared();
         }
      }

   }

   private void cancelAcquire(AbstractQueuedSynchronizer.Node var1) {
      if (var1 != null) {
         var1.thread = null;

         AbstractQueuedSynchronizer.Node var2;
         for(var2 = var1.prev; var2.waitStatus > 0; var1.prev = var2 = var2.prev) {
         }

         AbstractQueuedSynchronizer.Node var3 = var2.next;
         var1.waitStatus = 1;
         if (var1 == this.tail && this.compareAndSetTail(var1, var2)) {
            compareAndSetNext(var2, var3, (AbstractQueuedSynchronizer.Node)null);
         } else {
            int var4;
            if (var2 != this.head && ((var4 = var2.waitStatus) == -1 || var4 <= 0 && compareAndSetWaitStatus(var2, var4, -1)) && var2.thread != null) {
               AbstractQueuedSynchronizer.Node var5 = var1.next;
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

   private static boolean shouldParkAfterFailedAcquire(AbstractQueuedSynchronizer.Node var0, AbstractQueuedSynchronizer.Node var1) {
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

   final boolean acquireQueued(AbstractQueuedSynchronizer.Node var1, int var2) {
      boolean var3 = true;

      try {
         boolean var4 = false;

         while(true) {
            AbstractQueuedSynchronizer.Node var5 = var1.predecessor();
            if (var5 == this.head && this.tryAcquire(var2)) {
               this.setHead(var1);
               var5.next = null;
               var3 = false;
               boolean var6 = var4;
               return var6;
            }

            if (shouldParkAfterFailedAcquire(var5, var1) && this.parkAndCheckInterrupt()) {
               var4 = true;
            }
         }
      } finally {
         if (var3) {
            this.cancelAcquire(var1);
         }

      }
   }

   private void doAcquireInterruptibly(int var1) throws InterruptedException {
      AbstractQueuedSynchronizer.Node var2 = this.addWaiter(AbstractQueuedSynchronizer.Node.EXCLUSIVE);
      boolean var3 = true;

      try {
         AbstractQueuedSynchronizer.Node var4;
         do {
            var4 = var2.predecessor();
            if (var4 == this.head && this.tryAcquire(var1)) {
               this.setHead(var2);
               var4.next = null;
               var3 = false;
               return;
            }
         } while(!shouldParkAfterFailedAcquire(var4, var2) || !this.parkAndCheckInterrupt());

         throw new InterruptedException();
      } finally {
         if (var3) {
            this.cancelAcquire(var2);
         }

      }
   }

   private boolean doAcquireNanos(int var1, long var2) throws InterruptedException {
      if (var2 <= 0L) {
         return false;
      } else {
         long var4 = System.nanoTime() + var2;
         AbstractQueuedSynchronizer.Node var6 = this.addWaiter(AbstractQueuedSynchronizer.Node.EXCLUSIVE);
         boolean var7 = true;

         while(true) {
            boolean var9;
            try {
               AbstractQueuedSynchronizer.Node var8 = var6.predecessor();
               if (var8 == this.head && this.tryAcquire(var1)) {
                  this.setHead(var6);
                  var8.next = null;
                  var7 = false;
                  var9 = true;
                  return var9;
               }

               var2 = var4 - System.nanoTime();
               if (var2 > 0L) {
                  if (shouldParkAfterFailedAcquire(var8, var6) && var2 > 1000L) {
                     LockSupport.parkNanos(this, var2);
                  }

                  if (Thread.interrupted()) {
                     throw new InterruptedException();
                  }
                  continue;
               }

               var9 = false;
            } finally {
               if (var7) {
                  this.cancelAcquire(var6);
               }

            }

            return var9;
         }
      }
   }

   private void doAcquireShared(int var1) {
      AbstractQueuedSynchronizer.Node var2 = this.addWaiter(AbstractQueuedSynchronizer.Node.SHARED);
      boolean var3 = true;

      try {
         boolean var4 = false;

         while(true) {
            AbstractQueuedSynchronizer.Node var5 = var2.predecessor();
            if (var5 == this.head) {
               int var6 = this.tryAcquireShared(var1);
               if (var6 >= 0) {
                  this.setHeadAndPropagate(var2, var6);
                  var5.next = null;
                  if (var4) {
                     selfInterrupt();
                  }

                  var3 = false;
                  return;
               }
            }

            if (shouldParkAfterFailedAcquire(var5, var2) && this.parkAndCheckInterrupt()) {
               var4 = true;
            }
         }
      } finally {
         if (var3) {
            this.cancelAcquire(var2);
         }

      }
   }

   private void doAcquireSharedInterruptibly(int var1) throws InterruptedException {
      AbstractQueuedSynchronizer.Node var2 = this.addWaiter(AbstractQueuedSynchronizer.Node.SHARED);
      boolean var3 = true;

      try {
         AbstractQueuedSynchronizer.Node var4;
         do {
            var4 = var2.predecessor();
            if (var4 == this.head) {
               int var5 = this.tryAcquireShared(var1);
               if (var5 >= 0) {
                  this.setHeadAndPropagate(var2, var5);
                  var4.next = null;
                  var3 = false;
                  return;
               }
            }
         } while(!shouldParkAfterFailedAcquire(var4, var2) || !this.parkAndCheckInterrupt());

         throw new InterruptedException();
      } finally {
         if (var3) {
            this.cancelAcquire(var2);
         }

      }
   }

   private boolean doAcquireSharedNanos(int var1, long var2) throws InterruptedException {
      if (var2 <= 0L) {
         return false;
      } else {
         long var4 = System.nanoTime() + var2;
         AbstractQueuedSynchronizer.Node var6 = this.addWaiter(AbstractQueuedSynchronizer.Node.SHARED);
         boolean var7 = true;

         while(true) {
            boolean var14;
            try {
               AbstractQueuedSynchronizer.Node var8 = var6.predecessor();
               if (var8 == this.head) {
                  int var9 = this.tryAcquireShared(var1);
                  if (var9 >= 0) {
                     this.setHeadAndPropagate(var6, var9);
                     var8.next = null;
                     var7 = false;
                     boolean var10 = true;
                     return var10;
                  }
               }

               var2 = var4 - System.nanoTime();
               if (var2 > 0L) {
                  if (shouldParkAfterFailedAcquire(var8, var6) && var2 > 1000L) {
                     LockSupport.parkNanos(this, var2);
                  }

                  if (Thread.interrupted()) {
                     throw new InterruptedException();
                  }
                  continue;
               }

               var14 = false;
            } finally {
               if (var7) {
                  this.cancelAcquire(var6);
               }

            }

            return var14;
         }
      }
   }

   protected boolean tryAcquire(int var1) {
      throw new UnsupportedOperationException();
   }

   protected boolean tryRelease(int var1) {
      throw new UnsupportedOperationException();
   }

   protected int tryAcquireShared(int var1) {
      throw new UnsupportedOperationException();
   }

   protected boolean tryReleaseShared(int var1) {
      throw new UnsupportedOperationException();
   }

   protected boolean isHeldExclusively() {
      throw new UnsupportedOperationException();
   }

   public final void acquire(int var1) {
      if (!this.tryAcquire(var1) && this.acquireQueued(this.addWaiter(AbstractQueuedSynchronizer.Node.EXCLUSIVE), var1)) {
         selfInterrupt();
      }

   }

   public final void acquireInterruptibly(int var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         if (!this.tryAcquire(var1)) {
            this.doAcquireInterruptibly(var1);
         }

      }
   }

   public final boolean tryAcquireNanos(int var1, long var2) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this.tryAcquire(var1) || this.doAcquireNanos(var1, var2);
      }
   }

   public final boolean release(int var1) {
      if (this.tryRelease(var1)) {
         AbstractQueuedSynchronizer.Node var2 = this.head;
         if (var2 != null && var2.waitStatus != 0) {
            this.unparkSuccessor(var2);
         }

         return true;
      } else {
         return false;
      }
   }

   public final void acquireShared(int var1) {
      if (this.tryAcquireShared(var1) < 0) {
         this.doAcquireShared(var1);
      }

   }

   public final void acquireSharedInterruptibly(int var1) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         if (this.tryAcquireShared(var1) < 0) {
            this.doAcquireSharedInterruptibly(var1);
         }

      }
   }

   public final boolean tryAcquireSharedNanos(int var1, long var2) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else {
         return this.tryAcquireShared(var1) >= 0 || this.doAcquireSharedNanos(var1, var2);
      }
   }

   public final boolean releaseShared(int var1) {
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
      AbstractQueuedSynchronizer.Node var1;
      AbstractQueuedSynchronizer.Node var2;
      Thread var3;
      if (((var1 = this.head) == null || (var2 = var1.next) == null || var2.prev != this.head || (var3 = var2.thread) == null) && ((var1 = this.head) == null || (var2 = var1.next) == null || var2.prev != this.head || (var3 = var2.thread) == null)) {
         AbstractQueuedSynchronizer.Node var4 = this.tail;

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
         for(AbstractQueuedSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
            if (var2.thread == var1) {
               return true;
            }
         }

         return false;
      }
   }

   final boolean apparentlyFirstQueuedIsExclusive() {
      AbstractQueuedSynchronizer.Node var1;
      AbstractQueuedSynchronizer.Node var2;
      return (var1 = this.head) != null && (var2 = var1.next) != null && !var2.isShared() && var2.thread != null;
   }

   public final boolean hasQueuedPredecessors() {
      AbstractQueuedSynchronizer.Node var1 = this.tail;
      AbstractQueuedSynchronizer.Node var2 = this.head;
      AbstractQueuedSynchronizer.Node var3;
      return var2 != var1 && ((var3 = var2.next) == null || var3.thread != Thread.currentThread());
   }

   public final int getQueueLength() {
      int var1 = 0;

      for(AbstractQueuedSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
         if (var2.thread != null) {
            ++var1;
         }
      }

      return var1;
   }

   public final Collection<Thread> getQueuedThreads() {
      ArrayList var1 = new ArrayList();

      for(AbstractQueuedSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
         Thread var3 = var2.thread;
         if (var3 != null) {
            var1.add(var3);
         }
      }

      return var1;
   }

   public final Collection<Thread> getExclusiveQueuedThreads() {
      ArrayList var1 = new ArrayList();

      for(AbstractQueuedSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
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

      for(AbstractQueuedSynchronizer.Node var2 = this.tail; var2 != null; var2 = var2.prev) {
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
      int var1 = this.getState();
      String var2 = this.hasQueuedThreads() ? "non" : "";
      return super.toString() + "[State = " + var1 + ", " + var2 + "empty queue]";
   }

   final boolean isOnSyncQueue(AbstractQueuedSynchronizer.Node var1) {
      if (var1.waitStatus != -2 && var1.prev != null) {
         return var1.next != null ? true : this.findNodeFromTail(var1);
      } else {
         return false;
      }
   }

   private boolean findNodeFromTail(AbstractQueuedSynchronizer.Node var1) {
      for(AbstractQueuedSynchronizer.Node var2 = this.tail; var2 != var1; var2 = var2.prev) {
         if (var2 == null) {
            return false;
         }
      }

      return true;
   }

   final boolean transferForSignal(AbstractQueuedSynchronizer.Node var1) {
      if (!compareAndSetWaitStatus(var1, -2, 0)) {
         return false;
      } else {
         AbstractQueuedSynchronizer.Node var2 = this.enq(var1);
         int var3 = var2.waitStatus;
         if (var3 > 0 || !compareAndSetWaitStatus(var2, var3, -1)) {
            LockSupport.unpark(var1.thread);
         }

         return true;
      }
   }

   final boolean transferAfterCancelledWait(AbstractQueuedSynchronizer.Node var1) {
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

   final int fullyRelease(AbstractQueuedSynchronizer.Node var1) {
      boolean var2 = true;

      int var4;
      try {
         int var3 = this.getState();
         if (!this.release(var3)) {
            throw new IllegalMonitorStateException();
         }

         var2 = false;
         var4 = var3;
      } finally {
         if (var2) {
            var1.waitStatus = 1;
         }

      }

      return var4;
   }

   public final boolean owns(AbstractQueuedSynchronizer.ConditionObject var1) {
      return var1.isOwnedBy(this);
   }

   public final boolean hasWaiters(AbstractQueuedSynchronizer.ConditionObject var1) {
      if (!this.owns(var1)) {
         throw new IllegalArgumentException("Not owner");
      } else {
         return var1.hasWaiters();
      }
   }

   public final int getWaitQueueLength(AbstractQueuedSynchronizer.ConditionObject var1) {
      if (!this.owns(var1)) {
         throw new IllegalArgumentException("Not owner");
      } else {
         return var1.getWaitQueueLength();
      }
   }

   public final Collection<Thread> getWaitingThreads(AbstractQueuedSynchronizer.ConditionObject var1) {
      if (!this.owns(var1)) {
         throw new IllegalArgumentException("Not owner");
      } else {
         return var1.getWaitingThreads();
      }
   }

   private final boolean compareAndSetHead(AbstractQueuedSynchronizer.Node var1) {
      return unsafe.compareAndSwapObject(this, headOffset, (Object)null, var1);
   }

   private final boolean compareAndSetTail(AbstractQueuedSynchronizer.Node var1, AbstractQueuedSynchronizer.Node var2) {
      return unsafe.compareAndSwapObject(this, tailOffset, var1, var2);
   }

   private static final boolean compareAndSetWaitStatus(AbstractQueuedSynchronizer.Node var0, int var1, int var2) {
      return unsafe.compareAndSwapInt(var0, waitStatusOffset, var1, var2);
   }

   private static final boolean compareAndSetNext(AbstractQueuedSynchronizer.Node var0, AbstractQueuedSynchronizer.Node var1, AbstractQueuedSynchronizer.Node var2) {
      return unsafe.compareAndSwapObject(var0, nextOffset, var1, var2);
   }

   static {
      try {
         stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
         headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
         tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
         waitStatusOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.Node.class.getDeclaredField("waitStatus"));
         nextOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.Node.class.getDeclaredField("next"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   public class ConditionObject implements Condition, Serializable {
      private static final long serialVersionUID = 1173984872572414699L;
      private transient AbstractQueuedSynchronizer.Node firstWaiter;
      private transient AbstractQueuedSynchronizer.Node lastWaiter;
      private static final int REINTERRUPT = 1;
      private static final int THROW_IE = -1;

      private AbstractQueuedSynchronizer.Node addConditionWaiter() {
         AbstractQueuedSynchronizer.Node var1 = this.lastWaiter;
         if (var1 != null && var1.waitStatus != -2) {
            this.unlinkCancelledWaiters();
            var1 = this.lastWaiter;
         }

         AbstractQueuedSynchronizer.Node var2 = new AbstractQueuedSynchronizer.Node(Thread.currentThread(), -2);
         if (var1 == null) {
            this.firstWaiter = var2;
         } else {
            var1.nextWaiter = var2;
         }

         this.lastWaiter = var2;
         return var2;
      }

      private void doSignal(AbstractQueuedSynchronizer.Node var1) {
         do {
            if ((this.firstWaiter = var1.nextWaiter) == null) {
               this.lastWaiter = null;
            }

            var1.nextWaiter = null;
         } while(!AbstractQueuedSynchronizer.this.transferForSignal(var1) && (var1 = this.firstWaiter) != null);

      }

      private void doSignalAll(AbstractQueuedSynchronizer.Node var1) {
         this.lastWaiter = this.firstWaiter = null;

         AbstractQueuedSynchronizer.Node var2;
         do {
            var2 = var1.nextWaiter;
            var1.nextWaiter = null;
            AbstractQueuedSynchronizer.this.transferForSignal(var1);
            var1 = var2;
         } while(var2 != null);

      }

      private void unlinkCancelledWaiters() {
         AbstractQueuedSynchronizer.Node var1 = this.firstWaiter;

         AbstractQueuedSynchronizer.Node var3;
         for(AbstractQueuedSynchronizer.Node var2 = null; var1 != null; var1 = var3) {
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
         if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            AbstractQueuedSynchronizer.Node var1 = this.firstWaiter;
            if (var1 != null) {
               this.doSignal(var1);
            }

         }
      }

      public final void signalAll() {
         if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            AbstractQueuedSynchronizer.Node var1 = this.firstWaiter;
            if (var1 != null) {
               this.doSignalAll(var1);
            }

         }
      }

      public final void awaitUninterruptibly() {
         AbstractQueuedSynchronizer.Node var1 = this.addConditionWaiter();
         int var2 = AbstractQueuedSynchronizer.this.fullyRelease(var1);
         boolean var3 = false;

         while(!AbstractQueuedSynchronizer.this.isOnSyncQueue(var1)) {
            LockSupport.park(this);
            if (Thread.interrupted()) {
               var3 = true;
            }
         }

         if (AbstractQueuedSynchronizer.this.acquireQueued(var1, var2) || var3) {
            AbstractQueuedSynchronizer.selfInterrupt();
         }

      }

      private int checkInterruptWhileWaiting(AbstractQueuedSynchronizer.Node var1) {
         return Thread.interrupted() ? (AbstractQueuedSynchronizer.this.transferAfterCancelledWait(var1) ? -1 : 1) : 0;
      }

      private void reportInterruptAfterWait(int var1) throws InterruptedException {
         if (var1 == -1) {
            throw new InterruptedException();
         } else {
            if (var1 == 1) {
               AbstractQueuedSynchronizer.selfInterrupt();
            }

         }
      }

      public final void await() throws InterruptedException {
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedSynchronizer.Node var1 = this.addConditionWaiter();
            int var2 = AbstractQueuedSynchronizer.this.fullyRelease(var1);
            int var3 = 0;

            while(!AbstractQueuedSynchronizer.this.isOnSyncQueue(var1)) {
               LockSupport.park(this);
               if ((var3 = this.checkInterruptWhileWaiting(var1)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedSynchronizer.this.acquireQueued(var1, var2) && var3 != -1) {
               var3 = 1;
            }

            if (var1.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var3 != 0) {
               this.reportInterruptAfterWait(var3);
            }

         }
      }

      public final long awaitNanos(long var1) throws InterruptedException {
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedSynchronizer.Node var3 = this.addConditionWaiter();
            int var4 = AbstractQueuedSynchronizer.this.fullyRelease(var3);
            long var5 = System.nanoTime() + var1;

            int var7;
            for(var7 = 0; !AbstractQueuedSynchronizer.this.isOnSyncQueue(var3); var1 = var5 - System.nanoTime()) {
               if (var1 <= 0L) {
                  AbstractQueuedSynchronizer.this.transferAfterCancelledWait(var3);
                  break;
               }

               if (var1 >= 1000L) {
                  LockSupport.parkNanos(this, var1);
               }

               if ((var7 = this.checkInterruptWhileWaiting(var3)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedSynchronizer.this.acquireQueued(var3, var4) && var7 != -1) {
               var7 = 1;
            }

            if (var3.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var7 != 0) {
               this.reportInterruptAfterWait(var7);
            }

            return var5 - System.nanoTime();
         }
      }

      public final boolean awaitUntil(Date var1) throws InterruptedException {
         long var2 = var1.getTime();
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedSynchronizer.Node var4 = this.addConditionWaiter();
            int var5 = AbstractQueuedSynchronizer.this.fullyRelease(var4);
            boolean var6 = false;
            int var7 = 0;

            while(!AbstractQueuedSynchronizer.this.isOnSyncQueue(var4)) {
               if (System.currentTimeMillis() > var2) {
                  var6 = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(var4);
                  break;
               }

               LockSupport.parkUntil(this, var2);
               if ((var7 = this.checkInterruptWhileWaiting(var4)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedSynchronizer.this.acquireQueued(var4, var5) && var7 != -1) {
               var7 = 1;
            }

            if (var4.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var7 != 0) {
               this.reportInterruptAfterWait(var7);
            }

            return !var6;
         }
      }

      public final boolean await(long var1, TimeUnit var3) throws InterruptedException {
         long var4 = var3.toNanos(var1);
         if (Thread.interrupted()) {
            throw new InterruptedException();
         } else {
            AbstractQueuedSynchronizer.Node var6 = this.addConditionWaiter();
            int var7 = AbstractQueuedSynchronizer.this.fullyRelease(var6);
            long var8 = System.nanoTime() + var4;
            boolean var10 = false;

            int var11;
            for(var11 = 0; !AbstractQueuedSynchronizer.this.isOnSyncQueue(var6); var4 = var8 - System.nanoTime()) {
               if (var4 <= 0L) {
                  var10 = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(var6);
                  break;
               }

               if (var4 >= 1000L) {
                  LockSupport.parkNanos(this, var4);
               }

               if ((var11 = this.checkInterruptWhileWaiting(var6)) != 0) {
                  break;
               }
            }

            if (AbstractQueuedSynchronizer.this.acquireQueued(var6, var7) && var11 != -1) {
               var11 = 1;
            }

            if (var6.nextWaiter != null) {
               this.unlinkCancelledWaiters();
            }

            if (var11 != 0) {
               this.reportInterruptAfterWait(var11);
            }

            return !var10;
         }
      }

      final boolean isOwnedBy(AbstractQueuedSynchronizer var1) {
         return var1 == AbstractQueuedSynchronizer.this;
      }

      protected final boolean hasWaiters() {
         if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            for(AbstractQueuedSynchronizer.Node var1 = this.firstWaiter; var1 != null; var1 = var1.nextWaiter) {
               if (var1.waitStatus == -2) {
                  return true;
               }
            }

            return false;
         }
      }

      protected final int getWaitQueueLength() {
         if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            int var1 = 0;

            for(AbstractQueuedSynchronizer.Node var2 = this.firstWaiter; var2 != null; var2 = var2.nextWaiter) {
               if (var2.waitStatus == -2) {
                  ++var1;
               }
            }

            return var1;
         }
      }

      protected final Collection<Thread> getWaitingThreads() {
         if (!AbstractQueuedSynchronizer.this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            ArrayList var1 = new ArrayList();

            for(AbstractQueuedSynchronizer.Node var2 = this.firstWaiter; var2 != null; var2 = var2.nextWaiter) {
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
      static final AbstractQueuedSynchronizer.Node SHARED = new AbstractQueuedSynchronizer.Node();
      static final AbstractQueuedSynchronizer.Node EXCLUSIVE = null;
      static final int CANCELLED = 1;
      static final int SIGNAL = -1;
      static final int CONDITION = -2;
      static final int PROPAGATE = -3;
      volatile int waitStatus;
      volatile AbstractQueuedSynchronizer.Node prev;
      volatile AbstractQueuedSynchronizer.Node next;
      volatile Thread thread;
      AbstractQueuedSynchronizer.Node nextWaiter;

      final boolean isShared() {
         return this.nextWaiter == SHARED;
      }

      final AbstractQueuedSynchronizer.Node predecessor() throws NullPointerException {
         AbstractQueuedSynchronizer.Node var1 = this.prev;
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            return var1;
         }
      }

      Node() {
      }

      Node(Thread var1, AbstractQueuedSynchronizer.Node var2) {
         this.nextWaiter = var2;
         this.thread = var1;
      }

      Node(Thread var1, int var2) {
         this.waitStatus = var2;
         this.thread = var1;
      }
   }
}
