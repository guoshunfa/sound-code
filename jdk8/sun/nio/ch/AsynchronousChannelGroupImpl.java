package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.Channel;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.action.GetIntegerAction;

abstract class AsynchronousChannelGroupImpl extends AsynchronousChannelGroup implements Executor {
   private static final int internalThreadCount = (Integer)AccessController.doPrivileged((PrivilegedAction)(new GetIntegerAction("sun.nio.ch.internalThreadPoolSize", 1)));
   private final ThreadPool pool;
   private final AtomicInteger threadCount = new AtomicInteger();
   private ScheduledThreadPoolExecutor timeoutExecutor;
   private final Queue<Runnable> taskQueue;
   private final AtomicBoolean shutdown = new AtomicBoolean();
   private final Object shutdownNowLock = new Object();
   private volatile boolean terminateInitiated;

   AsynchronousChannelGroupImpl(AsynchronousChannelProvider var1, ThreadPool var2) {
      super(var1);
      this.pool = var2;
      if (var2.isFixedThreadPool()) {
         this.taskQueue = new ConcurrentLinkedQueue();
      } else {
         this.taskQueue = null;
      }

      this.timeoutExecutor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1, ThreadPool.defaultThreadFactory());
      this.timeoutExecutor.setRemoveOnCancelPolicy(true);
   }

   final ExecutorService executor() {
      return this.pool.executor();
   }

   final boolean isFixedThreadPool() {
      return this.pool.isFixedThreadPool();
   }

   final int fixedThreadCount() {
      return this.isFixedThreadPool() ? this.pool.poolSize() : this.pool.poolSize() + internalThreadCount;
   }

   private Runnable bindToGroup(final Runnable var1) {
      return new Runnable() {
         public void run() {
            Invoker.bindToGroup(AsynchronousChannelGroupImpl.this);
            var1.run();
         }
      };
   }

   private void startInternalThread(final Runnable var1) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            ThreadPool.defaultThreadFactory().newThread(var1).start();
            return null;
         }
      });
   }

   protected final void startThreads(Runnable var1) {
      int var2;
      if (!this.isFixedThreadPool()) {
         for(var2 = 0; var2 < internalThreadCount; ++var2) {
            this.startInternalThread(var1);
            this.threadCount.incrementAndGet();
         }
      }

      if (this.pool.poolSize() > 0) {
         var1 = this.bindToGroup(var1);

         try {
            for(var2 = 0; var2 < this.pool.poolSize(); ++var2) {
               this.pool.executor().execute(var1);
               this.threadCount.incrementAndGet();
            }
         } catch (RejectedExecutionException var3) {
         }
      }

   }

   final int threadCount() {
      return this.threadCount.get();
   }

   final int threadExit(Runnable var1, boolean var2) {
      if (var2) {
         try {
            if (Invoker.isBoundToAnyGroup()) {
               this.pool.executor().execute(this.bindToGroup(var1));
            } else {
               this.startInternalThread(var1);
            }

            return this.threadCount.get();
         } catch (RejectedExecutionException var4) {
         }
      }

      return this.threadCount.decrementAndGet();
   }

   abstract void executeOnHandlerTask(Runnable var1);

   final void executeOnPooledThread(Runnable var1) {
      if (this.isFixedThreadPool()) {
         this.executeOnHandlerTask(var1);
      } else {
         this.pool.executor().execute(this.bindToGroup(var1));
      }

   }

   final void offerTask(Runnable var1) {
      this.taskQueue.offer(var1);
   }

   final Runnable pollTask() {
      return this.taskQueue == null ? null : (Runnable)this.taskQueue.poll();
   }

   final Future<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      try {
         return this.timeoutExecutor.schedule(var1, var2, var4);
      } catch (RejectedExecutionException var6) {
         if (this.terminateInitiated) {
            return null;
         } else {
            throw new AssertionError(var6);
         }
      }
   }

   public final boolean isShutdown() {
      return this.shutdown.get();
   }

   public final boolean isTerminated() {
      return this.pool.executor().isTerminated();
   }

   abstract boolean isEmpty();

   abstract Object attachForeignChannel(Channel var1, FileDescriptor var2) throws IOException;

   abstract void detachForeignChannel(Object var1);

   abstract void closeAllChannels() throws IOException;

   abstract void shutdownHandlerTasks();

   private void shutdownExecutors() {
      AccessController.doPrivileged((PrivilegedAction)(new PrivilegedAction<Void>() {
         public Void run() {
            AsynchronousChannelGroupImpl.this.pool.executor().shutdown();
            AsynchronousChannelGroupImpl.this.timeoutExecutor.shutdown();
            return null;
         }
      }), (AccessControlContext)null, new RuntimePermission("modifyThread"));
   }

   public final void shutdown() {
      if (!this.shutdown.getAndSet(true)) {
         if (this.isEmpty()) {
            synchronized(this.shutdownNowLock) {
               if (!this.terminateInitiated) {
                  this.terminateInitiated = true;
                  this.shutdownHandlerTasks();
                  this.shutdownExecutors();
               }

            }
         }
      }
   }

   public final void shutdownNow() throws IOException {
      this.shutdown.set(true);
      synchronized(this.shutdownNowLock) {
         if (!this.terminateInitiated) {
            this.terminateInitiated = true;
            this.closeAllChannels();
            this.shutdownHandlerTasks();
            this.shutdownExecutors();
         }

      }
   }

   final void detachFromThreadPool() {
      if (this.shutdown.getAndSet(true)) {
         throw new AssertionError("Already shutdown");
      } else if (!this.isEmpty()) {
         throw new AssertionError("Group not empty");
      } else {
         this.shutdownHandlerTasks();
      }
   }

   public final boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      return this.pool.executor().awaitTermination(var1, var3);
   }

   public final void execute(final Runnable var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         final AccessControlContext var3 = AccessController.getContext();
         var1 = new Runnable() {
            public void run() {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     var1.run();
                     return null;
                  }
               }, var3);
            }
         };
      }

      this.executeOnPooledThread(var1);
   }
}
