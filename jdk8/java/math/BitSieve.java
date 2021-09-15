package java.math;

import java.util.Random;

class BitSieve {
   private long[] bits;
   private int length;
   private static BitSieve smallSieve = new BitSieve();

   private BitSieve() {
      this.length = 9600;
      this.bits = new long[unitIndex(this.length - 1) + 1];
      this.set(0);
      int var1 = 1;
      int var2 = 3;

      do {
         this.sieveSingle(this.length, var1 + var2, var2);
         var1 = this.sieveSearch(this.length, var1 + 1);
         var2 = 2 * var1 + 1;
      } while(var1 > 0 && var2 < this.length);

   }

   BitSieve(BigInteger var1, int var2) {
      this.bits = new long[unitIndex(var2 - 1) + 1];
      this.length = var2;
      byte var3 = 0;
      int var4 = smallSieve.sieveSearch(smallSieve.length, var3);
      int var5 = var4 * 2 + 1;
      MutableBigInteger var6 = new MutableBigInteger(var1);
      MutableBigInteger var7 = new MutableBigInteger();

      do {
         int var8 = var6.divideOneWord(var5, var7);
         var8 = var5 - var8;
         if (var8 % 2 == 0) {
            var8 += var5;
         }

         this.sieveSingle(var2, (var8 - 1) / 2, var5);
         var4 = smallSieve.sieveSearch(smallSieve.length, var4 + 1);
         var5 = var4 * 2 + 1;
      } while(var4 > 0);

   }

   private static int unitIndex(int var0) {
      return var0 >>> 6;
   }

   private static long bit(int var0) {
      return 1L << (var0 & 63);
   }

   private boolean get(int var1) {
      int var2 = unitIndex(var1);
      return (this.bits[var2] & bit(var1)) != 0L;
   }

   private void set(int var1) {
      int var2 = unitIndex(var1);
      long[] var10000 = this.bits;
      var10000[var2] |= bit(var1);
   }

   private int sieveSearch(int var1, int var2) {
      if (var2 >= var1) {
         return -1;
      } else {
         int var3 = var2;

         while(this.get(var3)) {
            ++var3;
            if (var3 >= var1 - 1) {
               return -1;
            }
         }

         return var3;
      }
   }

   private void sieveSingle(int var1, int var2, int var3) {
      while(var2 < var1) {
         this.set(var2);
         var2 += var3;
      }

   }

   BigInteger retrieve(BigInteger var1, int var2, Random var3) {
      int var4 = 1;

      for(int var5 = 0; var5 < this.bits.length; ++var5) {
         long var6 = ~this.bits[var5];

         for(int var8 = 0; var8 < 64; ++var8) {
            if ((var6 & 1L) == 1L) {
               BigInteger var9 = var1.add(BigInteger.valueOf((long)var4));
               if (var9.primeToCertainty(var2, var3)) {
                  return var9;
               }
            }

            var6 >>>= 1;
            var4 += 2;
         }
      }

      return null;
   }
}
