package sun.util.locale.provider;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.calendar.ZoneInfo;
import sun.util.resources.LocaleData;
import sun.util.resources.OpenListResourceBundle;
import sun.util.resources.ParallelListResourceBundle;
import sun.util.resources.TimeZoneNamesBundle;

public class LocaleResources {
   private final Locale locale;
   private final LocaleData localeData;
   private final LocaleProviderAdapter.Type type;
   private ConcurrentMap<String, LocaleResources.ResourceReference> cache = new ConcurrentHashMap();
   private ReferenceQueue<Object> referenceQueue = new ReferenceQueue();
   private static final String BREAK_ITERATOR_INFO = "BII.";
   private static final String CALENDAR_DATA = "CALD.";
   private static final String COLLATION_DATA_CACHEKEY = "COLD";
   private static final String DECIMAL_FORMAT_SYMBOLS_DATA_CACHEKEY = "DFSD";
   private static final String CURRENCY_NAMES = "CN.";
   private static final String LOCALE_NAMES = "LN.";
   private static final String TIME_ZONE_NAMES = "TZN.";
   private static final String ZONE_IDS_CACHEKEY = "ZID";
   private static final String CALENDAR_NAMES = "CALN.";
   private static final String NUMBER_PATTERNS_CACHEKEY = "NP";
   private static final String DATE_TIME_PATTERN = "DTP.";
   private static final Object NULLOBJECT = new Object();

   LocaleResources(ResourceBundleBasedAdapter var1, Locale var2) {
      this.locale = var2;
      this.localeData = var1.getLocaleData();
      this.type = ((LocaleProviderAdapter)var1).getAdapterType();
   }

   private void removeEmptyReferences() {
      Reference var1;
      while((var1 = this.referenceQueue.poll()) != null) {
         this.cache.remove(((LocaleResources.ResourceReference)var1).getCacheKey());
      }

   }

   Object getBreakIteratorInfo(String var1) {
      String var3 = "BII." + var1;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var4 = (LocaleResources.ResourceReference)this.cache.get(var3);
      Object var2;
      if (var4 == null || (var2 = var4.get()) == null) {
         var2 = this.localeData.getBreakIteratorInfo(this.locale).getObject(var1);
         this.cache.put(var3, new LocaleResources.ResourceReference(var3, var2, this.referenceQueue));
      }

      return var2;
   }

   int getCalendarData(String var1) {
      String var3 = "CALD." + var1;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var4 = (LocaleResources.ResourceReference)this.cache.get(var3);
      Integer var2;
      if (var4 == null || (var2 = (Integer)var4.get()) == null) {
         ResourceBundle var5 = this.localeData.getCalendarData(this.locale);
         if (var5.containsKey(var1)) {
            var2 = Integer.parseInt(var5.getString(var1));
         } else {
            var2 = 0;
         }

         this.cache.put(var3, new LocaleResources.ResourceReference(var3, var2, this.referenceQueue));
      }

      return var2;
   }

   public String getCollationData() {
      String var1 = "Rule";
      String var2 = "";
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var3 = (LocaleResources.ResourceReference)this.cache.get("COLD");
      if (var3 == null || (var2 = (String)var3.get()) == null) {
         ResourceBundle var4 = this.localeData.getCollationData(this.locale);
         if (var4.containsKey(var1)) {
            var2 = var4.getString(var1);
         }

         this.cache.put("COLD", new LocaleResources.ResourceReference("COLD", var2, this.referenceQueue));
      }

      return var2;
   }

   public Object[] getDecimalFormatSymbolsData() {
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var2 = (LocaleResources.ResourceReference)this.cache.get("DFSD");
      Object[] var1;
      if (var2 == null || (var1 = (Object[])((Object[])var2.get())) == null) {
         ResourceBundle var3 = this.localeData.getNumberFormatData(this.locale);
         var1 = new Object[3];
         String var5 = this.locale.getUnicodeLocaleType("nu");
         String var4;
         if (var5 != null) {
            var4 = var5 + ".NumberElements";
            if (var3.containsKey(var4)) {
               var1[0] = var3.getStringArray(var4);
            }
         }

         if (var1[0] == null && var3.containsKey("DefaultNumberingSystem")) {
            var4 = var3.getString("DefaultNumberingSystem") + ".NumberElements";
            if (var3.containsKey(var4)) {
               var1[0] = var3.getStringArray(var4);
            }
         }

         if (var1[0] == null) {
            var1[0] = var3.getStringArray("NumberElements");
         }

         this.cache.put("DFSD", new LocaleResources.ResourceReference("DFSD", var1, this.referenceQueue));
      }

      return var1;
   }

