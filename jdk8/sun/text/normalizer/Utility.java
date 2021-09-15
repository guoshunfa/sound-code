package sun.text.normalizer;

public final class Utility {
   private static final char[] UNESCAPE_MAP = new char[]{'a', '\u0007', 'b', '\b', 'e', '\u001b', 'f', '\f', 'n', '\n', 'r', '\r', 't', '\t', 'v', '\u000b'};
   static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

   public static final boolean arrayRegionMatches(char[] var0, int var1, char[] var2, int var3, int var4) {
      int var5 = var1 + var4;
      int var6 = var3 - var1;

      for(int var7 = var1; var7 < var5; ++var7) {
         if (var0[var7] != var2[var7 + var6]) {
            return false;
         }
      }

      return true;
   }

   public static final String escape(String var0) {
      StringBuffer var1 = new StringBuffer();
      int var2 = 0;

      while(true) {
         while(var2 < var0.length()) {
            int var3 = UTF16.charAt(var0, var2);
            var2 += UTF16.getCharCount(var3);
            if (var3 >= 32 && var3 <= 127) {
               if (var3 == 92) {
                  var1.append("\\\\");
               } else {
                  var1.append((char)var3);
               }
            } else {
               boolean var4 = var3 <= 65535;
               var1.append(var4 ? "\\u" : "\\U");
               hex(var3, var4 ? 4 : 8, var1);
            }
         }

         return var1.toString();
      }
   }

   public static int unescapeAt(String var0, int[] var1) {
      int var3 = 0;
      int var4 = 0;
      byte var5 = 0;
      byte var6 = 0;
      byte var7 = 4;
      boolean var10 = false;
      int var11 = var1[0];
      int var12 = var0.length();
      if (var11 >= 0 && var11 < var12) {
         int var2 = UTF16.charAt(var0, var11);
         var11 += UTF16.getCharCount(var2);
         int var8;
         switch(var2) {
         case 85:
            var6 = 8;
            var5 = 8;
            break;
         case 117:
            var6 = 4;
            var5 = 4;
            break;
         case 120:
            var5 = 1;
            if (var11 < var12 && UTF16.charAt(var0, var11) == 123) {
               ++var11;
               var10 = true;
               var6 = 8;
            } else {
               var6 = 2;
            }
            break;
         default:
            var8 = UCharacter.digit(var2, 8);
            if (var8 >= 0) {
               var5 = 1;
               var6 = 3;
               var4 = 1;
               var7 = 3;
               var3 = var8;
            }
         }

         if (var5 != 0) {
            while(var11 < var12 && var4 < var6) {
               var2 = UTF16.charAt(var0, var11);
               var8 = UCharacter.digit(var2, var7 == 3 ? 8 : 16);
               if (var8 < 0) {
                  break;
               }

               var3 = var3 << var7 | var8;
               var11 += UTF16.getCharCount(var2);
               ++var4;
            }

            if (var4 < var5) {
               return -1;
            } else {
               if (var10) {
                  if (var2 != 125) {
                     return -1;
                  }

                  ++var11;
               }

               if (var3 >= 0 && var3 < 1114112) {
                  if (var11 < var12 && UTF16.isLeadSurrogate((char)var3)) {
                     int var13 = var11 + 1;
                     var2 = var0.charAt(var11);
                     if (var2 == 92 && var13 < var12) {
                        int[] var14 = new int[]{var13};
                        var2 = unescapeAt(var0, var14);
                        var13 = var14[0];
                     }

                     if (UTF16.isTrailSurrogate((char)var2)) {
                        var11 = var13;
                        var3 = UCharacterProperty.getRawSupplementary((char)var3, (char)var2);
                     }
                  }

                  var1[0] = var11;
                  return var3;
               } else {
                  return -1;
               }
            }
         } else {
            for(int var9 = 0; var9 < UNESCAPE_MAP.length; var9 += 2) {
               if (var2 == UNESCAPE_MAP[var9]) {
                  var1[0] = var11;
                  return UNESCAPE_MAP[var9 + 1];
               }

               if (var2 < UNESCAPE_MAP[var9]) {
                  break;
               }
            }

            if (var2 == 99 && var11 < var12) {
               var2 = UTF16.charAt(var0, var11);
               var1[0] = var11 + UTF16.getCharCount(var2);
               return 31 & var2;
            } else {
               var1[0] = var11;
               return var2;
            }
         }
      } else {
         return -1;
      }
   }

   public static StringBuffer hex(int var0, int var1, StringBuffer var2) {
      return appendNumber(var2, var0, 16, var1);
   }

   public static String hex(int var0, int var1) {
      StringBuffer var2 = new StringBuffer();
      return appendNumber(var2, var0, 16, var1).toString();
   }

   public static int skipWhitespace(String var0, int var1) {
      while(true) {
         if (var1 < var0.length()) {
            int var2 = UTF16.charAt(var0, var1);
            if (UCharacterProperty.isRuleWhiteSpace(var2)) {
               var1 += UTF16.getCharCount(var2);
               continue;
            }
         }

         return var1;
      }
   }

   private static void recursiveAppendNumber(StringBuffer var0, int var1, int var2, int var3) {
      int var4 = var1 % var2;
      if (var1 >= var2 || var3 > 1) {
         recursiveAppendNumber(var0, var1 / var2, var2, var3 - 1);
      }

      var0.append(DIGITS[var4]);
   }

   public static StringBuffer appendNumber(StringBuffer var0, int var1, int var2, int var3) throws IllegalArgumentException {
      if (var2 >= 2 && var2 <= 36) {
         int var4 = var1;
         if (var1 < 0) {
            var4 = -var1;
            var0.append("-");
         }

         recursiveAppendNumber(var0, var4, var2, var3);
         return var0;
      } else {
         throw new IllegalArgumentException("Illegal radix " + var2);
      }
   }

   public static boolean isUnprintable(int var0) {
      return var0 < 32 || var0 > 126;
   }

   public static boolean escapeUnprintable(StringBuffer var0, int var1) {
      if (isUnprintable(var1)) {
         var0.append('\\');
         if ((var1 & -65536) != 0) {
            var0.append('U');
            var0.append(DIGITS[15 & var1 >> 28]);
            var0.append(DIGITS[15 & var1 >> 24]);
            var0.append(DIGITS[15 & var1 >> 20]);
            var0.append(DIGITS[15 & var1 >> 16]);
         } else {
            var0.append('u');
         }

         var0.append(DIGITS[15 & var1 >> 12]);
         var0.append(DIGITS[15 & var1 >> 8]);
         var0.append(DIGITS[15 & var1 >> 4]);
         var0.append(DIGITS[15 & var1]);
         return true;
      } else {
         return false;
      }
   }

   public static void getChars(StringBuffer var0, int var1, int var2, char[] var3, int var4) {
      if (var1 != var2) {
         var0.getChars(var1, var2, var3, var4);
      }
   }
}
