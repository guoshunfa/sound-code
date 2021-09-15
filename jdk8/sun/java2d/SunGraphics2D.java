package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import sun.awt.ConstrainableGraphics;
import sun.awt.SunHints;
import sun.awt.image.MultiResolutionImage;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.SurfaceManager;
import sun.awt.image.ToolkitImage;
import sun.font.FontDesignMetrics;
import sun.font.FontUtilities;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.FontInfo;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.DrawImagePipe;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.PixelDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderingEngine;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.ValidatePipe;
import sun.misc.PerformanceLogger;

public final class SunGraphics2D extends Graphics2D implements ConstrainableGraphics, Cloneable, DestSurfaceProvider {
   public static final int PAINT_CUSTOM = 6;
   public static final int PAINT_TEXTURE = 5;
   public static final int PAINT_RAD_GRADIENT = 4;
   public static final int PAINT_LIN_GRADIENT = 3;
   public static final int PAINT_GRADIENT = 2;
   public static final int PAINT_ALPHACOLOR = 1;
   public static final int PAINT_OPAQUECOLOR = 0;
   public static final int COMP_CUSTOM = 3;
   public static final int COMP_XOR = 2;
   public static final int COMP_ALPHA = 1;
   public static final int COMP_ISCOPY = 0;
   public static final int STROKE_CUSTOM = 3;
   public static final int STROKE_WIDE = 2;
   public static final int STROKE_THINDASHED = 1;
   public static final int STROKE_THIN = 0;
   public static final int TRANSFORM_GENERIC = 4;
   public static final int TRANSFORM_TRANSLATESCALE = 3;
   public static final int TRANSFORM_ANY_TRANSLATE = 2;
   public static final int TRANSFORM_INT_TRANSLATE = 1;
   public static final int TRANSFORM_ISIDENT = 0;
   public static final int CLIP_SHAPE = 2;
   public static final int CLIP_RECTANGULAR = 1;
   public static final int CLIP_DEVICE = 0;
   public int eargb;
   public int pixel;
   public SurfaceData surfaceData;
   public PixelDrawPipe drawpipe;
   public PixelFillPipe fillpipe;
   public DrawImagePipe imagepipe;
   public ShapeDrawPipe shapepipe;
   public TextPipe textpipe;
   public MaskFill alphafill;
   public RenderLoops loops;
   public CompositeType imageComp;
   public int paintState;
   public int compositeState;
   public int strokeState;
   public int transformState;
   public int clipState;
   public Color foregroundColor;
   public Color backgroundColor;
   public AffineTransform transform;
   public int transX;
   public int transY;
   protected static final Stroke defaultStroke = new BasicStroke();
   protected static final Composite defaultComposite;
   private static final Font defaultFont;
   public Paint paint;
   public Stroke stroke;
   public Composite composite;
   protected Font font;
   protected FontMetrics fontMetrics;
   public int renderHint;
   public int antialiasHint;
   public int textAntialiasHint;
   protected int fractionalMetricsHint;
   public int lcdTextContrast;
   private static int lcdTextContrastDefaultValue;
   private int interpolationHint;
   public int strokeHint;
   public int interpolationType;
   public RenderingHints hints;
   public Region constrainClip;
   public int constrainX;
   public int constrainY;
   public Region clipRegion;
   public Shape usrClip;
   protected Region devClip;
   private final int devScale;
   private int resolutionVariantHint;
   private boolean validFontInfo;
   private FontInfo fontInfo;
   private FontInfo glyphVectorFontInfo;
   private FontRenderContext glyphVectorFRC;
   private static final int slowTextTransformMask = 120;
   protected static ValidatePipe invalidpipe;
   private static final double[] IDENT_MATRIX;
   private static final AffineTransform IDENT_ATX;
   private static final int MINALLOCATED = 8;
   private static final int TEXTARRSIZE = 17;
   private static double[][] textTxArr;
   private static AffineTransform[] textAtArr;
   static final int NON_UNIFORM_SCALE_MASK = 36;
   public static final double MinPenSizeAA;
   public static final double MinPenSizeAASquared;
   public static final double MinPenSizeSquared = 1.000000001D;
   static final int NON_RECTILINEAR_TRANSFORM_MASK = 48;
   Blit lastCAblit;
   Composite lastCAcomp;
   private FontRenderContext cachedFRC;

   public SunGraphics2D(SurfaceData var1, Color var2, Color var3, Font var4) {
      this.surfaceData = var1;
      this.foregroundColor = var2;
      this.backgroundColor = var3;
      this.transform = new AffineTransform();
      this.stroke = defaultStroke;
      this.composite = defaultComposite;
      this.paint = this.foregroundColor;
      this.imageComp = CompositeType.SrcOverNoEa;
      this.renderHint = 0;
      this.antialiasHint = 1;
      this.textAntialiasHint = 0;
      this.fractionalMetricsHint = 1;
      this.lcdTextContrast = lcdTextContrastDefaultValue;
      this.interpolationHint = -1;
      this.strokeHint = 0;
      this.resolutionVariantHint = 0;
      this.interpolationType = 1;
      this.validateColor();
      this.devScale = var1.getDefaultScale();
      if (this.devScale != 1) {
         this.transform.setToScale((double)this.devScale, (double)this.devScale);
         this.invalidateTransform();
      }

      this.font = var4;
      if (this.font == null) {
         this.font = defaultFont;
      }

      this.setDevClip(var1.getBounds());
      this.invalidatePipe();
   }

