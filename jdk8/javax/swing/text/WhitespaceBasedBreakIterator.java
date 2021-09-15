package javax.swing.text;

import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;

class WhitespaceBasedBreakIterator extends BreakIterator {
   private char[] text = new char[0];
   private int[] breaks = new int[]{0};
   private int pos = 0;

   public void setText(CharacterIterator var1) {
      int var2 = var1.getBeginIndex();
      this.text = new char[var1.getEndIndex() - var2];
      int[] var3 = new int[this.text.length + 1];
      byte var4 = 0;
      int var9 = var4 + 1;
      var3[var4] = var2;
      int var5 = 0;
      boolean var6 = false;

      for(char var7 = var1.first(); var7 != '\uffff'; var7 = var1.next()) {
         this.text[var5] = var7;
         boolean var8 = Character.isWhitespace(var7);
         if (var6 && !var8) {
            var3[var9++] = var5 + var2;
         }

         var6 = var8;
         ++var5;
      }

      if (this.text.length > 0) {
         var3[var9++] = this.text.length + var2;
      }

      System.arraycopy(var3, 0, this.breaks = new int[var9], 0, var9);
   }

   public CharacterIterator getText() {
      return new StringCharacterIterator(new String(this.text));
   }

   public int first() {
      return this.breaks[this.pos = 0];
   }

   public int last() {
      return this.breaks[this.pos = this.breaks.length - 1];
   }

   public int current() {
      return this.breaks[this.pos];
   }

   public int next() {
      return this.pos == this.breaks.length - 1 ? -1 : this.breaks[++this.pos];
   }

   public int previous() {
      return this.pos == 0 ? -1 : this.breaks[--this.pos];
   }

   public int next(int var1) {
      return this.checkhit(this.pos + var1);
   }

   public int following(int var1) {
      return this.adjacent(var1, 1);
   }

   public int preceding(int var1) {
      return this.adjacent(var1, -1);
   }

   private int checkhit(int var1) {
      return var1 >= 0 && var1 < this.breaks.length ? this.breaks[this.pos = var1] : -1;
   }

   private int adjacent(int var1, int var2) {
      int var3 = Arrays.binarySearch(this.breaks, var1);
      int var4 = var3 < 0 ? (var2 < 0 ? -1 : -2) : 0;
      return this.checkhit(Math.abs(var3) + var2 + var4);
   }
}
