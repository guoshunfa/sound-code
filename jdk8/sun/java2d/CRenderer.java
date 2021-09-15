package sun.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.lwawt.macosx.CPrinterSurfaceData;

public class CRenderer implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe, DrawImagePipe {
   Line2D lineToShape;
   Rectangle2D rectToShape;
   RoundRectangle2D roundrectToShape;
   Ellipse2D ovalToShape;
   Arc2D arcToShape;

   static native void init();

   native void doLine(SurfaceData var1, float var2, float var3, float var4, float var5);

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.drawLine(var1, (float)var2, (float)var3, (float)var4, (float)var5);
   }

   public void drawLine(SunGraphics2D var1, float var2, float var3, float var4, float var5) {
      OSXSurfaceData var6 = (OSXSurfaceData)var1.getSurfaceData();
      if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
         var6.doLine(this, var1, var2, var3, var4, var5);
      } else {
         if (this.lineToShape == null) {
            synchronized(this) {
               if (this.lineToShape == null) {
                  this.lineToShape = new Line2D.Float();
               }
            }
         }

         synchronized(this.lineToShape) {
            this.lineToShape.setLine((double)var2, (double)var3, (double)var4, (double)var5);
            this.drawfillShape(var1, var1.stroke.createStrokedShape(this.lineToShape), true, true);
         }
      }

   }

   native void doRect(SurfaceData var1, float var2, float var3, float var4, float var5, boolean var6);

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.drawRect(var1, (float)var2, (float)var3, (float)var4, (float)var5);
   }

   public void drawRect(SunGraphics2D var1, float var2, float var3, float var4, float var5) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var6 = (OSXSurfaceData)var1.getSurfaceData();
         if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
            var6.doRect(this, var1, var2, var3, var4, var5, false);
         } else {
            if (this.rectToShape == null) {
               synchronized(this) {
                  if (this.rectToShape == null) {
                     this.rectToShape = new Rectangle2D.Float();
                  }
               }
            }

            synchronized(this.rectToShape) {
               this.rectToShape.setRect((double)var2, (double)var3, (double)var4, (double)var5);
               this.drawfillShape(var1, var1.stroke.createStrokedShape(this.rectToShape), true, true);
            }
         }

      }
   }

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.fillRect(var1, (float)var2, (float)var3, (float)var4, (float)var5);
   }

   public void fillRect(SunGraphics2D var1, float var2, float var3, float var4, float var5) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var6 = (OSXSurfaceData)var1.getSurfaceData();
         var6.doRect(this, var1, var2, var3, var4, var5, true);
      }

   }

   native void doRoundRect(SurfaceData var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8);

   public void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.drawRoundRect(var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7);
   }

   public void drawRoundRect(SunGraphics2D var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var8 = (OSXSurfaceData)var1.getSurfaceData();
         if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
            var8.doRoundRect(this, var1, var2, var3, var4, var5, var6, var7, false);
         } else {
            if (this.roundrectToShape == null) {
               synchronized(this) {
                  if (this.roundrectToShape == null) {
                     this.roundrectToShape = new RoundRectangle2D.Float();
                  }
               }
            }

            synchronized(this.roundrectToShape) {
               this.roundrectToShape.setRoundRect((double)var2, (double)var3, (double)var4, (double)var5, (double)var6, (double)var7);
               this.drawfillShape(var1, var1.stroke.createStrokedShape(this.roundrectToShape), true, true);
            }
         }

      }
   }

   public void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fillRoundRect(var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7);
   }

   public void fillRoundRect(SunGraphics2D var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var8 = (OSXSurfaceData)var1.getSurfaceData();
         var8.doRoundRect(this, var1, var2, var3, var4, var5, var6, var7, true);
      }
   }

   native void doOval(SurfaceData var1, float var2, float var3, float var4, float var5, boolean var6);

   public void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.drawOval(var1, (float)var2, (float)var3, (float)var4, (float)var5);
   }

   public void drawOval(SunGraphics2D var1, float var2, float var3, float var4, float var5) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var6 = (OSXSurfaceData)var1.getSurfaceData();
         if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
            var6.doOval(this, var1, var2, var3, var4, var5, false);
         } else {
            if (this.ovalToShape == null) {
               synchronized(this) {
                  if (this.ovalToShape == null) {
                     this.ovalToShape = new Ellipse2D.Float();
                  }
               }
            }

            synchronized(this.ovalToShape) {
               this.ovalToShape.setFrame((double)var2, (double)var3, (double)var4, (double)var5);
               this.drawfillShape(var1, var1.stroke.createStrokedShape(this.ovalToShape), true, true);
            }
         }

      }
   }

   public void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      this.fillOval(var1, (float)var2, (float)var3, (float)var4, (float)var5);
   }

   public void fillOval(SunGraphics2D var1, float var2, float var3, float var4, float var5) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var6 = (OSXSurfaceData)var1.getSurfaceData();
         var6.doOval(this, var1, var2, var3, var4, var5, true);
      }
   }

   native void doArc(SurfaceData var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, boolean var9);

   public void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.drawArc(var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 0);
   }

   public void drawArc(SunGraphics2D var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var9 = (OSXSurfaceData)var1.getSurfaceData();
         if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
            var9.doArc(this, var1, var2, var3, var4, var5, var6, var7, var8, false);
         } else {
            if (this.arcToShape == null) {
               synchronized(this) {
                  if (this.arcToShape == null) {
                     this.arcToShape = new Arc2D.Float();
                  }
               }
            }

            synchronized(this.arcToShape) {
               this.arcToShape.setArc((double)var2, (double)var3, (double)var4, (double)var5, (double)var6, (double)var7, var8);
               this.drawfillShape(var1, var1.stroke.createStrokedShape(this.arcToShape), true, true);
            }
         }

      }
   }

   public void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.fillArc(var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, 2);
   }

   public void fillArc(SunGraphics2D var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      if (var4 >= 0.0F && var5 >= 0.0F) {
         OSXSurfaceData var9 = (OSXSurfaceData)var1.getSurfaceData();
         var9.doArc(this, var1, var2, var3, var4, var5, var6, var7, var8, true);
      }
   }

   native void doPoly(SurfaceData var1, int[] var2, int[] var3, int var4, boolean var5, boolean var6);

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      OSXSurfaceData var5 = (OSXSurfaceData)var1.getSurfaceData();
      if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
         var5.doPolygon(this, var1, var2, var3, var4, false, false);
      } else {
         GeneralPath var6 = new GeneralPath();
         var6.moveTo((float)var2[0], (float)var3[0]);

         for(int var7 = 1; var7 < var4; ++var7) {
            var6.lineTo((float)var2[var7], (float)var3[var7]);
         }

         this.drawfillShape(var1, var1.stroke.createStrokedShape(var6), true, true);
      }

   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      OSXSurfaceData var5 = (OSXSurfaceData)var1.getSurfaceData();
      if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
         var5.doPolygon(this, var1, var2, var3, var4, true, false);
      } else {
         GeneralPath var6 = new GeneralPath();
         var6.moveTo((float)var2[0], (float)var3[0]);

         for(int var7 = 1; var7 < var4; ++var7) {
            var6.lineTo((float)var2[var7], (float)var3[var7]);
         }

         var6.lineTo((float)var2[0], (float)var3[0]);
         this.drawfillShape(var1, var1.stroke.createStrokedShape(var6), true, true);
      }

   }

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      OSXSurfaceData var5 = (OSXSurfaceData)var1.getSurfaceData();
      var5.doPolygon(this, var1, var2, var3, var4, true, true);
   }

   native void doShape(SurfaceData var1, int var2, FloatBuffer var3, IntBuffer var4, int var5, boolean var6, boolean var7);

   void drawfillShape(SunGraphics2D var1, Shape var2, boolean var3, boolean var4) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         OSXSurfaceData var5 = (OSXSurfaceData)var1.getSurfaceData();
         boolean var6 = true;
         GeneralPath var7;
         PathIterator var8;
         if (var6 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
            float var9;
            float var10;
            float var11;
            float var15;
            if (var2 instanceof Rectangle2D) {
               Rectangle2D var14 = (Rectangle2D)var2;
               var15 = (float)var14.getX();
               var9 = (float)var14.getY();
               var10 = (float)var14.getWidth();
               var11 = (float)var14.getHeight();
               if (var3) {
                  this.fillRect(var1, var15, var9, var10, var11);
               } else {
                  this.drawRect(var1, var15, var9, var10, var11);
               }
            } else if (var2 instanceof Ellipse2D) {
               Ellipse2D var16 = (Ellipse2D)var2;
               var15 = (float)var16.getX();
               var9 = (float)var16.getY();
               var10 = (float)var16.getWidth();
               var11 = (float)var16.getHeight();
               if (var3) {
                  this.fillOval(var1, var15, var9, var10, var11);
               } else {
                  this.drawOval(var1, var15, var9, var10, var11);
               }
            } else {
               float var12;
               float var13;
               if (var2 instanceof Arc2D) {
                  Arc2D var17 = (Arc2D)var2;
                  var15 = (float)var17.getX();
                  var9 = (float)var17.getY();
                  var10 = (float)var17.getWidth();
                  var11 = (float)var17.getHeight();
                  var12 = (float)var17.getAngleStart();
                  var13 = (float)var17.getAngleExtent();
                  if (var3) {
                     this.fillArc(var1, var15, var9, var10, var11, var12, var13, var17.getArcType());
                  } else {
                     this.drawArc(var1, var15, var9, var10, var11, var12, var13, var17.getArcType());
                  }
               } else if (var2 instanceof RoundRectangle2D) {
                  RoundRectangle2D var18 = (RoundRectangle2D)var2;
                  var15 = (float)var18.getX();
                  var9 = (float)var18.getY();
                  var10 = (float)var18.getWidth();
                  var11 = (float)var18.getHeight();
                  var12 = (float)var18.getArcWidth();
                  var13 = (float)var18.getArcHeight();
                  if (var3) {
                     this.fillRoundRect(var1, var15, var9, var10, var11, var12, var13);
                  } else {
                     this.drawRoundRect(var1, var15, var9, var10, var11, var12, var13);
                  }
               } else if (var2 instanceof Line2D) {
                  Line2D var19 = (Line2D)var2;
                  var15 = (float)var19.getX1();
                  var9 = (float)var19.getY1();
                  var10 = (float)var19.getX2();
                  var11 = (float)var19.getY2();
                  this.drawLine(var1, var15, var9, var10, var11);
               } else if (var2 instanceof Point2D) {
                  Point2D var20 = (Point2D)var2;
                  var15 = (float)var20.getX();
                  var9 = (float)var20.getY();
                  this.drawLine(var1, var15, var9, var15, var9);
               } else {
                  if (var2 instanceof GeneralPath) {
                     var7 = (GeneralPath)var2;
                  } else {
                     var7 = new GeneralPath(var2);
                  }

                  var8 = var7.getPathIterator((AffineTransform)null);
                  if (!var8.isDone()) {
                     var5.drawfillShape(this, var1, var7, var3, var4);
                  }
               }
            }
         } else {
            if (var2 instanceof GeneralPath) {
               var7 = (GeneralPath)var2;
            } else {
               var7 = new GeneralPath(var2);
            }

            var8 = var7.getPathIterator((AffineTransform)null);
            if (!var8.isDone()) {
               var5.drawfillShape(this, var1, var7, var3, var4);
            }
         }

      }
   }

   public void draw(SunGraphics2D var1, Shape var2) {
      OSXSurfaceData var3 = (OSXSurfaceData)var1.getSurfaceData();
      if (var1.strokeState != 3 && OSXSurfaceData.IsSimpleColor(var1.paint)) {
         this.drawfillShape(var1, var2, false, true);
      } else {
         this.drawfillShape(var1, var1.stroke.createStrokedShape(var2), true, true);
      }

   }

   public void fill(SunGraphics2D var1, Shape var2) {
      this.drawfillShape(var1, var2, true, false);
   }

   native void doImage(SurfaceData var1, SurfaceData var2, boolean var3, boolean var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14);

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, Color var7) {
      OSXSurfaceData var8 = (OSXSurfaceData)var1.getSurfaceData();
      byte var9 = 0;
      byte var10 = 0;
      int var11 = var2.getWidth((ImageObserver)null);
      int var12 = var2.getHeight((ImageObserver)null);
      return this.scaleImage(var1, var2, var3, var4, var3 + var5, var4 + var6, var9, var10, var9 + var11, var10 + var12, var7);
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Color var11) {
      boolean var20 = false;
      boolean var21 = false;
      boolean var22 = false;
      boolean var23 = false;
      int var12;
      int var16;
      if (var9 > var7) {
         var12 = var9 - var7;
         var16 = var7;
      } else {
         var20 = true;
         var12 = var7 - var9;
         var16 = var9;
      }

      int var13;
      int var17;
      if (var10 > var8) {
         var13 = var10 - var8;
         var17 = var8;
      } else {
         var21 = true;
         var13 = var8 - var10;
         var17 = var10;
      }

      int var14;
      int var18;
      if (var5 > var3) {
         var14 = var5 - var3;
         var18 = var3;
      } else {
         var14 = var3 - var5;
         var22 = true;
         var18 = var5;
      }

      int var15;
      int var19;
      if (var6 > var4) {
         var15 = var6 - var4;
         var19 = var4;
      } else {
         var15 = var4 - var6;
         var23 = true;
         var19 = var6;
      }

      if (var12 > 0 && var13 > 0) {
         boolean var24 = var21 != var23;
         boolean var25 = var20 != var22;
         return this.blitImage(var1, var2, var25, var24, var16, var17, var12, var13, var18, var19, var14, var15, var11);
      } else {
         return true;
      }
   }

   protected boolean blitImage(SunGraphics2D var1, Image var2, boolean var3, boolean var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, Color var13) {
      CPrinterSurfaceData var14 = (CPrinterSurfaceData)var1.getSurfaceData();
      OSXOffScreenSurfaceData var15 = OSXOffScreenSurfaceData.createNewSurface((BufferedImage)var2);
      var14.blitImage(this, var1, var15, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      return true;
   }

   protected boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, Color var5) {
      if (var2 == null) {
         return true;
      } else {
         byte var6 = 0;
         byte var7 = 0;
         int var8 = var2.getWidth((ImageObserver)null);
         int var9 = var2.getHeight((ImageObserver)null);
         return this.blitImage(var1, var2, false, false, var6, var7, var8, var9, var3, var4, var8, var9, var5);
      }
   }

   protected boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, Color var9) {
      return this.blitImage(var1, var2, false, false, var5, var6, var7, var8, var3, var4, var7, var8, var9);
   }

   protected void transformImage(SunGraphics2D var1, Image var2, int var3, int var4, BufferedImageOp var5, AffineTransform var6, Color var7) {
      if (var2 != null) {
         int var8 = ((Image)var2).getWidth((ImageObserver)null);
         int var9 = ((Image)var2).getHeight((ImageObserver)null);
         if (var5 != null && var2 instanceof BufferedImage) {
            if (((BufferedImage)var2).getType() == 0) {
               BufferedImage var10 = null;
               var10 = new BufferedImage(var8, var9, 3);
               Graphics2D var11 = var10.createGraphics();
               var11.drawImage((Image)var2, 0, 0, (ImageObserver)null);
               var11.dispose();
               var2 = var5.filter(var10, (BufferedImage)null);
            } else {
               var2 = var5.filter((BufferedImage)var2, (BufferedImage)null);
            }

            var8 = ((Image)var2).getWidth((ImageObserver)null);
            var9 = ((Image)var2).getHeight((ImageObserver)null);
         }

         if (var6 != null) {
            AffineTransform var12 = var1.getTransform();
            var1.transform(var6);
            this.scaleImage(var1, (Image)var2, var3, var4, var3 + var8, var4 + var9, 0, 0, var8, var9, var7);
            var1.setTransform(var12);
         } else {
            this.scaleImage(var1, (Image)var2, var3, var4, var3 + var8, var4 + var9, 0, 0, var8, var9, var7);
         }

      } else {
         throw new NullPointerException();
      }
   }

   protected boolean imageReady(ToolkitImage var1, ImageObserver var2) {
      if (var1.hasError()) {
         if (var2 != null) {
            var2.imageUpdate(var1, 192, -1, -1, -1, -1);
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, Color var5, ImageObserver var6) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof ToolkitImage)) {
         return this.copyImage(var1, var2, var3, var4, var5);
      } else {
         ToolkitImage var7 = (ToolkitImage)var2;
         if (!this.imageReady(var7, var6)) {
            return false;
         } else {
            ImageRepresentation var8 = var7.getImageRep();
            return var8.drawToBufImage(var1, var7, var3, var4, var5, var6);
         }
      }
   }

   public boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, Color var9, ImageObserver var10) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof ToolkitImage)) {
         return this.copyImage(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         ToolkitImage var11 = (ToolkitImage)var2;
         if (!this.imageReady(var11, var10)) {
            return false;
         } else {
            ImageRepresentation var12 = var11.getImageRep();
            return var12.drawToBufImage(var1, var11, var3, var4, var3 + var7, var4 + var8, var5, var6, var5 + var7, var6 + var8, (Color)null, var10);
         }
      }
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, Color var7, ImageObserver var8) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof ToolkitImage)) {
         return this.scaleImage(var1, var2, var3, var4, var5, var6, var7);
      } else {
         ToolkitImage var9 = (ToolkitImage)var2;
         if (!this.imageReady(var9, var8)) {
            return false;
         } else {
            ImageRepresentation var10 = var9.getImageRep();
            return var10.drawToBufImage(var1, var9, var3, var4, var5, var6, var7, var8);
         }
      }
   }

   public boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Color var11, ImageObserver var12) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof ToolkitImage)) {
         return this.scaleImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      } else {
         ToolkitImage var13 = (ToolkitImage)var2;
         if (!this.imageReady(var13, var12)) {
            return false;
         } else {
            ImageRepresentation var14 = var13.getImageRep();
            return var14.drawToBufImage(var1, var13, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
         }
      }
   }

   public boolean transformImage(SunGraphics2D var1, Image var2, AffineTransform var3, ImageObserver var4) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof ToolkitImage)) {
         this.transformImage(var1, var2, 0, 0, (BufferedImageOp)null, var3, (Color)null);
         return true;
      } else {
         ToolkitImage var5 = (ToolkitImage)var2;
         if (!this.imageReady(var5, var4)) {
            return false;
         } else {
            ImageRepresentation var6 = var5.getImageRep();
            return var6.drawToBufImage(var1, var5, var3, var4);
         }
      }
   }

   public void transformImage(SunGraphics2D var1, BufferedImage var2, BufferedImageOp var3, int var4, int var5) {
      if (var2 != null) {
         this.transformImage(var1, var2, var4, var5, var3, (AffineTransform)null, (Color)null);
      } else {
         throw new NullPointerException();
      }
   }

   public CRenderer traceWrap() {
      return new CRenderer.Tracer();
   }

   static {
      init();
   }

   public static class Tracer extends CRenderer {
      void doLine(SurfaceData var1, float var2, float var3, float var4, float var5) {
         GraphicsPrimitive.tracePrimitive("QuartzLine");
         super.doLine(var1, var2, var3, var4, var5);
      }

      void doRect(SurfaceData var1, float var2, float var3, float var4, float var5, boolean var6) {
         GraphicsPrimitive.tracePrimitive("QuartzRect");
         super.doRect(var1, var2, var3, var4, var5, var6);
      }

      void doRoundRect(SurfaceData var1, float var2, float var3, float var4, float var5, float var6, float var7, boolean var8) {
         GraphicsPrimitive.tracePrimitive("QuartzRoundRect");
         super.doRoundRect(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      void doOval(SurfaceData var1, float var2, float var3, float var4, float var5, boolean var6) {
         GraphicsPrimitive.tracePrimitive("QuartzOval");
         super.doOval(var1, var2, var3, var4, var5, var6);
      }

      void doArc(SurfaceData var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, boolean var9) {
         GraphicsPrimitive.tracePrimitive("QuartzArc");
         super.doArc(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

      void doPoly(SurfaceData var1, int[] var2, int[] var3, int var4, boolean var5, boolean var6) {
         GraphicsPrimitive.tracePrimitive("QuartzDoPoly");
         super.doPoly(var1, var2, var3, var4, var5, var6);
      }

      void doShape(SurfaceData var1, int var2, FloatBuffer var3, IntBuffer var4, int var5, boolean var6, boolean var7) {
         GraphicsPrimitive.tracePrimitive("QuartzFillOrDrawShape");
         super.doShape(var1, var2, var3, var4, var5, var6, var7);
      }

      void doImage(SurfaceData var1, SurfaceData var2, boolean var3, boolean var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14) {
         GraphicsPrimitive.tracePrimitive("QuartzDrawImage");
         super.doImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
      }
   }
}
