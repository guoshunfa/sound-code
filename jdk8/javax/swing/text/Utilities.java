package javax.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;

public class Utilities {
   static JComponent getJComponent(View var0) {
      if (var0 != null) {
         Container var1 = var0.getContainer();
         if (var1 instanceof JComponent) {
            return (JComponent)var1;
         }
      }

      return null;
   }

   public static final int drawTabbedText(Segment var0, int var1, int var2, Graphics var3, TabExpander var4, int var5) {
      return drawTabbedText((View)null, var0, var1, var2, var3, var4, var5);
   }

   static final int drawTabbedText(View var0, Segment var1, int var2, int var3, Graphics var4, TabExpander var5, int var6) {
      return drawTabbedText(var0, var1, var2, var3, var4, var5, var6, (int[])null);
   }

   static final int drawTabbedText(View var0, Segment var1, int var2, int var3, Graphics var4, TabExpander var5, int var6, int[] var7) {
      JComponent var8 = getJComponent(var0);
      FontMetrics var9 = SwingUtilities2.getFontMetrics(var8, var4);
      int var10 = var2;
      char[] var11 = var1.array;
      int var12 = var1.offset;
      int var13 = 0;
      int var14 = var1.offset;
      int var15 = 0;
      int var16 = -1;
      int var17 = 0;
      int var18 = 0;
      int var19;
      if (var7 != null) {
         var19 = -var6 + var12;
         View var20 = null;
         if (var0 != null && (var20 = var0.getParent()) != null) {
            var19 += var20.getStartOffset();
         }

         var15 = var7[0];
         var16 = var7[1] + var19;
         var17 = var7[2] + var19;
         var18 = var7[3] + var19;
      }

      var19 = var1.offset + var1.count;

      for(int var21 = var12; var21 < var19; ++var21) {
         if (var11[var21] != '\t' && (var15 == 0 && var21 > var16 || var11[var21] != ' ' || var17 > var21 || var21 > var18)) {
            if (var11[var21] != '\n' && var11[var21] != '\r') {
               ++var13;
            } else {
               if (var13 > 0) {
                  var10 = SwingUtilities2.drawChars(var8, var4, var11, var14, var13, var2, var3);
                  var13 = 0;
               }

               var14 = var21 + 1;
               var2 = var10;
            }
         } else {
            if (var13 > 0) {
               var10 = SwingUtilities2.drawChars(var8, var4, var11, var14, var13, var2, var3);
               var13 = 0;
            }

            var14 = var21 + 1;
            if (var11[var21] == '\t') {
               if (var5 != null) {
                  var10 = (int)var5.nextTabStop((float)var10, var6 + var21 - var12);
               } else {
                  var10 += var9.charWidth(' ');
               }
            } else if (var11[var21] == ' ') {
               var10 += var9.charWidth(' ') + var15;
               if (var21 <= var16) {
                  ++var10;
               }
            }

            var2 = var10;
         }
      }

      if (var13 > 0) {
         var10 = SwingUtilities2.drawChars(var8, var4, var11, var14, var13, var2, var3);
      }

      return var10;
   }

   public static final int getTabbedTextWidth(Segment var0, FontMetrics var1, int var2, TabExpander var3, int var4) {
      return getTabbedTextWidth((View)null, var0, var1, var2, var3, var4, (int[])null);
   }

