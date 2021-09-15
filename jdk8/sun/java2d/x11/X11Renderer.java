package sun.java2d.x11;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.SpanIterator;

public class X11Renderer implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe {
   public static X11Renderer getInstance() {
      return (X11Renderer)(GraphicsPrimitive.tracingEnabled() ? new X11Renderer.X11TracingRenderer() : new X11Renderer());
   }

   private final long validate(SunGraphics2D var1) {
      X11SurfaceData var2 = (X11SurfaceData)var1.surfaceData;
      return var2.getRenderGC(var1.getCompClip(), var1.compositeState, var1.composite, var1.pixel);
   }

   native void XDrawLine(long var1, long var3, int var5, int var6, int var7, int var8);

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      SunToolkit.awtLock();

      try {
         long var6 = this.validate(var1);
         int var8 = var1.transX;
         int var9 = var1.transY;
         this.XDrawLine(var1.surfaceData.getNativeOps(), var6, var2 + var8, var3 + var9, var4 + var8, var5 + var9);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XDrawRect(long var1, long var3, int var5, int var6, int var7, int var8);

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      SunToolkit.awtLock();

      try {
         long var6 = this.validate(var1);
         this.XDrawRect(var1.surfaceData.getNativeOps(), var6, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XDrawRoundRect(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10);

   public void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      SunToolkit.awtLock();

      try {
         long var8 = this.validate(var1);
         this.XDrawRoundRect(var1.surfaceData.getNativeOps(), var8, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XDrawOval(long var1, long var3, int var5, int var6, int var7, int var8);

   public void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      SunToolkit.awtLock();

      try {
         long var6 = this.validate(var1);
         this.XDrawOval(var1.surfaceData.getNativeOps(), var6, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XDrawArc(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10);

   public void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      SunToolkit.awtLock();

      try {
         long var8 = this.validate(var1);
         this.XDrawArc(var1.surfaceData.getNativeOps(), var8, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XDrawPoly(long var1, long var3, int var5, int var6, int[] var7, int[] var8, int var9, boolean var10);

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      SunToolkit.awtLock();

      try {
         long var5 = this.validate(var1);
         this.XDrawPoly(var1.surfaceData.getNativeOps(), var5, var1.transX, var1.transY, var2, var3, var4, false);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      SunToolkit.awtLock();

      try {
         long var5 = this.validate(var1);
         this.XDrawPoly(var1.surfaceData.getNativeOps(), var5, var1.transX, var1.transY, var2, var3, var4, true);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XFillRect(long var1, long var3, int var5, int var6, int var7, int var8);

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      SunToolkit.awtLock();

      try {
         long var6 = this.validate(var1);
         this.XFillRect(var1.surfaceData.getNativeOps(), var6, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XFillRoundRect(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10);

   public void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      SunToolkit.awtLock();

      try {
         long var8 = this.validate(var1);
         this.XFillRoundRect(var1.surfaceData.getNativeOps(), var8, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XFillOval(long var1, long var3, int var5, int var6, int var7, int var8);

   public void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      SunToolkit.awtLock();

      try {
         long var6 = this.validate(var1);
         this.XFillOval(var1.surfaceData.getNativeOps(), var6, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XFillArc(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10);

   public void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      SunToolkit.awtLock();

      try {
         long var8 = this.validate(var1);
         this.XFillArc(var1.surfaceData.getNativeOps(), var8, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XFillPoly(long var1, long var3, int var5, int var6, int[] var7, int[] var8, int var9);

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      SunToolkit.awtLock();

      try {
         long var5 = this.validate(var1);
         this.XFillPoly(var1.surfaceData.getNativeOps(), var5, var1.transX, var1.transY, var2, var3, var4);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   native void XFillSpans(long var1, long var3, SpanIterator var5, long var6, int var8, int var9);

   native void XDoPath(SunGraphics2D var1, long var2, long var4, int var6, int var7, Path2D.Float var8, boolean var9);

   private void doPath(SunGraphics2D var1, Shape var2, boolean var3) {
      Path2D.Float var4;
      int var5;
      int var6;
      if (var1.transformState <= 1) {
         if (var2 instanceof Path2D.Float) {
            var4 = (Path2D.Float)var2;
         } else {
            var4 = new Path2D.Float(var2);
         }

         var5 = var1.transX;
         var6 = var1.transY;
      } else {
         var4 = new Path2D.Float(var2, var1.transform);
         var5 = 0;
         var6 = 0;
      }

      SunToolkit.awtLock();

      try {
         long var7 = this.validate(var1);
         this.XDoPath(var1, var1.surfaceData.getNativeOps(), var7, var5, var6, var4, var3);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   public void draw(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState == 0) {
         if (var2 instanceof Polygon && var1.transformState < 3) {
            Polygon var3 = (Polygon)var2;
            this.drawPolygon(var1, var3.xpoints, var3.ypoints, var3.npoints);
            return;
         }

         this.doPath(var1, var2, false);
      } else if (var1.strokeState < 3) {
         ShapeSpanIterator var14 = LoopPipe.getStrokeSpans(var1, var2);

         try {
            SunToolkit.awtLock();

            try {
               long var4 = this.validate(var1);
               this.XFillSpans(var1.surfaceData.getNativeOps(), var4, var14, var14.getNativeIterator(), 0, 0);
            } finally {
               SunToolkit.awtUnlock();
            }
         } finally {
            var14.dispose();
         }
      } else {
         this.fill(var1, var1.stroke.createStrokedShape(var2));
      }

   }

   public void fill(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState == 0) {
         if (var2 instanceof Polygon && var1.transformState < 3) {
            Polygon var18 = (Polygon)var2;
            this.fillPolygon(var1, var18.xpoints, var18.ypoints, var18.npoints);
         } else {
            this.doPath(var1, var2, true);
         }
      } else {
         AffineTransform var3;
         int var4;
         int var5;
         if (var1.transformState < 3) {
            var3 = null;
            var4 = var1.transX;
            var5 = var1.transY;
         } else {
            var3 = var1.transform;
            var5 = 0;
            var4 = 0;
         }

         ShapeSpanIterator var6 = LoopPipe.getFillSSI(var1);

         try {
            Region var7 = var1.getCompClip();
            var6.setOutputAreaXYXY(var7.getLoX() - var4, var7.getLoY() - var5, var7.getHiX() - var4, var7.getHiY() - var5);
            var6.appendPath(var2.getPathIterator(var3));
            SunToolkit.awtLock();

            try {
               long var8 = this.validate(var1);
               this.XFillSpans(var1.surfaceData.getNativeOps(), var8, var6, var6.getNativeIterator(), var4, var5);
            } finally {
               SunToolkit.awtUnlock();
            }
         } finally {
            var6.dispose();
         }

      }
   }

   native void devCopyArea(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10);

   public static class X11TracingRenderer extends X11Renderer {
      void XDrawLine(long var1, long var3, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("X11DrawLine");
         super.XDrawLine(var1, var3, var5, var6, var7, var8);
      }

      void XDrawRect(long var1, long var3, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("X11DrawRect");
         super.XDrawRect(var1, var3, var5, var6, var7, var8);
      }

      void XDrawRoundRect(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("X11DrawRoundRect");
         super.XDrawRoundRect(var1, var3, var5, var6, var7, var8, var9, var10);
      }

      void XDrawOval(long var1, long var3, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("X11DrawOval");
         super.XDrawOval(var1, var3, var5, var6, var7, var8);
      }

      void XDrawArc(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("X11DrawArc");
         super.XDrawArc(var1, var3, var5, var6, var7, var8, var9, var10);
      }

      void XDrawPoly(long var1, long var3, int var5, int var6, int[] var7, int[] var8, int var9, boolean var10) {
         GraphicsPrimitive.tracePrimitive("X11DrawPoly");
         super.XDrawPoly(var1, var3, var5, var6, var7, var8, var9, var10);
      }

      void XDoPath(SunGraphics2D var1, long var2, long var4, int var6, int var7, Path2D.Float var8, boolean var9) {
         GraphicsPrimitive.tracePrimitive(var9 ? "X11FillPath" : "X11DrawPath");
         super.XDoPath(var1, var2, var4, var6, var7, var8, var9);
      }

      void XFillRect(long var1, long var3, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("X11FillRect");
         super.XFillRect(var1, var3, var5, var6, var7, var8);
      }

      void XFillRoundRect(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("X11FillRoundRect");
         super.XFillRoundRect(var1, var3, var5, var6, var7, var8, var9, var10);
      }

      void XFillOval(long var1, long var3, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("X11FillOval");
         super.XFillOval(var1, var3, var5, var6, var7, var8);
      }

      void XFillArc(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("X11FillArc");
         super.XFillArc(var1, var3, var5, var6, var7, var8, var9, var10);
      }

      void XFillPoly(long var1, long var3, int var5, int var6, int[] var7, int[] var8, int var9) {
         GraphicsPrimitive.tracePrimitive("X11FillPoly");
         super.XFillPoly(var1, var3, var5, var6, var7, var8, var9);
      }

      void XFillSpans(long var1, long var3, SpanIterator var5, long var6, int var8, int var9) {
         GraphicsPrimitive.tracePrimitive("X11FillSpans");
         super.XFillSpans(var1, var3, var5, var6, var8, var9);
      }

      void devCopyArea(long var1, long var3, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("X11CopyArea");
         super.devCopyArea(var1, var3, var5, var6, var7, var8, var9, var10);
      }
   }
}
