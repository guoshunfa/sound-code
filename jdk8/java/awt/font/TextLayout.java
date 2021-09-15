package java.awt.font;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.CoreMetrics;
import sun.font.FontResolver;
import sun.font.GraphicComponent;
import sun.font.LayoutPathImpl;
import sun.text.CodePointIterator;

public final class TextLayout implements Cloneable {
   private int characterCount;
   private boolean isVerticalLine = false;
   private byte baseline;
   private float[] baselineOffsets;
   private TextLine textLine;
   private TextLine.TextLineMetrics lineMetrics = null;
   private float visibleAdvance;
   private int hashCodeCache;
   private boolean cacheIsValid = false;
   private float justifyRatio;
   private static final float ALREADY_JUSTIFIED = -53.9F;
   private static float dx;
   private static float dy;
   private Rectangle2D naturalBounds = null;
   private Rectangle2D boundsRect = null;
   private boolean caretsInLigaturesAreAllowed = false;
   public static final TextLayout.CaretPolicy DEFAULT_CARET_POLICY = new TextLayout.CaretPolicy();

   public TextLayout(String var1, Font var2, FontRenderContext var3) {
      if (var2 == null) {
         throw new IllegalArgumentException("Null font passed to TextLayout constructor.");
      } else if (var1 == null) {
         throw new IllegalArgumentException("Null string passed to TextLayout constructor.");
      } else if (var1.length() == 0) {
         throw new IllegalArgumentException("Zero length string passed to TextLayout constructor.");
      } else {
         Map var4 = null;
         if (var2.hasLayoutAttributes()) {
            var4 = var2.getAttributes();
         }

         char[] var5 = var1.toCharArray();
         if (sameBaselineUpTo(var2, var5, 0, var5.length) == var5.length) {
            this.fastInit(var5, var2, var4, var3);
         } else {
            AttributedString var6 = var4 == null ? new AttributedString(var1) : new AttributedString(var1, var4);
            var6.addAttribute(TextAttribute.FONT, var2);
            this.standardInit(var6.getIterator(), var5, var3);
         }

      }
   }