   static final int getTabbedTextWidth(View var0, Segment var1, FontMetrics var2, int var3, TabExpander var4, int var5, int[] var6) {
      int var7 = var3;
      char[] var8 = var1.array;
      int var9 = var1.offset;
      int var10 = var1.offset + var1.count;
      int var11 = 0;
      int var12 = 0;
      int var13 = -1;
      int var14 = 0;
      int var15 = 0;
      int var16;
      if (var6 != null) {
         var16 = -var5 + var9;
         View var17 = null;
         if (var0 != null && (var17 = var0.getParent()) != null) {
            var16 += var17.getStartOffset();
         }

         var12 = var6[0];
         var13 = var6[1] + var16;
         var14 = var6[2] + var16;
         var15 = var6[3] + var16;
      }

      for(var16 = var9; var16 < var10; ++var16) {
         if (var8[var16] != '\t' && (var12 == 0 && var16 > var13 || var8[var16] != ' ' || var14 > var16 || var16 > var15)) {
            if (var8[var16] == '\n') {
               var7 += var2.charsWidth(var8, var16 - var11, var11);
               var11 = 0;
            } else {
               ++var11;
            }
         } else {
            var7 += var2.charsWidth(var8, var16 - var11, var11);
            var11 = 0;
            if (var8[var16] == '\t') {
               if (var4 != null) {
                  var7 = (int)var4.nextTabStop((float)var7, var5 + var16 - var9);
               } else {
                  var7 += var2.charWidth(' ');
               }
            } else if (var8[var16] == ' ') {
               var7 += var2.charWidth(' ') + var12;
               if (var16 <= var13) {
                  ++var7;
               }
            }
         }
      }

      var7 += var2.charsWidth(var8, var10 - var11, var11);
      return var7 - var3;
   }

   public static final int getTabbedTextOffset(Segment var0, FontMetrics var1, int var2, int var3, TabExpander var4, int var5) {
      return getTabbedTextOffset(var0, var1, var2, var3, var4, var5, true);
   }

   static final int getTabbedTextOffset(View var0, Segment var1, FontMetrics var2, int var3, int var4, TabExpander var5, int var6, int[] var7) {
      return getTabbedTextOffset(var0, var1, var2, var3, var4, var5, var6, true, var7);
   }

   public static final int getTabbedTextOffset(Segment var0, FontMetrics var1, int var2, int var3, TabExpander var4, int var5, boolean var6) {
      return getTabbedTextOffset((View)null, var0, var1, var2, var3, var4, var5, var6, (int[])null);
   }

   static final int getTabbedTextOffset(View var0, Segment var1, FontMetrics var2, int var3, int var4, TabExpander var5, int var6, boolean var7, int[] var8) {
      if (var3 >= var4) {
         return 0;
      } else {
         int var9 = var3;
         char[] var10 = var1.array;
         int var11 = var1.offset;
         int var12 = var1.count;
         int var13 = 0;
         int var14 = -1;
         int var15 = 0;
         int var16 = 0;
         int var17;
         if (var8 != null) {
            var17 = -var6 + var11;
            View var18 = null;
            if (var0 != null && (var18 = var0.getParent()) != null) {
               var17 += var18.getStartOffset();
            }

            var13 = var8[0];
            var14 = var8[1] + var17;
            var15 = var8[2] + var17;
            var16 = var8[3] + var17;
         }

         var17 = var1.offset + var1.count;

         for(int var23 = var1.offset; var23 < var17; ++var23) {
            if (var10[var23] != '\t' && (var13 == 0 && var23 > var14 || var10[var23] != ' ' || var15 > var23 || var23 > var16)) {
               var9 += var2.charWidth(var10[var23]);
            } else if (var10[var23] == '\t') {
               if (var5 != null) {
                  var9 = (int)var5.nextTabStop((float)var9, var6 + var23 - var11);
               } else {
                  var9 += var2.charWidth(' ');
               }
            } else if (var10[var23] == ' ') {
               var9 += var2.charWidth(' ') + var13;
               if (var23 <= var14) {
                  ++var9;
               }
            }

            if (var4 < var9) {
               int var19;
               if (var7) {
                  var19 = var23 + 1 - var11;
                  int var20 = var2.charsWidth(var10, var11, var19);
                  int var21 = var4 - var3;
                  if (var21 < var20) {
                     while(var19 > 0) {
                        int var22 = var19 > 1 ? var2.charsWidth(var10, var11, var19 - 1) : 0;
                        if (var21 >= var22) {
                           if (var21 - var22 < var20 - var21) {
                              --var19;
                           }
                           break;
                        }

                        var20 = var22;
                        --var19;
                     }
                  }
               } else {
                  for(var19 = var23 - var11; var19 > 0 && var2.charsWidth(var10, var11, var19) > var4 - var3; --var19) {
                  }
               }

               return var19;
            }
         }

         return var12;
      }
   }

