package sun.invoke.util;

public class BytecodeName {
   static char ESCAPE_C = '\\';
   static char NULL_ESCAPE_C = '=';
   static String NULL_ESCAPE;
   static final String DANGEROUS_CHARS = "\\/.;:$[]<>";
   static final String REPLACEMENT_CHARS = "-|,?!%{}^_";
   static final int DANGEROUS_CHAR_FIRST_INDEX = 1;
   static char[] DANGEROUS_CHARS_A;
   static char[] REPLACEMENT_CHARS_A;
   static final Character[] DANGEROUS_CHARS_CA;
   static final long[] SPECIAL_BITMAP;

   private BytecodeName() {
   }

   public static String toBytecodeName(String var0) {
      String var1 = mangle(var0);

      assert var1 == var0 || looksMangled(var1) : var1;

      assert var0.equals(toSourceName(var1)) : var0;

      return var1;
   }

   public static String toSourceName(String var0) {
      checkSafeBytecodeName(var0);
      String var1 = var0;
      if (looksMangled(var0)) {
         var1 = demangle(var0);

         assert var0.equals(mangle(var1)) : var0 + " => " + var1 + " => " + mangle(var1);
      }

      return var1;
   }

   public static Object[] parseBytecodeName(String var0) {
      int var1 = var0.length();
      Object[] var2 = null;

      for(int var3 = 0; var3 <= 1; ++var3) {
         int var4 = 0;
         int var5 = 0;

         for(int var6 = 0; var6 <= var1; ++var6) {
            int var7 = -1;
            if (var6 < var1) {
               var7 = "\\/.;:$[]<>".indexOf(var0.charAt(var6));
               if (var7 < 1) {
                  continue;
               }
            }

            if (var5 < var6) {
               if (var3 != 0) {
                  var2[var4] = toSourceName(var0.substring(var5, var6));
               }

               ++var4;
               var5 = var6 + 1;
            }

            if (var7 >= 1) {
               if (var3 != 0) {
                  var2[var4] = DANGEROUS_CHARS_CA[var7];
               }

               ++var4;
               var5 = var6 + 1;
            }
         }

         if (var3 != 0) {
            break;
         }

         var2 = new Object[var4];
         if (var4 <= 1 && var5 == 0) {
            if (var4 != 0) {
               var2[0] = toSourceName(var0);
            }
            break;
         }
      }

      return var2;
   }

   public static String unparseBytecodeName(Object[] var0) {
      Object[] var1 = var0;

      for(int var2 = 0; var2 < var0.length; ++var2) {
         Object var3 = var0[var2];
         if (var3 instanceof String) {
            String var4 = toBytecodeName((String)var3);
            if (var2 == 0 && var0.length == 1) {
               return var4;
            }

            if (var4 != var3) {
               if (var0 == var1) {
                  var0 = (Object[])var0.clone();
               }

               var0[var2] = var4;
            }
         }
      }

      return appendAll(var0);
   }

   private static String appendAll(Object[] var0) {
      if (var0.length <= 1) {
         return var0.length == 1 ? String.valueOf(var0[0]) : "";
      } else {
         int var1 = 0;
         Object[] var2 = var0;
         int var3 = var0.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            Object var5 = var2[var4];
            if (var5 instanceof String) {
               var1 += String.valueOf(var5).length();
            } else {
               ++var1;
            }
         }

         StringBuilder var7 = new StringBuilder(var1);
         Object[] var8 = var0;
         var4 = var0.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            Object var6 = var8[var9];
            var7.append(var6);
         }

