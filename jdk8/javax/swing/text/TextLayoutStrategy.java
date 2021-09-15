package javax.swing.text;

import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import sun.font.BidiUtils;
import sun.swing.SwingUtilities2;

class TextLayoutStrategy extends FlowView.FlowStrategy {
   private LineBreakMeasurer measurer;
   private TextLayoutStrategy.AttributedSegment text = new TextLayoutStrategy.AttributedSegment();

   public TextLayoutStrategy() {
   }

   public void insertUpdate(FlowView var1, DocumentEvent var2, Rectangle var3) {
      this.sync(var1);
      super.insertUpdate(var1, var2, var3);
   }

   public void removeUpdate(FlowView var1, DocumentEvent var2, Rectangle var3) {
      this.sync(var1);
      super.removeUpdate(var1, var2, var3);
   }

   public void changedUpdate(FlowView var1, DocumentEvent var2, Rectangle var3) {
      this.sync(var1);
      super.changedUpdate(var1, var2, var3);
   }

   public void layout(FlowView var1) {
      super.layout(var1);
   }

   protected int layoutRow(FlowView var1, int var2, int var3) {
      int var4 = super.layoutRow(var1, var2, var3);
      View var5 = var1.getView(var2);
      Document var6 = var1.getDocument();
      Object var7 = var6.getProperty("i18n");
      if (var7 != null && var7.equals(Boolean.TRUE)) {
         int var8 = var5.getViewCount();
         if (var8 > 1) {
            AbstractDocument var9 = (AbstractDocument)var1.getDocument();
            Element var10 = var9.getBidiRootElement();
            byte[] var11 = new byte[var8];
            View[] var12 = new View[var8];

            for(int var13 = 0; var13 < var8; ++var13) {
               View var14 = var5.getView(var13);
               int var15 = var10.getElementIndex(var14.getStartOffset());
               Element var16 = var10.getElement(var15);
               var11[var13] = (byte)StyleConstants.getBidiLevel(var16.getAttributes());
               var12[var13] = var14;
            }

            BidiUtils.reorderVisually(var11, var12);
            var5.replace(0, var8, var12);
         }
      }

      return var4;
   }

   protected void adjustRow(FlowView var1, int var2, int var3, int var4) {
   }

   protected View createView(FlowView var1, int var2, int var3, int var4) {
      View var5 = this.getLogicalView(var1);
      var1.getView(var4);
      boolean var7 = this.viewBuffer.size() != 0;
      int var8 = var5.getViewIndex(var2, Position.Bias.Forward);
      View var9 = var5.getView(var8);
      int var10 = this.getLimitingOffset(var9, var2, var3, var7);
      if (var10 == var2) {
         return null;
      } else {
         View var11;
         if (var2 == var9.getStartOffset() && var10 == var9.getEndOffset()) {
            var11 = var9;
         } else {
            var11 = var9.createFragment(var2, var10);
         }

         if (var11 instanceof GlyphView && this.measurer != null) {
            boolean var12 = false;
            int var13 = var11.getStartOffset();
            int var14 = var11.getEndOffset();
            if (var14 - var13 == 1) {
               Segment var15 = ((GlyphView)var11).getText(var13, var14);
               char var16 = var15.first();
               if (var16 == '\t') {
                  var12 = true;
               }
            }

            TextLayout var17 = var12 ? null : this.measurer.nextLayout((float)var3, this.text.toIteratorIndex(var10), var7);
            if (var17 != null) {
               ((GlyphView)var11).setGlyphPainter(new GlyphPainter2(var17));
            }
         }

         return var11;
      }
   }

