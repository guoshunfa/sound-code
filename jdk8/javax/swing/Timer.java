package javax.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EventListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.event.EventListenerList;

public class Timer implements Serializable {
   protected EventListenerList listenerList = new EventListenerList();
   private final transient AtomicBoolean notify = new AtomicBoolean(false);
   private volatile int initialDelay;
   private volatile int delay;
   private volatile boolean repeats = true;
   private volatile boolean coalesce = true;
   private final transient Runnable doPostEvent;
   private static volatile boolean logTimers;
   private final transient Lock lock = new ReentrantLock();
   transient TimerQueue.DelayedTimer delayedTimer = null;
   private volatile String actionCommand;
   private transient volatile AccessControlContext acc = AccessController.getContext();

   public Timer(int var1, ActionListener var2) {
      this.delay = var1;
      this.initialDelay = var1;
      this.doPostEvent = new Timer.DoPostEvent();
      if (var2 != null) {
         this.addActionListener(var2);
      }

   }

   final AccessControlContext getAccessControlContext() {
      if (this.acc == null) {
         throw new SecurityException("Timer is missing AccessControlContext");
      } else {
         return this.acc;
      }
   }

   public void addActionListener(ActionListener var1) {
      this.listenerList.add(ActionListener.class, var1);
   }

   public void removeActionListener(ActionListener var1) {
      this.listenerList.remove(ActionListener.class, var1);
   }

   public ActionListener[] getActionListeners() {
      return (ActionListener[])this.listenerList.getListeners(ActionListener.class);
   }

   protected void fireActionPerformed(ActionEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == ActionListener.class) {
            ((ActionListener)var2[var3 + 1]).actionPerformed(var1);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   private TimerQueue timerQueue() {
      return TimerQueue.sharedInstance();
   }

   public static void setLogTimers(boolean var0) {
      logTimers = var0;
   }

   public static boolean getLogTimers() {
      return logTimers;
   }

   public void setDelay(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Invalid delay: " + var1);
      } else {
         this.delay = var1;
      }
   }

   public int getDelay() {
      return this.delay;
   }

   public void setInitialDelay(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Invalid initial delay: " + var1);
      } else {
         this.initialDelay = var1;
      }
   }

   public int getInitialDelay() {
      return this.initialDelay;
   }

   public void setRepeats(boolean var1) {
      this.repeats = var1;
   }

   public boolean isRepeats() {
      return this.repeats;
   }

   public void setCoalesce(boolean var1) {
      boolean var2 = this.coalesce;
      this.coalesce = var1;
      if (!var2 && this.coalesce) {
         this.cancelEvent();
      }

   }

   public boolean isCoalesce() {
      return this.coalesce;
   }

   public void setActionCommand(String var1) {
      this.actionCommand = var1;
   }

   public String getActionCommand() {
      return this.actionCommand;
   }

   public void start() {
      this.timerQueue().addTimer(this, (long)this.getInitialDelay());
   }

   public boolean isRunning() {
      return this.timerQueue().containsTimer(this);
   }

   public void stop() {
      this.getLock().lock();

      try {
         this.cancelEvent();
         this.timerQueue().removeTimer(this);
      } finally {
         this.getLock().unlock();
      }

   }

   public void restart() {
      this.getLock().lock();

      try {
         this.stop();
         this.start();
      } finally {
         this.getLock().unlock();
      }

   }

   void cancelEvent() {
      this.notify.set(false);
   }

   void post() {
      if (this.notify.compareAndSet(false, true) || !this.coalesce) {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               SwingUtilities.invokeLater(Timer.this.doPostEvent);
               return null;
            }
         }, this.getAccessControlContext());
      }

   }

   Lock getLock() {
      return this.lock;
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      this.acc = AccessController.getContext();
      var1.defaultReadObject();
   }

   private Object readResolve() {
      Timer var1 = new Timer(this.getDelay(), (ActionListener)null);
      var1.listenerList = this.listenerList;
      var1.initialDelay = this.initialDelay;
      var1.delay = this.delay;
      var1.repeats = this.repeats;
      var1.coalesce = this.coalesce;
      var1.actionCommand = this.actionCommand;
      return var1;
   }

   class DoPostEvent implements Runnable {
      public void run() {
         if (Timer.logTimers) {
            System.out.println("Timer ringing: " + Timer.this);
         }

         if (Timer.this.notify.get()) {
            Timer.this.fireActionPerformed(new ActionEvent(Timer.this, 0, Timer.this.getActionCommand(), System.currentTimeMillis(), 0));
            if (Timer.this.coalesce) {
               Timer.this.cancelEvent();
            }
         }

      }

      Timer getTimer() {
         return Timer.this;
      }
   }
}
