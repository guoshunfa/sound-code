package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.BuddhistCalendar;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

public abstract class Calendar implements Serializable, Cloneable, Comparable<Calendar> {
   public static final int ERA = 0;
   public static final int YEAR = 1;
   public static final int MONTH = 2;
   public static final int WEEK_OF_YEAR = 3;
   public static final int WEEK_OF_MONTH = 4;
   public static final int DATE = 5;
   public static final int DAY_OF_MONTH = 5;
   public static final int DAY_OF_YEAR = 6;
   public static final int DAY_OF_WEEK = 7;
   public static final int DAY_OF_WEEK_IN_MONTH = 8;
   public static final int AM_PM = 9;
   public static final int HOUR = 10;
   public static final int HOUR_OF_DAY = 11;
   public static final int MINUTE = 12;
   public static final int SECOND = 13;
   public static final int MILLISECOND = 14;
   public static final int ZONE_OFFSET = 15;
   public static final int DST_OFFSET = 16;
   public static final int FIELD_COUNT = 17;
   public static final int SUNDAY = 1;
   public static final int MONDAY = 2;
   public static final int TUESDAY = 3;
   public static final int WEDNESDAY = 4;
   public static final int THURSDAY = 5;
   public static final int FRIDAY = 6;
   public static final int SATURDAY = 7;
   public static final int JANUARY = 0;
   public static final int FEBRUARY = 1;
   public static final int MARCH = 2;
   public static final int APRIL = 3;
   public static final int MAY = 4;
   public static final int JUNE = 5;
   public static final int JULY = 6;
   public static final int AUGUST = 7;
   public static final int SEPTEMBER = 8;
   public static final int OCTOBER = 9;
   public static final int NOVEMBER = 10;
   public static final int DECEMBER = 11;
   public static final int UNDECIMBER = 12;
   public static final int AM = 0;
   public static final int PM = 1;
   public static final int ALL_STYLES = 0;
   static final int STANDALONE_MASK = 32768;
   public static final int SHORT = 1;
   public static final int LONG = 2;
   public static final int NARROW_FORMAT = 4;
   public static final int NARROW_STANDALONE = 32772;
   public static final int SHORT_FORMAT = 1;
   public static final int LONG_FORMAT = 2;
   public static final int SHORT_STANDALONE = 32769;
   public static final int LONG_STANDALONE = 32770;
   protected int[] fields;
   protected boolean[] isSet;
   private transient int[] stamp;
   protected long time;
   protected boolean isTimeSet;
   protected boolean areFieldsSet;
   transient boolean areAllFieldsSet;
   private boolean lenient;
   private TimeZone zone;
   private transient boolean sharedZone;
   private int firstDayOfWeek;
   private int minimalDaysInFirstWeek;
   private static final ConcurrentMap<Locale, int[]> cachedLocaleData = new ConcurrentHashMap(3);
   private static final int UNSET = 0;
   private static final int COMPUTED = 1;
   private static final int MINIMUM_USER_STAMP = 2;
   static final int ALL_FIELDS = 131071;
   private int nextStamp;
   static final int currentSerialVersion = 1;
   private int serialVersionOnStream;
   static final long serialVersionUID = -1807547505821590642L;
   static final int ERA_MASK = 1;
   static final int YEAR_MASK = 2;
   static final int MONTH_MASK = 4;
   static final int WEEK_OF_YEAR_MASK = 8;
   static final int WEEK_OF_MONTH_MASK = 16;
   static final int DAY_OF_MONTH_MASK = 32;
   static final int DATE_MASK = 32;
   static final int DAY_OF_YEAR_MASK = 64;
   static final int DAY_OF_WEEK_MASK = 128;
   static final int DAY_OF_WEEK_IN_MONTH_MASK = 256;
   static final int AM_PM_MASK = 512;
   static final int HOUR_MASK = 1024;
   static final int HOUR_OF_DAY_MASK = 2048;
   static final int MINUTE_MASK = 4096;
   static final int SECOND_MASK = 8192;
   static final int MILLISECOND_MASK = 16384;
   static final int ZONE_OFFSET_MASK = 32768;
   static final int DST_OFFSET_MASK = 65536;
   private static final String[] FIELD_NAME = new String[]{"ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH", "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", "HOUR", "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET", "DST_OFFSET"};

   protected Calendar() {
      this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
      this.sharedZone = true;
   }

   protected Calendar(TimeZone var1, Locale var2) {
      this.lenient = true;
      this.sharedZone = false;
      this.nextStamp = 2;
      this.serialVersionOnStream = 1;
      this.fields = new int[17];
      this.isSet = new boolean[17];
      this.stamp = new int[17];
      this.zone = var1;
      this.setWeekCountData(var2);
   }

