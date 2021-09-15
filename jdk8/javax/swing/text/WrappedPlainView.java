package javax.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.lang.ref.SoftReference;
import javax.swing.event.DocumentEvent;

public class WrappedPlainView extends BoxView implements TabExpander {
   FontMetrics metrics;
   Segment lineBuffer;
   boolean widthChanging;
   int tabBase;
   int tabSize;
   boolean wordWrap;
   int sel0;
   int sel1;
   Color unselected;
   Color selected;

   public WrappedPlainView(Element var1) {
      this(var1, false);
   }

   public WrappedPlainView(Element var1, boolean var2) {
      super(var1, 1);
      this.wordWrap = var2;
   }

   protected int getTabSize() {
      Integer var1 = (Integer)this.getDocument().getProperty("tabSize");
      int var2 = var1 != null ? var1 : 8;
      return var2;
   }

   protected void drawLine(int var1, int var2, Graphics var3, int var4, int var5) {
      Element var6 = this.getElement();
      Element var7 = var6.getElement(var6.getElementIndex(var1));

      try {
         if (var7.isLeaf()) {
            this.drawText(var7, var1, var2, var3, var4, var5);
         } else {
            int var9 = var7.getElementIndex(var1);

            for(int var10 = var7.getElementIndex(var2); var9 <= var10; ++var9) {
               Element var8 = var7.getElement(var9);
               int var11 = Math.max(var8.getStartOffset(), var1);
               int var12 = Math.min(var8.getEndOffset(), var2);
               var4 = this.drawText(var8, var11, var12, var3, var4, var5);
            }
         }

      } catch (BadLocationException var13) {
         throw new StateInvariantError("Can't render: " + var1 + "," + var2);
      }
   }

   private int drawText(Element var1, int var2, int var3, Graphics var4, int var5, int var6) throws BadLocationException {
      var3 = Math.min(this.getDocument().getLength(), var3);
      AttributeSet var7 = var1.getAttributes();
      if (Utilities.isComposedTextAttributeDefined(var7)) {
         var4.setColor(this.unselected);
         var5 = Utilities.drawComposedText(this, var7, var4, var5, var6, var2 - var1.getStartOffset(), var3 - var1.getStartOffset());
      } else if (this.sel0 != this.sel1 && this.selected != this.unselected) {
         if (var2 >= this.sel0 && var2 <= this.sel1 && var3 >= this.sel0 && var3 <= this.sel1) {
            var5 = this.drawSelectedText(var4, var5, var6, var2, var3);
         } else if (this.sel0 >= var2 && this.sel0 <= var3) {
            if (this.sel1 >= var2 && this.sel1 <= var3) {
               var5 = this.drawUnselectedText(var4, var5, var6, var2, this.sel0);
               var5 = this.drawSelectedText(var4, var5, var6, this.sel0, this.sel1);
               var5 = this.drawUnselectedText(var4, var5, var6, this.sel1, var3);
            } else {
               var5 = this.drawUnselectedText(var4, var5, var6, var2, this.sel0);
               var5 = this.drawSelectedText(var4, var5, var6, this.sel0, var3);
            }
         } else if (this.sel1 >= var2 && this.sel1 <= var3) {
            var5 = this.drawSelectedText(var4, var5, var6, var2, this.sel1);
            var5 = this.drawUnselectedText(var4, var5, var6, this.sel1, var3);
         } else {
            var5 = this.drawUnselectedText(var4, var5, var6, var2, var3);
         }
      } else {
         var5 = this.drawUnselectedText(var4, var5, var6, var2, var3);
      }

      return var5;
   }

   protected int drawUnselectedText(Graphics var1, int var2, int var3, int var4, int var5) throws BadLocationException {
      var1.setColor(this.unselected);
      Document var6 = this.getDocument();
      Segment var7 = SegmentCache.getSharedSegment();
      var6.getText(var4, var5 - var4, var7);
      int var8 = Utilities.drawTabbedText(this, var7, var2, var3, var1, this, var4);
      SegmentCache.releaseSharedSegment(var7);
      return var8;
   }

