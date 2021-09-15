package java.util;

class TimerThread extends Thread {
   boolean newTasksMayBeScheduled = true;
   private TaskQueue queue;

   TimerThread(TaskQueue var1) {
      this.queue = var1;
   }

   public void run() {
      boolean var9 = false;

      try {
         var9 = true;
         this.mainLoop();
         var9 = false;
      } finally {
         if (var9) {
            synchronized(this.queue) {
               this.newTasksMayBeScheduled = false;
               this.queue.clear();
            }
         }
      }

      synchronized(this.queue) {
         this.newTasksMayBeScheduled = false;
         this.queue.clear();
      }
   }

   private void mainLoop() {
      while(true) {
         while(true) {
            try {
               TimerTask var1;
               boolean var2;
               synchronized(this.queue) {
                  while(this.queue.isEmpty() && this.newTasksMayBeScheduled) {
                     this.queue.wait();
                  }

                  if (this.queue.isEmpty()) {
                     return;
                  }

                  var1 = this.queue.getMin();
                  long var4;
                  long var6;
                  synchronized(var1.lock) {
                     if (var1.state == 3) {
                        this.queue.removeMin();
                        continue;
                     }

                     var4 = System.currentTimeMillis();
                     var6 = var1.nextExecutionTime;
                     if (var2 = var6 <= var4) {
                        if (var1.period == 0L) {
                           this.queue.removeMin();
                           var1.state = 2;
                        } else {
                           this.queue.rescheduleMin(var1.period < 0L ? var4 - var1.period : var6 + var1.period);
                        }
                     }
                  }

                  if (!var2) {
                     this.queue.wait(var6 - var4);
                  }
               }

               if (var2) {
                  var1.run();
               }
            } catch (InterruptedException var13) {
            }
         }
      }
   }
}
