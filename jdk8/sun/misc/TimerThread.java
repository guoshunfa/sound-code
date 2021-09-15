package sun.misc;

class TimerThread extends Thread {
   public static boolean debug = false;
   static TimerThread timerThread;
   static boolean notified = false;
   static Timer timerQueue = null;

   protected TimerThread() {
      super("TimerThread");
      timerThread = this;
      this.start();
   }

   public synchronized void run() {
      while(true) {
         if (timerQueue == null) {
            try {
               this.wait();
            } catch (InterruptedException var7) {
            }
         } else {
            notified = false;
            long var1 = timerQueue.sleepUntil - System.currentTimeMillis();
            if (var1 > 0L) {
               try {
                  this.wait(var1);
               } catch (InterruptedException var8) {
               }
            }

            if (!notified) {
               Timer var3 = timerQueue;
               timerQueue = timerQueue.next;
               TimerTickThread var4 = TimerTickThread.call(var3, var3.sleepUntil);
               if (debug) {
                  long var5 = System.currentTimeMillis() - var3.sleepUntil;
                  System.out.println("tick(" + var4.getName() + "," + var3.interval + "," + var5 + ")");
                  if (var5 > 250L) {
                     System.out.println("*** BIG DELAY ***");
                  }
               }
            }
         }
      }
   }

   protected static void enqueue(Timer var0) {
      Timer var1 = null;
      Timer var2 = timerQueue;
      if (var2 != null && var0.sleepUntil > var2.sleepUntil) {
         while(true) {
            var1 = var2;
            var2 = var2.next;
            if (var2 == null || var0.sleepUntil <= var2.sleepUntil) {
               var0.next = var2;
               var1.next = var0;
               break;
            }
         }
      } else {
         var0.next = timerQueue;
         timerQueue = var0;
         notified = true;
         timerThread.notify();
      }

      if (debug) {
         long var3 = System.currentTimeMillis();
         System.out.print(Thread.currentThread().getName() + ": enqueue " + var0.interval + ": ");

         for(var2 = timerQueue; var2 != null; var2 = var2.next) {
            long var5 = var2.sleepUntil - var3;
            System.out.print(var2.interval + "(" + var5 + ") ");
         }

         System.out.println();
      }

   }

   protected static boolean dequeue(Timer var0) {
      Timer var1 = null;

      Timer var2;
      for(var2 = timerQueue; var2 != null && var2 != var0; var2 = var2.next) {
         var1 = var2;
      }

      if (var2 == null) {
         if (debug) {
            System.out.println(Thread.currentThread().getName() + ": dequeue " + var0.interval + ": no-op");
         }

         return false;
      } else {
         if (var1 == null) {
            timerQueue = var0.next;
            notified = true;
            timerThread.notify();
         } else {
            var1.next = var0.next;
         }

         var0.next = null;
         if (debug) {
            long var3 = System.currentTimeMillis();
            System.out.print(Thread.currentThread().getName() + ": dequeue " + var0.interval + ": ");

            for(var2 = timerQueue; var2 != null; var2 = var2.next) {
               long var5 = var2.sleepUntil - var3;
               System.out.print(var2.interval + "(" + var5 + ") ");
            }

            System.out.println();
         }

         return true;
      }
   }

   protected static void requeue(Timer var0) {
      if (!var0.stopped) {
         long var1 = System.currentTimeMillis();
         if (var0.regular) {
            var0.sleepUntil += var0.interval;
         } else {
            var0.sleepUntil = var1 + var0.interval;
         }

         enqueue(var0);
      } else if (debug) {
         System.out.println(Thread.currentThread().getName() + ": requeue " + var0.interval + ": no-op");
      }

   }
}
