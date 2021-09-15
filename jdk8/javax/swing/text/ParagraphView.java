package javax.swing.text;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;

public class ParagraphView extends FlowView implements TabExpander {
   private int justification;
   private float lineSpacing;
   protected int firstLineIndent = 0;
   private int tabBase;
   static Class i18nStrategy;
   static char[] tabChars = new char[1];
   static char[] tabDecimalChars;

   public ParagraphView(Element var1) {
      super(var1, 1);
      this.setPropertiesFromAttributes();
      Document var2 = var1.getDocument();
      Object var3 = var2.getProperty("i18n");
      if (var3 != null && var3.equals(Boolean.TRUE)) {
         try {
            if (i18nStrategy == null) {
               String var4 = "javax.swing.text.TextLayoutStrategy";
               ClassLoader var5 = this.getClass().getClassLoader();
               if (var5 != null) {
                  i18nStrategy = var5.loadClass(var4);
               } else {
                  i18nStrategy = Class.forName(var4);
               }
            }

            Object var7 = i18nStrategy.newInstance();
            if (var7 instanceof FlowView.FlowStrategy) {
               this.strategy = (FlowView.FlowStrategy)var7;
            }
         } catch (Throwable var6) {
            throw new StateInvariantError("ParagraphView: Can't create i18n strategy: " + var6.getMessage());
         }
      }

   }

   protected void setJustification(int var1) {
      this.justification = var1;
   }

   protected void setLineSpacing(float var1) {
      this.lineSpacing = var1;
   }

   protected void setFirstLineIndent(float var1) {
      this.firstLineIndent = (int)var1;
   }

   protected void setPropertiesFromAttributes() {
      AttributeSet var1 = this.getAttributes();
      if (var1 != null) {
         this.setParagraphInsets(var1);
         Integer var2 = (Integer)var1.getAttribute(StyleConstants.Alignment);
         int var3;
         if (var2 == null) {
            Document var4 = this.getElement().getDocument();
            Object var5 = var4.getProperty(TextAttribute.RUN_DIRECTION);
            if (var5 != null && var5.equals(TextAttribute.RUN_DIRECTION_RTL)) {
               var3 = 2;
            } else {
               var3 = 0;
            }
         } else {
            var3 = var2;
         }

         this.setJustification(var3);
         this.setLineSpacing(StyleConstants.getLineSpacing(var1));
         this.setFirstLineIndent(StyleConstants.getFirstLineIndent(var1));
      }

   }

   protected int getLayoutViewCount() {
      return this.layoutPool.getViewCount();
   }

   protected View getLayoutView(int var1) {
      return this.layoutPool.getView(var1);
   }

   protected int getNextNorthSouthVisualPositionFrom(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      int var6;
      if (var1 == -1) {
         var6 = var4 == 1 ? this.getViewCount() - 1 : 0;
      } else {
         if (var2 == Position.Bias.Backward && var1 > 0) {
            var6 = this.getViewIndexAtPosition(var1 - 1);
         } else {
            var6 = this.getViewIndexAtPosition(var1);
         }

         if (var4 == 1) {
            if (var6 == 0) {
               return -1;
            }

            --var6;
         } else {
            ++var6;
            if (var6 >= this.getViewCount()) {
               return -1;
            }
         }
      }

      JTextComponent var7 = (JTextComponent)this.getContainer();
      Caret var8 = var7.getCaret();
      Point var9 = var8 != null ? var8.getMagicCaretPosition() : null;
      int var10;
      if (var9 == null) {
         Rectangle var11;
         try {
            var11 = var7.getUI().modelToView(var7, var1, var2);
         } catch (BadLocationException var13) {
            var11 = null;
         }

         if (var11 == null) {
            var10 = 0;
         } else {
            var10 = var11.getBounds().x;
         }
      } else {
         var10 = var9.x;
      }

      return this.getClosestPositionTo(var1, var2, var3, var4, var5, var6, var10);
   }