   public static final int getBreakLocation(Segment var0, FontMetrics var1, int var2, int var3, TabExpander var4, int var5) {
      char[] var6 = var0.array;
      int var7 = var0.offset;
      int var8 = var0.count;
      int var9 = getTabbedTextOffset(var0, var1, var2, var3, var4, var5, false);
      if (var9 >= var8 - 1) {
         return var8;
      } else {
         for(int var10 = var7 + var9; var10 >= var7; --var10) {
            char var11 = var6[var10];
            if (var11 >= 256) {
               BreakIterator var12 = BreakIterator.getLineInstance();
               var12.setText((CharacterIterator)var0);
               int var13 = var12.preceding(var10 + 1);
               if (var13 > var7) {
                  var9 = var13 - var7;
               }
               break;
            }

            if (Character.isWhitespace(var11)) {
               var9 = var10 - var7 + 1;
               break;
            }
         }

         return var9;
      }
   }

   public static final int getRowStart(JTextComponent var0, int var1) throws BadLocationException {
      Rectangle var2 = var0.modelToView(var1);
      if (var2 == null) {
         return -1;
      } else {
         int var3 = var1;

         for(int var4 = var2.y; var2 != null && var4 == var2.y; var2 = var3 >= 0 ? var0.modelToView(var3) : null) {
            if (var2.height != 0) {
               var1 = var3;
            }

            --var3;
         }

         return var1;
      }
   }

   public static final int getRowEnd(JTextComponent var0, int var1) throws BadLocationException {
      Rectangle var2 = var0.modelToView(var1);
      if (var2 == null) {
         return -1;
      } else {
         int var3 = var0.getDocument().getLength();
         int var4 = var1;

         for(int var5 = var2.y; var2 != null && var5 == var2.y; var2 = var4 <= var3 ? var0.modelToView(var4) : null) {
            if (var2.height != 0) {
               var1 = var4;
            }

            ++var4;
         }

         return var1;
      }
   }

   public static final int getPositionAbove(JTextComponent var0, int var1, int var2) throws BadLocationException {
      int var3 = getRowStart(var0, var1) - 1;
      if (var3 < 0) {
         return -1;
      } else {
         int var4 = Integer.MAX_VALUE;
         int var5 = 0;
         Rectangle var6 = null;
         if (var3 >= 0) {
            var6 = var0.modelToView(var3);
            var5 = var6.y;
         }

         while(var6 != null && var5 == var6.y) {
            int var7 = Math.abs(var6.x - var2);
            if (var7 < var4) {
               var1 = var3;
               var4 = var7;
            }

            --var3;
            var6 = var3 >= 0 ? var0.modelToView(var3) : null;
         }

         return var1;
      }
   }

   public static final int getPositionBelow(JTextComponent var0, int var1, int var2) throws BadLocationException {
      int var3 = getRowEnd(var0, var1) + 1;
      if (var3 <= 0) {
         return -1;
      } else {
         int var4 = Integer.MAX_VALUE;
         int var5 = var0.getDocument().getLength();
         int var6 = 0;
         Rectangle var7 = null;
         if (var3 <= var5) {
            var7 = var0.modelToView(var3);
            var6 = var7.y;
         }

         while(var7 != null && var6 == var7.y) {
            int var8 = Math.abs(var2 - var7.x);
            if (var8 < var4) {
               var1 = var3;
               var4 = var8;
            }

            ++var3;
            var7 = var3 <= var5 ? var0.modelToView(var3) : null;
         }

         return var1;
      }
   }

