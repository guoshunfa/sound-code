package java.awt;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import sun.awt.PeerEvent;
import sun.util.logging.PlatformLogger;

class WaitDispatchSupport implements SecondaryLoop {
   private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.event.WaitDispatchSupport");
   private EventDispatchThread dispatchThread;
   private EventFilter filter;
   private volatile Conditional extCondition;
   private volatile Conditional condition;
   private long interval;
   private static Timer timer;
   private TimerTask timerTask;
   private AtomicBoolean keepBlockingEDT;
   private AtomicBoolean keepBlockingCT;
   private AtomicBoolean afterExit;
   private final Runnable wakingRunnable;

   private static synchronized void initializeTimer() {
      if (timer == null) {
         timer = new Timer("AWT-WaitDispatchSupport-Timer", true);
      }

   }

   public WaitDispatchSupport(EventDispatchThread var1) {
      this(var1, (Conditional)null);
   }

   public WaitDispatchSupport(EventDispatchThread var1, Conditional var2) {
      this.keepBlockingEDT = new AtomicBoolean(false);
      this.keepBlockingCT = new AtomicBoolean(false);
      this.afterExit = new AtomicBoolean(false);
      this.wakingRunnable = new Runnable() {
         public void run() {
            WaitDispatchSupport.log.fine("Wake up EDT");
            synchronized(WaitDispatchSupport.getTreeLock()) {
               WaitDispatchSupport.this.keepBlockingCT.set(false);
               WaitDispatchSupport.getTreeLock().notifyAll();
            }

            WaitDispatchSupport.log.fine("Wake up EDT done");
         }
      };
      if (var1 == null) {
         throw new IllegalArgumentException("The dispatchThread can not be null");
      } else {
         this.dispatchThread = var1;
         this.extCondition = var2;
         this.condition = new Conditional() {
            public boolean evaluate() {
               if (WaitDispatchSupport.log.isLoggable(PlatformLogger.Level.FINEST)) {
                  WaitDispatchSupport.log.finest("evaluate(): blockingEDT=" + WaitDispatchSupport.this.keepBlockingEDT.get() + ", blockingCT=" + WaitDispatchSupport.this.keepBlockingCT.get());
               }

               boolean var1 = WaitDispatchSupport.this.extCondition != null ? WaitDispatchSupport.this.extCondition.evaluate() : true;
               if (WaitDispatchSupport.this.keepBlockingEDT.get() && var1) {
                  return true;
               } else {
                  if (WaitDispatchSupport.this.timerTask != null) {
                     WaitDispatchSupport.this.timerTask.cancel();
                     WaitDispatchSupport.this.timerTask = null;
                  }

                  return false;
               }
            }
         };
      }
   }

   public WaitDispatchSupport(EventDispatchThread var1, Conditional var2, EventFilter var3, long var4) {
      this(var1, var2);
      this.filter = var3;
      if (var4 < 0L) {
         throw new IllegalArgumentException("The interval value must be >= 0");
      } else {
         this.interval = var4;
         if (var4 != 0L) {
            initializeTimer();
         }

      }
   }

   public boolean enter() {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine("enter(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get());
      }

      if (!this.keepBlockingEDT.compareAndSet(false, true)) {
         log.fine("The secondary loop is already running, aborting");
         return false;
      } else {
         try {
            if (this.afterExit.get()) {
               log.fine("Exit was called already, aborting");
               boolean var22 = false;
               return var22;
            } else {
               final Runnable var1 = new Runnable() {
                  public void run() {
                     WaitDispatchSupport.log.fine("Starting a new event pump");
                     if (WaitDispatchSupport.this.filter == null) {
                        WaitDispatchSupport.this.dispatchThread.pumpEvents(WaitDispatchSupport.this.condition);
                     } else {
                        WaitDispatchSupport.this.dispatchThread.pumpEventsForFilter(WaitDispatchSupport.this.condition, WaitDispatchSupport.this.filter);
                     }

                  }
               };
               Thread var2 = Thread.currentThread();
               if (var2 == this.dispatchThread) {
                  if (log.isLoggable(PlatformLogger.Level.FINEST)) {
                     log.finest("On dispatch thread: " + this.dispatchThread);
                  }

                  if (this.interval != 0L) {
                     if (log.isLoggable(PlatformLogger.Level.FINEST)) {
                        log.finest("scheduling the timer for " + this.interval + " ms");
                     }

                     timer.schedule(this.timerTask = new TimerTask() {
                        public void run() {
                           if (WaitDispatchSupport.this.keepBlockingEDT.compareAndSet(true, false)) {
                              WaitDispatchSupport.this.wakeupEDT();
                           }

                        }
                     }, this.interval);
                  }

                  SequencedEvent var3 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent();
                  if (var3 != null) {
                     if (log.isLoggable(PlatformLogger.Level.FINE)) {
                        log.fine("Dispose current SequencedEvent: " + var3);
                     }

                     var3.dispose();
                  }

                  AccessController.doPrivileged(new PrivilegedAction<Void>() {
                     public Void run() {
                        var1.run();
                        return null;
                     }
                  });
               } else {
                  if (log.isLoggable(PlatformLogger.Level.FINEST)) {
                     log.finest("On non-dispatch thread: " + var2);
                  }

                  this.keepBlockingCT.set(true);
                  synchronized(getTreeLock()) {
                     if (this.afterExit.get()) {
                        boolean var24 = false;
                        return var24;
                     }

                     if (this.filter != null) {
                        this.dispatchThread.addEventFilter(this.filter);
                     }

                     try {
                        EventQueue var4 = this.dispatchThread.getEventQueue();
                        var4.postEvent(new PeerEvent(this, var1, 1L));
                        if (this.interval > 0L) {
                           long var5 = System.currentTimeMillis();

                           while(this.keepBlockingCT.get() && (this.extCondition == null || this.extCondition.evaluate()) && var5 + this.interval > System.currentTimeMillis()) {
                              getTreeLock().wait(this.interval);
                           }
                        } else {
                           while(this.keepBlockingCT.get() && (this.extCondition == null || this.extCondition.evaluate())) {
                              getTreeLock().wait();
                           }
                        }

                        if (log.isLoggable(PlatformLogger.Level.FINE)) {
                           log.fine("waitDone " + this.keepBlockingEDT.get() + " " + this.keepBlockingCT.get());
                        }
                     } catch (InterruptedException var18) {
                        if (log.isLoggable(PlatformLogger.Level.FINE)) {
                           log.fine("Exception caught while waiting: " + var18);
                        }
                     } finally {
                        if (this.filter != null) {
                           this.dispatchThread.removeEventFilter(this.filter);
                        }

                     }
                  }
               }

               boolean var23 = true;
               return var23;
            }
         } finally {
            this.keepBlockingEDT.set(false);
            this.keepBlockingCT.set(false);
            this.afterExit.set(false);
         }
      }
   }

   public boolean exit() {
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine("exit(): blockingEDT=" + this.keepBlockingEDT.get() + ", blockingCT=" + this.keepBlockingCT.get());
      }

      this.afterExit.set(true);
      if (this.keepBlockingEDT.getAndSet(false)) {
         this.wakeupEDT();
         return true;
      } else {
         return false;
      }
   }

   private static final Object getTreeLock() {
      return Component.LOCK;
   }

   private void wakeupEDT() {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
         log.finest("wakeupEDT(): EDT == " + this.dispatchThread);
      }

      EventQueue var1 = this.dispatchThread.getEventQueue();
      var1.postEvent(new PeerEvent(this, this.wakingRunnable, 1L));
   }
}
