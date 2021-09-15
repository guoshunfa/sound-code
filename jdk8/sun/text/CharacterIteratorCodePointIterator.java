package sun.text;

import java.text.CharacterIterator;

final class CharacterIteratorCodePointIterator extends CodePointIterator {
   private CharacterIterator iter;

   public CharacterIteratorCodePointIterator(CharacterIterator var1) {
      this.iter = var1;
   }

   public void setToStart() {
      this.iter.setIndex(this.iter.getBeginIndex());
   }

   public void setToLimit() {
      this.iter.setIndex(this.iter.getEndIndex());
   }

   public int next() {
      char var1 = this.iter.current();
      if (var1 != '\uffff') {
         char var2 = this.iter.next();
         if (Character.isHighSurrogate(var1) && var2 != '\uffff' && Character.isLowSurrogate(var2)) {
            this.iter.next();
            return Character.toCodePoint(var1, var2);
         } else {
            return var1;
         }
      } else {
         return -1;
      }
   }

   public int prev() {
      char var1 = this.iter.previous();
      if (var1 != '\uffff') {
         if (Character.isLowSurrogate(var1)) {
            char var2 = this.iter.previous();
            if (Character.isHighSurrogate(var2)) {
               return Character.toCodePoint(var2, var1);
            }

            this.iter.next();
         }

         return var1;
      } else {
         return -1;
      }
   }

   public int charIndex() {
      return this.iter.getIndex();
   }
}
