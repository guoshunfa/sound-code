package java.math;

import java.util.Arrays;

class MutableBigInteger {
   int[] value;
   int intLen;
   int offset = 0;
   static final MutableBigInteger ONE = new MutableBigInteger(1);
   static final int KNUTH_POW2_THRESH_LEN = 6;
   static final int KNUTH_POW2_THRESH_ZEROS = 3;

   MutableBigInteger() {
      this.value = new int[1];
      this.intLen = 0;
   }

   MutableBigInteger(int var1) {
      this.value = new int[1];
      this.intLen = 1;
      this.value[0] = var1;
   }

   MutableBigInteger(int[] var1) {
      this.value = var1;
      this.intLen = var1.length;
   }

   MutableBigInteger(BigInteger var1) {
      this.intLen = var1.mag.length;
      this.value = Arrays.copyOf(var1.mag, this.intLen);
   }

   MutableBigInteger(MutableBigInteger var1) {
      this.intLen = var1.intLen;
      this.value = Arrays.copyOfRange(var1.value, var1.offset, var1.offset + this.intLen);
   }

   private void ones(int var1) {
      if (var1 > this.value.length) {
         this.value = new int[var1];
      }

      Arrays.fill((int[])this.value, (int)-1);
      this.offset = 0;
      this.intLen = var1;
   }

   private int[] getMagnitudeArray() {
      return this.offset <= 0 && this.value.length == this.intLen ? this.value : Arrays.copyOfRange(this.value, this.offset, this.offset + this.intLen);
   }

   private long toLong() {
      assert this.intLen <= 2 : "this MutableBigInteger exceeds the range of long";

      if (this.intLen == 0) {
         return 0L;
      } else {
         long var1 = (long)this.value[this.offset] & 4294967295L;
         return this.intLen == 2 ? var1 << 32 | (long)this.value[this.offset + 1] & 4294967295L : var1;
      }
   }

   BigInteger toBigInteger(int var1) {
      return this.intLen != 0 && var1 != 0 ? new BigInteger(this.getMagnitudeArray(), var1) : BigInteger.ZERO;
   }

   BigInteger toBigInteger() {
      this.normalize();
      return this.toBigInteger(this.isZero() ? 0 : 1);
   }

   BigDecimal toBigDecimal(int var1, int var2) {
      if (this.intLen != 0 && var1 != 0) {
         int[] var3 = this.getMagnitudeArray();
         int var4 = var3.length;
         int var5 = var3[0];
         if (var4 > 2 || var5 < 0 && var4 == 2) {
            return new BigDecimal(new BigInteger(var3, var1), Long.MIN_VALUE, var2, 0);
         } else {
            long var6 = var4 == 2 ? (long)var3[1] & 4294967295L | ((long)var5 & 4294967295L) << 32 : (long)var5 & 4294967295L;
            return BigDecimal.valueOf(var1 == -1 ? -var6 : var6, var2);
         }
      } else {
         return BigDecimal.zeroValueOf(var2);
      }
   }

   long toCompactValue(int var1) {
      if (this.intLen != 0 && var1 != 0) {
         int[] var2 = this.getMagnitudeArray();
         int var3 = var2.length;
         int var4 = var2[0];
         if (var3 > 2 || var4 < 0 && var3 == 2) {
            return Long.MIN_VALUE;
         } else {
            long var5 = var3 == 2 ? (long)var2[1] & 4294967295L | ((long)var4 & 4294967295L) << 32 : (long)var4 & 4294967295L;
            return var1 == -1 ? -var5 : var5;
         }
      } else {
         return 0L;
      }
   }

   void clear() {
      this.offset = this.intLen = 0;
      int var1 = 0;

      for(int var2 = this.value.length; var1 < var2; ++var1) {
         this.value[var1] = 0;
      }

   }

   void reset() {
      this.offset = this.intLen = 0;
   }

   final int compare(MutableBigInteger var1) {
      int var2 = var1.intLen;
      if (this.intLen < var2) {
         return -1;
      } else if (this.intLen > var2) {
         return 1;
      } else {
         int[] var3 = var1.value;
         int var4 = this.offset;

         for(int var5 = var1.offset; var4 < this.intLen + this.offset; ++var5) {
            int var6 = this.value[var4] + Integer.MIN_VALUE;
            int var7 = var3[var5] + Integer.MIN_VALUE;
            if (var6 < var7) {
               return -1;
            }

            if (var6 > var7) {
               return 1;
            }

            ++var4;
         }

         return 0;
      }
   }

   private int compareShifted(MutableBigInteger var1, int var2) {
      int var3 = var1.intLen;
      int var4 = this.intLen - var2;
      if (var4 < var3) {
         return -1;
      } else if (var4 > var3) {
         return 1;
      } else {
         int[] var5 = var1.value;
         int var6 = this.offset;

         for(int var7 = var1.offset; var6 < var4 + this.offset; ++var7) {
            int var8 = this.value[var6] + Integer.MIN_VALUE;
            int var9 = var5[var7] + Integer.MIN_VALUE;
            if (var8 < var9) {
               return -1;
            }

            if (var8 > var9) {
               return 1;
            }

            ++var6;
         }

         return 0;
      }
   }

   final int compareHalf(MutableBigInteger var1) {
      int var2 = var1.intLen;
      int var3 = this.intLen;
      if (var3 <= 0) {
         return var2 <= 0 ? 0 : -1;
      } else if (var3 > var2) {
         return 1;
      } else if (var3 < var2 - 1) {
         return -1;
      } else {
         int[] var4 = var1.value;
         int var5 = 0;
         int var6 = 0;
         if (var3 != var2) {
            if (var4[var5] != 1) {
               return -1;
            }

            ++var5;
            var6 = Integer.MIN_VALUE;
         }

         int[] var7 = this.value;
         int var8 = this.offset;

         int var10;
         for(int var9 = var5; var8 < var3 + this.offset; var6 = (var10 & 1) << 31) {
            var10 = var4[var9++];
            long var11 = (long)((var10 >>> 1) + var6) & 4294967295L;
            long var13 = (long)var7[var8++] & 4294967295L;
            if (var13 != var11) {
               return var13 < var11 ? -1 : 1;
            }
         }

         return var6 == 0 ? 0 : -1;
      }
   }

   private final int getLowestSetBit() {
      if (this.intLen == 0) {
         return -1;
      } else {
         int var1;
         for(var1 = this.intLen - 1; var1 > 0 && this.value[var1 + this.offset] == 0; --var1) {
         }

         int var2 = this.value[var1 + this.offset];
         return var2 == 0 ? -1 : (this.intLen - 1 - var1 << 5) + Integer.numberOfTrailingZeros(var2);
      }
   }

   private final int getInt(int var1) {
      return this.value[this.offset + var1];
   }

   private final long getLong(int var1) {
      return (long)this.value[this.offset + var1] & 4294967295L;
   }

