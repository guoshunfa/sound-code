package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.spi.TimeZoneNameProvider;
import sun.util.calendar.ZoneInfo;

public final class TimeZoneNameUtility {
   private static ConcurrentHashMap<Locale, SoftReference<String[][]>> cachedZoneData = new ConcurrentHashMap();
   private static final Map<String, SoftReference<Map<Locale, String[]>>> cachedDisplayNames = new ConcurrentHashMap();

   public static String[][] getZoneStrings(Locale var0) {
      SoftReference var2 = (SoftReference)cachedZoneData.get(var0);
      String[][] var1;
      if (var2 == null || (var1 = (String[][])var2.get()) == null) {
         var1 = loadZoneStrings(var0);
         var2 = new SoftReference(var1);
         cachedZoneData.put(var0, var2);
      }

      return var1;
   }

   private static String[][] loadZoneStrings(Locale var0) {
      LocaleProviderAdapter var1 = LocaleProviderAdapter.getAdapter(TimeZoneNameProvider.class, var0);
      TimeZoneNameProvider var2 = var1.getTimeZoneNameProvider();
      if (var2 instanceof TimeZoneNameProviderImpl) {
         return ((TimeZoneNameProviderImpl)var2).getZoneStrings(var0);
      } else {
         Set var3 = LocaleProviderAdapter.forJRE().getLocaleResources(var0).getZoneIDs();
         LinkedList var4 = new LinkedList();
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            String[] var7 = retrieveDisplayNamesImpl(var6, var0);
            if (var7 != null) {
               var4.add(var7);
            }
         }

         String[][] var8 = new String[var4.size()][];
         return (String[][])var4.toArray(var8);
      }
   }

   public static String[] retrieveDisplayNames(String var0, Locale var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      return retrieveDisplayNamesImpl(var0, var1);
   }

   public static String retrieveGenericDisplayName(String var0, int var1, Locale var2) {
      String[] var3 = retrieveDisplayNamesImpl(var0, var2);
      return Objects.nonNull(var3) ? var3[6 - var1] : null;
   }

   public static String retrieveDisplayName(String var0, boolean var1, int var2, Locale var3) {
      String[] var4 = retrieveDisplayNamesImpl(var0, var3);
      return Objects.nonNull(var4) ? var4[(var1 ? 4 : 2) - var2] : null;
   }

   private static String[] retrieveDisplayNamesImpl(String var0, Locale var1) {
      LocaleServiceProviderPool var2 = LocaleServiceProviderPool.getPool(TimeZoneNameProvider.class);
      Object var4 = null;
      SoftReference var5 = (SoftReference)cachedDisplayNames.get(var0);
      String[] var3;
      if (Objects.nonNull(var5)) {
         var4 = (Map)var5.get();
         if (Objects.nonNull(var4)) {
            var3 = (String[])((Map)var4).get(var1);
            if (Objects.nonNull(var3)) {
               return var3;
            }
         }
      }

      var3 = new String[7];
      var3[0] = var0;

      for(int var6 = 1; var6 <= 6; ++var6) {
         var3[var6] = (String)var2.getLocalizedObject(TimeZoneNameUtility.TimeZoneNameGetter.INSTANCE, var1, var6 < 5 ? (var6 < 3 ? "std" : "dst") : "generic", var6 % 2, var0);
      }

      if (Objects.isNull(var4)) {
         var4 = new ConcurrentHashMap();
      }

      ((Map)var4).put(var1, var3);
      var5 = new SoftReference(var4);
      cachedDisplayNames.put(var0, var5);
      return var3;
   }

   private TimeZoneNameUtility() {
   }

   private static class TimeZoneNameGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<TimeZoneNameProvider, String> {
      private static final TimeZoneNameUtility.TimeZoneNameGetter INSTANCE = new TimeZoneNameUtility.TimeZoneNameGetter();

      public String getObject(TimeZoneNameProvider var1, Locale var2, String var3, Object... var4) {
         assert var4.length == 2;

         int var5 = (Integer)var4[0];
         String var6 = (String)var4[1];
         String var7 = getName(var1, var2, var3, var5, var6);
         if (var7 == null) {
            Map var8 = ZoneInfo.getAliasTable();
            if (var8 != null) {
               String var9 = (String)var8.get(var6);
               if (var9 != null) {
                  var7 = getName(var1, var2, var3, var5, var9);
               }

               if (var7 == null) {
                  var7 = examineAliases(var1, var2, var3, var9 != null ? var9 : var6, var5, var8);
               }
            }
         }

         return var7;
      }

      private static String examineAliases(TimeZoneNameProvider var0, Locale var1, String var2, String var3, int var4, Map<String, String> var5) {
         Iterator var6 = var5.entrySet().iterator();

         while(var6.hasNext()) {
            Map.Entry var7 = (Map.Entry)var6.next();
            if (((String)var7.getValue()).equals(var3)) {
               String var8 = (String)var7.getKey();
               String var9 = getName(var0, var1, var2, var4, var8);
               if (var9 != null) {
                  return var9;
               }

               var9 = examineAliases(var0, var1, var2, var8, var4, var5);
               if (var9 != null) {
                  return var9;
               }
            }
         }

         return null;
      }

      private static String getName(TimeZoneNameProvider var0, Locale var1, String var2, int var3, String var4) {
         String var5 = null;
         byte var7 = -1;
         switch(var2.hashCode()) {
         case -80148009:
            if (var2.equals("generic")) {
               var7 = 2;
            }
            break;
         case 99781:
            if (var2.equals("dst")) {
               var7 = 1;
            }
            break;
         case 114211:
            if (var2.equals("std")) {
               var7 = 0;
            }
         }

         switch(var7) {
         case 0:
            var5 = var0.getDisplayName(var4, false, var3, var1);
            break;
         case 1:
            var5 = var0.getDisplayName(var4, true, var3, var1);
            break;
         case 2:
            var5 = var0.getGenericDisplayName(var4, var3, var1);
         }

         return var5;
      }
   }
}
