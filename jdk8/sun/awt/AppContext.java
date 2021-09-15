package sun.awt;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.InvocationEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import sun.misc.JavaAWTAccess;
import sun.misc.SharedSecrets;
import sun.util.logging.PlatformLogger;

public final class AppContext {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.AppContext");
   public static final Object EVENT_QUEUE_KEY = new StringBuffer("EventQueue");
   public static final Object EVENT_QUEUE_LOCK_KEY = new StringBuilder("EventQueue.Lock");
   public static final Object EVENT_QUEUE_COND_KEY = new StringBuilder("EventQueue.Condition");
   private static final Map<ThreadGroup, AppContext> threadGroup2appContext = Collections.synchronizedMap(new IdentityHashMap());
   private static volatile AppContext mainAppContext = null;
   private static final Object getAppContextLock = new AppContext.GetAppContextLock();
   private final Map<Object, Object> table = new HashMap();
   private final ThreadGroup threadGroup;
   private PropertyChangeSupport changeSupport = null;
   public static final String DISPOSED_PROPERTY_NAME = "disposed";
   public static final String GUI_DISPOSED = "guidisposed";
   private volatile AppContext.State state;
   private static final AtomicInteger numAppContexts = new AtomicInteger(0);
   private final ClassLoader contextClassLoader;
   private static final ThreadLocal<AppContext> threadAppContext = new ThreadLocal();
   private long DISPOSAL_TIMEOUT;
   private long THREAD_INTERRUPT_TIMEOUT;
   private MostRecentKeyValue mostRecentKeyValue;
   private MostRecentKeyValue shadowMostRecentKeyValue;

   public static Set<AppContext> getAppContexts() {
      synchronized(threadGroup2appContext) {
         return new HashSet(threadGroup2appContext.values());
      }
   }

   public boolean isDisposed() {
      return this.state == AppContext.State.DISPOSED;
   }

   AppContext(ThreadGroup var1) {
      this.state = AppContext.State.VALID;
      this.DISPOSAL_TIMEOUT = 5000L;
      this.THREAD_INTERRUPT_TIMEOUT = 1000L;
      this.mostRecentKeyValue = null;
      this.shadowMostRecentKeyValue = null;
      numAppContexts.incrementAndGet();
      this.threadGroup = var1;
      threadGroup2appContext.put(var1, this);
      this.contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
      ReentrantLock var2 = new ReentrantLock();
      this.put(EVENT_QUEUE_LOCK_KEY, var2);
      Condition var3 = var2.newCondition();
      this.put(EVENT_QUEUE_COND_KEY, var3);
   }

   private static final void initMainAppContext() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            ThreadGroup var1 = Thread.currentThread().getThreadGroup();

            for(ThreadGroup var2 = var1.getParent(); var2 != null; var2 = var2.getParent()) {
               var1 = var2;
            }

