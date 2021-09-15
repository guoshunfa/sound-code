package sun.text;

final class CharArrayCodePointIterator extends CodePointIterator {
   private char[] text;
   private int start;
   private int limit;
   private int index;

   public CharArrayCodePointIterator(char[] var1) {
      this.text = var1;
      this.limit = var1.length;
   }

   public CharArrayCodePointIterator(char[] var1, int var2, int var3) {
      if (var2 >= 0 && var3 >= var2 && var3 <= var1.length) {
         this.text = var1;
         this.start = this.index = var2;
         this.limit = var3;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setToStart() {
      this.index = this.start;
   }

   public void setToLimit() {
      this.index = this.limit;
   }

   public int next() {
      if (this.index < this.limit) {
         char var1 = this.text[this.index++];
         if (Character.isHighSurrogate(var1) && this.index < this.limit) {
            char var2 = this.text[this.index];
            if (Character.isLowSurrogate(var2)) {
               ++this.index;
               return Character.toCodePoint(var1, var2);
            }
         }

         return var1;
      } else {
         return -1;
      }
   }

   public int prev() {
      if (this.index > this.start) {
         char var1 = this.text[--this.index];
         if (Character.isLowSurrogate(var1) && this.index > this.start) {
            char var2 = this.text[this.index - 1];
            if (Character.isHighSurrogate(var2)) {
               --this.index;
               return Character.toCodePoint(var2, var1);
            }
         }

         return var1;
      } else {
         return -1;
      }
   }

   public int charIndex() {
      return this.index;
   }
}
