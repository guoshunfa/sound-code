package java.lang;

class Shutdown {
   private static final int RUNNING = 0;
   private static final int HOOKS = 1;
   private static final int FINALIZERS = 2;
   private static int state = 0;
   private static boolean runFinalizersOnExit = false;
   private static final int MAX_SYSTEM_HOOKS = 10;
   private static final Runnable[] hooks = new Runnable[10];
   private static int currentRunningHook = 0;
   private static Object lock = new Shutdown.Lock();
   private static Object haltLock = new Shutdown.Lock();

   static void setRunFinalizersOnExit(boolean var0) {
      synchronized(lock) {
         runFinalizersOnExit = var0;
      }
   }

   static void add(int var0, boolean var1, Runnable var2) {
      synchronized(lock) {
         if (hooks[var0] != null) {
            throw new InternalError("Shutdown hook at slot " + var0 + " already registered");
         } else {
            if (!var1) {
               if (state > 0) {
                  throw new IllegalStateException("Shutdown in progress");
               }
            } else if (state > 1 || state == 1 && var0 <= currentRunningHook) {
               throw new IllegalStateException("Shutdown in progress");
            }

            hooks[var0] = var2;
         }
      }
   }

   private static void runHooks() {
      for(int var0 = 0; var0 < 10; ++var0) {
         try {
            Runnable var1;
            synchronized(lock) {
               currentRunningHook = var0;
               var1 = hooks[var0];
            }

            if (var1 != null) {
               var1.run();
            }
         } catch (Throwable var5) {
            if (var5 instanceof ThreadDeath) {
               ThreadDeath var2 = (ThreadDeath)var5;
               throw var2;
            }
         }
      }

   }

   static void halt(int var0) {
      synchronized(haltLock) {
         halt0(var0);
      }
   }

   static native void halt0(int var0);

   private static native void runAllFinalizers();

   private static void sequence() {
      synchronized(lock) {
         if (state != 1) {
            return;
         }
      }

      runHooks();
      boolean var0;
      synchronized(lock) {
         state = 2;
         var0 = runFinalizersOnExit;
      }

      if (var0) {
         runAllFinalizers();
      }

   }

   static void exit(int var0) {
      boolean var1 = false;
      synchronized(lock) {
         if (var0 != 0) {
            runFinalizersOnExit = false;
         }

         switch(state) {
         case 0:
            state = 1;
         case 1:
         default:
            break;
         case 2:
            if (var0 != 0) {
               halt(var0);
            } else {
               var1 = runFinalizersOnExit;
            }
         }
      }

      if (var1) {
         runAllFinalizers();
         halt(var0);
      }

      Class var2 = Shutdown.class;
      synchronized(Shutdown.class) {
         sequence();
         halt(var0);
      }
   }

   static void shutdown() {
      synchronized(lock) {
         switch(state) {
         case 0:
            state = 1;
         case 1:
         case 2:
         }
      }

      Class var0 = Shutdown.class;
      synchronized(Shutdown.class) {
         sequence();
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