   int getLimitingOffset(View var1, int var2, int var3, boolean var4) {
      int var5 = var1.getEndOffset();
      Document var6 = var1.getDocument();
      if (var6 instanceof AbstractDocument) {
         AbstractDocument var7 = (AbstractDocument)var6;
         Element var8 = var7.getBidiRootElement();
         if (var8.getElementCount() > 1) {
            int var9 = var8.getElementIndex(var2);
            Element var10 = var8.getElement(var9);
            var5 = Math.min(var10.getEndOffset(), var5);
         }
      }

      if (var1 instanceof GlyphView) {
         Segment var11 = ((GlyphView)var1).getText(var2, var5);
         char var13 = var11.first();
         if (var13 == '\t') {
            var5 = var2 + 1;
         } else {
            for(var13 = var11.next(); var13 != '\uffff'; var13 = var11.next()) {
               if (var13 == '\t') {
                  var5 = var2 + var11.getIndex() - var11.getBeginIndex();
                  break;
               }
            }
         }
      }

      int var12 = this.text.toIteratorIndex(var5);
      int var14;
      if (this.measurer != null) {
         var14 = this.text.toIteratorIndex(var2);
         if (this.measurer.getPosition() != var14) {
            this.measurer.setPosition(var14);
         }

         var12 = this.measurer.nextOffset((float)var3, var12, var4);
      }

      var14 = this.text.toModelPosition(var12);
      return var14;
   }

   void sync(FlowView var1) {
      View var2 = this.getLogicalView(var1);
      this.text.setView(var2);
      Container var3 = var1.getContainer();
      FontRenderContext var4 = SwingUtilities2.getFontRenderContext(var3);
      Container var6 = var1.getContainer();
      BreakIterator var5;
      if (var6 != null) {
         var5 = BreakIterator.getLineInstance(var6.getLocale());
      } else {
         var5 = BreakIterator.getLineInstance();
      }

      Object var7 = null;
      if (var6 instanceof JComponent) {
         var7 = ((JComponent)var6).getClientProperty(TextAttribute.NUMERIC_SHAPING);
      }

      this.text.setShaper(var7);
      this.measurer = new LineBreakMeasurer(this.text, var5, var4);
      int var8 = var2.getViewCount();

      for(int var9 = 0; var9 < var8; ++var9) {
         View var10 = var2.getView(var9);
         if (var10 instanceof GlyphView) {
            int var11 = var10.getStartOffset();
            int var12 = var10.getEndOffset();
            this.measurer.setPosition(this.text.toIteratorIndex(var11));
            TextLayout var13 = this.measurer.nextLayout(Float.MAX_VALUE, this.text.toIteratorIndex(var12), false);
            ((GlyphView)var10).setGlyphPainter(new GlyphPainter2(var13));
         }
      }

      this.measurer.setPosition(this.text.getBeginIndex());
   }

   static class AttributedSegment extends Segment implements AttributedCharacterIterator {
      View v;
      static Set<AttributedCharacterIterator.Attribute> keys = new HashSet();
      private Object shaper = null;

      View getView() {
         return this.v;
      }

      void setView(View var1) {
         this.v = var1;
         Document var2 = var1.getDocument();
         int var3 = var1.getStartOffset();
         int var4 = var1.getEndOffset();

         try {
            var2.getText(var3, var4 - var3, this);
         } catch (BadLocationException var6) {
            throw new IllegalArgumentException("Invalid view");
         }

         this.first();
      }

      int getFontBoundary(int var1, int var2) {
         View var3 = this.v.getView(var1);
         Font var4 = this.getFont(var1);

         for(var1 += var2; var1 >= 0 && var1 < this.v.getViewCount(); var1 += var2) {
            Font var5 = this.getFont(var1);
            if (var5 != var4) {
               break;
            }

            var3 = this.v.getView(var1);
         }

         return var2 < 0 ? var3.getStartOffset() : var3.getEndOffset();
      }

      Font getFont(int var1) {
         View var2 = this.v.getView(var1);
         return var2 instanceof GlyphView ? ((GlyphView)var2).getFont() : null;
      }