   public static final int getWordStart(JTextComponent var0, int var1) throws BadLocationException {
      Document var2 = var0.getDocument();
      Element var3 = getParagraphElement(var0, var1);
      if (var3 == null) {
         throw new BadLocationException("No word at " + var1, var1);
      } else {
         int var4 = var3.getStartOffset();
         int var5 = Math.min(var3.getEndOffset(), var2.getLength());
         Segment var6 = SegmentCache.getSharedSegment();
         var2.getText(var4, var5 - var4, var6);
         if (var6.count > 0) {
            BreakIterator var7 = BreakIterator.getWordInstance(var0.getLocale());
            var7.setText((CharacterIterator)var6);
            int var8 = var6.offset + var1 - var4;
            if (var8 >= var7.last()) {
               var8 = var7.last() - 1;
            }

            var7.following(var8);
            var1 = var4 + var7.previous() - var6.offset;
         }

         SegmentCache.releaseSharedSegment(var6);
         return var1;
      }
   }

   public static final int getWordEnd(JTextComponent var0, int var1) throws BadLocationException {
      Document var2 = var0.getDocument();
      Element var3 = getParagraphElement(var0, var1);
      if (var3 == null) {
         throw new BadLocationException("No word at " + var1, var1);
      } else {
         int var4 = var3.getStartOffset();
         int var5 = Math.min(var3.getEndOffset(), var2.getLength());
         Segment var6 = SegmentCache.getSharedSegment();
         var2.getText(var4, var5 - var4, var6);
         if (var6.count > 0) {
            BreakIterator var7 = BreakIterator.getWordInstance(var0.getLocale());
            var7.setText((CharacterIterator)var6);
            int var8 = var1 - var4 + var6.offset;
            if (var8 >= var7.last()) {
               var8 = var7.last() - 1;
            }

            var1 = var4 + var7.following(var8) - var6.offset;
         }

         SegmentCache.releaseSharedSegment(var6);
         return var1;
      }
   }

   public static final int getNextWord(JTextComponent var0, int var1) throws BadLocationException {
      Element var3 = getParagraphElement(var0, var1);

      int var2;
      for(var2 = getNextWordInParagraph(var0, var3, var1, false); var2 == -1; var2 = getNextWordInParagraph(var0, var3, var1, true)) {
         var1 = var3.getEndOffset();
         var3 = getParagraphElement(var0, var1);
      }

      return var2;
   }

   static int getNextWordInParagraph(JTextComponent var0, Element var1, int var2, boolean var3) throws BadLocationException {
      if (var1 == null) {
         throw new BadLocationException("No more words", var2);
      } else {
         Document var4 = var1.getDocument();
         int var5 = var1.getStartOffset();
         int var6 = Math.min(var1.getEndOffset(), var4.getLength());
         if (var2 < var6 && var2 >= var5) {
            Segment var7 = SegmentCache.getSharedSegment();
            var4.getText(var5, var6 - var5, var7);
            BreakIterator var8 = BreakIterator.getWordInstance(var0.getLocale());
            var8.setText((CharacterIterator)var7);
            if (var3 && var8.first() == var7.offset + var2 - var5 && !Character.isWhitespace(var7.array[var8.first()])) {
               return var2;
            } else {
               int var9 = var8.following(var7.offset + var2 - var5);
               if (var9 != -1 && var9 < var7.offset + var7.count) {
                  char var10 = var7.array[var9];
                  if (!Character.isWhitespace(var10)) {
                     return var5 + var9 - var7.offset;
                  } else {
                     var9 = var8.next();
                     if (var9 != -1) {
                        var2 = var5 + var9 - var7.offset;
                        if (var2 != var6) {
                           return var2;
                        }
                     }

                     SegmentCache.releaseSharedSegment(var7);
                     return -1;
                  }
               } else {
                  return -1;
               }
            }
         } else {
            throw new BadLocationException("No more words", var2);
         }
      }
   }

