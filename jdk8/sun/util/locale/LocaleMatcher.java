package sun.util.locale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class LocaleMatcher {
   public static List<Locale> filter(List<Locale.LanguageRange> var0, Collection<Locale> var1, Locale.FilteringMode var2) {
      if (!var0.isEmpty() && !var1.isEmpty()) {
         ArrayList var3 = new ArrayList();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Locale var5 = (Locale)var4.next();
            var3.add(var5.toLanguageTag());
         }

         List var8 = filterTags(var0, var3, var2);
         ArrayList var9 = new ArrayList(var8.size());
         Iterator var6 = var8.iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            var9.add(Locale.forLanguageTag(var7));
         }

         return var9;
      } else {
         return new ArrayList();
      }
   }

   public static List<String> filterTags(List<Locale.LanguageRange> var0, Collection<String> var1, Locale.FilteringMode var2) {
      if (!var0.isEmpty() && !var1.isEmpty()) {
         if (var2 == Locale.FilteringMode.EXTENDED_FILTERING) {
            return filterExtended(var0, var1);
         } else {
            ArrayList var3 = new ArrayList();
            Iterator var4 = var0.iterator();

            while(true) {
               while(var4.hasNext()) {
                  Locale.LanguageRange var5 = (Locale.LanguageRange)var4.next();
                  String var6 = var5.getRange();
                  if (!var6.startsWith("*-") && var6.indexOf("-*") == -1) {
                     var3.add(var5);
                  } else {
                     if (var2 == Locale.FilteringMode.AUTOSELECT_FILTERING) {
                        return filterExtended(var0, var1);
                     }

                     if (var2 == Locale.FilteringMode.MAP_EXTENDED_RANGES) {
                        if (var6.charAt(0) == '*') {
                           var6 = "*";
                        } else {
                           var6 = var6.replaceAll("-[*]", "");
                        }

                        var3.add(new Locale.LanguageRange(var6, var5.getWeight()));
                     } else if (var2 == Locale.FilteringMode.REJECT_EXTENDED_RANGES) {
                        throw new IllegalArgumentException("An extended range \"" + var6 + "\" found in REJECT_EXTENDED_RANGES mode.");
                     }
                  }
               }

               return filterBasic(var3, var1);
            }
         }
      } else {
         return new ArrayList();
      }
   }

   private static List<String> filterBasic(List<Locale.LanguageRange> var0, Collection<String> var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var0.iterator();

      label38:
      while(var3.hasNext()) {
         Locale.LanguageRange var4 = (Locale.LanguageRange)var3.next();
         String var5 = var4.getRange();
         if (var5.equals("*")) {
            return new ArrayList(var1);
         }

         Iterator var6 = var1.iterator();

         while(true) {
            String var7;
            int var8;
            do {
               do {
                  if (!var6.hasNext()) {
                     continue label38;
                  }

                  var7 = (String)var6.next();
                  var7 = var7.toLowerCase();
               } while(!var7.startsWith(var5));

               var8 = var5.length();
            } while(var7.length() != var8 && var7.charAt(var8) != '-');

            if (!var2.contains(var7)) {
               var2.add(var7);
            }
         }
      }

      return var2;
   }

   private static List<String> filterExtended(List<Locale.LanguageRange> var0, Collection<String> var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var0.iterator();

      label60:
      while(var3.hasNext()) {
         Locale.LanguageRange var4 = (Locale.LanguageRange)var3.next();
         String var5 = var4.getRange();
         if (var5.equals("*")) {
            return new ArrayList(var1);
         }

         String[] var6 = var5.split("-");
         Iterator var7 = var1.iterator();

         while(true) {
            String var8;
            String[] var9;
            do {
               if (!var7.hasNext()) {
                  continue label60;
               }

               var8 = (String)var7.next();
               var8 = var8.toLowerCase();
               var9 = var8.split("-");
            } while(!var6[0].equals(var9[0]) && !var6[0].equals("*"));

            int var10 = 1;
            int var11 = 1;

            while(var10 < var6.length && var11 < var9.length) {
               if (var6[var10].equals("*")) {
                  ++var10;
               } else if (var6[var10].equals(var9[var11])) {
                  ++var10;
                  ++var11;
               } else {
                  if (var9[var11].length() == 1 && !var9[var11].equals("*")) {
                     break;
                  }

                  ++var11;
               }
            }

            if (var6.length == var10 && !var2.contains(var8)) {
               var2.add(var8);
            }
         }
      }

      return var2;
   }

   public static Locale lookup(List<Locale.LanguageRange> var0, Collection<Locale> var1) {
      if (!var0.isEmpty() && !var1.isEmpty()) {
         ArrayList var2 = new ArrayList();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Locale var4 = (Locale)var3.next();
            var2.add(var4.toLanguageTag());
         }

         String var5 = lookupTag(var0, var2);
         return var5 == null ? null : Locale.forLanguageTag(var5);
      } else {
         return null;
      }
   }

   public static String lookupTag(List<Locale.LanguageRange> var0, Collection<String> var1) {
      if (!var0.isEmpty() && !var1.isEmpty()) {
         Iterator var2 = var0.iterator();

         while(true) {
            String var4;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               Locale.LanguageRange var3 = (Locale.LanguageRange)var2.next();
               var4 = var3.getRange();
            } while(var4.equals("*"));

            String var5 = var4.replaceAll("\\x2A", "\\\\p{Alnum}*");

            while(var5.length() > 0) {
               Iterator var6 = var1.iterator();

               while(var6.hasNext()) {
                  String var7 = (String)var6.next();
                  var7 = var7.toLowerCase();
                  if (var7.matches(var5)) {
                     return var7;
                  }
               }

               int var8 = var5.lastIndexOf(45);
               if (var8 >= 0) {
                  var5 = var5.substring(0, var8);
                  if (var5.lastIndexOf(45) == var5.length() - 2) {
                     var5 = var5.substring(0, var5.length() - 2);
                  }
               } else {
                  var5 = "";
               }
            }
         }
      } else {
         return null;
      }
   }

   public static List<Locale.LanguageRange> parse(String var0) {
      var0 = var0.replaceAll(" ", "").toLowerCase();
      if (var0.startsWith("accept-language:")) {
         var0 = var0.substring(16);
      }

      String[] var1 = var0.split(",");
      ArrayList var2 = new ArrayList(var1.length);
      ArrayList var3 = new ArrayList();
      int var4 = 0;
      String[] var5 = var1;
      int var6 = var1.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         int var9;
         String var10;
         double var11;
         if ((var9 = var8.indexOf(";q=")) == -1) {
            var10 = var8;
            var11 = 1.0D;
         } else {
            var10 = var8.substring(0, var9);
            var9 += 3;

            try {
               var11 = Double.parseDouble(var8.substring(var9));
            } catch (Exception var20) {
               throw new IllegalArgumentException("weight=\"" + var8.substring(var9) + "\" for language range \"" + var10 + "\"");
            }

            if (var11 < 0.0D || var11 > 1.0D) {
               throw new IllegalArgumentException("weight=" + var11 + " for language range \"" + var10 + "\". It must be between " + 0.0D + " and " + 1.0D + ".");
            }
         }

         if (!var3.contains(var10)) {
            Locale.LanguageRange var13 = new Locale.LanguageRange(var10, var11);
            var9 = var4;

            for(int var14 = 0; var14 < var4; ++var14) {
               if (((Locale.LanguageRange)var2.get(var14)).getWeight() < var11) {
                  var9 = var14;
                  break;
               }
            }

            var2.add(var9, var13);
            ++var4;
            var3.add(var10);
            String var21;
            if ((var21 = getEquivalentForRegionAndVariant(var10)) != null && !var3.contains(var21)) {
               var2.add(var9 + 1, new Locale.LanguageRange(var21, var11));
               ++var4;
               var3.add(var21);
            }

            String[] var15;
            if ((var15 = getEquivalentsForLanguage(var10)) != null) {
               String[] var16 = var15;
               int var17 = var15.length;

               for(int var18 = 0; var18 < var17; ++var18) {
                  String var19 = var16[var18];
                  if (!var3.contains(var19)) {
                     var2.add(var9 + 1, new Locale.LanguageRange(var19, var11));
                     ++var4;
                     var3.add(var19);
                  }

                  var21 = getEquivalentForRegionAndVariant(var19);
                  if (var21 != null && !var3.contains(var21)) {
                     var2.add(var9 + 1, new Locale.LanguageRange(var21, var11));
                     ++var4;
                     var3.add(var21);
                  }
               }
            }
         }
      }

      return var2;
   }

   private static String[] getEquivalentsForLanguage(String var0) {
      int var2;
      for(String var1 = var0; var1.length() > 0; var1 = var1.substring(0, var2)) {
         if (LocaleEquivalentMaps.singleEquivMap.containsKey(var1)) {
            String var5 = (String)LocaleEquivalentMaps.singleEquivMap.get(var1);
            return new String[]{var0.replaceFirst(var1, var5)};
         }

         if (LocaleEquivalentMaps.multiEquivsMap.containsKey(var1)) {
            String[] var4 = (String[])LocaleEquivalentMaps.multiEquivsMap.get(var1);

            for(int var3 = 0; var3 < var4.length; ++var3) {
               var4[var3] = var0.replaceFirst(var1, var4[var3]);
            }

            return var4;
         }

         var2 = var1.lastIndexOf(45);
         if (var2 == -1) {
            break;
         }
      }

      return null;
   }

   private static String getEquivalentForRegionAndVariant(String var0) {
      int var1 = getExtentionKeyIndex(var0);
      Iterator var2 = LocaleEquivalentMaps.regionVariantEquivMap.keySet().iterator();

      String var3;
      int var5;
      do {
         int var4;
         do {
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (String)var2.next();
            } while((var4 = var0.indexOf(var3)) == -1);
         } while(var1 != Integer.MIN_VALUE && var4 > var1);

         var5 = var4 + var3.length();
      } while(var0.length() != var5 && var0.charAt(var5) != '-');

      return var0.replaceFirst(var3, (String)LocaleEquivalentMaps.regionVariantEquivMap.get(var3));
   }

   private static int getExtentionKeyIndex(String var0) {
      char[] var1 = var0.toCharArray();
      int var2 = Integer.MIN_VALUE;

      for(int var3 = 1; var3 < var1.length; ++var3) {
         if (var1[var3] == '-') {
            if (var3 - var2 == 2) {
               return var2;
            }

            var2 = var3;
         }
      }

      return Integer.MIN_VALUE;
   }

   public static List<Locale.LanguageRange> mapEquivalents(List<Locale.LanguageRange> var0, Map<String, List<String>> var1) {
      if (var0.isEmpty()) {
         return new ArrayList();
      } else if (var1 != null && !var1.isEmpty()) {
         HashMap var2 = new HashMap();
         Iterator var3 = var1.keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var2.put(var4.toLowerCase(), var4);
         }

         ArrayList var13 = new ArrayList();
         Iterator var14 = var0.iterator();

         while(var14.hasNext()) {
            Locale.LanguageRange var5 = (Locale.LanguageRange)var14.next();
            String var6 = var5.getRange();
            String var7 = var6;

            boolean var8;
            int var9;
            label46:
            for(var8 = false; var7.length() > 0; var7 = var7.substring(0, var9)) {
               if (var2.containsKey(var7)) {
                  var8 = true;
                  List var15 = (List)var1.get(var2.get(var7));
                  if (var15 == null) {
                     break;
                  }

                  int var10 = var7.length();
                  Iterator var11 = var15.iterator();

                  while(true) {
                     if (!var11.hasNext()) {
                        break label46;
                     }

                     String var12 = (String)var11.next();
                     var13.add(new Locale.LanguageRange(var12.toLowerCase() + var6.substring(var10), var5.getWeight()));
                  }
               }

               var9 = var7.lastIndexOf(45);
               if (var9 == -1) {
                  break;
               }
            }

            if (!var8) {
               var13.add(var5);
            }
         }

         return var13;
      } else {
         return new ArrayList(var0);
      }
   }

   private LocaleMatcher() {
   }
}
