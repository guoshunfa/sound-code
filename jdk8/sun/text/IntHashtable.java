package sun.text;

public final class IntHashtable {
   private int defaultValue = 0;
   private int primeIndex;
   private static final float HIGH_WATER_FACTOR = 0.4F;
   private int highWaterMark;
   private static final float LOW_WATER_FACTOR = 0.0F;
   private int lowWaterMark;
   private int count;
   private int[] values;
   private int[] keyList;
   private static final int EMPTY = Integer.MIN_VALUE;
   private static final int DELETED = -2147483647;
   private static final int MAX_UNUSED = -2147483647;
   private static final int[] PRIMES = new int[]{17, 37, 67, 131, 257, 521, 1031, 2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, Integer.MAX_VALUE};

   public IntHashtable() {
      this.initialize(3);
   }

   public IntHashtable(int var1) {
      this.initialize(leastGreaterPrimeIndex((int)((float)var1 / 0.4F)));
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public void put(int var1, int var2) {
      if (this.count > this.highWaterMark) {
         this.rehash();
      }

      int var3 = this.find(var1);
      if (this.keyList[var3] <= -2147483647) {
         this.keyList[var3] = var1;
         ++this.count;
      }

      this.values[var3] = var2;
   }

   public int get(int var1) {
      return this.values[this.find(var1)];
   }

   public void remove(int var1) {
      int var2 = this.find(var1);
      if (this.keyList[var2] > -2147483647) {
         this.keyList[var2] = -2147483647;
         this.values[var2] = this.defaultValue;
         --this.count;
         if (this.count < this.lowWaterMark) {
            this.rehash();
         }
      }

   }

   public int getDefaultValue() {
      return this.defaultValue;
   }

   public void setDefaultValue(int var1) {
      this.defaultValue = var1;
      this.rehash();
   }

   public boolean equals(Object var1) {
      if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         IntHashtable var2 = (IntHashtable)var1;
         if (var2.size() == this.count && var2.defaultValue == this.defaultValue) {
            for(int var3 = 0; var3 < this.keyList.length; ++var3) {
               int var4 = this.keyList[var3];
               if (var4 > -2147483647 && var2.get(var4) != this.values[var3]) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = 465;
      int var2 = 1362796821;

      int var3;
      for(var3 = 0; var3 < this.keyList.length; ++var3) {
         var1 = var1 * var2 + 1;
         var1 += this.keyList[var3];
      }

      for(var3 = 0; var3 < this.values.length; ++var3) {
         var1 = var1 * var2 + 1;
         var1 += this.values[var3];
      }

      return var1;
   }

   public Object clone() throws CloneNotSupportedException {
      IntHashtable var1 = (IntHashtable)super.clone();
      this.values = (int[])this.values.clone();
      this.keyList = (int[])this.keyList.clone();
      return var1;
   }

   private void initialize(int var1) {
      if (var1 < 0) {
         var1 = 0;
      } else if (var1 >= PRIMES.length) {
         System.out.println("TOO BIG");
         var1 = PRIMES.length - 1;
      }

      this.primeIndex = var1;
      int var2 = PRIMES[var1];
      this.values = new int[var2];
      this.keyList = new int[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.keyList[var3] = Integer.MIN_VALUE;
         this.values[var3] = this.defaultValue;
      }

      this.count = 0;
      this.lowWaterMark = (int)((float)var2 * 0.0F);
      this.highWaterMark = (int)((float)var2 * 0.4F);
   }

   private void rehash() {
      int[] var1 = this.values;
      int[] var2 = this.keyList;
      int var3 = this.primeIndex;
      if (this.count > this.highWaterMark) {
         ++var3;
      } else if (this.count < this.lowWaterMark) {
         var3 -= 2;
      }

      this.initialize(var3);

      for(int var4 = var1.length - 1; var4 >= 0; --var4) {
         int var5 = var2[var4];
         if (var5 > -2147483647) {
            this.putInternal(var5, var1[var4]);
         }
      }

   }

   public void putInternal(int var1, int var2) {
      int var3 = this.find(var1);
      if (this.keyList[var3] < -2147483647) {
         this.keyList[var3] = var1;
         ++this.count;
      }

      this.values[var3] = var2;
   }

   private int find(int var1) {
      if (var1 <= -2147483647) {
         throw new IllegalArgumentException("key can't be less than 0xFFFFFFFE");
      } else {
         int var2 = -1;
         int var3 = (var1 ^ 67108864) % this.keyList.length;
         if (var3 < 0) {
            var3 = -var3;
         }

         int var4 = 0;

         do {
            int var5 = this.keyList[var3];
            if (var5 == var1) {
               return var3;
            }

            if (var5 <= -2147483647) {
               if (var5 == Integer.MIN_VALUE) {
                  if (var2 >= 0) {
                     var3 = var2;
                  }

                  return var3;
               }

               if (var2 < 0) {
                  var2 = var3;
               }
            }

            if (var4 == 0) {
               var4 = var1 % (this.keyList.length - 1);
               if (var4 < 0) {
                  var4 = -var4;
               }

               ++var4;
            }

            var3 = (var3 + var4) % this.keyList.length;
         } while(var3 != var2);

         return var3;
      }
   }

   private static int leastGreaterPrimeIndex(int var0) {
      int var1;
      for(var1 = 0; var1 < PRIMES.length && var0 >= PRIMES[var1]; ++var1) {
      }

      return var1 == 0 ? 0 : var1 - 1;
   }
}
