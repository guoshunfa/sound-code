package java.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class CountDownLatch {
   private final CountDownLatch.Sync sync;

   public CountDownLatch(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("count < 0");
      } else {
         this.sync = new CountDownLatch.Sync(var1);
      }
   }

   public void await() throws InterruptedException {
      this.sync.acquireSharedInterruptibly(1);
   }

   public boolean await(long var1, TimeUnit var3) throws InterruptedException {
      return this.sync.tryAcquireSharedNanos(1, var3.toNanos(var1));
   }

   public void countDown() {
      this.sync.releaseShared(1);
   }

   public long getCount() {
      return (long)this.sync.getCount();
   }

   public String toString() {
      return super.toString() + "[Count = " + this.sync.getCount() + "]";
   }

   private static final class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = 4982264981922014374L;

      Sync(int var1) {
         this.setState(var1);
      }

      int getCount() {
         return this.getState();
      }

      protected int tryAcquireShared(int var1) {
         return this.getState() == 0 ? 1 : -1;
      }

      protected boolean tryReleaseShared(int var1) {
         int var2;
         int var3;
         do {
            var2 = this.getState();
            if (var2 == 0) {
               return false;
            }

            var3 = var2 - 1;
         } while(!this.compareAndSetState(var2, var3));

         return var3 == 0;
      }
   }
}
