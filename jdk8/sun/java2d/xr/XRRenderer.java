package sun.java2d.xr;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import sun.awt.SunToolkit;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.ProcessPath;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.SpanIterator;

public class XRRenderer implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe {
   XRRenderer.XRDrawHandler drawHandler;
   MaskTileManager tileManager;
   XRDrawLine lineGen;
   GrowableRectArray rectBuffer;

   public XRRenderer(MaskTileManager var1) {
      this.tileManager = var1;
      this.rectBuffer = var1.getMainTile().getRects();
      this.drawHandler = new XRRenderer.XRDrawHandler();
      this.lineGen = new XRDrawLine();
   }

   private final void validateSurface(SunGraphics2D var1) {
      XRSurfaceData var2;
      try {
         var2 = (XRSurfaceData)var1.surfaceData;
      } catch (ClassCastException var4) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      var2.validateAsDestination(var1, var1.getCompClip());
      var2.maskBuffer.validateCompositeState(var1.composite, var1.transform, var1.paint, var1);
   }

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      Region var6 = var1.getCompClip();
      int var7 = Region.clipAdd(var2, var1.transX);
      int var8 = Region.clipAdd(var3, var1.transY);
      int var9 = Region.clipAdd(var4, var1.transX);
      int var10 = Region.clipAdd(var5, var1.transY);
      SunToolkit.awtLock();

      try {
         this.validateSurface(var1);
         this.lineGen.rasterizeLine(this.rectBuffer, var7, var8, var9, var10, var6.getLoX(), var6.getLoY(), var6.getHiX(), var6.getHiY(), true, true);
         this.tileManager.fillMask((XRSurfaceData)var1.surfaceData);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.draw(var1, new Rectangle2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      Path2D.Float var5 = new Path2D.Float();
      if (var4 > 1) {
         var5.moveTo((float)var2[0], (float)var3[0]);

         for(int var6 = 1; var6 < var4; ++var6) {
            var5.lineTo((float)var2[var6], (float)var3[var6]);
         }
      }

      this.draw(var1, var5);
   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.draw(var1, new Polygon(var2, var3, var4));
   }

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      var2 = Region.clipAdd(var2, var1.transX);
      var3 = Region.clipAdd(var3, var1.transY);
      if (var2 <= 32767 && var3 <= 32767) {
         int var6 = Region.dimAdd(var2, var4);
         int var7 = Region.dimAdd(var3, var5);
         if (var6 >= -32768 && var7 >= -32768) {
            short var11 = XRUtils.clampToShort(var2);
            short var12 = XRUtils.clampToShort(var3);
            var4 = XRUtils.clampToUShort(var6 - var11);
            var5 = XRUtils.clampToUShort(var7 - var12);
            if (var4 != 0 && var5 != 0) {
               SunToolkit.awtLock();

               try {
                  this.validateSurface(var1);
                  this.rectBuffer.pushRectValues(var11, var12, var4, var5);
                  this.tileManager.fillMask((XRSurfaceData)var1.surfaceData);
               } finally {
                  SunToolkit.awtUnlock();
               }

            }
         }
      }
   }

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.fill(var1, new Polygon(var2, var3, var4));
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

   protected void drawPath(SunGraphics2D var1, Path2D.Float var2, int var3, int var4) {
      SunToolkit.awtLock();

      try {
         this.validateSurface(var1);
         this.drawHandler.validate(var1);
         ProcessPath.drawPath(this.drawHandler, var2, var3, var4);
         this.tileManager.fillMask((XRSurfaceData)var1.surfaceData);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   protected void fillPath(SunGraphics2D var1, Path2D.Float var2, int var3, int var4) {
      SunToolkit.awtLock();

      try {
         this.validateSurface(var1);
         this.drawHandler.validate(var1);
         ProcessPath.fillPath(this.drawHandler, var2, var3, var4);
         this.tileManager.fillMask((XRSurfaceData)var1.surfaceData);
      } finally {
         SunToolkit.awtUnlock();
      }

   }

   protected void fillSpans(SunGraphics2D var1, SpanIterator var2, int var3, int var4) {
      SunToolkit.awtLock();

      try {
         this.validateSurface(var1);
         int[] var5 = new int[4];

         while(var2.nextSpan(var5)) {
            this.rectBuffer.pushRectValues(var5[0] + var3, var5[1] + var4, var5[2] - var5[0], var5[3] - var5[1]);
         }

         this.tileManager.fillMask((XRSurfaceData)var1.surfaceData);
      } finally {
         SunToolkit.awtUnlock();
      }
   }

   public void draw(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState == 0) {
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
         ShapeSpanIterator var9 = LoopPipe.getStrokeSpans(var1, var2);

         try {
            this.fillSpans(var1, var9, 0, 0);
         } finally {
            var9.dispose();
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

   private class XRDrawHandler extends ProcessPath.DrawHandler {
      DirtyRegion region = new DirtyRegion();

      XRDrawHandler() {
         super(0, 0, 0, 0);
      }

      void validate(SunGraphics2D var1) {
         Region var2 = var1.getCompClip();
         this.setBounds(var2.getLoX(), var2.getLoY(), var2.getHiX(), var2.getHiY(), var1.strokeHint);
         XRRenderer.this.validateSurface(var1);
      }

      public void drawLine(int var1, int var2, int var3, int var4) {
         this.region.setDirtyLineRegion(var1, var2, var3, var4);
         int var5 = this.region.x2 - this.region.x;
         int var6 = this.region.y2 - this.region.y;
         if (var5 != 0 && var6 != 0) {
            if (var5 == 1 && var6 == 1) {
               XRRenderer.this.rectBuffer.pushRectValues(var1, var2, 1, 1);
               XRRenderer.this.rectBuffer.pushRectValues(var3, var4, 1, 1);
            } else {
               XRRenderer.this.lineGen.rasterizeLine(XRRenderer.this.rectBuffer, var1, var2, var3, var4, 0, 0, 0, 0, false, false);
            }
         } else {
            XRRenderer.this.rectBuffer.pushRectValues(this.region.x, this.region.y, this.region.x2 - this.region.x + 1, this.region.y2 - this.region.y + 1);
         }

      }

      public void drawPixel(int var1, int var2) {
         XRRenderer.this.rectBuffer.pushRectValues(var1, var2, 1, 1);
      }

      public void drawScanline(int var1, int var2, int var3) {
         XRRenderer.this.rectBuffer.pushRectValues(var1, var3, var2 - var1 + 1, 1);
      }
   }
}
