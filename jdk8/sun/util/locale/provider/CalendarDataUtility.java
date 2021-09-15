package sun.util.locale.provider;

import java.util.Locale;
import java.util.Map;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;

public class CalendarDataUtility {
   public static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
   public static final String MINIMAL_DAYS_IN_FIRST_WEEK = "minimalDaysInFirstWeek";

   private CalendarDataUtility() {
   }

   public static int retrieveFirstDayOfWeek(Locale var0) {
      LocaleServiceProviderPool var1 = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
      Integer var2 = (Integer)var1.getLocalizedObject(CalendarDataUtility.CalendarWeekParameterGetter.INSTANCE, var0, "firstDayOfWeek");
      return var2 != null && var2 >= 1 && var2 <= 7 ? var2 : 1;
   }

   public static int retrieveMinimalDaysInFirstWeek(Locale var0) {
      LocaleServiceProviderPool var1 = LocaleServiceProviderPool.getPool(CalendarDataProvider.class);
      Integer var2 = (Integer)var1.getLocalizedObject(CalendarDataUtility.CalendarWeekParameterGetter.INSTANCE, var0, "minimalDaysInFirstWeek");
      return var2 != null && var2 >= 1 && var2 <= 7 ? var2 : 1;
   }

   public static String retrieveFieldValueName(String var0, int var1, int var2, int var3, Locale var4) {
      LocaleServiceProviderPool var5 = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
      return (String)var5.getLocalizedObject(CalendarDataUtility.CalendarFieldValueNameGetter.INSTANCE, var4, normalizeCalendarType(var0), var1, var2, var3, false);
   }

   public static String retrieveJavaTimeFieldValueName(String var0, int var1, int var2, int var3, Locale var4) {
      LocaleServiceProviderPool var5 = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
      String var6 = (String)var5.getLocalizedObject(CalendarDataUtility.CalendarFieldValueNameGetter.INSTANCE, var4, normalizeCalendarType(var0), var1, var2, var3, true);
      if (var6 == null) {
         var6 = (String)var5.getLocalizedObject(CalendarDataUtility.CalendarFieldValueNameGetter.INSTANCE, var4, normalizeCalendarType(var0), var1, var2, var3, false);
      }

      return var6;
   }

   public static Map<String, Integer> retrieveFieldValueNames(String var0, int var1, int var2, Locale var3) {
      LocaleServiceProviderPool var4 = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
      return (Map)var4.getLocalizedObject(CalendarDataUtility.CalendarFieldValueNamesMapGetter.INSTANCE, var3, normalizeCalendarType(var0), var1, var2, false);
   }

   public static Map<String, Integer> retrieveJavaTimeFieldValueNames(String var0, int var1, int var2, Locale var3) {
      LocaleServiceProviderPool var4 = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
      Map var5 = (Map)var4.getLocalizedObject(CalendarDataUtility.CalendarFieldValueNamesMapGetter.INSTANCE, var3, normalizeCalendarType(var0), var1, var2, true);
      if (var5 == null) {
         var5 = (Map)var4.getLocalizedObject(CalendarDataUtility.CalendarFieldValueNamesMapGetter.INSTANCE, var3, normalizeCalendarType(var0), var1, var2, false);
      }

      return var5;
   }

   static String normalizeCalendarType(String var0) {
      String var1;
      if (!var0.equals("gregorian") && !var0.equals("iso8601")) {
         if (var0.startsWith("islamic")) {
            var1 = "islamic";
         } else {
            var1 = var0;
         }
      } else {
         var1 = "gregory";
      }

      return var1;
   }

   private static class CalendarWeekParameterGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarDataProvider, Integer> {
      private static final CalendarDataUtility.CalendarWeekParameterGetter INSTANCE = new CalendarDataUtility.CalendarWeekParameterGetter();

      public Integer getObject(CalendarDataProvider var1, Locale var2, String var3, Object... var4) {
         assert var4.length == 0;

         byte var7 = -1;
         switch(var3.hashCode()) {
         case 522320983:
            if (var3.equals("firstDayOfWeek")) {
               var7 = 0;
            }
            break;
         case 1347492455:
            if (var3.equals("minimalDaysInFirstWeek")) {
               var7 = 1;
            }
         }

         int var5;
         switch(var7) {
         case 0:
            var5 = var1.getFirstDayOfWeek(var2);
            break;
         case 1:
            var5 = var1.getMinimalDaysInFirstWeek(var2);
            break;
         default:
            throw new InternalError("invalid requestID: " + var3);
         }

         return var5 != 0 ? var5 : null;
      }
   }

   private static class CalendarFieldValueNamesMapGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, Map<String, Integer>> {
      private static final CalendarDataUtility.CalendarFieldValueNamesMapGetter INSTANCE = new CalendarDataUtility.CalendarFieldValueNamesMapGetter();

      public Map<String, Integer> getObject(CalendarNameProvider var1, Locale var2, String var3, Object... var4) {
         assert var4.length == 3;

         int var5 = (Integer)var4[0];
         int var6 = (Integer)var4[1];
         boolean var7 = (Boolean)var4[2];
         if (var7 && var1 instanceof CalendarNameProviderImpl) {
            Map var8 = ((CalendarNameProviderImpl)var1).getJavaTimeDisplayNames(var3, var5, var6, var2);
            return var8;
         } else {
            return var1.getDisplayNames(var3, var5, var6, var2);
         }
      }
   }

   private static class CalendarFieldValueNameGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, String> {
      private static final CalendarDataUtility.CalendarFieldValueNameGetter INSTANCE = new CalendarDataUtility.CalendarFieldValueNameGetter();

      public String getObject(CalendarNameProvider var1, Locale var2, String var3, Object... var4) {
         assert var4.length == 4;

         int var5 = (Integer)var4[0];
         int var6 = (Integer)var4[1];
         int var7 = (Integer)var4[2];
         boolean var8 = (Boolean)var4[3];
         if (var8 && var1 instanceof CalendarNameProviderImpl) {
            String var9 = ((CalendarNameProviderImpl)var1).getJavaTimeDisplayName(var3, var5, var6, var7, var2);
            return var9;
         } else {
            return var1.getDisplayName(var3, var5, var6, var7, var2);
         }
      }
   }
}
