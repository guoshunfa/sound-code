package javax.swing.text;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.BitSet;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import sun.swing.SwingUtilities2;

public class GlyphView extends View implements TabableView, Cloneable {
   private byte[] selections = null;
   int offset = 0;
   int length = 0;
   boolean impliedCR;
   boolean skipWidth;
   TabExpander expander;
   private float minimumSpan = -1.0F;
   private int[] breakSpots = null;
   int x;
   GlyphView.GlyphPainter painter;
   static GlyphView.GlyphPainter defaultPainter;
   private GlyphView.JustificationInfo justificationInfo = null;

   public GlyphView(Element var1) {
      super(var1);
      Element var2 = var1.getParentElement();
      AttributeSet var3 = var1.getAttributes();
      this.impliedCR = var3 != null && var3.getAttribute("CR") != null && var2 != null && var2.getElementCount() > 1;
      this.skipWidth = var1.getName().equals("br");
   }

   protected final Object clone() {
      Object var1;
      try {
         var1 = super.clone();
      } catch (CloneNotSupportedException var3) {
         var1 = null;
      }

      return var1;
   }

   public GlyphView.GlyphPainter getGlyphPainter() {
      return this.painter;
   }

   public void setGlyphPainter(GlyphView.GlyphPainter var1) {
      this.painter = var1;
   }

   public Segment getText(int var1, int var2) {
      Segment var3 = SegmentCache.getSharedSegment();

      try {
         Document var4 = this.getDocument();
         var4.getText(var1, var2 - var1, var3);
         return var3;
      } catch (BadLocationException var5) {
         throw new StateInvariantError("GlyphView: Stale view: " + var5);
      }
   }

   public Color getBackground() {
      Document var1 = this.getDocument();
      if (var1 instanceof StyledDocument) {
         AttributeSet var2 = this.getAttributes();
         if (var2.isDefined(StyleConstants.Background)) {
            return ((StyledDocument)var1).getBackground(var2);
         }
      }

      return null;
   }

   public Color getForeground() {
      Document var1 = this.getDocument();
      if (var1 instanceof StyledDocument) {
         AttributeSet var3 = this.getAttributes();
         return ((StyledDocument)var1).getForeground(var3);
      } else {
         Container var2 = this.getContainer();
         return var2 != null ? var2.getForeground() : null;
      }
   }

   public Font getFont() {
      Document var1 = this.getDocument();
      if (var1 instanceof StyledDocument) {
         AttributeSet var3 = this.getAttributes();
         return ((StyledDocument)var1).getFont(var3);
      } else {
         Container var2 = this.getContainer();
         return var2 != null ? var2.getFont() : null;
      }
   }

   public boolean isUnderline() {
      AttributeSet var1 = this.getAttributes();
      return StyleConstants.isUnderline(var1);
   }

   public boolean isStrikeThrough() {
      AttributeSet var1 = this.getAttributes();
      return StyleConstants.isStrikeThrough(var1);
   }

   public boolean isSubscript() {
      AttributeSet var1 = this.getAttributes();
      return StyleConstants.isSubscript(var1);
   }

   public boolean isSuperscript() {
      AttributeSet var1 = this.getAttributes();
      return StyleConstants.isSuperscript(var1);
   }

   public TabExpander getTabExpander() {
      return this.expander;
   }

   protected void checkPainter() {
      if (this.painter == null) {
         if (defaultPainter == null) {
            String var1 = "javax.swing.text.GlyphPainter1";

            try {
               ClassLoader var3 = this.getClass().getClassLoader();
               Class var2;
               if (var3 != null) {
                  var2 = var3.loadClass(var1);
               } else {
                  var2 = Class.forName(var1);
               }

               Object var4 = var2.newInstance();
               if (var4 instanceof GlyphView.GlyphPainter) {
                  defaultPainter = (GlyphView.GlyphPainter)var4;
               }
            } catch (Throwable var5) {
               throw new StateInvariantError("GlyphView: Can't load glyph painter: " + var1);
            }
         }

         this.setGlyphPainter(defaultPainter.getPainter(this, this.getStartOffset(), this.getEndOffset()));
      }

   }

   public float getTabbedSpan(float var1, TabExpander var2) {
      this.checkPainter();
      TabExpander var3 = this.expander;
      this.expander = var2;
      if (this.expander != var3) {
         this.preferenceChanged((View)null, true, false);
      }

      this.x = (int)var1;
      int var4 = this.getStartOffset();
      int var5 = this.getEndOffset();
      float var6 = this.painter.getSpan(this, var4, var5, this.expander, var1);
      return var6;
   }