   final void normalize() {
      if (this.intLen == 0) {
         this.offset = 0;
      } else {
         int var1 = this.offset;
         if (this.value[var1] == 0) {
            int var2 = var1 + this.intLen;

            do {
               ++var1;
            } while(var1 < var2 && this.value[var1] == 0);

            int var3 = var1 - this.offset;
            this.intLen -= var3;
            this.offset = this.intLen == 0 ? 0 : this.offset + var3;
         }
      }
   }

   private final void ensureCapacity(int var1) {
      if (this.value.length < var1) {
         this.value = new int[var1];
         this.offset = 0;
         this.intLen = var1;
      }

   }

   int[] toIntArray() {
      int[] var1 = new int[this.intLen];

      for(int var2 = 0; var2 < this.intLen; ++var2) {
         var1[var2] = this.value[this.offset + var2];
      }

      return var1;
   }

   void setInt(int var1, int var2) {
      this.value[this.offset + var1] = var2;
   }

   void setValue(int[] var1, int var2) {
      this.value = var1;
      this.intLen = var2;
      this.offset = 0;
   }

   void copyValue(MutableBigInteger var1) {
      int var2 = var1.intLen;
      if (this.value.length < var2) {
         this.value = new int[var2];
      }

      System.arraycopy(var1.value, var1.offset, this.value, 0, var2);
      this.intLen = var2;
      this.offset = 0;
   }

   void copyValue(int[] var1) {
      int var2 = var1.length;
      if (this.value.length < var2) {
         this.value = new int[var2];
      }

      System.arraycopy(var1, 0, this.value, 0, var2);
      this.intLen = var2;
      this.offset = 0;
   }

   boolean isOne() {
      return this.intLen == 1 && this.value[this.offset] == 1;
   }

   boolean isZero() {
      return this.intLen == 0;
   }

   boolean isEven() {
      return this.intLen == 0 || (this.value[this.offset + this.intLen - 1] & 1) == 0;
   }

   boolean isOdd() {
      return this.isZero() ? false : (this.value[this.offset + this.intLen - 1] & 1) == 1;
   }

   boolean isNormal() {
      if (this.intLen + this.offset > this.value.length) {
         return false;
      } else if (this.intLen == 0) {
         return true;
      } else {
         return this.value[this.offset] != 0;
      }
   }

   public String toString() {
      BigInteger var1 = this.toBigInteger(1);
      return var1.toString();
   }

   void safeRightShift(int var1) {
      if (var1 / 32 >= this.intLen) {
         this.reset();
      } else {
         this.rightShift(var1);
      }

   }

   void rightShift(int var1) {
      if (this.intLen != 0) {
         int var2 = var1 >>> 5;
         int var3 = var1 & 31;
         this.intLen -= var2;
         if (var3 != 0) {
            int var4 = BigInteger.bitLengthForInt(this.value[this.offset]);
            if (var3 >= var4) {
               this.primitiveLeftShift(32 - var3);
               --this.intLen;
            } else {
               this.primitiveRightShift(var3);
            }

         }
      }
   }

   void safeLeftShift(int var1) {
      if (var1 > 0) {
         this.leftShift(var1);
      }

   }

   void leftShift(int var1) {
      if (this.intLen != 0) {
         int var2 = var1 >>> 5;
         int var3 = var1 & 31;
         int var4 = BigInteger.bitLengthForInt(this.value[this.offset]);
         if (var1 <= 32 - var4) {
            this.primitiveLeftShift(var3);
         } else {
            int var5 = this.intLen + var2 + 1;
            if (var3 <= 32 - var4) {
               --var5;
            }

            if (this.value.length < var5) {
               int[] var6 = new int[var5];

               for(int var7 = 0; var7 < this.intLen; ++var7) {
                  var6[var7] = this.value[this.offset + var7];
               }

               this.setValue(var6, var5);
            } else {
               int var8;
               if (this.value.length - this.offset >= var5) {
                  for(var8 = 0; var8 < var5 - this.intLen; ++var8) {
                     this.value[this.offset + this.intLen + var8] = 0;
                  }
               } else {
                  for(var8 = 0; var8 < this.intLen; ++var8) {
                     this.value[var8] = this.value[this.offset + var8];
                  }

                  for(var8 = this.intLen; var8 < var5; ++var8) {
                     this.value[var8] = 0;
                  }

                  this.offset = 0;
               }
            }

            this.intLen = var5;
            if (var3 != 0) {
               if (var3 <= 32 - var4) {
                  this.primitiveLeftShift(var3);
               } else {
                  this.primitiveRightShift(32 - var3);
               }

            }
         }
      }
   }

   private int divadd(int[] var1, int[] var2, int var3) {
      long var4 = 0L;

      for(int var6 = var1.length - 1; var6 >= 0; --var6) {
         long var7 = ((long)var1[var6] & 4294967295L) + ((long)var2[var6 + var3] & 4294967295L) + var4;
         var2[var6 + var3] = (int)var7;
         var4 = var7 >>> 32;
      }

      return (int)var4;
   }

   private int mulsub(int[] var1, int[] var2, int var3, int var4, int var5) {
      long var6 = (long)var3 & 4294967295L;
      long var8 = 0L;
      var5 += var4;

      for(int var10 = var4 - 1; var10 >= 0; --var10) {
         long var11 = ((long)var2[var10] & 4294967295L) * var6 + var8;
         long var13 = (long)var1[var5] - var11;
         var1[var5--] = (int)var13;
         var8 = (var11 >>> 32) + (long)((var13 & 4294967295L) > ((long)(~((int)var11)) & 4294967295L) ? 1 : 0);
      }

      return (int)var8;
   }

   private int mulsubBorrow(int[] var1, int[] var2, int var3, int var4, int var5) {
      long var6 = (long)var3 & 4294967295L;
      long var8 = 0L;
      var5 += var4;

      for(int var10 = var4 - 1; var10 >= 0; --var10) {
         long var11 = ((long)var2[var10] & 4294967295L) * var6 + var8;
         long var13 = (long)var1[var5--] - var11;
         var8 = (var11 >>> 32) + (long)((var13 & 4294967295L) > ((long)(~((int)var11)) & 4294967295L) ? 1 : 0);
      }

      return (int)var8;
   }

   private final void primitiveRightShift(int var1) {
      int[] var2 = this.value;
      int var3 = 32 - var1;
      int var4 = this.offset + this.intLen - 1;

      for(int var5 = var2[var4]; var4 > this.offset; --var4) {
         int var6 = var5;
         var5 = var2[var4 - 1];
         var2[var4] = var5 << var3 | var6 >>> var1;
      }

      int var10001 = this.offset;
      var2[var10001] >>>= var1;
   }

   private final void primitiveLeftShift(int var1) {
      int[] var2 = this.value;
      int var3 = 32 - var1;
      int var4 = this.offset;
      int var5 = var2[var4];

      for(int var6 = var4 + this.intLen - 1; var4 < var6; ++var4) {
         int var7 = var5;
         var5 = var2[var4 + 1];
         var2[var4] = var7 << var1 | var5 >>> var3;
      }

      int var10001 = this.offset + this.intLen - 1;
      var2[var10001] <<= var1;
   }

