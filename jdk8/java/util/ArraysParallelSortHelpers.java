package java.util;

import java.util.concurrent.CountedCompleter;

class ArraysParallelSortHelpers {
   static final class FJDouble {
      static final class Merger extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final double[] a;
         final double[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;

         Merger(CountedCompleter<?> var1, double[] var2, double[] var3, int var4, int var5, int var6, int var7, int var8, int var9) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
         }

         public final void compute() {
            double[] var1 = this.a;
            double[] var2 = this.w;
            int var3 = this.lbase;
            int var4 = this.lsize;
            int var5 = this.rbase;
            int var6 = this.rsize;
            int var7 = this.wbase;
            int var8 = this.gran;
            if (var1 != null && var2 != null && var3 >= 0 && var5 >= 0 && var7 >= 0) {
               int var9;
               int var10;
               double var11;
               while(true) {
                  int var13;
                  int var14;
                  if (var4 >= var6) {
                     if (var4 <= var8) {
                        break;
                     }

                     var10 = var6;
                     var11 = var1[(var9 = var4 >>> 1) + var3];
                     var13 = 0;

                     while(var13 < var10) {
                        var14 = var13 + var10 >>> 1;
                        if (var11 <= var1[var14 + var5]) {
                           var10 = var14;
                        } else {
                           var13 = var14 + 1;
                        }
                     }
                  } else {
                     if (var6 <= var8) {
                        break;
                     }

                     var9 = var4;
                     var11 = var1[(var10 = var6 >>> 1) + var5];
                     var13 = 0;

                     while(var13 < var9) {
                        var14 = var13 + var9 >>> 1;
                        if (var11 <= var1[var14 + var3]) {
                           var9 = var14;
                        } else {
                           var13 = var14 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJDouble.Merger var17 = new ArraysParallelSortHelpers.FJDouble.Merger(this, var1, var2, var3 + var9, var4 - var9, var5 + var10, var6 - var10, var7 + var9 + var10, var8);
                  var6 = var10;
                  var4 = var9;
                  this.addToPendingCount(1);
                  var17.fork();
               }

               var9 = var3 + var4;

               for(var10 = var5 + var6; var3 < var9 && var5 < var10; var2[var7++] = var11) {
                  double var15;
                  double var18;
                  if ((var18 = var1[var3]) <= (var15 = var1[var5])) {
                     ++var3;
                     var11 = var18;
                  } else {
                     ++var5;
                     var11 = var15;
                  }
               }

               if (var5 < var10) {
                  System.arraycopy(var1, var5, var2, var7, var10 - var5);
               } else if (var3 < var9) {
                  System.arraycopy(var1, var3, var2, var7, var9 - var3);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final double[] a;
         final double[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;

         Sorter(CountedCompleter<?> var1, double[] var2, double[] var3, int var4, int var5, int var6, int var7) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
         }

         public final void compute() {
            Object var1 = this;
            double[] var2 = this.a;
            double[] var3 = this.w;
            int var4 = this.base;
            int var5 = this.size;
            int var6 = this.wbase;

            int var9;
            for(int var7 = this.gran; var5 > var7; var5 = var9) {
               int var8 = var5 >>> 1;
               var9 = var8 >>> 1;
               int var10 = var8 + var9;
               ArraysParallelSortHelpers.Relay var11 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger((CountedCompleter)var1, var3, var2, var6, var8, var6 + var8, var5 - var8, var4, var7));
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger(var11, var2, var3, var4 + var8, var9, var4 + var10, var5 - var10, var6 + var8, var7));
               (new ArraysParallelSortHelpers.FJDouble.Sorter(var12, var2, var3, var4 + var10, var5 - var10, var6 + var10, var7)).fork();
               (new ArraysParallelSortHelpers.FJDouble.Sorter(var12, var2, var3, var4 + var8, var9, var6 + var8, var7)).fork();
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJDouble.Merger(var11, var2, var3, var4, var9, var4 + var9, var8 - var9, var6, var7));
               (new ArraysParallelSortHelpers.FJDouble.Sorter(var13, var2, var3, var4 + var9, var8 - var9, var6 + var9, var7)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var13);
            }

            DualPivotQuicksort.sort(var2, var4, var4 + var5 - 1, var3, var6, var5);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class FJFloat {
      static final class Merger extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final float[] a;
         final float[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;

         Merger(CountedCompleter<?> var1, float[] var2, float[] var3, int var4, int var5, int var6, int var7, int var8, int var9) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
         }

         public final void compute() {
            float[] var1 = this.a;
            float[] var2 = this.w;
            int var3 = this.lbase;
            int var4 = this.lsize;
            int var5 = this.rbase;
            int var6 = this.rsize;
            int var7 = this.wbase;
            int var8 = this.gran;
            if (var1 != null && var2 != null && var3 >= 0 && var5 >= 0 && var7 >= 0) {
               int var9;
               int var10;
               float var11;
               while(true) {
                  int var12;
                  int var13;
                  if (var4 >= var6) {
                     if (var4 <= var8) {
                        break;
                     }

                     var10 = var6;
                     var11 = var1[(var9 = var4 >>> 1) + var3];
                     var12 = 0;

                     while(var12 < var10) {
                        var13 = var12 + var10 >>> 1;
                        if (var11 <= var1[var13 + var5]) {
                           var10 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  } else {
                     if (var6 <= var8) {
                        break;
                     }

                     var9 = var4;
                     var11 = var1[(var10 = var6 >>> 1) + var5];
                     var12 = 0;

                     while(var12 < var9) {
                        var13 = var12 + var9 >>> 1;
                        if (var11 <= var1[var13 + var3]) {
                           var9 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJFloat.Merger var14 = new ArraysParallelSortHelpers.FJFloat.Merger(this, var1, var2, var3 + var9, var4 - var9, var5 + var10, var6 - var10, var7 + var9 + var10, var8);
                  var6 = var10;
                  var4 = var9;
                  this.addToPendingCount(1);
                  var14.fork();
               }

               var9 = var3 + var4;

               for(var10 = var5 + var6; var3 < var9 && var5 < var10; var2[var7++] = var11) {
                  float var15;
                  float var16;
                  if ((var15 = var1[var3]) <= (var16 = var1[var5])) {
                     ++var3;
                     var11 = var15;
                  } else {
                     ++var5;
                     var11 = var16;
                  }
               }

               if (var5 < var10) {
                  System.arraycopy(var1, var5, var2, var7, var10 - var5);
               } else if (var3 < var9) {
                  System.arraycopy(var1, var3, var2, var7, var9 - var3);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final float[] a;
         final float[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;

         Sorter(CountedCompleter<?> var1, float[] var2, float[] var3, int var4, int var5, int var6, int var7) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
         }

         public final void compute() {
            Object var1 = this;
            float[] var2 = this.a;
            float[] var3 = this.w;
            int var4 = this.base;
            int var5 = this.size;
            int var6 = this.wbase;

            int var9;
            for(int var7 = this.gran; var5 > var7; var5 = var9) {
               int var8 = var5 >>> 1;
               var9 = var8 >>> 1;
               int var10 = var8 + var9;
               ArraysParallelSortHelpers.Relay var11 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger((CountedCompleter)var1, var3, var2, var6, var8, var6 + var8, var5 - var8, var4, var7));
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger(var11, var2, var3, var4 + var8, var9, var4 + var10, var5 - var10, var6 + var8, var7));
               (new ArraysParallelSortHelpers.FJFloat.Sorter(var12, var2, var3, var4 + var10, var5 - var10, var6 + var10, var7)).fork();
               (new ArraysParallelSortHelpers.FJFloat.Sorter(var12, var2, var3, var4 + var8, var9, var6 + var8, var7)).fork();
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJFloat.Merger(var11, var2, var3, var4, var9, var4 + var9, var8 - var9, var6, var7));
               (new ArraysParallelSortHelpers.FJFloat.Sorter(var13, var2, var3, var4 + var9, var8 - var9, var6 + var9, var7)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var13);
            }

            DualPivotQuicksort.sort(var2, var4, var4 + var5 - 1, var3, var6, var5);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class FJLong {
      static final class Merger extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final long[] a;
         final long[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;

         Merger(CountedCompleter<?> var1, long[] var2, long[] var3, int var4, int var5, int var6, int var7, int var8, int var9) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
         }

         public final void compute() {
            long[] var1 = this.a;
            long[] var2 = this.w;
            int var3 = this.lbase;
            int var4 = this.lsize;
            int var5 = this.rbase;
            int var6 = this.rsize;
            int var7 = this.wbase;
            int var8 = this.gran;
            if (var1 != null && var2 != null && var3 >= 0 && var5 >= 0 && var7 >= 0) {
               int var9;
               int var10;
               long var11;
               while(true) {
                  int var13;
                  int var14;
                  if (var4 >= var6) {
                     if (var4 <= var8) {
                        break;
                     }

                     var10 = var6;
                     var11 = var1[(var9 = var4 >>> 1) + var3];
                     var13 = 0;

                     while(var13 < var10) {
                        var14 = var13 + var10 >>> 1;
                        if (var11 <= var1[var14 + var5]) {
                           var10 = var14;
                        } else {
                           var13 = var14 + 1;
                        }
                     }
                  } else {
                     if (var6 <= var8) {
                        break;
                     }

                     var9 = var4;
                     var11 = var1[(var10 = var6 >>> 1) + var5];
                     var13 = 0;

                     while(var13 < var9) {
                        var14 = var13 + var9 >>> 1;
                        if (var11 <= var1[var14 + var3]) {
                           var9 = var14;
                        } else {
                           var13 = var14 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJLong.Merger var17 = new ArraysParallelSortHelpers.FJLong.Merger(this, var1, var2, var3 + var9, var4 - var9, var5 + var10, var6 - var10, var7 + var9 + var10, var8);
                  var6 = var10;
                  var4 = var9;
                  this.addToPendingCount(1);
                  var17.fork();
               }

               var9 = var3 + var4;

               for(var10 = var5 + var6; var3 < var9 && var5 < var10; var2[var7++] = var11) {
                  long var15;
                  long var18;
                  if ((var18 = var1[var3]) <= (var15 = var1[var5])) {
                     ++var3;
                     var11 = var18;
                  } else {
                     ++var5;
                     var11 = var15;
                  }
               }

               if (var5 < var10) {
                  System.arraycopy(var1, var5, var2, var7, var10 - var5);
               } else if (var3 < var9) {
                  System.arraycopy(var1, var3, var2, var7, var9 - var3);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final long[] a;
         final long[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;

         Sorter(CountedCompleter<?> var1, long[] var2, long[] var3, int var4, int var5, int var6, int var7) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
         }

         public final void compute() {
            Object var1 = this;
            long[] var2 = this.a;
            long[] var3 = this.w;
            int var4 = this.base;
            int var5 = this.size;
            int var6 = this.wbase;

            int var9;
            for(int var7 = this.gran; var5 > var7; var5 = var9) {
               int var8 = var5 >>> 1;
               var9 = var8 >>> 1;
               int var10 = var8 + var9;
               ArraysParallelSortHelpers.Relay var11 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger((CountedCompleter)var1, var3, var2, var6, var8, var6 + var8, var5 - var8, var4, var7));
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger(var11, var2, var3, var4 + var8, var9, var4 + var10, var5 - var10, var6 + var8, var7));
               (new ArraysParallelSortHelpers.FJLong.Sorter(var12, var2, var3, var4 + var10, var5 - var10, var6 + var10, var7)).fork();
               (new ArraysParallelSortHelpers.FJLong.Sorter(var12, var2, var3, var4 + var8, var9, var6 + var8, var7)).fork();
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJLong.Merger(var11, var2, var3, var4, var9, var4 + var9, var8 - var9, var6, var7));
               (new ArraysParallelSortHelpers.FJLong.Sorter(var13, var2, var3, var4 + var9, var8 - var9, var6 + var9, var7)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var13);
            }

            DualPivotQuicksort.sort(var2, var4, var4 + var5 - 1, var3, var6, var5);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class FJInt {
      static final class Merger extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final int[] a;
         final int[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;

         Merger(CountedCompleter<?> var1, int[] var2, int[] var3, int var4, int var5, int var6, int var7, int var8, int var9) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
         }

         public final void compute() {
            int[] var1 = this.a;
            int[] var2 = this.w;
            int var3 = this.lbase;
            int var4 = this.lsize;
            int var5 = this.rbase;
            int var6 = this.rsize;
            int var7 = this.wbase;
            int var8 = this.gran;
            if (var1 != null && var2 != null && var3 >= 0 && var5 >= 0 && var7 >= 0) {
               int var9;
               int var10;
               int var11;
               int var12;
               int var13;
               while(true) {
                  if (var4 >= var6) {
                     if (var4 <= var8) {
                        break;
                     }

                     var10 = var6;
                     var11 = var1[(var9 = var4 >>> 1) + var3];
                     var12 = 0;

                     while(var12 < var10) {
                        var13 = var12 + var10 >>> 1;
                        if (var11 <= var1[var13 + var5]) {
                           var10 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  } else {
                     if (var6 <= var8) {
                        break;
                     }

                     var9 = var4;
                     var11 = var1[(var10 = var6 >>> 1) + var5];
                     var12 = 0;

                     while(var12 < var9) {
                        var13 = var12 + var9 >>> 1;
                        if (var11 <= var1[var13 + var3]) {
                           var9 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJInt.Merger var14 = new ArraysParallelSortHelpers.FJInt.Merger(this, var1, var2, var3 + var9, var4 - var9, var5 + var10, var6 - var10, var7 + var9 + var10, var8);
                  var6 = var10;
                  var4 = var9;
                  this.addToPendingCount(1);
                  var14.fork();
               }

               var9 = var3 + var4;

               for(var10 = var5 + var6; var3 < var9 && var5 < var10; var2[var7++] = var11) {
                  if ((var12 = var1[var3]) <= (var13 = var1[var5])) {
                     ++var3;
                     var11 = var12;
                  } else {
                     ++var5;
                     var11 = var13;
                  }
               }

               if (var5 < var10) {
                  System.arraycopy(var1, var5, var2, var7, var10 - var5);
               } else if (var3 < var9) {
                  System.arraycopy(var1, var3, var2, var7, var9 - var3);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final int[] a;
         final int[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;

         Sorter(CountedCompleter<?> var1, int[] var2, int[] var3, int var4, int var5, int var6, int var7) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
         }

         public final void compute() {
            Object var1 = this;
            int[] var2 = this.a;
            int[] var3 = this.w;
            int var4 = this.base;
            int var5 = this.size;
            int var6 = this.wbase;

            int var9;
            for(int var7 = this.gran; var5 > var7; var5 = var9) {
               int var8 = var5 >>> 1;
               var9 = var8 >>> 1;
               int var10 = var8 + var9;
               ArraysParallelSortHelpers.Relay var11 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger((CountedCompleter)var1, var3, var2, var6, var8, var6 + var8, var5 - var8, var4, var7));
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger(var11, var2, var3, var4 + var8, var9, var4 + var10, var5 - var10, var6 + var8, var7));
               (new ArraysParallelSortHelpers.FJInt.Sorter(var12, var2, var3, var4 + var10, var5 - var10, var6 + var10, var7)).fork();
               (new ArraysParallelSortHelpers.FJInt.Sorter(var12, var2, var3, var4 + var8, var9, var6 + var8, var7)).fork();
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJInt.Merger(var11, var2, var3, var4, var9, var4 + var9, var8 - var9, var6, var7));
               (new ArraysParallelSortHelpers.FJInt.Sorter(var13, var2, var3, var4 + var9, var8 - var9, var6 + var9, var7)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var13);
            }

            DualPivotQuicksort.sort(var2, var4, var4 + var5 - 1, var3, var6, var5);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class FJShort {
      static final class Merger extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final short[] a;
         final short[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;

         Merger(CountedCompleter<?> var1, short[] var2, short[] var3, int var4, int var5, int var6, int var7, int var8, int var9) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
         }

         public final void compute() {
            short[] var1 = this.a;
            short[] var2 = this.w;
            int var3 = this.lbase;
            int var4 = this.lsize;
            int var5 = this.rbase;
            int var6 = this.rsize;
            int var7 = this.wbase;
            int var8 = this.gran;
            if (var1 != null && var2 != null && var3 >= 0 && var5 >= 0 && var7 >= 0) {
               int var9;
               int var10;
               short var11;
               while(true) {
                  int var12;
                  int var13;
                  if (var4 >= var6) {
                     if (var4 <= var8) {
                        break;
                     }

                     var10 = var6;
                     var11 = var1[(var9 = var4 >>> 1) + var3];
                     var12 = 0;

                     while(var12 < var10) {
                        var13 = var12 + var10 >>> 1;
                        if (var11 <= var1[var13 + var5]) {
                           var10 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  } else {
                     if (var6 <= var8) {
                        break;
                     }

                     var9 = var4;
                     var11 = var1[(var10 = var6 >>> 1) + var5];
                     var12 = 0;

                     while(var12 < var9) {
                        var13 = var12 + var9 >>> 1;
                        if (var11 <= var1[var13 + var3]) {
                           var9 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJShort.Merger var14 = new ArraysParallelSortHelpers.FJShort.Merger(this, var1, var2, var3 + var9, var4 - var9, var5 + var10, var6 - var10, var7 + var9 + var10, var8);
                  var6 = var10;
                  var4 = var9;
                  this.addToPendingCount(1);
                  var14.fork();
               }

               var9 = var3 + var4;

               for(var10 = var5 + var6; var3 < var9 && var5 < var10; var2[var7++] = var11) {
                  short var15;
                  short var16;
                  if ((var15 = var1[var3]) <= (var16 = var1[var5])) {
                     ++var3;
                     var11 = var15;
                  } else {
                     ++var5;
                     var11 = var16;
                  }
               }

               if (var5 < var10) {
                  System.arraycopy(var1, var5, var2, var7, var10 - var5);
               } else if (var3 < var9) {
                  System.arraycopy(var1, var3, var2, var7, var9 - var3);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final short[] a;
         final short[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;

         Sorter(CountedCompleter<?> var1, short[] var2, short[] var3, int var4, int var5, int var6, int var7) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
         }

         public final void compute() {
            Object var1 = this;
            short[] var2 = this.a;
            short[] var3 = this.w;
            int var4 = this.base;
            int var5 = this.size;
            int var6 = this.wbase;

            int var9;
            for(int var7 = this.gran; var5 > var7; var5 = var9) {
               int var8 = var5 >>> 1;
               var9 = var8 >>> 1;
               int var10 = var8 + var9;
               ArraysParallelSortHelpers.Relay var11 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger((CountedCompleter)var1, var3, var2, var6, var8, var6 + var8, var5 - var8, var4, var7));
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger(var11, var2, var3, var4 + var8, var9, var4 + var10, var5 - var10, var6 + var8, var7));
               (new ArraysParallelSortHelpers.FJShort.Sorter(var12, var2, var3, var4 + var10, var5 - var10, var6 + var10, var7)).fork();
               (new ArraysParallelSortHelpers.FJShort.Sorter(var12, var2, var3, var4 + var8, var9, var6 + var8, var7)).fork();
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJShort.Merger(var11, var2, var3, var4, var9, var4 + var9, var8 - var9, var6, var7));
               (new ArraysParallelSortHelpers.FJShort.Sorter(var13, var2, var3, var4 + var9, var8 - var9, var6 + var9, var7)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var13);
            }

            DualPivotQuicksort.sort(var2, var4, var4 + var5 - 1, var3, var6, var5);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class FJChar {
      static final class Merger extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final char[] a;
         final char[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;

         Merger(CountedCompleter<?> var1, char[] var2, char[] var3, int var4, int var5, int var6, int var7, int var8, int var9) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
         }

         public final void compute() {
            char[] var1 = this.a;
            char[] var2 = this.w;
            int var3 = this.lbase;
            int var4 = this.lsize;
            int var5 = this.rbase;
            int var6 = this.rsize;
            int var7 = this.wbase;
            int var8 = this.gran;
            if (var1 != null && var2 != null && var3 >= 0 && var5 >= 0 && var7 >= 0) {
               int var9;
               int var10;
               char var11;
               while(true) {
                  int var12;
                  int var13;
                  if (var4 >= var6) {
                     if (var4 <= var8) {
                        break;
                     }

                     var10 = var6;
                     var11 = var1[(var9 = var4 >>> 1) + var3];
                     var12 = 0;

                     while(var12 < var10) {
                        var13 = var12 + var10 >>> 1;
                        if (var11 <= var1[var13 + var5]) {
                           var10 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  } else {
                     if (var6 <= var8) {
                        break;
                     }

                     var9 = var4;
                     var11 = var1[(var10 = var6 >>> 1) + var5];
                     var12 = 0;

                     while(var12 < var9) {
                        var13 = var12 + var9 >>> 1;
                        if (var11 <= var1[var13 + var3]) {
                           var9 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJChar.Merger var14 = new ArraysParallelSortHelpers.FJChar.Merger(this, var1, var2, var3 + var9, var4 - var9, var5 + var10, var6 - var10, var7 + var9 + var10, var8);
                  var6 = var10;
                  var4 = var9;
                  this.addToPendingCount(1);
                  var14.fork();
               }

               var9 = var3 + var4;

               for(var10 = var5 + var6; var3 < var9 && var5 < var10; var2[var7++] = var11) {
                  char var15;
                  char var16;
                  if ((var15 = var1[var3]) <= (var16 = var1[var5])) {
                     ++var3;
                     var11 = var15;
                  } else {
                     ++var5;
                     var11 = var16;
                  }
               }

               if (var5 < var10) {
                  System.arraycopy(var1, var5, var2, var7, var10 - var5);
               } else if (var3 < var9) {
                  System.arraycopy(var1, var3, var2, var7, var9 - var3);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final char[] a;
         final char[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;

         Sorter(CountedCompleter<?> var1, char[] var2, char[] var3, int var4, int var5, int var6, int var7) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
         }

         public final void compute() {
            Object var1 = this;
            char[] var2 = this.a;
            char[] var3 = this.w;
            int var4 = this.base;
            int var5 = this.size;
            int var6 = this.wbase;

            int var9;
            for(int var7 = this.gran; var5 > var7; var5 = var9) {
               int var8 = var5 >>> 1;
               var9 = var8 >>> 1;
               int var10 = var8 + var9;
               ArraysParallelSortHelpers.Relay var11 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger((CountedCompleter)var1, var3, var2, var6, var8, var6 + var8, var5 - var8, var4, var7));
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger(var11, var2, var3, var4 + var8, var9, var4 + var10, var5 - var10, var6 + var8, var7));
               (new ArraysParallelSortHelpers.FJChar.Sorter(var12, var2, var3, var4 + var10, var5 - var10, var6 + var10, var7)).fork();
               (new ArraysParallelSortHelpers.FJChar.Sorter(var12, var2, var3, var4 + var8, var9, var6 + var8, var7)).fork();
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJChar.Merger(var11, var2, var3, var4, var9, var4 + var9, var8 - var9, var6, var7));
               (new ArraysParallelSortHelpers.FJChar.Sorter(var13, var2, var3, var4 + var9, var8 - var9, var6 + var9, var7)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var13);
            }

            DualPivotQuicksort.sort(var2, var4, var4 + var5 - 1, var3, var6, var5);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class FJByte {
      static final class Merger extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final byte[] a;
         final byte[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;

         Merger(CountedCompleter<?> var1, byte[] var2, byte[] var3, int var4, int var5, int var6, int var7, int var8, int var9) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
         }

         public final void compute() {
            byte[] var1 = this.a;
            byte[] var2 = this.w;
            int var3 = this.lbase;
            int var4 = this.lsize;
            int var5 = this.rbase;
            int var6 = this.rsize;
            int var7 = this.wbase;
            int var8 = this.gran;
            if (var1 != null && var2 != null && var3 >= 0 && var5 >= 0 && var7 >= 0) {
               int var9;
               int var10;
               byte var11;
               while(true) {
                  int var12;
                  int var13;
                  if (var4 >= var6) {
                     if (var4 <= var8) {
                        break;
                     }

                     var10 = var6;
                     var11 = var1[(var9 = var4 >>> 1) + var3];
                     var12 = 0;

                     while(var12 < var10) {
                        var13 = var12 + var10 >>> 1;
                        if (var11 <= var1[var13 + var5]) {
                           var10 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  } else {
                     if (var6 <= var8) {
                        break;
                     }

                     var9 = var4;
                     var11 = var1[(var10 = var6 >>> 1) + var5];
                     var12 = 0;

                     while(var12 < var9) {
                        var13 = var12 + var9 >>> 1;
                        if (var11 <= var1[var13 + var3]) {
                           var9 = var13;
                        } else {
                           var12 = var13 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJByte.Merger var14 = new ArraysParallelSortHelpers.FJByte.Merger(this, var1, var2, var3 + var9, var4 - var9, var5 + var10, var6 - var10, var7 + var9 + var10, var8);
                  var6 = var10;
                  var4 = var9;
                  this.addToPendingCount(1);
                  var14.fork();
               }

               var9 = var3 + var4;

               for(var10 = var5 + var6; var3 < var9 && var5 < var10; var2[var7++] = var11) {
                  byte var15;
                  byte var16;
                  if ((var15 = var1[var3]) <= (var16 = var1[var5])) {
                     ++var3;
                     var11 = var15;
                  } else {
                     ++var5;
                     var11 = var16;
                  }
               }

               if (var5 < var10) {
                  System.arraycopy(var1, var5, var2, var7, var10 - var5);
               } else if (var3 < var9) {
                  System.arraycopy(var1, var3, var2, var7, var9 - var3);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final byte[] a;
         final byte[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;

         Sorter(CountedCompleter<?> var1, byte[] var2, byte[] var3, int var4, int var5, int var6, int var7) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
         }

         public final void compute() {
            Object var1 = this;
            byte[] var2 = this.a;
            byte[] var3 = this.w;
            int var4 = this.base;
            int var5 = this.size;
            int var6 = this.wbase;

            int var9;
            for(int var7 = this.gran; var5 > var7; var5 = var9) {
               int var8 = var5 >>> 1;
               var9 = var8 >>> 1;
               int var10 = var8 + var9;
               ArraysParallelSortHelpers.Relay var11 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger((CountedCompleter)var1, var3, var2, var6, var8, var6 + var8, var5 - var8, var4, var7));
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger(var11, var2, var3, var4 + var8, var9, var4 + var10, var5 - var10, var6 + var8, var7));
               (new ArraysParallelSortHelpers.FJByte.Sorter(var12, var2, var3, var4 + var10, var5 - var10, var6 + var10, var7)).fork();
               (new ArraysParallelSortHelpers.FJByte.Sorter(var12, var2, var3, var4 + var8, var9, var6 + var8, var7)).fork();
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJByte.Merger(var11, var2, var3, var4, var9, var4 + var9, var8 - var9, var6, var7));
               (new ArraysParallelSortHelpers.FJByte.Sorter(var13, var2, var3, var4 + var9, var8 - var9, var6 + var9, var7)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var13);
            }

            DualPivotQuicksort.sort(var2, var4, var4 + var5 - 1);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class FJObject {
      static final class Merger<T> extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final T[] a;
         final T[] w;
         final int lbase;
         final int lsize;
         final int rbase;
         final int rsize;
         final int wbase;
         final int gran;
         Comparator<? super T> comparator;

         Merger(CountedCompleter<?> var1, T[] var2, T[] var3, int var4, int var5, int var6, int var7, int var8, int var9, Comparator<? super T> var10) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.lbase = var4;
            this.lsize = var5;
            this.rbase = var6;
            this.rsize = var7;
            this.wbase = var8;
            this.gran = var9;
            this.comparator = var10;
         }

         public final void compute() {
            Comparator var1 = this.comparator;
            Object[] var2 = this.a;
            Object[] var3 = this.w;
            int var4 = this.lbase;
            int var5 = this.lsize;
            int var6 = this.rbase;
            int var7 = this.rsize;
            int var8 = this.wbase;
            int var9 = this.gran;
            if (var2 != null && var3 != null && var4 >= 0 && var6 >= 0 && var8 >= 0 && var1 != null) {
               int var10;
               int var11;
               Object var12;
               while(true) {
                  int var13;
                  int var14;
                  if (var5 >= var7) {
                     if (var5 <= var9) {
                        break;
                     }

                     var11 = var7;
                     var12 = var2[(var10 = var5 >>> 1) + var4];
                     var13 = 0;

                     while(var13 < var11) {
                        var14 = var13 + var11 >>> 1;
                        if (var1.compare(var12, var2[var14 + var6]) <= 0) {
                           var11 = var14;
                        } else {
                           var13 = var14 + 1;
                        }
                     }
                  } else {
                     if (var7 <= var9) {
                        break;
                     }

                     var10 = var5;
                     var12 = var2[(var11 = var7 >>> 1) + var6];
                     var13 = 0;

                     while(var13 < var10) {
                        var14 = var13 + var10 >>> 1;
                        if (var1.compare(var12, var2[var14 + var4]) <= 0) {
                           var10 = var14;
                        } else {
                           var13 = var14 + 1;
                        }
                     }
                  }

                  ArraysParallelSortHelpers.FJObject.Merger var15 = new ArraysParallelSortHelpers.FJObject.Merger(this, var2, var3, var4 + var10, var5 - var10, var6 + var11, var7 - var11, var8 + var10 + var11, var9, var1);
                  var7 = var11;
                  var5 = var10;
                  this.addToPendingCount(1);
                  var15.fork();
               }

               var10 = var4 + var5;

               for(var11 = var6 + var7; var4 < var10 && var6 < var11; var3[var8++] = var12) {
                  Object var16;
                  Object var17;
                  if (var1.compare(var16 = var2[var4], var17 = var2[var6]) <= 0) {
                     ++var4;
                     var12 = var16;
                  } else {
                     ++var6;
                     var12 = var17;
                  }
               }

               if (var6 < var11) {
                  System.arraycopy(var2, var6, var3, var8, var11 - var6);
               } else if (var4 < var10) {
                  System.arraycopy(var2, var4, var3, var8, var10 - var4);
               }

               this.tryComplete();
            } else {
               throw new IllegalStateException();
            }
         }
      }

      static final class Sorter<T> extends CountedCompleter<Void> {
         static final long serialVersionUID = 2446542900576103244L;
         final T[] a;
         final T[] w;
         final int base;
         final int size;
         final int wbase;
         final int gran;
         Comparator<? super T> comparator;

         Sorter(CountedCompleter<?> var1, T[] var2, T[] var3, int var4, int var5, int var6, int var7, Comparator<? super T> var8) {
            super(var1);
            this.a = var2;
            this.w = var3;
            this.base = var4;
            this.size = var5;
            this.wbase = var6;
            this.gran = var7;
            this.comparator = var8;
         }

         public final void compute() {
            Object var1 = this;
            Comparator var2 = this.comparator;
            Object[] var3 = this.a;
            Object[] var4 = this.w;
            int var5 = this.base;
            int var6 = this.size;
            int var7 = this.wbase;

            int var10;
            for(int var8 = this.gran; var6 > var8; var6 = var10) {
               int var9 = var6 >>> 1;
               var10 = var9 >>> 1;
               int var11 = var9 + var10;
               ArraysParallelSortHelpers.Relay var12 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger((CountedCompleter)var1, var4, var3, var7, var9, var7 + var9, var6 - var9, var5, var8, var2));
               ArraysParallelSortHelpers.Relay var13 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger(var12, var3, var4, var5 + var9, var10, var5 + var11, var6 - var11, var7 + var9, var8, var2));
               (new ArraysParallelSortHelpers.FJObject.Sorter(var13, var3, var4, var5 + var11, var6 - var11, var7 + var11, var8, var2)).fork();
               (new ArraysParallelSortHelpers.FJObject.Sorter(var13, var3, var4, var5 + var9, var10, var7 + var9, var8, var2)).fork();
               ArraysParallelSortHelpers.Relay var14 = new ArraysParallelSortHelpers.Relay(new ArraysParallelSortHelpers.FJObject.Merger(var12, var3, var4, var5, var10, var5 + var10, var9 - var10, var7, var8, var2));
               (new ArraysParallelSortHelpers.FJObject.Sorter(var14, var3, var4, var5 + var10, var9 - var10, var7 + var10, var8, var2)).fork();
               var1 = new ArraysParallelSortHelpers.EmptyCompleter(var14);
            }

            TimSort.sort(var3, var5, var5 + var6, var2, var4, var7, var6);
            ((CountedCompleter)var1).tryComplete();
         }
      }
   }

   static final class Relay extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;
      final CountedCompleter<?> task;

      Relay(CountedCompleter<?> var1) {
         super((CountedCompleter)null, 1);
         this.task = var1;
      }

      public final void compute() {
      }

      public final void onCompletion(CountedCompleter<?> var1) {
         this.task.compute();
      }
   }

   static final class EmptyCompleter extends CountedCompleter<Void> {
      static final long serialVersionUID = 2446542900576103244L;

      EmptyCompleter(CountedCompleter<?> var1) {
         super(var1);
      }

      public final void compute() {
      }
   }
}
