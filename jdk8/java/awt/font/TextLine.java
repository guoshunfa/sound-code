package java.awt.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.Bidi;
import java.text.CharacterIterator;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.BidiUtils;
import sun.font.CoreMetrics;
import sun.font.Decoration;
import sun.font.ExtendedTextLabel;
import sun.font.FontResolver;
import sun.font.GraphicComponent;
import sun.font.LayoutPathImpl;
import sun.font.TextLabelFactory;
import sun.font.TextLineComponent;
import sun.text.CodePointIterator;

final class TextLine {
   private TextLineComponent[] fComponents;
   private float[] fBaselineOffsets;
   private int[] fComponentVisualOrder;
   private float[] locs;
   private char[] fChars;
   private int fCharsStart;
   private int fCharsLimit;
   private int[] fCharVisualOrder;
   private int[] fCharLogicalOrder;
   private byte[] fCharLevels;
   private boolean fIsDirectionLTR;
   private LayoutPathImpl lp;
   private boolean isSimple;
   private Rectangle pixelBounds;
   private FontRenderContext frc;
   private TextLine.TextLineMetrics fMetrics = null;
   private static TextLine.Function fgPosAdvF = new TextLine.Function() {
      float computeFunction(TextLine var1, int var2, int var3) {
         TextLineComponent var4 = var1.fComponents[var2];
         int var5 = var1.getComponentVisualIndex(var2);
         return var1.locs[var5 * 2] + var4.getCharX(var3) + var4.getCharAdvance(var3);
      }
   };
   private static TextLine.Function fgAdvanceF = new TextLine.Function() {
      float computeFunction(TextLine var1, int var2, int var3) {
         TextLineComponent var4 = var1.fComponents[var2];
         return var4.getCharAdvance(var3);
      }
   };
   private static TextLine.Function fgXPositionF = new TextLine.Function() {
      float computeFunction(TextLine var1, int var2, int var3) {
         int var4 = var1.getComponentVisualIndex(var2);
         TextLineComponent var5 = var1.fComponents[var2];
         return var1.locs[var4 * 2] + var5.getCharX(var3);
      }
   };
   private static TextLine.Function fgYPositionF = new TextLine.Function() {
      float computeFunction(TextLine var1, int var2, int var3) {
         TextLineComponent var4 = var1.fComponents[var2];
         float var5 = var4.getCharY(var3);
         return var5 + var1.getComponentShift(var2);
      }
   };

   public TextLine(FontRenderContext var1, TextLineComponent[] var2, float[] var3, char[] var4, int var5, int var6, int[] var7, byte[] var8, boolean var9) {
      int[] var10 = computeComponentOrder(var2, var7);
      this.frc = var1;
      this.fComponents = var2;
      this.fBaselineOffsets = var3;
      this.fComponentVisualOrder = var10;
      this.fChars = var4;
      this.fCharsStart = var5;
      this.fCharsLimit = var6;
      this.fCharLogicalOrder = var7;
      this.fCharLevels = var8;
      this.fIsDirectionLTR = var9;
      this.checkCtorArgs();
      this.init();
   }

