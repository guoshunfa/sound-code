package sun.text.normalizer;

import java.text.CharacterIterator;

public class CharacterIteratorWrapper extends UCharacterIterator {
   private CharacterIterator iterator;

   public CharacterIteratorWrapper(CharacterIterator var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         this.iterator = var1;
      }
   }

   public int current() {
      char var1 = this.iterator.current();
      return var1 == '\uffff' ? -1 : var1;
   }

   public int getLength() {
      return this.iterator.getEndIndex() - this.iterator.getBeginIndex();
   }

   public int getIndex() {
      return this.iterator.getIndex();
   }

   public int next() {
      char var1 = this.iterator.current();
      this.iterator.next();
      return var1 == '\uffff' ? -1 : var1;
   }

   public int previous() {
      char var1 = this.iterator.previous();
      return var1 == '\uffff' ? -1 : var1;
   }

   public void setIndex(int var1) {
      this.iterator.setIndex(var1);
   }

   public int getText(char[] var1, int var2) {
      int var3 = this.iterator.getEndIndex() - this.iterator.getBeginIndex();
      int var4 = this.iterator.getIndex();
      if (var2 >= 0 && var2 + var3 <= var1.length) {
         for(char var5 = this.iterator.first(); var5 != '\uffff'; var5 = this.iterator.next()) {
            var1[var2++] = var5;
         }

         this.iterator.setIndex(var4);
         return var3;
      } else {
         throw new IndexOutOfBoundsException(Integer.toString(var3));
      }
   }

   public Object clone() {
      try {
         CharacterIteratorWrapper var1 = (CharacterIteratorWrapper)super.clone();
         var1.iterator = (CharacterIterator)this.iterator.clone();
         return var1;
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }
}
