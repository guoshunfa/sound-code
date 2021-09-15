package java.util;

class ComparableTimSort {
   private static final int MIN_MERGE = 32;
   private final Object[] a;
   private static final int MIN_GALLOP = 7;
   private int minGallop = 7;
   private static final int INITIAL_TMP_STORAGE_LENGTH = 256;
   private Object[] tmp;
   private int tmpBase;
   private int tmpLen;
   private int stackSize = 0;
   private final int[] runBase;
   private final int[] runLen;

   private ComparableTimSort(Object[] var1, Object[] var2, int var3, int var4) {
      this.a = var1;
      int var5 = var1.length;
      int var6 = var5 < 512 ? var5 >>> 1 : 256;
      if (var2 != null && var4 >= var6 && var3 + var6 <= var2.length) {
         this.tmp = var2;
         this.tmpBase = var3;
         this.tmpLen = var4;
      } else {
         this.tmp = new Object[var6];
         this.tmpBase = 0;
         this.tmpLen = var6;
      }

      int var7 = var5 < 120 ? 5 : (var5 < 1542 ? 10 : (var5 < 119151 ? 24 : 49));
      this.runBase = new int[var7];
      this.runLen = new int[var7];
   }

   static void sort(Object[] var0, int var1, int var2, Object[] var3, int var4, int var5) {
      assert var0 != null && var1 >= 0 && var1 <= var2 && var2 <= var0.length;

      int var6 = var2 - var1;
      if (var6 >= 2) {
         if (var6 < 32) {
            int var11 = countRunAndMakeAscending(var0, var1, var2);
            binarySort(var0, var1, var2, var1 + var11);
         } else {
            ComparableTimSort var7 = new ComparableTimSort(var0, var3, var4, var5);
            int var8 = minRunLength(var6);

            do {
               int var9 = countRunAndMakeAscending(var0, var1, var2);
               if (var9 < var8) {
                  int var10 = var6 <= var8 ? var6 : var8;
                  binarySort(var0, var1, var1 + var10, var1 + var9);
                  var9 = var10;
               }

               var7.pushRun(var1, var9);
               var7.mergeCollapse();
               var1 += var9;
               var6 -= var9;
            } while(var6 != 0);

            assert var1 == var2;

            var7.mergeForceCollapse();

            assert var7.stackSize == 1;

         }
      }
   }

   private static void binarySort(Object[] var0, int var1, int var2, int var3) {
      assert var1 <= var3 && var3 <= var2;

      if (var3 == var1) {
         ++var3;
      }

      while(var3 < var2) {
         Comparable var4 = (Comparable)var0[var3];
         int var5 = var1;
         int var6 = var3;

         assert var1 <= var3;

         int var7;
         while(var5 < var6) {
            var7 = var5 + var6 >>> 1;
            if (var4.compareTo(var0[var7]) < 0) {
               var6 = var7;
            } else {
               var5 = var7 + 1;
            }
         }

         assert var5 == var6;

         var7 = var3 - var5;
         switch(var7) {
         case 2:
            var0[var5 + 2] = var0[var5 + 1];
         case 1:
            var0[var5 + 1] = var0[var5];
            break;
         default:
            System.arraycopy(var0, var5, var0, var5 + 1, var7);
         }

         var0[var5] = var4;
         ++var3;
      }

   }

