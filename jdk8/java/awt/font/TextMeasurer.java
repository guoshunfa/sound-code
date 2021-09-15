package java.awt.font;

import java.awt.Font;
import java.text.AttributedCharacterIterator;
import java.text.Bidi;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.Hashtable;
import java.util.Map;
import sun.font.AttributeValues;
import sun.font.BidiUtils;
import sun.font.TextLabelFactory;
import sun.font.TextLineComponent;

public final class TextMeasurer implements Cloneable {
   private static float EST_LINES = 2.1F;
   private FontRenderContext fFrc;
   private int fStart;
   private char[] fChars;
   private Bidi fBidi;
   private byte[] fLevels;
   private TextLineComponent[] fComponents;
   private int fComponentStart;
   private int fComponentLimit;
   private boolean haveLayoutWindow;
   private BreakIterator fLineBreak = null;
   private CharArrayIterator charIter = null;
   int layoutCount = 0;
   int layoutCharCount = 0;
   private StyledParagraph fParagraph;
   private boolean fIsDirectionLTR;
   private byte fBaseline;
   private float[] fBaselineOffsets;
   private float fJustifyRatio = 1.0F;
   private int formattedChars = 0;
   private static boolean wantStats = false;
   private boolean collectStats = false;

   public TextMeasurer(AttributedCharacterIterator var1, FontRenderContext var2) {
      this.fFrc = var2;
      this.initAll(var1);
   }

   protected Object clone() {
      TextMeasurer var1;
      try {
         var1 = (TextMeasurer)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new Error();
      }

      if (this.fComponents != null) {
         var1.fComponents = (TextLineComponent[])this.fComponents.clone();
      }

      return var1;
   }

   private void invalidateComponents() {
      this.fComponentStart = this.fComponentLimit = this.fChars.length;
      this.fComponents = null;
      this.haveLayoutWindow = false;
   }

   private void initAll(AttributedCharacterIterator var1) {
      this.fStart = var1.getBeginIndex();
      this.fChars = new char[var1.getEndIndex() - this.fStart];
      int var2 = 0;

      for(char var3 = var1.first(); var3 != '\uffff'; var3 = var1.next()) {
         this.fChars[var2++] = var3;
      }

      var1.first();
      this.fBidi = new Bidi(var1);
      if (this.fBidi.isLeftToRight()) {
         this.fBidi = null;
      }

      var1.first();
      Map var10 = var1.getAttributes();
      NumericShaper var4 = AttributeValues.getNumericShaping(var10);
      if (var4 != null) {
         var4.shape(this.fChars, 0, this.fChars.length);
      }

      this.fParagraph = new StyledParagraph(var1, this.fChars);
      this.fJustifyRatio = AttributeValues.getJustification(var10);
      boolean var5 = TextLine.advanceToFirstFont(var1);
      if (var5) {
         Font var6 = TextLine.getFontAtCurrentPos(var1);
         int var7 = var1.getIndex() - var1.getBeginIndex();
         LineMetrics var8 = var6.getLineMetrics(this.fChars, var7, var7 + 1, this.fFrc);
         this.fBaseline = (byte)var8.getBaselineIndex();
         this.fBaselineOffsets = var8.getBaselineOffsets();
      } else {
         GraphicAttribute var11 = (GraphicAttribute)var10.get(TextAttribute.CHAR_REPLACEMENT);
         this.fBaseline = TextLayout.getBaselineFromGraphic(var11);
         Hashtable var12 = new Hashtable(5, 0.9F);
         Font var13 = new Font(var12);
         LineMetrics var9 = var13.getLineMetrics((String)" ", 0, 1, this.fFrc);
         this.fBaselineOffsets = var9.getBaselineOffsets();
      }

      this.fBaselineOffsets = TextLine.getNormalizedOffsets(this.fBaselineOffsets, this.fBaseline);
      this.invalidateComponents();
   }

   private void generateComponents(int var1, int var2) {
      if (this.collectStats) {
         this.formattedChars += var2 - var1;
      }

      byte var3 = 0;
      TextLabelFactory var4 = new TextLabelFactory(this.fFrc, this.fChars, this.fBidi, var3);
      int[] var5 = null;
      if (this.fBidi != null) {
         this.fLevels = BidiUtils.getLevels(this.fBidi);
         int[] var6 = BidiUtils.createVisualToLogicalMap(this.fLevels);
         var5 = BidiUtils.createInverseMap(var6);
         this.fIsDirectionLTR = this.fBidi.baseIsLeftToRight();
      } else {
         this.fLevels = null;
         this.fIsDirectionLTR = true;
      }

      try {
         this.fComponents = TextLine.getComponents(this.fParagraph, this.fChars, var1, var2, var5, this.fLevels, var4);
      } catch (IllegalArgumentException var7) {
         System.out.println("startingAt=" + var1 + "; endingAt=" + var2);
         System.out.println("fComponentLimit=" + this.fComponentLimit);
         throw var7;
      }

      this.fComponentStart = var1;
      this.fComponentLimit = var2;
   }

