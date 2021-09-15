package java.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.locale.provider.TimeZoneNameUtility;

public class DateFormatSymbols implements Serializable, Cloneable {
   String[] eras = null;
   String[] months = null;
   String[] shortMonths = null;
   String[] weekdays = null;
   String[] shortWeekdays = null;
   String[] ampms = null;
   String[][] zoneStrings = (String[][])null;
   transient boolean isZoneStringsSet = false;
   static final String patternChars = "GyMdkHmsSEDFwWahKzZYuXL";
   static final int PATTERN_ERA = 0;
   static final int PATTERN_YEAR = 1;
   static final int PATTERN_MONTH = 2;
   static final int PATTERN_DAY_OF_MONTH = 3;
   static final int PATTERN_HOUR_OF_DAY1 = 4;
   static final int PATTERN_HOUR_OF_DAY0 = 5;
   static final int PATTERN_MINUTE = 6;
   static final int PATTERN_SECOND = 7;
   static final int PATTERN_MILLISECOND = 8;
   static final int PATTERN_DAY_OF_WEEK = 9;
   static final int PATTERN_DAY_OF_YEAR = 10;
   static final int PATTERN_DAY_OF_WEEK_IN_MONTH = 11;
   static final int PATTERN_WEEK_OF_YEAR = 12;
   static final int PATTERN_WEEK_OF_MONTH = 13;
   static final int PATTERN_AM_PM = 14;
   static final int PATTERN_HOUR1 = 15;
   static final int PATTERN_HOUR0 = 16;
   static final int PATTERN_ZONE_NAME = 17;
   static final int PATTERN_ZONE_VALUE = 18;
   static final int PATTERN_WEEK_YEAR = 19;
   static final int PATTERN_ISO_DAY_OF_WEEK = 20;
   static final int PATTERN_ISO_ZONE = 21;
   static final int PATTERN_MONTH_STANDALONE = 22;
   String localPatternChars = null;
   Locale locale = null;
   static final long serialVersionUID = -5987973545549424702L;
   static final int millisPerHour = 3600000;
   private static final ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> cachedInstances = new ConcurrentHashMap(3);
   private transient int lastZoneIndex = 0;
   transient volatile int cachedHashCode = 0;

   public DateFormatSymbols() {
      this.initializeData(Locale.getDefault(Locale.Category.FORMAT));
   }

   public DateFormatSymbols(Locale var1) {
      this.initializeData(var1);
   }

   private DateFormatSymbols(boolean var1) {
   }

   public static Locale[] getAvailableLocales() {
      LocaleServiceProviderPool var0 = LocaleServiceProviderPool.getPool(DateFormatSymbolsProvider.class);
      return var0.getAvailableLocales();
   }