   protected Object clone() {
      try {
         SunGraphics2D var1 = (SunGraphics2D)super.clone();
         var1.transform = new AffineTransform(this.transform);
         if (this.hints != null) {
            var1.hints = (RenderingHints)this.hints.clone();
         }

         if (this.fontInfo != null) {
            if (this.validFontInfo) {
               var1.fontInfo = (FontInfo)this.fontInfo.clone();
            } else {
               var1.fontInfo = null;
            }
         }

         if (this.glyphVectorFontInfo != null) {
            var1.glyphVectorFontInfo = (FontInfo)this.glyphVectorFontInfo.clone();
            var1.glyphVectorFRC = this.glyphVectorFRC;
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }

   public Graphics create() {
      return (Graphics)this.clone();
   }

   public void setDevClip(int var1, int var2, int var3, int var4) {
      Region var5 = this.constrainClip;
      if (var5 == null) {
         this.devClip = Region.getInstanceXYWH(var1, var2, var3, var4);
      } else {
         this.devClip = var5.getIntersectionXYWH(var1, var2, var3, var4);
      }

      this.validateCompClip();
   }

   public void setDevClip(Rectangle var1) {
      this.setDevClip(var1.x, var1.y, var1.width, var1.height);
   }

   public void constrain(int var1, int var2, int var3, int var4, Region var5) {
      if ((var1 | var2) != 0) {
         this.translate(var1, var2);
      }

      if (this.transformState > 3) {
         this.clipRect(0, 0, var3, var4);
      } else {
         double var6 = this.transform.getScaleX();
         double var8 = this.transform.getScaleY();
         var1 = this.constrainX = (int)this.transform.getTranslateX();
         var2 = this.constrainY = (int)this.transform.getTranslateY();
         var3 = Region.dimAdd(var1, Region.clipScale(var3, var6));
         var4 = Region.dimAdd(var2, Region.clipScale(var4, var8));
         Region var10 = this.constrainClip;
         if (var10 == null) {
            var10 = Region.getInstanceXYXY(var1, var2, var3, var4);
         } else {
            var10 = var10.getIntersectionXYXY(var1, var2, var3, var4);
         }

         if (var5 != null) {
            var5 = var5.getScaledRegion(var6, var8);
            var5 = var5.getTranslatedRegion(var1, var2);
            var10 = var10.getIntersection(var5);
         }

         if (var10 != this.constrainClip) {
            this.constrainClip = var10;
            if (!this.devClip.isInsideQuickCheck(var10)) {
               this.devClip = this.devClip.getIntersection(var10);
               this.validateCompClip();
            }

         }
      }
   }

   public void constrain(int var1, int var2, int var3, int var4) {
      this.constrain(var1, var2, var3, var4, (Region)null);
   }

   protected void invalidatePipe() {
      this.drawpipe = invalidpipe;
      this.fillpipe = invalidpipe;
      this.shapepipe = invalidpipe;
      this.textpipe = invalidpipe;
      this.imagepipe = invalidpipe;
      this.loops = null;
   }

   public void validatePipe() {
      if (!this.surfaceData.isValid()) {
         throw new InvalidPipeException("attempt to validate Pipe with invalid SurfaceData");
      } else {
         this.surfaceData.validatePipe(this);
      }
   }

   Shape intersectShapes(Shape var1, Shape var2, boolean var3, boolean var4) {
      if (var1 instanceof Rectangle && var2 instanceof Rectangle) {
         return ((Rectangle)var1).intersection((Rectangle)var2);
      } else if (var1 instanceof Rectangle2D) {
         return this.intersectRectShape((Rectangle2D)var1, var2, var3, var4);
      } else {
         return var2 instanceof Rectangle2D ? this.intersectRectShape((Rectangle2D)var2, var1, var4, var3) : this.intersectByArea(var1, var2, var3, var4);
      }
   }

   Shape intersectRectShape(Rectangle2D var1, Shape var2, boolean var3, boolean var4) {
      if (var2 instanceof Rectangle2D) {
         Rectangle2D var5 = (Rectangle2D)var2;
         Object var6;
         if (!var3) {
            var6 = var1;
         } else if (!var4) {
            var6 = var5;
         } else {
            var6 = new Rectangle2D.Float();
         }

         double var7 = Math.max(var1.getX(), var5.getX());
         double var9 = Math.min(var1.getX() + var1.getWidth(), var5.getX() + var5.getWidth());
         double var11 = Math.max(var1.getY(), var5.getY());
         double var13 = Math.min(var1.getY() + var1.getHeight(), var5.getY() + var5.getHeight());
         if (var9 - var7 >= 0.0D && var13 - var11 >= 0.0D) {
            ((Rectangle2D)var6).setFrameFromDiagonal(var7, var11, var9, var13);
         } else {
            ((Rectangle2D)var6).setFrameFromDiagonal(0.0D, 0.0D, 0.0D, 0.0D);
         }

         return (Shape)var6;
      } else if (var1.contains(var2.getBounds2D())) {
         if (var4) {
            var2 = cloneShape(var2);
         }

         return var2;
      } else {
         return this.intersectByArea(var1, var2, var3, var4);
      }
   }

   protected static Shape cloneShape(Shape var0) {
      return new GeneralPath(var0);
   }

   Shape intersectByArea(Shape var1, Shape var2, boolean var3, boolean var4) {
      Area var5;
      if (!var3 && var1 instanceof Area) {
         var5 = (Area)var1;
      } else if (!var4 && var2 instanceof Area) {
         var5 = (Area)var2;
         var2 = var1;
      } else {
         var5 = new Area(var1);
      }

      Area var6;
      if (var2 instanceof Area) {
         var6 = (Area)var2;
      } else {
         var6 = new Area(var2);
      }

      var5.intersect(var6);
      return (Shape)(var5.isRectangular() ? var5.getBounds() : var5);
   }

   public Region getCompClip() {
      if (!this.surfaceData.isValid()) {
         this.revalidateAll();
      }

      return this.clipRegion;
   }

   public Font getFont() {
      if (this.font == null) {
         this.font = defaultFont;
      }

      return this.font;
   }

   public FontInfo checkFontInfo(FontInfo var1, Font var2, FontRenderContext var3) {
      if (var1 == null) {
         var1 = new FontInfo();
      }

      float var4 = var2.getSize2D();
      AffineTransform var7 = null;
      AffineTransform var6;
      double var8;
      double var10;
      int var13;
      if (var2.isTransformed()) {
         var7 = var2.getTransform();
         var7.scale((double)var4, (double)var4);
         int var5 = var7.getType();
         var1.originX = (float)var7.getTranslateX();
         var1.originY = (float)var7.getTranslateY();
         var7.translate((double)(-var1.originX), (double)(-var1.originY));
         if (this.transformState >= 3) {
            this.transform.getMatrix(var1.devTx = new double[4]);
            var6 = new AffineTransform(var1.devTx);
            var7.preConcatenate(var6);
         } else {
            var1.devTx = IDENT_MATRIX;
            var6 = IDENT_ATX;
         }

         var7.getMatrix(var1.glyphTx = new double[4]);
         var8 = var7.getShearX();
         var10 = var7.getScaleY();
         if (var8 != 0.0D) {
            var10 = Math.sqrt(var8 * var8 + var10 * var10);
         }

         var1.pixelHeight = (int)(Math.abs(var10) + 0.5D);
      } else {
         boolean var12 = false;
         var1.originX = var1.originY = 0.0F;
         if (this.transformState >= 3) {
            this.transform.getMatrix(var1.devTx = new double[4]);
            var6 = new AffineTransform(var1.devTx);
            var1.glyphTx = new double[4];

            for(var13 = 0; var13 < 4; ++var13) {
               var1.glyphTx[var13] = var1.devTx[var13] * (double)var4;
            }

            var7 = new AffineTransform(var1.glyphTx);
            var8 = this.transform.getShearX();
            var10 = this.transform.getScaleY();
            if (var8 != 0.0D) {
               var10 = Math.sqrt(var8 * var8 + var10 * var10);
            }

            var1.pixelHeight = (int)(Math.abs(var10 * (double)var4) + 0.5D);
         } else {
            var13 = (int)var4;
            if (var4 == (float)var13 && var13 >= 8 && var13 < 17) {
               var1.glyphTx = textTxArr[var13];
               var7 = textAtArr[var13];
               var1.pixelHeight = var13;
            } else {
               var1.pixelHeight = (int)((double)var4 + 0.5D);
            }

            if (var7 == null) {
               var1.glyphTx = new double[]{(double)var4, 0.0D, 0.0D, (double)var4};
               var7 = new AffineTransform(var1.glyphTx);
            }

            var1.devTx = IDENT_MATRIX;
            var6 = IDENT_ATX;
         }
      }

      var1.font2D = FontUtilities.getFont2D(var2);
      var13 = this.fractionalMetricsHint;
      if (var13 == 0) {
         var13 = 1;
      }

      var1.lcdSubPixPos = false;
      int var9;
      if (var3 == null) {
         var9 = this.textAntialiasHint;
      } else {
         var9 = ((SunHints.Value)var3.getAntiAliasingHint()).getIndex();
      }

      if (var9 == 0) {
         if (this.antialiasHint == 2) {
            var9 = 2;
         } else {
            var9 = 1;
         }
      } else if (var9 == 3) {
         if (var1.font2D.useAAForPtSize(var1.pixelHeight)) {
            var9 = 2;
         } else {
            var9 = 1;
         }
      } else if (var9 >= 4) {
         if (!this.surfaceData.canRenderLCDText(this)) {
            var9 = 2;
         } else {
            var1.lcdRGBOrder = true;
            if (var9 == 5) {
               var9 = 4;
               var1.lcdRGBOrder = false;
            } else if (var9 == 7) {
               var9 = 6;
               var1.lcdRGBOrder = false;
            }

            var1.lcdSubPixPos = var13 == 2 && var9 == 4;
         }
      }

      var1.aaHint = var9;
      var1.fontStrike = var1.font2D.getStrike(var2, var6, var7, var9, var13);
      return var1;
   }

   public static boolean isRotated(double[] var0) {
      return var0[0] != var0[3] || var0[1] != 0.0D || var0[2] != 0.0D || var0[0] <= 0.0D;
   }

   public void setFont(Font var1) {
      if (var1 != null && var1 != this.font) {
         if (this.textAntialiasHint == 3 && this.textpipe != invalidpipe && (this.transformState > 2 || var1.isTransformed() || this.fontInfo == null || this.fontInfo.aaHint == 2 != FontUtilities.getFont2D(var1).useAAForPtSize(var1.getSize()))) {
            this.textpipe = invalidpipe;
         }

         this.font = var1;
         this.fontMetrics = null;
         this.validFontInfo = false;
      }

   }

   public FontInfo getFontInfo() {
      if (!this.validFontInfo) {
         this.fontInfo = this.checkFontInfo(this.fontInfo, this.font, (FontRenderContext)null);
         this.validFontInfo = true;
      }

      return this.fontInfo;
   }

   public FontInfo getGVFontInfo(Font var1, FontRenderContext var2) {
      if (this.glyphVectorFontInfo != null && this.glyphVectorFontInfo.font == var1 && this.glyphVectorFRC == var2) {
         return this.glyphVectorFontInfo;
      } else {
         this.glyphVectorFRC = var2;
         return this.glyphVectorFontInfo = this.checkFontInfo(this.glyphVectorFontInfo, var1, var2);
      }
   }

   public FontMetrics getFontMetrics() {
      return this.fontMetrics != null ? this.fontMetrics : (this.fontMetrics = FontDesignMetrics.getMetrics(this.font, this.getFontRenderContext()));
   }

   public FontMetrics getFontMetrics(Font var1) {
      if (this.fontMetrics != null && var1 == this.font) {
         return this.fontMetrics;
      } else {
         FontDesignMetrics var2 = FontDesignMetrics.getMetrics(var1, this.getFontRenderContext());
         if (this.font == var1) {
            this.fontMetrics = var2;
         }

         return var2;
      }
   }

   public boolean hit(Rectangle var1, Shape var2, boolean var3) {
      if (var3) {
         var2 = this.stroke.createStrokedShape(var2);
      }

      var2 = this.transformShape(var2);
      if ((this.constrainX | this.constrainY) != 0) {
         var1 = new Rectangle(var1);
         var1.translate(this.constrainX, this.constrainY);
      }

      return var2.intersects(var1);
   }

   public ColorModel getDeviceColorModel() {
      return this.surfaceData.getColorModel();
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.surfaceData.getDeviceConfiguration();
   }

   public final SurfaceData getSurfaceData() {
      return this.surfaceData;
   }

   public void setComposite(Composite var1) {
      if (this.composite != var1) {
         byte var2;
         CompositeType var3;
         if (var1 instanceof AlphaComposite) {
            AlphaComposite var4 = (AlphaComposite)var1;
            var3 = CompositeType.forAlphaComposite(var4);
            if (var3 == CompositeType.SrcOverNoEa) {
               if (this.paintState != 0 && (this.paintState <= 1 || this.paint.getTransparency() != 1)) {
                  var2 = 1;
               } else {
                  var2 = 0;
               }
            } else if (var3 != CompositeType.SrcNoEa && var3 != CompositeType.Src && var3 != CompositeType.Clear) {
               if (this.surfaceData.getTransparency() == 1 && var3 == CompositeType.SrcIn) {
                  var2 = 0;
               } else {
                  var2 = 1;
               }
            } else {
               var2 = 0;
            }
         } else if (var1 instanceof XORComposite) {
            var2 = 2;
            var3 = CompositeType.Xor;
         } else {
            if (var1 == null) {
               throw new IllegalArgumentException("null Composite");
            }

            this.surfaceData.checkCustomComposite();
            var2 = 3;
            var3 = CompositeType.General;
         }

         if (this.compositeState != var2 || this.imageComp != var3) {
            this.compositeState = var2;
            this.imageComp = var3;
            this.invalidatePipe();
            this.validFontInfo = false;
         }

         this.composite = var1;
         if (this.paintState <= 1) {
            this.validateColor();
         }

      }
   }

   public void setPaint(Paint var1) {
      if (var1 instanceof Color) {
         this.setColor((Color)var1);
      } else if (var1 != null && this.paint != var1) {
         this.paint = var1;
         if (this.imageComp == CompositeType.SrcOverNoEa) {
            if (var1.getTransparency() == 1) {
               if (this.compositeState != 0) {
                  this.compositeState = 0;
               }
            } else if (this.compositeState == 0) {
               this.compositeState = 1;
            }
         }

         Class var2 = var1.getClass();
         if (var2 == GradientPaint.class) {
            this.paintState = 2;
         } else if (var2 == LinearGradientPaint.class) {
            this.paintState = 3;
         } else if (var2 == RadialGradientPaint.class) {
            this.paintState = 4;
         } else if (var2 == TexturePaint.class) {
            this.paintState = 5;
         } else {
            this.paintState = 6;
         }

         this.validFontInfo = false;
         this.invalidatePipe();
      }
   }

   private void validateBasicStroke(BasicStroke var1) {
      boolean var2 = this.antialiasHint == 2;
      if (this.transformState < 3) {
         if (var2) {
            if ((double)var1.getLineWidth() <= MinPenSizeAA) {
               if (var1.getDashArray() == null) {
                  this.strokeState = 0;
               } else {
                  this.strokeState = 1;
               }
            } else {
               this.strokeState = 2;
            }
         } else if (var1 == defaultStroke) {
            this.strokeState = 0;
         } else if (var1.getLineWidth() <= 1.0F) {
            if (var1.getDashArray() == null) {
               this.strokeState = 0;
            } else {
               this.strokeState = 1;
            }
         } else {
            this.strokeState = 2;
         }
      } else {
         double var3;
         if ((this.transform.getType() & 36) == 0) {
            var3 = Math.abs(this.transform.getDeterminant());
         } else {
            double var5 = this.transform.getScaleX();
            double var7 = this.transform.getShearX();
            double var9 = this.transform.getShearY();
            double var11 = this.transform.getScaleY();
            double var13 = var5 * var5 + var9 * var9;
            double var15 = 2.0D * (var5 * var7 + var9 * var11);
            double var17 = var7 * var7 + var11 * var11;
            double var19 = Math.sqrt(var15 * var15 + (var13 - var17) * (var13 - var17));
            var3 = (var13 + var17 + var19) / 2.0D;
         }

         if (var1 != defaultStroke) {
            var3 *= (double)(var1.getLineWidth() * var1.getLineWidth());
         }

         if (var3 <= (var2 ? MinPenSizeAASquared : 1.000000001D)) {
            if (var1.getDashArray() == null) {
               this.strokeState = 0;
            } else {
               this.strokeState = 1;
            }
         } else {
            this.strokeState = 2;
         }
      }

   }

   public void setStroke(Stroke var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null Stroke");
      } else {
         int var2 = this.strokeState;
         this.stroke = var1;
         if (var1 instanceof BasicStroke) {
            this.validateBasicStroke((BasicStroke)var1);
         } else {
            this.strokeState = 3;
         }

         if (this.strokeState != var2) {
            this.invalidatePipe();
         }

      }
   }

   public void setRenderingHint(RenderingHints.Key var1, Object var2) {
      if (!var1.isCompatibleValue(var2)) {
         throw new IllegalArgumentException(var2 + " is not compatible with " + var1);
      } else {
         if (var1 instanceof SunHints.Key) {
            boolean var4 = false;
            boolean var5 = true;
            SunHints.Key var6 = (SunHints.Key)var1;
            int var7;
            if (var6 == SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST) {
               var7 = (Integer)var2;
            } else {
               var7 = ((SunHints.Value)var2).getIndex();
            }

            boolean var3;
            switch(var6.getIndex()) {
            case 0:
               var3 = this.renderHint != var7;
               if (var3) {
                  this.renderHint = var7;
                  if (this.interpolationHint == -1) {
                     this.interpolationType = var7 == 2 ? 2 : 1;
                  }
               }
               break;
            case 1:
               var3 = this.antialiasHint != var7;
               this.antialiasHint = var7;
               if (var3) {
                  var4 = this.textAntialiasHint == 0;
                  if (this.strokeState != 3) {
                     this.validateBasicStroke((BasicStroke)this.stroke);
                  }
               }
               break;
            case 2:
               var3 = this.textAntialiasHint != var7;
               var4 = var3;
               this.textAntialiasHint = var7;
               break;
            case 3:
               var3 = this.fractionalMetricsHint != var7;
               var4 = var3;
               this.fractionalMetricsHint = var7;
               break;
            case 5:
               this.interpolationHint = var7;
               byte var8;
               switch(var7) {
               case 0:
               default:
                  var8 = 1;
                  break;
               case 1:
                  var8 = 2;
                  break;
               case 2:
                  var8 = 3;
               }

               var3 = this.interpolationType != var8;
               this.interpolationType = var8;
               break;
            case 8:
               var3 = this.strokeHint != var7;
               this.strokeHint = var7;
               break;
            case 9:
               var3 = this.resolutionVariantHint != var7;
               this.resolutionVariantHint = var7;
               break;
            case 100:
               var3 = false;
               this.lcdTextContrast = var7;
               break;
            default:
               var5 = false;
               var3 = false;
            }

            if (var5) {
               if (var3) {
                  this.invalidatePipe();
                  if (var4) {
                     this.fontMetrics = null;
                     this.cachedFRC = null;
                     this.validFontInfo = false;
                     this.glyphVectorFontInfo = null;
                  }
               }

               if (this.hints != null) {
                  this.hints.put(var1, var2);
               }

               return;
            }
         }

         if (this.hints == null) {
            this.hints = this.makeHints((Map)null);
         }

         this.hints.put(var1, var2);
      }
   }

   public Object getRenderingHint(RenderingHints.Key var1) {
      if (this.hints != null) {
         return this.hints.get(var1);
      } else if (!(var1 instanceof SunHints.Key)) {
         return null;
      } else {
         int var2 = ((SunHints.Key)var1).getIndex();
         switch(var2) {
         case 0:
            return SunHints.Value.get(0, this.renderHint);
         case 1:
            return SunHints.Value.get(1, this.antialiasHint);
         case 2:
            return SunHints.Value.get(2, this.textAntialiasHint);
         case 3:
            return SunHints.Value.get(3, this.fractionalMetricsHint);
         case 5:
            switch(this.interpolationHint) {
            case 0:
               return SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            case 1:
               return SunHints.VALUE_INTERPOLATION_BILINEAR;
            case 2:
               return SunHints.VALUE_INTERPOLATION_BICUBIC;
            default:
               return null;
            }
         case 8:
            return SunHints.Value.get(8, this.strokeHint);
         case 9:
            return SunHints.Value.get(9, this.resolutionVariantHint);
         case 100:
            return new Integer(this.lcdTextContrast);
         default:
            return null;
         }
      }
   }

   public void setRenderingHints(Map<?, ?> var1) {
      this.hints = null;
      this.renderHint = 0;
      this.antialiasHint = 1;
      this.textAntialiasHint = 0;
      this.fractionalMetricsHint = 1;
      this.lcdTextContrast = lcdTextContrastDefaultValue;
      this.interpolationHint = -1;
      this.interpolationType = 1;
      boolean var2 = false;
      Iterator var3 = var1.keySet().iterator();

      while(true) {
         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 != SunHints.KEY_RENDERING && var4 != SunHints.KEY_ANTIALIASING && var4 != SunHints.KEY_TEXT_ANTIALIASING && var4 != SunHints.KEY_FRACTIONALMETRICS && var4 != SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST && var4 != SunHints.KEY_STROKE_CONTROL && var4 != SunHints.KEY_INTERPOLATION) {
               var2 = true;
            } else {
               this.setRenderingHint((RenderingHints.Key)var4, var1.get(var4));
            }
         }

         if (var2) {
            this.hints = this.makeHints(var1);
         }

         this.invalidatePipe();
         return;
      }
   }

   public void addRenderingHints(Map<?, ?> var1) {
      boolean var2 = false;
      Iterator var3 = var1.keySet().iterator();

      while(true) {
         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 != SunHints.KEY_RENDERING && var4 != SunHints.KEY_ANTIALIASING && var4 != SunHints.KEY_TEXT_ANTIALIASING && var4 != SunHints.KEY_FRACTIONALMETRICS && var4 != SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST && var4 != SunHints.KEY_STROKE_CONTROL && var4 != SunHints.KEY_INTERPOLATION) {
               var2 = true;
            } else {
               this.setRenderingHint((RenderingHints.Key)var4, var1.get(var4));
            }
         }

         if (var2) {
            if (this.hints == null) {
               this.hints = this.makeHints(var1);
            } else {
               this.hints.putAll(var1);
            }
         }

         return;
      }
   }

   public RenderingHints getRenderingHints() {
      return this.hints == null ? this.makeHints((Map)null) : (RenderingHints)this.hints.clone();
   }

   RenderingHints makeHints(Map var1) {
      RenderingHints var2 = new RenderingHints(var1);
      var2.put(SunHints.KEY_RENDERING, SunHints.Value.get(0, this.renderHint));
      var2.put(SunHints.KEY_ANTIALIASING, SunHints.Value.get(1, this.antialiasHint));
      var2.put(SunHints.KEY_TEXT_ANTIALIASING, SunHints.Value.get(2, this.textAntialiasHint));
      var2.put(SunHints.KEY_FRACTIONALMETRICS, SunHints.Value.get(3, this.fractionalMetricsHint));
      var2.put(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, this.lcdTextContrast);
      Object var3;
      switch(this.interpolationHint) {
      case 0:
         var3 = SunHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
         break;
      case 1:
         var3 = SunHints.VALUE_INTERPOLATION_BILINEAR;
         break;
      case 2:
         var3 = SunHints.VALUE_INTERPOLATION_BICUBIC;
         break;
      default:
         var3 = null;
      }

      if (var3 != null) {
         var2.put(SunHints.KEY_INTERPOLATION, var3);
      }

      var2.put(SunHints.KEY_STROKE_CONTROL, SunHints.Value.get(8, this.strokeHint));
      return var2;
   }

   public void translate(double var1, double var3) {
      this.transform.translate(var1, var3);
      this.invalidateTransform();
   }

   public void rotate(double var1) {
      this.transform.rotate(var1);
      this.invalidateTransform();
   }

   public void rotate(double var1, double var3, double var5) {
      this.transform.rotate(var1, var3, var5);
      this.invalidateTransform();
   }

   public void scale(double var1, double var3) {
      this.transform.scale(var1, var3);
      this.invalidateTransform();
   }

   public void shear(double var1, double var3) {
      this.transform.shear(var1, var3);
      this.invalidateTransform();
   }

   public void transform(AffineTransform var1) {
      this.transform.concatenate(var1);
      this.invalidateTransform();
   }

   public void translate(int var1, int var2) {
      this.transform.translate((double)var1, (double)var2);
      if (this.transformState <= 1) {
         this.transX += var1;
         this.transY += var2;
         this.transformState = (this.transX | this.transY) == 0 ? 0 : 1;
      } else {
         this.invalidateTransform();
      }

   }

   public void setTransform(AffineTransform var1) {
      if ((this.constrainX | this.constrainY) == 0 && this.devScale == 1) {
         this.transform.setTransform(var1);
      } else {
         this.transform.setTransform((double)this.devScale, 0.0D, 0.0D, (double)this.devScale, (double)this.constrainX, (double)this.constrainY);
         this.transform.concatenate(var1);
      }

      this.invalidateTransform();
   }

   protected void invalidateTransform() {
      int var1 = this.transform.getType();
      int var2 = this.transformState;
      if (var1 == 0) {
         this.transformState = 0;
         this.transX = this.transY = 0;
      } else if (var1 == 1) {
         double var3 = this.transform.getTranslateX();
         double var5 = this.transform.getTranslateY();
         this.transX = (int)Math.floor(var3 + 0.5D);
         this.transY = (int)Math.floor(var5 + 0.5D);
         if (var3 == (double)this.transX && var5 == (double)this.transY) {
            this.transformState = 1;
         } else {
            this.transformState = 2;
         }
      } else if ((var1 & 120) == 0) {
         this.transformState = 3;
         this.transX = this.transY = 0;
      } else {
         this.transformState = 4;
         this.transX = this.transY = 0;
      }

      if (this.transformState >= 3 || var2 >= 3) {
         this.cachedFRC = null;
         this.validFontInfo = false;
         this.fontMetrics = null;
         this.glyphVectorFontInfo = null;
         if (this.transformState != var2) {
            this.invalidatePipe();
         }
      }

      if (this.strokeState != 3) {
         this.validateBasicStroke((BasicStroke)this.stroke);
      }

   }

   public AffineTransform getTransform() {
      if ((this.constrainX | this.constrainY) == 0 && this.devScale == 1) {
         return new AffineTransform(this.transform);
      } else {
         double var1 = 1.0D / (double)this.devScale;
         AffineTransform var3 = new AffineTransform(var1, 0.0D, 0.0D, var1, (double)(-this.constrainX) * var1, (double)(-this.constrainY) * var1);
         var3.concatenate(this.transform);
         return var3;
      }
   }

   public AffineTransform cloneTransform() {
      return new AffineTransform(this.transform);
   }

   public Paint getPaint() {
      return this.paint;
   }

   public Composite getComposite() {
      return this.composite;
   }

   public Color getColor() {
      return this.foregroundColor;
   }

   final void validateColor() {
      int var1;
      if (this.imageComp == CompositeType.Clear) {
         var1 = 0;
      } else {
         var1 = this.foregroundColor.getRGB();
         if (this.compositeState <= 1 && this.imageComp != CompositeType.SrcNoEa && this.imageComp != CompositeType.SrcOverNoEa) {
            AlphaComposite var2 = (AlphaComposite)this.composite;
            int var3 = Math.round(var2.getAlpha() * (float)(var1 >>> 24));
            var1 = var1 & 16777215 | var3 << 24;
         }
      }

      this.eargb = var1;
      this.pixel = this.surfaceData.pixelFor(var1);
   }

   public void setColor(Color var1) {
      if (var1 != null && var1 != this.paint) {
         this.paint = this.foregroundColor = var1;
         this.validateColor();
         if (this.eargb >> 24 == -1) {
            if (this.paintState == 0) {
               return;
            }

            this.paintState = 0;
            if (this.imageComp == CompositeType.SrcOverNoEa) {
               this.compositeState = 0;
            }
         } else {
            if (this.paintState == 1) {
               return;
            }

            this.paintState = 1;
            if (this.imageComp == CompositeType.SrcOverNoEa) {
               this.compositeState = 1;
            }
         }

         this.validFontInfo = false;
         this.invalidatePipe();
      }
   }

   public void setBackground(Color var1) {
      this.backgroundColor = var1;
   }

   public Color getBackground() {
      return this.backgroundColor;
   }

   public Stroke getStroke() {
      return this.stroke;
   }

   public Rectangle getClipBounds() {
      return this.clipState == 0 ? null : this.getClipBounds(new Rectangle());
   }

   public Rectangle getClipBounds(Rectangle var1) {
      if (this.clipState != 0) {
         if (this.transformState <= 1) {
            if (this.usrClip instanceof Rectangle) {
               var1.setBounds((Rectangle)this.usrClip);
            } else {
               var1.setFrame(this.usrClip.getBounds2D());
            }

            var1.translate(-this.transX, -this.transY);
         } else {
            var1.setFrame(this.getClip().getBounds2D());
         }
      } else if (var1 == null) {
         throw new NullPointerException("null rectangle parameter");
      }

      return var1;
   }

   public boolean hitClip(int var1, int var2, int var3, int var4) {
      if (var3 > 0 && var4 > 0) {
         if (this.transformState > 1) {
            double[] var5 = new double[]{(double)var1, (double)var2, (double)(var1 + var3), (double)var2, (double)var1, (double)(var2 + var4), (double)(var1 + var3), (double)(var2 + var4)};
            this.transform.transform((double[])var5, 0, (double[])var5, 0, 4);
            var1 = (int)Math.floor(Math.min(Math.min(var5[0], var5[2]), Math.min(var5[4], var5[6])));
            var2 = (int)Math.floor(Math.min(Math.min(var5[1], var5[3]), Math.min(var5[5], var5[7])));
            var3 = (int)Math.ceil(Math.max(Math.max(var5[0], var5[2]), Math.max(var5[4], var5[6])));
            var4 = (int)Math.ceil(Math.max(Math.max(var5[1], var5[3]), Math.max(var5[5], var5[7])));
         } else {
            var1 += this.transX;
            var2 += this.transY;
            var3 += var1;
            var4 += var2;
         }

         try {
            return this.getCompClip().intersectsQuickCheckXYXY(var1, var2, var3, var4);
         } catch (InvalidPipeException var6) {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void validateCompClip() {
      int var1 = this.clipState;
      if (this.usrClip == null) {
         this.clipState = 0;
         this.clipRegion = this.devClip;
      } else if (this.usrClip instanceof Rectangle2D) {
         this.clipState = 1;
         if (this.usrClip instanceof Rectangle) {
            this.clipRegion = this.devClip.getIntersection((Rectangle)this.usrClip);
         } else {
            this.clipRegion = this.devClip.getIntersection(this.usrClip.getBounds());
         }
      } else {
         PathIterator var2 = this.usrClip.getPathIterator((AffineTransform)null);
         int[] var3 = new int[4];
         ShapeSpanIterator var4 = LoopPipe.getFillSSI(this);

         try {
            var4.setOutputArea(this.devClip);
            var4.appendPath(var2);
            var4.getPathBox(var3);
            Region var5 = Region.getInstance(var3);
            var5.appendSpans(var4);
            this.clipRegion = var5;
            this.clipState = var5.isRectangular() ? 1 : 2;
         } finally {
            var4.dispose();
         }
      }

      if (var1 != this.clipState && (this.clipState == 2 || var1 == 2)) {
         this.validFontInfo = false;
         this.invalidatePipe();
      }

   }

   protected Shape transformShape(Shape var1) {
      if (var1 == null) {
         return null;
      } else {
         return this.transformState > 1 ? transformShape(this.transform, var1) : transformShape(this.transX, this.transY, var1);
      }
   }

   public Shape untransformShape(Shape var1) {
      if (var1 == null) {
         return null;
      } else if (this.transformState > 1) {
         try {
            return transformShape(this.transform.createInverse(), var1);
         } catch (NoninvertibleTransformException var3) {
            return null;
         }
      } else {
         return transformShape(-this.transX, -this.transY, var1);
      }
   }

   protected static Shape transformShape(int var0, int var1, Shape var2) {
      if (var2 == null) {
         return null;
      } else if (var2 instanceof Rectangle) {
         Rectangle var5 = var2.getBounds();
         var5.translate(var0, var1);
         return var5;
      } else if (var2 instanceof Rectangle2D) {
         Rectangle2D var4 = (Rectangle2D)var2;
         return new Rectangle2D.Double(var4.getX() + (double)var0, var4.getY() + (double)var1, var4.getWidth(), var4.getHeight());
      } else if (var0 == 0 && var1 == 0) {
         return cloneShape(var2);
      } else {
         AffineTransform var3 = AffineTransform.getTranslateInstance((double)var0, (double)var1);
         return var3.createTransformedShape(var2);
      }
   }

   protected static Shape transformShape(AffineTransform var0, Shape var1) {
      if (var1 == null) {
         return null;
      } else if (var1 instanceof Rectangle2D && (var0.getType() & 48) == 0) {
         Rectangle2D var2 = (Rectangle2D)var1;
         double[] var3 = new double[]{var2.getX(), var2.getY(), 0.0D, 0.0D};
         var3[2] = var3[0] + var2.getWidth();
         var3[3] = var3[1] + var2.getHeight();
         var0.transform((double[])var3, 0, (double[])var3, 0, 2);
         fixRectangleOrientation(var3, var2);
         return new Rectangle2D.Double(var3[0], var3[1], var3[2] - var3[0], var3[3] - var3[1]);
      } else {
         return var0.isIdentity() ? cloneShape(var1) : var0.createTransformedShape(var1);
      }
   }

   private static void fixRectangleOrientation(double[] var0, Rectangle2D var1) {
      double var2;
      if (var1.getWidth() > 0.0D != var0[2] - var0[0] > 0.0D) {
         var2 = var0[0];
         var0[0] = var0[2];
         var0[2] = var2;
      }

      if (var1.getHeight() > 0.0D != var0[3] - var0[1] > 0.0D) {
         var2 = var0[1];
         var0[1] = var0[3];
         var0[3] = var2;
      }

   }

   public void clipRect(int var1, int var2, int var3, int var4) {
      this.clip(new Rectangle(var1, var2, var3, var4));
   }

   public void setClip(int var1, int var2, int var3, int var4) {
      this.setClip(new Rectangle(var1, var2, var3, var4));
   }

   public Shape getClip() {
      return this.untransformShape(this.usrClip);
   }

   public void setClip(Shape var1) {
      this.usrClip = this.transformShape(var1);
      this.validateCompClip();
   }

   public void clip(Shape var1) {
      var1 = this.transformShape(var1);
      if (this.usrClip != null) {
         var1 = this.intersectShapes(this.usrClip, var1, true, true);
      }

      this.usrClip = var1;
      this.validateCompClip();
   }

   public void setPaintMode() {
      this.setComposite(AlphaComposite.SrcOver);
   }

   public void setXORMode(Color var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null XORColor");
      } else {
         this.setComposite(new XORComposite(var1, this.surfaceData));
      }
   }

   public void copyArea(int var1, int var2, int var3, int var4, int var5, int var6) {
      try {
         this.doCopyArea(var1, var2, var3, var4, var5, var6);
      } catch (InvalidPipeException var14) {
         try {
            this.revalidateAll();
            this.doCopyArea(var1, var2, var3, var4, var5, var6);
         } catch (InvalidPipeException var13) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   private void doCopyArea(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var3 > 0 && var4 > 0) {
         SurfaceData var7 = this.surfaceData;
         if (!var7.copyArea(this, var1, var2, var3, var4, var5, var6)) {
            if (this.transformState > 3) {
               throw new InternalError("transformed copyArea not implemented yet");
            } else {
               Region var8 = this.getCompClip();
               Composite var9 = this.composite;
               if (this.lastCAcomp != var9) {
                  SurfaceType var10 = var7.getSurfaceType();
                  CompositeType var11 = this.imageComp;
                  if (CompositeType.SrcOverNoEa.equals(var11) && var7.getTransparency() == 1) {
                     var11 = CompositeType.SrcNoEa;
                  }

                  this.lastCAblit = Blit.locate(var10, var11, var10);
                  this.lastCAcomp = var9;
               }

               double[] var14 = new double[]{(double)var1, (double)var2, (double)(var1 + var3), (double)(var2 + var4), (double)(var1 + var5), (double)(var2 + var6)};
               this.transform.transform((double[])var14, 0, (double[])var14, 0, 3);
               var1 = (int)Math.ceil(var14[0] - 0.5D);
               var2 = (int)Math.ceil(var14[1] - 0.5D);
               var3 = (int)Math.ceil(var14[2] - 0.5D) - var1;
               var4 = (int)Math.ceil(var14[3] - 0.5D) - var2;
               var5 = (int)Math.ceil(var14[4] - 0.5D) - var1;
               var6 = (int)Math.ceil(var14[5] - 0.5D) - var2;
               if (var3 < 0) {
                  var3 *= -1;
                  var1 -= var3;
               }

               if (var4 < 0) {
                  var4 *= -1;
                  var2 -= var4;
               }

               Blit var15 = this.lastCAblit;
               int var12;
               int var13;
               if (var6 == 0 && var5 > 0 && var5 < var3) {
                  while(var3 > 0) {
                     var12 = Math.min(var3, var5);
                     var3 -= var12;
                     var13 = var1 + var3;
                     var15.Blit(var7, var7, var9, var8, var13, var2, var13 + var5, var2 + var6, var12, var4);
                  }

               } else if (var6 > 0 && var6 < var4 && var5 > -var3 && var5 < var3) {
                  while(var4 > 0) {
                     var12 = Math.min(var4, var6);
                     var4 -= var12;
                     var13 = var2 + var4;
                     var15.Blit(var7, var7, var9, var8, var1, var13, var1 + var5, var13 + var6, var3, var12);
                  }

               } else {
                  var15.Blit(var7, var7, var9, var8, var1, var2, var1 + var5, var2 + var6, var3, var4);
               }
            }
         }
      }
   }

   public void drawLine(int var1, int var2, int var3, int var4) {
      try {
         this.drawpipe.drawLine(this, var1, var2, var3, var4);
      } catch (InvalidPipeException var12) {
         try {
            this.revalidateAll();
            this.drawpipe.drawLine(this, var1, var2, var3, var4);
         } catch (InvalidPipeException var11) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void drawRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      try {
         this.drawpipe.drawRoundRect(this, var1, var2, var3, var4, var5, var6);
      } catch (InvalidPipeException var14) {
         try {
            this.revalidateAll();
            this.drawpipe.drawRoundRect(this, var1, var2, var3, var4, var5, var6);
         } catch (InvalidPipeException var13) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void fillRoundRect(int var1, int var2, int var3, int var4, int var5, int var6) {
      try {
         this.fillpipe.fillRoundRect(this, var1, var2, var3, var4, var5, var6);
      } catch (InvalidPipeException var14) {
         try {
            this.revalidateAll();
            this.fillpipe.fillRoundRect(this, var1, var2, var3, var4, var5, var6);
         } catch (InvalidPipeException var13) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void drawOval(int var1, int var2, int var3, int var4) {
      try {
         this.drawpipe.drawOval(this, var1, var2, var3, var4);
      } catch (InvalidPipeException var12) {
         try {
            this.revalidateAll();
            this.drawpipe.drawOval(this, var1, var2, var3, var4);
         } catch (InvalidPipeException var11) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void fillOval(int var1, int var2, int var3, int var4) {
      try {
         this.fillpipe.fillOval(this, var1, var2, var3, var4);
      } catch (InvalidPipeException var12) {
         try {
            this.revalidateAll();
            this.fillpipe.fillOval(this, var1, var2, var3, var4);
         } catch (InvalidPipeException var11) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void drawArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      try {
         this.drawpipe.drawArc(this, var1, var2, var3, var4, var5, var6);
      } catch (InvalidPipeException var14) {
         try {
            this.revalidateAll();
            this.drawpipe.drawArc(this, var1, var2, var3, var4, var5, var6);
         } catch (InvalidPipeException var13) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void fillArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      try {
         this.fillpipe.fillArc(this, var1, var2, var3, var4, var5, var6);
      } catch (InvalidPipeException var14) {
         try {
            this.revalidateAll();
            this.fillpipe.fillArc(this, var1, var2, var3, var4, var5, var6);
         } catch (InvalidPipeException var13) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void drawPolyline(int[] var1, int[] var2, int var3) {
      try {
         this.drawpipe.drawPolyline(this, var1, var2, var3);
      } catch (InvalidPipeException var11) {
         try {
            this.revalidateAll();
            this.drawpipe.drawPolyline(this, var1, var2, var3);
         } catch (InvalidPipeException var10) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void drawPolygon(int[] var1, int[] var2, int var3) {
      try {
         this.drawpipe.drawPolygon(this, var1, var2, var3);
      } catch (InvalidPipeException var11) {
         try {
            this.revalidateAll();
            this.drawpipe.drawPolygon(this, var1, var2, var3);
         } catch (InvalidPipeException var10) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void fillPolygon(int[] var1, int[] var2, int var3) {
      try {
         this.fillpipe.fillPolygon(this, var1, var2, var3);
      } catch (InvalidPipeException var11) {
         try {
            this.revalidateAll();
            this.fillpipe.fillPolygon(this, var1, var2, var3);
         } catch (InvalidPipeException var10) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void drawRect(int var1, int var2, int var3, int var4) {
      try {
         this.drawpipe.drawRect(this, var1, var2, var3, var4);
      } catch (InvalidPipeException var12) {
         try {
            this.revalidateAll();
            this.drawpipe.drawRect(this, var1, var2, var3, var4);
         } catch (InvalidPipeException var11) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void fillRect(int var1, int var2, int var3, int var4) {
      try {
         this.fillpipe.fillRect(this, var1, var2, var3, var4);
      } catch (InvalidPipeException var12) {
         try {
            this.revalidateAll();
            this.fillpipe.fillRect(this, var1, var2, var3, var4);
         } catch (InvalidPipeException var11) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   private void revalidateAll() {
      this.surfaceData = this.surfaceData.getReplacement();
      if (this.surfaceData == null) {
         this.surfaceData = NullSurfaceData.theInstance;
      }

      this.invalidatePipe();
      this.setDevClip(this.surfaceData.getBounds());
      if (this.paintState <= 1) {
         this.validateColor();
      }

      if (this.composite instanceof XORComposite) {
         Color var1 = ((XORComposite)this.composite).getXorColor();
         this.setComposite(new XORComposite(var1, this.surfaceData));
      }

      this.validatePipe();
   }

   public void clearRect(int var1, int var2, int var3, int var4) {
      Composite var5 = this.composite;
      Paint var6 = this.paint;
      this.setComposite(AlphaComposite.Src);
      this.setColor(this.getBackground());
      this.fillRect(var1, var2, var3, var4);
      this.setPaint(var6);
      this.setComposite(var5);
   }

   public void draw(Shape var1) {
      try {
         this.shapepipe.draw(this, var1);
      } catch (InvalidPipeException var9) {
         try {
            this.revalidateAll();
            this.shapepipe.draw(this, var1);
         } catch (InvalidPipeException var8) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   public void fill(Shape var1) {
      try {
         this.shapepipe.fill(this, var1);
      } catch (InvalidPipeException var9) {
         try {
            this.revalidateAll();
            this.shapepipe.fill(this, var1);
         } catch (InvalidPipeException var8) {
         }
      } finally {
         this.surfaceData.markDirty();
      }

   }

   private static boolean isIntegerTranslation(AffineTransform var0) {
      if (var0.isIdentity()) {
         return true;
      } else if (var0.getType() != 1) {
         return false;
      } else {
         double var1 = var0.getTranslateX();
         double var3 = var0.getTranslateY();
         return var1 == (double)((int)var1) && var3 == (double)((int)var3);
      }
   }

   private static int getTileIndex(int var0, int var1, int var2) {
      var0 -= var1;
      if (var0 < 0) {
         var0 += 1 - var2;
      }

      return var0 / var2;
   }

   private static Rectangle getImageRegion(RenderedImage var0, Region var1, AffineTransform var2, AffineTransform var3, int var4, int var5) {
      Rectangle var6 = new Rectangle(var0.getMinX(), var0.getMinY(), var0.getWidth(), var0.getHeight());
      Rectangle var7 = null;

      try {
         double[] var8 = new double[8];
         var8[0] = var8[2] = (double)var1.getLoX();
         var8[4] = var8[6] = (double)var1.getHiX();
         var8[1] = var8[5] = (double)var1.getLoY();
         var8[3] = var8[7] = (double)var1.getHiY();
         var2.inverseTransform(var8, 0, var8, 0, 4);
         var3.inverseTransform(var8, 0, var8, 0, 4);
         double var11;
         double var9 = var11 = var8[0];
         double var15;
         double var13 = var15 = var8[1];
         int var17 = 2;

         while(var17 < 8) {
            double var18 = var8[var17++];
            if (var18 < var9) {
               var9 = var18;
            } else if (var18 > var11) {
               var11 = var18;
            }

            var18 = var8[var17++];
            if (var18 < var13) {
               var13 = var18;
            } else if (var18 > var15) {
               var15 = var18;
            }
         }

         var17 = (int)var9 - var4;
         int var23 = (int)(var11 - var9 + (double)(2 * var4));
         int var19 = (int)var13 - var5;
         int var20 = (int)(var15 - var13 + (double)(2 * var5));
         Rectangle var21 = new Rectangle(var17, var19, var23, var20);
         var7 = var21.intersection(var6);
      } catch (NoninvertibleTransformException var22) {
         var7 = var6;
      }

      return var7;
   }

   public void drawRenderedImage(RenderedImage var1, AffineTransform var2) {
      if (var1 != null) {
         if (var1 instanceof BufferedImage) {
            BufferedImage var19 = (BufferedImage)var1;
            this.drawImage(var19, var2, (ImageObserver)null);
         } else {
            boolean var3 = this.transformState <= 1 && isIntegerTranslation(var2);
            int var4 = var3 ? 0 : 3;

            Region var5;
            try {
               var5 = this.getCompClip();
            } catch (InvalidPipeException var18) {
               return;
            }

            Rectangle var6 = getImageRegion(var1, var5, this.transform, var2, var4, var4);
            if (var6.width > 0 && var6.height > 0) {
               if (var3) {
                  this.drawTranslatedRenderedImage(var1, var6, (int)var2.getTranslateX(), (int)var2.getTranslateY());
               } else {
                  Raster var7 = var1.getData(var6);
                  WritableRaster var8 = Raster.createWritableRaster(var7.getSampleModel(), var7.getDataBuffer(), (Point)null);
                  int var9 = var7.getMinX();
                  int var10 = var7.getMinY();
                  int var11 = var7.getWidth();
                  int var12 = var7.getHeight();
                  int var13 = var9 - var7.getSampleModelTranslateX();
                  int var14 = var10 - var7.getSampleModelTranslateY();
                  if (var13 != 0 || var14 != 0 || var11 != var8.getWidth() || var12 != var8.getHeight()) {
                     var8 = var8.createWritableChild(var13, var14, var11, var12, 0, 0, (int[])null);
                  }

                  AffineTransform var15 = (AffineTransform)var2.clone();
                  var15.translate((double)var9, (double)var10);
                  ColorModel var16 = var1.getColorModel();
                  BufferedImage var17 = new BufferedImage(var16, var8, var16.isAlphaPremultiplied(), (Hashtable)null);
                  this.drawImage(var17, var15, (ImageObserver)null);
               }
            }
         }
      }
   }

   private boolean clipTo(Rectangle var1, Rectangle var2) {
      int var3 = Math.max(var1.x, var2.x);
      int var4 = Math.min(var1.x + var1.width, var2.x + var2.width);
      int var5 = Math.max(var1.y, var2.y);
      int var6 = Math.min(var1.y + var1.height, var2.y + var2.height);
      if (var4 - var3 >= 0 && var6 - var5 >= 0) {
         var1.x = var3;
         var1.y = var5;
         var1.width = var4 - var3;
         var1.height = var6 - var5;
         return true;
      } else {
         var1.width = -1;
         var1.height = -1;
         return false;
      }
   }

   private void drawTranslatedRenderedImage(RenderedImage var1, Rectangle var2, int var3, int var4) {
      int var5 = var1.getTileGridXOffset();
      int var6 = var1.getTileGridYOffset();
      int var7 = var1.getTileWidth();
      int var8 = var1.getTileHeight();
      int var9 = getTileIndex(var2.x, var5, var7);
      int var10 = getTileIndex(var2.y, var6, var8);
      int var11 = getTileIndex(var2.x + var2.width - 1, var5, var7);
      int var12 = getTileIndex(var2.y + var2.height - 1, var6, var8);
      ColorModel var13 = var1.getColorModel();
      Rectangle var14 = new Rectangle();

      for(int var15 = var10; var15 <= var12; ++var15) {
         for(int var16 = var9; var16 <= var11; ++var16) {
            Raster var17 = var1.getTile(var16, var15);
            var14.x = var16 * var7 + var5;
            var14.y = var15 * var8 + var6;
            var14.width = var7;
            var14.height = var8;
            this.clipTo(var14, var2);
            WritableRaster var18 = null;
            if (var17 instanceof WritableRaster) {
               var18 = (WritableRaster)var17;
            } else {
               var18 = Raster.createWritableRaster(var17.getSampleModel(), var17.getDataBuffer(), (Point)null);
            }

            var18 = var18.createWritableChild(var14.x, var14.y, var14.width, var14.height, 0, 0, (int[])null);
            BufferedImage var19 = new BufferedImage(var13, var18, var13.isAlphaPremultiplied(), (Hashtable)null);
            this.copyImage(var19, var14.x + var3, var14.y + var4, 0, 0, var14.width, var14.height, (Color)null, (ImageObserver)null);
         }
      }

   }

   public void drawRenderableImage(RenderableImage var1, AffineTransform var2) {
      if (var1 != null) {
         AffineTransform var3 = this.transform;
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

   protected Rectangle transformBounds(Rectangle var1, AffineTransform var2) {
      if (var2.isIdentity()) {
         return var1;
      } else {
         Shape var3 = transformShape(var2, var1);
         return var3.getBounds();
      }
   }

   public void drawString(String var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("String is null");
      } else if (this.font.hasLayoutAttributes()) {
         if (var1.length() != 0) {
            (new TextLayout(var1, this.font, this.getFontRenderContext())).draw(this, (float)var2, (float)var3);
         }
      } else {
         try {
            this.textpipe.drawString(this, var1, (double)var2, (double)var3);
         } catch (InvalidPipeException var11) {
            try {
               this.revalidateAll();
               this.textpipe.drawString(this, var1, (double)var2, (double)var3);
            } catch (InvalidPipeException var10) {
            }
         } finally {
            this.surfaceData.markDirty();
         }

      }
   }

   public void drawString(String var1, float var2, float var3) {
      if (var1 == null) {
         throw new NullPointerException("String is null");
      } else if (this.font.hasLayoutAttributes()) {
         if (var1.length() != 0) {
            (new TextLayout(var1, this.font, this.getFontRenderContext())).draw(this, var2, var3);
         }
      } else {
         try {
            this.textpipe.drawString(this, var1, (double)var2, (double)var3);
         } catch (InvalidPipeException var11) {
            try {
               this.revalidateAll();
               this.textpipe.drawString(this, var1, (double)var2, (double)var3);
            } catch (InvalidPipeException var10) {
            }
         } finally {
            this.surfaceData.markDirty();
         }

      }
   }

   public void drawString(AttributedCharacterIterator var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("AttributedCharacterIterator is null");
      } else if (var1.getBeginIndex() != var1.getEndIndex()) {
         TextLayout var4 = new TextLayout(var1, this.getFontRenderContext());
         var4.draw(this, (float)var2, (float)var3);
      }
   }

   public void drawString(AttributedCharacterIterator var1, float var2, float var3) {
      if (var1 == null) {
         throw new NullPointerException("AttributedCharacterIterator is null");
      } else if (var1.getBeginIndex() != var1.getEndIndex()) {
         TextLayout var4 = new TextLayout(var1, this.getFontRenderContext());
         var4.draw(this, var2, var3);
      }
   }

   public void drawGlyphVector(GlyphVector var1, float var2, float var3) {
      if (var1 == null) {
         throw new NullPointerException("GlyphVector is null");
      } else {
         try {
            this.textpipe.drawGlyphVector(this, var1, var2, var3);
         } catch (InvalidPipeException var11) {
            try {
               this.revalidateAll();
               this.textpipe.drawGlyphVector(this, var1, var2, var3);
            } catch (InvalidPipeException var10) {
            }
         } finally {
            this.surfaceData.markDirty();
         }

      }
   }

   public void drawChars(char[] var1, int var2, int var3, int var4, int var5) {
      if (var1 == null) {
         throw new NullPointerException("char data is null");
      } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
         if (this.font.hasLayoutAttributes()) {
            if (var1.length != 0) {
               (new TextLayout(new String(var1, var2, var3), this.font, this.getFontRenderContext())).draw(this, (float)var4, (float)var5);
            }
         } else {
            try {
               this.textpipe.drawChars(this, var1, var2, var3, var4, var5);
            } catch (InvalidPipeException var13) {
               try {
                  this.revalidateAll();
                  this.textpipe.drawChars(this, var1, var2, var3, var4, var5);
               } catch (InvalidPipeException var12) {
               }
            } finally {
               this.surfaceData.markDirty();
            }

         }
      } else {
         throw new ArrayIndexOutOfBoundsException("bad offset/length");
      }
   }

   public void drawBytes(byte[] var1, int var2, int var3, int var4, int var5) {
      if (var1 == null) {
         throw new NullPointerException("byte data is null");
      } else if (var2 >= 0 && var3 >= 0 && var2 + var3 <= var1.length) {
         char[] var6 = new char[var3];

         for(int var7 = var3; var7-- > 0; var6[var7] = (char)(var1[var7 + var2] & 255)) {
         }

         if (this.font.hasLayoutAttributes()) {
            if (var1.length != 0) {
               (new TextLayout(new String(var6), this.font, this.getFontRenderContext())).draw(this, (float)var4, (float)var5);
            }
         } else {
            try {
               this.textpipe.drawChars(this, var6, 0, var3, var4, var5);
            } catch (InvalidPipeException var14) {
               try {
                  this.revalidateAll();
                  this.textpipe.drawChars(this, var6, 0, var3, var4, var5);
               } catch (InvalidPipeException var13) {
               }
            } finally {
               this.surfaceData.markDirty();
            }

         }
      } else {
         throw new ArrayIndexOutOfBoundsException("bad offset/length");
      }
   }

   private boolean isHiDPIImage(Image var1) {
      return SurfaceManager.getImageScale(var1) != 1 || this.resolutionVariantHint != 1 && var1 instanceof MultiResolutionImage;
   }

   private boolean drawHiDPIImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      int var12;
      if (SurfaceManager.getImageScale(var1) != 1) {
         var12 = SurfaceManager.getImageScale(var1);
         var6 = Region.clipScale(var6, (double)var12);
         var8 = Region.clipScale(var8, (double)var12);
         var7 = Region.clipScale(var7, (double)var12);
         var9 = Region.clipScale(var9, (double)var12);
      } else if (var1 instanceof MultiResolutionImage) {
         var12 = var1.getWidth(var11);
         int var13 = var1.getHeight(var11);
         Image var14 = this.getResolutionVariant((MultiResolutionImage)var1, var12, var13, var2, var3, var4, var5, var6, var7, var8, var9);
         if (var14 != var1 && var14 != null) {
            ImageObserver var15 = MultiResolutionToolkitImage.getResolutionVariantObserver(var1, var11, var12, var13, -1, -1);
            int var16 = var14.getWidth(var15);
            int var17 = var14.getHeight(var15);
            if (0 < var12 && 0 < var13 && 0 < var16 && 0 < var17) {
               float var18 = (float)var16 / (float)var12;
               float var19 = (float)var17 / (float)var13;
               var6 = Region.clipScale(var6, (double)var18);
               var7 = Region.clipScale(var7, (double)var19);
               var8 = Region.clipScale(var8, (double)var18);
               var9 = Region.clipScale(var9, (double)var19);
               var11 = var15;
               var1 = var14;
            }
         }
      }

      boolean var29;
      try {
         boolean var27 = this.imagepipe.scaleImage(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
         return var27;
      } catch (InvalidPipeException var25) {
         try {
            this.revalidateAll();
            boolean var28 = this.imagepipe.scaleImage(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
            return var28;
         } catch (InvalidPipeException var24) {
            var29 = false;
         }
      } finally {
         this.surfaceData.markDirty();
      }

      return var29;
   }

   private Image getResolutionVariant(MultiResolutionImage var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      if (var2 > 0 && var3 > 0) {
         int var12 = var10 - var8;
         int var13 = var11 - var9;
         if (var12 != 0 && var13 != 0) {
            int var14 = this.transform.getType();
            int var15 = var6 - var4;
            int var16 = var7 - var5;
            double var17;
            double var19;
            if ((var14 & -66) == 0) {
               var17 = (double)var15;
               var19 = (double)var16;
            } else if ((var14 & -72) == 0) {
               var17 = (double)var15 * this.transform.getScaleX();
               var19 = (double)var16 * this.transform.getScaleY();
            } else {
               var17 = (double)var15 * Math.hypot(this.transform.getScaleX(), this.transform.getShearY());
               var19 = (double)var16 * Math.hypot(this.transform.getShearX(), this.transform.getScaleY());
            }

            int var21 = (int)Math.abs((double)var2 * var17 / (double)var12);
            int var22 = (int)Math.abs((double)var3 * var19 / (double)var13);
            Image var23 = var1.getResolutionVariant(var21, var22);
            return var23 instanceof ToolkitImage && ((ToolkitImage)var23).hasError() ? null : var23;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, ImageObserver var6) {
      return this.drawImage(var1, var2, var3, var4, var5, (Color)null, var6);
   }

   public boolean copyImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, Color var8, ImageObserver var9) {
      boolean var12;
      try {
         boolean var10 = this.imagepipe.copyImage(this, var1, var2, var3, var4, var5, var6, var7, var8, var9);
         return var10;
      } catch (InvalidPipeException var18) {
         try {
            this.revalidateAll();
            boolean var11 = this.imagepipe.copyImage(this, var1, var2, var3, var4, var5, var6, var7, var8, var9);
            return var11;
         } catch (InvalidPipeException var17) {
            var12 = false;
         }
      } finally {
         this.surfaceData.markDirty();
      }

      return var12;
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, Color var6, ImageObserver var7) {
      if (var1 == null) {
         return true;
      } else if (var4 != 0 && var5 != 0) {
         int var8 = var1.getWidth((ImageObserver)null);
         int var9 = var1.getHeight((ImageObserver)null);
         if (this.isHiDPIImage(var1)) {
            return this.drawHiDPIImage(var1, var2, var3, var2 + var4, var3 + var5, 0, 0, var8, var9, var6, var7);
         } else if (var4 == var8 && var5 == var9) {
            return this.copyImage(var1, var2, var3, 0, 0, var4, var5, var6, var7);
         } else {
            boolean var12;
            try {
               boolean var10 = this.imagepipe.scaleImage(this, var1, var2, var3, var4, var5, var6, var7);
               return var10;
            } catch (InvalidPipeException var18) {
               try {
                  this.revalidateAll();
                  boolean var11 = this.imagepipe.scaleImage(this, var1, var2, var3, var4, var5, var6, var7);
                  return var11;
               } catch (InvalidPipeException var17) {
                  var12 = false;
               }
            } finally {
               this.surfaceData.markDirty();
            }

            return var12;
         }
      } else {
         return true;
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, ImageObserver var4) {
      return this.drawImage(var1, var2, var3, (Color)null, var4);
   }

   public boolean drawImage(Image var1, int var2, int var3, Color var4, ImageObserver var5) {
      if (var1 == null) {
         return true;
      } else if (this.isHiDPIImage(var1)) {
         int var16 = var1.getWidth((ImageObserver)null);
         int var17 = var1.getHeight((ImageObserver)null);
         return this.drawHiDPIImage(var1, var2, var3, var2 + var16, var3 + var17, 0, 0, var16, var17, var4, var5);
      } else {
         boolean var7;
         try {
            boolean var6 = this.imagepipe.copyImage(this, var1, var2, var3, var4, var5);
            return var6;
         } catch (InvalidPipeException var14) {
            try {
               this.revalidateAll();
               var7 = this.imagepipe.copyImage(this, var1, var2, var3, var4, var5);
            } catch (InvalidPipeException var13) {
               boolean var8 = false;
               return var8;
            }
         } finally {
            this.surfaceData.markDirty();
         }

         return var7;
      }
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, ImageObserver var10) {
      return this.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, (Color)null, var10);
   }

   public boolean drawImage(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, Color var10, ImageObserver var11) {
      if (var1 == null) {
         return true;
      } else if (var2 != var4 && var3 != var5 && var6 != var8 && var7 != var9) {
         if (this.isHiDPIImage(var1)) {
            return this.drawHiDPIImage(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
         } else if (var8 - var6 == var4 - var2 && var9 - var7 == var5 - var3) {
            int var16;
            int var25;
            int var27;
            if (var8 > var6) {
               var16 = var8 - var6;
               var25 = var6;
               var27 = var2;
            } else {
               var16 = var6 - var8;
               var25 = var8;
               var27 = var4;
            }

            int var15;
            int var17;
            int var26;
            if (var9 > var7) {
               var17 = var9 - var7;
               var26 = var7;
               var15 = var3;
            } else {
               var17 = var7 - var9;
               var26 = var9;
               var15 = var5;
            }

            return this.copyImage(var1, var27, var15, var25, var26, var16, var17, var10, var11);
         } else {
            boolean var13;
            try {
               boolean var12 = this.imagepipe.scaleImage(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
               return var12;
            } catch (InvalidPipeException var23) {
               try {
                  this.revalidateAll();
                  var13 = this.imagepipe.scaleImage(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
               } catch (InvalidPipeException var22) {
                  boolean var14 = false;
                  return var14;
               }
            } finally {
               this.surfaceData.markDirty();
            }

            return var13;
         }
      } else {
         return true;
      }
   }

   public boolean drawImage(Image var1, AffineTransform var2, ImageObserver var3) {
      if (var1 == null) {
         return true;
      } else if (var2 != null && !var2.isIdentity()) {
         if (this.isHiDPIImage(var1)) {
            int var15 = var1.getWidth((ImageObserver)null);
            int var16 = var1.getHeight((ImageObserver)null);
            AffineTransform var17 = new AffineTransform(this.transform);
            this.transform(var2);
            boolean var7 = this.drawHiDPIImage(var1, 0, 0, var15, var16, 0, 0, var15, var16, (Color)null, var3);
            this.transform.setTransform(var17);
            this.invalidateTransform();
            return var7;
         } else {
            boolean var6;
            try {
               boolean var4 = this.imagepipe.transformImage(this, var1, var2, var3);
               return var4;
            } catch (InvalidPipeException var13) {
               try {
                  this.revalidateAll();
                  boolean var5 = this.imagepipe.transformImage(this, var1, var2, var3);
                  return var5;
               } catch (InvalidPipeException var12) {
                  var6 = false;
               }
            } finally {
               this.surfaceData.markDirty();
            }

            return var6;
         }
      } else {
         return this.drawImage(var1, 0, 0, (Color)null, var3);
      }
   }

   public void drawImage(BufferedImage var1, BufferedImageOp var2, int var3, int var4) {
      if (var1 != null) {
         try {
            this.imagepipe.transformImage(this, var1, var2, var3, var4);
         } catch (InvalidPipeException var12) {
            try {
               this.revalidateAll();
               this.imagepipe.transformImage(this, var1, var2, var3, var4);
            } catch (InvalidPipeException var11) {
            }
         } finally {
            this.surfaceData.markDirty();
         }

      }
   }

   public FontRenderContext getFontRenderContext() {
      if (this.cachedFRC == null) {
         int var1 = this.textAntialiasHint;
         if (var1 == 0 && this.antialiasHint == 2) {
            var1 = 2;
         }

         AffineTransform var2 = null;
         if (this.transformState >= 3) {
            if (this.transform.getTranslateX() == 0.0D && this.transform.getTranslateY() == 0.0D) {
               var2 = this.transform;
            } else {
               var2 = new AffineTransform(this.transform.getScaleX(), this.transform.getShearY(), this.transform.getShearX(), this.transform.getScaleY(), 0.0D, 0.0D);
            }
         }

         this.cachedFRC = new FontRenderContext(var2, SunHints.Value.get(2, var1), SunHints.Value.get(3, this.fractionalMetricsHint));
      }

      return this.cachedFRC;
   }

   public void dispose() {
      this.surfaceData = NullSurfaceData.theInstance;
      this.invalidatePipe();
   }

   public void finalize() {
   }

   public Object getDestination() {
      return this.surfaceData.getDestination();
   }

   public Surface getDestSurface() {
      return this.surfaceData;
   }

   static {
      defaultComposite = AlphaComposite.SrcOver;
      defaultFont = new Font("Dialog", 0, 12);
      lcdTextContrastDefaultValue = 140;
      if (PerformanceLogger.loggingEnabled()) {
         PerformanceLogger.setTime("SunGraphics2D static initialization");
      }

      invalidpipe = new ValidatePipe();
      IDENT_MATRIX = new double[]{1.0D, 0.0D, 0.0D, 1.0D};
      IDENT_ATX = new AffineTransform();
      textTxArr = new double[17][];
      textAtArr = new AffineTransform[17];

      for(int var0 = 8; var0 < 17; ++var0) {
         textTxArr[var0] = new double[]{(double)var0, 0.0D, 0.0D, (double)var0};
         textAtArr[var0] = new AffineTransform(textTxArr[var0]);
      }

      MinPenSizeAA = (double)RenderingEngine.getInstance().getMinimumAAPenSize();
      MinPenSizeAASquared = MinPenSizeAA * MinPenSizeAA;
   }
}
