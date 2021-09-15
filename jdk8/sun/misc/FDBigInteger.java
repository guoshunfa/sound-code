package sun.misc;

import java.math.BigInteger;
import java.util.Arrays;

public class FDBigInteger {
   static final int[] SMALL_5_POW = new int[]{1, 5, 25, 125, 625, 3125, 15625, 78125, 390625, 1953125, 9765625, 48828125, 244140625, 1220703125};
   static final long[] LONG_5_POW = new long[]{1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 78125L, 390625L, 1953125L, 9765625L, 48828125L, 244140625L, 1220703125L, 6103515625L, 30517578125L, 152587890625L, 762939453125L, 3814697265625L, 19073486328125L, 95367431640625L, 476837158203125L, 2384185791015625L, 11920928955078125L, 59604644775390625L, 298023223876953125L, 1490116119384765625L};
   private static final int MAX_FIVE_POW = 340;
   private static final FDBigInteger[] POW_5_CACHE = new FDBigInteger[340];
   public static final FDBigInteger ZERO;
   private static final long LONG_MASK = 4294967295L;
   private int[] data;
   private int offset;
   private int nWords;
   private boolean isImmutable = false;

   private FDBigInteger(int[] var1, int var2) {
      this.data = var1;
      this.offset = var2;
      this.nWords = var1.length;
      this.trimLeadingZeros();
   }

   public FDBigInteger(long var1, char[] var3, int var4, int var5) {
      int var6 = Math.max((var5 + 8) / 9, 2);
      this.data = new int[var6];
      this.data[0] = (int)var1;
      this.data[1] = (int)(var1 >>> 32);
      this.offset = 0;
      this.nWords = 2;
      int var7 = var4;
      int var8 = var5 - 5;

      int var9;
      int var10;
      while(var7 < var8) {
         var10 = var7 + 5;

         for(var9 = var3[var7++] - 48; var7 < var10; var9 = 10 * var9 + var3[var7++] - 48) {
         }

         this.multAddMe(100000, var9);
      }

      var10 = 1;

      for(var9 = 0; var7 < var5; var10 *= 10) {
         var9 = 10 * var9 + var3[var7++] - 48;
      }

      if (var10 != 1) {
         this.multAddMe(var10, var9);
      }

      this.trimLeadingZeros();
   }

   public static FDBigInteger valueOfPow52(int var0, int var1) {
      if (var0 != 0) {
         if (var1 == 0) {
            return big5pow(var0);
         } else if (var0 < SMALL_5_POW.length) {
            int var2 = SMALL_5_POW[var0];
            int var3 = var1 >> 5;
            int var4 = var1 & 31;
            return var4 == 0 ? new FDBigInteger(new int[]{var2}, var3) : new FDBigInteger(new int[]{var2 << var4, var2 >>> 32 - var4}, var3);
         } else {
            return big5pow(var0).leftShift(var1);
         }
      } else {
         return valueOfPow2(var1);
      }
   }

