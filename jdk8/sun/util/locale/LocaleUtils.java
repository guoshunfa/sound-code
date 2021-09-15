package sun.util.locale;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LocaleUtils {
   private LocaleUtils() {
   }

   public static boolean caseIgnoreMatch(String var0, String var1) {
      if (var0 == var1) {
         return true;
      } else {
         int var2 = var0.length();
         if (var2 != var1.length()) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               char var4 = var0.charAt(var3);
               char var5 = var1.charAt(var3);
               if (var4 != var5 && toLower(var4) != toLower(var5)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   static int caseIgnoreCompare(String var0, String var1) {
      return var0 == var1 ? 0 : toLowerString(var0).compareTo(toLowerString(var1));
   }

   static char toUpper(char var0) {
      return isLower(var0) ? (char)(var0 - 32) : var0;
   }

   static char toLower(char var0) {
      return isUpper(var0) ? (char)(var0 + 32) : var0;
   }

   public static String toLowerString(String var0) {
      int var1 = var0.length();

      int var2;
      for(var2 = 0; var2 < var1 && !isUpper(var0.charAt(var2)); ++var2) {
      }

      if (var2 == var1) {
         return var0;
      } else {
         char[] var3 = new char[var1];

         for(int var4 = 0; var4 < var1; ++var4) {
            char var5 = var0.charAt(var4);
            var3[var4] = var4 < var2 ? var5 : toLower(var5);
         }

         return new String(var3);
      }
   }

   static String toUpperString(String var0) {
      int var1 = var0.length();

      int var2;
      for(var2 = 0; var2 < var1 && !isLower(var0.charAt(var2)); ++var2) {
      }

      if (var2 == var1) {
         return var0;
      } else {
         char[] var3 = new char[var1];

         for(int var4 = 0; var4 < var1; ++var4) {
            char var5 = var0.charAt(var4);
            var3[var4] = var4 < var2 ? var5 : toUpper(var5);
         }

         return new String(var3);
      }
   }

   static String toTitleString(String var0) {
      int var1;
      if ((var1 = var0.length()) == 0) {
         return var0;
      } else {
         int var2 = 0;
         if (!isLower(var0.charAt(var2))) {
            for(var2 = 1; var2 < var1 && !isUpper(var0.charAt(var2)); ++var2) {
            }
         }

         if (var2 == var1) {
            return var0;
         } else {
            char[] var3 = new char[var1];

            for(int var4 = 0; var4 < var1; ++var4) {
               char var5 = var0.charAt(var4);
               if (var4 == 0 && var2 == 0) {
                  var3[var4] = toUpper(var5);
               } else if (var4 < var2) {
                  var3[var4] = var5;
               } else {
                  var3[var4] = toLower(var5);
               }
            }

            return new String(var3);
         }
      }
   }

   private static boolean isUpper(char var0) {
      return var0 >= 'A' && var0 <= 'Z';
   }

   private static boolean isLower(char var0) {
      return var0 >= 'a' && var0 <= 'z';
   }

   static boolean isAlpha(char var0) {
      return var0 >= 'A' && var0 <= 'Z' || var0 >= 'a' && var0 <= 'z';
   }

   static boolean isAlphaString(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (!isAlpha(var0.charAt(var2))) {
            return false;
         }
      }

      return true;
   }

   static boolean isNumeric(char var0) {
      return var0 >= '0' && var0 <= '9';
   }

   static boolean isNumericString(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (!isNumeric(var0.charAt(var2))) {
            return false;
         }
      }

      return true;
   }

   static boolean isAlphaNumeric(char var0) {
      return isAlpha(var0) || isNumeric(var0);
   }

   public static boolean isAlphaNumericString(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (!isAlphaNumeric(var0.charAt(var2))) {
            return false;
         }
      }

      return true;
   }

   static boolean isEmpty(String var0) {
      return var0 == null || var0.length() == 0;
   }

   static boolean isEmpty(Set<?> var0) {
      return var0 == null || var0.isEmpty();
   }

   static boolean isEmpty(Map<?, ?> var0) {
      return var0 == null || var0.isEmpty();
   }

   static boolean isEmpty(List<?> var0) {
      return var0 == null || var0.isEmpty();
   }
}
