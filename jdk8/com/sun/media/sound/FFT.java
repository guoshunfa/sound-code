package com.sun.media.sound;

public final class FFT {
   private final double[] w;
   private final int fftFrameSize;
   private final int sign;
   private final int[] bitm_array;
   private final int fftFrameSize2;

   public FFT(int var1, int var2) {
      this.w = computeTwiddleFactors(var1, var2);
      this.fftFrameSize = var1;
      this.sign = var2;
      this.fftFrameSize2 = var1 << 1;
      this.bitm_array = new int[this.fftFrameSize2];

      for(int var3 = 2; var3 < this.fftFrameSize2; var3 += 2) {
         int var5 = 2;

         int var4;
         for(var4 = 0; var5 < this.fftFrameSize2; var5 <<= 1) {
            if ((var3 & var5) != 0) {
               ++var4;
            }

            var4 <<= 1;
         }

         this.bitm_array[var3] = var4;
      }

   }

   public void transform(double[] var1) {
      this.bitreversal(var1);
      calc(this.fftFrameSize, var1, this.sign, this.w);
   }

   private static final double[] computeTwiddleFactors(int var0, int var1) {
      int var2 = (int)(Math.log((double)var0) / Math.log(2.0D));
      double[] var3 = new double[(var0 - 1) * 4];
      int var4 = 0;
      int var5 = 0;

      int var6;
      int var7;
      for(var6 = 2; var5 < var2; ++var5) {
         var7 = var6;
         var6 <<= 1;
         double var8 = 1.0D;
         double var10 = 0.0D;
         double var12 = 3.141592653589793D / (double)(var7 >> 1);
         double var14 = Math.cos(var12);
         double var16 = (double)var1 * Math.sin(var12);

         for(int var18 = 0; var18 < var7; var18 += 2) {
            var3[var4++] = var8;
            var3[var4++] = var10;
            double var19 = var8;
            var8 = var8 * var14 - var10 * var16;
            var10 = var19 * var16 + var10 * var14;
         }
      }

      var4 = 0;
      var5 = var3.length >> 1;
      var6 = 0;

      for(var7 = 2; var6 < var2 - 1; ++var6) {
         int var21 = var7;
         var7 *= 2;
         int var9 = var4 + var21;

         for(int var22 = 0; var22 < var21; var22 += 2) {
            double var11 = var3[var4++];
            double var13 = var3[var4++];
            double var15 = var3[var9++];
            double var17 = var3[var9++];
            var3[var5++] = var11 * var15 - var13 * var17;
            var3[var5++] = var11 * var17 + var13 * var15;
         }
      }

      return var3;
   }

   private static final void calc(int var0, double[] var1, int var2, double[] var3) {
      int var4 = var0 << 1;
      byte var5 = 2;
      if (var5 < var4) {
         int var6 = var5 - 2;
         if (var2 == -1) {
            calcF4F(var0, var1, var6, var5, var3);
         } else {
            calcF4I(var0, var1, var6, var5, var3);
         }

      }
   }

   private static final void calcF2E(int var0, double[] var1, int var2, int var3, double[] var4) {
      int var5 = var3;

      for(int var6 = 0; var6 < var5; var6 += 2) {
         double var7 = var4[var2++];
         double var9 = var4[var2++];
         int var11 = var6 + var5;
         double var12 = var1[var11];
         double var14 = var1[var11 + 1];
         double var16 = var1[var6];
         double var18 = var1[var6 + 1];
         double var20 = var12 * var7 - var14 * var9;
         double var22 = var12 * var9 + var14 * var7;
         var1[var11] = var16 - var20;
         var1[var11 + 1] = var18 - var22;
         var1[var6] = var16 + var20;
         var1[var6 + 1] = var18 + var22;
      }

   }

