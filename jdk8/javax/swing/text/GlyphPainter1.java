package javax.swing.text;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;

class GlyphPainter1 extends GlyphView.GlyphPainter {
   FontMetrics metrics;

   public float getSpan(GlyphView var1, int var2, int var3, TabExpander var4, float var5) {
      this.sync(var1);
      Segment var6 = var1.getText(var2, var3);
      int[] var7 = this.getJustificationData(var1);
      int var8 = Utilities.getTabbedTextWidth(var1, var6, this.metrics, (int)var5, var4, var2, var7);
      SegmentCache.releaseSharedSegment(var6);
      return (float)var8;
   }

   public float getHeight(GlyphView var1) {
      this.sync(var1);
      return (float)this.metrics.getHeight();
   }

   public float getAscent(GlyphView var1) {
      this.sync(var1);
      return (float)this.metrics.getAscent();
   }

   public float getDescent(GlyphView var1) {
      this.sync(var1);
      return (float)this.metrics.getDescent();
   }

   public void paint(GlyphView var1, Graphics var2, Shape var3, int var4, int var5) {
      this.sync(var1);
      TabExpander var7 = var1.getTabExpander();
      Rectangle var8 = var3 instanceof Rectangle ? (Rectangle)var3 : var3.getBounds();
      int var9 = var8.x;
      int var10 = var1.getStartOffset();
      int[] var11 = this.getJustificationData(var1);
      Segment var6;
      int var12;
      if (var10 != var4) {
         var6 = var1.getText(var10, var4);
         var12 = Utilities.getTabbedTextWidth(var1, var6, this.metrics, var9, var7, var10, var11);
         var9 += var12;
         SegmentCache.releaseSharedSegment(var6);
      }

      var12 = var8.y + this.metrics.getHeight() - this.metrics.getDescent();
      var6 = var1.getText(var4, var5);
      var2.setFont(this.metrics.getFont());
      Utilities.drawTabbedText(var1, var6, var9, var12, var2, var7, var4, var11);
      SegmentCache.releaseSharedSegment(var6);
   }

   public Shape modelToView(GlyphView var1, int var2, Position.Bias var3, Shape var4) throws BadLocationException {
      this.sync(var1);
      Rectangle var5 = var4 instanceof Rectangle ? (Rectangle)var4 : var4.getBounds();
      int var6 = var1.getStartOffset();
      int var7 = var1.getEndOffset();
      TabExpander var8 = var1.getTabExpander();
      if (var2 == var7) {
         return new Rectangle(var5.x + var5.width, var5.y, 0, this.metrics.getHeight());
      } else if (var2 >= var6 && var2 <= var7) {
         Segment var9 = var1.getText(var6, var2);
         int[] var10 = this.getJustificationData(var1);
         int var11 = Utilities.getTabbedTextWidth(var1, var9, this.metrics, var5.x, var8, var6, var10);
         SegmentCache.releaseSharedSegment(var9);
         return new Rectangle(var5.x + var11, var5.y, 0, this.metrics.getHeight());
      } else {
         throw new BadLocationException("modelToView - can't convert", var7);
      }
   }

   public int viewToModel(GlyphView var1, float var2, float var3, Shape var4, Position.Bias[] var5) {
      this.sync(var1);
      Rectangle var6 = var4 instanceof Rectangle ? (Rectangle)var4 : var4.getBounds();
      int var7 = var1.getStartOffset();
      int var8 = var1.getEndOffset();
      TabExpander var9 = var1.getTabExpander();
      Segment var10 = var1.getText(var7, var8);
      int[] var11 = this.getJustificationData(var1);
      int var12 = Utilities.getTabbedTextOffset(var1, var10, this.metrics, var6.x, (int)var2, var9, var7, var11);
      SegmentCache.releaseSharedSegment(var10);
      int var13 = var7 + var12;
      if (var13 == var8) {
         --var13;
      }

      var5[0] = Position.Bias.Forward;
      return var13;
   }

   public int getBoundedPosition(GlyphView var1, int var2, float var3, float var4) {
      this.sync(var1);
      TabExpander var5 = var1.getTabExpander();
      Segment var6 = var1.getText(var2, var1.getEndOffset());
      int[] var7 = this.getJustificationData(var1);
      int var8 = Utilities.getTabbedTextOffset(var1, var6, this.metrics, (int)var3, (int)(var3 + var4), var5, var2, false, var7);
      SegmentCache.releaseSharedSegment(var6);
      int var9 = var2 + var8;
      return var9;
   }

   void sync(GlyphView var1) {
      Font var2 = var1.getFont();
      if (this.metrics == null || !var2.equals(this.metrics.getFont())) {
         Container var3 = var1.getContainer();
         this.metrics = var3 != null ? var3.getFontMetrics(var2) : Toolkit.getDefaultToolkit().getFontMetrics(var2);
      }

   }

   private int[] getJustificationData(GlyphView var1) {
      View var2 = var1.getParent();
      int[] var3 = null;
      if (var2 instanceof ParagraphView.Row) {
         ParagraphView.Row var4 = (ParagraphView.Row)var2;
         var3 = var4.justificationData;
      }

      return var3;
   }
}
