package sun.misc;

public class Timer {
   public Timeable owner;
   long interval;
   long sleepUntil;
   long remainingTime;
   boolean regular;
   boolean stopped;
   Timer next;
   static TimerThread timerThread = null;

   public Timer(Timeable var1, long var2) {
      this.owner = var1;
      this.interval = var2;
      this.remainingTime = var2;
      this.regular = true;
      this.sleepUntil = System.currentTimeMillis();
      this.stopped = true;
      synchronized(this.getClass()) {
         if (timerThread == null) {
            timerThread = new TimerThread();
         }

      }
   }

   public synchronized boolean isStopped() {
      return this.stopped;
   }

   public void stop() {
      long var1 = System.currentTimeMillis();
      synchronized(timerThread) {
         synchronized(this) {
            if (!this.stopped) {
               TimerThread.dequeue(this);
               this.remainingTime = Math.max(0L, this.sleepUntil - var1);
               this.sleepUntil = var1;
               this.stopped = true;
            }
         }

      }
   }

   public void cont() {
      synchronized(timerThread) {
         synchronized(this) {
            if (this.stopped) {
               this.sleepUntil = Math.max(this.sleepUntil + 1L, System.currentTimeMillis() + this.remainingTime);
               TimerThread.enqueue(this);
               this.stopped = false;
            }
         }

      }
   }

   public void reset() {
      synchronized(timerThread) {
         synchronized(this) {
            this.setRemainingTime(this.interval);
         }

      }
   }

   public synchronized long getStopTime() {
      return this.sleepUntil;
   }

   public synchronized long getInterval() {
      return this.interval;
   }

   public synchronized void setInterval(long var1) {
      this.interval = var1;
   }

   public synchronized long getRemainingTime() {
      return this.remainingTime;
   }

   public void setRemainingTime(long var1) {
      synchronized(timerThread) {
         synchronized(this) {
            if (this.stopped) {
               this.remainingTime = var1;
            } else {
               this.stop();
               this.remainingTime = var1;
               this.cont();
            }
         }

      }
   }

   public synchronized void setRegular(boolean var1) {
      this.regular = var1;
   }

   protected Thread getTimerThread() {
      return TimerThread.timerThread;
   }
}
