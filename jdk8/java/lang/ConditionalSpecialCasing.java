package java.lang;

import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import sun.text.Normalizer;

final class ConditionalSpecialCasing {
   static final int FINAL_CASED = 1;
   static final int AFTER_SOFT_DOTTED = 2;
   static final int MORE_ABOVE = 3;
   static final int AFTER_I = 4;
   static final int NOT_BEFORE_DOT = 5;
   static final int COMBINING_CLASS_ABOVE = 230;
   static ConditionalSpecialCasing.Entry[] entry = new ConditionalSpecialCasing.Entry[]{new ConditionalSpecialCasing.Entry(931, new char[]{'ς'}, new char[]{'Σ'}, (String)null, 1), new ConditionalSpecialCasing.Entry(304, new char[]{'i', '̇'}, new char[]{'İ'}, (String)null, 0), new ConditionalSpecialCasing.Entry(775, new char[]{'̇'}, new char[0], "lt", 2), new ConditionalSpecialCasing.Entry(73, new char[]{'i', '̇'}, new char[]{'I'}, "lt", 3), new ConditionalSpecialCasing.Entry(74, new char[]{'j', '̇'}, new char[]{'J'}, "lt", 3), new ConditionalSpecialCasing.Entry(302, new char[]{'į', '̇'}, new char[]{'Į'}, "lt", 3), new ConditionalSpecialCasing.Entry(204, new char[]{'i', '̇', '̀'}, new char[]{'Ì'}, "lt", 0), new ConditionalSpecialCasing.Entry(205, new char[]{'i', '̇', '́'}, new char[]{'Í'}, "lt", 0), new ConditionalSpecialCasing.Entry(296, new char[]{'i', '̇', '̃'}, new char[]{'Ĩ'}, "lt", 0), new ConditionalSpecialCasing.Entry(304, new char[]{'i'}, new char[]{'İ'}, "tr", 0), new ConditionalSpecialCasing.Entry(304, new char[]{'i'}, new char[]{'İ'}, "az", 0), new ConditionalSpecialCasing.Entry(775, new char[0], new char[]{'̇'}, "tr", 4), new ConditionalSpecialCasing.Entry(775, new char[0], new char[]{'̇'}, "az", 4), new ConditionalSpecialCasing.Entry(73, new char[]{'ı'}, new char[]{'I'}, "tr", 5), new ConditionalSpecialCasing.Entry(73, new char[]{'ı'}, new char[]{'I'}, "az", 5), new ConditionalSpecialCasing.Entry(105, new char[]{'i'}, new char[]{'İ'}, "tr", 0), new ConditionalSpecialCasing.Entry(105, new char[]{'i'}, new char[]{'İ'}, "az", 0)};
   static Hashtable<Integer, HashSet<ConditionalSpecialCasing.Entry>> entryTable = new Hashtable();

   static int toLowerCaseEx(String var0, int var1, Locale var2) {
      char[] var3 = lookUpTable(var0, var1, var2, true);
      if (var3 != null) {
         return var3.length == 1 ? var3[0] : -1;
      } else {
         return Character.toLowerCase(var0.codePointAt(var1));
      }
   }

   static int toUpperCaseEx(String var0, int var1, Locale var2) {
      char[] var3 = lookUpTable(var0, var1, var2, false);
      if (var3 != null) {
         return var3.length == 1 ? var3[0] : -1;
      } else {
         return Character.toUpperCaseEx(var0.codePointAt(var1));
      }
   }

   static char[] toLowerCaseCharArray(String var0, int var1, Locale var2) {
      return lookUpTable(var0, var1, var2, true);
   }

   static char[] toUpperCaseCharArray(String var0, int var1, Locale var2) {
      char[] var3 = lookUpTable(var0, var1, var2, false);
      return var3 != null ? var3 : Character.toUpperCaseCharArray(var0.codePointAt(var1));
   }

   private static char[] lookUpTable(String var0, int var1, Locale var2, boolean var3) {
      HashSet var4 = (HashSet)entryTable.get(new Integer(var0.codePointAt(var1)));
      char[] var5 = null;
      if (var4 != null) {
         Iterator var6 = var4.iterator();
         String var7 = var2.getLanguage();

         while(true) {
            ConditionalSpecialCasing.Entry var8;
            String var9;
            do {
               if (!var6.hasNext()) {
                  return var5;
               }

               var8 = (ConditionalSpecialCasing.Entry)var6.next();
               var9 = var8.getLanguage();
            } while(var9 != null && !var9.equals(var7));

            if (isConditionMet(var0, var1, var2, var8.getCondition())) {
               var5 = var3 ? var8.getLowerCase() : var8.getUpperCase();
               if (var9 != null) {
                  break;
               }
            }
         }
      }

      return var5;
   }

