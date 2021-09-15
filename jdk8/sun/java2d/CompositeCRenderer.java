package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.TextPipe;

public class CompositeCRenderer extends CRenderer implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, DrawImagePipe, TextPipe {
   static final int fPadding = 4;
   static final int fPaddingHalf = 2;
   private static AffineTransform sIdentityMatrix = new AffineTransform();
   AffineTransform ShapeTM = new AffineTransform();
   Rectangle2D ShapeBounds = new Rectangle2D.Float();
   Line2D line = new Line2D.Float();
   Rectangle2D rectangle = new Rectangle2D.Float();
   RoundRectangle2D roundrectangle = new RoundRectangle2D.Float();
   Ellipse2D ellipse = new Ellipse2D.Float();
   Arc2D arc = new Arc2D.Float();

   public synchronized void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.line.setLine((double)var2, (double)var3, (double)var4, (double)var5);
      this.draw(var1, this.line);
   }

   public synchronized void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.rectangle.setRect((double)var2, (double)var3, (double)var4, (double)var5);
      this.draw(var1, this.rectangle);
   }

   public synchronized void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.roundrectangle.setRoundRect((double)var2, (double)var3, (double)var4, (double)var5, (double)var6, (double)var7);
      this.draw(var1, this.roundrectangle);
   }

   public synchronized void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.ellipse.setFrame((double)var2, (double)var3, (double)var4, (double)var5);
      this.draw(var1, this.ellipse);
   }

   public synchronized void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.arc.setArc((double)var2, (double)var3, (double)var4, (double)var5, (double)var6, (double)var7, 0);
      this.draw(var1, this.arc);
   }

   public synchronized void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.doPolygon(var1, var2, var3, var4, false, false);
   }

   public synchronized void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.doPolygon(var1, var2, var3, var4, true, false);
   }

   public synchronized void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.rectangle.setRect((double)var2, (double)var3, (double)var4, (double)var5);
      this.fill(var1, this.rectangle);
   }

   public synchronized void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.roundrectangle.setRoundRect((double)var2, (double)var3, (double)var4, (double)var5, (double)var6, (double)var7);
      this.fill(var1, this.roundrectangle);
   }

   public synchronized void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.ellipse.setFrame((double)var2, (double)var3, (double)var4, (double)var5);
      this.fill(var1, this.ellipse);
   }

   public synchronized void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.arc.setArc((double)var2, (double)var3, (double)var4, (double)var5, (double)var6, (double)var7, 2);
      this.fill(var1, this.arc);
   }

   public synchronized void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      this.doPolygon(var1, var2, var3, var4, true, true);
   }

   public synchronized void doPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4, boolean var5, boolean var6) {
      GeneralPath var7 = new GeneralPath(1, var4);
      var7.moveTo((float)var2[0], (float)var3[0]);

      for(int var8 = 1; var8 < var4; ++var8) {
         var7.lineTo((float)var2[var8], (float)var3[var8]);
      }

      if (var5 && (var2[0] != var2[var4 - 1] || var3[0] != var3[var4 - 1])) {
         var7.lineTo((float)var2[0], (float)var3[0]);
      }

      this.doShape(var1, (OSXSurfaceData)var1.getSurfaceData(), var7, var6);
   }

   public synchronized void draw(SunGraphics2D var1, Shape var2) {
      this.doShape(var1, (OSXSurfaceData)var1.getSurfaceData(), var2, false);
   }

   public synchronized void fill(SunGraphics2D var1, Shape var2) {
      this.doShape(var1, (OSXSurfaceData)var1.getSurfaceData(), var2, true);
   }

   void doShape(SunGraphics2D var1, OSXSurfaceData var2, Shape var3, boolean var4) {
      Rectangle2D var5 = var3.getBounds2D();
      if (var5.getWidth() >= 0.0D && var5.getHeight() >= 0.0D) {
         Rectangle2D var6 = this.padBounds(var1, var3);
         this.clipBounds(var1, var6);
         if (!var6.isEmpty()) {
            BufferedImage var7 = var2.getCompositingSrcImage((int)var6.getWidth(), (int)var6.getHeight());
            Graphics2D var8 = var7.createGraphics();
            this.ShapeTM.setToTranslation(-var6.getX(), -var6.getY());
            this.ShapeTM.concatenate(var1.transform);
            var8.setTransform(this.ShapeTM);
            var8.setRenderingHints(var1.getRenderingHints());
            var8.setPaint(var1.getPaint());
            var8.setStroke(var1.getStroke());
            if (var4) {
               var8.fill(var3);
            } else {
               var8.draw(var3);
            }

            var8.dispose();
            this.composite(var1, var2, var7, var6);
         }

      }
   }

   public synchronized void drawString(SunGraphics2D var1, String var2, double var3, double var5) {
      this.drawGlyphVector(var1, var1.getFont().createGlyphVector(var1.getFontRenderContext(), var2), var3, var5);
   }

   public synchronized void drawChars(SunGraphics2D var1, char[] var2, int var3, int var4, int var5, int var6) {
      this.drawString(var1, new String(var2, var3, var4), (double)var5, (double)var6);
   }

   public synchronized void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, double var3, double var5) {
      this.drawGlyphVector(var1, var2, (float)var3, (float)var5);
   }

   public synchronized void drawGlyphVector(SunGraphics2D var1, GlyphVector var2, float var3, float var4) {
      OSXSurfaceData var5 = (OSXSurfaceData)var1.getSurfaceData();
      Shape var6 = var2.getOutline(var3, var4);
      Rectangle2D var7 = this.padBounds(var1, var6);
      this.clipBounds(var1, var7);
      if (!var7.isEmpty()) {
         BufferedImage var8 = var5.getCompositingSrcImage((int)var7.getWidth(), (int)var7.getHeight());
         Graphics2D var9 = var8.createGraphics();
         this.ShapeTM.setToTranslation(-var7.getX(), -var7.getY());
         this.ShapeTM.concatenate(var1.transform);
         var9.setTransform(this.ShapeTM);
         var9.setPaint(var1.getPaint());
         var9.setStroke(var1.getStroke());
         var9.setFont(var1.getFont());
         var9.setRenderingHints(var1.getRenderingHints());
         var9.drawGlyphVector(var2, var3, var4);
         var9.dispose();
         this.composite(var1, var5, var8, var7);
      }

   }

   protected boolean blitImage(SunGraphics2D var1, Image var2, boolean var3, boolean var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, Color var13) {
      OSXSurfaceData var14 = (OSXSurfaceData)var1.getSurfaceData();
      var9 = !var4 ? var9 : var9 - var11;
      var10 = !var3 ? var10 : var10 - var12;
      this.ShapeBounds.setFrame((double)var9, (double)var10, (double)var11, (double)var12);
      Rectangle2D var15 = this.ShapeBounds;
      boolean var16 = var1.transformState >= 3;
      if (!var16) {
         double var17 = Math.floor(var15.getX() + (double)var1.transX);
         double var19 = Math.floor(var15.getY() + (double)var1.transY);
         double var21 = Math.ceil(var15.getWidth()) + (double)(var17 < var15.getX() ? 1 : 0);
         double var23 = Math.ceil(var15.getHeight()) + (double)(var19 < var15.getY() ? 1 : 0);
         var15.setRect(var17, var19, var21, var23);
      } else {
         Shape var26 = var1.transform.createTransformedShape(var15);
         var15 = var26.getBounds2D();
         double var18 = Math.floor(var15.getX());
         double var20 = Math.floor(var15.getY());
         double var22 = Math.ceil(var15.getWidth()) + (double)(var18 < var15.getX() ? 1 : 0);
         double var24 = Math.ceil(var15.getHeight()) + (double)(var20 < var15.getY() ? 1 : 0);
         var15.setRect(var18, var20, var22, var24);
      }

      this.clipBounds(var1, var15);
      if (!var15.isEmpty()) {
         BufferedImage var27 = var14.getCompositingSrcImage((int)var15.getWidth(), (int)var15.getHeight());
         Graphics2D var28 = var27.createGraphics();
         this.ShapeTM.setToTranslation(-var15.getX(), -var15.getY());
         this.ShapeTM.concatenate(var1.transform);
         var28.setTransform(this.ShapeTM);
         var28.setRenderingHints(var1.getRenderingHints());
         var28.setComposite(AlphaComposite.Src);
         int var29 = !var4 ? var5 + var7 : var5 - var7;
         int var30 = !var3 ? var6 + var8 : var6 - var8;
         var28.drawImage(var2, var9, var10, var9 + var11, var10 + var12, var5, var6, var29, var30, (ImageObserver)null);
         var28.dispose();
         this.composite(var1, var14, var27, var15);
      }

      return true;
   }

   Rectangle2D padBounds(SunGraphics2D var1, Shape var2) {
      var2 = var1.transformShape(var2);
      int var3 = 2;
      int var4 = 4;
      if (var1.stroke != null) {
         if (var1.stroke instanceof BasicStroke) {
            int var5 = (int)(((BasicStroke)var1.stroke).getLineWidth() + 0.5F);
            int var6 = var5 / 2 + 1;
            var3 += var6;
            var4 += 2 * var6;
         } else {
            var2 = var1.stroke.createStrokedShape(var2);
         }
      }

      Rectangle2D var14 = var2.getBounds2D();
      var14.setRect(var14.getX() - (double)var3, var14.getY() - (double)var3, var14.getWidth() + (double)var4, var14.getHeight() + (double)var4);
      double var15 = Math.floor(var14.getX());
      double var8 = Math.floor(var14.getY());
      double var10 = Math.ceil(var14.getWidth()) + (double)(var15 < var14.getX() ? 1 : 0);
      double var12 = Math.ceil(var14.getHeight()) + (double)(var8 < var14.getY() ? 1 : 0);
      var14.setRect(var15, var8, var10, var12);
      return var14;
   }

   void clipBounds(SunGraphics2D var1, Rectangle2D var2) {
      Region var3 = var1.clipRegion.getIntersectionXYWH((int)var2.getX(), (int)var2.getY(), (int)var2.getWidth(), (int)var2.getHeight());
      var2.setRect((double)var3.getLoX(), (double)var3.getLoY(), (double)var3.getWidth(), (double)var3.getHeight());
   }

   BufferedImage getSurfacePixels(SunGraphics2D var1, OSXSurfaceData var2, int var3, int var4, int var5, int var6) {
      BufferedImage var7 = var2.getCompositingDstInImage(var5, var6);
      return var2.copyArea(var1, var3, var4, var5, var6, var7);
   }

   void composite(SunGraphics2D var1, OSXSurfaceData var2, BufferedImage var3, Rectangle2D var4) {
      int var5 = (int)var4.getX();
      int var6 = (int)var4.getY();
      int var7 = (int)var4.getWidth();
      int var8 = (int)var4.getHeight();
      boolean var9 = false;
      Composite var10 = var1.getComposite();
      if (var10 instanceof XORComposite) {
         try {
            var9 = var2.xorSurfacePixels(var1, var3, var5, var6, var7, var8, ((XORComposite)var10).getXorColor().getRGB());
         } catch (Exception var18) {
            var9 = false;
         }
      }

      if (!var9) {
         BufferedImage var11 = this.getSurfacePixels(var1, var2, var5, var6, var7, var8);
         BufferedImage var12 = null;
         if (var10 instanceof XORComposite) {
            try {
               OSXSurfaceData var13 = (OSXSurfaceData)((OSXSurfaceData)BufImgSurfaceData.createData(var11));
               var9 = var13.xorSurfacePixels(var1, var3, 0, 0, var7, var8, ((XORComposite)var10).getXorColor().getRGB());
               var12 = var11;
            } catch (Exception var17) {
               var9 = false;
            }
         }

         if (!var9) {
            var12 = var2.getCompositingDstOutImage(var7, var8);
            WritableRaster var19 = var3.getRaster();
            WritableRaster var14 = var11.getRaster();
            WritableRaster var15 = var12.getRaster();
            CompositeContext var16 = var10.createContext(var3.getColorModel(), var12.getColorModel(), var1.getRenderingHints());
            var16.compose(var19, var14, var15);
            var16.dispose();
         }

         Composite var20 = var1.getComposite();
         AffineTransform var21 = var1.getTransform();
         int var22 = var1.constrainX;
         int var23 = var1.constrainY;
         var1.setComposite(AlphaComposite.SrcOver);
         var1.constrainX = 0;
         var1.constrainY = 0;
         var1.setTransform(sIdentityMatrix);
         var1.drawImage(var12, var5, var6, var5 + var7, var6 + var8, 0, 0, var7, var8, (ImageObserver)null);
         var1.constrainX = var22;
         var1.constrainY = var23;
         var1.setTransform(var21);
         var1.setComposite(var20);
      }

   }
}
