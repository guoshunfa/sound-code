package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class ReentrantReadWriteLock implements ReadWriteLock, Serializable {
   private static final long serialVersionUID = -6992448646407690164L;
   private final ReentrantReadWriteLock.ReadLock readerLock;
   private final ReentrantReadWriteLock.WriteLock writerLock;
   final ReentrantReadWriteLock.Sync sync;
   private static final Unsafe UNSAFE;
   private static final long TID_OFFSET;

   public ReentrantReadWriteLock() {
      this(false);
   }

   public ReentrantReadWriteLock(boolean var1) {
      this.sync = (ReentrantReadWriteLock.Sync)(var1 ? new ReentrantReadWriteLock.FairSync() : new ReentrantReadWriteLock.NonfairSync());
      this.readerLock = new ReentrantReadWriteLock.ReadLock(this);
      this.writerLock = new ReentrantReadWriteLock.WriteLock(this);
   }

   public ReentrantReadWriteLock.WriteLock writeLock() {
      return this.writerLock;
   }

   public ReentrantReadWriteLock.ReadLock readLock() {
      return this.readerLock;
   }

   public final boolean isFair() {
      return this.sync instanceof ReentrantReadWriteLock.FairSync;
   }

   protected Thread getOwner() {
      return this.sync.getOwner();
   }

   public int getReadLockCount() {
      return this.sync.getReadLockCount();
   }

   public boolean isWriteLocked() {
      return this.sync.isWriteLocked();
   }

   public boolean isWriteLockedByCurrentThread() {
      return this.sync.isHeldExclusively();
   }

   public int getWriteHoldCount() {
      return this.sync.getWriteHoldCount();
   }

   public int getReadHoldCount() {
      return this.sync.getReadHoldCount();
   }

   protected Collection<Thread> getQueuedWriterThreads() {
      return this.sync.getExclusiveQueuedThreads();
   }

   protected Collection<Thread> getQueuedReaderThreads() {
      return this.sync.getSharedQueuedThreads();
   }

   public final boolean hasQueuedThreads() {
      return this.sync.hasQueuedThreads();
   }

   public final boolean hasQueuedThread(Thread var1) {
      return this.sync.isQueued(var1);
   }

   public final int getQueueLength() {
      return this.sync.getQueueLength();
   }

   protected Collection<Thread> getQueuedThreads() {
      return this.sync.getQueuedThreads();
   }

   public boolean hasWaiters(Condition var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof AbstractQueuedSynchronizer.ConditionObject)) {
         throw new IllegalArgumentException("not owner");
      } else {
         return this.sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)var1);
      }
   }

   public int getWaitQueueLength(Condition var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof AbstractQueuedSynchronizer.ConditionObject)) {
         throw new IllegalArgumentException("not owner");
      } else {
         return this.sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)var1);
      }
   }

   protected Collection<Thread> getWaitingThreads(Condition var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof AbstractQueuedSynchronizer.ConditionObject)) {
         throw new IllegalArgumentException("not owner");
      } else {
         return this.sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)var1);
      }
   }

   public String toString() {
      int var1 = this.sync.getCount();
      int var2 = ReentrantReadWriteLock.Sync.exclusiveCount(var1);
      int var3 = ReentrantReadWriteLock.Sync.sharedCount(var1);
      return super.toString() + "[Write locks = " + var2 + ", Read locks = " + var3 + "]";
   }

   static final long getThreadId(Thread var0) {
      return UNSAFE.getLongVolatile(var0, TID_OFFSET);
   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = Thread.class;
         TID_OFFSET = UNSAFE.objectFieldOffset(var0.getDeclaredField("tid"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   public static class WriteLock implements Lock, Serializable {
      private static final long serialVersionUID = -4992448646407690164L;
      private final ReentrantReadWriteLock.Sync sync;

      protected WriteLock(ReentrantReadWriteLock var1) {
         this.sync = var1.sync;
      }

      public void lock() {
         this.sync.acquire(1);
      }

      public void lockInterruptibly() throws InterruptedException {
         this.sync.acquireInterruptibly(1);
      }

      public boolean tryLock() {
         return this.sync.tryWriteLock();
      }

      public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
         return this.sync.tryAcquireNanos(1, var3.toNanos(var1));
      }

      public void unlock() {
         this.sync.release(1);
      }

      public Condition newCondition() {
         return this.sync.newCondition();
      }

      public String toString() {
         Thread var1 = this.sync.getOwner();
         return super.toString() + (var1 == null ? "[Unlocked]" : "[Locked by thread " + var1.getName() + "]");
      }

      public boolean isHeldByCurrentThread() {
         return this.sync.isHeldExclusively();
      }

      public int getHoldCount() {
         return this.sync.getWriteHoldCount();
      }
   }

   public static class ReadLock implements Lock, Serializable {
      private static final long serialVersionUID = -5992448646407690164L;
      private final ReentrantReadWriteLock.Sync sync;

      protected ReadLock(ReentrantReadWriteLock var1) {
         this.sync = var1.sync;
      }

      public void lock() {
         this.sync.acquireShared(1);
      }

      public void lockInterruptibly() throws InterruptedException {
         this.sync.acquireSharedInterruptibly(1);
      }

      public boolean tryLock() {
         return this.sync.tryReadLock();
      }

      public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
         return this.sync.tryAcquireSharedNanos(1, var3.toNanos(var1));
      }

      public void unlock() {
         this.sync.releaseShared(1);
      }

      public Condition newCondition() {
         throw new UnsupportedOperationException();
      }

      public String toString() {
         int var1 = this.sync.getReadLockCount();
         return super.toString() + "[Read locks = " + var1 + "]";
      }
   }

   static final class FairSync extends ReentrantReadWriteLock.Sync {
      private static final long serialVersionUID = -2274990926593161451L;

      final boolean writerShouldBlock() {
         return this.hasQueuedPredecessors();
      }

      final boolean readerShouldBlock() {
         return this.hasQueuedPredecessors();
      }
   }

   static final class NonfairSync extends ReentrantReadWriteLock.Sync {
      private static final long serialVersionUID = -8159625535654395037L;

      final boolean writerShouldBlock() {
         return false;
      }

      final boolean readerShouldBlock() {
         return this.apparentlyFirstQueuedIsExclusive();
      }
   }

   abstract static class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = 6317671515068378041L;
      static final int SHARED_SHIFT = 16;
      static final int SHARED_UNIT = 65536;
      static final int MAX_COUNT = 65535;
      static final int EXCLUSIVE_MASK = 65535;
      private transient ReentrantReadWriteLock.Sync.ThreadLocalHoldCounter readHolds = new ReentrantReadWriteLock.Sync.ThreadLocalHoldCounter();
      private transient ReentrantReadWriteLock.Sync.HoldCounter cachedHoldCounter;
      private transient Thread firstReader = null;
      private transient int firstReaderHoldCount;

      static int sharedCount(int var0) {
         return var0 >>> 16;
      }

      static int exclusiveCount(int var0) {
         return var0 & '\uffff';
      }

      Sync() {
         this.setState(this.getState());
      }

      abstract boolean readerShouldBlock();

      abstract boolean writerShouldBlock();

      protected final boolean tryRelease(int var1) {
         if (!this.isHeldExclusively()) {
            throw new IllegalMonitorStateException();
         } else {
            int var2 = this.getState() - var1;
            boolean var3 = exclusiveCount(var2) == 0;
            if (var3) {
               this.setExclusiveOwnerThread((Thread)null);
            }

            this.setState(var2);
            return var3;
         }
      }

      protected final boolean tryAcquire(int var1) {
         Thread var2 = Thread.currentThread();
         int var3 = this.getState();
         int var4 = exclusiveCount(var3);
         if (var3 != 0) {
            if (var4 != 0 && var2 == this.getExclusiveOwnerThread()) {
               if (var4 + exclusiveCount(var1) > 65535) {
                  throw new Error("Maximum lock count exceeded");
               } else {
                  this.setState(var3 + var1);
                  return true;
               }
            } else {
               return false;
            }
         } else if (!this.writerShouldBlock() && this.compareAndSetState(var3, var3 + var1)) {
            this.setExclusiveOwnerThread(var2);
            return true;
         } else {
            return false;
         }
      }

      protected final boolean tryReleaseShared(int var1) {
         Thread var2 = Thread.currentThread();
         int var4;
         if (this.firstReader == var2) {
            if (this.firstReaderHoldCount == 1) {
               this.firstReader = null;
            } else {
               --this.firstReaderHoldCount;
            }
         } else {
            ReentrantReadWriteLock.Sync.HoldCounter var3 = this.cachedHoldCounter;
            if (var3 == null || var3.tid != ReentrantReadWriteLock.getThreadId(var2)) {
               var3 = (ReentrantReadWriteLock.Sync.HoldCounter)this.readHolds.get();
            }

            var4 = var3.count;
            if (var4 <= 1) {
               this.readHolds.remove();
               if (var4 <= 0) {
                  throw this.unmatchedUnlockException();
               }
            }

            --var3.count;
         }

         int var5;
         do {
            var5 = this.getState();
            var4 = var5 - 65536;
         } while(!this.compareAndSetState(var5, var4));

         return var4 == 0;
      }

      private IllegalMonitorStateException unmatchedUnlockException() {
         return new IllegalMonitorStateException("attempt to unlock read lock, not locked by current thread");
      }

      protected final int tryAcquireShared(int var1) {
         Thread var2 = Thread.currentThread();
         int var3 = this.getState();
         if (exclusiveCount(var3) != 0 && this.getExclusiveOwnerThread() != var2) {
            return -1;
         } else {
            int var4 = sharedCount(var3);
            if (!this.readerShouldBlock() && var4 < 65535 && this.compareAndSetState(var3, var3 + 65536)) {
               if (var4 == 0) {
                  this.firstReader = var2;
                  this.firstReaderHoldCount = 1;
               } else if (this.firstReader == var2) {
                  ++this.firstReaderHoldCount;
               } else {
                  ReentrantReadWriteLock.Sync.HoldCounter var5 = this.cachedHoldCounter;
                  if (var5 != null && var5.tid == ReentrantReadWriteLock.getThreadId(var2)) {
                     if (var5.count == 0) {
                        this.readHolds.set(var5);
                     }
                  } else {
                     this.cachedHoldCounter = var5 = (ReentrantReadWriteLock.Sync.HoldCounter)this.readHolds.get();
                  }

                  ++var5.count;
               }

               return 1;
            } else {
               return this.fullTryAcquireShared(var2);
            }
         }
      }

      final int fullTryAcquireShared(Thread var1) {
         ReentrantReadWriteLock.Sync.HoldCounter var2 = null;

         int var3;
         do {
            var3 = this.getState();
            if (exclusiveCount(var3) != 0) {
               if (this.getExclusiveOwnerThread() != var1) {
                  return -1;
               }
            } else if (this.readerShouldBlock() && this.firstReader != var1) {
               if (var2 == null) {
                  var2 = this.cachedHoldCounter;
                  if (var2 == null || var2.tid != ReentrantReadWriteLock.getThreadId(var1)) {
                     var2 = (ReentrantReadWriteLock.Sync.HoldCounter)this.readHolds.get();
                     if (var2.count == 0) {
                        this.readHolds.remove();
                     }
                  }
               }

               if (var2.count == 0) {
                  return -1;
               }
            }

            if (sharedCount(var3) == 65535) {
               throw new Error("Maximum lock count exceeded");
            }
         } while(!this.compareAndSetState(var3, var3 + 65536));

         if (sharedCount(var3) == 0) {
            this.firstReader = var1;
            this.firstReaderHoldCount = 1;
         } else if (this.firstReader == var1) {
            ++this.firstReaderHoldCount;
         } else {
            if (var2 == null) {
               var2 = this.cachedHoldCounter;
            }

            if (var2 != null && var2.tid == ReentrantReadWriteLock.getThreadId(var1)) {
               if (var2.count == 0) {
                  this.readHolds.set(var2);
               }
            } else {
               var2 = (ReentrantReadWriteLock.Sync.HoldCounter)this.readHolds.get();
            }

            ++var2.count;
            this.cachedHoldCounter = var2;
         }

         return 1;
      }

      final boolean tryWriteLock() {
         Thread var1 = Thread.currentThread();
         int var2 = this.getState();
         if (var2 != 0) {
            int var3 = exclusiveCount(var2);
            if (var3 == 0 || var1 != this.getExclusiveOwnerThread()) {
               return false;
            }

            if (var3 == 65535) {
               throw new Error("Maximum lock count exceeded");
            }
         }

         if (!this.compareAndSetState(var2, var2 + 1)) {
            return false;
         } else {
            this.setExclusiveOwnerThread(var1);
            return true;
         }
      }

      final boolean tryReadLock() {
         Thread var1 = Thread.currentThread();

         int var2;
         int var3;
         do {
            var2 = this.getState();
            if (exclusiveCount(var2) != 0 && this.getExclusiveOwnerThread() != var1) {
               return false;
            }

            var3 = sharedCount(var2);
            if (var3 == 65535) {
               throw new Error("Maximum lock count exceeded");
            }
         } while(!this.compareAndSetState(var2, var2 + 65536));

         if (var3 == 0) {
            this.firstReader = var1;
            this.firstReaderHoldCount = 1;
         } else if (this.firstReader == var1) {
            ++this.firstReaderHoldCount;
         } else {
            ReentrantReadWriteLock.Sync.HoldCounter var4 = this.cachedHoldCounter;
            if (var4 != null && var4.tid == ReentrantReadWriteLock.getThreadId(var1)) {
               if (var4.count == 0) {
                  this.readHolds.set(var4);
               }
            } else {
               this.cachedHoldCounter = var4 = (ReentrantReadWriteLock.Sync.HoldCounter)this.readHolds.get();
            }

            ++var4.count;
         }

         return true;
      }

      protected final boolean isHeldExclusively() {
         return this.getExclusiveOwnerThread() == Thread.currentThread();
      }

      final AbstractQueuedSynchronizer.ConditionObject newCondition() {
         return new AbstractQueuedSynchronizer.ConditionObject();
      }

      final Thread getOwner() {
         return exclusiveCount(this.getState()) == 0 ? null : this.getExclusiveOwnerThread();
      }

      final int getReadLockCount() {
         return sharedCount(this.getState());
      }

      final boolean isWriteLocked() {
         return exclusiveCount(this.getState()) != 0;
      }

      final int getWriteHoldCount() {
         return this.isHeldExclusively() ? exclusiveCount(this.getState()) : 0;
      }

      final int getReadHoldCount() {
         if (this.getReadLockCount() == 0) {
            return 0;
         } else {
            Thread var1 = Thread.currentThread();
            if (this.firstReader == var1) {
               return this.firstReaderHoldCount;
            } else {
               ReentrantReadWriteLock.Sync.HoldCounter var2 = this.cachedHoldCounter;
               if (var2 != null && var2.tid == ReentrantReadWriteLock.getThreadId(var1)) {
                  return var2.count;
               } else {
                  int var3 = ((ReentrantReadWriteLock.Sync.HoldCounter)this.readHolds.get()).count;
                  if (var3 == 0) {
                     this.readHolds.remove();
                  }

                  return var3;
               }
            }
         }
      }

      private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
         var1.defaultReadObject();
         this.readHolds = new ReentrantReadWriteLock.Sync.ThreadLocalHoldCounter();
         this.setState(0);
      }

      final int getCount() {
         return this.getState();
      }

      static final class ThreadLocalHoldCounter extends ThreadLocal<ReentrantReadWriteLock.Sync.HoldCounter> {
         public ReentrantReadWriteLock.Sync.HoldCounter initialValue() {
            return new ReentrantReadWriteLock.Sync.HoldCounter();
         }
      }

      static final class HoldCounter {
         int count = 0;
         final long tid = ReentrantReadWriteLock.getThreadId(Thread.currentThread());
      }
   }
}
