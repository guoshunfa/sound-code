package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.TexturePaint;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Hashtable;
import javax.swing.plaf.ColorUIResource;
import sun.awt.SunHints;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.lwawt.macosx.CTextPipe;

public abstract class OSXSurfaceData extends BufImgSurfaceData {
   static final float UPPER_BND = 1.7014117E38F;
   static final float LOWER_BND = -1.7014117E38F;
   protected static CRenderer sQuartzPipe = null;
   protected static CTextPipe sCocoaTextPipe = null;
   protected static CompositeCRenderer sQuartzCompositePipe = null;
   private GraphicsConfiguration fConfig;
   private Rectangle fBounds;
   GraphicsConfiguration sDefaultGraphicsConfiguration;
   BufferedImage sSrcComposite;
   BufferedImage sDstInComposite;
   BufferedImage sDstOutComposite;
   static final int kPrimitive = 0;
   static final int kImage = 1;
   static final int kText = 2;
   static final int kCopyArea = 3;
   static final int kExternal = 4;
   static final int kLine = 5;
   static final int kRect = 6;
   static final int kRoundRect = 7;
   static final int kOval = 8;
   static final int kArc = 9;
   static final int kPolygon = 10;
   static final int kShape = 11;
   static final int kString = 13;
   static final int kGlyphs = 14;
   static final int kUnicodes = 15;
   static final int kCommonParameterCount = 10;
   static final int kLineParametersCount = 10;
   static final int kRectParametersCount = 11;
   static final int kRoundRectParametersCount = 13;
   static final int kOvalParametersCount = 11;
   static final int kArcParametersCount = 14;
   static final int kPolygonParametersCount = 0;
   static final int kShapeParametersCount = 0;
   static final int kImageParametersCount = 22;
   static final int kStringParametersCount = 0;
   static final int kGlyphsParametersCount = 0;
   static final int kUnicodesParametersCount = 0;
   static final int kPixelParametersCount = 0;
   static final int kExternalParametersCount = 0;
   static final int kChangeFlagIndex = 0;
   static final int kBoundsXIndex = 1;
   static final int kBoundsYIndex = 2;
   static final int kBoundsWidthIndex = 3;
   static final int kBoundsHeightIndex = 4;
   static final int kClipStateIndex = 5;
   static final int kClipNumTypesIndex = 6;
   static final int kClipNumCoordsIndex = 7;
   static final int kClipWindingRuleIndex = 8;
   static final int kClipXIndex = 9;
   static final int kClipYIndex = 10;
   static final int kClipWidthIndex = 11;
   static final int kClipHeightIndex = 12;
   static final int kCTMaIndex = 13;
   static final int kCTMbIndex = 14;
   static final int kCTMcIndex = 15;
   static final int kCTMdIndex = 16;
   static final int kCTMtxIndex = 17;
   static final int kCTMtyIndex = 18;
   static final int kColorStateIndex = 19;
   static final int kColorRGBValueIndex = 20;
   static final int kColorIndexValueIndex = 21;
   static final int kColorPointerIndex = 22;
   static final int kColorPointerIndex2 = 23;
   static final int kColorRGBValue1Index = 24;
   static final int kColorWidthIndex = 25;
   static final int kColorRGBValue2Index = 26;
   static final int kColorHeightIndex = 27;
   static final int kColorIsCyclicIndex = 28;
   static final int kColorx1Index = 29;
   static final int kColortxIndex = 30;
   static final int kColory1Index = 31;
   static final int kColortyIndex = 32;
   static final int kColorx2Index = 33;
   static final int kColorsxIndex = 34;
   static final int kColory2Index = 35;
   static final int kColorsyIndex = 36;
   static final int kCompositeRuleIndex = 37;
   static final int kCompositeValueIndex = 38;
   static final int kStrokeJoinIndex = 39;
   static final int kStrokeCapIndex = 40;
   static final int kStrokeWidthIndex = 41;
   static final int kStrokeDashPhaseIndex = 42;
   static final int kStrokeLimitIndex = 43;
   static final int kHintsAntialiasIndex = 44;
   static final int kHintsTextAntialiasIndex = 45;
   static final int kHintsFractionalMetricsIndex = 46;
   static final int kHintsRenderingIndex = 47;
   static final int kHintsInterpolationIndex = 48;
   static final int kRadiusIndex = 49;
   static final int kSizeOfParameters = 50;
   static final int kClipCoordinatesIndex = 0;
   static final int kClipTypesIndex = 1;
   static final int kTextureImageIndex = 2;
   static final int kStrokeDashArrayIndex = 3;
   static final int kFontIndex = 4;
   static final int kFontPaintIndex = 5;
   static final int kColorArrayIndex = 6;
   static final int kFractionsArrayIndex = 7;
   static final int kBoundsChangedBit = 1;
   static final int kBoundsNotChangedBit = -2;
   static final int kClipChangedBit = 2;
   static final int kClipNotChangedBit = -3;
   static final int kCTMChangedBit = 4;
   static final int kCTMNotChangedBit = -5;
   static final int kColorChangedBit = 8;
   static final int kColorNotChangedBit = -9;
   static final int kCompositeChangedBit = 16;
   static final int kCompositeNotChangedBit = -17;
   static final int kStrokeChangedBit = 32;
   static final int kStrokeNotChangedBit = -33;
   static final int kHintsChangedBit = 64;
   static final int kHintsNotChangedBit = -65;
   static final int kFontChangedBit = 128;
   static final int kFontNotChangedBit = -129;
   static final int kEverythingChangedFlag = -1;
   static final int kColorSimple = 0;
   static final int kColorSystem = 1;
   static final int kColorGradient = 2;
   static final int kColorTexture = 3;
   static final int kColorLinearGradient = 4;
   static final int kColorRadialGradient = 5;
   static final int kColorNonCyclic = 0;
   static final int kColorCyclic = 1;
   static final int kClipRect = 0;
   static final int kClipShape = 1;
   int fChangeFlag;
   protected ByteBuffer fGraphicsStates;
   IntBuffer fGraphicsStatesInt;
   FloatBuffer fGraphicsStatesFloat;
   LongBuffer fGraphicsStatesLong;
   protected Object[] fGraphicsStatesObject;
   Rectangle userBounds;
   float lastUserX;
   float lastUserY;
   float lastUserW;
   float lastUserH;
   FloatBuffer clipCoordinatesArray;
   IntBuffer clipTypesArray;
   Shape lastClipShape;
   float lastClipX;
   float lastClipY;
   float lastClipW;
   float lastClipH;
   final double[] lastCTM;
   float lastCTMa;
   float lastCTMb;
   float lastCTMc;
   float lastCTMd;
   float lastCTMtx;
   float lastCTMty;
   static AffineTransform sIdentityMatrix;
   Paint lastPaint;
   long lastPaintPtr;
   int lastPaintRGB;
   int lastPaintIndex;
   BufferedImage texturePaintImage;
   Composite lastComposite;
   int lastCompositeAlphaRule;
   float lastCompositeAlphaValue;
   BasicStroke lastStroke;
   static BasicStroke defaultBasicStroke;
   Font lastFont;
   SunGraphics2D sg2dCurrent;
   Thread threadCurrent;
   final float[] segmentCoordinatesArray;
   FloatBuffer shapeCoordinatesArray;
   IntBuffer shapeTypesArray;
   Rectangle srcCopyAreaRect;
   Rectangle dstCopyAreaRect;
   Rectangle finalCopyAreaRect;
   Rectangle copyAreaBounds;

