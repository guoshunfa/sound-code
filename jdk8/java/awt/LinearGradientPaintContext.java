package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

final class LinearGradientPaintContext extends MultipleGradientPaintContext {
   private float dgdX;
   private float dgdY;
   private float gc;

   LinearGradientPaintContext(LinearGradientPaint var1, ColorModel var2, Rectangle var3, Rectangle2D var4, AffineTransform var5, RenderingHints var6, Point2D var7, Point2D var8, float[] var9, Color[] var10, MultipleGradientPaint.CycleMethod var11, MultipleGradientPaint.ColorSpaceType var12) {
      super(var1, var2, var3, var4, var5, var6, var9, var10, var11, var12);
      float var13 = (float)var7.getX();
      float var14 = (float)var7.getY();
      float var15 = (float)var8.getX();
      float var16 = (float)var8.getY();
      float var17 = var15 - var13;
      float var18 = var16 - var14;
      float var19 = var17 * var17 + var18 * var18;
      float var20 = var17 / var19;
      float var21 = var18 / var19;
      this.dgdX = this.a00 * var20 + this.a10 * var21;
      this.dgdY = this.a01 * var20 + this.a11 * var21;
      this.gc = (this.a02 - var13) * var20 + (this.a12 - var14) * var21;
   }

   protected void fillRaster(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      float var8 = 0.0F;
      int var9 = var2 + var6;
      float var10 = this.dgdX * (float)var4 + this.gc;

      for(int var11 = 0; var11 < var7; ++var11) {
         for(var8 = var10 + this.dgdY * (float)(var5 + var11); var2 < var9; var8 += this.dgdX) {
            var1[var2++] = this.indexIntoGradientsArrays(var8);
         }

         var2 += var3;
         var9 = var2 + var6;
      }

   }
}
