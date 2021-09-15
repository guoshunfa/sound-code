package sun.font;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.text.CharacterIterator;
import sun.java2d.loops.FontInfo;

public class StandardGlyphVector extends GlyphVector {
   private Font font;
   private FontRenderContext frc;
   private int[] glyphs;
   private int[] userGlyphs;
   private float[] positions;
   private int[] charIndices;
   private int flags;
   private static final int UNINITIALIZED_FLAGS = -1;
   private StandardGlyphVector.GlyphTransformInfo gti;
   private AffineTransform ftx;
   private AffineTransform dtx;
   private AffineTransform invdtx;
   private AffineTransform frctx;
   private Font2D font2D;
   private SoftReference fsref;
   private SoftReference lbcacheRef;
   private SoftReference vbcacheRef;
   public static final int FLAG_USES_VERTICAL_BASELINE = 128;
   public static final int FLAG_USES_VERTICAL_METRICS = 256;
   public static final int FLAG_USES_ALTERNATE_ORIENTATION = 512;

   public StandardGlyphVector(Font var1, String var2, FontRenderContext var3) {
      this.init(var1, var2.toCharArray(), 0, var2.length(), var3, -1);
   }

   public StandardGlyphVector(Font var1, char[] var2, FontRenderContext var3) {
      this.init(var1, var2, 0, var2.length, var3, -1);
   }

   public StandardGlyphVector(Font var1, char[] var2, int var3, int var4, FontRenderContext var5) {
      this.init(var1, var2, var3, var4, var5, -1);
   }

   private float getTracking(Font var1) {
      if (var1.hasLayoutAttributes()) {
         AttributeValues var2 = ((AttributeMap)var1.getAttributes()).getValues();
         return var2.getTracking();
      } else {
         return 0.0F;
      }
   }

   public StandardGlyphVector(Font var1, FontRenderContext var2, int[] var3, float[] var4, int[] var5, int var6) {
      this.initGlyphVector(var1, var2, var3, var4, var5, var6);
      float var7 = this.getTracking(var1);
      if (var7 != 0.0F) {
         var7 *= var1.getSize2D();
         Point2D.Float var8 = new Point2D.Float(var7, 0.0F);
         if (var1.isTransformed()) {
            AffineTransform var9 = var1.getTransform();
            var9.deltaTransform(var8, var8);
         }

         Font2D var17 = FontUtilities.getFont2D(var1);
         FontStrike var10 = var17.getStrike(var1, var2);
         float[] var11 = new float[]{var8.x, var8.y};

         for(int var12 = 0; var12 < var11.length; ++var12) {
            float var13 = var11[var12];
            if (var13 != 0.0F) {
               float var14 = 0.0F;
               int var15 = var12;

               for(int var16 = 0; var16 < var3.length; var15 += 2) {
                  if (var10.getGlyphAdvance(var3[var16++]) != 0.0F) {
                     var4[var15] += var14;
                     var14 += var13;
                  }
               }

               var4[var4.length - 2 + var12] += var14;
            }
         }
      }

   }

   public void initGlyphVector(Font var1, FontRenderContext var2, int[] var3, float[] var4, int[] var5, int var6) {
      this.font = var1;
      this.frc = var2;
      this.glyphs = var3;
      this.userGlyphs = var3;
      this.positions = var4;
      this.charIndices = var5;
      this.flags = var6;
      this.initFontData();
   }

   public StandardGlyphVector(Font var1, CharacterIterator var2, FontRenderContext var3) {
      int var4 = var2.getBeginIndex();
      char[] var5 = new char[var2.getEndIndex() - var4];

      for(char var6 = var2.first(); var6 != '\uffff'; var6 = var2.next()) {
         var5[var2.getIndex() - var4] = var6;
      }

      this.init(var1, var5, 0, var5.length, var3, -1);
   }

   public StandardGlyphVector(Font var1, int[] var2, FontRenderContext var3) {
      this.font = var1;
      this.frc = var3;
      this.flags = -1;
      this.initFontData();
      this.userGlyphs = var2;
      this.glyphs = this.getValidatedGlyphs(this.userGlyphs);
   }

   public static StandardGlyphVector getStandardGV(GlyphVector var0, FontInfo var1) {
      if (var1.aaHint == 2) {
         Object var2 = var0.getFontRenderContext().getAntiAliasingHint();
         if (var2 != RenderingHints.VALUE_TEXT_ANTIALIAS_ON && var2 != RenderingHints.VALUE_TEXT_ANTIALIAS_GASP) {
            FontRenderContext var3 = var0.getFontRenderContext();
            var3 = new FontRenderContext(var3.getTransform(), RenderingHints.VALUE_TEXT_ANTIALIAS_ON, var3.getFractionalMetricsHint());
            return new StandardGlyphVector(var0, var3);
         }
      }

      return var0 instanceof StandardGlyphVector ? (StandardGlyphVector)var0 : new StandardGlyphVector(var0, var0.getFontRenderContext());
   }

   public Font getFont() {
      return this.font;
   }

   public FontRenderContext getFontRenderContext() {
      return this.frc;
   }

   public void performDefaultLayout() {
      this.positions = null;
      if (this.getTracking(this.font) == 0.0F) {
         this.clearFlags(2);
      }

   }

   public int getNumGlyphs() {
      return this.glyphs.length;
   }

   public int getGlyphCode(int var1) {
      return this.userGlyphs[var1];
   }