   protected int getClosestPositionTo(int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5, int var6, int var7) throws BadLocationException {
      JTextComponent var8 = (JTextComponent)this.getContainer();
      Document var9 = this.getDocument();
      View var10 = this.getView(var6);
      int var11 = -1;
      var5[0] = Position.Bias.Forward;
      int var12 = 0;

      for(int var13 = var10.getViewCount(); var12 < var13; ++var12) {
         View var14 = var10.getView(var12);
         int var15 = var14.getStartOffset();
         boolean var16 = AbstractDocument.isLeftToRight(var9, var15, var15 + 1);
         if (var16) {
            var11 = var15;

            for(int var19 = var14.getEndOffset(); var11 < var19; ++var11) {
               float var18 = (float)var8.modelToView(var11).getBounds().x;
               if (var18 >= (float)var7) {
                  do {
                     ++var11;
                  } while(var11 < var19 && (float)var8.modelToView(var11).getBounds().x == var18);

                  --var11;
                  return var11;
               }
            }

            --var11;
         } else {
            for(var11 = var14.getEndOffset() - 1; var11 >= var15; --var11) {
               float var17 = (float)var8.modelToView(var11).getBounds().x;
               if (var17 >= (float)var7) {
                  do {
                     --var11;
                  } while(var11 >= var15 && (float)var8.modelToView(var11).getBounds().x == var17);

                  ++var11;
                  return var11;
               }
            }

            ++var11;
         }
      }

      if (var11 == -1) {
         return this.getStartOffset();
      } else {
         return var11;
      }
   }

   protected boolean flipEastAndWestAtEnds(int var1, Position.Bias var2) {
      Document var3 = this.getDocument();
      var1 = this.getStartOffset();
      return !AbstractDocument.isLeftToRight(var3, var1, var1 + 1);
   }

   public int getFlowSpan(int var1) {
      View var2 = this.getView(var1);
      int var3 = 0;
      if (var2 instanceof ParagraphView.Row) {
         ParagraphView.Row var4 = (ParagraphView.Row)var2;
         var3 = var4.getLeftInset() + var4.getRightInset();
      }

      return this.layoutSpan == Integer.MAX_VALUE ? this.layoutSpan : this.layoutSpan - var3;
   }

   public int getFlowStart(int var1) {
      View var2 = this.getView(var1);
      short var3 = 0;
      if (var2 instanceof ParagraphView.Row) {
         ParagraphView.Row var4 = (ParagraphView.Row)var2;
         var3 = var4.getLeftInset();
      }

      return this.tabBase + var3;
   }

   protected View createRow() {
      return new ParagraphView.Row(this.getElement());
   }

   public float nextTabStop(float var1, int var2) {
      if (this.justification != 0) {
         return var1 + 10.0F;
      } else {
         var1 -= (float)this.tabBase;
         TabSet var3 = this.getTabSet();
         if (var3 == null) {
            return (float)(this.tabBase + ((int)var1 / 72 + 1) * 72);
         } else {
            TabStop var4 = var3.getTabAfter(var1 + 0.01F);
            if (var4 == null) {
               return (float)this.tabBase + var1 + 5.0F;
            } else {
               int var5 = var4.getAlignment();
               int var6;
               switch(var5) {
               case 0:
               case 3:
               default:
                  return (float)this.tabBase + var4.getPosition();
               case 1:
               case 2:
                  var6 = this.findOffsetToCharactersInString(tabChars, var2 + 1);
                  break;
               case 4:
                  var6 = this.findOffsetToCharactersInString(tabDecimalChars, var2 + 1);
                  break;
               case 5:
                  return (float)this.tabBase + var4.getPosition();
               }

               if (var6 == -1) {
                  var6 = this.getEndOffset();
               }

               float var7 = this.getPartialSize(var2 + 1, var6);
               switch(var5) {
               case 1:
               case 4:
                  return (float)this.tabBase + Math.max(var1, var4.getPosition() - var7);
               case 2:
                  return (float)this.tabBase + Math.max(var1, var4.getPosition() - var7 / 2.0F);
               case 3:
               default:
                  return var1;
               }
            }
         }
      }
   }

   protected TabSet getTabSet() {
      return StyleConstants.getTabSet(this.getElement().getAttributes());
   }

   protected float getPartialSize(int var1, int var2) {
      float var3 = 0.0F;
      int var5 = this.getViewCount();
      int var4 = this.getElement().getElementIndex(var1);

      int var7;
      for(var5 = this.layoutPool.getViewCount(); var1 < var2 && var4 < var5; var1 = var7) {
         View var6 = this.layoutPool.getView(var4++);
         var7 = var6.getEndOffset();
         int var8 = Math.min(var2, var7);
         if (var6 instanceof TabableView) {
            var3 += ((TabableView)var6).getPartialSpan(var1, var8);
         } else {
            if (var1 != var6.getStartOffset() || var8 != var6.getEndOffset()) {
               return 0.0F;
            }

            var3 += var6.getPreferredSpan(0);
         }
      }

      return var3;
   }