   private int calcLineBreak(int var1, float var2) {
      int var3 = var1;
      float var4 = var2;
      int var6 = this.fComponentStart;

      int var5;
      for(var5 = 0; var5 < this.fComponents.length; ++var5) {
         int var7 = var6 + this.fComponents[var5].getNumCharacters();
         if (var7 > var3) {
            break;
         }

         var6 = var7;
      }

      while(var5 < this.fComponents.length) {
         TextLineComponent var10 = this.fComponents[var5];
         int var8 = var10.getNumCharacters();
         int var9 = var10.getLineBreakIndex(var3 - var6, var4);
         if (var9 != var8 || var5 >= this.fComponents.length) {
            return var6 + var9;
         }

         var4 -= var10.getAdvanceBetween(var3 - var6, var9);
         var6 += var8;
         var3 = var6;
         ++var5;
      }

      if (this.fComponentLimit < this.fChars.length) {
         this.generateComponents(var1, this.fChars.length);
         return this.calcLineBreak(var1, var2);
      } else {
         return this.fChars.length;
      }
   }

   private int trailingCdWhitespaceStart(int var1, int var2) {
      if (this.fLevels != null) {
         byte var3 = (byte)(this.fIsDirectionLTR ? 0 : 1);
         int var4 = var2;

         while(true) {
            --var4;
            if (var4 < var1) {
               break;
            }

            if (this.fLevels[var4] % 2 == var3 || Character.getDirectionality(this.fChars[var4]) != 12) {
               ++var4;
               return var4;
            }
         }
      }

      return var1;
   }

   private TextLineComponent[] makeComponentsOnRange(int var1, int var2) {
      int var3 = this.trailingCdWhitespaceStart(var1, var2);
      int var5 = this.fComponentStart;

      int var4;
      int var6;
      for(var4 = 0; var4 < this.fComponents.length; ++var4) {
         var6 = var5 + this.fComponents[var4].getNumCharacters();
         if (var6 > var1) {
            break;
         }

         var5 = var6;
      }

      boolean var7 = false;
      int var8 = var5;
      int var9 = var4;

      int var11;
      for(boolean var10 = true; var10; ++var9) {
         var11 = var8 + this.fComponents[var9].getNumCharacters();
         if (var3 > Math.max(var8, var1) && var3 < Math.min(var11, var2)) {
            var7 = true;
         }

         if (var11 >= var2) {
            var10 = false;
         } else {
            var8 = var11;
         }
      }

      var6 = var9 - var4;
      if (var7) {
         ++var6;
      }

      TextLineComponent[] var16 = new TextLineComponent[var6];
      var8 = 0;
      var9 = var1;
      int var17 = var3;
      if (var3 == var1) {
         var11 = this.fIsDirectionLTR ? 0 : 1;
         var17 = var2;
      } else {
         var11 = 2;
      }

      while(var9 < var2) {
         int var12 = this.fComponents[var4].getNumCharacters();
         int var13 = var5 + var12;
         int var14 = Math.max(var9, var5);
         int var15 = Math.min(var17, var13);
         var16[var8++] = this.fComponents[var4].getSubset(var14 - var5, var15 - var5, var11);
         var9 += var15 - var14;
         if (var9 == var17) {
            var17 = var2;
            var11 = this.fIsDirectionLTR ? 0 : 1;
         }

         if (var9 == var13) {
            ++var4;
            var5 = var13;
         }
      }

      return var16;
   }

   private TextLine makeTextLineOnRange(int var1, int var2) {
      int[] var3 = null;
      byte[] var4 = null;
      if (this.fBidi != null) {
         Bidi var5 = this.fBidi.createLineBidi(var1, var2);
         var4 = BidiUtils.getLevels(var5);
         int[] var6 = BidiUtils.createVisualToLogicalMap(var4);
         var3 = BidiUtils.createInverseMap(var6);
      }

      TextLineComponent[] var7 = this.makeComponentsOnRange(var1, var2);
      return new TextLine(this.fFrc, var7, this.fBaselineOffsets, this.fChars, var1, var2, var3, var4, this.fIsDirectionLTR);
   }

   private void ensureComponents(int var1, int var2) {
      if (var1 < this.fComponentStart || var2 > this.fComponentLimit) {
         this.generateComponents(var1, var2);
      }

   }

