package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.DrawParallelogram;
import sun.java2d.loops.FillParallelogram;
import sun.java2d.loops.FillSpans;

public class LoopPipe implements PixelDrawPipe, PixelFillPipe, ParallelogramPipe, ShapeDrawPipe, LoopBasedPipe {
   static final RenderingEngine RenderEngine = RenderingEngine.getInstance();

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.transX;
      int var7 = var1.transY;
      var1.loops.drawLineLoop.DrawLine(var1, var1.getSurfaceData(), var2 + var6, var3 + var7, var4 + var6, var5 + var7);
   }

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      var1.loops.drawRectLoop.DrawRect(var1, var1.getSurfaceData(), var2 + var1.transX, var3 + var1.transY, var4, var5);
   }

   public void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      var1.shapepipe.draw(var1, new RoundRectangle2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7));
   }

   public void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      var1.shapepipe.draw(var1, new Ellipse2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      var1.shapepipe.draw(var1, new Arc2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 0));
   }

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      int[] var5 = new int[]{var4};
      var1.loops.drawPolygonsLoop.DrawPolygons(var1, var1.getSurfaceData(), var2, var3, var5, 1, var1.transX, var1.transY, false);
   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      int[] var5 = new int[]{var4};
      var1.loops.drawPolygonsLoop.DrawPolygons(var1, var1.getSurfaceData(), var2, var3, var5, 1, var1.transX, var1.transY, true);
   }

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      var1.loops.fillRectLoop.FillRect(var1, var1.getSurfaceData(), var2 + var1.transX, var3 + var1.transY, var4, var5);
   }

   public void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      var1.shapepipe.fill(var1, new RoundRectangle2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7));
   }

   public void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      var1.shapepipe.fill(var1, new Ellipse2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      var1.shapepipe.fill(var1, new Arc2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 2));
   }

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      ShapeSpanIterator var5 = getFillSSI(var1);

      try {
         var5.setOutputArea(var1.getCompClip());
         var5.appendPoly(var2, var3, var4, var1.transX, var1.transY);
         fillSpans(var1, var5);
      } finally {
         var5.dispose();
      }

   }

   public void draw(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState == 0) {
         int var4;
         int var5;
         Path2D.Float var9;
         if (var1.transformState <= 1) {
            if (var2 instanceof Path2D.Float) {
               var9 = (Path2D.Float)var2;
            } else {
               var9 = new Path2D.Float(var2);
            }

            var4 = var1.transX;
            var5 = var1.transY;
         } else {
            var9 = new Path2D.Float(var2, var1.transform);
            var4 = 0;
            var5 = 0;
         }

         var1.loops.drawPathLoop.DrawPath(var1, var1.getSurfaceData(), var4, var5, var9);
      } else if (var1.strokeState == 3) {
         this.fill(var1, var1.stroke.createStrokedShape(var2));
      } else {
         ShapeSpanIterator var3 = getStrokeSpans(var1, var2);

         try {
            fillSpans(var1, var3);
         } finally {
            var3.dispose();
         }

      }
   }

   public static ShapeSpanIterator getFillSSI(SunGraphics2D var0) {
      boolean var1 = var0.stroke instanceof BasicStroke && var0.strokeHint != 2;
      return new ShapeSpanIterator(var1);
   }

   public static ShapeSpanIterator getStrokeSpans(SunGraphics2D var0, Shape var1) {
      ShapeSpanIterator var2 = new ShapeSpanIterator(false);

      try {
         var2.setOutputArea(var0.getCompClip());
         var2.setRule(1);
         BasicStroke var3 = (BasicStroke)var0.stroke;
         boolean var4 = var0.strokeState <= 1;
         boolean var5 = var0.strokeHint != 2;
         RenderEngine.strokeTo(var1, var0.transform, var3, var4, var5, false, var2);
         return var2;
      } catch (Throwable var6) {
         var2.dispose();
         var2 = null;
         throw new InternalError("Unable to Stroke shape (" + var6.getMessage() + ")", var6);
      }
   }

   public void fill(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState == 0) {
         int var5;
         Path2D.Float var9;
         int var10;
         if (var1.transformState <= 1) {
            if (var2 instanceof Path2D.Float) {
               var9 = (Path2D.Float)var2;
            } else {
               var9 = new Path2D.Float(var2);
            }

            var10 = var1.transX;
            var5 = var1.transY;
         } else {
            var9 = new Path2D.Float(var2, var1.transform);
            var10 = 0;
            var5 = 0;
         }

         var1.loops.fillPathLoop.FillPath(var1, var1.getSurfaceData(), var10, var5, var9);
      } else {
         ShapeSpanIterator var3 = getFillSSI(var1);

         try {
            var3.setOutputArea(var1.getCompClip());
            AffineTransform var4 = var1.transformState == 0 ? null : var1.transform;
            var3.appendPath(var2.getPathIterator(var4));
            fillSpans(var1, var3);
         } finally {
            var3.dispose();
         }

      }
   }

   private static void fillSpans(SunGraphics2D var0, SpanIterator var1) {
      if (var0.clipState == 2) {
         var1 = var0.clipRegion.filter(var1);
      } else {
         FillSpans var2 = var0.loops.fillSpansLoop;
         if (var2 != null) {
            var2.FillSpans(var0, var0.getSurfaceData(), var1);
            return;
         }
      }

      int[] var8 = new int[4];
      SurfaceData var3 = var0.getSurfaceData();

      while(var1.nextSpan(var8)) {
         int var4 = var8[0];
         int var5 = var8[1];
         int var6 = var8[2] - var4;
         int var7 = var8[3] - var5;
         var0.loops.fillRectLoop.FillRect(var0, var3, var4, var5, var6, var7);
      }

   }

   public void fillParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
      FillParallelogram var22 = var1.loops.fillParallelogramLoop;
      var22.FillParallelogram(var1, var1.getSurfaceData(), var10, var12, var14, var16, var18, var20);
   }

   public void drawParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
      DrawParallelogram var26 = var1.loops.drawParallelogramLoop;
      var26.DrawParallelogram(var1, var1.getSurfaceData(), var10, var12, var14, var16, var18, var20, var22, var24);
   }
}
