package sun.net.idn;

import java.text.ParseException;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UTF16;

public final class Punycode {
   private static final int BASE = 36;
   private static final int TMIN = 1;
   private static final int TMAX = 26;
   private static final int SKEW = 38;
   private static final int DAMP = 700;
   private static final int INITIAL_BIAS = 72;
   private static final int INITIAL_N = 128;
   private static final int HYPHEN = 45;
   private static final int DELIMITER = 45;
   private static final int ZERO = 48;
   private static final int NINE = 57;
   private static final int SMALL_A = 97;
   private static final int SMALL_Z = 122;
   private static final int CAPITAL_A = 65;
   private static final int CAPITAL_Z = 90;
   private static final int MAX_CP_COUNT = 256;
   private static final int UINT_MAGIC = Integer.MIN_VALUE;
   private static final long ULONG_MAGIC = Long.MIN_VALUE;
   static final int[] basicToDigit = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

   private static int adaptBias(int var0, int var1, boolean var2) {
      if (var2) {
         var0 /= 700;
      } else {
         var0 /= 2;
      }

      var0 += var0 / var1;

      int var3;
      for(var3 = 0; var0 > 455; var3 += 36) {
         var0 /= 35;
      }

      return var3 + 36 * var0 / (var0 + 38);
   }

   private static char asciiCaseMap(char var0, boolean var1) {
      if (var1) {
         if ('a' <= var0 && var0 <= 'z') {
            var0 = (char)(var0 - 32);
         }
      } else if ('A' <= var0 && var0 <= 'Z') {
         var0 = (char)(var0 + 32);
      }

      return var0;
   }

   private static char digitToBasic(int var0, boolean var1) {
      if (var0 < 26) {
         return var1 ? (char)(65 + var0) : (char)(97 + var0);
      } else {
         return (char)(22 + var0);
      }
   }

   public static StringBuffer encode(StringBuffer var0, boolean[] var1) throws ParseException {
      int[] var2 = new int[256];
      int var17 = var0.length();
      short var18 = 256;
      char[] var19 = new char[var18];
      StringBuffer var20 = new StringBuffer();
      int var7 = 0;
      int var14 = 0;

      int var3;
      int var9;
      for(var9 = 0; var9 < var17; ++var9) {
         if (var14 == 256) {
            throw new IndexOutOfBoundsException();
         }

         char var15 = var0.charAt(var9);
         if (isBasic(var15)) {
            if (var7 < var18) {
               var2[var14++] = 0;
               var19[var7] = var1 != null ? asciiCaseMap(var15, var1[var9]) : var15;
            }

            ++var7;
         } else {
            var3 = (var1 != null && var1[var9] ? 1 : 0) << 31;
            if (!UTF16.isSurrogate(var15)) {
               var3 |= var15;
            } else {
               char var16;
               if (!UTF16.isLeadSurrogate(var15) || var9 + 1 >= var17 || !UTF16.isTrailSurrogate(var16 = var0.charAt(var9 + 1))) {
                  throw new ParseException("Illegal char found", -1);
               }

               ++var9;
               var3 |= UCharacter.getCodePoint(var15, var16);
            }

            var2[var14++] = var3;
         }
      }

      int var6 = var7;
      if (var7 > 0) {
         if (var7 < var18) {
            var19[var7] = '-';
         }

         ++var7;
      }

      var3 = 128;
      int var4 = 0;
      int var8 = 72;

      for(int var5 = var7; var5 < var14; ++var3) {
         int var10 = Integer.MAX_VALUE;

         int var11;
         for(var9 = 0; var9 < var14; ++var9) {
            var11 = var2[var9] & Integer.MAX_VALUE;
            if (var3 <= var11 && var11 < var10) {
               var10 = var11;
            }
         }

         if (var10 - var3 > (2147483391 - var4) / (var5 + 1)) {
            throw new RuntimeException("Internal program error");
         }

         var4 += (var10 - var3) * (var5 + 1);
         var3 = var10;

         for(var9 = 0; var9 < var14; ++var9) {
            var11 = var2[var9] & Integer.MAX_VALUE;
            if (var11 < var3) {
               ++var4;
            } else if (var11 == var3) {
               var11 = var4;
               int var12 = 36;

               while(true) {
                  int var13 = var12 - var8;
                  if (var13 < 1) {
                     var13 = 1;
                  } else if (var12 >= var8 + 26) {
                     var13 = 26;
                  }

                  if (var11 < var13) {
                     if (var7 < var18) {
                        var19[var7++] = digitToBasic(var11, var2[var9] < 0);
                     }

                     var8 = adaptBias(var4, var5 + 1, var5 == var6);
                     var4 = 0;
                     ++var5;
                     break;
                  }

                  if (var7 < var18) {
                     var19[var7++] = digitToBasic(var13 + (var11 - var13) % (36 - var13), false);
                  }

                  var11 = (var11 - var13) / (36 - var13);
                  var12 += 36;
               }
            }
         }

         ++var4;
      }

      return var20.append((char[])var19, 0, var7);
   }

