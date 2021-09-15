package sun.dc;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import sun.awt.geom.PathConsumer2D;
import sun.dc.path.FastPathProducer;
import sun.dc.path.PathConsumer;
import sun.dc.path.PathException;
import sun.dc.pr.PRException;
import sun.dc.pr.PathDasher;
import sun.dc.pr.PathStroker;
import sun.dc.pr.Rasterizer;
import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderingEngine;

public class DuctusRenderingEngine extends RenderingEngine {
   static final float PenUnits = 0.01F;
   static final int MinPenUnits = 100;
   static final int MinPenUnitsAA = 20;
   static final float MinPenSizeAA = 0.19999999F;
   static final float UPPER_BND = 1.7014117E38F;
   static final float LOWER_BND = -1.7014117E38F;
   private static final int[] RasterizerCaps = new int[]{30, 10, 20};
   private static final int[] RasterizerCorners = new int[]{50, 10, 40};
   private static Rasterizer theRasterizer;

   static float[] getTransformMatrix(AffineTransform var0) {
      float[] var1 = new float[4];
      double[] var2 = new double[6];
      var0.getMatrix(var2);

      for(int var3 = 0; var3 < 4; ++var3) {
         var1[var3] = (float)var2[var3];
      }

      return var1;
   }

   public Shape createStrokedShape(Shape var1, float var2, int var3, int var4, float var5, float[] var6, float var7) {
      DuctusRenderingEngine.FillAdapter var8 = new DuctusRenderingEngine.FillAdapter();
      PathStroker var9 = new PathStroker(var8);
      PathDasher var10 = null;

      try {
         var9.setPenDiameter(var2);
         var9.setPenT4((float[])null);
         var9.setCaps(RasterizerCaps[var3]);
         var9.setCorners(RasterizerCorners[var4], var5);
         Object var11;
         if (var6 != null) {
            var10 = new PathDasher(var9);
            var10.setDash(var6, var7);
            var10.setDashT4((float[])null);
            var11 = var10;
         } else {
            var11 = var9;
         }

         this.feedConsumer((PathConsumer)var11, var1.getPathIterator((AffineTransform)null));
      } finally {
         var9.dispose();
         if (var10 != null) {
            var10.dispose();
         }

      }

      return var8.getShape();
   }

   public void strokeTo(Shape var1, AffineTransform var2, BasicStroke var3, boolean var4, boolean var5, boolean var6, PathConsumer2D var7) {
      PathStroker var8 = new PathStroker(var7);
      Object var9 = var8;
      float[] var10 = null;
      if (!var4) {
         var8.setPenDiameter(var3.getLineWidth());
         if (var2 != null) {
            var10 = getTransformMatrix(var2);
         }

         var8.setPenT4(var10);
         var8.setPenFitting(0.01F, 100);
      }

      var8.setCaps(RasterizerCaps[var3.getEndCap()]);
      var8.setCorners(RasterizerCorners[var3.getLineJoin()], var3.getMiterLimit());
      float[] var11 = var3.getDashArray();
      if (var11 != null) {
         PathDasher var12 = new PathDasher(var8);
         var12.setDash(var11, var3.getDashPhase());
         if (var2 != null && var10 == null) {
            var10 = getTransformMatrix(var2);
         }

         var12.setDashT4(var10);
         var9 = var12;
      }

      boolean var17 = false;

      try {
         var17 = true;
         PathIterator var20 = var1.getPathIterator(var2);
         feedConsumer(var20, (PathConsumer)var9, var5, 0.25F);
         var17 = false;
      } catch (PathException var18) {
         throw new InternalError("Unable to Stroke shape (" + var18.getMessage() + ")", var18);
      } finally {
         if (var17) {
            while(var9 != null && var9 != var7) {
               PathConsumer var14 = ((PathConsumer)var9).getConsumer();
               ((PathConsumer)var9).dispose();
               var9 = var14;
            }

         }
      }

      while(var9 != null && var9 != var7) {
         PathConsumer var21 = ((PathConsumer)var9).getConsumer();
         ((PathConsumer)var9).dispose();
         var9 = var21;
      }

   }