   private static final void calcF4F(int var0, double[] var1, int var2, int var3, double[] var4) {
      int var5 = var0 << 1;

      int var7;
      for(int var6 = var4.length >> 1; var3 < var5; var2 += var7 << 1) {
         if (var3 << 2 == var5) {
            calcF4FE(var0, var1, var2, var3, var4);
            return;
         }

         var7 = var3;
         int var8 = var3 << 1;
         if (var8 == var5) {
            calcF2E(var0, var1, var2, var3, var4);
            return;
         }

         var3 <<= 2;
         int var9 = var2 + var7;
         int var10 = var2 + var6;
         var2 += 2;
         var9 += 2;
         var10 += 2;

         int var11;
         for(var11 = 0; var11 < var5; var11 += var3) {
            int var12 = var11 + var7;
            double var13 = var1[var12];
            double var15 = var1[var12 + 1];
            double var17 = var1[var11];
            double var19 = var1[var11 + 1];
            var11 += var8;
            var12 += var8;
            double var21 = var1[var12];
            double var23 = var1[var12 + 1];
            double var25 = var1[var11];
            double var27 = var1[var11 + 1];
            double var29 = var13;
            double var31 = var15;
            var13 = var17 - var13;
            var15 = var19 - var15;
            var17 += var29;
            var19 += var31;
            double var37 = var21;
            double var39 = var23;
            var29 = var21 - var25;
            var31 = var23 - var27;
            var21 = var13 + var31;
            var23 = var15 - var29;
            var13 -= var31;
            var15 += var29;
            var29 = var25 + var37;
            var31 = var27 + var39;
            var25 = var17 - var29;
            var27 = var19 - var31;
            var17 += var29;
            var19 += var31;
            var1[var12] = var21;
            var1[var12 + 1] = var23;
            var1[var11] = var25;
            var1[var11 + 1] = var27;
            var11 -= var8;
            var12 -= var8;
            var1[var12] = var13;
            var1[var12 + 1] = var15;
            var1[var11] = var17;
            var1[var11 + 1] = var19;
         }

         for(var11 = 2; var11 < var7; var11 += 2) {
            double var54 = var4[var2++];
            double var14 = var4[var2++];
            double var16 = var4[var9++];
            double var18 = var4[var9++];
            double var20 = var4[var10++];
            double var22 = var4[var10++];

            for(int var24 = var11; var24 < var5; var24 += var3) {
               int var55 = var24 + var7;
               double var26 = var1[var55];
               double var28 = var1[var55 + 1];
               double var30 = var1[var24];
               double var32 = var1[var24 + 1];
               var24 += var8;
               var55 += var8;
               double var34 = var1[var55];
               double var36 = var1[var55 + 1];
               double var38 = var1[var24];
               double var40 = var1[var24 + 1];
               double var42 = var26 * var54 - var28 * var14;
               double var44 = var26 * var14 + var28 * var54;
               var26 = var30 - var42;
               var28 = var32 - var44;
               var30 += var42;
               var32 += var44;
               double var46 = var38 * var16 - var40 * var18;
               double var48 = var38 * var18 + var40 * var16;
               double var50 = var34 * var20 - var36 * var22;
               double var52 = var34 * var22 + var36 * var20;
               var42 = var50 - var46;
               var44 = var52 - var48;
               var34 = var26 + var44;
               var36 = var28 - var42;
               var26 -= var44;
               var28 += var42;
               var42 = var46 + var50;
               var44 = var48 + var52;
               var38 = var30 - var42;
               var40 = var32 - var44;
               var30 += var42;
               var32 += var44;
               var1[var55] = var34;
               var1[var55 + 1] = var36;
               var1[var24] = var38;
               var1[var24 + 1] = var40;
               var24 -= var8;
               var55 -= var8;
               var1[var55] = var26;
               var1[var55 + 1] = var28;
               var1[var24] = var30;
               var1[var24 + 1] = var32;
            }
         }
      }

      calcF2E(var0, var1, var2, var3, var4);
   }

