package java.lang;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public interface CharSequence {
   int length();

   char charAt(int var1);

   CharSequence subSequence(int var1, int var2);

   String toString();

   default IntStream chars() {
      return StreamSupport.intStream(() -> {
         class CharIterator implements PrimitiveIterator.OfInt {
            int cur = 0;

            public boolean hasNext() {
               return this.cur < CharSequence.this.length();
            }

            public int nextInt() {
               if (this.hasNext()) {
                  return CharSequence.this.charAt(this.cur++);
               } else {
                  throw new NoSuchElementException();
               }
            }

            public void forEachRemaining(IntConsumer var1) {
               while(this.cur < CharSequence.this.length()) {
                  var1.accept(CharSequence.this.charAt(this.cur));
                  ++this.cur;
               }

            }
         }

         return Spliterators.spliterator((PrimitiveIterator.OfInt)(new CharIterator()), (long)this.length(), 16);
      }, 16464, false);
   }

   default IntStream codePoints() {
      return StreamSupport.intStream(() -> {
         class CodePointIterator implements PrimitiveIterator.OfInt {
            int cur = 0;

            public void forEachRemaining(IntConsumer var1) {
               int var2 = CharSequence.this.length();
               int var3 = this.cur;

               try {
                  while(var3 < var2) {
                     char var4 = CharSequence.this.charAt(var3++);
                     if (Character.isHighSurrogate(var4) && var3 < var2) {
                        char var5 = CharSequence.this.charAt(var3);
                        if (Character.isLowSurrogate(var5)) {
                           ++var3;
                           var1.accept(Character.toCodePoint(var4, var5));
                        } else {
                           var1.accept(var4);
                        }
                     } else {
                        var1.accept(var4);
                     }
                  }
               } finally {
                  this.cur = var3;
               }

            }

            public boolean hasNext() {
               return this.cur < CharSequence.this.length();
            }

            public int nextInt() {
               int var1 = CharSequence.this.length();
               if (this.cur >= var1) {
                  throw new NoSuchElementException();
               } else {
                  char var2 = CharSequence.this.charAt(this.cur++);
                  if (Character.isHighSurrogate(var2) && this.cur < var1) {
                     char var3 = CharSequence.this.charAt(this.cur);
                     if (Character.isLowSurrogate(var3)) {
                        ++this.cur;
                        return Character.toCodePoint(var2, var3);
                     }
                  }

                  return var2;
               }
            }
         }

         return Spliterators.spliteratorUnknownSize((PrimitiveIterator.OfInt)(new CodePointIterator()), 16);
      }, 16, false);
   }
}