   public static void feedConsumer(PathIterator var0, PathConsumer var1, boolean var2, float var3) throws PathException {
      var1.beginPath();
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      float var7 = 0.0F;
      float var8 = 0.0F;
      float[] var9 = new float[6];
      float var10 = 0.5F - var3;
      float var11 = 0.0F;

      for(float var12 = 0.0F; !var0.isDone(); var0.next()) {
         int var13 = var0.currentSegment(var9);
         if (var4) {
            var4 = false;
            if (var13 != 0) {
               var1.beginSubpath(var7, var8);
               var6 = true;
            }
         }

         if (var2) {
            byte var14;
            switch(var13) {
            case 0:
            case 1:
               var14 = 0;
               break;
            case 2:
               var14 = 2;
               break;
            case 3:
               var14 = 4;
               break;
            case 4:
            default:
               var14 = -1;
            }

            if (var14 >= 0) {
               float var15 = var9[var14];
               float var16 = var9[var14 + 1];
               float var17 = (float)Math.floor((double)(var15 + var10)) + var3;
               float var18 = (float)Math.floor((double)(var16 + var10)) + var3;
               var9[var14] = var17;
               var9[var14 + 1] = var18;
               var17 -= var15;
               var18 -= var16;
               switch(var13) {
               case 0:
               case 1:
               case 4:
               default:
                  break;
               case 2:
                  var9[0] += (var17 + var11) / 2.0F;
                  var9[1] += (var18 + var12) / 2.0F;
                  break;
               case 3:
                  var9[0] += var11;
                  var9[1] += var12;
                  var9[2] += var17;
                  var9[3] += var18;
               }

               var11 = var17;
               var12 = var18;
            }
         }

         switch(var13) {
         case 0:
            if (var9[0] < 1.7014117E38F && var9[0] > -1.7014117E38F && var9[1] < 1.7014117E38F && var9[1] > -1.7014117E38F) {
               var7 = var9[0];
               var8 = var9[1];
               var1.beginSubpath(var7, var8);
               var6 = true;
               var5 = false;
               break;
            }

            var5 = true;
            break;
         case 1:
            if (var9[0] < 1.7014117E38F && var9[0] > -1.7014117E38F && var9[1] < 1.7014117E38F && var9[1] > -1.7014117E38F) {
               if (var5) {
                  var1.beginSubpath(var9[0], var9[1]);
                  var6 = true;
                  var5 = false;
               } else {
                  var1.appendLine(var9[0], var9[1]);
               }
            }
            break;
         case 2:
            if (var9[2] >= 1.7014117E38F || var9[2] <= -1.7014117E38F || var9[3] >= 1.7014117E38F || var9[3] <= -1.7014117E38F) {
               break;
            }

            if (var5) {
               var1.beginSubpath(var9[2], var9[3]);
               var6 = true;
               var5 = false;
            } else {
               if (var9[0] < 1.7014117E38F && var9[0] > -1.7014117E38F && var9[1] < 1.7014117E38F && var9[1] > -1.7014117E38F) {
                  var1.appendQuadratic(var9[0], var9[1], var9[2], var9[3]);
                  break;
               }

               var1.appendLine(var9[2], var9[3]);
            }
            break;
         case 3:
            if (var9[4] >= 1.7014117E38F || var9[4] <= -1.7014117E38F || var9[5] >= 1.7014117E38F || var9[5] <= -1.7014117E38F) {
               break;
            }

            if (var5) {
               var1.beginSubpath(var9[4], var9[5]);
               var6 = true;
               var5 = false;
            } else {
               if (var9[0] < 1.7014117E38F && var9[0] > -1.7014117E38F && var9[1] < 1.7014117E38F && var9[1] > -1.7014117E38F && var9[2] < 1.7014117E38F && var9[2] > -1.7014117E38F && var9[3] < 1.7014117E38F && var9[3] > -1.7014117E38F) {
                  var1.appendCubic(var9[0], var9[1], var9[2], var9[3], var9[4], var9[5]);
                  break;
               }

               var1.appendLine(var9[4], var9[5]);
            }
            break;
         case 4:
            if (var6) {
               var1.closedSubpath();
               var6 = false;
               var4 = true;
            }
         }
      }

      var1.endPath();
   }

   public static synchronized Rasterizer getRasterizer() {
      Rasterizer var0 = theRasterizer;
      if (var0 == null) {
         var0 = new Rasterizer();
      } else {
         theRasterizer = null;
      }

      return var0;
   }

   public static synchronized void dropRasterizer(Rasterizer var0) {
      var0.reset();
      theRasterizer = var0;
   }

   public float getMinimumAAPenSize() {
      return 0.19999999F;
   }