   protected int findOffsetToCharactersInString(char[] var1, int var2) {
      int var3 = var1.length;
      int var4 = this.getEndOffset();
      Segment var5 = new Segment();

      try {
         this.getDocument().getText(var2, var4 - var2, var5);
      } catch (BadLocationException var10) {
         return -1;
      }

      int var6 = var5.offset;

      for(int var7 = var5.offset + var5.count; var6 < var7; ++var6) {
         char var8 = var5.array[var6];

         for(int var9 = 0; var9 < var3; ++var9) {
            if (var8 == var1[var9]) {
               return var6 - var5.offset + var2;
            }
         }
      }

      return -1;
   }

   protected float getTabBase() {
      return (float)this.tabBase;
   }

   public void paint(Graphics var1, Shape var2) {
      Rectangle var3 = var2 instanceof Rectangle ? (Rectangle)var2 : var2.getBounds();
      this.tabBase = var3.x + this.getLeftInset();
      super.paint(var1, var2);
      if (this.firstLineIndent < 0) {
         Shape var4 = this.getChildAllocation(0, var2);
         if (var4 != null && var4.intersects(var3)) {
            int var5 = var3.x + this.getLeftInset() + this.firstLineIndent;
            int var6 = var3.y + this.getTopInset();
            Rectangle var7 = var1.getClipBounds();
            this.tempRect.x = var5 + this.getOffset(0, 0);
            this.tempRect.y = var6 + this.getOffset(1, 0);
            this.tempRect.width = this.getSpan(0, 0) - this.firstLineIndent;
            this.tempRect.height = this.getSpan(1, 0);
            if (this.tempRect.intersects(var7)) {
               this.tempRect.x -= this.firstLineIndent;
               this.paintChild(var1, this.tempRect, 0);
            }
         }
      }

   }

   public float getAlignment(int var1) {
      switch(var1) {
      case 0:
         return 0.5F;
      case 1:
         float var2 = 0.5F;
         if (this.getViewCount() != 0) {
            int var3 = (int)this.getPreferredSpan(1);
            View var4 = this.getView(0);
            int var5 = (int)var4.getPreferredSpan(1);
            var2 = var3 != 0 ? (float)(var5 / 2) / (float)var3 : 0.0F;
         }

         return var2;
      default:
         throw new IllegalArgumentException("Invalid axis: " + var1);
      }
   }

   public View breakView(int var1, float var2, Shape var3) {
      if (var1 == 1) {
         if (var3 != null) {
            Rectangle var4 = var3.getBounds();
            this.setSize((float)var4.width, (float)var4.height);
         }

         return this;
      } else {
         return this;
      }
   }

   public int getBreakWeight(int var1, float var2) {
      return var1 == 1 ? 0 : 0;
   }

   protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
      var2 = super.calculateMinorAxisRequirements(var1, var2);
      float var3 = 0.0F;
      float var4 = 0.0F;
      int var5 = this.getLayoutViewCount();

      for(int var6 = 0; var6 < var5; ++var6) {
         View var7 = this.getLayoutView(var6);
         float var8 = var7.getMinimumSpan(var1);
         if (var7.getBreakWeight(var1, 0.0F, var7.getMaximumSpan(var1)) > 0) {
            int var9 = var7.getStartOffset();
            int var10 = var7.getEndOffset();
            float var11 = this.findEdgeSpan(var7, var1, var9, var9, var10);
            float var12 = this.findEdgeSpan(var7, var1, var10, var9, var10);
            var4 += var11;
            var3 = Math.max(var3, Math.max(var8, var4));
            var4 = var12;
         } else {
            var4 += var8;
            var3 = Math.max(var3, var4);
         }
      }