   private static boolean isConditionMet(String var0, int var1, Locale var2, int var3) {
      switch(var3) {
      case 1:
         return isFinalCased(var0, var1, var2);
      case 2:
         return isAfterSoftDotted(var0, var1);
      case 3:
         return isMoreAbove(var0, var1);
      case 4:
         return isAfterI(var0, var1);
      case 5:
         return !isBeforeDot(var0, var1);
      default:
         return true;
      }
   }

   private static boolean isFinalCased(String var0, int var1, Locale var2) {
      BreakIterator var3 = BreakIterator.getWordInstance(var2);
      var3.setText(var0);

      int var4;
      for(int var5 = var1; var5 >= 0 && !var3.isBoundary(var5); var5 -= Character.charCount(var4)) {
         var4 = var0.codePointBefore(var5);
         if (isCased(var4)) {
            int var6 = var0.length();

            for(var5 = var1 + Character.charCount(var0.codePointAt(var1)); var5 < var6 && !var3.isBoundary(var5); var5 += Character.charCount(var4)) {
               var4 = var0.codePointAt(var5);
               if (isCased(var4)) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   private static boolean isAfterI(String var0, int var1) {
      int var2;
      for(int var4 = var1; var4 > 0; var4 -= Character.charCount(var2)) {
         var2 = var0.codePointBefore(var4);
         if (var2 == 73) {
            return true;
         }

         int var3 = Normalizer.getCombiningClass(var2);
         if (var3 == 0 || var3 == 230) {
            return false;
         }
      }

      return false;
   }

   private static boolean isAfterSoftDotted(String var0, int var1) {
      int var2;
      for(int var4 = var1; var4 > 0; var4 -= Character.charCount(var2)) {
         var2 = var0.codePointBefore(var4);
         if (isSoftDotted(var2)) {
            return true;
         }

         int var3 = Normalizer.getCombiningClass(var2);
         if (var3 == 0 || var3 == 230) {
            return false;
         }
      }

      return false;
   }

   private static boolean isMoreAbove(String var0, int var1) {
      int var4 = var0.length();

      int var2;
      for(int var5 = var1 + Character.charCount(var0.codePointAt(var1)); var5 < var4; var5 += Character.charCount(var2)) {
         var2 = var0.codePointAt(var5);
         int var3 = Normalizer.getCombiningClass(var2);
         if (var3 == 230) {
            return true;
         }

         if (var3 == 0) {
            return false;
         }
      }

      return false;
   }

   private static boolean isBeforeDot(String var0, int var1) {
      int var4 = var0.length();

      int var2;
      for(int var5 = var1 + Character.charCount(var0.codePointAt(var1)); var5 < var4; var5 += Character.charCount(var2)) {
         var2 = var0.codePointAt(var5);
         if (var2 == 775) {
            return true;
         }

         int var3 = Normalizer.getCombiningClass(var2);
         if (var3 == 0 || var3 == 230) {
            return false;
         }
      }

      return false;
   }

   private static boolean isCased(int var0) {
      int var1 = Character.getType(var0);
      if (var1 != 2 && var1 != 1 && var1 != 3) {
         if (var0 >= 688 && var0 <= 696) {
            return true;
         } else if (var0 >= 704 && var0 <= 705) {
            return true;
         } else if (var0 >= 736 && var0 <= 740) {
            return true;
         } else if (var0 == 837) {
            return true;
         } else if (var0 == 890) {
            return true;
         } else if (var0 >= 7468 && var0 <= 7521) {
            return true;
         } else if (var0 >= 8544 && var0 <= 8575) {
            return true;
         } else {
            return var0 >= 9398 && var0 <= 9449;
         }
      } else {
         return true;
      }
   }

   private static boolean isSoftDotted(int var0) {
      switch(var0) {
      case 105:
      case 106:
      case 303:
      case 616:
      case 1110:
      case 1112:
      case 7522:
      case 7725:
      case 7883:
      case 8305:
         return true;
      default:
         return false;
      }
   }

   static {
      for(int var0 = 0; var0 < entry.length; ++var0) {
         ConditionalSpecialCasing.Entry var1 = entry[var0];
         Integer var2 = new Integer(var1.getCodePoint());
         HashSet var3 = (HashSet)entryTable.get(var2);
         if (var3 == null) {
            var3 = new HashSet();
         }

         var3.add(var1);
         entryTable.put(var2, var3);
      }

   }

   static class Entry {
      int ch;
      char[] lower;
      char[] upper;
      String lang;
      int condition;

      Entry(int var1, char[] var2, char[] var3, String var4, int var5) {
         this.ch = var1;
         this.lower = var2;
         this.upper = var3;
         this.lang = var4;
         this.condition = var5;
      }

      int getCodePoint() {
         return this.ch;
      }

      char[] getLowerCase() {
         return this.lower;
      }

      char[] getUpperCase() {
         return this.upper;
      }

      String getLanguage() {
         return this.lang;
      }

      int getCondition() {
         return this.condition;
      }
   }
}