   public static FDBigInteger valueOfMulPow52(long var0, int var2, int var3) {
      assert var2 >= 0 : var2;

      assert var3 >= 0 : var3;

      int var4 = (int)var0;
      int var5 = (int)(var0 >>> 32);
      int var6 = var3 >> 5;
      int var7 = var3 & 31;
      if (var2 != 0) {
         if (var2 < SMALL_5_POW.length) {
            long var13 = (long)SMALL_5_POW[var2] & 4294967295L;
            long var10 = ((long)var4 & 4294967295L) * var13;
            var4 = (int)var10;
            var10 >>>= 32;
            var10 += ((long)var5 & 4294967295L) * var13;
            var5 = (int)var10;
            int var12 = (int)(var10 >>> 32);
            return var7 == 0 ? new FDBigInteger(new int[]{var4, var5, var12}, var6) : new FDBigInteger(new int[]{var4 << var7, var5 << var7 | var4 >>> 32 - var7, var12 << var7 | var5 >>> 32 - var7, var12 >>> 32 - var7}, var6);
         } else {
            FDBigInteger var8 = big5pow(var2);
            int[] var9;
            if (var5 == 0) {
               var9 = new int[var8.nWords + 1 + (var3 != 0 ? 1 : 0)];
               mult(var8.data, var8.nWords, var4, var9);
            } else {
               var9 = new int[var8.nWords + 2 + (var3 != 0 ? 1 : 0)];
               mult(var8.data, var8.nWords, var4, var5, var9);
            }

            return (new FDBigInteger(var9, var8.offset)).leftShift(var3);
         }
      } else if (var3 != 0) {
         return var7 == 0 ? new FDBigInteger(new int[]{var4, var5}, var6) : new FDBigInteger(new int[]{var4 << var7, var5 << var7 | var4 >>> 32 - var7, var5 >>> 32 - var7}, var6);
      } else {
         return new FDBigInteger(new int[]{var4, var5}, 0);
      }
   }

   private static FDBigInteger valueOfPow2(int var0) {
      int var1 = var0 >> 5;
      int var2 = var0 & 31;
      return new FDBigInteger(new int[]{1 << var2}, var1);
   }

   private void trimLeadingZeros() {
      int var1 = this.nWords;
      if (var1 > 0) {
         --var1;
         if (this.data[var1] == 0) {
            while(var1 > 0 && this.data[var1 - 1] == 0) {
               --var1;
            }

            this.nWords = var1;
            if (var1 == 0) {
               this.offset = 0;
            }
         }
      }

   }

   public int getNormalizationBias() {
      if (this.nWords == 0) {
         throw new IllegalArgumentException("Zero value cannot be normalized");
      } else {
         int var1 = Integer.numberOfLeadingZeros(this.data[this.nWords - 1]);
         return var1 < 4 ? 28 + var1 : var1 - 4;
      }
   }

   private static void leftShift(int[] var0, int var1, int[] var2, int var3, int var4, int var5) {
      int var6;
      while(var1 > 0) {
         var6 = var5 << var3;
         var5 = var0[var1 - 1];
         var6 |= var5 >>> var4;
         var2[var1] = var6;
         --var1;
      }

      var6 = var5 << var3;
      var2[0] = var6;
   }

   public FDBigInteger leftShift(int var1) {
      if (var1 != 0 && this.nWords != 0) {
         int var2 = var1 >> 5;
         int var3 = var1 & 31;
         int var4;
         int var5;
         int var6;
         int var7;
         int[] var8;
         if (this.isImmutable) {
            if (var3 == 0) {
               return new FDBigInteger(Arrays.copyOf(this.data, this.nWords), this.offset + var2);
            } else {
               var4 = 32 - var3;
               var5 = this.nWords - 1;
               var6 = this.data[var5];
               var7 = var6 >>> var4;
               if (var7 != 0) {
                  var8 = new int[this.nWords + 1];
                  var8[this.nWords] = var7;
               } else {
                  var8 = new int[this.nWords];
               }

               leftShift(this.data, var5, var8, var3, var4, var6);
               return new FDBigInteger(var8, this.offset + var2);
            }
         } else {
            if (var3 != 0) {
               var4 = 32 - var3;
               if (this.data[0] << var3 == 0) {
                  var5 = 0;

                  for(var6 = this.data[var5]; var5 < this.nWords - 1; ++var5) {
                     var7 = var6 >>> var4;
                     var6 = this.data[var5 + 1];
                     var7 |= var6 << var3;
                     this.data[var5] = var7;
                  }

                  var7 = var6 >>> var4;
                  this.data[var5] = var7;
                  if (var7 == 0) {
                     --this.nWords;
                  }

                  ++this.offset;
               } else {
                  var5 = this.nWords - 1;
                  var6 = this.data[var5];
                  var7 = var6 >>> var4;
                  var8 = this.data;
                  int[] var9 = this.data;
                  if (var7 != 0) {
                     if (this.nWords == this.data.length) {
                        this.data = var8 = new int[this.nWords + 1];
                     }

                     var8[this.nWords++] = var7;
                  }

                  leftShift(var9, var5, var8, var3, var4, var6);
               }
            }

            this.offset += var2;
            return this;
         }
      } else {
         return this;
      }
   }

