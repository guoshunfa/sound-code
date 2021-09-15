package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import sun.misc.Contended;
import sun.misc.Unsafe;

@Contended
public class ForkJoinPool extends AbstractExecutorService {
   static final int SMASK = 65535;
   static final int MAX_CAP = 32767;
   static final int EVENMASK = 65534;
   static final int SQMASK = 126;
   static final int SCANNING = 1;
   static final int INACTIVE = Integer.MIN_VALUE;
   static final int SS_SEQ = 65536;
   static final int MODE_MASK = -65536;
   static final int LIFO_QUEUE = 0;
   static final int FIFO_QUEUE = 65536;
   static final int SHARED_QUEUE = Integer.MIN_VALUE;
   public static final ForkJoinPool.ForkJoinWorkerThreadFactory defaultForkJoinWorkerThreadFactory;
   private static final RuntimePermission modifyThreadPermission;
   static final ForkJoinPool common;
   static final int commonParallelism;
   private static int commonMaxSpares;
   private static int poolNumberSequence;
   private static final long IDLE_TIMEOUT = 2000000000L;
   private static final long TIMEOUT_SLOP = 20000000L;
   private static final int DEFAULT_COMMON_MAX_SPARES = 256;
   private static final int SPINS = 0;
   private static final int SEED_INCREMENT = -1640531527;
   private static final long SP_MASK = 4294967295L;
   private static final long UC_MASK = -4294967296L;
   private static final int AC_SHIFT = 48;
   private static final long AC_UNIT = 281474976710656L;
   private static final long AC_MASK = -281474976710656L;
   private static final int TC_SHIFT = 32;
   private static final long TC_UNIT = 4294967296L;
   private static final long TC_MASK = 281470681743360L;
   private static final long ADD_WORKER = 140737488355328L;
   private static final int RSLOCK = 1;
   private static final int RSIGNAL = 2;
   private static final int STARTED = 4;
   private static final int STOP = 536870912;
   private static final int TERMINATED = 1073741824;
   private static final int SHUTDOWN = Integer.MIN_VALUE;
   volatile long ctl;
   volatile int runState;
   final int config;
   int indexSeed;
   volatile ForkJoinPool.WorkQueue[] workQueues;
   final ForkJoinPool.ForkJoinWorkerThreadFactory factory;
   final Thread.UncaughtExceptionHandler ueh;
   final String workerNamePrefix;
   volatile AtomicLong stealCounter;
   private static final Unsafe U;
   private static final int ABASE;
   private static final int ASHIFT;
   private static final long CTL;
   private static final long RUNSTATE;
   private static final long STEALCOUNTER;
   private static final long PARKBLOCKER;
   private static final long QTOP;
   private static final long QLOCK;
   private static final long QSCANSTATE;
   private static final long QPARKER;
   private static final long QCURRENTSTEAL;
   private static final long QCURRENTJOIN;

