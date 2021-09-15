package java.lang;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

class ApplicationShutdownHooks {
   private static IdentityHashMap<Thread, Thread> hooks;

   private ApplicationShutdownHooks() {
   }

   static synchronized void add(Thread var0) {
      if (hooks == null) {
         throw new IllegalStateException("Shutdown in progress");
      } else if (var0.isAlive()) {
         throw new IllegalArgumentException("Hook already running");
      } else if (hooks.containsKey(var0)) {
         throw new IllegalArgumentException("Hook previously registered");
      } else {
         hooks.put(var0, var0);
      }
   }

   static synchronized boolean remove(Thread var0) {
      if (hooks == null) {
         throw new IllegalStateException("Shutdown in progress");
      } else if (var0 == null) {
         throw new NullPointerException();
      } else {
         return hooks.remove(var0) != null;
      }
   }

   static void runHooks() {
      Class var1 = ApplicationShutdownHooks.class;
      Set var0;
      synchronized(ApplicationShutdownHooks.class) {
         var0 = hooks.keySet();
         hooks = null;
      }

      Iterator var6 = var0.iterator();

      Thread var2;
      while(var6.hasNext()) {
         var2 = (Thread)var6.next();
         var2.start();
      }

      var6 = var0.iterator();

      while(var6.hasNext()) {
         var2 = (Thread)var6.next();

         while(true) {
            try {
               var2.join();
               break;
            } catch (InterruptedException var5) {
            }
         }
      }

   }

   static {
      try {
         Shutdown.add(1, false, new Runnable() {
            public void run() {
               ApplicationShutdownHooks.runHooks();
            }
         });
         hooks = new IdentityHashMap();
      } catch (IllegalStateException var1) {
         hooks = null;
      }

   }
}