            AppContext.mainAppContext = SunToolkit.createNewAppContext(var1);
            return null;
         }
      });
   }

   public static final AppContext getAppContext() {
      if (numAppContexts.get() == 1 && mainAppContext != null) {
         return mainAppContext;
      } else {
         AppContext var0 = (AppContext)threadAppContext.get();
         if (null == var0) {
            var0 = (AppContext)AccessController.doPrivileged(new PrivilegedAction<AppContext>() {
               public AppContext run() {
                  ThreadGroup var1 = Thread.currentThread().getThreadGroup();
                  ThreadGroup var2 = var1;
                  synchronized(AppContext.getAppContextLock) {
                     if (AppContext.numAppContexts.get() == 0) {
                        if (System.getProperty("javaplugin.version") == null && System.getProperty("javawebstart.version") == null) {
                           AppContext.initMainAppContext();
                        } else if (System.getProperty("javafx.version") != null && var2.getParent() != null) {
                           SunToolkit.createNewAppContext();
                        }
                     }
                  }

                  AppContext var3;
                  for(var3 = (AppContext)AppContext.threadGroup2appContext.get(var1); var3 == null; var3 = (AppContext)AppContext.threadGroup2appContext.get(var2)) {
                     var2 = var2.getParent();
                     if (var2 == null) {
                        SecurityManager var4 = System.getSecurityManager();
                        if (var4 != null) {
                           ThreadGroup var5 = var4.getThreadGroup();
                           if (var5 != null) {
                              return (AppContext)AppContext.threadGroup2appContext.get(var5);
                           }
                        }

                        return null;
                     }
                  }

                  for(ThreadGroup var7 = var1; var7 != var2; var7 = var7.getParent()) {
                     AppContext.threadGroup2appContext.put(var7, var3);
                  }

                  AppContext.threadAppContext.set(var3);
                  return var3;
               }
            });
         }

         return var0;
      }
   }

   public static final boolean isMainContext(AppContext var0) {
      return var0 != null && var0 == mainAppContext;
   }

   private static final AppContext getExecutionAppContext() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null && var0 instanceof AWTSecurityManager) {
         AWTSecurityManager var1 = (AWTSecurityManager)var0;
         AppContext var2 = var1.getAppContext();
         return var2;
      } else {
         return null;
      }
   }

   public void dispose() throws IllegalThreadStateException {
      if (this.threadGroup.parentOf(Thread.currentThread().getThreadGroup())) {
         throw new IllegalThreadStateException("Current Thread is contained within AppContext to be disposed.");
      } else {
         synchronized(this) {
            if (this.state != AppContext.State.VALID) {
               return;
            }

            this.state = AppContext.State.BEING_DISPOSED;
         }

         final PropertyChangeSupport var1 = this.changeSupport;
         if (var1 != null) {
            var1.firePropertyChange("disposed", false, true);
         }

         final Object var2 = new Object();
         Runnable var3 = new Runnable() {
            public void run() {
               Window[] var1x = Window.getOwnerlessWindows();
               Window[] var2x = var1x;
               int var3 = var1x.length;

               for(int var4 = 0; var4 < var3; ++var4) {
                  Window var5 = var2x[var4];

                  try {
                     var5.dispose();
                  } catch (Throwable var9) {
                     AppContext.log.finer("exception occurred while disposing app context", var9);
                  }
               }

               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     if (!GraphicsEnvironment.isHeadless() && SystemTray.isSupported()) {
                        SystemTray var1x = SystemTray.getSystemTray();
                        TrayIcon[] var2x = var1x.getTrayIcons();
                        TrayIcon[] var3 = var2x;
                        int var4 = var2x.length;

                        for(int var5 = 0; var5 < var4; ++var5) {
                           TrayIcon var6 = var3[var5];
                           var1x.remove(var6);
                        }
                     }

                     return null;
                  }
               });
               if (var1 != null) {
                  var1.firePropertyChange("guidisposed", false, true);
               }

               synchronized(var2) {
                  var2.notifyAll();
               }
            }
         };
         synchronized(var2) {
            SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), var3));

            try {
               var2.wait(this.DISPOSAL_TIMEOUT);
            } catch (InterruptedException var19) {
            }
         }

         var3 = new Runnable() {
            public void run() {
               synchronized(var2) {
                  var2.notifyAll();
               }
            }
         };
         synchronized(var2) {
            SunToolkit.postEvent(this, new InvocationEvent(Toolkit.getDefaultToolkit(), var3));

            try {
               var2.wait(this.DISPOSAL_TIMEOUT);
            } catch (InterruptedException var17) {
            }
         }

         synchronized(this) {
            this.state = AppContext.State.DISPOSED;
         }

         this.threadGroup.interrupt();
         long var4 = System.currentTimeMillis();
         long var6 = var4 + this.THREAD_INTERRUPT_TIMEOUT;

         while(this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < var6) {
            try {
               Thread.sleep(10L);
            } catch (InterruptedException var15) {
            }
         }

         this.threadGroup.stop();
         var4 = System.currentTimeMillis();
         var6 = var4 + this.THREAD_INTERRUPT_TIMEOUT;

         while(this.threadGroup.activeCount() > 0 && System.currentTimeMillis() < var6) {
            try {
               Thread.sleep(10L);
            } catch (InterruptedException var14) {
            }
         }

         int var8 = this.threadGroup.activeGroupCount();
         if (var8 > 0) {
            ThreadGroup[] var9 = new ThreadGroup[var8];
            var8 = this.threadGroup.enumerate(var9);

            for(int var10 = 0; var10 < var8; ++var10) {
               threadGroup2appContext.remove(var9[var10]);
            }
         }

         threadGroup2appContext.remove(this.threadGroup);
         threadAppContext.set((Object)null);

         try {
            this.threadGroup.destroy();
         } catch (IllegalThreadStateException var13) {
         }

         synchronized(this.table) {
            this.table.clear();
         }

         numAppContexts.decrementAndGet();
         this.mostRecentKeyValue = null;
      }
   }

   static void stopEventDispatchThreads() {
      Iterator var0 = getAppContexts().iterator();

      while(var0.hasNext()) {
         AppContext var1 = (AppContext)var0.next();
         if (!var1.isDisposed()) {
            AppContext.PostShutdownEventRunnable var2 = new AppContext.PostShutdownEventRunnable(var1);
            if (var1 != getAppContext()) {
               AppContext.CreateThreadAction var3 = new AppContext.CreateThreadAction(var1, var2);
               Thread var4 = (Thread)AccessController.doPrivileged((PrivilegedAction)var3);
               var4.start();
            } else {
               var2.run();
            }
         }
      }

   }

   public Object get(Object var1) {
      synchronized(this.table) {
         MostRecentKeyValue var3 = this.mostRecentKeyValue;
         if (var3 != null && var3.key == var1) {
            return var3.value;
         } else {
            Object var4 = this.table.get(var1);
            if (this.mostRecentKeyValue == null) {
               this.mostRecentKeyValue = new MostRecentKeyValue(var1, var4);
               this.shadowMostRecentKeyValue = new MostRecentKeyValue(var1, var4);
            } else {
               MostRecentKeyValue var5 = this.mostRecentKeyValue;
               this.shadowMostRecentKeyValue.setPair(var1, var4);
               this.mostRecentKeyValue = this.shadowMostRecentKeyValue;
               this.shadowMostRecentKeyValue = var5;
            }

            return var4;
         }
      }
   }

   public Object put(Object var1, Object var2) {
      synchronized(this.table) {
         MostRecentKeyValue var4 = this.mostRecentKeyValue;
         if (var4 != null && var4.key == var1) {
            var4.value = var2;
         }

         return this.table.put(var1, var2);
      }
   }

   public Object remove(Object var1) {
      synchronized(this.table) {
         MostRecentKeyValue var3 = this.mostRecentKeyValue;
         if (var3 != null && var3.key == var1) {
            var3.value = null;
         }

         return this.table.remove(var1);
      }
   }

   public ThreadGroup getThreadGroup() {
      return this.threadGroup;
   }

   public ClassLoader getContextClassLoader() {
      return this.contextClassLoader;
   }

   public String toString() {
      return this.getClass().getName() + "[threadGroup=" + this.threadGroup.getName() + "]";
   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
      return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners();
   }

   public synchronized void addPropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null) {
         if (this.changeSupport == null) {
            this.changeSupport = new PropertyChangeSupport(this);
         }

         this.changeSupport.addPropertyChangeListener(var1, var2);
      }
   }

   public synchronized void removePropertyChangeListener(String var1, PropertyChangeListener var2) {
      if (var2 != null && this.changeSupport != null) {
         this.changeSupport.removePropertyChangeListener(var1, var2);
      }
   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners(String var1) {
      return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(var1);
   }

   public static <T> T getSoftReferenceValue(Object var0, Supplier<T> var1) {
      AppContext var2 = getAppContext();
      SoftReference var3 = (SoftReference)var2.get(var0);
      Object var4;
      if (var3 != null) {
         var4 = var3.get();
         if (var4 != null) {
            return var4;
         }
      }

      var4 = var1.get();
      var3 = new SoftReference(var4);
      var2.put(var0, var3);
      return var4;
   }

   static {
      SharedSecrets.setJavaAWTAccess(new JavaAWTAccess() {
         private boolean hasRootThreadGroup(final AppContext var1) {
            return (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
               public Boolean run() {
                  return var1.threadGroup.getParent() == null;
               }
            });
         }

         public Object getAppletContext() {
            if (AppContext.numAppContexts.get() == 0) {
               return null;
            } else {
               AppContext var1 = AppContext.getExecutionAppContext();
               if (AppContext.numAppContexts.get() > 0) {
                  var1 = var1 != null ? var1 : AppContext.getAppContext();
               }

               boolean var2 = var1 == null || AppContext.mainAppContext == var1 || AppContext.mainAppContext == null && this.hasRootThreadGroup(var1);
               return var2 ? null : var1;
            }
         }
      });
   }

   static final class CreateThreadAction implements PrivilegedAction<Thread> {
      private final AppContext appContext;
      private final Runnable runnable;

      public CreateThreadAction(AppContext var1, Runnable var2) {
         this.appContext = var1;
         this.runnable = var2;
      }

      public Thread run() {
         Thread var1 = new Thread(this.appContext.getThreadGroup(), this.runnable);
         var1.setContextClassLoader(this.appContext.getContextClassLoader());
         var1.setPriority(6);
         var1.setDaemon(true);
         return var1;
      }
   }

   static final class PostShutdownEventRunnable implements Runnable {
      private final AppContext appContext;

      public PostShutdownEventRunnable(AppContext var1) {
         this.appContext = var1;
      }

      public void run() {
         EventQueue var1 = (EventQueue)this.appContext.get(AppContext.EVENT_QUEUE_KEY);
         if (var1 != null) {
            var1.postEvent(AWTAutoShutdown.getShutdownEvent());
         }

      }
   }

   private static enum State {
      VALID,
      BEING_DISPOSED,
      DISPOSED;
   }

   private static class GetAppContextLock {
      private GetAppContextLock() {
      }

      // $FF: synthetic method
      GetAppContextLock(Object var1) {
         this();
      }
   }
}