   public static final int getPreviousWord(JTextComponent var0, int var1) throws BadLocationException {
      Element var3 = getParagraphElement(var0, var1);

      int var2;
      for(var2 = getPrevWordInParagraph(var0, var3, var1); var2 == -1; var2 = getPrevWordInParagraph(var0, var3, var1)) {
         var1 = var3.getStartOffset() - 1;
         var3 = getParagraphElement(var0, var1);
      }

      return var2;
   }

   static int getPrevWordInParagraph(JTextComponent var0, Element var1, int var2) throws BadLocationException {
      if (var1 == null) {
         throw new BadLocationException("No more words", var2);
      } else {
         Document var3 = var1.getDocument();
         int var4 = var1.getStartOffset();
         int var5 = var1.getEndOffset();
         if (var2 <= var5 && var2 >= var4) {
            Segment var6 = SegmentCache.getSharedSegment();
            var3.getText(var4, var5 - var4, var6);
            BreakIterator var7 = BreakIterator.getWordInstance(var0.getLocale());
            var7.setText((CharacterIterator)var6);
            if (var7.following(var6.offset + var2 - var4) == -1) {
               var7.last();
            }

            int var8 = var7.previous();
            if (var8 == var6.offset + var2 - var4) {
               var8 = var7.previous();
            }

            if (var8 == -1) {
               return -1;
            } else {
               char var9 = var6.array[var8];
               if (!Character.isWhitespace(var9)) {
                  return var4 + var8 - var6.offset;
               } else {
                  var8 = var7.previous();
                  if (var8 != -1) {
                     return var4 + var8 - var6.offset;
                  } else {
                     SegmentCache.releaseSharedSegment(var6);
                     return -1;
                  }
               }
            }
         } else {
            throw new BadLocationException("No more words", var2);
         }
      }
   }

   public static final Element getParagraphElement(JTextComponent var0, int var1) {
      Document var2 = var0.getDocument();
      if (var2 instanceof StyledDocument) {
         return ((StyledDocument)var2).getParagraphElement(var1);
      } else {
         Element var3 = var2.getDefaultRootElement();
         int var4 = var3.getElementIndex(var1);
         Element var5 = var3.getElement(var4);
         return var1 >= var5.getStartOffset() && var1 < var5.getEndOffset() ? var5 : null;
      }
   }

   static boolean isComposedTextElement(Document var0, int var1) {
      Element var2;
      for(var2 = var0.getDefaultRootElement(); !var2.isLeaf(); var2 = var2.getElement(var2.getElementIndex(var1))) {
      }

      return isComposedTextElement(var2);
   }

   static boolean isComposedTextElement(Element var0) {
      AttributeSet var1 = var0.getAttributes();
      return isComposedTextAttributeDefined(var1);
   }

   static boolean isComposedTextAttributeDefined(AttributeSet var0) {
      return var0 != null && var0.isDefined(StyleConstants.ComposedTextAttribute);
   }

   static int drawComposedText(View var0, AttributeSet var1, Graphics var2, int var3, int var4, int var5, int var6) throws BadLocationException {
      Graphics2D var7 = (Graphics2D)var2;
      AttributedString var8 = (AttributedString)var1.getAttribute(StyleConstants.ComposedTextAttribute);
      var8.addAttribute(TextAttribute.FONT, var2.getFont());
      if (var5 >= var6) {
         return var3;
      } else {
         AttributedCharacterIterator var9 = var8.getIterator((AttributedCharacterIterator.Attribute[])null, var5, var6);
         return var3 + (int)SwingUtilities2.drawString(getJComponent(var0), var7, (AttributedCharacterIterator)var9, var3, var4);
      }
   }