   private int size() {
      return this.nWords + this.offset;
   }

   public int quoRemIteration(FDBigInteger var1) throws IllegalArgumentException {
      assert !this.isImmutable : "cannot modify immutable value";

      int var2 = this.size();
      int var3 = var1.size();
      if (var2 < var3) {
         int var15 = multAndCarryBy10(this.data, this.nWords, this.data);
         if (var15 != 0) {
            this.data[this.nWords++] = var15;
         } else {
            this.trimLeadingZeros();
         }

         return 0;
      } else if (var2 > var3) {
         throw new IllegalArgumentException("disparate values");
      } else {
         long var4 = ((long)this.data[this.nWords - 1] & 4294967295L) / ((long)var1.data[var1.nWords - 1] & 4294967295L);
         long var6 = this.multDiffMe(var4, var1);
         if (var6 != 0L) {
            long var8 = 0L;
            int var10 = var1.offset - this.offset;
            int[] var11 = var1.data;

            for(int[] var12 = this.data; var8 == 0L; --var4) {
               int var13 = 0;

               for(int var14 = var10; var14 < this.nWords; ++var14) {
                  var8 += ((long)var12[var14] & 4294967295L) + ((long)var11[var13] & 4294967295L);
                  var12[var14] = (int)var8;
                  var8 >>>= 32;
                  ++var13;
               }

               assert var8 == 0L || var8 == 1L : var8;
            }
         }

         int var16 = multAndCarryBy10(this.data, this.nWords, this.data);

         assert var16 == 0 : var16;

         this.trimLeadingZeros();
         return (int)var4;
      }
   }

   public FDBigInteger multBy10() {
      if (this.nWords == 0) {
         return this;
      } else if (this.isImmutable) {
         int[] var2 = new int[this.nWords + 1];
         var2[this.nWords] = multAndCarryBy10(this.data, this.nWords, var2);
         return new FDBigInteger(var2, this.offset);
      } else {
         int var1 = multAndCarryBy10(this.data, this.nWords, this.data);
         if (var1 != 0) {
            if (this.nWords == this.data.length) {
               if (this.data[0] == 0) {
                  System.arraycopy(this.data, 1, this.data, 0, --this.nWords);
                  ++this.offset;
               } else {
                  this.data = Arrays.copyOf(this.data, this.data.length + 1);
               }
            }

            this.data[this.nWords++] = var1;
         } else {
            this.trimLeadingZeros();
         }

         return this;
      }
   }

   public FDBigInteger multByPow52(int var1, int var2) {
      if (this.nWords == 0) {
         return this;
      } else {
         FDBigInteger var3 = this;
         if (var1 != 0) {
            int var5 = var2 != 0 ? 1 : 0;
            int[] var4;
            if (var1 < SMALL_5_POW.length) {
               var4 = new int[this.nWords + 1 + var5];
               mult(this.data, this.nWords, SMALL_5_POW[var1], var4);
               var3 = new FDBigInteger(var4, this.offset);
            } else {
               FDBigInteger var6 = big5pow(var1);
               var4 = new int[this.nWords + var6.size() + var5];
               mult(this.data, this.nWords, var6.data, var6.nWords, var4);
               var3 = new FDBigInteger(var4, this.offset + var6.offset);
            }
         }

         return var3.leftShift(var2);
      }
   }