   public float getPartialSpan(int var1, int var2) {
      this.checkPainter();
      float var3 = this.painter.getSpan(this, var1, var2, this.expander, (float)this.x);
      return var3;
   }

   public int getStartOffset() {
      Element var1 = this.getElement();
      return this.length > 0 ? var1.getStartOffset() + this.offset : var1.getStartOffset();
   }

   public int getEndOffset() {
      Element var1 = this.getElement();
      return this.length > 0 ? var1.getStartOffset() + this.offset + this.length : var1.getEndOffset();
   }

   private void initSelections(int var1, int var2) {
      int var3 = var2 - var1 + 1;
      if (this.selections != null && var3 <= this.selections.length) {
         for(int var4 = 0; var4 < var3; this.selections[var4++] = 0) {
         }

      } else {
         this.selections = new byte[var3];
      }
   }

   public void paint(Graphics var1, Shape var2) {
      this.checkPainter();
      boolean var3 = false;
      Container var4 = this.getContainer();
      int var5 = this.getStartOffset();
      int var6 = this.getEndOffset();
      Rectangle var7 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
      Color var8 = this.getBackground();
      Color var9 = this.getForeground();
      if (var4 != null && !var4.isEnabled()) {
         var9 = var4 instanceof JTextComponent ? ((JTextComponent)var4).getDisabledTextColor() : UIManager.getColor("textInactiveText");
      }

      if (var8 != null) {
         var1.setColor(var8);
         var1.fillRect(var7.x, var7.y, var7.width, var7.height);
      }

      JTextComponent var10;
      if (var4 instanceof JTextComponent) {
         var10 = (JTextComponent)var4;
         Highlighter var11 = var10.getHighlighter();
         if (var11 instanceof LayeredHighlighter) {
            ((LayeredHighlighter)var11).paintLayeredHighlights(var1, var5, var6, var2, var10, this);
         }
      }

      if (Utilities.isComposedTextElement(this.getElement())) {
         Utilities.paintComposedText(var1, var2.getBounds(), this);
         var3 = true;
      } else if (var4 instanceof JTextComponent) {
         var10 = (JTextComponent)var4;
         Color var19 = var10.getSelectedTextColor();
         if (var10.getHighlighter() != null && var19 != null && !var19.equals(var9)) {
            Highlighter.Highlight[] var12 = var10.getHighlighter().getHighlights();
            if (var12.length != 0) {
               boolean var13 = false;
               int var14 = 0;

               int var15;
               int var17;
               int var18;
               for(var15 = 0; var15 < var12.length; ++var15) {
                  Highlighter.Highlight var16 = var12[var15];
                  var17 = var16.getStartOffset();
                  var18 = var16.getEndOffset();
                  if (var17 <= var6 && var18 >= var5 && SwingUtilities2.useSelectedTextColor(var16, var10)) {
                     if (var17 <= var5 && var18 >= var6) {
                        this.paintTextUsingColor(var1, var2, var19, var5, var6);
                        var3 = true;
                        break;
                     }

                     if (!var13) {
                        this.initSelections(var5, var6);
                        var13 = true;
                     }

                     var17 = Math.max(var5, var17);
                     var18 = Math.min(var6, var18);
                     this.paintTextUsingColor(var1, var2, var19, var17, var18);
                     ++this.selections[var17 - var5];
                     --this.selections[var18 - var5];
                     ++var14;
                  }
               }

               if (!var3 && var14 > 0) {
                  var15 = -1;
                  int var20 = 0;

                  for(var17 = var6 - var5; var15++ < var17; var20 = var15) {
                     while(var15 < var17 && this.selections[var15] == 0) {
                        ++var15;
                     }

                     if (var20 != var15) {
                        this.paintTextUsingColor(var1, var2, var9, var5 + var20, var5 + var15);
                     }

                     for(var18 = 0; var15 < var17 && (var18 += this.selections[var15]) != 0; ++var15) {
                     }
                  }

                  var3 = true;
               }
            }
         }
      }

      if (!var3) {
         this.paintTextUsingColor(var1, var2, var9, var5, var6);
      }

   }

