package sun.util.locale.provider;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.spi.CalendarNameProvider;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.Era;

public class CalendarNameProviderImpl extends CalendarNameProvider implements AvailableLanguageTags {
   private final LocaleProviderAdapter.Type type;
   private final Set<String> langtags;
   private static int[] REST_OF_STYLES = new int[]{32769, 2, 32770, 4, 32772};

   public CalendarNameProviderImpl(LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public String getDisplayName(String var1, int var2, int var3, int var4, Locale var5) {
      return this.getDisplayNameImpl(var1, var2, var3, var4, var5, false);
   }

   public String getJavaTimeDisplayName(String var1, int var2, int var3, int var4, Locale var5) {
      return this.getDisplayNameImpl(var1, var2, var3, var4, var5, true);
   }

   public String getDisplayNameImpl(String var1, int var2, int var3, int var4, Locale var5, boolean var6) {
      String var7 = null;
      String var8 = this.getResourceKey(var1, var2, var4, var6);
      if (var8 != null) {
         LocaleResources var9 = LocaleProviderAdapter.forType(this.type).getLocaleResources(var5);
         String[] var10 = var6 ? var9.getJavaTimeNames(var8) : var9.getCalendarNames(var8);
         if (var10 != null && var10.length > 0) {
            if (var2 == 7 || var2 == 1) {
               --var3;
            }

            if (var3 < 0 || var3 > var10.length) {
               return null;
            }

            if (var3 == var10.length) {
               if (var2 == 0 && "japanese".equals(var1)) {
                  Era[] var11 = CalendarSystem.forName("japanese").getEras();
                  if (var11.length == var3) {
                     Era var12 = var11[var3 - 1];
                     return var4 == 2 ? var12.getName() : var12.getAbbreviation();
                  }
               }

               return null;
            }

            var7 = var10[var3];
            if (var7.length() == 0 && (var4 == 32769 || var4 == 32770 || var4 == 32772)) {
               var7 = this.getDisplayName(var1, var2, var3, this.getBaseStyle(var4), var5);
            }
         }
      }

      return var7;
   }

   public Map<String, Integer> getDisplayNames(String var1, int var2, int var3, Locale var4) {
      Map var5;
      if (var3 == 0) {
         var5 = this.getDisplayNamesImpl(var1, var2, 1, var4, false);
         int[] var6 = REST_OF_STYLES;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = var6[var8];
            var5.putAll(this.getDisplayNamesImpl(var1, var2, var9, var4, false));
         }
      } else {
         var5 = this.getDisplayNamesImpl(var1, var2, var3, var4, false);
      }

      return var5.isEmpty() ? null : var5;
   }

   public Map<String, Integer> getJavaTimeDisplayNames(String var1, int var2, int var3, Locale var4) {
      Map var5 = this.getDisplayNamesImpl(var1, var2, var3, var4, true);
      return var5.isEmpty() ? null : var5;
   }

   private Map<String, Integer> getDisplayNamesImpl(String var1, int var2, int var3, Locale var4, boolean var5) {
      String var6 = this.getResourceKey(var1, var2, var3, var5);
      TreeMap var7 = new TreeMap(CalendarNameProviderImpl.LengthBasedComparator.INSTANCE);
      if (var6 != null) {
         LocaleResources var8 = LocaleProviderAdapter.forType(this.type).getLocaleResources(var4);
         String[] var9 = var5 ? var8.getJavaTimeNames(var6) : var8.getCalendarNames(var6);
         if (var9 != null && !this.hasDuplicates(var9)) {
            if (var2 == 1) {
               if (var9.length > 0) {
                  var7.put(var9[0], 1);
               }
            } else {
               int var10 = var2 == 7 ? 1 : 0;

               for(int var11 = 0; var11 < var9.length; ++var11) {
                  String var12 = var9[var11];
                  if (var12.length() != 0) {
                     var7.put(var12, var10 + var11);
                  }
               }
            }
         }
      }

      return var7;
   }

   private int getBaseStyle(int var1) {
      return var1 & -32769;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      if (Locale.ROOT.equals(var1)) {
         return true;
      } else {
         String var2 = null;
         if (var1.hasExtensions()) {
            var2 = var1.getUnicodeLocaleType("ca");
            var1 = var1.stripExtensions();
         }

         if (var2 != null) {
            byte var4 = -1;
            switch(var2.hashCode()) {
            case -1581060683:
               if (var2.equals("buddhist")) {
                  var4 = 0;
               }
               break;
            case -752730191:
               if (var2.equals("japanese")) {
                  var4 = 1;
               }
               break;
            case 113094:
               if (var2.equals("roc")) {
                  var4 = 4;
               }
               break;
            case 283776265:
               if (var2.equals("gregory")) {
                  var4 = 2;
               }
               break;
            case 2093696456:
               if (var2.equals("islamic")) {
                  var4 = 3;
               }
            }

            switch(var4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
               break;
            default:
               return false;
            }
         }

         if (this.langtags.contains(var1.toLanguageTag())) {
            return true;
         } else if (this.type == LocaleProviderAdapter.Type.JRE) {
            String var3 = var1.toString().replace('_', '-');
            return this.langtags.contains(var3);
         } else {
            return false;
         }
      }
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }

   private boolean hasDuplicates(String[] var1) {
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2 - 1; ++var3) {
         String var4 = var1[var3];
         if (var4 != null) {
            for(int var5 = var3 + 1; var5 < var2; ++var5) {
               if (var4.equals(var1[var5])) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private String getResourceKey(String var1, int var2, int var3, boolean var4) {
      int var5 = this.getBaseStyle(var3);
      boolean var6 = var3 != var5;
      if ("gregory".equals(var1)) {
         var1 = null;
      }

      boolean var7 = var5 == 4;
      StringBuilder var8 = new StringBuilder();
      if (var4) {
         var8.append("java.time.");
      }

      switch(var2) {
      case 0:
         if (var1 != null) {
            var8.append(var1).append('.');
         }

         if (var7) {
            var8.append("narrow.");
         } else if (this.type == LocaleProviderAdapter.Type.JRE) {
            if (var4 && var5 == 2) {
               var8.append("long.");
            }

            if (var5 == 1) {
               var8.append("short.");
            }
         } else if (var5 == 2) {
            var8.append("long.");
         }

         var8.append("Eras");
         break;
      case 1:
         if (!var7) {
            var8.append(var1).append(".FirstYear");
         }
         break;
      case 2:
         if ("islamic".equals(var1)) {
            var8.append(var1).append('.');
         }

         if (var6) {
            var8.append("standalone.");
         }

         var8.append("Month").append(this.toStyleName(var5));
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
      default:
         break;
      case 7:
         if (var6 && var7) {
            var8.append("standalone.");
         }

         var8.append("Day").append(this.toStyleName(var5));
         break;
      case 9:
         if (var7) {
            var8.append("narrow.");
         }

         var8.append("AmPmMarkers");
      }

      return var8.length() > 0 ? var8.toString() : null;
   }

   private String toStyleName(int var1) {
      switch(var1) {
      case 1:
         return "Abbreviations";
      case 4:
         return "Narrows";
      default:
         return "Names";
      }
   }

   private static class LengthBasedComparator implements Comparator<String> {
      private static final CalendarNameProviderImpl.LengthBasedComparator INSTANCE = new CalendarNameProviderImpl.LengthBasedComparator();

      public int compare(String var1, String var2) {
         int var3 = var2.length() - var1.length();
         return var3 == 0 ? var1.compareTo(var2) : var3;
      }
   }
}
