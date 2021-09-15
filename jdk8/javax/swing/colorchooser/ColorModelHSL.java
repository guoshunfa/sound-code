package javax.swing.colorchooser;

final class ColorModelHSL extends ColorModel {
   ColorModelHSL() {
      super("hsl", "Hue", "Saturation", "Lightness", "Transparency");
   }

   void setColor(int var1, float[] var2) {
      super.setColor(var1, var2);
      RGBtoHSL(var2, var2);
      var2[3] = 1.0F - var2[3];
   }

   int getColor(float[] var1) {
      var1[3] = 1.0F - var1[3];
      HSLtoRGB(var1, var1);
      return super.getColor(var1);
   }

   int getMaximum(int var1) {
      return var1 == 0 ? 360 : 100;
   }

   float getDefault(int var1) {
      return var1 == 0 ? -1.0F : (var1 == 2 ? 0.5F : 1.0F);
   }

   private static float[] HSLtoRGB(float[] var0, float[] var1) {
      if (var1 == null) {
         var1 = new float[3];
      }

      float var2 = var0[0];
      float var3 = var0[1];
      float var4 = var0[2];
      if (var3 > 0.0F) {
         var2 = var2 < 1.0F ? var2 * 6.0F : 0.0F;
         float var5 = var4 + var3 * (var4 > 0.5F ? 1.0F - var4 : var4);
         float var6 = 2.0F * var4 - var5;
         var1[0] = normalize(var5, var6, var2 < 4.0F ? var2 + 2.0F : var2 - 4.0F);
         var1[1] = normalize(var5, var6, var2);
         var1[2] = normalize(var5, var6, var2 < 2.0F ? var2 + 4.0F : var2 - 2.0F);
      } else {
         var1[0] = var4;
         var1[1] = var4;
         var1[2] = var4;
      }

      return var1;
   }

   private static float[] RGBtoHSL(float[] var0, float[] var1) {
      if (var1 == null) {
         var1 = new float[3];
      }

      float var2 = max(var0[0], var0[1], var0[2]);
      float var3 = min(var0[0], var0[1], var0[2]);
      float var4 = var2 + var3;
      float var5 = var2 - var3;
      if (var5 > 0.0F) {
         var5 /= var4 > 1.0F ? 2.0F - var4 : var4;
      }

      var1[0] = getHue(var0[0], var0[1], var0[2], var2, var3);
      var1[1] = var5;
      var1[2] = var4 / 2.0F;
      return var1;
   }

   static float min(float var0, float var1, float var2) {
      float var3 = var0 < var1 ? var0 : var1;
      return var3 < var2 ? var3 : var2;
   }

   static float max(float var0, float var1, float var2) {
      float var3 = var0 > var1 ? var0 : var1;
      return var3 > var2 ? var3 : var2;
   }

   static float getHue(float var0, float var1, float var2, float var3, float var4) {
      float var5 = var3 - var4;
      if (var5 > 0.0F) {
         if (var3 == var0) {
            var5 = (var1 - var2) / var5;
            if (var5 < 0.0F) {
               var5 += 6.0F;
            }
         } else if (var3 == var1) {
            var5 = 2.0F + (var2 - var0) / var5;
         } else {
            var5 = 4.0F + (var0 - var1) / var5;
         }

         var5 /= 6.0F;
      }

      return var5;
   }

   private static float normalize(float var0, float var1, float var2) {
      if (var2 < 1.0F) {
         return var1 + (var0 - var1) * var2;
      } else if (var2 < 3.0F) {
         return var0;
      } else {
         return var2 < 4.0F ? var1 + (var0 - var1) * (4.0F - var2) : var1;
      }
   }
}