   private BigInteger getLower(int var1) {
      if (this.isZero()) {
         return BigInteger.ZERO;
      } else if (this.intLen < var1) {
         return this.toBigInteger(1);
      } else {
         int var2;
         for(var2 = var1; var2 > 0 && this.value[this.offset + this.intLen - var2] == 0; --var2) {
         }

         int var3 = var2 > 0 ? 1 : 0;
         return new BigInteger(Arrays.copyOfRange(this.value, this.offset + this.intLen - var2, this.offset + this.intLen), var3);
      }
   }

   private void keepLower(int var1) {
      if (this.intLen >= var1) {
         this.offset += this.intLen - var1;
         this.intLen = var1;
      }

   }

   void add(MutableBigInteger var1) {
      int var2 = this.intLen;
      int var3 = var1.intLen;
      int var4 = this.intLen > var1.intLen ? this.intLen : var1.intLen;
      int[] var5 = this.value.length < var4 ? new int[var4] : this.value;
      int var6 = var5.length - 1;

      long var7;
      long var9;
      for(var9 = 0L; var2 > 0 && var3 > 0; var9 = var7 >>> 32) {
         --var2;
         --var3;
         var7 = ((long)this.value[var2 + this.offset] & 4294967295L) + ((long)var1.value[var3 + var1.offset] & 4294967295L) + var9;
         var5[var6--] = (int)var7;
      }

      while(var2 > 0) {
         --var2;
         if (var9 == 0L && var5 == this.value && var6 == var2 + this.offset) {
            return;
         }

         var7 = ((long)this.value[var2 + this.offset] & 4294967295L) + var9;
         var5[var6--] = (int)var7;
         var9 = var7 >>> 32;
      }

      while(var3 > 0) {
         --var3;
         var7 = ((long)var1.value[var3 + var1.offset] & 4294967295L) + var9;
         var5[var6--] = (int)var7;
         var9 = var7 >>> 32;
      }

      if (var9 > 0L) {
         ++var4;
         if (var5.length < var4) {
            int[] var11 = new int[var4];
            System.arraycopy(var5, 0, var11, 1, var5.length);
            var11[0] = 1;
            var5 = var11;
         } else {
            var5[var6--] = 1;
         }
      }

      this.value = var5;
      this.intLen = var4;
      this.offset = var5.length - var4;
   }

   void addShifted(MutableBigInteger var1, int var2) {
      if (!var1.isZero()) {
         int var3 = this.intLen;
         int var4 = var1.intLen + var2;
         int var5 = this.intLen > var4 ? this.intLen : var4;
         int[] var6 = this.value.length < var5 ? new int[var5] : this.value;
         int var7 = var6.length - 1;

         long var8;
         long var10;
         int var12;
         for(var10 = 0L; var3 > 0 && var4 > 0; var10 = var8 >>> 32) {
            --var3;
            --var4;
            var12 = var4 + var1.offset < var1.value.length ? var1.value[var4 + var1.offset] : 0;
            var8 = ((long)this.value[var3 + this.offset] & 4294967295L) + ((long)var12 & 4294967295L) + var10;
            var6[var7--] = (int)var8;
         }

         while(var3 > 0) {
            --var3;
            if (var10 == 0L && var6 == this.value && var7 == var3 + this.offset) {
               return;
            }

            var8 = ((long)this.value[var3 + this.offset] & 4294967295L) + var10;
            var6[var7--] = (int)var8;
            var10 = var8 >>> 32;
         }

         while(var4 > 0) {
            --var4;
            var12 = var4 + var1.offset < var1.value.length ? var1.value[var4 + var1.offset] : 0;
            var8 = ((long)var12 & 4294967295L) + var10;
            var6[var7--] = (int)var8;
            var10 = var8 >>> 32;
         }

         if (var10 > 0L) {
            ++var5;
            if (var6.length < var5) {
               int[] var13 = new int[var5];
               System.arraycopy(var6, 0, var13, 1, var6.length);
               var13[0] = 1;
               var6 = var13;
            } else {
               var6[var7--] = 1;
            }
         }

         this.value = var6;
         this.intLen = var5;
         this.offset = var6.length - var5;
      }
   }

   void addDisjoint(MutableBigInteger var1, int var2) {
      if (!var1.isZero()) {
         int var3 = this.intLen;
         int var4 = var1.intLen + var2;
         int var5 = this.intLen > var4 ? this.intLen : var4;
         int[] var6;
         if (this.value.length < var5) {
            var6 = new int[var5];
         } else {
            var6 = this.value;
            Arrays.fill((int[])this.value, this.offset + this.intLen, this.value.length, (int)0);
         }

         int var7 = var6.length - 1;
         System.arraycopy(this.value, this.offset, var6, var7 + 1 - var3, var3);
         var4 -= var3;
         var7 -= var3;
         int var8 = Math.min(var4, var1.value.length - var1.offset);
         System.arraycopy(var1.value, var1.offset, var6, var7 + 1 - var4, var8);

         for(int var9 = var7 + 1 - var4 + var8; var9 < var7 + 1; ++var9) {
            var6[var9] = 0;
         }

         this.value = var6;
         this.intLen = var5;
         this.offset = var6.length - var5;
      }
   }

   void addLower(MutableBigInteger var1, int var2) {
      MutableBigInteger var3 = new MutableBigInteger(var1);
      if (var3.offset + var3.intLen >= var2) {
         var3.offset = var3.offset + var3.intLen - var2;
         var3.intLen = var2;
      }

      var3.normalize();
      this.add(var3);
   }

   int subtract(MutableBigInteger var1) {
      MutableBigInteger var2 = this;
      int[] var3 = this.value;
      int var4 = this.compare(var1);
      if (var4 == 0) {
         this.reset();
         return 0;
      } else {
         if (var4 < 0) {
            var2 = var1;
            var1 = this;
         }

         int var5 = var2.intLen;
         if (var3.length < var5) {
            var3 = new int[var5];
         }

         long var6 = 0L;
         int var8 = var2.intLen;
         int var9 = var1.intLen;

         int var10;
         for(var10 = var3.length - 1; var9 > 0; var3[var10--] = (int)var6) {
            --var8;
            --var9;
            var6 = ((long)var2.value[var8 + var2.offset] & 4294967295L) - ((long)var1.value[var9 + var1.offset] & 4294967295L) - (long)((int)(-(var6 >> 32)));
         }

         while(var8 > 0) {
            --var8;
            var6 = ((long)var2.value[var8 + var2.offset] & 4294967295L) - (long)((int)(-(var6 >> 32)));
            var3[var10--] = (int)var6;
         }

         this.value = var3;
         this.intLen = var5;
         this.offset = this.value.length - var5;
         this.normalize();
         return var4;
      }
   }