         return var7.toString();
      }
   }

   public static String toDisplayName(String var0) {
      Object[] var1 = parseBytecodeName(var0);

      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2] instanceof String) {
            String var3 = (String)var1[var2];
            if (!isJavaIdent(var3) || var3.indexOf(36) >= 0) {
               var1[var2] = quoteDisplay(var3);
            }
         }
      }

      return appendAll(var1);
   }

   private static boolean isJavaIdent(String var0) {
      int var1 = var0.length();
      if (var1 == 0) {
         return false;
      } else if (!Character.isJavaIdentifierStart(var0.charAt(0))) {
         return false;
      } else {
         for(int var2 = 1; var2 < var1; ++var2) {
            if (!Character.isJavaIdentifierPart(var0.charAt(var2))) {
               return false;
            }
         }

         return true;
      }
   }

   private static String quoteDisplay(String var0) {
      return "'" + var0.replaceAll("['\\\\]", "\\\\$0") + "'";
   }

   private static void checkSafeBytecodeName(String var0) throws IllegalArgumentException {
      if (!isSafeBytecodeName(var0)) {
         throw new IllegalArgumentException(var0);
      }
   }

   public static boolean isSafeBytecodeName(String var0) {
      if (var0.length() == 0) {
         return false;
      } else {
         char[] var1 = DANGEROUS_CHARS_A;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            char var4 = var1[var3];
            if (var4 != ESCAPE_C && var0.indexOf(var4) >= 0) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isSafeBytecodeChar(char var0) {
      return "\\/.;:$[]<>".indexOf(var0) < 1;
   }

   private static boolean looksMangled(String var0) {
      return var0.charAt(0) == ESCAPE_C;
   }

   private static String mangle(String var0) {
      if (var0.length() == 0) {
         return NULL_ESCAPE;
      } else {
         StringBuilder var1 = null;
         int var2 = 0;

         for(int var3 = var0.length(); var2 < var3; ++var2) {
            char var4 = var0.charAt(var2);
            boolean var5 = false;
            if (var4 == ESCAPE_C) {
               if (var2 + 1 < var3) {
                  char var6 = var0.charAt(var2 + 1);
                  if (var2 == 0 && var6 == NULL_ESCAPE_C || var6 != originalOfReplacement(var6)) {
                     var5 = true;
                  }
               }
            } else {
               var5 = isDangerous(var4);
            }

            if (!var5) {
               if (var1 != null) {
                  var1.append(var4);
               }
            } else {
               if (var1 == null) {
                  var1 = new StringBuilder(var0.length() + 10);
                  if (var0.charAt(0) != ESCAPE_C && var2 > 0) {
                     var1.append(NULL_ESCAPE);
                  }

                  var1.append(var0.substring(0, var2));
               }

               var1.append(ESCAPE_C);
               var1.append(replacementOf(var4));
            }
         }

         if (var1 != null) {
            return var1.toString();
         } else {
            return var0;
         }
      }
   }

   private static String demangle(String var0) {
      StringBuilder var1 = null;
      byte var2 = 0;
      if (var0.startsWith(NULL_ESCAPE)) {
         var2 = 2;
      }

      int var3 = var2;

      for(int var4 = var0.length(); var3 < var4; ++var3) {
         char var5 = var0.charAt(var3);
         if (var5 == ESCAPE_C && var3 + 1 < var4) {
            char var6 = var0.charAt(var3 + 1);
            char var7 = originalOfReplacement(var6);
            if (var7 != var6) {
               if (var1 == null) {
                  var1 = new StringBuilder(var0.length());
                  var1.append(var0.substring(var2, var3));
               }

               ++var3;
               var5 = var7;
            }
         }

         if (var1 != null) {
            var1.append(var5);
         }
      }

      if (var1 != null) {
         return var1.toString();
      } else {
         return var0.substring(var2);
      }
   }

   static boolean isSpecial(char var0) {
      if (var0 >>> 6 < SPECIAL_BITMAP.length) {
         return (SPECIAL_BITMAP[var0 >>> 6] >> var0 & 1L) != 0L;
      } else {
         return false;
      }
   }

   static char replacementOf(char var0) {
      if (!isSpecial(var0)) {
         return var0;
      } else {
         int var1 = "\\/.;:$[]<>".indexOf(var0);
         return var1 < 0 ? var0 : "-|,?!%{}^_".charAt(var1);
      }
   }

   static char originalOfReplacement(char var0) {
      if (!isSpecial(var0)) {
         return var0;
      } else {
         int var1 = "-|,?!%{}^_".indexOf(var0);
         return var1 < 0 ? var0 : "\\/.;:$[]<>".charAt(var1);
      }
   }

   static boolean isDangerous(char var0) {
      if (!isSpecial(var0)) {
         return false;
      } else {
         return "\\/.;:$[]<>".indexOf(var0) >= 1;
      }
   }

   static int indexOfDangerousChar(String var0, int var1) {
      int var2 = var1;

      for(int var3 = var0.length(); var2 < var3; ++var2) {
         if (isDangerous(var0.charAt(var2))) {
            return var2;
         }
      }

      return -1;
   }

   static int lastIndexOfDangerousChar(String var0, int var1) {
      for(int var2 = Math.min(var1, var0.length() - 1); var2 >= 0; --var2) {
         if (isDangerous(var0.charAt(var2))) {
            return var2;
         }
      }

      return -1;
   }

   static {
      NULL_ESCAPE = ESCAPE_C + "" + NULL_ESCAPE_C;
      DANGEROUS_CHARS_A = "\\/.;:$[]<>".toCharArray();
      REPLACEMENT_CHARS_A = "-|,?!%{}^_".toCharArray();
      Character[] var0 = new Character["\\/.;:$[]<>".length()];

      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1] = "\\/.;:$[]<>".charAt(var1);
      }

      DANGEROUS_CHARS_CA = var0;
      SPECIAL_BITMAP = new long[2];
      String var5 = "\\/.;:$[]<>-|,?!%{}^_";
      char[] var6 = var5.toCharArray();
      int var2 = var6.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         char var4 = var6[var3];
         long[] var10000 = SPECIAL_BITMAP;
         var10000[var4 >>> 6] |= 1L << var4;
      }

   }
}
