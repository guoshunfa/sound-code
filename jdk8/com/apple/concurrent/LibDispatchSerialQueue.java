package com.apple.concurrent;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

class LibDispatchSerialQueue extends AbstractExecutorService {
   static final int RUNNING = 0;
   static final int SHUTDOWN = 1;
   static final int TERMINATED = 3;
   final Object lock = new Object();
   LibDispatchQueue nativeQueueWrapper;
   volatile int runState;

   LibDispatchSerialQueue(long var1) {
      this.nativeQueueWrapper = new LibDispatchQueue(var1);
   }

   public void execute(Runnable var1) {
      if (this.nativeQueueWrapper != null) {
         LibDispatchNative.nativeExecuteAsync(this.nativeQueueWrapper.ptr, var1);
      }
   }

   public boolean isShutdown() {
      return this.runState != 0;
   }

   public boolean isTerminated() {
      return this.runState == 3;
   }

   public void shutdown() {
      synchronized(this.lock) {
         if (this.runState == 0) {
            this.runState = 1;
            this.execute(new Runnable() {
               public void run() {
                  synchronized(LibDispatchSerialQueue.this.lock) {
                     LibDispatchSerialQueue.this.runState = 3;
                     LibDispatchSerialQueue.this.lock.notifyAll();
                  }
               }
            });
            this.nativeQueueWrapper = null;
         }
      }
   }

   public List<Runnable> shutdownNow() {
      this.shutdown();
      return null;
   }

   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      if (this.runState == 3) {
         return true;
      } else {
         long var4 = var3.toMillis(var1);
         if (var4 <= 0L) {
            return false;
         } else {
            synchronized(this.lock) {
               if (this.runState == 3) {
                  return true;
               } else {
                  this.lock.wait(var1);
                  return this.runState == 3;
               }
            }
         }
      }
   }
}
