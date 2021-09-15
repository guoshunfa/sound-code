package com.sun.istack.internal;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface Pool<T> {
   @NotNull
   T take();

   void recycle(@NotNull T var1);

   public abstract static class Impl<T> implements Pool<T> {
      private volatile WeakReference<ConcurrentLinkedQueue<T>> queue;

      @NotNull
      public final T take() {
         T t = this.getQueue().poll();
         return t == null ? this.create() : t;
      }

      public final void recycle(T t) {
         this.getQueue().offer(t);
      }

      private ConcurrentLinkedQueue<T> getQueue() {
         WeakReference<ConcurrentLinkedQueue<T>> q = this.queue;
         ConcurrentLinkedQueue d;
         if (q != null) {
            d = (ConcurrentLinkedQueue)q.get();
            if (d != null) {
               return d;
            }
         }

         d = new ConcurrentLinkedQueue();
         this.queue = new WeakReference(d);
         return d;
      }

      @NotNull
      protected abstract T create();
   }
}
