package java.lang.ref;

import java.util.function.Consumer;
import sun.misc.VM;

public class ReferenceQueue<T> {
   static ReferenceQueue<Object> NULL = new ReferenceQueue.Null();
   static ReferenceQueue<Object> ENQUEUED = new ReferenceQueue.Null();
   private ReferenceQueue.Lock lock = new ReferenceQueue.Lock();
   private volatile Reference<? extends T> head = null;
   private long queueLength = 0L;

   boolean enqueue(Reference<? extends T> var1) {
      synchronized(this.lock) {
         ReferenceQueue var3 = var1.queue;
         if (var3 != NULL && var3 != ENQUEUED) {
            assert var3 == this;

            var1.queue = ENQUEUED;
            var1.next = this.head == null ? var1 : this.head;
            this.head = var1;
            ++this.queueLength;
            if (var1 instanceof FinalReference) {
               VM.addFinalRefCount(1);
            }

            this.lock.notifyAll();
            return true;
         } else {
            return false;
         }
      }
   }

   private Reference<? extends T> reallyPoll() {
      Reference var1 = this.head;
      if (var1 != null) {
         Reference var2 = var1.next;
         this.head = var2 == var1 ? null : var2;
         var1.queue = NULL;
         var1.next = var1;
         --this.queueLength;
         if (var1 instanceof FinalReference) {
            VM.addFinalRefCount(-1);
         }

         return var1;
      } else {
         return null;
      }
   }

   public Reference<? extends T> poll() {
      if (this.head == null) {
         return null;
      } else {
         synchronized(this.lock) {
            return this.reallyPoll();
         }
      }
   }

   public Reference<? extends T> remove(long var1) throws IllegalArgumentException, InterruptedException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Negative timeout value");
      } else {
         synchronized(this.lock) {
            Reference var4 = this.reallyPoll();
            if (var4 != null) {
               return var4;
            } else {
               long var5 = var1 == 0L ? 0L : System.nanoTime();

               while(true) {
                  this.lock.wait(var1);
                  var4 = this.reallyPoll();
                  if (var4 != null) {
                     return var4;
                  }

                  if (var1 != 0L) {
                     long var7 = System.nanoTime();
                     var1 -= (var7 - var5) / 1000000L;
                     if (var1 <= 0L) {
                        return null;
                     }

                     var5 = var7;
                  }
               }
            }
         }
      }
   }

   public Reference<? extends T> remove() throws InterruptedException {
      return this.remove(0L);
   }

   void forEach(Consumer<? super Reference<? extends T>> var1) {
      Reference var2 = this.head;

      while(var2 != null) {
         var1.accept(var2);
         Reference var3 = var2.next;
         if (var3 == var2) {
            if (var2.queue == ENQUEUED) {
               var2 = null;
            } else {
               var2 = this.head;
            }
         } else {
            var2 = var3;
         }
      }

   }

   private static class Lock {
      private Lock() {
      }

      // $FF: synthetic method
      Lock(Object var1) {
         this();
      }
   }

   private static class Null<S> extends ReferenceQueue<S> {
      private Null() {
      }

      boolean enqueue(Reference<? extends S> var1) {
         return false;
      }

      // $FF: synthetic method
      Null(Object var1) {
         this();
      }
   }
}