   final void paintTextUsingColor(Graphics var1, Shape var2, Color var3, int var4, int var5) {
      var1.setColor(var3);
      this.painter.paint(this, var1, var2, var4, var5);
      boolean var6 = this.isUnderline();
      boolean var7 = this.isStrikeThrough();
      if (var6 || var7) {
         Rectangle var8 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
         View var9 = this.getParent();
         if (var9 != null && var9.getEndOffset() == var5) {
            Segment var10;
            for(var10 = this.getText(var4, var5); Character.isWhitespace(var10.last()); --var10.count) {
               --var5;
            }

            SegmentCache.releaseSharedSegment(var10);
         }

         int var15 = var8.x;
         int var11 = this.getStartOffset();
         if (var11 != var4) {
            var15 += (int)this.painter.getSpan(this, var11, var4, this.getTabExpander(), (float)var15);
         }

         int var12 = var15 + (int)this.painter.getSpan(this, var4, var5, this.getTabExpander(), (float)var15);
         int var13 = var8.y + (int)(this.painter.getHeight(this) - this.painter.getDescent(this));
         int var14;
         if (var6) {
            var14 = var13 + 1;
            var1.drawLine(var15, var14, var12, var14);
         }

         if (var7) {
            var14 = var13 - (int)(this.painter.getAscent(this) * 0.3F);
            var1.drawLine(var15, var14, var12, var14);
         }
      }

   }

   public float getMinimumSpan(int var1) {
      switch(var1) {
      case 0:
         if (this.minimumSpan < 0.0F) {
            this.minimumSpan = 0.0F;
            int var2 = this.getStartOffset();

            int var4;
            for(int var3 = this.getEndOffset(); var3 > var2; var3 = var4 - 1) {
               var4 = this.getBreakSpot(var2, var3);
               if (var4 == -1) {
                  var4 = var2;
               }

               this.minimumSpan = Math.max(this.minimumSpan, this.getPartialSpan(var4, var3));
            }
         }

         return this.minimumSpan;
      case 1:
         return super.getMinimumSpan(var1);
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public float getPreferredSpan(int var1) {
      if (this.impliedCR) {
         return 0.0F;
      } else {
         this.checkPainter();
         int var2 = this.getStartOffset();
         int var3 = this.getEndOffset();
         switch(var1) {
         case 0:
            if (this.skipWidth) {
               return 0.0F;
            }

            return this.painter.getSpan(this, var2, var3, this.expander, (float)this.x);
         case 1:
            float var4 = this.painter.getHeight(this);
            if (this.isSuperscript()) {
               var4 += var4 / 3.0F;
            }

            return var4;
         default:
            throw new IllegalArgumentException("Invalid axis: " + var1);
         }
      }
   }

   public float getAlignment(int var1) {
      this.checkPainter();
      if (var1 == 1) {
         boolean var2 = this.isSuperscript();
         boolean var3 = this.isSubscript();
         float var4 = this.painter.getHeight(this);
         float var5 = this.painter.getDescent(this);
         float var6 = this.painter.getAscent(this);
         float var7;
         if (var2) {
            var7 = 1.0F;
         } else if (var3) {
            var7 = var4 > 0.0F ? (var4 - (var5 + var6 / 2.0F)) / var4 : 0.0F;
         } else {
            var7 = var4 > 0.0F ? (var4 - var5) / var4 : 0.0F;
         }

         return var7;
      } else {
         return super.getAlignment(var1);
      }
   }

   public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
      this.checkPainter();
      return this.painter.modelToView(this, var1, var3, var2);
   }

   public int viewToModel(float var1, float var2, Shape var3, Position.Bias[] var4) {
      this.checkPainter();
      return this.painter.viewToModel(this, var1, var2, var3, var4);
   }

   public int getBreakWeight(int var1, float var2, float var3) {
      if (var1 == 0) {
         this.checkPainter();
         int var4 = this.getStartOffset();
         int var5 = this.painter.getBoundedPosition(this, var4, var2, var3);
         return var5 == var4 ? 0 : (this.getBreakSpot(var4, var5) != -1 ? 2000 : 1000);
      } else {
         return super.getBreakWeight(var1, var2, var3);
      }
   }

   public View breakView(int var1, int var2, float var3, float var4) {
      if (var1 == 0) {
         this.checkPainter();
         int var5 = this.painter.getBoundedPosition(this, var2, var3, var4);
         int var6 = this.getBreakSpot(var2, var5);
         if (var6 != -1) {
            var5 = var6;
         }

         if (var2 == this.getStartOffset() && var5 == this.getEndOffset()) {
            return this;
         } else {
            GlyphView var7 = (GlyphView)this.createFragment(var2, var5);
            var7.x = (int)var3;
            return var7;
         }
      } else {
         return this;
      }
   }

   private int getBreakSpot(int var1, int var2) {
      int var3;
      int var4;
      if (this.breakSpots == null) {
         var3 = this.getStartOffset();
         var4 = this.getEndOffset();
         int[] var5 = new int[var4 + 1 - var3];
         int var6 = 0;
         Element var7 = this.getElement().getParentElement();
         int var8 = var7 == null ? var3 : var7.getStartOffset();
         int var9 = var7 == null ? var4 : var7.getEndOffset();
         Segment var10 = this.getText(var8, var9);
         var10.first();
         BreakIterator var11 = this.getBreaker();
         var11.setText((CharacterIterator)var10);
         int var12 = var4 + (var9 > var4 ? 1 : 0);

         while(true) {
            var12 = var11.preceding(var10.offset + (var12 - var8)) + (var8 - var10.offset);
            if (var12 <= var3) {
               SegmentCache.releaseSharedSegment(var10);
               this.breakSpots = new int[var6];
               System.arraycopy(var5, 0, this.breakSpots, 0, var6);
               break;
            }

            var5[var6++] = var12;
         }
      }

      var3 = -1;

      for(var4 = 0; var4 < this.breakSpots.length; ++var4) {
         int var13 = this.breakSpots[var4];
         if (var13 <= var2) {
            if (var13 > var1) {
               var3 = var13;
            }
            break;
         }
      }

      return var3;
   }

   private BreakIterator getBreaker() {
      Document var1 = this.getDocument();
      if (var1 != null && Boolean.TRUE.equals(var1.getProperty(AbstractDocument.MultiByteProperty))) {
         Container var2 = this.getContainer();
         Locale var3 = var2 == null ? Locale.getDefault() : var2.getLocale();
         return BreakIterator.getLineInstance(var3);
      } else {
         return new WhitespaceBasedBreakIterator();
      }
   }

   public View createFragment(int var1, int var2) {
      this.checkPainter();
      Element var3 = this.getElement();
      GlyphView var4 = (GlyphView)this.clone();
      var4.offset = var1 - var3.getStartOffset();
      var4.length = var2 - var1;
      var4.painter = this.painter.getPainter(var4, var1, var2);
      var4.justificationInfo = null;
      return var4;
   }

   public int getNextVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      if (var1 < -1) {
         throw new BadLocationException("invalid position", var1);
      } else {
         return this.painter.getNextVisualPositionFrom(this, var1, var2, var3, var4, var5);
      }
   }

