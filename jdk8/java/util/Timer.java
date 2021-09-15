package java.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Timer {
   private final TaskQueue queue;
   private final TimerThread thread;
   private final Object threadReaper;
   private static final AtomicInteger nextSerialNumber = new AtomicInteger(0);

   private static int serialNumber() {
      return nextSerialNumber.getAndIncrement();
   }

   public Timer() {
      this("Timer-" + serialNumber());
   }

   public Timer(boolean var1) {
      this("Timer-" + serialNumber(), var1);
   }

   public Timer(String var1) {
      this.queue = new TaskQueue();
      this.thread = new TimerThread(this.queue);
      this.threadReaper = new Object() {
         protected void finalize() throws Throwable {
            synchronized(Timer.this.queue) {
               Timer.this.thread.newTasksMayBeScheduled = false;
               Timer.this.queue.notify();
            }
         }
      };
      this.thread.setName(var1);
      this.thread.start();
   }

   public Timer(String var1, boolean var2) {
      this.queue = new TaskQueue();
      this.thread = new TimerThread(this.queue);
      this.threadReaper = new Object() {
         protected void finalize() throws Throwable {
            synchronized(Timer.this.queue) {
               Timer.this.thread.newTasksMayBeScheduled = false;
               Timer.this.queue.notify();
            }
         }
      };
      this.thread.setName(var1);
      this.thread.setDaemon(var2);
      this.thread.start();
   }

   public void schedule(TimerTask var1, long var2) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else {
         this.sched(var1, System.currentTimeMillis() + var2, 0L);
      }
   }

   public void schedule(TimerTask var1, Date var2) {
      this.sched(var1, var2.getTime(), 0L);
   }

   public void schedule(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, System.currentTimeMillis() + var2, -var4);
      }
   }

   public void schedule(TimerTask var1, Date var2, long var3) {
      if (var3 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, var2.getTime(), -var3);
      }
   }

   public void scheduleAtFixedRate(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Negative delay.");
      } else if (var4 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, System.currentTimeMillis() + var2, var4);
      }
   }

   public void scheduleAtFixedRate(TimerTask var1, Date var2, long var3) {
      if (var3 <= 0L) {
         throw new IllegalArgumentException("Non-positive period.");
      } else {
         this.sched(var1, var2.getTime(), var3);
      }
   }

   private void sched(TimerTask var1, long var2, long var4) {
      if (var2 < 0L) {
         throw new IllegalArgumentException("Illegal execution time.");
      } else {
         if (Math.abs(var4) > 4611686018427387903L) {
            var4 >>= 1;
         }

         synchronized(this.queue) {
            if (!this.thread.newTasksMayBeScheduled) {
               throw new IllegalStateException("Timer already cancelled.");
            } else {
               synchronized(var1.lock) {
                  if (var1.state != 0) {
                     throw new IllegalStateException("Task already scheduled or cancelled");
                  }

                  var1.nextExecutionTime = var2;
                  var1.period = var4;
                  var1.state = 1;
               }

               this.queue.add(var1);
               if (this.queue.getMin() == var1) {
                  this.queue.notify();
               }

            }
         }
      }
   }

   public void cancel() {
      synchronized(this.queue) {
         this.thread.newTasksMayBeScheduled = false;
         this.queue.clear();
         this.queue.notify();
      }
   }

   public int purge() {
      int var1 = 0;
      synchronized(this.queue) {
         for(int var3 = this.queue.size(); var3 > 0; --var3) {
            if (this.queue.get(var3).state == 3) {
               this.queue.quickRemove(var3);
               ++var1;
            }
         }

         if (var1 != 0) {
            this.queue.heapify();
         }

         return var1;
      }
   }
}
