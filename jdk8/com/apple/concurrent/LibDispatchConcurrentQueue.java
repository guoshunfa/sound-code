package com.apple.concurrent;

import java.util.concurrent.Executor;

class LibDispatchConcurrentQueue extends LibDispatchQueue implements Executor {
   LibDispatchConcurrentQueue(long var1) {
      super(var1);
   }

   public void execute(Runnable var1) {
      LibDispatchNative.nativeExecuteAsync(this.ptr, var1);
   }

   protected synchronized void dispose() {
   }
}
