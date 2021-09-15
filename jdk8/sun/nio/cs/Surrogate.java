package sun.nio.cs;

import java.nio.CharBuffer;
import java.nio.charset.CoderResult;

public class Surrogate {
   public static final char MIN_HIGH = '\ud800';
   public static final char MAX_HIGH = '\udbff';
   public static final char MIN_LOW = '\udc00';
   public static final char MAX_LOW = '\udfff';
   public static final char MIN = '\ud800';
   public static final char MAX = '\udfff';
   public static final int UCS4_MIN = 65536;
   public static final int UCS4_MAX = 1114111;

   private Surrogate() {
   }

   public static boolean isHigh(int var0) {
      return 55296 <= var0 && var0 <= 56319;
   }

   public static boolean isLow(int var0) {
      return 56320 <= var0 && var0 <= 57343;
   }

   public static boolean is(int var0) {
      return 55296 <= var0 && var0 <= 57343;
   }

   public static boolean neededFor(int var0) {
      return Character.isSupplementaryCodePoint(var0);
   }

   public static char high(int var0) {
      assert Character.isSupplementaryCodePoint(var0);

      return Character.highSurrogate(var0);
   }

   public static char low(int var0) {
      assert Character.isSupplementaryCodePoint(var0);

      return Character.lowSurrogate(var0);
   }

   public static int toUCS4(char var0, char var1) {
      assert Character.isHighSurrogate(var0) && Character.isLowSurrogate(var1);

      return Character.toCodePoint(var0, var1);
   }

   public static class Generator {
      private CoderResult error;

      public Generator() {
         this.error = CoderResult.OVERFLOW;
      }

      public CoderResult error() {
         assert this.error != null;

         return this.error;
      }

      public int generate(int var1, int var2, CharBuffer var3) {
         if (Character.isBmpCodePoint(var1)) {
            char var4 = (char)var1;
            if (Character.isSurrogate(var4)) {
               this.error = CoderResult.malformedForLength(var2);
               return -1;
            } else if (var3.remaining() < 1) {
               this.error = CoderResult.OVERFLOW;
               return -1;
            } else {
               var3.put(var4);
               this.error = null;
               return 1;
            }
         } else if (Character.isValidCodePoint(var1)) {
            if (var3.remaining() < 2) {
               this.error = CoderResult.OVERFLOW;
               return -1;
            } else {
               var3.put(Character.highSurrogate(var1));
               var3.put(Character.lowSurrogate(var1));
               this.error = null;
               return 2;
            }
         } else {
            this.error = CoderResult.unmappableForLength(var2);
            return -1;
         }
      }

      public int generate(int var1, int var2, char[] var3, int var4, int var5) {
         if (Character.isBmpCodePoint(var1)) {
            char var6 = (char)var1;
            if (Character.isSurrogate(var6)) {
               this.error = CoderResult.malformedForLength(var2);
               return -1;
            } else if (var5 - var4 < 1) {
               this.error = CoderResult.OVERFLOW;
               return -1;
            } else {
               var3[var4] = var6;
               this.error = null;
               return 1;
            }
         } else if (Character.isValidCodePoint(var1)) {
            if (var5 - var4 < 2) {
               this.error = CoderResult.OVERFLOW;
               return -1;
            } else {
               var3[var4] = Character.highSurrogate(var1);
               var3[var4 + 1] = Character.lowSurrogate(var1);
               this.error = null;
               return 2;
            }
         } else {
            this.error = CoderResult.unmappableForLength(var2);
            return -1;
         }
      }
   }

   public static class Parser {
      private int character;
      private CoderResult error;
      private boolean isPair;

      public Parser() {
         this.error = CoderResult.UNDERFLOW;
      }

      public int character() {
         assert this.error == null;

         return this.character;
      }

      public boolean isPair() {
         assert this.error == null;

         return this.isPair;
      }

      public int increment() {
         assert this.error == null;

         return this.isPair ? 2 : 1;
      }

      public CoderResult error() {
         assert this.error != null;

         return this.error;
      }

      public CoderResult unmappableResult() {
         assert this.error == null;

         return CoderResult.unmappableForLength(this.isPair ? 2 : 1);
      }

      public int parse(char var1, CharBuffer var2) {
         if (Character.isHighSurrogate(var1)) {
            if (!var2.hasRemaining()) {
               this.error = CoderResult.UNDERFLOW;
               return -1;
            } else {
               char var3 = var2.get();
               if (Character.isLowSurrogate(var3)) {
                  this.character = Character.toCodePoint(var1, var3);
                  this.isPair = true;
                  this.error = null;
                  return this.character;
               } else {
                  this.error = CoderResult.malformedForLength(1);
                  return -1;
               }
            }
         } else if (Character.isLowSurrogate(var1)) {
            this.error = CoderResult.malformedForLength(1);
            return -1;
         } else {
            this.character = var1;
            this.isPair = false;
            this.error = null;
            return this.character;
         }
      }

      public int parse(char var1, char[] var2, int var3, int var4) {
         assert var2[var3] == var1;

         if (Character.isHighSurrogate(var1)) {
            if (var4 - var3 < 2) {
               this.error = CoderResult.UNDERFLOW;
               return -1;
            } else {
               char var5 = var2[var3 + 1];
               if (Character.isLowSurrogate(var5)) {
                  this.character = Character.toCodePoint(var1, var5);
                  this.isPair = true;
                  this.error = null;
                  return this.character;
               } else {
                  this.error = CoderResult.malformedForLength(1);
                  return -1;
               }
            }
         } else if (Character.isLowSurrogate(var1)) {
            this.error = CoderResult.malformedForLength(1);
            return -1;
         } else {
            this.character = var1;
            this.isPair = false;
            this.error = null;
            return this.character;
         }
      }
   }
}
