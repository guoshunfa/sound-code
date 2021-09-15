package com.sun.media.sound;

public final class SoftSincResampler extends SoftAbstractResampler {
   float[][][] sinc_table;
   int sinc_scale_size = 100;
   int sinc_table_fsize = 800;
   int sinc_table_size = 30;
   int sinc_table_center;

   public SoftSincResampler() {
      this.sinc_table_center = this.sinc_table_size / 2;
      this.sinc_table = new float[this.sinc_scale_size][this.sinc_table_fsize][];

      for(int var1 = 0; var1 < this.sinc_scale_size; ++var1) {
         float var2 = (float)(1.0D / (1.0D + Math.pow((double)var1, 1.1D) / 10.0D));

         for(int var3 = 0; var3 < this.sinc_table_fsize; ++var3) {
            this.sinc_table[var1][var3] = sincTable(this.sinc_table_size, (float)(-var3) / (float)this.sinc_table_fsize, var2);
         }
      }

   }

   public static double sinc(double var0) {
      return var0 == 0.0D ? 1.0D : Math.sin(3.141592653589793D * var0) / (3.141592653589793D * var0);
   }

   public static float[] wHanning(int var0, float var1) {
      float[] var2 = new float[var0];

      for(int var3 = 0; var3 < var0; ++var3) {
         var2[var3] = (float)(-0.5D * Math.cos(6.283185307179586D * (double)((float)var3 + var1) / (double)var0) + 0.5D);
      }

      return var2;
   }

   public static float[] sincTable(int var0, float var1, float var2) {
      int var3 = var0 / 2;
      float[] var4 = wHanning(var0, var1);

      for(int var5 = 0; var5 < var0; ++var5) {
         var4[var5] = (float)((double)var4[var5] * sinc((double)(((float)(-var3 + var5) + var1) * var2)) * (double)var2);
      }

      return var4;
   }

   public int getPadding() {
      return this.sinc_table_size / 2 + 2;
   }

   public void interpolate(float[] var1, float[] var2, float var3, float[] var4, float var5, float[] var6, int[] var7, int var8) {
      float var9 = var4[0];
      float var10 = var2[0];
      int var11 = var7[0];
      float var12 = var3;
      int var13 = var8;
      int var14 = this.sinc_scale_size - 1;
      int var15;
      float[] var18;
      int var19;
      float var20;
      int var21;
      if (var5 == 0.0F) {
         var15 = (int)((var9 - 1.0F) * 10.0F);
         if (var15 < 0) {
            var15 = 0;
         } else if (var15 > var14) {
            var15 = var14;
         }

         for(float[][] var22 = this.sinc_table[var15]; var10 < var12 && var11 < var13; var10 += var9) {
            int var23 = (int)var10;
            var18 = var22[(int)((var10 - (float)var23) * (float)this.sinc_table_fsize)];
            var19 = var23 - this.sinc_table_center;
            var20 = 0.0F;

            for(var21 = 0; var21 < this.sinc_table_size; ++var19) {
               var20 += var1[var19] * var18[var21];
               ++var21;
            }

            var6[var11++] = var20;
         }
      } else {
         while(var10 < var12 && var11 < var13) {
            var15 = (int)var10;
            int var16 = (int)((var9 - 1.0F) * 10.0F);
            if (var16 < 0) {
               var16 = 0;
            } else if (var16 > var14) {
               var16 = var14;
            }

            float[][] var17 = this.sinc_table[var16];
            var18 = var17[(int)((var10 - (float)var15) * (float)this.sinc_table_fsize)];
            var19 = var15 - this.sinc_table_center;
            var20 = 0.0F;

            for(var21 = 0; var21 < this.sinc_table_size; ++var19) {
               var20 += var1[var19] * var18[var21];
               ++var21;
            }

            var6[var11++] = var20;
            var10 += var9;
            var9 += var5;
         }
      }

      var2[0] = var10;
      var7[0] = var11;
      var4[0] = var9;
   }
}
