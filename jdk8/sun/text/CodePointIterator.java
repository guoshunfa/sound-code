package sun.text;

import java.text.CharacterIterator;

public abstract class CodePointIterator {
   public static final int DONE = -1;

   public abstract void setToStart();

   public abstract void setToLimit();

   public abstract int next();

   public abstract int prev();

   public abstract int charIndex();

   public static CodePointIterator create(char[] var0) {
      return new CharArrayCodePointIterator(var0);
   }

   public static CodePointIterator create(char[] var0, int var1, int var2) {
      return new CharArrayCodePointIterator(var0, var1, var2);
   }

   public static CodePointIterator create(CharSequence var0) {
      return new CharSequenceCodePointIterator(var0);
   }

   public static CodePointIterator create(CharacterIterator var0) {
      return new CharacterIteratorCodePointIterator(var0);
   }
}
