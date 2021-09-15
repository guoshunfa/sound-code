package sun.java2d.pisces;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Arrays;
import sun.awt.geom.PathConsumer2D;
import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderingEngine;

public class PiscesRenderingEngine extends RenderingEngine {
   public Shape createStrokedShape(Shape var1, float var2, int var3, int var4, float var5, float[] var6, float var7) {
      final Path2D.Float var8 = new Path2D.Float();
      this.strokeTo(var1, (AffineTransform)null, var2, PiscesRenderingEngine.NormMode.OFF, var3, var4, var5, var6, var7, new PathConsumer2D() {
         public void moveTo(float var1, float var2) {
            var8.moveTo((double)var1, (double)var2);
         }

         public void lineTo(float var1, float var2) {
            var8.lineTo((double)var1, (double)var2);
         }

         public void closePath() {
            var8.closePath();
         }

         public void pathDone() {
         }

         public void curveTo(float var1, float var2, float var3, float var4, float var5, float var6) {
            var8.curveTo((double)var1, (double)var2, (double)var3, (double)var4, (double)var5, (double)var6);
         }

         public void quadTo(float var1, float var2, float var3, float var4) {
            var8.quadTo((double)var1, (double)var2, (double)var3, (double)var4);
         }

         public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
         }
      });
      return var8;
   }

   public void strokeTo(Shape var1, AffineTransform var2, BasicStroke var3, boolean var4, boolean var5, boolean var6, PathConsumer2D var7) {
      PiscesRenderingEngine.NormMode var8 = var5 ? (var6 ? PiscesRenderingEngine.NormMode.ON_WITH_AA : PiscesRenderingEngine.NormMode.ON_NO_AA) : PiscesRenderingEngine.NormMode.OFF;
      this.strokeTo(var1, var2, var3, var4, var8, var6, var7);
   }

   void strokeTo(Shape var1, AffineTransform var2, BasicStroke var3, boolean var4, PiscesRenderingEngine.NormMode var5, boolean var6, PathConsumer2D var7) {
      float var8;
      if (var4) {
         if (var6) {
            var8 = this.userSpaceLineWidth(var2, 0.5F);
         } else {
            var8 = this.userSpaceLineWidth(var2, 1.0F);
         }
      } else {
         var8 = var3.getLineWidth();
      }

      this.strokeTo(var1, var2, var8, var5, var3.getEndCap(), var3.getLineJoin(), var3.getMiterLimit(), var3.getDashArray(), var3.getDashPhase(), var7);
   }

   private float userSpaceLineWidth(AffineTransform var1, float var2) {
      double var3;
      if ((var1.getType() & 36) != 0) {
         var3 = Math.sqrt(var1.getDeterminant());
      } else {
         double var5 = var1.getScaleX();
         double var7 = var1.getShearX();
         double var9 = var1.getShearY();
         double var11 = var1.getScaleY();
         double var13 = var5 * var5 + var9 * var9;
         double var15 = 2.0D * (var5 * var7 + var9 * var11);
         double var17 = var7 * var7 + var11 * var11;
         double var19 = Math.sqrt(var15 * var15 + (var13 - var17) * (var13 - var17));
         double var21 = (var13 + var17 + var19) / 2.0D;
         var3 = Math.sqrt(var21);
      }

      return (float)((double)var2 / var3);
   }

   void strokeTo(Shape var1, AffineTransform var2, float var3, PiscesRenderingEngine.NormMode var4, int var5, int var6, float var7, float[] var8, float var9, PathConsumer2D var10) {
      AffineTransform var11 = null;
      AffineTransform var12 = null;
      Object var13 = null;
      if (var2 != null && !var2.isIdentity()) {
         double var14 = var2.getScaleX();
         double var16 = var2.getShearX();
         double var18 = var2.getShearY();
         double var20 = var2.getScaleY();
         double var22 = var14 * var20 - var18 * var16;
         if (Math.abs(var22) <= 2.802596928649634E-45D) {
            var10.moveTo(0.0F, 0.0F);
            var10.pathDone();
            return;
         }

         if (nearZero(var14 * var16 + var18 * var20, 2) && nearZero(var14 * var14 + var18 * var18 - (var16 * var16 + var20 * var20), 2)) {
            double var24 = Math.sqrt(var14 * var14 + var18 * var18);
            if (var8 != null) {
               var8 = Arrays.copyOf(var8, var8.length);

               for(int var26 = 0; var26 < var8.length; ++var26) {
                  var8[var26] = (float)(var24 * (double)var8[var26]);
               }

               var9 = (float)(var24 * (double)var9);
            }

            var3 = (float)(var24 * (double)var3);
            var13 = var1.getPathIterator(var2);
            if (var4 != PiscesRenderingEngine.NormMode.OFF) {
               var13 = new PiscesRenderingEngine.NormalizingPathIterator((PathIterator)var13, var4);
            }
         } else if (var4 != PiscesRenderingEngine.NormMode.OFF) {
            var11 = var2;
            PathIterator var28 = var1.getPathIterator(var2);
            var13 = new PiscesRenderingEngine.NormalizingPathIterator(var28, var4);
         } else {
            var12 = var2;
            var13 = var1.getPathIterator((AffineTransform)null);
         }
      } else {
         var13 = var1.getPathIterator((AffineTransform)null);
         if (var4 != PiscesRenderingEngine.NormMode.OFF) {
            var13 = new PiscesRenderingEngine.NormalizingPathIterator((PathIterator)var13, var4);
         }
      }

      var10 = TransformingPathConsumer2D.transformConsumer(var10, var12);
      var10 = TransformingPathConsumer2D.deltaTransformConsumer(var10, var11);
      Object var27 = new Stroker(var10, var3, var5, var6, var7);
      if (var8 != null) {
         var27 = new Dasher((PathConsumer2D)var27, var8, var9);
      }

      var10 = TransformingPathConsumer2D.inverseDeltaTransformConsumer((PathConsumer2D)var27, var11);
      pathTo((PathIterator)var13, var10);
   }

   private static boolean nearZero(double var0, int var2) {
      return Math.abs(var0) < (double)var2 * Math.ulp(var0);
   }

   static void pathTo(PathIterator var0, PathConsumer2D var1) {
      RenderingEngine.feedConsumer(var0, var1);
      var1.pathDone();
   }

   public AATileGenerator getAATileGenerator(Shape var1, AffineTransform var2, Region var3, BasicStroke var4, boolean var5, boolean var6, int[] var7) {
      PiscesRenderingEngine.NormMode var9 = var6 ? PiscesRenderingEngine.NormMode.ON_WITH_AA : PiscesRenderingEngine.NormMode.OFF;
      Renderer var8;
      if (var4 == null) {
         Object var10;
         if (var6) {
            var10 = new PiscesRenderingEngine.NormalizingPathIterator(var1.getPathIterator(var2), var9);
         } else {
            var10 = var1.getPathIterator(var2);
         }

         var8 = new Renderer(3, 3, var3.getLoX(), var3.getLoY(), var3.getWidth(), var3.getHeight(), ((PathIterator)var10).getWindingRule());
         pathTo((PathIterator)var10, var8);
      } else {
         var8 = new Renderer(3, 3, var3.getLoX(), var3.getLoY(), var3.getWidth(), var3.getHeight(), 1);
         this.strokeTo(var1, var2, var4, var5, var9, true, var8);
      }

      var8.endRendering();
      PiscesTileGenerator var11 = new PiscesTileGenerator(var8, var8.MAX_AA_ALPHA);
      var11.getBbox(var7);
      return var11;
   }

   public AATileGenerator getAATileGenerator(double var1, double var3, double var5, double var7, double var9, double var11, double var13, double var15, Region var17, int[] var18) {
      boolean var27 = var13 > 0.0D && var15 > 0.0D;
      double var19;
      double var21;
      double var23;
      double var25;
      if (var27) {
         var19 = var5 * var13;
         var21 = var7 * var13;
         var23 = var9 * var15;
         var25 = var11 * var15;
         var1 -= (var19 + var23) / 2.0D;
         var3 -= (var21 + var25) / 2.0D;
         var5 += var19;
         var7 += var21;
         var9 += var23;
         var11 += var25;
         if (var13 > 1.0D && var15 > 1.0D) {
            var27 = false;
         }
      } else {
         var25 = 0.0D;
         var23 = 0.0D;
         var21 = 0.0D;
         var19 = 0.0D;
      }

      Renderer var28 = new Renderer(3, 3, var17.getLoX(), var17.getLoY(), var17.getWidth(), var17.getHeight(), 0);
      var28.moveTo((float)var1, (float)var3);
      var28.lineTo((float)(var1 + var5), (float)(var3 + var7));
      var28.lineTo((float)(var1 + var5 + var9), (float)(var3 + var7 + var11));
      var28.lineTo((float)(var1 + var9), (float)(var3 + var11));
      var28.closePath();
      if (var27) {
         var1 += var19 + var23;
         var3 += var21 + var25;
         var5 -= 2.0D * var19;
         var7 -= 2.0D * var21;
         var9 -= 2.0D * var23;
         var11 -= 2.0D * var25;
         var28.moveTo((float)var1, (float)var3);
         var28.lineTo((float)(var1 + var5), (float)(var3 + var7));
         var28.lineTo((float)(var1 + var5 + var9), (float)(var3 + var7 + var11));
         var28.lineTo((float)(var1 + var9), (float)(var3 + var11));
         var28.closePath();
      }

      var28.pathDone();
      var28.endRendering();
      PiscesTileGenerator var29 = new PiscesTileGenerator(var28, var28.MAX_AA_ALPHA);
      var29.getBbox(var18);
      return var29;
   }

   public float getMinimumAAPenSize() {
      return 0.5F;
   }

   private static class NormalizingPathIterator implements PathIterator {
      private final PathIterator src;
      private float curx_adjust;
      private float cury_adjust;
      private float movx_adjust;
      private float movy_adjust;
      private final float lval;
      private final float rval;

      NormalizingPathIterator(PathIterator var1, PiscesRenderingEngine.NormMode var2) {
         this.src = var1;
         switch(var2) {
         case ON_NO_AA:
            this.lval = this.rval = 0.25F;
            break;
         case ON_WITH_AA:
            this.lval = 0.0F;
            this.rval = 0.5F;
            break;
         case OFF:
            throw new InternalError("A NormalizingPathIterator should not be created if no normalization is being done");
         default:
            throw new InternalError("Unrecognized normalization mode");
         }

      }

      public int currentSegment(float[] var1) {
         int var2 = this.src.currentSegment(var1);
         byte var3;
         switch(var2) {
         case 0:
         case 1:
            var3 = 0;
            break;
         case 2:
            var3 = 2;
            break;
         case 3:
            var3 = 4;
            break;
         case 4:
            this.curx_adjust = this.movx_adjust;
            this.cury_adjust = this.movy_adjust;
            return var2;
         default:
            throw new InternalError("Unrecognized curve type");
         }

         float var4 = (float)Math.floor((double)(var1[var3] + this.lval)) + this.rval - var1[var3];
         float var5 = (float)Math.floor((double)(var1[var3 + 1] + this.lval)) + this.rval - var1[var3 + 1];
         var1[var3] += var4;
         var1[var3 + 1] += var5;
         switch(var2) {
         case 0:
            this.movx_adjust = var4;
            this.movy_adjust = var5;
         case 1:
         default:
            break;
         case 2:
            var1[0] += (this.curx_adjust + var4) / 2.0F;
            var1[1] += (this.cury_adjust + var5) / 2.0F;
            break;
         case 3:
            var1[0] += this.curx_adjust;
            var1[1] += this.cury_adjust;
            var1[2] += var4;
            var1[3] += var5;
            break;
         case 4:
            throw new InternalError("This should be handled earlier.");
         }

         this.curx_adjust = var4;
         this.cury_adjust = var5;
         return var2;
      }

      public int currentSegment(double[] var1) {
         float[] var2 = new float[6];
         int var3 = this.currentSegment(var2);

         for(int var4 = 0; var4 < 6; ++var4) {
            var1[var4] = (double)var2[var4];
         }

         return var3;
      }

      public int getWindingRule() {
         return this.src.getWindingRule();
      }

      public boolean isDone() {
         return this.src.isDone();
      }

      public void next() {
         this.src.next();
      }
   }

   private static enum NormMode {
      OFF,
      ON_NO_AA,
      ON_WITH_AA;
   }
}