   private int difference(MutableBigInteger var1) {
      MutableBigInteger var2 = this;
      int var3 = this.compare(var1);
      if (var3 == 0) {
         return 0;
      } else {
         if (var3 < 0) {
            var2 = var1;
            var1 = this;
         }

         long var4 = 0L;
         int var6 = var2.intLen;

         for(int var7 = var1.intLen; var7 > 0; var2.value[var2.offset + var6] = (int)var4) {
            --var6;
            --var7;
            var4 = ((long)var2.value[var2.offset + var6] & 4294967295L) - ((long)var1.value[var1.offset + var7] & 4294967295L) - (long)((int)(-(var4 >> 32)));
         }

         while(var6 > 0) {
            --var6;
            var4 = ((long)var2.value[var2.offset + var6] & 4294967295L) - (long)((int)(-(var4 >> 32)));
            var2.value[var2.offset + var6] = (int)var4;
         }

         var2.normalize();
         return var3;
      }
   }

   void multiply(MutableBigInteger var1, MutableBigInteger var2) {
      int var3 = this.intLen;
      int var4 = var1.intLen;
      int var5 = var3 + var4;
      if (var2.value.length < var5) {
         var2.value = new int[var5];
      }

      var2.offset = 0;
      var2.intLen = var5;
      long var6 = 0L;
      int var8 = var4 - 1;

      int var9;
      for(var9 = var4 + var3 - 1; var8 >= 0; --var9) {
         long var10 = ((long)var1.value[var8 + var1.offset] & 4294967295L) * ((long)this.value[var3 - 1 + this.offset] & 4294967295L) + var6;
         var2.value[var9] = (int)var10;
         var6 = var10 >>> 32;
         --var8;
      }

      var2.value[var3 - 1] = (int)var6;

      for(var8 = var3 - 2; var8 >= 0; --var8) {
         var6 = 0L;
         var9 = var4 - 1;

         for(int var13 = var4 + var8; var9 >= 0; --var13) {
            long var11 = ((long)var1.value[var9 + var1.offset] & 4294967295L) * ((long)this.value[var8 + this.offset] & 4294967295L) + ((long)var2.value[var13] & 4294967295L) + var6;
            var2.value[var13] = (int)var11;
            var6 = var11 >>> 32;
            --var9;
         }

         var2.value[var8] = (int)var6;
      }

      var2.normalize();
   }

   void mul(int var1, MutableBigInteger var2) {
      if (var1 == 1) {
         var2.copyValue(this);
      } else if (var1 == 0) {
         var2.clear();
      } else {
         long var3 = (long)var1 & 4294967295L;
         int[] var5 = var2.value.length < this.intLen + 1 ? new int[this.intLen + 1] : var2.value;
         long var6 = 0L;

         for(int var8 = this.intLen - 1; var8 >= 0; --var8) {
            long var9 = var3 * ((long)this.value[var8 + this.offset] & 4294967295L) + var6;
            var5[var8 + 1] = (int)var9;
            var6 = var9 >>> 32;
         }

         if (var6 == 0L) {
            var2.offset = 1;
            var2.intLen = this.intLen;
         } else {
            var2.offset = 0;
            var2.intLen = this.intLen + 1;
            var5[0] = (int)var6;
         }

         var2.value = var5;
      }
   }

   int divideOneWord(int var1, MutableBigInteger var2) {
      long var3 = (long)var1 & 4294967295L;
      if (this.intLen == 1) {
         long var15 = (long)this.value[this.offset] & 4294967295L;
         int var16 = (int)(var15 / var3);
         int var8 = (int)(var15 - (long)var16 * var3);
         var2.value[0] = var16;
         var2.intLen = var16 == 0 ? 0 : 1;
         var2.offset = 0;
         return var8;
      } else {
         if (var2.value.length < this.intLen) {
            var2.value = new int[this.intLen];
         }

         var2.offset = 0;
         var2.intLen = this.intLen;
         int var5 = Integer.numberOfLeadingZeros(var1);
         int var6 = this.value[this.offset];
         long var7 = (long)var6 & 4294967295L;
         if (var7 < var3) {
            var2.value[0] = 0;
         } else {
            var2.value[0] = (int)(var7 / var3);
            var6 = (int)(var7 - (long)var2.value[0] * var3);
            var7 = (long)var6 & 4294967295L;
         }

         int var9 = this.intLen;

         while(true) {
            --var9;
            if (var9 <= 0) {
               var2.normalize();
               if (var5 > 0) {
                  return var6 % var1;
               }

               return var6;
            }

            long var10 = var7 << 32 | (long)this.value[this.offset + this.intLen - var9] & 4294967295L;
            int var12;
            if (var10 >= 0L) {
               var12 = (int)(var10 / var3);
               var6 = (int)(var10 - (long)var12 * var3);
            } else {
               long var13 = divWord(var10, var1);
               var12 = (int)(var13 & 4294967295L);
               var6 = (int)(var13 >>> 32);
            }

            var2.value[this.intLen - var9] = var12;
            var7 = (long)var6 & 4294967295L;
         }
      }
   }

   MutableBigInteger divide(MutableBigInteger var1, MutableBigInteger var2) {
      return this.divide(var1, var2, true);
   }

   MutableBigInteger divide(MutableBigInteger var1, MutableBigInteger var2, boolean var3) {
      return var1.intLen >= 80 && this.intLen - var1.intLen >= 40 ? this.divideAndRemainderBurnikelZiegler(var1, var2) : this.divideKnuth(var1, var2, var3);
   }

   MutableBigInteger divideKnuth(MutableBigInteger var1, MutableBigInteger var2) {
      return this.divideKnuth(var1, var2, true);
   }

   MutableBigInteger divideKnuth(MutableBigInteger var1, MutableBigInteger var2, boolean var3) {
      if (var1.intLen == 0) {
         throw new ArithmeticException("BigInteger divide by zero");
      } else if (this.intLen == 0) {
         var2.intLen = var2.offset = 0;
         return var3 ? new MutableBigInteger() : null;
      } else {
         int var4 = this.compare(var1);
         if (var4 < 0) {
            var2.intLen = var2.offset = 0;
            return var3 ? new MutableBigInteger(this) : null;
         } else if (var4 == 0) {
            var2.value[0] = var2.intLen = 1;
            var2.offset = 0;
            return var3 ? new MutableBigInteger() : null;
         } else {
            var2.clear();
            int var5;
            if (var1.intLen == 1) {
               var5 = this.divideOneWord(var1.value[var1.offset], var2);
               if (var3) {
                  return var5 == 0 ? new MutableBigInteger() : new MutableBigInteger(var5);
               } else {
                  return null;
               }
            } else {
               if (this.intLen >= 6) {
                  var5 = Math.min(this.getLowestSetBit(), var1.getLowestSetBit());
                  if (var5 >= 96) {
                     MutableBigInteger var6 = new MutableBigInteger(this);
                     var1 = new MutableBigInteger(var1);
                     var6.rightShift(var5);
                     var1.rightShift(var5);
                     MutableBigInteger var7 = var6.divideKnuth(var1, var2);
                     var7.leftShift(var5);
                     return var7;
                  }
               }

               return this.divideMagnitude(var1, var2, var3);
            }
         }
      }
   }