   private static boolean isBasic(int var0) {
      return var0 < 128;
   }

   private static boolean isBasicUpperCase(int var0) {
      return 65 <= var0 && var0 <= 90;
   }

   private static boolean isSurrogate(int var0) {
      return (var0 & -2048) == 55296;
   }

   public static StringBuffer decode(StringBuffer var0, boolean[] var1) throws ParseException {
      int var2 = var0.length();
      StringBuffer var3 = new StringBuffer();
      short var20 = 256;
      char[] var21 = new char[var20];
      int var9 = var2;

      while(var9 > 0) {
         --var9;
         if (var0.charAt(var9) == '-') {
            break;
         }
      }

      int var16 = var9;
      int var8 = var9;
      int var5 = var9;

      while(var9 > 0) {
         --var9;
         char var19 = var0.charAt(var9);
         if (!isBasic(var19)) {
            throw new ParseException("Illegal char found", -1);
         }

         if (var9 < var20) {
            var21[var9] = var19;
            if (var1 != null) {
               var1[var9] = isBasicUpperCase(var19);
            }
         }
      }

      int var4 = 128;
      int var6 = 0;
      int var7 = 72;
      int var17 = 1000000000;
      int var10 = var8 > 0 ? var8 + 1 : 0;

      label106:
      while(var10 < var2) {
         int var11 = var6;
         int var12 = 1;

         for(int var13 = 36; var10 < var2; var13 += 36) {
            int var14 = basicToDigit[(byte)var0.charAt(var10++)];
            if (var14 < 0) {
               throw new ParseException("Invalid char found", -1);
            }

            if (var14 > (Integer.MAX_VALUE - var6) / var12) {
               throw new ParseException("Illegal char found", -1);
            }

            var6 += var14 * var12;
            int var15 = var13 - var7;
            if (var15 < 1) {
               var15 = 1;
            } else if (var13 >= var7 + 26) {
               var15 = 26;
            }

            if (var14 < var15) {
               ++var16;
               var7 = adaptBias(var6 - var11, var16, var11 == 0);
               if (var6 / var16 > Integer.MAX_VALUE - var4) {
                  throw new ParseException("Illegal char found", -1);
               }

               var4 += var6 / var16;
               var6 %= var16;
               if (var4 <= 1114111 && !isSurrogate(var4)) {
                  int var18 = UTF16.getCharCount(var4);
                  if (var5 + var18 < var20) {
                     int var22;
                     if (var6 <= var17) {
                        var22 = var6;
                        if (var18 > 1) {
                           var17 = var6;
                        } else {
                           ++var17;
                        }
                     } else {
                        var22 = UTF16.moveCodePointOffset(var21, 0, var5, var17, var6 - var17);
                     }

                     if (var22 < var5) {
                        System.arraycopy(var21, var22, var21, var22 + var18, var5 - var22);
                        if (var1 != null) {
                           System.arraycopy(var1, var22, var1, var22 + var18, var5 - var22);
                        }
                     }

                     if (var18 == 1) {
                        var21[var22] = (char)var4;
                     } else {
                        var21[var22] = UTF16.getLeadSurrogate(var4);
                        var21[var22 + 1] = UTF16.getTrailSurrogate(var4);
                     }

                     if (var1 != null) {
                        var1[var22] = isBasicUpperCase(var0.charAt(var10 - 1));
                        if (var18 == 2) {
                           var1[var22 + 1] = false;
                        }
                     }
                  }

                  var5 += var18;
                  ++var6;
                  continue label106;
               }

               throw new ParseException("Illegal char found", -1);
            }

            if (var12 > Integer.MAX_VALUE / (36 - var15)) {
               throw new ParseException("Illegal char found", -1);
            }

            var12 *= 36 - var15;
         }

         throw new ParseException("Illegal char found", -1);
      }

      var3.append((char[])var21, 0, var5);
      return var3;
   }
}
