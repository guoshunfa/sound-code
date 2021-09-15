package java.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class Semaphore implements Serializable {
   private static final long serialVersionUID = -3222578661600680210L;
   private final Semaphore.Sync sync;

   public Semaphore(int var1) {
      this.sync = new Semaphore.NonfairSync(var1);
   }

   public Semaphore(int var1, boolean var2) {
      this.sync = (Semaphore.Sync)(var2 ? new Semaphore.FairSync(var1) : new Semaphore.NonfairSync(var1));
   }

   public void acquire() throws InterruptedException {
      this.sync.acquireSharedInterruptibly(1);
   }

   public void acquireUninterruptibly() {
      this.sync.acquireShared(1);
   }

   public boolean tryAcquire() {
      return this.sync.nonfairTryAcquireShared(1) >= 0;
   }

   public boolean tryAcquire(long var1, TimeUnit var3) throws InterruptedException {
      return this.sync.tryAcquireSharedNanos(1, var3.toNanos(var1));
   }

   public void release() {
      this.sync.releaseShared(1);
   }

   public void acquire(int var1) throws InterruptedException {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.sync.acquireSharedInterruptibly(var1);
      }
   }

   public void acquireUninterruptibly(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.sync.acquireShared(var1);
      }
   }

   public boolean tryAcquire(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         return this.sync.nonfairTryAcquireShared(var1) >= 0;
      }
   }

   public boolean tryAcquire(int var1, long var2, TimeUnit var4) throws InterruptedException {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         return this.sync.tryAcquireSharedNanos(var1, var4.toNanos(var2));
      }
   }

   public void release(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.sync.releaseShared(var1);
      }
   }

   public int availablePermits() {
      return this.sync.getPermits();
   }

   public int drainPermits() {
      return this.sync.drainPermits();
   }

   protected void reducePermits(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         this.sync.reducePermits(var1);
      }
   }

   public boolean isFair() {
      return this.sync instanceof Semaphore.FairSync;
   }

   public final boolean hasQueuedThreads() {
      return this.sync.hasQueuedThreads();
   }

   public final int getQueueLength() {
      return this.sync.getQueueLength();
   }

   protected Collection<Thread> getQueuedThreads() {
      return this.sync.getQueuedThreads();
   }

   public String toString() {
      return super.toString() + "[Permits = " + this.sync.getPermits() + "]";
   }

   static final class FairSync extends Semaphore.Sync {
      private static final long serialVersionUID = 2014338818796000944L;

      FairSync(int var1) {
         super(var1);
      }

      protected int tryAcquireShared(int var1) {
         int var2;
         int var3;
         do {
            if (this.hasQueuedPredecessors()) {
               return -1;
            }

            var2 = this.getState();
            var3 = var2 - var1;
         } while(var3 >= 0 && !this.compareAndSetState(var2, var3));

         return var3;
      }
   }

   static final class NonfairSync extends Semaphore.Sync {
      private static final long serialVersionUID = -2694183684443567898L;

      NonfairSync(int var1) {
         super(var1);
      }

      protected int tryAcquireShared(int var1) {
         return this.nonfairTryAcquireShared(var1);
      }
   }

   abstract static class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = 1192457210091910933L;

      Sync(int var1) {
         this.setState(var1);
      }

      final int getPermits() {
         return this.getState();
      }

      final int nonfairTryAcquireShared(int var1) {
         int var2;
         int var3;
         do {
            var2 = this.getState();
            var3 = var2 - var1;
         } while(var3 >= 0 && !this.compareAndSetState(var2, var3));

         return var3;
      }

      protected final boolean tryReleaseShared(int var1) {
         int var2;
         int var3;
         do {
            var2 = this.getState();
            var3 = var2 + var1;
            if (var3 < var2) {
               throw new Error("Maximum permit count exceeded");
            }
         } while(!this.compareAndSetState(var2, var3));

         return true;
      }

      final void reducePermits(int var1) {
         int var2;
         int var3;
         do {
            var2 = this.getState();
            var3 = var2 - var1;
            if (var3 > var2) {
               throw new Error("Permit count underflow");
            }
         } while(!this.compareAndSetState(var2, var3));

      }

      final int drainPermits() {
         int var1;
         do {
            var1 = this.getState();
         } while(var1 != 0 && !this.compareAndSetState(var1, 0));

         return var1;
      }
   }
}