   private static int countRunAndMakeAscending(Object[] var0, int var1, int var2) {
      assert var1 < var2;

      int var3 = var1 + 1;
      if (var3 == var2) {
         return 1;
      } else {
         if (((Comparable)var0[var3++]).compareTo(var0[var1]) >= 0) {
            while(var3 < var2 && ((Comparable)var0[var3]).compareTo(var0[var3 - 1]) >= 0) {
               ++var3;
            }
         } else {
            while(var3 < var2 && ((Comparable)var0[var3]).compareTo(var0[var3 - 1]) < 0) {
               ++var3;
            }

            reverseRange(var0, var1, var3);
         }

         return var3 - var1;
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
      int var6 = gallopRight((Comparable)this.a[var4], this.a, var2, var3, 0);

      assert var6 >= 0;

      var2 += var6;
      var3 -= var6;
      if (var3 != 0) {
         var5 = gallopLeft((Comparable)this.a[var2 + var3 - 1], this.a, var4, var5, var5 - 1);

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

   private static int gallopLeft(Comparable<Object> var0, Object[] var1, int var2, int var3, int var4) {
      assert var3 > 0 && var4 >= 0 && var4 < var3;

      int var5 = 0;
      int var6 = 1;
      int var7;
      if (var0.compareTo(var1[var2 + var4]) > 0) {
         var7 = var3 - var4;

         while(var6 < var7 && var0.compareTo(var1[var2 + var4 + var6]) > 0) {
            var5 = var6;
            var6 = (var6 << 1) + 1;
            if (var6 <= 0) {
               var6 = var7;
            }
         }

         if (var6 > var7) {
            var6 = var7;
         }

         var5 += var4;
         var6 += var4;
      } else {
         var7 = var4 + 1;

         while(var6 < var7 && var0.compareTo(var1[var2 + var4 - var6]) <= 0) {
            var5 = var6;
            var6 = (var6 << 1) + 1;
            if (var6 <= 0) {
               var6 = var7;
            }
         }

         if (var6 > var7) {
            var6 = var7;
         }

         int var8 = var5;
         var5 = var4 - var6;
         var6 = var4 - var8;
      }

      assert -1 <= var5 && var5 < var6 && var6 <= var3;

      ++var5;

      while(var5 < var6) {
         var7 = var5 + (var6 - var5 >>> 1);
         if (var0.compareTo(var1[var2 + var7]) > 0) {
            var5 = var7 + 1;
         } else {
            var6 = var7;
         }
      }

      assert var5 == var6;

      return var6;
   }

   private static int gallopRight(Comparable<Object> var0, Object[] var1, int var2, int var3, int var4) {
      assert var3 > 0 && var4 >= 0 && var4 < var3;

      int var5 = 1;
      int var6 = 0;
      int var7;
      if (var0.compareTo(var1[var2 + var4]) < 0) {
         var7 = var4 + 1;

         while(var5 < var7 && var0.compareTo(var1[var2 + var4 - var5]) < 0) {
            var6 = var5;
            var5 = (var5 << 1) + 1;
            if (var5 <= 0) {
               var5 = var7;
            }
         }

         if (var5 > var7) {
            var5 = var7;
         }

         int var8 = var6;
         var6 = var4 - var5;
         var5 = var4 - var8;
      } else {
         var7 = var3 - var4;

         while(var5 < var7 && var0.compareTo(var1[var2 + var4 + var5]) >= 0) {
            var6 = var5;
            var5 = (var5 << 1) + 1;
            if (var5 <= 0) {
               var5 = var7;
            }
         }

         if (var5 > var7) {
            var5 = var7;
         }

         var6 += var4;
         var5 += var4;
      }

      assert -1 <= var6 && var6 < var5 && var5 <= var3;

      ++var6;

      while(var6 < var5) {
         var7 = var6 + (var5 - var6 >>> 1);
         if (var0.compareTo(var1[var2 + var7]) < 0) {
            var5 = var7;
         } else {
            var6 = var7 + 1;
         }
      }

      assert var6 == var5;

      return var5;
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
         int var10 = this.minGallop;

         label131:
         while(true) {
            int var11 = 0;
            int var12 = 0;

            while($assertionsDisabled || var2 > 1 && var4 > 0) {
               label145: {
                  if (((Comparable)var5[var8]).compareTo(var6[var7]) < 0) {
                     var5[var9++] = var5[var8++];
                     ++var12;
                     var11 = 0;
                     --var4;
                     if (var4 == 0) {
                        break label145;
                     }
                  } else {
                     var5[var9++] = var6[var7++];
                     ++var11;
                     var12 = 0;
                     --var2;
                     if (var2 == 1) {
                        break label145;
                     }
                  }

                  if ((var11 | var12) < var10) {
                     continue;
                  }

                  while($assertionsDisabled || var2 > 1 && var4 > 0) {
                     var11 = gallopRight((Comparable)var5[var8], var6, var7, var2, 0);
                     if (var11 != 0) {
                        System.arraycopy(var6, var7, var5, var9, var11);
                        var9 += var11;
                        var7 += var11;
                        var2 -= var11;
                        if (var2 <= 1) {
                           break label145;
                        }
                     }

                     var5[var9++] = var5[var8++];
                     --var4;
                     if (var4 == 0) {
                        break label145;
                     }

                     var12 = gallopLeft((Comparable)var6[var7], var5, var8, var4, 0);
                     if (var12 != 0) {
                        System.arraycopy(var5, var8, var5, var9, var12);
                        var9 += var12;
                        var8 += var12;
                        var4 -= var12;
                        if (var4 == 0) {
                           break label145;
                        }
                     }

                     var5[var9++] = var6[var7++];
                     --var2;
                     if (var2 == 1) {
                        break label145;
                     }

                     --var10;
                     if (!(var11 >= 7 | var12 >= 7)) {
                        if (var10 < 0) {
                           var10 = 0;
                        }

                        var10 += 2;
                        continue label131;
                     }
                  }

                  throw new AssertionError();
               }

               this.minGallop = var10 < 1 ? 1 : var10;
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
         int var11 = this.minGallop;

         label131:
         while(true) {
            int var12 = 0;
            int var13 = 0;

            while($assertionsDisabled || var2 > 0 && var4 > 1) {
               label145: {
                  if (((Comparable)var6[var9]).compareTo(var5[var8]) < 0) {
                     var5[var10--] = var5[var8--];
                     ++var12;
                     var13 = 0;
                     --var2;
                     if (var2 == 0) {
                        break label145;
                     }
                  } else {
                     var5[var10--] = var6[var9--];
                     ++var13;
                     var12 = 0;
                     --var4;
                     if (var4 == 1) {
                        break label145;
                     }
                  }

                  if ((var12 | var13) < var11) {
                     continue;
                  }

                  while($assertionsDisabled || var2 > 0 && var4 > 1) {
                     var12 = var2 - gallopRight((Comparable)var6[var9], var5, var1, var2, var2 - 1);
                     if (var12 != 0) {
                        var10 -= var12;
                        var8 -= var12;
                        var2 -= var12;
                        System.arraycopy(var5, var8 + 1, var5, var10 + 1, var12);
                        if (var2 == 0) {
                           break label145;
                        }
                     }

                     var5[var10--] = var6[var9--];
                     --var4;
                     if (var4 == 1) {
                        break label145;
                     }

                     var13 = var4 - gallopLeft((Comparable)var5[var8], var6, var7, var4, var4 - 1);
                     if (var13 != 0) {
                        var10 -= var13;
                        var9 -= var13;
                        var4 -= var13;
                        System.arraycopy(var6, var9 + 1, var5, var10 + 1, var13);
                        if (var4 <= 1) {
                           break label145;
                        }
                     }

                     var5[var10--] = var5[var8--];
                     --var2;
                     if (var2 == 0) {
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

   private Object[] ensureCapacity(int var1) {
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

         Object[] var3 = new Object[var2];
         this.tmp = var3;
         this.tmpLen = var2;
         this.tmpBase = 0;
      }

      return this.tmp;
   }
}