   protected int drawSelectedText(Graphics var1, int var2, int var3, int var4, int var5) throws BadLocationException {
      var1.setColor(this.selected);
      Document var6 = this.getDocument();
      Segment var7 = SegmentCache.getSharedSegment();
      var6.getText(var4, var5 - var4, var7);
      int var8 = Utilities.drawTabbedText(this, var7, var2, var3, var1, this, var4);
      SegmentCache.releaseSharedSegment(var7);
      return var8;
   }

   protected final Segment getLineBuffer() {
      if (this.lineBuffer == null) {
         this.lineBuffer = new Segment();
      }

      return this.lineBuffer;
   }

   protected int calculateBreakPosition(int var1, int var2) {
      Segment var4 = SegmentCache.getSharedSegment();
      this.loadText(var4, var1, var2);
      int var5 = this.getWidth();
      int var3;
      if (this.wordWrap) {
         var3 = var1 + Utilities.getBreakLocation(var4, this.metrics, this.tabBase, this.tabBase + var5, this, var1);
      } else {
         var3 = var1 + Utilities.getTabbedTextOffset(var4, this.metrics, this.tabBase, this.tabBase + var5, this, var1, false);
      }

      SegmentCache.releaseSharedSegment(var4);
      return var3;
   }

   protected void loadChildren(ViewFactory var1) {
      Element var2 = this.getElement();
      int var3 = var2.getElementCount();
      if (var3 > 0) {
         View[] var4 = new View[var3];

         for(int var5 = 0; var5 < var3; ++var5) {
            var4[var5] = new WrappedPlainView.WrappedLine(var2.getElement(var5));
         }

         this.replace(0, 0, var4);
      }

   }

   void updateChildren(DocumentEvent var1, Shape var2) {
      Element var3 = this.getElement();
      DocumentEvent.ElementChange var4 = var1.getChange(var3);
      if (var4 != null) {
         Element[] var5 = var4.getChildrenRemoved();
         Element[] var6 = var4.getChildrenAdded();
         View[] var7 = new View[var6.length];

         for(int var8 = 0; var8 < var6.length; ++var8) {
            var7[var8] = new WrappedPlainView.WrappedLine(var6[var8]);
         }

         this.replace(var4.getIndex(), var5.length, var7);
         if (var2 != null) {
            this.preferenceChanged((View)null, true, true);
            this.getContainer().repaint();
         }
      }

      this.updateMetrics();
   }

   final void loadText(Segment var1, int var2, int var3) {
      try {
         Document var4 = this.getDocument();
         var4.getText(var2, var3 - var2, var1);
      } catch (BadLocationException var5) {
         throw new StateInvariantError("Can't get line text");
      }
   }

   final void updateMetrics() {
      Container var1 = this.getContainer();
      Font var2 = var1.getFont();
      this.metrics = var1.getFontMetrics(var2);
      this.tabSize = this.getTabSize() * this.metrics.charWidth('m');
   }

   public float nextTabStop(float var1, int var2) {
      if (this.tabSize == 0) {
         return var1;
      } else {
         int var3 = ((int)var1 - this.tabBase) / this.tabSize;
         return (float)(this.tabBase + (var3 + 1) * this.tabSize);
      }
   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = (Rectangle)var2;
      this.tabBase = var3.x;
      JTextComponent var4 = (JTextComponent)this.getContainer();
      this.sel0 = var4.getSelectionStart();
      this.sel1 = var4.getSelectionEnd();
      this.unselected = var4.isEnabled() ? var4.getForeground() : var4.getDisabledTextColor();
      Caret var5 = var4.getCaret();
      this.selected = var5.isSelectionVisible() && var4.getHighlighter() != null ? var4.getSelectedTextColor() : this.unselected;
      var1.setFont(var4.getFont());
      super.paint(var1, var2);
   }

   public void setSize(float var1, float var2) {
      this.updateMetrics();
      if ((int)var1 != this.getWidth()) {
         this.preferenceChanged((View)null, true, true);
         this.widthChanging = true;
      }

      super.setSize(var1, var2);
      this.widthChanging = false;
   }

   public float getPreferredSpan(int var1) {
      this.updateMetrics();
      return super.getPreferredSpan(var1);
   }

   public float getMinimumSpan(int var1) {
      this.updateMetrics();
      return super.getMinimumSpan(var1);
   }

