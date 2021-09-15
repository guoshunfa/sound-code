package sun.print;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import sun.awt.image.SunWritableRaster;
import sun.awt.image.ToolkitImage;
import sun.font.CompositeFont;
import sun.font.Font2D;
import sun.font.Font2DHandle;
import sun.font.FontUtilities;
import sun.font.PhysicalFont;

public abstract class PathGraphics extends ProxyGraphics2D {
   private Printable mPainter;
   private PageFormat mPageFormat;
   private int mPageIndex;
   private boolean mCanRedraw;
   protected boolean printingGlyphVector;
   protected static SoftReference<Hashtable<Font2DHandle, Object>> fontMapRef = new SoftReference((Object)null);

   protected PathGraphics(Graphics2D var1, PrinterJob var2, Printable var3, PageFormat var4, int var5, boolean var6) {
      super(var1, var2);
      this.mPainter = var3;
      this.mPageFormat = var4;
      this.mPageIndex = var5;
      this.mCanRedraw = var6;
   }

   protected Printable getPrintable() {
      return this.mPainter;
   }

   protected PageFormat getPageFormat() {
      return this.mPageFormat;
   }

   protected int getPageIndex() {
      return this.mPageIndex;
   }

   public boolean canDoRedraws() {
      return this.mCanRedraw;
   }

   public abstract void redrawRegion(Rectangle2D var1, double var2, double var4, Shape var6, AffineTransform var7) throws PrinterException;

   public void drawLine(int var1, int var2, int var3, int var4) {
      Paint var5 = this.getPaint();

      try {
         AffineTransform var6 = this.getTransform();
         if (this.getClip() != null) {
            this.deviceClip(this.getClip().getPathIterator(var6));
         }

         this.deviceDrawLine(var1, var2, var3, var4, (Color)var5);
      } catch (ClassCastException var7) {
         throw new IllegalArgumentException("Expected a Color instance");
      }
   }

   public void drawRect(int var1, int var2, int var3, int var4) {
      Paint var5 = this.getPaint();

      try {
         AffineTransform var6 = this.getTransform();
         if (this.getClip() != null) {
            this.deviceClip(this.getClip().getPathIterator(var6));
         }

         this.deviceFrameRect(var1, var2, var3, var4, (Color)var5);
      } catch (ClassCastException var7) {
         throw new IllegalArgumentException("Expected a Color instance");
      }
   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      Paint var5 = this.getPaint();

      try {
         AffineTransform var6 = this.getTransform();
         if (this.getClip() != null) {
            this.deviceClip(this.getClip().getPathIterator(var6));
         }

         this.deviceFillRect(var1, var2, var3, var4, (Color)var5);
      } catch (ClassCastException var7) {
         throw new IllegalArgumentException("Expected a Color instance");
      }
   }

