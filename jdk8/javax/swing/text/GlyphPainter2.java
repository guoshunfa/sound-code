package javax.swing.text;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

class GlyphPainter2 extends GlyphView.GlyphPainter {
   TextLayout layout;

   public GlyphPainter2(TextLayout var1) {
      this.layout = var1;
   }

   public GlyphView.GlyphPainter getPainter(GlyphView var1, int var2, int var3) {
      return null;
   }

   public float getSpan(GlyphView var1, int var2, int var3, TabExpander var4, float var5) {
      if (var2 == var1.getStartOffset() && var3 == var1.getEndOffset()) {
         return this.layout.getAdvance();
      } else {
         int var6 = var1.getStartOffset();
         int var7 = var2 - var6;
         int var8 = var3 - var6;
         TextHitInfo var9 = TextHitInfo.afterOffset(var7);
         TextHitInfo var10 = TextHitInfo.beforeOffset(var8);
         float[] var11 = this.layout.getCaretInfo(var9);
         float var12 = var11[0];
         var11 = this.layout.getCaretInfo(var10);
         float var13 = var11[0];
         return var13 > var12 ? var13 - var12 : var12 - var13;
      }
   }

   public float getHeight(GlyphView var1) {
      return this.layout.getAscent() + this.layout.getDescent() + this.layout.getLeading();
   }

   public float getAscent(GlyphView var1) {
      return this.layout.getAscent();
   }

   public float getDescent(GlyphView var1) {
      return this.layout.getDescent();
   }

   public void paint(GlyphView var1, Graphics var2, Shape var3, int var4, int var5) {
      if (var2 instanceof Graphics2D) {
         Rectangle2D var6 = var3.getBounds2D();
         Graphics2D var7 = (Graphics2D)var2;
         float var8 = (float)var6.getY() + this.layout.getAscent() + this.layout.getLeading();
         float var9 = (float)var6.getX();
         if (var4 <= var1.getStartOffset() && var5 >= var1.getEndOffset()) {
            this.layout.draw(var7, var9, var8);
         } else {
            try {
               Shape var10 = var1.modelToView(var4, Position.Bias.Forward, var5, Position.Bias.Backward, var3);
               Shape var11 = var2.getClip();
               var7.clip(var10);
               this.layout.draw(var7, var9, var8);
               var2.setClip(var11);
            } catch (BadLocationException var12) {
            }
         }
      }

   }

   public Shape modelToView(GlyphView var1, int var2, Position.Bias var3, Shape var4) throws BadLocationException {
      int var5 = var2 - var1.getStartOffset();
      Rectangle2D var6 = var4.getBounds2D();
      TextHitInfo var7 = var3 == Position.Bias.Forward ? TextHitInfo.afterOffset(var5) : TextHitInfo.beforeOffset(var5);
      float[] var8 = this.layout.getCaretInfo(var7);
      var6.setRect(var6.getX() + (double)var8[0], var6.getY(), 1.0D, var6.getHeight());
      return var6;
   }

   public int viewToModel(GlyphView var1, float var2, float var3, Shape var4, Position.Bias[] var5) {
      Rectangle2D var6 = var4 instanceof Rectangle2D ? (Rectangle2D)var4 : var4.getBounds2D();
      TextHitInfo var7 = this.layout.hitTestChar(var2 - (float)var6.getX(), 0.0F);
      int var8 = var7.getInsertionIndex();
      if (var8 == var1.getEndOffset()) {
         --var8;
      }

      var5[0] = var7.isLeadingEdge() ? Position.Bias.Forward : Position.Bias.Backward;
      return var8 + var1.getStartOffset();
   }

   public int getBoundedPosition(GlyphView var1, int var2, float var3, float var4) {
      if (var4 < 0.0F) {
         throw new IllegalArgumentException("Length must be >= 0.");
      } else {
         TextHitInfo var5;
         if (this.layout.isLeftToRight()) {
            var5 = this.layout.hitTestChar(var4, 0.0F);
         } else {
            var5 = this.layout.hitTestChar(this.layout.getAdvance() - var4, 0.0F);
         }

         return var1.getStartOffset() + var5.getCharIndex();
      }
   }

