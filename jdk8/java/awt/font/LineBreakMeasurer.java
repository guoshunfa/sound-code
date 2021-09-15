package java.awt.font;

import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;
import java.text.CharacterIterator;

public final class LineBreakMeasurer {
   private BreakIterator breakIter;
   private int start;
   private int pos;
   private int limit;
   private TextMeasurer measurer;
   private CharArrayIterator charIter;

   public LineBreakMeasurer(AttributedCharacterIterator var1, FontRenderContext var2) {
      this(var1, BreakIterator.getLineInstance(), var2);
   }

   public LineBreakMeasurer(AttributedCharacterIterator var1, BreakIterator var2, FontRenderContext var3) {
      if (var1.getEndIndex() - var1.getBeginIndex() < 1) {
         throw new IllegalArgumentException("Text must contain at least one character.");
      } else {
         this.breakIter = var2;
         this.measurer = new TextMeasurer(var1, var3);
         this.limit = var1.getEndIndex();
         this.pos = this.start = var1.getBeginIndex();
         this.charIter = new CharArrayIterator(this.measurer.getChars(), this.start);
         this.breakIter.setText((CharacterIterator)this.charIter);
      }
   }

   public int nextOffset(float var1) {
      return this.nextOffset(var1, this.limit, false);
   }

   public int nextOffset(float var1, int var2, boolean var3) {
      int var4 = this.pos;
      if (this.pos < this.limit) {
         if (var2 <= this.pos) {
            throw new IllegalArgumentException("offsetLimit must be after current position");
         }

         int var5 = this.measurer.getLineBreakIndex(this.pos, var1);
         if (var5 == this.limit) {
            var4 = this.limit;
         } else if (Character.isWhitespace(this.measurer.getChars()[var5 - this.start])) {
            var4 = this.breakIter.following(var5);
         } else {
            int var6 = var5 + 1;
            if (var6 == this.limit) {
               this.breakIter.last();
               var4 = this.breakIter.previous();
            } else {
               var4 = this.breakIter.preceding(var6);
            }

            if (var4 <= this.pos) {
               if (var3) {
                  var4 = this.pos;
               } else {
                  var4 = Math.max(this.pos + 1, var5);
               }
            }
         }
      }

      if (var4 > var2) {
         var4 = var2;
      }

      return var4;
   }

   public TextLayout nextLayout(float var1) {
      return this.nextLayout(var1, this.limit, false);
   }

   public TextLayout nextLayout(float var1, int var2, boolean var3) {
      if (this.pos < this.limit) {
         int var4 = this.nextOffset(var1, var2, var3);
         if (var4 == this.pos) {
            return null;
         } else {
            TextLayout var5 = this.measurer.getLayout(this.pos, var4);
            this.pos = var4;
            return var5;
         }
      } else {
         return null;
      }
   }

   public int getPosition() {
      return this.pos;
   }

   public void setPosition(int var1) {
      if (var1 >= this.start && var1 <= this.limit) {
         this.pos = var1;
      } else {
         throw new IllegalArgumentException("position is out of range");
      }
   }

   public void insertChar(AttributedCharacterIterator var1, int var2) {
      this.measurer.insertChar(var1, var2);
      this.limit = var1.getEndIndex();
      this.pos = this.start = var1.getBeginIndex();
      this.charIter.reset(this.measurer.getChars(), var1.getBeginIndex());
      this.breakIter.setText((CharacterIterator)this.charIter);
   }

   public void deleteChar(AttributedCharacterIterator var1, int var2) {
      this.measurer.deleteChar(var1, var2);
      this.limit = var1.getEndIndex();
      this.pos = this.start = var1.getBeginIndex();
      this.charIter.reset(this.measurer.getChars(), this.start);
      this.breakIter.setText((CharacterIterator)this.charIter);
   }
}