   public TextLayout(String var1, Map<? extends AttributedCharacterIterator.Attribute, ?> var2, FontRenderContext var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null string passed to TextLayout constructor.");
      } else if (var2 == null) {
         throw new IllegalArgumentException("Null map passed to TextLayout constructor.");
      } else if (var1.length() == 0) {
         throw new IllegalArgumentException("Zero length string passed to TextLayout constructor.");
      } else {
         char[] var4 = var1.toCharArray();
         Font var5 = singleFont(var4, 0, var4.length, var2);
         if (var5 != null) {
            this.fastInit(var4, var5, var2, var3);
         } else {
            AttributedString var6 = new AttributedString(var1, var2);
            this.standardInit(var6.getIterator(), var4, var3);
         }

      }
   }

   private static Font singleFont(char[] var0, int var1, int var2, Map<? extends AttributedCharacterIterator.Attribute, ?> var3) {
      if (var3.get(TextAttribute.CHAR_REPLACEMENT) != null) {
         return null;
      } else {
         Font var4 = null;

         try {
            var4 = (Font)var3.get(TextAttribute.FONT);
         } catch (ClassCastException var8) {
         }

         if (var4 == null) {
            if (var3.get(TextAttribute.FAMILY) != null) {
               var4 = Font.getFont(var3);
               if (var4.canDisplayUpTo(var0, var1, var2) != -1) {
                  return null;
               }
            } else {
               FontResolver var5 = FontResolver.getInstance();
               CodePointIterator var6 = CodePointIterator.create(var0, var1, var2);
               int var7 = var5.nextFontRunIndex(var6);
               if (var6.charIndex() == var2) {
                  var4 = var5.getFont(var7, var3);
               }
            }
         }

         return sameBaselineUpTo(var4, var0, var1, var2) != var2 ? null : var4;
      }
   }

   public TextLayout(AttributedCharacterIterator var1, FontRenderContext var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null iterator passed to TextLayout constructor.");
      } else {
         int var3 = var1.getBeginIndex();
         int var4 = var1.getEndIndex();
         if (var3 == var4) {
            throw new IllegalArgumentException("Zero length iterator passed to TextLayout constructor.");
         } else {
            int var5 = var4 - var3;
            var1.first();
            char[] var6 = new char[var5];
            int var7 = 0;

            for(char var8 = var1.first(); var8 != '\uffff'; var8 = var1.next()) {
               var6[var7++] = var8;
            }

            var1.first();
            if (var1.getRunLimit() == var4) {
               Map var10 = var1.getAttributes();
               Font var9 = singleFont(var6, 0, var5, var10);
               if (var9 != null) {
                  this.fastInit(var6, var9, var10, var2);
                  return;
               }
            }

            this.standardInit(var1, var6, var2);
         }
      }
   }

   TextLayout(TextLine var1, byte var2, float[] var3, float var4) {
      this.characterCount = var1.characterCount();
      this.baseline = var2;
      this.baselineOffsets = var3;
      this.textLine = var1;
      this.justifyRatio = var4;
   }

   private void paragraphInit(byte var1, CoreMetrics var2, Map<? extends AttributedCharacterIterator.Attribute, ?> var3, char[] var4) {
      this.baseline = var1;
      this.baselineOffsets = TextLine.getNormalizedOffsets(var2.baselineOffsets, this.baseline);
      this.justifyRatio = AttributeValues.getJustification(var3);
      NumericShaper var5 = AttributeValues.getNumericShaping(var3);
      if (var5 != null) {
         var5.shape(var4, 0, var4.length);
      }

   }

   private void fastInit(char[] var1, Font var2, Map<? extends AttributedCharacterIterator.Attribute, ?> var3, FontRenderContext var4) {
      this.isVerticalLine = false;
      LineMetrics var5 = var2.getLineMetrics((char[])var1, 0, var1.length, var4);
      CoreMetrics var6 = CoreMetrics.get(var5);
      byte var7 = (byte)var6.baselineIndex;
      if (var3 == null) {
         this.baseline = var7;
         this.baselineOffsets = var6.baselineOffsets;
         this.justifyRatio = 1.0F;
      } else {
         this.paragraphInit(var7, var6, var3, var1);
      }

      this.characterCount = var1.length;
      this.textLine = TextLine.fastCreateTextLine(var4, var1, var2, var6, var3);
   }

   private void standardInit(AttributedCharacterIterator var1, char[] var2, FontRenderContext var3) {
      this.characterCount = var2.length;
      Map var4 = var1.getAttributes();
      boolean var5 = TextLine.advanceToFirstFont(var1);
      if (var5) {
         Font var6 = TextLine.getFontAtCurrentPos(var1);
         int var7 = var1.getIndex() - var1.getBeginIndex();
         LineMetrics var8 = var6.getLineMetrics(var2, var7, var7 + 1, var3);
         CoreMetrics var9 = CoreMetrics.get(var8);
         this.paragraphInit((byte)var9.baselineIndex, var9, var4, var2);
      } else {
         GraphicAttribute var10 = (GraphicAttribute)var4.get(TextAttribute.CHAR_REPLACEMENT);
         byte var11 = getBaselineFromGraphic(var10);
         CoreMetrics var12 = GraphicComponent.createCoreMetrics(var10);
         this.paragraphInit(var11, var12, var4, var2);
      }

      this.textLine = TextLine.standardCreateTextLine(var3, var1, var2, this.baselineOffsets);
   }

   private void ensureCache() {
      if (!this.cacheIsValid) {
         this.buildCache();
      }

   }

   private void buildCache() {
      this.lineMetrics = this.textLine.getMetrics();
      int var1;
      int var2;
      if (this.textLine.isDirectionLTR()) {
         for(var1 = this.characterCount - 1; var1 != -1; --var1) {
            var2 = this.textLine.visualToLogical(var1);
            if (!this.textLine.isCharSpace(var2)) {
               break;
            }
         }

         if (var1 == this.characterCount - 1) {
            this.visibleAdvance = this.lineMetrics.advance;
         } else if (var1 == -1) {
            this.visibleAdvance = 0.0F;
         } else {
            var2 = this.textLine.visualToLogical(var1);
            this.visibleAdvance = this.textLine.getCharLinePosition(var2) + this.textLine.getCharAdvance(var2);
         }
      } else {
         for(var1 = 0; var1 != this.characterCount; ++var1) {
            var2 = this.textLine.visualToLogical(var1);
            if (!this.textLine.isCharSpace(var2)) {
               break;
            }
         }

         if (var1 == this.characterCount) {
            this.visibleAdvance = 0.0F;
         } else if (var1 == 0) {
            this.visibleAdvance = this.lineMetrics.advance;
         } else {
            var2 = this.textLine.visualToLogical(var1);
            float var3 = this.textLine.getCharLinePosition(var2);
            this.visibleAdvance = this.lineMetrics.advance - var3;
         }
      }

      this.naturalBounds = null;
      this.boundsRect = null;
      this.hashCodeCache = 0;
      this.cacheIsValid = true;
   }

   private Rectangle2D getNaturalBounds() {
      this.ensureCache();
      if (this.naturalBounds == null) {
         this.naturalBounds = this.textLine.getItalicBounds();
      }

      return this.naturalBounds;
   }

   protected Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   private void checkTextHit(TextHitInfo var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("TextHitInfo is null.");
      } else if (var1.getInsertionIndex() < 0 || var1.getInsertionIndex() > this.characterCount) {
         throw new IllegalArgumentException("TextHitInfo is out of range");
      }
   }

   public TextLayout getJustifiedLayout(float var1) {
      if (var1 <= 0.0F) {
         throw new IllegalArgumentException("justificationWidth <= 0 passed to TextLayout.getJustifiedLayout()");
      } else if (this.justifyRatio == -53.9F) {
         throw new Error("Can't justify again.");
      } else {
         this.ensureCache();

         int var2;
         for(var2 = this.characterCount; var2 > 0 && this.textLine.isCharWhitespace(var2 - 1); --var2) {
         }

         TextLine var3 = this.textLine.getJustifiedLine(var1, this.justifyRatio, 0, var2);
         return var3 != null ? new TextLayout(var3, this.baseline, this.baselineOffsets, -53.9F) : this;
      }
   }

   protected void handleJustify(float var1) {
   }

   public byte getBaseline() {
      return this.baseline;
   }

   public float[] getBaselineOffsets() {
      float[] var1 = new float[this.baselineOffsets.length];
      System.arraycopy(this.baselineOffsets, 0, var1, 0, var1.length);
      return var1;
   }

   public float getAdvance() {
      this.ensureCache();
      return this.lineMetrics.advance;
   }

   public float getVisibleAdvance() {
      this.ensureCache();
      return this.visibleAdvance;
   }

   public float getAscent() {
      this.ensureCache();
      return this.lineMetrics.ascent;
   }

   public float getDescent() {
      this.ensureCache();
      return this.lineMetrics.descent;
   }

   public float getLeading() {
      this.ensureCache();
      return this.lineMetrics.leading;
   }

   public Rectangle2D getBounds() {
      this.ensureCache();
      if (this.boundsRect == null) {
         Rectangle2D var1 = this.textLine.getVisualBounds();
         if (dx != 0.0F || dy != 0.0F) {
            var1.setRect(var1.getX() - (double)dx, var1.getY() - (double)dy, var1.getWidth(), var1.getHeight());
         }

         this.boundsRect = var1;
      }

      Rectangle2D.Float var2 = new Rectangle2D.Float();
      var2.setRect(this.boundsRect);
      return var2;
   }

   public Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3) {
      return this.textLine.getPixelBounds(var1, var2, var3);
   }

   public boolean isLeftToRight() {
      return this.textLine.isDirectionLTR();
   }

   public boolean isVertical() {
      return this.isVerticalLine;
   }

   public int getCharacterCount() {
      return this.characterCount;
   }

   private float[] getCaretInfo(int var1, Rectangle2D var2, float[] var3) {
      float var4;
      float var5;
      float var6;
      float var7;
      float var8;
      float var10;
      float var13;
      if (var1 != 0 && var1 != this.characterCount) {
         int var12 = this.textLine.visualToLogical(var1 - 1);
         var13 = this.textLine.getCharAngle(var12);
         var10 = this.textLine.getCharLinePosition(var12) + this.textLine.getCharAdvance(var12);
         if (var13 != 0.0F) {
            var10 += var13 * this.textLine.getCharShift(var12);
            var4 = var10 + var13 * this.textLine.getCharAscent(var12);
            var6 = var10 - var13 * this.textLine.getCharDescent(var12);
         } else {
            var6 = var10;
            var4 = var10;
         }

         var12 = this.textLine.visualToLogical(var1);
         var13 = this.textLine.getCharAngle(var12);
         var10 = this.textLine.getCharLinePosition(var12);
         if (var13 != 0.0F) {
            var10 += var13 * this.textLine.getCharShift(var12);
            var5 = var10 + var13 * this.textLine.getCharAscent(var12);
            var7 = var10 - var13 * this.textLine.getCharDescent(var12);
         } else {
            var7 = var10;
            var5 = var10;
         }
      } else {
         int var9;
         if (var1 == this.characterCount) {
            var9 = this.textLine.visualToLogical(this.characterCount - 1);
            var8 = this.textLine.getCharLinePosition(var9) + this.textLine.getCharAdvance(var9);
         } else {
            var9 = this.textLine.visualToLogical(var1);
            var8 = this.textLine.getCharLinePosition(var9);
         }

         var10 = this.textLine.getCharAngle(var9);
         float var11 = this.textLine.getCharShift(var9);
         var8 += var10 * var11;
         var4 = var5 = var8 + var10 * this.textLine.getCharAscent(var9);
         var6 = var7 = var8 - var10 * this.textLine.getCharDescent(var9);
      }

      var8 = (var4 + var5) / 2.0F;
      var13 = (var6 + var7) / 2.0F;
      if (var3 == null) {
         var3 = new float[2];
      }

      if (this.isVerticalLine) {
         var3[1] = (float)((double)(var8 - var13) / var2.getWidth());
         var3[0] = (float)((double)var8 + (double)var3[1] * var2.getX());
      } else {
         var3[1] = (float)((double)(var8 - var13) / var2.getHeight());
         var3[0] = (float)((double)var13 + (double)var3[1] * var2.getMaxY());
      }

      return var3;
   }

   public float[] getCaretInfo(TextHitInfo var1, Rectangle2D var2) {
      this.ensureCache();
      this.checkTextHit(var1);
      return this.getCaretInfoTestInternal(var1, var2);
   }

   private float[] getCaretInfoTestInternal(TextHitInfo var1, Rectangle2D var2) {
      this.ensureCache();
      this.checkTextHit(var1);
      float[] var3 = new float[6];
      this.getCaretInfo(this.hitToCaret(var1), var2, var3);
      int var16 = var1.getCharIndex();
      boolean var17 = var1.isLeadingEdge();
      boolean var18 = this.textLine.isDirectionLTR();
      boolean var19 = !this.isVertical();
      double var4;
      double var8;
      double var10;
      double var12;
      double var14;
      if (var16 != -1 && var16 != this.characterCount) {
         CoreMetrics var24 = this.textLine.getCoreMetricsAt(var16);
         var4 = (double)var24.italicAngle;
         double var6 = (double)this.textLine.getCharLinePosition(var16, var17);
         if (var24.baselineIndex < 0) {
            TextLine.TextLineMetrics var22 = this.textLine.getMetrics();
            if (var19) {
               var12 = var6;
               var8 = var6;
               if (var24.baselineIndex == -1) {
                  var10 = (double)(-var22.ascent);
                  var14 = var10 + (double)var24.height;
               } else {
                  var14 = (double)var22.descent;
                  var10 = var14 - (double)var24.height;
               }
            } else {
               var14 = var6;
               var10 = var6;
               var8 = (double)var22.descent;
               var12 = (double)var22.ascent;
            }
         } else {
            float var23 = this.baselineOffsets[var24.baselineIndex];
            if (var19) {
               var6 += var4 * (double)var24.ssOffset;
               var8 = var6 + var4 * (double)var24.ascent;
               var12 = var6 - var4 * (double)var24.descent;
               var10 = (double)(var23 - var24.ascent);
               var14 = (double)(var23 + var24.descent);
            } else {
               var6 -= var4 * (double)var24.ssOffset;
               var10 = var6 + var4 * (double)var24.ascent;
               var14 = var6 - var4 * (double)var24.descent;
               var8 = (double)(var23 + var24.ascent);
               var12 = (double)(var23 + var24.descent);
            }
         }
      } else {
         TextLine.TextLineMetrics var20 = this.textLine.getMetrics();
         boolean var21 = var18 == (var16 == -1);
         var4 = 0.0D;
         if (var19) {
            var8 = var12 = var21 ? 0.0D : (double)var20.advance;
            var10 = (double)(-var20.ascent);
            var14 = (double)var20.descent;
         } else {
            var10 = var14 = var21 ? 0.0D : (double)var20.advance;
            var8 = (double)var20.descent;
            var12 = (double)var20.ascent;
         }
      }

      var3[2] = (float)var8;
      var3[3] = (float)var10;
      var3[4] = (float)var12;
      var3[5] = (float)var14;
      return var3;
   }

   public float[] getCaretInfo(TextHitInfo var1) {
      return this.getCaretInfo(var1, this.getNaturalBounds());
   }

   private int hitToCaret(TextHitInfo var1) {
      int var2 = var1.getCharIndex();
      if (var2 < 0) {
         return this.textLine.isDirectionLTR() ? 0 : this.characterCount;
      } else if (var2 >= this.characterCount) {
         return this.textLine.isDirectionLTR() ? this.characterCount : 0;
      } else {
         int var3 = this.textLine.logicalToVisual(var2);
         if (var1.isLeadingEdge() != this.textLine.isCharLTR(var2)) {
            ++var3;
         }

         return var3;
      }
   }

   private TextHitInfo caretToHit(int var1) {
      if (var1 != 0 && var1 != this.characterCount) {
         int var2 = this.textLine.visualToLogical(var1);
         boolean var3 = this.textLine.isCharLTR(var2);
         return var3 ? TextHitInfo.leading(var2) : TextHitInfo.trailing(var2);
      } else {
         return var1 == this.characterCount == this.textLine.isDirectionLTR() ? TextHitInfo.leading(this.characterCount) : TextHitInfo.trailing(-1);
      }
   }

   private boolean caretIsValid(int var1) {
      if (var1 != this.characterCount && var1 != 0) {
         int var2 = this.textLine.visualToLogical(var1);
         if (!this.textLine.isCharLTR(var2)) {
            var2 = this.textLine.visualToLogical(var1 - 1);
            if (this.textLine.isCharLTR(var2)) {
               return true;
            }
         }

         return this.textLine.caretAtOffsetIsValid(var2);
      } else {
         return true;
      }
   }

   public TextHitInfo getNextRightHit(TextHitInfo var1) {
      this.ensureCache();
      this.checkTextHit(var1);
      int var2 = this.hitToCaret(var1);
      if (var2 == this.characterCount) {
         return null;
      } else {
         do {
            ++var2;
         } while(!this.caretIsValid(var2));

         return this.caretToHit(var2);
      }
   }

   public TextHitInfo getNextRightHit(int var1, TextLayout.CaretPolicy var2) {
      if (var1 >= 0 && var1 <= this.characterCount) {
         if (var2 == null) {
            throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextRightHit()");
         } else {
            TextHitInfo var3 = TextHitInfo.afterOffset(var1);
            TextHitInfo var4 = var3.getOtherHit();
            TextHitInfo var5 = this.getNextRightHit(var2.getStrongCaret(var3, var4, this));
            if (var5 != null) {
               TextHitInfo var6 = this.getVisualOtherHit(var5);
               return var2.getStrongCaret(var6, var5, this);
            } else {
               return null;
            }
         }
      } else {
         throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextRightHit()");
      }
   }

   public TextHitInfo getNextRightHit(int var1) {
      return this.getNextRightHit(var1, DEFAULT_CARET_POLICY);
   }

   public TextHitInfo getNextLeftHit(TextHitInfo var1) {
      this.ensureCache();
      this.checkTextHit(var1);
      int var2 = this.hitToCaret(var1);
      if (var2 == 0) {
         return null;
      } else {
         do {
            --var2;
         } while(!this.caretIsValid(var2));

         return this.caretToHit(var2);
      }
   }

   public TextHitInfo getNextLeftHit(int var1, TextLayout.CaretPolicy var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getNextLeftHit()");
      } else if (var1 >= 0 && var1 <= this.characterCount) {
         TextHitInfo var3 = TextHitInfo.afterOffset(var1);
         TextHitInfo var4 = var3.getOtherHit();
         TextHitInfo var5 = this.getNextLeftHit(var2.getStrongCaret(var3, var4, this));
         if (var5 != null) {
            TextHitInfo var6 = this.getVisualOtherHit(var5);
            return var2.getStrongCaret(var6, var5, this);
         } else {
            return null;
         }
      } else {
         throw new IllegalArgumentException("Offset out of bounds in TextLayout.getNextLeftHit()");
      }
   }

   public TextHitInfo getNextLeftHit(int var1) {
      return this.getNextLeftHit(var1, DEFAULT_CARET_POLICY);
   }

   public TextHitInfo getVisualOtherHit(TextHitInfo var1) {
      this.ensureCache();
      this.checkTextHit(var1);
      int var2 = var1.getCharIndex();
      int var3;
      boolean var4;
      int var5;
      if (var2 != -1 && var2 != this.characterCount) {
         var5 = this.textLine.logicalToVisual(var2);
         boolean var6;
         if (this.textLine.isCharLTR(var2) == var1.isLeadingEdge()) {
            --var5;
            var6 = false;
         } else {
            ++var5;
            var6 = true;
         }

         if (var5 > -1 && var5 < this.characterCount) {
            var3 = this.textLine.visualToLogical(var5);
            var4 = var6 == this.textLine.isCharLTR(var3);
         } else {
            var3 = var6 == this.textLine.isDirectionLTR() ? this.characterCount : -1;
            var4 = var3 == this.characterCount;
         }
      } else {
         if (this.textLine.isDirectionLTR() == (var2 == -1)) {
            var5 = 0;
         } else {
            var5 = this.characterCount - 1;
         }

         var3 = this.textLine.visualToLogical(var5);
         if (this.textLine.isDirectionLTR() == (var2 == -1)) {
            var4 = this.textLine.isCharLTR(var3);
         } else {
            var4 = !this.textLine.isCharLTR(var3);
         }
      }

      return var4 ? TextHitInfo.leading(var3) : TextHitInfo.trailing(var3);
   }

   private double[] getCaretPath(TextHitInfo var1, Rectangle2D var2) {
      float[] var3 = this.getCaretInfo(var1, var2);
      return new double[]{(double)var3[2], (double)var3[3], (double)var3[4], (double)var3[5]};
   }

   private double[] getCaretPath(int var1, Rectangle2D var2, boolean var3) {
      float[] var4 = this.getCaretInfo(var1, var2, (float[])null);
      double var5 = (double)var4[0];
      double var7 = (double)var4[1];
      double var17 = -3141.59D;
      double var19 = -2.7D;
      double var21 = var2.getX();
      double var23 = var21 + var2.getWidth();
      double var25 = var2.getY();
      double var27 = var25 + var2.getHeight();
      boolean var29 = false;
      double var9;
      double var11;
      double var13;
      double var15;
      if (this.isVerticalLine) {
         if (var7 >= 0.0D) {
            var9 = var21;
            var13 = var23;
         } else {
            var13 = var21;
            var9 = var23;
         }

         var11 = var5 + var9 * var7;
         var15 = var5 + var13 * var7;
         if (var3) {
            if (var11 < var25) {
               if (var7 > 0.0D && var15 > var25) {
                  var29 = true;
                  var11 = var25;
                  var19 = var25;
                  var17 = var13 + (var25 - var15) / var7;
                  if (var15 > var27) {
                     var15 = var27;
                  }
               } else {
                  var15 = var25;
                  var11 = var25;
               }
            } else if (var15 > var27) {
               if (var7 < 0.0D && var11 < var27) {
                  var29 = true;
                  var15 = var27;
                  var19 = var27;
                  var17 = var9 + (var27 - var13) / var7;
               } else {
                  var15 = var27;
                  var11 = var27;
               }
            }
         }
      } else {
         if (var7 >= 0.0D) {
            var11 = var27;
            var15 = var25;
         } else {
            var15 = var27;
            var11 = var25;
         }

         var9 = var5 - var11 * var7;
         var13 = var5 - var15 * var7;
         if (var3) {
            if (var9 < var21) {
               if (var7 > 0.0D && var13 > var21) {
                  var29 = true;
                  var9 = var21;
                  var17 = var21;
                  var19 = var15 - (var21 - var13) / var7;
                  if (var13 > var23) {
                     var13 = var23;
                  }
               } else {
                  var13 = var21;
                  var9 = var21;
               }
            } else if (var13 > var23) {
               if (var7 < 0.0D && var9 < var23) {
                  var29 = true;
                  var13 = var23;
                  var17 = var23;
                  var19 = var11 - (var23 - var9) / var7;
               } else {
                  var13 = var23;
                  var9 = var23;
               }
            }
         }
      }

      return var29 ? new double[]{var9, var11, var17, var19, var13, var15} : new double[]{var9, var11, var13, var15};
   }

   private static GeneralPath pathToShape(double[] var0, boolean var1, LayoutPathImpl var2) {
      GeneralPath var3 = new GeneralPath(0, var0.length);
      var3.moveTo((float)var0[0], (float)var0[1]);

      for(int var4 = 2; var4 < var0.length; var4 += 2) {
         var3.lineTo((float)var0[var4], (float)var0[var4 + 1]);
      }

      if (var1) {
         var3.closePath();
      }

      if (var2 != null) {
         var3 = (GeneralPath)var2.mapShape(var3);
      }

      return var3;
   }

   public Shape getCaretShape(TextHitInfo var1, Rectangle2D var2) {
      this.ensureCache();
      this.checkTextHit(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaret()");
      } else {
         return pathToShape(this.getCaretPath(var1, var2), false, this.textLine.getLayoutPath());
      }
   }

   public Shape getCaretShape(TextHitInfo var1) {
      return this.getCaretShape(var1, this.getNaturalBounds());
   }

   private final TextHitInfo getStrongHit(TextHitInfo var1, TextHitInfo var2) {
      byte var3 = this.getCharacterLevel(var1.getCharIndex());
      byte var4 = this.getCharacterLevel(var2.getCharIndex());
      if (var3 == var4) {
         return var2.isLeadingEdge() && !var1.isLeadingEdge() ? var2 : var1;
      } else {
         return var3 < var4 ? var1 : var2;
      }
   }

   public byte getCharacterLevel(int var1) {
      if (var1 >= -1 && var1 <= this.characterCount) {
         this.ensureCache();
         return var1 != -1 && var1 != this.characterCount ? this.textLine.getCharLevel(var1) : (byte)(this.textLine.isDirectionLTR() ? 0 : 1);
      } else {
         throw new IllegalArgumentException("Index is out of range in getCharacterLevel.");
      }
   }

   public Shape[] getCaretShapes(int var1, Rectangle2D var2, TextLayout.CaretPolicy var3) {
      this.ensureCache();
      if (var1 >= 0 && var1 <= this.characterCount) {
         if (var2 == null) {
            throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getCaretShapes()");
         } else if (var3 == null) {
            throw new IllegalArgumentException("Null CaretPolicy passed to TextLayout.getCaretShapes()");
         } else {
            Shape[] var4 = new Shape[2];
            TextHitInfo var5 = TextHitInfo.afterOffset(var1);
            int var6 = this.hitToCaret(var5);
            LayoutPathImpl var7 = this.textLine.getLayoutPath();
            GeneralPath var8 = pathToShape(this.getCaretPath(var5, var2), false, var7);
            TextHitInfo var9 = var5.getOtherHit();
            int var10 = this.hitToCaret(var9);
            if (var6 == var10) {
               var4[0] = var8;
            } else {
               GeneralPath var11 = pathToShape(this.getCaretPath(var9, var2), false, var7);
               TextHitInfo var12 = var3.getStrongCaret(var5, var9, this);
               boolean var13 = var12.equals(var5);
               if (var13) {
                  var4[0] = var8;
                  var4[1] = var11;
               } else {
                  var4[0] = var11;
                  var4[1] = var8;
               }
            }

            return var4;
         }
      } else {
         throw new IllegalArgumentException("Offset out of bounds in TextLayout.getCaretShapes()");
      }
   }

   public Shape[] getCaretShapes(int var1, Rectangle2D var2) {
      return this.getCaretShapes(var1, var2, DEFAULT_CARET_POLICY);
   }

   public Shape[] getCaretShapes(int var1) {
      return this.getCaretShapes(var1, this.getNaturalBounds(), DEFAULT_CARET_POLICY);
   }

   private GeneralPath boundingShape(double[] var1, double[] var2) {
      GeneralPath var3 = pathToShape(var1, false, (LayoutPathImpl)null);
      boolean var4;
      if (this.isVerticalLine) {
         var4 = var1[1] > var1[var1.length - 1] == var2[1] > var2[var2.length - 1];
      } else {
         var4 = var1[0] > var1[var1.length - 2] == var2[0] > var2[var2.length - 2];
      }

      int var5;
      int var6;
      byte var7;
      if (var4) {
         var5 = var2.length - 2;
         var6 = -2;
         var7 = -2;
      } else {
         var5 = 0;
         var6 = var2.length;
         var7 = 2;
      }

      for(int var8 = var5; var8 != var6; var8 += var7) {
         var3.lineTo((float)var2[var8], (float)var2[var8 + 1]);
      }

      var3.closePath();
      return var3;
   }

   private GeneralPath caretBoundingShape(int var1, int var2, Rectangle2D var3) {
      if (var1 > var2) {
         int var4 = var1;
         var1 = var2;
         var2 = var4;
      }

      return this.boundingShape(this.getCaretPath(var1, var3, true), this.getCaretPath(var2, var3, true));
   }

   private GeneralPath leftShape(Rectangle2D var1) {
      double[] var2;
      if (this.isVerticalLine) {
         var2 = new double[]{var1.getX(), var1.getY(), var1.getX() + var1.getWidth(), var1.getY()};
      } else {
         var2 = new double[]{var1.getX(), var1.getY() + var1.getHeight(), var1.getX(), var1.getY()};
      }

      double[] var3 = this.getCaretPath(0, var1, true);
      return this.boundingShape(var2, var3);
   }

   private GeneralPath rightShape(Rectangle2D var1) {
      double[] var2;
      if (this.isVerticalLine) {
         var2 = new double[]{var1.getX(), var1.getY() + var1.getHeight(), var1.getX() + var1.getWidth(), var1.getY() + var1.getHeight()};
      } else {
         var2 = new double[]{var1.getX() + var1.getWidth(), var1.getY() + var1.getHeight(), var1.getX() + var1.getWidth(), var1.getY()};
      }

      double[] var3 = this.getCaretPath(this.characterCount, var1, true);
      return this.boundingShape(var3, var2);
   }

   public int[] getLogicalRangesForVisualSelection(TextHitInfo var1, TextHitInfo var2) {
      this.ensureCache();
      this.checkTextHit(var1);
      this.checkTextHit(var2);
      boolean[] var3 = new boolean[this.characterCount];
      int var4 = this.hitToCaret(var1);
      int var5 = this.hitToCaret(var2);
      int var6;
      if (var4 > var5) {
         var6 = var4;
         var4 = var5;
         var5 = var6;
      }

      if (var4 < var5) {
         for(var6 = var4; var6 < var5; ++var6) {
            var3[this.textLine.visualToLogical(var6)] = true;
         }
      }

      var6 = 0;
      boolean var7 = false;

      for(int var8 = 0; var8 < this.characterCount; ++var8) {
         if (var3[var8] != var7) {
            var7 = !var7;
            if (var7) {
               ++var6;
            }
         }
      }

      int[] var10 = new int[var6 * 2];
      var6 = 0;
      var7 = false;

      for(int var9 = 0; var9 < this.characterCount; ++var9) {
         if (var3[var9] != var7) {
            var10[var6++] = var9;
            var7 = !var7;
         }
      }

      if (var7) {
         var10[var6++] = this.characterCount;
      }

      return var10;
   }

   public Shape getVisualHighlightShape(TextHitInfo var1, TextHitInfo var2, Rectangle2D var3) {
      this.ensureCache();
      this.checkTextHit(var1);
      this.checkTextHit(var2);
      if (var3 == null) {
         throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getVisualHighlightShape()");
      } else {
         GeneralPath var4 = new GeneralPath(0);
         int var5 = this.hitToCaret(var1);
         int var6 = this.hitToCaret(var2);
         var4.append(this.caretBoundingShape(var5, var6, var3), false);
         GeneralPath var7;
         if (var5 == 0 || var6 == 0) {
            var7 = this.leftShape(var3);
            if (!var7.getBounds().isEmpty()) {
               var4.append(var7, false);
            }
         }

         if (var5 == this.characterCount || var6 == this.characterCount) {
            var7 = this.rightShape(var3);
            if (!var7.getBounds().isEmpty()) {
               var4.append(var7, false);
            }
         }

         LayoutPathImpl var8 = this.textLine.getLayoutPath();
         if (var8 != null) {
            var4 = (GeneralPath)var8.mapShape(var4);
         }

         return var4;
      }
   }

   public Shape getVisualHighlightShape(TextHitInfo var1, TextHitInfo var2) {
      return this.getVisualHighlightShape(var1, var2, this.getNaturalBounds());
   }

   public Shape getLogicalHighlightShape(int var1, int var2, Rectangle2D var3) {
      if (var3 == null) {
         throw new IllegalArgumentException("Null Rectangle2D passed to TextLayout.getLogicalHighlightShape()");
      } else {
         this.ensureCache();
         if (var1 > var2) {
            int var4 = var1;
            var1 = var2;
            var2 = var4;
         }

         if (var1 >= 0 && var2 <= this.characterCount) {
            GeneralPath var11 = new GeneralPath(0);
            int[] var5 = new int[10];
            int var6 = 0;
            int var7;
            if (var1 < var2) {
               var7 = var1;

               do {
                  var5[var6++] = this.hitToCaret(TextHitInfo.leading(var7));
                  boolean var8 = this.textLine.isCharLTR(var7);

                  do {
                     ++var7;
                  } while(var7 < var2 && this.textLine.isCharLTR(var7) == var8);

                  var5[var6++] = this.hitToCaret(TextHitInfo.trailing(var7 - 1));
                  if (var6 == var5.length) {
                     int[] var10 = new int[var5.length + 10];
                     System.arraycopy(var5, 0, var10, 0, var6);
                     var5 = var10;
                  }
               } while(var7 < var2);
            } else {
               var6 = 2;
               var5[0] = var5[1] = this.hitToCaret(TextHitInfo.leading(var1));
            }

            for(var7 = 0; var7 < var6; var7 += 2) {
               var11.append(this.caretBoundingShape(var5[var7], var5[var7 + 1], var3), false);
            }

            if (var1 != var2) {
               GeneralPath var12;
               if (this.textLine.isDirectionLTR() && var1 == 0 || !this.textLine.isDirectionLTR() && var2 == this.characterCount) {
                  var12 = this.leftShape(var3);
                  if (!var12.getBounds().isEmpty()) {
                     var11.append(var12, false);
                  }
               }

               if (this.textLine.isDirectionLTR() && var2 == this.characterCount || !this.textLine.isDirectionLTR() && var1 == 0) {
                  var12 = this.rightShape(var3);
                  if (!var12.getBounds().isEmpty()) {
                     var11.append(var12, false);
                  }
               }
            }

            LayoutPathImpl var13 = this.textLine.getLayoutPath();
            if (var13 != null) {
               var11 = (GeneralPath)var13.mapShape(var11);
            }

            return var11;
         } else {
            throw new IllegalArgumentException("Range is invalid in TextLayout.getLogicalHighlightShape()");
         }
      }
   }

   public Shape getLogicalHighlightShape(int var1, int var2) {
      return this.getLogicalHighlightShape(var1, var2, this.getNaturalBounds());
   }

   public Shape getBlackBoxBounds(int var1, int var2) {
      this.ensureCache();
      if (var1 > var2) {
         int var3 = var1;
         var1 = var2;
         var2 = var3;
      }

      if (var1 >= 0 && var2 <= this.characterCount) {
         GeneralPath var6 = new GeneralPath(1);
         if (var1 < this.characterCount) {
            for(int var4 = var1; var4 < var2; ++var4) {
               Rectangle2D var5 = this.textLine.getCharBounds(var4);
               if (!var5.isEmpty()) {
                  var6.append(var5, false);
               }
            }
         }

         if (dx != 0.0F || dy != 0.0F) {
            AffineTransform var7 = AffineTransform.getTranslateInstance((double)dx, (double)dy);
            var6 = (GeneralPath)var7.createTransformedShape(var6);
         }

         LayoutPathImpl var8 = this.textLine.getLayoutPath();
         if (var8 != null) {
            var6 = (GeneralPath)var8.mapShape(var6);
         }

         return var6;
      } else {
         throw new IllegalArgumentException("Invalid range passed to TextLayout.getBlackBoxBounds()");
      }
   }

   private float caretToPointDistance(float[] var1, float var2, float var3) {
      float var4 = this.isVerticalLine ? var3 : var2;
      float var5 = this.isVerticalLine ? -var2 : var3;
      return var4 - var1[0] + var5 * var1[1];
   }

   public TextHitInfo hitTestChar(float var1, float var2, Rectangle2D var3) {
      LayoutPathImpl var4 = this.textLine.getLayoutPath();
      boolean var5 = false;
      if (var4 != null) {
         Point2D.Float var6 = new Point2D.Float(var1, var2);
         var4.pointToPath(var6, var6);
         var1 = var6.x;
         var2 = var6.y;
      }

      if (this.isVertical()) {
         if ((double)var2 < var3.getMinY()) {
            return TextHitInfo.leading(0);
         }

         if ((double)var2 >= var3.getMaxY()) {
            return TextHitInfo.trailing(this.characterCount - 1);
         }
      } else {
         if ((double)var1 < var3.getMinX()) {
            return this.isLeftToRight() ? TextHitInfo.leading(0) : TextHitInfo.trailing(this.characterCount - 1);
         }

         if ((double)var1 >= var3.getMaxX()) {
            return this.isLeftToRight() ? TextHitInfo.trailing(this.characterCount - 1) : TextHitInfo.leading(0);
         }
      }

      double var24 = Double.MAX_VALUE;
      int var8 = 0;
      int var9 = -1;
      CoreMetrics var10 = null;
      float var11 = 0.0F;
      float var12 = 0.0F;
      float var13 = 0.0F;
      float var14 = 0.0F;
      float var15 = 0.0F;
      float var16 = 0.0F;

      for(int var17 = 0; var17 < this.characterCount; ++var17) {
         if (this.textLine.caretAtOffsetIsValid(var17)) {
            if (var9 == -1) {
               var9 = var17;
            }

            CoreMetrics var18 = this.textLine.getCoreMetricsAt(var17);
            float var19;
            if (var18 != var10) {
               var10 = var18;
               if (var18.baselineIndex == -1) {
                  var14 = -(this.textLine.getMetrics().ascent - var18.ascent) + var18.ssOffset;
               } else if (var18.baselineIndex == -2) {
                  var14 = this.textLine.getMetrics().descent - var18.descent + var18.ssOffset;
               } else {
                  var14 = var18.effectiveBaselineOffset(this.baselineOffsets) + var18.ssOffset;
               }

               var19 = (var18.descent - var18.ascent) / 2.0F - var14;
               var15 = var19 * var18.italicAngle;
               var14 += var19;
               var16 = (var14 - var2) * (var14 - var2);
            }

            var19 = this.textLine.getCharXPosition(var17);
            float var20 = this.textLine.getCharAdvance(var17);
            float var21 = var20 / 2.0F;
            var19 += var21 - var15;
            double var22 = Math.sqrt((double)(4.0F * (var19 - var1) * (var19 - var1) + var16));
            if (var22 < var24) {
               var24 = var22;
               var8 = var17;
               var9 = -1;
               var11 = var19;
               var12 = var14;
               var13 = var18.italicAngle;
            }
         }
      }

      boolean var25 = var1 < var11 - (var2 - var12) * var13;
      boolean var26 = this.textLine.isCharLTR(var8) == var25;
      if (var9 == -1) {
         var9 = this.characterCount;
      }

      TextHitInfo var27 = var26 ? TextHitInfo.leading(var8) : TextHitInfo.trailing(var9 - 1);
      return var27;
   }

   public TextHitInfo hitTestChar(float var1, float var2) {
      return this.hitTestChar(var1, var2, this.getNaturalBounds());
   }

   public int hashCode() {
      if (this.hashCodeCache == 0) {
         this.ensureCache();
         this.hashCodeCache = this.textLine.hashCode();
      }

      return this.hashCodeCache;
   }

   public boolean equals(Object var1) {
      return var1 instanceof TextLayout && this.equals((TextLayout)var1);
   }

   public boolean equals(TextLayout var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else {
         this.ensureCache();
         return this.textLine.equals(var1.textLine);
      }
   }

   public String toString() {
      this.ensureCache();
      return this.textLine.toString();
   }

   public void draw(Graphics2D var1, float var2, float var3) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null Graphics2D passed to TextLayout.draw()");
      } else {
         this.textLine.draw(var1, var2 - dx, var3 - dy);
      }
   }

   TextLine getTextLineForTesting() {
      return this.textLine;
   }

   private static int sameBaselineUpTo(Font var0, char[] var1, int var2, int var3) {
      return var3;
   }

   static byte getBaselineFromGraphic(GraphicAttribute var0) {
      byte var1 = (byte)var0.getAlignment();
      return var1 != -2 && var1 != -1 ? var1 : 0;
   }

   public Shape getOutline(AffineTransform var1) {
      this.ensureCache();
      Shape var2 = this.textLine.getOutline(var1);
      LayoutPathImpl var3 = this.textLine.getLayoutPath();
      if (var3 != null) {
         var2 = var3.mapShape(var2);
      }

      return var2;
   }

   public LayoutPath getLayoutPath() {
      return this.textLine.getLayoutPath();
   }

   public void hitToPoint(TextHitInfo var1, Point2D var2) {
      if (var1 != null && var2 != null) {
         this.ensureCache();
         this.checkTextHit(var1);
         float var3 = 0.0F;
         float var4 = 0.0F;
         int var5 = var1.getCharIndex();
         boolean var6 = var1.isLeadingEdge();
         boolean var7;
         if (var5 != -1 && var5 != this.textLine.characterCount()) {
            var7 = this.textLine.isCharLTR(var5);
            var3 = this.textLine.getCharLinePosition(var5, var6);
            var4 = this.textLine.getCharYPosition(var5);
         } else {
            var7 = this.textLine.isDirectionLTR();
            var3 = var7 == (var5 == -1) ? 0.0F : this.lineMetrics.advance;
         }

         var2.setLocation((double)var3, (double)var4);
         LayoutPathImpl var8 = this.textLine.getLayoutPath();
         if (var8 != null) {
            var8.pathToPoint(var2, var7 != var6, var2);
         }

      } else {
         throw new NullPointerException((var1 == null ? "hit" : "point") + " can't be null");
      }
   }

   public static class CaretPolicy {
      public TextHitInfo getStrongCaret(TextHitInfo var1, TextHitInfo var2, TextLayout var3) {
         return var3.getStrongHit(var1, var2);
      }
   }
}
