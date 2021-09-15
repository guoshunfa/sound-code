package java.time.format;

import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

class DateTimeTextProvider {
   private static final ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> CACHE = new ConcurrentHashMap(16, 0.75F, 2);
   private static final Comparator<Map.Entry<String, Long>> COMPARATOR = new Comparator<Map.Entry<String, Long>>() {
      public int compare(Map.Entry<String, Long> var1, Map.Entry<String, Long> var2) {
         return ((String)var2.getKey()).length() - ((String)var1.getKey()).length();
      }
   };

   static DateTimeTextProvider getInstance() {
      return new DateTimeTextProvider();
   }

   public String getText(TemporalField var1, long var2, TextStyle var4, Locale var5) {
      Object var6 = this.findStore(var1, var5);
      return var6 instanceof DateTimeTextProvider.LocaleStore ? ((DateTimeTextProvider.LocaleStore)var6).getText(var2, var4) : null;
   }

   public String getText(Chronology var1, TemporalField var2, long var3, TextStyle var5, Locale var6) {
      if (var1 != IsoChronology.INSTANCE && var2 instanceof ChronoField) {
         byte var7;
         int var8;
         if (var2 == ChronoField.ERA) {
            var7 = 0;
            if (var1 == JapaneseChronology.INSTANCE) {
               if (var3 == -999L) {
                  var8 = 0;
               } else {
                  var8 = (int)var3 + 2;
               }
            } else {
               var8 = (int)var3;
            }
         } else if (var2 == ChronoField.MONTH_OF_YEAR) {
            var7 = 2;
            var8 = (int)var3 - 1;
         } else if (var2 == ChronoField.DAY_OF_WEEK) {
            var7 = 7;
            var8 = (int)var3 + 1;
            if (var8 > 7) {
               var8 = 1;
            }
         } else {
            if (var2 != ChronoField.AMPM_OF_DAY) {
               return null;
            }

            var7 = 9;
            var8 = (int)var3;
         }

         return CalendarDataUtility.retrieveJavaTimeFieldValueName(var1.getCalendarType(), var7, var8, var5.toCalendarStyle(), var6);
      } else {
         return this.getText(var2, var3, var5, var6);
      }
   }

   public Iterator<Map.Entry<String, Long>> getTextIterator(TemporalField var1, TextStyle var2, Locale var3) {
      Object var4 = this.findStore(var1, var3);
      return var4 instanceof DateTimeTextProvider.LocaleStore ? ((DateTimeTextProvider.LocaleStore)var4).getTextIterator(var2) : null;
   }

   public Iterator<Map.Entry<String, Long>> getTextIterator(Chronology var1, TemporalField var2, TextStyle var3, Locale var4) {
      if (var1 != IsoChronology.INSTANCE && var2 instanceof ChronoField) {
         byte var5;
         switch((ChronoField)var2) {
         case ERA:
            var5 = 0;
            break;
         case MONTH_OF_YEAR:
            var5 = 2;
            break;
         case DAY_OF_WEEK:
            var5 = 7;
            break;
         case AMPM_OF_DAY:
            var5 = 9;
            break;
         default:
            return null;
         }

         int var6 = var3 == null ? 0 : var3.toCalendarStyle();
         Map var7 = CalendarDataUtility.retrieveJavaTimeFieldValueNames(var1.getCalendarType(), var5, var6, var4);
         if (var7 == null) {
            return null;
         } else {
            ArrayList var8 = new ArrayList(var7.size());
            Iterator var9;
            Map.Entry var10;
            switch(var5) {
            case 0:
               int var11;
               for(var9 = var7.entrySet().iterator(); var9.hasNext(); var8.add(createEntry(var10.getKey(), (long)var11))) {
                  var10 = (Map.Entry)var9.next();
                  var11 = (Integer)var10.getValue();
                  if (var1 == JapaneseChronology.INSTANCE) {
                     if (var11 == 0) {
                        var11 = -999;
                     } else {
                        var11 -= 2;
                     }
                  }
               }

               return var8.iterator();
            case 2:
               var9 = var7.entrySet().iterator();

               while(var9.hasNext()) {
                  var10 = (Map.Entry)var9.next();
                  var8.add(createEntry(var10.getKey(), (long)((Integer)var10.getValue() + 1)));
               }

               return var8.iterator();
            case 7:
               var9 = var7.entrySet().iterator();

               while(var9.hasNext()) {
                  var10 = (Map.Entry)var9.next();
                  var8.add(createEntry(var10.getKey(), (long)toWeekDay((Integer)var10.getValue())));
               }

               return var8.iterator();
            default:
               var9 = var7.entrySet().iterator();

               while(var9.hasNext()) {
                  var10 = (Map.Entry)var9.next();
                  var8.add(createEntry(var10.getKey(), (long)(Integer)var10.getValue()));
               }

               return var8.iterator();
            }
         }
      } else {
         return this.getTextIterator(var2, var3, var4);
      }
   }