   public int[] getGlyphCodes(int var1, int var2, int[] var3) {
      if (var2 < 0) {
         throw new IllegalArgumentException("count = " + var2);
      } else if (var1 < 0) {
         throw new IndexOutOfBoundsException("start = " + var1);
      } else if (var1 > this.glyphs.length - var2) {
         throw new IndexOutOfBoundsException("start + count = " + (var1 + var2));
      } else {
         if (var3 == null) {
            var3 = new int[var2];
         }

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = this.userGlyphs[var4 + var1];
         }

         return var3;
      }
   }

   public int getGlyphCharIndex(int var1) {
      if (var1 < 0 && var1 >= this.glyphs.length) {
         throw new IndexOutOfBoundsException("" + var1);
      } else if (this.charIndices == null) {
         return (this.getLayoutFlags() & 4) != 0 ? this.glyphs.length - 1 - var1 : var1;
      } else {
         return this.charIndices[var1];
      }
   }

   public int[] getGlyphCharIndices(int var1, int var2, int[] var3) {
      if (var1 >= 0 && var2 >= 0 && var2 <= this.glyphs.length - var1) {
         if (var3 == null) {
            var3 = new int[var2];
         }

         int var4;
         if (this.charIndices == null) {
            int var5;
            if ((this.getLayoutFlags() & 4) != 0) {
               var4 = 0;

               for(var5 = this.glyphs.length - 1 - var1; var4 < var2; --var5) {
                  var3[var4] = var5;
                  ++var4;
               }
            } else {
               var4 = 0;

               for(var5 = var1; var4 < var2; ++var5) {
                  var3[var4] = var5;
                  ++var4;
               }
            }
         } else {
            for(var4 = 0; var4 < var2; ++var4) {
               var3[var4] = this.charIndices[var4 + var1];
            }
         }

         return var3;
      } else {
         throw new IndexOutOfBoundsException("" + var1 + ", " + var2);
      }
   }

   public Rectangle2D getLogicalBounds() {
      this.setFRCTX();
      this.initPositions();
      LineMetrics var1 = this.font.getLineMetrics("", this.frc);
      float var2 = 0.0F;
      float var3 = -var1.getAscent();
      float var4 = 0.0F;
      float var5 = var1.getDescent() + var1.getLeading();
      if (this.glyphs.length > 0) {
         var4 = this.positions[this.positions.length - 2];
      }

      return new Rectangle2D.Float(var2, var3, var4 - var2, var5 - var3);
   }

   public Rectangle2D getVisualBounds() {
      Object var1 = null;

      for(int var2 = 0; var2 < this.glyphs.length; ++var2) {
         Rectangle2D var3 = this.getGlyphVisualBounds(var2).getBounds2D();
         if (!var3.isEmpty()) {
            if (var1 == null) {
               var1 = var3;
            } else {
               Rectangle2D.union((Rectangle2D)var1, var3, (Rectangle2D)var1);
            }
         }
      }

      if (var1 == null) {
         var1 = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
      }

      return (Rectangle2D)var1;
   }

   public Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3) {
      return this.getGlyphsPixelBounds(var1, var2, var3, 0, this.glyphs.length);
   }

   public Shape getOutline() {
      return this.getGlyphsOutline(0, this.glyphs.length, 0.0F, 0.0F);
   }

   public Shape getOutline(float var1, float var2) {
      return this.getGlyphsOutline(0, this.glyphs.length, var1, var2);
   }

   public Shape getGlyphOutline(int var1) {
      return this.getGlyphsOutline(var1, 1, 0.0F, 0.0F);
   }

   public Shape getGlyphOutline(int var1, float var2, float var3) {
      return this.getGlyphsOutline(var1, 1, var2, var3);
   }

   public Point2D getGlyphPosition(int var1) {
      this.initPositions();
      var1 *= 2;
      return new Point2D.Float(this.positions[var1], this.positions[var1 + 1]);
   }

   public void setGlyphPosition(int var1, Point2D var2) {
      this.initPositions();
      int var3 = var1 << 1;
      this.positions[var3] = (float)var2.getX();
      this.positions[var3 + 1] = (float)var2.getY();
      this.clearCaches(var1);
      this.addFlags(2);
   }

   public AffineTransform getGlyphTransform(int var1) {
      if (var1 >= 0 && var1 < this.glyphs.length) {
         return this.gti != null ? this.gti.getGlyphTransform(var1) : null;
      } else {
         throw new IndexOutOfBoundsException("ix = " + var1);
      }
   }

   public void setGlyphTransform(int var1, AffineTransform var2) {
      if (var1 >= 0 && var1 < this.glyphs.length) {
         if (this.gti == null) {
            if (var2 == null || var2.isIdentity()) {
               return;
            }

            this.gti = new StandardGlyphVector.GlyphTransformInfo(this);
         }

         this.gti.setGlyphTransform(var1, var2);
         if (this.gti.transformCount() == 0) {
            this.gti = null;
         }

      } else {
         throw new IndexOutOfBoundsException("ix = " + var1);
      }
   }

   public int getLayoutFlags() {
      if (this.flags == -1) {
         this.flags = 0;
         if (this.charIndices != null && this.glyphs.length > 1) {
            boolean var1 = true;
            boolean var2 = true;
            int var3 = this.charIndices.length;

            for(int var4 = 0; var4 < this.charIndices.length && (var1 || var2); ++var4) {
               boolean var10000;
               label43: {
                  int var5 = this.charIndices[var4];
                  var1 = var1 && var5 == var4;
                  if (var2) {
                     --var3;
                     if (var5 == var3) {
                        var10000 = true;
                        break label43;
                     }
                  }

                  var10000 = false;
               }

               var2 = var10000;
            }

            if (var2) {
               this.flags |= 4;
            }

            if (!var2 && !var1) {
               this.flags |= 8;
            }
         }
      }

      return this.flags;
   }

   public float[] getGlyphPositions(int var1, int var2, float[] var3) {
      if (var2 < 0) {
         throw new IllegalArgumentException("count = " + var2);
      } else if (var1 < 0) {
         throw new IndexOutOfBoundsException("start = " + var1);
      } else if (var1 > this.glyphs.length + 1 - var2) {
         throw new IndexOutOfBoundsException("start + count = " + (var1 + var2));
      } else {
         return this.internalGetGlyphPositions(var1, var2, 0, var3);
      }
   }

   public Shape getGlyphLogicalBounds(int var1) {
      if (var1 >= 0 && var1 < this.glyphs.length) {
         Shape[] var2;
         if (this.lbcacheRef == null || (var2 = (Shape[])((Shape[])this.lbcacheRef.get())) == null) {
            var2 = new Shape[this.glyphs.length];
            this.lbcacheRef = new SoftReference(var2);
         }

         Object var3 = var2[var1];
         if (var3 == null) {
            this.setFRCTX();
            this.initPositions();
            StandardGlyphVector.ADL var4 = new StandardGlyphVector.ADL();
            StandardGlyphVector.GlyphStrike var5 = this.getGlyphStrike(var1);
            var5.getADL(var4);
            Point2D.Float var6 = var5.strike.getGlyphMetrics(this.glyphs[var1]);
            float var7 = var6.x;
            float var8 = var6.y;
            float var9 = var4.descentX + var4.leadingX + var4.ascentX;
            float var10 = var4.descentY + var4.leadingY + var4.ascentY;
            float var11 = this.positions[var1 * 2] + var5.dx - var4.ascentX;
            float var12 = this.positions[var1 * 2 + 1] + var5.dy - var4.ascentY;
            GeneralPath var13 = new GeneralPath();
            var13.moveTo(var11, var12);
            var13.lineTo(var11 + var7, var12 + var8);
            var13.lineTo(var11 + var7 + var9, var12 + var8 + var10);
            var13.lineTo(var11 + var9, var12 + var10);
            var13.closePath();
            var3 = new DelegatingShape(var13);
            var2[var1] = (Shape)var3;
         }

         return (Shape)var3;
      } else {
         throw new IndexOutOfBoundsException("ix = " + var1);
      }
   }

   public Shape getGlyphVisualBounds(int var1) {
      if (var1 >= 0 && var1 < this.glyphs.length) {
         Shape[] var2;
         if (this.vbcacheRef == null || (var2 = (Shape[])((Shape[])this.vbcacheRef.get())) == null) {
            var2 = new Shape[this.glyphs.length];
            this.vbcacheRef = new SoftReference(var2);
         }

         Object var3 = var2[var1];
         if (var3 == null) {
            var3 = new DelegatingShape(this.getGlyphOutlineBounds(var1));
            var2[var1] = (Shape)var3;
         }

         return (Shape)var3;
      } else {
         throw new IndexOutOfBoundsException("ix = " + var1);
      }
   }

   public Rectangle getGlyphPixelBounds(int var1, FontRenderContext var2, float var3, float var4) {
      return this.getGlyphsPixelBounds(var2, var3, var4, var1, 1);
   }

   public GlyphMetrics getGlyphMetrics(int var1) {
      if (var1 >= 0 && var1 < this.glyphs.length) {
         Rectangle2D var2 = this.getGlyphVisualBounds(var1).getBounds2D();
         Point2D var3 = this.getGlyphPosition(var1);
         var2.setRect(var2.getMinX() - var3.getX(), var2.getMinY() - var3.getY(), var2.getWidth(), var2.getHeight());
         Point2D.Float var4 = this.getGlyphStrike(var1).strike.getGlyphMetrics(this.glyphs[var1]);
         GlyphMetrics var5 = new GlyphMetrics(true, var4.x, var4.y, var2, (byte)0);
         return var5;
      } else {
         throw new IndexOutOfBoundsException("ix = " + var1);
      }
   }

   public GlyphJustificationInfo getGlyphJustificationInfo(int var1) {
      if (var1 >= 0 && var1 < this.glyphs.length) {
         return null;
      } else {
         throw new IndexOutOfBoundsException("ix = " + var1);
      }
   }

   public boolean equals(GlyphVector var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else {
         try {
            StandardGlyphVector var2 = (StandardGlyphVector)var1;
            if (this.glyphs.length != var2.glyphs.length) {
               return false;
            } else {
               int var3;
               for(var3 = 0; var3 < this.glyphs.length; ++var3) {
                  if (this.glyphs[var3] != var2.glyphs[var3]) {
                     return false;
                  }
               }

               if (!this.font.equals(var2.font)) {
                  return false;
               } else if (!this.frc.equals(var2.frc)) {
                  return false;
               } else {
                  if (var2.positions == null != (this.positions == null)) {
                     if (this.positions == null) {
                        this.initPositions();
                     } else {
                        var2.initPositions();
                     }
                  }

                  if (this.positions != null) {
                     for(var3 = 0; var3 < this.positions.length; ++var3) {
                        if (this.positions[var3] != var2.positions[var3]) {
                           return false;
                        }
                     }
                  }

                  if (this.gti == null) {
                     return var2.gti == null;
                  } else {
                     return this.gti.equals(var2.gti);
                  }
               }
            }
         } catch (ClassCastException var4) {
            return false;
         }
      }
   }

   public int hashCode() {
      return this.font.hashCode() ^ this.glyphs.length;
   }

   public boolean equals(Object var1) {
      try {
         return this.equals((GlyphVector)var1);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public StandardGlyphVector copy() {
      return (StandardGlyphVector)this.clone();
   }

   public Object clone() {
      try {
         StandardGlyphVector var1 = (StandardGlyphVector)super.clone();
         var1.clearCaches();
         if (this.positions != null) {
            var1.positions = (float[])((float[])this.positions.clone());
         }

         if (this.gti != null) {
            var1.gti = new StandardGlyphVector.GlyphTransformInfo(var1, this.gti);
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         return this;
      }
   }

   public void setGlyphPositions(float[] var1, int var2, int var3, int var4) {
      if (var4 < 0) {
         throw new IllegalArgumentException("count = " + var4);
      } else {
         this.initPositions();
         int var5 = var3 * 2;
         int var6 = var5 + var4 * 2;

         for(int var7 = var2; var5 < var6; ++var7) {
            this.positions[var5] = var1[var7];
            ++var5;
         }

         this.clearCaches();
         this.addFlags(2);
      }
   }

   public void setGlyphPositions(float[] var1) {
      int var2 = this.glyphs.length * 2 + 2;
      if (var1.length != var2) {
         throw new IllegalArgumentException("srcPositions.length != " + var2);
      } else {
         this.positions = (float[])((float[])var1.clone());
         this.clearCaches();
         this.addFlags(2);
      }
   }

   public float[] getGlyphPositions(float[] var1) {
      return this.internalGetGlyphPositions(0, this.glyphs.length + 1, 0, var1);
   }

   public AffineTransform[] getGlyphTransforms(int var1, int var2, AffineTransform[] var3) {
      if (var1 >= 0 && var2 >= 0 && var1 + var2 <= this.glyphs.length) {
         if (this.gti == null) {
            return null;
         } else {
            if (var3 == null) {
               var3 = new AffineTransform[var2];
            }

            for(int var4 = 0; var4 < var2; ++var1) {
               var3[var4] = this.gti.getGlyphTransform(var1);
               ++var4;
            }

            return var3;
         }
      } else {
         throw new IllegalArgumentException("start: " + var1 + " count: " + var2);
      }
   }

   public AffineTransform[] getGlyphTransforms() {
      return this.getGlyphTransforms(0, this.glyphs.length, (AffineTransform[])null);
   }

   public void setGlyphTransforms(AffineTransform[] var1, int var2, int var3, int var4) {
      int var5 = var3;

      for(int var6 = var3 + var4; var5 < var6; ++var5) {
         this.setGlyphTransform(var5, var1[var2 + var5]);
      }

   }

   public void setGlyphTransforms(AffineTransform[] var1) {
      this.setGlyphTransforms(var1, 0, 0, this.glyphs.length);
   }

   public float[] getGlyphInfo() {
      this.setFRCTX();
      this.initPositions();
      float[] var1 = new float[this.glyphs.length * 8];
      int var2 = 0;

      for(int var3 = 0; var2 < this.glyphs.length; var3 += 8) {
         float var4 = this.positions[var2 * 2];
         float var5 = this.positions[var2 * 2 + 1];
         var1[var3] = var4;
         var1[var3 + 1] = var5;
         int var6 = this.glyphs[var2];
         StandardGlyphVector.GlyphStrike var7 = this.getGlyphStrike(var2);
         Point2D.Float var8 = var7.strike.getGlyphMetrics(var6);
         var1[var3 + 2] = var8.x;
         var1[var3 + 3] = var8.y;
         Rectangle2D var9 = this.getGlyphVisualBounds(var2).getBounds2D();
         var1[var3 + 4] = (float)var9.getMinX();
         var1[var3 + 5] = (float)var9.getMinY();
         var1[var3 + 6] = (float)var9.getWidth();
         var1[var3 + 7] = (float)var9.getHeight();
         ++var2;
      }

      return var1;
   }

   public void pixellate(FontRenderContext var1, Point2D var2, Point var3) {
      if (var1 == null) {
         var1 = this.frc;
      }

      AffineTransform var4 = var1.getTransform();
      var4.transform(var2, var2);
      var3.x = (int)var2.getX();
      var3.y = (int)var2.getY();
      var2.setLocation((double)var3.x, (double)var3.y);

      try {
         var4.inverseTransform(var2, var2);
      } catch (NoninvertibleTransformException var6) {
         throw new IllegalArgumentException("must be able to invert frc transform");
      }
   }

   boolean needsPositions(double[] var1) {
      return this.gti != null || (this.getLayoutFlags() & 2) != 0 || !matchTX(var1, this.frctx);
   }

   Object setupGlyphImages(long[] var1, float[] var2, double[] var3) {
      this.initPositions();
      this.setRenderTransform(var3);
      if (this.gti != null) {
         return this.gti.setupGlyphImages(var1, var2, this.dtx);
      } else {
         StandardGlyphVector.GlyphStrike var4 = this.getDefaultStrike();
         var4.strike.getGlyphImagePtrs(this.glyphs, var1, this.glyphs.length);
         if (var2 != null) {
            if (this.dtx.isIdentity()) {
               System.arraycopy(this.positions, 0, var2, 0, this.glyphs.length * 2);
            } else {
               this.dtx.transform((float[])this.positions, 0, (float[])var2, 0, this.glyphs.length);
            }
         }

         return var4;
      }
   }

   private static boolean matchTX(double[] var0, AffineTransform var1) {
      return var0[0] == var1.getScaleX() && var0[1] == var1.getShearY() && var0[2] == var1.getShearX() && var0[3] == var1.getScaleY();
   }

   private static AffineTransform getNonTranslateTX(AffineTransform var0) {
      if (var0.getTranslateX() != 0.0D || var0.getTranslateY() != 0.0D) {
         var0 = new AffineTransform(var0.getScaleX(), var0.getShearY(), var0.getShearX(), var0.getScaleY(), 0.0D, 0.0D);
      }

      return var0;
   }

   private static boolean equalNonTranslateTX(AffineTransform var0, AffineTransform var1) {
      return var0.getScaleX() == var1.getScaleX() && var0.getShearY() == var1.getShearY() && var0.getShearX() == var1.getShearX() && var0.getScaleY() == var1.getScaleY();
   }

   private void setRenderTransform(double[] var1) {
      assert var1.length == 4;

      if (!matchTX(var1, this.dtx)) {
         this.resetDTX(new AffineTransform(var1));
      }

   }

   private final void setDTX(AffineTransform var1) {
      if (!equalNonTranslateTX(this.dtx, var1)) {
         this.resetDTX(getNonTranslateTX(var1));
      }

   }

   private final void setFRCTX() {
      if (!equalNonTranslateTX(this.frctx, this.dtx)) {
         this.resetDTX(getNonTranslateTX(this.frctx));
      }

   }

   private final void resetDTX(AffineTransform var1) {
      this.fsref = null;
      this.dtx = var1;
      this.invdtx = null;
      if (!this.dtx.isIdentity()) {
         try {
            this.invdtx = this.dtx.createInverse();
         } catch (NoninvertibleTransformException var3) {
         }
      }

      if (this.gti != null) {
         this.gti.strikesRef = null;
      }

   }

   private StandardGlyphVector(GlyphVector var1, FontRenderContext var2) {
      this.font = var1.getFont();
      this.frc = var2;
      this.initFontData();
      int var3 = var1.getNumGlyphs();
      this.userGlyphs = var1.getGlyphCodes(0, var3, (int[])null);
      if (var1 instanceof StandardGlyphVector) {
         this.glyphs = this.userGlyphs;
      } else {
         this.glyphs = this.getValidatedGlyphs(this.userGlyphs);
      }

      this.flags = var1.getLayoutFlags() & 15;
      if ((this.flags & 2) != 0) {
         this.positions = var1.getGlyphPositions(0, var3 + 1, (float[])null);
      }

      if ((this.flags & 8) != 0) {
         this.charIndices = var1.getGlyphCharIndices(0, var3, (int[])null);
      }

      if ((this.flags & 1) != 0) {
         AffineTransform[] var4 = new AffineTransform[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = var1.getGlyphTransform(var5);
         }

         this.setGlyphTransforms(var4);
      }

   }

   int[] getValidatedGlyphs(int[] var1) {
      int var2 = var1.length;
      int[] var3 = new int[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         if (var1[var4] != 65534 && var1[var4] != 65535) {
            var3[var4] = this.font2D.getValidatedGlyphCode(var1[var4]);
         } else {
            var3[var4] = var1[var4];
         }
      }

      return var3;
   }

   private void init(Font var1, char[] var2, int var3, int var4, FontRenderContext var5, int var6) {
      if (var3 >= 0 && var4 >= 0 && var3 + var4 <= var2.length) {
         this.font = var1;
         this.frc = var5;
         this.flags = var6;
         if (this.getTracking(var1) != 0.0F) {
            this.addFlags(2);
         }

         if (var3 != 0) {
            char[] var7 = new char[var4];
            System.arraycopy(var2, var3, var7, 0, var4);
            var2 = var7;
         }

         this.initFontData();
         this.glyphs = new int[var4];
         this.userGlyphs = this.glyphs;
         this.font2D.getMapper().charsToGlyphs(var4, var2, this.glyphs);
      } else {
         throw new ArrayIndexOutOfBoundsException("start or count out of bounds");
      }
   }

   private void initFontData() {
      this.font2D = FontUtilities.getFont2D(this.font);
      if (this.font2D instanceof FontSubstitution) {
         this.font2D = ((FontSubstitution)this.font2D).getCompositeFont2D();
      }

      float var1 = this.font.getSize2D();
      if (this.font.isTransformed()) {
         this.ftx = this.font.getTransform();
         if (this.ftx.getTranslateX() != 0.0D || this.ftx.getTranslateY() != 0.0D) {
            this.addFlags(2);
         }

         this.ftx.setTransform(this.ftx.getScaleX(), this.ftx.getShearY(), this.ftx.getShearX(), this.ftx.getScaleY(), 0.0D, 0.0D);
         this.ftx.scale((double)var1, (double)var1);
      } else {
         this.ftx = AffineTransform.getScaleInstance((double)var1, (double)var1);
      }

      this.frctx = this.frc.getTransform();
      this.resetDTX(getNonTranslateTX(this.frctx));
   }

   private float[] internalGetGlyphPositions(int var1, int var2, int var3, float[] var4) {
      if (var4 == null) {
         var4 = new float[var3 + var2 * 2];
      }

      this.initPositions();
      int var5 = var3;
      int var6 = var3 + var2 * 2;

      for(int var7 = var1 * 2; var5 < var6; ++var7) {
         var4[var5] = this.positions[var7];
         ++var5;
      }

      return var4;
   }

   private Rectangle2D getGlyphOutlineBounds(int var1) {
      this.setFRCTX();
      this.initPositions();
      return this.getGlyphStrike(var1).getGlyphOutlineBounds(this.glyphs[var1], this.positions[var1 * 2], this.positions[var1 * 2 + 1]);
   }

   private Shape getGlyphsOutline(int var1, int var2, float var3, float var4) {
      this.setFRCTX();
      this.initPositions();
      GeneralPath var5 = new GeneralPath(1);
      int var6 = var1;
      int var7 = var1 + var2;

      for(int var8 = var1 * 2; var6 < var7; var8 += 2) {
         float var9 = var3 + this.positions[var8];
         float var10 = var4 + this.positions[var8 + 1];
         this.getGlyphStrike(var6).appendGlyphOutline(this.glyphs[var6], var5, var9, var10);
         ++var6;
      }

      return var5;
   }

   private Rectangle getGlyphsPixelBounds(FontRenderContext var1, float var2, float var3, int var4, int var5) {
      this.initPositions();
      AffineTransform var6 = null;
      if (var1 != null && !var1.equals(this.frc)) {
         var6 = var1.getTransform();
      } else {
         var6 = this.frctx;
      }

      this.setDTX(var6);
      if (this.gti != null) {
         return this.gti.getGlyphsPixelBounds(var6, var2, var3, var4, var5);
      } else {
         FontStrike var7 = this.getDefaultStrike().strike;
         Rectangle var8 = null;
         Rectangle var9 = new Rectangle();
         Point2D.Float var10 = new Point2D.Float();
         int var11 = var4 * 2;

         while(true) {
            --var5;
            if (var5 < 0) {
               return var8 != null ? var8 : var9;
            }

            var10.x = var2 + this.positions[var11++];
            var10.y = var3 + this.positions[var11++];
            var6.transform(var10, var10);
            var7.getGlyphImageBounds(this.glyphs[var4++], var10, var9);
            if (!var9.isEmpty()) {
               if (var8 == null) {
                  var8 = new Rectangle(var9);
               } else {
                  var8.add(var9);
               }
            }
         }
      }
   }

   private void clearCaches(int var1) {
      Shape[] var2;
      if (this.lbcacheRef != null) {
         var2 = (Shape[])((Shape[])this.lbcacheRef.get());
         if (var2 != null) {
            var2[var1] = null;
         }
      }

      if (this.vbcacheRef != null) {
         var2 = (Shape[])((Shape[])this.vbcacheRef.get());
         if (var2 != null) {
            var2[var1] = null;
         }
      }

   }

   private void clearCaches() {
      this.lbcacheRef = null;
      this.vbcacheRef = null;
   }

   private void initPositions() {
      if (this.positions == null) {
         this.setFRCTX();
         this.positions = new float[this.glyphs.length * 2 + 2];
         Point2D.Float var1 = null;
         float var2 = this.getTracking(this.font);
         if (var2 != 0.0F) {
            var2 *= this.font.getSize2D();
            var1 = new Point2D.Float(var2, 0.0F);
         }

         Point2D.Float var3 = new Point2D.Float(0.0F, 0.0F);
         if (this.font.isTransformed()) {
            AffineTransform var4 = this.font.getTransform();
            var4.transform(var3, var3);
            this.positions[0] = var3.x;
            this.positions[1] = var3.y;
            if (var1 != null) {
               var4.deltaTransform(var1, var1);
            }
         }

         int var6 = 0;

         for(int var5 = 2; var6 < this.glyphs.length; var5 += 2) {
            this.getGlyphStrike(var6).addDefaultGlyphAdvance(this.glyphs[var6], var3);
            if (var1 != null) {
               var3.x += var1.x;
               var3.y += var1.y;
            }

            this.positions[var5] = var3.x;
            this.positions[var5 + 1] = var3.y;
            ++var6;
         }
      }

   }

   private void addFlags(int var1) {
      this.flags = this.getLayoutFlags() | var1;
   }

   private void clearFlags(int var1) {
      this.flags = this.getLayoutFlags() & ~var1;
   }

   private StandardGlyphVector.GlyphStrike getGlyphStrike(int var1) {
      return this.gti == null ? this.getDefaultStrike() : this.gti.getStrike(var1);
   }

   private StandardGlyphVector.GlyphStrike getDefaultStrike() {
      StandardGlyphVector.GlyphStrike var1 = null;
      if (this.fsref != null) {
         var1 = (StandardGlyphVector.GlyphStrike)this.fsref.get();
      }

      if (var1 == null) {
         var1 = StandardGlyphVector.GlyphStrike.create(this, this.dtx, (AffineTransform)null);
         this.fsref = new SoftReference(var1);
      }

      return var1;
   }

   public String toString() {
      return this.appendString((StringBuffer)null).toString();
   }

   StringBuffer appendString(StringBuffer var1) {
      if (var1 == null) {
         var1 = new StringBuffer();
      }

      try {
         var1.append("SGV{font: ");
         var1.append(this.font.toString());
         var1.append(", frc: ");
         var1.append(this.frc.toString());
         var1.append(", glyphs: (");
         var1.append(this.glyphs.length);
         var1.append(")[");

         int var2;
         for(var2 = 0; var2 < this.glyphs.length; ++var2) {
            if (var2 > 0) {
               var1.append(", ");
            }

            var1.append(Integer.toHexString(this.glyphs[var2]));
         }

         var1.append("]");
         if (this.positions != null) {
            var1.append(", positions: (");
            var1.append(this.positions.length);
            var1.append(")[");

            for(var2 = 0; var2 < this.positions.length; var2 += 2) {
               if (var2 > 0) {
                  var1.append(", ");
               }

               var1.append(this.positions[var2]);
               var1.append("@");
               var1.append(this.positions[var2 + 1]);
            }

            var1.append("]");
         }

         if (this.charIndices != null) {
            var1.append(", indices: (");
            var1.append(this.charIndices.length);
            var1.append(")[");

            for(var2 = 0; var2 < this.charIndices.length; ++var2) {
               if (var2 > 0) {
                  var1.append(", ");
               }

               var1.append(this.charIndices[var2]);
            }

            var1.append("]");
         }

         var1.append(", flags:");
         if (this.getLayoutFlags() == 0) {
            var1.append(" default");
         } else {
            if ((this.flags & 1) != 0) {
               var1.append(" tx");
            }

            if ((this.flags & 2) != 0) {
               var1.append(" pos");
            }

            if ((this.flags & 4) != 0) {
               var1.append(" rtl");
            }

            if ((this.flags & 8) != 0) {
               var1.append(" complex");
            }
         }
      } catch (Exception var3) {
         var1.append(" " + var3.getMessage());
      }

      var1.append("}");
      return var1;
   }

   static class ADL {
      public float ascentX;
      public float ascentY;
      public float descentX;
      public float descentY;
      public float leadingX;
      public float leadingY;

      public String toString() {
         return this.toStringBuffer((StringBuffer)null).toString();
      }

      protected StringBuffer toStringBuffer(StringBuffer var1) {
         if (var1 == null) {
            var1 = new StringBuffer();
         }

         var1.append("ax: ");
         var1.append(this.ascentX);
         var1.append(" ay: ");
         var1.append(this.ascentY);
         var1.append(" dx: ");
         var1.append(this.descentX);
         var1.append(" dy: ");
         var1.append(this.descentY);
         var1.append(" lx: ");
         var1.append(this.leadingX);
         var1.append(" ly: ");
         var1.append(this.leadingY);
         return var1;
      }
   }

   public static final class GlyphStrike {
      StandardGlyphVector sgv;
      FontStrike strike;
      float dx;
      float dy;

      static StandardGlyphVector.GlyphStrike create(StandardGlyphVector var0, AffineTransform var1, AffineTransform var2) {
         float var3 = 0.0F;
         float var4 = 0.0F;
         AffineTransform var5 = var0.ftx;
         if (!var1.isIdentity() || var2 != null) {
            var5 = new AffineTransform(var0.ftx);
            if (var2 != null) {
               var5.preConcatenate(var2);
               var3 = (float)var5.getTranslateX();
               var4 = (float)var5.getTranslateY();
            }

            if (!var1.isIdentity()) {
               var5.preConcatenate(var1);
            }
         }

         int var6 = 1;
         Object var7 = var0.frc.getAntiAliasingHint();
         if (var7 == RenderingHints.VALUE_TEXT_ANTIALIAS_GASP && !var5.isIdentity() && (var5.getType() & -2) != 0) {
            double var8 = var5.getShearX();
            if (var8 != 0.0D) {
               double var10 = var5.getScaleY();
               var6 = (int)Math.sqrt(var8 * var8 + var10 * var10);
            } else {
               var6 = (int)Math.abs(var5.getScaleY());
            }
         }

         int var14 = FontStrikeDesc.getAAHintIntVal(var7, var0.font2D, var6);
         int var9 = FontStrikeDesc.getFMHintIntVal(var0.frc.getFractionalMetricsHint());
         FontStrikeDesc var13 = new FontStrikeDesc(var1, var5, var0.font.getStyle(), var14, var9);
         Object var11 = var0.font2D;
         if (var11 instanceof FontSubstitution) {
            var11 = ((FontSubstitution)var11).getCompositeFont2D();
         }

         FontStrike var12 = ((Font2D)var11).handle.font2D.getStrike(var13);
         return new StandardGlyphVector.GlyphStrike(var0, var12, var3, var4);
      }

      private GlyphStrike(StandardGlyphVector var1, FontStrike var2, float var3, float var4) {
         this.sgv = var1;
         this.strike = var2;
         this.dx = var3;
         this.dy = var4;
      }

      void getADL(StandardGlyphVector.ADL var1) {
         StrikeMetrics var2 = this.strike.getFontMetrics();
         Point2D.Float var3 = null;
         if (this.sgv.font.isTransformed()) {
            var3 = new Point2D.Float();
            var3.x = (float)this.sgv.font.getTransform().getTranslateX();
            var3.y = (float)this.sgv.font.getTransform().getTranslateY();
         }

         var1.ascentX = -var2.ascentX;
         var1.ascentY = -var2.ascentY;
         var1.descentX = var2.descentX;
         var1.descentY = var2.descentY;
         var1.leadingX = var2.leadingX;
         var1.leadingY = var2.leadingY;
      }

      void getGlyphPosition(int var1, int var2, float[] var3, float[] var4) {
         var4[var2] = var3[var2] + this.dx;
         ++var2;
         var4[var2] = var3[var2] + this.dy;
      }

      void addDefaultGlyphAdvance(int var1, Point2D.Float var2) {
         Point2D.Float var3 = this.strike.getGlyphMetrics(var1);
         var2.x += var3.x + this.dx;
         var2.y += var3.y + this.dy;
      }

      Rectangle2D getGlyphOutlineBounds(int var1, float var2, float var3) {
         Object var4 = null;
         if (this.sgv.invdtx == null) {
            var4 = new Rectangle2D.Float();
            ((Rectangle2D)var4).setRect(this.strike.getGlyphOutlineBounds(var1));
         } else {
            GeneralPath var5 = this.strike.getGlyphOutline(var1, 0.0F, 0.0F);
            var5.transform(this.sgv.invdtx);
            var4 = var5.getBounds2D();
         }

         if (!((Rectangle2D)var4).isEmpty()) {
            ((Rectangle2D)var4).setRect(((Rectangle2D)var4).getMinX() + (double)var2 + (double)this.dx, ((Rectangle2D)var4).getMinY() + (double)var3 + (double)this.dy, ((Rectangle2D)var4).getWidth(), ((Rectangle2D)var4).getHeight());
         }

         return (Rectangle2D)var4;
      }

      void appendGlyphOutline(int var1, GeneralPath var2, float var3, float var4) {
         GeneralPath var5 = null;
         if (this.sgv.invdtx == null) {
            var5 = this.strike.getGlyphOutline(var1, var3 + this.dx, var4 + this.dy);
         } else {
            var5 = this.strike.getGlyphOutline(var1, 0.0F, 0.0F);
            var5.transform(this.sgv.invdtx);
            var5.transform(AffineTransform.getTranslateInstance((double)(var3 + this.dx), (double)(var4 + this.dy)));
         }

         PathIterator var6 = var5.getPathIterator((AffineTransform)null);
         var2.append(var6, false);
      }
   }

   static final class GlyphTransformInfo {
      StandardGlyphVector sgv;
      int[] indices;
      double[] transforms;
      SoftReference strikesRef;
      boolean haveAllStrikes;

      GlyphTransformInfo(StandardGlyphVector var1) {
         this.sgv = var1;
      }

      GlyphTransformInfo(StandardGlyphVector var1, StandardGlyphVector.GlyphTransformInfo var2) {
         this.sgv = var1;
         this.indices = var2.indices == null ? null : (int[])((int[])var2.indices.clone());
         this.transforms = var2.transforms == null ? null : (double[])((double[])var2.transforms.clone());
         this.strikesRef = null;
      }

      public boolean equals(StandardGlyphVector.GlyphTransformInfo var1) {
         if (var1 == null) {
            return false;
         } else if (var1 == this) {
            return true;
         } else if (this.indices.length != var1.indices.length) {
            return false;
         } else if (this.transforms.length != var1.transforms.length) {
            return false;
         } else {
            for(int var2 = 0; var2 < this.indices.length; ++var2) {
               int var3 = this.indices[var2];
               int var4 = var1.indices[var2];
               if (var3 == 0 != (var4 == 0)) {
                  return false;
               }

               if (var3 != 0) {
                  var3 *= 6;
                  var4 *= 6;

                  for(int var5 = 6; var5 > 0; --var5) {
                     --var3;
                     int var10000 = this.indices[var3];
                     --var4;
                     if (var10000 != var1.indices[var4]) {
                        return false;
                     }
                  }
               }
            }

            return true;
         }
      }

      void setGlyphTransform(int var1, AffineTransform var2) {
         double[] var3 = new double[6];
         boolean var4 = true;
         if (var2 != null && !var2.isIdentity()) {
            var4 = false;
            var2.getMatrix(var3);
         } else {
            var3[0] = var3[3] = 1.0D;
         }

         if (this.indices == null) {
            if (var4) {
               return;
            }

            this.indices = new int[this.sgv.glyphs.length];
            this.indices[var1] = 1;
            this.transforms = var3;
         } else {
            boolean var5 = false;
            boolean var6 = true;
            int var7;
            int var11;
            if (var4) {
               var11 = 0;
            } else {
               var5 = true;
               var7 = 0;

               label95:
               while(var7 < this.transforms.length) {
                  for(int var8 = 0; var8 < 6; ++var8) {
                     if (this.transforms[var7 + var8] != var3[var8]) {
                        var7 += 6;
                        continue label95;
                     }
                  }

                  var5 = false;
                  break;
               }

               var11 = var7 / 6 + 1;
            }

            var7 = this.indices[var1];
            if (var11 != var7) {
               boolean var12 = false;
               if (var7 != 0) {
                  var12 = true;

                  for(int var9 = 0; var9 < this.indices.length; ++var9) {
                     if (this.indices[var9] == var7 && var9 != var1) {
                        var12 = false;
                        break;
                     }
                  }
               }

               if (var12 && var5) {
                  var11 = var7;
                  System.arraycopy(var3, 0, this.transforms, (var7 - 1) * 6, 6);
               } else {
                  double[] var13;
                  if (!var12) {
                     if (var5) {
                        var13 = new double[this.transforms.length + 6];
                        System.arraycopy(this.transforms, 0, var13, 0, this.transforms.length);
                        System.arraycopy(var3, 0, var13, this.transforms.length, 6);
                        this.transforms = var13;
                     }
                  } else {
                     if (this.transforms.length == 6) {
                        this.indices = null;
                        this.transforms = null;
                        this.sgv.clearCaches(var1);
                        this.sgv.clearFlags(1);
                        this.strikesRef = null;
                        return;
                     }

                     var13 = new double[this.transforms.length - 6];
                     System.arraycopy(this.transforms, 0, var13, 0, (var7 - 1) * 6);
                     System.arraycopy(this.transforms, var7 * 6, var13, (var7 - 1) * 6, this.transforms.length - var7 * 6);
                     this.transforms = var13;

                     for(int var10 = 0; var10 < this.indices.length; ++var10) {
                        if (this.indices[var10] > var7) {
                           int var10002 = this.indices[var10]--;
                        }
                     }

                     if (var11 > var7) {
                        --var11;
                     }
                  }
               }

               this.indices[var1] = var11;
            }
         }

         this.sgv.clearCaches(var1);
         this.sgv.addFlags(1);
         this.strikesRef = null;
      }

      AffineTransform getGlyphTransform(int var1) {
         int var2 = this.indices[var1];
         if (var2 == 0) {
            return null;
         } else {
            int var3 = (var2 - 1) * 6;
            return new AffineTransform(this.transforms[var3 + 0], this.transforms[var3 + 1], this.transforms[var3 + 2], this.transforms[var3 + 3], this.transforms[var3 + 4], this.transforms[var3 + 5]);
         }
      }

      int transformCount() {
         return this.transforms == null ? 0 : this.transforms.length / 6;
      }

      Object setupGlyphImages(long[] var1, float[] var2, AffineTransform var3) {
         int var4 = this.sgv.glyphs.length;
         StandardGlyphVector.GlyphStrike[] var5 = this.getAllStrikes();

         for(int var6 = 0; var6 < var4; ++var6) {
            StandardGlyphVector.GlyphStrike var7 = var5[this.indices[var6]];
            int var8 = this.sgv.glyphs[var6];
            var1[var6] = var7.strike.getGlyphImagePtr(var8);
            var7.getGlyphPosition(var8, var6 * 2, this.sgv.positions, var2);
         }

         var3.transform((float[])var2, 0, (float[])var2, 0, var4);
         return var5;
      }

      Rectangle getGlyphsPixelBounds(AffineTransform var1, float var2, float var3, int var4, int var5) {
         Rectangle var6 = null;
         Rectangle var7 = new Rectangle();
         Point2D.Float var8 = new Point2D.Float();
         int var9 = var4 * 2;

         while(true) {
            --var5;
            if (var5 < 0) {
               return var6 != null ? var6 : var7;
            }

            StandardGlyphVector.GlyphStrike var10 = this.getStrike(var4);
            var8.x = var2 + this.sgv.positions[var9++] + var10.dx;
            var8.y = var3 + this.sgv.positions[var9++] + var10.dy;
            var1.transform(var8, var8);
            var10.strike.getGlyphImageBounds(this.sgv.glyphs[var4++], var8, var7);
            if (!var7.isEmpty()) {
               if (var6 == null) {
                  var6 = new Rectangle(var7);
               } else {
                  var6.add(var7);
               }
            }
         }
      }

      StandardGlyphVector.GlyphStrike getStrike(int var1) {
         if (this.indices != null) {
            StandardGlyphVector.GlyphStrike[] var2 = this.getStrikeArray();
            return this.getStrikeAtIndex(var2, this.indices[var1]);
         } else {
            return this.sgv.getDefaultStrike();
         }
      }

      private StandardGlyphVector.GlyphStrike[] getAllStrikes() {
         if (this.indices == null) {
            return null;
         } else {
            StandardGlyphVector.GlyphStrike[] var1 = this.getStrikeArray();
            if (!this.haveAllStrikes) {
               for(int var2 = 0; var2 < var1.length; ++var2) {
                  this.getStrikeAtIndex(var1, var2);
               }

               this.haveAllStrikes = true;
            }

            return var1;
         }
      }

      private StandardGlyphVector.GlyphStrike[] getStrikeArray() {
         StandardGlyphVector.GlyphStrike[] var1 = null;
         if (this.strikesRef != null) {
            var1 = (StandardGlyphVector.GlyphStrike[])((StandardGlyphVector.GlyphStrike[])this.strikesRef.get());
         }

         if (var1 == null) {
            this.haveAllStrikes = false;
            var1 = new StandardGlyphVector.GlyphStrike[this.transformCount() + 1];
            this.strikesRef = new SoftReference(var1);
         }

         return var1;
      }

      private StandardGlyphVector.GlyphStrike getStrikeAtIndex(StandardGlyphVector.GlyphStrike[] var1, int var2) {
         StandardGlyphVector.GlyphStrike var3 = var1[var2];
         if (var3 == null) {
            if (var2 == 0) {
               var3 = this.sgv.getDefaultStrike();
            } else {
               int var4 = (var2 - 1) * 6;
               AffineTransform var5 = new AffineTransform(this.transforms[var4], this.transforms[var4 + 1], this.transforms[var4 + 2], this.transforms[var4 + 3], this.transforms[var4 + 4], this.transforms[var4 + 5]);
               var3 = StandardGlyphVector.GlyphStrike.create(this.sgv, this.sgv.dtx, var5);
            }

            var1[var2] = var3;
         }

         return var3;
      }
   }
}
