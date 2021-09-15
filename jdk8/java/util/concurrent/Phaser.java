package java.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class Phaser {
   private volatile long state;
   private static final int MAX_PARTIES = 65535;
   private static final int MAX_PHASE = Integer.MAX_VALUE;
   private static final int PARTIES_SHIFT = 16;
   private static final int PHASE_SHIFT = 32;
   private static final int UNARRIVED_MASK = 65535;
   private static final long PARTIES_MASK = 4294901760L;
   private static final long COUNTS_MASK = 4294967295L;
   private static final long TERMINATION_BIT = Long.MIN_VALUE;
   private static final int ONE_ARRIVAL = 1;
   private static final int ONE_PARTY = 65536;
   private static final int ONE_DEREGISTER = 65537;
   private static final int EMPTY = 1;
   private final Phaser parent;
   private final Phaser root;
   private final AtomicReference<Phaser.QNode> evenQ;
   private final AtomicReference<Phaser.QNode> oddQ;
   private static final int NCPU = Runtime.getRuntime().availableProcessors();
   static final int SPINS_PER_ARRIVAL;
   private static final Unsafe UNSAFE;
   private static final long stateOffset;

   private static int unarrivedOf(long var0) {
      int var2 = (int)var0;
      return var2 == 1 ? 0 : var2 & '\uffff';
   }

   private static int partiesOf(long var0) {
      return (int)var0 >>> 16;
   }

   private static int phaseOf(long var0) {
      return (int)(var0 >>> 32);
   }

   private static int arrivedOf(long var0) {
      int var2 = (int)var0;
      return var2 == 1 ? 0 : (var2 >>> 16) - (var2 & '\uffff');
   }

   private AtomicReference<Phaser.QNode> queueFor(int var1) {
      return (var1 & 1) == 0 ? this.evenQ : this.oddQ;
   }

   private String badArrive(long var1) {
      return "Attempted arrival of unregistered party for " + this.stateToString(var1);
   }

   private String badRegister(long var1) {
      return "Attempt to register more than 65535 parties for " + this.stateToString(var1);
   }

   private int doArrive(int var1) {
      Phaser var2 = this.root;

      long var3;
      int var5;
      int var7;
      do {
         var3 = var2 == this ? this.state : this.reconcileState();
         var5 = (int)(var3 >>> 32);
         if (var5 < 0) {
            return var5;
         }

         int var6 = (int)var3;
         var7 = var6 == 1 ? 0 : var6 & '\uffff';
         if (var7 <= 0) {
            throw new IllegalStateException(this.badArrive(var3));
         }
      } while(!UNSAFE.compareAndSwapLong(this, stateOffset, var3, var3 -= (long)var1));

      if (var7 == 1) {
         long var8 = var3 & 4294901760L;
         int var10 = (int)var8 >>> 16;
         if (var2 == this) {
            if (this.onAdvance(var5, var10)) {
               var8 |= Long.MIN_VALUE;
            } else if (var10 == 0) {
               var8 |= 1L;
            } else {
               var8 |= (long)var10;
            }

            int var11 = var5 + 1 & Integer.MAX_VALUE;
            var8 |= (long)var11 << 32;
            UNSAFE.compareAndSwapLong(this, stateOffset, var3, var8);
            this.releaseWaiters(var5);
         } else if (var10 == 0) {
            var5 = this.parent.doArrive(65537);
            UNSAFE.compareAndSwapLong(this, stateOffset, var3, var3 | 1L);
         } else {
            var5 = this.parent.doArrive(1);
         }
      }

      return var5;
   }

   private int doRegister(int var1) {
      long var2 = (long)var1 << 16 | (long)var1;
      Phaser var4 = this.parent;

      int var5;
      while(true) {
         long var6 = var4 == null ? this.state : this.reconcileState();
         int var8 = (int)var6;
         int var9 = var8 >>> 16;
         int var10 = var8 & '\uffff';
         if (var1 > '\uffff' - var9) {
            throw new IllegalStateException(this.badRegister(var6));
         }

         var5 = (int)(var6 >>> 32);
         if (var5 < 0) {
            break;
         }

         if (var8 != 1) {
            if (var4 == null || this.reconcileState() == var6) {
               if (var10 == 0) {
                  this.root.internalAwaitAdvance(var5, (Phaser.QNode)null);
               } else if (UNSAFE.compareAndSwapLong(this, stateOffset, var6, var6 + var2)) {
                  break;
               }
            }
         } else if (var4 == null) {
            long var11 = (long)var5 << 32 | var2;
            if (UNSAFE.compareAndSwapLong(this, stateOffset, var6, var11)) {
               break;
            }
         } else {
            synchronized(this) {
               if (this.state == var6) {
                  var5 = var4.doRegister(1);
                  if (var5 < 0) {
                     break;
                  } else {
                     while(!UNSAFE.compareAndSwapLong(this, stateOffset, var6, (long)var5 << 32 | var2)) {
                        var6 = this.state;
                        var5 = (int)(this.root.state >>> 32);
                     }

                     return var5;
                  }
               }
            }
         }
      }

      return var5;
   }

   private long reconcileState() {
      Phaser var1 = this.root;
      long var2 = this.state;
      int var4;
      int var5;
      if (var1 != this) {
         while((var4 = (int)(var1.state >>> 32)) != (int)(var2 >>> 32) && !UNSAFE.compareAndSwapLong(this, stateOffset, var2, var2 = (long)var4 << 32 | (var4 < 0 ? var2 & 4294967295L : ((var5 = (int)var2 >>> 16) == 0 ? 1L : var2 & 4294901760L | (long)var5)))) {
            var2 = this.state;
         }
      }

      return var2;
   }

   public Phaser() {
      this((Phaser)null, 0);
   }

   public Phaser(int var1) {
      this((Phaser)null, var1);
   }

   public Phaser(Phaser var1) {
      this(var1, 0);
   }

   public Phaser(Phaser var1, int var2) {
      if (var2 >>> 16 != 0) {
         throw new IllegalArgumentException("Illegal number of parties");
      } else {
         int var3 = 0;
         this.parent = var1;
         if (var1 != null) {
            Phaser var4 = var1.root;
            this.root = var4;
            this.evenQ = var4.evenQ;
            this.oddQ = var4.oddQ;
            if (var2 != 0) {
               var3 = var1.doRegister(1);
            }
         } else {
            this.root = this;
            this.evenQ = new AtomicReference();
            this.oddQ = new AtomicReference();
         }

         this.state = var2 == 0 ? 1L : (long)var3 << 32 | (long)var2 << 16 | (long)var2;
      }
   }

   public int register() {
      return this.doRegister(1);
   }

   public int bulkRegister(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else {
         return var1 == 0 ? this.getPhase() : this.doRegister(var1);
      }
   }

   public int arrive() {
      return this.doArrive(1);
   }

   public int arriveAndDeregister() {
      return this.doArrive(65537);
   }

   public int arriveAndAwaitAdvance() {
      Phaser var1 = this.root;

      long var2;
      int var4;
      int var6;
      do {
         var2 = var1 == this ? this.state : this.reconcileState();
         var4 = (int)(var2 >>> 32);
         if (var4 < 0) {
            return var4;
         }

         int var5 = (int)var2;
         var6 = var5 == 1 ? 0 : var5 & '\uffff';
         if (var6 <= 0) {
            throw new IllegalStateException(this.badArrive(var2));
         }
      } while(!UNSAFE.compareAndSwapLong(this, stateOffset, var2, --var2));

      if (var6 > 1) {
         return var1.internalAwaitAdvance(var4, (Phaser.QNode)null);
      } else if (var1 != this) {
         return this.parent.arriveAndAwaitAdvance();
      } else {
         long var7 = var2 & 4294901760L;
         int var9 = (int)var7 >>> 16;
         if (this.onAdvance(var4, var9)) {
            var7 |= Long.MIN_VALUE;
         } else if (var9 == 0) {
            var7 |= 1L;
         } else {
            var7 |= (long)var9;
         }

         int var10 = var4 + 1 & Integer.MAX_VALUE;
         var7 |= (long)var10 << 32;
         if (!UNSAFE.compareAndSwapLong(this, stateOffset, var2, var7)) {
            return (int)(this.state >>> 32);
         } else {
            this.releaseWaiters(var4);
            return var10;
         }
      }
   }

   public int awaitAdvance(int var1) {
      Phaser var2 = this.root;
      long var3 = var2 == this ? this.state : this.reconcileState();
      int var5 = (int)(var3 >>> 32);
      if (var1 < 0) {
         return var1;
      } else {
         return var5 == var1 ? var2.internalAwaitAdvance(var1, (Phaser.QNode)null) : var5;
      }
   }

   public int awaitAdvanceInterruptibly(int var1) throws InterruptedException {
      Phaser var2 = this.root;
      long var3 = var2 == this ? this.state : this.reconcileState();
      int var5 = (int)(var3 >>> 32);
      if (var1 < 0) {
         return var1;
      } else {
         if (var5 == var1) {
            Phaser.QNode var6 = new Phaser.QNode(this, var1, true, false, 0L);
            var5 = var2.internalAwaitAdvance(var1, var6);
            if (var6.wasInterrupted) {
               throw new InterruptedException();
            }
         }

         return var5;
      }
   }

   public int awaitAdvanceInterruptibly(int var1, long var2, TimeUnit var4) throws InterruptedException, TimeoutException {
      long var5 = var4.toNanos(var2);
      Phaser var7 = this.root;
      long var8 = var7 == this ? this.state : this.reconcileState();
      int var10 = (int)(var8 >>> 32);
      if (var1 < 0) {
         return var1;
      } else {
         if (var10 == var1) {
            Phaser.QNode var11 = new Phaser.QNode(this, var1, true, true, var5);
            var10 = var7.internalAwaitAdvance(var1, var11);
            if (var11.wasInterrupted) {
               throw new InterruptedException();
            }

            if (var10 == var1) {
               throw new TimeoutException();
            }
         }

         return var10;
      }
   }

   public void forceTermination() {
      Phaser var1 = this.root;

      long var2;
      do {
         if ((var2 = var1.state) < 0L) {
            return;
         }
      } while(!UNSAFE.compareAndSwapLong(var1, stateOffset, var2, var2 | Long.MIN_VALUE));

      this.releaseWaiters(0);
      this.releaseWaiters(1);
   }

   public final int getPhase() {
      return (int)(this.root.state >>> 32);
   }

   public int getRegisteredParties() {
      return partiesOf(this.state);
   }

   public int getArrivedParties() {
      return arrivedOf(this.reconcileState());
   }

   public int getUnarrivedParties() {
      return unarrivedOf(this.reconcileState());
   }

   public Phaser getParent() {
      return this.parent;
   }

   public Phaser getRoot() {
      return this.root;
   }

   public boolean isTerminated() {
      return this.root.state < 0L;
   }

   protected boolean onAdvance(int var1, int var2) {
      return var2 == 0;
   }

   public String toString() {
      return this.stateToString(this.reconcileState());
   }

   private String stateToString(long var1) {
      return super.toString() + "[phase = " + phaseOf(var1) + " parties = " + partiesOf(var1) + " arrived = " + arrivedOf(var1) + "]";
   }

   private void releaseWaiters(int var1) {
      AtomicReference var4 = (var1 & 1) == 0 ? this.evenQ : this.oddQ;

      Phaser.QNode var2;
      while((var2 = (Phaser.QNode)var4.get()) != null && var2.phase != (int)(this.root.state >>> 32)) {
         Thread var3;
         if (var4.compareAndSet(var2, var2.next) && (var3 = var2.thread) != null) {
            var2.thread = null;
            LockSupport.unpark(var3);
         }
      }

   }

   private int abortWait(int var1) {
      AtomicReference var2 = (var1 & 1) == 0 ? this.evenQ : this.oddQ;

      while(true) {
         Phaser.QNode var4 = (Phaser.QNode)var2.get();
         int var5 = (int)(this.root.state >>> 32);
         Thread var3;
         if (var4 == null || (var3 = var4.thread) != null && var4.phase == var5) {
            return var5;
         }

         if (var2.compareAndSet(var4, var4.next) && var3 != null) {
            var4.thread = null;
            LockSupport.unpark(var3);
         }
      }
   }

   private int internalAwaitAdvance(int var1, Phaser.QNode var2) {
      this.releaseWaiters(var1 - 1);
      boolean var3 = false;
      int var4 = 0;
      int var5 = SPINS_PER_ARRIVAL;

      long var6;
      int var8;
      while((var8 = (int)((var6 = this.state) >>> 32)) == var1) {
         if (var2 == null) {
            int var12 = (int)var6 & '\uffff';
            if (var12 != var4) {
               var4 = var12;
               if (var12 < NCPU) {
                  var5 += SPINS_PER_ARRIVAL;
               }
            }

            boolean var13 = Thread.interrupted();
            if (!var13) {
               --var5;
               if (var5 >= 0) {
                  continue;
               }
            }

            var2 = new Phaser.QNode(this, var1, false, false, 0L);
            var2.wasInterrupted = var13;
         } else {
            if (var2.isReleasable()) {
               break;
            }

            if (!var3) {
               AtomicReference var9 = (var1 & 1) == 0 ? this.evenQ : this.oddQ;
               Phaser.QNode var10 = var2.next = (Phaser.QNode)var9.get();
               if ((var10 == null || var10.phase == var1) && (int)(this.state >>> 32) == var1) {
                  var3 = var9.compareAndSet(var10, var2);
               }
            } else {
               try {
                  ForkJoinPool.managedBlock(var2);
               } catch (InterruptedException var11) {
                  var2.wasInterrupted = true;
               }
            }
         }
      }

      if (var2 != null) {
         if (var2.thread != null) {
            var2.thread = null;
         }

         if (var2.wasInterrupted && !var2.interruptible) {
            Thread.currentThread().interrupt();
         }

         if (var8 == var1 && (var8 = (int)(this.state >>> 32)) == var1) {
            return this.abortWait(var1);
         }
      }

      this.releaseWaiters(var1);
      return var8;
   }

   static {
      SPINS_PER_ARRIVAL = NCPU < 2 ? 1 : 256;

      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = Phaser.class;
         stateOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("state"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }

   static final class QNode implements ForkJoinPool.ManagedBlocker {
      final Phaser phaser;
      final int phase;
      final boolean interruptible;
      final boolean timed;
      boolean wasInterrupted;
      long nanos;
      final long deadline;
      volatile Thread thread;
      Phaser.QNode next;

      QNode(Phaser var1, int var2, boolean var3, boolean var4, long var5) {
         this.phaser = var1;
         this.phase = var2;
         this.interruptible = var3;
         this.nanos = var5;
         this.timed = var4;
         this.deadline = var4 ? System.nanoTime() + var5 : 0L;
         this.thread = Thread.currentThread();
      }

      public boolean isReleasable() {
         if (this.thread == null) {
            return true;
         } else if (this.phaser.getPhase() != this.phase) {
            this.thread = null;
            return true;
         } else {
            if (Thread.interrupted()) {
               this.wasInterrupted = true;
            }

            if (this.wasInterrupted && this.interruptible) {
               this.thread = null;
               return true;
            } else {
               if (this.timed) {
                  if (this.nanos > 0L) {
                     this.nanos = this.deadline - System.nanoTime();
                  }

                  if (this.nanos <= 0L) {
                     this.thread = null;
                     return true;
                  }
               }

               return false;
            }
         }
      }

      public boolean block() {
         if (this.isReleasable()) {
            return true;
         } else {
            if (!this.timed) {
               LockSupport.park(this);
            } else if (this.nanos > 0L) {
               LockSupport.parkNanos(this, this.nanos);
            }

            return this.isReleasable();
         }
      }
   }
}
