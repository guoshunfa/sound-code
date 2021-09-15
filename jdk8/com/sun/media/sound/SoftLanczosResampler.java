package com.sun.media.sound;

public final class SoftLanczosResampler extends SoftAbstractResampler {
   float[][] sinc_table;
   int sinc_table_fsize = 2000;
   int sinc_table_size = 5;
   int sinc_table_center;

   public SoftLanczosResampler() {
      this.sinc_table_center = this.sinc_table_size / 2;
      this.sinc_table = new float[this.sinc_table_fsize][];

      for(int var1 = 0; var1 < this.sinc_table_fsize; ++var1) {
         this.sinc_table[var1] = sincTable(this.sinc_table_size, (float)(-var1) / (float)this.sinc_table_fsize);
      }

   }

   public static double sinc(double var0) {
      return var0 == 0.0D ? 1.0D : Math.sin(3.141592653589793D * var0) / (3.141592653589793D * var0);
   }

   public static float[] sincTable(int var0, float var1) {
      int var2 = var0 / 2;
      float[] var3 = new float[var0];

      for(int var4 = 0; var4 < var0; ++var4) {
         float var5 = (float)(-var2 + var4) + var1;
         if (var5 >= -2.0F && var5 <= 2.0F) {
            if (var5 == 0.0F) {
               var3[var4] = 1.0F;
            } else {
               var3[var4] = (float)(2.0D * Math.sin(3.141592653589793D * (double)var5) * Math.sin(3.141592653589793D * (double)var5 / 2.0D) / (3.141592653589793D * (double)var5 * 3.141592653589793D * (double)var5));
            }
         } else {
            var3[var4] = 0.0F;
         }
      }

      return var3;
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
      int var14;
      float[] var15;
      int var16;
      float var17;
      int var18;
      if (var5 == 0.0F) {
         while(var10 < var12 && var11 < var13) {
            var14 = (int)var10;
            var15 = this.sinc_table[(int)((var10 - (float)var14) * (float)this.sinc_table_fsize)];
            var16 = var14 - this.sinc_table_center;
            var17 = 0.0F;

            for(var18 = 0; var18 < this.sinc_table_size; ++var16) {
               var17 += var1[var16] * var15[var18];
               ++var18;
            }

            var6[var11++] = var17;
            var10 += var9;
         }
      } else {
         while(var10 < var12 && var11 < var13) {
            var14 = (int)var10;
            var15 = this.sinc_table[(int)((var10 - (float)var14) * (float)this.sinc_table_fsize)];
            var16 = var14 - this.sinc_table_center;
            var17 = 0.0F;

            for(var18 = 0; var18 < this.sinc_table_size; ++var16) {
               var17 += var1[var16] * var15[var18];
               ++var18;
            }

            var6[var11++] = var17;
            var10 += var9;
            var9 += var5;
         }
      }

      var2[0] = var10;
      var7[0] = var11;
      var4[0] = var9;
   }
}
