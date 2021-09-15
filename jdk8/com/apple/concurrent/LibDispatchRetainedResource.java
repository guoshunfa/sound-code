package com.apple.concurrent;

class LibDispatchRetainedResource {
   protected long ptr;

   protected LibDispatchRetainedResource(long var1) {
      this.ptr = var1;
   }

   protected synchronized void dispose() {
      if (this.ptr != 0L) {
         LibDispatchNative.nativeReleaseQueue(this.ptr);
      }

      this.ptr = 0L;
   }

   protected void finalize() throws Throwable {
      this.dispose();
   }
}
