package javax.swing.colorchooser;

final class ColorModelCMYK extends ColorModel {
   ColorModelCMYK() {
      super("cmyk", "Cyan", "Magenta", "Yellow", "Black", "Alpha");
   }

   void setColor(int var1, float[] var2) {
      super.setColor(var1, var2);
      var2[4] = var2[3];
      RGBtoCMYK(var2, var2);
   }

   int getColor(float[] var1) {
      CMYKtoRGB(var1, var1);
      var1[3] = var1[4];
      return super.getColor(var1);
   }

   private static float[] CMYKtoRGB(float[] var0, float[] var1) {
      if (var1 == null) {
         var1 = new float[3];
      }

      var1[0] = 1.0F + var0[0] * var0[3] - var0[3] - var0[0];
      var1[1] = 1.0F + var0[1] * var0[3] - var0[3] - var0[1];
      var1[2] = 1.0F + var0[2] * var0[3] - var0[3] - var0[2];
      return var1;
   }

   private static float[] RGBtoCMYK(float[] var0, float[] var1) {
      if (var1 == null) {
         var1 = new float[4];
      }

      float var2 = ColorModelHSL.max(var0[0], var0[1], var0[2]);
      if (var2 > 0.0F) {
         var1[0] = 1.0F - var0[0] / var2;
         var1[1] = 1.0F - var0[1] / var2;
         var1[2] = 1.0F - var0[2] / var2;
      } else {
         var1[0] = 0.0F;
         var1[1] = 0.0F;
         var1[2] = 0.0F;
      }

      var1[3] = 1.0F - var2;
      return var1;
   }
}