   MutableBigInteger divideAndRemainderBurnikelZiegler(MutableBigInteger var1, MutableBigInteger var2) {
      int var3 = this.intLen;
      int var4 = var1.intLen;
      var2.offset = var2.intLen = 0;
      if (var3 < var4) {
         return this;
      } else {
         int var5 = 1 << 32 - Integer.numberOfLeadingZeros(var4 / 80);
         int var6 = (var4 + var5 - 1) / var5;
         int var7 = var6 * var5;
         long var8 = 32L * (long)var7;
         int var10 = (int)Math.max(0L, var8 - var1.bitLength());
         MutableBigInteger var11 = new MutableBigInteger(var1);
         var11.safeLeftShift(var10);
         MutableBigInteger var12 = new MutableBigInteger(this);
         var12.safeLeftShift(var10);
         int var13 = (int)((var12.bitLength() + var8) / var8);
         if (var13 < 2) {
            var13 = 2;
         }

         MutableBigInteger var14 = var12.getBlock(var13 - 1, var13, var7);
         MutableBigInteger var15 = var12.getBlock(var13 - 2, var13, var7);
         var15.addDisjoint(var14, var7);
         MutableBigInteger var16 = new MutableBigInteger();

         MutableBigInteger var17;
         for(int var18 = var13 - 2; var18 > 0; --var18) {
            var17 = var15.divide2n1n(var11, var16);
            var15 = var12.getBlock(var18 - 1, var13, var7);
            var15.addDisjoint(var17, var7);
            var2.addShifted(var16, var18 * var7);
         }

         var17 = var15.divide2n1n(var11, var16);
         var2.add(var16);
         var17.rightShift(var10);
         return var17;
      }
   }

   private MutableBigInteger divide2n1n(MutableBigInteger var1, MutableBigInteger var2) {
      int var3 = var1.intLen;
      if (var3 % 2 == 0 && var3 >= 80) {
         MutableBigInteger var4 = new MutableBigInteger(this);
         var4.safeRightShift(32 * (var3 / 2));
         this.keepLower(var3 / 2);
         MutableBigInteger var5 = new MutableBigInteger();
         MutableBigInteger var6 = var4.divide3n2n(var1, var5);
         this.addDisjoint(var6, var3 / 2);
         MutableBigInteger var7 = this.divide3n2n(var1, var2);
         var2.addDisjoint(var5, var3 / 2);
         return var7;
      } else {
         return this.divideKnuth(var1, var2);
      }
   }

   private MutableBigInteger divide3n2n(MutableBigInteger var1, MutableBigInteger var2) {
      int var3 = var1.intLen / 2;
      MutableBigInteger var4 = new MutableBigInteger(this);
      var4.safeRightShift(32 * var3);
      MutableBigInteger var5 = new MutableBigInteger(var1);
      var5.safeRightShift(var3 * 32);
      BigInteger var6 = var1.getLower(var3);
      MutableBigInteger var7;
      MutableBigInteger var8;
      if (this.compareShifted(var1, var3) < 0) {
         var7 = var4.divide2n1n(var5, var2);
         var8 = new MutableBigInteger(var2.toBigInteger().multiply(var6));
      } else {
         var2.ones(var3);
         var4.add(var5);
         var5.leftShift(32 * var3);
         var4.subtract(var5);
         var7 = var4;
         var8 = new MutableBigInteger(var6);
         var8.leftShift(32 * var3);
         var8.subtract(new MutableBigInteger(var6));
      }

      var7.leftShift(32 * var3);
      var7.addLower(this, var3);

      while(var7.compare(var8) < 0) {
         var7.add(var1);
         var2.subtract(ONE);
      }

      var7.subtract(var8);
      return var7;
   }

   private MutableBigInteger getBlock(int var1, int var2, int var3) {
      int var4 = var1 * var3;
      if (var4 >= this.intLen) {
         return new MutableBigInteger();
      } else {
         int var5;
         if (var1 == var2 - 1) {
            var5 = this.intLen;
         } else {
            var5 = (var1 + 1) * var3;
         }

         if (var5 > this.intLen) {
            return new MutableBigInteger();
         } else {
            int[] var6 = Arrays.copyOfRange(this.value, this.offset + this.intLen - var5, this.offset + this.intLen - var4);
            return new MutableBigInteger(var6);
         }
      }
   }

   long bitLength() {
      return this.intLen == 0 ? 0L : (long)this.intLen * 32L - (long)Integer.numberOfLeadingZeros(this.value[this.offset]);
   }

   long divide(long var1, MutableBigInteger var3) {
      if (var1 == 0L) {
         throw new ArithmeticException("BigInteger divide by zero");
      } else if (this.intLen == 0) {
         var3.intLen = var3.offset = 0;
         return 0L;
      } else {
         if (var1 < 0L) {
            var1 = -var1;
         }

         int var4 = (int)(var1 >>> 32);
         var3.clear();
         return var4 == 0 ? (long)this.divideOneWord((int)var1, var3) & 4294967295L : this.divideLongMagnitude(var1, var3).toLong();
      }
   }

   private static void copyAndShift(int[] var0, int var1, int var2, int[] var3, int var4, int var5) {
      int var6 = 32 - var5;
      int var7 = var0[var1];

      for(int var8 = 0; var8 < var2 - 1; ++var8) {
         int var9 = var7;
         ++var1;
         var7 = var0[var1];
         var3[var4 + var8] = var9 << var5 | var7 >>> var6;
      }

      var3[var4 + var2 - 1] = var7 << var5;
   }