   public void clearRect(int var1, int var2, int var3, int var4) {
      this.fill(new Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4), this.getBackground());
   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.draw(new RoundRectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6));
   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.fill(new RoundRectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6));
   }

   public void drawOval(int var1, int var2, int var3, int var4) {
      this.draw(new Ellipse2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
   }

   public void fillOval(int var1, int var2, int var3, int var4) {
      this.fill(new Ellipse2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.draw(new Arc2D.Float((float)var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, 0));
   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.fill(new Arc2D.Float((float)var1, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, 2));
   }

   public void drawPolyline(int[] var1, int[] var2, int var3) {
      if (var3 > 0) {
         float var4 = (float)var1[0];
         float var5 = (float)var2[0];

         for(int var8 = 1; var8 < var3; ++var8) {
            float var6 = (float)var1[var8];
            float var7 = (float)var2[var8];
            this.draw(new Line2D.Float(var4, var5, var6, var7));
            var4 = var6;
            var5 = var7;
         }
      }

   }

   public void drawPolygon(int[] var1, int[] var2, int var3) {
      this.draw(new Polygon(var1, var2, var3));
   }

   public void drawPolygon(Polygon var1) {
      this.draw(var1);
   }

   public void fillPolygon(int[] var1, int[] var2, int var3) {
      this.fill(new Polygon(var1, var2, var3));
   }

   public void fillPolygon(Polygon var1) {
      this.fill(var1);
   }

   public void drawString(String var1, int var2, int var3) {
      this.drawString(var1, (float)var2, (float)var3);
   }

   public void drawString(String var1, float var2, float var3) {
      if (var1.length() != 0) {
         TextLayout var4 = new TextLayout(var1, this.getFont(), this.getFontRenderContext());
         var4.draw(this, var2, var3);
      }
   }

   protected void drawString(String var1, float var2, float var3, Font var4, FontRenderContext var5, float var6) {
      TextLayout var7 = new TextLayout(var1, var4, var5);
      Shape var8 = var7.getOutline(AffineTransform.getTranslateInstance((double)var2, (double)var3));
      this.fill(var8);
   }

   public void drawString(AttributedCharacterIterator var1, int var2, int var3) {
      this.drawString(var1, (float)var2, (float)var3);
   }

   public void drawString(AttributedCharacterIterator var1, float var2, float var3) {
      if (var1 == null) {
         throw new NullPointerException("attributedcharacteriterator is null");
      } else {
         TextLayout var4 = new TextLayout(var1, this.getFontRenderContext());
         var4.draw(this, var2, var3);
      }
   }

   public void drawGlyphVector(GlyphVector var1, float var2, float var3) {
      if (this.printingGlyphVector) {
         assert !this.printingGlyphVector;

         this.fill(var1.getOutline(var2, var3));
      } else {
         try {
            this.printingGlyphVector = true;
            if (RasterPrinterJob.shapeTextProp || !this.printedSimpleGlyphVector(var1, var2, var3)) {
               this.fill(var1.getOutline(var2, var3));
            }
         } finally {
            this.printingGlyphVector = false;
         }

      }
   }

   protected int platformFontCount(Font var1, String var2) {
      return 0;
   }

   protected boolean printGlyphVector(GlyphVector var1, float var2, float var3) {
      return false;
   }

   boolean printedSimpleGlyphVector(GlyphVector var1, float var2, float var3) {
      int var4 = var1.getLayoutFlags();
      if (var4 != 0 && var4 != 2) {
         return this.printGlyphVector(var1, var2, var3);
      } else {
         Font var5 = var1.getFont();
         Font2D var6 = FontUtilities.getFont2D(var5);
         if (var6.handle.font2D != var6) {
            return false;
         } else {
            Class var8 = PathGraphics.class;
            Hashtable var7;
            synchronized(PathGraphics.class) {
               var7 = (Hashtable)fontMapRef.get();
               if (var7 == null) {
                  var7 = new Hashtable();
                  fontMapRef = new SoftReference(var7);
               }
            }

            int var33 = var1.getNumGlyphs();
            int[] var9 = var1.getGlyphCodes(0, var33, (int[])null);
            char[] var10 = null;
            char[][] var11 = (char[][])null;
            CompositeFont var12 = null;
            int var14;
            int var15;
            synchronized(var7) {
               if (var6 instanceof CompositeFont) {
                  var12 = (CompositeFont)var6;
                  var14 = var12.getNumSlots();
                  var11 = (char[][])((char[][])var7.get(var6.handle));
                  if (var11 == null) {
                     var11 = new char[var14][];
                     var7.put(var6.handle, var11);
                  }

                  for(var15 = 0; var15 < var33; ++var15) {
                     int var16 = var9[var15] >>> 24;
                     if (var16 >= var14) {
                        return false;
                     }

                     if (var11[var16] == null) {
                        PhysicalFont var17 = var12.getSlotFont(var16);
                        char[] var18 = (char[])((char[])var7.get(var17.handle));
                        if (var18 == null) {
                           var18 = getGlyphToCharMapForFont(var17);
                        }

                        var11[var16] = var18;
                     }
                  }
               } else {
                  var10 = (char[])((char[])var7.get(var6.handle));
                  if (var10 == null) {
                     var10 = getGlyphToCharMapForFont(var6);
                     var7.put(var6.handle, var10);
                  }
               }
            }

            char[] var13 = new char[var33];
            if (var12 != null) {
               for(var14 = 0; var14 < var33; ++var14) {
                  var15 = var9[var14];
                  char[] var37 = var11[var15 >>> 24];
                  var15 &= 16777215;
                  if (var37 == null) {
                     return false;
                  }

                  char var38;
                  if (var15 == 65535) {
                     var38 = '\n';
                  } else {
                     if (var15 < 0 || var15 >= var37.length) {
                        return false;
                     }

                     var38 = var37[var15];
                  }

                  if (var38 == '\uffff') {
                     return false;
                  }

                  var13[var14] = var38;
               }
            } else {
               for(var14 = 0; var14 < var33; ++var14) {
                  var15 = var9[var14];
                  char var35;
                  if (var15 == 65535) {
                     var35 = '\n';
                  } else {
                     if (var15 < 0 || var15 >= var10.length) {
                        return false;
                     }

                     var35 = var10[var15];
                  }

                  if (var35 == '\uffff') {
                     return false;
                  }

                  var13[var14] = var35;
               }
            }

            FontRenderContext var34 = var1.getFontRenderContext();
            GlyphVector var36 = var5.createGlyphVector(var34, var13);
            if (var36.getNumGlyphs() != var33) {
               return this.printGlyphVector(var1, var2, var3);
            } else {
               int[] var39 = var36.getGlyphCodes(0, var33, (int[])null);

               for(int var40 = 0; var40 < var33; ++var40) {
                  if (var9[var40] != var39[var40]) {
                     return this.printGlyphVector(var1, var2, var3);
                  }
               }

               FontRenderContext var41 = this.getFontRenderContext();
               boolean var42 = var34.equals(var41);
               if (!var42 && var34.usesFractionalMetrics() == var41.usesFractionalMetrics()) {
                  AffineTransform var19 = var34.getTransform();
                  AffineTransform var20 = this.getTransform();
                  double[] var21 = new double[4];
                  double[] var22 = new double[4];
                  var19.getMatrix(var21);
                  var20.getMatrix(var22);
                  var42 = true;

                  for(int var23 = 0; var23 < 4; ++var23) {
                     if (var21[var23] != var22[var23]) {
                        var42 = false;
                        break;
                     }
                  }
               }

               String var43 = new String(var13, 0, var33);
               int var44 = this.platformFontCount(var5, var43);
               if (var44 == 0) {
                  return false;
               } else {
                  float[] var45 = var1.getGlyphPositions(0, var33, (float[])null);
                  boolean var46 = (var4 & 2) == 0 || this.samePositions(var36, var39, var9, var45);
                  Point2D var47 = var1.getGlyphPosition(var33);
                  float var24 = (float)var47.getX();
                  boolean var25 = false;
                  if (var5.hasLayoutAttributes() && this.printingGlyphVector && var46) {
                     Map var26 = var5.getAttributes();
                     Object var27 = var26.get(TextAttribute.TRACKING);
                     boolean var28 = var27 != null && var27 instanceof Number && ((Number)var27).floatValue() != 0.0F;
                     if (var28) {
                        var46 = false;
                     } else {
                        Rectangle2D var29 = var5.getStringBounds(var43, var34);
                        float var30 = (float)var29.getWidth();
                        if ((double)Math.abs(var30 - var24) > 1.0E-5D) {
                           var25 = true;
                        }
                     }
                  }

                  if (var42 && var46 && !var25) {
                     this.drawString(var43, var2, var3, var5, var34, 0.0F);
                     return true;
                  } else if (var44 == 1 && this.canDrawStringToWidth() && var46) {
                     this.drawString(var43, var2, var3, var5, var34, var24);
                     return true;
                  } else if (FontUtilities.isComplexText(var13, 0, var13.length)) {
                     return this.printGlyphVector(var1, var2, var3);
                  } else if (var33 > 10 && this.printGlyphVector(var1, var2, var3)) {
                     return true;
                  } else {
                     for(int var48 = 0; var48 < var33; ++var48) {
                        String var49 = new String(var13, var48, 1);
                        this.drawString(var49, var2 + var45[var48 * 2], var3 + var45[var48 * 2 + 1], var5, var34, 0.0F);
                     }

                     return true;
                  }
               }
            }
         }
      }
   }

   private boolean samePositions(GlyphVector var1, int[] var2, int[] var3, float[] var4) {
      int var5 = var1.getNumGlyphs();
      float[] var6 = var1.getGlyphPositions(0, var5, (float[])null);
      if (var5 == var2.length && var3.length == var2.length && var4.length == var6.length) {
         for(int var7 = 0; var7 < var5; ++var7) {
            if (var2[var7] != var3[var7] || var6[var7] != var4[var7]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean canDrawStringToWidth() {
      return false;
   }

   private static char[] getGlyphToCharMapForFont(Font2D var0) {
      int var1 = var0.getNumGlyphs();
      int var2 = var0.getMissingGlyphCode();
      char[] var3 = new char[var1];

      for(int var5 = 0; var5 < var1; ++var5) {
         var3[var5] = '\uffff';
      }

      for(char var6 = 0; var6 < '\uffff'; ++var6) {
         if (var6 < '\ud800' || var6 > '\udfff') {
            int var4 = var0.charToGlyph(var6);
            if (var4 != var2 && var4 >= 0 && var4 < var1 && var3[var4] == '\uffff') {
               var3[var4] = var6;
            }
         }
      }

      return var3;
   }

   public void draw(Shape var1) {
      this.fill(this.getStroke().createStrokedShape(var1));
   }

   public void fill(Shape var1) {
      Paint var2 = this.getPaint();

      try {
         this.fill(var1, (Color)var2);
      } catch (ClassCastException var4) {
         throw new IllegalArgumentException("Expected a Color instance");
      }
   }

   public void fill(Shape var1, Color var2) {
      AffineTransform var3 = this.getTransform();
      if (this.getClip() != null) {
         this.deviceClip(this.getClip().getPathIterator(var3));
      }

      this.deviceFill(var1.getPathIterator(var3), var2);
   }

   protected abstract void deviceFill(PathIterator var1, Color var2);

   protected abstract void deviceClip(PathIterator var1);

   protected abstract void deviceFrameRect(int var1, int var2, int var3, int var4, Color var5);

   protected abstract void deviceDrawLine(int var1, int var2, int var3, int var4, Color var5);

   protected abstract void deviceFillRect(int var1, int var2, int var3, int var4, Color var5);

   protected BufferedImage getBufferedImage(Image var1) {
      if (var1 instanceof BufferedImage) {
         return (BufferedImage)var1;
      } else if (var1 instanceof ToolkitImage) {
         return ((ToolkitImage)var1).getBufferedImage();
      } else {
         return var1 instanceof VolatileImage ? ((VolatileImage)var1).getSnapshot() : null;
      }
   }

   protected boolean hasTransparentPixels(BufferedImage var1) {
      ColorModel var2 = var1.getColorModel();
      boolean var3 = var2 == null ? true : var2.getTransparency() != 1;
      if (var3 && var1 != null && (var1.getType() == 2 || var1.getType() == 3)) {
         DataBuffer var4 = var1.getRaster().getDataBuffer();
         SampleModel var5 = var1.getRaster().getSampleModel();
         if (var4 instanceof DataBufferInt && var5 instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel var6 = (SinglePixelPackedSampleModel)var5;
            int[] var7 = SunWritableRaster.stealData((DataBufferInt)((DataBufferInt)var4), 0);
            int var8 = var1.getMinX();
            int var9 = var1.getMinY();
            int var10 = var1.getWidth();
            int var11 = var1.getHeight();
            int var12 = var6.getScanlineStride();
            boolean var13 = false;

            for(int var14 = var9; var14 < var9 + var11; ++var14) {
               int var15 = var14 * var12;

               for(int var16 = var8; var16 < var8 + var10; ++var16) {
                  if ((var7[var15 + var16] & -16777216) != -16777216) {
                     var13 = true;
                     break;
                  }
               }

               if (var13) {
                  break;
               }
            }

            if (!var13) {
               var3 = false;
            }
         }
      }

      return var3;
   }

   protected boolean isBitmaskTransparency(BufferedImage var1) {
      ColorModel var2 = var1.getColorModel();
      return var2 != null && var2.getTransparency() == 2;
   }

   protected boolean drawBitmaskImage(BufferedImage var1, AffineTransform var2, Color var3, int var4, int var5, int var6, int var7) {
      ColorModel var8 = var1.getColorModel();
      if (!(var8 instanceof IndexColorModel)) {
         return false;
      } else {
         IndexColorModel var9 = (IndexColorModel)var8;
         if (var8.getTransparency() != 2) {
            return false;
         } else if (var3 != null && var3.getAlpha() < 128) {
            return false;
         } else if ((var2.getType() & -12) != 0) {
            return false;
         } else if ((this.getTransform().getType() & -12) != 0) {
            return false;
         } else {
            BufferedImage var11 = null;
            WritableRaster var12 = var1.getRaster();
            int var13 = var9.getTransparentPixel();
            byte[] var14 = new byte[var9.getMapSize()];
            var9.getAlphas(var14);
            if (var13 >= 0) {
               var14[var13] = 0;
            }

            int var15 = var12.getWidth();
            int var16 = var12.getHeight();
            if (var4 <= var15 && var5 <= var16) {
               int var17;
               int var19;
               if (var4 + var6 > var15) {
                  var17 = var15;
                  var19 = var15 - var4;
               } else {
                  var17 = var4 + var6;
                  var19 = var6;
               }

               int var18;
               if (var5 + var7 > var16) {
                  var18 = var16;
                  int var10000 = var16 - var5;
               } else {
                  var18 = var5 + var7;
               }

               int[] var10 = new int[var19];

               for(int var21 = var5; var21 < var18; ++var21) {
                  int var22 = -1;
                  var12.getPixels(var4, var21, var19, 1, (int[])var10);

                  for(int var23 = var4; var23 < var17; ++var23) {
                     if (var14[var10[var23 - var4]] == 0) {
                        if (var22 >= 0) {
                           var11 = var1.getSubimage(var22, var21, var23 - var22, 1);
                           var2.translate((double)var22, (double)var21);
                           this.drawImageToPlatform(var11, var2, var3, 0, 0, var23 - var22, 1, true);
                           var2.translate((double)(-var22), (double)(-var21));
                           var22 = -1;
                        }
                     } else if (var22 < 0) {
                        var22 = var23;
                     }
                  }

                  if (var22 >= 0) {
                     var11 = var1.getSubimage(var22, var21, var17 - var22, 1);
                     var2.translate((double)var22, (double)var21);
                     this.drawImageToPlatform(var11, var2, var3, 0, 0, var17 - var22, 1, true);
                     var2.translate((double)(-var22), (double)(-var21));
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   protected abstract boolean drawImageToPlatform(Image var1, AffineTransform var2, Color var3, int var4, int var5, int var6, int var7, boolean var8);

   public boolean drawImage(Image var1, int var2, int var3, ImageObserver var4) {
      return this.drawImage(var1, var2, var3, (Color)null, var4);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, ImageObserver var6) {
      return this.drawImage(var1, var2, var3, var4, var5, (Color)null, var6);
   }

   public boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5) {
      if (var1 == null) {
         return true;
      } else {
         int var7 = var1.getWidth((ImageObserver)null);
         int var8 = var1.getHeight((ImageObserver)null);
         boolean var6;
         if (var7 >= 0 && var8 >= 0) {
            var6 = this.drawImage(var1, var2, var3, var7, var8, var4, var5);
         } else {
            var6 = false;
         }

         return var6;
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7) {
      if (var1 == null) {
         return true;
      } else {
         int var9 = var1.getWidth((ImageObserver)null);
         int var10 = var1.getHeight((ImageObserver)null);
         boolean var8;
         if (var9 >= 0 && var10 >= 0) {
            var8 = this.drawImage(var1, var2, var3, var2 + var4, var3 + var5, 0, 0, var9, var10, var7);
         } else {
            var8 = false;
         }

         return var8;
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ImageObserver var10) {
      return this.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, (Color)null, var10);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      if (var1 == null) {
         return true;
      } else {
         int var12 = var1.getWidth((ImageObserver)null);
         int var13 = var1.getHeight((ImageObserver)null);
         if (var12 >= 0 && var13 >= 0) {
            int var14 = var8 - var6;
            int var15 = var9 - var7;
            float var16 = (float)(var4 - var2) / (float)var14;
            float var17 = (float)(var5 - var3) / (float)var15;
            AffineTransform var18 = new AffineTransform(var16, 0.0F, 0.0F, var17, (float)var2 - (float)var6 * var16, (float)var3 - (float)var7 * var17);
            boolean var19 = false;
            int var20;
            if (var8 < var6) {
               var20 = var6;
               var6 = var8;
               var8 = var20;
            }

            if (var9 < var7) {
               var20 = var7;
               var7 = var9;
               var9 = var20;
            }

            if (var6 < 0) {
               var6 = 0;
            } else if (var6 > var12) {
               var6 = var12;
            }

            if (var8 < 0) {
               var8 = 0;
            } else if (var8 > var12) {
               var8 = var12;
            }

            if (var7 < 0) {
               var7 = 0;
            } else if (var7 > var13) {
               var7 = var13;
            }

            if (var9 < 0) {
               var9 = 0;
            } else if (var9 > var13) {
               var9 = var13;
            }

            var14 = var8 - var6;
            var15 = var9 - var7;
            return var14 > 0 && var15 > 0 ? this.drawImageToPlatform(var1, var18, var10, var6, var7, var14, var15, false) : true;
         } else {
            return true;
         }
      }
   }

   public boolean drawImage(Image var1, AffineTransform var2, ImageObserver var3) {
      if (var1 == null) {
         return true;
      } else {
         int var5 = var1.getWidth((ImageObserver)null);
         int var6 = var1.getHeight((ImageObserver)null);
         boolean var4;
         if (var5 >= 0 && var6 >= 0) {
            var4 = this.drawImageToPlatform(var1, var2, (Color)null, 0, 0, var5, var6, false);
         } else {
            var4 = false;
         }

         return var4;
      }
   }

   public void drawImage(BufferedImage var1, BufferedImageOp var2, int var3, int var4) {
      if (var1 != null) {
         int var5 = var1.getWidth((ImageObserver)null);
         int var6 = var1.getHeight((ImageObserver)null);
         if (var2 != null) {
            var1 = var2.filter(var1, (BufferedImage)null);
         }

         if (var5 > 0 && var6 > 0) {
            AffineTransform var7 = new AffineTransform(1.0F, 0.0F, 0.0F, 1.0F, (float)var3, (float)var4);
            this.drawImageToPlatform(var1, var7, (Color)null, 0, 0, var5, var6, false);
         }
      }
   }

   public void drawRenderedImage(RenderedImage var1, AffineTransform var2) {
      if (var1 != null) {
         BufferedImage var3 = null;
         int var4 = var1.getWidth();
         int var5 = var1.getHeight();
         if (var4 > 0 && var5 > 0) {
            if (var1 instanceof BufferedImage) {
               var3 = (BufferedImage)var1;
            } else {
               var3 = new BufferedImage(var4, var5, 2);
               Graphics2D var6 = var3.createGraphics();
               var6.drawRenderedImage(var1, var2);
            }

            this.drawImageToPlatform(var3, var2, (Color)null, 0, 0, var4, var5, false);
         }
      }
   }
}
