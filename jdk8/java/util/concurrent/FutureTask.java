package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class FutureTask<V> implements RunnableFuture<V> {
   private volatile int state;
   private static final int NEW = 0;
   private static final int COMPLETING = 1;
   private static final int NORMAL = 2;
   private static final int EXCEPTIONAL = 3;
   private static final int CANCELLED = 4;
   private static final int INTERRUPTING = 5;
   private static final int INTERRUPTED = 6;
   private Callable<V> callable;
   private Object outcome;
   private volatile Thread runner;
   private volatile FutureTask.WaitNode waiters;
   private static final Unsafe UNSAFE;
   private static final long stateOffset;
   private static final long runnerOffset;
   private static final long waitersOffset;

   private V report(int var1) throws ExecutionException {
      Object var2 = this.outcome;
      if (var1 == 2) {
         return var2;
      } else if (var1 >= 4) {
         throw new CancellationException();
      } else {
         throw new ExecutionException((Throwable)var2);
      }
   }

   public FutureTask(Callable<V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.callable = var1;
         this.state = 0;
      }
   }

   public FutureTask(Runnable var1, V var2) {
      this.callable = Executors.callable(var1, var2);
      this.state = 0;
   }

   public boolean isCancelled() {
      return this.state >= 4;
   }

   public boolean isDone() {
      return this.state != 0;
   }

   public boolean cancel(boolean var1) {
      if (this.state == 0 && UNSAFE.compareAndSwapInt(this, stateOffset, 0, var1 ? 5 : 4)) {
         try {
            if (var1) {
               try {
                  Thread var2 = this.runner;
                  if (var2 != null) {
                     var2.interrupt();
                  }
               } finally {
                  UNSAFE.putOrderedInt(this, stateOffset, 6);
               }
            }
         } finally {
            this.finishCompletion();
         }

         return true;
      } else {
         return false;
      }
   }

   public V get() throws InterruptedException, ExecutionException {
      int var1 = this.state;
      if (var1 <= 1) {
         var1 = this.awaitDone(false, 0L);
      }

      return this.report(var1);
   }

   public V get(long var1, TimeUnit var3) throws InterruptedException, ExecutionException, TimeoutException {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         int var4 = this.state;
         if (var4 <= 1 && (var4 = this.awaitDone(true, var3.toNanos(var1))) <= 1) {
            throw new TimeoutException();
         } else {
            return this.report(var4);
         }
      }
   }

   protected void done() {
   }

   protected void set(V var1) {
      if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1)) {
         this.outcome = var1;
         UNSAFE.putOrderedInt(this, stateOffset, 2);
         this.finishCompletion();
      }

   }

   protected void setException(Throwable var1) {
      if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1)) {
         this.outcome = var1;
         UNSAFE.putOrderedInt(this, stateOffset, 3);
         this.finishCompletion();
      }

   }

   public void run() {
      if (this.state == 0 && UNSAFE.compareAndSwapObject(this, runnerOffset, (Object)null, Thread.currentThread())) {
         boolean var9 = false;

         try {
            var9 = true;
            Callable var1 = this.callable;
            if (var1 != null) {
               if (this.state == 0) {
                  Object var2;
                  boolean var3;
                  try {
                     var2 = var1.call();
                     var3 = true;
                  } catch (Throwable var10) {
                     var2 = null;
                     var3 = false;
                     this.setException(var10);
                  }

                  if (var3) {
                     this.set(var2);
                     var9 = false;
                  } else {
                     var9 = false;
                  }
               } else {
                  var9 = false;
               }
            } else {
               var9 = false;
            }
         } finally {
            if (var9) {
               this.runner = null;
               int var6 = this.state;
               if (var6 >= 5) {
                  this.handlePossibleCancellationInterrupt(var6);
               }

            }
         }

         this.runner = null;
         int var12 = this.state;
         if (var12 >= 5) {
            this.handlePossibleCancellationInterrupt(var12);
         }

      }
   }

   protected boolean runAndReset() {
      if (this.state == 0 && UNSAFE.compareAndSwapObject(this, runnerOffset, (Object)null, Thread.currentThread())) {
         boolean var1 = false;
         int var2 = this.state;

         try {
            Callable var3 = this.callable;
            if (var3 != null && var2 == 0) {
               try {
                  var3.call();
                  var1 = true;
               } catch (Throwable var8) {
                  this.setException(var8);
               }
            }
         } finally {
            this.runner = null;
            var2 = this.state;
            if (var2 >= 5) {
               this.handlePossibleCancellationInterrupt(var2);
            }

         }

         return var1 && var2 == 0;
      } else {
         return false;
      }
   }

   private void handlePossibleCancellationInterrupt(int var1) {
      if (var1 == 5) {
         while(this.state == 5) {
            Thread.yield();
         }
      }

   }

   private void finishCompletion() {
      while(true) {
         FutureTask.WaitNode var1;
         if ((var1 = this.waiters) != null) {
            if (!UNSAFE.compareAndSwapObject(this, waitersOffset, var1, (Object)null)) {
               continue;
            }

            while(true) {
               Thread var2 = var1.thread;
               if (var2 != null) {
                  var1.thread = null;
                  LockSupport.unpark(var2);
               }

               FutureTask.WaitNode var3 = var1.next;
               if (var3 == null) {
                  break;
               }

               var1.next = null;
               var1 = var3;
            }
         }

         this.done();
         this.callable = null;
         return;
      }
   }

   private int awaitDone(boolean var1, long var2) throws InterruptedException {
      long var4 = var1 ? System.nanoTime() + var2 : 0L;
      FutureTask.WaitNode var6 = null;
      boolean var7 = false;

      while(!Thread.interrupted()) {
         int var8 = this.state;
         if (var8 > 1) {
            if (var6 != null) {
               var6.thread = null;
            }

            return var8;
         }

         if (var8 == 1) {
            Thread.yield();
         } else if (var6 == null) {
            var6 = new FutureTask.WaitNode();
         } else if (!var7) {
            var7 = UNSAFE.compareAndSwapObject(this, waitersOffset, var6.next = this.waiters, var6);
         } else if (var1) {
            var2 = var4 - System.nanoTime();
            if (var2 <= 0L) {
               this.removeWaiter(var6);
               return this.state;
            }

            LockSupport.parkNanos(this, var2);
         } else {
            LockSupport.park(this);
         }
      }

      this.removeWaiter(var6);
      throw new InterruptedException();
   }

   private void removeWaiter(FutureTask.WaitNode var1) {
      if (var1 != null) {
         var1.thread = null;

         label29:
         while(true) {
            FutureTask.WaitNode var2 = null;

            FutureTask.WaitNode var4;
            for(FutureTask.WaitNode var3 = this.waiters; var3 != null; var3 = var4) {
               var4 = var3.next;
               if (var3.thread != null) {
                  var2 = var3;
               } else if (var2 != null) {
                  var2.next = var4;
                  if (var2.thread == null) {
                     continue label29;
                  }
               } else if (!UNSAFE.compareAndSwapObject(this, waitersOffset, var3, var4)) {
                  continue label29;
               }
            }

            return;
         }
      }
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = FutureTask.class;
         stateOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("state"));
         runnerOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("runner"));
         waitersOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("waiters"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class WaitNode {
      volatile Thread thread = Thread.currentThread();
      volatile FutureTask.WaitNode next;
   }
}
