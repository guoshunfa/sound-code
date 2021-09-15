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
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.awt.print.PrinterGraphics;
import java.awt.print.PrinterJob;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import sun.java2d.Spans;

public class PeekGraphics extends Graphics2D implements PrinterGraphics, ImageObserver, Cloneable {
   Graphics2D mGraphics;
   PrinterJob mPrinterJob;
   private Spans mDrawingArea = new Spans();
   private PeekMetrics mPrintMetrics = new PeekMetrics();
   private boolean mAWTDrawingOnly = false;

   public PeekGraphics(Graphics2D var1, PrinterJob var2) {
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

   public void setAWTDrawingOnly() {
      this.mAWTDrawingOnly = true;
   }

   public boolean getAWTDrawingOnly() {
      return this.mAWTDrawingOnly;
   }

   public Spans getDrawingArea() {
      return this.mDrawingArea;
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return ((RasterPrinterJob)this.mPrinterJob).getPrinterGraphicsConfig();
   }

   public Graphics create() {
      PeekGraphics var1 = null;

      try {
         var1 = (PeekGraphics)this.clone();
         var1.mGraphics = (Graphics2D)this.mGraphics.create();
      } catch (CloneNotSupportedException var3) {
      }

      return var1;
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
   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      this.addStrokeShape(new Line2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
      this.mPrintMetrics.draw(this);
   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      this.addDrawingRect(new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
      this.mPrintMetrics.fill(this);
   }

   public void clearRect(int var1, int var2, int var3, int var4) {
      Rectangle2D.Float var5 = new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4);
      this.addDrawingRect(var5);
      this.mPrintMetrics.clear(this);
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.addStrokeShape(new RoundRectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6));
      this.mPrintMetrics.draw(this);
   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      Rectangle2D.Float var7 = new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4);
      this.addDrawingRect(var7);
      this.mPrintMetrics.fill(this);
   }

   public void drawOval(int var1, int var2, int var3, int var4) {
      this.addStrokeShape(new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
      this.mPrintMetrics.draw(this);
   }

   public void fillOval(int var1, int var2, int var3, int var4) {
      Rectangle2D.Float var5 = new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4);
      this.addDrawingRect(var5);
      this.mPrintMetrics.fill(this);
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.addStrokeShape(new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
      this.mPrintMetrics.draw(this);
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      Rectangle2D.Float var7 = new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4);
      this.addDrawingRect(var7);
      this.mPrintMetrics.fill(this);
   }

   public void drawPolyline(int[] var1, int[] var2, int var3) {
      if (var3 > 0) {
         int var4 = var1[0];
         int var5 = var2[0];

         for(int var6 = 1; var6 < var3; ++var6) {
            this.drawLine(var4, var5, var1[var6], var2[var6]);
            var4 = var1[var6];
            var5 = var2[var6];
         }
      }

   }

   public void drawPolygon(int[] var1, int[] var2, int var3) {
      if (var3 > 0) {
         this.drawPolyline(var1, var2, var3);
         this.drawLine(var1[var3 - 1], var2[var3 - 1], var1[0], var2[0]);
      }

   }

   public void fillPolygon(int[] var1, int[] var2, int var3) {
      if (var3 > 0) {
         int var4 = var1[0];
         int var5 = var2[0];
         int var6 = var1[0];
         int var7 = var2[0];

         for(int var8 = 1; var8 < var3; ++var8) {
            if (var1[var8] < var4) {
               var4 = var1[var8];
            } else if (var1[var8] > var6) {
               var6 = var1[var8];
            }

            if (var2[var8] < var5) {
               var5 = var2[var8];
            } else if (var2[var8] > var7) {
               var7 = var2[var8];
            }
         }

         this.addDrawingRect((float)var4, (float)var5, (float)(var6 - var4), (float)(var7 - var5));
      }

      this.mPrintMetrics.fill(this);
   }

   public void drawString(String var1, int var2, int var3) {
      this.drawString(var1, (float)var2, (float)var3);
   }

   public void drawString(AttributedCharacterIterator var1, int var2, int var3) {
      this.drawString(var1, (float)var2, (float)var3);
   }