   public static final DateFormatSymbols getInstance() {
      return getInstance(Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DateFormatSymbols getInstance(Locale var0) {
      DateFormatSymbols var1 = getProviderInstance(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new RuntimeException("DateFormatSymbols instance creation failed.");
      }
   }

   static final DateFormatSymbols getInstanceRef(Locale var0) {
      DateFormatSymbols var1 = getProviderInstance(var0);
      if (var1 != null) {
         return var1;
      } else {
         throw new RuntimeException("DateFormatSymbols instance creation failed.");
      }
   }

   private static DateFormatSymbols getProviderInstance(Locale var0) {
      LocaleProviderAdapter var1 = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, var0);
      DateFormatSymbolsProvider var2 = var1.getDateFormatSymbolsProvider();
      DateFormatSymbols var3 = var2.getInstance(var0);
      if (var3 == null) {
         var2 = LocaleProviderAdapter.forJRE().getDateFormatSymbolsProvider();
         var3 = var2.getInstance(var0);
      }

      return var3;
   }

   public String[] getEras() {
      return (String[])Arrays.copyOf((Object[])this.eras, this.eras.length);
   }

   public void setEras(String[] var1) {
      this.eras = (String[])Arrays.copyOf((Object[])var1, var1.length);
      this.cachedHashCode = 0;
   }

   public String[] getMonths() {
      return (String[])Arrays.copyOf((Object[])this.months, this.months.length);
   }

   public void setMonths(String[] var1) {
      this.months = (String[])Arrays.copyOf((Object[])var1, var1.length);
      this.cachedHashCode = 0;
   }

   public String[] getShortMonths() {
      return (String[])Arrays.copyOf((Object[])this.shortMonths, this.shortMonths.length);
   }

   public void setShortMonths(String[] var1) {
      this.shortMonths = (String[])Arrays.copyOf((Object[])var1, var1.length);
      this.cachedHashCode = 0;
   }

   public String[] getWeekdays() {
      return (String[])Arrays.copyOf((Object[])this.weekdays, this.weekdays.length);
   }

   public void setWeekdays(String[] var1) {
      this.weekdays = (String[])Arrays.copyOf((Object[])var1, var1.length);
      this.cachedHashCode = 0;
   }

   public String[] getShortWeekdays() {
      return (String[])Arrays.copyOf((Object[])this.shortWeekdays, this.shortWeekdays.length);
   }

   public void setShortWeekdays(String[] var1) {
      this.shortWeekdays = (String[])Arrays.copyOf((Object[])var1, var1.length);
      this.cachedHashCode = 0;
   }

   public String[] getAmPmStrings() {
      return (String[])Arrays.copyOf((Object[])this.ampms, this.ampms.length);
   }

   public void setAmPmStrings(String[] var1) {
      this.ampms = (String[])Arrays.copyOf((Object[])var1, var1.length);
      this.cachedHashCode = 0;
   }

   public String[][] getZoneStrings() {
      return this.getZoneStringsImpl(true);
   }

   public void setZoneStrings(String[][] var1) {
      String[][] var2 = new String[var1.length][];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         int var4 = var1[var3].length;
         if (var4 < 5) {
            throw new IllegalArgumentException();
         }

         var2[var3] = (String[])Arrays.copyOf((Object[])var1[var3], var4);
      }

      this.zoneStrings = var2;
      this.isZoneStringsSet = true;
      this.cachedHashCode = 0;
   }

   public String getLocalPatternChars() {
      return this.localPatternChars;
   }

   public void setLocalPatternChars(String var1) {
      this.localPatternChars = var1.toString();
      this.cachedHashCode = 0;
   }

