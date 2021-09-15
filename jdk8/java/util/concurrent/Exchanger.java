package java.util.concurrent;

import sun.misc.Contended;
import sun.misc.Unsafe;

public class Exchanger<V> {
   private static final int ASHIFT = 7;
   private static final int MMASK = 255;
   private static final int SEQ = 256;
   private static final int NCPU = Runtime.getRuntime().availableProcessors();
   static final int FULL;
   private static final int SPINS = 1024;
   private static final Object NULL_ITEM;
   private static final Object TIMED_OUT;
   private final Exchanger.Participant participant = new Exchanger.Participant();
   private volatile Exchanger.Node[] arena;
   private volatile Exchanger.Node slot;
   private volatile int bound;
   private static final Unsafe U;
   private static final long BOUND;
   private static final long SLOT;
   private static final long MATCH;
   private static final long BLOCKER;
   private static final int ABASE;

   private final Object arenaExchange(Object var1, boolean var2, long var3) {
      Exchanger.Node[] var5 = this.arena;
      Exchanger.Node var6 = (Exchanger.Node)this.participant.get();
      int var7 = var6.index;

      int var9;
      label135:
      do {
         while(true) {
            while(true) {
               long var11;
               Exchanger.Node var13 = (Exchanger.Node)U.getObjectVolatile(var5, var11 = (long)((var7 << 7) + ABASE));
               if (var13 != null && U.compareAndSwapObject(var5, var11, var13, (Object)null)) {
                  Object var20 = var13.item;
                  var13.match = var1;
                  Thread var15 = var13.parked;
                  if (var15 != null) {
                     U.unpark(var15);
                  }

                  return var20;
               }

               int var8;
               if (var7 <= (var9 = (var8 = this.bound) & 255) && var13 == null) {
                  var6.item = var1;
                  if (U.compareAndSwapObject(var5, var11, (Object)null, var6)) {
                     long var14 = var2 && var9 == 0 ? System.nanoTime() + var3 : 0L;
                     Thread var16 = Thread.currentThread();
                     int var17 = var6.hash;
                     int var18 = 1024;

                     while(true) {
                        while(true) {
                           Object var19 = var6.match;
                           if (var19 != null) {
                              U.putOrderedObject(var6, MATCH, (Object)null);
                              var6.item = null;
                              var6.hash = var17;
                              return var19;
                           }

                           if (var18 > 0) {
                              var17 <<= 1;
                              var17 >>>= 3;
                              var17 <<= 10;
                              if (var17 == 0) {
                                 var17 = 1024 | (int)var16.getId();
                              } else if (var17 < 0) {
                                 --var18;
                                 if ((var18 & 511) == 0) {
                                    Thread.yield();
                                 }
                              }
                           } else if (U.getObjectVolatile(var5, var11) != var6) {
                              var18 = 1024;
                           } else if (!var16.isInterrupted() && var9 == 0 && (!var2 || (var3 = var14 - System.nanoTime()) > 0L)) {
                              U.putObject(var16, BLOCKER, this);
                              var6.parked = var16;
                              if (U.getObjectVolatile(var5, var11) == var6) {
                                 U.park(false, var3);
                              }

                              var6.parked = null;
                              U.putObject(var16, BLOCKER, (Object)null);
                           } else if (U.getObjectVolatile(var5, var11) == var6 && U.compareAndSwapObject(var5, var11, var6, (Object)null)) {
                              if (var9 != 0) {
                                 U.compareAndSwapInt(this, BOUND, var8, var8 + 256 - 1);
                              }

                              var6.item = null;
                              var6.hash = var17;
                              var7 = var6.index >>>= 1;
                              if (Thread.interrupted()) {
                                 return null;
                              }
                              continue label135;
                           }
                        }
                     }
                  }

                  var6.item = null;
               } else {
                  if (var6.bound != var8) {
                     var6.bound = var8;
                     var6.collides = 0;
                     var7 = var7 == var9 && var9 != 0 ? var9 - 1 : var9;
                  } else {
                     int var10;
                     if ((var10 = var6.collides) >= var9 && var9 != FULL && U.compareAndSwapInt(this, BOUND, var8, var8 + 256 + 1)) {
                        var7 = var9 + 1;
                     } else {
                        var6.collides = var10 + 1;
                        var7 = var7 == 0 ? var9 : var7 - 1;
                     }
                  }

                  var6.index = var7;
               }
            }
         }
      } while(!var2 || var9 != 0 || var3 > 0L);

      return TIMED_OUT;
   }

