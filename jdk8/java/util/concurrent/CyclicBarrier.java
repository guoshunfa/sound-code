package java.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier {
   private final ReentrantLock lock;
   private final Condition trip;
   private final int parties;
   private final Runnable barrierCommand;
   private CyclicBarrier.Generation generation;
   private int count;

   private void nextGeneration() {
      this.trip.signalAll();
      this.count = this.parties;
      this.generation = new CyclicBarrier.Generation();
   }

   private void breakBarrier() {
      this.generation.broken = true;
      this.count = this.parties;
      this.trip.signalAll();
   }

   private int dowait(boolean var1, long var2) throws InterruptedException, BrokenBarrierException, TimeoutException {
      ReentrantLock var4 = this.lock;
      var4.lock();

      byte var9;
      try {
         CyclicBarrier.Generation var5 = this.generation;
         if (var5.broken) {
            throw new BrokenBarrierException();
         }

         if (Thread.interrupted()) {
            this.breakBarrier();
            throw new InterruptedException();
         }

         int var6 = --this.count;
         if (var6 != 0) {
            do {
               try {
                  if (!var1) {
                     this.trip.await();
                  } else if (var2 > 0L) {
                     var2 = this.trip.awaitNanos(var2);
                  }
               } catch (InterruptedException var19) {
                  if (var5 == this.generation && !var5.broken) {
                     this.breakBarrier();
                     throw var19;
                  }

                  Thread.currentThread().interrupt();
               }

               if (var5.broken) {
                  throw new BrokenBarrierException();
               }

               if (var5 != this.generation) {
                  int var21 = var6;
                  return var21;
               }
            } while(!var1 || var2 > 0L);

            this.breakBarrier();
            throw new TimeoutException();
         }

         boolean var7 = false;

         try {
            Runnable var8 = this.barrierCommand;
            if (var8 != null) {
               var8.run();
            }

            var7 = true;
            this.nextGeneration();
            var9 = 0;
         } finally {
            if (!var7) {
               this.breakBarrier();
            }

         }
      } finally {
         var4.unlock();
      }

      return var9;
   }

   public CyclicBarrier(int var1, Runnable var2) {
      this.lock = new ReentrantLock();
      this.trip = this.lock.newCondition();
      this.generation = new CyclicBarrier.Generation();
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.parties = var1;
         this.count = var1;
         this.barrierCommand = var2;
      }
   }

   public CyclicBarrier(int var1) {
      this(var1, (Runnable)null);
   }

   public int getParties() {
      return this.parties;
   }

   public int await() throws InterruptedException, BrokenBarrierException {
      try {
         return this.dowait(false, 0L);
      } catch (TimeoutException var2) {
         throw new Error(var2);
      }
   }

   public int await(long var1, TimeUnit var3) throws InterruptedException, BrokenBarrierException, TimeoutException {
      return this.dowait(true, var3.toNanos(var1));
   }

   public boolean isBroken() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      boolean var2;
      try {
         var2 = this.generation.broken;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public void reset() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      try {
         this.breakBarrier();
         this.nextGeneration();
      } finally {
         var1.unlock();
      }

   }

   public int getNumberWaiting() {
      ReentrantLock var1 = this.lock;
      var1.lock();

      int var2;
      try {
         var2 = this.parties - this.count;
      } finally {
         var1.unlock();
      }

      return var2;
   }

   private static class Generation {
      boolean broken;

      private Generation() {
         this.broken = false;
      }

      // $FF: synthetic method
      Generation(Object var1) {
         this();
      }
   }
}
