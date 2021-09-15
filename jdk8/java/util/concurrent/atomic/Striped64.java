package java.util.concurrent.atomic;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;
import sun.misc.Contended;
import sun.misc.Unsafe;

abstract class Striped64 extends Number {
   static final int NCPU = Runtime.getRuntime().availableProcessors();
   transient volatile Striped64.Cell[] cells;
   transient volatile long base;
   transient volatile int cellsBusy;
   private static final Unsafe UNSAFE;
   private static final long BASE;
   private static final long CELLSBUSY;
   private static final long PROBE;

   final boolean casBase(long var1, long var3) {
      return UNSAFE.compareAndSwapLong(this, BASE, var1, var3);
   }

   final boolean casCellsBusy() {
      return UNSAFE.compareAndSwapInt(this, CELLSBUSY, 0, 1);
   }

   static final int getProbe() {
      return UNSAFE.getInt(Thread.currentThread(), PROBE);
   }

   static final int advanceProbe(int var0) {
      var0 ^= var0 << 13;
      var0 ^= var0 >>> 17;
      var0 ^= var0 << 5;
      UNSAFE.putInt(Thread.currentThread(), PROBE, var0);
      return var0;
   }

   final void longAccumulate(long var1, LongBinaryOperator var3, boolean var4) {
      int var5;
      if ((var5 = getProbe()) == 0) {
         ThreadLocalRandom.current();
         var5 = getProbe();
         var4 = true;
      }

      boolean var6 = false;

      while(true) {
         Striped64.Cell[] var7;
         int var9;
         long var10;
         if ((var7 = this.cells) != null && (var9 = var7.length) > 0) {
            Striped64.Cell var8;
            if ((var8 = var7[var9 - 1 & var5]) == null) {
               if (this.cellsBusy == 0) {
                  Striped64.Cell var32 = new Striped64.Cell(var1);
                  if (this.cellsBusy == 0 && this.casCellsBusy()) {
                     boolean var33 = false;

                     try {
                        Striped64.Cell[] var14;
                        int var15;
                        int var16;
                        if ((var14 = this.cells) != null && (var15 = var14.length) > 0 && var14[var16 = var15 - 1 & var5] == null) {
                           var14[var16] = var32;
                           var33 = true;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     if (var33) {
                        break;
                     }
                     continue;
                  }
               }

               var6 = false;
            } else if (!var4) {
               var4 = true;
            } else {
               if (var8.cas(var10 = var8.value, var3 == null ? var10 + var1 : var3.applyAsLong(var10, var1))) {
                  break;
               }

               if (var9 < NCPU && this.cells == var7) {
                  if (!var6) {
                     var6 = true;
                  } else if (this.cellsBusy == 0 && this.casCellsBusy()) {
                     try {
                        if (this.cells == var7) {
                           Striped64.Cell[] var34 = new Striped64.Cell[var9 << 1];

                           for(int var35 = 0; var35 < var9; ++var35) {
                              var34[var35] = var7[var35];
                           }

                           this.cells = var34;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     var6 = false;
                     continue;
                  }
               } else {
                  var6 = false;
               }
            }

            var5 = advanceProbe(var5);
         } else if (this.cellsBusy == 0 && this.cells == var7 && this.casCellsBusy()) {
            boolean var12 = false;

            try {
               if (this.cells == var7) {
                  Striped64.Cell[] var13 = new Striped64.Cell[2];
                  var13[var5 & 1] = new Striped64.Cell(var1);
                  this.cells = var13;
                  var12 = true;
               }
            } finally {
               this.cellsBusy = 0;
            }

            if (var12) {
               break;
            }
         } else if (this.casBase(var10 = this.base, var3 == null ? var10 + var1 : var3.applyAsLong(var10, var1))) {
            break;
         }
      }

   }

   final void doubleAccumulate(double var1, DoubleBinaryOperator var3, boolean var4) {
      int var5;
      if ((var5 = getProbe()) == 0) {
         ThreadLocalRandom.current();
         var5 = getProbe();
         var4 = true;
      }

      boolean var6 = false;

      while(true) {
         Striped64.Cell[] var7;
         int var9;
         long var10;
         if ((var7 = this.cells) != null && (var9 = var7.length) > 0) {
            Striped64.Cell var8;
            if ((var8 = var7[var9 - 1 & var5]) == null) {
               if (this.cellsBusy == 0) {
                  Striped64.Cell var32 = new Striped64.Cell(Double.doubleToRawLongBits(var1));
                  if (this.cellsBusy == 0 && this.casCellsBusy()) {
                     boolean var33 = false;

                     try {
                        Striped64.Cell[] var14;
                        int var15;
                        int var16;
                        if ((var14 = this.cells) != null && (var15 = var14.length) > 0 && var14[var16 = var15 - 1 & var5] == null) {
                           var14[var16] = var32;
                           var33 = true;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     if (var33) {
                        break;
                     }
                     continue;
                  }
               }

               var6 = false;
            } else if (!var4) {
               var4 = true;
            } else {
               if (var8.cas(var10 = var8.value, var3 == null ? Double.doubleToRawLongBits(Double.longBitsToDouble(var10) + var1) : Double.doubleToRawLongBits(var3.applyAsDouble(Double.longBitsToDouble(var10), var1)))) {
                  break;
               }

               if (var9 < NCPU && this.cells == var7) {
                  if (!var6) {
                     var6 = true;
                  } else if (this.cellsBusy == 0 && this.casCellsBusy()) {
                     try {
                        if (this.cells == var7) {
                           Striped64.Cell[] var34 = new Striped64.Cell[var9 << 1];

                           for(int var35 = 0; var35 < var9; ++var35) {
                              var34[var35] = var7[var35];
                           }

                           this.cells = var34;
                        }
                     } finally {
                        this.cellsBusy = 0;
                     }

                     var6 = false;
                     continue;
                  }
               } else {
                  var6 = false;
               }
            }

            var5 = advanceProbe(var5);
         } else if (this.cellsBusy == 0 && this.cells == var7 && this.casCellsBusy()) {
            boolean var12 = false;

            try {
               if (this.cells == var7) {
                  Striped64.Cell[] var13 = new Striped64.Cell[2];
                  var13[var5 & 1] = new Striped64.Cell(Double.doubleToRawLongBits(var1));
                  this.cells = var13;
                  var12 = true;
               }
            } finally {
               this.cellsBusy = 0;
            }

            if (var12) {
               break;
            }
         } else if (this.casBase(var10 = this.base, var3 == null ? Double.doubleToRawLongBits(Double.longBitsToDouble(var10) + var1) : Double.doubleToRawLongBits(var3.applyAsDouble(Double.longBitsToDouble(var10), var1)))) {
            break;
         }
      }

   }

   static {
      try {
         UNSAFE = Unsafe.getUnsafe();
         Class var0 = Striped64.class;
         BASE = UNSAFE.objectFieldOffset(var0.getDeclaredField("base"));
         CELLSBUSY = UNSAFE.objectFieldOffset(var0.getDeclaredField("cellsBusy"));
         Class var1 = Thread.class;
         PROBE = UNSAFE.objectFieldOffset(var1.getDeclaredField("threadLocalRandomProbe"));
      } catch (Exception var2) {
         throw new Error(var2);
      }
   }

   @Contended
   static final class Cell {
      volatile long value;
      private static final Unsafe UNSAFE;
      private static final long valueOffset;

      Cell(long var1) {
         this.value = var1;
      }

      final boolean cas(long var1, long var3) {
         return UNSAFE.compareAndSwapLong(this, valueOffset, var1, var3);
      }

      static {
         try {
            UNSAFE = Unsafe.getUnsafe();
            Class var0 = Striped64.Cell.class;
            valueOffset = UNSAFE.objectFieldOffset(var0.getDeclaredField("value"));
         } catch (Exception var1) {
            throw new Error(var1);
         }
      }
   }
}
