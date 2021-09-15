package java.lang.ref;

import sun.misc.Cleaner;
import sun.misc.JavaLangRefAccess;
import sun.misc.SharedSecrets;

public abstract class Reference<T> {
   private T referent;
   volatile ReferenceQueue<? super T> queue;
   volatile Reference next;
   private transient Reference<T> discovered;
   private static Reference.Lock lock = new Reference.Lock();
   private static Reference<Object> pending = null;

   static boolean tryHandlePending(boolean var0) {
      Reference var1;
      Cleaner var2;
      try {
         synchronized(lock) {
            if (pending == null) {
               if (var0) {
                  lock.wait();
               }

               return var0;
            }

            var1 = pending;
            var2 = var1 instanceof Cleaner ? (Cleaner)var1 : null;
            pending = var1.discovered;
            var1.discovered = null;
         }
      } catch (OutOfMemoryError var6) {
         Thread.yield();
         return true;
      } catch (InterruptedException var7) {
         return true;
      }

      if (var2 != null) {
         var2.clean();
         return true;
      } else {
         ReferenceQueue var3 = var1.queue;
         if (var3 != ReferenceQueue.NULL) {
            var3.enqueue(var1);
         }

         return true;
      }
   }

   public T get() {
      return this.referent;
   }

   public void clear() {
      this.referent = null;
   }

   public boolean isEnqueued() {
      return this.queue == ReferenceQueue.ENQUEUED;
   }

   public boolean enqueue() {
      return this.queue.enqueue(this);
   }

   Reference(T var1) {
      this(var1, (ReferenceQueue)null);
   }

   Reference(T var1, ReferenceQueue<? super T> var2) {
      this.referent = var1;
      this.queue = var2 == null ? ReferenceQueue.NULL : var2;
   }

   static {
      ThreadGroup var0 = Thread.currentThread().getThreadGroup();

      for(ThreadGroup var1 = var0; var1 != null; var1 = var1.getParent()) {
         var0 = var1;
      }

      Reference.ReferenceHandler var2 = new Reference.ReferenceHandler(var0, "Reference Handler");
      var2.setPriority(10);
      var2.setDaemon(true);
      var2.start();
      SharedSecrets.setJavaLangRefAccess(new JavaLangRefAccess() {
         public boolean tryHandlePendingReference() {
            return Reference.tryHandlePending(false);
         }
      });
   }

   private static class ReferenceHandler extends Thread {
      private static void ensureClassInitialized(Class<?> var0) {
         try {
            Class.forName(var0.getName(), true, var0.getClassLoader());
         } catch (ClassNotFoundException var2) {
            throw (Error)(new NoClassDefFoundError(var2.getMessage())).initCause(var2);
         }
      }

      ReferenceHandler(ThreadGroup var1, String var2) {
         super(var1, var2);
      }

      public void run() {
         while(true) {
            Reference.tryHandlePending(true);
         }
      }

      static {
         ensureClassInitialized(InterruptedException.class);
         ensureClassInitialized(Cleaner.class);
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
}
