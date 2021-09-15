package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ReentrantLock implements Lock, Serializable {
   private static final long serialVersionUID = 7373984872572414699L;
   private final ReentrantLock.Sync sync;

   public ReentrantLock() {
      this.sync = new ReentrantLock.NonfairSync();
   }

   public ReentrantLock(boolean var1) {
      this.sync = (ReentrantLock.Sync)(var1 ? new ReentrantLock.FairSync() : new ReentrantLock.NonfairSync());
   }

   public void lock() {
      this.sync.lock();
   }

   public void lockInterruptibly() throws InterruptedException {
      this.sync.acquireInterruptibly(1);
   }

   public boolean tryLock() {
      return this.sync.nonfairTryAcquire(1);
   }

   public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
      return this.sync.tryAcquireNanos(1, var3.toNanos(var1));
   }

   public void unlock() {
      this.sync.release(1);
   }

   public Condition newCondition() {
      return this.sync.newCondition();
   }

   public int getHoldCount() {
      return this.sync.getHoldCount();
   }

   public boolean isHeldByCurrentThread() {
      return this.sync.isHeldExclusively();
   }

   public boolean isLocked() {
      return this.sync.isLocked();
   }

   public final boolean isFair() {
      return this.sync instanceof ReentrantLock.FairSync;
   }

   protected Thread getOwner() {
      return this.sync.getOwner();
   }

   public final boolean hasQueuedThreads() {
      return this.sync.hasQueuedThreads();
   }

   public final boolean hasQueuedThread(Thread var1) {
      return this.sync.isQueued(var1);
   }

   public final int getQueueLength() {
      return this.sync.getQueueLength();
   }

   protected Collection<Thread> getQueuedThreads() {
      return this.sync.getQueuedThreads();
   }

   public boolean hasWaiters(Condition var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof AbstractQueuedSynchronizer.ConditionObject)) {
         throw new IllegalArgumentException("not owner");
      } else {
         return this.sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)var1);
      }
   }

   public int getWaitQueueLength(Condition var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof AbstractQueuedSynchronizer.ConditionObject)) {
         throw new IllegalArgumentException("not owner");
      } else {
         return this.sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)var1);
      }
   }

   protected Collection<Thread> getWaitingThreads(Condition var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof AbstractQueuedSynchronizer.ConditionObject)) {
         throw new IllegalArgumentException("not owner");
      } else {
         return this.sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)var1);
      }
   }

   public String toString() {
      Thread var1 = this.sync.getOwner();
      return super.toString() + (var1 == null ? "[Unlocked]" : "[Locked by thread " + var1.getName() + "]");
   }

   static final class FairSync extends ReentrantLock.Sync {
      private static final long serialVersionUID = -3000897897090466540L;

      final void lock() {
         this.acquire(1);
      }

      protected final boolean tryAcquire(int var1) {
         Thread var2 = Thread.currentThread();
         int var3 = this.getState();
         if (var3 == 0) {
            if (!this.hasQueuedPredecessors() && this.compareAndSetState(0, var1)) {
               this.setExclusiveOwnerThread(var2);
               return true;
            }
         } else if (var2 == this.getExclusiveOwnerThread()) {
            int var4 = var3 + var1;
            if (var4 < 0) {
               throw new Error("Maximum lock count exceeded");
            }

            this.setState(var4);
            return true;
         }

         return false;
      }
   }

   static final class NonfairSync extends ReentrantLock.Sync {
      private static final long serialVersionUID = 7316153563782823691L;

      final void lock() {
         if (this.compareAndSetState(0, 1)) {
            this.setExclusiveOwnerThread(Thread.currentThread());
         } else {
            this.acquire(1);
         }

      }

      protected final boolean tryAcquire(int var1) {
         return this.nonfairTryAcquire(var1);
      }
   }

   abstract static class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = -5179523762034025860L;

      abstract void lock();

      final boolean nonfairTryAcquire(int var1) {
         Thread var2 = Thread.currentThread();
         int var3 = this.getState();
         if (var3 == 0) {
            if (this.compareAndSetState(0, var1)) {
               this.setExclusiveOwnerThread(var2);
               return true;
            }
         } else if (var2 == this.getExclusiveOwnerThread()) {
            int var4 = var3 + var1;
            if (var4 < 0) {
               throw new Error("Maximum lock count exceeded");
            }

            this.setState(var4);
            return true;
         }

         return false;
      }

      protected final boolean tryRelease(int var1) {
         int var2 = this.getState() - var1;
         if (Thread.currentThread() != this.getExclusiveOwnerThread()) {
            throw new IllegalMonitorStateException();
         } else {
            boolean var3 = false;
            if (var2 == 0) {
               var3 = true;
               this.setExclusiveOwnerThread((Thread)null);
            }

            this.setState(var2);
            return var3;
         }
      }

      protected final boolean isHeldExclusively() {
         return this.getExclusiveOwnerThread() == Thread.currentThread();
      }

      final AbstractQueuedSynchronizer.ConditionObject newCondition() {
         return new AbstractQueuedSynchronizer.ConditionObject();
      }

      final Thread getOwner() {
         return this.getState() == 0 ? null : this.getExclusiveOwnerThread();
      }

      final int getHoldCount() {
         return this.isHeldExclusively() ? this.getState() : 0;
      }

      final boolean isLocked() {
         return this.getState() != 0;
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.setState(0);
      }
   }
}
