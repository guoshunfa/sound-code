package com.sun.media.sound;

public final class SoftLinearResampler extends SoftAbstractResampler {
   public int getPadding() {
      return 2;
   }

   public void interpolate(float[] var1, float[] var2, float var3, float[] var4, float var5, float[] var6, int[] var7, int var8) {
      float var9 = var4[0];
      float var10 = var2[0];
      int var11 = var7[0];
      float var12 = var3;
      int var13 = var8;
      int var14;
      float var15;
      float var16;
      if (var5 == 0.0F) {
         while(var10 < var12 && var11 < var13) {
            var14 = (int)var10;
            var15 = var10 - (float)var14;
            var16 = var1[var14];
            var6[var11++] = var16 + (var1[var14 + 1] - var16) * var15;
            var10 += var9;
         }
      } else {
         while(var10 < var12 && var11 < var13) {
            var14 = (int)var10;
            var15 = var10 - (float)var14;
            var16 = var1[var14];
            var6[var11++] = var16 + (var1[var14 + 1] - var16) * var15;
            var10 += var9;
            var9 += var5;
         }
      }

      var2[0] = var10;
      var7[0] = var11;
      var4[0] = var9;
   }
}
