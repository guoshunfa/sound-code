package java.text;

import java.io.InvalidObjectException;
import java.text.spi.DateFormatProvider;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class DateFormat extends Format {
   protected Calendar calendar;
   protected NumberFormat numberFormat;
   public static final int ERA_FIELD = 0;
   public static final int YEAR_FIELD = 1;
   public static final int MONTH_FIELD = 2;
   public static final int DATE_FIELD = 3;
   public static final int HOUR_OF_DAY1_FIELD = 4;
   public static final int HOUR_OF_DAY0_FIELD = 5;
   public static final int MINUTE_FIELD = 6;
   public static final int SECOND_FIELD = 7;
   public static final int MILLISECOND_FIELD = 8;
   public static final int DAY_OF_WEEK_FIELD = 9;
   public static final int DAY_OF_YEAR_FIELD = 10;
   public static final int DAY_OF_WEEK_IN_MONTH_FIELD = 11;
   public static final int WEEK_OF_YEAR_FIELD = 12;
   public static final int WEEK_OF_MONTH_FIELD = 13;
   public static final int AM_PM_FIELD = 14;
   public static final int HOUR1_FIELD = 15;
   public static final int HOUR0_FIELD = 16;
   public static final int TIMEZONE_FIELD = 17;
   private static final long serialVersionUID = 7218322306649953788L;
   public static final int FULL = 0;
   public static final int LONG = 1;
   public static final int MEDIUM = 2;
   public static final int SHORT = 3;
   public static final int DEFAULT = 2;

   public final StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3) {
      if (var1 instanceof Date) {
         return this.format((Date)var1, var2, var3);
      } else if (var1 instanceof Number) {
         return this.format(new Date(((Number)var1).longValue()), var2, var3);
      } else {
         throw new IllegalArgumentException("Cannot format given Object as a Date");
      }
   }

   public abstract StringBuffer format(Date var1, StringBuffer var2, FieldPosition var3);

   public final String format(Date var1) {
      return this.format(var1, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString();
   }

   public Date parse(String var1) throws ParseException {
      ParsePosition var2 = new ParsePosition(0);
      Date var3 = this.parse(var1, var2);
      if (var2.index == 0) {
         throw new ParseException("Unparseable date: \"" + var1 + "\"", var2.errorIndex);
      } else {
         return var3;
      }
   }

   public abstract Date parse(String var1, ParsePosition var2);

   public Object parseObject(String var1, ParsePosition var2) {
      return this.parse(var1, var2);
   }

   public static final DateFormat getTimeInstance() {
      return get(2, 0, 1, Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DateFormat getTimeInstance(int var0) {
      return get(var0, 0, 1, Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DateFormat getTimeInstance(int var0, Locale var1) {
      return get(var0, 0, 1, var1);
   }

   public static final DateFormat getDateInstance() {
      return get(0, 2, 2, Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DateFormat getDateInstance(int var0) {
      return get(0, var0, 2, Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DateFormat getDateInstance(int var0, Locale var1) {
      return get(0, var0, 2, var1);
   }

   public static final DateFormat getDateTimeInstance() {
      return get(2, 2, 3, Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DateFormat getDateTimeInstance(int var0, int var1) {
      return get(var1, var0, 3, Locale.getDefault(Locale.Category.FORMAT));
   }

   public static final DateFormat getDateTimeInstance(int var0, int var1, Locale var2) {
      return get(var1, var0, 3, var2);
   }

   public static final DateFormat getInstance() {
      return getDateTimeInstance(3, 3);
   }

   public static Locale[] getAvailableLocales() {
      LocaleServiceProviderPool var0 = LocaleServiceProviderPool.getPool(DateFormatProvider.class);
      return var0.getAvailableLocales();
   }

   public void setCalendar(Calendar var1) {
      this.calendar = var1;
   }

   public Calendar getCalendar() {
      return this.calendar;
   }

   public void setNumberFormat(NumberFormat var1) {
      this.numberFormat = var1;
   }

   public NumberFormat getNumberFormat() {
      return this.numberFormat;
   }

   public void setTimeZone(TimeZone var1) {
      this.calendar.setTimeZone(var1);
   }

   public TimeZone getTimeZone() {
      return this.calendar.getTimeZone();
   }

   public void setLenient(boolean var1) {
      this.calendar.setLenient(var1);
   }

   public boolean isLenient() {
      return this.calendar.isLenient();
   }

   public int hashCode() {
      return this.numberFormat.hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         DateFormat var2 = (DateFormat)var1;
         return this.calendar.getFirstDayOfWeek() == var2.calendar.getFirstDayOfWeek() && this.calendar.getMinimalDaysInFirstWeek() == var2.calendar.getMinimalDaysInFirstWeek() && this.calendar.isLenient() == var2.calendar.isLenient() && this.calendar.getTimeZone().equals(var2.calendar.getTimeZone()) && this.numberFormat.equals(var2.numberFormat);
      } else {
         return false;
      }
   }

   public Object clone() {
      DateFormat var1 = (DateFormat)super.clone();
      var1.calendar = (Calendar)this.calendar.clone();
      var1.numberFormat = (NumberFormat)this.numberFormat.clone();
      return var1;
   }

   private static DateFormat get(int var0, int var1, int var2, Locale var3) {
      if ((var2 & 1) != 0) {
         if (var0 < 0 || var0 > 3) {
            throw new IllegalArgumentException("Illegal time style " + var0);
         }
      } else {
         var0 = -1;
      }

      if ((var2 & 2) != 0) {
         if (var1 < 0 || var1 > 3) {
            throw new IllegalArgumentException("Illegal date style " + var1);
         }
      } else {
         var1 = -1;
      }

      LocaleProviderAdapter var4 = LocaleProviderAdapter.getAdapter(DateFormatProvider.class, var3);
      DateFormat var5 = get(var4, var0, var1, var3);
      if (var5 == null) {
         var5 = get(LocaleProviderAdapter.forJRE(), var0, var1, var3);
      }

      return var5;
   }

   private static DateFormat get(LocaleProviderAdapter var0, int var1, int var2, Locale var3) {
      DateFormatProvider var4 = var0.getDateFormatProvider();
      DateFormat var5;
      if (var1 == -1) {
         var5 = var4.getDateInstance(var2, var3);
      } else if (var2 == -1) {
         var5 = var4.getTimeInstance(var1, var3);
      } else {
         var5 = var4.getDateTimeInstance(var2, var1, var3);
      }

      return var5;
   }

   protected DateFormat() {
   }

   public static class Field extends Format.Field {
      private static final long serialVersionUID = 7441350119349544720L;
      private static final Map<String, DateFormat.Field> instanceMap = new HashMap(18);
      private static final DateFormat.Field[] calendarToFieldMapping = new DateFormat.Field[17];
      private int calendarField;
      public static final DateFormat.Field ERA = new DateFormat.Field("era", 0);
      public static final DateFormat.Field YEAR = new DateFormat.Field("year", 1);
      public static final DateFormat.Field MONTH = new DateFormat.Field("month", 2);
      public static final DateFormat.Field DAY_OF_MONTH = new DateFormat.Field("day of month", 5);
      public static final DateFormat.Field HOUR_OF_DAY1 = new DateFormat.Field("hour of day 1", -1);
      public static final DateFormat.Field HOUR_OF_DAY0 = new DateFormat.Field("hour of day", 11);
      public static final DateFormat.Field MINUTE = new DateFormat.Field("minute", 12);
      public static final DateFormat.Field SECOND = new DateFormat.Field("second", 13);
      public static final DateFormat.Field MILLISECOND = new DateFormat.Field("millisecond", 14);
      public static final DateFormat.Field DAY_OF_WEEK = new DateFormat.Field("day of week", 7);
      public static final DateFormat.Field DAY_OF_YEAR = new DateFormat.Field("day of year", 6);
      public static final DateFormat.Field DAY_OF_WEEK_IN_MONTH = new DateFormat.Field("day of week in month", 8);
      public static final DateFormat.Field WEEK_OF_YEAR = new DateFormat.Field("week of year", 3);
      public static final DateFormat.Field WEEK_OF_MONTH = new DateFormat.Field("week of month", 4);
      public static final DateFormat.Field AM_PM = new DateFormat.Field("am pm", 9);
      public static final DateFormat.Field HOUR1 = new DateFormat.Field("hour 1", -1);
      public static final DateFormat.Field HOUR0 = new DateFormat.Field("hour", 10);
      public static final DateFormat.Field TIME_ZONE = new DateFormat.Field("time zone", -1);

      public static DateFormat.Field ofCalendarField(int var0) {
         if (var0 >= 0 && var0 < calendarToFieldMapping.length) {
            return calendarToFieldMapping[var0];
         } else {
            throw new IllegalArgumentException("Unknown Calendar constant " + var0);
         }
      }

      protected Field(String var1, int var2) {
         super(var1);
         this.calendarField = var2;
         if (this.getClass() == DateFormat.Field.class) {
            instanceMap.put(var1, this);
            if (var2 >= 0) {
               calendarToFieldMapping[var2] = this;
            }
         }

      }

      public int getCalendarField() {
         return this.calendarField;
      }

      protected Object readResolve() throws InvalidObjectException {
         if (this.getClass() != DateFormat.Field.class) {
            throw new InvalidObjectException("subclass didn't correctly implement readResolve");
         } else {
            Object var1 = instanceMap.get(this.getName());
            if (var1 != null) {
               return var1;
            } else {
               throw new InvalidObjectException("unknown attribute name");
            }
         }
      }
   }
}