   private static void checkPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(modifyThreadPermission);
      }

   }

   private static final synchronized int nextPoolId() {
      return ++poolNumberSequence;
   }

   private int lockRunState() {
      int var1;
      return ((var1 = this.runState) & 1) == 0 && U.compareAndSwapInt(this, RUNSTATE, var1, var1 |= 1) ? var1 : this.awaitRunStateLock();
   }

   private int awaitRunStateLock() {
      boolean var2 = false;
      int var3 = 0;
      int var4 = 0;

      int var5;
      int var6;
      do {
         while(((var5 = this.runState) & 1) != 0) {
            if (var4 == 0) {
               var4 = ThreadLocalRandom.nextSecondarySeed();
            } else if (var3 > 0) {
               var4 <<= 6;
               var4 >>>= 21;
               var4 <<= 7;
               if (var4 >= 0) {
                  --var3;
               }
            } else {
               AtomicLong var1;
               if ((var5 & 4) != 0 && (var1 = this.stealCounter) != null) {
                  if (U.compareAndSwapInt(this, RUNSTATE, var5, var5 | 2)) {
                     synchronized(var1) {
                        if ((this.runState & 2) != 0) {
                           try {
                              var1.wait();
                           } catch (InterruptedException var11) {
                              if (!(Thread.currentThread() instanceof ForkJoinWorkerThread)) {
                                 var2 = true;
                              }
                           }
                        } else {
                           var1.notifyAll();
                        }
                     }
                  }
               } else {
                  Thread.yield();
               }
            }
         }
      } while(!U.compareAndSwapInt(this, RUNSTATE, var5, var6 = var5 | 1));

      if (var2) {
         try {
            Thread.currentThread().interrupt();
         } catch (SecurityException var10) {
         }
      }

      return var6;
   }

   private void unlockRunState(int var1, int var2) {
      if (!U.compareAndSwapInt(this, RUNSTATE, var1, var2)) {
         AtomicLong var3 = this.stealCounter;
         this.runState = var2;
         if (var3 != null) {
            synchronized(var3) {
               var3.notifyAll();
            }
         }
      }

   }

   private boolean createWorker() {
      ForkJoinPool.ForkJoinWorkerThreadFactory var1 = this.factory;
      Throwable var2 = null;
      ForkJoinWorkerThread var3 = null;

      try {
         if (var1 != null && (var3 = var1.newThread(this)) != null) {
            var3.start();
            return true;
         }
      } catch (Throwable var5) {
         var2 = var5;
      }

      this.deregisterWorker(var3, var2);
      return false;
   }

   private void tryAddWorker(long var1) {
      boolean var3 = false;

      do {
         long var4 = -281474976710656L & var1 + 281474976710656L | 281470681743360L & var1 + 4294967296L;
         if (this.ctl == var1) {
            int var6;
            int var7;
            if ((var7 = (var6 = this.lockRunState()) & 536870912) == 0) {
               var3 = U.compareAndSwapLong(this, CTL, var1, var4);
            }

            this.unlockRunState(var6, var6 & -2);
            if (var7 != 0) {
               break;
            }

            if (var3) {
               this.createWorker();
               break;
            }
         }
      } while(((var1 = this.ctl) & 140737488355328L) != 0L && (int)var1 == 0);

   }

   final ForkJoinPool.WorkQueue registerWorker(ForkJoinWorkerThread var1) {
      var1.setDaemon(true);
      Thread.UncaughtExceptionHandler var2;
      if ((var2 = this.ueh) != null) {
         var1.setUncaughtExceptionHandler(var2);
      }

      ForkJoinPool.WorkQueue var3 = new ForkJoinPool.WorkQueue(this, var1);
      int var4 = 0;
      int var5 = this.config & -65536;
      int var6 = this.lockRunState();

      try {
         ForkJoinPool.WorkQueue[] var7;
         int var8;
         if ((var7 = this.workQueues) != null && (var8 = var7.length) > 0) {
            int var9 = this.indexSeed += -1640531527;
            int var10 = var8 - 1;
            var4 = (var9 << 1 | 1) & var10;
            if (var7[var4] != null) {
               int var11 = 0;
               int var12 = var8 <= 4 ? 2 : (var8 >>> 1 & '\ufffe') + 2;

               while(var7[var4 = var4 + var12 & var10] != null) {
                  ++var11;
                  if (var11 >= var8) {
                     this.workQueues = var7 = (ForkJoinPool.WorkQueue[])Arrays.copyOf((Object[])var7, var8 <<= 1);
                     var10 = var8 - 1;
                     var11 = 0;
                  }
               }
            }

            var3.hint = var9;
            var3.config = var4 | var5;
            var3.scanState = var4;
            var7[var4] = var3;
         }
      } finally {
         this.unlockRunState(var6, var6 & -2);
      }

      var1.setName(this.workerNamePrefix.concat(Integer.toString(var4 >>> 1)));
      return var3;
   }

   final void deregisterWorker(ForkJoinWorkerThread var1, Throwable var2) {
      ForkJoinPool.WorkQueue var3 = null;
      if (var1 != null && (var3 = var1.workQueue) != null) {
         int var5 = var3.config & '\uffff';
         int var6 = this.lockRunState();
         ForkJoinPool.WorkQueue[] var4;
         if ((var4 = this.workQueues) != null && var4.length > var5 && var4[var5] == var3) {
            var4[var5] = null;
         }

         this.unlockRunState(var6, var6 & -2);
      }

      long var9;
      while(!U.compareAndSwapLong(this, CTL, var9 = this.ctl, -281474976710656L & var9 - 281474976710656L | 281470681743360L & var9 - 4294967296L | 4294967295L & var9)) {
      }

      if (var3 != null) {
         var3.qlock = -1;
         var3.transferStealCount(this);
         var3.cancelAll();
      }

      int var7;
      ForkJoinPool.WorkQueue[] var10;
      while(!this.tryTerminate(false, false) && var3 != null && var3.array != null && (this.runState & 536870912) == 0 && (var10 = this.workQueues) != null && (var7 = var10.length - 1) >= 0) {
         int var8;
         if ((var8 = (int)(var9 = this.ctl)) == 0) {
            if (var2 != null && (var9 & 140737488355328L) != 0L) {
               this.tryAddWorker(var9);
            }
            break;
         }

         if (this.tryRelease(var9, var10[var8 & var7], 281474976710656L)) {
            break;
         }
      }

      if (var2 == null) {
         ForkJoinTask.helpExpungeStaleExceptions();
      } else {
         ForkJoinTask.rethrow(var2);
      }

   }

   final void signalWork(ForkJoinPool.WorkQueue[] var1, ForkJoinPool.WorkQueue var2) {
      while(true) {
         long var3;
         if ((var3 = this.ctl) < 0L) {
            int var5;
            if ((var5 = (int)var3) == 0) {
               if ((var3 & 140737488355328L) != 0L) {
                  this.tryAddWorker(var3);
               }
            } else {
               int var6;
               ForkJoinPool.WorkQueue var7;
               if (var1 != null && var1.length > (var6 = var5 & '\uffff') && (var7 = var1[var6]) != null) {
                  int var9 = var5 + 65536 & Integer.MAX_VALUE;
                  int var10 = var5 - var7.scanState;
                  long var11 = -4294967296L & var3 + 281474976710656L | 4294967295L & (long)var7.stackPred;
                  if (var10 == 0 && U.compareAndSwapLong(this, CTL, var3, var11)) {
                     var7.scanState = var9;
                     Thread var8;
                     if ((var8 = var7.parker) != null) {
                        U.unpark(var8);
                     }
                  } else if (var2 == null || var2.base != var2.top) {
                     continue;
                  }
               }
            }
         }

         return;
      }
   }

   private boolean tryRelease(long var1, ForkJoinPool.WorkQueue var3, long var4) {
      int var6 = (int)var1;
      int var7 = var6 + 65536 & Integer.MAX_VALUE;
      if (var3 != null && var3.scanState == var6) {
         long var9 = -4294967296L & var1 + var4 | 4294967295L & (long)var3.stackPred;
         if (U.compareAndSwapLong(this, CTL, var1, var9)) {
            var3.scanState = var7;
            Thread var8;
            if ((var8 = var3.parker) != null) {
               U.unpark(var8);
            }

            return true;
         }
      }

      return false;
   }

   final void runWorker(ForkJoinPool.WorkQueue var1) {
      var1.growArray();
      int var2 = var1.hint;
      int var3 = var2 == 0 ? 1 : var2;

      while(true) {
         ForkJoinTask var4;
         if ((var4 = this.scan(var1, var3)) != null) {
            var1.runTask(var4);
         } else if (!this.awaitWork(var1, var3)) {
            return;
         }

         var3 ^= var3 << 13;
         var3 ^= var3 >>> 17;
         var3 ^= var3 << 5;
      }
   }

   private ForkJoinTask<?> scan(ForkJoinPool.WorkQueue var1, int var2) {
      ForkJoinPool.WorkQueue[] var3;
      int var4;
      if ((var3 = this.workQueues) != null && (var4 = var3.length - 1) > 0 && var1 != null) {
         int var5 = var1.scanState;
         int var6 = var2 & var4;
         int var7 = var6;
         int var8 = 0;
         int var9 = 0;

         while(true) {
            while(true) {
               ForkJoinPool.WorkQueue var10;
               long var15;
               if ((var10 = var3[var7]) != null) {
                  ForkJoinTask[] var11;
                  int var13;
                  int var14;
                  if ((var14 = (var13 = var10.base) - var10.top) < 0 && (var11 = var10.array) != null) {
                     long var20 = (long)(((var11.length - 1 & var13) << ASHIFT) + ABASE);
                     ForkJoinTask var12;
                     if ((var12 = (ForkJoinTask)U.getObjectVolatile(var11, var20)) != null && var10.base == var13) {
                        if (var5 >= 0) {
                           if (U.compareAndSwapObject(var11, var20, var12, (Object)null)) {
                              var10.base = var13 + 1;
                              if (var14 < -1) {
                                 this.signalWork(var3, var10);
                              }

                              return var12;
                           }
                        } else if (var8 == 0 && var1.scanState < 0) {
                           this.tryRelease(var15 = this.ctl, var3[var4 & (int)var15], 281474976710656L);
                        }
                     }

                     if (var5 < 0) {
                        var5 = var1.scanState;
                     }

                     var2 ^= var2 << 1;
                     var2 ^= var2 >>> 3;
                     var2 ^= var2 << 10;
                     var6 = var7 = var2 & var4;
                     var9 = 0;
                     var8 = 0;
                     continue;
                  }

                  var9 += var13;
               }

               if ((var7 = var7 + 1 & var4) == var6) {
                  if (var5 >= 0 || var5 == (var5 = var1.scanState)) {
                     int var10000 = var8;
                     var8 = var9;
                     if (var10000 == var9) {
                        if (var5 < 0 || var1.qlock < 0) {
                           return null;
                        }

                        int var17 = var5 | Integer.MIN_VALUE;
                        long var18 = 4294967295L & (long)var17 | -4294967296L & (var15 = this.ctl) - 281474976710656L;
                        var1.stackPred = (int)var15;
                        U.putInt(var1, QSCANSTATE, var17);
                        if (U.compareAndSwapLong(this, CTL, var15, var18)) {
                           var5 = var17;
                        } else {
                           var1.scanState = var5;
                        }
                     }
                  }

                  var9 = 0;
               }
            }
         }
      } else {
         return null;
      }
   }

   private boolean awaitWork(ForkJoinPool.WorkQueue var1, int var2) {
      if (var1 != null && var1.qlock >= 0) {
         int var3 = var1.stackPred;
         int var4 = 0;

         while(true) {
            int var5;
            if ((var5 = var1.scanState) < 0) {
               if (var4 > 0) {
                  var2 ^= var2 << 6;
                  var2 ^= var2 >>> 21;
                  var2 ^= var2 << 7;
                  if (var2 < 0) {
                     continue;
                  }

                  --var4;
                  ForkJoinPool.WorkQueue[] var7;
                  int var9;
                  ForkJoinPool.WorkQueue var16;
                  if (var4 == 0 && var3 != 0 && (var7 = this.workQueues) != null && (var9 = var3 & '\uffff') < var7.length && (var16 = var7[var9]) != null && (var16.parker == null || var16.scanState >= 0)) {
                     var4 = 0;
                  }
                  continue;
               }

               if (var1.qlock < 0) {
                  return false;
               }

               if (Thread.interrupted()) {
                  continue;
               }

               long var6;
               int var14 = (int)((var6 = this.ctl) >> 48) + (this.config & '\uffff');
               if (var14 <= 0 && this.tryTerminate(false, false) || (this.runState & 536870912) != 0) {
                  return false;
               }

               long var8;
               long var10;
               long var12;
               if (var14 <= 0 && var5 == (int)var6) {
                  var8 = -4294967296L & var6 + 281474976710656L | 4294967295L & (long)var3;
                  short var15 = (short)((int)(var6 >>> 32));
                  if (var15 > 2 && U.compareAndSwapLong(this, CTL, var6, var8)) {
                     return false;
                  }

                  var10 = 2000000000L * (long)(var15 >= 0 ? 1 : 1 - var15);
                  var12 = System.nanoTime() + var10 - 20000000L;
               } else {
                  var12 = 0L;
                  var10 = 0L;
                  var8 = 0L;
               }

               Thread var17 = Thread.currentThread();
               U.putObject(var17, PARKBLOCKER, this);
               var1.parker = var17;
               if (var1.scanState < 0 && this.ctl == var6) {
                  U.park(false, var10);
               }

               U.putOrderedObject(var1, QPARKER, (Object)null);
               U.putObject(var17, PARKBLOCKER, (Object)null);
               if (var1.scanState < 0) {
                  if (var10 != 0L && this.ctl == var6 && var12 - System.nanoTime() <= 0L && U.compareAndSwapLong(this, CTL, var6, var8)) {
                     return false;
                  }
                  continue;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   final int helpComplete(ForkJoinPool.WorkQueue var1, CountedCompleter<?> var2, int var3) {
      int var5 = 0;
      ForkJoinPool.WorkQueue[] var4;
      int var6;
      if ((var4 = this.workQueues) != null && (var6 = var4.length - 1) >= 0 && var2 != null && var1 != null) {
         int var7 = var1.config;
         int var8 = var1.hint ^ var1.top;
         int var9 = var8 & var6;
         int var10 = 1;
         int var11 = var9;
         int var12 = 0;
         int var13 = 0;

         while(true) {
            while((var5 = var2.status) >= 0) {
               CountedCompleter var14;
               if (var10 == 1 && (var14 = var1.popCC(var2, var7)) != null) {
                  var14.doExec();
                  if (var3 != 0) {
                     --var3;
                     if (var3 == 0) {
                        return var5;
                     }
                  }

                  var9 = var11;
                  var13 = 0;
                  var12 = 0;
               } else {
                  ForkJoinPool.WorkQueue var15;
                  if ((var15 = var4[var11]) == null) {
                     var10 = 0;
                  } else if ((var10 = var15.pollAndExecCC(var2)) < 0) {
                     var13 += var10;
                  }

                  if (var10 > 0) {
                     if (var10 == 1 && var3 != 0) {
                        --var3;
                        if (var3 == 0) {
                           return var5;
                        }
                     }

                     var8 ^= var8 << 13;
                     var8 ^= var8 >>> 17;
                     var8 ^= var8 << 5;
                     var9 = var11 = var8 & var6;
                     var13 = 0;
                     var12 = 0;
                  } else if ((var11 = var11 + 1 & var6) == var9) {
                     int var10000 = var12;
                     var12 = var13;
                     if (var10000 == var13) {
                        return var5;
                     }

                     var13 = 0;
                  }
               }
            }

            return var5;
         }
      } else {
         return var5;
      }
   }

   private void helpStealer(ForkJoinPool.WorkQueue var1, ForkJoinTask<?> var2) {
      ForkJoinPool.WorkQueue[] var3 = this.workQueues;
      int var4 = 0;
      int var5;
      int var6;
      int var10000;
      if (var3 != null && (var6 = var3.length - 1) >= 0 && var1 != null && var2 != null) {
         do {
            var5 = 0;
            ForkJoinPool.WorkQueue var8 = var1;

            ForkJoinPool.WorkQueue var9;
            label95:
            for(ForkJoinTask var7 = var2; var7.status >= 0; var8 = var9) {
               int var10 = var8.hint | 1;
               int var11 = 0;

               while(true) {
                  if (var11 > var6) {
                     break label95;
                  }

                  int var12;
                  if ((var9 = var3[var12 = var10 + var11 & var6]) != null) {
                     if (var9.currentSteal == var7) {
                        var8.hint = var12;

                        do {
                           int var13;
                           ForkJoinTask var14;
                           ForkJoinTask[] var17;
                           do {
                              do {
                                 var5 += var11 = var9.base;
                                 ForkJoinTask var18 = var9.currentJoin;
                                 if (var7.status < 0 || var8.currentJoin != var7 || var9.currentSteal != var7) {
                                    break label95;
                                 }

                                 if (var11 - var9.top >= 0 || (var17 = var9.array) == null) {
                                    var7 = var18;
                                    if (var18 == null) {
                                       break label95;
                                    }
                                    continue label95;
                                 }

                                 var13 = ((var17.length - 1 & var11) << ASHIFT) + ABASE;
                                 var14 = (ForkJoinTask)U.getObjectVolatile(var17, (long)var13);
                              } while(var9.base != var11);

                              if (var14 == null) {
                                 break label95;
                              }
                           } while(!U.compareAndSwapObject(var17, (long)var13, var14, (Object)null));

                           var9.base = var11 + 1;
                           ForkJoinTask var15 = var1.currentSteal;
                           int var16 = var1.top;

                           do {
                              U.putOrderedObject(var1, QCURRENTSTEAL, var14);
                              var14.doExec();
                           } while(var2.status >= 0 && var1.top != var16 && (var14 = var1.pop()) != null);

                           U.putOrderedObject(var1, QCURRENTSTEAL, var15);
                        } while(var1.base == var1.top);

                        return;
                     }

                     var5 += var9.base;
                  }

                  var11 += 2;
               }
            }

            if (var2.status < 0) {
               break;
            }

            var10000 = var4;
            var4 = var5;
         } while(var10000 != var5);
      }

   }

   private boolean tryCompensate(ForkJoinPool.WorkQueue var1) {
      boolean var2;
      ForkJoinPool.WorkQueue[] var3;
      int var6;
      int var7;
      if (var1 != null && var1.qlock >= 0 && (var3 = this.workQueues) != null && (var6 = var3.length - 1) > 0 && (var7 = this.config & '\uffff') != 0) {
         long var4;
         int var8;
         if ((var8 = (int)(var4 = this.ctl)) != 0) {
            var2 = this.tryRelease(var4, var3[var8 & var6], 0L);
         } else {
            int var9 = (int)(var4 >> 48) + var7;
            int var10 = (short)((int)(var4 >> 32)) + var7;
            int var11 = 0;

            for(int var12 = 0; var12 <= var6; ++var12) {
               ForkJoinPool.WorkQueue var13;
               if ((var13 = var3[(var12 << 1 | 1) & var6]) != null) {
                  if ((var13.scanState & 1) != 0) {
                     break;
                  }

                  ++var11;
               }
            }

            if (var11 == var10 << 1 && this.ctl == var4) {
               if (var10 >= var7 && var9 > 1 && var1.isEmpty()) {
                  long var18 = -281474976710656L & var4 - 281474976710656L | 281474976710655L & var4;
                  var2 = U.compareAndSwapLong(this, CTL, var4, var18);
               } else {
                  if (var10 >= 32767 || this == common && var10 >= var7 + commonMaxSpares) {
                     throw new RejectedExecutionException("Thread limit exceeded replacing blocked worker");
                  }

                  boolean var16 = false;
                  long var14 = -281474976710656L & var4 | 281470681743360L & var4 + 4294967296L;
                  int var17;
                  if (((var17 = this.lockRunState()) & 536870912) == 0) {
                     var16 = U.compareAndSwapLong(this, CTL, var4, var14);
                  }

                  this.unlockRunState(var17, var17 & -2);
                  var2 = var16 && this.createWorker();
               }
            } else {
               var2 = false;
            }
         }
      } else {
         var2 = false;
      }

      return var2;
   }

   final int awaitJoin(ForkJoinPool.WorkQueue var1, ForkJoinTask<?> var2, long var3) {
      int var5 = 0;
      if (var2 != null && var1 != null) {
         ForkJoinTask var6 = var1.currentJoin;
         U.putOrderedObject(var1, QCURRENTJOIN, var2);
         CountedCompleter var7 = var2 instanceof CountedCompleter ? (CountedCompleter)var2 : null;

         while((var5 = var2.status) >= 0) {
            if (var7 != null) {
               this.helpComplete(var1, var7, 0);
            } else if (var1.base == var1.top || var1.tryRemoveAndExec(var2)) {
               this.helpStealer(var1, var2);
            }

            if ((var5 = var2.status) < 0) {
               break;
            }

            long var8;
            if (var3 == 0L) {
               var8 = 0L;
            } else {
               long var10;
               if ((var10 = var3 - System.nanoTime()) <= 0L) {
                  break;
               }

               if ((var8 = TimeUnit.NANOSECONDS.toMillis(var10)) <= 0L) {
                  var8 = 1L;
               }
            }

            if (this.tryCompensate(var1)) {
               var2.internalWait(var8);
               U.getAndAddLong(this, CTL, 281474976710656L);
            }
         }

         U.putOrderedObject(var1, QCURRENTJOIN, var6);
      }

      return var5;
   }

   private ForkJoinPool.WorkQueue findNonEmptyStealQueue() {
      int var3 = ThreadLocalRandom.nextSecondarySeed();
      ForkJoinPool.WorkQueue[] var1;
      int var2;
      if ((var1 = this.workQueues) != null && (var2 = var1.length - 1) >= 0) {
         int var4 = var3 & var2;
         int var5 = var4;
         int var6 = 0;
         int var7 = 0;

         while(true) {
            ForkJoinPool.WorkQueue var8;
            if ((var8 = var1[var5]) != null) {
               int var9;
               if ((var9 = var8.base) - var8.top < 0) {
                  return var8;
               }

               var7 += var9;
            }

            if ((var5 = var5 + 1 & var2) == var4) {
               int var10000 = var6;
               var6 = var7;
               if (var10000 == var7) {
                  break;
               }

               var7 = 0;
            }
         }
      }

      return null;
   }

   final void helpQuiescePool(ForkJoinPool.WorkQueue var1) {
      ForkJoinTask var2 = var1.currentSteal;
      boolean var3 = true;

      label42:
      while(true) {
         while(true) {
            var1.execLocalTasks();
            ForkJoinPool.WorkQueue var6;
            if ((var6 = this.findNonEmptyStealQueue()) == null) {
               long var4;
               if (var3) {
                  long var9 = -281474976710656L & (var4 = this.ctl) - 281474976710656L | 281474976710655L & var4;
                  if ((int)(var9 >> 48) + (this.config & '\uffff') <= 0) {
                     break label42;
                  }

                  if (U.compareAndSwapLong(this, CTL, var4, var9)) {
                     var3 = false;
                  }
               } else if ((int)((var4 = this.ctl) >> 48) + (this.config & '\uffff') <= 0 && U.compareAndSwapLong(this, CTL, var4, var4 + 281474976710656L)) {
                  break label42;
               }
            } else {
               if (!var3) {
                  var3 = true;
                  U.getAndAddLong(this, CTL, 281474976710656L);
               }

               ForkJoinTask var7;
               int var8;
               if ((var8 = var6.base) - var6.top < 0 && (var7 = var6.pollAt(var8)) != null) {
                  U.putOrderedObject(var1, QCURRENTSTEAL, var7);
                  var7.doExec();
                  if (++var1.nsteals < 0) {
                     var1.transferStealCount(this);
                  }
               }
            }
         }
      }

      U.putOrderedObject(var1, QCURRENTSTEAL, var2);
   }

   final ForkJoinTask<?> nextTaskFor(ForkJoinPool.WorkQueue var1) {
      ForkJoinTask var2;
      ForkJoinPool.WorkQueue var3;
      int var4;
      do {
         if ((var2 = var1.nextLocalTask()) != null) {
            return var2;
         }

         if ((var3 = this.findNonEmptyStealQueue()) == null) {
            return null;
         }
      } while((var4 = var3.base) - var3.top >= 0 || (var2 = var3.pollAt(var4)) == null);

      return var2;
   }

   static int getSurplusQueuedTaskCount() {
      Thread var0;
      if ((var0 = Thread.currentThread()) instanceof ForkJoinWorkerThread) {
         ForkJoinWorkerThread var1;
         ForkJoinPool var2;
         int var4 = (var2 = (var1 = (ForkJoinWorkerThread)var0).pool).config & '\uffff';
         ForkJoinPool.WorkQueue var3;
         int var5 = (var3 = var1.workQueue).top - var3.base;
         int var6 = (int)(var2.ctl >> 48) + var4;
         return var5 - (var6 > (var4 >>>= 1) ? 0 : (var6 > (var4 >>>= 1) ? 1 : (var6 > (var4 >>>= 1) ? 2 : (var6 > (var4 >>>= 1) ? 4 : 8))));
      } else {
         return 0;
      }
   }

   private boolean tryTerminate(boolean var1, boolean var2) {
      if (this == common) {
         return false;
      } else {
         int var3;
         if ((var3 = this.runState) >= 0) {
            if (!var2) {
               return false;
            }

            var3 = this.lockRunState();
            this.unlockRunState(var3, var3 & -2 | Integer.MIN_VALUE);
         }

         if ((var3 & 536870912) == 0) {
            if (!var1) {
               long var4 = 0L;

               long var12;
               long var10000;
               do {
                  var12 = this.ctl;
                  if ((int)(var12 >> 48) + (this.config & '\uffff') > 0) {
                     return false;
                  }

                  ForkJoinPool.WorkQueue[] var6;
                  int var8;
                  if ((var6 = this.workQueues) == null || (var8 = var6.length - 1) <= 0) {
                     break;
                  }

                  for(int var14 = 0; var14 <= var8; ++var14) {
                     ForkJoinPool.WorkQueue var7;
                     if ((var7 = var6[var14]) != null) {
                        int var9;
                        if ((var9 = var7.base) != var7.top || var7.scanState >= 0 || var7.currentSteal != null) {
                           long var10;
                           this.tryRelease(var10 = this.ctl, var6[var8 & (int)var10], 281474976710656L);
                           return false;
                        }

                        var12 += (long)var9;
                        if ((var14 & 1) == 0) {
                           var7.qlock = -1;
                        }
                     }
                  }

                  var10000 = var4;
                  var4 = var12;
               } while(var10000 != var12);
            }

            if ((this.runState & 536870912) == 0) {
               var3 = this.lockRunState();
               this.unlockRunState(var3, var3 & -2 | 536870912);
            }
         }

         int var19 = 0;
         long var5 = 0L;

         label114:
         while(true) {
            long var11 = this.ctl;
            ForkJoinPool.WorkQueue[] var20;
            int var23;
            if ((short)((int)(var11 >>> 32)) + (this.config & '\uffff') > 0 && (var20 = this.workQueues) != null && (var23 = var20.length - 1) > 0) {
               for(int var13 = 0; var13 <= var23; ++var13) {
                  ForkJoinPool.WorkQueue var21;
                  if ((var21 = var20[var13]) != null) {
                     var11 += (long)var21.base;
                     var21.qlock = -1;
                     if (var19 > 0) {
                        var21.cancelAll();
                        ForkJoinWorkerThread var22;
                        if (var19 > 1 && (var22 = var21.owner) != null) {
                           if (!var22.isInterrupted()) {
                              try {
                                 var22.interrupt();
                              } catch (Throwable var18) {
                              }
                           }

                           if (var21.scanState < 0) {
                              U.unpark(var22);
                           }
                        }
                     }
                  }
               }

               if (var11 != var5) {
                  var5 = var11;
                  var19 = 0;
                  continue;
               }

               if (var19 > 3 && var19 > var23) {
                  break;
               }

               ++var19;
               if (var19 <= 1) {
                  continue;
               }

               int var15 = 0;

               while(true) {
                  int var16;
                  long var24;
                  if (var15++ > var23 || (var16 = (int)(var24 = this.ctl)) == 0) {
                     continue label114;
                  }

                  this.tryRelease(var24, var20[var16 & var23], 281474976710656L);
               }
            }

            if ((this.runState & 1073741824) == 0) {
               var3 = this.lockRunState();
               this.unlockRunState(var3, var3 & -2 | 1073741824);
               synchronized(this) {
                  this.notifyAll();
               }
            }
            break;
         }

         return true;
      }
   }

   private void externalSubmit(ForkJoinTask<?> var1) {
      int var2;
      if ((var2 = ThreadLocalRandom.getProbe()) == 0) {
         ThreadLocalRandom.localInit();
         var2 = ThreadLocalRandom.getProbe();
      }

      while(true) {
         boolean var8 = false;
         int var5;
         if ((var5 = this.runState) < 0) {
            this.tryTerminate(false, false);
            throw new RejectedExecutionException();
         }

         ForkJoinPool.WorkQueue[] var3;
         int var6;
         int var10;
         if ((var5 & 4) != 0 && (var3 = this.workQueues) != null && (var6 = var3.length - 1) >= 0) {
            ForkJoinPool.WorkQueue var4;
            int var7;
            if ((var4 = var3[var7 = var2 & var6 & 126]) == null) {
               if ((this.runState & 1) == 0) {
                  var4 = new ForkJoinPool.WorkQueue(this, (ForkJoinWorkerThread)null);
                  var4.hint = var2;
                  var4.config = var7 | Integer.MIN_VALUE;
                  var4.scanState = Integer.MIN_VALUE;
                  var5 = this.lockRunState();
                  if (var5 > 0 && (var3 = this.workQueues) != null && var7 < var3.length && var3[var7] == null) {
                     var3[var7] = var4;
                  }

                  this.unlockRunState(var5, var5 & -2);
               } else {
                  var8 = true;
               }
            } else {
               if (var4.qlock == 0 && U.compareAndSwapInt(var4, QLOCK, 0, 1)) {
                  ForkJoinTask[] var20 = var4.array;
                  var10 = var4.top;
                  boolean var21 = false;

                  try {
                     if (var20 != null && var20.length > var10 + 1 - var4.base || (var20 = var4.growArray()) != null) {
                        int var12 = ((var20.length - 1 & var10) << ASHIFT) + ABASE;
                        U.putOrderedObject(var20, (long)var12, var1);
                        U.putOrderedInt(var4, QTOP, var10 + 1);
                        var21 = true;
                     }
                  } finally {
                     U.compareAndSwapInt(var4, QLOCK, 1, 0);
                  }

                  if (var21) {
                     this.signalWork(var3, var4);
                     return;
                  }
               }

               var8 = true;
            }
         } else {
            byte var9 = 0;
            var5 = this.lockRunState();

            try {
               if ((var5 & 4) == 0) {
                  U.compareAndSwapObject(this, STEALCOUNTER, (Object)null, new AtomicLong());
                  var10 = this.config & '\uffff';
                  int var11 = var10 > 1 ? var10 - 1 : 1;
                  var11 |= var11 >>> 1;
                  var11 |= var11 >>> 2;
                  var11 |= var11 >>> 4;
                  var11 |= var11 >>> 8;
                  var11 |= var11 >>> 16;
                  var11 = var11 + 1 << 1;
                  this.workQueues = new ForkJoinPool.WorkQueue[var11];
                  var9 = 4;
               }
            } finally {
               this.unlockRunState(var5, var5 & -2 | var9);
            }
         }

         if (var8) {
            var2 = ThreadLocalRandom.advanceProbe(var2);
         }
      }
   }

   final void externalPush(ForkJoinTask<?> var1) {
      int var5 = ThreadLocalRandom.getProbe();
      int var6 = this.runState;
      ForkJoinPool.WorkQueue[] var2;
      ForkJoinPool.WorkQueue var3;
      int var4;
      if ((var2 = this.workQueues) != null && (var4 = var2.length - 1) >= 0 && (var3 = var2[var4 & var5 & 126]) != null && var5 != 0 && var6 > 0 && U.compareAndSwapInt(var3, QLOCK, 0, 1)) {
         ForkJoinTask[] var7;
         int var8;
         int var9;
         int var10;
         if ((var7 = var3.array) != null && (var8 = var7.length - 1) > (var9 = (var10 = var3.top) - var3.base)) {
            int var11 = ((var8 & var10) << ASHIFT) + ABASE;
            U.putOrderedObject(var7, (long)var11, var1);
            U.putOrderedInt(var3, QTOP, var10 + 1);
            U.putIntVolatile(var3, QLOCK, 0);
            if (var9 <= 1) {
               this.signalWork(var2, var3);
            }

            return;
         }

         U.compareAndSwapInt(var3, QLOCK, 1, 0);
      }

      this.externalSubmit(var1);
   }

   static ForkJoinPool.WorkQueue commonSubmitterQueue() {
      ForkJoinPool var0 = common;
      int var1 = ThreadLocalRandom.getProbe();
      ForkJoinPool.WorkQueue[] var2;
      int var3;
      return var0 != null && (var2 = var0.workQueues) != null && (var3 = var2.length - 1) >= 0 ? var2[var3 & var1 & 126] : null;
   }

   final boolean tryExternalUnpush(ForkJoinTask<?> var1) {
      int var7 = ThreadLocalRandom.getProbe();
      ForkJoinPool.WorkQueue[] var2;
      ForkJoinPool.WorkQueue var3;
      ForkJoinTask[] var4;
      int var5;
      int var6;
      if ((var2 = this.workQueues) != null && (var5 = var2.length - 1) >= 0 && (var3 = var2[var5 & var7 & 126]) != null && (var4 = var3.array) != null && (var6 = var3.top) != var3.base) {
         long var8 = (long)(((var4.length - 1 & var6 - 1) << ASHIFT) + ABASE);
         if (U.compareAndSwapInt(var3, QLOCK, 0, 1)) {
            if (var3.top == var6 && var3.array == var4 && U.getObject(var4, var8) == var1 && U.compareAndSwapObject(var4, var8, var1, (Object)null)) {
               U.putOrderedInt(var3, QTOP, var6 - 1);
               U.putOrderedInt(var3, QLOCK, 0);
               return true;
            }

            U.compareAndSwapInt(var3, QLOCK, 1, 0);
         }
      }

      return false;
   }

   final int externalHelpComplete(CountedCompleter<?> var1, int var2) {
      int var5 = ThreadLocalRandom.getProbe();
      ForkJoinPool.WorkQueue[] var3;
      int var4;
      return (var3 = this.workQueues) != null && (var4 = var3.length) != 0 ? this.helpComplete(var3[var4 - 1 & var5 & 126], var1, var2) : 0;
   }

   public ForkJoinPool() {
      this(Math.min(32767, Runtime.getRuntime().availableProcessors()), defaultForkJoinWorkerThreadFactory, (Thread.UncaughtExceptionHandler)null, false);
   }

   public ForkJoinPool(int var1) {
      this(var1, defaultForkJoinWorkerThreadFactory, (Thread.UncaughtExceptionHandler)null, false);
   }

   public ForkJoinPool(int var1, ForkJoinPool.ForkJoinWorkerThreadFactory var2, Thread.UncaughtExceptionHandler var3, boolean var4) {
      this(checkParallelism(var1), checkFactory(var2), var3, var4 ? 65536 : 0, "ForkJoinPool-" + nextPoolId() + "-worker-");
      checkPermission();
   }

   private static int checkParallelism(int var0) {
      if (var0 > 0 && var0 <= 32767) {
         return var0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static ForkJoinPool.ForkJoinWorkerThreadFactory checkFactory(ForkJoinPool.ForkJoinWorkerThreadFactory var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return var0;
      }
   }

   private ForkJoinPool(int var1, ForkJoinPool.ForkJoinWorkerThreadFactory var2, Thread.UncaughtExceptionHandler var3, int var4, String var5) {
      this.workerNamePrefix = var5;
      this.factory = var2;
      this.ueh = var3;
      this.config = var1 & '\uffff' | var4;
      long var6 = (long)(-var1);
      this.ctl = var6 << 48 & -281474976710656L | var6 << 32 & 281470681743360L;
   }

   public static ForkJoinPool commonPool() {
      return common;
   }

   public <T> T invoke(ForkJoinTask<T> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.externalPush(var1);
         return var1.join();
      }
   }

   public void execute(ForkJoinTask<?> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.externalPush(var1);
      }
   }

   public void execute(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2;
         if (var1 instanceof ForkJoinTask) {
            var2 = (ForkJoinTask)var1;
         } else {
            var2 = new ForkJoinTask.RunnableExecuteAction(var1);
         }

         this.externalPush((ForkJoinTask)var2);
      }
   }

   public <T> ForkJoinTask<T> submit(ForkJoinTask<T> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.externalPush(var1);
         return var1;
      }
   }

   public <T> ForkJoinTask<T> submit(Callable<T> var1) {
      ForkJoinTask.AdaptedCallable var2 = new ForkJoinTask.AdaptedCallable(var1);
      this.externalPush(var2);
      return var2;
   }

   public <T> ForkJoinTask<T> submit(Runnable var1, T var2) {
      ForkJoinTask.AdaptedRunnable var3 = new ForkJoinTask.AdaptedRunnable(var1, var2);
      this.externalPush(var3);
      return var3;
   }

   public ForkJoinTask<?> submit(Runnable var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         Object var2;
         if (var1 instanceof ForkJoinTask) {
            var2 = (ForkJoinTask)var1;
         } else {
            var2 = new ForkJoinTask.AdaptedRunnableAction(var1);
         }

         this.externalPush((ForkJoinTask)var2);
         return (ForkJoinTask)var2;
      }
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) {
      ArrayList var2 = new ArrayList(var1.size());
      boolean var3 = false;
      boolean var11 = false;

      ArrayList var14;
      int var15;
      try {
         var11 = true;
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Callable var5 = (Callable)var4.next();
            ForkJoinTask.AdaptedCallable var6 = new ForkJoinTask.AdaptedCallable(var5);
            var2.add(var6);
            this.externalPush(var6);
         }

         int var13 = 0;
         var15 = var2.size();

         while(true) {
            if (var13 >= var15) {
               var3 = true;
               var14 = var2;
               var11 = false;
               break;
            }

            ((ForkJoinTask)var2.get(var13)).quietlyJoin();
            ++var13;
         }
      } finally {
         if (var11) {
            if (!var3) {
               int var8 = 0;

               for(int var9 = var2.size(); var8 < var9; ++var8) {
                  ((Future)var2.get(var8)).cancel(false);
               }
            }

         }
      }

      if (!var3) {
         var15 = 0;

         for(int var16 = var2.size(); var15 < var16; ++var15) {
            ((Future)var2.get(var15)).cancel(false);
         }
      }

      return var14;
   }

   public ForkJoinPool.ForkJoinWorkerThreadFactory getFactory() {
      return this.factory;
   }

   public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
      return this.ueh;
   }

   public int getParallelism() {
      int var1;
      return (var1 = this.config & '\uffff') > 0 ? var1 : 1;
   }

   public static int getCommonPoolParallelism() {
      return commonParallelism;
   }

   public int getPoolSize() {
      return (this.config & '\uffff') + (short)((int)(this.ctl >>> 32));
   }

   public boolean getAsyncMode() {
      return (this.config & 65536) != 0;
   }

   public int getRunningThreadCount() {
      int var1 = 0;
      ForkJoinPool.WorkQueue[] var2;
      if ((var2 = this.workQueues) != null) {
         for(int var4 = 1; var4 < var2.length; var4 += 2) {
            ForkJoinPool.WorkQueue var3;
            if ((var3 = var2[var4]) != null && var3.isApparentlyUnblocked()) {
               ++var1;
            }
         }
      }

      return var1;
   }

   public int getActiveThreadCount() {
      int var1 = (this.config & '\uffff') + (int)(this.ctl >> 48);
      return var1 <= 0 ? 0 : var1;
   }

   public boolean isQuiescent() {
      return (this.config & '\uffff') + (int)(this.ctl >> 48) <= 0;
   }

   public long getStealCount() {
      AtomicLong var1 = this.stealCounter;
      long var2 = var1 == null ? 0L : var1.get();
      ForkJoinPool.WorkQueue[] var4;
      if ((var4 = this.workQueues) != null) {
         for(int var6 = 1; var6 < var4.length; var6 += 2) {
            ForkJoinPool.WorkQueue var5;
            if ((var5 = var4[var6]) != null) {
               var2 += (long)var5.nsteals;
            }
         }
      }

      return var2;
   }

   public long getQueuedTaskCount() {
      long var1 = 0L;
      ForkJoinPool.WorkQueue[] var3;
      if ((var3 = this.workQueues) != null) {
         for(int var5 = 1; var5 < var3.length; var5 += 2) {
            ForkJoinPool.WorkQueue var4;
            if ((var4 = var3[var5]) != null) {
               var1 += (long)var4.queueSize();
            }
         }
      }

      return var1;
   }

   public int getQueuedSubmissionCount() {
      int var1 = 0;
      ForkJoinPool.WorkQueue[] var2;
      if ((var2 = this.workQueues) != null) {
         for(int var4 = 0; var4 < var2.length; var4 += 2) {
            ForkJoinPool.WorkQueue var3;
            if ((var3 = var2[var4]) != null) {
               var1 += var3.queueSize();
            }
         }
      }

      return var1;
   }

   public boolean hasQueuedSubmissions() {
      ForkJoinPool.WorkQueue[] var1;
      if ((var1 = this.workQueues) != null) {
         for(int var3 = 0; var3 < var1.length; var3 += 2) {
            ForkJoinPool.WorkQueue var2;
            if ((var2 = var1[var3]) != null && !var2.isEmpty()) {
               return true;
            }
         }
      }

      return false;
   }

   protected ForkJoinTask<?> pollSubmission() {
      ForkJoinPool.WorkQueue[] var1;
      if ((var1 = this.workQueues) != null) {
         for(int var4 = 0; var4 < var1.length; var4 += 2) {
            ForkJoinPool.WorkQueue var2;
            ForkJoinTask var3;
            if ((var2 = var1[var4]) != null && (var3 = var2.poll()) != null) {
               return var3;
            }
         }
      }

      return null;
   }

   protected int drainTasksTo(Collection<? super ForkJoinTask<?>> var1) {
      int var2 = 0;
      ForkJoinPool.WorkQueue[] var3;
      if ((var3 = this.workQueues) != null) {
         for(int var6 = 0; var6 < var3.length; ++var6) {
            ForkJoinPool.WorkQueue var4;
            ForkJoinTask var5;
            if ((var4 = var3[var6]) != null) {
               while((var5 = var4.poll()) != null) {
                  var1.add(var5);
                  ++var2;
               }
            }
         }
      }

      return var2;
   }

   public String toString() {
      long var1 = 0L;
      long var3 = 0L;
      int var5 = 0;
      AtomicLong var6 = this.stealCounter;
      long var7 = var6 == null ? 0L : var6.get();
      long var9 = this.ctl;
      ForkJoinPool.WorkQueue[] var11;
      int var13;
      int var14;
      if ((var11 = this.workQueues) != null) {
         for(var13 = 0; var13 < var11.length; ++var13) {
            ForkJoinPool.WorkQueue var12;
            if ((var12 = var11[var13]) != null) {
               var14 = var12.queueSize();
               if ((var13 & 1) == 0) {
                  var3 += (long)var14;
               } else {
                  var1 += (long)var14;
                  var7 += (long)var12.nsteals;
                  if (var12.isApparentlyUnblocked()) {
                     ++var5;
                  }
               }
            }
         }
      }

      var13 = this.config & '\uffff';
      var14 = var13 + (short)((int)(var9 >>> 32));
      int var15 = var13 + (int)(var9 >> 48);
      if (var15 < 0) {
         var15 = 0;
      }

      int var16 = this.runState;
      String var17 = (var16 & 1073741824) != 0 ? "Terminated" : ((var16 & 536870912) != 0 ? "Terminating" : ((var16 & Integer.MIN_VALUE) != 0 ? "Shutting down" : "Running"));
      return super.toString() + "[" + var17 + ", parallelism = " + var13 + ", size = " + var14 + ", active = " + var15 + ", running = " + var5 + ", steals = " + var7 + ", tasks = " + var1 + ", submissions = " + var3 + "]";
   }

   public void shutdown() {
      checkPermission();
      this.tryTerminate(false, true);
   }

   public List<Runnable> shutdownNow() {
      checkPermission();
      this.tryTerminate(true, true);
      return Collections.emptyList();
   }

   public boolean isTerminated() {
      return (this.runState & 1073741824) != 0;
   }

   public boolean isTerminating() {
      int var1 = this.runState;
      return (var1 & 536870912) != 0 && (var1 & 1073741824) == 0;
   }

   public boolean isShutdown() {
      return (this.runState & Integer.MIN_VALUE) != 0;
   }

   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      if (Thread.interrupted()) {
         throw new InterruptedException();
      } else if (this == common) {
         this.awaitQuiescence(var1, var3);
         return false;
      } else {
         long var4 = var3.toNanos(var1);
         if (this.isTerminated()) {
            return true;
         } else if (var4 <= 0L) {
            return false;
         } else {
            long var6 = System.nanoTime() + var4;
            synchronized(this) {
               while(!this.isTerminated()) {
                  if (var4 <= 0L) {
                     return false;
                  }

                  long var9 = TimeUnit.NANOSECONDS.toMillis(var4);
                  this.wait(var9 > 0L ? var9 : 1L);
                  var4 = var6 - System.nanoTime();
               }

               return true;
            }
         }
      }
   }

   public boolean awaitQuiescence(long var1, TimeUnit var3) {
      long var4 = var3.toNanos(var1);
      Thread var7 = Thread.currentThread();
      ForkJoinWorkerThread var6;
      if (var7 instanceof ForkJoinWorkerThread && (var6 = (ForkJoinWorkerThread)var7).pool == this) {
         this.helpQuiescePool(var6.workQueue);
         return true;
      } else {
         long var8 = System.nanoTime();
         int var11 = 0;
         boolean var13 = true;

         ForkJoinPool.WorkQueue[] var10;
         int var12;
         while(!this.isQuiescent() && (var10 = this.workQueues) != null && (var12 = var10.length - 1) >= 0) {
            if (!var13) {
               if (System.nanoTime() - var8 > var4) {
                  return false;
               }

               Thread.yield();
            }

            var13 = false;

            for(int var14 = var12 + 1 << 2; var14 >= 0; --var14) {
               ForkJoinPool.WorkQueue var16;
               int var17;
               int var18;
               if ((var18 = var11++ & var12) <= var12 && var18 >= 0 && (var16 = var10[var18]) != null && (var17 = var16.base) - var16.top < 0) {
                  var13 = true;
                  ForkJoinTask var15;
                  if ((var15 = var16.pollAt(var17)) != null) {
                     var15.doExec();
                  }
                  break;
               }
            }
         }

         return true;
      }
   }

   static void quiesceCommonPool() {
      common.awaitQuiescence(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
   }

   public static void managedBlock(ForkJoinPool.ManagedBlocker var0) throws InterruptedException {
      Thread var3 = Thread.currentThread();
      ForkJoinPool var1;
      ForkJoinWorkerThread var2;
      if (var3 instanceof ForkJoinWorkerThread && (var1 = (var2 = (ForkJoinWorkerThread)var3).pool) != null) {
         ForkJoinPool.WorkQueue var4 = var2.workQueue;

         while(!var0.isReleasable()) {
            if (var1.tryCompensate(var4)) {
               try {
                  while(!var0.isReleasable() && !var0.block()) {
                  }

                  return;
               } finally {
                  U.getAndAddLong(var1, CTL, 281474976710656L);
               }
            }
         }
      } else {
         while(!var0.isReleasable() && !var0.block()) {
         }
      }

   }

   protected <T> RunnableFuture<T> newTaskFor(Runnable var1, T var2) {
      return new ForkJoinTask.AdaptedRunnable(var1, var2);
   }

   protected <T> RunnableFuture<T> newTaskFor(Callable<T> var1) {
      return new ForkJoinTask.AdaptedCallable(var1);
   }

   private static ForkJoinPool makeCommonPool() {
      int var0 = -1;
      Object var1 = null;
      Thread.UncaughtExceptionHandler var2 = null;

      try {
         String var3 = System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism");
         String var4 = System.getProperty("java.util.concurrent.ForkJoinPool.common.threadFactory");
         String var5 = System.getProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler");
         if (var3 != null) {
            var0 = Integer.parseInt(var3);
         }

         if (var4 != null) {
            var1 = (ForkJoinPool.ForkJoinWorkerThreadFactory)ClassLoader.getSystemClassLoader().loadClass(var4).newInstance();
         }

         if (var5 != null) {
            var2 = (Thread.UncaughtExceptionHandler)ClassLoader.getSystemClassLoader().loadClass(var5).newInstance();
         }
      } catch (Exception var6) {
      }

      if (var1 == null) {
         if (System.getSecurityManager() == null) {
            var1 = defaultForkJoinWorkerThreadFactory;
         } else {
            var1 = new ForkJoinPool.InnocuousForkJoinWorkerThreadFactory();
         }
      }

      if (var0 < 0 && (var0 = Runtime.getRuntime().availableProcessors() - 1) <= 0) {
         var0 = 1;
      }

      if (var0 > 32767) {
         var0 = 32767;
      }

      return new ForkJoinPool(var0, (ForkJoinPool.ForkJoinWorkerThreadFactory)var1, var2, 0, "ForkJoinPool.commonPool-worker-");
   }

   static {
      try {
         U = Unsafe.getUnsafe();
         Class var0 = ForkJoinPool.class;
         CTL = U.objectFieldOffset(var0.getDeclaredField("ctl"));
         RUNSTATE = U.objectFieldOffset(var0.getDeclaredField("runState"));
         STEALCOUNTER = U.objectFieldOffset(var0.getDeclaredField("stealCounter"));
         Class var1 = Thread.class;
         PARKBLOCKER = U.objectFieldOffset(var1.getDeclaredField("parkBlocker"));
         Class var2 = ForkJoinPool.WorkQueue.class;
         QTOP = U.objectFieldOffset(var2.getDeclaredField("top"));
         QLOCK = U.objectFieldOffset(var2.getDeclaredField("qlock"));
         QSCANSTATE = U.objectFieldOffset(var2.getDeclaredField("scanState"));
         QPARKER = U.objectFieldOffset(var2.getDeclaredField("parker"));
         QCURRENTSTEAL = U.objectFieldOffset(var2.getDeclaredField("currentSteal"));
         QCURRENTJOIN = U.objectFieldOffset(var2.getDeclaredField("currentJoin"));
         Class var3 = ForkJoinTask[].class;
         ABASE = U.arrayBaseOffset(var3);
         int var4 = U.arrayIndexScale(var3);
         if ((var4 & var4 - 1) != 0) {
            throw new Error("data type scale not a power of two");
         }

         ASHIFT = 31 - Integer.numberOfLeadingZeros(var4);
      } catch (Exception var5) {
         throw new Error(var5);
      }

      commonMaxSpares = 256;
      defaultForkJoinWorkerThreadFactory = new ForkJoinPool.DefaultForkJoinWorkerThreadFactory();
      modifyThreadPermission = new RuntimePermission("modifyThread");
      common = (ForkJoinPool)AccessController.doPrivileged(new PrivilegedAction<ForkJoinPool>() {
         public ForkJoinPool run() {
            return ForkJoinPool.makeCommonPool();
         }
      });
      int var6 = common.config & '\uffff';
      commonParallelism = var6 > 0 ? var6 : 1;
   }

   static final class InnocuousForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
      private static final AccessControlContext innocuousAcc;

      public final ForkJoinWorkerThread newThread(final ForkJoinPool var1) {
         return (ForkJoinWorkerThread.InnocuousForkJoinWorkerThread)AccessController.doPrivileged(new PrivilegedAction<ForkJoinWorkerThread>() {
            public ForkJoinWorkerThread run() {
               return new ForkJoinWorkerThread.InnocuousForkJoinWorkerThread(var1);
            }
         }, innocuousAcc);
      }

      static {
         Permissions var0 = new Permissions();
         var0.add(ForkJoinPool.modifyThreadPermission);
         var0.add(new RuntimePermission("enableContextClassLoaderOverride"));
         var0.add(new RuntimePermission("modifyThreadGroup"));
         innocuousAcc = new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var0)});
      }
   }

   public interface ManagedBlocker {
      boolean block() throws InterruptedException;

      boolean isReleasable();
   }

   @Contended
   static final class WorkQueue {
      static final int INITIAL_QUEUE_CAPACITY = 8192;
      static final int MAXIMUM_QUEUE_CAPACITY = 67108864;
      volatile int scanState;
      int stackPred;
      int nsteals;
      int hint;
      int config;
      volatile int qlock;
      volatile int base;
      int top;
      ForkJoinTask<?>[] array;
      final ForkJoinPool pool;
      final ForkJoinWorkerThread owner;
      volatile Thread parker;
      volatile ForkJoinTask<?> currentJoin;
      volatile ForkJoinTask<?> currentSteal;
      private static final Unsafe U;
      private static final int ABASE;
      private static final int ASHIFT;
      private static final long QTOP;
      private static final long QLOCK;
      private static final long QCURRENTSTEAL;

      WorkQueue(ForkJoinPool var1, ForkJoinWorkerThread var2) {
         this.pool = var1;
         this.owner = var2;
         this.base = this.top = 4096;
      }

      final int getPoolIndex() {
         return (this.config & '\uffff') >>> 1;
      }

      final int queueSize() {
         int var1 = this.base - this.top;
         return var1 >= 0 ? 0 : -var1;
      }

      final boolean isEmpty() {
         ForkJoinTask[] var1;
         int var2;
         int var3;
         int var4;
         return (var2 = this.base - (var4 = this.top)) >= 0 || var2 == -1 && ((var1 = this.array) == null || (var3 = var1.length - 1) < 0 || U.getObject(var1, (long)((var3 & var4 - 1) << ASHIFT) + (long)ABASE) == null);
      }

      final void push(ForkJoinTask<?> var1) {
         int var4 = this.base;
         int var5 = this.top;
         ForkJoinTask[] var2;
         if ((var2 = this.array) != null) {
            int var7 = var2.length - 1;
            U.putOrderedObject(var2, (long)(((var7 & var5) << ASHIFT) + ABASE), var1);
            U.putOrderedInt(this, QTOP, var5 + 1);
            int var6;
            if ((var6 = var5 - var4) <= 1) {
               ForkJoinPool var3;
               if ((var3 = this.pool) != null) {
                  var3.signalWork(var3.workQueues, this);
               }
            } else if (var6 >= var7) {
               this.growArray();
            }
         }

      }

      final ForkJoinTask<?>[] growArray() {
         ForkJoinTask[] var1 = this.array;
         int var2 = var1 != null ? var1.length << 1 : 8192;
         if (var2 > 67108864) {
            throw new RejectedExecutionException("Queue capacity exceeded");
         } else {
            ForkJoinTask[] var6 = this.array = new ForkJoinTask[var2];
            int var3;
            int var4;
            int var5;
            if (var1 != null && (var3 = var1.length - 1) >= 0 && (var4 = this.top) - (var5 = this.base) > 0) {
               int var7 = var2 - 1;

               do {
                  int var9 = ((var5 & var3) << ASHIFT) + ABASE;
                  int var10 = ((var5 & var7) << ASHIFT) + ABASE;
                  ForkJoinTask var8 = (ForkJoinTask)U.getObjectVolatile(var1, (long)var9);
                  if (var8 != null && U.compareAndSwapObject(var1, (long)var9, var8, (Object)null)) {
                     U.putObjectVolatile(var6, (long)var10, var8);
                  }

                  ++var5;
               } while(var5 != var4);
            }

            return var6;
         }
      }

      final ForkJoinTask<?> pop() {
         ForkJoinTask[] var1;
         int var3;
         int var4;
         if ((var1 = this.array) != null && (var3 = var1.length - 1) >= 0) {
            while((var4 = this.top - 1) - this.base >= 0) {
               long var5 = (long)(((var3 & var4) << ASHIFT) + ABASE);
               ForkJoinTask var2;
               if ((var2 = (ForkJoinTask)U.getObject(var1, var5)) == null) {
                  break;
               }

               if (U.compareAndSwapObject(var1, var5, var2, (Object)null)) {
                  U.putOrderedInt(this, QTOP, var4);
                  return var2;
               }
            }
         }

         return null;
      }

      final ForkJoinTask<?> pollAt(int var1) {
         ForkJoinTask[] var3;
         if ((var3 = this.array) != null) {
            int var4 = ((var3.length - 1 & var1) << ASHIFT) + ABASE;
            ForkJoinTask var2;
            if ((var2 = (ForkJoinTask)U.getObjectVolatile(var3, (long)var4)) != null && this.base == var1 && U.compareAndSwapObject(var3, (long)var4, var2, (Object)null)) {
               this.base = var1 + 1;
               return var2;
            }
         }

         return null;
      }

      final ForkJoinTask<?> poll() {
         while(true) {
            ForkJoinTask[] var1;
            int var2;
            if ((var2 = this.base) - this.top < 0 && (var1 = this.array) != null) {
               int var4 = ((var1.length - 1 & var2) << ASHIFT) + ABASE;
               ForkJoinTask var3 = (ForkJoinTask)U.getObjectVolatile(var1, (long)var4);
               if (this.base != var2) {
                  continue;
               }

               if (var3 != null) {
                  if (!U.compareAndSwapObject(var1, (long)var4, var3, (Object)null)) {
                     continue;
                  }

                  this.base = var2 + 1;
                  return var3;
               }

               if (var2 + 1 != this.top) {
                  continue;
               }
            }

            return null;
         }
      }

      final ForkJoinTask<?> nextLocalTask() {
         return (this.config & 65536) == 0 ? this.pop() : this.poll();
      }

      final ForkJoinTask<?> peek() {
         ForkJoinTask[] var1 = this.array;
         int var2;
         if (var1 != null && (var2 = var1.length - 1) >= 0) {
            int var3 = (this.config & 65536) == 0 ? this.top - 1 : this.base;
            int var4 = ((var3 & var2) << ASHIFT) + ABASE;
            return (ForkJoinTask)U.getObjectVolatile(var1, (long)var4);
         } else {
            return null;
         }
      }

      final boolean tryUnpush(ForkJoinTask<?> var1) {
         ForkJoinTask[] var2;
         int var3;
         if ((var2 = this.array) != null && (var3 = this.top) != this.base) {
            int var10002 = var2.length - 1;
            --var3;
            if (U.compareAndSwapObject(var2, (long)(((var10002 & var3) << ASHIFT) + ABASE), var1, (Object)null)) {
               U.putOrderedInt(this, QTOP, var3);
               return true;
            }
         }

         return false;
      }

      final void cancelAll() {
         ForkJoinTask var1;
         if ((var1 = this.currentJoin) != null) {
            this.currentJoin = null;
            ForkJoinTask.cancelIgnoringExceptions(var1);
         }

         if ((var1 = this.currentSteal) != null) {
            this.currentSteal = null;
            ForkJoinTask.cancelIgnoringExceptions(var1);
         }

         while((var1 = this.poll()) != null) {
            ForkJoinTask.cancelIgnoringExceptions(var1);
         }

      }

      final void pollAndExecAll() {
         ForkJoinTask var1;
         while((var1 = this.poll()) != null) {
            var1.doExec();
         }

      }

      final void execLocalTasks() {
         int var1 = this.base;
         ForkJoinTask[] var4 = this.array;
         int var2;
         int var3;
         if (var1 - (var3 = this.top - 1) <= 0 && var4 != null && (var2 = var4.length - 1) >= 0) {
            ForkJoinTask var5;
            if ((this.config & 65536) == 0) {
               while((var5 = (ForkJoinTask)U.getAndSetObject(var4, (long)(((var2 & var3) << ASHIFT) + ABASE), (Object)null)) != null) {
                  U.putOrderedInt(this, QTOP, var3);
                  var5.doExec();
                  if (this.base - (var3 = this.top - 1) > 0) {
                     break;
                  }
               }
            } else {
               this.pollAndExecAll();
            }
         }

      }

      final void runTask(ForkJoinTask<?> var1) {
         if (var1 != null) {
            this.scanState &= -2;
            (this.currentSteal = var1).doExec();
            U.putOrderedObject(this, QCURRENTSTEAL, (Object)null);
            this.execLocalTasks();
            ForkJoinWorkerThread var2 = this.owner;
            if (++this.nsteals < 0) {
               this.transferStealCount(this.pool);
            }

            this.scanState |= 1;
            if (var2 != null) {
               var2.afterTopLevelExec();
            }
         }

      }

      final void transferStealCount(ForkJoinPool var1) {
         AtomicLong var2;
         if (var1 != null && (var2 = var1.stealCounter) != null) {
            int var3 = this.nsteals;
            this.nsteals = 0;
            var2.getAndAdd((long)(var3 < 0 ? Integer.MAX_VALUE : var3));
         }

      }

      final boolean tryRemoveAndExec(ForkJoinTask<?> var1) {
         ForkJoinTask[] var2;
         int var3;
         int var4;
         int var5;
         int var6;
         if ((var2 = this.array) != null && (var3 = var2.length - 1) >= 0 && var1 != null) {
            while((var6 = (var4 = this.top) - (var5 = this.base)) > 0) {
               while(true) {
                  --var4;
                  long var8 = (long)(((var4 & var3) << ASHIFT) + ABASE);
                  ForkJoinTask var7;
                  if ((var7 = (ForkJoinTask)U.getObject(var2, var8)) == null) {
                     return var4 + 1 == this.top;
                  }

                  if (var7 == var1) {
                     boolean var10 = false;
                     if (var4 + 1 == this.top) {
                        if (U.compareAndSwapObject(var2, var8, var1, (Object)null)) {
                           U.putOrderedInt(this, QTOP, var4);
                           var10 = true;
                        }
                     } else if (this.base == var5) {
                        var10 = U.compareAndSwapObject(var2, var8, var1, new ForkJoinPool.EmptyTask());
                     }

                     if (var10) {
                        var1.doExec();
                     }
                     break;
                  }

                  if (var7.status < 0 && var4 + 1 == this.top) {
                     if (U.compareAndSwapObject(var2, var8, var7, (Object)null)) {
                        U.putOrderedInt(this, QTOP, var4);
                     }
                     break;
                  }

                  --var6;
                  if (var6 == 0) {
                     return false;
                  }
               }

               if (var1.status < 0) {
                  return false;
               }
            }
         }

         return true;
      }

      final CountedCompleter<?> popCC(CountedCompleter<?> var1, int var2) {
         int var3;
         ForkJoinTask[] var4;
         if (this.base - (var3 = this.top) < 0 && (var4 = this.array) != null) {
            long var6 = (long)(((var4.length - 1 & var3 - 1) << ASHIFT) + ABASE);
            Object var5;
            if ((var5 = U.getObjectVolatile(var4, var6)) != null && var5 instanceof CountedCompleter) {
               CountedCompleter var8 = (CountedCompleter)var5;
               CountedCompleter var9 = var8;

               do {
                  if (var9 == var1) {
                     if (var2 < 0) {
                        if (U.compareAndSwapInt(this, QLOCK, 0, 1)) {
                           if (this.top == var3 && this.array == var4 && U.compareAndSwapObject(var4, var6, var8, (Object)null)) {
                              U.putOrderedInt(this, QTOP, var3 - 1);
                              U.putOrderedInt(this, QLOCK, 0);
                              return var8;
                           }

                           U.compareAndSwapInt(this, QLOCK, 1, 0);
                        }
                     } else if (U.compareAndSwapObject(var4, var6, var8, (Object)null)) {
                        U.putOrderedInt(this, QTOP, var3 - 1);
                        return var8;
                     }
                     break;
                  }
               } while((var9 = var9.completer) != null);
            }
         }

         return null;
      }

      final int pollAndExecCC(CountedCompleter<?> var1) {
         int var2;
         int var3;
         ForkJoinTask[] var4;
         if ((var2 = this.base) - this.top < 0 && (var4 = this.array) != null) {
            long var6 = (long)(((var4.length - 1 & var2) << ASHIFT) + ABASE);
            Object var5;
            if ((var5 = U.getObjectVolatile(var4, var6)) == null) {
               var3 = 2;
            } else if (!(var5 instanceof CountedCompleter)) {
               var3 = -1;
            } else {
               CountedCompleter var8 = (CountedCompleter)var5;
               CountedCompleter var9 = var8;

               while(var9 != var1) {
                  if ((var9 = var9.completer) == null) {
                     var3 = -1;
                     return var3;
                  }
               }

               if (this.base == var2 && U.compareAndSwapObject(var4, var6, var8, (Object)null)) {
                  this.base = var2 + 1;
                  var8.doExec();
                  var3 = 1;
               } else {
                  var3 = 2;
               }
            }
         } else {
            var3 = var2 | Integer.MIN_VALUE;
         }

         return var3;
      }

      final boolean isApparentlyUnblocked() {
         ForkJoinWorkerThread var1;
         Thread.State var2;
         return this.scanState >= 0 && (var1 = this.owner) != null && (var2 = var1.getState()) != Thread.State.BLOCKED && var2 != Thread.State.WAITING && var2 != Thread.State.TIMED_WAITING;
      }

      static {
         try {
            U = Unsafe.getUnsafe();
            Class var0 = ForkJoinPool.WorkQueue.class;
            Class var1 = ForkJoinTask[].class;
            QTOP = U.objectFieldOffset(var0.getDeclaredField("top"));
            QLOCK = U.objectFieldOffset(var0.getDeclaredField("qlock"));
            QCURRENTSTEAL = U.objectFieldOffset(var0.getDeclaredField("currentSteal"));
            ABASE = U.arrayBaseOffset(var1);
            int var2 = U.arrayIndexScale(var1);
            if ((var2 & var2 - 1) != 0) {
               throw new Error("data type scale not a power of two");
            } else {
               ASHIFT = 31 - Integer.numberOfLeadingZeros(var2);
            }
         } catch (Exception var3) {
            throw new Error(var3);
         }
      }
   }

   static final class EmptyTask extends ForkJoinTask<Void> {
      private static final long serialVersionUID = -7721805057305804111L;

      EmptyTask() {
         this.status = -268435456;
      }

      public final Void getRawResult() {
         return null;
      }

      public final void setRawResult(Void var1) {
      }

      public final boolean exec() {
         return true;
      }
   }

   static final class DefaultForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
      public final ForkJoinWorkerThread newThread(ForkJoinPool var1) {
         return new ForkJoinWorkerThread(var1);
      }
   }

   public interface ForkJoinWorkerThreadFactory {
      ForkJoinWorkerThread newThread(ForkJoinPool var1);
   }
}
