package sun.text.normalizer;

import java.text.CharacterIterator;

public abstract class UCharacterIterator implements Cloneable {
   public static final int DONE = -1;

   protected UCharacterIterator() {
   }

   public static final UCharacterIterator getInstance(String var0) {
      return new ReplaceableUCharacterIterator(var0);
   }

   public static final UCharacterIterator getInstance(StringBuffer var0) {
      return new ReplaceableUCharacterIterator(var0);
   }

   public static final UCharacterIterator getInstance(CharacterIterator var0) {
      return new CharacterIteratorWrapper(var0);
   }

   public abstract int current();

   public abstract int getLength();

   public abstract int getIndex();

   public abstract int next();

   public int nextCodePoint() {
      int var1 = this.next();
      if (UTF16.isLeadSurrogate((char)var1)) {
         int var2 = this.next();
         if (UTF16.isTrailSurrogate((char)var2)) {
            return UCharacterProperty.getRawSupplementary((char)var1, (char)var2);
         }

         if (var2 != -1) {
            this.previous();
         }
      }

      return var1;
   }

   public abstract int previous();

   public abstract void setIndex(int var1);

   public abstract int getText(char[] var1, int var2);

   public final int getText(char[] var1) {
      return this.getText(var1, 0);
   }

   public String getText() {
      char[] var1 = new char[this.getLength()];
      this.getText(var1);
      return new String(var1);
   }

   public int moveIndex(int var1) {
      int var2 = Math.max(0, Math.min(this.getIndex() + var1, this.getLength()));
      this.setIndex(var2);
      return var2;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