      var2.minimum = Math.max(var2.minimum, (int)var3);
      var2.preferred = Math.max(var2.minimum, var2.preferred);
      var2.maximum = Math.max(var2.preferred, var2.maximum);
      return var2;
   }

   private float findEdgeSpan(View var1, int var2, int var3, int var4, int var5) {
      int var6 = var5 - var4;
      if (var6 <= 1) {
         return var1.getMinimumSpan(var2);
      } else {
         int var7 = var4 + var6 / 2;
         boolean var8 = var7 > var3;
         View var9 = var8 ? var1.createFragment(var3, var7) : var1.createFragment(var7, var3);
         boolean var10 = var9.getBreakWeight(var2, 0.0F, var9.getMaximumSpan(var2)) > 0;
         if (var10 == var8) {
            var5 = var7;
         } else {
            var4 = var7;
         }

         return this.findEdgeSpan(var9, var2, var3, var4, var5);
      }
   }

   public void changedUpdate(DocumentEvent var1, Shape var2, ViewFactory var3) {
      this.setPropertiesFromAttributes();
      this.layoutChanged(0);
      this.layoutChanged(1);
      super.changedUpdate(var1, var2, var3);
   }

   static {
      tabChars[0] = '\t';
      tabDecimalChars = new char[2];
      tabDecimalChars[0] = '\t';
      tabDecimalChars[1] = '.';
   }

   class Row extends BoxView {
      static final int SPACE_ADDON = 0;
      static final int SPACE_ADDON_LEFTOVER_END = 1;
      static final int START_JUSTIFIABLE = 2;
      static final int END_JUSTIFIABLE = 3;
      int[] justificationData = null;

      Row(Element var2) {
         super(var2, 0);
      }

      protected void loadChildren(ViewFactory var1) {
      }

      public AttributeSet getAttributes() {
         View var1 = this.getParent();
         return var1 != null ? var1.getAttributes() : null;
      }

      public float getAlignment(int var1) {
         if (var1 == 0) {
            switch(ParagraphView.this.justification) {
            case 0:
               return 0.0F;
            case 1:
               return 0.5F;
            case 2:
               return 1.0F;
            case 3:
               float var2 = 0.5F;
               if (this.isJustifiableDocument()) {
                  var2 = 0.0F;
               }

               return var2;
            }
         }

         return super.getAlignment(var1);
      }

      public Shape modelToView(int var1, Shape var2, Position.Bias var3) throws BadLocationException {
         Rectangle var4 = var2.getBounds();
         View var5 = this.getViewAtPosition(var1, var4);
         if (var5 != null && !var5.getElement().isLeaf()) {
            return super.modelToView(var1, var2, var3);
         } else {
            var4 = var2.getBounds();
            int var6 = var4.height;
            int var7 = var4.y;
            Shape var8 = super.modelToView(var1, var2, var3);
            var4 = var8.getBounds();
            var4.height = var6;
            var4.y = var7;
            return var4;
         }
      }

      public int getStartOffset() {
         int var1 = Integer.MAX_VALUE;
         int var2 = this.getViewCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            View var4 = this.getView(var3);
            var1 = Math.min(var1, var4.getStartOffset());
         }

         return var1;
      }

      public int getEndOffset() {
         int var1 = 0;
         int var2 = this.getViewCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            View var4 = this.getView(var3);
            var1 = Math.max(var1, var4.getEndOffset());
         }

         return var1;
      }

      protected void layoutMinorAxis(int var1, int var2, int[] var3, int[] var4) {
         this.baselineLayout(var1, var2, var3, var4);
      }

      protected SizeRequirements calculateMinorAxisRequirements(int var1, SizeRequirements var2) {
         return this.baselineRequirements(var1, var2);
      }

      private boolean isLastRow() {
         View var1;
         return (var1 = this.getParent()) == null || this == var1.getView(var1.getViewCount() - 1);
      }

      private boolean isBrokenRow() {
         boolean var1 = false;
         int var2 = this.getViewCount();
         if (var2 > 0) {
            View var3 = this.getView(var2 - 1);
            if (var3.getBreakWeight(0, 0.0F, 0.0F) >= 3000) {
               var1 = true;
            }
         }

         return var1;
      }

      private boolean isJustifiableDocument() {
         return !Boolean.TRUE.equals(this.getDocument().getProperty("i18n"));
      }

      private boolean isJustifyEnabled() {
         boolean var1 = ParagraphView.this.justification == 3;
         var1 = var1 && this.isJustifiableDocument();
         var1 = var1 && !this.isLastRow();
         var1 = var1 && !this.isBrokenRow();
         return var1;
      }

      protected SizeRequirements calculateMajorAxisRequirements(int var1, SizeRequirements var2) {
         int[] var3 = this.justificationData;
         this.justificationData = null;
         SizeRequirements var4 = super.calculateMajorAxisRequirements(var1, var2);
         if (this.isJustifyEnabled()) {
            this.justificationData = var3;
         }

         return var4;
      }

      protected void layoutMajorAxis(int var1, int var2, int[] var3, int[] var4) {
         int[] var5 = this.justificationData;
         this.justificationData = null;
         super.layoutMajorAxis(var1, var2, var3, var4);
         if (this.isJustifyEnabled()) {
            int var6 = 0;
            int[] var7 = var4;
            int var8 = var4.length;

            int var9;
            int var10;
            for(var9 = 0; var9 < var8; ++var9) {
               var10 = var7[var9];
               var6 += var10;
            }

            if (var6 != var1) {
               int var20 = 0;
               var8 = -1;
               var9 = -1;
               var10 = 0;
               int var11 = this.getStartOffset();
               int var12 = this.getEndOffset();
               int[] var13 = new int[var12 - var11];
               Arrays.fill((int[])var13, (int)0);

               int var14;
               int var17;
               int var18;
               for(var14 = this.getViewCount() - 1; var14 >= 0; --var14) {
                  View var15 = this.getView(var14);
                  if (var15 instanceof GlyphView) {
                     GlyphView.JustificationInfo var16 = ((GlyphView)var15).getJustificationInfo(var11);
                     var17 = var15.getStartOffset();
                     var18 = var17 - var11;

                     for(int var19 = 0; var19 < var16.spaceMap.length(); ++var19) {
                        if (var16.spaceMap.get(var19)) {
                           var13[var19 + var18] = 1;
                        }
                     }

                     if (var8 > 0) {
                        if (var16.end >= 0) {
                           var20 += var16.trailingSpaces;
                        } else {
                           var10 += var16.trailingSpaces;
                        }
                     }

                     if (var16.start >= 0) {
                        var8 = var16.start + var17;
                        var20 += var10;
                     }

                     if (var16.end >= 0 && var9 < 0) {
                        var9 = var16.end + var17;
                     }

                     var20 += var16.contentSpaces;
                     var10 = var16.leadingSpaces;
                     if (var16.hasTab) {
                        break;
                     }
                  }
               }

               if (var20 > 0) {
                  var14 = var1 - var6;
                  int var21 = var20 > 0 ? var14 / var20 : 0;
                  int var22 = -1;
                  var17 = var8 - var11;

                  for(var18 = var14 - var21 * var20; var18 > 0; ++var17) {
                     var22 = var17;
                     var18 -= var13[var17];
                  }

                  if (var21 > 0 || var22 >= 0) {
                     this.justificationData = var5 != null ? var5 : new int[4];
                     this.justificationData[0] = var21;
                     this.justificationData[1] = var22;
                     this.justificationData[2] = var8 - var11;
                     this.justificationData[3] = var9 - var11;
                     super.layoutMajorAxis(var1, var2, var3, var4);
                  }

               }
            }
         }
      }

      public float getMaximumSpan(int var1) {
         float var2;
         if (0 == var1 && this.isJustifyEnabled()) {
            var2 = Float.MAX_VALUE;
         } else {
            var2 = super.getMaximumSpan(var1);
         }

         return var2;
      }

      protected int getViewIndexAtPosition(int var1) {
         if (var1 >= this.getStartOffset() && var1 < this.getEndOffset()) {
            for(int var2 = this.getViewCount() - 1; var2 >= 0; --var2) {
               View var3 = this.getView(var2);
               if (var1 >= var3.getStartOffset() && var1 < var3.getEndOffset()) {
                  return var2;
               }
            }

            return -1;
         } else {
            return -1;
         }
      }

      protected short getLeftInset() {
         int var2 = 0;
         View var1;
         if ((var1 = this.getParent()) != null && this == var1.getView(0)) {
            var2 = ParagraphView.this.firstLineIndent;
         }

         return (short)(super.getLeftInset() + var2);
      }

      protected short getBottomInset() {
         return (short)((int)((float)super.getBottomInset() + (float)(this.minorRequest != null ? this.minorRequest.preferred : 0) * ParagraphView.this.lineSpacing));
      }
   }
}
