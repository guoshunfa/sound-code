package java.util;

import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.LongBinaryOperator;

class ArrayPrefixHelpers {
   static final int CUMULATE = 1;
   static final int SUMMED = 2;
   static final int FINISHED = 4;
   static final int MIN_PARTITION = 16;

   private ArrayPrefixHelpers() {
   }

   static final class IntCumulateTask extends CountedCompleter<Void> {
      final int[] array;
      final IntBinaryOperator function;
      ArrayPrefixHelpers.IntCumulateTask left;
      ArrayPrefixHelpers.IntCumulateTask right;
      int in;
      int out;
      final int lo;
      final int hi;
      final int origin;
      final int fence;
      final int threshold;

      public IntCumulateTask(ArrayPrefixHelpers.IntCumulateTask var1, IntBinaryOperator var2, int[] var3, int var4, int var5) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.lo = this.origin = var4;
         this.hi = this.fence = var5;
         int var6;
         this.threshold = (var6 = (var5 - var4) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : var6;
      }

      IntCumulateTask(ArrayPrefixHelpers.IntCumulateTask var1, IntBinaryOperator var2, int[] var3, int var4, int var5, int var6, int var7, int var8) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.origin = var4;
         this.fence = var5;
         this.threshold = var6;
         this.lo = var7;
         this.hi = var8;
      }

