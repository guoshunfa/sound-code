package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import sun.java2d.SunGraphics2D;

public class PixelToParallelogramConverter extends PixelToShapeConverter implements ShapeDrawPipe {
   ParallelogramPipe outrenderer;
   double minPenSize;
   double normPosition;
   double normRoundingBias;
   boolean adjustfill;

   public PixelToParallelogramConverter(ShapeDrawPipe var1, ParallelogramPipe var2, double var3, double var5, boolean var7) {
      super(var1);
      this.outrenderer = var2;
      this.minPenSize = var3;
      this.normPosition = var5;
      this.normRoundingBias = 0.5D - var5;
      this.adjustfill = var7;
   }

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      if (!this.drawGeneralLine(var1, (double)var2, (double)var3, (double)var4, (double)var5)) {
         super.drawLine(var1, var2, var3, var4, var5);
      }

   }

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      if (var4 >= 0 && var5 >= 0) {
         if (var1.strokeState < 3) {
            BasicStroke var6 = (BasicStroke)var1.stroke;
            if (var4 <= 0 || var5 <= 0) {
               this.drawLine(var1, var2, var3, var2 + var4, var3 + var5);
               return;
            }

            if (var6.getLineJoin() == 0 && var6.getDashArray() == null) {
               double var7 = (double)var6.getLineWidth();
               this.drawRectangle(var1, (double)var2, (double)var3, (double)var4, (double)var5, var7);
               return;
            }
         }

         super.drawRect(var1, var2, var3, var4, var5);
      }

   }

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      if (var4 > 0 && var5 > 0) {
         this.fillRectangle(var1, (double)var2, (double)var3, (double)var4, (double)var5);
      }

   }

   public void draw(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState < 3) {
         BasicStroke var3 = (BasicStroke)var1.stroke;
         if (var2 instanceof Rectangle2D) {
            if (var3.getLineJoin() == 0 && var3.getDashArray() == null) {
               Rectangle2D var4 = (Rectangle2D)var2;
               double var5 = var4.getWidth();
               double var7 = var4.getHeight();
               double var9 = var4.getX();
               double var11 = var4.getY();
               if (var5 >= 0.0D && var7 >= 0.0D) {
                  double var13 = (double)var3.getLineWidth();
                  this.drawRectangle(var1, var9, var11, var5, var7, var13);
               }

               return;
            }
         } else if (var2 instanceof Line2D) {
            Line2D var15 = (Line2D)var2;
            if (this.drawGeneralLine(var1, var15.getX1(), var15.getY1(), var15.getX2(), var15.getY2())) {
               return;
            }
         }
      }

      this.outpipe.draw(var1, var2);
   }

   public void fill(SunGraphics2D var1, Shape var2) {
      if (var2 instanceof Rectangle2D) {
         Rectangle2D var3 = (Rectangle2D)var2;
         double var4 = var3.getWidth();
         double var6 = var3.getHeight();
         if (var4 > 0.0D && var6 > 0.0D) {
            double var8 = var3.getX();
            double var10 = var3.getY();
            this.fillRectangle(var1, var8, var10, var4, var6);
         }

      } else {
         this.outpipe.fill(var1, var2);
      }
   }

   static double len(double var0, double var2) {
      return var0 == 0.0D ? Math.abs(var2) : (var2 == 0.0D ? Math.abs(var0) : Math.sqrt(var0 * var0 + var2 * var2));
   }

   double normalize(double var1) {
      return Math.floor(var1 + this.normRoundingBias) + this.normPosition;
   }

   public boolean drawGeneralLine(SunGraphics2D var1, double var2, double var4, double var6, double var8) {
      if (var1.strokeState != 3 && var1.strokeState != 1) {
         BasicStroke var10 = (BasicStroke)var1.stroke;
         int var11 = var10.getEndCap();
         if (var11 != 1 && var10.getDashArray() == null) {
            double var12 = (double)var10.getLineWidth();
            double var14 = var6 - var2;
            double var16 = var8 - var4;
            double var18;
            double var20;
            double var22;
            double var24;
            double var28;
            double var36;
            switch(var1.transformState) {
            case 0:
               var18 = var2;
               var20 = var4;
               var22 = var6;
               var24 = var8;
               break;
            case 1:
            case 2:
               var36 = var1.transform.getTranslateX();
               var28 = var1.transform.getTranslateY();
               var18 = var2 + var36;
               var20 = var4 + var28;
               var22 = var6 + var36;
               var24 = var8 + var28;
               break;
            case 3:
            case 4:
               double[] var26 = new double[]{var2, var4, var6, var8};
               var1.transform.transform((double[])var26, 0, (double[])var26, 0, 2);
               var18 = var26[0];
               var20 = var26[1];
               var22 = var26[2];
               var24 = var26[3];
               break;
            default:
               throw new InternalError("unknown TRANSFORM state...");
            }

            if (var1.strokeHint != 2) {
               if (var1.strokeState == 0 && this.outrenderer instanceof PixelDrawPipe) {
                  int var37 = (int)Math.floor(var18 - (double)var1.transX);
                  int var27 = (int)Math.floor(var20 - (double)var1.transY);
                  int var39 = (int)Math.floor(var22 - (double)var1.transX);
                  int var29 = (int)Math.floor(var24 - (double)var1.transY);
                  ((PixelDrawPipe)this.outrenderer).drawLine(var1, var37, var27, var39, var29);
                  return true;
               }

               var18 = this.normalize(var18);
               var20 = this.normalize(var20);
               var22 = this.normalize(var22);
               var24 = this.normalize(var24);
            }

            if (var1.transformState >= 3) {
               var36 = len(var14, var16);
               if (var36 == 0.0D) {
                  var36 = 1.0D;
                  var14 = 1.0D;
               }

               double[] var38 = new double[]{var16 / var36, -var14 / var36};
               var1.transform.deltaTransform(var38, 0, var38, 0, 1);
               var12 *= len(var38[0], var38[1]);
            }

            var12 = Math.max(var12, this.minPenSize);
            var14 = var22 - var18;
            var16 = var24 - var20;
            var36 = len(var14, var16);
            double var30;
            if (var36 == 0.0D) {
               if (var11 == 0) {
                  return true;
               }

               var28 = var12;
               var30 = 0.0D;
            } else {
               var28 = var12 * var14 / var36;
               var30 = var12 * var16 / var36;
            }

            double var32 = var18 + var30 / 2.0D;
            double var34 = var20 - var28 / 2.0D;
            if (var11 == 2) {
               var32 -= var28 / 2.0D;
               var34 -= var30 / 2.0D;
               var14 += var28;
               var16 += var30;
            }

            this.outrenderer.fillParallelogram(var1, var2, var4, var6, var8, var32, var34, -var30, var28, var14, var16);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void fillRectangle(SunGraphics2D var1, double var2, double var4, double var6, double var8) {
      AffineTransform var22 = var1.transform;
      double var14 = var22.getScaleX();
      double var16 = var22.getShearY();
      double var18 = var22.getShearX();
      double var20 = var22.getScaleY();
      double var10 = var2 * var14 + var4 * var18 + var22.getTranslateX();
      double var12 = var2 * var16 + var4 * var20 + var22.getTranslateY();
      var14 *= var6;
      var16 *= var6;
      var18 *= var8;
      var20 *= var8;
      if (this.adjustfill && var1.strokeState < 3 && var1.strokeHint != 2) {
         double var23 = this.normalize(var10);
         double var25 = this.normalize(var12);
         var14 = this.normalize(var10 + var14) - var23;
         var16 = this.normalize(var12 + var16) - var25;
         var18 = this.normalize(var10 + var18) - var23;
         var20 = this.normalize(var12 + var20) - var25;
         var10 = var23;
         var12 = var25;
      }

      this.outrenderer.fillParallelogram(var1, var2, var4, var2 + var6, var4 + var8, var10, var12, var14, var16, var18, var20);
   }

   public void drawRectangle(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10) {
      AffineTransform var28 = var1.transform;
      double var16 = var28.getScaleX();
      double var18 = var28.getShearY();
      double var20 = var28.getShearX();
      double var22 = var28.getScaleY();
      double var12 = var2 * var16 + var4 * var20 + var28.getTranslateX();
      double var14 = var2 * var18 + var4 * var22 + var28.getTranslateY();
      double var24 = len(var16, var18) * var10;
      double var26 = len(var20, var22) * var10;
      var16 *= var6;
      var18 *= var6;
      var20 *= var8;
      var22 *= var8;
      double var29;
      double var31;
      if (var1.strokeState < 3 && var1.strokeHint != 2) {
         var29 = this.normalize(var12);
         var31 = this.normalize(var14);
         var16 = this.normalize(var12 + var16) - var29;
         var18 = this.normalize(var14 + var18) - var31;
         var20 = this.normalize(var12 + var20) - var29;
         var22 = this.normalize(var14 + var22) - var31;
         var12 = var29;
         var14 = var31;
      }

      var24 = Math.max(var24, this.minPenSize);
      var26 = Math.max(var26, this.minPenSize);
      var29 = len(var16, var18);
      var31 = len(var20, var22);
      if (var24 < var29 && var26 < var31) {
         this.outrenderer.drawParallelogram(var1, var2, var4, var2 + var6, var4 + var8, var12, var14, var16, var18, var20, var22, var24 / var29, var26 / var31);
      } else {
         this.fillOuterParallelogram(var1, var2, var4, var2 + var6, var4 + var8, var12, var14, var16, var18, var20, var22, var29, var31, var24, var26);
      }

   }

   public void fillOuterParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24, double var26, double var28) {
      double var30 = var14 / var22;
      double var32 = var16 / var22;
      double var34 = var18 / var24;
      double var36 = var20 / var24;
      if (var22 == 0.0D) {
         if (var24 == 0.0D) {
            var34 = 0.0D;
            var36 = 1.0D;
         }

         var30 = var36;
         var32 = -var34;
      } else if (var24 == 0.0D) {
         var34 = var32;
         var36 = -var30;
      }

      var30 *= var26;
      var32 *= var26;
      var34 *= var28;
      var36 *= var28;
      var10 -= (var30 + var34) / 2.0D;
      var12 -= (var32 + var36) / 2.0D;
      var14 += var30;
      var16 += var32;
      var18 += var34;
      var20 += var36;
      this.outrenderer.fillParallelogram(var1, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20);
   }
}
