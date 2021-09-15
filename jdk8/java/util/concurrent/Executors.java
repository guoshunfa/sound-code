package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.util.SecurityConstants;

public class Executors {
   public static ExecutorService newFixedThreadPool(int var0) {
      return new ThreadPoolExecutor(var0, var0, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
   }

   public static ExecutorService newWorkStealingPool(int var0) {
      return new ForkJoinPool(var0, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (Thread.UncaughtExceptionHandler)null, true);
   }

   public static ExecutorService newWorkStealingPool() {
      return new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, (Thread.UncaughtExceptionHandler)null, true);
   }

   public static ExecutorService newFixedThreadPool(int var0, ThreadFactory var1) {
      return new ThreadPoolExecutor(var0, var0, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), var1);
   }

   public static ExecutorService newSingleThreadExecutor() {
      return new Executors.FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue()));
   }

   public static ExecutorService newSingleThreadExecutor(ThreadFactory var0) {
      return new Executors.FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), var0));
   }

   public static ExecutorService newCachedThreadPool() {
      return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue());
   }

   public static ExecutorService newCachedThreadPool(ThreadFactory var0) {
      return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue(), var0);
   }

   public static ScheduledExecutorService newSingleThreadScheduledExecutor() {
      return new Executors.DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1));
   }

   public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory var0) {
      return new Executors.DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1, var0));
   }

   public static ScheduledExecutorService newScheduledThreadPool(int var0) {
      return new ScheduledThreadPoolExecutor(var0);
   }

   public static ScheduledExecutorService newScheduledThreadPool(int var0, ThreadFactory var1) {
      return new ScheduledThreadPoolExecutor(var0, var1);
   }

   public static ExecutorService unconfigurableExecutorService(ExecutorService var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Executors.DelegatedExecutorService(var0);
      }
   }

   public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Executors.DelegatedScheduledExecutorService(var0);
      }
   }

   public static ThreadFactory defaultThreadFactory() {
      return new Executors.DefaultThreadFactory();
   }

   public static ThreadFactory privilegedThreadFactory() {
      return new Executors.PrivilegedThreadFactory();
   }

   public static <T> Callable<T> callable(Runnable var0, T var1) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Executors.RunnableAdapter(var0, var1);
      }
   }

   public static Callable<Object> callable(Runnable var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Executors.RunnableAdapter(var0, (Object)null);
      }
   }

   public static Callable<Object> callable(final PrivilegedAction<?> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Callable<Object>() {
            public Object call() {
               return var0.run();
            }
         };
      }
   }

   public static Callable<Object> callable(final PrivilegedExceptionAction<?> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Callable<Object>() {
            public Object call() throws Exception {
               return var0.run();
            }
         };
      }
   }

   public static <T> Callable<T> privilegedCallable(Callable<T> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Executors.PrivilegedCallable(var0);
      }
   }

   public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return new Executors.PrivilegedCallableUsingCurrentClassLoader(var0);
      }
   }

   private Executors() {
   }

   static class DelegatedScheduledExecutorService extends Executors.DelegatedExecutorService implements ScheduledExecutorService {
      private final ScheduledExecutorService e;

      DelegatedScheduledExecutorService(ScheduledExecutorService var1) {
         super(var1);
         this.e = var1;
      }

      public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
         return this.e.schedule(var1, var2, var4);
      }

      public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
         return this.e.schedule(var1, var2, var4);
      }

      public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
         return this.e.scheduleAtFixedRate(var1, var2, var4, var6);
      }

      public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
         return this.e.scheduleWithFixedDelay(var1, var2, var4, var6);
      }
   }

   static class FinalizableDelegatedExecutorService extends Executors.DelegatedExecutorService {
      FinalizableDelegatedExecutorService(ExecutorService var1) {
         super(var1);
      }

      protected void finalize() {
         super.shutdown();
      }
   }

   static class DelegatedExecutorService extends AbstractExecutorService {
      private final ExecutorService e;

      DelegatedExecutorService(ExecutorService var1) {
         this.e = var1;
      }

      public void execute(Runnable var1) {
         this.e.execute(var1);
      }

      public void shutdown() {
         this.e.shutdown();
      }

      public List<Runnable> shutdownNow() {
         return this.e.shutdownNow();
      }

      public boolean isShutdown() {
         return this.e.isShutdown();
      }

      public boolean isTerminated() {
         return this.e.isTerminated();
      }

      public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
         return this.e.awaitTermination(var1, var3);
      }

      public Future<?> submit(Runnable var1) {
         return this.e.submit(var1);
      }

      public <T> Future<T> submit(Callable<T> var1) {
         return this.e.submit(var1);
      }

      public <T> Future<T> submit(Runnable var1, T var2) {
         return this.e.submit(var1, var2);
      }

      public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
         return this.e.invokeAll(var1);
      }

      public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
         return this.e.invokeAll(var1, var2, var4);
      }

      public <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException {
         return this.e.invokeAny(var1);
      }

      public <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException {
         return this.e.invokeAny(var1, var2, var4);
      }
   }

   static class PrivilegedThreadFactory extends Executors.DefaultThreadFactory {
      private final AccessControlContext acc;
      private final ClassLoader ccl;

      PrivilegedThreadFactory() {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            var1.checkPermission(new RuntimePermission("setContextClassLoader"));
         }

         this.acc = AccessController.getContext();
         this.ccl = Thread.currentThread().getContextClassLoader();
      }

      public Thread newThread(final Runnable var1) {
         return super.newThread(new Runnable() {
            public void run() {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     Thread.currentThread().setContextClassLoader(PrivilegedThreadFactory.this.ccl);
                     var1.run();
                     return null;
                  }
               }, PrivilegedThreadFactory.this.acc);
            }
         });
      }
   }

   static class DefaultThreadFactory implements ThreadFactory {
      private static final AtomicInteger poolNumber = new AtomicInteger(1);
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      DefaultThreadFactory() {
         SecurityManager var1 = System.getSecurityManager();
         this.group = var1 != null ? var1.getThreadGroup() : Thread.currentThread().getThreadGroup();
         this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
      }

      public Thread newThread(Runnable var1) {
         Thread var2 = new Thread(this.group, var1, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
         if (var2.isDaemon()) {
            var2.setDaemon(false);
         }

         if (var2.getPriority() != 5) {
            var2.setPriority(5);
         }

         return var2;
      }
   }

   static final class PrivilegedCallableUsingCurrentClassLoader<T> implements Callable<T> {
      private final Callable<T> task;
      private final AccessControlContext acc;
      private final ClassLoader ccl;

      PrivilegedCallableUsingCurrentClassLoader(Callable<T> var1) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            var2.checkPermission(new RuntimePermission("setContextClassLoader"));
         }

         this.task = var1;
         this.acc = AccessController.getContext();
         this.ccl = Thread.currentThread().getContextClassLoader();
      }

      public T call() throws Exception {
         try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<T>() {
               public T run() throws Exception {
                  Thread var1 = Thread.currentThread();
                  ClassLoader var2 = var1.getContextClassLoader();
                  if (PrivilegedCallableUsingCurrentClassLoader.this.ccl == var2) {
                     return PrivilegedCallableUsingCurrentClassLoader.this.task.call();
                  } else {
                     var1.setContextClassLoader(PrivilegedCallableUsingCurrentClassLoader.this.ccl);

                     Object var3;
                     try {
                        var3 = PrivilegedCallableUsingCurrentClassLoader.this.task.call();
                     } finally {
                        var1.setContextClassLoader(var2);
                     }

                     return var3;
                  }
               }
            }, this.acc);
         } catch (PrivilegedActionException var2) {
            throw var2.getException();
         }
      }
   }

   static final class PrivilegedCallable<T> implements Callable<T> {
      private final Callable<T> task;
      private final AccessControlContext acc;

      PrivilegedCallable(Callable<T> var1) {
         this.task = var1;
         this.acc = AccessController.getContext();
      }

      public T call() throws Exception {
         try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<T>() {
               public T run() throws Exception {
                  return PrivilegedCallable.this.task.call();
               }
            }, this.acc);
         } catch (PrivilegedActionException var2) {
            throw var2.getException();
         }
      }
   }

   static final class RunnableAdapter<T> implements Callable<T> {
      final Runnable task;
      final T result;

      RunnableAdapter(Runnable var1, T var2) {
         this.task = var1;
         this.result = var2;
      }

      public T call() {
         this.task.run();
         return this.result;
      }
   }
}
