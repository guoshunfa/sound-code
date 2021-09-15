package java.lang;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.misc.Contended;
import sun.misc.VM;
import sun.nio.ch.Interruptible;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;

public class Thread implements Runnable {
   private volatile String name;
   private int priority;
   private Thread threadQ;
   private long eetop;
   private boolean single_step;
   private boolean daemon = false;
   private boolean stillborn = false;
   private Runnable target;
   private ThreadGroup group;
   private ClassLoader contextClassLoader;
   private AccessControlContext inheritedAccessControlContext;
   private static int threadInitNumber;
   ThreadLocal.ThreadLocalMap threadLocals = null;
   ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
   private long stackSize;
   private long nativeParkEventPointer;
   private long tid;
   private static long threadSeqNumber;
   private volatile int threadStatus = 0;
   volatile Object parkBlocker;
   private volatile Interruptible blocker;
   private final Object blockerLock = new Object();
   public static final int MIN_PRIORITY = 1;
   public static final int NORM_PRIORITY = 5;
   public static final int MAX_PRIORITY = 10;
   private static final StackTraceElement[] EMPTY_STACK_TRACE;
   private static final RuntimePermission SUBCLASS_IMPLEMENTATION_PERMISSION;
   private volatile Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
   private static volatile Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;
   @Contended("tlr")
   long threadLocalRandomSeed;
   @Contended("tlr")
   int threadLocalRandomProbe;
   @Contended("tlr")
   int threadLocalRandomSecondarySeed;

   private static native void registerNatives();

   private static synchronized int nextThreadNum() {
      return threadInitNumber++;
   }

   private static synchronized long nextThreadID() {
      return ++threadSeqNumber;
   }

   void blockedOn(Interruptible var1) {
      synchronized(this.blockerLock) {
         this.blocker = var1;
      }
   }

   public static native Thread currentThread();

   public static native void yield();

   public static native void sleep(long var0) throws InterruptedException;

   public static void sleep(long var0, int var2) throws InterruptedException {
      if (var0 < 0L) {
         throw new IllegalArgumentException("timeout value is negative");
      } else if (var2 >= 0 && var2 <= 999999) {
         if (var2 >= 500000 || var2 != 0 && var0 == 0L) {
            ++var0;
         }

         sleep(var0);
      } else {
         throw new IllegalArgumentException("nanosecond timeout value out of range");
      }
   }

   private void init(ThreadGroup var1, Runnable var2, String var3, long var4) {
      this.init(var1, var2, var3, var4, (AccessControlContext)null, true);
   }

   private void init(ThreadGroup var1, Runnable var2, String var3, long var4, AccessControlContext var6, boolean var7) {
      if (var3 == null) {
         throw new NullPointerException("name cannot be null");
      } else {
         this.name = var3;
         Thread var8 = currentThread();
         SecurityManager var9 = System.getSecurityManager();
         if (var1 == null) {
            if (var9 != null) {
               var1 = var9.getThreadGroup();
            }

            if (var1 == null) {
               var1 = var8.getThreadGroup();
            }
         }

         var1.checkAccess();
         if (var9 != null && isCCLOverridden(this.getClass())) {
            var9.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
         }

         var1.addUnstarted();
         this.group = var1;
         this.daemon = var8.isDaemon();
         this.priority = var8.getPriority();
         if (var9 != null && !isCCLOverridden(var8.getClass())) {
            this.contextClassLoader = var8.contextClassLoader;
         } else {
            this.contextClassLoader = var8.getContextClassLoader();
         }

         this.inheritedAccessControlContext = var6 != null ? var6 : AccessController.getContext();
         this.target = var2;
         this.setPriority(this.priority);
         if (var7 && var8.inheritableThreadLocals != null) {
            this.inheritableThreadLocals = ThreadLocal.createInheritedMap(var8.inheritableThreadLocals);
         }

         this.stackSize = var4;
         this.tid = nextThreadID();
      }
   }

