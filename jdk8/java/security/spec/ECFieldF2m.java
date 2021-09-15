package java.security.spec;

import java.math.BigInteger;
import java.util.Arrays;

public class ECFieldF2m implements ECField {
   private int m;
   private int[] ks;
   private BigInteger rp;

   public ECFieldF2m(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("m is not positive");
      } else {
         this.m = var1;
         this.ks = null;
         this.rp = null;
      }
   }

   public ECFieldF2m(int var1, BigInteger var2) {
      this.m = var1;
      this.rp = var2;
      if (var1 <= 0) {
         throw new IllegalArgumentException("m is not positive");
      } else {
         int var3 = this.rp.bitCount();
         if (!this.rp.testBit(0) || !this.rp.testBit(var1) || var3 != 3 && var3 != 5) {
            throw new IllegalArgumentException("rp does not represent a valid reduction polynomial");
         } else {
            BigInteger var4 = this.rp.clearBit(0).clearBit(var1);
            this.ks = new int[var3 - 2];

            for(int var5 = this.ks.length - 1; var5 >= 0; --var5) {
               int var6 = var4.getLowestSetBit();
               this.ks[var5] = var6;
               var4 = var4.clearBit(var6);
            }

         }
      }
   }

   public ECFieldF2m(int var1, int[] var2) {
      this.m = var1;
      this.ks = (int[])var2.clone();
      if (var1 <= 0) {
         throw new IllegalArgumentException("m is not positive");
      } else if (this.ks.length != 1 && this.ks.length != 3) {
         throw new IllegalArgumentException("length of ks is neither 1 nor 3");
      } else {
         int var3;
         for(var3 = 0; var3 < this.ks.length; ++var3) {
            if (this.ks[var3] < 1 || this.ks[var3] > var1 - 1) {
               throw new IllegalArgumentException("ks[" + var3 + "] is out of range");
            }

            if (var3 != 0 && this.ks[var3] >= this.ks[var3 - 1]) {
               throw new IllegalArgumentException("values in ks are not in descending order");
            }
         }

         this.rp = BigInteger.ONE;
         this.rp = this.rp.setBit(var1);

         for(var3 = 0; var3 < this.ks.length; ++var3) {
            this.rp = this.rp.setBit(this.ks[var3]);
         }

      }
   }

   public int getFieldSize() {
      return this.m;
   }

   public int getM() {
      return this.m;
   }

   public BigInteger getReductionPolynomial() {
      return this.rp;
   }

   public int[] getMidTermsOfReductionPolynomial() {
      return this.ks == null ? null : (int[])this.ks.clone();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ECFieldF2m)) {
         return false;
      } else {
         return this.m == ((ECFieldF2m)var1).m && Arrays.equals(this.ks, ((ECFieldF2m)var1).ks);
      }
   }

   public int hashCode() {
      int var1 = this.m << 5;
      var1 += this.rp == null ? 0 : this.rp.hashCode();
      return var1;
   }
}