   public String getCurrencyName(String var1) {
      Object var2 = null;
      String var3 = "CN." + var1;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var4 = (LocaleResources.ResourceReference)this.cache.get(var3);
      if (var4 != null && (var2 = var4.get()) != null) {
         if (var2.equals(NULLOBJECT)) {
            var2 = null;
         }

         return (String)var2;
      } else {
         OpenListResourceBundle var5 = this.localeData.getCurrencyNames(this.locale);
         if (var5.containsKey(var1)) {
            var2 = var5.getObject(var1);
            this.cache.put(var3, new LocaleResources.ResourceReference(var3, var2, this.referenceQueue));
         }

         return (String)var2;
      }
   }

   public String getLocaleName(String var1) {
      Object var2 = null;
      String var3 = "LN." + var1;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var4 = (LocaleResources.ResourceReference)this.cache.get(var3);
      if (var4 != null && (var2 = var4.get()) != null) {
         if (var2.equals(NULLOBJECT)) {
            var2 = null;
         }

         return (String)var2;
      } else {
         OpenListResourceBundle var5 = this.localeData.getLocaleNames(this.locale);
         if (var5.containsKey(var1)) {
            var2 = var5.getObject(var1);
            this.cache.put(var3, new LocaleResources.ResourceReference(var3, var2, this.referenceQueue));
         }

         return (String)var2;
      }
   }

   String[] getTimeZoneNames(String var1) {
      String[] var2 = null;
      String var3 = "TZN.." + var1;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var4 = (LocaleResources.ResourceReference)this.cache.get(var3);
      if (Objects.isNull(var4) || Objects.isNull(var2 = (String[])((String[])var4.get()))) {
         TimeZoneNamesBundle var5 = this.localeData.getTimeZoneNames(this.locale);
         if (var5.containsKey(var1)) {
            var2 = var5.getStringArray(var1);
            this.cache.put(var3, new LocaleResources.ResourceReference(var3, var2, this.referenceQueue));
         }
      }

      return var2;
   }

   Set<String> getZoneIDs() {
      Set var1 = null;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var2 = (LocaleResources.ResourceReference)this.cache.get("ZID");
      if (var2 == null || (var1 = (Set)var2.get()) == null) {
         TimeZoneNamesBundle var3 = this.localeData.getTimeZoneNames(this.locale);
         var1 = var3.keySet();
         this.cache.put("ZID", new LocaleResources.ResourceReference("ZID", var1, this.referenceQueue));
      }

      return var1;
   }

   String[][] getZoneStrings() {
      TimeZoneNamesBundle var1 = this.localeData.getTimeZoneNames(this.locale);
      Set var2 = this.getZoneIDs();
      LinkedHashSet var3 = new LinkedHashSet();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.add(var1.getStringArray(var5));
      }

      if (this.type == LocaleProviderAdapter.Type.CLDR) {
         Map var9 = ZoneInfo.getAliasTable();
         Iterator var10 = var9.keySet().iterator();

         while(var10.hasNext()) {
            String var6 = (String)var10.next();
            if (!var2.contains(var6)) {
               String var7 = (String)var9.get(var6);
               if (var2.contains(var7)) {
                  String[] var8 = var1.getStringArray(var7);
                  var8[0] = var6;
                  var3.add(var8);
               }
            }
         }
      }

