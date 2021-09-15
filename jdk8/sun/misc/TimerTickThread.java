package sun.misc;

class TimerTickThread extends Thread {
   static final int MAX_POOL_SIZE = 3;
   static int curPoolSize = 0;
   static TimerTickThread pool = null;
   TimerTickThread next = null;
   Timer timer;
   long lastSleepUntil;

   protected static synchronized TimerTickThread call(Timer var0, long var1) {
      TimerTickThread var3 = pool;
      if (var3 == null) {
         var3 = new TimerTickThread();
         var3.timer = var0;
         var3.lastSleepUntil = var1;
         var3.start();
      } else {
         pool = pool.next;
         var3.timer = var0;
         var3.lastSleepUntil = var1;
         synchronized(var3) {
            var3.notify();
         }
      }

      return var3;
   }

   private boolean returnToPool() {
      synchronized(this.getClass()) {
         if (curPoolSize >= 3) {
            return false;
         }

         this.next = pool;
         pool = this;
         ++curPoolSize;
         this.timer = null;
      }

      while(this.timer == null) {
         synchronized(this) {
            try {
               this.wait();
            } catch (InterruptedException var6) {
            }
         }
      }

      synchronized(this.getClass()) {
         --curPoolSize;
         return true;
      }
   }

   public void run() {
      do {
         this.timer.owner.tick(this.timer);
         synchronized(TimerThread.timerThread) {
            synchronized(this.timer) {
               if (this.lastSleepUntil == this.timer.sleepUntil) {
                  TimerThread.requeue(this.timer);
               }
            }
         }
      } while(this.returnToPool());

   }
}
