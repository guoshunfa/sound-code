package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.ComponentPeer;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import sun.awt.AWTAccessor;
import sun.awt.AWTAutoShutdown;
import sun.awt.AppContext;
import sun.awt.EventQueueItem;
import sun.awt.FwDispatcher;
import sun.awt.PeerEvent;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetEvent;
import sun.misc.JavaSecurityAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public class EventQueue {
   private static final AtomicInteger threadInitNumber = new AtomicInteger(0);
   private static final int LOW_PRIORITY = 0;
   private static final int NORM_PRIORITY = 1;
   private static final int HIGH_PRIORITY = 2;
   private static final int ULTIMATE_PRIORITY = 3;
   private static final int NUM_PRIORITIES = 4;
   private Queue[] queues = new Queue[4];
   private EventQueue nextQueue;
   private EventQueue previousQueue;
   private final Lock pushPopLock;
   private final Condition pushPopCond;
   private static final Runnable dummyRunnable = new Runnable() {
      public void run() {
      }
   };
   private EventDispatchThread dispatchThread;
   private final ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
   private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
   private long mostRecentEventTime = System.currentTimeMillis();
   private long mostRecentKeyEventTime = System.currentTimeMillis();
   private WeakReference<AWTEvent> currentEvent;
   private volatile int waitForID;
   private final AppContext appContext;
   private final String name;
   private FwDispatcher fwDispatcher;
   private static volatile PlatformLogger eventLog;
   private static final int PAINT = 0;
   private static final int UPDATE = 1;
   private static final int MOVE = 2;
   private static final int DRAG = 3;
   private static final int PEER = 4;
   private static final int CACHE_LENGTH = 5;
   private static final JavaSecurityAccess javaSecurityAccess;

   private static final PlatformLogger getEventLog() {
      if (eventLog == null) {
         eventLog = PlatformLogger.getLogger("java.awt.event.EventQueue");
      }

      return eventLog;
   }

   public EventQueue() {
      this.name = "AWT-EventQueue-" + threadInitNumber.getAndIncrement();

      for(int var1 = 0; var1 < 4; ++var1) {
         this.queues[var1] = new Queue();
      }

      this.appContext = AppContext.getAppContext();
      this.pushPopLock = (Lock)this.appContext.get(AppContext.EVENT_QUEUE_LOCK_KEY);
      this.pushPopCond = (Condition)this.appContext.get(AppContext.EVENT_QUEUE_COND_KEY);
   }

   public void postEvent(AWTEvent var1) {
      SunToolkit.flushPendingEvents(this.appContext);
      this.postEventPrivate(var1);
   }

   private final void postEventPrivate(AWTEvent var1) {
      var1.isPosted = true;
      this.pushPopLock.lock();

      try {
         if (this.nextQueue != null) {
            this.nextQueue.postEventPrivate(var1);
         } else {
            if (this.dispatchThread == null) {
               if (var1.getSource() == AWTAutoShutdown.getInstance()) {
                  return;
               }

               this.initDispatchThread();
            }

            this.postEvent(var1, getPriority(var1));
         }
      } finally {
         this.pushPopLock.unlock();
      }
   }

   private static int getPriority(AWTEvent var0) {
      if (var0 instanceof PeerEvent) {
         PeerEvent var1 = (PeerEvent)var0;
         if ((var1.getFlags() & 2L) != 0L) {
            return 3;
         }

         if ((var1.getFlags() & 1L) != 0L) {
            return 2;
         }

         if ((var1.getFlags() & 4L) != 0L) {
            return 0;
         }
      }

      int var2 = var0.getID();
      return var2 >= 800 && var2 <= 801 ? 0 : 1;
   }

   private void postEvent(AWTEvent var1, int var2) {
      if (!this.coalesceEvent(var1, var2)) {
         EventQueueItem var3 = new EventQueueItem(var1);
         this.cacheEQItem(var3);
         boolean var4 = var1.getID() == this.waitForID;
         if (this.queues[var2].head == null) {
            boolean var5 = this.noEvents();
            this.queues[var2].head = this.queues[var2].tail = var3;
            if (var5) {
               if (var1.getSource() != AWTAutoShutdown.getInstance()) {
                  AWTAutoShutdown.getInstance().notifyThreadBusy(this.dispatchThread);
               }

               this.pushPopCond.signalAll();
            } else if (var4) {
               this.pushPopCond.signalAll();
            }
         } else {
            this.queues[var2].tail.next = var3;
            this.queues[var2].tail = var3;
            if (var4) {
               this.pushPopCond.signalAll();
            }
         }

      }
   }

   private boolean coalescePaintEvent(PaintEvent var1) {
      ComponentPeer var2 = ((Component)var1.getSource()).peer;
      if (var2 != null) {
         var2.coalescePaintEvent(var1);
      }

      EventQueueItem[] var3 = ((Component)var1.getSource()).eventCache;
      if (var3 == null) {
         return false;
      } else {
         int var4 = eventToCacheIndex(var1);
         if (var4 != -1 && var3[var4] != null) {
            PaintEvent var5 = this.mergePaintEvents(var1, (PaintEvent)var3[var4].event);
            if (var5 != null) {
               var3[var4].event = var5;
               return true;
            }
         }

         return false;
      }
   }

   private PaintEvent mergePaintEvents(PaintEvent var1, PaintEvent var2) {
      Rectangle var3 = var1.getUpdateRect();
      Rectangle var4 = var2.getUpdateRect();
      if (var4.contains(var3)) {
         return var2;
      } else {
         return var3.contains(var4) ? var1 : null;
      }
   }

   private boolean coalesceMouseEvent(MouseEvent var1) {
      EventQueueItem[] var2 = ((Component)var1.getSource()).eventCache;
      if (var2 == null) {
         return false;
      } else {
         int var3 = eventToCacheIndex(var1);
         if (var3 != -1 && var2[var3] != null) {
            var2[var3].event = var1;
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean coalescePeerEvent(PeerEvent var1) {
      EventQueueItem[] var2 = ((Component)var1.getSource()).eventCache;
      if (var2 == null) {
         return false;
      } else {
         int var3 = eventToCacheIndex(var1);
         if (var3 != -1 && var2[var3] != null) {
            var1 = var1.coalesceEvents((PeerEvent)var2[var3].event);
            if (var1 != null) {
               var2[var3].event = var1;
               return true;
            }

            var2[var3] = null;
         }

         return false;
      }
   }

   private boolean coalesceOtherEvent(AWTEvent var1, int var2) {
      int var3 = var1.getID();
      Component var4 = (Component)var1.getSource();

      for(EventQueueItem var5 = this.queues[var2].head; var5 != null; var5 = var5.next) {
         if (var5.event.getSource() == var4 && var5.event.getID() == var3) {
            AWTEvent var6 = var4.coalesceEvents(var5.event, var1);
            if (var6 != null) {
               var5.event = var6;
               return true;
            }
         }
      }

      return false;
   }

   private boolean coalesceEvent(AWTEvent var1, int var2) {
      if (!(var1.getSource() instanceof Component)) {
         return false;
      } else if (var1 instanceof PeerEvent) {
         return this.coalescePeerEvent((PeerEvent)var1);
      } else if (((Component)var1.getSource()).isCoalescingEnabled() && this.coalesceOtherEvent(var1, var2)) {
         return true;
      } else if (var1 instanceof PaintEvent) {
         return this.coalescePaintEvent((PaintEvent)var1);
      } else {
         return var1 instanceof MouseEvent ? this.coalesceMouseEvent((MouseEvent)var1) : false;
      }
   }

   private void cacheEQItem(EventQueueItem var1) {
      int var2 = eventToCacheIndex(var1.event);
      if (var2 != -1 && var1.event.getSource() instanceof Component) {
         Component var3 = (Component)var1.event.getSource();
         if (var3.eventCache == null) {
            var3.eventCache = new EventQueueItem[5];
         }

         var3.eventCache[var2] = var1;
      }

   }

   private void uncacheEQItem(EventQueueItem var1) {
      int var2 = eventToCacheIndex(var1.event);
      if (var2 != -1 && var1.event.getSource() instanceof Component) {
         Component var3 = (Component)var1.event.getSource();
         if (var3.eventCache == null) {
            return;
         }

         var3.eventCache[var2] = null;
      }

   }

   private static int eventToCacheIndex(AWTEvent var0) {
      switch(var0.getID()) {
      case 503:
         return 2;
      case 506:
         return var0 instanceof SunDropTargetEvent ? -1 : 3;
      case 800:
         return 0;
      case 801:
         return 1;
      default:
         return var0 instanceof PeerEvent ? 4 : -1;
      }
   }

   private boolean noEvents() {
      for(int var1 = 0; var1 < 4; ++var1) {
         if (this.queues[var1].head != null) {
            return false;
         }
      }

      return true;
   }

   public AWTEvent getNextEvent() throws InterruptedException {
      while(true) {
         SunToolkit.flushPendingEvents(this.appContext);
         this.pushPopLock.lock();

         AWTEvent var2;
         try {
            AWTEvent var1 = this.getNextEventPrivate();
            if (var1 == null) {
               AWTAutoShutdown.getInstance().notifyThreadFree(this.dispatchThread);
               this.pushPopCond.await();
               continue;
            }

            var2 = var1;
         } finally {
            this.pushPopLock.unlock();
         }

         return var2;
      }
   }

   AWTEvent getNextEventPrivate() throws InterruptedException {
      for(int var1 = 3; var1 >= 0; --var1) {
         if (this.queues[var1].head != null) {
            EventQueueItem var2 = this.queues[var1].head;
            this.queues[var1].head = var2.next;
            if (var2.next == null) {
               this.queues[var1].tail = null;
            }

            this.uncacheEQItem(var2);
            return var2.event;
         }
      }

      return null;
   }

   AWTEvent getNextEvent(int var1) throws InterruptedException {
      while(true) {
         SunToolkit.flushPendingEvents(this.appContext);
         this.pushPopLock.lock();

         try {
            for(int var2 = 0; var2 < 4; ++var2) {
               EventQueueItem var3 = this.queues[var2].head;

               for(EventQueueItem var4 = null; var3 != null; var3 = var3.next) {
                  if (var3.event.getID() == var1) {
                     if (var4 == null) {
                        this.queues[var2].head = var3.next;
                     } else {
                        var4.next = var3.next;
                     }

                     if (this.queues[var2].tail == var3) {
                        this.queues[var2].tail = var4;
                     }

                     this.uncacheEQItem(var3);
                     AWTEvent var5 = var3.event;
                     return var5;
                  }

                  var4 = var3;
               }
            }

            this.waitForID = var1;
            this.pushPopCond.await();
            this.waitForID = 0;
         } finally {
            this.pushPopLock.unlock();
         }
      }
   }

   public AWTEvent peekEvent() {
      this.pushPopLock.lock();

      try {
         for(int var1 = 3; var1 >= 0; --var1) {
            if (this.queues[var1].head != null) {
               AWTEvent var2 = this.queues[var1].head.event;
               return var2;
            }
         }
      } finally {
         this.pushPopLock.unlock();
      }

      return null;
   }

   public AWTEvent peekEvent(int var1) {
      this.pushPopLock.lock();

      try {
         for(int var2 = 3; var2 >= 0; --var2) {
            for(EventQueueItem var3 = this.queues[var2].head; var3 != null; var3 = var3.next) {
               if (var3.event.getID() == var1) {
                  AWTEvent var4 = var3.event;
                  return var4;
               }
            }
         }
      } finally {
         this.pushPopLock.unlock();
      }

      return null;
   }

   protected void dispatchEvent(final AWTEvent var1) {
      final Object var2 = var1.getSource();
      final PrivilegedAction var3 = new PrivilegedAction<Void>() {
         public Void run() {
            if (EventQueue.this.fwDispatcher != null && !EventQueue.this.isDispatchThreadImpl()) {
               EventQueue.this.fwDispatcher.scheduleDispatch(new Runnable() {
                  public void run() {
                     if (EventQueue.this.dispatchThread.filterAndCheckEvent(var1)) {
                        EventQueue.this.dispatchEventImpl(var1, var2);
                     }

                  }
               });
            } else {
               EventQueue.this.dispatchEventImpl(var1, var2);
            }

            return null;
         }
      };
      AccessControlContext var4 = AccessController.getContext();
      AccessControlContext var5 = getAccessControlContextFrom(var2);
      final AccessControlContext var6 = var1.getAccessControlContext();
      if (var5 == null) {
         javaSecurityAccess.doIntersectionPrivilege(var3, var4, var6);
      } else {
         javaSecurityAccess.doIntersectionPrivilege(new PrivilegedAction<Void>() {
            public Void run() {
               EventQueue.javaSecurityAccess.doIntersectionPrivilege(var3, var6);
               return null;
            }
         }, var4, var5);
      }

   }

   private static AccessControlContext getAccessControlContextFrom(Object var0) {
      return var0 instanceof Component ? ((Component)var0).getAccessControlContext() : (var0 instanceof MenuComponent ? ((MenuComponent)var0).getAccessControlContext() : (var0 instanceof TrayIcon ? ((TrayIcon)var0).getAccessControlContext() : null));
   }

   private void dispatchEventImpl(AWTEvent var1, Object var2) {
      var1.isPosted = true;
      if (var1 instanceof ActiveEvent) {
         this.setCurrentEventAndMostRecentTimeImpl(var1);
         ((ActiveEvent)var1).dispatch();
      } else if (var2 instanceof Component) {
         ((Component)var2).dispatchEvent(var1);
         var1.dispatched();
      } else if (var2 instanceof MenuComponent) {
         ((MenuComponent)var2).dispatchEvent(var1);
      } else if (var2 instanceof TrayIcon) {
         ((TrayIcon)var2).dispatchEvent(var1);
      } else if (var2 instanceof AWTAutoShutdown) {
         if (this.noEvents()) {
            this.dispatchThread.stopDispatching();
         }
      } else if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
         getEventLog().fine("Unable to dispatch event: " + var1);
      }

   }

   public static long getMostRecentEventTime() {
      return Toolkit.getEventQueue().getMostRecentEventTimeImpl();
   }

   private long getMostRecentEventTimeImpl() {
      this.pushPopLock.lock();

      long var1;
      try {
         var1 = Thread.currentThread() == this.dispatchThread ? this.mostRecentEventTime : System.currentTimeMillis();
      } finally {
         this.pushPopLock.unlock();
      }

      return var1;
   }

   long getMostRecentEventTimeEx() {
      this.pushPopLock.lock();

      long var1;
      try {
         var1 = this.mostRecentEventTime;
      } finally {
         this.pushPopLock.unlock();
      }

      return var1;
   }

   public static AWTEvent getCurrentEvent() {
      return Toolkit.getEventQueue().getCurrentEventImpl();
   }

   private AWTEvent getCurrentEventImpl() {
      this.pushPopLock.lock();

      AWTEvent var1;
      try {
         var1 = Thread.currentThread() == this.dispatchThread ? (AWTEvent)this.currentEvent.get() : null;
      } finally {
         this.pushPopLock.unlock();
      }

      return var1;
   }

   public void push(EventQueue var1) {
      if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
         getEventLog().fine("EventQueue.push(" + var1 + ")");
      }

      this.pushPopLock.lock();

      try {
         EventQueue var2;
         for(var2 = this; var2.nextQueue != null; var2 = var2.nextQueue) {
         }

         if (var2.fwDispatcher != null) {
            throw new RuntimeException("push() to queue with fwDispatcher");
         }

         if (var2.dispatchThread != null && var2.dispatchThread.getEventQueue() == this) {
            var1.dispatchThread = var2.dispatchThread;
            var2.dispatchThread.setEventQueue(var1);
         }

         while(var2.peekEvent() != null) {
            try {
               var1.postEventPrivate(var2.getNextEventPrivate());
            } catch (InterruptedException var7) {
               if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
                  getEventLog().fine("Interrupted push", (Throwable)var7);
               }
            }
         }

         if (var2.dispatchThread != null) {
            var2.postEventPrivate(new InvocationEvent(var2, dummyRunnable));
         }

         var1.previousQueue = var2;
         var2.nextQueue = var1;
         if (this.appContext.get(AppContext.EVENT_QUEUE_KEY) == var2) {
            this.appContext.put(AppContext.EVENT_QUEUE_KEY, var1);
         }

         this.pushPopCond.signalAll();
      } finally {
         this.pushPopLock.unlock();
      }

   }

   protected void pop() throws EmptyStackException {
      if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
         getEventLog().fine("EventQueue.pop(" + this + ")");
      }

      this.pushPopLock.lock();

      try {
         EventQueue var1;
         for(var1 = this; var1.nextQueue != null; var1 = var1.nextQueue) {
         }

         EventQueue var2 = var1.previousQueue;
         if (var2 == null) {
            throw new EmptyStackException();
         } else {
            var1.previousQueue = null;
            var2.nextQueue = null;

            while(var1.peekEvent() != null) {
               try {
                  var2.postEventPrivate(var1.getNextEventPrivate());
               } catch (InterruptedException var7) {
                  if (getEventLog().isLoggable(PlatformLogger.Level.FINE)) {
                     getEventLog().fine("Interrupted pop", (Throwable)var7);
                  }
               }
            }

            if (var1.dispatchThread != null && var1.dispatchThread.getEventQueue() == this) {
               var2.dispatchThread = var1.dispatchThread;
               var1.dispatchThread.setEventQueue(var2);
            }

            if (this.appContext.get(AppContext.EVENT_QUEUE_KEY) == this) {
               this.appContext.put(AppContext.EVENT_QUEUE_KEY, var2);
            }

            var1.postEventPrivate(new InvocationEvent(var1, dummyRunnable));
            this.pushPopCond.signalAll();
         }
      } finally {
         this.pushPopLock.unlock();
      }
   }

   public SecondaryLoop createSecondaryLoop() {
      return this.createSecondaryLoop((Conditional)null, (EventFilter)null, 0L);
   }

   SecondaryLoop createSecondaryLoop(Conditional var1, EventFilter var2, long var3) {
      this.pushPopLock.lock();

      EventQueue.FwSecondaryLoopWrapper var5;
      try {
         if (this.nextQueue != null) {
            SecondaryLoop var10 = this.nextQueue.createSecondaryLoop(var1, var2, var3);
            return var10;
         }

         if (this.fwDispatcher == null) {
            if (this.dispatchThread == null) {
               this.initDispatchThread();
            }

            WaitDispatchSupport var9 = new WaitDispatchSupport(this.dispatchThread, var1, var2, var3);
            return var9;
         }

         var5 = new EventQueue.FwSecondaryLoopWrapper(this.fwDispatcher.createSecondaryLoop(), var2);
      } finally {
         this.pushPopLock.unlock();
      }

      return var5;
   }

   public static boolean isDispatchThread() {
      EventQueue var0 = Toolkit.getEventQueue();
      return var0.isDispatchThreadImpl();
   }

   final boolean isDispatchThreadImpl() {
      EventQueue var1 = this;
      this.pushPopLock.lock();

      boolean var3;
      try {
         for(EventQueue var2 = var1.nextQueue; var2 != null; var2 = var2.nextQueue) {
            var1 = var2;
         }

         if (var1.fwDispatcher == null) {
            var3 = Thread.currentThread() == var1.dispatchThread;
            return var3;
         }

         var3 = var1.fwDispatcher.isDispatchThread();
      } finally {
         this.pushPopLock.unlock();
      }

      return var3;
   }

   final void initDispatchThread() {
      this.pushPopLock.lock();

      try {
         if (this.dispatchThread == null && !this.threadGroup.isDestroyed() && !this.appContext.isDisposed()) {
            this.dispatchThread = (EventDispatchThread)AccessController.doPrivileged(new PrivilegedAction<EventDispatchThread>() {
               public EventDispatchThread run() {
                  EventDispatchThread var1 = new EventDispatchThread(EventQueue.this.threadGroup, EventQueue.this.name, EventQueue.this);
                  var1.setContextClassLoader(EventQueue.this.classLoader);
                  var1.setPriority(6);
                  var1.setDaemon(false);
                  AWTAutoShutdown.getInstance().notifyThreadBusy(var1);
                  return var1;
               }
            });
            this.dispatchThread.start();
         }
      } finally {
         this.pushPopLock.unlock();
      }

   }

   final void detachDispatchThread(EventDispatchThread var1) {
      SunToolkit.flushPendingEvents(this.appContext);
      this.pushPopLock.lock();

      try {
         if (var1 == this.dispatchThread) {
            this.dispatchThread = null;
         }

         AWTAutoShutdown.getInstance().notifyThreadFree(var1);
         if (this.peekEvent() != null) {
            this.initDispatchThread();
         }
      } finally {
         this.pushPopLock.unlock();
      }

   }

   final EventDispatchThread getDispatchThread() {
      this.pushPopLock.lock();

      EventDispatchThread var1;
      try {
         var1 = this.dispatchThread;
      } finally {
         this.pushPopLock.unlock();
      }

      return var1;
   }

   final void removeSourceEvents(Object var1, boolean var2) {
      SunToolkit.flushPendingEvents(this.appContext);
      this.pushPopLock.lock();

      try {
         for(int var3 = 0; var3 < 4; ++var3) {
            EventQueueItem var4 = this.queues[var3].head;

            EventQueueItem var5;
            for(var5 = null; var4 != null; var4 = var4.next) {
               if (var4.event.getSource() != var1 || !var2 && (var4.event instanceof SequencedEvent || var4.event instanceof SentEvent || var4.event instanceof FocusEvent || var4.event instanceof WindowEvent || var4.event instanceof KeyEvent || var4.event instanceof InputMethodEvent)) {
                  var5 = var4;
               } else {
                  if (var4.event instanceof SequencedEvent) {
                     ((SequencedEvent)var4.event).dispose();
                  }

                  if (var4.event instanceof SentEvent) {
                     ((SentEvent)var4.event).dispose();
                  }

                  if (var4.event instanceof InvocationEvent) {
                     AWTAccessor.getInvocationEventAccessor().dispose((InvocationEvent)var4.event);
                  }

                  if (var5 == null) {
                     this.queues[var3].head = var4.next;
                  } else {
                     var5.next = var4.next;
                  }

                  this.uncacheEQItem(var4);
               }
            }

            this.queues[var3].tail = var5;
         }
      } finally {
         this.pushPopLock.unlock();
      }

   }

   synchronized long getMostRecentKeyEventTime() {
      this.pushPopLock.lock();

      long var1;
      try {
         var1 = this.mostRecentKeyEventTime;
      } finally {
         this.pushPopLock.unlock();
      }

      return var1;
   }

   static void setCurrentEventAndMostRecentTime(AWTEvent var0) {
      Toolkit.getEventQueue().setCurrentEventAndMostRecentTimeImpl(var0);
   }

   private void setCurrentEventAndMostRecentTimeImpl(AWTEvent var1) {
      this.pushPopLock.lock();

      try {
         if (Thread.currentThread() != this.dispatchThread) {
            return;
         }

         this.currentEvent = new WeakReference(var1);
         long var2 = Long.MIN_VALUE;
         if (var1 instanceof InputEvent) {
            InputEvent var4 = (InputEvent)var1;
            var2 = var4.getWhen();
            if (var1 instanceof KeyEvent) {
               this.mostRecentKeyEventTime = var4.getWhen();
            }
         } else if (var1 instanceof InputMethodEvent) {
            InputMethodEvent var8 = (InputMethodEvent)var1;
            var2 = var8.getWhen();
         } else if (var1 instanceof ActionEvent) {
            ActionEvent var9 = (ActionEvent)var1;
            var2 = var9.getWhen();
         } else if (var1 instanceof InvocationEvent) {
            InvocationEvent var10 = (InvocationEvent)var1;
            var2 = var10.getWhen();
         }

         this.mostRecentEventTime = Math.max(this.mostRecentEventTime, var2);
      } finally {
         this.pushPopLock.unlock();
      }

   }

   public static void invokeLater(Runnable var0) {
      Toolkit.getEventQueue().postEvent(new InvocationEvent(Toolkit.getDefaultToolkit(), var0));
   }

   public static void invokeAndWait(Runnable var0) throws InterruptedException, InvocationTargetException {
      invokeAndWait(Toolkit.getDefaultToolkit(), var0);
   }

   static void invokeAndWait(Object var0, Runnable var1) throws InterruptedException, InvocationTargetException {
      if (isDispatchThread()) {
         throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
      } else {
         class AWTInvocationLock {
         }

         AWTInvocationLock var2 = new AWTInvocationLock();
         InvocationEvent var3 = new InvocationEvent(var0, var1, var2, true);
         synchronized(var2) {
            Toolkit.getEventQueue().postEvent(var3);

            while(true) {
               if (var3.isDispatched()) {
                  break;
               }

               var2.wait();
            }
         }

         Throwable var4 = var3.getThrowable();
         if (var4 != null) {
            throw new InvocationTargetException(var4);
         }
      }
   }

   private void wakeup(boolean var1) {
      this.pushPopLock.lock();

      try {
         if (this.nextQueue != null) {
            this.nextQueue.wakeup(var1);
         } else if (this.dispatchThread != null) {
            this.pushPopCond.signalAll();
         } else if (!var1) {
            this.initDispatchThread();
         }
      } finally {
         this.pushPopLock.unlock();
      }

   }

   private void setFwDispatcher(FwDispatcher var1) {
      if (this.nextQueue != null) {
         this.nextQueue.setFwDispatcher(var1);
      } else {
         this.fwDispatcher = var1;
      }

   }

   static {
      AWTAccessor.setEventQueueAccessor(new AWTAccessor.EventQueueAccessor() {
         public Thread getDispatchThread(EventQueue var1) {
            return var1.getDispatchThread();
         }

         public boolean isDispatchThreadImpl(EventQueue var1) {
            return var1.isDispatchThreadImpl();
         }

         public void removeSourceEvents(EventQueue var1, Object var2, boolean var3) {
            var1.removeSourceEvents(var2, var3);
         }

         public boolean noEvents(EventQueue var1) {
            return var1.noEvents();
         }

         public void wakeup(EventQueue var1, boolean var2) {
            var1.wakeup(var2);
         }

         public void invokeAndWait(Object var1, Runnable var2) throws InterruptedException, InvocationTargetException {
            EventQueue.invokeAndWait(var1, var2);
         }

         public void setFwDispatcher(EventQueue var1, FwDispatcher var2) {
            var1.setFwDispatcher(var2);
         }

         public long getMostRecentEventTime(EventQueue var1) {
            return var1.getMostRecentEventTimeImpl();
         }
      });
      javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
   }

   private class FwSecondaryLoopWrapper implements SecondaryLoop {
      private final SecondaryLoop loop;
      private final EventFilter filter;

      public FwSecondaryLoopWrapper(SecondaryLoop var2, EventFilter var3) {
         this.loop = var2;
         this.filter = var3;
      }

      public boolean enter() {
         if (this.filter != null) {
            EventQueue.this.dispatchThread.addEventFilter(this.filter);
         }

         return this.loop.enter();
      }

      public boolean exit() {
         if (this.filter != null) {
            EventQueue.this.dispatchThread.removeEventFilter(this.filter);
         }

         return this.loop.exit();
      }
   }
}
