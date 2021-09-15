package java.awt.image;

public abstract class RGBImageFilter extends ImageFilter {
   protected ColorModel origmodel;
   protected ColorModel newmodel;
   protected boolean canFilterIndexColorModel;

   public void setColorModel(ColorModel var1) {
      if (this.canFilterIndexColorModel && var1 instanceof IndexColorModel) {
         IndexColorModel var2 = this.filterIndexColorModel((IndexColorModel)var1);
         this.substituteColorModel(var1, var2);
         this.consumer.setColorModel(var2);
      } else {
         this.consumer.setColorModel(ColorModel.getRGBdefault());
      }

   }

   public void substituteColorModel(ColorModel var1, ColorModel var2) {
      this.origmodel = var1;
      this.newmodel = var2;
   }

   public IndexColorModel filterIndexColorModel(IndexColorModel var1) {
      int var2 = var1.getMapSize();
      byte[] var3 = new byte[var2];
      byte[] var4 = new byte[var2];
      byte[] var5 = new byte[var2];
      byte[] var6 = new byte[var2];
      var1.getReds(var3);
      var1.getGreens(var4);
      var1.getBlues(var5);
      var1.getAlphas(var6);
      int var7 = var1.getTransparentPixel();
      boolean var8 = false;

      for(int var9 = 0; var9 < var2; ++var9) {
         int var10 = this.filterRGB(-1, -1, var1.getRGB(var9));
         var6[var9] = (byte)(var10 >> 24);
         if (var6[var9] != -1 && var9 != var7) {
            var8 = true;
         }

         var3[var9] = (byte)(var10 >> 16);
         var4[var9] = (byte)(var10 >> 8);
         var5[var9] = (byte)(var10 >> 0);
      }

      if (var8) {
         return new IndexColorModel(var1.getPixelSize(), var2, var3, var4, var5, var6);
      } else {
         return new IndexColorModel(var1.getPixelSize(), var2, var3, var4, var5, var7);
      }
   }

   public void filterRGBPixels(int var1, int var2, int var3, int var4, int[] var5, int var6, int var7) {
      int var8 = var6;

      for(int var9 = 0; var9 < var4; ++var9) {
         for(int var10 = 0; var10 < var3; ++var10) {
            var5[var8] = this.filterRGB(var1 + var10, var2 + var9, var5[var8]);
            ++var8;
         }

         var8 += var7 - var3;
      }

      this.consumer.setPixels(var1, var2, var3, var4, ColorModel.getRGBdefault(), var5, var6, var7);
   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, byte[] var6, int var7, int var8) {
      if (var5 == this.origmodel) {
         this.consumer.setPixels(var1, var2, var3, var4, this.newmodel, var6, var7, var8);
      } else {
         int[] var9 = new int[var3];
         int var10 = var7;

         for(int var11 = 0; var11 < var4; ++var11) {
            for(int var12 = 0; var12 < var3; ++var12) {
               var9[var12] = var5.getRGB(var6[var10] & 255);
               ++var10;
            }

            var10 += var8 - var3;
            this.filterRGBPixels(var1, var2 + var11, var3, 1, var9, 0, var3);
         }
      }

   }

   public void setPixels(int var1, int var2, int var3, int var4, ColorModel var5, int[] var6, int var7, int var8) {
      if (var5 == this.origmodel) {
         this.consumer.setPixels(var1, var2, var3, var4, this.newmodel, var6, var7, var8);
      } else {
         int[] var9 = new int[var3];
         int var10 = var7;

         for(int var11 = 0; var11 < var4; ++var11) {
            for(int var12 = 0; var12 < var3; ++var12) {
               var9[var12] = var5.getRGB(var6[var10]);
               ++var10;
            }

            var10 += var8 - var3;
            this.filterRGBPixels(var1, var2 + var11, var3, 1, var9, 0, var3);
         }
      }

   }

   public abstract int filterRGB(int var1, int var2, int var3);
}