   private MutableBigInteger divideMagnitude(MutableBigInteger var1, MutableBigInteger var2, boolean var3) {
      int var4 = Integer.numberOfLeadingZeros(var1.value[var1.offset]);
      int var5 = var1.intLen;
      int[] var6;
      MutableBigInteger var7;
      int var9;
      int var11;
      if (var4 > 0) {
         var6 = new int[var5];
         copyAndShift(var1.value, var1.offset, var5, var6, 0, var4);
         int[] var8;
         if (Integer.numberOfLeadingZeros(this.value[this.offset]) >= var4) {
            var8 = new int[this.intLen + 1];
            var7 = new MutableBigInteger(var8);
            var7.intLen = this.intLen;
            var7.offset = 1;
            copyAndShift(this.value, this.offset, this.intLen, var8, 1, var4);
         } else {
            var8 = new int[this.intLen + 2];
            var7 = new MutableBigInteger(var8);
            var7.intLen = this.intLen + 1;
            var7.offset = 1;
            var9 = this.offset;
            int var10 = 0;
            var11 = 32 - var4;

            for(int var12 = 1; var12 < this.intLen + 1; ++var9) {
               int var13 = var10;
               var10 = this.value[var9];
               var8[var12] = var13 << var4 | var10 >>> var11;
               ++var12;
            }

            var8[this.intLen + 1] = var10 << var4;
         }
      } else {
         var6 = Arrays.copyOfRange(var1.value, var1.offset, var1.offset + var1.intLen);
         var7 = new MutableBigInteger(new int[this.intLen + 1]);
         System.arraycopy(this.value, this.offset, var7.value, 1, this.intLen);
         var7.intLen = this.intLen;
         var7.offset = 1;
      }

      int var28 = var7.intLen;
      var9 = var28 - var5 + 1;
      if (var2.value.length < var9) {
         var2.value = new int[var9];
         var2.offset = 0;
      }

      var2.intLen = var9;
      int[] var29 = var2.value;
      if (var7.intLen == var28) {
         var7.offset = 0;
         var7.value[0] = 0;
         ++var7.intLen;
      }

      var11 = var6[0];
      long var30 = (long)var11 & 4294967295L;
      int var14 = var6[1];

      int var15;
      boolean var16;
      boolean var17;
      int var19;
      int var20;
      int var21;
      int var32;
      for(var15 = 0; var15 < var9 - 1; ++var15) {
         var16 = false;
         var17 = false;
         boolean var18 = false;
         var19 = var7.value[var15 + var7.offset];
         var20 = var19 + Integer.MIN_VALUE;
         var21 = var7.value[var15 + 1 + var7.offset];
         long var22;
         long var24;
         int var33;
         if (var19 == var11) {
            var32 = -1;
            var33 = var19 + var21;
            var18 = var33 + Integer.MIN_VALUE < var20;
         } else {
            var22 = (long)var19 << 32 | (long)var21 & 4294967295L;
            if (var22 >= 0L) {
               var32 = (int)(var22 / var30);
               var33 = (int)(var22 - (long)var32 * var30);
            } else {
               var24 = divWord(var22, var11);
               var32 = (int)(var24 & 4294967295L);
               var33 = (int)(var24 >>> 32);
            }
         }

         if (var32 != 0) {
            if (!var18) {
               var22 = (long)var7.value[var15 + 2 + var7.offset] & 4294967295L;
               var24 = ((long)var33 & 4294967295L) << 32 | var22;
               long var26 = ((long)var14 & 4294967295L) * ((long)var32 & 4294967295L);
               if (this.unsignedLongCompare(var26, var24)) {
                  --var32;
                  var33 = (int)(((long)var33 & 4294967295L) + var30);
                  if (((long)var33 & 4294967295L) >= var30) {
                     var26 -= (long)var14 & 4294967295L;
                     var24 = ((long)var33 & 4294967295L) << 32 | var22;
                     if (this.unsignedLongCompare(var26, var24)) {
                        --var32;
                     }
                  }
               }
            }

            var7.value[var15 + var7.offset] = 0;
            int var36 = this.mulsub(var7.value, var6, var32, var5, var15 + var7.offset);
            if (var36 + Integer.MIN_VALUE > var20) {
               this.divadd(var6, var7.value, var15 + 1 + var7.offset);
               --var32;
            }

            var29[var15] = var32;
         }
      }

      boolean var31 = false;
      var16 = false;
      var17 = false;
      int var34 = var7.value[var9 - 1 + var7.offset];
      var19 = var34 + Integer.MIN_VALUE;
      var20 = var7.value[var9 + var7.offset];
      long var23;
      long var35;
      if (var34 == var11) {
         var15 = -1;
         var32 = var34 + var20;
         var17 = var32 + Integer.MIN_VALUE < var19;
      } else {
         var35 = (long)var34 << 32 | (long)var20 & 4294967295L;
         if (var35 >= 0L) {
            var15 = (int)(var35 / var30);
            var32 = (int)(var35 - (long)var15 * var30);
         } else {
            var23 = divWord(var35, var11);
            var15 = (int)(var23 & 4294967295L);
            var32 = (int)(var23 >>> 32);
         }
      }

      if (var15 != 0) {
         if (!var17) {
            var35 = (long)var7.value[var9 + 1 + var7.offset] & 4294967295L;
            var23 = ((long)var32 & 4294967295L) << 32 | var35;
            long var25 = ((long)var14 & 4294967295L) * ((long)var15 & 4294967295L);
            if (this.unsignedLongCompare(var25, var23)) {
               --var15;
               var32 = (int)(((long)var32 & 4294967295L) + var30);
               if (((long)var32 & 4294967295L) >= var30) {
                  var25 -= (long)var14 & 4294967295L;
                  var23 = ((long)var32 & 4294967295L) << 32 | var35;
                  if (this.unsignedLongCompare(var25, var23)) {
                     --var15;
                  }
               }
            }
         }

         var7.value[var9 - 1 + var7.offset] = 0;
         if (var3) {
            var21 = this.mulsub(var7.value, var6, var15, var5, var9 - 1 + var7.offset);
         } else {
            var21 = this.mulsubBorrow(var7.value, var6, var15, var5, var9 - 1 + var7.offset);
         }

         if (var21 + Integer.MIN_VALUE > var19) {
            if (var3) {
               this.divadd(var6, var7.value, var9 - 1 + 1 + var7.offset);
            }

            --var15;
         }

         var29[var9 - 1] = var15;
      }

      if (var3) {
         if (var4 > 0) {
            var7.rightShift(var4);
         }

         var7.normalize();
      }

      var2.normalize();
      return var3 ? var7 : null;
   }