   public static Calendar getInstance() {
      return createCalendar(TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
   }

   public static Calendar getInstance(TimeZone var0) {
      return createCalendar(var0, Locale.getDefault(Locale.Category.FORMAT));
   }

   public static Calendar getInstance(Locale var0) {
      return createCalendar(TimeZone.getDefault(), var0);
   }

   public static Calendar getInstance(TimeZone var0, Locale var1) {
      return createCalendar(var0, var1);
   }

   private static Calendar createCalendar(TimeZone var0, Locale var1) {
      CalendarProvider var2 = LocaleProviderAdapter.getAdapter(CalendarProvider.class, var1).getCalendarProvider();
      if (var2 != null) {
         try {
            return var2.getInstance(var0, var1);
         } catch (IllegalArgumentException var7) {
         }
      }

      Object var3 = null;
      if (var1.hasExtensions()) {
         String var4 = var1.getUnicodeLocaleType("ca");
         if (var4 != null) {
            byte var6 = -1;
            switch(var4.hashCode()) {
            case -1581060683:
               if (var4.equals("buddhist")) {
                  var6 = 0;
               }
               break;
            case -752730191:
               if (var4.equals("japanese")) {
                  var6 = 1;
               }
               break;
            case 283776265:
               if (var4.equals("gregory")) {
                  var6 = 2;
               }
            }

            switch(var6) {
            case 0:
               var3 = new BuddhistCalendar(var0, var1);
               break;
            case 1:
               var3 = new JapaneseImperialCalendar(var0, var1);
               break;
            case 2:
               var3 = new GregorianCalendar(var0, var1);
            }
         }
      }

      if (var3 == null) {
         if (var1.getLanguage() == "th" && var1.getCountry() == "TH") {
            var3 = new BuddhistCalendar(var0, var1);
         } else if (var1.getVariant() == "JP" && var1.getLanguage() == "ja" && var1.getCountry() == "JP") {
            var3 = new JapaneseImperialCalendar(var0, var1);
         } else {
            var3 = new GregorianCalendar(var0, var1);
         }
      }

      return (Calendar)var3;
   }

   public static synchronized Locale[] getAvailableLocales() {
      return DateFormat.getAvailableLocales();
   }

   protected abstract void computeTime();

   protected abstract void computeFields();

   public final Date getTime() {
      return new Date(this.getTimeInMillis());
   }

   public final void setTime(Date var1) {
      this.setTimeInMillis(var1.getTime());
   }

   public long getTimeInMillis() {
      if (!this.isTimeSet) {
         this.updateTime();
      }

      return this.time;
   }

   public void setTimeInMillis(long var1) {
      if (this.time != var1 || !this.isTimeSet || !this.areFieldsSet || !this.areAllFieldsSet || !(this.zone instanceof ZoneInfo) || ((ZoneInfo)this.zone).isDirty()) {
         this.time = var1;
         this.isTimeSet = true;
         this.areFieldsSet = false;
         this.computeFields();
         this.areAllFieldsSet = this.areFieldsSet = true;
      }
   }

   public int get(int var1) {
      this.complete();
      return this.internalGet(var1);
   }

   protected final int internalGet(int var1) {
      return this.fields[var1];
   }

   final void internalSet(int var1, int var2) {
      this.fields[var1] = var2;
   }

   public void set(int var1, int var2) {
      if (this.areFieldsSet && !this.areAllFieldsSet) {
         this.computeFields();
      }

      this.internalSet(var1, var2);
      this.isTimeSet = false;
      this.areFieldsSet = false;
      this.isSet[var1] = true;
      this.stamp[var1] = this.nextStamp++;
      if (this.nextStamp == Integer.MAX_VALUE) {
         this.adjustStamp();
      }

   }

   public final void set(int var1, int var2, int var3) {
      this.set(1, var1);
      this.set(2, var2);
      this.set(5, var3);
   }

   public final void set(int var1, int var2, int var3, int var4, int var5) {
      this.set(1, var1);
      this.set(2, var2);
      this.set(5, var3);
      this.set(11, var4);
      this.set(12, var5);
   }

   public final void set(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.set(1, var1);
      this.set(2, var2);
      this.set(5, var3);
      this.set(11, var4);
      this.set(12, var5);
      this.set(13, var6);
   }

   public final void clear() {
      for(int var1 = 0; var1 < this.fields.length; this.isSet[var1++] = false) {
         this.stamp[var1] = this.fields[var1] = 0;
      }

      this.areAllFieldsSet = this.areFieldsSet = false;
      this.isTimeSet = false;
   }

   public final void clear(int var1) {
      this.fields[var1] = 0;
      this.stamp[var1] = 0;
      this.isSet[var1] = false;
      this.areAllFieldsSet = this.areFieldsSet = false;
      this.isTimeSet = false;
   }

   public final boolean isSet(int var1) {
      return this.stamp[var1] != 0;
   }

   public String getDisplayName(int var1, int var2, Locale var3) {
      if (!this.checkDisplayNameParams(var1, var2, 1, 4, var3, 645)) {
         return null;
      } else {
         String var4 = this.getCalendarType();
         int var5 = this.get(var1);
         if (!this.isStandaloneStyle(var2) && !this.isNarrowFormatStyle(var2)) {
            DateFormatSymbols var8 = DateFormatSymbols.getInstance(var3);
            String[] var7 = this.getFieldStrings(var1, var2, var8);
            return var7 != null && var5 < var7.length ? var7[var5] : null;
         } else {
            String var6 = CalendarDataUtility.retrieveFieldValueName(var4, var1, var5, var2, var3);
            if (var6 == null) {
               if (this.isNarrowFormatStyle(var2)) {
                  var6 = CalendarDataUtility.retrieveFieldValueName(var4, var1, var5, this.toStandaloneStyle(var2), var3);
               } else if (this.isStandaloneStyle(var2)) {
                  var6 = CalendarDataUtility.retrieveFieldValueName(var4, var1, var5, this.getBaseStyle(var2), var3);
               }
            }

            return var6;
         }
      }
   }

   public Map<String, Integer> getDisplayNames(int var1, int var2, Locale var3) {
      if (!this.checkDisplayNameParams(var1, var2, 0, 4, var3, 645)) {
         return null;
      } else {
         String var4 = this.getCalendarType();
         if (var2 != 0 && !this.isStandaloneStyle(var2) && !this.isNarrowFormatStyle(var2)) {
            return this.getDisplayNamesImpl(var1, var2, var3);
         } else {
            Map var5 = CalendarDataUtility.retrieveFieldValueNames(var4, var1, var2, var3);
            if (var5 == null) {
               if (this.isNarrowFormatStyle(var2)) {
                  var5 = CalendarDataUtility.retrieveFieldValueNames(var4, var1, this.toStandaloneStyle(var2), var3);
               } else if (var2 != 0) {
                  var5 = CalendarDataUtility.retrieveFieldValueNames(var4, var1, this.getBaseStyle(var2), var3);
               }
            }

            return var5;
         }
      }
   }

   private Map<String, Integer> getDisplayNamesImpl(int var1, int var2, Locale var3) {
      DateFormatSymbols var4 = DateFormatSymbols.getInstance(var3);
      String[] var5 = this.getFieldStrings(var1, var2, var4);
      if (var5 != null) {
         HashMap var6 = new HashMap();

         for(int var7 = 0; var7 < var5.length; ++var7) {
            if (var5[var7].length() != 0) {
               var6.put(var5[var7], var7);
            }
         }

         return var6;
      } else {
         return null;
      }
   }

   boolean checkDisplayNameParams(int var1, int var2, int var3, int var4, Locale var5, int var6) {
      int var7 = this.getBaseStyle(var2);
      if (var1 >= 0 && var1 < this.fields.length && var7 >= var3 && var7 <= var4) {
         if (var5 == null) {
            throw new NullPointerException();
         } else {
            return isFieldSet(var6, var1);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private String[] getFieldStrings(int var1, int var2, DateFormatSymbols var3) {
      int var4 = this.getBaseStyle(var2);
      if (var4 == 4) {
         return null;
      } else {
         String[] var5 = null;
         switch(var1) {
         case 0:
            var5 = var3.getEras();
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
         default:
            break;
         case 2:
            var5 = var4 == 2 ? var3.getMonths() : var3.getShortMonths();
            break;
         case 7:
            var5 = var4 == 2 ? var3.getWeekdays() : var3.getShortWeekdays();
            break;
         case 9:
            var5 = var3.getAmPmStrings();
         }

         return var5;
      }
   }

   protected void complete() {
      if (!this.isTimeSet) {
         this.updateTime();
      }

      if (!this.areFieldsSet || !this.areAllFieldsSet) {
         this.computeFields();
         this.areAllFieldsSet = this.areFieldsSet = true;
      }

   }

   final boolean isExternallySet(int var1) {
      return this.stamp[var1] >= 2;
   }

   final int getSetStateFields() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.fields.length; ++var2) {
         if (this.stamp[var2] != 0) {
            var1 |= 1 << var2;
         }
      }

      return var1;
   }

   final void setFieldsComputed(int var1) {
      int var2;
      if (var1 == 131071) {
         for(var2 = 0; var2 < this.fields.length; ++var2) {
            this.stamp[var2] = 1;
            this.isSet[var2] = true;
         }

         this.areFieldsSet = this.areAllFieldsSet = true;
      } else {
         for(var2 = 0; var2 < this.fields.length; ++var2) {
            if ((var1 & 1) == 1) {
               this.stamp[var2] = 1;
               this.isSet[var2] = true;
            } else if (this.areAllFieldsSet && !this.isSet[var2]) {
               this.areAllFieldsSet = false;
            }

            var1 >>>= 1;
         }
      }

   }

   final void setFieldsNormalized(int var1) {
      if (var1 != 131071) {
         for(int var2 = 0; var2 < this.fields.length; ++var2) {
            if ((var1 & 1) == 0) {
               this.stamp[var2] = this.fields[var2] = 0;
               this.isSet[var2] = false;
            }

            var1 >>= 1;
         }
      }

      this.areFieldsSet = true;
      this.areAllFieldsSet = false;
   }

   final boolean isPartiallyNormalized() {
      return this.areFieldsSet && !this.areAllFieldsSet;
   }

   final boolean isFullyNormalized() {
      return this.areFieldsSet && this.areAllFieldsSet;
   }

   final void setUnnormalized() {
      this.areFieldsSet = this.areAllFieldsSet = false;
   }

   static boolean isFieldSet(int var0, int var1) {
      return (var0 & 1 << var1) != 0;
   }

   final int selectFields() {
      int var1 = 2;
      if (this.stamp[0] != 0) {
         var1 |= 1;
      }

      int var2 = this.stamp[7];
      int var3 = this.stamp[2];
      int var4 = this.stamp[5];
      int var5 = aggregateStamp(this.stamp[4], var2);
      int var6 = aggregateStamp(this.stamp[8], var2);
      int var7 = this.stamp[6];
      int var8 = aggregateStamp(this.stamp[3], var2);
      int var9 = var4;
      if (var5 > var4) {
         var9 = var5;
      }

      if (var6 > var9) {
         var9 = var6;
      }

      if (var7 > var9) {
         var9 = var7;
      }

      if (var8 > var9) {
         var9 = var8;
      }

      if (var9 == 0) {
         var5 = this.stamp[4];
         var6 = Math.max(this.stamp[8], var2);
         var8 = this.stamp[3];
         var9 = Math.max(Math.max(var5, var6), var8);
         if (var9 == 0) {
            var4 = var3;
            var9 = var3;
         }
      }

      if (var9 != var4 && (var9 != var5 || this.stamp[4] < this.stamp[3]) && (var9 != var6 || this.stamp[8] < this.stamp[3])) {
         assert var9 == var7 || var9 == var8 || var9 == 0;

         if (var9 == var7) {
            var1 |= 64;
         } else {
            assert var9 == var8;

            if (var2 != 0) {
               var1 |= 128;
            }

            var1 |= 8;
         }
      } else {
         var1 |= 4;
         if (var9 == var4) {
            var1 |= 32;
         } else {
            assert var9 == var5 || var9 == var6;

            if (var2 != 0) {
               var1 |= 128;
            }

            if (var5 == var6) {
               if (this.stamp[4] >= this.stamp[8]) {
                  var1 |= 16;
               } else {
                  var1 |= 256;
               }
            } else if (var9 == var5) {
               var1 |= 16;
            } else {
               assert var9 == var6;

               if (this.stamp[8] != 0) {
                  var1 |= 256;
               }
            }
         }
      }

      int var10 = this.stamp[11];
      int var11 = aggregateStamp(this.stamp[10], this.stamp[9]);
      var9 = var11 > var10 ? var11 : var10;
      if (var9 == 0) {
         var9 = Math.max(this.stamp[10], this.stamp[9]);
      }

      if (var9 != 0) {
         if (var9 == var10) {
            var1 |= 2048;
         } else {
            var1 |= 1024;
            if (this.stamp[9] != 0) {
               var1 |= 512;
            }
         }
      }

      if (this.stamp[12] != 0) {
         var1 |= 4096;
      }

      if (this.stamp[13] != 0) {
         var1 |= 8192;
      }

      if (this.stamp[14] != 0) {
         var1 |= 16384;
      }

      if (this.stamp[15] >= 2) {
         var1 |= 32768;
      }

      if (this.stamp[16] >= 2) {
         var1 |= 65536;
      }

      return var1;
   }

   int getBaseStyle(int var1) {
      return var1 & -32769;
   }

   private int toStandaloneStyle(int var1) {
      return var1 | '耀';
   }

   private boolean isStandaloneStyle(int var1) {
      return (var1 & '耀') != 0;
   }

   private boolean isNarrowStyle(int var1) {
      return var1 == 4 || var1 == 32772;
   }

   private boolean isNarrowFormatStyle(int var1) {
      return var1 == 4;
   }

   private static int aggregateStamp(int var0, int var1) {
      if (var0 != 0 && var1 != 0) {
         return var0 > var1 ? var0 : var1;
      } else {
         return 0;
      }
   }

   public static Set<String> getAvailableCalendarTypes() {
      return Calendar.AvailableCalendarTypes.SET;
   }

   public String getCalendarType() {
      return this.getClass().getName();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         try {
            Calendar var2 = (Calendar)var1;
            return this.compareTo(getMillisOf(var2)) == 0 && this.lenient == var2.lenient && this.firstDayOfWeek == var2.firstDayOfWeek && this.minimalDaysInFirstWeek == var2.minimalDaysInFirstWeek && this.zone.equals(var2.zone);
         } catch (Exception var3) {
            return false;
         }
      }
   }

   public int hashCode() {
      int var1 = (this.lenient ? 1 : 0) | this.firstDayOfWeek << 1 | this.minimalDaysInFirstWeek << 4 | this.zone.hashCode() << 7;
      long var2 = getMillisOf(this);
      return (int)var2 ^ (int)(var2 >> 32) ^ var1;
   }

   public boolean before(Object var1) {
      return var1 instanceof Calendar && this.compareTo((Calendar)var1) < 0;
   }

   public boolean after(Object var1) {
      return var1 instanceof Calendar && this.compareTo((Calendar)var1) > 0;
   }

   public int compareTo(Calendar var1) {
      return this.compareTo(getMillisOf(var1));
   }

   public abstract void add(int var1, int var2);

   public abstract void roll(int var1, boolean var2);

   public void roll(int var1, int var2) {
      while(var2 > 0) {
         this.roll(var1, true);
         --var2;
      }

      while(var2 < 0) {
         this.roll(var1, false);
         ++var2;
      }

   }

   public void setTimeZone(TimeZone var1) {
      this.zone = var1;
      this.sharedZone = false;
      this.areAllFieldsSet = this.areFieldsSet = false;
   }

   public TimeZone getTimeZone() {
      if (this.sharedZone) {
         this.zone = (TimeZone)this.zone.clone();
         this.sharedZone = false;
      }

      return this.zone;
   }

   TimeZone getZone() {
      return this.zone;
   }

   void setZoneShared(boolean var1) {
      this.sharedZone = var1;
   }

   public void setLenient(boolean var1) {
      this.lenient = var1;
   }

   public boolean isLenient() {
      return this.lenient;
   }

   public void setFirstDayOfWeek(int var1) {
      if (this.firstDayOfWeek != var1) {
         this.firstDayOfWeek = var1;
         this.invalidateWeekFields();
      }
   }

   public int getFirstDayOfWeek() {
      return this.firstDayOfWeek;
   }

   public void setMinimalDaysInFirstWeek(int var1) {
      if (this.minimalDaysInFirstWeek != var1) {
         this.minimalDaysInFirstWeek = var1;
         this.invalidateWeekFields();
      }
   }

   public int getMinimalDaysInFirstWeek() {
      return this.minimalDaysInFirstWeek;
   }

   public boolean isWeekDateSupported() {
      return false;
   }

   public int getWeekYear() {
      throw new UnsupportedOperationException();
   }

   public void setWeekDate(int var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   public int getWeeksInWeekYear() {
      throw new UnsupportedOperationException();
   }

   public abstract int getMinimum(int var1);

   public abstract int getMaximum(int var1);

   public abstract int getGreatestMinimum(int var1);

   public abstract int getLeastMaximum(int var1);

   public int getActualMinimum(int var1) {
      int var2 = this.getGreatestMinimum(var1);
      int var3 = this.getMinimum(var1);
      if (var2 == var3) {
         return var2;
      } else {
         Calendar var4 = (Calendar)this.clone();
         var4.setLenient(true);
         int var5 = var2;

         do {
            var4.set(var1, var2);
            if (var4.get(var1) != var2) {
               break;
            }

            var5 = var2--;
         } while(var2 >= var3);

         return var5;
      }
   }

   public int getActualMaximum(int var1) {
      int var2 = this.getLeastMaximum(var1);
      int var3 = this.getMaximum(var1);
      if (var2 == var3) {
         return var2;
      } else {
         Calendar var4 = (Calendar)this.clone();
         var4.setLenient(true);
         if (var1 == 3 || var1 == 4) {
            var4.set(7, this.firstDayOfWeek);
         }

         int var5 = var2;

         do {
            var4.set(var1, var2);
            if (var4.get(var1) != var2) {
               break;
            }

            var5 = var2++;
         } while(var2 <= var3);

         return var5;
      }
   }

   public Object clone() {
      try {
         Calendar var1 = (Calendar)super.clone();
         var1.fields = new int[17];
         var1.isSet = new boolean[17];
         var1.stamp = new int[17];

         for(int var2 = 0; var2 < 17; ++var2) {
            var1.fields[var2] = this.fields[var2];
            var1.stamp[var2] = this.stamp[var2];
            var1.isSet[var2] = this.isSet[var2];
         }

         var1.zone = (TimeZone)this.zone.clone();
         return var1;
      } catch (CloneNotSupportedException var3) {
         throw new InternalError(var3);
      }
   }

   static String getFieldName(int var0) {
      return FIELD_NAME[var0];
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(800);
      var1.append(this.getClass().getName()).append('[');
      appendValue(var1, "time", this.isTimeSet, this.time);
      var1.append(",areFieldsSet=").append(this.areFieldsSet);
      var1.append(",areAllFieldsSet=").append(this.areAllFieldsSet);
      var1.append(",lenient=").append(this.lenient);
      var1.append(",zone=").append((Object)this.zone);
      appendValue(var1, ",firstDayOfWeek", true, (long)this.firstDayOfWeek);
      appendValue(var1, ",minimalDaysInFirstWeek", true, (long)this.minimalDaysInFirstWeek);

      for(int var2 = 0; var2 < 17; ++var2) {
         var1.append(',');
         appendValue(var1, FIELD_NAME[var2], this.isSet(var2), (long)this.fields[var2]);
      }

      var1.append(']');
      return var1.toString();
   }

   private static void appendValue(StringBuilder var0, String var1, boolean var2, long var3) {
      var0.append(var1).append('=');
      if (var2) {
         var0.append(var3);
      } else {
         var0.append('?');
      }

   }

   private void setWeekCountData(Locale var1) {
      int[] var2 = (int[])cachedLocaleData.get(var1);
      if (var2 == null) {
         var2 = new int[]{CalendarDataUtility.retrieveFirstDayOfWeek(var1), CalendarDataUtility.retrieveMinimalDaysInFirstWeek(var1)};
         cachedLocaleData.putIfAbsent(var1, var2);
      }

      this.firstDayOfWeek = var2[0];
      this.minimalDaysInFirstWeek = var2[1];
   }

   private void updateTime() {
      this.computeTime();
      this.isTimeSet = true;
   }

   private int compareTo(long var1) {
      long var3 = getMillisOf(this);
      return var3 > var1 ? 1 : (var3 == var1 ? 0 : -1);
   }

   private static long getMillisOf(Calendar var0) {
      if (var0.isTimeSet) {
         return var0.time;
      } else {
         Calendar var1 = (Calendar)var0.clone();
         var1.setLenient(true);
         return var1.getTimeInMillis();
      }
   }

   private void adjustStamp() {
      int var1 = 2;
      int var2 = 2;

      int var3;
      do {
         var3 = Integer.MAX_VALUE;

         int var4;
         for(var4 = 0; var4 < this.stamp.length; ++var4) {
            int var5 = this.stamp[var4];
            if (var5 >= var2 && var3 > var5) {
               var3 = var5;
            }

            if (var1 < var5) {
               var1 = var5;
            }
         }

         if (var1 != var3 && var3 == Integer.MAX_VALUE) {
            break;
         }

         for(var4 = 0; var4 < this.stamp.length; ++var4) {
            if (this.stamp[var4] == var3) {
               this.stamp[var4] = var2;
            }
         }

         ++var2;
      } while(var3 != var1);

      this.nextStamp = var2;
   }

   private void invalidateWeekFields() {
      if (this.stamp[4] == 1 || this.stamp[3] == 1) {
         Calendar var1 = (Calendar)this.clone();
         var1.setLenient(true);
         var1.clear(4);
         var1.clear(3);
         int var2;
         if (this.stamp[4] == 1) {
            var2 = var1.get(4);
            if (this.fields[4] != var2) {
               this.fields[4] = var2;
            }
         }

         if (this.stamp[3] == 1) {
            var2 = var1.get(3);
            if (this.fields[3] != var2) {
               this.fields[3] = var2;
            }
         }

      }
   }

   private synchronized void writeObject(ObjectOutputStream var1) throws IOException {
      if (!this.isTimeSet) {
         try {
            this.updateTime();
         } catch (IllegalArgumentException var4) {
         }
      }

      TimeZone var2 = null;
      if (this.zone instanceof ZoneInfo) {
         SimpleTimeZone var3 = ((ZoneInfo)this.zone).getLastRuleInstance();
         if (var3 == null) {
            var3 = new SimpleTimeZone(this.zone.getRawOffset(), this.zone.getID());
         }

         var2 = this.zone;
         this.zone = var3;
      }

      var1.defaultWriteObject();
      var1.writeObject(var2);
      if (var2 != null) {
         this.zone = var2;
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      final ObjectInputStream var2 = var1;
      var1.defaultReadObject();
      this.stamp = new int[17];
      if (this.serialVersionOnStream >= 2) {
         this.isTimeSet = true;
         if (this.fields == null) {
            this.fields = new int[17];
         }

         if (this.isSet == null) {
            this.isSet = new boolean[17];
         }
      } else if (this.serialVersionOnStream >= 0) {
         for(int var3 = 0; var3 < 17; ++var3) {
            this.stamp[var3] = this.isSet[var3] ? 1 : 0;
         }
      }

      this.serialVersionOnStream = 1;
      ZoneInfo var7 = null;

      try {
         var7 = (ZoneInfo)AccessController.doPrivileged(new PrivilegedExceptionAction<ZoneInfo>() {
            public ZoneInfo run() throws Exception {
               return (ZoneInfo)var2.readObject();
            }
         }, Calendar.CalendarAccessControlContext.INSTANCE);
      } catch (PrivilegedActionException var6) {
         Exception var5 = var6.getException();
         if (!(var5 instanceof OptionalDataException)) {
            if (var5 instanceof RuntimeException) {
               throw (RuntimeException)var5;
            }

            if (var5 instanceof IOException) {
               throw (IOException)var5;
            }

            if (var5 instanceof ClassNotFoundException) {
               throw (ClassNotFoundException)var5;
            }

            throw new RuntimeException(var5);
         }
      }

      if (var7 != null) {
         this.zone = var7;
      }

      if (this.zone instanceof SimpleTimeZone) {
         String var4 = this.zone.getID();
         TimeZone var8 = TimeZone.getTimeZone(var4);
         if (var8 != null && var8.hasSameRules(this.zone) && var8.getID().equals(var4)) {
            this.zone = var8;
         }
      }

   }

   public final Instant toInstant() {
      return Instant.ofEpochMilli(this.getTimeInMillis());
   }

   private static class CalendarAccessControlContext {
      private static final AccessControlContext INSTANCE;

      static {
         RuntimePermission var0 = new RuntimePermission("accessClassInPackage.sun.util.calendar");
         PermissionCollection var1 = var0.newPermissionCollection();
         var1.add(var0);
         INSTANCE = new AccessControlContext(new ProtectionDomain[]{new ProtectionDomain((CodeSource)null, var1)});
      }
   }

   private static class AvailableCalendarTypes {
      private static final Set<String> SET;

      static {
         HashSet var0 = new HashSet(3);
         var0.add("gregory");
         var0.add("buddhist");
         var0.add("japanese");
         SET = Collections.unmodifiableSet(var0);
      }
   }

   public static class Builder {
      private static final int NFIELDS = 18;
      private static final int WEEK_YEAR = 17;
      private long instant;
      private int[] fields;
      private int nextStamp;
      private int maxFieldIndex;
      private String type;
      private TimeZone zone;
      private boolean lenient = true;
      private Locale locale;
      private int firstDayOfWeek;
      private int minimalDaysInFirstWeek;

      public Calendar.Builder setInstant(long var1) {
         if (this.fields != null) {
            throw new IllegalStateException();
         } else {
            this.instant = var1;
            this.nextStamp = 1;
            return this;
         }
      }

      public Calendar.Builder setInstant(Date var1) {
         return this.setInstant(var1.getTime());
      }

      public Calendar.Builder set(int var1, int var2) {
         if (var1 >= 0 && var1 < 17) {
            if (this.isInstantSet()) {
               throw new IllegalStateException("instant has been set");
            } else {
               this.allocateFields();
               this.internalSet(var1, var2);
               return this;
            }
         } else {
            throw new IllegalArgumentException("field is invalid");
         }
      }

      public Calendar.Builder setFields(int... var1) {
         int var2 = var1.length;
         if (var2 % 2 != 0) {
            throw new IllegalArgumentException();
         } else if (this.isInstantSet()) {
            throw new IllegalStateException("instant has been set");
         } else if (this.nextStamp + var2 / 2 < 0) {
            throw new IllegalStateException("stamp counter overflow");
         } else {
            this.allocateFields();
            int var3 = 0;

            while(var3 < var2) {
               int var4 = var1[var3++];
               if (var4 < 0 || var4 >= 17) {
                  throw new IllegalArgumentException("field is invalid");
               }

               this.internalSet(var4, var1[var3++]);
            }

            return this;
         }
      }

      public Calendar.Builder setDate(int var1, int var2, int var3) {
         return this.setFields(1, var1, 2, var2, 5, var3);
      }

      public Calendar.Builder setTimeOfDay(int var1, int var2, int var3) {
         return this.setTimeOfDay(var1, var2, var3, 0);
      }

      public Calendar.Builder setTimeOfDay(int var1, int var2, int var3, int var4) {
         return this.setFields(11, var1, 12, var2, 13, var3, 14, var4);
      }

      public Calendar.Builder setWeekDate(int var1, int var2, int var3) {
         this.allocateFields();
         this.internalSet(17, var1);
         this.internalSet(3, var2);
         this.internalSet(7, var3);
         return this;
      }

      public Calendar.Builder setTimeZone(TimeZone var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.zone = var1;
            return this;
         }
      }

      public Calendar.Builder setLenient(boolean var1) {
         this.lenient = var1;
         return this;
      }

      public Calendar.Builder setCalendarType(String var1) {
         if (var1.equals("gregorian")) {
            var1 = "gregory";
         }

         if (!Calendar.getAvailableCalendarTypes().contains(var1) && !var1.equals("iso8601")) {
            throw new IllegalArgumentException("unknown calendar type: " + var1);
         } else {
            if (this.type == null) {
               this.type = var1;
            } else if (!this.type.equals(var1)) {
               throw new IllegalStateException("calendar type override");
            }

            return this;
         }
      }

      public Calendar.Builder setLocale(Locale var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.locale = var1;
            return this;
         }
      }

      public Calendar.Builder setWeekDefinition(int var1, int var2) {
         if (this.isValidWeekParameter(var1) && this.isValidWeekParameter(var2)) {
            this.firstDayOfWeek = var1;
            this.minimalDaysInFirstWeek = var2;
            return this;
         } else {
            throw new IllegalArgumentException();
         }
      }

      public Calendar build() {
         if (this.locale == null) {
            this.locale = Locale.getDefault();
         }

         if (this.zone == null) {
            this.zone = TimeZone.getDefault();
         }

         if (this.type == null) {
            this.type = this.locale.getUnicodeLocaleType("ca");
         }

         if (this.type == null) {
            if (this.locale.getCountry() == "TH" && this.locale.getLanguage() == "th") {
               this.type = "buddhist";
            } else {
               this.type = "gregory";
            }
         }

         String var2 = this.type;
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -1581060683:
            if (var2.equals("buddhist")) {
               var3 = 2;
            }
            break;
         case -752730191:
            if (var2.equals("japanese")) {
               var3 = 3;
            }
            break;
         case 283776265:
            if (var2.equals("gregory")) {
               var3 = 0;
            }
            break;
         case 2095190916:
            if (var2.equals("iso8601")) {
               var3 = 1;
            }
         }

         Object var1;
         switch(var3) {
         case 0:
            var1 = new GregorianCalendar(this.zone, this.locale, true);
            break;
         case 1:
            GregorianCalendar var4 = new GregorianCalendar(this.zone, this.locale, true);
            var4.setGregorianChange(new Date(Long.MIN_VALUE));
            this.setWeekDefinition(2, 4);
            var1 = var4;
            break;
         case 2:
            var1 = new BuddhistCalendar(this.zone, this.locale);
            ((Calendar)var1).clear();
            break;
         case 3:
            var1 = new JapaneseImperialCalendar(this.zone, this.locale, true);
            break;
         default:
            throw new IllegalArgumentException("unknown calendar type: " + this.type);
         }

         ((Calendar)var1).setLenient(this.lenient);
         if (this.firstDayOfWeek != 0) {
            ((Calendar)var1).setFirstDayOfWeek(this.firstDayOfWeek);
            ((Calendar)var1).setMinimalDaysInFirstWeek(this.minimalDaysInFirstWeek);
         }

         if (this.isInstantSet()) {
            ((Calendar)var1).setTimeInMillis(this.instant);
            ((Calendar)var1).complete();
            return (Calendar)var1;
         } else {
            if (this.fields != null) {
               boolean var5 = this.isSet(17) && this.fields[17] > this.fields[1];
               if (var5 && !((Calendar)var1).isWeekDateSupported()) {
                  throw new IllegalArgumentException("week date is unsupported by " + this.type);
               }

               int var6;
               int var7;
               for(var7 = 2; var7 < this.nextStamp; ++var7) {
                  for(var6 = 0; var6 <= this.maxFieldIndex; ++var6) {
                     if (this.fields[var6] == var7) {
                        ((Calendar)var1).set(var6, this.fields[18 + var6]);
                        break;
                     }
                  }
               }

               if (var5) {
                  var7 = this.isSet(3) ? this.fields[21] : 1;
                  var6 = this.isSet(7) ? this.fields[25] : ((Calendar)var1).getFirstDayOfWeek();
                  ((Calendar)var1).setWeekDate(this.fields[35], var7, var6);
               }

               ((Calendar)var1).complete();
            }

            return (Calendar)var1;
         }
      }

      private void allocateFields() {
         if (this.fields == null) {
            this.fields = new int[36];
            this.nextStamp = 2;
            this.maxFieldIndex = -1;
         }

      }

      private void internalSet(int var1, int var2) {
         this.fields[var1] = this.nextStamp++;
         if (this.nextStamp < 0) {
            throw new IllegalStateException("stamp counter overflow");
         } else {
            this.fields[18 + var1] = var2;
            if (var1 > this.maxFieldIndex && var1 < 17) {
               this.maxFieldIndex = var1;
            }

         }
      }

      private boolean isInstantSet() {
         return this.nextStamp == 1;
      }

      private boolean isSet(int var1) {
         return this.fields != null && this.fields[var1] > 0;
      }

      private boolean isValidWeekParameter(int var1) {
         return var1 > 0 && var1 <= 7;
      }
   }
}
