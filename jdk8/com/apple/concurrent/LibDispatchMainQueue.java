package com.apple.concurrent;

import java.util.concurrent.Executor;

abstract class LibDispatchMainQueue extends LibDispatchQueue implements Executor {
   public LibDispatchMainQueue() {
      super(LibDispatchNative.nativeGetMainQueue());
   }

   protected synchronized void dispose() {
   }

   static class ASync extends LibDispatchMainQueue {
      public void execute(Runnable var1) {
         LibDispatchNative.nativeExecuteAsync(this.ptr, var1);
      }
   }

   static class Sync extends LibDispatchMainQueue {
      public void execute(Runnable var1) {
         LibDispatchNative.nativeExecuteSync(this.ptr, var1);
      }
   }
}