   private MutableBigInteger divideLongMagnitude(long var1, MutableBigInteger var3) {
      MutableBigInteger var4 = new MutableBigInteger(new int[this.intLen + 1]);
      System.arraycopy(this.value, this.offset, var4.value, 1, this.intLen);
      var4.intLen = this.intLen;
      var4.offset = 1;
      int var5 = var4.intLen;
      int var6 = var5 - 2 + 1;
      if (var3.value.length < var6) {
         var3.value = new int[var6];
         var3.offset = 0;
      }

      var3.intLen = var6;
      int[] var7 = var3.value;
      int var8 = Long.numberOfLeadingZeros(var1);
      if (var8 > 0) {
         var1 <<= var8;
         var4.leftShift(var8);
      }

      if (var4.intLen == var5) {
         var4.offset = 0;
         var4.value[0] = 0;
         ++var4.intLen;
      }

      int var9 = (int)(var1 >>> 32);
      long var10 = (long)var9 & 4294967295L;
      int var12 = (int)(var1 & 4294967295L);

      for(int var13 = 0; var13 < var6; ++var13) {
         boolean var14 = false;
         boolean var15 = false;
         boolean var16 = false;
         int var17 = var4.value[var13 + var4.offset];
         int var18 = var17 + Integer.MIN_VALUE;
         int var19 = var4.value[var13 + 1 + var4.offset];
         long var20;
         long var22;
         int var26;
         int var27;
         if (var17 == var9) {
            var26 = -1;
            var27 = var17 + var19;
            var16 = var27 + Integer.MIN_VALUE < var18;
         } else {
            var20 = (long)var17 << 32 | (long)var19 & 4294967295L;
            if (var20 >= 0L) {
               var26 = (int)(var20 / var10);
               var27 = (int)(var20 - (long)var26 * var10);
            } else {
               var22 = divWord(var20, var9);
               var26 = (int)(var22 & 4294967295L);
               var27 = (int)(var22 >>> 32);
            }
         }

         if (var26 != 0) {
            if (!var16) {
               var20 = (long)var4.value[var13 + 2 + var4.offset] & 4294967295L;
               var22 = ((long)var27 & 4294967295L) << 32 | var20;
               long var24 = ((long)var12 & 4294967295L) * ((long)var26 & 4294967295L);
               if (this.unsignedLongCompare(var24, var22)) {
                  --var26;
                  var27 = (int)(((long)var27 & 4294967295L) + var10);
                  if (((long)var27 & 4294967295L) >= var10) {
                     var24 -= (long)var12 & 4294967295L;
                     var22 = ((long)var27 & 4294967295L) << 32 | var20;
                     if (this.unsignedLongCompare(var24, var22)) {
                        --var26;
                     }
                  }
               }
            }

            var4.value[var13 + var4.offset] = 0;
            int var28 = this.mulsubLong(var4.value, var9, var12, var26, var13 + var4.offset);
            if (var28 + Integer.MIN_VALUE > var18) {
               this.divaddLong(var9, var12, var4.value, var13 + 1 + var4.offset);
               --var26;
            }

            var7[var13] = var26;
         }
      }

      if (var8 > 0) {
         var4.rightShift(var8);
      }

      var3.normalize();
      var4.normalize();
      return var4;
   }

   private int divaddLong(int var1, int var2, int[] var3, int var4) {
      long var5 = 0L;
      long var7 = ((long)var2 & 4294967295L) + ((long)var3[1 + var4] & 4294967295L);
      var3[1 + var4] = (int)var7;
      var7 = ((long)var1 & 4294967295L) + ((long)var3[var4] & 4294967295L) + var5;
      var3[var4] = (int)var7;
      var5 = var7 >>> 32;
      return (int)var5;
   }

   private int mulsubLong(int[] var1, int var2, int var3, int var4, int var5) {
      long var6 = (long)var4 & 4294967295L;
      var5 += 2;
      long var8 = ((long)var3 & 4294967295L) * var6;
      long var10 = (long)var1[var5] - var8;
      var1[var5--] = (int)var10;
      long var12 = (var8 >>> 32) + (long)((var10 & 4294967295L) > ((long)(~((int)var8)) & 4294967295L) ? 1 : 0);
      var8 = ((long)var2 & 4294967295L) * var6 + var12;
      var10 = (long)var1[var5] - var8;
      var1[var5--] = (int)var10;
      var12 = (var8 >>> 32) + (long)((var10 & 4294967295L) > ((long)(~((int)var8)) & 4294967295L) ? 1 : 0);
      return (int)var12;
   }

   private boolean unsignedLongCompare(long var1, long var3) {
      return var1 + Long.MIN_VALUE > var3 + Long.MIN_VALUE;
   }

   static long divWord(long var0, int var2) {
      long var3 = (long)var2 & 4294967295L;
      long var5;
      long var7;
      if (var3 == 1L) {
         var7 = (long)((int)var0);
         var5 = 0L;
         return var5 << 32 | var7 & 4294967295L;
      } else {
         var7 = (var0 >>> 1) / (var3 >>> 1);

         for(var5 = var0 - var7 * var3; var5 < 0L; --var7) {
            var5 += var3;
         }

         while(var5 >= var3) {
            var5 -= var3;
            ++var7;
         }

         return var5 << 32 | var7 & 4294967295L;
      }
   }

   MutableBigInteger hybridGCD(MutableBigInteger var1) {
      MutableBigInteger var2 = this;

      MutableBigInteger var4;
      for(MutableBigInteger var3 = new MutableBigInteger(); var1.intLen != 0; var1 = var4) {
         if (Math.abs(var2.intLen - var1.intLen) < 2) {
            return var2.binaryGCD(var1);
         }

         var4 = var2.divide(var1, var3);
         var2 = var1;
      }

      return var2;
   }

   private MutableBigInteger binaryGCD(MutableBigInteger var1) {
      MutableBigInteger var2 = this;
      MutableBigInteger var3 = new MutableBigInteger();
      int var4 = this.getLowestSetBit();
      int var5 = var1.getLowestSetBit();
      int var6 = var4 < var5 ? var4 : var5;
      if (var6 != 0) {
         this.rightShift(var6);
         var1.rightShift(var6);
      }

      boolean var7 = var6 == var4;
      MutableBigInteger var8 = var7 ? var1 : this;

      int var10;
      for(int var9 = var7 ? -1 : 1; (var10 = var8.getLowestSetBit()) >= 0; var8 = var9 >= 0 ? var2 : var1) {
         var8.rightShift(var10);
         if (var9 > 0) {
            var2 = var8;
         } else {
            var1 = var8;
         }

         if (var2.intLen < 2 && var1.intLen < 2) {
            int var11 = var2.value[var2.offset];
            int var12 = var1.value[var1.offset];
            var11 = binaryGcd(var11, var12);
            var3.value[0] = var11;
            var3.intLen = 1;
            var3.offset = 0;
            if (var6 > 0) {
               var3.leftShift(var6);
            }

            return var3;
         }

         if ((var9 = var2.difference(var1)) == 0) {
            break;
         }
      }

      if (var6 > 0) {
         var2.leftShift(var6);
      }

      return var2;
   }

   static int binaryGcd(int var0, int var1) {
      if (var1 == 0) {
         return var0;
      } else if (var0 == 0) {
         return var1;
      } else {
         int var2 = Integer.numberOfTrailingZeros(var0);
         int var3 = Integer.numberOfTrailingZeros(var1);
         var0 >>>= var2;
         var1 >>>= var3;
         int var4 = var2 < var3 ? var2 : var3;

         while(var0 != var1) {
            if (var0 + Integer.MIN_VALUE > var1 + Integer.MIN_VALUE) {
               var0 -= var1;
               var0 >>>= Integer.numberOfTrailingZeros(var0);
            } else {
               var1 -= var0;
               var1 >>>= Integer.numberOfTrailingZeros(var1);
            }
         }

         return var0 << var4;
      }
   }

