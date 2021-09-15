package sun.java2d.pipe;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.ProcessPath;

public abstract class BufferedRenderPipe implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, ParallelogramPipe {
   ParallelogramPipe aapgrampipe = new BufferedRenderPipe.AAParallelogramPipe();
   static final int BYTES_PER_POLY_POINT = 8;
   static final int BYTES_PER_SCANLINE = 12;
   static final int BYTES_PER_SPAN = 16;
   protected RenderQueue rq;
   protected RenderBuffer buf;
   private BufferedRenderPipe.BufferedDrawHandler drawHandler;

   public BufferedRenderPipe(RenderQueue var1) {
      this.rq = var1;
      this.buf = var1.getBuffer();
      this.drawHandler = new BufferedRenderPipe.BufferedDrawHandler();
   }

   public ParallelogramPipe getAAParallelogramPipe() {
      return this.aapgrampipe;
   }

   protected abstract void validateContext(SunGraphics2D var1);

   protected abstract void validateContextAA(SunGraphics2D var1);

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.transX;
      int var7 = var1.transY;
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.rq.ensureCapacity(20);
         this.buf.putInt(10);
         this.buf.putInt(var2 + var6);
         this.buf.putInt(var3 + var7);
         this.buf.putInt(var4 + var6);
         this.buf.putInt(var5 + var7);
      } finally {
         this.rq.unlock();
      }

   }

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.rq.ensureCapacity(20);
         this.buf.putInt(11);
         this.buf.putInt(var2 + var1.transX);
         this.buf.putInt(var3 + var1.transY);
         this.buf.putInt(var4);
         this.buf.putInt(var5);
      } finally {
         this.rq.unlock();
      }

   }

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.rq.ensureCapacity(20);
         this.buf.putInt(20);
         this.buf.putInt(var2 + var1.transX);
         this.buf.putInt(var3 + var1.transY);
         this.buf.putInt(var4);
         this.buf.putInt(var5);
      } finally {
         this.rq.unlock();
      }

   }

   public void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.draw(var1, new RoundRectangle2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7));
   }

   public void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fill(var1, new RoundRectangle2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7));
   }

   public void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.draw(var1, new Ellipse2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.fill(var1, new Ellipse2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.draw(var1, new Arc2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 0));
   }

   public void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fill(var1, new Arc2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 2));
   }

   protected void drawPoly(final SunGraphics2D var1, final int[] var2, final int[] var3, final int var4, final boolean var5) {
      if (var2 != null && var3 != null) {
         if (var2.length >= var4 && var3.length >= var4) {
            if (var4 >= 2) {
               if (var4 == 2 && !var5) {
                  this.drawLine(var1, var2[0], var3[0], var2[1], var3[1]);
               } else {
                  this.rq.lock();

                  try {
                     this.validateContext(var1);
                     int var6 = var4 * 8;
                     int var7 = 20 + var6;
                     if (var7 <= this.buf.capacity()) {
                        if (var7 > this.buf.remaining()) {
                           this.rq.flushNow();
                        }

                        this.buf.putInt(12);
                        this.buf.putInt(var4);
                        this.buf.putInt(var5 ? 1 : 0);
                        this.buf.putInt(var1.transX);
                        this.buf.putInt(var1.transY);
                        this.buf.put((int[])var2, 0, var4);
                        this.buf.put((int[])var3, 0, var4);
                     } else {
                        this.rq.flushAndInvokeNow(new Runnable() {
                           public void run() {
                              BufferedRenderPipe.this.drawPoly(var2, var3, var4, var5, var1.transX, var1.transY);
                           }
                        });
                     }
                  } finally {
                     this.rq.unlock();
                  }

               }
            }
         } else {
            throw new ArrayIndexOutOfBoundsException("coordinate array");
         }
      } else {
         throw new NullPointerException("coordinate array");
      }
   }

   protected abstract void drawPoly(int[] var1, int[] var2, int var3, boolean var4, int var5, int var6);

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.drawPoly(var1, var2, var3, var4, false);
   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.drawPoly(var1, var2, var3, var4, true);
   }

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.fill(var1, new Polygon(var2, var3, var4));
   }

   protected void drawPath(SunGraphics2D var1, Path2D.Float var2, int var3, int var4) {
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.drawHandler.validate(var1);
         ProcessPath.drawPath(this.drawHandler, var2, var3, var4);
      } finally {
         this.rq.unlock();
      }

   }

   protected void fillPath(SunGraphics2D var1, Path2D.Float var2, int var3, int var4) {
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.drawHandler.validate(var1);
         this.drawHandler.startFillPath();
         ProcessPath.fillPath(this.drawHandler, var2, var3, var4);
         this.drawHandler.endFillPath();
      } finally {
         this.rq.unlock();
      }

   }

   private native int fillSpans(RenderQueue var1, long var2, int var4, int var5, SpanIterator var6, long var7, int var9, int var10);

   protected void fillSpans(SunGraphics2D var1, SpanIterator var2, int var3, int var4) {
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.rq.ensureCapacity(24);
         int var5 = this.fillSpans(this.rq, this.buf.getAddress(), this.buf.position(), this.buf.capacity(), var2, var2.getNativeIterator(), var3, var4);
         this.buf.position((long)var5);
      } finally {
         this.rq.unlock();
      }

   }

   public void fillParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.rq.ensureCapacity(28);
         this.buf.putInt(22);
         this.buf.putFloat((float)var10);
         this.buf.putFloat((float)var12);
         this.buf.putFloat((float)var14);
         this.buf.putFloat((float)var16);
         this.buf.putFloat((float)var18);
         this.buf.putFloat((float)var20);
      } finally {
         this.rq.unlock();
      }

   }

   public void drawParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
      this.rq.lock();

      try {
         this.validateContext(var1);
         this.rq.ensureCapacity(36);
         this.buf.putInt(15);
         this.buf.putFloat((float)var10);
         this.buf.putFloat((float)var12);
         this.buf.putFloat((float)var14);
         this.buf.putFloat((float)var16);
         this.buf.putFloat((float)var18);
         this.buf.putFloat((float)var20);
         this.buf.putFloat((float)var22);
         this.buf.putFloat((float)var24);
      } finally {
         this.rq.unlock();
      }

   }

   public void draw(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState == 0) {
         if (var2 instanceof Polygon && var1.transformState < 3) {
            Polygon var9 = (Polygon)var2;
            this.drawPolygon(var1, var9.xpoints, var9.ypoints, var9.npoints);
            return;
         }

         Path2D.Float var3;
         int var4;
         int var5;
         if (var1.transformState <= 1) {
            if (var2 instanceof Path2D.Float) {
               var3 = (Path2D.Float)var2;
            } else {
               var3 = new Path2D.Float(var2);
            }

            var4 = var1.transX;
            var5 = var1.transY;
         } else {
            var3 = new Path2D.Float(var2, var1.transform);
            var4 = 0;
            var5 = 0;
         }

         this.drawPath(var1, var3, var4, var5);
      } else if (var1.strokeState < 3) {
         ShapeSpanIterator var10 = LoopPipe.getStrokeSpans(var1, var2);

         try {
            this.fillSpans(var1, var10, 0, 0);
         } finally {
            var10.dispose();
         }
      } else {
         this.fill(var1, var1.stroke.createStrokedShape(var2));
      }

   }

   public void fill(SunGraphics2D var1, Shape var2) {
      int var3;
      int var4;
      if (var1.strokeState == 0) {
         Path2D.Float var11;
         if (var1.transformState <= 1) {
            if (var2 instanceof Path2D.Float) {
               var11 = (Path2D.Float)var2;
            } else {
               var11 = new Path2D.Float(var2);
            }

            var3 = var1.transX;
            var4 = var1.transY;
         } else {
            var11 = new Path2D.Float(var2, var1.transform);
            var3 = 0;
            var4 = 0;
         }

         this.fillPath(var1, var11, var3, var4);
      } else {
         AffineTransform var5;
         if (var1.transformState <= 1) {
            var5 = null;
            var3 = var1.transX;
            var4 = var1.transY;
         } else {
            var5 = var1.transform;
            var4 = 0;
            var3 = 0;
         }

         ShapeSpanIterator var6 = LoopPipe.getFillSSI(var1);

         try {
            Region var7 = var1.getCompClip();
            var6.setOutputAreaXYXY(var7.getLoX() - var3, var7.getLoY() - var4, var7.getHiX() - var3, var7.getHiY() - var4);
            var6.appendPath(var2.getPathIterator(var5));
            this.fillSpans(var1, var6, var3, var4);
         } finally {
            var6.dispose();
         }

      }
   }

   private class AAParallelogramPipe implements ParallelogramPipe {
      private AAParallelogramPipe() {
      }

      public void fillParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
         BufferedRenderPipe.this.rq.lock();

         try {
            BufferedRenderPipe.this.validateContextAA(var1);
            BufferedRenderPipe.this.rq.ensureCapacity(28);
            BufferedRenderPipe.this.buf.putInt(23);
            BufferedRenderPipe.this.buf.putFloat((float)var10);
            BufferedRenderPipe.this.buf.putFloat((float)var12);
            BufferedRenderPipe.this.buf.putFloat((float)var14);
            BufferedRenderPipe.this.buf.putFloat((float)var16);
            BufferedRenderPipe.this.buf.putFloat((float)var18);
            BufferedRenderPipe.this.buf.putFloat((float)var20);
         } finally {
            BufferedRenderPipe.this.rq.unlock();
         }

      }

      public void drawParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
         BufferedRenderPipe.this.rq.lock();

         try {
            BufferedRenderPipe.this.validateContextAA(var1);
            BufferedRenderPipe.this.rq.ensureCapacity(36);
            BufferedRenderPipe.this.buf.putInt(16);
            BufferedRenderPipe.this.buf.putFloat((float)var10);
            BufferedRenderPipe.this.buf.putFloat((float)var12);
            BufferedRenderPipe.this.buf.putFloat((float)var14);
            BufferedRenderPipe.this.buf.putFloat((float)var16);
            BufferedRenderPipe.this.buf.putFloat((float)var18);
            BufferedRenderPipe.this.buf.putFloat((float)var20);
            BufferedRenderPipe.this.buf.putFloat((float)var22);
            BufferedRenderPipe.this.buf.putFloat((float)var24);
         } finally {
            BufferedRenderPipe.this.rq.unlock();
         }

      }

      // $FF: synthetic method
      AAParallelogramPipe(Object var2) {
         this();
      }
   }

   private class BufferedDrawHandler extends ProcessPath.DrawHandler {
      private int scanlineCount;
      private int scanlineCountIndex;
      private int remainingScanlines;

      BufferedDrawHandler() {
         super(0, 0, 0, 0);
      }

      void validate(SunGraphics2D var1) {
         Region var2 = var1.getCompClip();
         this.setBounds(var2.getLoX(), var2.getLoY(), var2.getHiX(), var2.getHiY(), var1.strokeHint);
      }

      public void drawLine(int var1, int var2, int var3, int var4) {
         BufferedRenderPipe.this.rq.ensureCapacity(20);
         BufferedRenderPipe.this.buf.putInt(10);
         BufferedRenderPipe.this.buf.putInt(var1);
         BufferedRenderPipe.this.buf.putInt(var2);
         BufferedRenderPipe.this.buf.putInt(var3);
         BufferedRenderPipe.this.buf.putInt(var4);
      }

      public void drawPixel(int var1, int var2) {
         BufferedRenderPipe.this.rq.ensureCapacity(12);
         BufferedRenderPipe.this.buf.putInt(13);
         BufferedRenderPipe.this.buf.putInt(var1);
         BufferedRenderPipe.this.buf.putInt(var2);
      }

      private void resetFillPath() {
         BufferedRenderPipe.this.buf.putInt(14);
         this.scanlineCountIndex = BufferedRenderPipe.this.buf.position();
         BufferedRenderPipe.this.buf.putInt(0);
         this.scanlineCount = 0;
         this.remainingScanlines = BufferedRenderPipe.this.buf.remaining() / 12;
      }

      private void updateScanlineCount() {
         BufferedRenderPipe.this.buf.putInt(this.scanlineCountIndex, this.scanlineCount);
      }

      public void startFillPath() {
         BufferedRenderPipe.this.rq.ensureCapacity(20);
         this.resetFillPath();
      }

      public void drawScanline(int var1, int var2, int var3) {
         if (this.remainingScanlines == 0) {
            this.updateScanlineCount();
            BufferedRenderPipe.this.rq.flushNow();
            this.resetFillPath();
         }

         BufferedRenderPipe.this.buf.putInt(var1);
         BufferedRenderPipe.this.buf.putInt(var2);
         BufferedRenderPipe.this.buf.putInt(var3);
         ++this.scanlineCount;
         --this.remainingScanlines;
      }

      public void endFillPath() {
         this.updateScanlineCount();
      }
   }
}