      return (String[][])var3.toArray(new String[0][]);
   }

   String[] getCalendarNames(String var1) {
      String[] var2 = null;
      String var3 = "CALN." + var1;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var4 = (LocaleResources.ResourceReference)this.cache.get(var3);
      if (var4 == null || (var2 = (String[])((String[])var4.get())) == null) {
         ResourceBundle var5 = this.localeData.getDateFormatData(this.locale);
         if (var5.containsKey(var1)) {
            var2 = var5.getStringArray(var1);
            this.cache.put(var3, new LocaleResources.ResourceReference(var3, var2, this.referenceQueue));
         }
      }

      return var2;
   }

   String[] getJavaTimeNames(String var1) {
      String[] var2 = null;
      String var3 = "CALN." + var1;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var4 = (LocaleResources.ResourceReference)this.cache.get(var3);
      if (var4 == null || (var2 = (String[])((String[])var4.get())) == null) {
         ResourceBundle var5 = this.getJavaTimeFormatData();
         if (var5.containsKey(var1)) {
            var2 = var5.getStringArray(var1);
            this.cache.put(var3, new LocaleResources.ResourceReference(var3, var2, this.referenceQueue));
         }
      }

      return var2;
   }

   public String getDateTimePattern(int var1, int var2, Calendar var3) {
      if (var3 == null) {
         var3 = Calendar.getInstance(this.locale);
      }

      return this.getDateTimePattern((String)null, var1, var2, var3.getCalendarType());
   }

   public String getJavaTimeDateTimePattern(int var1, int var2, String var3) {
      var3 = CalendarDataUtility.normalizeCalendarType(var3);
      String var4 = this.getDateTimePattern("java.time.", var1, var2, var3);
      if (var4 == null) {
         var4 = this.getDateTimePattern((String)null, var1, var2, var3);
      }

      return var4;
   }

   private String getDateTimePattern(String var1, int var2, int var3, String var4) {
      String var6 = null;
      String var7 = null;
      if (var2 >= 0) {
         if (var1 != null) {
            var6 = this.getDateTimePattern(var1, "TimePatterns", var2, var4);
         }

         if (var6 == null) {
            var6 = this.getDateTimePattern((String)null, "TimePatterns", var2, var4);
         }
      }

      if (var3 >= 0) {
         if (var1 != null) {
            var7 = this.getDateTimePattern(var1, "DatePatterns", var3, var4);
         }

         if (var7 == null) {
            var7 = this.getDateTimePattern((String)null, "DatePatterns", var3, var4);
         }
      }

      String var5;
      if (var2 >= 0) {
         if (var3 >= 0) {
            String var8 = null;
            if (var1 != null) {
               var8 = this.getDateTimePattern(var1, "DateTimePatterns", 0, var4);
            }

            if (var8 == null) {
               var8 = this.getDateTimePattern((String)null, "DateTimePatterns", 0, var4);
            }

            byte var10 = -1;
            switch(var8.hashCode()) {
            case -1015484401:
               if (var8.equals("{0} {1}")) {
                  var10 = 1;
               }
               break;
            case -986855281:
               if (var8.equals("{1} {0}")) {
                  var10 = 0;
               }
            }

            switch(var10) {
            case 0:
               var5 = var7 + " " + var6;
               break;
            case 1:
               var5 = var6 + " " + var7;
               break;
            default:
               var5 = MessageFormat.format(var8, var6, var7);
            }
         } else {
            var5 = var6;
         }
      } else {
         if (var3 < 0) {
            throw new IllegalArgumentException("No date or time style specified");
         }

         var5 = var7;
      }

      return var5;
   }

   public String[] getNumberPatterns() {
      String[] var1 = null;
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var2 = (LocaleResources.ResourceReference)this.cache.get("NP");
      if (var2 == null || (var1 = (String[])((String[])var2.get())) == null) {
         ResourceBundle var3 = this.localeData.getNumberFormatData(this.locale);
         var1 = var3.getStringArray("NumberPatterns");
         this.cache.put("NP", new LocaleResources.ResourceReference("NP", var1, this.referenceQueue));
      }

      return var1;
   }

   public ResourceBundle getJavaTimeFormatData() {
      ResourceBundle var1 = this.localeData.getDateFormatData(this.locale);
      if (var1 instanceof ParallelListResourceBundle) {
         this.localeData.setSupplementary((ParallelListResourceBundle)var1);
      }

      return var1;
   }

   private String getDateTimePattern(String var1, String var2, int var3, String var4) {
      StringBuilder var5 = new StringBuilder();
      if (var1 != null) {
         var5.append(var1);
      }

      if (!"gregory".equals(var4)) {
         var5.append(var4).append('.');
      }

      var5.append(var2);
      String var6 = var5.toString();
      String var7 = var5.insert(0, (String)"DTP.").toString();
      this.removeEmptyReferences();
      LocaleResources.ResourceReference var8 = (LocaleResources.ResourceReference)this.cache.get(var7);
      Object var9 = NULLOBJECT;
      if (var8 == null || (var9 = var8.get()) == null) {
         ResourceBundle var10 = var1 != null ? this.getJavaTimeFormatData() : this.localeData.getDateFormatData(this.locale);
         if (var10.containsKey(var6)) {
            var9 = var10.getStringArray(var6);
         } else {
            assert !var6.equals(var2);

            if (var10.containsKey(var2)) {
               var9 = var10.getStringArray(var2);
            }
         }

         this.cache.put(var7, new LocaleResources.ResourceReference(var7, var9, this.referenceQueue));
      }

      if (var9 == NULLOBJECT) {
         assert var1 != null;

         return null;
      } else {
         return ((String[])((String[])var9))[var3];
      }
   }

   private static class ResourceReference extends SoftReference<Object> {
      private final String cacheKey;

      ResourceReference(String var1, Object var2, ReferenceQueue<Object> var3) {
         super(var2, var3);
         this.cacheKey = var1;
      }

      String getCacheKey() {
         return this.cacheKey;
      }
   }
}