   static void paintComposedText(Graphics var0, Rectangle var1, GlyphView var2) {
      if (var0 instanceof Graphics2D) {
         Graphics2D var3 = (Graphics2D)var0;
         int var4 = var2.getStartOffset();
         int var5 = var2.getEndOffset();
         AttributeSet var6 = var2.getElement().getAttributes();
         AttributedString var7 = (AttributedString)var6.getAttribute(StyleConstants.ComposedTextAttribute);
         int var8 = var2.getElement().getStartOffset();
         int var9 = var1.y + var1.height - (int)var2.getGlyphPainter().getDescent(var2);
         int var10 = var1.x;
         var7.addAttribute(TextAttribute.FONT, var2.getFont());
         var7.addAttribute(TextAttribute.FOREGROUND, var2.getForeground());
         if (StyleConstants.isBold(var2.getAttributes())) {
            var7.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
         }

         if (StyleConstants.isItalic(var2.getAttributes())) {
            var7.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
         }

         if (var2.isUnderline()) {
            var7.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
         }

         if (var2.isStrikeThrough()) {
            var7.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
         }

         if (var2.isSuperscript()) {
            var7.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
         }

         if (var2.isSubscript()) {
            var7.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
         }

         AttributedCharacterIterator var11 = var7.getIterator((AttributedCharacterIterator.Attribute[])null, var4 - var8, var5 - var8);
         SwingUtilities2.drawString(getJComponent(var2), var3, (AttributedCharacterIterator)var11, var10, var9);
      }

   }

   static boolean isLeftToRight(Component var0) {
      return var0.getComponentOrientation().isLeftToRight();
   }

   static int getNextVisualPositionFrom(View var0, int var1, Position.Bias var2, Shape var3, int var4, Position.Bias[] var5) throws BadLocationException {
      if (var0.getViewCount() == 0) {
         return var1;
      } else {
         boolean var6 = var4 == 1 || var4 == 7;
         int var7;
         int var8;
         if (var1 == -1) {
            var8 = var6 ? var0.getViewCount() - 1 : 0;
            View var9 = var0.getView(var8);
            Shape var10 = var0.getChildAllocation(var8, var3);
            var7 = var9.getNextVisualPositionFrom(var1, var2, var10, var4, var5);
            if (var7 == -1 && !var6 && var0.getViewCount() > 1) {
               var9 = var0.getView(1);
               var10 = var0.getChildAllocation(1, var3);
               var7 = var9.getNextVisualPositionFrom(-1, var5[0], var10, var4, var5);
            }
         } else {
            var8 = var6 ? -1 : 1;
            int var14;
            if (var2 == Position.Bias.Backward && var1 > 0) {
               var14 = var0.getViewIndex(var1 - 1, Position.Bias.Forward);
            } else {
               var14 = var0.getViewIndex(var1, Position.Bias.Forward);
            }

            View var15 = var0.getView(var14);
            Shape var11 = var0.getChildAllocation(var14, var3);
            var7 = var15.getNextVisualPositionFrom(var1, var2, var11, var4, var5);
            if ((var4 == 3 || var4 == 7) && var0 instanceof CompositeView && ((CompositeView)var0).flipEastAndWestAtEnds(var1, var2)) {
               var8 *= -1;
            }

            var14 += var8;
            if (var7 == -1 && var14 >= 0 && var14 < var0.getViewCount()) {
               var15 = var0.getView(var14);
               var11 = var0.getChildAllocation(var14, var3);
               var7 = var15.getNextVisualPositionFrom(-1, var2, var11, var4, var5);
               if (var7 == var1 && var5[0] != var2) {
                  return getNextVisualPositionFrom(var0, var1, var5[0], var3, var4, var5);
               }
            } else if (var7 != -1 && var5[0] != var2 && (var8 == 1 && var15.getEndOffset() == var7 || var8 == -1 && var15.getStartOffset() == var7) && var14 >= 0 && var14 < var0.getViewCount()) {
               var15 = var0.getView(var14);
               var11 = var0.getChildAllocation(var14, var3);
               Position.Bias var12 = var5[0];
               int var13 = var15.getNextVisualPositionFrom(-1, var2, var11, var4, var5);
               if (var5[0] == var2) {
                  var7 = var13;
               } else {
                  var5[0] = var12;
               }
            }
         }

         return var7;
      }
   }
}