   public AATileGenerator getAATileGenerator(Shape var1, AffineTransform var2, Region var3, BasicStroke var4, boolean var5, boolean var6, int[] var7) {
      Rasterizer var8 = getRasterizer();
      PathIterator var9 = var1.getPathIterator(var2);
      if (var4 != null) {
         float[] var10 = null;
         var8.setUsage(3);
         if (var5) {
            var8.setPenDiameter(0.19999999F);
         } else {
            var8.setPenDiameter(var4.getLineWidth());
            if (var2 != null) {
               var10 = getTransformMatrix(var2);
               var8.setPenT4(var10);
            }

            var8.setPenFitting(0.01F, 20);
         }

         var8.setCaps(RasterizerCaps[var4.getEndCap()]);
         var8.setCorners(RasterizerCorners[var4.getLineJoin()], var4.getMiterLimit());
         float[] var11 = var4.getDashArray();
         if (var11 != null) {
            var8.setDash(var11, var4.getDashPhase());
            if (var2 != null && var10 == null) {
               var10 = getTransformMatrix(var2);
            }

            var8.setDashT4(var10);
         }
      } else {
         var8.setUsage(var9.getWindingRule() == 0 ? 1 : 2);
      }

      var8.beginPath();
      boolean var26 = false;
      boolean var25 = false;
      boolean var12 = false;
      float var13 = 0.0F;
      float var14 = 0.0F;
      float[] var15 = new float[6];
      float var16 = 0.0F;

      for(float var17 = 0.0F; !var9.isDone(); var9.next()) {
         int var18 = var9.currentSegment(var15);
         if (var26) {
            var26 = false;
            if (var18 != 0) {
               var8.beginSubpath(var13, var14);
               var12 = true;
            }
         }

         if (var6) {
            byte var19;
            switch(var18) {
            case 0:
            case 1:
               var19 = 0;
               break;
            case 2:
               var19 = 2;
               break;
            case 3:
               var19 = 4;
               break;
            case 4:
            default:
               var19 = -1;
            }

            if (var19 >= 0) {
               float var20 = var15[var19];
               float var21 = var15[var19 + 1];
               float var22 = (float)Math.floor((double)var20) + 0.5F;
               float var23 = (float)Math.floor((double)var21) + 0.5F;
               var15[var19] = var22;
               var15[var19 + 1] = var23;
               var22 -= var20;
               var23 -= var21;
               switch(var18) {
               case 0:
               case 1:
               case 4:
               default:
                  break;
               case 2:
                  var15[0] += (var22 + var16) / 2.0F;
                  var15[1] += (var23 + var17) / 2.0F;
                  break;
               case 3:
                  var15[0] += var16;
                  var15[1] += var17;
                  var15[2] += var22;
                  var15[3] += var23;
               }

               var16 = var22;
               var17 = var23;
            }
         }

         switch(var18) {
         case 0:
            if (var15[0] < 1.7014117E38F && var15[0] > -1.7014117E38F && var15[1] < 1.7014117E38F && var15[1] > -1.7014117E38F) {
               var13 = var15[0];
               var14 = var15[1];
               var8.beginSubpath(var13, var14);
               var12 = true;
               var25 = false;
               break;
            }

            var25 = true;
            break;
         case 1:
            if (var15[0] < 1.7014117E38F && var15[0] > -1.7014117E38F && var15[1] < 1.7014117E38F && var15[1] > -1.7014117E38F) {
               if (var25) {
                  var8.beginSubpath(var15[0], var15[1]);
                  var12 = true;
                  var25 = false;
               } else {
                  var8.appendLine(var15[0], var15[1]);
               }
            }
            break;
         case 2:
            if (var15[2] >= 1.7014117E38F || var15[2] <= -1.7014117E38F || var15[3] >= 1.7014117E38F || var15[3] <= -1.7014117E38F) {
               break;
            }

            if (var25) {
               var8.beginSubpath(var15[2], var15[3]);
               var12 = true;
               var25 = false;
            } else {
               if (var15[0] < 1.7014117E38F && var15[0] > -1.7014117E38F && var15[1] < 1.7014117E38F && var15[1] > -1.7014117E38F) {
                  var8.appendQuadratic(var15[0], var15[1], var15[2], var15[3]);
                  break;
               }

               var8.appendLine(var15[2], var15[3]);
            }
            break;
         case 3:
            if (var15[4] >= 1.7014117E38F || var15[4] <= -1.7014117E38F || var15[5] >= 1.7014117E38F || var15[5] <= -1.7014117E38F) {
               break;
            }

            if (var25) {
               var8.beginSubpath(var15[4], var15[5]);
               var12 = true;
               var25 = false;
            } else {
               if (var15[0] < 1.7014117E38F && var15[0] > -1.7014117E38F && var15[1] < 1.7014117E38F && var15[1] > -1.7014117E38F && var15[2] < 1.7014117E38F && var15[2] > -1.7014117E38F && var15[3] < 1.7014117E38F && var15[3] > -1.7014117E38F) {
                  var8.appendCubic(var15[0], var15[1], var15[2], var15[3], var15[4], var15[5]);
                  break;
               }

               var8.appendLine(var15[4], var15[5]);
            }
            break;
         case 4:
            if (var12) {
               var8.closedSubpath();
               var12 = false;
               var26 = true;
            }
         }
      }

      try {
         var8.endPath();
         var8.getAlphaBox(var7);
         var3.clipBoxToBounds(var7);
         if (var7[0] >= var7[2] || var7[1] >= var7[3]) {
            dropRasterizer(var8);
            return null;
         }

         var8.setOutputArea((float)var7[0], (float)var7[1], var7[2] - var7[0], var7[3] - var7[1]);
      } catch (PRException var24) {
         System.err.println("DuctusRenderingEngine.getAATileGenerator: " + var24);
      }

      return var8;
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

      Rasterizer var28 = getRasterizer();
      var28.setUsage(1);
      var28.beginPath();
      var28.beginSubpath((float)var1, (float)var3);
      var28.appendLine((float)(var1 + var5), (float)(var3 + var7));
      var28.appendLine((float)(var1 + var5 + var9), (float)(var3 + var7 + var11));
      var28.appendLine((float)(var1 + var9), (float)(var3 + var11));
      var28.closedSubpath();
      if (var27) {
         var1 += var19 + var23;
         var3 += var21 + var25;
         var5 -= 2.0D * var19;
         var7 -= 2.0D * var21;
         var9 -= 2.0D * var23;
         var11 -= 2.0D * var25;
         var28.beginSubpath((float)var1, (float)var3);
         var28.appendLine((float)(var1 + var5), (float)(var3 + var7));
         var28.appendLine((float)(var1 + var5 + var9), (float)(var3 + var7 + var11));
         var28.appendLine((float)(var1 + var9), (float)(var3 + var11));
         var28.closedSubpath();
      }

      try {
         var28.endPath();
         var28.getAlphaBox(var18);
         var17.clipBoxToBounds(var18);
         if (var18[0] >= var18[2] || var18[1] >= var18[3]) {
            dropRasterizer(var28);
            return null;
         }

         var28.setOutputArea((float)var18[0], (float)var18[1], var18[2] - var18[0], var18[3] - var18[1]);
      } catch (PRException var30) {
         System.err.println("DuctusRenderingEngine.getAATileGenerator: " + var30);
      }

      return var28;
   }