   public void drawString(AttributedCharacterIterator var1, float var2, float var3) {
      if (var1 == null) {
         throw new NullPointerException("AttributedCharacterIterator is null");
      } else {
         TextLayout var4 = new TextLayout(var1, this.getFontRenderContext());
         var4.draw(this, var2, var3);
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, ImageObserver var4) {
      if (var1 == null) {
         return true;
      } else {
         PeekGraphics.ImageWaiter var5 = new PeekGraphics.ImageWaiter(var1);
         this.addDrawingRect((float)var2, (float)var3, (float)var5.getWidth(), (float)var5.getHeight());
         this.mPrintMetrics.drawImage(this, (Image)var1);
         return this.mGraphics.drawImage(var1, var2, var3, var4);
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, ImageObserver var6) {
      if (var1 == null) {
         return true;
      } else {
         this.addDrawingRect((float)var2, (float)var3, (float)var4, (float)var5);
         this.mPrintMetrics.drawImage(this, (Image)var1);
         return this.mGraphics.drawImage(var1, var2, var3, var4, var5, var6);
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5) {
      if (var1 == null) {
         return true;
      } else {
         PeekGraphics.ImageWaiter var6 = new PeekGraphics.ImageWaiter(var1);
         this.addDrawingRect((float)var2, (float)var3, (float)var6.getWidth(), (float)var6.getHeight());
         this.mPrintMetrics.drawImage(this, (Image)var1);
         return this.mGraphics.drawImage(var1, var2, var3, var4, var5);
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7) {
      if (var1 == null) {
         return true;
      } else {
         this.addDrawingRect((float)var2, (float)var3, (float)var4, (float)var5);
         this.mPrintMetrics.drawImage(this, (Image)var1);
         return this.mGraphics.drawImage(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ImageObserver var10) {
      if (var1 == null) {
         return true;
      } else {
         int var11 = var4 - var2;
         int var12 = var5 - var3;
         this.addDrawingRect((float)var2, (float)var3, (float)var11, (float)var12);
         this.mPrintMetrics.drawImage(this, (Image)var1);
         return this.mGraphics.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      if (var1 == null) {
         return true;
      } else {
         int var12 = var4 - var2;
         int var13 = var5 - var3;
         this.addDrawingRect((float)var2, (float)var3, (float)var12, (float)var13);
         this.mPrintMetrics.drawImage(this, (Image)var1);
         return this.mGraphics.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }

   public void drawRenderedImage(RenderedImage var1, AffineTransform var2) {
      if (var1 != null) {
         this.mPrintMetrics.drawImage(this, (RenderedImage)var1);
         this.mDrawingArea.addInfinite();
      }
   }

   public void drawRenderableImage(RenderableImage var1, AffineTransform var2) {
      if (var1 != null) {
         this.mPrintMetrics.drawImage(this, (RenderableImage)var1);
         this.mDrawingArea.addInfinite();
      }
   }

   public void dispose() {
      this.mGraphics.dispose();
   }

   public void finalize() {
   }

   public void draw(Shape var1) {
      this.addStrokeShape(var1);
      this.mPrintMetrics.draw(this);
   }

   public boolean drawImage(Image var1, AffineTransform var2, ImageObserver var3) {
      if (var1 == null) {
         return true;
      } else {
         this.mDrawingArea.addInfinite();
         this.mPrintMetrics.drawImage(this, (Image)var1);
         return this.mGraphics.drawImage(var1, var2, var3);
      }
   }

   public void drawImage(BufferedImage var1, BufferedImageOp var2, int var3, int var4) {
      if (var1 != null) {
         this.mPrintMetrics.drawImage(this, (RenderedImage)var1);
         this.mDrawingArea.addInfinite();
      }
   }

   public void drawString(String var1, float var2, float var3) {
      if (var1.length() != 0) {
         FontRenderContext var4 = this.getFontRenderContext();
         Rectangle2D var5 = this.getFont().getStringBounds(var1, var4);
         this.addDrawingRect(var5, var2, var3);
         this.mPrintMetrics.drawText(this);
      }
   }

   public void drawGlyphVector(GlyphVector var1, float var2, float var3) {
      Rectangle2D var4 = var1.getLogicalBounds();
      this.addDrawingRect(var4, var2, var3);
      this.mPrintMetrics.drawText(this);
   }

   public void fill(Shape var1) {
      this.addDrawingRect(var1.getBounds());
      this.mPrintMetrics.fill(this);
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

   public boolean hitsDrawingArea(Rectangle var1) {
      return this.mDrawingArea.intersects((float)var1.getMinY(), (float)var1.getMaxY());
   }

   public PeekMetrics getMetrics() {
      return this.mPrintMetrics;
   }

   private void addDrawingRect(Rectangle2D var1, float var2, float var3) {
      this.addDrawingRect((float)(var1.getX() + (double)var2), (float)(var1.getY() + (double)var3), (float)var1.getWidth(), (float)var1.getHeight());
   }

   private void addDrawingRect(float var1, float var2, float var3, float var4) {
      Rectangle2D.Float var5 = new Rectangle2D.Float(var1, var2, var3, var4);
      this.addDrawingRect(var5);
   }

   private void addDrawingRect(Rectangle2D var1) {
      AffineTransform var2 = this.getTransform();
      Shape var3 = var2.createTransformedShape(var1);
      Rectangle2D var4 = var3.getBounds2D();
      this.mDrawingArea.add((float)var4.getMinY(), (float)var4.getMaxY());
   }

   private void addStrokeShape(Shape var1) {
      Shape var2 = this.getStroke().createStrokedShape(var1);
      this.addDrawingRect(var2.getBounds2D());
   }

   public synchronized boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
      boolean var7 = false;
      if ((var2 & 3) != 0) {
         var7 = true;
         this.notify();
      }

      return var7;
   }

   private synchronized int getImageWidth(Image var1) {
      while(var1.getWidth(this) == -1) {
         try {
            this.wait();
         } catch (InterruptedException var3) {
         }
      }

      return var1.getWidth(this);
   }

   private synchronized int getImageHeight(Image var1) {
      while(var1.getHeight(this) == -1) {
         try {
            this.wait();
         } catch (InterruptedException var3) {
         }
      }

      return var1.getHeight(this);
   }

   protected class ImageWaiter implements ImageObserver {
      private int mWidth;
      private int mHeight;
      private boolean badImage = false;

      ImageWaiter(Image var2) {
         this.waitForDimensions(var2);
      }

      public int getWidth() {
         return this.mWidth;
      }

      public int getHeight() {
         return this.mHeight;
      }

      private synchronized void waitForDimensions(Image var1) {
         this.mHeight = var1.getHeight(this);

         for(this.mWidth = var1.getWidth(this); !this.badImage && (this.mWidth < 0 || this.mHeight < 0); this.mWidth = var1.getWidth(this)) {
            try {
               Thread.sleep(50L);
            } catch (InterruptedException var3) {
            }

            this.mHeight = var1.getHeight(this);
         }

         if (this.badImage) {
            this.mHeight = 0;
            this.mWidth = 0;
         }

      }

      public synchronized boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
         boolean var7 = (var2 & 194) != 0;
         this.badImage = (var2 & 192) != 0;
         return var7;
      }
   }
}