   private void makeLayoutWindow(int var1) {
      int var2 = var1;
      int var3 = this.fChars.length;
      if (this.layoutCount > 0 && !this.haveLayoutWindow) {
         float var4 = (float)Math.max(this.layoutCharCount / this.layoutCount, 1);
         var3 = Math.min(var1 + (int)(var4 * EST_LINES), this.fChars.length);
      }

      if (var1 > 0 || var3 < this.fChars.length) {
         if (this.charIter == null) {
            this.charIter = new CharArrayIterator(this.fChars);
         } else {
            this.charIter.reset(this.fChars);
         }

         if (this.fLineBreak == null) {
            this.fLineBreak = BreakIterator.getLineInstance();
         }

         this.fLineBreak.setText((CharacterIterator)this.charIter);
         if (var1 > 0 && !this.fLineBreak.isBoundary(var1)) {
            var2 = this.fLineBreak.preceding(var1);
         }

         if (var3 < this.fChars.length && !this.fLineBreak.isBoundary(var3)) {
            var3 = this.fLineBreak.following(var3);
         }
      }

      this.ensureComponents(var2, var3);
      this.haveLayoutWindow = true;
   }

   public int getLineBreakIndex(int var1, float var2) {
      int var3 = var1 - this.fStart;
      if (!this.haveLayoutWindow || var3 < this.fComponentStart || var3 >= this.fComponentLimit) {
         this.makeLayoutWindow(var3);
      }

      return this.calcLineBreak(var3, var2) + this.fStart;
   }

   public float getAdvanceBetween(int var1, int var2) {
      int var3 = var1 - this.fStart;
      int var4 = var2 - this.fStart;
      this.ensureComponents(var3, var4);
      TextLine var5 = this.makeTextLineOnRange(var3, var4);
      return var5.getMetrics().advance;
   }

   public TextLayout getLayout(int var1, int var2) {
      int var3 = var1 - this.fStart;
      int var4 = var2 - this.fStart;
      this.ensureComponents(var3, var4);
      TextLine var5 = this.makeTextLineOnRange(var3, var4);
      if (var4 < this.fChars.length) {
         this.layoutCharCount += var2 - var1;
         ++this.layoutCount;
      }

      return new TextLayout(var5, this.fBaseline, this.fBaselineOffsets, this.fJustifyRatio);
   }

   private void printStats() {
      System.out.println("formattedChars: " + this.formattedChars);
      this.collectStats = false;
   }

   public void insertChar(AttributedCharacterIterator var1, int var2) {
      if (this.collectStats) {
         this.printStats();
      }

      if (wantStats) {
         this.collectStats = true;
      }

      this.fStart = var1.getBeginIndex();
      int var3 = var1.getEndIndex();
      if (var3 - this.fStart != this.fChars.length + 1) {
         this.initAll(var1);
      }

      char[] var4 = new char[var3 - this.fStart];
      int var5 = var2 - this.fStart;
      System.arraycopy(this.fChars, 0, var4, 0, var5);
      char var6 = var1.setIndex(var2);
      var4[var5] = var6;
      System.arraycopy(this.fChars, var5, var4, var5 + 1, var3 - var2 - 1);
      this.fChars = var4;
      if (this.fBidi != null || Bidi.requiresBidi(var4, var5, var5 + 1) || var1.getAttribute(TextAttribute.BIDI_EMBEDDING) != null) {
         this.fBidi = new Bidi(var1);
         if (this.fBidi.isLeftToRight()) {
            this.fBidi = null;
         }
      }

      this.fParagraph = StyledParagraph.insertChar(var1, this.fChars, var2, this.fParagraph);
      this.invalidateComponents();
   }

   public void deleteChar(AttributedCharacterIterator var1, int var2) {
      this.fStart = var1.getBeginIndex();
      int var3 = var1.getEndIndex();
      if (var3 - this.fStart != this.fChars.length - 1) {
         this.initAll(var1);
      }

      char[] var4 = new char[var3 - this.fStart];
      int var5 = var2 - this.fStart;
      System.arraycopy(this.fChars, 0, var4, 0, var2 - this.fStart);
      System.arraycopy(this.fChars, var5 + 1, var4, var5, var3 - var2);
      this.fChars = var4;
      if (this.fBidi != null) {
         this.fBidi = new Bidi(var1);
         if (this.fBidi.isLeftToRight()) {
            this.fBidi = null;
         }
      }

      this.fParagraph = StyledParagraph.deleteChar(var1, this.fChars, var2, this.fParagraph);
      this.invalidateComponents();
   }

   char[] getChars() {
      return this.fChars;
   }
}