   public Object clone() {
      try {
         DateFormatSymbols var1 = (DateFormatSymbols)super.clone();
         this.copyMembers(this, var1);
         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2);
      }
   }

   public int hashCode() {
      int var1 = this.cachedHashCode;
      if (var1 == 0) {
         byte var2 = 5;
         var1 = 11 * var2 + Arrays.hashCode((Object[])this.eras);
         var1 = 11 * var1 + Arrays.hashCode((Object[])this.months);
         var1 = 11 * var1 + Arrays.hashCode((Object[])this.shortMonths);
         var1 = 11 * var1 + Arrays.hashCode((Object[])this.weekdays);
         var1 = 11 * var1 + Arrays.hashCode((Object[])this.shortWeekdays);
         var1 = 11 * var1 + Arrays.hashCode((Object[])this.ampms);
         var1 = 11 * var1 + Arrays.deepHashCode(this.getZoneStringsWrapper());
         var1 = 11 * var1 + Objects.hashCode(this.localPatternChars);
         this.cachedHashCode = var1;
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         DateFormatSymbols var2 = (DateFormatSymbols)var1;
         return Arrays.equals((Object[])this.eras, (Object[])var2.eras) && Arrays.equals((Object[])this.months, (Object[])var2.months) && Arrays.equals((Object[])this.shortMonths, (Object[])var2.shortMonths) && Arrays.equals((Object[])this.weekdays, (Object[])var2.weekdays) && Arrays.equals((Object[])this.shortWeekdays, (Object[])var2.shortWeekdays) && Arrays.equals((Object[])this.ampms, (Object[])var2.ampms) && Arrays.deepEquals(this.getZoneStringsWrapper(), var2.getZoneStringsWrapper()) && (this.localPatternChars != null && this.localPatternChars.equals(var2.localPatternChars) || this.localPatternChars == null && var2.localPatternChars == null);
      } else {
         return false;
      }
   }

   private void initializeData(Locale var1) {
      SoftReference var2 = (SoftReference)cachedInstances.get(var1);
      DateFormatSymbols var3;
      if (var2 == null || (var3 = (DateFormatSymbols)var2.get()) == null) {
         if (var2 != null) {
            cachedInstances.remove(var1, var2);
         }

         var3 = new DateFormatSymbols(false);
         LocaleProviderAdapter var4 = LocaleProviderAdapter.getAdapter(DateFormatSymbolsProvider.class, var1);
         if (!(var4 instanceof ResourceBundleBasedAdapter)) {
            var4 = LocaleProviderAdapter.getResourceBundleBased();
         }

         ResourceBundle var5 = ((ResourceBundleBasedAdapter)var4).getLocaleData().getDateFormatData(var1);
         var3.locale = var1;
         if (var5.containsKey("Eras")) {
            var3.eras = var5.getStringArray("Eras");
         } else if (var5.containsKey("long.Eras")) {
            var3.eras = var5.getStringArray("long.Eras");
         } else if (var5.containsKey("short.Eras")) {
            var3.eras = var5.getStringArray("short.Eras");
         }

         var3.months = var5.getStringArray("MonthNames");
         var3.shortMonths = var5.getStringArray("MonthAbbreviations");
         var3.ampms = var5.getStringArray("AmPmMarkers");
         var3.localPatternChars = var5.getString("DateTimePatternChars");
         var3.weekdays = toOneBasedArray(var5.getStringArray("DayNames"));
         var3.shortWeekdays = toOneBasedArray(var5.getStringArray("DayAbbreviations"));
         var2 = new SoftReference(var3);
         SoftReference var6 = (SoftReference)cachedInstances.putIfAbsent(var1, var2);
         if (var6 != null) {
            DateFormatSymbols var7 = (DateFormatSymbols)var6.get();
            if (var7 == null) {
               cachedInstances.replace(var1, var6, var2);
            } else {
               var2 = var6;
               var3 = var7;
            }
         }

         Locale var9 = var5.getLocale();
         if (!var9.equals(var1)) {
            SoftReference var8 = (SoftReference)cachedInstances.putIfAbsent(var9, var2);
            if (var8 != null && var8.get() == null) {
               cachedInstances.replace(var9, var8, var2);
            }
         }
      }

      this.copyMembers(var3, this);
   }

   private static String[] toOneBasedArray(String[] var0) {
      int var1 = var0.length;
      String[] var2 = new String[var1 + 1];
      var2[0] = "";

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3 + 1] = var0[var3];
      }

      return var2;
   }

   final int getZoneIndex(String var1) {
      String[][] var2 = this.getZoneStringsWrapper();
      if (this.lastZoneIndex < var2.length && var1.equals(var2[this.lastZoneIndex][0])) {
         return this.lastZoneIndex;
      } else {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.equals(var2[var3][0])) {
               this.lastZoneIndex = var3;
               return var3;
            }
         }

         return -1;
      }
   }

   final String[][] getZoneStringsWrapper() {
      return this.isSubclassObject() ? this.getZoneStrings() : this.getZoneStringsImpl(false);
   }

   private String[][] getZoneStringsImpl(boolean var1) {
      if (this.zoneStrings == null) {
         this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale);
      }

      if (!var1) {
         return this.zoneStrings;
      } else {
         int var2 = this.zoneStrings.length;
         String[][] var3 = new String[var2][];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = (String[])Arrays.copyOf((Object[])this.zoneStrings[var4], this.zoneStrings[var4].length);
         }

         return var3;
      }
   }

   private boolean isSubclassObject() {
      return !this.getClass().getName().equals("java.text.DateFormatSymbols");
   }

   private void copyMembers(DateFormatSymbols var1, DateFormatSymbols var2) {
      var2.locale = var1.locale;
      var2.eras = (String[])Arrays.copyOf((Object[])var1.eras, var1.eras.length);
      var2.months = (String[])Arrays.copyOf((Object[])var1.months, var1.months.length);
      var2.shortMonths = (String[])Arrays.copyOf((Object[])var1.shortMonths, var1.shortMonths.length);
      var2.weekdays = (String[])Arrays.copyOf((Object[])var1.weekdays, var1.weekdays.length);
      var2.shortWeekdays = (String[])Arrays.copyOf((Object[])var1.shortWeekdays, var1.shortWeekdays.length);
      var2.ampms = (String[])Arrays.copyOf((Object[])var1.ampms, var1.ampms.length);
      if (var1.zoneStrings != null) {
         var2.zoneStrings = var1.getZoneStringsImpl(true);
      } else {
         var2.zoneStrings = (String[][])null;
      }

      var2.localPatternChars = var1.localPatternChars;
      var2.cachedHashCode = 0;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.zoneStrings == null) {
         this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale);
      }

      var1.defaultWriteObject();
   }
}