   private Object findStore(TemporalField var1, Locale var2) {
      Map.Entry var3 = createEntry(var1, var2);
      Object var4 = CACHE.get(var3);
      if (var4 == null) {
         var4 = this.createStore(var1, var2);
         CACHE.putIfAbsent(var3, var4);
         var4 = CACHE.get(var3);
      }

      return var4;
   }

   private static int toWeekDay(int var0) {
      return var0 == 1 ? 7 : var0 - 1;
   }

   private Object createStore(TemporalField var1, Locale var2) {
      HashMap var3 = new HashMap();
      int var5;
      HashMap var9;
      Iterator var10;
      Map.Entry var11;
      TextStyle[] var12;
      int var13;
      TextStyle var14;
      Map var15;
      if (var1 == ChronoField.ERA) {
         var12 = TextStyle.values();
         var5 = var12.length;

         for(var13 = 0; var13 < var5; ++var13) {
            var14 = var12[var13];
            if (!var14.isStandalone()) {
               var15 = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 0, var14.toCalendarStyle(), var2);
               if (var15 != null) {
                  var9 = new HashMap();
                  var10 = var15.entrySet().iterator();

                  while(var10.hasNext()) {
                     var11 = (Map.Entry)var10.next();
                     var9.put((long)(Integer)var11.getValue(), var11.getKey());
                  }

                  if (!var9.isEmpty()) {
                     var3.put(var14, var9);
                  }
               }
            }
         }

         return new DateTimeTextProvider.LocaleStore(var3);
      } else {
         int var16;
         String var17;
         if (var1 == ChronoField.MONTH_OF_YEAR) {
            var12 = TextStyle.values();
            var5 = var12.length;

            for(var13 = 0; var13 < var5; ++var13) {
               var14 = var12[var13];
               var15 = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 2, var14.toCalendarStyle(), var2);
               var9 = new HashMap();
               if (var15 != null) {
                  var10 = var15.entrySet().iterator();

                  while(var10.hasNext()) {
                     var11 = (Map.Entry)var10.next();
                     var9.put((long)((Integer)var11.getValue() + 1), var11.getKey());
                  }
               } else {
                  for(var16 = 0; var16 <= 11; ++var16) {
                     var17 = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 2, var16, var14.toCalendarStyle(), var2);
                     if (var17 == null) {
                        break;
                     }

                     var9.put((long)(var16 + 1), var17);
                  }
               }

               if (!var9.isEmpty()) {
                  var3.put(var14, var9);
               }
            }