   public int getNextVisualPositionFrom(GlyphView var1, int var2, Position.Bias var3, Shape var4, int var5, Position.Bias[] var6) throws BadLocationException {
      Document var7 = var1.getDocument();
      int var8 = var1.getStartOffset();
      int var9 = var1.getEndOffset();
      Segment var10;
      boolean var11;
      TextHitInfo var12;
      TextHitInfo var13;
      char var14;
      switch(var5) {
      case 1:
      case 5:
         return var2;
      case 2:
      case 4:
      case 6:
      default:
         throw new IllegalArgumentException("Bad direction: " + var5);
      case 3:
         var11 = AbstractDocument.isLeftToRight(var7, var8, var9);
         if (var8 == var7.getLength()) {
            if (var2 == -1) {
               var6[0] = Position.Bias.Forward;
               return var8;
            }

            return -1;
         } else if (var2 == -1) {
            if (var11) {
               var6[0] = Position.Bias.Forward;
               return var8;
            } else {
               var10 = var1.getText(var9 - 1, var9);
               var14 = var10.array[var10.offset];
               SegmentCache.releaseSharedSegment(var10);
               if (var14 == '\n') {
                  var6[0] = Position.Bias.Forward;
                  return var9 - 1;
               }

               var6[0] = Position.Bias.Backward;
               return var9;
            }
         } else {
            if (var3 == Position.Bias.Forward) {
               var12 = TextHitInfo.afterOffset(var2 - var8);
            } else {
               var12 = TextHitInfo.beforeOffset(var2 - var8);
            }

            var13 = this.layout.getNextRightHit(var12);
            if (var13 == null) {
               return -1;
            } else {
               if (var11 != this.layout.isLeftToRight()) {
                  var13 = this.layout.getVisualOtherHit(var13);
               }

               var2 = var13.getInsertionIndex() + var8;
               if (var2 == var9) {
                  var10 = var1.getText(var9 - 1, var9);
                  var14 = var10.array[var10.offset];
                  SegmentCache.releaseSharedSegment(var10);
                  if (var14 == '\n') {
                     return -1;
                  }

                  var6[0] = Position.Bias.Backward;
               } else {
                  var6[0] = Position.Bias.Forward;
               }

               return var2;
            }
         }
      case 7:
         var11 = AbstractDocument.isLeftToRight(var7, var8, var9);
         if (var8 == var7.getLength()) {
            if (var2 == -1) {
               var6[0] = Position.Bias.Forward;
               return var8;
            } else {
               return -1;
            }
         } else if (var2 == -1) {
            if (var11) {
               var10 = var1.getText(var9 - 1, var9);
               var14 = var10.array[var10.offset];
               SegmentCache.releaseSharedSegment(var10);
               if (var14 != '\n' && !Character.isSpaceChar(var14)) {
                  var6[0] = Position.Bias.Backward;
                  return var9;
               } else {
                  var6[0] = Position.Bias.Forward;
                  return var9 - 1;
               }
            } else {
               var6[0] = Position.Bias.Forward;
               return var8;
            }
         } else {
            if (var3 == Position.Bias.Forward) {
               var12 = TextHitInfo.afterOffset(var2 - var8);
            } else {
               var12 = TextHitInfo.beforeOffset(var2 - var8);
            }

            var13 = this.layout.getNextLeftHit(var12);
            if (var13 == null) {
               return -1;
            } else {
               if (var11 != this.layout.isLeftToRight()) {
                  var13 = this.layout.getVisualOtherHit(var13);
               }

               var2 = var13.getInsertionIndex() + var8;
               if (var2 == var9) {
                  var10 = var1.getText(var9 - 1, var9);
                  var14 = var10.array[var10.offset];
                  SegmentCache.releaseSharedSegment(var10);
                  if (var14 == '\n') {
                     return -1;
                  }

                  var6[0] = Position.Bias.Backward;
               } else {
                  var6[0] = Position.Bias.Forward;
               }

               return var2;
            }
         }
      }
   }
}