   public OSXSurfaceData(SurfaceType var1, ColorModel var2) {
      this(var1, var2, (GraphicsConfiguration)null, new Rectangle());
   }

   public OSXSurfaceData(SurfaceType var1, ColorModel var2, GraphicsConfiguration var3, Rectangle var4) {
      super(var1, var2);
      this.sDefaultGraphicsConfiguration = null;
      this.sSrcComposite = null;
      this.sDstInComposite = null;
      this.sDstOutComposite = null;
      this.fGraphicsStates = null;
      this.fGraphicsStatesInt = null;
      this.fGraphicsStatesFloat = null;
      this.fGraphicsStatesLong = null;
      this.fGraphicsStatesObject = null;
      this.userBounds = new Rectangle();
      this.lastUserX = 0.0F;
      this.lastUserY = 0.0F;
      this.lastUserW = 0.0F;
      this.lastUserH = 0.0F;
      this.clipCoordinatesArray = null;
      this.clipTypesArray = null;
      this.lastClipShape = null;
      this.lastClipX = 0.0F;
      this.lastClipY = 0.0F;
      this.lastClipW = 0.0F;
      this.lastClipH = 0.0F;
      this.lastCTM = new double[6];
      this.lastCTMa = 0.0F;
      this.lastCTMb = 0.0F;
      this.lastCTMc = 0.0F;
      this.lastCTMd = 0.0F;
      this.lastCTMtx = 0.0F;
      this.lastCTMty = 0.0F;
      this.lastPaint = null;
      this.lastPaintPtr = 0L;
      this.lastPaintRGB = 0;
      this.lastPaintIndex = 0;
      this.texturePaintImage = null;
      this.lastCompositeAlphaRule = 0;
      this.lastCompositeAlphaValue = 0.0F;
      this.lastStroke = null;
      this.sg2dCurrent = null;
      this.threadCurrent = null;
      this.segmentCoordinatesArray = new float[6];
      this.shapeCoordinatesArray = null;
      this.shapeTypesArray = null;
      this.srcCopyAreaRect = new Rectangle();
      this.dstCopyAreaRect = new Rectangle();
      this.finalCopyAreaRect = new Rectangle();
      this.copyAreaBounds = new Rectangle();
      this.fConfig = var3;
      this.fBounds = new Rectangle(var4.x, var4.y, var4.width, var4.y + var4.height);
      this.fGraphicsStates = getBufferOfSize(50);
      this.fGraphicsStatesInt = this.fGraphicsStates.asIntBuffer();
      this.fGraphicsStatesFloat = this.fGraphicsStates.asFloatBuffer();
      this.fGraphicsStatesLong = this.fGraphicsStates.asLongBuffer();
      this.fGraphicsStatesObject = new Object[8];
   }

   public void validatePipe(SunGraphics2D var1) {
      if (var1.compositeState <= 1) {
         if (sCocoaTextPipe == null) {
            sCocoaTextPipe = new CTextPipe();
         }

         var1.imagepipe = sQuartzPipe;
         var1.drawpipe = sQuartzPipe;
         var1.fillpipe = sQuartzPipe;
         var1.shapepipe = sQuartzPipe;
         var1.textpipe = sCocoaTextPipe;
      } else {
         this.setPipesToQuartzComposite(var1);
      }

   }

   protected void setPipesToQuartzComposite(SunGraphics2D var1) {
      if (sQuartzCompositePipe == null) {
         sQuartzCompositePipe = new CompositeCRenderer();
      }

      if (sCocoaTextPipe == null) {
         sCocoaTextPipe = new CTextPipe();
      }

      var1.imagepipe = sQuartzCompositePipe;
      var1.drawpipe = sQuartzCompositePipe;
      var1.fillpipe = sQuartzCompositePipe;
      var1.shapepipe = sQuartzCompositePipe;
      var1.textpipe = sCocoaTextPipe;
   }