   private static void mult(int[] var0, int var1, int[] var2, int var3, int[] var4) {
      for(int var5 = 0; var5 < var1; ++var5) {
         long var6 = (long)var0[var5] & 4294967295L;
         long var8 = 0L;

         for(int var10 = 0; var10 < var3; ++var10) {
            var8 += ((long)var4[var5 + var10] & 4294967295L) + var6 * ((long)var2[var10] & 4294967295L);
            var4[var5 + var10] = (int)var8;
            var8 >>>= 32;
         }

         var4[var5 + var3] = (int)var8;
      }

   }

   public FDBigInteger leftInplaceSub(FDBigInteger var1) {
      assert this.size() >= var1.size() : "result should be positive";

      FDBigInteger var2;
      if (this.isImmutable) {
         var2 = new FDBigInteger((int[])this.data.clone(), this.offset);
      } else {
         var2 = this;
      }

      int var3 = var1.offset - var2.offset;
      int[] var4 = var1.data;
      int[] var5 = var2.data;
      int var6 = var1.nWords;
      int var7 = var2.nWords;
      if (var3 < 0) {
         int var8 = var7 - var3;
         if (var8 < var5.length) {
            System.arraycopy(var5, 0, var5, -var3, var7);
            Arrays.fill((int[])var5, 0, -var3, (int)0);
         } else {
            int[] var9 = new int[var8];
            System.arraycopy(var5, 0, var9, -var3, var7);
            var5 = var9;
            var2.data = var9;
         }

         var2.offset = var1.offset;
         var7 = var8;
         var2.nWords = var8;
         var3 = 0;
      }

      long var14 = 0L;
      int var10 = var3;

      for(int var11 = 0; var11 < var6 && var10 < var7; ++var10) {
         long var12 = ((long)var5[var10] & 4294967295L) - ((long)var4[var11] & 4294967295L) + var14;
         var5[var10] = (int)var12;
         var14 = var12 >> 32;
         ++var11;
      }

      while(var14 != 0L && var10 < var7) {
         long var15 = ((long)var5[var10] & 4294967295L) + var14;
         var5[var10] = (int)var15;
         var14 = var15 >> 32;
         ++var10;
      }

      assert var14 == 0L : var14;

      var2.trimLeadingZeros();
      return var2;
   }

   public FDBigInteger rightInplaceSub(FDBigInteger var1) {
      assert this.size() >= var1.size() : "result should be positive";

      if (var1.isImmutable) {
         var1 = new FDBigInteger((int[])var1.data.clone(), var1.offset);
      }

      int var3 = this.offset - var1.offset;
      int[] var4 = var1.data;
      int[] var5 = this.data;
      int var6 = var1.nWords;
      int var7 = this.nWords;
      int var8;
      if (var3 < 0) {
         if (var7 < var4.length) {
            System.arraycopy(var4, 0, var4, -var3, var6);
            Arrays.fill((int[])var4, 0, -var3, (int)0);
         } else {
            int[] var9 = new int[var7];
            System.arraycopy(var4, 0, var9, -var3, var6);
            var4 = var9;
            var1.data = var9;
         }

         var1.offset = this.offset;
         int var10000 = var6 - var3;
         var3 = 0;
      } else {
         var8 = var7 + var3;
         if (var8 >= var4.length) {
            var1.data = var4 = Arrays.copyOf(var4, var8);
         }
      }

      var8 = 0;

      long var14;
      for(var14 = 0L; var8 < var3; ++var8) {
         long var11 = 0L - ((long)var4[var8] & 4294967295L) + var14;
         var4[var8] = (int)var11;
         var14 = var11 >> 32;
      }

      for(int var15 = 0; var15 < var7; ++var15) {
         long var12 = ((long)var5[var15] & 4294967295L) - ((long)var4[var8] & 4294967295L) + var14;
         var4[var8] = (int)var12;
         var14 = var12 >> 32;
         ++var8;
      }

      assert var14 == 0L : var14;

      var1.nWords = var8;
      var1.trimLeadingZeros();
      return var1;
   }

   private static int checkZeroTail(int[] var0, int var1) {
      while(true) {
         if (var1 > 0) {
            --var1;
            if (var0[var1] == 0) {
               continue;
            }

            return 1;
         }

         return 0;
      }
   }