   private static final void calcF4I(int var0, double[] var1, int var2, int var3, double[] var4) {
      int var5 = var0 << 1;

      int var7;
      for(int var6 = var4.length >> 1; var3 < var5; var2 += var7 << 1) {
         if (var3 << 2 == var5) {
            calcF4IE(var0, var1, var2, var3, var4);
            return;
         }

         var7 = var3;
         int var8 = var3 << 1;
         if (var8 == var5) {
            calcF2E(var0, var1, var2, var3, var4);
            return;
         }

         var3 <<= 2;
         int var9 = var2 + var7;
         int var10 = var2 + var6;
         var2 += 2;
         var9 += 2;
         var10 += 2;

         int var11;
         for(var11 = 0; var11 < var5; var11 += var3) {
            int var12 = var11 + var7;
            double var13 = var1[var12];
            double var15 = var1[var12 + 1];
            double var17 = var1[var11];
            double var19 = var1[var11 + 1];
            var11 += var8;
            var12 += var8;
            double var21 = var1[var12];
            double var23 = var1[var12 + 1];
            double var25 = var1[var11];
            double var27 = var1[var11 + 1];
            double var29 = var13;
            double var31 = var15;
            var13 = var17 - var13;
            var15 = var19 - var15;
            var17 += var29;
            var19 += var31;
            double var37 = var21;
            double var39 = var23;
            var29 = var25 - var21;
            var31 = var27 - var23;
            var21 = var13 + var31;
            var23 = var15 - var29;
            var13 -= var31;
            var15 += var29;
            var29 = var25 + var37;
            var31 = var27 + var39;
            var25 = var17 - var29;
            var27 = var19 - var31;
            var17 += var29;
            var19 += var31;
            var1[var12] = var21;
            var1[var12 + 1] = var23;
            var1[var11] = var25;
            var1[var11 + 1] = var27;
            var11 -= var8;
            var12 -= var8;
            var1[var12] = var13;
            var1[var12 + 1] = var15;
            var1[var11] = var17;
            var1[var11 + 1] = var19;
         }

         for(var11 = 2; var11 < var7; var11 += 2) {
            double var54 = var4[var2++];
            double var14 = var4[var2++];
            double var16 = var4[var9++];
            double var18 = var4[var9++];
            double var20 = var4[var10++];
            double var22 = var4[var10++];

            for(int var24 = var11; var24 < var5; var24 += var3) {
               int var55 = var24 + var7;
               double var26 = var1[var55];
               double var28 = var1[var55 + 1];
               double var30 = var1[var24];
               double var32 = var1[var24 + 1];
               var24 += var8;
               var55 += var8;
               double var34 = var1[var55];
               double var36 = var1[var55 + 1];
               double var38 = var1[var24];
               double var40 = var1[var24 + 1];
               double var42 = var26 * var54 - var28 * var14;
               double var44 = var26 * var14 + var28 * var54;
               var26 = var30 - var42;
               var28 = var32 - var44;
               var30 += var42;
               var32 += var44;
               double var46 = var38 * var16 - var40 * var18;
               double var48 = var38 * var18 + var40 * var16;
               double var50 = var34 * var20 - var36 * var22;
               double var52 = var34 * var22 + var36 * var20;
               var42 = var46 - var50;
               var44 = var48 - var52;
               var34 = var26 + var44;
               var36 = var28 - var42;
               var26 -= var44;
               var28 += var42;
               var42 = var46 + var50;
               var44 = var48 + var52;
               var38 = var30 - var42;
               var40 = var32 - var44;
               var30 += var42;
               var32 += var44;
               var1[var55] = var34;
               var1[var55 + 1] = var36;
               var1[var24] = var38;
               var1[var24 + 1] = var40;
               var24 -= var8;
               var55 -= var8;
               var1[var55] = var26;
               var1[var55 + 1] = var28;
               var1[var24] = var30;
               var1[var24 + 1] = var32;
            }
         }
      }

      calcF2E(var0, var1, var2, var3, var4);
   }

   private static final void calcF4FE(int var0, double[] var1, int var2, int var3, double[] var4) {
      int var5 = var0 << 1;

      int var7;
      for(int var6 = var4.length >> 1; var3 < var5; var2 += var7 << 1) {
         var7 = var3;
         int var8 = var3 << 1;
         if (var8 == var5) {
            calcF2E(var0, var1, var2, var3, var4);
            return;
         }

         var3 <<= 2;
         int var9 = var2 + var7;
         int var10 = var2 + var6;

         for(int var11 = 0; var11 < var7; var11 += 2) {
            double var12 = var4[var2++];
            double var14 = var4[var2++];
            double var16 = var4[var9++];
            double var18 = var4[var9++];
            double var20 = var4[var10++];
            double var22 = var4[var10++];
            int var24 = var11 + var7;
            double var25 = var1[var24];
            double var27 = var1[var24 + 1];
            double var29 = var1[var11];
            double var31 = var1[var11 + 1];
            var11 += var8;
            var24 += var8;
            double var33 = var1[var24];
            double var35 = var1[var24 + 1];
            double var37 = var1[var11];
            double var39 = var1[var11 + 1];
            double var41 = var25 * var12 - var27 * var14;
            double var43 = var25 * var14 + var27 * var12;
            var25 = var29 - var41;
            var27 = var31 - var43;
            var29 += var41;
            var31 += var43;
            double var45 = var37 * var16 - var39 * var18;
            double var47 = var37 * var18 + var39 * var16;
            double var49 = var33 * var20 - var35 * var22;
            double var51 = var33 * var22 + var35 * var20;
            var41 = var49 - var45;
            var43 = var51 - var47;
            var33 = var25 + var43;
            var35 = var27 - var41;
            var25 -= var43;
            var27 += var41;
            var41 = var45 + var49;
            var43 = var47 + var51;
            var37 = var29 - var41;
            var39 = var31 - var43;
            var29 += var41;
            var31 += var43;
            var1[var24] = var33;
            var1[var24 + 1] = var35;
            var1[var11] = var37;
            var1[var11 + 1] = var39;
            var11 -= var8;
            var24 -= var8;
            var1[var24] = var25;
            var1[var24 + 1] = var27;
            var1[var11] = var29;
            var1[var11 + 1] = var31;
         }
      }

   }