            return new DateTimeTextProvider.LocaleStore(var3);
         } else if (var1 == ChronoField.DAY_OF_WEEK) {
            var12 = TextStyle.values();
            var5 = var12.length;

            for(var13 = 0; var13 < var5; ++var13) {
               var14 = var12[var13];
               var15 = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 7, var14.toCalendarStyle(), var2);
               var9 = new HashMap();
               if (var15 != null) {
                  var10 = var15.entrySet().iterator();

                  while(var10.hasNext()) {
                     var11 = (Map.Entry)var10.next();
                     var9.put((long)toWeekDay((Integer)var11.getValue()), var11.getKey());
                  }
               } else {
                  for(var16 = 1; var16 <= 7; ++var16) {
                     var17 = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 7, var16, var14.toCalendarStyle(), var2);
                     if (var17 == null) {
                        break;
                     }

                     var9.put((long)toWeekDay(var16), var17);
                  }
               }

               if (!var9.isEmpty()) {
                  var3.put(var14, var9);
               }
            }

            return new DateTimeTextProvider.LocaleStore(var3);
         } else if (var1 == ChronoField.AMPM_OF_DAY) {
            var12 = TextStyle.values();
            var5 = var12.length;

            for(var13 = 0; var13 < var5; ++var13) {
               var14 = var12[var13];
               if (!var14.isStandalone()) {
                  var15 = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 9, var14.toCalendarStyle(), var2);
                  if (var15 != null) {
                     var9 = new HashMap();
                     var10 = var15.entrySet().iterator();

                     while(var10.hasNext()) {
                        var11 = (Map.Entry)var10.next();
                        var9.put((long)(Integer)var11.getValue(), var11.getKey());
                     }

                     if (!var9.isEmpty()) {
                        var3.put(var14, var9);
                     }
                  }
               }
            }

            return new DateTimeTextProvider.LocaleStore(var3);
         } else if (var1 != IsoFields.QUARTER_OF_YEAR) {
            return "";
         } else {
            String[] var4 = new String[]{"QuarterNames", "standalone.QuarterNames", "QuarterAbbreviations", "standalone.QuarterAbbreviations", "QuarterNarrows", "standalone.QuarterNarrows"};

            for(var5 = 0; var5 < var4.length; ++var5) {
               String[] var6 = (String[])getLocalizedResource(var4[var5], var2);
               if (var6 != null) {
                  HashMap var7 = new HashMap();

                  for(int var8 = 0; var8 < var6.length; ++var8) {
                     var7.put((long)(var8 + 1), var6[var8]);
                  }

                  var3.put(TextStyle.values()[var5], var7);
               }
            }

            return new DateTimeTextProvider.LocaleStore(var3);
         }
      }
   }

   private static <A, B> Map.Entry<A, B> createEntry(A var0, B var1) {
      return new AbstractMap.SimpleImmutableEntry(var0, var1);
   }

   static <T> T getLocalizedResource(String var0, Locale var1) {
      LocaleResources var2 = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(var1);
      ResourceBundle var3 = var2.getJavaTimeFormatData();
      return var3.containsKey(var0) ? var3.getObject(var0) : null;
   }

   static final class LocaleStore {
      private final Map<TextStyle, Map<Long, String>> valueTextMap;
      private final Map<TextStyle, List<Map.Entry<String, Long>>> parsable;

      LocaleStore(Map<TextStyle, Map<Long, String>> var1) {
         this.valueTextMap = var1;
         HashMap var2 = new HashMap();
         ArrayList var3 = new ArrayList();
         Iterator var4 = var1.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
            HashMap var6 = new HashMap();
            Iterator var7 = ((Map)var5.getValue()).entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry var8 = (Map.Entry)var7.next();
               if (var6.put(var8.getValue(), DateTimeTextProvider.createEntry(var8.getValue(), var8.getKey())) != null) {
               }
            }

            ArrayList var9 = new ArrayList(var6.values());
            Collections.sort(var9, DateTimeTextProvider.COMPARATOR);
            var2.put(var5.getKey(), var9);
            var3.addAll(var9);
            var2.put((Object)null, var3);
         }

         Collections.sort(var3, DateTimeTextProvider.COMPARATOR);
         this.parsable = var2;
      }

      String getText(long var1, TextStyle var3) {
         Map var4 = (Map)this.valueTextMap.get(var3);
         return var4 != null ? (String)var4.get(var1) : null;
      }

      Iterator<Map.Entry<String, Long>> getTextIterator(TextStyle var1) {
         List var2 = (List)this.parsable.get(var1);
         return var2 != null ? var2.iterator() : null;
      }
   }
}
