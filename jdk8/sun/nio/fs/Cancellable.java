package sun.nio.fs;

import java.util.concurrent.ExecutionException;
import sun.misc.Unsafe;

abstract class Cancellable implements Runnable {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private final long pollingAddress;
   private final Object lock = new Object();
   private boolean completed;
   private Throwable exception;

   protected Cancellable() {
      this.pollingAddress = unsafe.allocateMemory(4L);
      unsafe.putIntVolatile((Object)null, this.pollingAddress, 0);
   }

   protected long addressToPollForCancel() {
      return this.pollingAddress;
   }

   protected int cancelValue() {
      return Integer.MAX_VALUE;
   }

   final void cancel() {
      synchronized(this.lock) {
         if (!this.completed) {
            unsafe.putIntVolatile((Object)null, this.pollingAddress, this.cancelValue());
         }

      }
   }

   private Throwable exception() {
      synchronized(this.lock) {
         return this.exception;
      }
   }

   public final void run() {
      boolean var14 = false;

      label99: {
         try {
            var14 = true;
            this.implRun();
            var14 = false;
            break label99;
         } catch (Throwable var19) {
            Throwable var1 = var19;
            synchronized(this.lock) {
               this.exception = var1;
               var14 = false;
            }
         } finally {
            if (var14) {
               synchronized(this.lock) {
                  this.completed = true;
                  unsafe.freeMemory(this.pollingAddress);
               }
            }
         }

         synchronized(this.lock) {
            this.completed = true;
            unsafe.freeMemory(this.pollingAddress);
            return;
         }
      }

      synchronized(this.lock) {
         this.completed = true;
         unsafe.freeMemory(this.pollingAddress);
      }

   }

   abstract void implRun() throws Throwable;

   static void runInterruptibly(Cancellable var0) throws ExecutionException {
      Thread var1 = new Thread(var0);
      var1.start();
      boolean var2 = false;

      while(var1.isAlive()) {
         try {
            var1.join();
         } catch (InterruptedException var4) {
            var2 = true;
            var0.cancel();
         }
      }

      if (var2) {
         Thread.currentThread().interrupt();
      }

      Throwable var3 = var0.exception();
      if (var3 != null) {
         throw new ExecutionException(var3);
      }
   }
}
