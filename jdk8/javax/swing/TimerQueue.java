package javax.swing;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import sun.awt.AppContext;

class TimerQueue implements Runnable {
   private static final Object sharedInstanceKey = new StringBuffer("TimerQueue.sharedInstanceKey");
   private static final Object expiredTimersKey = new StringBuffer("TimerQueue.expiredTimersKey");
   private final DelayQueue<TimerQueue.DelayedTimer> queue = new DelayQueue();
   private volatile boolean running;
   private final Lock runningLock = new ReentrantLock();
   private static final Object classLock = new Object();
   private static final long NANO_ORIGIN = System.nanoTime();

   public TimerQueue() {
      this.startIfNeeded();
   }

   public static TimerQueue sharedInstance() {
      synchronized(classLock) {
         TimerQueue var1 = (TimerQueue)SwingUtilities.appContextGet(sharedInstanceKey);
         if (var1 == null) {
            var1 = new TimerQueue();
            SwingUtilities.appContextPut(sharedInstanceKey, var1);
         }

         return var1;
      }
   }

   void startIfNeeded() {
      if (!this.running) {
         this.runningLock.lock();
         if (this.running) {
            return;
         }

         try {
            final ThreadGroup var1 = AppContext.getAppContext().getThreadGroup();
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  Thread var1x = new Thread(var1, TimerQueue.this, "TimerQueue");
                  var1x.setDaemon(true);
                  var1x.setPriority(5);
                  var1x.start();
                  return null;
               }
            });
            this.running = true;
         } finally {
            this.runningLock.unlock();
         }
      }

   }

   void addTimer(Timer var1, long var2) {
      var1.getLock().lock();

      try {
         if (!this.containsTimer(var1)) {
            this.addTimer(new TimerQueue.DelayedTimer(var1, TimeUnit.MILLISECONDS.toNanos(var2) + now()));
         }
      } finally {
         var1.getLock().unlock();
      }

   }

   private void addTimer(TimerQueue.DelayedTimer var1) {
      assert var1 != null && !this.containsTimer(var1.getTimer());

      Timer var2 = var1.getTimer();
      var2.getLock().lock();

      try {
         var2.delayedTimer = var1;
         this.queue.add((Delayed)var1);
      } finally {
         var2.getLock().unlock();
      }

   }

   void removeTimer(Timer var1) {
      var1.getLock().lock();

      try {
         if (var1.delayedTimer != null) {
            this.queue.remove(var1.delayedTimer);
            var1.delayedTimer = null;
         }
      } finally {
         var1.getLock().unlock();
      }

   }

   boolean containsTimer(Timer var1) {
      var1.getLock().lock();

      boolean var2;
      try {
         var2 = var1.delayedTimer != null;
      } finally {
         var1.getLock().unlock();
      }

      return var2;
   }

   public void run() {
      this.runningLock.lock();

      try {
         TimerQueue.DelayedTimer var3;
         try {
            while(this.running) {
               try {
                  TimerQueue.DelayedTimer var1 = (TimerQueue.DelayedTimer)this.queue.take();
                  Timer var21 = var1.getTimer();
                  var21.getLock().lock();

                  try {
                     var3 = var21.delayedTimer;
                     if (var3 == var1) {
                        var21.post();
                        var21.delayedTimer = null;
                        if (var21.isRepeats()) {
                           var3.setTime(now() + TimeUnit.MILLISECONDS.toNanos((long)var21.getDelay()));
                           this.addTimer(var3);
                        }
                     }

                     var21.getLock().newCondition().awaitNanos(1L);
                  } catch (SecurityException var16) {
                  } finally {
                     var21.getLock().unlock();
                  }
               } catch (InterruptedException var18) {
                  if (AppContext.getAppContext().isDisposed()) {
                     break;
                  }
               }
            }
         } catch (ThreadDeath var19) {
            Iterator var2 = this.queue.iterator();

            while(var2.hasNext()) {
               var3 = (TimerQueue.DelayedTimer)var2.next();
               var3.getTimer().cancelEvent();
            }

            throw var19;
         }
      } finally {
         this.running = false;
         this.runningLock.unlock();
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("TimerQueue (");
      boolean var2 = true;

      for(Iterator var3 = this.queue.iterator(); var3.hasNext(); var2 = false) {
         TimerQueue.DelayedTimer var4 = (TimerQueue.DelayedTimer)var3.next();
         if (!var2) {
            var1.append(", ");
         }

         var1.append(var4.getTimer().toString());
      }

      var1.append(")");
      return var1.toString();
   }

   private static long now() {
      return System.nanoTime() - NANO_ORIGIN;
   }

   static class DelayedTimer implements Delayed {
      private static final AtomicLong sequencer = new AtomicLong(0L);
      private final long sequenceNumber;
      private volatile long time;
      private final Timer timer;

      DelayedTimer(Timer var1, long var2) {
         this.timer = var1;
         this.time = var2;
         this.sequenceNumber = sequencer.getAndIncrement();
      }

      public final long getDelay(TimeUnit var1) {
         return var1.convert(this.time - TimerQueue.now(), TimeUnit.NANOSECONDS);
      }

      final void setTime(long var1) {
         this.time = var1;
      }

      final Timer getTimer() {
         return this.timer;
      }

      public int compareTo(Delayed var1) {
         if (var1 == this) {
            return 0;
         } else if (var1 instanceof TimerQueue.DelayedTimer) {
            TimerQueue.DelayedTimer var5 = (TimerQueue.DelayedTimer)var1;
            long var3 = this.time - var5.time;
            if (var3 < 0L) {
               return -1;
            } else if (var3 > 0L) {
               return 1;
            } else {
               return this.sequenceNumber < var5.sequenceNumber ? -1 : 1;
            }
         } else {
            long var2 = this.getDelay(TimeUnit.NANOSECONDS) - var1.getDelay(TimeUnit.NANOSECONDS);
            return var2 == 0L ? 0 : (var2 < 0L ? -1 : 1);
         }
      }
   }
}
