package sun.text;

final class CharSequenceCodePointIterator extends CodePointIterator {
   private CharSequence text;
   private int index;

   public CharSequenceCodePointIterator(CharSequence var1) {
      this.text = var1;
   }

   public void setToStart() {
      this.index = 0;
   }

   public void setToLimit() {
      this.index = this.text.length();
   }

   public int next() {
      if (this.index < this.text.length()) {
         char var1 = this.text.charAt(this.index++);
         if (Character.isHighSurrogate(var1) && this.index < this.text.length()) {
            char var2 = this.text.charAt(this.index + 1);
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
      if (this.index > 0) {
         char var1 = this.text.charAt(--this.index);
         if (Character.isLowSurrogate(var1) && this.index > 0) {
            char var2 = this.text.charAt(this.index - 1);
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
