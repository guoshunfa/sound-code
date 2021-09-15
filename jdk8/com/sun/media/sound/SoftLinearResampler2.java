package com.sun.media.sound;

public final class SoftLinearResampler2 extends SoftAbstractResampler {
   public int getPadding() {
      return 2;
   }

   public void interpolate(float[] var1, float[] var2, float var3, float[] var4, float var5, float[] var6, int[] var7, int var8) {
      float var9 = var4[0];
      float var10 = var2[0];
      int var11 = var7[0];
      int var13 = var8;
      if (var10 < var3 && var11 < var8) {
         int var14 = (int)(var10 * 32768.0F);
         int var15 = (int)(var3 * 32768.0F);
         int var16 = (int)(var9 * 32768.0F);
         var9 = (float)var16 * 3.0517578E-5F;
         int var17;
         int var18;
         if (var5 == 0.0F) {
            var17 = var15 - var14;
            var18 = var17 % var16;
            if (var18 != 0) {
               var17 += var16 - var18;
            }

            int var19 = var11 + var17 / var16;
            if (var19 < var8) {
               var13 = var19;
            }

            while(var11 < var13) {
               int var20 = var14 >> 15;
               float var21 = var10 - (float)var20;
               float var22 = var1[var20];
               var6[var11++] = var22 + (var1[var20 + 1] - var22) * var21;
               var14 += var16;
               var10 += var9;
            }
         } else {
            var17 = (int)(var5 * 32768.0F);

            for(var5 = (float)var17 * 3.0517578E-5F; var14 < var15 && var11 < var13; var16 += var17) {
               var18 = var14 >> 15;
               float var23 = var10 - (float)var18;
               float var24 = var1[var18];
               var6[var11++] = var24 + (var1[var18 + 1] - var24) * var23;
               var10 += var9;
               var14 += var16;
               var9 += var5;
            }
         }

         var2[0] = var10;
         var7[0] = var11;
         var4[0] = var9;
      }
   }
}