      public final void compute() {
         IntBinaryOperator var1;
         int[] var2;
         if ((var1 = this.function) != null && (var2 = this.array) != null) {
            int var3 = this.threshold;
            int var4 = this.origin;
            int var5 = this.fence;
            ArrayPrefixHelpers.IntCumulateTask var8 = this;

            int var6;
            int var7;
            while((var6 = var8.lo) >= 0 && (var7 = var8.hi) <= var2.length) {
               ArrayPrefixHelpers.IntCumulateTask var11;
               int var12;
               int var13;
               if (var7 - var6 <= var3) {
                  int var17;
                  int var18;
                  do {
                     if (((var18 = var8.getPendingCount()) & 4) != 0) {
                        return;
                     }

                     var17 = (var18 & 1) != 0 ? 4 : (var6 > var4 ? 2 : 6);
                  } while(!var8.compareAndSetPendingCount(var18, var18 | var17));

                  int var19;
                  if (var17 != 2) {
                     if (var6 == var4) {
                        var18 = var2[var4];
                        var19 = var4 + 1;
                     } else {
                        var18 = var8.in;
                        var19 = var6;
                     }

                     for(var12 = var19; var12 < var7; ++var12) {
                        var2[var12] = var18 = var1.applyAsInt(var18, var2[var12]);
                     }
                  } else if (var7 < var5) {
                     var18 = var2[var6];

                     for(var19 = var6 + 1; var19 < var7; ++var19) {
                        var18 = var1.applyAsInt(var18, var2[var19]);
                     }
                  } else {
                     var18 = var8.in;
                  }

                  var8.out = var18;

                  while(true) {
                     while((var11 = (ArrayPrefixHelpers.IntCumulateTask)var8.getCompleter()) != null) {
                        var12 = var11.getPendingCount();
                        if ((var12 & var17 & 4) != 0) {
                           var8 = var11;
                        } else if ((var12 & var17 & 2) != 0) {
                           ArrayPrefixHelpers.IntCumulateTask var15;
                           int var16;
                           ArrayPrefixHelpers.IntCumulateTask var20;
                           if ((var20 = var11.left) != null && (var15 = var11.right) != null) {
                              var16 = var20.out;
                              var11.out = var15.hi == var5 ? var16 : var1.applyAsInt(var16, var15.out);
                           }

                           var16 = (var12 & 1) == 0 && var11.lo == var4 ? 1 : 0;
                           if ((var13 = var12 | var17 | var16) == var12 || var11.compareAndSetPendingCount(var12, var13)) {
                              var17 = 2;
                              var8 = var11;
                              if (var16 != 0) {
                                 var11.fork();
                              }
                           }
                        } else if (var11.compareAndSetPendingCount(var12, var12 | var17)) {
                           return;
                        }
                     }

                     if ((var17 & 4) != 0) {
                        var8.quietlyComplete();
                     }

                     return;
                  }
               }

               ArrayPrefixHelpers.IntCumulateTask var9 = var8.left;
               ArrayPrefixHelpers.IntCumulateTask var10 = var8.right;
               if (var9 == null) {
                  var12 = var6 + var7 >>> 1;
                  var11 = var8.right = new ArrayPrefixHelpers.IntCumulateTask(var8, var1, var2, var4, var5, var3, var12, var7);
                  var8 = var8.left = new ArrayPrefixHelpers.IntCumulateTask(var8, var1, var2, var4, var5, var3, var6, var12);
               } else {
                  var12 = var8.in;
                  var9.in = var12;
                  var8 = null;
                  var11 = null;
                  if (var10 != null) {
                     var13 = var9.out;
                     var10.in = var6 == var4 ? var13 : var1.applyAsInt(var12, var13);

                     int var14;
                     while(((var14 = var10.getPendingCount()) & 1) == 0) {
                        if (var10.compareAndSetPendingCount(var14, var14 | 1)) {
                           var8 = var10;
                           break;
                        }
                     }
                  }

                  while(((var13 = var9.getPendingCount()) & 1) == 0) {
                     if (var9.compareAndSetPendingCount(var13, var13 | 1)) {
                        if (var8 != null) {
                           var11 = var8;
                        }

                        var8 = var9;
                        break;
                     }
                  }

                  if (var8 == null) {
                     break;
                  }
               }

               if (var11 != null) {
                  var11.fork();
               }
            }

         } else {
            throw new NullPointerException();
         }
      }
   }

   static final class DoubleCumulateTask extends CountedCompleter<Void> {
      final double[] array;
      final DoubleBinaryOperator function;
      ArrayPrefixHelpers.DoubleCumulateTask left;
      ArrayPrefixHelpers.DoubleCumulateTask right;
      double in;
      double out;
      final int lo;
      final int hi;
      final int origin;
      final int fence;
      final int threshold;

      public DoubleCumulateTask(ArrayPrefixHelpers.DoubleCumulateTask var1, DoubleBinaryOperator var2, double[] var3, int var4, int var5) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.lo = this.origin = var4;
         this.hi = this.fence = var5;
         int var6;
         this.threshold = (var6 = (var5 - var4) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : var6;
      }

      DoubleCumulateTask(ArrayPrefixHelpers.DoubleCumulateTask var1, DoubleBinaryOperator var2, double[] var3, int var4, int var5, int var6, int var7, int var8) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.origin = var4;
         this.fence = var5;
         this.threshold = var6;
         this.lo = var7;
         this.hi = var8;
      }

      public final void compute() {
         DoubleBinaryOperator var1;
         double[] var2;
         if ((var1 = this.function) != null && (var2 = this.array) != null) {
            int var3 = this.threshold;
            int var4 = this.origin;
            int var5 = this.fence;
            ArrayPrefixHelpers.DoubleCumulateTask var8 = this;

            int var6;
            int var7;
            while((var6 = var8.lo) >= 0 && (var7 = var8.hi) <= var2.length) {
               int var12;
               int var23;
               if (var7 - var6 <= var3) {
                  int var19;
                  int var20;
                  do {
                     if (((var20 = var8.getPendingCount()) & 4) != 0) {
                        return;
                     }

                     var19 = (var20 & 1) != 0 ? 4 : (var6 > var4 ? 2 : 6);
                  } while(!var8.compareAndSetPendingCount(var20, var20 | var19));

                  int var13;
                  double var21;
                  if (var19 != 2) {
                     if (var6 == var4) {
                        var21 = var2[var4];
                        var12 = var4 + 1;
                     } else {
                        var21 = var8.in;
                        var12 = var6;
                     }

                     for(var13 = var12; var13 < var7; ++var13) {
                        var2[var13] = var21 = var1.applyAsDouble(var21, var2[var13]);
                     }
                  } else if (var7 < var5) {
                     var21 = var2[var6];

                     for(var12 = var6 + 1; var12 < var7; ++var12) {
                        var21 = var1.applyAsDouble(var21, var2[var12]);
                     }
                  } else {
                     var21 = var8.in;
                  }

                  var8.out = var21;

                  while(true) {
                     ArrayPrefixHelpers.DoubleCumulateTask var24;
                     while((var24 = (ArrayPrefixHelpers.DoubleCumulateTask)var8.getCompleter()) != null) {
                        var13 = var24.getPendingCount();
                        if ((var13 & var19 & 4) != 0) {
                           var8 = var24;
                        } else if ((var13 & var19 & 2) != 0) {
                           ArrayPrefixHelpers.DoubleCumulateTask var15;
                           ArrayPrefixHelpers.DoubleCumulateTask var25;
                           if ((var15 = var24.left) != null && (var25 = var24.right) != null) {
                              double var17 = var15.out;
                              var24.out = var25.hi == var5 ? var17 : var1.applyAsDouble(var17, var25.out);
                           }

                           int var26 = (var13 & 1) == 0 && var24.lo == var4 ? 1 : 0;
                           if ((var23 = var13 | var19 | var26) == var13 || var24.compareAndSetPendingCount(var13, var23)) {
                              var19 = 2;
                              var8 = var24;
                              if (var26 != 0) {
                                 var24.fork();
                              }
                           }
                        } else if (var24.compareAndSetPendingCount(var13, var13 | var19)) {
                           return;
                        }
                     }

                     if ((var19 & 4) != 0) {
                        var8.quietlyComplete();
                     }

                     return;
                  }
               }

               ArrayPrefixHelpers.DoubleCumulateTask var9 = var8.left;
               ArrayPrefixHelpers.DoubleCumulateTask var10 = var8.right;
               ArrayPrefixHelpers.DoubleCumulateTask var11;
               if (var9 == null) {
                  var12 = var6 + var7 >>> 1;
                  var11 = var8.right = new ArrayPrefixHelpers.DoubleCumulateTask(var8, var1, var2, var4, var5, var3, var12, var7);
                  var8 = var8.left = new ArrayPrefixHelpers.DoubleCumulateTask(var8, var1, var2, var4, var5, var3, var6, var12);
               } else {
                  double var22 = var8.in;
                  var9.in = var22;
                  var8 = null;
                  var11 = null;
                  if (var10 != null) {
                     double var14 = var9.out;
                     var10.in = var6 == var4 ? var14 : var1.applyAsDouble(var22, var14);

                     int var16;
                     while(((var16 = var10.getPendingCount()) & 1) == 0) {
                        if (var10.compareAndSetPendingCount(var16, var16 | 1)) {
                           var8 = var10;
                           break;
                        }
                     }
                  }

                  while(((var23 = var9.getPendingCount()) & 1) == 0) {
                     if (var9.compareAndSetPendingCount(var23, var23 | 1)) {
                        if (var8 != null) {
                           var11 = var8;
                        }

                        var8 = var9;
                        break;
                     }
                  }

                  if (var8 == null) {
                     break;
                  }
               }

               if (var11 != null) {
                  var11.fork();
               }
            }

         } else {
            throw new NullPointerException();
         }
      }
   }

   static final class LongCumulateTask extends CountedCompleter<Void> {
      final long[] array;
      final LongBinaryOperator function;
      ArrayPrefixHelpers.LongCumulateTask left;
      ArrayPrefixHelpers.LongCumulateTask right;
      long in;
      long out;
      final int lo;
      final int hi;
      final int origin;
      final int fence;
      final int threshold;

      public LongCumulateTask(ArrayPrefixHelpers.LongCumulateTask var1, LongBinaryOperator var2, long[] var3, int var4, int var5) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.lo = this.origin = var4;
         this.hi = this.fence = var5;
         int var6;
         this.threshold = (var6 = (var5 - var4) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : var6;
      }

      LongCumulateTask(ArrayPrefixHelpers.LongCumulateTask var1, LongBinaryOperator var2, long[] var3, int var4, int var5, int var6, int var7, int var8) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.origin = var4;
         this.fence = var5;
         this.threshold = var6;
         this.lo = var7;
         this.hi = var8;
      }

      public final void compute() {
         LongBinaryOperator var1;
         long[] var2;
         if ((var1 = this.function) != null && (var2 = this.array) != null) {
            int var3 = this.threshold;
            int var4 = this.origin;
            int var5 = this.fence;
            ArrayPrefixHelpers.LongCumulateTask var8 = this;

            int var6;
            int var7;
            while((var6 = var8.lo) >= 0 && (var7 = var8.hi) <= var2.length) {
               int var12;
               int var23;
               if (var7 - var6 <= var3) {
                  int var19;
                  int var20;
                  do {
                     if (((var20 = var8.getPendingCount()) & 4) != 0) {
                        return;
                     }

                     var19 = (var20 & 1) != 0 ? 4 : (var6 > var4 ? 2 : 6);
                  } while(!var8.compareAndSetPendingCount(var20, var20 | var19));

                  int var13;
                  long var21;
                  if (var19 != 2) {
                     if (var6 == var4) {
                        var21 = var2[var4];
                        var12 = var4 + 1;
                     } else {
                        var21 = var8.in;
                        var12 = var6;
                     }

                     for(var13 = var12; var13 < var7; ++var13) {
                        var2[var13] = var21 = var1.applyAsLong(var21, var2[var13]);
                     }
                  } else if (var7 < var5) {
                     var21 = var2[var6];

                     for(var12 = var6 + 1; var12 < var7; ++var12) {
                        var21 = var1.applyAsLong(var21, var2[var12]);
                     }
                  } else {
                     var21 = var8.in;
                  }

                  var8.out = var21;

                  while(true) {
                     ArrayPrefixHelpers.LongCumulateTask var24;
                     while((var24 = (ArrayPrefixHelpers.LongCumulateTask)var8.getCompleter()) != null) {
                        var13 = var24.getPendingCount();
                        if ((var13 & var19 & 4) != 0) {
                           var8 = var24;
                        } else if ((var13 & var19 & 2) != 0) {
                           ArrayPrefixHelpers.LongCumulateTask var15;
                           ArrayPrefixHelpers.LongCumulateTask var25;
                           if ((var15 = var24.left) != null && (var25 = var24.right) != null) {
                              long var17 = var15.out;
                              var24.out = var25.hi == var5 ? var17 : var1.applyAsLong(var17, var25.out);
                           }

                           int var26 = (var13 & 1) == 0 && var24.lo == var4 ? 1 : 0;
                           if ((var23 = var13 | var19 | var26) == var13 || var24.compareAndSetPendingCount(var13, var23)) {
                              var19 = 2;
                              var8 = var24;
                              if (var26 != 0) {
                                 var24.fork();
                              }
                           }
                        } else if (var24.compareAndSetPendingCount(var13, var13 | var19)) {
                           return;
                        }
                     }

                     if ((var19 & 4) != 0) {
                        var8.quietlyComplete();
                     }

                     return;
                  }
               }

               ArrayPrefixHelpers.LongCumulateTask var9 = var8.left;
               ArrayPrefixHelpers.LongCumulateTask var10 = var8.right;
               ArrayPrefixHelpers.LongCumulateTask var11;
               if (var9 == null) {
                  var12 = var6 + var7 >>> 1;
                  var11 = var8.right = new ArrayPrefixHelpers.LongCumulateTask(var8, var1, var2, var4, var5, var3, var12, var7);
                  var8 = var8.left = new ArrayPrefixHelpers.LongCumulateTask(var8, var1, var2, var4, var5, var3, var6, var12);
               } else {
                  long var22 = var8.in;
                  var9.in = var22;
                  var8 = null;
                  var11 = null;
                  if (var10 != null) {
                     long var14 = var9.out;
                     var10.in = var6 == var4 ? var14 : var1.applyAsLong(var22, var14);

                     int var16;
                     while(((var16 = var10.getPendingCount()) & 1) == 0) {
                        if (var10.compareAndSetPendingCount(var16, var16 | 1)) {
                           var8 = var10;
                           break;
                        }
                     }
                  }

                  while(((var23 = var9.getPendingCount()) & 1) == 0) {
                     if (var9.compareAndSetPendingCount(var23, var23 | 1)) {
                        if (var8 != null) {
                           var11 = var8;
                        }

                        var8 = var9;
                        break;
                     }
                  }

                  if (var8 == null) {
                     break;
                  }
               }

               if (var11 != null) {
                  var11.fork();
               }
            }

         } else {
            throw new NullPointerException();
         }
      }
   }

   static final class CumulateTask<T> extends CountedCompleter<Void> {
      final T[] array;
      final BinaryOperator<T> function;
      ArrayPrefixHelpers.CumulateTask<T> left;
      ArrayPrefixHelpers.CumulateTask<T> right;
      T in;
      T out;
      final int lo;
      final int hi;
      final int origin;
      final int fence;
      final int threshold;

      public CumulateTask(ArrayPrefixHelpers.CumulateTask<T> var1, BinaryOperator<T> var2, T[] var3, int var4, int var5) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.lo = this.origin = var4;
         this.hi = this.fence = var5;
         int var6;
         this.threshold = (var6 = (var5 - var4) / (ForkJoinPool.getCommonPoolParallelism() << 3)) <= 16 ? 16 : var6;
      }

      CumulateTask(ArrayPrefixHelpers.CumulateTask<T> var1, BinaryOperator<T> var2, T[] var3, int var4, int var5, int var6, int var7, int var8) {
         super(var1);
         this.function = var2;
         this.array = var3;
         this.origin = var4;
         this.fence = var5;
         this.threshold = var6;
         this.lo = var7;
         this.hi = var8;
      }

      public final void compute() {
         BinaryOperator var1;
         Object[] var2;
         if ((var1 = this.function) != null && (var2 = this.array) != null) {
            int var3 = this.threshold;
            int var4 = this.origin;
            int var5 = this.fence;
            ArrayPrefixHelpers.CumulateTask var8 = this;

            int var6;
            int var7;
            while((var6 = var8.lo) >= 0 && (var7 = var8.hi) <= var2.length) {
               ArrayPrefixHelpers.CumulateTask var11;
               int var12;
               int var22;
               if (var7 - var6 <= var3) {
                  int var17;
                  int var18;
                  do {
                     if (((var18 = var8.getPendingCount()) & 4) != 0) {
                        return;
                     }

                     var17 = (var18 & 1) != 0 ? 4 : (var6 > var4 ? 2 : 6);
                  } while(!var8.compareAndSetPendingCount(var18, var18 | var17));

                  Object var19;
                  int var21;
                  if (var17 != 2) {
                     if (var6 == var4) {
                        var19 = var2[var4];
                        var21 = var4 + 1;
                     } else {
                        var19 = var8.in;
                        var21 = var6;
                     }

                     for(var12 = var21; var12 < var7; ++var12) {
                        var2[var12] = var19 = var1.apply(var19, var2[var12]);
                     }
                  } else if (var7 < var5) {
                     var19 = var2[var6];

                     for(var21 = var6 + 1; var21 < var7; ++var21) {
                        var19 = var1.apply(var19, var2[var21]);
                     }
                  } else {
                     var19 = var8.in;
                  }

                  var8.out = var19;

                  while(true) {
                     while((var11 = (ArrayPrefixHelpers.CumulateTask)var8.getCompleter()) != null) {
                        var12 = var11.getPendingCount();
                        if ((var12 & var17 & 4) != 0) {
                           var8 = var11;
                        } else if ((var12 & var17 & 2) != 0) {
                           ArrayPrefixHelpers.CumulateTask var15;
                           ArrayPrefixHelpers.CumulateTask var23;
                           if ((var23 = var11.left) != null && (var15 = var11.right) != null) {
                              Object var16 = var23.out;
                              var11.out = var15.hi == var5 ? var16 : var1.apply(var16, var15.out);
                           }

                           int var24 = (var12 & 1) == 0 && var11.lo == var4 ? 1 : 0;
                           if ((var22 = var12 | var17 | var24) == var12 || var11.compareAndSetPendingCount(var12, var22)) {
                              var17 = 2;
                              var8 = var11;
                              if (var24 != 0) {
                                 var11.fork();
                              }
                           }
                        } else if (var11.compareAndSetPendingCount(var12, var12 | var17)) {
                           return;
                        }
                     }

                     if ((var17 & 4) != 0) {
                        var8.quietlyComplete();
                     }

                     return;
                  }
               }

               ArrayPrefixHelpers.CumulateTask var9 = var8.left;
               ArrayPrefixHelpers.CumulateTask var10 = var8.right;
               if (var9 == null) {
                  var12 = var6 + var7 >>> 1;
                  var11 = var8.right = new ArrayPrefixHelpers.CumulateTask(var8, var1, var2, var4, var5, var3, var12, var7);
                  var8 = var8.left = new ArrayPrefixHelpers.CumulateTask(var8, var1, var2, var4, var5, var3, var6, var12);
               } else {
                  Object var20 = var8.in;
                  var9.in = var20;
                  var8 = null;
                  var11 = null;
                  if (var10 != null) {
                     Object var13 = var9.out;
                     var10.in = var6 == var4 ? var13 : var1.apply(var20, var13);

                     int var14;
                     while(((var14 = var10.getPendingCount()) & 1) == 0) {
                        if (var10.compareAndSetPendingCount(var14, var14 | 1)) {
                           var8 = var10;
                           break;
                        }
                     }
                  }

                  while(((var22 = var9.getPendingCount()) & 1) == 0) {
                     if (var9.compareAndSetPendingCount(var22, var22 | 1)) {
                        if (var8 != null) {
                           var11 = var8;
                        }

                        var8 = var9;
                        break;
                     }
                  }

                  if (var8 == null) {
                     break;
                  }
               }

               if (var11 != null) {
                  var11.fork();
               }
            }

         } else {
            throw new NullPointerException();
         }
      }
   }
}
