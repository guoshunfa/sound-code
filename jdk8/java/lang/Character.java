package java.lang;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Character implements Serializable, Comparable<Character> {
   public static final int MIN_RADIX = 2;
   public static final int MAX_RADIX = 36;
   public static final char MIN_VALUE = '\u0000';
   public static final char MAX_VALUE = '\uffff';
   public static final Class<Character> TYPE = Class.getPrimitiveClass("char");
   public static final byte UNASSIGNED = 0;
   public static final byte UPPERCASE_LETTER = 1;
   public static final byte LOWERCASE_LETTER = 2;
   public static final byte TITLECASE_LETTER = 3;
   public static final byte MODIFIER_LETTER = 4;
   public static final byte OTHER_LETTER = 5;
   public static final byte NON_SPACING_MARK = 6;
   public static final byte ENCLOSING_MARK = 7;
   public static final byte COMBINING_SPACING_MARK = 8;
   public static final byte DECIMAL_DIGIT_NUMBER = 9;
   public static final byte LETTER_NUMBER = 10;
   public static final byte OTHER_NUMBER = 11;
   public static final byte SPACE_SEPARATOR = 12;
   public static final byte LINE_SEPARATOR = 13;
   public static final byte PARAGRAPH_SEPARATOR = 14;
   public static final byte CONTROL = 15;
   public static final byte FORMAT = 16;
   public static final byte PRIVATE_USE = 18;
   public static final byte SURROGATE = 19;
   public static final byte DASH_PUNCTUATION = 20;
   public static final byte START_PUNCTUATION = 21;
   public static final byte END_PUNCTUATION = 22;
   public static final byte CONNECTOR_PUNCTUATION = 23;
   public static final byte OTHER_PUNCTUATION = 24;
   public static final byte MATH_SYMBOL = 25;
   public static final byte CURRENCY_SYMBOL = 26;
   public static final byte MODIFIER_SYMBOL = 27;
   public static final byte OTHER_SYMBOL = 28;
   public static final byte INITIAL_QUOTE_PUNCTUATION = 29;
   public static final byte FINAL_QUOTE_PUNCTUATION = 30;
   static final int ERROR = -1;
   public static final byte DIRECTIONALITY_UNDEFINED = -1;
   public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;
   public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;
   public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;
   public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;
   public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;
   public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;
   public static final byte DIRECTIONALITY_ARABIC_NUMBER = 6;
   public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;
   public static final byte DIRECTIONALITY_NONSPACING_MARK = 8;
   public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;
   public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;
   public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;
   public static final byte DIRECTIONALITY_WHITESPACE = 12;
   public static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13;
   public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;
   public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;
   public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;
   public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;
   public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;
   public static final char MIN_HIGH_SURROGATE = '\ud800';
   public static final char MAX_HIGH_SURROGATE = '\udbff';
   public static final char MIN_LOW_SURROGATE = '\udc00';
   public static final char MAX_LOW_SURROGATE = '\udfff';
   public static final char MIN_SURROGATE = '\ud800';
   public static final char MAX_SURROGATE = '\udfff';
   public static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;
   public static final int MIN_CODE_POINT = 0;
   public static final int MAX_CODE_POINT = 1114111;
   private final char value;
   private static final long serialVersionUID = 3786198910865385080L;
   public static final int SIZE = 16;
   public static final int BYTES = 2;

   public Character(char var1) {
      this.value = var1;
   }

   public static Character valueOf(char var0) {
      return var0 <= 127 ? Character.CharacterCache.cache[var0] : new Character(var0);
   }

   public char charValue() {
      return this.value;
   }

   public int hashCode() {
      return hashCode(this.value);
   }

   public static int hashCode(char var0) {
      return var0;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Character) {
         return this.value == (Character)var1;
      } else {
         return false;
      }
   }

   public String toString() {
      char[] var1 = new char[]{this.value};
      return String.valueOf(var1);
   }

   public static String toString(char var0) {
      return String.valueOf(var0);
   }

   public static boolean isValidCodePoint(int var0) {
      int var1 = var0 >>> 16;
      return var1 < 17;
   }

   public static boolean isBmpCodePoint(int var0) {
      return var0 >>> 16 == 0;
   }

   public static boolean isSupplementaryCodePoint(int var0) {
      return var0 >= 65536 && var0 < 1114112;
   }

   public static boolean isHighSurrogate(char var0) {
      return var0 >= '\ud800' && var0 < '\udc00';
   }

   public static boolean isLowSurrogate(char var0) {
      return var0 >= '\udc00' && var0 < '\ue000';
   }

   public static boolean isSurrogate(char var0) {
      return var0 >= '\ud800' && var0 < '\ue000';
   }

   public static boolean isSurrogatePair(char var0, char var1) {
      return isHighSurrogate(var0) && isLowSurrogate(var1);
   }

   public static int charCount(int var0) {
      return var0 >= 65536 ? 2 : 1;
   }

   public static int toCodePoint(char var0, char var1) {
      return (var0 << 10) + var1 + -56613888;
   }

   public static int codePointAt(CharSequence var0, int var1) {
      char var2 = var0.charAt(var1);
      if (isHighSurrogate(var2)) {
         ++var1;
         if (var1 < var0.length()) {
            char var3 = var0.charAt(var1);
            if (isLowSurrogate(var3)) {
               return toCodePoint(var2, var3);
            }
         }
      }

      return var2;
   }

   public static int codePointAt(char[] var0, int var1) {
      return codePointAtImpl(var0, var1, var0.length);
   }

   public static int codePointAt(char[] var0, int var1, int var2) {
      if (var1 < var2 && var2 >= 0 && var2 <= var0.length) {
         return codePointAtImpl(var0, var1, var2);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static int codePointAtImpl(char[] var0, int var1, int var2) {
      char var3 = var0[var1];
      if (isHighSurrogate(var3)) {
         ++var1;
         if (var1 < var2) {
            char var4 = var0[var1];
            if (isLowSurrogate(var4)) {
               return toCodePoint(var3, var4);
            }
         }
      }

      return var3;
   }

   public static int codePointBefore(CharSequence var0, int var1) {
      --var1;
      char var2 = var0.charAt(var1);
      if (isLowSurrogate(var2) && var1 > 0) {
         --var1;
         char var3 = var0.charAt(var1);
         if (isHighSurrogate(var3)) {
            return toCodePoint(var3, var2);
         }
      }

      return var2;
   }

   public static int codePointBefore(char[] var0, int var1) {
      return codePointBeforeImpl(var0, var1, 0);
   }

   public static int codePointBefore(char[] var0, int var1, int var2) {
      if (var1 > var2 && var2 >= 0 && var2 < var0.length) {
         return codePointBeforeImpl(var0, var1, var2);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static int codePointBeforeImpl(char[] var0, int var1, int var2) {
      --var1;
      char var3 = var0[var1];
      if (isLowSurrogate(var3) && var1 > var2) {
         --var1;
         char var4 = var0[var1];
         if (isHighSurrogate(var4)) {
            return toCodePoint(var4, var3);
         }
      }

      return var3;
   }

   public static char highSurrogate(int var0) {
      return (char)((var0 >>> 10) + 'ퟀ');
   }

   public static char lowSurrogate(int var0) {
      return (char)((var0 & 1023) + '\udc00');
   }

   public static int toChars(int var0, char[] var1, int var2) {
      if (isBmpCodePoint(var0)) {
         var1[var2] = (char)var0;
         return 1;
      } else if (isValidCodePoint(var0)) {
         toSurrogates(var0, var1, var2);
         return 2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static char[] toChars(int var0) {
      if (isBmpCodePoint(var0)) {
         return new char[]{(char)var0};
      } else if (isValidCodePoint(var0)) {
         char[] var1 = new char[2];
         toSurrogates(var0, var1, 0);
         return var1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   static void toSurrogates(int var0, char[] var1, int var2) {
      var1[var2 + 1] = lowSurrogate(var0);
      var1[var2] = highSurrogate(var0);
   }

   public static int codePointCount(CharSequence var0, int var1, int var2) {
      int var3 = var0.length();
      if (var1 >= 0 && var2 <= var3 && var1 <= var2) {
         int var4 = var2 - var1;
         int var5 = var1;

         while(var5 < var2) {
            if (isHighSurrogate(var0.charAt(var5++)) && var5 < var2 && isLowSurrogate(var0.charAt(var5))) {
               --var4;
               ++var5;
            }
         }

         return var4;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public static int codePointCount(char[] var0, int var1, int var2) {
      if (var2 <= var0.length - var1 && var1 >= 0 && var2 >= 0) {
         return codePointCountImpl(var0, var1, var2);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static int codePointCountImpl(char[] var0, int var1, int var2) {
      int var3 = var1 + var2;
      int var4 = var2;
      int var5 = var1;

      while(var5 < var3) {
         if (isHighSurrogate(var0[var5++]) && var5 < var3 && isLowSurrogate(var0[var5])) {
            --var4;
            ++var5;
         }
      }

      return var4;
   }

   public static int offsetByCodePoints(CharSequence var0, int var1, int var2) {
      int var3 = var0.length();
      if (var1 >= 0 && var1 <= var3) {
         int var4 = var1;
         int var5;
         if (var2 >= 0) {
            for(var5 = 0; var4 < var3 && var5 < var2; ++var5) {
               if (isHighSurrogate(var0.charAt(var4++)) && var4 < var3 && isLowSurrogate(var0.charAt(var4))) {
                  ++var4;
               }
            }

            if (var5 < var2) {
               throw new IndexOutOfBoundsException();
            }
         } else {
            for(var5 = var2; var4 > 0 && var5 < 0; ++var5) {
               --var4;
               if (isLowSurrogate(var0.charAt(var4)) && var4 > 0 && isHighSurrogate(var0.charAt(var4 - 1))) {
                  --var4;
               }
            }

            if (var5 < 0) {
               throw new IndexOutOfBoundsException();
            }
         }

         return var4;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public static int offsetByCodePoints(char[] var0, int var1, int var2, int var3, int var4) {
      if (var2 <= var0.length - var1 && var1 >= 0 && var2 >= 0 && var3 >= var1 && var3 <= var1 + var2) {
         return offsetByCodePointsImpl(var0, var1, var2, var3, var4);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   static int offsetByCodePointsImpl(char[] var0, int var1, int var2, int var3, int var4) {
      int var5 = var3;
      int var6;
      if (var4 >= 0) {
         var6 = var1 + var2;

         int var7;
         for(var7 = 0; var5 < var6 && var7 < var4; ++var7) {
            if (isHighSurrogate(var0[var5++]) && var5 < var6 && isLowSurrogate(var0[var5])) {
               ++var5;
            }
         }

         if (var7 < var4) {
            throw new IndexOutOfBoundsException();
         }
      } else {
         for(var6 = var4; var5 > var1 && var6 < 0; ++var6) {
            --var5;
            if (isLowSurrogate(var0[var5]) && var5 > var1 && isHighSurrogate(var0[var5 - 1])) {
               --var5;
            }
         }

         if (var6 < 0) {
            throw new IndexOutOfBoundsException();
         }
      }

      return var5;
   }

   public static boolean isLowerCase(char var0) {
      return isLowerCase((int)var0);
   }

   public static boolean isLowerCase(int var0) {
      return getType(var0) == 2 || CharacterData.of(var0).isOtherLowercase(var0);
   }

   public static boolean isUpperCase(char var0) {
      return isUpperCase((int)var0);
   }

   public static boolean isUpperCase(int var0) {
      return getType(var0) == 1 || CharacterData.of(var0).isOtherUppercase(var0);
   }

   public static boolean isTitleCase(char var0) {
      return isTitleCase((int)var0);
   }

   public static boolean isTitleCase(int var0) {
      return getType(var0) == 3;
   }

   public static boolean isDigit(char var0) {
      return isDigit((int)var0);
   }

   public static boolean isDigit(int var0) {
      return getType(var0) == 9;
   }

   public static boolean isDefined(char var0) {
      return isDefined((int)var0);
   }

   public static boolean isDefined(int var0) {
      return getType(var0) != 0;
   }

   public static boolean isLetter(char var0) {
      return isLetter((int)var0);
   }

   public static boolean isLetter(int var0) {
      return (62 >> getType(var0) & 1) != 0;
   }

   public static boolean isLetterOrDigit(char var0) {
      return isLetterOrDigit((int)var0);
   }

   public static boolean isLetterOrDigit(int var0) {
      return (574 >> getType(var0) & 1) != 0;
   }

   /** @deprecated */
   @Deprecated
   public static boolean isJavaLetter(char var0) {
      return isJavaIdentifierStart(var0);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isJavaLetterOrDigit(char var0) {
      return isJavaIdentifierPart(var0);
   }

   public static boolean isAlphabetic(int var0) {
      return (1086 >> getType(var0) & 1) != 0 || CharacterData.of(var0).isOtherAlphabetic(var0);
   }

   public static boolean isIdeographic(int var0) {
      return CharacterData.of(var0).isIdeographic(var0);
   }

   public static boolean isJavaIdentifierStart(char var0) {
      return isJavaIdentifierStart((int)var0);
   }

   public static boolean isJavaIdentifierStart(int var0) {
      return CharacterData.of(var0).isJavaIdentifierStart(var0);
   }

   public static boolean isJavaIdentifierPart(char var0) {
      return isJavaIdentifierPart((int)var0);
   }

   public static boolean isJavaIdentifierPart(int var0) {
      return CharacterData.of(var0).isJavaIdentifierPart(var0);
   }

   public static boolean isUnicodeIdentifierStart(char var0) {
      return isUnicodeIdentifierStart((int)var0);
   }

   public static boolean isUnicodeIdentifierStart(int var0) {
      return CharacterData.of(var0).isUnicodeIdentifierStart(var0);
   }

   public static boolean isUnicodeIdentifierPart(char var0) {
      return isUnicodeIdentifierPart((int)var0);
   }

   public static boolean isUnicodeIdentifierPart(int var0) {
      return CharacterData.of(var0).isUnicodeIdentifierPart(var0);
   }

   public static boolean isIdentifierIgnorable(char var0) {
      return isIdentifierIgnorable((int)var0);
   }

   public static boolean isIdentifierIgnorable(int var0) {
      return CharacterData.of(var0).isIdentifierIgnorable(var0);
   }

   public static char toLowerCase(char var0) {
      return (char)toLowerCase((int)var0);
   }

   public static int toLowerCase(int var0) {
      return CharacterData.of(var0).toLowerCase(var0);
   }

   public static char toUpperCase(char var0) {
      return (char)toUpperCase((int)var0);
   }

   public static int toUpperCase(int var0) {
      return CharacterData.of(var0).toUpperCase(var0);
   }

   public static char toTitleCase(char var0) {
      return (char)toTitleCase((int)var0);
   }

   public static int toTitleCase(int var0) {
      return CharacterData.of(var0).toTitleCase(var0);
   }

   public static int digit(char var0, int var1) {
      return digit((int)var0, var1);
   }

   public static int digit(int var0, int var1) {
      return CharacterData.of(var0).digit(var0, var1);
   }

   public static int getNumericValue(char var0) {
      return getNumericValue((int)var0);
   }

   public static int getNumericValue(int var0) {
      return CharacterData.of(var0).getNumericValue(var0);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isSpace(char var0) {
      return var0 <= ' ' && (4294981120L >> var0 & 1L) != 0L;
   }

   public static boolean isSpaceChar(char var0) {
      return isSpaceChar((int)var0);
   }

   public static boolean isSpaceChar(int var0) {
      return (28672 >> getType(var0) & 1) != 0;
   }

   public static boolean isWhitespace(char var0) {
      return isWhitespace((int)var0);
   }

   public static boolean isWhitespace(int var0) {
      return CharacterData.of(var0).isWhitespace(var0);
   }

   public static boolean isISOControl(char var0) {
      return isISOControl((int)var0);
   }

   public static boolean isISOControl(int var0) {
      return var0 <= 159 && (var0 >= 127 || var0 >>> 5 == 0);
   }

   public static int getType(char var0) {
      return getType((int)var0);
   }

   public static int getType(int var0) {
      return CharacterData.of(var0).getType(var0);
   }

   public static char forDigit(int var0, int var1) {
      if (var0 < var1 && var0 >= 0) {
         if (var1 >= 2 && var1 <= 36) {
            return var0 < 10 ? (char)(48 + var0) : (char)(87 + var0);
         } else {
            return '\u0000';
         }
      } else {
         return '\u0000';
      }
   }

   public static byte getDirectionality(char var0) {
      return getDirectionality((int)var0);
   }

   public static byte getDirectionality(int var0) {
      return CharacterData.of(var0).getDirectionality(var0);
   }

   public static boolean isMirrored(char var0) {
      return isMirrored((int)var0);
   }

   public static boolean isMirrored(int var0) {
      return CharacterData.of(var0).isMirrored(var0);
   }

   public int compareTo(Character var1) {
      return compare(this.value, var1.value);
   }

   public static int compare(char var0, char var1) {
      return var0 - var1;
   }

   static int toUpperCaseEx(int var0) {
      assert isValidCodePoint(var0);

      return CharacterData.of(var0).toUpperCaseEx(var0);
   }

   static char[] toUpperCaseCharArray(int var0) {
      assert isBmpCodePoint(var0);

      return CharacterData.of(var0).toUpperCaseCharArray(var0);
   }

   public static char reverseBytes(char var0) {
      return (char)((var0 & '\uff00') >> 8 | var0 << 8);
   }

   public static String getName(int var0) {
      if (!isValidCodePoint(var0)) {
         throw new IllegalArgumentException();
      } else {
         String var1 = CharacterName.get(var0);
         if (var1 != null) {
            return var1;
         } else if (getType(var0) == 0) {
            return null;
         } else {
            Character.UnicodeBlock var2 = Character.UnicodeBlock.of(var0);
            return var2 != null ? var2.toString().replace('_', ' ') + " " + Integer.toHexString(var0).toUpperCase(Locale.ENGLISH) : Integer.toHexString(var0).toUpperCase(Locale.ENGLISH);
         }
      }
   }

   private static class CharacterCache {
      static final Character[] cache = new Character[128];

      static {
         for(int var0 = 0; var0 < cache.length; ++var0) {
            cache[var0] = new Character((char)var0);
         }

      }
   }

   public static enum UnicodeScript {
      COMMON,
      LATIN,
      GREEK,
      CYRILLIC,
      ARMENIAN,
      HEBREW,
      ARABIC,
      SYRIAC,
      THAANA,
      DEVANAGARI,
      BENGALI,
      GURMUKHI,
      GUJARATI,
      ORIYA,
      TAMIL,
      TELUGU,
      KANNADA,
      MALAYALAM,
      SINHALA,
      THAI,
      LAO,
      TIBETAN,
      MYANMAR,
      GEORGIAN,
      HANGUL,
      ETHIOPIC,
      CHEROKEE,
      CANADIAN_ABORIGINAL,
      OGHAM,
      RUNIC,
      KHMER,
      MONGOLIAN,
      HIRAGANA,
      KATAKANA,
      BOPOMOFO,
      HAN,
      YI,
      OLD_ITALIC,
      GOTHIC,
      DESERET,
      INHERITED,
      TAGALOG,
      HANUNOO,
      BUHID,
      TAGBANWA,
      LIMBU,
      TAI_LE,
      LINEAR_B,
      UGARITIC,
      SHAVIAN,
      OSMANYA,
      CYPRIOT,
      BRAILLE,
      BUGINESE,
      COPTIC,
      NEW_TAI_LUE,
      GLAGOLITIC,
      TIFINAGH,
      SYLOTI_NAGRI,
      OLD_PERSIAN,
      KHAROSHTHI,
      BALINESE,
      CUNEIFORM,
      PHOENICIAN,
      PHAGS_PA,
      NKO,
      SUNDANESE,
      BATAK,
      LEPCHA,
      OL_CHIKI,
      VAI,
      SAURASHTRA,
      KAYAH_LI,
      REJANG,
      LYCIAN,
      CARIAN,
      LYDIAN,
      CHAM,
      TAI_THAM,
      TAI_VIET,
      AVESTAN,
      EGYPTIAN_HIEROGLYPHS,
      SAMARITAN,
      MANDAIC,
      LISU,
      BAMUM,
      JAVANESE,
      MEETEI_MAYEK,
      IMPERIAL_ARAMAIC,
      OLD_SOUTH_ARABIAN,
      INSCRIPTIONAL_PARTHIAN,
      INSCRIPTIONAL_PAHLAVI,
      OLD_TURKIC,
      BRAHMI,
      KAITHI,
      MEROITIC_HIEROGLYPHS,
      MEROITIC_CURSIVE,
      SORA_SOMPENG,
      CHAKMA,
      SHARADA,
      TAKRI,
      MIAO,
      UNKNOWN;

      private static final int[] scriptStarts = new int[]{0, 65, 91, 97, 123, 170, 171, 186, 187, 192, 215, 216, 247, 248, 697, 736, 741, 746, 748, 768, 880, 884, 885, 894, 900, 901, 902, 903, 904, 994, 1008, 1024, 1157, 1159, 1329, 1417, 1418, 1425, 1536, 1548, 1549, 1563, 1566, 1567, 1568, 1600, 1601, 1611, 1622, 1632, 1642, 1648, 1649, 1757, 1758, 1792, 1872, 1920, 1984, 2048, 2112, 2208, 2304, 2385, 2387, 2404, 2406, 2433, 2561, 2689, 2817, 2946, 3073, 3202, 3330, 3458, 3585, 3647, 3648, 3713, 3840, 4053, 4057, 4096, 4256, 4347, 4348, 4352, 4608, 5024, 5120, 5760, 5792, 5867, 5870, 5888, 5920, 5941, 5952, 5984, 6016, 6144, 6146, 6148, 6149, 6150, 6320, 6400, 6480, 6528, 6624, 6656, 6688, 6912, 7040, 7104, 7168, 7248, 7360, 7376, 7379, 7380, 7393, 7394, 7401, 7405, 7406, 7412, 7413, 7424, 7462, 7467, 7468, 7517, 7522, 7526, 7531, 7544, 7545, 7615, 7616, 7680, 7936, 8192, 8204, 8206, 8305, 8308, 8319, 8320, 8336, 8352, 8400, 8448, 8486, 8487, 8490, 8492, 8498, 8499, 8526, 8527, 8544, 8585, 10240, 10496, 11264, 11360, 11392, 11520, 11568, 11648, 11744, 11776, 11904, 12272, 12293, 12294, 12295, 12296, 12321, 12330, 12334, 12336, 12344, 12348, 12353, 12441, 12443, 12445, 12448, 12449, 12539, 12541, 12549, 12593, 12688, 12704, 12736, 12784, 12800, 12832, 12896, 12927, 13008, 13144, 13312, 19904, 19968, 40960, 42192, 42240, 42560, 42656, 42752, 42786, 42888, 42891, 43008, 43056, 43072, 43136, 43232, 43264, 43312, 43360, 43392, 43520, 43616, 43648, 43744, 43777, 43968, 44032, 55292, 63744, 64256, 64275, 64285, 64336, 64830, 64848, 65021, 65024, 65040, 65056, 65072, 65136, 65279, 65313, 65339, 65345, 65371, 65382, 65392, 65393, 65438, 65440, 65504, 65536, 65792, 65856, 65936, 66045, 66176, 66208, 66304, 66352, 66432, 66464, 66560, 66640, 66688, 67584, 67648, 67840, 67872, 67968, 68000, 68096, 68192, 68352, 68416, 68448, 68608, 69216, 69632, 69760, 69840, 69888, 70016, 71296, 73728, 77824, 92160, 93952, 110592, 110593, 118784, 119143, 119146, 119163, 119171, 119173, 119180, 119210, 119214, 119296, 119552, 126464, 126976, 127488, 127489, 131072, 917505, 917760, 918000};
      private static final Character.UnicodeScript[] scripts = new Character.UnicodeScript[]{COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, BOPOMOFO, COMMON, INHERITED, GREEK, COMMON, GREEK, COMMON, GREEK, COMMON, GREEK, COMMON, GREEK, COPTIC, GREEK, CYRILLIC, INHERITED, CYRILLIC, ARMENIAN, COMMON, ARMENIAN, HEBREW, ARABIC, COMMON, ARABIC, COMMON, ARABIC, COMMON, ARABIC, COMMON, ARABIC, INHERITED, ARABIC, COMMON, ARABIC, INHERITED, ARABIC, COMMON, ARABIC, SYRIAC, ARABIC, THAANA, NKO, SAMARITAN, MANDAIC, ARABIC, DEVANAGARI, INHERITED, DEVANAGARI, COMMON, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, ORIYA, TAMIL, TELUGU, KANNADA, MALAYALAM, SINHALA, THAI, COMMON, THAI, LAO, TIBETAN, COMMON, TIBETAN, MYANMAR, GEORGIAN, COMMON, GEORGIAN, HANGUL, ETHIOPIC, CHEROKEE, CANADIAN_ABORIGINAL, OGHAM, RUNIC, COMMON, RUNIC, TAGALOG, HANUNOO, COMMON, BUHID, TAGBANWA, KHMER, MONGOLIAN, COMMON, MONGOLIAN, COMMON, MONGOLIAN, CANADIAN_ABORIGINAL, LIMBU, TAI_LE, NEW_TAI_LUE, KHMER, BUGINESE, TAI_THAM, BALINESE, SUNDANESE, BATAK, LEPCHA, OL_CHIKI, SUNDANESE, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, LATIN, GREEK, CYRILLIC, LATIN, GREEK, LATIN, GREEK, LATIN, CYRILLIC, LATIN, GREEK, INHERITED, LATIN, GREEK, COMMON, INHERITED, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, INHERITED, COMMON, GREEK, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, BRAILLE, COMMON, GLAGOLITIC, LATIN, COPTIC, GEORGIAN, TIFINAGH, ETHIOPIC, CYRILLIC, COMMON, HAN, COMMON, HAN, COMMON, HAN, COMMON, HAN, INHERITED, HANGUL, COMMON, HAN, COMMON, HIRAGANA, INHERITED, COMMON, HIRAGANA, COMMON, KATAKANA, COMMON, KATAKANA, BOPOMOFO, HANGUL, COMMON, BOPOMOFO, COMMON, KATAKANA, HANGUL, COMMON, HANGUL, COMMON, KATAKANA, COMMON, HAN, COMMON, HAN, YI, LISU, VAI, CYRILLIC, BAMUM, COMMON, LATIN, COMMON, LATIN, SYLOTI_NAGRI, COMMON, PHAGS_PA, SAURASHTRA, DEVANAGARI, KAYAH_LI, REJANG, HANGUL, JAVANESE, CHAM, MYANMAR, TAI_VIET, MEETEI_MAYEK, ETHIOPIC, MEETEI_MAYEK, HANGUL, UNKNOWN, HAN, LATIN, ARMENIAN, HEBREW, ARABIC, COMMON, ARABIC, COMMON, INHERITED, COMMON, INHERITED, COMMON, ARABIC, COMMON, LATIN, COMMON, LATIN, COMMON, KATAKANA, COMMON, KATAKANA, COMMON, HANGUL, COMMON, LINEAR_B, COMMON, GREEK, COMMON, INHERITED, LYCIAN, CARIAN, OLD_ITALIC, GOTHIC, UGARITIC, OLD_PERSIAN, DESERET, SHAVIAN, OSMANYA, CYPRIOT, IMPERIAL_ARAMAIC, PHOENICIAN, LYDIAN, MEROITIC_HIEROGLYPHS, MEROITIC_CURSIVE, KHAROSHTHI, OLD_SOUTH_ARABIAN, AVESTAN, INSCRIPTIONAL_PARTHIAN, INSCRIPTIONAL_PAHLAVI, OLD_TURKIC, ARABIC, BRAHMI, KAITHI, SORA_SOMPENG, CHAKMA, SHARADA, TAKRI, CUNEIFORM, EGYPTIAN_HIEROGLYPHS, BAMUM, MIAO, KATAKANA, HIRAGANA, COMMON, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, GREEK, COMMON, ARABIC, COMMON, HIRAGANA, COMMON, HAN, COMMON, INHERITED, UNKNOWN};
      private static HashMap<String, Character.UnicodeScript> aliases = new HashMap(128);

      public static Character.UnicodeScript of(int var0) {
         if (!Character.isValidCodePoint(var0)) {
            throw new IllegalArgumentException();
         } else {
            int var1 = Character.getType(var0);
            if (var1 == 0) {
               return UNKNOWN;
            } else {
               int var2 = Arrays.binarySearch(scriptStarts, var0);
               if (var2 < 0) {
                  var2 = -var2 - 2;
               }

               return scripts[var2];
            }
         }
      }

      public static final Character.UnicodeScript forName(String var0) {
         var0 = var0.toUpperCase(Locale.ENGLISH);
         Character.UnicodeScript var1 = (Character.UnicodeScript)aliases.get(var0);
         return var1 != null ? var1 : valueOf(var0);
      }

      static {
         aliases.put("ARAB", ARABIC);
         aliases.put("ARMI", IMPERIAL_ARAMAIC);
         aliases.put("ARMN", ARMENIAN);
         aliases.put("AVST", AVESTAN);
         aliases.put("BALI", BALINESE);
         aliases.put("BAMU", BAMUM);
         aliases.put("BATK", BATAK);
         aliases.put("BENG", BENGALI);
         aliases.put("BOPO", BOPOMOFO);
         aliases.put("BRAI", BRAILLE);
         aliases.put("BRAH", BRAHMI);
         aliases.put("BUGI", BUGINESE);
         aliases.put("BUHD", BUHID);
         aliases.put("CAKM", CHAKMA);
         aliases.put("CANS", CANADIAN_ABORIGINAL);
         aliases.put("CARI", CARIAN);
         aliases.put("CHAM", CHAM);
         aliases.put("CHER", CHEROKEE);
         aliases.put("COPT", COPTIC);
         aliases.put("CPRT", CYPRIOT);
         aliases.put("CYRL", CYRILLIC);
         aliases.put("DEVA", DEVANAGARI);
         aliases.put("DSRT", DESERET);
         aliases.put("EGYP", EGYPTIAN_HIEROGLYPHS);
         aliases.put("ETHI", ETHIOPIC);
         aliases.put("GEOR", GEORGIAN);
         aliases.put("GLAG", GLAGOLITIC);
         aliases.put("GOTH", GOTHIC);
         aliases.put("GREK", GREEK);
         aliases.put("GUJR", GUJARATI);
         aliases.put("GURU", GURMUKHI);
         aliases.put("HANG", HANGUL);
         aliases.put("HANI", HAN);
         aliases.put("HANO", HANUNOO);
         aliases.put("HEBR", HEBREW);
         aliases.put("HIRA", HIRAGANA);
         aliases.put("ITAL", OLD_ITALIC);
         aliases.put("JAVA", JAVANESE);
         aliases.put("KALI", KAYAH_LI);
         aliases.put("KANA", KATAKANA);
         aliases.put("KHAR", KHAROSHTHI);
         aliases.put("KHMR", KHMER);
         aliases.put("KNDA", KANNADA);
         aliases.put("KTHI", KAITHI);
         aliases.put("LANA", TAI_THAM);
         aliases.put("LAOO", LAO);
         aliases.put("LATN", LATIN);
         aliases.put("LEPC", LEPCHA);
         aliases.put("LIMB", LIMBU);
         aliases.put("LINB", LINEAR_B);
         aliases.put("LISU", LISU);
         aliases.put("LYCI", LYCIAN);
         aliases.put("LYDI", LYDIAN);
         aliases.put("MAND", MANDAIC);
         aliases.put("MERC", MEROITIC_CURSIVE);
         aliases.put("MERO", MEROITIC_HIEROGLYPHS);
         aliases.put("MLYM", MALAYALAM);
         aliases.put("MONG", MONGOLIAN);
         aliases.put("MTEI", MEETEI_MAYEK);
         aliases.put("MYMR", MYANMAR);
         aliases.put("NKOO", NKO);
         aliases.put("OGAM", OGHAM);
         aliases.put("OLCK", OL_CHIKI);
         aliases.put("ORKH", OLD_TURKIC);
         aliases.put("ORYA", ORIYA);
         aliases.put("OSMA", OSMANYA);
         aliases.put("PHAG", PHAGS_PA);
         aliases.put("PLRD", MIAO);
         aliases.put("PHLI", INSCRIPTIONAL_PAHLAVI);
         aliases.put("PHNX", PHOENICIAN);
         aliases.put("PRTI", INSCRIPTIONAL_PARTHIAN);
         aliases.put("RJNG", REJANG);
         aliases.put("RUNR", RUNIC);
         aliases.put("SAMR", SAMARITAN);
         aliases.put("SARB", OLD_SOUTH_ARABIAN);
         aliases.put("SAUR", SAURASHTRA);
         aliases.put("SHAW", SHAVIAN);
         aliases.put("SHRD", SHARADA);
         aliases.put("SINH", SINHALA);
         aliases.put("SORA", SORA_SOMPENG);
         aliases.put("SUND", SUNDANESE);
         aliases.put("SYLO", SYLOTI_NAGRI);
         aliases.put("SYRC", SYRIAC);
         aliases.put("TAGB", TAGBANWA);
         aliases.put("TALE", TAI_LE);
         aliases.put("TAKR", TAKRI);
         aliases.put("TALU", NEW_TAI_LUE);
         aliases.put("TAML", TAMIL);
         aliases.put("TAVT", TAI_VIET);
         aliases.put("TELU", TELUGU);
         aliases.put("TFNG", TIFINAGH);
         aliases.put("TGLG", TAGALOG);
         aliases.put("THAA", THAANA);
         aliases.put("THAI", THAI);
         aliases.put("TIBT", TIBETAN);
         aliases.put("UGAR", UGARITIC);
         aliases.put("VAII", VAI);
         aliases.put("XPEO", OLD_PERSIAN);
         aliases.put("XSUX", CUNEIFORM);
         aliases.put("YIII", YI);
         aliases.put("ZINH", INHERITED);
         aliases.put("ZYYY", COMMON);
         aliases.put("ZZZZ", UNKNOWN);
      }
   }

   public static final class UnicodeBlock extends Character.Subset {
      private static Map<String, Character.UnicodeBlock> map = new HashMap(256);
      public static final Character.UnicodeBlock BASIC_LATIN = new Character.UnicodeBlock("BASIC_LATIN", new String[]{"BASIC LATIN", "BASICLATIN"});
      public static final Character.UnicodeBlock LATIN_1_SUPPLEMENT = new Character.UnicodeBlock("LATIN_1_SUPPLEMENT", new String[]{"LATIN-1 SUPPLEMENT", "LATIN-1SUPPLEMENT"});
      public static final Character.UnicodeBlock LATIN_EXTENDED_A = new Character.UnicodeBlock("LATIN_EXTENDED_A", new String[]{"LATIN EXTENDED-A", "LATINEXTENDED-A"});
      public static final Character.UnicodeBlock LATIN_EXTENDED_B = new Character.UnicodeBlock("LATIN_EXTENDED_B", new String[]{"LATIN EXTENDED-B", "LATINEXTENDED-B"});
      public static final Character.UnicodeBlock IPA_EXTENSIONS = new Character.UnicodeBlock("IPA_EXTENSIONS", new String[]{"IPA EXTENSIONS", "IPAEXTENSIONS"});
      public static final Character.UnicodeBlock SPACING_MODIFIER_LETTERS = new Character.UnicodeBlock("SPACING_MODIFIER_LETTERS", new String[]{"SPACING MODIFIER LETTERS", "SPACINGMODIFIERLETTERS"});
      public static final Character.UnicodeBlock COMBINING_DIACRITICAL_MARKS = new Character.UnicodeBlock("COMBINING_DIACRITICAL_MARKS", new String[]{"COMBINING DIACRITICAL MARKS", "COMBININGDIACRITICALMARKS"});
      public static final Character.UnicodeBlock GREEK = new Character.UnicodeBlock("GREEK", new String[]{"GREEK AND COPTIC", "GREEKANDCOPTIC"});
      public static final Character.UnicodeBlock CYRILLIC = new Character.UnicodeBlock("CYRILLIC");
      public static final Character.UnicodeBlock ARMENIAN = new Character.UnicodeBlock("ARMENIAN");
      public static final Character.UnicodeBlock HEBREW = new Character.UnicodeBlock("HEBREW");
      public static final Character.UnicodeBlock ARABIC = new Character.UnicodeBlock("ARABIC");
      public static final Character.UnicodeBlock DEVANAGARI = new Character.UnicodeBlock("DEVANAGARI");
      public static final Character.UnicodeBlock BENGALI = new Character.UnicodeBlock("BENGALI");
      public static final Character.UnicodeBlock GURMUKHI = new Character.UnicodeBlock("GURMUKHI");
      public static final Character.UnicodeBlock GUJARATI = new Character.UnicodeBlock("GUJARATI");
      public static final Character.UnicodeBlock ORIYA = new Character.UnicodeBlock("ORIYA");
      public static final Character.UnicodeBlock TAMIL = new Character.UnicodeBlock("TAMIL");
      public static final Character.UnicodeBlock TELUGU = new Character.UnicodeBlock("TELUGU");
      public static final Character.UnicodeBlock KANNADA = new Character.UnicodeBlock("KANNADA");
      public static final Character.UnicodeBlock MALAYALAM = new Character.UnicodeBlock("MALAYALAM");
      public static final Character.UnicodeBlock THAI = new Character.UnicodeBlock("THAI");
      public static final Character.UnicodeBlock LAO = new Character.UnicodeBlock("LAO");
      public static final Character.UnicodeBlock TIBETAN = new Character.UnicodeBlock("TIBETAN");
      public static final Character.UnicodeBlock GEORGIAN = new Character.UnicodeBlock("GEORGIAN");
      public static final Character.UnicodeBlock HANGUL_JAMO = new Character.UnicodeBlock("HANGUL_JAMO", new String[]{"HANGUL JAMO", "HANGULJAMO"});
      public static final Character.UnicodeBlock LATIN_EXTENDED_ADDITIONAL = new Character.UnicodeBlock("LATIN_EXTENDED_ADDITIONAL", new String[]{"LATIN EXTENDED ADDITIONAL", "LATINEXTENDEDADDITIONAL"});
      public static final Character.UnicodeBlock GREEK_EXTENDED = new Character.UnicodeBlock("GREEK_EXTENDED", new String[]{"GREEK EXTENDED", "GREEKEXTENDED"});
      public static final Character.UnicodeBlock GENERAL_PUNCTUATION = new Character.UnicodeBlock("GENERAL_PUNCTUATION", new String[]{"GENERAL PUNCTUATION", "GENERALPUNCTUATION"});
      public static final Character.UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS = new Character.UnicodeBlock("SUPERSCRIPTS_AND_SUBSCRIPTS", new String[]{"SUPERSCRIPTS AND SUBSCRIPTS", "SUPERSCRIPTSANDSUBSCRIPTS"});
      public static final Character.UnicodeBlock CURRENCY_SYMBOLS = new Character.UnicodeBlock("CURRENCY_SYMBOLS", new String[]{"CURRENCY SYMBOLS", "CURRENCYSYMBOLS"});
      public static final Character.UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS = new Character.UnicodeBlock("COMBINING_MARKS_FOR_SYMBOLS", new String[]{"COMBINING DIACRITICAL MARKS FOR SYMBOLS", "COMBININGDIACRITICALMARKSFORSYMBOLS", "COMBINING MARKS FOR SYMBOLS", "COMBININGMARKSFORSYMBOLS"});
      public static final Character.UnicodeBlock LETTERLIKE_SYMBOLS = new Character.UnicodeBlock("LETTERLIKE_SYMBOLS", new String[]{"LETTERLIKE SYMBOLS", "LETTERLIKESYMBOLS"});
      public static final Character.UnicodeBlock NUMBER_FORMS = new Character.UnicodeBlock("NUMBER_FORMS", new String[]{"NUMBER FORMS", "NUMBERFORMS"});
      public static final Character.UnicodeBlock ARROWS = new Character.UnicodeBlock("ARROWS");
      public static final Character.UnicodeBlock MATHEMATICAL_OPERATORS = new Character.UnicodeBlock("MATHEMATICAL_OPERATORS", new String[]{"MATHEMATICAL OPERATORS", "MATHEMATICALOPERATORS"});
      public static final Character.UnicodeBlock MISCELLANEOUS_TECHNICAL = new Character.UnicodeBlock("MISCELLANEOUS_TECHNICAL", new String[]{"MISCELLANEOUS TECHNICAL", "MISCELLANEOUSTECHNICAL"});
      public static final Character.UnicodeBlock CONTROL_PICTURES = new Character.UnicodeBlock("CONTROL_PICTURES", new String[]{"CONTROL PICTURES", "CONTROLPICTURES"});
      public static final Character.UnicodeBlock OPTICAL_CHARACTER_RECOGNITION = new Character.UnicodeBlock("OPTICAL_CHARACTER_RECOGNITION", new String[]{"OPTICAL CHARACTER RECOGNITION", "OPTICALCHARACTERRECOGNITION"});
      public static final Character.UnicodeBlock ENCLOSED_ALPHANUMERICS = new Character.UnicodeBlock("ENCLOSED_ALPHANUMERICS", new String[]{"ENCLOSED ALPHANUMERICS", "ENCLOSEDALPHANUMERICS"});
      public static final Character.UnicodeBlock BOX_DRAWING = new Character.UnicodeBlock("BOX_DRAWING", new String[]{"BOX DRAWING", "BOXDRAWING"});
      public static final Character.UnicodeBlock BLOCK_ELEMENTS = new Character.UnicodeBlock("BLOCK_ELEMENTS", new String[]{"BLOCK ELEMENTS", "BLOCKELEMENTS"});
      public static final Character.UnicodeBlock GEOMETRIC_SHAPES = new Character.UnicodeBlock("GEOMETRIC_SHAPES", new String[]{"GEOMETRIC SHAPES", "GEOMETRICSHAPES"});
      public static final Character.UnicodeBlock MISCELLANEOUS_SYMBOLS = new Character.UnicodeBlock("MISCELLANEOUS_SYMBOLS", new String[]{"MISCELLANEOUS SYMBOLS", "MISCELLANEOUSSYMBOLS"});
      public static final Character.UnicodeBlock DINGBATS = new Character.UnicodeBlock("DINGBATS");
      public static final Character.UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION = new Character.UnicodeBlock("CJK_SYMBOLS_AND_PUNCTUATION", new String[]{"CJK SYMBOLS AND PUNCTUATION", "CJKSYMBOLSANDPUNCTUATION"});
      public static final Character.UnicodeBlock HIRAGANA = new Character.UnicodeBlock("HIRAGANA");
      public static final Character.UnicodeBlock KATAKANA = new Character.UnicodeBlock("KATAKANA");
      public static final Character.UnicodeBlock BOPOMOFO = new Character.UnicodeBlock("BOPOMOFO");
      public static final Character.UnicodeBlock HANGUL_COMPATIBILITY_JAMO = new Character.UnicodeBlock("HANGUL_COMPATIBILITY_JAMO", new String[]{"HANGUL COMPATIBILITY JAMO", "HANGULCOMPATIBILITYJAMO"});
      public static final Character.UnicodeBlock KANBUN = new Character.UnicodeBlock("KANBUN");
      public static final Character.UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS = new Character.UnicodeBlock("ENCLOSED_CJK_LETTERS_AND_MONTHS", new String[]{"ENCLOSED CJK LETTERS AND MONTHS", "ENCLOSEDCJKLETTERSANDMONTHS"});
      public static final Character.UnicodeBlock CJK_COMPATIBILITY = new Character.UnicodeBlock("CJK_COMPATIBILITY", new String[]{"CJK COMPATIBILITY", "CJKCOMPATIBILITY"});
      public static final Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS = new Character.UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS", new String[]{"CJK UNIFIED IDEOGRAPHS", "CJKUNIFIEDIDEOGRAPHS"});
      public static final Character.UnicodeBlock HANGUL_SYLLABLES = new Character.UnicodeBlock("HANGUL_SYLLABLES", new String[]{"HANGUL SYLLABLES", "HANGULSYLLABLES"});
      public static final Character.UnicodeBlock PRIVATE_USE_AREA = new Character.UnicodeBlock("PRIVATE_USE_AREA", new String[]{"PRIVATE USE AREA", "PRIVATEUSEAREA"});
      public static final Character.UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS = new Character.UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS", new String[]{"CJK COMPATIBILITY IDEOGRAPHS", "CJKCOMPATIBILITYIDEOGRAPHS"});
      public static final Character.UnicodeBlock ALPHABETIC_PRESENTATION_FORMS = new Character.UnicodeBlock("ALPHABETIC_PRESENTATION_FORMS", new String[]{"ALPHABETIC PRESENTATION FORMS", "ALPHABETICPRESENTATIONFORMS"});
      public static final Character.UnicodeBlock ARABIC_PRESENTATION_FORMS_A = new Character.UnicodeBlock("ARABIC_PRESENTATION_FORMS_A", new String[]{"ARABIC PRESENTATION FORMS-A", "ARABICPRESENTATIONFORMS-A"});
      public static final Character.UnicodeBlock COMBINING_HALF_MARKS = new Character.UnicodeBlock("COMBINING_HALF_MARKS", new String[]{"COMBINING HALF MARKS", "COMBININGHALFMARKS"});
      public static final Character.UnicodeBlock CJK_COMPATIBILITY_FORMS = new Character.UnicodeBlock("CJK_COMPATIBILITY_FORMS", new String[]{"CJK COMPATIBILITY FORMS", "CJKCOMPATIBILITYFORMS"});
      public static final Character.UnicodeBlock SMALL_FORM_VARIANTS = new Character.UnicodeBlock("SMALL_FORM_VARIANTS", new String[]{"SMALL FORM VARIANTS", "SMALLFORMVARIANTS"});
      public static final Character.UnicodeBlock ARABIC_PRESENTATION_FORMS_B = new Character.UnicodeBlock("ARABIC_PRESENTATION_FORMS_B", new String[]{"ARABIC PRESENTATION FORMS-B", "ARABICPRESENTATIONFORMS-B"});
      public static final Character.UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS = new Character.UnicodeBlock("HALFWIDTH_AND_FULLWIDTH_FORMS", new String[]{"HALFWIDTH AND FULLWIDTH FORMS", "HALFWIDTHANDFULLWIDTHFORMS"});
      public static final Character.UnicodeBlock SPECIALS = new Character.UnicodeBlock("SPECIALS");
      /** @deprecated */
      @Deprecated
      public static final Character.UnicodeBlock SURROGATES_AREA = new Character.UnicodeBlock("SURROGATES_AREA");
      public static final Character.UnicodeBlock SYRIAC = new Character.UnicodeBlock("SYRIAC");
      public static final Character.UnicodeBlock THAANA = new Character.UnicodeBlock("THAANA");
      public static final Character.UnicodeBlock SINHALA = new Character.UnicodeBlock("SINHALA");
      public static final Character.UnicodeBlock MYANMAR = new Character.UnicodeBlock("MYANMAR");
      public static final Character.UnicodeBlock ETHIOPIC = new Character.UnicodeBlock("ETHIOPIC");
      public static final Character.UnicodeBlock CHEROKEE = new Character.UnicodeBlock("CHEROKEE");
      public static final Character.UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = new Character.UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS", new String[]{"UNIFIED CANADIAN ABORIGINAL SYLLABICS", "UNIFIEDCANADIANABORIGINALSYLLABICS"});
      public static final Character.UnicodeBlock OGHAM = new Character.UnicodeBlock("OGHAM");
      public static final Character.UnicodeBlock RUNIC = new Character.UnicodeBlock("RUNIC");
      public static final Character.UnicodeBlock KHMER = new Character.UnicodeBlock("KHMER");
      public static final Character.UnicodeBlock MONGOLIAN = new Character.UnicodeBlock("MONGOLIAN");
      public static final Character.UnicodeBlock BRAILLE_PATTERNS = new Character.UnicodeBlock("BRAILLE_PATTERNS", new String[]{"BRAILLE PATTERNS", "BRAILLEPATTERNS"});
      public static final Character.UnicodeBlock CJK_RADICALS_SUPPLEMENT = new Character.UnicodeBlock("CJK_RADICALS_SUPPLEMENT", new String[]{"CJK RADICALS SUPPLEMENT", "CJKRADICALSSUPPLEMENT"});
      public static final Character.UnicodeBlock KANGXI_RADICALS = new Character.UnicodeBlock("KANGXI_RADICALS", new String[]{"KANGXI RADICALS", "KANGXIRADICALS"});
      public static final Character.UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS = new Character.UnicodeBlock("IDEOGRAPHIC_DESCRIPTION_CHARACTERS", new String[]{"IDEOGRAPHIC DESCRIPTION CHARACTERS", "IDEOGRAPHICDESCRIPTIONCHARACTERS"});
      public static final Character.UnicodeBlock BOPOMOFO_EXTENDED = new Character.UnicodeBlock("BOPOMOFO_EXTENDED", new String[]{"BOPOMOFO EXTENDED", "BOPOMOFOEXTENDED"});
      public static final Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A = new Character.UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A", new String[]{"CJK UNIFIED IDEOGRAPHS EXTENSION A", "CJKUNIFIEDIDEOGRAPHSEXTENSIONA"});
      public static final Character.UnicodeBlock YI_SYLLABLES = new Character.UnicodeBlock("YI_SYLLABLES", new String[]{"YI SYLLABLES", "YISYLLABLES"});
      public static final Character.UnicodeBlock YI_RADICALS = new Character.UnicodeBlock("YI_RADICALS", new String[]{"YI RADICALS", "YIRADICALS"});
      public static final Character.UnicodeBlock CYRILLIC_SUPPLEMENTARY = new Character.UnicodeBlock("CYRILLIC_SUPPLEMENTARY", new String[]{"CYRILLIC SUPPLEMENTARY", "CYRILLICSUPPLEMENTARY", "CYRILLIC SUPPLEMENT", "CYRILLICSUPPLEMENT"});
      public static final Character.UnicodeBlock TAGALOG = new Character.UnicodeBlock("TAGALOG");
      public static final Character.UnicodeBlock HANUNOO = new Character.UnicodeBlock("HANUNOO");
      public static final Character.UnicodeBlock BUHID = new Character.UnicodeBlock("BUHID");
      public static final Character.UnicodeBlock TAGBANWA = new Character.UnicodeBlock("TAGBANWA");
      public static final Character.UnicodeBlock LIMBU = new Character.UnicodeBlock("LIMBU");
      public static final Character.UnicodeBlock TAI_LE = new Character.UnicodeBlock("TAI_LE", new String[]{"TAI LE", "TAILE"});
      public static final Character.UnicodeBlock KHMER_SYMBOLS = new Character.UnicodeBlock("KHMER_SYMBOLS", new String[]{"KHMER SYMBOLS", "KHMERSYMBOLS"});
      public static final Character.UnicodeBlock PHONETIC_EXTENSIONS = new Character.UnicodeBlock("PHONETIC_EXTENSIONS", new String[]{"PHONETIC EXTENSIONS", "PHONETICEXTENSIONS"});
      public static final Character.UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A = new Character.UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A", new String[]{"MISCELLANEOUS MATHEMATICAL SYMBOLS-A", "MISCELLANEOUSMATHEMATICALSYMBOLS-A"});
      public static final Character.UnicodeBlock SUPPLEMENTAL_ARROWS_A = new Character.UnicodeBlock("SUPPLEMENTAL_ARROWS_A", new String[]{"SUPPLEMENTAL ARROWS-A", "SUPPLEMENTALARROWS-A"});
      public static final Character.UnicodeBlock SUPPLEMENTAL_ARROWS_B = new Character.UnicodeBlock("SUPPLEMENTAL_ARROWS_B", new String[]{"SUPPLEMENTAL ARROWS-B", "SUPPLEMENTALARROWS-B"});
      public static final Character.UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B = new Character.UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B", new String[]{"MISCELLANEOUS MATHEMATICAL SYMBOLS-B", "MISCELLANEOUSMATHEMATICALSYMBOLS-B"});
      public static final Character.UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS = new Character.UnicodeBlock("SUPPLEMENTAL_MATHEMATICAL_OPERATORS", new String[]{"SUPPLEMENTAL MATHEMATICAL OPERATORS", "SUPPLEMENTALMATHEMATICALOPERATORS"});
      public static final Character.UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS = new Character.UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_ARROWS", new String[]{"MISCELLANEOUS SYMBOLS AND ARROWS", "MISCELLANEOUSSYMBOLSANDARROWS"});
      public static final Character.UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS = new Character.UnicodeBlock("KATAKANA_PHONETIC_EXTENSIONS", new String[]{"KATAKANA PHONETIC EXTENSIONS", "KATAKANAPHONETICEXTENSIONS"});
      public static final Character.UnicodeBlock YIJING_HEXAGRAM_SYMBOLS = new Character.UnicodeBlock("YIJING_HEXAGRAM_SYMBOLS", new String[]{"YIJING HEXAGRAM SYMBOLS", "YIJINGHEXAGRAMSYMBOLS"});
      public static final Character.UnicodeBlock VARIATION_SELECTORS = new Character.UnicodeBlock("VARIATION_SELECTORS", new String[]{"VARIATION SELECTORS", "VARIATIONSELECTORS"});
      public static final Character.UnicodeBlock LINEAR_B_SYLLABARY = new Character.UnicodeBlock("LINEAR_B_SYLLABARY", new String[]{"LINEAR B SYLLABARY", "LINEARBSYLLABARY"});
      public static final Character.UnicodeBlock LINEAR_B_IDEOGRAMS = new Character.UnicodeBlock("LINEAR_B_IDEOGRAMS", new String[]{"LINEAR B IDEOGRAMS", "LINEARBIDEOGRAMS"});
      public static final Character.UnicodeBlock AEGEAN_NUMBERS = new Character.UnicodeBlock("AEGEAN_NUMBERS", new String[]{"AEGEAN NUMBERS", "AEGEANNUMBERS"});
      public static final Character.UnicodeBlock OLD_ITALIC = new Character.UnicodeBlock("OLD_ITALIC", new String[]{"OLD ITALIC", "OLDITALIC"});
      public static final Character.UnicodeBlock GOTHIC = new Character.UnicodeBlock("GOTHIC");
      public static final Character.UnicodeBlock UGARITIC = new Character.UnicodeBlock("UGARITIC");
      public static final Character.UnicodeBlock DESERET = new Character.UnicodeBlock("DESERET");
      public static final Character.UnicodeBlock SHAVIAN = new Character.UnicodeBlock("SHAVIAN");
      public static final Character.UnicodeBlock OSMANYA = new Character.UnicodeBlock("OSMANYA");
      public static final Character.UnicodeBlock CYPRIOT_SYLLABARY = new Character.UnicodeBlock("CYPRIOT_SYLLABARY", new String[]{"CYPRIOT SYLLABARY", "CYPRIOTSYLLABARY"});
      public static final Character.UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS = new Character.UnicodeBlock("BYZANTINE_MUSICAL_SYMBOLS", new String[]{"BYZANTINE MUSICAL SYMBOLS", "BYZANTINEMUSICALSYMBOLS"});
      public static final Character.UnicodeBlock MUSICAL_SYMBOLS = new Character.UnicodeBlock("MUSICAL_SYMBOLS", new String[]{"MUSICAL SYMBOLS", "MUSICALSYMBOLS"});
      public static final Character.UnicodeBlock TAI_XUAN_JING_SYMBOLS = new Character.UnicodeBlock("TAI_XUAN_JING_SYMBOLS", new String[]{"TAI XUAN JING SYMBOLS", "TAIXUANJINGSYMBOLS"});
      public static final Character.UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS = new Character.UnicodeBlock("MATHEMATICAL_ALPHANUMERIC_SYMBOLS", new String[]{"MATHEMATICAL ALPHANUMERIC SYMBOLS", "MATHEMATICALALPHANUMERICSYMBOLS"});
      public static final Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B = new Character.UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B", new String[]{"CJK UNIFIED IDEOGRAPHS EXTENSION B", "CJKUNIFIEDIDEOGRAPHSEXTENSIONB"});
      public static final Character.UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT = new Character.UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT", new String[]{"CJK COMPATIBILITY IDEOGRAPHS SUPPLEMENT", "CJKCOMPATIBILITYIDEOGRAPHSSUPPLEMENT"});
      public static final Character.UnicodeBlock TAGS = new Character.UnicodeBlock("TAGS");
      public static final Character.UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT = new Character.UnicodeBlock("VARIATION_SELECTORS_SUPPLEMENT", new String[]{"VARIATION SELECTORS SUPPLEMENT", "VARIATIONSELECTORSSUPPLEMENT"});
      public static final Character.UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A = new Character.UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_A", new String[]{"SUPPLEMENTARY PRIVATE USE AREA-A", "SUPPLEMENTARYPRIVATEUSEAREA-A"});
      public static final Character.UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B = new Character.UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_B", new String[]{"SUPPLEMENTARY PRIVATE USE AREA-B", "SUPPLEMENTARYPRIVATEUSEAREA-B"});
      public static final Character.UnicodeBlock HIGH_SURROGATES = new Character.UnicodeBlock("HIGH_SURROGATES", new String[]{"HIGH SURROGATES", "HIGHSURROGATES"});
      public static final Character.UnicodeBlock HIGH_PRIVATE_USE_SURROGATES = new Character.UnicodeBlock("HIGH_PRIVATE_USE_SURROGATES", new String[]{"HIGH PRIVATE USE SURROGATES", "HIGHPRIVATEUSESURROGATES"});
      public static final Character.UnicodeBlock LOW_SURROGATES = new Character.UnicodeBlock("LOW_SURROGATES", new String[]{"LOW SURROGATES", "LOWSURROGATES"});
      public static final Character.UnicodeBlock ARABIC_SUPPLEMENT = new Character.UnicodeBlock("ARABIC_SUPPLEMENT", new String[]{"ARABIC SUPPLEMENT", "ARABICSUPPLEMENT"});
      public static final Character.UnicodeBlock NKO = new Character.UnicodeBlock("NKO");
      public static final Character.UnicodeBlock SAMARITAN = new Character.UnicodeBlock("SAMARITAN");
      public static final Character.UnicodeBlock MANDAIC = new Character.UnicodeBlock("MANDAIC");
      public static final Character.UnicodeBlock ETHIOPIC_SUPPLEMENT = new Character.UnicodeBlock("ETHIOPIC_SUPPLEMENT", new String[]{"ETHIOPIC SUPPLEMENT", "ETHIOPICSUPPLEMENT"});
      public static final Character.UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED = new Character.UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED", new String[]{"UNIFIED CANADIAN ABORIGINAL SYLLABICS EXTENDED", "UNIFIEDCANADIANABORIGINALSYLLABICSEXTENDED"});
      public static final Character.UnicodeBlock NEW_TAI_LUE = new Character.UnicodeBlock("NEW_TAI_LUE", new String[]{"NEW TAI LUE", "NEWTAILUE"});
      public static final Character.UnicodeBlock BUGINESE = new Character.UnicodeBlock("BUGINESE");
      public static final Character.UnicodeBlock TAI_THAM = new Character.UnicodeBlock("TAI_THAM", new String[]{"TAI THAM", "TAITHAM"});
      public static final Character.UnicodeBlock BALINESE = new Character.UnicodeBlock("BALINESE");
      public static final Character.UnicodeBlock SUNDANESE = new Character.UnicodeBlock("SUNDANESE");
      public static final Character.UnicodeBlock BATAK = new Character.UnicodeBlock("BATAK");
      public static final Character.UnicodeBlock LEPCHA = new Character.UnicodeBlock("LEPCHA");
      public static final Character.UnicodeBlock OL_CHIKI = new Character.UnicodeBlock("OL_CHIKI", new String[]{"OL CHIKI", "OLCHIKI"});
      public static final Character.UnicodeBlock VEDIC_EXTENSIONS = new Character.UnicodeBlock("VEDIC_EXTENSIONS", new String[]{"VEDIC EXTENSIONS", "VEDICEXTENSIONS"});
      public static final Character.UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT = new Character.UnicodeBlock("PHONETIC_EXTENSIONS_SUPPLEMENT", new String[]{"PHONETIC EXTENSIONS SUPPLEMENT", "PHONETICEXTENSIONSSUPPLEMENT"});
      public static final Character.UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT = new Character.UnicodeBlock("COMBINING_DIACRITICAL_MARKS_SUPPLEMENT", new String[]{"COMBINING DIACRITICAL MARKS SUPPLEMENT", "COMBININGDIACRITICALMARKSSUPPLEMENT"});
      public static final Character.UnicodeBlock GLAGOLITIC = new Character.UnicodeBlock("GLAGOLITIC");
      public static final Character.UnicodeBlock LATIN_EXTENDED_C = new Character.UnicodeBlock("LATIN_EXTENDED_C", new String[]{"LATIN EXTENDED-C", "LATINEXTENDED-C"});
      public static final Character.UnicodeBlock COPTIC = new Character.UnicodeBlock("COPTIC");
      public static final Character.UnicodeBlock GEORGIAN_SUPPLEMENT = new Character.UnicodeBlock("GEORGIAN_SUPPLEMENT", new String[]{"GEORGIAN SUPPLEMENT", "GEORGIANSUPPLEMENT"});
      public static final Character.UnicodeBlock TIFINAGH = new Character.UnicodeBlock("TIFINAGH");
      public static final Character.UnicodeBlock ETHIOPIC_EXTENDED = new Character.UnicodeBlock("ETHIOPIC_EXTENDED", new String[]{"ETHIOPIC EXTENDED", "ETHIOPICEXTENDED"});
      public static final Character.UnicodeBlock CYRILLIC_EXTENDED_A = new Character.UnicodeBlock("CYRILLIC_EXTENDED_A", new String[]{"CYRILLIC EXTENDED-A", "CYRILLICEXTENDED-A"});
      public static final Character.UnicodeBlock SUPPLEMENTAL_PUNCTUATION = new Character.UnicodeBlock("SUPPLEMENTAL_PUNCTUATION", new String[]{"SUPPLEMENTAL PUNCTUATION", "SUPPLEMENTALPUNCTUATION"});
      public static final Character.UnicodeBlock CJK_STROKES = new Character.UnicodeBlock("CJK_STROKES", new String[]{"CJK STROKES", "CJKSTROKES"});
      public static final Character.UnicodeBlock LISU = new Character.UnicodeBlock("LISU");
      public static final Character.UnicodeBlock VAI = new Character.UnicodeBlock("VAI");
      public static final Character.UnicodeBlock CYRILLIC_EXTENDED_B = new Character.UnicodeBlock("CYRILLIC_EXTENDED_B", new String[]{"CYRILLIC EXTENDED-B", "CYRILLICEXTENDED-B"});
      public static final Character.UnicodeBlock BAMUM = new Character.UnicodeBlock("BAMUM");
      public static final Character.UnicodeBlock MODIFIER_TONE_LETTERS = new Character.UnicodeBlock("MODIFIER_TONE_LETTERS", new String[]{"MODIFIER TONE LETTERS", "MODIFIERTONELETTERS"});
      public static final Character.UnicodeBlock LATIN_EXTENDED_D = new Character.UnicodeBlock("LATIN_EXTENDED_D", new String[]{"LATIN EXTENDED-D", "LATINEXTENDED-D"});
      public static final Character.UnicodeBlock SYLOTI_NAGRI = new Character.UnicodeBlock("SYLOTI_NAGRI", new String[]{"SYLOTI NAGRI", "SYLOTINAGRI"});
      public static final Character.UnicodeBlock COMMON_INDIC_NUMBER_FORMS = new Character.UnicodeBlock("COMMON_INDIC_NUMBER_FORMS", new String[]{"COMMON INDIC NUMBER FORMS", "COMMONINDICNUMBERFORMS"});
      public static final Character.UnicodeBlock PHAGS_PA = new Character.UnicodeBlock("PHAGS_PA", "PHAGS-PA");
      public static final Character.UnicodeBlock SAURASHTRA = new Character.UnicodeBlock("SAURASHTRA");
      public static final Character.UnicodeBlock DEVANAGARI_EXTENDED = new Character.UnicodeBlock("DEVANAGARI_EXTENDED", new String[]{"DEVANAGARI EXTENDED", "DEVANAGARIEXTENDED"});
      public static final Character.UnicodeBlock KAYAH_LI = new Character.UnicodeBlock("KAYAH_LI", new String[]{"KAYAH LI", "KAYAHLI"});
      public static final Character.UnicodeBlock REJANG = new Character.UnicodeBlock("REJANG");
      public static final Character.UnicodeBlock HANGUL_JAMO_EXTENDED_A = new Character.UnicodeBlock("HANGUL_JAMO_EXTENDED_A", new String[]{"HANGUL JAMO EXTENDED-A", "HANGULJAMOEXTENDED-A"});
      public static final Character.UnicodeBlock JAVANESE = new Character.UnicodeBlock("JAVANESE");
      public static final Character.UnicodeBlock CHAM = new Character.UnicodeBlock("CHAM");
      public static final Character.UnicodeBlock MYANMAR_EXTENDED_A = new Character.UnicodeBlock("MYANMAR_EXTENDED_A", new String[]{"MYANMAR EXTENDED-A", "MYANMAREXTENDED-A"});
      public static final Character.UnicodeBlock TAI_VIET = new Character.UnicodeBlock("TAI_VIET", new String[]{"TAI VIET", "TAIVIET"});
      public static final Character.UnicodeBlock ETHIOPIC_EXTENDED_A = new Character.UnicodeBlock("ETHIOPIC_EXTENDED_A", new String[]{"ETHIOPIC EXTENDED-A", "ETHIOPICEXTENDED-A"});
      public static final Character.UnicodeBlock MEETEI_MAYEK = new Character.UnicodeBlock("MEETEI_MAYEK", new String[]{"MEETEI MAYEK", "MEETEIMAYEK"});
      public static final Character.UnicodeBlock HANGUL_JAMO_EXTENDED_B = new Character.UnicodeBlock("HANGUL_JAMO_EXTENDED_B", new String[]{"HANGUL JAMO EXTENDED-B", "HANGULJAMOEXTENDED-B"});
      public static final Character.UnicodeBlock VERTICAL_FORMS = new Character.UnicodeBlock("VERTICAL_FORMS", new String[]{"VERTICAL FORMS", "VERTICALFORMS"});
      public static final Character.UnicodeBlock ANCIENT_GREEK_NUMBERS = new Character.UnicodeBlock("ANCIENT_GREEK_NUMBERS", new String[]{"ANCIENT GREEK NUMBERS", "ANCIENTGREEKNUMBERS"});
      public static final Character.UnicodeBlock ANCIENT_SYMBOLS = new Character.UnicodeBlock("ANCIENT_SYMBOLS", new String[]{"ANCIENT SYMBOLS", "ANCIENTSYMBOLS"});
      public static final Character.UnicodeBlock PHAISTOS_DISC = new Character.UnicodeBlock("PHAISTOS_DISC", new String[]{"PHAISTOS DISC", "PHAISTOSDISC"});
      public static final Character.UnicodeBlock LYCIAN = new Character.UnicodeBlock("LYCIAN");
      public static final Character.UnicodeBlock CARIAN = new Character.UnicodeBlock("CARIAN");
      public static final Character.UnicodeBlock OLD_PERSIAN = new Character.UnicodeBlock("OLD_PERSIAN", new String[]{"OLD PERSIAN", "OLDPERSIAN"});
      public static final Character.UnicodeBlock IMPERIAL_ARAMAIC = new Character.UnicodeBlock("IMPERIAL_ARAMAIC", new String[]{"IMPERIAL ARAMAIC", "IMPERIALARAMAIC"});
      public static final Character.UnicodeBlock PHOENICIAN = new Character.UnicodeBlock("PHOENICIAN");
      public static final Character.UnicodeBlock LYDIAN = new Character.UnicodeBlock("LYDIAN");
      public static final Character.UnicodeBlock KHAROSHTHI = new Character.UnicodeBlock("KHAROSHTHI");
      public static final Character.UnicodeBlock OLD_SOUTH_ARABIAN = new Character.UnicodeBlock("OLD_SOUTH_ARABIAN", new String[]{"OLD SOUTH ARABIAN", "OLDSOUTHARABIAN"});
      public static final Character.UnicodeBlock AVESTAN = new Character.UnicodeBlock("AVESTAN");
      public static final Character.UnicodeBlock INSCRIPTIONAL_PARTHIAN = new Character.UnicodeBlock("INSCRIPTIONAL_PARTHIAN", new String[]{"INSCRIPTIONAL PARTHIAN", "INSCRIPTIONALPARTHIAN"});
      public static final Character.UnicodeBlock INSCRIPTIONAL_PAHLAVI = new Character.UnicodeBlock("INSCRIPTIONAL_PAHLAVI", new String[]{"INSCRIPTIONAL PAHLAVI", "INSCRIPTIONALPAHLAVI"});
      public static final Character.UnicodeBlock OLD_TURKIC = new Character.UnicodeBlock("OLD_TURKIC", new String[]{"OLD TURKIC", "OLDTURKIC"});
      public static final Character.UnicodeBlock RUMI_NUMERAL_SYMBOLS = new Character.UnicodeBlock("RUMI_NUMERAL_SYMBOLS", new String[]{"RUMI NUMERAL SYMBOLS", "RUMINUMERALSYMBOLS"});
      public static final Character.UnicodeBlock BRAHMI = new Character.UnicodeBlock("BRAHMI");
      public static final Character.UnicodeBlock KAITHI = new Character.UnicodeBlock("KAITHI");
      public static final Character.UnicodeBlock CUNEIFORM = new Character.UnicodeBlock("CUNEIFORM");
      public static final Character.UnicodeBlock CUNEIFORM_NUMBERS_AND_PUNCTUATION = new Character.UnicodeBlock("CUNEIFORM_NUMBERS_AND_PUNCTUATION", new String[]{"CUNEIFORM NUMBERS AND PUNCTUATION", "CUNEIFORMNUMBERSANDPUNCTUATION"});
      public static final Character.UnicodeBlock EGYPTIAN_HIEROGLYPHS = new Character.UnicodeBlock("EGYPTIAN_HIEROGLYPHS", new String[]{"EGYPTIAN HIEROGLYPHS", "EGYPTIANHIEROGLYPHS"});
      public static final Character.UnicodeBlock BAMUM_SUPPLEMENT = new Character.UnicodeBlock("BAMUM_SUPPLEMENT", new String[]{"BAMUM SUPPLEMENT", "BAMUMSUPPLEMENT"});
      public static final Character.UnicodeBlock KANA_SUPPLEMENT = new Character.UnicodeBlock("KANA_SUPPLEMENT", new String[]{"KANA SUPPLEMENT", "KANASUPPLEMENT"});
      public static final Character.UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION = new Character.UnicodeBlock("ANCIENT_GREEK_MUSICAL_NOTATION", new String[]{"ANCIENT GREEK MUSICAL NOTATION", "ANCIENTGREEKMUSICALNOTATION"});
      public static final Character.UnicodeBlock COUNTING_ROD_NUMERALS = new Character.UnicodeBlock("COUNTING_ROD_NUMERALS", new String[]{"COUNTING ROD NUMERALS", "COUNTINGRODNUMERALS"});
      public static final Character.UnicodeBlock MAHJONG_TILES = new Character.UnicodeBlock("MAHJONG_TILES", new String[]{"MAHJONG TILES", "MAHJONGTILES"});
      public static final Character.UnicodeBlock DOMINO_TILES = new Character.UnicodeBlock("DOMINO_TILES", new String[]{"DOMINO TILES", "DOMINOTILES"});
      public static final Character.UnicodeBlock PLAYING_CARDS = new Character.UnicodeBlock("PLAYING_CARDS", new String[]{"PLAYING CARDS", "PLAYINGCARDS"});
      public static final Character.UnicodeBlock ENCLOSED_ALPHANUMERIC_SUPPLEMENT = new Character.UnicodeBlock("ENCLOSED_ALPHANUMERIC_SUPPLEMENT", new String[]{"ENCLOSED ALPHANUMERIC SUPPLEMENT", "ENCLOSEDALPHANUMERICSUPPLEMENT"});
      public static final Character.UnicodeBlock ENCLOSED_IDEOGRAPHIC_SUPPLEMENT = new Character.UnicodeBlock("ENCLOSED_IDEOGRAPHIC_SUPPLEMENT", new String[]{"ENCLOSED IDEOGRAPHIC SUPPLEMENT", "ENCLOSEDIDEOGRAPHICSUPPLEMENT"});
      public static final Character.UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS = new Character.UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS", new String[]{"MISCELLANEOUS SYMBOLS AND PICTOGRAPHS", "MISCELLANEOUSSYMBOLSANDPICTOGRAPHS"});
      public static final Character.UnicodeBlock EMOTICONS = new Character.UnicodeBlock("EMOTICONS");
      public static final Character.UnicodeBlock TRANSPORT_AND_MAP_SYMBOLS = new Character.UnicodeBlock("TRANSPORT_AND_MAP_SYMBOLS", new String[]{"TRANSPORT AND MAP SYMBOLS", "TRANSPORTANDMAPSYMBOLS"});
      public static final Character.UnicodeBlock ALCHEMICAL_SYMBOLS = new Character.UnicodeBlock("ALCHEMICAL_SYMBOLS", new String[]{"ALCHEMICAL SYMBOLS", "ALCHEMICALSYMBOLS"});
      public static final Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C = new Character.UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C", new String[]{"CJK UNIFIED IDEOGRAPHS EXTENSION C", "CJKUNIFIEDIDEOGRAPHSEXTENSIONC"});
      public static final Character.UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D = new Character.UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D", new String[]{"CJK UNIFIED IDEOGRAPHS EXTENSION D", "CJKUNIFIEDIDEOGRAPHSEXTENSIOND"});
      public static final Character.UnicodeBlock ARABIC_EXTENDED_A = new Character.UnicodeBlock("ARABIC_EXTENDED_A", new String[]{"ARABIC EXTENDED-A", "ARABICEXTENDED-A"});
      public static final Character.UnicodeBlock SUNDANESE_SUPPLEMENT = new Character.UnicodeBlock("SUNDANESE_SUPPLEMENT", new String[]{"SUNDANESE SUPPLEMENT", "SUNDANESESUPPLEMENT"});
      public static final Character.UnicodeBlock MEETEI_MAYEK_EXTENSIONS = new Character.UnicodeBlock("MEETEI_MAYEK_EXTENSIONS", new String[]{"MEETEI MAYEK EXTENSIONS", "MEETEIMAYEKEXTENSIONS"});
      public static final Character.UnicodeBlock MEROITIC_HIEROGLYPHS = new Character.UnicodeBlock("MEROITIC_HIEROGLYPHS", new String[]{"MEROITIC HIEROGLYPHS", "MEROITICHIEROGLYPHS"});
      public static final Character.UnicodeBlock MEROITIC_CURSIVE = new Character.UnicodeBlock("MEROITIC_CURSIVE", new String[]{"MEROITIC CURSIVE", "MEROITICCURSIVE"});
      public static final Character.UnicodeBlock SORA_SOMPENG = new Character.UnicodeBlock("SORA_SOMPENG", new String[]{"SORA SOMPENG", "SORASOMPENG"});
      public static final Character.UnicodeBlock CHAKMA = new Character.UnicodeBlock("CHAKMA");
      public static final Character.UnicodeBlock SHARADA = new Character.UnicodeBlock("SHARADA");
      public static final Character.UnicodeBlock TAKRI = new Character.UnicodeBlock("TAKRI");
      public static final Character.UnicodeBlock MIAO = new Character.UnicodeBlock("MIAO");
      public static final Character.UnicodeBlock ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS = new Character.UnicodeBlock("ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS", new String[]{"ARABIC MATHEMATICAL ALPHABETIC SYMBOLS", "ARABICMATHEMATICALALPHABETICSYMBOLS"});
      private static final int[] blockStarts = new int[]{0, 128, 256, 384, 592, 688, 768, 880, 1024, 1280, 1328, 1424, 1536, 1792, 1872, 1920, 1984, 2048, 2112, 2144, 2208, 2304, 2432, 2560, 2688, 2816, 2944, 3072, 3200, 3328, 3456, 3584, 3712, 3840, 4096, 4256, 4352, 4608, 4992, 5024, 5120, 5760, 5792, 5888, 5920, 5952, 5984, 6016, 6144, 6320, 6400, 6480, 6528, 6624, 6656, 6688, 6832, 6912, 7040, 7104, 7168, 7248, 7296, 7360, 7376, 7424, 7552, 7616, 7680, 7936, 8192, 8304, 8352, 8400, 8448, 8528, 8592, 8704, 8960, 9216, 9280, 9312, 9472, 9600, 9632, 9728, 9984, 10176, 10224, 10240, 10496, 10624, 10752, 11008, 11264, 11360, 11392, 11520, 11568, 11648, 11744, 11776, 11904, 12032, 12256, 12272, 12288, 12352, 12448, 12544, 12592, 12688, 12704, 12736, 12784, 12800, 13056, 13312, 19904, 19968, 40960, 42128, 42192, 42240, 42560, 42656, 42752, 42784, 43008, 43056, 43072, 43136, 43232, 43264, 43312, 43360, 43392, 43488, 43520, 43616, 43648, 43744, 43776, 43824, 43968, 44032, 55216, 55296, 56192, 56320, 57344, 63744, 64256, 64336, 65024, 65040, 65056, 65072, 65104, 65136, 65280, 65520, 65536, 65664, 65792, 65856, 65936, 66000, 66048, 66176, 66208, 66272, 66304, 66352, 66384, 66432, 66464, 66528, 66560, 66640, 66688, 66736, 67584, 67648, 67680, 67840, 67872, 67904, 67968, 68000, 68096, 68192, 68224, 68352, 68416, 68448, 68480, 68608, 68688, 69216, 69248, 69632, 69760, 69840, 69888, 69968, 70016, 70112, 71296, 71376, 73728, 74752, 74880, 77824, 78896, 92160, 92736, 93952, 94112, 110592, 110848, 118784, 119040, 119296, 119376, 119552, 119648, 119680, 119808, 120832, 126464, 126720, 126976, 127024, 127136, 127232, 127488, 127744, 128512, 128592, 128640, 128768, 128896, 131072, 173792, 173824, 177984, 178208, 194560, 195104, 917504, 917632, 917760, 918000, 983040, 1048576};
      private static final Character.UnicodeBlock[] blocks;

      private UnicodeBlock(String var1) {
         super(var1);
         map.put(var1, this);
      }

      private UnicodeBlock(String var1, String var2) {
         this(var1);
         map.put(var2, this);
      }

      private UnicodeBlock(String var1, String... var2) {
         this(var1);
         String[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String var6 = var3[var5];
            map.put(var6, this);
         }

      }

      public static Character.UnicodeBlock of(char var0) {
         return of((int)var0);
      }

      public static Character.UnicodeBlock of(int var0) {
         if (!Character.isValidCodePoint(var0)) {
            throw new IllegalArgumentException();
         } else {
            int var2 = 0;
            int var1 = blockStarts.length;

            int var3;
            for(var3 = var1 / 2; var1 - var2 > 1; var3 = (var1 + var2) / 2) {
               if (var0 >= blockStarts[var3]) {
                  var2 = var3;
               } else {
                  var1 = var3;
               }
            }

            return blocks[var3];
         }
      }

      public static final Character.UnicodeBlock forName(String var0) {
         Character.UnicodeBlock var1 = (Character.UnicodeBlock)map.get(var0.toUpperCase(Locale.US));
         if (var1 == null) {
            throw new IllegalArgumentException();
         } else {
            return var1;
         }
      }

      static {
         blocks = new Character.UnicodeBlock[]{BASIC_LATIN, LATIN_1_SUPPLEMENT, LATIN_EXTENDED_A, LATIN_EXTENDED_B, IPA_EXTENSIONS, SPACING_MODIFIER_LETTERS, COMBINING_DIACRITICAL_MARKS, GREEK, CYRILLIC, CYRILLIC_SUPPLEMENTARY, ARMENIAN, HEBREW, ARABIC, SYRIAC, ARABIC_SUPPLEMENT, THAANA, NKO, SAMARITAN, MANDAIC, null, ARABIC_EXTENDED_A, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, ORIYA, TAMIL, TELUGU, KANNADA, MALAYALAM, SINHALA, THAI, LAO, TIBETAN, MYANMAR, GEORGIAN, HANGUL_JAMO, ETHIOPIC, ETHIOPIC_SUPPLEMENT, CHEROKEE, UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS, OGHAM, RUNIC, TAGALOG, HANUNOO, BUHID, TAGBANWA, KHMER, MONGOLIAN, UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED, LIMBU, TAI_LE, NEW_TAI_LUE, KHMER_SYMBOLS, BUGINESE, TAI_THAM, null, BALINESE, SUNDANESE, BATAK, LEPCHA, OL_CHIKI, null, SUNDANESE_SUPPLEMENT, VEDIC_EXTENSIONS, PHONETIC_EXTENSIONS, PHONETIC_EXTENSIONS_SUPPLEMENT, COMBINING_DIACRITICAL_MARKS_SUPPLEMENT, LATIN_EXTENDED_ADDITIONAL, GREEK_EXTENDED, GENERAL_PUNCTUATION, SUPERSCRIPTS_AND_SUBSCRIPTS, CURRENCY_SYMBOLS, COMBINING_MARKS_FOR_SYMBOLS, LETTERLIKE_SYMBOLS, NUMBER_FORMS, ARROWS, MATHEMATICAL_OPERATORS, MISCELLANEOUS_TECHNICAL, CONTROL_PICTURES, OPTICAL_CHARACTER_RECOGNITION, ENCLOSED_ALPHANUMERICS, BOX_DRAWING, BLOCK_ELEMENTS, GEOMETRIC_SHAPES, MISCELLANEOUS_SYMBOLS, DINGBATS, MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A, SUPPLEMENTAL_ARROWS_A, BRAILLE_PATTERNS, SUPPLEMENTAL_ARROWS_B, MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B, SUPPLEMENTAL_MATHEMATICAL_OPERATORS, MISCELLANEOUS_SYMBOLS_AND_ARROWS, GLAGOLITIC, LATIN_EXTENDED_C, COPTIC, GEORGIAN_SUPPLEMENT, TIFINAGH, ETHIOPIC_EXTENDED, CYRILLIC_EXTENDED_A, SUPPLEMENTAL_PUNCTUATION, CJK_RADICALS_SUPPLEMENT, KANGXI_RADICALS, null, IDEOGRAPHIC_DESCRIPTION_CHARACTERS, CJK_SYMBOLS_AND_PUNCTUATION, HIRAGANA, KATAKANA, BOPOMOFO, HANGUL_COMPATIBILITY_JAMO, KANBUN, BOPOMOFO_EXTENDED, CJK_STROKES, KATAKANA_PHONETIC_EXTENSIONS, ENCLOSED_CJK_LETTERS_AND_MONTHS, CJK_COMPATIBILITY, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A, YIJING_HEXAGRAM_SYMBOLS, CJK_UNIFIED_IDEOGRAPHS, YI_SYLLABLES, YI_RADICALS, LISU, VAI, CYRILLIC_EXTENDED_B, BAMUM, MODIFIER_TONE_LETTERS, LATIN_EXTENDED_D, SYLOTI_NAGRI, COMMON_INDIC_NUMBER_FORMS, PHAGS_PA, SAURASHTRA, DEVANAGARI_EXTENDED, KAYAH_LI, REJANG, HANGUL_JAMO_EXTENDED_A, JAVANESE, null, CHAM, MYANMAR_EXTENDED_A, TAI_VIET, MEETEI_MAYEK_EXTENSIONS, ETHIOPIC_EXTENDED_A, null, MEETEI_MAYEK, HANGUL_SYLLABLES, HANGUL_JAMO_EXTENDED_B, HIGH_SURROGATES, HIGH_PRIVATE_USE_SURROGATES, LOW_SURROGATES, PRIVATE_USE_AREA, CJK_COMPATIBILITY_IDEOGRAPHS, ALPHABETIC_PRESENTATION_FORMS, ARABIC_PRESENTATION_FORMS_A, VARIATION_SELECTORS, VERTICAL_FORMS, COMBINING_HALF_MARKS, CJK_COMPATIBILITY_FORMS, SMALL_FORM_VARIANTS, ARABIC_PRESENTATION_FORMS_B, HALFWIDTH_AND_FULLWIDTH_FORMS, SPECIALS, LINEAR_B_SYLLABARY, LINEAR_B_IDEOGRAMS, AEGEAN_NUMBERS, ANCIENT_GREEK_NUMBERS, ANCIENT_SYMBOLS, PHAISTOS_DISC, null, LYCIAN, CARIAN, null, OLD_ITALIC, GOTHIC, null, UGARITIC, OLD_PERSIAN, null, DESERET, SHAVIAN, OSMANYA, null, CYPRIOT_SYLLABARY, IMPERIAL_ARAMAIC, null, PHOENICIAN, LYDIAN, null, MEROITIC_HIEROGLYPHS, MEROITIC_CURSIVE, KHAROSHTHI, OLD_SOUTH_ARABIAN, null, AVESTAN, INSCRIPTIONAL_PARTHIAN, INSCRIPTIONAL_PAHLAVI, null, OLD_TURKIC, null, RUMI_NUMERAL_SYMBOLS, null, BRAHMI, KAITHI, SORA_SOMPENG, CHAKMA, null, SHARADA, null, TAKRI, null, CUNEIFORM, CUNEIFORM_NUMBERS_AND_PUNCTUATION, null, EGYPTIAN_HIEROGLYPHS, null, BAMUM_SUPPLEMENT, null, MIAO, null, KANA_SUPPLEMENT, null, BYZANTINE_MUSICAL_SYMBOLS, MUSICAL_SYMBOLS, ANCIENT_GREEK_MUSICAL_NOTATION, null, TAI_XUAN_JING_SYMBOLS, COUNTING_ROD_NUMERALS, null, MATHEMATICAL_ALPHANUMERIC_SYMBOLS, null, ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS, null, MAHJONG_TILES, DOMINO_TILES, PLAYING_CARDS, ENCLOSED_ALPHANUMERIC_SUPPLEMENT, ENCLOSED_IDEOGRAPHIC_SUPPLEMENT, MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS, EMOTICONS, null, TRANSPORT_AND_MAP_SYMBOLS, ALCHEMICAL_SYMBOLS, null, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B, null, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D, null, CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT, null, TAGS, null, VARIATION_SELECTORS_SUPPLEMENT, null, SUPPLEMENTARY_PRIVATE_USE_AREA_A, SUPPLEMENTARY_PRIVATE_USE_AREA_B};
      }
   }

   public static class Subset {
      private String name;

      protected Subset(String var1) {
         if (var1 == null) {
            throw new NullPointerException("name");
         } else {
            this.name = var1;
         }
      }

      public final boolean equals(Object var1) {
         return this == var1;
      }

      public final int hashCode() {
         return super.hashCode();
      }

      public final String toString() {
         return this.name;
      }
   }
}
