package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService {
   private volatile boolean continueExistingPeriodicTasksAfterShutdown;
   private volatile boolean executeExistingDelayedTasksAfterShutdown = true;
   private volatile boolean removeOnCancel = false;
   private static final AtomicLong sequencer = new AtomicLong();

   final long now() {
      return System.nanoTime();
   }

   boolean canRunInCurrentRunState(boolean var1) {
      return this.isRunningOrShutdown(var1 ? this.continueExistingPeriodicTasksAfterShutdown : this.executeExistingDelayedTasksAfterShutdown);
   }

   private void delayedExecute(RunnableScheduledFuture<?> var1) {
      if (this.isShutdown()) {
         this.reject(var1);
      } else {
         super.getQueue().add(var1);
         if (this.isShutdown() && !this.canRunInCurrentRunState(var1.isPeriodic()) && this.remove(var1)) {
            var1.cancel(false);
         } else {
            this.ensurePrestart();
         }
      }

   }

   void reExecutePeriodic(RunnableScheduledFuture<?> var1) {
      if (this.canRunInCurrentRunState(true)) {
         super.getQueue().add(var1);
         if (!this.canRunInCurrentRunState(true) && this.remove(var1)) {
            var1.cancel(false);
         } else {
            this.ensurePrestart();
         }
      }

   }

   void onShutdown() {
      BlockingQueue var1 = super.getQueue();
      boolean var2 = this.getExecuteExistingDelayedTasksAfterShutdownPolicy();
      boolean var3 = this.getContinueExistingPeriodicTasksAfterShutdownPolicy();
      Object[] var4;
      int var5;
      int var6;
      Object var7;
      if (!var2 && !var3) {
         var4 = var1.toArray();
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            if (var7 instanceof RunnableScheduledFuture) {
               ((RunnableScheduledFuture)var7).cancel(false);
            }
         }

         var1.clear();
      } else {
         var4 = var1.toArray();
         var5 = var4.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            if (var7 instanceof RunnableScheduledFuture) {
               RunnableScheduledFuture var8;
               label37: {
                  var8 = (RunnableScheduledFuture)var7;
                  if (var8.isPeriodic()) {
                     if (!var3) {
                        break label37;
                     }
                  } else if (!var2) {
                     break label37;
                  }

                  if (!var8.isCancelled()) {
                     continue;
                  }
               }

               if (var1.remove(var8)) {
                  var8.cancel(false);
               }
            }
         }
      }

      this.tryTerminate();
   }

   protected <V> RunnableScheduledFuture<V> decorateTask(Runnable var1, RunnableScheduledFuture<V> var2) {
      return var2;
   }

   protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> var1, RunnableScheduledFuture<V> var2) {
      return var2;
   }

   public ScheduledThreadPoolExecutor(int var1) {
      super(var1, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new ScheduledThreadPoolExecutor.DelayedWorkQueue());
   }

   public ScheduledThreadPoolExecutor(int var1, ThreadFactory var2) {
      super(var1, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new ScheduledThreadPoolExecutor.DelayedWorkQueue(), (ThreadFactory)var2);
   }

   public ScheduledThreadPoolExecutor(int var1, RejectedExecutionHandler var2) {
      super(var1, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new ScheduledThreadPoolExecutor.DelayedWorkQueue(), (RejectedExecutionHandler)var2);
   }

   public ScheduledThreadPoolExecutor(int var1, ThreadFactory var2, RejectedExecutionHandler var3) {
      super(var1, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS, new ScheduledThreadPoolExecutor.DelayedWorkQueue(), var2, var3);
   }

   private long triggerTime(long var1, TimeUnit var3) {
      return this.triggerTime(var3.toNanos(var1 < 0L ? 0L : var1));
   }

   long triggerTime(long var1) {
      return this.now() + (var1 < 4611686018427387903L ? var1 : this.overflowFree(var1));
   }

   private long overflowFree(long var1) {
      Delayed var3 = (Delayed)super.getQueue().peek();
      if (var3 != null) {
         long var4 = var3.getDelay(TimeUnit.NANOSECONDS);
         if (var4 < 0L && var1 - var4 < 0L) {
            var1 = Long.MAX_VALUE + var4;
         }
      }

      return var1;
   }

   public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
      if (var1 != null && var4 != null) {
         RunnableScheduledFuture var5 = this.decorateTask((Runnable)var1, new ScheduledThreadPoolExecutor.ScheduledFutureTask(var1, (Object)null, this.triggerTime(var2, var4)));
         this.delayedExecute(var5);
         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
      if (var1 != null && var4 != null) {
         RunnableScheduledFuture var5 = this.decorateTask((Callable)var1, new ScheduledThreadPoolExecutor.ScheduledFutureTask(var1, this.triggerTime(var2, var4)));
         this.delayedExecute(var5);
         return var5;
      } else {
         throw new NullPointerException();
      }
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
      if (var1 != null && var6 != null) {
         if (var4 <= 0L) {
            throw new IllegalArgumentException();
         } else {
            ScheduledThreadPoolExecutor.ScheduledFutureTask var7 = new ScheduledThreadPoolExecutor.ScheduledFutureTask(var1, (Object)null, this.triggerTime(var2, var6), var6.toNanos(var4));
            RunnableScheduledFuture var8 = this.decorateTask((Runnable)var1, var7);
            var7.outerTask = var8;
            this.delayedExecute(var8);
            return var8;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
      if (var1 != null && var6 != null) {
         if (var4 <= 0L) {
            throw new IllegalArgumentException();
         } else {
            ScheduledThreadPoolExecutor.ScheduledFutureTask var7 = new ScheduledThreadPoolExecutor.ScheduledFutureTask(var1, (Object)null, this.triggerTime(var2, var6), var6.toNanos(-var4));
            RunnableScheduledFuture var8 = this.decorateTask((Runnable)var1, var7);
            var7.outerTask = var8;
            this.delayedExecute(var8);
            return var8;
         }
      } else {
         throw new NullPointerException();
      }
   }

   public void execute(Runnable var1) {
      this.schedule(var1, 0L, TimeUnit.NANOSECONDS);
   }

   public Future<?> submit(Runnable var1) {
      return this.schedule(var1, 0L, TimeUnit.NANOSECONDS);
   }

   public <T> Future<T> submit(Runnable var1, T var2) {
      return this.schedule(Executors.callable(var1, var2), 0L, TimeUnit.NANOSECONDS);
   }

   public <T> Future<T> submit(Callable<T> var1) {
      return this.schedule(var1, 0L, TimeUnit.NANOSECONDS);
   }

   public void setContinueExistingPeriodicTasksAfterShutdownPolicy(boolean var1) {
      this.continueExistingPeriodicTasksAfterShutdown = var1;
      if (!var1 && this.isShutdown()) {
         this.onShutdown();
      }

   }

   public boolean getContinueExistingPeriodicTasksAfterShutdownPolicy() {
      return this.continueExistingPeriodicTasksAfterShutdown;
   }

   public void setExecuteExistingDelayedTasksAfterShutdownPolicy(boolean var1) {
      this.executeExistingDelayedTasksAfterShutdown = var1;
      if (!var1 && this.isShutdown()) {
         this.onShutdown();
      }

   }

   public boolean getExecuteExistingDelayedTasksAfterShutdownPolicy() {
      return this.executeExistingDelayedTasksAfterShutdown;
   }

   public void setRemoveOnCancelPolicy(boolean var1) {
      this.removeOnCancel = var1;
   }

   public boolean getRemoveOnCancelPolicy() {
      return this.removeOnCancel;
   }

   public void shutdown() {
      super.shutdown();
   }

   public List<Runnable> shutdownNow() {
      return super.shutdownNow();
   }

   public BlockingQueue<Runnable> getQueue() {
      return super.getQueue();
   }

   static class DelayedWorkQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {
      private static final int INITIAL_CAPACITY = 16;
      private RunnableScheduledFuture<?>[] queue = new RunnableScheduledFuture[16];
      private final ReentrantLock lock = new ReentrantLock();
      private int size = 0;
      private Thread leader = null;
      private final Condition available;

      DelayedWorkQueue() {
         this.available = this.lock.newCondition();
      }

      private void setIndex(RunnableScheduledFuture<?> var1, int var2) {
         if (var1 instanceof ScheduledThreadPoolExecutor.ScheduledFutureTask) {
            ((ScheduledThreadPoolExecutor.ScheduledFutureTask)var1).heapIndex = var2;
         }

      }

      private void siftUp(int var1, RunnableScheduledFuture<?> var2) {
         while(true) {
            if (var1 > 0) {
               int var3 = var1 - 1 >>> 1;
               RunnableScheduledFuture var4 = this.queue[var3];
               if (var2.compareTo(var4) < 0) {
                  this.queue[var1] = var4;
                  this.setIndex(var4, var1);
                  var1 = var3;
                  continue;
               }
            }

            this.queue[var1] = var2;
            this.setIndex(var2, var1);
            return;
         }
      }

      private void siftDown(int var1, RunnableScheduledFuture<?> var2) {
         int var4;
         for(int var3 = this.size >>> 1; var1 < var3; var1 = var4) {
            var4 = (var1 << 1) + 1;
            RunnableScheduledFuture var5 = this.queue[var4];
            int var6 = var4 + 1;
            if (var6 < this.size && var5.compareTo(this.queue[var6]) > 0) {
               var4 = var6;
               var5 = this.queue[var6];
            }

            if (var2.compareTo(var5) <= 0) {
               break;
            }

            this.queue[var1] = var5;
            this.setIndex(var5, var1);
         }

         this.queue[var1] = var2;
         this.setIndex(var2, var1);
      }

      private void grow() {
         int var1 = this.queue.length;
         int var2 = var1 + (var1 >> 1);
         if (var2 < 0) {
            var2 = Integer.MAX_VALUE;
         }

         this.queue = (RunnableScheduledFuture[])Arrays.copyOf((Object[])this.queue, var2);
      }

      private int indexOf(Object var1) {
         if (var1 != null) {
            int var2;
            if (var1 instanceof ScheduledThreadPoolExecutor.ScheduledFutureTask) {
               var2 = ((ScheduledThreadPoolExecutor.ScheduledFutureTask)var1).heapIndex;
               if (var2 >= 0 && var2 < this.size && this.queue[var2] == var1) {
                  return var2;
               }
            } else {
               for(var2 = 0; var2 < this.size; ++var2) {
                  if (var1.equals(this.queue[var2])) {
                     return var2;
                  }
               }
            }
         }

         return -1;
      }

      public boolean contains(Object var1) {
         ReentrantLock var2 = this.lock;
         var2.lock();

         boolean var3;
         try {
            var3 = this.indexOf(var1) != -1;
         } finally {
            var2.unlock();
         }

         return var3;
      }

      public boolean remove(Object var1) {
         ReentrantLock var2 = this.lock;
         var2.lock();

         boolean var4;
         try {
            int var3 = this.indexOf(var1);
            if (var3 >= 0) {
               this.setIndex(this.queue[var3], -1);
               int var10 = --this.size;
               RunnableScheduledFuture var5 = this.queue[var10];
               this.queue[var10] = null;
               if (var10 != var3) {
                  this.siftDown(var3, var5);
                  if (this.queue[var3] == var5) {
                     this.siftUp(var3, var5);
                  }
               }

               boolean var6 = true;
               return var6;
            }

            var4 = false;
         } finally {
            var2.unlock();
         }

         return var4;
      }

      public int size() {
         ReentrantLock var1 = this.lock;
         var1.lock();

         int var2;
         try {
            var2 = this.size;
         } finally {
            var1.unlock();
         }

         return var2;
      }

      public boolean isEmpty() {
         return this.size() == 0;
      }

      public int remainingCapacity() {
         return Integer.MAX_VALUE;
      }

      public RunnableScheduledFuture<?> peek() {
         ReentrantLock var1 = this.lock;
         var1.lock();

         RunnableScheduledFuture var2;
         try {
            var2 = this.queue[0];
         } finally {
            var1.unlock();
         }

         return var2;
      }

      public boolean offer(Runnable var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            RunnableScheduledFuture var2 = (RunnableScheduledFuture)var1;
            ReentrantLock var3 = this.lock;
            var3.lock();

            try {
               int var4 = this.size;
               if (var4 >= this.queue.length) {
                  this.grow();
               }

               this.size = var4 + 1;
               if (var4 == 0) {
                  this.queue[0] = var2;
                  this.setIndex(var2, 0);
               } else {
                  this.siftUp(var4, var2);
               }

               if (this.queue[0] == var2) {
                  this.leader = null;
                  this.available.signal();
               }
            } finally {
               var3.unlock();
            }

            return true;
         }
      }

      public void put(Runnable var1) {
         this.offer(var1);
      }

      public boolean add(Runnable var1) {
         return this.offer(var1);
      }

      public boolean offer(Runnable var1, long var2, TimeUnit var4) {
         return this.offer(var1);
      }

      private RunnableScheduledFuture<?> finishPoll(RunnableScheduledFuture<?> var1) {
         int var2 = --this.size;
         RunnableScheduledFuture var3 = this.queue[var2];
         this.queue[var2] = null;
         if (var2 != 0) {
            this.siftDown(0, var3);
         }

         this.setIndex(var1, -1);
         return var1;
      }

      public RunnableScheduledFuture<?> poll() {
         ReentrantLock var1 = this.lock;
         var1.lock();

         RunnableScheduledFuture var3;
         try {
            RunnableScheduledFuture var2 = this.queue[0];
            if (var2 != null && var2.getDelay(TimeUnit.NANOSECONDS) <= 0L) {
               var3 = this.finishPoll(var2);
               return var3;
            }

            var3 = null;
         } finally {
            var1.unlock();
         }

         return var3;
      }

      public RunnableScheduledFuture<?> take() throws InterruptedException {
         ReentrantLock var1 = this.lock;
         var1.lockInterruptibly();

         try {
            while(true) {
               while(true) {
                  RunnableScheduledFuture var2 = this.queue[0];
                  if (var2 != null) {
                     long var3 = var2.getDelay(TimeUnit.NANOSECONDS);
                     if (var3 <= 0L) {
                        RunnableScheduledFuture var14 = this.finishPoll(var2);
                        return var14;
                     }

                     var2 = null;
                     if (this.leader != null) {
                        this.available.await();
                     } else {
                        Thread var5 = Thread.currentThread();
                        this.leader = var5;

                        try {
                           this.available.awaitNanos(var3);
                        } finally {
                           if (this.leader == var5) {
                              this.leader = null;
                           }

                        }
                     }
                  } else {
                     this.available.await();
                  }
               }
            }
         } finally {
            if (this.leader == null && this.queue[0] != null) {
               this.available.signal();
            }

            var1.unlock();
         }
      }

      public RunnableScheduledFuture<?> poll(long var1, TimeUnit var3) throws InterruptedException {
         long var4 = var3.toNanos(var1);
         ReentrantLock var6 = this.lock;
         var6.lockInterruptibly();

         try {
            while(true) {
               RunnableScheduledFuture var7 = this.queue[0];
               if (var7 != null) {
                  long var22 = var7.getDelay(TimeUnit.NANOSECONDS);
                  if (var22 <= 0L) {
                     RunnableScheduledFuture var21 = this.finishPoll(var7);
                     return var21;
                  }

                  Thread var10;
                  if (var4 <= 0L) {
                     var10 = null;
                     return var10;
                  }

                  var7 = null;
                  if (var4 >= var22 && this.leader == null) {
                     var10 = Thread.currentThread();
                     this.leader = var10;

                     try {
                        long var11 = this.available.awaitNanos(var22);
                        var4 -= var22 - var11;
                     } finally {
                        if (this.leader == var10) {
                           this.leader = null;
                        }

                     }
                  } else {
                     var4 = this.available.awaitNanos(var4);
                  }
               } else {
                  if (var4 <= 0L) {
                     Object var8 = null;
                     return (RunnableScheduledFuture)var8;
                  }

                  var4 = this.available.awaitNanos(var4);
               }
            }
         } finally {
            if (this.leader == null && this.queue[0] != null) {
               this.available.signal();
            }

            var6.unlock();
         }
      }

      public void clear() {
         ReentrantLock var1 = this.lock;
         var1.lock();

         try {
            for(int var2 = 0; var2 < this.size; ++var2) {
               RunnableScheduledFuture var3 = this.queue[var2];
               if (var3 != null) {
                  this.queue[var2] = null;
                  this.setIndex(var3, -1);
               }
            }

            this.size = 0;
         } finally {
            var1.unlock();
         }

      }

      private RunnableScheduledFuture<?> peekExpired() {
         RunnableScheduledFuture var1 = this.queue[0];
         return var1 != null && var1.getDelay(TimeUnit.NANOSECONDS) <= 0L ? var1 : null;
      }

      public int drainTo(Collection<? super Runnable> var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var1 == this) {
            throw new IllegalArgumentException();
         } else {
            ReentrantLock var2 = this.lock;
            var2.lock();

            try {
               RunnableScheduledFuture var3;
               int var4;
               for(var4 = 0; (var3 = this.peekExpired()) != null; ++var4) {
                  var1.add(var3);
                  this.finishPoll(var3);
               }

               int var5 = var4;
               return var5;
            } finally {
               var2.unlock();
            }
         }
      }

      public int drainTo(Collection<? super Runnable> var1, int var2) {
         if (var1 == null) {
            throw new NullPointerException();
         } else if (var1 == this) {
            throw new IllegalArgumentException();
         } else if (var2 <= 0) {
            return 0;
         } else {
            ReentrantLock var3 = this.lock;
            var3.lock();

            try {
               RunnableScheduledFuture var4;
               int var5;
               for(var5 = 0; var5 < var2 && (var4 = this.peekExpired()) != null; ++var5) {
                  var1.add(var4);
                  this.finishPoll(var4);
               }

               int var6 = var5;
               return var6;
            } finally {
               var3.unlock();
            }
         }
      }

      public Object[] toArray() {
         ReentrantLock var1 = this.lock;
         var1.lock();

         Object[] var2;
         try {
            var2 = Arrays.copyOf(this.queue, this.size, Object[].class);
         } finally {
            var1.unlock();
         }

         return var2;
      }

      public <T> T[] toArray(T[] var1) {
         ReentrantLock var2 = this.lock;
         var2.lock();

         Object[] var3;
         try {
            if (var1.length >= this.size) {
               System.arraycopy(this.queue, 0, var1, 0, this.size);
               if (var1.length > this.size) {
                  var1[this.size] = null;
               }

               var3 = var1;
               return var3;
            }

            var3 = (Object[])Arrays.copyOf(this.queue, this.size, var1.getClass());
         } finally {
            var2.unlock();
         }

         return var3;
      }

      public Iterator<Runnable> iterator() {
         return new ScheduledThreadPoolExecutor.DelayedWorkQueue.Itr((RunnableScheduledFuture[])Arrays.copyOf((Object[])this.queue, this.size));
      }

      private class Itr implements Iterator<Runnable> {
         final RunnableScheduledFuture<?>[] array;
         int cursor = 0;
         int lastRet = -1;

         Itr(RunnableScheduledFuture<?>[] var2) {
            this.array = var2;
         }

         public boolean hasNext() {
            return this.cursor < this.array.length;
         }

         public Runnable next() {
            if (this.cursor >= this.array.length) {
               throw new NoSuchElementException();
            } else {
               this.lastRet = this.cursor;
               return this.array[this.cursor++];
            }
         }

         public void remove() {
            if (this.lastRet < 0) {
               throw new IllegalStateException();
            } else {
               DelayedWorkQueue.this.remove(this.array[this.lastRet]);
               this.lastRet = -1;
            }
         }
      }
   }

   private class ScheduledFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
      private final long sequenceNumber;
      private long time;
      private final long period;
      RunnableScheduledFuture<V> outerTask = this;
      int heapIndex;

      ScheduledFutureTask(Runnable var2, V var3, long var4) {
         super(var2, var3);
         this.time = var4;
         this.period = 0L;
         this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
      }

      ScheduledFutureTask(Runnable var2, V var3, long var4, long var6) {
         super(var2, var3);
         this.time = var4;
         this.period = var6;
         this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
      }

      ScheduledFutureTask(Callable<V> var2, long var3) {
         super(var2);
         this.time = var3;
         this.period = 0L;
         this.sequenceNumber = ScheduledThreadPoolExecutor.sequencer.getAndIncrement();
      }

      public long getDelay(TimeUnit var1) {
         return var1.convert(this.time - ScheduledThreadPoolExecutor.this.now(), TimeUnit.NANOSECONDS);
      }

      public int compareTo(Delayed var1) {
         if (var1 == this) {
            return 0;
         } else if (var1 instanceof ScheduledThreadPoolExecutor.ScheduledFutureTask) {
            ScheduledThreadPoolExecutor.ScheduledFutureTask var5 = (ScheduledThreadPoolExecutor.ScheduledFutureTask)var1;
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
            return var2 < 0L ? -1 : (var2 > 0L ? 1 : 0);
         }
      }

      public boolean isPeriodic() {
         return this.period != 0L;
      }

      private void setNextRunTime() {
         long var1 = this.period;
         if (var1 > 0L) {
            this.time += var1;
         } else {
            this.time = ScheduledThreadPoolExecutor.this.triggerTime(-var1);
         }

      }

      public boolean cancel(boolean var1) {
         boolean var2 = super.cancel(var1);
         if (var2 && ScheduledThreadPoolExecutor.this.removeOnCancel && this.heapIndex >= 0) {
            ScheduledThreadPoolExecutor.this.remove(this);
         }

         return var2;
      }

      public void run() {
         boolean var1 = this.isPeriodic();
         if (!ScheduledThreadPoolExecutor.this.canRunInCurrentRunState(var1)) {
            this.cancel(false);
         } else if (!var1) {
            access$201(this);
         } else if (access$301(this)) {
            this.setNextRunTime();
            ScheduledThreadPoolExecutor.this.reExecutePeriodic(this.outerTask);
         }

      }

      // $FF: synthetic method
      static void access$201(ScheduledThreadPoolExecutor.ScheduledFutureTask var0) {
         var0.run();
      }

      // $FF: synthetic method
      static boolean access$301(ScheduledThreadPoolExecutor.ScheduledFutureTask var0) {
         return var0.runAndReset();
      }
   }
}