   public float getMaximumSpan(int var1) {
      this.updateMetrics();
      return super.getMaximumSpan(var1);
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.updateChildren(var1, var2);
      Rectangle var4 = var2 != null && this.isAllocationValid() ? this.getInsideAllocation(var2) : null;
      int var5 = var1.getOffset();
      View var6 = this.getViewAtPosition(var5, var4);
      if (var6 != null) {
         var6.insertUpdate(var1, var4, var3);
      }

   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.updateChildren(var1, var2);
      Rectangle var4 = var2 != null && this.isAllocationValid() ? this.getInsideAllocation(var2) : null;
      int var5 = var1.getOffset();
      View var6 = this.getViewAtPosition(var5, var4);
      if (var6 != null) {
         var6.removeUpdate(var1, var4, var3);
      }

   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.updateChildren(var1, var2);
   }

   class WrappedLine extends View {
      int lineCount = -1;
      SoftReference<int[]> lineCache = null;

      WrappedLine(Element var2) {
         super(var2);
      }

      public float getPreferredSpan(int var1) {
         switch(var1) {
         case 0:
            float var2 = (float)WrappedPlainView.this.getWidth();
            if (var2 == 2.14748365E9F) {
               return 100.0F;
            }

            return var2;
         case 1:
            if (this.lineCount < 0 || WrappedPlainView.this.widthChanging) {
               this.breakLines(this.getStartOffset());
            }

            return (float)(this.lineCount * WrappedPlainView.this.metrics.getHeight());
         default:
            throw new IllegalArgumentException("Invalid axis: " + var1);
         }
      }

      public void paint(Graphics var1, Shape var2) {
         Rectangle var3 = (Rectangle)var2;
         int var4 = var3.y + WrappedPlainView.this.metrics.getAscent();
         int var5 = var3.x;
         JTextComponent var6 = (JTextComponent)this.getContainer();
         Highlighter var7 = var6.getHighlighter();
         LayeredHighlighter var8 = var7 instanceof LayeredHighlighter ? (LayeredHighlighter)var7 : null;
         int var9 = this.getStartOffset();
         int var10 = this.getEndOffset();
         int var11 = var9;
         int[] var12 = this.getLineEnds();

         for(int var13 = 0; var13 < this.lineCount; ++var13) {
            int var14 = var12 == null ? var10 : var9 + var12[var13];
            if (var8 != null) {
               int var15 = var14 == var10 ? var14 - 1 : var14;
               var8.paintLayeredHighlights(var1, var11, var15, var2, var6, this);
            }

            WrappedPlainView.this.drawLine(var11, var14, var1, var5, var4);
            var11 = var14;
            var4 += WrappedPlainView.this.metrics.getHeight();
         }

      }

      public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
         Rectangle var4 = var2.getBounds();
         var4.height = WrappedPlainView.this.metrics.getHeight();
         var4.width = 1;
         int var5 = this.getStartOffset();
         if (var1 >= var5 && var1 <= this.getEndOffset()) {
            int var6 = var3 == Position.Bias.Forward ? var1 : Math.max(var5, var1 - 1);
            boolean var7 = false;
            int[] var8 = this.getLineEnds();
            if (var8 != null) {
               int var10 = this.findLine(var6 - var5);
               if (var10 > 0) {
                  var5 += var8[var10 - 1];
               }

               var4.y += var4.height * var10;
            }

            if (var1 > var5) {
               Segment var9 = SegmentCache.getSharedSegment();
               WrappedPlainView.this.loadText(var9, var5, var1);
               var4.x += Utilities.getTabbedTextWidth(var9, WrappedPlainView.this.metrics, var4.x, WrappedPlainView.this, var5);
               SegmentCache.releaseSharedSegment(var9);
            }

            return var4;
         } else {
            throw new BadLocationException("Position out of range", var1);
         }
      }