   private static final void calcF4IE(int var0, double[] var1, int var2, int var3, double[] var4) {
      int var5 = var0 << 1;

      int var7;
      for(int var6 = var4.length >> 1; var3 < var5; var2 += var7 << 1) {
         var7 = var3;
         int var8 = var3 << 1;
         if (var8 == var5) {
            calcF2E(var0, var1, var2, var3, var4);
            return;
         }

         var3 <<= 2;
         int var9 = var2 + var7;
         int var10 = var2 + var6;

         for(int var11 = 0; var11 < var7; var11 += 2) {
            double var12 = var4[var2++];
            double var14 = var4[var2++];
            double var16 = var4[var9++];
            double var18 = var4[var9++];
            double var20 = var4[var10++];
            double var22 = var4[var10++];
            int var24 = var11 + var7;
            double var25 = var1[var24];
            double var27 = var1[var24 + 1];
            double var29 = var1[var11];
            double var31 = var1[var11 + 1];
            var11 += var8;
            var24 += var8;
            double var33 = var1[var24];
            double var35 = var1[var24 + 1];
            double var37 = var1[var11];
            double var39 = var1[var11 + 1];
            double var41 = var25 * var12 - var27 * var14;
            double var43 = var25 * var14 + var27 * var12;
            var25 = var29 - var41;
            var27 = var31 - var43;
            var29 += var41;
            var31 += var43;
            double var45 = var37 * var16 - var39 * var18;
            double var47 = var37 * var18 + var39 * var16;
            double var49 = var33 * var20 - var35 * var22;
            double var51 = var33 * var22 + var35 * var20;
            var41 = var45 - var49;
            var43 = var47 - var51;
            var33 = var25 + var43;
            var35 = var27 - var41;
            var25 -= var43;
            var27 += var41;
            var41 = var45 + var49;
            var43 = var47 + var51;
            var37 = var29 - var41;
            var39 = var31 - var43;
            var29 += var41;
            var31 += var43;
            var1[var24] = var33;
            var1[var24 + 1] = var35;
            var1[var11] = var37;
            var1[var11 + 1] = var39;
            var11 -= var8;
            var24 -= var8;
            var1[var24] = var25;
            var1[var24 + 1] = var27;
            var1[var11] = var29;
            var1[var11 + 1] = var31;
         }
      }

   }

   private final void bitreversal(double[] var1) {
      if (this.fftFrameSize >= 4) {
         int var2 = this.fftFrameSize2 - 2;

         for(int var3 = 0; var3 < this.fftFrameSize; var3 += 4) {
            int var4 = this.bitm_array[var3];
            int var5;
            int var6;
            double var7;
            double var9;
            if (var3 < var4) {
               var7 = var1[var3];
               var1[var3] = var1[var4];
               var1[var4] = var7;
               var5 = var3 + 1;
               var6 = var4 + 1;
               var9 = var1[var5];
               var1[var5] = var1[var6];
               var1[var6] = var9;
               var5 = var2 - var3;
               var6 = var2 - var4;
               var7 = var1[var5];
               var1[var5] = var1[var6];
               var1[var6] = var7;
               ++var5;
               ++var6;
               var9 = var1[var5];
               var1[var5] = var1[var6];
               var1[var6] = var9;
            }

            var5 = var4 + this.fftFrameSize;
            var6 = var3 + 2;
            var7 = var1[var6];
            var1[var6] = var1[var5];
            var1[var5] = var7;
            ++var6;
            ++var5;
            var9 = var1[var6];
            var1[var6] = var1[var5];
            var1[var5] = var9;
         }

      }
   }
}
