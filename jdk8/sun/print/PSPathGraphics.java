package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import sun.awt.image.ByteComponentRaster;

class PSPathGraphics extends PathGraphics {
   private static final int DEFAULT_USER_RES = 72;

   PSPathGraphics(Graphics2D var1, PrinterJob var2, Printable var3, PageFormat var4, int var5, boolean var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public Graphics create() {
      return new PSPathGraphics((Graphics2D)this.getDelegate().create(), this.getPrinterJob(), this.getPrintable(), this.getPageFormat(), this.getPageIndex(), this.canDoRedraws());
   }

   public void fill(Shape var1, Color var2) {
      this.deviceFill(var1.getPathIterator(new AffineTransform()), var2);
   }

   public void drawString(String var1, int var2, int var3) {
      this.drawString(var1, (float)var2, (float)var3);
   }

   public void drawString(String var1, float var2, float var3) {
      this.drawString(var1, var2, var3, this.getFont(), this.getFontRenderContext(), 0.0F);
   }

   protected boolean canDrawStringToWidth() {
      return true;
   }

   protected int platformFontCount(Font var1, String var2) {
      PSPrinterJob var3 = (PSPrinterJob)this.getPrinterJob();
      return var3.platformFontCount(var1, var2);
   }

   protected void drawString(String var1, float var2, float var3, Font var4, FontRenderContext var5, float var6) {
      if (var1.length() != 0) {
         if (var4.hasLayoutAttributes() && !this.printingGlyphVector) {
            TextLayout var16 = new TextLayout(var1, var4, var5);
            var16.draw(this, var2, var3);
         } else {
            Font var7 = this.getFont();
            if (!var7.equals(var4)) {
               this.setFont(var4);
            } else {
               var7 = null;
            }

            boolean var8 = false;
            float var9 = 0.0F;
            float var10 = 0.0F;
            boolean var11 = this.getFont().isTransformed();
            if (var11) {
               AffineTransform var12 = this.getFont().getTransform();
               int var13 = var12.getType();
               if (var13 == 1) {
                  var9 = (float)var12.getTranslateX();
                  var10 = (float)var12.getTranslateY();
                  if ((double)Math.abs(var9) < 1.0E-5D) {
                     var9 = 0.0F;
                  }

                  if ((double)Math.abs(var10) < 1.0E-5D) {
                     var10 = 0.0F;
                  }

                  var11 = false;
               }
            }

            boolean var17 = !var11;
            if (!PSPrinterJob.shapeTextProp && var17) {
               PSPrinterJob var18 = (PSPrinterJob)this.getPrinterJob();
               if (var18.setFont(this.getFont())) {
                  try {
                     var18.setColor((Color)this.getPaint());
                  } catch (ClassCastException var15) {
                     if (var7 != null) {
                        this.setFont(var7);
                     }

                     throw new IllegalArgumentException("Expected a Color instance");
                  }

                  var18.setTransform(this.getTransform());
                  var18.setClip(this.getClip());
                  var8 = var18.textOut(this, var1, var2 + var9, var3 + var10, var4, var5, var6);
               }
            }

            if (!var8) {
               if (var7 != null) {
                  this.setFont(var7);
                  var7 = null;
               }

               super.drawString(var1, var2, var3, var4, var5, var6);
            }

            if (var7 != null) {
               this.setFont(var7);
            }

         }
      }
   }

   protected boolean drawImageToPlatform(Image var1, AffineTransform var2, Color var3, int var4, int var5, int var6, int var7, boolean var8) {
      BufferedImage var9 = this.getBufferedImage(var1);
      if (var9 == null) {
         return true;
      } else {
         PSPrinterJob var10 = (PSPrinterJob)this.getPrinterJob();
         AffineTransform var11 = this.getTransform();
         if (var2 == null) {
            var2 = new AffineTransform();
         }

         var11.concatenate(var2);
         double[] var12 = new double[6];
         var11.getMatrix(var12);
         Point2D.Float var13 = new Point2D.Float(1.0F, 0.0F);
         Point2D.Float var14 = new Point2D.Float(0.0F, 1.0F);
         var11.deltaTransform(var13, var13);
         var11.deltaTransform(var14, var14);
         Point2D.Float var15 = new Point2D.Float(0.0F, 0.0F);
         double var16 = var13.distance(var15);
         double var18 = var14.distance(var15);
         double var20 = var10.getXRes();
         double var22 = var10.getYRes();
         double var24 = var20 / 72.0D;
         double var26 = var22 / 72.0D;
         int var28 = var11.getType();
         boolean var29 = (var28 & 48) != 0;
         if (var29) {
            if (var16 > var24) {
               var16 = var24;
            }

            if (var18 > var26) {
               var18 = var26;
            }
         }

         if (var16 != 0.0D && var18 != 0.0D) {
            AffineTransform var30 = new AffineTransform(var12[0] / var16, var12[1] / var18, var12[2] / var16, var12[3] / var18, var12[4] / var16, var12[5] / var18);
            Rectangle2D.Float var31 = new Rectangle2D.Float((float)var4, (float)var5, (float)var6, (float)var7);
            Shape var32 = var30.createTransformedShape(var31);
            Rectangle2D var33 = var32.getBounds2D();
            var33.setRect(var33.getX(), var33.getY(), var33.getWidth() + 0.001D, var33.getHeight() + 0.001D);
            int var34 = (int)var33.getWidth();
            int var35 = (int)var33.getHeight();
            if (var34 > 0 && var35 > 0) {
               boolean var36 = true;
               if (!var8 && this.hasTransparentPixels(var9)) {
                  var36 = false;
                  if (this.isBitmaskTransparency(var9)) {
                     if (var3 == null) {
                        if (this.drawBitmaskImage(var9, var2, var3, var4, var5, var6, var7)) {
                           return true;
                        }
                     } else if (var3.getTransparency() == 1) {
                        var36 = true;
                     }
                  }

                  if (!this.canDoRedraws()) {
                     var36 = true;
                  }
               } else {
                  var3 = null;
               }

               if ((var4 + var6 > var9.getWidth((ImageObserver)null) || var5 + var7 > var9.getHeight((ImageObserver)null)) && this.canDoRedraws()) {
                  var36 = false;
               }

               Shape var39;
               if (!var36) {
                  var11.getMatrix(var12);
                  new AffineTransform(var12[0] / var24, var12[1] / var26, var12[2] / var24, var12[3] / var26, var12[4] / var24, var12[5] / var26);
                  Rectangle2D.Float var57 = new Rectangle2D.Float((float)var4, (float)var5, (float)var6, (float)var7);
                  var39 = var11.createTransformedShape(var57);
                  Rectangle2D var58 = var39.getBounds2D();
                  var58.setRect(var58.getX(), var58.getY(), var58.getWidth() + 0.001D, var58.getHeight() + 0.001D);
                  int var59 = (int)var58.getWidth();
                  int var60 = (int)var58.getHeight();
                  int var61 = var59 * var60 * 3;
                  int var62 = 8388608;
                  double var63 = var20 < var22 ? var20 : var22;
                  int var47 = (int)var63;
                  double var48 = 1.0D;
                  double var50 = (double)var59 / (double)var34;
                  double var52 = (double)var60 / (double)var35;
                  double var54 = var50 > var52 ? var52 : var50;
                  int var56 = (int)((double)var47 / var54);
                  if (var56 < 72) {
                     var56 = 72;
                  }

                  while(var61 > var62 && var47 > var56) {
                     var48 *= 2.0D;
                     var47 /= 2;
                     var61 /= 4;
                  }

                  if (var47 < var56) {
                     var48 = var63 / (double)var56;
                  }

                  var58.setRect(var58.getX() / var48, var58.getY() / var48, var58.getWidth() / var48, var58.getHeight() / var48);
                  var10.saveState(this.getTransform(), this.getClip(), var58, var48, var48);
                  return true;
               }

               BufferedImage var37 = new BufferedImage((int)var33.getWidth(), (int)var33.getHeight(), 5);
               Graphics2D var38 = var37.createGraphics();
               var38.clipRect(0, 0, var37.getWidth(), var37.getHeight());
               var38.translate(-var33.getX(), -var33.getY());
               var38.transform(var30);
               if (var3 == null) {
                  var3 = Color.white;
               }

               var38.drawImage(var9, var4, var5, var4 + var6, var5 + var7, var4, var5, var4 + var6, var5 + var7, var3, (ImageObserver)null);
               var39 = this.getClip();
               Shape var40 = this.getTransform().createTransformedShape(var39);
               AffineTransform var41 = AffineTransform.getScaleInstance(var16, var18);
               Shape var42 = var41.createTransformedShape(var32);
               Area var43 = new Area(var42);
               Area var44 = new Area(var40);
               var43.intersect(var44);
               var10.setClip(var43);
               Rectangle2D.Float var45 = new Rectangle2D.Float((float)(var33.getX() * var16), (float)(var33.getY() * var18), (float)(var33.getWidth() * var16), (float)(var33.getHeight() * var18));
               ByteComponentRaster var46 = (ByteComponentRaster)var37.getRaster();
               var10.drawImageBGR(var46.getDataStorage(), var45.x, var45.y, (float)Math.rint((double)var45.width + 0.5D), (float)Math.rint((double)var45.height + 0.5D), 0.0F, 0.0F, (float)var37.getWidth(), (float)var37.getHeight(), var37.getWidth(), var37.getHeight());
               var10.setClip(this.getTransform().createTransformedShape(var39));
               var38.dispose();
            }
         }

         return true;
      }
   }

   public void redrawRegion(Rectangle2D var1, double var2, double var4, Shape var6, AffineTransform var7) throws PrinterException {
      PSPrinterJob var8 = (PSPrinterJob)this.getPrinterJob();
      Printable var9 = this.getPrintable();
      PageFormat var10 = this.getPageFormat();
      int var11 = this.getPageIndex();
      BufferedImage var12 = new BufferedImage((int)var1.getWidth(), (int)var1.getHeight(), 5);
      Graphics2D var13 = var12.createGraphics();
      ProxyGraphics2D var14 = new ProxyGraphics2D(var13, var8);
      var14.setColor(Color.white);
      var14.fillRect(0, 0, var12.getWidth(), var12.getHeight());
      var14.clipRect(0, 0, var12.getWidth(), var12.getHeight());
      var14.translate(-var1.getX(), -var1.getY());
      float var15 = (float)(var8.getXRes() / var2);
      float var16 = (float)(var8.getYRes() / var4);
      var14.scale((double)(var15 / 72.0F), (double)(var16 / 72.0F));
      var14.translate(-var8.getPhysicalPrintableX(var10.getPaper()) / var8.getXRes() * 72.0D, -var8.getPhysicalPrintableY(var10.getPaper()) / var8.getYRes() * 72.0D);
      var14.transform(new AffineTransform(this.getPageFormat().getMatrix()));
      var14.setPaint(Color.black);
      var9.print(var14, var10, var11);
      var13.dispose();
      var8.setClip(var7.createTransformedShape(var6));
      Rectangle2D.Float var17 = new Rectangle2D.Float((float)(var1.getX() * var2), (float)(var1.getY() * var4), (float)(var1.getWidth() * var2), (float)(var1.getHeight() * var4));
      ByteComponentRaster var18 = (ByteComponentRaster)var12.getRaster();
      var8.drawImageBGR(var18.getDataStorage(), var17.x, var17.y, var17.width, var17.height, 0.0F, 0.0F, (float)var12.getWidth(), (float)var12.getHeight(), var12.getWidth(), var12.getHeight());
   }

   protected void deviceFill(PathIterator var1, Color var2) {
      PSPrinterJob var3 = (PSPrinterJob)this.getPrinterJob();
      var3.deviceFill(var1, var2, this.getTransform(), this.getClip());
   }

   protected void deviceFrameRect(int var1, int var2, int var3, int var4, Color var5) {
      this.draw(new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
   }

   protected void deviceDrawLine(int var1, int var2, int var3, int var4, Color var5) {
      this.draw(new Line2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
   }

   protected void deviceFillRect(int var1, int var2, int var3, int var4, Color var5) {
      this.fill(new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
   }

   protected void deviceClip(PathIterator var1) {
   }
}