   private void feedConsumer(PathConsumer var1, PathIterator var2) {
      try {
         var1.beginPath();
         boolean var3 = false;
         float var4 = 0.0F;
         float var5 = 0.0F;

         for(float[] var6 = new float[6]; !var2.isDone(); var2.next()) {
            int var7 = var2.currentSegment(var6);
            if (var3) {
               var3 = false;
               if (var7 != 0) {
                  var1.beginSubpath(var4, var5);
               }
            }

            switch(var7) {
            case 0:
               var4 = var6[0];
               var5 = var6[1];
               var1.beginSubpath(var6[0], var6[1]);
               break;
            case 1:
               var1.appendLine(var6[0], var6[1]);
               break;
            case 2:
               var1.appendQuadratic(var6[0], var6[1], var6[2], var6[3]);
               break;
            case 3:
               var1.appendCubic(var6[0], var6[1], var6[2], var6[3], var6[4], var6[5]);
               break;
            case 4:
               var1.closedSubpath();
               var3 = true;
            }
         }

         var1.endPath();
      } catch (PathException var8) {
         throw new InternalError("Unable to Stroke shape (" + var8.getMessage() + ")", var8);
      }
   }

   private class FillAdapter implements PathConsumer {
      boolean closed;
      Path2D.Float path = new Path2D.Float(1);

      public FillAdapter() {
      }

      public Shape getShape() {
         return this.path;
      }

      public void dispose() {
      }

      public PathConsumer getConsumer() {
         return null;
      }

      public void beginPath() {
      }

      public void beginSubpath(float var1, float var2) {
         if (this.closed) {
            this.path.closePath();
            this.closed = false;
         }

         this.path.moveTo(var1, var2);
      }

      public void appendLine(float var1, float var2) {
         this.path.lineTo(var1, var2);
      }

      public void appendQuadratic(float var1, float var2, float var3, float var4) {
         this.path.quadTo(var1, var2, var3, var4);
      }

      public void appendCubic(float var1, float var2, float var3, float var4, float var5, float var6) {
         this.path.curveTo(var1, var2, var3, var4, var5, var6);
      }

      public void closedSubpath() {
         this.closed = true;
      }

      public void endPath() {
         if (this.closed) {
            this.path.closePath();
            this.closed = false;
         }

      }

      public void useProxy(FastPathProducer var1) throws PathException {
         var1.sendTo(this);
      }

      public long getCPathConsumer() {
         return 0L;
      }
   }
}
