package com.sun.media.sound;

public final class SoftPointResampler extends SoftAbstractResampler {
   public int getPadding() {
      return 100;
   }

   public void interpolate(float[] var1, float[] var2, float var3, float[] var4, float var5, float[] var6, int[] var7, int var8) {
      float var9 = var4[0];
      float var10 = var2[0];
      int var11 = var7[0];
      float var12 = var3;
      float var13 = (float)var8;
      if (var5 == 0.0F) {
         while(var10 < var12 && (float)var11 < var13) {
            var6[var11++] = var1[(int)var10];
            var10 += var9;
         }
      } else {
         while(var10 < var12 && (float)var11 < var13) {
            var6[var11++] = var1[(int)var10];
            var10 += var9;
            var9 += var5;
         }
      }

      var2[0] = var10;
      var7[0] = var11;
      var4[0] = var9;
   }
}
