package java.util;

import java.lang.reflect.Array;

class TimSort<T> {
   private static final int MIN_MERGE = 32;
   private final T[] a;
   private final Comparator<? super T> c;
   private static final int MIN_GALLOP = 7;
   private int minGallop = 7;
   private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
   private T[] tmp;
   private int tmpBase;
   private int tmpLen;
   private int stackSize = 0;
   private final int[] runBase;
   private final int[] runLen;

   private TimSort(T[] var1, Comparator<? super T> var2, T[] var3, int var4, int var5) {
      this.a = var1;
      this.c = var2;
      int var6 = var1.length;
      int var7 = var6 < 512 ? var6 >>> 1 : 256;
      if (var3 != null && var5 >= var7 && var4 + var7 <= var3.length) {
         this.tmp = var3;
         this.tmpBase = var4;
         this.tmpLen = var5;
      } else {
         Object[] var8 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), var7));
         this.tmp = var8;
         this.tmpBase = 0;
         this.tmpLen = var7;
      }

      int var9 = var6 < 120 ? 5 : (var6 < 1542 ? 10 : (var6 < 119151 ? 24 : 49));
      this.runBase = new int[var9];
      this.runLen = new int[var9];
   }

   static <T> void sort(T[] var0, int var1, int var2, Comparator<? super T> var3, T[] var4, int var5, int var6) {
      assert var3 != null && var0 != null && var1 >= 0 && var1 <= var2 && var2 <= var0.length;

      int var7 = var2 - var1;
      if (var7 >= 2) {
         if (var7 < 32) {
            int var12 = countRunAndMakeAscending(var0, var1, var2, var3);
            binarySort(var0, var1, var2, var1 + var12, var3);
         } else {
            TimSort var8 = new TimSort(var0, var3, var4, var5, var6);
            int var9 = minRunLength(var7);

            do {
               int var10 = countRunAndMakeAscending(var0, var1, var2, var3);
               if (var10 < var9) {
                  int var11 = var7 <= var9 ? var7 : var9;
                  binarySort(var0, var1, var1 + var11, var1 + var10, var3);
                  var10 = var11;
               }

               var8.pushRun(var1, var10);
               var8.mergeCollapse();
               var1 += var10;
               var7 -= var10;
            } while(var7 != 0);

            assert var1 == var2;

            var8.mergeForceCollapse();

            assert var8.stackSize == 1;

         }
      }
   }

   private static <T> void binarySort(T[] var0, int var1, int var2, int var3, Comparator<? super T> var4) {
      assert var1 <= var3 && var3 <= var2;

      if (var3 == var1) {
         ++var3;
      }

      while(var3 < var2) {
         Object var5 = var0[var3];
         int var6 = var1;
         int var7 = var3;

         assert var1 <= var3;

         int var8;
         while(var6 < var7) {
            var8 = var6 + var7 >>> 1;
            if (var4.compare(var5, var0[var8]) < 0) {
               var7 = var8;
            } else {
               var6 = var8 + 1;
            }
         }

         assert var6 == var7;

         var8 = var3 - var6;
         switch(var8) {
         case 2:
            var0[var6 + 2] = var0[var6 + 1];
         case 1:
            var0[var6 + 1] = var0[var6];
            break;
         default:
            System.arraycopy(var0, var6, var0, var6 + 1, var8);
         }

         var0[var6] = var5;
         ++var3;
      }

   }

   private static <T> int countRunAndMakeAscending(T[] var0, int var1, int var2, Comparator<? super T> var3) {
      assert var1 < var2;

      int var4 = var1 + 1;
      if (var4 == var2) {
         return 1;
      } else {
         if (var3.compare(var0[var4++], var0[var1]) >= 0) {
            while(var4 < var2 && var3.compare(var0[var4], var0[var4 - 1]) >= 0) {
               ++var4;
            }
         } else {
            while(var4 < var2 && var3.compare(var0[var4], var0[var4 - 1]) < 0) {
               ++var4;
            }

            reverseRange(var0, var1, var4);
         }

         return var4 - var1;
      }
   }

   private static void reverseRange(Object[] var0, int var1, int var2) {
      --var2;

      while(var1 < var2) {
         Object var3 = var0[var1];
         var0[var1++] = var0[var2];
         var0[var2--] = var3;
      }

   }

   private static int minRunLength(int var0) {
      assert var0 >= 0;

      int var1;
      for(var1 = 0; var0 >= 32; var0 >>= 1) {
         var1 |= var0 & 1;
      }

      return var0 + var1;
   }

   private void pushRun(int var1, int var2) {
      this.runBase[this.stackSize] = var1;
      this.runLen[this.stackSize] = var2;
      ++this.stackSize;
   }

   private void mergeCollapse() {
      while(true) {
         if (this.stackSize > 1) {
            int var1 = this.stackSize - 2;
            if (var1 > 0 && this.runLen[var1 - 1] <= this.runLen[var1] + this.runLen[var1 + 1]) {
               if (this.runLen[var1 - 1] < this.runLen[var1 + 1]) {
                  --var1;
               }

               this.mergeAt(var1);
               continue;
            }

            if (this.runLen[var1] <= this.runLen[var1 + 1]) {
               this.mergeAt(var1);
               continue;
            }
         }

         return;
      }
   }

   private void mergeForceCollapse() {
      int var1;
      for(; this.stackSize > 1; this.mergeAt(var1)) {
         var1 = this.stackSize - 2;
         if (var1 > 0 && this.runLen[var1 - 1] < this.runLen[var1 + 1]) {
            --var1;
         }
      }

   }

   private void mergeAt(int var1) {
      assert this.stackSize >= 2;

      assert var1 >= 0;

      assert var1 == this.stackSize - 2 || var1 == this.stackSize - 3;

      int var2 = this.runBase[var1];
      int var3 = this.runLen[var1];
      int var4 = this.runBase[var1 + 1];
      int var5 = this.runLen[var1 + 1];

      assert var3 > 0 && var5 > 0;

      assert var2 + var3 == var4;

      this.runLen[var1] = var3 + var5;
      if (var1 == this.stackSize - 3) {
         this.runBase[var1 + 1] = this.runBase[var1 + 2];
         this.runLen[var1 + 1] = this.runLen[var1 + 2];
      }

      --this.stackSize;
      int var6 = gallopRight(this.a[var4], this.a, var2, var3, 0, this.c);

      assert var6 >= 0;

      var2 += var6;
      var3 -= var6;
      if (var3 != 0) {
         var5 = gallopLeft(this.a[var2 + var3 - 1], this.a, var4, var5, var5 - 1, this.c);

         assert var5 >= 0;

         if (var5 != 0) {
            if (var3 <= var5) {
               this.mergeLo(var2, var3, var4, var5);
            } else {
               this.mergeHi(var2, var3, var4, var5);
            }

         }
      }
   }

   private static <T> int gallopLeft(T var0, T[] var1, int var2, int var3, int var4, Comparator<? super T> var5) {
      assert var3 > 0 && var4 >= 0 && var4 < var3;

      int var6 = 0;
      int var7 = 1;
      int var8;
      if (var5.compare(var0, var1[var2 + var4]) > 0) {
         var8 = var3 - var4;

         while(var7 < var8 && var5.compare(var0, var1[var2 + var4 + var7]) > 0) {
            var6 = var7;
            var7 = (var7 << 1) + 1;
            if (var7 <= 0) {
               var7 = var8;
            }
         }

         if (var7 > var8) {
            var7 = var8;
         }

         var6 += var4;
         var7 += var4;
      } else {
         var8 = var4 + 1;

         while(var7 < var8 && var5.compare(var0, var1[var2 + var4 - var7]) <= 0) {
            var6 = var7;
            var7 = (var7 << 1) + 1;
            if (var7 <= 0) {
               var7 = var8;
            }
         }

         if (var7 > var8) {
            var7 = var8;
         }

         int var9 = var6;
         var6 = var4 - var7;
         var7 = var4 - var9;
      }

      assert -1 <= var6 && var6 < var7 && var7 <= var3;

      ++var6;

      while(var6 < var7) {
         var8 = var6 + (var7 - var6 >>> 1);
         if (var5.compare(var0, var1[var2 + var8]) > 0) {
            var6 = var8 + 1;
         } else {
            var7 = var8;
         }
      }

      assert var6 == var7;

      return var7;
   }

   private static <T> int gallopRight(T var0, T[] var1, int var2, int var3, int var4, Comparator<? super T> var5) {
      assert var3 > 0 && var4 >= 0 && var4 < var3;

      int var6 = 1;
      int var7 = 0;
      int var8;
      if (var5.compare(var0, var1[var2 + var4]) < 0) {
         var8 = var4 + 1;

         while(var6 < var8 && var5.compare(var0, var1[var2 + var4 - var6]) < 0) {
            var7 = var6;
            var6 = (var6 << 1) + 1;
            if (var6 <= 0) {
               var6 = var8;
            }
         }

         if (var6 > var8) {
            var6 = var8;
         }

         int var9 = var7;
         var7 = var4 - var6;
         var6 = var4 - var9;
      } else {
         var8 = var3 - var4;

         while(var6 < var8 && var5.compare(var0, var1[var2 + var4 + var6]) >= 0) {
            var7 = var6;
            var6 = (var6 << 1) + 1;
            if (var6 <= 0) {
               var6 = var8;
            }
         }

         if (var6 > var8) {
            var6 = var8;
         }

         var7 += var4;
         var6 += var4;
      }

      assert -1 <= var7 && var7 < var6 && var6 <= var3;

      ++var7;

      while(var7 < var6) {
         var8 = var7 + (var6 - var7 >>> 1);
         if (var5.compare(var0, var1[var2 + var8]) < 0) {
            var6 = var8;
         } else {
            var7 = var8 + 1;
         }
      }

      assert var7 == var6;

      return var6;
   }

   private void mergeLo(int var1, int var2, int var3, int var4) {
      assert var2 > 0 && var4 > 0 && var1 + var2 == var3;

      Object[] var5 = this.a;
      Object[] var6 = this.ensureCapacity(var2);
      int var7 = this.tmpBase;
      System.arraycopy(var5, var1, var6, var7, var2);
      int var9 = var1 + 1;
      int var8 = var3 + 1;
      var5[var1] = var5[var3];
      --var4;
      if (var4 == 0) {
         System.arraycopy(var6, var7, var5, var9, var2);
      } else if (var2 == 1) {
         System.arraycopy(var5, var8, var5, var9, var4);
         var5[var9 + var4] = var6[var7];
      } else {
         Comparator var10 = this.c;
         int var11 = this.minGallop;

         label131:
         while(true) {
            int var12 = 0;
            int var13 = 0;

            while($assertionsDisabled || var2 > 1 && var4 > 0) {
               label145: {
                  if (var10.compare(var5[var8], var6[var7]) < 0) {
                     var5[var9++] = var5[var8++];
                     ++var13;
                     var12 = 0;
                     --var4;
                     if (var4 == 0) {
                        break label145;
                     }
                  } else {
                     var5[var9++] = var6[var7++];
                     ++var12;
                     var13 = 0;
                     --var2;
                     if (var2 == 1) {
                        break label145;
                     }
                  }

                  if ((var12 | var13) < var11) {
                     continue;
                  }

                  while($assertionsDisabled || var2 > 1 && var4 > 0) {
                     var12 = gallopRight(var5[var8], var6, var7, var2, 0, var10);
                     if (var12 != 0) {
                        System.arraycopy(var6, var7, var5, var9, var12);
                        var9 += var12;
                        var7 += var12;
                        var2 -= var12;
                        if (var2 <= 1) {
                           break label145;
                        }
                     }

                     var5[var9++] = var5[var8++];
                     --var4;
                     if (var4 == 0) {
                        break label145;
                     }

                     var13 = gallopLeft(var6[var7], var5, var8, var4, 0, var10);
                     if (var13 != 0) {
                        System.arraycopy(var5, var8, var5, var9, var13);
                        var9 += var13;
                        var8 += var13;
                        var4 -= var13;
                        if (var4 == 0) {
                           break label145;
                        }
                     }

                     var5[var9++] = var6[var7++];
                     --var2;
                     if (var2 == 1) {
                        break label145;
                     }

                     --var11;
                     if (!(var12 >= 7 | var13 >= 7)) {
                        if (var11 < 0) {
                           var11 = 0;
                        }

                        var11 += 2;
                        continue label131;
                     }
                  }

                  throw new AssertionError();
               }

               this.minGallop = var11 < 1 ? 1 : var11;
               if (var2 == 1) {
                  assert var4 > 0;

                  System.arraycopy(var5, var8, var5, var9, var4);
                  var5[var9 + var4] = var6[var7];
               } else {
                  if (var2 == 0) {
                     throw new IllegalArgumentException("Comparison method violates its general contract!");
                  }

                  assert var4 == 0;

                  assert var2 > 1;

                  System.arraycopy(var6, var7, var5, var9, var2);
               }

               return;
            }

            throw new AssertionError();
         }
      }
   }

   private void mergeHi(int var1, int var2, int var3, int var4) {
      assert var2 > 0 && var4 > 0 && var1 + var2 == var3;

      Object[] var5 = this.a;
      Object[] var6 = this.ensureCapacity(var4);
      int var7 = this.tmpBase;
      System.arraycopy(var5, var3, var6, var7, var4);
      int var8 = var1 + var2 - 1;
      int var9 = var7 + var4 - 1;
      int var10 = var3 + var4 - 1;
      var5[var10--] = var5[var8--];
      --var2;
      if (var2 == 0) {
         System.arraycopy(var6, var7, var5, var10 - (var4 - 1), var4);
      } else if (var4 == 1) {
         var10 -= var2;
         var8 -= var2;
         System.arraycopy(var5, var8 + 1, var5, var10 + 1, var2);
         var5[var10] = var6[var9];
      } else {
         Comparator var11 = this.c;
         int var12 = this.minGallop;

         label131:
         while(true) {
            int var13 = 0;
            int var14 = 0;

            while($assertionsDisabled || var2 > 0 && var4 > 1) {
               label145: {
                  if (var11.compare(var6[var9], var5[var8]) < 0) {
                     var5[var10--] = var5[var8--];
                     ++var13;
                     var14 = 0;
                     --var2;
                     if (var2 == 0) {
                        break label145;
                     }
                  } else {
                     var5[var10--] = var6[var9--];
                     ++var14;
                     var13 = 0;
                     --var4;
                     if (var4 == 1) {
                        break label145;
                     }
                  }

                  if ((var13 | var14) < var12) {
                     continue;
                  }

                  while($assertionsDisabled || var2 > 0 && var4 > 1) {
                     var13 = var2 - gallopRight(var6[var9], var5, var1, var2, var2 - 1, var11);
                     if (var13 != 0) {
                        var10 -= var13;
                        var8 -= var13;
                        var2 -= var13;
                        System.arraycopy(var5, var8 + 1, var5, var10 + 1, var13);
                        if (var2 == 0) {
                           break label145;
                        }
                     }

                     var5[var10--] = var6[var9--];
                     --var4;
                     if (var4 == 1) {
                        break label145;
                     }

                     var14 = var4 - gallopLeft(var5[var8], var6, var7, var4, var4 - 1, var11);
                     if (var14 != 0) {
                        var10 -= var14;
                        var9 -= var14;
                        var4 -= var14;
                        System.arraycopy(var6, var9 + 1, var5, var10 + 1, var14);
                        if (var4 <= 1) {
                           break label145;
                        }
                     }

                     var5[var10--] = var5[var8--];
                     --var2;
                     if (var2 == 0) {
                        break label145;
                     }

                     --var12;
                     if (!(var13 >= 7 | var14 >= 7)) {
                        if (var12 < 0) {
                           var12 = 0;
                        }

                        var12 += 2;
                        continue label131;
                     }
                  }

                  throw new AssertionError();
               }

               this.minGallop = var12 < 1 ? 1 : var12;
               if (var4 == 1) {
                  assert var2 > 0;

                  var10 -= var2;
                  var8 -= var2;
                  System.arraycopy(var5, var8 + 1, var5, var10 + 1, var2);
                  var5[var10] = var6[var9];
               } else {
                  if (var4 == 0) {
                     throw new IllegalArgumentException("Comparison method violates its general contract!");
                  }

                  assert var2 == 0;

                  assert var4 > 0;

                  System.arraycopy(var6, var7, var5, var10 - (var4 - 1), var4);
               }

               return;
            }

            throw new AssertionError();
         }
      }
   }

   private T[] ensureCapacity(int var1) {
      if (this.tmpLen < var1) {
         int var2 = var1 | var1 >> 1;
         var2 |= var2 >> 2;
         var2 |= var2 >> 4;
         var2 |= var2 >> 8;
         var2 |= var2 >> 16;
         ++var2;
         if (var2 < 0) {
            var2 = var1;
         } else {
            var2 = Math.min(var2, this.a.length >>> 1);
         }

         Object[] var3 = (Object[])((Object[])Array.newInstance(this.a.getClass().getComponentType(), var2));
         this.tmp = var3;
         this.tmpLen = var2;
         this.tmpBase = 0;
      }

      return this.tmp;
   }
}