   public int cmp(FDBigInteger var1) {
      int var2 = this.nWords + this.offset;
      int var3 = var1.nWords + var1.offset;
      if (var2 > var3) {
         return 1;
      } else if (var2 < var3) {
         return -1;
      } else {
         int var4 = this.nWords;
         int var5 = var1.nWords;

         while(var4 > 0 && var5 > 0) {
            --var4;
            int var6 = this.data[var4];
            --var5;
            int var7 = var1.data[var5];
            if (var6 != var7) {
               return ((long)var6 & 4294967295L) < ((long)var7 & 4294967295L) ? -1 : 1;
            }
         }

         if (var4 > 0) {
            return checkZeroTail(this.data, var4);
         } else {
            return var5 > 0 ? -checkZeroTail(var1.data, var5) : 0;
         }
      }
   }

   public int cmpPow52(int var1, int var2) {
      if (var1 == 0) {
         int var3 = var2 >> 5;
         int var4 = var2 & 31;
         int var5 = this.nWords + this.offset;
         if (var5 > var3 + 1) {
            return 1;
         } else if (var5 < var3 + 1) {
            return -1;
         } else {
            int var6 = this.data[this.nWords - 1];
            int var7 = 1 << var4;
            if (var6 != var7) {
               return ((long)var6 & 4294967295L) < ((long)var7 & 4294967295L) ? -1 : 1;
            } else {
               return checkZeroTail(this.data, this.nWords - 1);
            }
         }
      } else {
         return this.cmp(big5pow(var1).leftShift(var2));
      }
   }

   public int addAndCmp(FDBigInteger var1, FDBigInteger var2) {
      int var5 = var1.size();
      int var6 = var2.size();
      FDBigInteger var3;
      FDBigInteger var4;
      int var7;
      int var8;
      if (var5 >= var6) {
         var3 = var1;
         var4 = var2;
         var7 = var5;
         var8 = var6;
      } else {
         var3 = var2;
         var4 = var1;
         var7 = var6;
         var8 = var5;
      }

      int var9 = this.size();
      if (var7 == 0) {
         return var9 == 0 ? 0 : 1;
      } else if (var8 == 0) {
         return this.cmp(var3);
      } else if (var7 > var9) {
         return -1;
      } else if (var7 + 1 < var9) {
         return 1;
      } else {
         long var10 = (long)var3.data[var3.nWords - 1] & 4294967295L;
         if (var8 == var7) {
            var10 += (long)var4.data[var4.nWords - 1] & 4294967295L;
         }

         long var12;
         if (var10 >>> 32 == 0L) {
            if (var10 + 1L >>> 32 == 0L) {
               if (var7 < var9) {
                  return 1;
               }

               var12 = (long)this.data[this.nWords - 1] & 4294967295L;
               if (var12 < var10) {
                  return -1;
               }

               if (var12 > var10 + 1L) {
                  return 1;
               }
            }
         } else {
            if (var7 + 1 > var9) {
               return -1;
            }

            var10 >>>= 32;
            var12 = (long)this.data[this.nWords - 1] & 4294967295L;
            if (var12 < var10) {
               return -1;
            }

            if (var12 > var10 + 1L) {
               return 1;
            }
         }

         return this.cmp(var3.add(var4));
      }
   }

   public void makeImmutable() {
      this.isImmutable = true;
   }

   private FDBigInteger mult(int var1) {
      if (this.nWords == 0) {
         return this;
      } else {
         int[] var2 = new int[this.nWords + 1];
         mult(this.data, this.nWords, var1, var2);
         return new FDBigInteger(var2, this.offset);
      }
   }

