package sun.misc;

import java.util.Comparator;

public class ASCIICaseInsensitiveComparator implements Comparator<String> {
   public static final Comparator<String> CASE_INSENSITIVE_ORDER = new ASCIICaseInsensitiveComparator();

   public int compare(String var1, String var2) {
      int var3 = var1.length();
      int var4 = var2.length();
      int var5 = var3 < var4 ? var3 : var4;

      for(int var6 = 0; var6 < var5; ++var6) {
         char var7 = var1.charAt(var6);
         char var8 = var2.charAt(var6);

         assert var7 <= 127 && var8 <= 127;

         if (var7 != var8) {
            var7 = (char)toLower(var7);
            var8 = (char)toLower(var8);
            if (var7 != var8) {
               return var7 - var8;
            }
         }
      }

      return var3 - var4;
   }

   public static int lowerCaseHashCode(String var0) {
      int var1 = 0;
      int var2 = var0.length();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1 = 31 * var1 + toLower(var0.charAt(var3));
      }

      return var1;
   }

   static boolean isLower(int var0) {
      return (var0 - 97 | 122 - var0) >= 0;
   }

   static boolean isUpper(int var0) {
      return (var0 - 65 | 90 - var0) >= 0;
   }

   static int toLower(int var0) {
      return isUpper(var0) ? var0 + 32 : var0;
   }

   static int toUpper(int var0) {
      return isLower(var0) ? var0 - 32 : var0;
   }
}
