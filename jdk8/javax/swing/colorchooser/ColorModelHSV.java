package javax.swing.colorchooser;

final class ColorModelHSV extends ColorModel {
   ColorModelHSV() {
      super("hsv", "Hue", "Saturation", "Value", "Transparency");
   }

   void setColor(int var1, float[] var2) {
      super.setColor(var1, var2);
      RGBtoHSV(var2, var2);
      var2[3] = 1.0F - var2[3];
   }

   int getColor(float[] var1) {
      var1[3] = 1.0F - var1[3];
      HSVtoRGB(var1, var1);
      return super.getColor(var1);
   }

   int getMaximum(int var1) {
      return var1 == 0 ? 360 : 100;
   }

   float getDefault(int var1) {
      return var1 == 0 ? -1.0F : 1.0F;
   }

   private static float[] HSVtoRGB(float[] var0, float[] var1) {
      if (var1 == null) {
         var1 = new float[3];
      }

      float var2 = var0[0];
      float var3 = var0[1];
      float var4 = var0[2];
      var1[0] = var4;
      var1[1] = var4;
      var1[2] = var4;
      if (var3 > 0.0F) {
         var2 = var2 < 1.0F ? var2 * 6.0F : 0.0F;
         int var5 = (int)var2;
         float var6 = var2 - (float)var5;
         switch(var5) {
         case 0:
            var1[1] *= 1.0F - var3 * (1.0F - var6);
            var1[2] *= 1.0F - var3;
            break;
         case 1:
            var1[0] *= 1.0F - var3 * var6;
            var1[2] *= 1.0F - var3;
            break;
         case 2:
            var1[0] *= 1.0F - var3;
            var1[2] *= 1.0F - var3 * (1.0F - var6);
            break;
         case 3:
            var1[0] *= 1.0F - var3;
            var1[1] *= 1.0F - var3 * var6;
            break;
         case 4:
            var1[0] *= 1.0F - var3 * (1.0F - var6);
            var1[1] *= 1.0F - var3;
            break;
         case 5:
            var1[1] *= 1.0F - var3;
            var1[2] *= 1.0F - var3 * var6;
         }
      }

      return var1;
   }

   private static float[] RGBtoHSV(float[] var0, float[] var1) {
      if (var1 == null) {
         var1 = new float[3];
      }

      float var2 = ColorModelHSL.max(var0[0], var0[1], var0[2]);
      float var3 = ColorModelHSL.min(var0[0], var0[1], var0[2]);
      float var4 = var2 - var3;
      if (var4 > 0.0F) {
         var4 /= var2;
      }

      var1[0] = ColorModelHSL.getHue(var0[0], var0[1], var0[2], var2, var3);
      var1[1] = var4;
      var1[2] = var2;
      return var1;
   }
}