   private final Object slotExchange(Object var1, boolean var2, long var3) {
      Exchanger.Node var5 = (Exchanger.Node)this.participant.get();
      Thread var6 = Thread.currentThread();
      if (var6.isInterrupted()) {
         return null;
      } else {
         while(true) {
            Exchanger.Node var7;
            while((var7 = this.slot) == null) {
               if (this.arena != null) {
                  return null;
               }

               var5.item = var1;
               if (U.compareAndSwapObject(this, SLOT, (Object)null, var5)) {
                  int var12 = var5.hash;
                  long var8 = var2 ? System.nanoTime() + var3 : 0L;
                  int var10 = NCPU > 1 ? 1024 : 1;

                  Object var11;
                  while((var11 = var5.match) == null) {
                     if (var10 > 0) {
                        var12 ^= var12 << 1;
                        var12 ^= var12 >>> 3;
                        var12 ^= var12 << 10;
                        if (var12 == 0) {
                           var12 = 1024 | (int)var6.getId();
                        } else if (var12 < 0) {
                           --var10;
                           if ((var10 & 511) == 0) {
                              Thread.yield();
                           }
                        }
                     } else if (this.slot != var5) {
                        var10 = 1024;
                     } else if (var6.isInterrupted() || this.arena != null || var2 && (var3 = var8 - System.nanoTime()) <= 0L) {
                        if (U.compareAndSwapObject(this, SLOT, var5, (Object)null)) {
                           var11 = var2 && var3 <= 0L && !var6.isInterrupted() ? TIMED_OUT : null;
                           break;
                        }
                     } else {
                        U.putObject(var6, BLOCKER, this);
                        var5.parked = var6;
                        if (this.slot == var5) {
                           U.park(false, var3);
                        }

                        var5.parked = null;
                        U.putObject(var6, BLOCKER, (Object)null);
                     }
                  }

                  U.putOrderedObject(var5, MATCH, (Object)null);
                  var5.item = null;
                  var5.hash = var12;
                  return var11;
               }

               var5.item = null;
            }

            if (U.compareAndSwapObject(this, SLOT, var7, (Object)null)) {
               Object var13 = var7.item;
               var7.match = var1;
               Thread var9 = var7.parked;
               if (var9 != null) {
                  U.unpark(var9);
               }

               return var13;
            }

            if (NCPU > 1 && this.bound == 0 && U.compareAndSwapInt(this, BOUND, 0, 256)) {
               this.arena = new Exchanger.Node[FULL + 2 << 7];
            }
         }
      }
   }

   public V exchange(V var1) throws InterruptedException {
      Object var3 = var1 == null ? NULL_ITEM : var1;
      Object var2;
      if ((this.arena != null || (var2 = this.slotExchange(var3, false, 0L)) == null) && (Thread.interrupted() || (var2 = this.arenaExchange(var3, false, 0L)) == null)) {
         throw new InterruptedException();
      } else {
         return var2 == NULL_ITEM ? null : var2;
      }
   }

   public V exchange(V var1, long var2, TimeUnit var4) throws InterruptedException, TimeoutException {
      Object var6 = var1 == null ? NULL_ITEM : var1;
      long var7 = var4.toNanos(var2);
      Object var5;
      if ((this.arena != null || (var5 = this.slotExchange(var6, true, var7)) == null) && (Thread.interrupted() || (var5 = this.arenaExchange(var6, true, var7)) == null)) {
         throw new InterruptedException();
      } else if (var5 == TIMED_OUT) {
         throw new TimeoutException();
      } else {
         return var5 == NULL_ITEM ? null : var5;
      }
   }

   static {
      FULL = NCPU >= 510 ? 255 : NCPU >>> 1;
      NULL_ITEM = new Object();
      TIMED_OUT = new Object();

      int var0;
      try {
         U = Unsafe.getUnsafe();
         Class var1 = Exchanger.class;
         Class var2 = Exchanger.Node.class;
         Class var3 = Exchanger.Node[].class;
         Class var4 = Thread.class;
         BOUND = U.objectFieldOffset(var1.getDeclaredField("bound"));
         SLOT = U.objectFieldOffset(var1.getDeclaredField("slot"));
         MATCH = U.objectFieldOffset(var2.getDeclaredField("match"));
         BLOCKER = U.objectFieldOffset(var4.getDeclaredField("parkBlocker"));
         var0 = U.arrayIndexScale(var3);
         ABASE = U.arrayBaseOffset(var3) + 128;
      } catch (Exception var5) {
         throw new Error(var5);
      }

      if ((var0 & var0 - 1) != 0 || var0 > 128) {
         throw new Error("Unsupported array scale");
      }
   }

   static final class Participant extends ThreadLocal<Exchanger.Node> {
      public Exchanger.Node initialValue() {
         return new Exchanger.Node();
      }
   }

   @Contended
   static final class Node {
      int index;
      int bound;
      int collides;
      int hash;
      Object item;
      volatile Object match;
      volatile Thread parked;
   }
}