   private FDBigInteger mult(FDBigInteger var1) {
      if (this.nWords == 0) {
         return this;
      } else if (this.size() == 1) {
         return var1.mult(this.data[0]);
      } else if (var1.nWords == 0) {
         return var1;
      } else if (var1.size() == 1) {
         return this.mult(var1.data[0]);
      } else {
         int[] var2 = new int[this.nWords + var1.nWords];
         mult(this.data, this.nWords, var1.data, var1.nWords, var2);
         return new FDBigInteger(var2, this.offset + var1.offset);
      }
   }

   private FDBigInteger add(FDBigInteger var1) {
      int var6 = this.size();
      int var7 = var1.size();
      FDBigInteger var2;
      FDBigInteger var3;
      int var4;
      int var5;
      if (var6 >= var7) {
         var2 = this;
         var4 = var6;
         var3 = var1;
         var5 = var7;
      } else {
         var2 = var1;
         var4 = var7;
         var3 = this;
         var5 = var6;
      }

      int[] var8 = new int[var4 + 1];
      int var9 = 0;

      long var10;
      for(var10 = 0L; var9 < var5; ++var9) {
         var10 += (var9 < var2.offset ? 0L : (long)var2.data[var9 - var2.offset] & 4294967295L) + (var9 < var3.offset ? 0L : (long)var3.data[var9 - var3.offset] & 4294967295L);
         var8[var9] = (int)var10;
         var10 >>= 32;
      }

      while(var9 < var4) {
         var10 += var9 < var2.offset ? 0L : (long)var2.data[var9 - var2.offset] & 4294967295L;
         var8[var9] = (int)var10;
         var10 >>= 32;
         ++var9;
      }

      var8[var4] = (int)var10;
      return new FDBigInteger(var8, 0);
   }

   private void multAddMe(int var1, int var2) {
      long var3 = (long)var1 & 4294967295L;
      long var5 = var3 * ((long)this.data[0] & 4294967295L) + ((long)var2 & 4294967295L);
      this.data[0] = (int)var5;
      var5 >>>= 32;

      for(int var7 = 1; var7 < this.nWords; ++var7) {
         var5 += var3 * ((long)this.data[var7] & 4294967295L);
         this.data[var7] = (int)var5;
         var5 >>>= 32;
      }

      if (var5 != 0L) {
         this.data[this.nWords++] = (int)var5;
      }

   }

   private long multDiffMe(long var1, FDBigInteger var3) {
      long var4 = 0L;
      if (var1 != 0L) {
         int var6 = var3.offset - this.offset;
         int[] var7;
         int var9;
         if (var6 >= 0) {
            var7 = var3.data;
            int[] var8 = this.data;
            var9 = 0;

            for(int var10 = var6; var9 < var3.nWords; ++var10) {
               var4 += ((long)var8[var10] & 4294967295L) - var1 * ((long)var7[var9] & 4294967295L);
               var8[var10] = (int)var4;
               var4 >>= 32;
               ++var9;
            }
         } else {
            var6 = -var6;
            var7 = new int[this.nWords + var6];
            int var13 = 0;
            var9 = 0;

            int[] var14;
            for(var14 = var3.data; var9 < var6 && var13 < var3.nWords; ++var9) {
               var4 -= var1 * ((long)var14[var13] & 4294967295L);
               var7[var9] = (int)var4;
               var4 >>= 32;
               ++var13;
            }

            int var11 = 0;

            for(int[] var12 = this.data; var13 < var3.nWords; ++var9) {
               var4 += ((long)var12[var11] & 4294967295L) - var1 * ((long)var14[var13] & 4294967295L);
               var7[var9] = (int)var4;
               var4 >>= 32;
               ++var13;
               ++var11;
            }

            this.nWords += var6;
            this.offset -= var6;
            this.data = var7;
         }
      }

      return var4;
   }

   private static int multAndCarryBy10(int[] var0, int var1, int[] var2) {
      long var3 = 0L;

      for(int var5 = 0; var5 < var1; ++var5) {
         long var6 = ((long)var0[var5] & 4294967295L) * 10L + var3;
         var2[var5] = (int)var6;
         var3 = var6 >>> 32;
      }

      return (int)var3;
   }