   public Rectangle getBounds() {
      return new Rectangle(this.fBounds.x, this.fBounds.y, this.fBounds.width, this.fBounds.height - this.fBounds.y);
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.fConfig;
   }

   protected void setBounds(int var1, int var2, int var3, int var4) {
      this.fBounds.reshape(var1, var2, var3, var2 + var4);
   }

   public abstract BufferedImage copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, BufferedImage var6);

   public abstract boolean xorSurfacePixels(SunGraphics2D var1, BufferedImage var2, int var3, int var4, int var5, int var6, int var7);

   protected BufferedImage getCompositingImage(int var1, int var2) {
      if (this.sDefaultGraphicsConfiguration == null) {
         this.sDefaultGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      }

      BufferedImage var3 = new BufferedImage(var1, var2, 3);
      this.clearRect(var3, var1, var2);
      return var3;
   }

   protected BufferedImage getCompositingImageSame(BufferedImage var1, int var2, int var3) {
      if (var1 == null || var1.getWidth() != var2 || var1.getHeight() != var3) {
         var1 = this.getCompositingImage(var2, var3);
      }

      return var1;
   }

   public BufferedImage getCompositingSrcImage(int var1, int var2) {
      BufferedImage var3 = this.getCompositingImageSame(this.sSrcComposite, var1, var2);
      this.sSrcComposite = var3;
      return var3;
   }

   public BufferedImage getCompositingDstInImage(int var1, int var2) {
      BufferedImage var3 = this.getCompositingImageSame(this.sDstInComposite, var1, var2);
      this.sDstInComposite = var3;
      return var3;
   }

   public BufferedImage getCompositingDstOutImage(int var1, int var2) {
      BufferedImage var3 = this.getCompositingImageSame(this.sDstOutComposite, var1, var2);
      this.sDstOutComposite = var3;
      return var3;
   }

   public void clearRect(BufferedImage var1, int var2, int var3) {
      Graphics2D var4 = var1.createGraphics();
      var4.setComposite(AlphaComposite.Clear);
      var4.fillRect(0, 0, var2, var3);
      var4.dispose();
   }

   public void invalidate() {
   }

   static int getRendererTypeForPrimitive(int var0) {
      switch(var0) {
      case 1:
         return 1;
      case 2:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      default:
         return 0;
      case 3:
         return 3;
      case 4:
         return 4;
      case 13:
      case 14:
      case 15:
         return 2;
      }
   }

   void setUserBounds(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      if (this.lastUserX == (float)var2 && this.lastUserY == (float)var3 && this.lastUserW == (float)var4 && this.lastUserH == (float)var5) {
         this.fChangeFlag &= -2;
      } else {
         this.lastUserX = (float)var2;
         this.lastUserY = (float)var3;
         this.lastUserW = (float)var4;
         this.lastUserH = (float)var5;
         this.fGraphicsStatesInt.put(1, var2);
         this.fGraphicsStatesInt.put(2, var3);
         this.fGraphicsStatesInt.put(3, var4);
         this.fGraphicsStatesInt.put(4, var5);
         this.userBounds.setBounds(var2, var3, var4, var5);
         this.fChangeFlag |= 1;
      }

   }

   static ByteBuffer getBufferOfSize(int var0) {
      ByteBuffer var1 = ByteBuffer.allocateDirect(var0 * 4);
      var1.order(ByteOrder.nativeOrder());
      return var1;
   }

   void setupClip(SunGraphics2D var1) {
      switch(var1.clipState) {
      case 0:
      case 1:
         Region var7 = var1.getCompClip();
         float var8 = (float)var7.getLoX();
         float var9 = (float)var7.getLoY();
         float var5 = (float)var7.getWidth();
         float var6 = (float)var7.getHeight();
         if (this.fGraphicsStatesInt.get(5) == 0 && var8 == this.lastClipX && var9 == this.lastClipY && var5 == this.lastClipW && var6 == this.lastClipH) {
            this.fChangeFlag &= -3;
         } else {
            this.fGraphicsStatesFloat.put(9, var8);
            this.fGraphicsStatesFloat.put(10, var9);
            this.fGraphicsStatesFloat.put(11, var5);
            this.fGraphicsStatesFloat.put(12, var6);
            this.lastClipX = var8;
            this.lastClipY = var9;
            this.lastClipW = var5;
            this.lastClipH = var6;
            this.fChangeFlag |= 2;
         }

         this.fGraphicsStatesInt.put(5, 0);
         break;
      case 2:
         this.lastClipShape = var1.usrClip;
         GeneralPath var2 = null;
         if (var1.usrClip instanceof GeneralPath) {
            var2 = (GeneralPath)var1.usrClip;
         } else {
            var2 = new GeneralPath(var1.usrClip);
         }

         int var3 = this.getPathLength(var2);
         if (this.clipCoordinatesArray == null || this.clipCoordinatesArray.capacity() < var3 * 6) {
            this.clipCoordinatesArray = getBufferOfSize(var3 * 6).asFloatBuffer();
         }

         if (this.clipTypesArray == null || this.clipTypesArray.capacity() < var3) {
            this.clipTypesArray = getBufferOfSize(var3).asIntBuffer();
         }

         int var4 = this.getPathCoordinates(var2, this.clipCoordinatesArray, this.clipTypesArray);
         this.fGraphicsStatesInt.put(6, this.clipTypesArray.position());
         this.fGraphicsStatesInt.put(7, this.clipCoordinatesArray.position());
         this.fGraphicsStatesInt.put(8, var4);
         this.fGraphicsStatesObject[1] = this.clipTypesArray;
         this.fGraphicsStatesObject[0] = this.clipCoordinatesArray;
         this.fChangeFlag |= 2;
         this.fGraphicsStatesInt.put(5, 1);
      }

   }

   void setupTransform(SunGraphics2D var1) {
      var1.transform.getMatrix(this.lastCTM);
      float var2 = (float)this.lastCTM[0];
      float var3 = (float)this.lastCTM[1];
      float var4 = (float)this.lastCTM[2];
      float var5 = (float)this.lastCTM[3];
      float var6 = (float)this.lastCTM[4];
      float var7 = (float)this.lastCTM[5];
      if (var6 == this.lastCTMtx && var7 == this.lastCTMty && var2 == this.lastCTMa && var3 == this.lastCTMb && var4 == this.lastCTMc && var5 == this.lastCTMd) {
         this.fChangeFlag &= -5;
      } else {
         this.fGraphicsStatesFloat.put(13, var2);
         this.fGraphicsStatesFloat.put(14, var3);
         this.fGraphicsStatesFloat.put(15, var4);
         this.fGraphicsStatesFloat.put(16, var5);
         this.fGraphicsStatesFloat.put(17, var6);
         this.fGraphicsStatesFloat.put(18, var7);
         this.lastCTMa = var2;
         this.lastCTMb = var3;
         this.lastCTMc = var4;
         this.lastCTMd = var5;
         this.lastCTMtx = var6;
         this.lastCTMty = var7;
         this.fChangeFlag |= 4;
      }

   }

   void setGradientViaRasterPath(SunGraphics2D var1) {
      if (this.fGraphicsStatesInt.get(19) == 3 && this.lastPaint == var1.paint && (this.fChangeFlag & 1) == 0) {
         this.fChangeFlag &= -9;
      } else {
         PaintContext var2 = var1.paint.createContext(var1.getDeviceColorModel(), this.userBounds, this.userBounds, sIdentityMatrix, var1.getRenderingHints());
         WritableRaster var3 = (WritableRaster)((WritableRaster)var2.getRaster(this.userBounds.x, this.userBounds.y, this.userBounds.width, this.userBounds.height));
         ColorModel var4 = var2.getColorModel();
         this.texturePaintImage = new BufferedImage(var4, var3, var4.isAlphaPremultiplied(), (Hashtable)null);
         this.fGraphicsStatesInt.put(19, 3);
         this.fGraphicsStatesInt.put(25, this.texturePaintImage.getWidth());
         this.fGraphicsStatesInt.put(27, this.texturePaintImage.getHeight());
         this.fGraphicsStatesFloat.put(30, (float)this.userBounds.getX());
         this.fGraphicsStatesFloat.put(32, (float)this.userBounds.getY());
         this.fGraphicsStatesFloat.put(34, 1.0F);
         this.fGraphicsStatesFloat.put(36, 1.0F);
         this.fGraphicsStatesObject[2] = OSXOffScreenSurfaceData.createNewSurface(this.texturePaintImage);
         this.fChangeFlag |= 8;
      }

   }

   void setupPaint(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      int var7;
      if (var1.paint instanceof SystemColor) {
         SystemColor var6 = (SystemColor)var1.paint;
         var7 = var6.hashCode();
         if (this.fGraphicsStatesInt.get(19) == 1 && var7 == this.lastPaintIndex) {
            this.fChangeFlag &= -9;
         } else {
            this.lastPaintIndex = var7;
            this.fGraphicsStatesInt.put(19, 1);
            this.fGraphicsStatesInt.put(21, var7);
            this.fChangeFlag |= 8;
         }
      } else if (var1.paint instanceof Color) {
         Color var12 = (Color)var1.paint;
         var7 = var12.getRGB();
         if (this.fGraphicsStatesInt.get(19) == 0 && var7 == this.lastPaintRGB) {
            this.fChangeFlag &= -9;
         } else {
            this.lastPaintRGB = var7;
            this.fGraphicsStatesInt.put(19, 0);
            this.fGraphicsStatesInt.put(20, var7);
            this.fChangeFlag |= 8;
         }
      } else if (var1.paint instanceof GradientPaint) {
         if (this.fGraphicsStatesInt.get(19) == 2 && this.lastPaint == var1.paint) {
            this.fChangeFlag &= -9;
         } else {
            GradientPaint var13 = (GradientPaint)var1.paint;
            this.fGraphicsStatesInt.put(19, 2);
            this.fGraphicsStatesInt.put(24, var13.getColor1().getRGB());
            this.fGraphicsStatesInt.put(26, var13.getColor2().getRGB());
            this.fGraphicsStatesInt.put(28, var13.isCyclic() ? 1 : 0);
            Point2D var16 = var13.getPoint1();
            this.fGraphicsStatesFloat.put(29, (float)var16.getX());
            this.fGraphicsStatesFloat.put(31, (float)var16.getY());
            var16 = var13.getPoint2();
            this.fGraphicsStatesFloat.put(33, (float)var16.getX());
            this.fGraphicsStatesFloat.put(35, (float)var16.getY());
            this.fChangeFlag |= 8;
         }
      } else {
         int[] var8;
         int var9;
         float[] var10;
         int var11;
         Point2D var20;
         if (var1.paint instanceof LinearGradientPaint) {
            LinearGradientPaint var14 = (LinearGradientPaint)var1.paint;
            if (var14.getCycleMethod() == MultipleGradientPaint.CycleMethod.NO_CYCLE) {
               if (this.fGraphicsStatesInt.get(19) == 4 && this.lastPaint == var1.paint) {
                  this.fChangeFlag &= -9;
               } else {
                  this.fGraphicsStatesInt.put(19, 4);
                  var7 = var14.getColors().length;
                  var8 = new int[var7];

                  for(var9 = 0; var9 < var7; ++var9) {
                     var8[var9] = var14.getColors()[var9].getRGB();
                  }

                  this.fGraphicsStatesObject[6] = var8;
                  var9 = var14.getFractions().length;
                  var10 = new float[var9];

                  for(var11 = 0; var11 < var9; ++var11) {
                     var10[var11] = var14.getFractions()[var11];
                  }

                  this.fGraphicsStatesObject[7] = var14.getFractions();
                  var20 = var14.getStartPoint();
                  this.fGraphicsStatesFloat.put(29, (float)var20.getX());
                  this.fGraphicsStatesFloat.put(31, (float)var20.getY());
                  var20 = var14.getEndPoint();
                  this.fGraphicsStatesFloat.put(33, (float)var20.getX());
                  this.fGraphicsStatesFloat.put(35, (float)var20.getY());
                  this.fChangeFlag |= 8;
               }
            } else {
               this.setGradientViaRasterPath(var1);
            }
         } else if (var1.paint instanceof RadialGradientPaint) {
            RadialGradientPaint var15 = (RadialGradientPaint)var1.paint;
            if (var15.getCycleMethod() == MultipleGradientPaint.CycleMethod.NO_CYCLE) {
               if (this.fGraphicsStatesInt.get(19) == 5 && this.lastPaint == var1.paint) {
                  this.fChangeFlag &= -9;
               } else {
                  this.fGraphicsStatesInt.put(19, 5);
                  var7 = var15.getColors().length;
                  var8 = new int[var7];

                  for(var9 = 0; var9 < var7; ++var9) {
                     var8[var9] = var15.getColors()[var9].getRGB();
                  }

                  this.fGraphicsStatesObject[6] = var8;
                  var9 = var15.getFractions().length;
                  var10 = new float[var9];

                  for(var11 = 0; var11 < var9; ++var11) {
                     var10[var11] = var15.getFractions()[var11];
                  }

                  this.fGraphicsStatesObject[7] = var15.getFractions();
                  var20 = var15.getFocusPoint();
                  this.fGraphicsStatesFloat.put(29, (float)var20.getX());
                  this.fGraphicsStatesFloat.put(31, (float)var20.getY());
                  var20 = var15.getCenterPoint();
                  this.fGraphicsStatesFloat.put(33, (float)var20.getX());
                  this.fGraphicsStatesFloat.put(35, (float)var20.getY());
                  this.fGraphicsStatesFloat.put(49, var15.getRadius());
                  this.fChangeFlag |= 8;
               }
            } else {
               this.setGradientViaRasterPath(var1);
            }
         } else if (var1.paint instanceof TexturePaint) {
            if (this.fGraphicsStatesInt.get(19) == 3 && this.lastPaint == var1.paint) {
               this.fChangeFlag &= -9;
            } else {
               TexturePaint var17 = (TexturePaint)var1.paint;
               this.fGraphicsStatesInt.put(19, 3);
               this.texturePaintImage = var17.getImage();
               OSXOffScreenSurfaceData var19 = OSXOffScreenSurfaceData.createNewSurface(this.texturePaintImage);
               this.fGraphicsStatesInt.put(25, this.texturePaintImage.getWidth());
               this.fGraphicsStatesInt.put(27, this.texturePaintImage.getHeight());
               Rectangle2D var18 = var17.getAnchorRect();
               this.fGraphicsStatesFloat.put(30, (float)var18.getX());
               this.fGraphicsStatesFloat.put(32, (float)var18.getY());
               this.fGraphicsStatesFloat.put(34, (float)(var18.getWidth() / (double)this.texturePaintImage.getWidth()));
               this.fGraphicsStatesFloat.put(36, (float)(var18.getHeight() / (double)this.texturePaintImage.getHeight()));
               this.fGraphicsStatesObject[2] = var19;
               this.fChangeFlag |= 8;
            }
         } else {
            this.setGradientViaRasterPath(var1);
         }
      }

      this.lastPaint = var1.paint;
   }

   void setupComposite(SunGraphics2D var1) {
      Composite var2 = var1.composite;
      if (this.lastComposite != var2) {
         this.lastComposite = var2;
         int var3 = 3;
         float var4 = 1.0F;
         if (var1.compositeState <= 1 && var2 != null) {
            AlphaComposite var5 = (AlphaComposite)var2;
            var3 = var5.getRule();
            var4 = var5.getAlpha();
         }

         if (this.lastCompositeAlphaRule == var3 && this.lastCompositeAlphaValue == var4) {
            this.fChangeFlag &= -17;
         } else {
            this.fGraphicsStatesInt.put(37, var3);
            this.fGraphicsStatesFloat.put(38, var4);
            this.lastCompositeAlphaRule = var3;
            this.lastCompositeAlphaValue = var4;
            this.fChangeFlag |= 16;
         }
      } else {
         this.fChangeFlag &= -17;
      }

   }

   void setupStroke(SunGraphics2D var1) {
      BasicStroke var2 = defaultBasicStroke;
      if (var1.stroke instanceof BasicStroke) {
         var2 = (BasicStroke)var1.stroke;
      }

      if (this.lastStroke != var2) {
         this.fGraphicsStatesObject[3] = var2.getDashArray();
         this.fGraphicsStatesFloat.put(42, var2.getDashPhase());
         this.fGraphicsStatesInt.put(40, var2.getEndCap());
         this.fGraphicsStatesInt.put(39, var2.getLineJoin());
         this.fGraphicsStatesFloat.put(41, var2.getLineWidth());
         this.fGraphicsStatesFloat.put(43, var2.getMiterLimit());
         this.fChangeFlag |= 32;
         this.lastStroke = var2;
      } else {
         this.fChangeFlag &= -33;
      }

   }

   void setupFont(Font var1, Paint var2) {
      if (var1 != null) {
         if (var1 == this.lastFont && (this.fChangeFlag & 8) == 0) {
            this.fChangeFlag &= -129;
         } else {
            this.fGraphicsStatesObject[4] = var1;
            this.fGraphicsStatesObject[5] = var2;
            this.fChangeFlag |= 128;
            this.lastFont = var1;
         }

      }
   }

   void setupRenderingHints(SunGraphics2D var1) {
      boolean var2 = false;
      int var3 = var1.antialiasHint;
      if (this.fGraphicsStatesInt.get(44) != var3) {
         this.fGraphicsStatesInt.put(44, var3);
         var2 = true;
      }

      int var4 = var1.textAntialiasHint;
      if (this.fGraphicsStatesInt.get(45) != var4) {
         this.fGraphicsStatesInt.put(45, var4);
         var2 = true;
      }

      int var5 = var1.fractionalMetricsHint;
      if (this.fGraphicsStatesInt.get(46) != var5) {
         this.fGraphicsStatesInt.put(46, var5);
         var2 = true;
      }

      int var6 = var1.renderHint;
      if (this.fGraphicsStatesInt.get(47) != var6) {
         this.fGraphicsStatesInt.put(47, var6);
         var2 = true;
      }

      Object var7 = var1.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
      int var8 = var7 != null ? ((SunHints.Value)var7).getIndex() : -1;
      if (this.fGraphicsStatesInt.get(48) != var8) {
         this.fGraphicsStatesInt.put(48, var8);
         var2 = true;
      }

      if (var2) {
         this.fChangeFlag |= 64;
      } else {
         this.fChangeFlag &= -65;
      }

   }

   void setupGraphicsState(SunGraphics2D var1, int var2) {
      this.setupGraphicsState(var1, var2, var1.font, 0, 0, this.fBounds.width, this.fBounds.height);
   }

   void setupGraphicsState(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6) {
      this.setupGraphicsState(var1, var2, var1.font, var3, var4, var5, var6);
   }

   void setupGraphicsState(SunGraphics2D var1, int var2, Font var3, int var4, int var5, int var6, int var7) {
      this.fChangeFlag = 0;
      this.setUserBounds(var1, var4, var5, var6, var7);
      Thread var8 = Thread.currentThread();
      if (this.sg2dCurrent == var1 && this.threadCurrent == var8) {
         int var9 = getRendererTypeForPrimitive(var2);
         this.setupClip(var1);
         this.setupTransform(var1);
         if (var9 != 3) {
            this.setupComposite(var1);
            this.setupRenderingHints(var1);
            if (var9 != 1) {
               this.setupPaint(var1, var4, var5, var6, var7);
               this.setupStroke(var1);
            }

            if (var9 != 0) {
               this.setupFont(var3, var1.paint);
            }
         }
      } else {
         this.sg2dCurrent = var1;
         this.threadCurrent = var8;
         this.setupClip(var1);
         this.setupTransform(var1);
         this.setupPaint(var1, var4, var5, var6, var7);
         this.setupComposite(var1);
         this.setupStroke(var1);
         this.setupFont(var3, var1.paint);
         this.setupRenderingHints(var1);
         this.fChangeFlag = -1;
      }

      this.fGraphicsStatesInt.put(0, this.fChangeFlag);
   }

   boolean isCustomPaint(SunGraphics2D var1) {
      return !(var1.paint instanceof Color) && !(var1.paint instanceof SystemColor) && !(var1.paint instanceof GradientPaint) && !(var1.paint instanceof TexturePaint);
   }

   int getPathLength(GeneralPath var1) {
      int var2 = 0;

      for(PathIterator var3 = var1.getPathIterator((AffineTransform)null); !var3.isDone(); ++var2) {
         var3.next();
      }

      return var2;
   }

   int getPathCoordinates(GeneralPath var1, FloatBuffer var2, IntBuffer var3) {
      boolean var4 = false;
      var2.clear();
      var3.clear();

      PathIterator var6;
      for(var6 = var1.getPathIterator((AffineTransform)null); !var6.isDone(); var6.next()) {
         var4 = false;
         int var5 = var6.currentSegment(this.segmentCoordinatesArray);
         switch(var5) {
         case 0:
            if (this.segmentCoordinatesArray[0] < 1.7014117E38F && this.segmentCoordinatesArray[0] > -1.7014117E38F && this.segmentCoordinatesArray[1] < 1.7014117E38F && this.segmentCoordinatesArray[1] > -1.7014117E38F) {
               var2.put(this.segmentCoordinatesArray[0]);
               var2.put(this.segmentCoordinatesArray[1]);
               break;
            }

            var4 = true;
            break;
         case 1:
            if (this.segmentCoordinatesArray[0] < 1.7014117E38F && this.segmentCoordinatesArray[0] > -1.7014117E38F && this.segmentCoordinatesArray[1] < 1.7014117E38F && this.segmentCoordinatesArray[1] > -1.7014117E38F) {
               var2.put(this.segmentCoordinatesArray[0]);
               var2.put(this.segmentCoordinatesArray[1]);
               break;
            }

            var4 = true;
            break;
         case 2:
            if (this.segmentCoordinatesArray[0] < 1.7014117E38F && this.segmentCoordinatesArray[0] > -1.7014117E38F && this.segmentCoordinatesArray[1] < 1.7014117E38F && this.segmentCoordinatesArray[1] > -1.7014117E38F && this.segmentCoordinatesArray[2] < 1.7014117E38F && this.segmentCoordinatesArray[2] > -1.7014117E38F && this.segmentCoordinatesArray[3] < 1.7014117E38F && this.segmentCoordinatesArray[3] > -1.7014117E38F) {
               var2.put(this.segmentCoordinatesArray[0]);
               var2.put(this.segmentCoordinatesArray[1]);
               var2.put(this.segmentCoordinatesArray[2]);
               var2.put(this.segmentCoordinatesArray[3]);
               break;
            }

            var4 = true;
            break;
         case 3:
            if (this.segmentCoordinatesArray[0] < 1.7014117E38F && this.segmentCoordinatesArray[0] > -1.7014117E38F && this.segmentCoordinatesArray[1] < 1.7014117E38F && this.segmentCoordinatesArray[1] > -1.7014117E38F && this.segmentCoordinatesArray[2] < 1.7014117E38F && this.segmentCoordinatesArray[2] > -1.7014117E38F && this.segmentCoordinatesArray[3] < 1.7014117E38F && this.segmentCoordinatesArray[3] > -1.7014117E38F && this.segmentCoordinatesArray[4] < 1.7014117E38F && this.segmentCoordinatesArray[4] > -1.7014117E38F && this.segmentCoordinatesArray[5] < 1.7014117E38F && this.segmentCoordinatesArray[5] > -1.7014117E38F) {
               var2.put(this.segmentCoordinatesArray[0]);
               var2.put(this.segmentCoordinatesArray[1]);
               var2.put(this.segmentCoordinatesArray[2]);
               var2.put(this.segmentCoordinatesArray[3]);
               var2.put(this.segmentCoordinatesArray[4]);
               var2.put(this.segmentCoordinatesArray[5]);
            } else {
               var4 = true;
            }
         case 4:
         }

         if (!var4) {
            var3.put(var5);
         }
      }

      return var6.getWindingRule();
   }

   public void doLine(CRenderer var1, SunGraphics2D var2, float var3, float var4, float var5, float var6) {
      this.setupGraphicsState(var2, 5, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      var1.doLine(this, var3, var4, var5, var6);
   }

   public void doRect(CRenderer var1, SunGraphics2D var2, float var3, float var4, float var5, float var6, boolean var7) {
      if (var7 && this.isCustomPaint(var2)) {
         this.setupGraphicsState(var2, 6, (int)var3, (int)var4, (int)var5, (int)var6);
      } else {
         this.setupGraphicsState(var2, 6, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      }

      var1.doRect(this, var3, var4, var5, var6, var7);
   }

   public void doRoundRect(CRenderer var1, SunGraphics2D var2, float var3, float var4, float var5, float var6, float var7, float var8, boolean var9) {
      if (var9 && this.isCustomPaint(var2)) {
         this.setupGraphicsState(var2, 7, (int)var3, (int)var4, (int)var5, (int)var6);
      } else {
         this.setupGraphicsState(var2, 7, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      }

      var1.doRoundRect(this, var3, var4, var5, var6, var7, var8, var9);
   }

   public void doOval(CRenderer var1, SunGraphics2D var2, float var3, float var4, float var5, float var6, boolean var7) {
      if (var7 && this.isCustomPaint(var2)) {
         this.setupGraphicsState(var2, 8, (int)var3, (int)var4, (int)var5, (int)var6);
      } else {
         this.setupGraphicsState(var2, 8, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      }

      var1.doOval(this, var3, var4, var5, var6, var7);
   }

   public void doArc(CRenderer var1, SunGraphics2D var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10) {
      if (var10 && this.isCustomPaint(var2)) {
         this.setupGraphicsState(var2, 9, (int)var3, (int)var4, (int)var5, (int)var6);
      } else {
         this.setupGraphicsState(var2, 9, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      }

      var1.doArc(this, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public void doPolygon(CRenderer var1, SunGraphics2D var2, int[] var3, int[] var4, int var5, boolean var6, boolean var7) {
      if (var7 && this.isCustomPaint(var2)) {
         int var8 = var3[0];
         int var9 = var4[0];
         int var10 = var8;
         int var11 = var9;

         for(int var12 = 1; var12 < var5; ++var12) {
            int var13 = var3[var12];
            if (var13 < var8) {
               var8 = var13;
            } else if (var13 > var10) {
               var10 = var13;
            }

            int var14 = var4[var12];
            if (var14 < var9) {
               var9 = var14;
            } else if (var14 > var11) {
               var11 = var14;
            }
         }

         this.setupGraphicsState(var2, 10, var8, var9, var10 - var8, var11 - var9);
      } else {
         this.setupGraphicsState(var2, 10, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      }

      var1.doPoly(this, var3, var4, var5, var6, var7);
   }

   public void drawfillShape(CRenderer var1, SunGraphics2D var2, GeneralPath var3, boolean var4, boolean var5) {
      if (var4 && this.isCustomPaint(var2)) {
         Rectangle var6 = var3.getBounds();
         this.setupGraphicsState(var2, 11, var6.x, var6.y, var6.width, var6.height);
      } else {
         this.setupGraphicsState(var2, 11, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      }

      int var8 = this.getPathLength(var3);
      if (this.shapeCoordinatesArray == null || this.shapeCoordinatesArray.capacity() < var8 * 6) {
         this.shapeCoordinatesArray = getBufferOfSize(var8 * 6).asFloatBuffer();
      }

      if (this.shapeTypesArray == null || this.shapeTypesArray.capacity() < var8) {
         this.shapeTypesArray = getBufferOfSize(var8).asIntBuffer();
      }

      int var7 = this.getPathCoordinates(var3, this.shapeCoordinatesArray, this.shapeTypesArray);
      var1.doShape(this, var8, this.shapeCoordinatesArray, this.shapeTypesArray, var7, var4, var5);
   }

   public void blitImage(CRenderer var1, SunGraphics2D var2, SurfaceData var3, boolean var4, boolean var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, Color var14) {
      OSXOffScreenSurfaceData var15 = (OSXOffScreenSurfaceData)var3;
      synchronized(var15.getLockObject()) {
         int var17 = var15.bim.getWidth();
         int var18 = var15.bim.getHeight();
         this.setupGraphicsState(var2, 1, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
         if (var14 != null) {
            var3 = var15.getCopyWithBgColor(var14);
         }

         var1.doImage(this, var3, var4, var5, var17, var18, var6, var7, var8, var9, var10, var11, var12, var13);
      }
   }

   public void drawString(CTextPipe var1, SunGraphics2D var2, long var3, String var5, double var6, double var8) {
      if (var5.length() != 0) {
         this.setupGraphicsState(var2, 13, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
         var1.doDrawString(this, var3, var5, var6, var8);
      }
   }

   public void drawGlyphs(CTextPipe var1, SunGraphics2D var2, long var3, GlyphVector var5, float var6, float var7) {
      this.setupGraphicsState(var2, 14, var5.getFont(), 0, 0, this.fBounds.width, this.fBounds.height);
      var1.doDrawGlyphs(this, var3, var5, var6, var7);
   }

   public void drawUnicodes(CTextPipe var1, SunGraphics2D var2, long var3, char[] var5, int var6, int var7, float var8, float var9) {
      this.setupGraphicsState(var2, 15, var2.font, 0, 0, this.fBounds.width, this.fBounds.height);
      if (var7 == 1) {
         var1.doOneUnicode(this, var3, var5[var6], var8, var9);
      } else {
         var1.doUnicodes(this, var3, var5, var6, var7, var8, var9);
      }

   }

   void intersection(Rectangle var1, Rectangle var2, Rectangle var3) {
      int var4 = var1.x;
      int var5 = var1.y;
      long var6 = (long)(var4 + var1.width);
      long var8 = (long)(var5 + var1.height);
      int var10 = var2.x;
      int var11 = var2.y;
      long var12 = (long)(var10 + var2.width);
      long var14 = (long)(var11 + var2.height);
      if (var4 < var10) {
         var4 = var10;
      }

      if (var5 < var11) {
         var5 = var11;
      }

      if (var6 > var12) {
         var6 = var12;
      }

      if (var8 > var14) {
         var8 = var14;
      }

      var6 -= (long)var4;
      var8 -= (long)var5;
      if (var6 < -2147483648L) {
         var6 = -2147483648L;
      }

      if (var8 < -2147483648L) {
         var8 = -2147483648L;
      }

      var3.setBounds(var4, var5, (int)var6, (int)var8);
   }

   protected Rectangle clipCopyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.copyAreaBounds.setBounds(var1.devClip.getLoX(), var1.devClip.getLoY(), var1.devClip.getWidth(), var1.devClip.getHeight());
      var2 += var1.transX;
      var3 += var1.transY;
      this.srcCopyAreaRect.setBounds(var2, var3, var4, var5);
      this.intersection(this.srcCopyAreaRect, this.copyAreaBounds, this.srcCopyAreaRect);
      if (this.srcCopyAreaRect.width > 0 && this.srcCopyAreaRect.height > 0) {
         this.dstCopyAreaRect.setBounds(this.srcCopyAreaRect.x + var6, this.srcCopyAreaRect.y + var7, this.srcCopyAreaRect.width, this.srcCopyAreaRect.height);
         this.intersection(this.dstCopyAreaRect, this.copyAreaBounds, this.dstCopyAreaRect);
         if (this.dstCopyAreaRect.width > 0 && this.dstCopyAreaRect.height > 0) {
            var2 = this.dstCopyAreaRect.x - var6;
            var3 = this.dstCopyAreaRect.y - var7;
            var4 = this.dstCopyAreaRect.width;
            var5 = this.dstCopyAreaRect.height;
            this.finalCopyAreaRect.setBounds(var2, var3, var4, var5);
            return this.finalCopyAreaRect;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   protected void markDirty(boolean var1) {
   }

   public boolean canRenderLCDText(SunGraphics2D var1) {
      return var1.compositeState <= 0 && var1.paintState <= 1 && var1.clipState <= 1 && var1.antialiasHint != 2;
   }

   public static boolean IsSimpleColor(Object var0) {
      return var0 instanceof Color || var0 instanceof SystemColor || var0 instanceof ColorUIResource;
   }

   static {
      sQuartzPipe = new CRenderer();
      sIdentityMatrix = new AffineTransform();
      defaultBasicStroke = new BasicStroke();
   }

   public interface CGContextDrawable {
      void drawIntoCGContext(long var1);
   }
}
