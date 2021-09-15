package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class StampedLock implements Serializable {
   private static final long serialVersionUID = -6001602636862214147L;
   private static final int NCPU = Runtime.getRuntime().availableProcessors();
   private static final int SPINS;
   private static final int HEAD_SPINS;
   private static final int MAX_HEAD_SPINS;
   private static final int OVERFLOW_YIELD_RATE = 7;
   private static final int LG_READERS = 7;
   private static final long RUNIT = 1L;
   private static final long WBIT = 128L;
   private static final long RBITS = 127L;
   private static final long RFULL = 126L;
   private static final long ABITS = 255L;
   private static final long SBITS = -128L;
   private static final long ORIGIN = 256L;
   private static final long INTERRUPTED = 1L;
   private static final int WAITING = -1;
   private static final int CANCELLED = 1;
   private static final int RMODE = 0;
   private static final int WMODE = 1;
   private transient volatile StampedLock.WNode whead;
   private transient volatile StampedLock.WNode wtail;
   transient StampedLock.ReadLockView readLockView;
   transient StampedLock.WriteLockView writeLockView;
   transient StampedLock.ReadWriteLockView readWriteLockView;
   private transient volatile long state = 256L;
   private transient int readerOverflow;
   private static final Unsafe U;
   private static final long STATE;
   private static final long WHEAD;
   private static final long WTAIL;
   private static final long WNEXT;
   private static final long WSTATUS;
   private static final long WCOWAIT;
   private static final long PARKBLOCKER;

   public long writeLock() {
      long var1;
      long var3;
      return ((var1 = this.state) & 255L) == 0L && U.compareAndSwapLong(this, STATE, var1, var3 = var1 + 128L) ? var3 : this.acquireWrite(false, 0L);
   }

   public long tryWriteLock() {
      long var1;
      long var3;
      return ((var1 = this.state) & 255L) == 0L && U.compareAndSwapLong(this, STATE, var1, var3 = var1 + 128L) ? var3 : 0L;
   }

   public long tryWriteLock(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = var3.toNanos(var1);
      if (!Thread.interrupted()) {
         long var6;
         if ((var6 = this.tryWriteLock()) != 0L) {
            return var6;
         }

         if (var4 <= 0L) {
            return 0L;
         }

         long var8;
         if ((var8 = System.nanoTime() + var4) == 0L) {
            var8 = 1L;
         }

         if ((var6 = this.acquireWrite(true, var8)) != 1L) {
            return var6;
         }
      }

      throw new InterruptedException();
   }

   public long writeLockInterruptibly() throws InterruptedException {
      long var1;
      if (!Thread.interrupted() && (var1 = this.acquireWrite(true, 0L)) != 1L) {
         return var1;
      } else {
         throw new InterruptedException();
      }
   }

   public long readLock() {
      long var1 = this.state;
      long var3;
      return this.whead == this.wtail && (var1 & 255L) < 126L && U.compareAndSwapLong(this, STATE, var1, var3 = var1 + 1L) ? var3 : this.acquireRead(false, 0L);
   }

   public long tryReadLock() {
      long var1;
      long var3;
      while((var3 = (var1 = this.state) & 255L) != 128L) {
         long var5;
         if (var3 < 126L) {
            if (U.compareAndSwapLong(this, STATE, var1, var5 = var1 + 1L)) {
               return var5;
            }
         } else if ((var5 = this.tryIncReaderOverflow(var1)) != 0L) {
            return var5;
         }
      }

      return 0L;
   }

   public long tryReadLock(long var1, TimeUnit var3) throws InterruptedException {
      long var12 = var3.toNanos(var1);
      if (!Thread.interrupted()) {
         long var4;
         long var6;
         long var8;
         if ((var6 = (var4 = this.state) & 255L) != 128L) {
            if (var6 < 126L) {
               if (U.compareAndSwapLong(this, STATE, var4, var8 = var4 + 1L)) {
                  return var8;
               }
            } else if ((var8 = this.tryIncReaderOverflow(var4)) != 0L) {
               return var8;
            }
         }

         if (var12 <= 0L) {
            return 0L;
         }

         long var10;
         if ((var10 = System.nanoTime() + var12) == 0L) {
            var10 = 1L;
         }

         if ((var8 = this.acquireRead(true, var10)) != 1L) {
            return var8;
         }
      }

      throw new InterruptedException();
   }

   public long readLockInterruptibly() throws InterruptedException {
      long var1;
      if (!Thread.interrupted() && (var1 = this.acquireRead(true, 0L)) != 1L) {
         return var1;
      } else {
         throw new InterruptedException();
      }
   }

   public long tryOptimisticRead() {
      long var1;
      return ((var1 = this.state) & 128L) == 0L ? var1 & -128L : 0L;
   }

   public boolean validate(long var1) {
      U.loadFence();
      return (var1 & -128L) == (this.state & -128L);
   }

   public void unlockWrite(long var1) {
      if (this.state == var1 && (var1 & 128L) != 0L) {
         this.state = (var1 += 128L) == 0L ? 256L : var1;
         StampedLock.WNode var3;
         if ((var3 = this.whead) != null && var3.status != 0) {
            this.release(var3);
         }

      } else {
         throw new IllegalMonitorStateException();
      }
   }

   public void unlockRead(long var1) {
      while(true) {
         long var3;
         long var5;
         if (((var3 = this.state) & -128L) == (var1 & -128L) && (var1 & 255L) != 0L && (var5 = var3 & 255L) != 0L && var5 != 128L) {
            if (var5 < 126L) {
               if (!U.compareAndSwapLong(this, STATE, var3, var3 - 1L)) {
                  continue;
               }

               StampedLock.WNode var7;
               if (var5 == 1L && (var7 = this.whead) != null && var7.status != 0) {
                  this.release(var7);
               }
            } else if (this.tryDecReaderOverflow(var3) == 0L) {
               continue;
            }

            return;
         }

         throw new IllegalMonitorStateException();
      }
   }

   public void unlock(long var1) {
      long var3 = var1 & 255L;

      long var5;
      long var7;
      while(((var7 = this.state) & -128L) == (var1 & -128L) && (var5 = var7 & 255L) != 0L) {
         StampedLock.WNode var9;
         if (var5 == 128L) {
            if (var3 == var5) {
               this.state = (var7 += 128L) == 0L ? 256L : var7;
               if ((var9 = this.whead) != null && var9.status != 0) {
                  this.release(var9);
               }

               return;
            }
            break;
         }

         if (var3 == 0L || var3 >= 128L) {
            break;
         }

         if (var5 < 126L) {
            if (U.compareAndSwapLong(this, STATE, var7, var7 - 1L)) {
               if (var5 == 1L && (var9 = this.whead) != null && var9.status != 0) {
                  this.release(var9);
               }

               return;
            }
         } else if (this.tryDecReaderOverflow(var7) != 0L) {
            return;
         }
      }

      throw new IllegalMonitorStateException();
   }

   public long tryConvertToWriteLock(long var1) {
      long var3 = var1 & 255L;

      long var7;
      while(((var7 = this.state) & -128L) == (var1 & -128L)) {
         long var5;
         long var9;
         if ((var5 = var7 & 255L) == 0L) {
            if (var3 != 0L) {
               break;
            }

            if (U.compareAndSwapLong(this, STATE, var7, var9 = var7 + 128L)) {
               return var9;
            }
         } else {
            if (var5 == 128L) {
               if (var3 == var5) {
                  return var1;
               }
               break;
            }

            if (var5 != 1L || var3 == 0L) {
               break;
            }

            if (U.compareAndSwapLong(this, STATE, var7, var9 = var7 - 1L + 128L)) {
               return var9;
            }
         }
      }

      return 0L;
   }

   public long tryConvertToReadLock(long var1) {
      long var3 = var1 & 255L;

      long var7;
      while(((var7 = this.state) & -128L) == (var1 & -128L)) {
         long var5;
         long var9;
         if ((var5 = var7 & 255L) != 0L) {
            if (var5 == 128L) {
               if (var3 == var5) {
                  this.state = var9 = var7 + 129L;
                  StampedLock.WNode var11;
                  if ((var11 = this.whead) != null && var11.status != 0) {
                     this.release(var11);
                  }

                  return var9;
               }
            } else if (var3 != 0L && var3 < 128L) {
               return var1;
            }
            break;
         }

         if (var3 != 0L) {
            break;
         }

         if (var5 < 126L) {
            if (U.compareAndSwapLong(this, STATE, var7, var9 = var7 + 1L)) {
               return var9;
            }
         } else if ((var9 = this.tryIncReaderOverflow(var7)) != 0L) {
            return var9;
         }
      }

      return 0L;
   }

   public long tryConvertToOptimisticRead(long var1) {
      long var3 = var1 & 255L;
      U.loadFence();

      long var7;
      while(((var7 = this.state) & -128L) == (var1 & -128L)) {
         long var5;
         if ((var5 = var7 & 255L) == 0L) {
            if (var3 == 0L) {
               return var7;
            }
            break;
         }

         long var9;
         StampedLock.WNode var11;
         if (var5 == 128L) {
            if (var3 == var5) {
               this.state = var9 = (var7 += 128L) == 0L ? 256L : var7;
               if ((var11 = this.whead) != null && var11.status != 0) {
                  this.release(var11);
               }

               return var9;
            }
            break;
         }

         if (var3 == 0L || var3 >= 128L) {
            break;
         }

         if (var5 < 126L) {
            if (U.compareAndSwapLong(this, STATE, var7, var9 = var7 - 1L)) {
               if (var5 == 1L && (var11 = this.whead) != null && var11.status != 0) {
                  this.release(var11);
               }

               return var9 & -128L;
            }
         } else if ((var9 = this.tryDecReaderOverflow(var7)) != 0L) {
            return var9 & -128L;
         }
      }

      return 0L;
   }

   public boolean tryUnlockWrite() {
      long var1;
      if (((var1 = this.state) & 128L) != 0L) {
         this.state = (var1 += 128L) == 0L ? 256L : var1;
         StampedLock.WNode var3;
         if ((var3 = this.whead) != null && var3.status != 0) {
            this.release(var3);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean tryUnlockRead() {
      while(true) {
         long var1;
         long var3;
         if ((var3 = (var1 = this.state) & 255L) != 0L && var3 < 128L) {
            if (var3 < 126L) {
               if (!U.compareAndSwapLong(this, STATE, var1, var1 - 1L)) {
                  continue;
               }

               StampedLock.WNode var5;
               if (var3 == 1L && (var5 = this.whead) != null && var5.status != 0) {
                  this.release(var5);
               }

               return true;
            }

            if (this.tryDecReaderOverflow(var1) == 0L) {
               continue;
            }

            return true;
         }

         return false;
      }
   }

   private int getReadLockCount(long var1) {
      long var3;
      if ((var3 = var1 & 127L) >= 126L) {
         var3 = 126L + (long)this.readerOverflow;
      }

      return (int)var3;
   }

   public boolean isWriteLocked() {
      return (this.state & 128L) != 0L;
   }

   public boolean isReadLocked() {
      return (this.state & 127L) != 0L;
   }

   public int getReadLockCount() {
      return this.getReadLockCount(this.state);
   }

   public String toString() {
      long var1 = this.state;
      return super.toString() + ((var1 & 255L) == 0L ? "[Unlocked]" : ((var1 & 128L) != 0L ? "[Write-locked]" : "[Read-locks:" + this.getReadLockCount(var1) + "]"));
   }

   public Lock asReadLock() {
      StampedLock.ReadLockView var1;
      return (var1 = this.readLockView) != null ? var1 : (this.readLockView = new StampedLock.ReadLockView());
   }

   public Lock asWriteLock() {
      StampedLock.WriteLockView var1;
      return (var1 = this.writeLockView) != null ? var1 : (this.writeLockView = new StampedLock.WriteLockView());
   }

   public ReadWriteLock asReadWriteLock() {
      StampedLock.ReadWriteLockView var1;
      return (var1 = this.readWriteLockView) != null ? var1 : (this.readWriteLockView = new StampedLock.ReadWriteLockView());
   }

   final void unstampedUnlockWrite() {
      long var2;
      if (((var2 = this.state) & 128L) == 0L) {
         throw new IllegalMonitorStateException();
      } else {
         this.state = (var2 += 128L) == 0L ? 256L : var2;
         StampedLock.WNode var1;
         if ((var1 = this.whead) != null && var1.status != 0) {
            this.release(var1);
         }

      }
   }

   final void unstampedUnlockRead() {
      while(true) {
         long var1;
         long var3;
         if ((var3 = (var1 = this.state) & 255L) != 0L && var3 < 128L) {
            if (var3 < 126L) {
               if (!U.compareAndSwapLong(this, STATE, var1, var1 - 1L)) {
                  continue;
               }

               StampedLock.WNode var5;
               if (var3 == 1L && (var5 = this.whead) != null && var5.status != 0) {
                  this.release(var5);
               }
            } else if (this.tryDecReaderOverflow(var1) == 0L) {
               continue;
            }

            return;
         }

         throw new IllegalMonitorStateException();
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.state = 256L;
   }

   private long tryIncReaderOverflow(long var1) {
      if ((var1 & 255L) == 126L) {
         if (U.compareAndSwapLong(this, STATE, var1, var1 | 127L)) {
            ++this.readerOverflow;
            this.state = var1;
            return var1;
         }
      } else if ((LockSupport.nextSecondarySeed() & 7) == 0) {
         Thread.yield();
      }

      return 0L;
   }

   private long tryDecReaderOverflow(long var1) {
      if ((var1 & 255L) == 126L) {
         if (U.compareAndSwapLong(this, STATE, var1, var1 | 127L)) {
            int var3;
            long var4;
            if ((var3 = this.readerOverflow) > 0) {
               this.readerOverflow = var3 - 1;
               var4 = var1;
            } else {
               var4 = var1 - 1L;
            }

            this.state = var4;
            return var4;
         }
      } else if ((LockSupport.nextSecondarySeed() & 7) == 0) {
         Thread.yield();
      }

      return 0L;
   }

   private void release(StampedLock.WNode var1) {
      if (var1 != null) {
         U.compareAndSwapInt(var1, WSTATUS, -1, 0);
         StampedLock.WNode var2;
         if ((var2 = var1.next) == null || var2.status == 1) {
            for(StampedLock.WNode var4 = this.wtail; var4 != null && var4 != var1; var4 = var4.prev) {
               if (var4.status <= 0) {
                  var2 = var4;
               }
            }
         }

         Thread var3;
         if (var2 != null && (var3 = var2.thread) != null) {
            U.unpark(var3);
         }
      }

   }

   private long acquireWrite(boolean var1, long var2) {
      StampedLock.WNode var4 = null;
      int var6 = -1;

      long var7;
      long var9;
      long var19;
      do {
         while((var7 = (var9 = this.state) & 255L) != 0L) {
            if (var6 >= 0) {
               if (var6 > 0) {
                  if (LockSupport.nextSecondarySeed() >= 0) {
                     --var6;
                  }
               } else {
                  StampedLock.WNode var5;
                  if ((var5 = this.wtail) == null) {
                     StampedLock.WNode var13 = new StampedLock.WNode(1, (StampedLock.WNode)null);
                     if (U.compareAndSwapObject(this, WHEAD, (Object)null, var13)) {
                        this.wtail = var13;
                     }
                  } else if (var4 == null) {
                     var4 = new StampedLock.WNode(1, var5);
                  } else if (var4.prev != var5) {
                     var4.prev = var5;
                  } else if (U.compareAndSwapObject(this, WTAIL, var5, var4)) {
                     var5.next = var4;
                     var6 = -1;

                     while(true) {
                        while(true) {
                           StampedLock.WNode var16;
                           label107:
                           do {
                              if ((var16 = this.whead) != var5) {
                                 StampedLock.WNode var18;
                                 if (var16 != null) {
                                    while((var18 = var16.cowait) != null) {
                                       Thread var20;
                                       if (U.compareAndSwapObject(var16, WCOWAIT, var18, var18.cowait) && (var20 = var18.thread) != null) {
                                          U.unpark(var20);
                                       }
                                    }
                                 }
                              } else {
                                 if (var6 < 0) {
                                    var6 = HEAD_SPINS;
                                 } else if (var6 < MAX_HEAD_SPINS) {
                                    var6 <<= 1;
                                 }

                                 int var11 = var6;

                                 long var12;
                                 long var14;
                                 do {
                                    while(((var12 = this.state) & 255L) != 0L) {
                                       if (LockSupport.nextSecondarySeed() >= 0) {
                                          --var11;
                                          if (var11 <= 0) {
                                             continue label107;
                                          }
                                       }
                                    }
                                 } while(!U.compareAndSwapLong(this, STATE, var12, var14 = var12 + 128L));

                                 this.whead = var4;
                                 var4.prev = null;
                                 return var14;
                              }
                           } while(this.whead != var16);

                           StampedLock.WNode var8;
                           if ((var8 = var4.prev) != var5) {
                              if (var8 != null) {
                                 var5 = var8;
                                 var8.next = var4;
                              }
                           } else {
                              int var10;
                              if ((var10 = var5.status) == 0) {
                                 U.compareAndSwapInt(var5, WSTATUS, 0, -1);
                              } else if (var10 == 1) {
                                 StampedLock.WNode var17;
                                 if ((var17 = var5.prev) != null) {
                                    var4.prev = var17;
                                    var17.next = var4;
                                 }
                              } else {
                                 if (var2 == 0L) {
                                    var19 = 0L;
                                 } else if ((var19 = var2 - System.nanoTime()) <= 0L) {
                                    return this.cancelWaiter(var4, var4, false);
                                 }

                                 Thread var21 = Thread.currentThread();
                                 U.putObject(var21, PARKBLOCKER, this);
                                 var4.thread = var21;
                                 if (var5.status < 0 && (var5 != var16 || (this.state & 255L) != 0L) && this.whead == var16 && var4.prev == var5) {
                                    U.park(false, var19);
                                 }

                                 var4.thread = null;
                                 U.putObject(var21, PARKBLOCKER, (Object)null);
                                 if (var1 && Thread.interrupted()) {
                                    return this.cancelWaiter(var4, var4, true);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               var6 = var7 == 128L && this.wtail == this.whead ? SPINS : 0;
            }
         }
      } while(!U.compareAndSwapLong(this, STATE, var9, var19 = var9 + 128L));

      return var19;
   }

   private long acquireRead(boolean var1, long var2) {
      StampedLock.WNode var4 = null;
      int var6 = -1;

      while(true) {
         StampedLock.WNode var5;
         StampedLock.WNode var7;
         long var12;
         if ((var7 = this.whead) == (var5 = this.wtail)) {
            label272: {
               while(true) {
                  long var8;
                  long var10;
                  if ((var8 = (var10 = this.state) & 255L) < 126L) {
                     if (U.compareAndSwapLong(this, STATE, var10, var12 = var10 + 1L)) {
                        break;
                     }
                  } else if (var8 < 128L && (var12 = this.tryIncReaderOverflow(var10)) != 0L) {
                     break;
                  }

                  if (var8 >= 128L) {
                     if (var6 > 0) {
                        if (LockSupport.nextSecondarySeed() >= 0) {
                           --var6;
                        }
                     } else {
                        if (var6 == 0) {
                           StampedLock.WNode var14 = this.whead;
                           StampedLock.WNode var15 = this.wtail;
                           if (var14 == var7 && var15 == var5) {
                              break label272;
                           }

                           var7 = var14;
                           var5 = var15;
                           if (var14 != var15) {
                              break label272;
                           }
                        }

                        var6 = SPINS;
                     }
                  }
               }

               return var12;
            }
         }

         StampedLock.WNode var20;
         if (var5 == null) {
            var20 = new StampedLock.WNode(1, (StampedLock.WNode)null);
            if (U.compareAndSwapObject(this, WHEAD, (Object)null, var20)) {
               this.wtail = var20;
            }
         } else if (var4 == null) {
            var4 = new StampedLock.WNode(0, var5);
         } else {
            StampedLock.WNode var9;
            Thread var13;
            long var24;
            if (var7 != var5 && var5.mode == 0) {
               if (!U.compareAndSwapObject(var5, WCOWAIT, var4.cowait = var5.cowait, var4)) {
                  var4.cowait = null;
               } else {
                  while(true) {
                     Thread var22;
                     if ((var7 = this.whead) != null && (var9 = var7.cowait) != null && U.compareAndSwapObject(var7, WCOWAIT, var9, var9.cowait) && (var22 = var9.thread) != null) {
                        U.unpark(var22);
                     }

                     if (var7 == (var20 = var5.prev) || var7 == var5 || var20 == null) {
                        label282: {
                           long var28;
                           while(true) {
                              long var26;
                              if ((var24 = (var26 = this.state) & 255L) < 126L) {
                                 if (U.compareAndSwapLong(this, STATE, var26, var28 = var26 + 1L)) {
                                    break;
                                 }
                              } else if (var24 < 128L && (var28 = this.tryIncReaderOverflow(var26)) != 0L) {
                                 break;
                              }

                              if (var24 >= 128L) {
                                 break label282;
                              }
                           }

                           return var28;
                        }
                     }

                     if (this.whead == var7 && var5.prev == var20) {
                        if (var20 == null || var7 == var5 || var5.status > 0) {
                           var4 = null;
                           break;
                        }

                        if (var2 == 0L) {
                           var24 = 0L;
                        } else if ((var24 = var2 - System.nanoTime()) <= 0L) {
                           return this.cancelWaiter(var4, var5, false);
                        }

                        var13 = Thread.currentThread();
                        U.putObject(var13, PARKBLOCKER, this);
                        var4.thread = var13;
                        if ((var7 != var20 || (this.state & 255L) == 128L) && this.whead == var7 && var5.prev == var20) {
                           U.park(false, var24);
                        }

                        var4.thread = null;
                        U.putObject(var13, PARKBLOCKER, (Object)null);
                        if (var1 && Thread.interrupted()) {
                           return this.cancelWaiter(var4, var5, true);
                        }
                     }
                  }
               }
            } else if (var4.prev != var5) {
               var4.prev = var5;
            } else if (U.compareAndSwapObject(this, WTAIL, var5, var4)) {
               var5.next = var4;
               var6 = -1;

               long var16;
               label194:
               while(true) {
                  if ((var7 = this.whead) == var5) {
                     if (var6 < 0) {
                        var6 = HEAD_SPINS;
                     } else if (var6 < MAX_HEAD_SPINS) {
                        var6 <<= 1;
                     }

                     int var11 = var6;

                     do {
                        do {
                           do {
                              long var27;
                              if ((var12 = (var27 = this.state) & 255L) < 126L) {
                                 if (U.compareAndSwapLong(this, STATE, var27, var16 = var27 + 1L)) {
                                    break label194;
                                 }
                              } else if (var12 < 128L && (var16 = this.tryIncReaderOverflow(var27)) != 0L) {
                                 break label194;
                              }
                           } while(var12 < 128L);
                        } while(LockSupport.nextSecondarySeed() < 0);

                        --var11;
                     } while(var11 > 0);
                  } else {
                     StampedLock.WNode var23;
                     if (var7 != null) {
                        while((var23 = var7.cowait) != null) {
                           Thread var25;
                           if (U.compareAndSwapObject(var7, WCOWAIT, var23, var23.cowait) && (var25 = var23.thread) != null) {
                              U.unpark(var25);
                           }
                        }
                     }
                  }

                  if (this.whead == var7) {
                     if ((var20 = var4.prev) != var5) {
                        if (var20 != null) {
                           var5 = var20;
                           var20.next = var4;
                        }
                     } else {
                        int var21;
                        if ((var21 = var5.status) == 0) {
                           U.compareAndSwapInt(var5, WSTATUS, 0, -1);
                        } else if (var21 == 1) {
                           if ((var9 = var5.prev) != null) {
                              var4.prev = var9;
                              var9.next = var4;
                           }
                        } else {
                           if (var2 == 0L) {
                              var24 = 0L;
                           } else if ((var24 = var2 - System.nanoTime()) <= 0L) {
                              return this.cancelWaiter(var4, var4, false);
                           }

                           var13 = Thread.currentThread();
                           U.putObject(var13, PARKBLOCKER, this);
                           var4.thread = var13;
                           if (var5.status < 0 && (var5 != var7 || (this.state & 255L) == 128L) && this.whead == var7 && var4.prev == var5) {
                              U.park(false, var24);
                           }

                           var4.thread = null;
                           U.putObject(var13, PARKBLOCKER, (Object)null);
                           if (var1 && Thread.interrupted()) {
                              return this.cancelWaiter(var4, var4, true);
                           }
                        }
                     }
                  }
               }

               this.whead = var4;
               var4.prev = null;

               StampedLock.WNode var18;
               while((var18 = var4.cowait) != null) {
                  Thread var19;
                  if (U.compareAndSwapObject(var4, WCOWAIT, var18, var18.cowait) && (var19 = var18.thread) != null) {
                     U.unpark(var19);
                  }
               }

               return var16;
            }
         }
      }
   }

   private long cancelWaiter(StampedLock.WNode var1, StampedLock.WNode var2, boolean var3) {
      StampedLock.WNode var7;
      StampedLock.WNode var8;
      if (var1 != null && var2 != null) {
         var1.status = 1;
         StampedLock.WNode var5 = var2;

         StampedLock.WNode var6;
         while((var6 = var5.cowait) != null) {
            if (var6.status == 1) {
               U.compareAndSwapObject(var5, WCOWAIT, var6, var6.cowait);
               var5 = var2;
            } else {
               var5 = var6;
            }
         }

         if (var2 == var1) {
            Thread var4;
            for(var5 = var2.cowait; var5 != null; var5 = var5.cowait) {
               if ((var4 = var5.thread) != null) {
                  U.unpark(var4);
               }
            }

            for(var5 = var1.prev; var5 != null; var5 = var7) {
               label132: {
                  StampedLock.WNode var10003;
                  do {
                     if ((var6 = var1.next) != null && var6.status != 1) {
                        break label132;
                     }

                     var8 = null;

                     for(StampedLock.WNode var9 = this.wtail; var9 != null && var9 != var1; var9 = var9.prev) {
                        if (var9.status != 1) {
                           var8 = var9;
                        }
                     }

                     if (var6 == var8) {
                        break;
                     }

                     var10003 = var6;
                     var6 = var8;
                  } while(!U.compareAndSwapObject(var1, WNEXT, var10003, var8));

                  if (var6 == null && var1 == this.wtail) {
                     U.compareAndSwapObject(this, WTAIL, var1, var5);
                  }
               }

               if (var5.next == var1) {
                  U.compareAndSwapObject(var5, WNEXT, var1, var6);
               }

               if (var6 != null && (var4 = var6.thread) != null) {
                  var6.thread = null;
                  U.unpark(var4);
               }

               if (var5.status != 1 || (var7 = var5.prev) == null) {
                  break;
               }

               var1.prev = var7;
               U.compareAndSwapObject(var7, WNEXT, var5, var6);
            }
         }
      }

      StampedLock.WNode var10;
      while((var10 = this.whead) != null) {
         if ((var7 = var10.next) == null || var7.status == 1) {
            for(var8 = this.wtail; var8 != null && var8 != var10; var8 = var8.prev) {
               if (var8.status <= 0) {
                  var7 = var8;
               }
            }
         }

         if (var10 == this.whead) {
            long var11;
            if (var7 != null && var10.status == 0 && ((var11 = this.state) & 255L) != 128L && (var11 == 0L || var7.mode == 0)) {
               this.release(var10);
            }
            break;
         }
      }

      return !var3 && !Thread.interrupted() ? 0L : 1L;
   }

   static {
      SPINS = NCPU > 1 ? 64 : 0;
      HEAD_SPINS = NCPU > 1 ? 1024 : 0;
      MAX_HEAD_SPINS = NCPU > 1 ? 65536 : 0;

      try {
         U = Unsafe.getUnsafe();
         Class var0 = StampedLock.class;
         Class var1 = StampedLock.WNode.class;
         STATE = U.objectFieldOffset(var0.getDeclaredField("state"));
         WHEAD = U.objectFieldOffset(var0.getDeclaredField("whead"));
         WTAIL = U.objectFieldOffset(var0.getDeclaredField("wtail"));
         WSTATUS = U.objectFieldOffset(var1.getDeclaredField("status"));
         WNEXT = U.objectFieldOffset(var1.getDeclaredField("next"));
         WCOWAIT = U.objectFieldOffset(var1.getDeclaredField("cowait"));
         Class var2 = Thread.class;
         PARKBLOCKER = U.objectFieldOffset(var2.getDeclaredField("parkBlocker"));
      } catch (Exception var3) {
         throw new Error(var3);
      }
   }

   final class ReadWriteLockView implements ReadWriteLock {
      public Lock readLock() {
         return StampedLock.this.asReadLock();
      }

      public Lock writeLock() {
         return StampedLock.this.asWriteLock();
      }
   }

   final class WriteLockView implements Lock {
      public void lock() {
         StampedLock.this.writeLock();
      }

      public void lockInterruptibly() throws InterruptedException {
         StampedLock.this.writeLockInterruptibly();
      }

      public boolean tryLock() {
         return StampedLock.this.tryWriteLock() != 0L;
      }

      public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
         return StampedLock.this.tryWriteLock(var1, var3) != 0L;
      }

      public void unlock() {
         StampedLock.this.unstampedUnlockWrite();
      }

      public Condition newCondition() {
         throw new UnsupportedOperationException();
      }
   }

   final class ReadLockView implements Lock {
      public void lock() {
         StampedLock.this.readLock();
      }

      public void lockInterruptibly() throws InterruptedException {
         StampedLock.this.readLockInterruptibly();
      }

      public boolean tryLock() {
         return StampedLock.this.tryReadLock() != 0L;
      }

      public boolean tryLock(long var1, TimeUnit var3) throws InterruptedException {
         return StampedLock.this.tryReadLock(var1, var3) != 0L;
      }

      public void unlock() {
         StampedLock.this.unstampedUnlockRead();
      }

      public Condition newCondition() {
         throw new UnsupportedOperationException();
      }
   }

   static final class WNode {
      volatile StampedLock.WNode prev;
      volatile StampedLock.WNode next;
      volatile StampedLock.WNode cowait;
      volatile Thread thread;
      volatile int status;
      final int mode;

      WNode(int var1, StampedLock.WNode var2) {
         this.mode = var1;
         this.prev = var2;
      }
   }
}