   protected Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
   }

   public Thread() {
      this.init((ThreadGroup)null, (Runnable)null, "Thread-" + nextThreadNum(), 0L);
   }

   public Thread(Runnable var1) {
      this.init((ThreadGroup)null, var1, "Thread-" + nextThreadNum(), 0L);
   }

   Thread(Runnable var1, AccessControlContext var2) {
      this.init((ThreadGroup)null, var1, "Thread-" + nextThreadNum(), 0L, var2, false);
   }

   public Thread(ThreadGroup var1, Runnable var2) {
      this.init(var1, var2, "Thread-" + nextThreadNum(), 0L);
   }

   public Thread(String var1) {
      this.init((ThreadGroup)null, (Runnable)null, var1, 0L);
   }

   public Thread(ThreadGroup var1, String var2) {
      this.init(var1, (Runnable)null, var2, 0L);
   }

   public Thread(Runnable var1, String var2) {
      this.init((ThreadGroup)null, var1, var2, 0L);
   }

   public Thread(ThreadGroup var1, Runnable var2, String var3) {
      this.init(var1, var2, var3, 0L);
   }

   public Thread(ThreadGroup var1, Runnable var2, String var3, long var4) {
      this.init(var1, var2, var3, var4);
   }

   public synchronized void start() {
      if (this.threadStatus != 0) {
         throw new IllegalThreadStateException();
      } else {
         this.group.add(this);
         boolean var1 = false;

         try {
            this.start0();
            var1 = true;
         } finally {
            try {
               if (!var1) {
                  this.group.threadStartFailed(this);
               }
            } catch (Throwable var8) {
            }

         }

      }
   }

   private native void start0();

   public void run() {
      if (this.target != null) {
         this.target.run();
      }

   }

   private void exit() {
      if (this.group != null) {
         this.group.threadTerminated(this);
         this.group = null;
      }

      this.target = null;
      this.threadLocals = null;
      this.inheritableThreadLocals = null;
      this.inheritedAccessControlContext = null;
      this.blocker = null;
      this.uncaughtExceptionHandler = null;
   }

   /** @deprecated */
   @Deprecated
   public final void stop() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         this.checkAccess();
         if (this != currentThread()) {
            var1.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
         }
      }

      if (this.threadStatus != 0) {
         this.resume();
      }

      this.stop0(new ThreadDeath());
   }

   /** @deprecated */
   @Deprecated
   public final synchronized void stop(Throwable var1) {
      throw new UnsupportedOperationException();
   }

   public void interrupt() {
      if (this != currentThread()) {
         this.checkAccess();
      }

      synchronized(this.blockerLock) {
         Interruptible var2 = this.blocker;
         if (var2 != null) {
            this.interrupt0();
            var2.interrupt(this);
            return;
         }
      }

      this.interrupt0();
   }

   public static boolean interrupted() {
      return currentThread().isInterrupted(true);
   }

   public boolean isInterrupted() {
      return this.isInterrupted(false);
   }

   private native boolean isInterrupted(boolean var1);

   /** @deprecated */
   @Deprecated
   public void destroy() {
      throw new NoSuchMethodError();
   }

   public final native boolean isAlive();

   /** @deprecated */
   @Deprecated
   public final void suspend() {
      this.checkAccess();
      this.suspend0();
   }

   /** @deprecated */
   @Deprecated
   public final void resume() {
      this.checkAccess();
      this.resume0();
   }

   public final void setPriority(int var1) {
      this.checkAccess();
      if (var1 <= 10 && var1 >= 1) {
         ThreadGroup var2;
         if ((var2 = this.getThreadGroup()) != null) {
            if (var1 > var2.getMaxPriority()) {
               var1 = var2.getMaxPriority();
            }

            this.setPriority0(this.priority = var1);
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   public final int getPriority() {
      return this.priority;
   }

   public final synchronized void setName(String var1) {
      this.checkAccess();
      if (var1 == null) {
         throw new NullPointerException("name cannot be null");
      } else {
         this.name = var1;
         if (this.threadStatus != 0) {
            this.setNativeName(var1);
         }

      }
   }

   public final String getName() {
      return this.name;
   }

   public final ThreadGroup getThreadGroup() {
      return this.group;
   }

   public static int activeCount() {
      return currentThread().getThreadGroup().activeCount();
   }

   public static int enumerate(Thread[] var0) {
      return currentThread().getThreadGroup().enumerate(var0);
   }

   /** @deprecated */
   @Deprecated
   public native int countStackFrames();

   public final synchronized void join(long var1) throws InterruptedException {
      long var3 = System.currentTimeMillis();
      long var5 = 0L;
      if (var1 < 0L) {
         throw new IllegalArgumentException("timeout value is negative");
      } else {
         if (var1 == 0L) {
            while(this.isAlive()) {
               this.wait(0L);
            }
         } else {
            while(this.isAlive()) {
               long var7 = var1 - var5;
               if (var7 <= 0L) {
                  break;
               }

               this.wait(var7);
               var5 = System.currentTimeMillis() - var3;
            }
         }

      }
   }

   public final synchronized void join(long var1, int var3) throws InterruptedException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("timeout value is negative");
      } else if (var3 >= 0 && var3 <= 999999) {
         if (var3 >= 500000 || var3 != 0 && var1 == 0L) {
            ++var1;
         }

         this.join(var1);
      } else {
         throw new IllegalArgumentException("nanosecond timeout value out of range");
      }
   }

   public final void join() throws InterruptedException {
      this.join(0L);
   }

   public static void dumpStack() {
      (new Exception("Stack trace")).printStackTrace();
   }

   public final void setDaemon(boolean var1) {
      this.checkAccess();
      if (this.isAlive()) {
         throw new IllegalThreadStateException();
      } else {
         this.daemon = var1;
      }
   }

   public final boolean isDaemon() {
      return this.daemon;
   }

   public final void checkAccess() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkAccess(this);
      }

   }

   public String toString() {
      ThreadGroup var1 = this.getThreadGroup();
      return var1 != null ? "Thread[" + this.getName() + "," + this.getPriority() + "," + var1.getName() + "]" : "Thread[" + this.getName() + "," + this.getPriority() + ",]";
   }

   @CallerSensitive
   public ClassLoader getContextClassLoader() {
      if (this.contextClassLoader == null) {
         return null;
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            ClassLoader.checkClassLoaderPermission(this.contextClassLoader, Reflection.getCallerClass());
         }

         return this.contextClassLoader;
      }
   }

   public void setContextClassLoader(ClassLoader var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkPermission(new RuntimePermission("setContextClassLoader"));
      }

      this.contextClassLoader = var1;
   }

   public static native boolean holdsLock(Object var0);

   public StackTraceElement[] getStackTrace() {
      if (this != currentThread()) {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(SecurityConstants.GET_STACK_TRACE_PERMISSION);
         }

         if (!this.isAlive()) {
            return EMPTY_STACK_TRACE;
         } else {
            StackTraceElement[][] var2 = dumpThreads(new Thread[]{this});
            StackTraceElement[] var3 = var2[0];
            if (var3 == null) {
               var3 = EMPTY_STACK_TRACE;
            }

            return var3;
         }
      } else {
         return (new Exception()).getStackTrace();
      }
   }

   public static Map<Thread, StackTraceElement[]> getAllStackTraces() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(SecurityConstants.GET_STACK_TRACE_PERMISSION);
         var0.checkPermission(SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
      }

      Thread[] var1 = getThreads();
      StackTraceElement[][] var2 = dumpThreads(var1);
      HashMap var3 = new HashMap(var1.length);

      for(int var4 = 0; var4 < var1.length; ++var4) {
         StackTraceElement[] var5 = var2[var4];
         if (var5 != null) {
            var3.put(var1[var4], var5);
         }
      }

      return var3;
   }

   private static boolean isCCLOverridden(Class<?> var0) {
      if (var0 == Thread.class) {
         return false;
      } else {
         processQueue(Thread.Caches.subclassAuditsQueue, Thread.Caches.subclassAudits);
         Thread.WeakClassKey var1 = new Thread.WeakClassKey(var0, Thread.Caches.subclassAuditsQueue);
         Boolean var2 = (Boolean)Thread.Caches.subclassAudits.get(var1);
         if (var2 == null) {
            var2 = auditSubclass(var0);
            Thread.Caches.subclassAudits.putIfAbsent(var1, var2);
         }

         return var2;
      }
   }

   private static boolean auditSubclass(final Class<?> var0) {
      Boolean var1 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            Class var1 = var0;

            while(var1 != Thread.class) {
               try {
                  var1.getDeclaredMethod("getContextClassLoader");
                  return Boolean.TRUE;
               } catch (NoSuchMethodException var4) {
                  try {
                     Class[] var2 = new Class[]{ClassLoader.class};
                     var1.getDeclaredMethod("setContextClassLoader", var2);
                     return Boolean.TRUE;
                  } catch (NoSuchMethodException var3) {
                     var1 = var1.getSuperclass();
                  }
               }
            }

            return Boolean.FALSE;
         }
      });
      return var1;
   }

   private static native StackTraceElement[][] dumpThreads(Thread[] var0);

   private static native Thread[] getThreads();

   public long getId() {
      return this.tid;
   }

   public Thread.State getState() {
      return VM.toThreadState(this.threadStatus);
   }

   public static void setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("setDefaultUncaughtExceptionHandler"));
      }

      defaultUncaughtExceptionHandler = var0;
   }

   public static Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
      return defaultUncaughtExceptionHandler;
   }

   public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
      return (Thread.UncaughtExceptionHandler)(this.uncaughtExceptionHandler != null ? this.uncaughtExceptionHandler : this.group);
   }

   public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler var1) {
      this.checkAccess();
      this.uncaughtExceptionHandler = var1;
   }

   private void dispatchUncaughtException(Throwable var1) {
      this.getUncaughtExceptionHandler().uncaughtException(this, var1);
   }

   static void processQueue(ReferenceQueue<Class<?>> var0, ConcurrentMap<? extends WeakReference<Class<?>>, ?> var1) {
      Reference var2;
      while((var2 = var0.poll()) != null) {
         var1.remove(var2);
      }

   }

   private native void setPriority0(int var1);

   private native void stop0(Object var1);

   private native void suspend0();

   private native void resume0();

   private native void interrupt0();

   private native void setNativeName(String var1);

   static {
      registerNatives();
      EMPTY_STACK_TRACE = new StackTraceElement[0];
      SUBCLASS_IMPLEMENTATION_PERMISSION = new RuntimePermission("enableContextClassLoaderOverride");
   }

   static class WeakClassKey extends WeakReference<Class<?>> {
      private final int hash;

      WeakClassKey(Class<?> var1, ReferenceQueue<Class<?>> var2) {
         super(var1, var2);
         this.hash = System.identityHashCode(var1);
      }

      public int hashCode() {
         return this.hash;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Thread.WeakClassKey)) {
            return false;
         } else {
            Object var2 = this.get();
            return var2 != null && var2 == ((Thread.WeakClassKey)var1).get();
         }
      }
   }

   @FunctionalInterface
   public interface UncaughtExceptionHandler {
      void uncaughtException(Thread var1, Throwable var2);
   }

   public static enum State {
      NEW,
      RUNNABLE,
      BLOCKED,
      WAITING,
      TIMED_WAITING,
      TERMINATED;
   }

   private static class Caches {
      static final ConcurrentMap<Thread.WeakClassKey, Boolean> subclassAudits = new ConcurrentHashMap();
      static final ReferenceQueue<Class<?>> subclassAuditsQueue = new ReferenceQueue();
   }
}
