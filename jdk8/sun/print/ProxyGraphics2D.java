package sun.print;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.awt.print.PrinterGraphics;
import java.awt.print.PrinterJob;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class ProxyGraphics2D extends Graphics2D implements PrinterGraphics {
   Graphics2D mGraphics;
   PrinterJob mPrinterJob;

   public ProxyGraphics2D(Graphics2D var1, PrinterJob var2) {
      this.mGraphics = var1;
      this.mPrinterJob = var2;
   }

   public Graphics2D getDelegate() {
      return this.mGraphics;
   }

   public void setDelegate(Graphics2D var1) {
      this.mGraphics = var1;
   }

   public PrinterJob getPrinterJob() {
      return this.mPrinterJob;
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return ((RasterPrinterJob)this.mPrinterJob).getPrinterGraphicsConfig();
   }

   public Graphics create() {
      return new ProxyGraphics2D((Graphics2D)this.mGraphics.create(), this.mPrinterJob);
   }

   public void translate(int var1, int var2) {
      this.mGraphics.translate(var1, var2);
   }

   public void translate(double var1, double var3) {
      this.mGraphics.translate(var1, var3);
   }

   public void rotate(double var1) {
      this.mGraphics.rotate(var1);
   }

   public void rotate(double var1, double var3, double var5) {
      this.mGraphics.rotate(var1, var3, var5);
   }

   public void scale(double var1, double var3) {
      this.mGraphics.scale(var1, var3);
   }

   public void shear(double var1, double var3) {
      this.mGraphics.shear(var1, var3);
   }

   public Color getColor() {
      return this.mGraphics.getColor();
   }

   public void setColor(Color var1) {
      this.mGraphics.setColor(var1);
   }

   public void setPaintMode() {
      this.mGraphics.setPaintMode();
   }

   public void setXORMode(Color var1) {
      this.mGraphics.setXORMode(var1);
   }

   public Font getFont() {
      return this.mGraphics.getFont();
   }

   public void setFont(Font var1) {
      this.mGraphics.setFont(var1);
   }

   public FontMetrics getFontMetrics(Font var1) {
      return this.mGraphics.getFontMetrics(var1);
   }

   public FontRenderContext getFontRenderContext() {
      return this.mGraphics.getFontRenderContext();
   }

   public Rectangle getClipBounds() {
      return this.mGraphics.getClipBounds();
   }

   public void clipRect(int var1, int var2, int var3, int var4) {
      this.mGraphics.clipRect(var1, var2, var3, var4);
   }

   public void setClip(int var1, int var2, int var3, int var4) {
      this.mGraphics.setClip(var1, var2, var3, var4);
   }

   public Shape getClip() {
      return this.mGraphics.getClip();
   }

   public void setClip(Shape var1) {
      this.mGraphics.setClip(var1);
   }

   public void copyArea(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.mGraphics.copyArea(var1, var2, var3, var4, var5, var6);
   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      this.mGraphics.drawLine(var1, var2, var3, var4);
   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      this.mGraphics.fillRect(var1, var2, var3, var4);
   }

   public void clearRect(int var1, int var2, int var3, int var4) {
      this.mGraphics.clearRect(var1, var2, var3, var4);
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.mGraphics.drawRoundRect(var1, var2, var3, var4, var5, var6);
   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.mGraphics.fillRoundRect(var1, var2, var3, var4, var5, var6);
   }

   public void drawOval(int var1, int var2, int var3, int var4) {
      this.mGraphics.drawOval(var1, var2, var3, var4);
   }

   public void fillOval(int var1, int var2, int var3, int var4) {
      this.mGraphics.fillOval(var1, var2, var3, var4);
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.mGraphics.drawArc(var1, var2, var3, var4, var5, var6);
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.mGraphics.fillArc(var1, var2, var3, var4, var5, var6);
   }

   public void drawPolyline(int[] var1, int[] var2, int var3) {
      this.mGraphics.drawPolyline(var1, var2, var3);
   }

   public void drawPolygon(int[] var1, int[] var2, int var3) {
      this.mGraphics.drawPolygon(var1, var2, var3);
   }

   public void fillPolygon(int[] var1, int[] var2, int var3) {
      this.mGraphics.fillPolygon(var1, var2, var3);
   }

   public void drawString(String var1, int var2, int var3) {
      this.mGraphics.drawString(var1, var2, var3);
   }

   public void drawString(AttributedCharacterIterator var1, int var2, int var3) {
      this.mGraphics.drawString(var1, var2, var3);
   }

   public void drawString(AttributedCharacterIterator var1, float var2, float var3) {
      this.mGraphics.drawString(var1, var2, var3);
   }

   public boolean drawImage(Image var1, int var2, int var3, ImageObserver var4) {
      return this.mGraphics.drawImage(var1, var2, var3, var4);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, ImageObserver var6) {
      return this.mGraphics.drawImage(var1, var2, var3, var4, var5, var6);
   }

   public boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5) {
      if (var1 == null) {
         return true;
      } else {
         boolean var6;
         if (this.needToCopyBgColorImage(var1)) {
            BufferedImage var7 = this.getBufferedImageCopy(var1, var4);
            var6 = this.mGraphics.drawImage(var7, var2, var3, (ImageObserver)null);
         } else {
            var6 = this.mGraphics.drawImage(var1, var2, var3, var4, var5);
         }

         return var6;
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7) {
      if (var1 == null) {
         return true;
      } else {
         boolean var8;
         if (this.needToCopyBgColorImage(var1)) {
            BufferedImage var9 = this.getBufferedImageCopy(var1, var6);
            var8 = this.mGraphics.drawImage(var9, var2, var3, var4, var5, (ImageObserver)null);
         } else {
            var8 = this.mGraphics.drawImage(var1, var2, var3, var4, var5, var6, var7);
         }

         return var8;
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ImageObserver var10) {
      return this.mGraphics.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      if (var1 == null) {
         return true;
      } else {
         boolean var12;
         if (this.needToCopyBgColorImage(var1)) {
            BufferedImage var13 = this.getBufferedImageCopy(var1, var10);
            var12 = this.mGraphics.drawImage(var13, var2, var3, var4, var5, var7, var7, var8, var9, (ImageObserver)null);
         } else {
            var12 = this.mGraphics.drawImage(var1, var2, var3, var4, var5, var7, var7, var8, var9, var10, var11);
         }

         return var12;
      }
   }

   private boolean needToCopyBgColorImage(Image var1) {
      AffineTransform var3 = this.getTransform();
      return (var3.getType() & 48) != 0;
   }

   private BufferedImage getBufferedImageCopy(Image var1, Color var2) {
      BufferedImage var3 = null;
      int var4 = var1.getWidth((ImageObserver)null);
      int var5 = var1.getHeight((ImageObserver)null);
      if (var4 > 0 && var5 > 0) {
         int var6;
         if (var1 instanceof BufferedImage) {
            BufferedImage var7 = (BufferedImage)var1;
            var6 = var7.getType();
         } else {
            var6 = 2;
         }

         var3 = new BufferedImage(var4, var5, var6);
         Graphics2D var8 = var3.createGraphics();
         var8.drawImage(var1, 0, 0, var2, (ImageObserver)null);
         var8.dispose();
      } else {
         var3 = null;
      }

      return var3;
   }

   public void drawRenderedImage(RenderedImage var1, AffineTransform var2) {
      this.mGraphics.drawRenderedImage(var1, var2);
   }

   public void drawRenderableImage(RenderableImage var1, AffineTransform var2) {
      if (var1 != null) {
         AffineTransform var3 = this.getTransform();
         AffineTransform var4 = new AffineTransform(var2);
         var4.concatenate(var3);
         RenderContext var6 = new RenderContext(var4);

         AffineTransform var5;
         try {
            var5 = var3.createInverse();
         } catch (NoninvertibleTransformException var8) {
            var6 = new RenderContext(var3);
            var5 = new AffineTransform();
         }

         RenderedImage var7 = var1.createRendering(var6);
         this.drawRenderedImage(var7, var5);
      }
   }

   public void dispose() {
      this.mGraphics.dispose();
   }

   public void finalize() {
   }

   public void draw(Shape var1) {
      this.mGraphics.draw(var1);
   }

   public boolean drawImage(Image var1, AffineTransform var2, ImageObserver var3) {
      return this.mGraphics.drawImage(var1, var2, var3);
   }

   public void drawImage(BufferedImage var1, BufferedImageOp var2, int var3, int var4) {
      this.mGraphics.drawImage(var1, var2, var3, var4);
   }

   public void drawString(String var1, float var2, float var3) {
      this.mGraphics.drawString(var1, var2, var3);
   }

   public void drawGlyphVector(GlyphVector var1, float var2, float var3) {
      this.mGraphics.drawGlyphVector(var1, var2, var3);
   }

   public void fill(Shape var1) {
      this.mGraphics.fill(var1);
   }

   public boolean hit(Rectangle var1, Shape var2, boolean var3) {
      return this.mGraphics.hit(var1, var2, var3);
   }

   public void setComposite(Composite var1) {
      this.mGraphics.setComposite(var1);
   }

   public void setPaint(Paint var1) {
      this.mGraphics.setPaint(var1);
   }

   public void setStroke(Stroke var1) {
      this.mGraphics.setStroke(var1);
   }

   public void setRenderingHint(RenderingHints.Key var1, Object var2) {
      this.mGraphics.setRenderingHint(var1, var2);
   }

   public Object getRenderingHint(RenderingHints.Key var1) {
      return this.mGraphics.getRenderingHint(var1);
   }

   public void setRenderingHints(Map<?, ?> var1) {
      this.mGraphics.setRenderingHints(var1);
   }

   public void addRenderingHints(Map<?, ?> var1) {
      this.mGraphics.addRenderingHints(var1);
   }

   public RenderingHints getRenderingHints() {
      return this.mGraphics.getRenderingHints();
   }

   public void transform(AffineTransform var1) {
      this.mGraphics.transform(var1);
   }

   public void setTransform(AffineTransform var1) {
      this.mGraphics.setTransform(var1);
   }

   public AffineTransform getTransform() {
      return this.mGraphics.getTransform();
   }

   public Paint getPaint() {
      return this.mGraphics.getPaint();
   }

   public Composite getComposite() {
      return this.mGraphics.getComposite();
   }

   public void setBackground(Color var1) {
      this.mGraphics.setBackground(var1);
   }

   public Color getBackground() {
      return this.mGraphics.getBackground();
   }

   public Stroke getStroke() {
      return this.mGraphics.getStroke();
   }

   public void clip(Shape var1) {
      this.mGraphics.clip(var1);
   }
}
