package com.sun.imageio.plugins.png;

public class RowFilter {
   private static final int abs(int var0) {
      return var0 < 0 ? -var0 : var0;
   }

   protected static int subFilter(byte[] var0, byte[] var1, int var2, int var3) {
      int var4 = 0;

      for(int var5 = var2; var5 < var3 + var2; ++var5) {
         int var6 = var0[var5] & 255;
         int var7 = var0[var5 - var2] & 255;
         int var8 = var6 - var7;
         var1[var5] = (byte)var8;
         var4 += abs(var8);
      }

      return var4;
   }

   protected static int upFilter(byte[] var0, byte[] var1, byte[] var2, int var3, int var4) {
      int var5 = 0;

      for(int var6 = var3; var6 < var4 + var3; ++var6) {
         int var7 = var0[var6] & 255;
         int var8 = var1[var6] & 255;
         int var9 = var7 - var8;
         var2[var6] = (byte)var9;
         var5 += abs(var9);
      }

      return var5;
   }

   protected final int paethPredictor(int var1, int var2, int var3) {
      int var4 = var1 + var2 - var3;
      int var5 = abs(var4 - var1);
      int var6 = abs(var4 - var2);
      int var7 = abs(var4 - var3);
      if (var5 <= var6 && var5 <= var7) {
         return var1;
      } else {
         return var6 <= var7 ? var2 : var3;
      }
   }

   public int filterRow(int var1, byte[] var2, byte[] var3, byte[][] var4, int var5, int var6) {
      if (var1 != 3) {
         System.arraycopy(var2, var6, var4[0], var6, var5);
         return 0;
      } else {
         int[] var7 = new int[5];

         int var8;
         for(var8 = 0; var8 < 5; ++var8) {
            var7[var8] = Integer.MAX_VALUE;
         }

         var8 = 0;

         int var9;
         int var10;
         for(var9 = var6; var9 < var5 + var6; ++var9) {
            var10 = var2[var9] & 255;
            var8 += var10;
         }

         var7[0] = var8;
         byte[] var17 = var4[1];
         var9 = subFilter(var2, var17, var6, var5);
         var7[1] = var9;
         var17 = var4[2];
         var9 = upFilter(var2, var3, var17, var6, var5);
         var7[2] = var9;
         var17 = var4[3];
         var9 = 0;

         int var11;
         int var12;
         int var13;
         int var14;
         for(var10 = var6; var10 < var5 + var6; ++var10) {
            var11 = var2[var10] & 255;
            var12 = var2[var10 - var6] & 255;
            var13 = var3[var10] & 255;
            var14 = var11 - (var12 + var13) / 2;
            var17[var10] = (byte)var14;
            var9 += abs(var14);
         }

         var7[3] = var9;
         var17 = var4[4];
         var9 = 0;

         for(var10 = var6; var10 < var5 + var6; ++var10) {
            var11 = var2[var10] & 255;
            var12 = var2[var10 - var6] & 255;
            var13 = var3[var10] & 255;
            var14 = var3[var10 - var6] & 255;
            int var15 = this.paethPredictor(var12, var13, var14);
            int var16 = var11 - var15;
            var17[var10] = (byte)var16;
            var9 += abs(var16);
         }

         var7[4] = var9;
         var8 = var7[0];
         var9 = 0;

         for(var10 = 1; var10 < 5; ++var10) {
            if (var7[var10] < var8) {
               var8 = var7[var10];
               var9 = var10;
            }
         }

         if (var9 == 0) {
            System.arraycopy(var2, var6, var4[0], var6, var5);
         }

         return var9;
      }
   }
}