      public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
         var4[0] = Position.Bias.Forward;
         Rectangle var5 = (Rectangle)var3;
         int var6 = (int)var1;
         int var7 = (int)var2;
         if (var7 < var5.y) {
            return this.getStartOffset();
         } else if (var7 > var5.y + var5.height) {
            return this.getEndOffset() - 1;
         } else {
            var5.height = WrappedPlainView.this.metrics.getHeight();
            int var8 = var5.height > 0 ? (var7 - var5.y) / var5.height : this.lineCount - 1;
            if (var8 >= this.lineCount) {
               return this.getEndOffset() - 1;
            } else {
               int var9 = this.getStartOffset();
               int var10;
               if (this.lineCount == 1) {
                  var10 = this.getEndOffset();
               } else {
                  int[] var11 = this.getLineEnds();
                  var10 = var9 + var11[var8];
                  if (var8 > 0) {
                     var9 += var11[var8 - 1];
                  }
               }

               if (var6 < var5.x) {
                  return var9;
               } else if (var6 > var5.x + var5.width) {
                  return var10 - 1;
               } else {
                  Segment var13 = SegmentCache.getSharedSegment();
                  WrappedPlainView.this.loadText(var13, var9, var10);
                  int var12 = Utilities.getTabbedTextOffset(var13, WrappedPlainView.this.metrics, var5.x, var6, WrappedPlainView.this, var9);
                  SegmentCache.releaseSharedSegment(var13);
                  return Math.min(var9 + var12, var10 - 1);
               }
            }
         }
      }

      public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         this.update(var1, var2);
      }

      public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
         this.update(var1, var2);
      }

      private void update(DocumentEvent var1, Shape var2) {
         int var3 = this.lineCount;
         this.breakLines(var1.getOffset());
         if (var3 != this.lineCount) {
            WrappedPlainView.this.preferenceChanged(this, false, true);
            this.getContainer().repaint();
         } else if (var2 != null) {
            Container var4 = this.getContainer();
            Rectangle var5 = (Rectangle)var2;
            var4.repaint(var5.x, var5.y, var5.width, var5.height);
         }

      }

      final int[] getLineEnds() {
         if (this.lineCache == null) {
            return null;
         } else {
            int[] var1 = (int[])this.lineCache.get();
            return var1 == null ? this.breakLines(this.getStartOffset()) : var1;
         }
      }

      final int[] breakLines(int var1) {
         int[] var2 = this.lineCache == null ? null : (int[])this.lineCache.get();
         int[] var3 = var2;
         int var4 = this.getStartOffset();
         int var5 = 0;
         if (var2 != null) {
            var5 = this.findLine(var1 - var4);
            if (var5 > 0) {
               --var5;
            }
         }

         int var6 = var5 == 0 ? var4 : var4 + var2[var5 - 1];

         int var8;
         for(int var7 = this.getEndOffset(); var6 < var7; var2[var5++] = var6 - var4) {
            var8 = WrappedPlainView.this.calculateBreakPosition(var6, var7);
            int var10000;
            if (var8 == var6) {
               ++var8;
               var10000 = var8;
            } else {
               var10000 = var8;
            }

            var6 = var10000;
            if (var5 == 0 && var6 >= var7) {
               this.lineCache = null;
               var2 = null;
               var5 = 1;
               break;
            }

            if (var2 == null || var5 >= var2.length) {
               double var9 = (double)(var7 - var4) / (double)(var6 - var4);
               int var11 = (int)Math.ceil((double)(var5 + 1) * var9);
               var11 = Math.max(var11, var5 + 2);
               int[] var12 = new int[var11];
               if (var2 != null) {
                  System.arraycopy(var2, 0, var12, 0, var5);
               }

               var2 = var12;
            }
         }

         this.lineCount = var5;
         if (this.lineCount > 1) {
            var8 = this.lineCount + this.lineCount / 3;
            if (var2.length > var8) {
               int[] var13 = new int[var8];
               System.arraycopy(var2, 0, var13, 0, this.lineCount);
               var2 = var13;
            }
         }

         if (var2 != null && var2 != var3) {
            this.lineCache = new SoftReference(var2);
         }

         return var2;
      }

      private int findLine(int var1) {
         int[] var2 = (int[])this.lineCache.get();
         if (var1 < var2[0]) {
            return 0;
         } else {
            return var1 > var2[this.lineCount - 1] ? this.lineCount : this.findLine(var2, var1, 0, this.lineCount - 1);
         }
      }

      private int findLine(int[] var1, int var2, int var3, int var4) {
         if (var4 - var3 <= 1) {
            return var4;
         } else {
            int var5 = (var4 + var3) / 2;
            return var2 < var1[var5] ? this.findLine(var1, var2, var3, var5) : this.findLine(var1, var2, var5, var4);
         }
      }
   }
}
