package java.lang.ref;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;

final class Finalizer extends FinalReference<Object> {
   private static ReferenceQueue<Object> queue = new ReferenceQueue();
   private static Finalizer unfinalized = null;
   private static final Object lock = new Object();
   private Finalizer next = null;
   private Finalizer prev = null;

   private boolean hasBeenFinalized() {
      return this.next == this;
   }

   private void add() {
      synchronized(lock) {
         if (unfinalized != null) {
            this.next = unfinalized;
            unfinalized.prev = this;
         }

         unfinalized = this;
      }
   }

   private void remove() {
      synchronized(lock) {
         if (unfinalized == this) {
            if (this.next != null) {
               unfinalized = this.next;
            } else {
               unfinalized = this.prev;
            }
         }

         if (this.next != null) {
            this.next.prev = this.prev;
         }

         if (this.prev != null) {
            this.prev.next = this.next;
         }

         this.next = this;
         this.prev = this;
      }
   }

   private Finalizer(Object var1) {
      super(var1, queue);
      this.add();
   }

   static ReferenceQueue<Object> getQueue() {
      return queue;
   }

   static void register(Object var0) {
      new Finalizer(var0);
   }

   private void runFinalizer(JavaLangAccess var1) {
      synchronized(this) {
         if (this.hasBeenFinalized()) {
            return;
         }

         this.remove();
      }

      try {
         Object var2 = this.get();
         if (var2 != null && !(var2 instanceof Enum)) {
            var1.invokeFinalize(var2);
            var2 = null;
         }
      } catch (Throwable var4) {
      }

      super.clear();
   }

   private static void forkSecondaryFinalizer(final Runnable var0) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            ThreadGroup var1 = Thread.currentThread().getThreadGroup();

            for(ThreadGroup var2 = var1; var2 != null; var2 = var2.getParent()) {
               var1 = var2;
            }

            Thread var5 = new Thread(var1, var0, "Secondary finalizer");
            var5.start();

            try {
               var5.join();
            } catch (InterruptedException var4) {
               Thread.currentThread().interrupt();
            }

            return null;
         }
      });
   }

   static void runFinalization() {
      if (VM.isBooted()) {
         forkSecondaryFinalizer(new Runnable() {
            private volatile boolean running;

            public void run() {
               if (!this.running) {
                  JavaLangAccess var1 = SharedSecrets.getJavaLangAccess();
                  this.running = true;

                  while(true) {
                     Finalizer var2 = (Finalizer)Finalizer.queue.poll();
                     if (var2 == null) {
                        return;
                     }

                     var2.runFinalizer(var1);
                  }
               }
            }
         });
      }
   }

   static void runAllFinalizers() {
      if (VM.isBooted()) {
         forkSecondaryFinalizer(new Runnable() {
            private volatile boolean running;

            public void run() {
               if (!this.running) {
                  JavaLangAccess var1 = SharedSecrets.getJavaLangAccess();
                  this.running = true;

                  while(true) {
                     Finalizer var2;
                     synchronized(Finalizer.lock) {
                        var2 = Finalizer.unfinalized;
                        if (var2 == null) {
                           return;
                        }

                        Finalizer.unfinalized = var2.next;
                     }

                     var2.runFinalizer(var1);
                  }
               }
            }
         });
      }
   }

   static {
      ThreadGroup var0 = Thread.currentThread().getThreadGroup();

      for(ThreadGroup var1 = var0; var1 != null; var1 = var1.getParent()) {
         var0 = var1;
      }

      Finalizer.FinalizerThread var2 = new Finalizer.FinalizerThread(var0);
      var2.setPriority(8);
      var2.setDaemon(true);
      var2.start();
   }

   private static class FinalizerThread extends Thread {
      private volatile boolean running;

      FinalizerThread(ThreadGroup var1) {
         super(var1, "Finalizer");
      }

      public void run() {
         if (!this.running) {
            while(!VM.isBooted()) {
               try {
                  VM.awaitBooted();
               } catch (InterruptedException var3) {
               }
            }

            JavaLangAccess var1 = SharedSecrets.getJavaLangAccess();
            this.running = true;

            while(true) {
               while(true) {
                  try {
                     Finalizer var2 = (Finalizer)Finalizer.queue.remove();
                     var2.runFinalizer(var1);
                  } catch (InterruptedException var4) {
                  }
               }
            }
         }

      }
   }
}
