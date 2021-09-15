package java.awt.image;

public class AreaAveragingScaleFilter extends ReplicateScaleFilter {
   private static final ColorModel rgbmodel = ColorModel.getRGBdefault();
   private static final int neededHints = 6;
   private boolean passthrough;
   private float[] reds;
   private float[] greens;
   private float[] blues;
   private float[] alphas;
   private int savedy;
   private int savedyrem;

   public AreaAveragingScaleFilter(int var1, int var2) {
      super(var1, var2);
   }

   public void setHints(int var1) {
      this.passthrough = (var1 & 6) != 6;
      super.setHints(var1);
   }

   private void makeAccumBuffers() {
      this.reds = new float[this.destWidth];
      this.greens = new float[this.destWidth];
      this.blues = new float[this.destWidth];
      this.alphas = new float[this.destWidth];
   }

   private int[] calcRow() {
      float var1 = (float)this.srcWidth * (float)this.srcHeight;
      if (this.outpixbuf == null || !(this.outpixbuf instanceof int[])) {
         this.outpixbuf = new int[this.destWidth];
      }

      int[] var2 = (int[])((int[])this.outpixbuf);

      for(int var3 = 0; var3 < this.destWidth; ++var3) {
         float var4 = var1;
         int var5 = Math.round(this.alphas[var3] / var1);
         if (var5 <= 0) {
            var5 = 0;
         } else if (var5 >= 255) {
            var5 = 255;
         } else {
            var4 = this.alphas[var3] / 255.0F;
         }

         int var6 = Math.round(this.reds[var3] / var4);
         int var7 = Math.round(this.greens[var3] / var4);
         int var8 = Math.round(this.blues[var3] / var4);
         if (var6 < 0) {
            var6 = 0;
         } else if (var6 > 255) {
            var6 = 255;
         }

         if (var7 < 0) {
            var7 = 0;
         } else if (var7 > 255) {
            var7 = 255;
         }

         if (var8 < 0) {
            var8 = 0;
         } else if (var8 > 255) {
            var8 = 255;
         }

         var2[var3] = var5 << 24 | var6 << 16 | var7 << 8 | var8;
      }

      return var2;
   }

   private void accumPixels(int var1, int var2, int var3, int var4, ColorModel var5, Object var6, int var7, int var8) {
      if (this.reds == null) {
         this.makeAccumBuffers();
      }

      int var9 = var2;
      int var10 = this.destHeight;
      int var11;
      int var12;
      if (var2 == 0) {
         var11 = 0;
         var12 = 0;
      } else {
         var11 = this.savedy;
         var12 = this.savedyrem;
      }

      while(var9 < var2 + var4) {
         int var14;
         if (var12 == 0) {
            for(var14 = 0; var14 < this.destWidth; ++var14) {
               this.alphas[var14] = this.reds[var14] = this.greens[var14] = this.blues[var14] = 0.0F;
            }

            var12 = this.srcHeight;
         }

         int var13;
         if (var10 < var12) {
            var13 = var10;
         } else {
            var13 = var12;
         }

         var14 = 0;
         int var15 = 0;
         int var16 = 0;
         int var17 = this.srcWidth;
         float var18 = 0.0F;
         float var19 = 0.0F;
         float var20 = 0.0F;
         float var21 = 0.0F;

         while(var14 < var3) {
            int var22;
            float var23;
            if (var16 == 0) {
               var16 = this.destWidth;
               if (var6 instanceof byte[]) {
                  var22 = ((byte[])((byte[])var6))[var7 + var14] & 255;
               } else {
                  var22 = ((int[])((int[])var6))[var7 + var14];
               }

               var22 = var5.getRGB(var22);
               var18 = (float)(var22 >>> 24);
               var19 = (float)(var22 >> 16 & 255);
               var20 = (float)(var22 >> 8 & 255);
               var21 = (float)(var22 & 255);
               if (var18 != 255.0F) {
                  var23 = var18 / 255.0F;
                  var19 *= var23;
                  var20 *= var23;
                  var21 *= var23;
               }
            }

            if (var16 < var17) {
               var22 = var16;
            } else {
               var22 = var17;
            }

            var23 = (float)var22 * (float)var13;
            float[] var10000 = this.alphas;
            var10000[var15] += var23 * var18;
            var10000 = this.reds;
            var10000[var15] += var23 * var19;
            var10000 = this.greens;
            var10000[var15] += var23 * var20;
            var10000 = this.blues;
            var10000[var15] += var23 * var21;
            if ((var16 -= var22) == 0) {
               ++var14;
            }

            if ((var17 -= var22) == 0) {
               ++var15;
               var17 = this.srcWidth;
            }
         }

         if ((var12 -= var13) == 0) {
            int[] var24 = this.calcRow();

            do {
               this.consumer.setPixels(0, var11, this.destWidth, 1, rgbmodel, (int[])var24, 0, this.destWidth);
               ++var11;
            } while((var10 -= var13) >= var13 && var13 == this.srcHeight);
         } else {
            var10 -= var13;
         }

         if (var10 == 0) {
            var10 = this.destHeight;
            ++var9;
            var7 += var8;
         }
      }

      this.savedyrem = var12;
      this.savedy = var11;
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      if (this.passthrough) {
         super.setPixels(var1, var2, var3, var4, var5, var6, var7, var8);
      } else {
         this.accumPixels(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      if (this.passthrough) {
         super.setPixels(var1, var2, var3, var4, var5, var6, var7, var8);
      } else {
         this.accumPixels(var1, var2, var3, var4, var5, var6, var7, var8);
      }

   }
}