   private static void mult(int[] var0, int var1, int var2, int[] var3) {
      long var4 = (long)var2 & 4294967295L;
      long var6 = 0L;

      for(int var8 = 0; var8 < var1; ++var8) {
         long var9 = ((long)var0[var8] & 4294967295L) * var4 + var6;
         var3[var8] = (int)var9;
         var6 = var9 >>> 32;
      }

      var3[var1] = (int)var6;
   }

   private static void mult(int[] var0, int var1, int var2, int var3, int[] var4) {
      long var5 = (long)var2 & 4294967295L;
      long var7 = 0L;

      int var9;
      long var10;
      for(var9 = 0; var9 < var1; ++var9) {
         var10 = var5 * ((long)var0[var9] & 4294967295L) + var7;
         var4[var9] = (int)var10;
         var7 = var10 >>> 32;
      }

      var4[var1] = (int)var7;
      var5 = (long)var3 & 4294967295L;
      var7 = 0L;

      for(var9 = 0; var9 < var1; ++var9) {
         var10 = ((long)var4[var9 + 1] & 4294967295L) + var5 * ((long)var0[var9] & 4294967295L) + var7;
         var4[var9 + 1] = (int)var10;
         var7 = var10 >>> 32;
      }

      var4[var1 + 1] = (int)var7;
   }

   private static FDBigInteger big5pow(int var0) {
      assert var0 >= 0 : var0;

      return var0 < 340 ? POW_5_CACHE[var0] : big5powRec(var0);
   }

   private static FDBigInteger big5powRec(int var0) {
      if (var0 < 340) {
         return POW_5_CACHE[var0];
      } else {
         int var1 = var0 >> 1;
         int var2 = var0 - var1;
         FDBigInteger var3 = big5powRec(var1);
         return var2 < SMALL_5_POW.length ? var3.mult(SMALL_5_POW[var2]) : var3.mult(big5powRec(var2));
      }
   }

   public String toHexString() {
      if (this.nWords == 0) {
         return "0";
      } else {
         StringBuilder var1 = new StringBuilder((this.nWords + this.offset) * 8);

         int var2;
         for(var2 = this.nWords - 1; var2 >= 0; --var2) {
            String var3 = Integer.toHexString(this.data[var2]);

            for(int var4 = var3.length(); var4 < 8; ++var4) {
               var1.append('0');
            }

            var1.append(var3);
         }

         for(var2 = this.offset; var2 > 0; --var2) {
            var1.append("00000000");
         }

         return var1.toString();
      }
   }

   public BigInteger toBigInteger() {
      byte[] var1 = new byte[this.nWords * 4 + 1];

      for(int var2 = 0; var2 < this.nWords; ++var2) {
         int var3 = this.data[var2];
         var1[var1.length - 4 * var2 - 1] = (byte)var3;
         var1[var1.length - 4 * var2 - 2] = (byte)(var3 >> 8);
         var1[var1.length - 4 * var2 - 3] = (byte)(var3 >> 16);
         var1[var1.length - 4 * var2 - 4] = (byte)(var3 >> 24);
      }

      return (new BigInteger(var1)).shiftLeft(this.offset * 32);
   }

   public String toString() {
      return this.toBigInteger().toString();
   }

   static {
      int var0;
      FDBigInteger var1;
      for(var0 = 0; var0 < SMALL_5_POW.length; ++var0) {
         var1 = new FDBigInteger(new int[]{SMALL_5_POW[var0]}, 0);
         var1.makeImmutable();
         POW_5_CACHE[var0] = var1;
      }

      for(var1 = POW_5_CACHE[var0 - 1]; var0 < 340; ++var0) {
         POW_5_CACHE[var0] = var1 = var1.mult(5);
         var1.makeImmutable();
      }

      ZERO = new FDBigInteger(new int[0], 0);
      ZERO.makeImmutable();
   }
}
