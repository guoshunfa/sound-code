package java.util.concurrent;

import sun.misc.Unsafe;

public abstract class CountedCompleter<T> extends ForkJoinTask<T> {
   private static final long serialVersionUID = 5232453752276485070L;
   final CountedCompleter<?> completer;
   volatile int pending;
   private static final Unsafe U;
   private static final long PENDING;

   protected CountedCompleter(CountedCompleter<?> var1, int var2) {
      this.completer = var1;
      this.pending = var2;
   }

   protected CountedCompleter(CountedCompleter<?> var1) {
      this.completer = var1;
   }

   protected CountedCompleter() {
      this.completer = null;
   }

   public abstract void compute();

   public void onCompletion(CountedCompleter<?> var1) {
   }

   public boolean onExceptionalCompletion(Throwable var1, CountedCompleter<?> var2) {
      return true;
   }

   public final CountedCompleter<?> getCompleter() {
      return this.completer;
   }

   public final int getPendingCount() {
      return this.pending;
   }

   public final void setPendingCount(int var1) {
      this.pending = var1;
   }

   public final void addToPendingCount(int var1) {
      U.getAndAddInt(this, PENDING, var1);
   }

   public final boolean compareAndSetPendingCount(int var1, int var2) {
      return U.compareAndSwapInt(this, PENDING, var1, var2);
   }

   public final int decrementPendingCountUnlessZero() {
      int var1;
      while((var1 = this.pending) != 0 && !U.compareAndSwapInt(this, PENDING, var1, var1 - 1)) {
      }

      return var1;
   }

   public final CountedCompleter<?> getRoot() {
      CountedCompleter var1;
      CountedCompleter var2;
      for(var1 = this; (var2 = var1.completer) != null; var1 = var2) {
      }

      return var1;
   }

   public final void tryComplete() {
      CountedCompleter var1 = this;
      CountedCompleter var2 = this;

      do {
         int var3;
         while((var3 = var1.pending) != 0) {
            if (U.compareAndSwapInt(var1, PENDING, var3, var3 - 1)) {
               return;
            }
         }

         var1.onCompletion(var2);
         var2 = var1;
      } while((var1 = var1.completer) != null);

      var2.quietlyComplete();
   }

   public final void propagateCompletion() {
      CountedCompleter var1 = this;

      CountedCompleter var2;
      do {
         int var3;
         while((var3 = var1.pending) != 0) {
            if (U.compareAndSwapInt(var1, PENDING, var3, var3 - 1)) {
               return;
            }
         }

         var2 = var1;
      } while((var1 = var1.completer) != null);

      var2.quietlyComplete();
   }

   public void complete(T var1) {
      this.setRawResult(var1);
      this.onCompletion(this);
      this.quietlyComplete();
      CountedCompleter var2;
      if ((var2 = this.completer) != null) {
         var2.tryComplete();
      }

   }

   public final CountedCompleter<?> firstComplete() {
      int var1;
      do {
         if ((var1 = this.pending) == 0) {
            return this;
         }
      } while(!U.compareAndSwapInt(this, PENDING, var1, var1 - 1));

      return null;
   }

   public final CountedCompleter<?> nextComplete() {
      CountedCompleter var1;
      if ((var1 = this.completer) != null) {
         return var1.firstComplete();
      } else {
         this.quietlyComplete();
         return null;
      }
   }

   public final void quietlyCompleteRoot() {
      CountedCompleter var1;
      CountedCompleter var2;
      for(var1 = this; (var2 = var1.completer) != null; var1 = var2) {
      }

      var1.quietlyComplete();
   }

   public final void helpComplete(int var1) {
      if (var1 > 0 && this.status >= 0) {
         Thread var2;
         if ((var2 = Thread.currentThread()) instanceof ForkJoinWorkerThread) {
            ForkJoinWorkerThread var3;
            (var3 = (ForkJoinWorkerThread)var2).pool.helpComplete(var3.workQueue, this, var1);
         } else {
            ForkJoinPool.common.externalHelpComplete(this, var1);
         }
      }

   }

   void internalPropagateException(Throwable var1) {
      CountedCompleter var2 = this;
      CountedCompleter var3 = this;

      while(var2.onExceptionalCompletion(var1, var3)) {
         var3 = var2;
         if ((var2 = var2.completer) == null || var2.status < 0 || var2.recordExceptionalCompletion(var1) != Integer.MIN_VALUE) {
            break;
         }
      }

   }

   protected final boolean exec() {
      this.compute();
      return false;
   }

   public T getRawResult() {
      return null;
   }

   protected void setRawResult(T var1) {
   }

   static {
      try {
         U = Unsafe.getUnsafe();
         PENDING = U.objectFieldOffset(CountedCompleter.class.getDeclaredField("pending"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