   private void checkCtorArgs() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.fComponents.length; ++var2) {
         var1 += this.fComponents[var2].getNumCharacters();
      }

      if (var1 != this.characterCount()) {
         throw new IllegalArgumentException("Invalid TextLine!  char count is different from sum of char counts of components.");
      }
   }

   private void init() {
      float var1 = 0.0F;
      float var2 = 0.0F;
      float var3 = 0.0F;
      float var4 = 0.0F;
      float var5 = 0.0F;
      float var6 = 0.0F;
      boolean var8 = false;
      this.isSimple = true;

      TextLineComponent var7;
      float var13;
      for(int var9 = 0; var9 < this.fComponents.length; ++var9) {
         var7 = this.fComponents[var9];
         this.isSimple &= var7.isSimple();
         CoreMetrics var10 = var7.getCoreMetrics();
         byte var11 = (byte)var10.baselineIndex;
         float var12;
         if (var11 >= 0) {
            var12 = this.fBaselineOffsets[var11];
            var1 = Math.max(var1, -var12 + var10.ascent);
            var13 = var12 + var10.descent;
            var2 = Math.max(var2, var13);
            var3 = Math.max(var3, var13 + var10.leading);
         } else {
            var8 = true;
            var12 = var10.ascent + var10.descent;
            var13 = var12 + var10.leading;
            var5 = Math.max(var5, var12);
            var6 = Math.max(var6, var13);
         }
      }

      if (var8) {
         if (var5 > var1 + var2) {
            var2 = var5 - var1;
         }

         if (var6 > var1 + var3) {
            var3 = var6 - var1;
         }
      }

      var3 -= var2;
      if (var8) {
         this.fBaselineOffsets = new float[]{this.fBaselineOffsets[0], this.fBaselineOffsets[1], this.fBaselineOffsets[2], var2, -var1};
      }

      float var31 = 0.0F;
      float var32 = 0.0F;
      CoreMetrics var33 = null;
      boolean var34 = false;
      this.locs = new float[this.fComponents.length * 2 + 2];
      int var35 = 0;

      float var16;
      for(int var14 = 0; var35 < this.fComponents.length; var14 += 2) {
         var7 = this.fComponents[this.getComponentLogicalIndex(var35)];
         CoreMetrics var15 = var7.getCoreMetrics();
         if (var33 == null || var33.italicAngle == 0.0F && var15.italicAngle == 0.0F || var33.italicAngle == var15.italicAngle && var33.baselineIndex == var15.baselineIndex && var33.ssOffset == var15.ssOffset) {
            var32 = var15.effectiveBaselineOffset(this.fBaselineOffsets);
         } else {
            var16 = var33.effectiveBaselineOffset(this.fBaselineOffsets);
            float var17 = var16 - var33.ascent;
            float var18 = var16 + var33.descent;
            float var19 = var15.effectiveBaselineOffset(this.fBaselineOffsets);
            float var20 = var19 - var15.ascent;
            float var21 = var19 + var15.descent;
            float var22 = Math.max(var17, var20);
            float var23 = Math.min(var18, var21);
            float var24 = var33.italicAngle * (var16 - var22);
            float var25 = var33.italicAngle * (var16 - var23);
            float var26 = var15.italicAngle * (var19 - var22);
            float var27 = var15.italicAngle * (var19 - var23);
            float var28 = var24 - var26;
            float var29 = var25 - var27;
            float var30 = Math.max(var28, var29);
            var31 += var30;
            var32 = var19;
         }

         this.locs[var14] = var31;
         this.locs[var14 + 1] = var32;
         var31 += var7.getAdvance();
         var33 = var15;
         var34 |= var7.getBaselineTransform() != null;
         ++var35;
      }

      if (var33.italicAngle != 0.0F) {
         var13 = var33.effectiveBaselineOffset(this.fBaselineOffsets);
         float var10000 = var13 - var33.ascent;
         var10000 = var13 + var33.descent;
         var13 += var33.ssOffset;
         if (var33.italicAngle > 0.0F) {
            var16 = var13 + var33.ascent;
         } else {
            var16 = var13 - var33.descent;
         }

         var16 *= var33.italicAngle;
         var31 += var16;
      }

      this.locs[this.locs.length - 2] = var31;
      this.fMetrics = new TextLine.TextLineMetrics(var1, var2, var3, var31);
      if (var34) {
         this.isSimple = false;
         Point2D.Double var37 = new Point2D.Double();
         double var36 = 0.0D;
         double var38 = 0.0D;
         LayoutPathImpl.SegmentPathBuilder var39 = new LayoutPathImpl.SegmentPathBuilder();
         var39.moveTo((double)this.locs[0], 0.0D);
         int var40 = 0;

         for(int var42 = 0; var40 < this.fComponents.length; var42 += 2) {
            var7 = this.fComponents[this.getComponentLogicalIndex(var40)];
            AffineTransform var43 = var7.getBaselineTransform();
            if (var43 != null && (var43.getType() & 1) != 0) {
               double var44 = var43.getTranslateX();
               double var45 = var43.getTranslateY();
               var39.moveTo(var36 += var44, var38 += var45);
            }

            var37.x = (double)(this.locs[var42 + 2] - this.locs[var42]);
            var37.y = 0.0D;
            if (var43 != null) {
               var43.deltaTransform(var37, var37);
            }

            var39.lineTo(var36 += var37.x, var38 += var37.y);
            ++var40;
         }

         this.lp = var39.complete();
         if (this.lp == null) {
            var7 = this.fComponents[this.getComponentLogicalIndex(0)];
            AffineTransform var41 = var7.getBaselineTransform();
            if (var41 != null) {
               this.lp = new LayoutPathImpl.EmptyPath(var41);
            }
         }
      }

   }

   public Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3) {
      Rectangle var4 = null;
      if (var1 != null && var1.equals(this.frc)) {
         var1 = null;
      }

      int var5 = (int)Math.floor((double)var2);
      int var6 = (int)Math.floor((double)var3);
      float var7 = var2 - (float)var5;
      float var8 = var3 - (float)var6;
      boolean var9 = var1 == null && var7 == 0.0F && var8 == 0.0F;
      if (var9 && this.pixelBounds != null) {
         var4 = new Rectangle(this.pixelBounds);
         var4.x += var5;
         var4.y += var6;
         return var4;
      } else {
         if (this.isSimple) {
            int var10 = 0;

            for(int var11 = 0; var10 < this.fComponents.length; var11 += 2) {
               TextLineComponent var12 = this.fComponents[this.getComponentLogicalIndex(var10)];
               Rectangle var13 = var12.getPixelBounds(var1, this.locs[var11] + var7, this.locs[var11 + 1] + var8);
               if (!var13.isEmpty()) {
                  if (var4 == null) {
                     var4 = var13;
                  } else {
                     var4.add(var13);
                  }
               }

               ++var10;
            }

            if (var4 == null) {
               var4 = new Rectangle(0, 0, 0, 0);
            }
         } else {
            Object var15 = this.getVisualBounds();
            if (this.lp != null) {
               var15 = this.lp.mapShape((Shape)var15).getBounds();
            }

            Rectangle var16 = ((Rectangle2D)var15).getBounds();
            BufferedImage var17 = new BufferedImage(var16.width + 6, var16.height + 6, 2);
            Graphics2D var14 = var17.createGraphics();
            var14.setColor(Color.WHITE);
            var14.fillRect(0, 0, var17.getWidth(), var17.getHeight());
            var14.setColor(Color.BLACK);
            this.draw(var14, var7 + 3.0F - (float)var16.x, var8 + 3.0F - (float)var16.y);
            var4 = computePixelBounds(var17);
            var4.x -= 3 - var16.x;
            var4.y -= 3 - var16.y;
         }

         if (var9) {
            this.pixelBounds = new Rectangle(var4);
         }

         var4.x += var5;
         var4.y += var6;
         return var4;
      }
   }

   static Rectangle computePixelBounds(BufferedImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      int var3 = -1;
      int var4 = -1;
      int var5 = var1;
      int var6 = var2;
      int[] var7 = new int[var1];

      int var8;
      label80:
      while(true) {
         ++var4;
         if (var4 >= var2) {
            break;
         }

         var0.getRGB(0, var4, var7.length, 1, var7, 0, var1);

         for(var8 = 0; var8 < var7.length; ++var8) {
            if (var7[var8] != -1) {
               break label80;
            }
         }
      }

      var7 = new int[var1];

      label68:
      while(true) {
         --var6;
         if (var6 <= var4) {
            break;
         }

         var0.getRGB(0, var6, var7.length, 1, var7, 0, var1);

         for(var8 = 0; var8 < var7.length; ++var8) {
            if (var7[var8] != -1) {
               break label68;
            }
         }
      }

      ++var6;

      int var9;
      label56:
      while(true) {
         ++var3;
         if (var3 >= var5) {
            break;
         }

         for(var9 = var4; var9 < var6; ++var9) {
            var8 = var0.getRGB(var3, var9);
            if (var8 != -1) {
               break label56;
            }
         }
      }

      label45:
      while(true) {
         --var5;
         if (var5 <= var3) {
            break;
         }

         for(var9 = var4; var9 < var6; ++var9) {
            var8 = var0.getRGB(var5, var9);
            if (var8 != -1) {
               break label45;
            }
         }
      }

      ++var5;
      return new Rectangle(var3, var4, var5 - var3, var6 - var4);
   }

   public int characterCount() {
      return this.fCharsLimit - this.fCharsStart;
   }

   public boolean isDirectionLTR() {
      return this.fIsDirectionLTR;
   }

   public TextLine.TextLineMetrics getMetrics() {
      return this.fMetrics;
   }

   public int visualToLogical(int var1) {
      if (this.fCharLogicalOrder == null) {
         return var1;
      } else {
         if (this.fCharVisualOrder == null) {
            this.fCharVisualOrder = BidiUtils.createInverseMap(this.fCharLogicalOrder);
         }

         return this.fCharVisualOrder[var1];
      }
   }

   public int logicalToVisual(int var1) {
      return this.fCharLogicalOrder == null ? var1 : this.fCharLogicalOrder[var1];
   }

   public byte getCharLevel(int var1) {
      return this.fCharLevels == null ? 0 : this.fCharLevels[var1];
   }

   public boolean isCharLTR(int var1) {
      return (this.getCharLevel(var1) & 1) == 0;
   }

   public int getCharType(int var1) {
      return Character.getType(this.fChars[var1 + this.fCharsStart]);
   }

   public boolean isCharSpace(int var1) {
      return Character.isSpaceChar(this.fChars[var1 + this.fCharsStart]);
   }

   public boolean isCharWhitespace(int var1) {
      return Character.isWhitespace(this.fChars[var1 + this.fCharsStart]);
   }

   public float getCharAngle(int var1) {
      return this.getCoreMetricsAt(var1).italicAngle;
   }

   public CoreMetrics getCoreMetricsAt(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative logicalIndex.");
      } else if (var1 > this.fCharsLimit - this.fCharsStart) {
         throw new IllegalArgumentException("logicalIndex too large.");
      } else {
         int var2 = 0;
         boolean var3 = false;
         int var4 = 0;

         do {
            var4 += this.fComponents[var2].getNumCharacters();
            if (var4 > var1) {
               break;
            }

            ++var2;
         } while(var2 < this.fComponents.length);

         return this.fComponents[var2].getCoreMetrics();
      }
   }

   public float getCharAscent(int var1) {
      return this.getCoreMetricsAt(var1).ascent;
   }

   public float getCharDescent(int var1) {
      return this.getCoreMetricsAt(var1).descent;
   }

   public float getCharShift(int var1) {
      return this.getCoreMetricsAt(var1).ssOffset;
   }

   private float applyFunctionAtIndex(int var1, TextLine.Function var2) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative logicalIndex.");
      } else {
         int var3 = 0;

         for(int var4 = 0; var4 < this.fComponents.length; ++var4) {
            int var5 = var3 + this.fComponents[var4].getNumCharacters();
            if (var5 > var1) {
               return var2.computeFunction(this, var4, var1 - var3);
            }

            var3 = var5;
         }

         throw new IllegalArgumentException("logicalIndex too large.");
      }
   }

   public float getCharAdvance(int var1) {
      return this.applyFunctionAtIndex(var1, fgAdvanceF);
   }

   public float getCharXPosition(int var1) {
      return this.applyFunctionAtIndex(var1, fgXPositionF);
   }

   public float getCharYPosition(int var1) {
      return this.applyFunctionAtIndex(var1, fgYPositionF);
   }

   public float getCharLinePosition(int var1) {
      return this.getCharXPosition(var1);
   }

   public float getCharLinePosition(int var1, boolean var2) {
      TextLine.Function var3 = this.isCharLTR(var1) == var2 ? fgXPositionF : fgPosAdvF;
      return this.applyFunctionAtIndex(var1, var3);
   }

   public boolean caretAtOffsetIsValid(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative offset.");
      } else {
         int var2 = 0;

         for(int var3 = 0; var3 < this.fComponents.length; ++var3) {
            int var4 = var2 + this.fComponents[var3].getNumCharacters();
            if (var4 > var1) {
               return this.fComponents[var3].caretAtOffsetIsValid(var1 - var2);
            }

            var2 = var4;
         }

         throw new IllegalArgumentException("logicalIndex too large.");
      }
   }

   private int getComponentLogicalIndex(int var1) {
      return this.fComponentVisualOrder == null ? var1 : this.fComponentVisualOrder[var1];
   }

   private int getComponentVisualIndex(int var1) {
      if (this.fComponentVisualOrder == null) {
         return var1;
      } else {
         for(int var2 = 0; var2 < this.fComponentVisualOrder.length; ++var2) {
            if (this.fComponentVisualOrder[var2] == var1) {
               return var2;
            }
         }

         throw new IndexOutOfBoundsException("bad component index: " + var1);
      }
   }

   public Rectangle2D getCharBounds(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Negative logicalIndex.");
      } else {
         int var2 = 0;

         for(int var3 = 0; var3 < this.fComponents.length; ++var3) {
            int var4 = var2 + this.fComponents[var3].getNumCharacters();
            if (var4 > var1) {
               TextLineComponent var5 = this.fComponents[var3];
               int var6 = var1 - var2;
               Rectangle2D var7 = var5.getCharVisualBounds(var6);
               int var8 = this.getComponentVisualIndex(var3);
               var7.setRect(var7.getX() + (double)this.locs[var8 * 2], var7.getY() + (double)this.locs[var8 * 2 + 1], var7.getWidth(), var7.getHeight());
               return var7;
            }

            var2 = var4;
         }

         throw new IllegalArgumentException("logicalIndex too large.");
      }
   }

   private float getComponentShift(int var1) {
      CoreMetrics var2 = this.fComponents[var1].getCoreMetrics();
      return var2.effectiveBaselineOffset(this.fBaselineOffsets);
   }

   public void draw(Graphics2D var1, float var2, float var3) {
      if (this.lp == null) {
         int var4 = 0;

         for(int var5 = 0; var4 < this.fComponents.length; var5 += 2) {
            TextLineComponent var6 = this.fComponents[this.getComponentLogicalIndex(var4)];
            var6.draw(var1, this.locs[var5] + var2, this.locs[var5 + 1] + var3);
            ++var4;
         }
      } else {
         AffineTransform var10 = var1.getTransform();
         Point2D.Float var11 = new Point2D.Float();
         int var12 = 0;

         for(int var7 = 0; var12 < this.fComponents.length; var7 += 2) {
            TextLineComponent var8 = this.fComponents[this.getComponentLogicalIndex(var12)];
            this.lp.pathToPoint((double)this.locs[var7], (double)this.locs[var7 + 1], false, var11);
            var11.x += var2;
            var11.y += var3;
            AffineTransform var9 = var8.getBaselineTransform();
            if (var9 != null) {
               var1.translate((double)var11.x - var9.getTranslateX(), (double)var11.y - var9.getTranslateY());
               var1.transform(var9);
               var8.draw(var1, 0.0F, 0.0F);
               var1.setTransform(var10);
            } else {
               var8.draw(var1, var11.x, var11.y);
            }

            ++var12;
         }
      }

   }

   public Rectangle2D getVisualBounds() {
      Object var1 = null;
      int var2 = 0;

      for(int var3 = 0; var2 < this.fComponents.length; var3 += 2) {
         TextLineComponent var4 = this.fComponents[this.getComponentLogicalIndex(var2)];
         Rectangle2D var5 = var4.getVisualBounds();
         Point2D.Float var6 = new Point2D.Float(this.locs[var3], this.locs[var3 + 1]);
         if (this.lp == null) {
            var5.setRect(var5.getMinX() + (double)var6.x, var5.getMinY() + (double)var6.y, var5.getWidth(), var5.getHeight());
         } else {
            this.lp.pathToPoint(var6, false, var6);
            AffineTransform var7 = var4.getBaselineTransform();
            if (var7 != null) {
               AffineTransform var8 = AffineTransform.getTranslateInstance((double)var6.x - var7.getTranslateX(), (double)var6.y - var7.getTranslateY());
               var8.concatenate(var7);
               var5 = var8.createTransformedShape(var5).getBounds2D();
            } else {
               var5.setRect(var5.getMinX() + (double)var6.x, var5.getMinY() + (double)var6.y, var5.getWidth(), var5.getHeight());
            }
         }

         if (var1 == null) {
            var1 = var5;
         } else {
            ((Rectangle2D)var1).add(var5);
         }

         ++var2;
      }

      if (var1 == null) {
         var1 = new Rectangle2D.Float(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
      }

      return (Rectangle2D)var1;
   }

   public Rectangle2D getItalicBounds() {
      float var1 = Float.MAX_VALUE;
      float var2 = -3.4028235E38F;
      float var3 = Float.MAX_VALUE;
      float var4 = -3.4028235E38F;
      int var5 = 0;

      for(int var6 = 0; var5 < this.fComponents.length; var6 += 2) {
         TextLineComponent var7 = this.fComponents[this.getComponentLogicalIndex(var5)];
         Rectangle2D var8 = var7.getItalicBounds();
         float var9 = this.locs[var6];
         float var10 = this.locs[var6 + 1];
         var1 = Math.min(var1, var9 + (float)var8.getX());
         var2 = Math.max(var2, var9 + (float)var8.getMaxX());
         var3 = Math.min(var3, var10 + (float)var8.getY());
         var4 = Math.max(var4, var10 + (float)var8.getMaxY());
         ++var5;
      }

      return new Rectangle2D.Float(var1, var3, var2 - var1, var4 - var3);
   }

   public Shape getOutline(AffineTransform var1) {
      GeneralPath var2 = new GeneralPath(1);
      int var3 = 0;

      for(int var4 = 0; var3 < this.fComponents.length; var4 += 2) {
         TextLineComponent var5 = this.fComponents[this.getComponentLogicalIndex(var3)];
         var2.append(var5.getOutline(this.locs[var4], this.locs[var4 + 1]), false);
         ++var3;
      }

      if (var1 != null) {
         var2.transform(var1);
      }

      return var2;
   }

   public int hashCode() {
      return this.fComponents.length << 16 ^ this.fComponents[0].hashCode() << 3 ^ this.fCharsLimit - this.fCharsStart;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < this.fComponents.length; ++var2) {
         var1.append((Object)this.fComponents[var2]);
      }

      return var1.toString();
   }

   public static TextLine fastCreateTextLine(FontRenderContext var0, char[] var1, Font var2, CoreMetrics var3, Map<? extends AttributedCharacterIterator.Attribute, ?> var4) {
      boolean var5 = true;
      byte[] var6 = null;
      int[] var7 = null;
      Bidi var8 = null;
      int var9 = var1.length;
      boolean var10 = false;
      byte[] var11 = null;
      AttributeValues var12 = null;
      if (var4 != null) {
         var12 = AttributeValues.fromMap(var4);
         if (var12.getRunDirection() >= 0) {
            var5 = var12.getRunDirection() == 0;
            var10 = !var5;
         }

         if (var12.getBidiEmbedding() != 0) {
            var10 = true;
            byte var13 = (byte)var12.getBidiEmbedding();
            var11 = new byte[var9];

            for(int var14 = 0; var14 < var11.length; ++var14) {
               var11[var14] = var13;
            }
         }
      }

      if (!var10) {
         var10 = Bidi.requiresBidi(var1, 0, var1.length);
      }

      if (var10) {
         int var19 = var12 == null ? -2 : var12.getRunDirection();
         var8 = new Bidi(var1, 0, var11, 0, var1.length, var19);
         if (!var8.isLeftToRight()) {
            var6 = BidiUtils.getLevels(var8);
            int[] var21 = BidiUtils.createVisualToLogicalMap(var6);
            var7 = BidiUtils.createInverseMap(var21);
            var5 = var8.baseIsLeftToRight();
         }
      }

      Decoration var20 = Decoration.getDecoration(var12);
      byte var22 = 0;
      TextLabelFactory var15 = new TextLabelFactory(var0, var1, var8, var22);
      TextLineComponent[] var16 = new TextLineComponent[1];
      var16 = createComponentsOnRun(0, var1.length, var1, var7, var6, var15, var2, var3, var0, var20, var16, 0);

      int var17;
      for(var17 = var16.length; var16[var17 - 1] == null; --var17) {
      }

      if (var17 != var16.length) {
         TextLineComponent[] var18 = new TextLineComponent[var17];
         System.arraycopy(var16, 0, var18, 0, var17);
         var16 = var18;
      }

      return new TextLine(var0, var16, var3.baselineOffsets, var1, 0, var1.length, var7, var6, var5);
   }

   private static TextLineComponent[] expandArray(TextLineComponent[] var0) {
      TextLineComponent[] var1 = new TextLineComponent[var0.length + 8];
      System.arraycopy(var0, 0, var1, 0, var0.length);
      return var1;
   }

   public static TextLineComponent[] createComponentsOnRun(int var0, int var1, char[] var2, int[] var3, byte[] var4, TextLabelFactory var5, Font var6, CoreMetrics var7, FontRenderContext var8, Decoration var9, TextLineComponent[] var10, int var11) {
      int var12 = var0;

      do {
         int var13 = firstVisualChunk(var3, var4, var12, var1);

         do {
            int var15;
            if (var7 == null) {
               LineMetrics var16 = var6.getLineMetrics(var2, var12, var13, var8);
               var7 = CoreMetrics.get(var16);
               var15 = var16.getNumChars();
            } else {
               var15 = var13 - var12;
            }

            ExtendedTextLabel var17 = var5.createExtended(var6, var7, var9, var12, var12 + var15);
            ++var11;
            if (var11 >= var10.length) {
               var10 = expandArray(var10);
            }

            var10[var11 - 1] = var17;
            var12 += var15;
         } while(var12 < var13);
      } while(var12 < var1);

      return var10;
   }

   public static TextLineComponent[] getComponents(StyledParagraph var0, char[] var1, int var2, int var3, int[] var4, byte[] var5, TextLabelFactory var6) {
      FontRenderContext var7 = var6.getFontRenderContext();
      int var8 = 0;
      TextLineComponent[] var9 = new TextLineComponent[1];
      int var10 = var2;

      do {
         int var11 = Math.min(var0.getRunLimit(var10), var3);
         Decoration var12 = var0.getDecorationAt(var10);
         Object var13 = var0.getFontOrGraphicAt(var10);
         Font var14;
         if (var13 instanceof GraphicAttribute) {
            var14 = null;
            GraphicAttribute var15 = (GraphicAttribute)var13;

            int var16;
            do {
               var16 = firstVisualChunk(var4, var5, var10, var11);
               GraphicComponent var17 = new GraphicComponent(var15, var12, var4, var5, var10, var16, var14);
               var10 = var16;
               ++var8;
               if (var8 >= var9.length) {
                  var9 = expandArray(var9);
               }

               var9[var8 - 1] = var17;
            } while(var16 < var11);
         } else {
            var14 = (Font)var13;
            var9 = createComponentsOnRun(var10, var11, var1, var4, var5, var6, var14, (CoreMetrics)null, var7, var12, var9, var8);
            var10 = var11;

            for(var8 = var9.length; var9[var8 - 1] == null; --var8) {
            }
         }
      } while(var10 < var3);

      TextLineComponent[] var18;
      if (var9.length == var8) {
         var18 = var9;
      } else {
         var18 = new TextLineComponent[var8];
         System.arraycopy(var9, 0, var18, 0, var8);
      }

      return var18;
   }

   public static TextLine createLineFromText(char[] var0, StyledParagraph var1, TextLabelFactory var2, boolean var3, float[] var4) {
      var2.setLineContext(0, var0.length);
      Bidi var5 = var2.getLineBidi();
      int[] var6 = null;
      byte[] var7 = null;
      if (var5 != null) {
         var7 = BidiUtils.getLevels(var5);
         int[] var8 = BidiUtils.createVisualToLogicalMap(var7);
         var6 = BidiUtils.createInverseMap(var8);
      }

      TextLineComponent[] var9 = getComponents(var1, var0, 0, var0.length, var6, var7, var2);
      return new TextLine(var2.getFontRenderContext(), var9, var4, var0, 0, var0.length, var6, var7, var3);
   }

   private static int[] computeComponentOrder(TextLineComponent[] var0, int[] var1) {
      int[] var2 = null;
      if (var1 != null && var0.length > 1) {
         var2 = new int[var0.length];
         int var3 = 0;

         for(int var4 = 0; var4 < var0.length; ++var4) {
            var2[var4] = var1[var3];
            var3 += var0[var4].getNumCharacters();
         }

         var2 = BidiUtils.createContiguousOrder(var2);
         var2 = BidiUtils.createInverseMap(var2);
      }

      return var2;
   }

   public static TextLine standardCreateTextLine(FontRenderContext var0, AttributedCharacterIterator var1, char[] var2, float[] var3) {
      StyledParagraph var4 = new StyledParagraph(var1, var2);
      Bidi var5 = new Bidi(var1);
      if (var5.isLeftToRight()) {
         var5 = null;
      }

      byte var6 = 0;
      TextLabelFactory var7 = new TextLabelFactory(var0, var2, var5, var6);
      boolean var8 = true;
      if (var5 != null) {
         var8 = var5.baseIsLeftToRight();
      }

      return createLineFromText(var2, var4, var7, var8, var3);
   }

   static boolean advanceToFirstFont(AttributedCharacterIterator var0) {
      for(char var1 = var0.first(); var1 != '\uffff'; var1 = var0.setIndex(var0.getRunLimit())) {
         if (var0.getAttribute(TextAttribute.CHAR_REPLACEMENT) == null) {
            return true;
         }
      }

      return false;
   }

   static float[] getNormalizedOffsets(float[] var0, byte var1) {
      if (var0[var1] != 0.0F) {
         float var2 = var0[var1];
         float[] var3 = new float[var0.length];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = var0[var4] - var2;
         }

         var0 = var3;
      }

      return var0;
   }

   static Font getFontAtCurrentPos(AttributedCharacterIterator var0) {
      Object var1 = var0.getAttribute(TextAttribute.FONT);
      if (var1 != null) {
         return (Font)var1;
      } else if (var0.getAttribute(TextAttribute.FAMILY) != null) {
         return Font.getFont(var0.getAttributes());
      } else {
         int var2 = CodePointIterator.create((CharacterIterator)var0).next();
         if (var2 != -1) {
            FontResolver var3 = FontResolver.getInstance();
            return var3.getFont(var3.getFontIndex(var2), var0.getAttributes());
         } else {
            return null;
         }
      }
   }

   private static int firstVisualChunk(int[] var0, byte[] var1, int var2, int var3) {
      if (var0 != null && var1 != null) {
         byte var4 = var1[var2];

         do {
            ++var2;
         } while(var2 < var3 && var1[var2] == var4);

         return var2;
      } else {
         return var3;
      }
   }

   public TextLine getJustifiedLine(float var1, float var2, int var3, int var4) {
      TextLineComponent[] var5 = new TextLineComponent[this.fComponents.length];
      System.arraycopy(this.fComponents, 0, var5, 0, this.fComponents.length);
      float var6 = 0.0F;
      float var7 = 0.0F;
      float var8 = 0.0F;
      boolean var9 = false;

      do {
         getAdvanceBetween(var5, 0, this.characterCount());
         float var10 = getAdvanceBetween(var5, var3, var4);
         var8 = (var1 - var10) * var2;
         int[] var11 = new int[var5.length];
         int var12 = 0;

         for(int var13 = 0; var13 < var5.length; ++var13) {
            int var14 = this.getComponentLogicalIndex(var13);
            var11[var14] = var12;
            var12 += var5[var14].getNumJustificationInfos();
         }

         GlyphJustificationInfo[] var28 = new GlyphJustificationInfo[var12];
         byte var29 = 0;

         int var15;
         for(var15 = 0; var15 < var5.length; ++var15) {
            TextLineComponent var16 = var5[var15];
            int var17 = var16.getNumCharacters();
            int var18 = var29 + var17;
            if (var18 > var3) {
               int var19 = Math.max(0, var3 - var29);
               int var20 = Math.min(var17, var4 - var29);
               var16.getJustificationInfos(var28, var11[var15], var19, var20);
               if (var18 >= var4) {
                  break;
               }
            }
         }

         var15 = 0;

         int var30;
         for(var30 = var12; var15 < var30 && var28[var15] == null; ++var15) {
         }

         while(var30 > var15 && var28[var30 - 1] == null) {
            --var30;
         }

         TextJustifier var31 = new TextJustifier(var28, var15, var30);
         float[] var32 = var31.justify(var8);
         boolean var33 = !var9;
         boolean var34 = false;
         boolean[] var21 = new boolean[1];
         var29 = 0;

         for(int var22 = 0; var22 < var5.length; ++var22) {
            TextLineComponent var23 = var5[var22];
            int var24 = var23.getNumCharacters();
            int var25 = var29 + var24;
            if (var25 > var3) {
               int var26 = Math.max(0, var3 - var29);
               Math.min(var24, var4 - var29);
               var5[var22] = var23.applyJustificationDeltas(var32, var11[var22] * 2, var21);
               var34 |= var21[0];
               if (var25 >= var4) {
                  break;
               }
            }
         }

         var9 = var34 && !var9;
      } while(var9);

      return new TextLine(this.frc, var5, this.fBaselineOffsets, this.fChars, this.fCharsStart, this.fCharsLimit, this.fCharLogicalOrder, this.fCharLevels, this.fIsDirectionLTR);
   }

   public static float getAdvanceBetween(TextLineComponent[] var0, int var1, int var2) {
      float var3 = 0.0F;
      int var4 = 0;

      for(int var5 = 0; var5 < var0.length; ++var5) {
         TextLineComponent var6 = var0[var5];
         int var7 = var6.getNumCharacters();
         int var8 = var4 + var7;
         if (var8 > var1) {
            int var9 = Math.max(0, var1 - var4);
            int var10 = Math.min(var7, var2 - var4);
            var3 += var6.getAdvanceBetween(var9, var10);
            if (var8 >= var2) {
               break;
            }
         }

         var4 = var8;
      }

      return var3;
   }

   LayoutPathImpl getLayoutPath() {
      return this.lp;
   }

   private abstract static class Function {
      private Function() {
      }

      abstract float computeFunction(TextLine var1, int var2, int var3);

      // $FF: synthetic method
      Function(Object var1) {
         this();
      }
   }

   static final class TextLineMetrics {
      public final float ascent;
      public final float descent;
      public final float leading;
      public final float advance;

      public TextLineMetrics(float var1, float var2, float var3, float var4) {
         this.ascent = var1;
         this.descent = var2;
         this.leading = var3;
         this.advance = var4;
      }
   }
}
