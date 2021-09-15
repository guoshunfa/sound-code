package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;
import sun.java2d.SunGraphics2D;

public class PixelToShapeConverter implements PixelDrawPipe, PixelFillPipe {
   ShapeDrawPipe outpipe;

   public PixelToShapeConverter(ShapeDrawPipe var1) {
      this.outpipe = var1;
   }

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.outpipe.draw(var1, new Line2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.outpipe.draw(var1, new Rectangle(var2, var3, var4, var5));
   }

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.outpipe.fill(var1, new Rectangle(var2, var3, var4, var5));
   }

   public void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.outpipe.draw(var1, new RoundRectangle2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7));
   }

   public void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.outpipe.fill(var1, new RoundRectangle2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7));
   }

   public void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.outpipe.draw(var1, new Ellipse2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.outpipe.fill(var1, new Ellipse2D.Float((float)var2, (float)var3, (float)var4, (float)var5));
   }

   public void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.outpipe.draw(var1, new Arc2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 0));
   }

   public void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.outpipe.fill(var1, new Arc2D.Float((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 2));
   }

   private Shape makePoly(int[] var1, int[] var2, int var3, boolean var4) {
      GeneralPath var5 = new GeneralPath(0);
      if (var3 > 0) {
         var5.moveTo((float)var1[0], (float)var2[0]);

         for(int var6 = 1; var6 < var3; ++var6) {
            var5.lineTo((float)var1[var6], (float)var2[var6]);
         }

         if (var4) {
            var5.closePath();
         }
      }

      return var5;
   }

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.outpipe.draw(var1, this.makePoly(var2, var3, var4, false));
   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.outpipe.draw(var1, this.makePoly(var2, var3, var4, true));
   }

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.outpipe.fill(var1, this.makePoly(var2, var3, var4, true));
   }
}
