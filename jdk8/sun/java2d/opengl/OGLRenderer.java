package sun.java2d.opengl;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.BufferedRenderPipe;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.SpanIterator;

class OGLRenderer extends BufferedRenderPipe {
   OGLRenderer(RenderQueue var1) {
      super(var1);
   }

   protected void validateContext(SunGraphics2D var1) {
      int var2 = var1.paint.getTransparency() == 1 ? 1 : 0;

      OGLSurfaceData var3;
      try {
         var3 = (OGLSurfaceData)var1.surfaceData;
      } catch (ClassCastException var5) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      OGLContext.validateContext(var3, var3, var1.getCompClip(), var1.composite, (AffineTransform)null, var1.paint, var1, var2);
   }

   protected void validateContextAA(SunGraphics2D var1) {
      byte var2 = 0;

      OGLSurfaceData var3;
      try {
         var3 = (OGLSurfaceData)var1.surfaceData;
      } catch (ClassCastException var5) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      OGLContext.validateContext(var3, var3, var1.getCompClip(), var1.composite, (AffineTransform)null, var1.paint, var1, var2);
   }

   void copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.rq.lock();

      try {
         int var8 = var1.surfaceData.getTransparency() == 1 ? 1 : 0;

         OGLSurfaceData var9;
         try {
            var9 = (OGLSurfaceData)var1.surfaceData;
         } catch (ClassCastException var14) {
            throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
         }

         OGLContext.validateContext(var9, var9, var1.getCompClip(), var1.composite, (AffineTransform)null, (Paint)null, (SunGraphics2D)null, var8);
         this.rq.ensureCapacity(28);
         this.buf.putInt(30);
         this.buf.putInt(var2).putInt(var3).putInt(var4).putInt(var5);
         this.buf.putInt(var6).putInt(var7);
      } finally {
         this.rq.unlock();
      }

   }

   protected native void drawPoly(int[] var1, int[] var2, int var3, boolean var4, int var5, int var6);

   OGLRenderer traceWrap() {
      return new OGLRenderer.Tracer(this);
   }

   private class Tracer extends OGLRenderer {
      private OGLRenderer oglr;

      Tracer(OGLRenderer var2) {
         super(var2.rq);
         this.oglr = var2;
      }

      public ParallelogramPipe getAAParallelogramPipe() {
         final ParallelogramPipe var1 = this.oglr.getAAParallelogramPipe();
         return new ParallelogramPipe() {
            public void fillParallelogram(SunGraphics2D var1x, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
               GraphicsPrimitive.tracePrimitive("OGLFillAAParallelogram");
               var1.fillParallelogram(var1x, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20);
            }

            public void drawParallelogram(SunGraphics2D var1x, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
               GraphicsPrimitive.tracePrimitive("OGLDrawAAParallelogram");
               var1.drawParallelogram(var1x, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22, var24);
            }
         };
      }

      protected void validateContext(SunGraphics2D var1) {
         this.oglr.validateContext(var1);
      }

      public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
         GraphicsPrimitive.tracePrimitive("OGLDrawLine");
         this.oglr.drawLine(var1, var2, var3, var4, var5);
      }

      public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
         GraphicsPrimitive.tracePrimitive("OGLDrawRect");
         this.oglr.drawRect(var1, var2, var3, var4, var5);
      }

      protected void drawPoly(SunGraphics2D var1, int[] var2, int[] var3, int var4, boolean var5) {
         GraphicsPrimitive.tracePrimitive("OGLDrawPoly");
         this.oglr.drawPoly(var1, var2, var3, var4, var5);
      }

      public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
         GraphicsPrimitive.tracePrimitive("OGLFillRect");
         this.oglr.fillRect(var1, var2, var3, var4, var5);
      }

      protected void drawPath(SunGraphics2D var1, Path2D.Float var2, int var3, int var4) {
         GraphicsPrimitive.tracePrimitive("OGLDrawPath");
         this.oglr.drawPath(var1, var2, var3, var4);
      }

      protected void fillPath(SunGraphics2D var1, Path2D.Float var2, int var3, int var4) {
         GraphicsPrimitive.tracePrimitive("OGLFillPath");
         this.oglr.fillPath(var1, var2, var3, var4);
      }

      protected void fillSpans(SunGraphics2D var1, SpanIterator var2, int var3, int var4) {
         GraphicsPrimitive.tracePrimitive("OGLFillSpans");
         this.oglr.fillSpans(var1, var2, var3, var4);
      }

      public void fillParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
         GraphicsPrimitive.tracePrimitive("OGLFillParallelogram");
         this.oglr.fillParallelogram(var1, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20);
      }

      public void drawParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
         GraphicsPrimitive.tracePrimitive("OGLDrawParallelogram");
         this.oglr.drawParallelogram(var1, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22, var24);
      }

      public void copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         GraphicsPrimitive.tracePrimitive("OGLCopyArea");
         this.oglr.copyArea(var1, var2, var3, var4, var5, var6, var7);
      }
   }
}
