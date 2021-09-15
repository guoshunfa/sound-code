package javax.swing.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.event.DocumentEvent;

public class PlainView extends View implements TabExpander {
   protected FontMetrics metrics;
   Element longLine;
   Font font;
   Segment lineBuffer;
   int tabSize;
   int tabBase;
   int sel0;
   int sel1;
   Color unselected;
   Color selected;
   int firstLineOffset;

   public PlainView(Element var1) {
      super(var1);
   }

   protected int getTabSize() {
      Integer var1 = (Integer)this.getDocument().getProperty("tabSize");
      int var2 = var1 != null ? var1 : 8;
      return var2;
   }

   protected void drawLine(int var1, Graphics var2, int var3, int var4) {
      Element var5 = this.getElement().getElement(var1);

      try {
         if (var5.isLeaf()) {
            this.drawElement(var1, var5, var2, var3, var4);
         } else {
            int var7 = var5.getElementCount();

            for(int var8 = 0; var8 < var7; ++var8) {
               Element var6 = var5.getElement(var8);
               var3 = this.drawElement(var1, var6, var2, var3, var4);
            }
         }

      } catch (BadLocationException var9) {
         throw new StateInvariantError("Can't render line: " + var1);
      }
   }

   private int drawElement(int var1, Element var2, Graphics var3, int var4, int var5) throws BadLocationException {
      int var6 = var2.getStartOffset();
      int var7 = var2.getEndOffset();
      var7 = Math.min(this.getDocument().getLength(), var7);
      if (var1 == 0) {
         var4 += this.firstLineOffset;
      }

      AttributeSet var8 = var2.getAttributes();
      if (Utilities.isComposedTextAttributeDefined(var8)) {
         var3.setColor(this.unselected);
         var4 = Utilities.drawComposedText(this, var8, var3, var4, var5, var6 - var2.getStartOffset(), var7 - var2.getStartOffset());
      } else if (this.sel0 != this.sel1 && this.selected != this.unselected) {
         if (var6 >= this.sel0 && var6 <= this.sel1 && var7 >= this.sel0 && var7 <= this.sel1) {
            var4 = this.drawSelectedText(var3, var4, var5, var6, var7);
         } else if (this.sel0 >= var6 && this.sel0 <= var7) {
            if (this.sel1 >= var6 && this.sel1 <= var7) {
               var4 = this.drawUnselectedText(var3, var4, var5, var6, this.sel0);
               var4 = this.drawSelectedText(var3, var4, var5, this.sel0, this.sel1);
               var4 = this.drawUnselectedText(var3, var4, var5, this.sel1, var7);
            } else {
               var4 = this.drawUnselectedText(var3, var4, var5, var6, this.sel0);
               var4 = this.drawSelectedText(var3, var4, var5, this.sel0, var7);
            }
         } else if (this.sel1 >= var6 && this.sel1 <= var7) {
            var4 = this.drawSelectedText(var3, var4, var5, var6, this.sel1);
            var4 = this.drawUnselectedText(var3, var4, var5, this.sel1, var7);
         } else {
            var4 = this.drawUnselectedText(var3, var4, var5, var6, var7);
         }
      } else {
         var4 = this.drawUnselectedText(var3, var4, var5, var6, var7);
      }

      return var4;
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

   protected void updateMetrics() {
      Container var1 = this.getContainer();
      Font var2 = var1.getFont();
      if (this.font != var2) {
         this.calculateLongestLine();
         this.tabSize = this.getTabSize() * this.metrics.charWidth('m');
      }

   }

   public float getPreferredSpan(int var1) {
      this.updateMetrics();
      switch(var1) {
      case 0:
         return (float)this.getLineWidth(this.longLine);
      case 1:
         return (float)(this.getElement().getElementCount() * this.metrics.getHeight());
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public void paint(Graphics var1, Shape var2) {
      Shape var3 = var2;
      var2 = this.adjustPaintRegion(var2);
      Rectangle var4 = (Rectangle)var2;
      this.tabBase = var4.x;
      JTextComponent var5 = (JTextComponent)this.getContainer();
      Highlighter var6 = var5.getHighlighter();
      var1.setFont(var5.getFont());
      this.sel0 = var5.getSelectionStart();
      this.sel1 = var5.getSelectionEnd();
      this.unselected = var5.isEnabled() ? var5.getForeground() : var5.getDisabledTextColor();
      Caret var7 = var5.getCaret();
      this.selected = var7.isSelectionVisible() && var6 != null ? var5.getSelectedTextColor() : this.unselected;
      this.updateMetrics();
      Rectangle var8 = var1.getClipBounds();
      int var9 = this.metrics.getHeight();
      int var10 = var4.y + var4.height - (var8.y + var8.height);
      int var11 = var8.y - var4.y;
      int var12;
      int var13;
      int var14;
      if (var9 > 0) {
         var12 = Math.max(0, var10 / var9);
         var13 = Math.max(0, var11 / var9);
         var14 = var4.height / var9;
         if (var4.height % var9 != 0) {
            ++var14;
         }
      } else {
         var14 = 0;
         var13 = 0;
         var12 = 0;
      }

      Rectangle var15 = this.lineToRect(var2, var13);
      int var16 = var15.y + this.metrics.getAscent();
      int var17 = var15.x;
      Element var18 = this.getElement();
      int var19 = var18.getElementCount();
      int var20 = Math.min(var19, var14 - var12);
      --var19;
      LayeredHighlighter var21 = var6 instanceof LayeredHighlighter ? (LayeredHighlighter)var6 : null;

      for(int var22 = var13; var22 < var20; ++var22) {
         if (var21 != null) {
            Element var23 = var18.getElement(var22);
            if (var22 == var19) {
               var21.paintLayeredHighlights(var1, var23.getStartOffset(), var23.getEndOffset(), var3, var5, this);
            } else {
               var21.paintLayeredHighlights(var1, var23.getStartOffset(), var23.getEndOffset() - 1, var3, var5, this);
            }
         }

         this.drawLine(var22, var1, var17, var16);
         var16 += var9;
         if (var22 == 0) {
            var17 -= this.firstLineOffset;
         }
      }

   }

   Shape adjustPaintRegion(Shape var1) {
      return var1;
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      Document var4 = this.getDocument();
      Element var5 = this.getElement();
      int var6 = var5.getElementIndex(var1);
      if (var6 < 0) {
         return this.lineToRect(var2, 0);
      } else {
         Rectangle var7 = this.lineToRect(var2, var6);
         this.tabBase = var7.x;
         Element var8 = var5.getElement(var6);
         int var9 = var8.getStartOffset();
         Segment var10 = SegmentCache.getSharedSegment();
         var4.getText(var9, var1 - var9, var10);
         int var11 = Utilities.getTabbedTextWidth(var10, this.metrics, this.tabBase, this, var9);
         SegmentCache.releaseSharedSegment(var10);
         var7.x += var11;
         var7.width = 1;
         var7.height = this.metrics.getHeight();
         return var7;
      }
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      var4[0] = Position.Bias.Forward;
      Rectangle var5 = var3.getBounds();
      Document var6 = this.getDocument();
      int var7 = (int)var1;
      int var8 = (int)var2;
      if (var8 < var5.y) {
         return this.getStartOffset();
      } else if (var8 > var5.y + var5.height) {
         return this.getEndOffset() - 1;
      } else {
         Element var9 = var6.getDefaultRootElement();
         int var10 = this.metrics.getHeight();
         int var11 = var10 > 0 ? Math.abs((var8 - var5.y) / var10) : var9.getElementCount() - 1;
         if (var11 >= var9.getElementCount()) {
            return this.getEndOffset() - 1;
         } else {
            Element var12 = var9.getElement(var11);
            boolean var13 = false;
            if (var11 == 0) {
               var5.x += this.firstLineOffset;
               var5.width -= this.firstLineOffset;
            }

            if (var7 < var5.x) {
               return var12.getStartOffset();
            } else if (var7 > var5.x + var5.width) {
               return var12.getEndOffset() - 1;
            } else {
               try {
                  int var14 = var12.getStartOffset();
                  int var15 = var12.getEndOffset() - 1;
                  Segment var16 = SegmentCache.getSharedSegment();
                  var6.getText(var14, var15 - var14, var16);
                  this.tabBase = var5.x;
                  int var17 = var14 + Utilities.getTabbedTextOffset(var16, this.metrics, this.tabBase, var7, this, var14);
                  SegmentCache.releaseSharedSegment(var16);
                  return var17;
               } catch (BadLocationException var18) {
                  return -1;
               }
            }
         }
      }
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.updateDamage(var1, var2, var3);
   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.updateDamage(var1, var2, var3);
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.updateDamage(var1, var2, var3);
   }

   public void setSize(float var1, float var2) {
      super.setSize(var1, var2);
      this.updateMetrics();
   }

   public float nextTabStop(float var1, int var2) {
      if (this.tabSize == 0) {
         return var1;
      } else {
         int var3 = ((int)var1 - this.tabBase) / this.tabSize;
         return (float)(this.tabBase + (var3 + 1) * this.tabSize);
      }
   }

   protected void updateDamage(DocumentEvent var1, Shape var2, ViewFactory var3) {
      Container var4 = this.getContainer();
      this.updateMetrics();
      Element var5 = this.getElement();
      DocumentEvent.ElementChange var6 = var1.getChange(var5);
      Element[] var7 = var6 != null ? var6.getChildrenAdded() : null;
      Element[] var8 = var6 != null ? var6.getChildrenRemoved() : null;
      int var10;
      int var11;
      if ((var7 == null || var7.length <= 0) && (var8 == null || var8.length <= 0)) {
         Element var13 = this.getElement();
         var10 = var13.getElementIndex(var1.getOffset());
         this.damageLineRange(var10, var10, var2, var4);
         if (var1.getType() == DocumentEvent.EventType.INSERT) {
            var11 = this.getLineWidth(this.longLine);
            Element var12 = var13.getElement(var10);
            if (var12 == this.longLine) {
               this.preferenceChanged((View)null, true, false);
            } else if (this.getLineWidth(var12) > var11) {
               this.longLine = var12;
               this.preferenceChanged((View)null, true, false);
            }
         } else if (var1.getType() == DocumentEvent.EventType.REMOVE && var13.getElement(var10) == this.longLine) {
            this.calculateLongestLine();
            this.preferenceChanged((View)null, true, false);
         }
      } else {
         int var9;
         if (var7 != null) {
            var9 = this.getLineWidth(this.longLine);

            for(var10 = 0; var10 < var7.length; ++var10) {
               var11 = this.getLineWidth(var7[var10]);
               if (var11 > var9) {
                  var9 = var11;
                  this.longLine = var7[var10];
               }
            }
         }

         if (var8 != null) {
            for(var9 = 0; var9 < var8.length; ++var9) {
               if (var8[var9] == this.longLine) {
                  this.calculateLongestLine();
                  break;
               }
            }
         }

         this.preferenceChanged((View)null, true, true);
         var4.repaint();
      }

   }

   protected void damageLineRange(int var1, int var2, Shape var3, Component var4) {
      if (var3 != null) {
         Rectangle var5 = this.lineToRect(var3, var1);
         Rectangle var6 = this.lineToRect(var3, var2);
         if (var5 != null && var6 != null) {
            Rectangle var7 = var5.union(var6);
            var4.repaint(var7.x, var7.y, var7.width, var7.height);
         } else {
            var4.repaint();
         }
      }

   }

   protected Rectangle lineToRect(Shape var1, int var2) {
      Rectangle var3 = null;
      this.updateMetrics();
      if (this.metrics != null) {
         Rectangle var4 = var1.getBounds();
         if (var2 == 0) {
            var4.x += this.firstLineOffset;
            var4.width -= this.firstLineOffset;
         }

         var3 = new Rectangle(var4.x, var4.y + var2 * this.metrics.getHeight(), var4.width, this.metrics.getHeight());
      }

      return var3;
   }

   private void calculateLongestLine() {
      Container var1 = this.getContainer();
      this.font = var1.getFont();
      this.metrics = var1.getFontMetrics(this.font);
      Document var2 = this.getDocument();
      Element var3 = this.getElement();
      int var4 = var3.getElementCount();
      int var5 = -1;

      for(int var6 = 0; var6 < var4; ++var6) {
         Element var7 = var3.getElement(var6);
         int var8 = this.getLineWidth(var7);
         if (var8 > var5) {
            var5 = var8;
            this.longLine = var7;
         }
      }

   }

   private int getLineWidth(Element var1) {
      if (var1 == null) {
         return 0;
      } else {
         int var2 = var1.getStartOffset();
         int var3 = var1.getEndOffset();
         Segment var5 = SegmentCache.getSharedSegment();

         int var4;
         try {
            var1.getDocument().getText(var2, var3 - var2, var5);
            var4 = Utilities.getTabbedTextWidth(var5, this.metrics, this.tabBase, this, var2);
         } catch (BadLocationException var7) {
            var4 = 0;
         }

         SegmentCache.releaseSharedSegment(var5);
         return var4;
      }
   }
}
