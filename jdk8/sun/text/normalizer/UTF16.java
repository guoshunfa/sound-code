package sun.text.normalizer;

public final class UTF16 {
   public static final int CODEPOINT_MIN_VALUE = 0;
   public static final int CODEPOINT_MAX_VALUE = 1114111;
   public static final int SUPPLEMENTARY_MIN_VALUE = 65536;
   public static final int LEAD_SURROGATE_MIN_VALUE = 55296;
   public static final int TRAIL_SURROGATE_MIN_VALUE = 56320;
   public static final int LEAD_SURROGATE_MAX_VALUE = 56319;
   public static final int TRAIL_SURROGATE_MAX_VALUE = 57343;
   public static final int SURROGATE_MIN_VALUE = 55296;
   private static final int LEAD_SURROGATE_SHIFT_ = 10;
   private static final int TRAIL_SURROGATE_MASK_ = 1023;
   private static final int LEAD_SURROGATE_OFFSET_ = 55232;

   public static int charAt(String var0, int var1) {
      char var2 = var0.charAt(var1);
      return var2 < '\ud800' ? var2 : _charAt(var0, var1, var2);
   }

   private static int _charAt(String var0, int var1, char var2) {
      if (var2 > '\udfff') {
         return var2;
      } else {
         char var3;
         if (var2 <= '\udbff') {
            ++var1;
            if (var0.length() != var1) {
               var3 = var0.charAt(var1);
               if (var3 >= '\udc00' && var3 <= '\udfff') {
                  return UCharacterProperty.getRawSupplementary(var2, var3);
               }
            }
         } else {
            --var1;
            if (var1 >= 0) {
               var3 = var0.charAt(var1);
               if (var3 >= '\ud800' && var3 <= '\udbff') {
                  return UCharacterProperty.getRawSupplementary(var3, var2);
               }
            }
         }

         return var2;
      }
   }

   public static int charAt(char[] var0, int var1, int var2, int var3) {
      var3 += var1;
      if (var3 >= var1 && var3 < var2) {
         char var4 = var0[var3];
         if (!isSurrogate(var4)) {
            return var4;
         } else {
            char var5;
            if (var4 <= '\udbff') {
               ++var3;
               if (var3 >= var2) {
                  return var4;
               }

               var5 = var0[var3];
               if (isTrailSurrogate(var5)) {
                  return UCharacterProperty.getRawSupplementary(var4, var5);
               }
            } else {
               if (var3 == var1) {
                  return var4;
               }

               --var3;
               var5 = var0[var3];
               if (isLeadSurrogate(var5)) {
                  return UCharacterProperty.getRawSupplementary(var5, var4);
               }
            }

            return var4;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException(var3);
      }
   }

   public static int getCharCount(int var0) {
      return var0 < 65536 ? 1 : 2;
   }

   public static boolean isSurrogate(char var0) {
      return '\ud800' <= var0 && var0 <= '\udfff';
   }

   public static boolean isTrailSurrogate(char var0) {
      return '\udc00' <= var0 && var0 <= '\udfff';
   }

   public static boolean isLeadSurrogate(char var0) {
      return '\ud800' <= var0 && var0 <= '\udbff';
   }

   public static char getLeadSurrogate(int var0) {
      return var0 >= 65536 ? (char)('ퟀ' + (var0 >> 10)) : '\u0000';
   }

   public static char getTrailSurrogate(int var0) {
      return var0 >= 65536 ? (char)('\udc00' + (var0 & 1023)) : (char)var0;
   }

   public static String valueOf(int var0) {
      if (var0 >= 0 && var0 <= 1114111) {
         return toString(var0);
      } else {
         throw new IllegalArgumentException("Illegal codepoint");
      }
   }

   public static StringBuffer append(StringBuffer var0, int var1) {
      if (var1 >= 0 && var1 <= 1114111) {
         if (var1 >= 65536) {
            var0.append(getLeadSurrogate(var1));
            var0.append(getTrailSurrogate(var1));
         } else {
            var0.append((char)var1);
         }

         return var0;
      } else {
         throw new IllegalArgumentException("Illegal codepoint: " + Integer.toHexString(var1));
      }
   }

   public static int moveCodePointOffset(char[] var0, int var1, int var2, int var3, int var4) {
      int var5 = var0.length;
      int var8 = var3 + var1;
      if (var1 >= 0 && var2 >= var1) {
         if (var2 > var5) {
            throw new StringIndexOutOfBoundsException(var2);
         } else if (var3 >= 0 && var8 <= var2) {
            int var6;
            char var7;
            if (var4 > 0) {
               if (var4 + var8 > var5) {
                  throw new StringIndexOutOfBoundsException(var8);
               }

               for(var6 = var4; var8 < var2 && var6 > 0; ++var8) {
                  var7 = var0[var8];
                  if (isLeadSurrogate(var7) && var8 + 1 < var2 && isTrailSurrogate(var0[var8 + 1])) {
                     ++var8;
                  }

                  --var6;
               }
            } else {
               if (var8 + var4 < var1) {
                  throw new StringIndexOutOfBoundsException(var8);
               }

               for(var6 = -var4; var6 > 0; --var6) {
                  --var8;
                  if (var8 < var1) {
                     break;
                  }

                  var7 = var0[var8];
                  if (isTrailSurrogate(var7) && var8 > var1 && isLeadSurrogate(var0[var8 - 1])) {
                     --var8;
                  }
               }
            }

            if (var6 != 0) {
               throw new StringIndexOutOfBoundsException(var4);
            } else {
               var8 -= var1;
               return var8;
            }
         } else {
            throw new StringIndexOutOfBoundsException(var3);
         }
      } else {
         throw new StringIndexOutOfBoundsException(var1);
      }
   }

   private static String toString(int var0) {
      if (var0 < 65536) {
         return String.valueOf((char)var0);
      } else {
         StringBuffer var1 = new StringBuffer();
         var1.append(getLeadSurrogate(var0));
         var1.append(getTrailSurrogate(var0));
         return var1.toString();
      }
   }
}