   MutableBigInteger mutableModInverse(MutableBigInteger var1) {
      if (var1.isOdd()) {
         return this.modInverse(var1);
      } else if (this.isEven()) {
         throw new ArithmeticException("BigInteger not invertible.");
      } else {
         int var2 = var1.getLowestSetBit();
         MutableBigInteger var3 = new MutableBigInteger(var1);
         var3.rightShift(var2);
         if (var3.isOne()) {
            return this.modInverseMP2(var2);
         } else {
            MutableBigInteger var4 = this.modInverse(var3);
            MutableBigInteger var5 = this.modInverseMP2(var2);
            MutableBigInteger var6 = modInverseBP2(var3, var2);
            MutableBigInteger var7 = var3.modInverseMP2(var2);
            MutableBigInteger var8 = new MutableBigInteger();
            MutableBigInteger var9 = new MutableBigInteger();
            MutableBigInteger var10 = new MutableBigInteger();
            var4.leftShift(var2);
            var4.multiply(var6, var10);
            var5.multiply(var3, var8);
            var8.multiply(var7, var9);
            var10.add(var9);
            return var10.divide(var1, var8);
         }
      }
   }

   MutableBigInteger modInverseMP2(int var1) {
      if (this.isEven()) {
         throw new ArithmeticException("Non-invertible. (GCD != 1)");
      } else if (var1 > 64) {
         return this.euclidModInverse(var1);
      } else {
         int var2 = inverseMod32(this.value[this.offset + this.intLen - 1]);
         if (var1 < 33) {
            var2 = var1 == 32 ? var2 : var2 & (1 << var1) - 1;
            return new MutableBigInteger(var2);
         } else {
            long var3 = (long)this.value[this.offset + this.intLen - 1] & 4294967295L;
            if (this.intLen > 1) {
               var3 |= (long)this.value[this.offset + this.intLen - 2] << 32;
            }

            long var5 = (long)var2 & 4294967295L;
            var5 *= 2L - var3 * var5;
            var5 = var1 == 64 ? var5 : var5 & (1L << var1) - 1L;
            MutableBigInteger var7 = new MutableBigInteger(new int[2]);
            var7.value[0] = (int)(var5 >>> 32);
            var7.value[1] = (int)var5;
            var7.intLen = 2;
            var7.normalize();
            return var7;
         }
      }
   }

   static int inverseMod32(int var0) {
      int var1 = var0 * (2 - var0 * var0);
      var1 *= 2 - var0 * var1;
      var1 *= 2 - var0 * var1;
      var1 *= 2 - var0 * var1;
      return var1;
   }

   static long inverseMod64(long var0) {
      long var2 = var0 * (2L - var0 * var0);
      var2 *= 2L - var0 * var2;
      var2 *= 2L - var0 * var2;
      var2 *= 2L - var0 * var2;
      var2 *= 2L - var0 * var2;

      assert var2 * var0 == 1L;

      return var2;
   }

   static MutableBigInteger modInverseBP2(MutableBigInteger var0, int var1) {
      return fixup(new MutableBigInteger(1), new MutableBigInteger(var0), var1);
   }

   private MutableBigInteger modInverse(MutableBigInteger var1) {
      MutableBigInteger var2 = new MutableBigInteger(var1);
      MutableBigInteger var3 = new MutableBigInteger(this);
      MutableBigInteger var4 = new MutableBigInteger(var2);
      SignedMutableBigInteger var5 = new SignedMutableBigInteger(1);
      SignedMutableBigInteger var6 = new SignedMutableBigInteger();
      MutableBigInteger var7 = null;
      SignedMutableBigInteger var8 = null;
      int var9 = 0;
      int var10;
      if (var3.isEven()) {
         var10 = var3.getLowestSetBit();
         var3.rightShift(var10);
         var6.leftShift(var10);
         var9 = var10;
      }

      while(!var3.isOne()) {
         if (var3.isZero()) {
            throw new ArithmeticException("BigInteger not invertible.");
         }

         if (var3.compare(var4) < 0) {
            var7 = var3;
            var3 = var4;
            var4 = var7;
            var8 = var6;
            var6 = var5;
            var5 = var8;
         }

         if (((var3.value[var3.offset + var3.intLen - 1] ^ var4.value[var4.offset + var4.intLen - 1]) & 3) == 0) {
            var3.subtract(var4);
            var5.signedSubtract(var6);
         } else {
            var3.add(var4);
            var5.signedAdd(var6);
         }

         var10 = var3.getLowestSetBit();
         var3.rightShift(var10);
         var6.leftShift(var10);
         var9 += var10;
      }

      while(var5.sign < 0) {
         var5.signedAdd(var2);
      }

      return fixup(var5, var2, var9);
   }

   static MutableBigInteger fixup(MutableBigInteger var0, MutableBigInteger var1, int var2) {
      MutableBigInteger var3 = new MutableBigInteger();
      int var4 = -inverseMod32(var1.value[var1.offset + var1.intLen - 1]);
      int var5 = 0;

      int var6;
      for(var6 = var2 >> 5; var5 < var6; ++var5) {
         int var7 = var4 * var0.value[var0.offset + var0.intLen - 1];
         var1.mul(var7, var3);
         var0.add(var3);
         --var0.intLen;
      }

      var5 = var2 & 31;
      if (var5 != 0) {
         var6 = var4 * var0.value[var0.offset + var0.intLen - 1];
         var6 &= (1 << var5) - 1;
         var1.mul(var6, var3);
         var0.add(var3);
         var0.rightShift(var5);
      }

      while(var0.compare(var1) >= 0) {
         var0.subtract(var1);
      }

      return var0;
   }

   MutableBigInteger euclidModInverse(int var1) {
      MutableBigInteger var2 = new MutableBigInteger(1);
      var2.leftShift(var1);
      MutableBigInteger var3 = new MutableBigInteger(var2);
      MutableBigInteger var4 = new MutableBigInteger(this);
      MutableBigInteger var5 = new MutableBigInteger();
      MutableBigInteger var6 = var2.divide(var4, var5);
      var2 = var6;
      MutableBigInteger var8 = new MutableBigInteger(var5);
      MutableBigInteger var9 = new MutableBigInteger(1);
      MutableBigInteger var10 = new MutableBigInteger();

      while(!var2.isOne()) {
         var6 = var4.divide(var2, var5);
         if (var6.intLen == 0) {
            throw new ArithmeticException("BigInteger not invertible.");
         }

         var4 = var6;
         if (var5.intLen == 1) {
            var8.mul(var5.value[var5.offset], var10);
         } else {
            var5.multiply(var8, var10);
         }

         MutableBigInteger var7 = var5;
         var5 = var10;
         var10 = var7;
         var9.add(var5);
         if (var6.isOne()) {
            return var9;
         }

         var6 = var2.divide(var6, var5);
         if (var6.intLen == 0) {
            throw new ArithmeticException("BigInteger not invertible.");
         }

         var2 = var6;
         if (var5.intLen == 1) {
            var9.mul(var5.value[var5.offset], var7);
         } else {
            var5.multiply(var9, var7);
         }

         var7 = var5;
         var5 = var10;
         var10 = var7;
         var8.add(var5);
      }

      var3.subtract(var8);
      return var3;
   }
}