      int toModelPosition(int var1) {
         return this.v.getStartOffset() + (var1 - this.getBeginIndex());
      }

      int toIteratorIndex(int var1) {
         return var1 - this.v.getStartOffset() + this.getBeginIndex();
      }

      private void setShaper(Object var1) {
         this.shaper = var1;
      }

      public int getRunStart() {
         int var1 = this.toModelPosition(this.getIndex());
         int var2 = this.v.getViewIndex(var1, Position.Bias.Forward);
         View var3 = this.v.getView(var2);
         return this.toIteratorIndex(var3.getStartOffset());
      }

      public int getRunStart(AttributedCharacterIterator.Attribute var1) {
         if (var1 instanceof TextAttribute) {
            int var2 = this.toModelPosition(this.getIndex());
            int var3 = this.v.getViewIndex(var2, Position.Bias.Forward);
            if (var1 == TextAttribute.FONT) {
               return this.toIteratorIndex(this.getFontBoundary(var3, -1));
            }
         }

         return this.getBeginIndex();
      }

      public int getRunStart(Set<? extends AttributedCharacterIterator.Attribute> var1) {
         int var2 = this.getBeginIndex();
         Object[] var3 = var1.toArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            TextAttribute var5 = (TextAttribute)var3[var4];
            var2 = Math.max(this.getRunStart((AttributedCharacterIterator.Attribute)var5), var2);
         }

         return Math.min(this.getIndex(), var2);
      }

      public int getRunLimit() {
         int var1 = this.toModelPosition(this.getIndex());
         int var2 = this.v.getViewIndex(var1, Position.Bias.Forward);
         View var3 = this.v.getView(var2);
         return this.toIteratorIndex(var3.getEndOffset());
      }

      public int getRunLimit(AttributedCharacterIterator.Attribute var1) {
         if (var1 instanceof TextAttribute) {
            int var2 = this.toModelPosition(this.getIndex());
            int var3 = this.v.getViewIndex(var2, Position.Bias.Forward);
            if (var1 == TextAttribute.FONT) {
               return this.toIteratorIndex(this.getFontBoundary(var3, 1));
            }
         }

         return this.getEndIndex();
      }

      public int getRunLimit(Set<? extends AttributedCharacterIterator.Attribute> var1) {
         int var2 = this.getEndIndex();
         Object[] var3 = var1.toArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            TextAttribute var5 = (TextAttribute)var3[var4];
            var2 = Math.min(this.getRunLimit((AttributedCharacterIterator.Attribute)var5), var2);
         }

         return Math.max(this.getIndex(), var2);
      }

      public Map<AttributedCharacterIterator.Attribute, Object> getAttributes() {
         Object[] var1 = keys.toArray();
         Hashtable var2 = new Hashtable();

         for(int var3 = 0; var3 < var1.length; ++var3) {
            TextAttribute var4 = (TextAttribute)var1[var3];
            Object var5 = this.getAttribute(var4);
            if (var5 != null) {
               var2.put(var4, var5);
            }
         }

         return var2;
      }

      public Object getAttribute(AttributedCharacterIterator.Attribute var1) {
         int var2 = this.toModelPosition(this.getIndex());
         int var3 = this.v.getViewIndex(var2, Position.Bias.Forward);
         if (var1 == TextAttribute.FONT) {
            return this.getFont(var3);
         } else if (var1 == TextAttribute.RUN_DIRECTION) {
            return this.v.getDocument().getProperty(TextAttribute.RUN_DIRECTION);
         } else {
            return var1 == TextAttribute.NUMERIC_SHAPING ? this.shaper : null;
         }
      }

      public Set<AttributedCharacterIterator.Attribute> getAllAttributeKeys() {
         return keys;
      }

      static {
         keys.add(TextAttribute.FONT);
         keys.add(TextAttribute.RUN_DIRECTION);
         keys.add(TextAttribute.NUMERIC_SHAPING);
      }
   }
}