   public void insertUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.justificationInfo = null;
      this.breakSpots = null;
      this.minimumSpan = -1.0F;
      this.syncCR();
      this.preferenceChanged((View)null, true, false);
   }

   public void removeUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.justificationInfo = null;
      this.breakSpots = null;
      this.minimumSpan = -1.0F;
      this.syncCR();
      this.preferenceChanged((View)null, true, false);
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.minimumSpan = -1.0F;
      this.syncCR();
      this.preferenceChanged((View)null, true, true);
   }

   private void syncCR() {
      if (this.impliedCR) {
         Element var1 = this.getElement().getParentElement();
         this.impliedCR = var1 != null && var1.getElementCount() > 1;
      }

   }

   void updateAfterChange() {
      this.breakSpots = null;
   }

   GlyphView.JustificationInfo getJustificationInfo(int var1) {
      if (this.justificationInfo != null) {
         return this.justificationInfo;
      } else {
         int var5 = this.getStartOffset();
         int var6 = this.getEndOffset();
         Segment var7 = this.getText(var5, var6);
         int var8 = var7.offset;
         int var9 = var7.offset + var7.count - 1;
         int var10 = var9 + 1;
         int var11 = var8 - 1;
         int var12 = var8 - 1;
         int var13 = 0;
         int var14 = 0;
         int var15 = 0;
         boolean var16 = false;
         BitSet var17 = new BitSet(var6 - var5 + 1);
         int var18 = var9;

         for(byte var19 = 0; var18 >= var8; --var18) {
            if (' ' == var7.array[var18]) {
               var17.set(var18 - var8);
               if (var19 == 0) {
                  ++var13;
               } else if (var19 == 1) {
                  var19 = 2;
                  var15 = 1;
               } else if (var19 == 2) {
                  ++var15;
               }
            } else {
               if ('\t' == var7.array[var18]) {
                  var16 = true;
                  break;
               }

               if (var19 == 0) {
                  if ('\n' != var7.array[var18] && '\r' != var7.array[var18]) {
                     var19 = 1;
                     var11 = var18;
                  }
               } else if (var19 != 1 && var19 == 2) {
                  var14 += var15;
                  var15 = 0;
               }

               var10 = var18;
            }
         }

         SegmentCache.releaseSharedSegment(var7);
         var18 = -1;
         if (var10 < var9) {
            var18 = var10 - var8;
         }

         int var20 = -1;
         if (var11 > var8) {
            var20 = var11 - var8;
         }

         this.justificationInfo = new GlyphView.JustificationInfo(var18, var20, var15, var14, var13, var16, var17);
         return this.justificationInfo;
      }
   }

   public abstract static class GlyphPainter {
      public abstract float getSpan(GlyphView var1, int var2, int var3, TabExpander var4, float var5);

      public abstract float getHeight(GlyphView var1);

      public abstract float getAscent(GlyphView var1);

      public abstract float getDescent(GlyphView var1);

      public abstract void paint(GlyphView var1, Graphics var2, Shape var3, int var4, int var5);

      public abstract Shape modelToView(GlyphView var1, int var2, Position.Bias var3, Shape var4) throws BadLocationException;

      public abstract int viewToModel(GlyphView var1, float var2, float var3, Shape var4, Position.Bias[] var5);

      public abstract int getBoundedPosition(GlyphView var1, int var2, float var3, float var4);

      public GlyphView.GlyphPainter getPainter(GlyphView var1, int var2, int var3) {
         return this;
      }

      public int getNextVisualPositionFrom(GlyphView var1, int var2, Position.Bias var3, Shape var4, int var5, Position.Bias[] var6) throws BadLocationException {
         int var7 = var1.getStartOffset();
         int var8 = var1.getEndOffset();
         switch(var5) {
         case 1:
         case 5:
            if (var2 != -1) {
               return -1;
            } else {
               Container var10 = var1.getContainer();
               if (var10 instanceof JTextComponent) {
                  Caret var11 = ((JTextComponent)var10).getCaret();
                  Point var12 = var11 != null ? var11.getMagicCaretPosition() : null;
                  if (var12 == null) {
                     var6[0] = Position.Bias.Forward;
                     return var7;
                  }

                  int var13 = var1.viewToModel((float)var12.x, 0.0F, var4, var6);
                  return var13;
               }

               return var2;
            }
         case 2:
         case 4:
         case 6:
         default:
            throw new IllegalArgumentException("Bad direction: " + var5);
         case 3:
            if (var7 == var1.getDocument().getLength()) {
               if (var2 == -1) {
                  var6[0] = Position.Bias.Forward;
                  return var7;
               }

               return -1;
            } else if (var2 == -1) {
               var6[0] = Position.Bias.Forward;
               return var7;
            } else if (var2 == var8) {
               return -1;
            } else {
               ++var2;
               if (var2 == var8) {
                  return -1;
               }

               var6[0] = Position.Bias.Forward;
               return var2;
            }
         case 7:
            if (var7 == var1.getDocument().getLength()) {
               if (var2 == -1) {
                  var6[0] = Position.Bias.Forward;
                  return var7;
               } else {
                  return -1;
               }
            } else if (var2 == -1) {
               var6[0] = Position.Bias.Forward;
               return var8 - 1;
            } else if (var2 == var7) {
               return -1;
            } else {
               var6[0] = Position.Bias.Forward;
               return var2 - 1;
            }
         }
      }
   }

   static class JustificationInfo {
      final int start;
      final int end;
      final int leadingSpaces;
      final int contentSpaces;
      final int trailingSpaces;
      final boolean hasTab;
      final BitSet spaceMap;

      JustificationInfo(int var1, int var2, int var3, int var4, int var5, boolean var6, BitSet var7) {
         this.start = var1;
         this.end = var2;
         this.leadingSpaces = var3;
         this.contentSpaces = var4;
         this.trailingSpaces = var5;
         this.hasTab = var6;
         this.spaceMap = var7;
      }
   }
}
