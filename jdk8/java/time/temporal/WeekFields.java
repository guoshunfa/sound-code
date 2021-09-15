package java.time.temporal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public final class WeekFields implements Serializable {
   private static final ConcurrentMap<String, WeekFields> CACHE = new ConcurrentHashMap(4, 0.75F, 2);
   public static final WeekFields ISO;
   public static final WeekFields SUNDAY_START;
   public static final TemporalUnit WEEK_BASED_YEARS;
   private static final long serialVersionUID = -1177360819670808121L;
   private final DayOfWeek firstDayOfWeek;
   private final int minimalDays;
   private final transient TemporalField dayOfWeek = WeekFields.ComputedDayOfField.ofDayOfWeekField(this);
   private final transient TemporalField weekOfMonth = WeekFields.ComputedDayOfField.ofWeekOfMonthField(this);
   private final transient TemporalField weekOfYear = WeekFields.ComputedDayOfField.ofWeekOfYearField(this);
   private final transient TemporalField weekOfWeekBasedYear = WeekFields.ComputedDayOfField.ofWeekOfWeekBasedYearField(this);
   private final transient TemporalField weekBasedYear = WeekFields.ComputedDayOfField.ofWeekBasedYearField(this);

   public static WeekFields of(Locale var0) {
      Objects.requireNonNull(var0, (String)"locale");
      var0 = new Locale(var0.getLanguage(), var0.getCountry());
      int var1 = CalendarDataUtility.retrieveFirstDayOfWeek(var0);
      DayOfWeek var2 = DayOfWeek.SUNDAY.plus((long)(var1 - 1));
      int var3 = CalendarDataUtility.retrieveMinimalDaysInFirstWeek(var0);
      return of(var2, var3);
   }

   public static WeekFields of(DayOfWeek var0, int var1) {
      String var2 = var0.toString() + var1;
      WeekFields var3 = (WeekFields)CACHE.get(var2);
      if (var3 == null) {
         var3 = new WeekFields(var0, var1);
         CACHE.putIfAbsent(var2, var3);
         var3 = (WeekFields)CACHE.get(var2);
      }

      return var3;
   }

   private WeekFields(DayOfWeek var1, int var2) {
      Objects.requireNonNull(var1, (String)"firstDayOfWeek");
      if (var2 >= 1 && var2 <= 7) {
         this.firstDayOfWeek = var1;
         this.minimalDays = var2;
      } else {
         throw new IllegalArgumentException("Minimal number of days is invalid");
      }
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException, InvalidObjectException {
      var1.defaultReadObject();
      if (this.firstDayOfWeek == null) {
         throw new InvalidObjectException("firstDayOfWeek is null");
      } else if (this.minimalDays < 1 || this.minimalDays > 7) {
         throw new InvalidObjectException("Minimal number of days is invalid");
      }
   }

   private Object readResolve() throws InvalidObjectException {
      try {
         return of(this.firstDayOfWeek, this.minimalDays);
      } catch (IllegalArgumentException var2) {
         throw new InvalidObjectException("Invalid serialized WeekFields: " + var2.getMessage());
      }
   }

   public DayOfWeek getFirstDayOfWeek() {
      return this.firstDayOfWeek;
   }

   public int getMinimalDaysInFirstWeek() {
      return this.minimalDays;
   }

   public TemporalField dayOfWeek() {
      return this.dayOfWeek;
   }

   public TemporalField weekOfMonth() {
      return this.weekOfMonth;
   }

   public TemporalField weekOfYear() {
      return this.weekOfYear;
   }

   public TemporalField weekOfWeekBasedYear() {
      return this.weekOfWeekBasedYear;
   }

   public TemporalField weekBasedYear() {
      return this.weekBasedYear;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof WeekFields) {
         return this.hashCode() == var1.hashCode();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.firstDayOfWeek.ordinal() * 7 + this.minimalDays;
   }

   public String toString() {
      return "WeekFields[" + this.firstDayOfWeek + ',' + this.minimalDays + ']';
   }

   static {
      ISO = new WeekFields(DayOfWeek.MONDAY, 4);
      SUNDAY_START = of(DayOfWeek.SUNDAY, 1);
      WEEK_BASED_YEARS = IsoFields.WEEK_BASED_YEARS;
   }

   static class ComputedDayOfField implements TemporalField {
      private final String name;
      private final WeekFields weekDef;
      private final TemporalUnit baseUnit;
      private final TemporalUnit rangeUnit;
      private final ValueRange range;
      private static final ValueRange DAY_OF_WEEK_RANGE = ValueRange.of(1L, 7L);
      private static final ValueRange WEEK_OF_MONTH_RANGE = ValueRange.of(0L, 1L, 4L, 6L);
      private static final ValueRange WEEK_OF_YEAR_RANGE = ValueRange.of(0L, 1L, 52L, 54L);
      private static final ValueRange WEEK_OF_WEEK_BASED_YEAR_RANGE = ValueRange.of(1L, 52L, 53L);

      static WeekFields.ComputedDayOfField ofDayOfWeekField(WeekFields var0) {
         return new WeekFields.ComputedDayOfField("DayOfWeek", var0, ChronoUnit.DAYS, ChronoUnit.WEEKS, DAY_OF_WEEK_RANGE);
      }

      static WeekFields.ComputedDayOfField ofWeekOfMonthField(WeekFields var0) {
         return new WeekFields.ComputedDayOfField("WeekOfMonth", var0, ChronoUnit.WEEKS, ChronoUnit.MONTHS, WEEK_OF_MONTH_RANGE);
      }

      static WeekFields.ComputedDayOfField ofWeekOfYearField(WeekFields var0) {
         return new WeekFields.ComputedDayOfField("WeekOfYear", var0, ChronoUnit.WEEKS, ChronoUnit.YEARS, WEEK_OF_YEAR_RANGE);
      }

      static WeekFields.ComputedDayOfField ofWeekOfWeekBasedYearField(WeekFields var0) {
         return new WeekFields.ComputedDayOfField("WeekOfWeekBasedYear", var0, ChronoUnit.WEEKS, IsoFields.WEEK_BASED_YEARS, WEEK_OF_WEEK_BASED_YEAR_RANGE);
      }

      static WeekFields.ComputedDayOfField ofWeekBasedYearField(WeekFields var0) {
         return new WeekFields.ComputedDayOfField("WeekBasedYear", var0, IsoFields.WEEK_BASED_YEARS, ChronoUnit.FOREVER, ChronoField.YEAR.range());
      }

      private ChronoLocalDate ofWeekBasedYear(Chronology var1, int var2, int var3, int var4) {
         ChronoLocalDate var5 = var1.date(var2, 1, 1);
         int var6 = this.localizedDayOfWeek(var5);
         int var7 = this.startOfWeekOffset(1, var6);
         int var8 = var5.lengthOfYear();
         int var9 = this.computeWeek(var7, var8 + this.weekDef.getMinimalDaysInFirstWeek());
         var3 = Math.min(var3, var9 - 1);
         int var10 = -var7 + (var4 - 1) + (var3 - 1) * 7;
         return var5.plus((long)var10, ChronoUnit.DAYS);
      }

      private ComputedDayOfField(String var1, WeekFields var2, TemporalUnit var3, TemporalUnit var4, ValueRange var5) {
         this.name = var1;
         this.weekDef = var2;
         this.baseUnit = var3;
         this.rangeUnit = var4;
         this.range = var5;
      }

      public long getFrom(TemporalAccessor var1) {
         if (this.rangeUnit == ChronoUnit.WEEKS) {
            return (long)this.localizedDayOfWeek(var1);
         } else if (this.rangeUnit == ChronoUnit.MONTHS) {
            return this.localizedWeekOfMonth(var1);
         } else if (this.rangeUnit == ChronoUnit.YEARS) {
            return this.localizedWeekOfYear(var1);
         } else if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS) {
            return (long)this.localizedWeekOfWeekBasedYear(var1);
         } else if (this.rangeUnit == ChronoUnit.FOREVER) {
            return (long)this.localizedWeekBasedYear(var1);
         } else {
            throw new IllegalStateException("unreachable, rangeUnit: " + this.rangeUnit + ", this: " + this);
         }
      }

      private int localizedDayOfWeek(TemporalAccessor var1) {
         int var2 = this.weekDef.getFirstDayOfWeek().getValue();
         int var3 = var1.get(ChronoField.DAY_OF_WEEK);
         return Math.floorMod(var3 - var2, 7) + 1;
      }

      private int localizedDayOfWeek(int var1) {
         int var2 = this.weekDef.getFirstDayOfWeek().getValue();
         return Math.floorMod(var1 - var2, 7) + 1;
      }

      private long localizedWeekOfMonth(TemporalAccessor var1) {
         int var2 = this.localizedDayOfWeek(var1);
         int var3 = var1.get(ChronoField.DAY_OF_MONTH);
         int var4 = this.startOfWeekOffset(var3, var2);
         return (long)this.computeWeek(var4, var3);
      }

      private long localizedWeekOfYear(TemporalAccessor var1) {
         int var2 = this.localizedDayOfWeek(var1);
         int var3 = var1.get(ChronoField.DAY_OF_YEAR);
         int var4 = this.startOfWeekOffset(var3, var2);
         return (long)this.computeWeek(var4, var3);
      }

      private int localizedWeekBasedYear(TemporalAccessor var1) {
         int var2 = this.localizedDayOfWeek(var1);
         int var3 = var1.get(ChronoField.YEAR);
         int var4 = var1.get(ChronoField.DAY_OF_YEAR);
         int var5 = this.startOfWeekOffset(var4, var2);
         int var6 = this.computeWeek(var5, var4);
         if (var6 == 0) {
            return var3 - 1;
         } else {
            ValueRange var7 = var1.range(ChronoField.DAY_OF_YEAR);
            int var8 = (int)var7.getMaximum();
            int var9 = this.computeWeek(var5, var8 + this.weekDef.getMinimalDaysInFirstWeek());
            return var6 >= var9 ? var3 + 1 : var3;
         }
      }

      private int localizedWeekOfWeekBasedYear(TemporalAccessor var1) {
         int var2 = this.localizedDayOfWeek(var1);
         int var3 = var1.get(ChronoField.DAY_OF_YEAR);
         int var4 = this.startOfWeekOffset(var3, var2);
         int var5 = this.computeWeek(var4, var3);
         if (var5 == 0) {
            ChronoLocalDate var9 = Chronology.from(var1).date(var1);
            var9 = var9.minus((long)var3, ChronoUnit.DAYS);
            return this.localizedWeekOfWeekBasedYear(var9);
         } else {
            if (var5 > 50) {
               ValueRange var6 = var1.range(ChronoField.DAY_OF_YEAR);
               int var7 = (int)var6.getMaximum();
               int var8 = this.computeWeek(var4, var7 + this.weekDef.getMinimalDaysInFirstWeek());
               if (var5 >= var8) {
                  var5 = var5 - var8 + 1;
               }
            }

            return var5;
         }
      }

      private int startOfWeekOffset(int var1, int var2) {
         int var3 = Math.floorMod(var1 - var2, 7);
         int var4 = -var3;
         if (var3 + 1 > this.weekDef.getMinimalDaysInFirstWeek()) {
            var4 = 7 - var3;
         }

         return var4;
      }

      private int computeWeek(int var1, int var2) {
         return (7 + var1 + (var2 - 1)) / 7;
      }

      public <R extends Temporal> R adjustInto(R var1, long var2) {
         int var4 = this.range.checkValidIntValue(var2, this);
         int var5 = var1.get(this);
         if (var4 == var5) {
            return var1;
         } else if (this.rangeUnit == ChronoUnit.FOREVER) {
            int var6 = var1.get(this.weekDef.dayOfWeek);
            int var7 = var1.get(this.weekDef.weekOfWeekBasedYear);
            return this.ofWeekBasedYear(Chronology.from(var1), (int)var2, var7, var6);
         } else {
            return var1.plus((long)(var4 - var5), this.baseUnit);
         }
      }

      public ChronoLocalDate resolve(Map<TemporalField, Long> var1, TemporalAccessor var2, ResolverStyle var3) {
         long var4 = (Long)var1.get(this);
         int var6 = Math.toIntExact(var4);
         int var7;
         int var8;
         if (this.rangeUnit == ChronoUnit.WEEKS) {
            var7 = this.range.checkValidIntValue(var4, this);
            var8 = this.weekDef.getFirstDayOfWeek().getValue();
            long var13 = (long)(Math.floorMod(var8 - 1 + (var7 - 1), 7) + 1);
            var1.remove(this);
            var1.put(ChronoField.DAY_OF_WEEK, var13);
            return null;
         } else if (!var1.containsKey(ChronoField.DAY_OF_WEEK)) {
            return null;
         } else {
            var7 = ChronoField.DAY_OF_WEEK.checkValidIntValue((Long)var1.get(ChronoField.DAY_OF_WEEK));
            var8 = this.localizedDayOfWeek(var7);
            Chronology var9 = Chronology.from(var2);
            if (var1.containsKey(ChronoField.YEAR)) {
               int var10 = ChronoField.YEAR.checkValidIntValue((Long)var1.get(ChronoField.YEAR));
               if (this.rangeUnit == ChronoUnit.MONTHS && var1.containsKey(ChronoField.MONTH_OF_YEAR)) {
                  long var11 = (Long)var1.get(ChronoField.MONTH_OF_YEAR);
                  return this.resolveWoM(var1, var9, var10, var11, (long)var6, var8, var3);
               }

               if (this.rangeUnit == ChronoUnit.YEARS) {
                  return this.resolveWoY(var1, var9, var10, (long)var6, var8, var3);
               }
            } else if ((this.rangeUnit == WeekFields.WEEK_BASED_YEARS || this.rangeUnit == ChronoUnit.FOREVER) && var1.containsKey(this.weekDef.weekBasedYear) && var1.containsKey(this.weekDef.weekOfWeekBasedYear)) {
               return this.resolveWBY(var1, var9, var8, var3);
            }

            return null;
         }
      }

      private ChronoLocalDate resolveWoM(Map<TemporalField, Long> var1, Chronology var2, int var3, long var4, long var6, int var8, ResolverStyle var9) {
         ChronoLocalDate var10;
         int var13;
         if (var9 == ResolverStyle.LENIENT) {
            var10 = var2.date(var3, 1, 1).plus(Math.subtractExact(var4, 1L), ChronoUnit.MONTHS);
            long var11 = Math.subtractExact(var6, this.localizedWeekOfMonth(var10));
            var13 = var8 - this.localizedDayOfWeek(var10);
            var10 = var10.plus(Math.addExact(Math.multiplyExact(var11, 7L), (long)var13), ChronoUnit.DAYS);
         } else {
            int var15 = ChronoField.MONTH_OF_YEAR.checkValidIntValue(var4);
            var10 = var2.date(var3, var15, 1);
            int var12 = this.range.checkValidIntValue(var6, this);
            var13 = (int)((long)var12 - this.localizedWeekOfMonth(var10));
            int var14 = var8 - this.localizedDayOfWeek(var10);
            var10 = var10.plus((long)(var13 * 7 + var14), ChronoUnit.DAYS);
            if (var9 == ResolverStyle.STRICT && var10.getLong(ChronoField.MONTH_OF_YEAR) != var4) {
               throw new DateTimeException("Strict mode rejected resolved date as it is in a different month");
            }
         }

         var1.remove(this);
         var1.remove(ChronoField.YEAR);
         var1.remove(ChronoField.MONTH_OF_YEAR);
         var1.remove(ChronoField.DAY_OF_WEEK);
         return var10;
      }

      private ChronoLocalDate resolveWoY(Map<TemporalField, Long> var1, Chronology var2, int var3, long var4, int var6, ResolverStyle var7) {
         ChronoLocalDate var8 = var2.date(var3, 1, 1);
         int var11;
         if (var7 == ResolverStyle.LENIENT) {
            long var9 = Math.subtractExact(var4, this.localizedWeekOfYear(var8));
            var11 = var6 - this.localizedDayOfWeek(var8);
            var8 = var8.plus(Math.addExact(Math.multiplyExact(var9, 7L), (long)var11), ChronoUnit.DAYS);
         } else {
            int var12 = this.range.checkValidIntValue(var4, this);
            int var10 = (int)((long)var12 - this.localizedWeekOfYear(var8));
            var11 = var6 - this.localizedDayOfWeek(var8);
            var8 = var8.plus((long)(var10 * 7 + var11), ChronoUnit.DAYS);
            if (var7 == ResolverStyle.STRICT && var8.getLong(ChronoField.YEAR) != (long)var3) {
               throw new DateTimeException("Strict mode rejected resolved date as it is in a different year");
            }
         }

         var1.remove(this);
         var1.remove(ChronoField.YEAR);
         var1.remove(ChronoField.DAY_OF_WEEK);
         return var8;
      }

      private ChronoLocalDate resolveWBY(Map<TemporalField, Long> var1, Chronology var2, int var3, ResolverStyle var4) {
         int var5 = this.weekDef.weekBasedYear.range().checkValidIntValue((Long)var1.get(this.weekDef.weekBasedYear), this.weekDef.weekBasedYear);
         ChronoLocalDate var6;
         if (var4 == ResolverStyle.LENIENT) {
            var6 = this.ofWeekBasedYear(var2, var5, 1, var3);
            long var7 = (Long)var1.get(this.weekDef.weekOfWeekBasedYear);
            long var9 = Math.subtractExact(var7, 1L);
            var6 = var6.plus(var9, ChronoUnit.WEEKS);
         } else {
            int var11 = this.weekDef.weekOfWeekBasedYear.range().checkValidIntValue((Long)var1.get(this.weekDef.weekOfWeekBasedYear), this.weekDef.weekOfWeekBasedYear);
            var6 = this.ofWeekBasedYear(var2, var5, var11, var3);
            if (var4 == ResolverStyle.STRICT && this.localizedWeekBasedYear(var6) != var5) {
               throw new DateTimeException("Strict mode rejected resolved date as it is in a different week-based-year");
            }
         }

         var1.remove(this);
         var1.remove(this.weekDef.weekBasedYear);
         var1.remove(this.weekDef.weekOfWeekBasedYear);
         var1.remove(ChronoField.DAY_OF_WEEK);
         return var6;
      }

      public String getDisplayName(Locale var1) {
         Objects.requireNonNull(var1, (String)"locale");
         if (this.rangeUnit == ChronoUnit.YEARS) {
            LocaleResources var2 = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(var1);
            ResourceBundle var3 = var2.getJavaTimeFormatData();
            return var3.containsKey("field.week") ? var3.getString("field.week") : this.name;
         } else {
            return this.name;
         }
      }

      public TemporalUnit getBaseUnit() {
         return this.baseUnit;
      }

      public TemporalUnit getRangeUnit() {
         return this.rangeUnit;
      }

      public boolean isDateBased() {
         return true;
      }

      public boolean isTimeBased() {
         return false;
      }

      public ValueRange range() {
         return this.range;
      }

      public boolean isSupportedBy(TemporalAccessor var1) {
         if (var1.isSupported(ChronoField.DAY_OF_WEEK)) {
            if (this.rangeUnit == ChronoUnit.WEEKS) {
               return true;
            }

            if (this.rangeUnit == ChronoUnit.MONTHS) {
               return var1.isSupported(ChronoField.DAY_OF_MONTH);
            }

            if (this.rangeUnit == ChronoUnit.YEARS) {
               return var1.isSupported(ChronoField.DAY_OF_YEAR);
            }

            if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS) {
               return var1.isSupported(ChronoField.DAY_OF_YEAR);
            }

            if (this.rangeUnit == ChronoUnit.FOREVER) {
               return var1.isSupported(ChronoField.YEAR);
            }
         }

         return false;
      }

      public ValueRange rangeRefinedBy(TemporalAccessor var1) {
         if (this.rangeUnit == ChronoUnit.WEEKS) {
            return this.range;
         } else if (this.rangeUnit == ChronoUnit.MONTHS) {
            return this.rangeByWeek(var1, ChronoField.DAY_OF_MONTH);
         } else if (this.rangeUnit == ChronoUnit.YEARS) {
            return this.rangeByWeek(var1, ChronoField.DAY_OF_YEAR);
         } else if (this.rangeUnit == WeekFields.WEEK_BASED_YEARS) {
            return this.rangeWeekOfWeekBasedYear(var1);
         } else if (this.rangeUnit == ChronoUnit.FOREVER) {
            return ChronoField.YEAR.range();
         } else {
            throw new IllegalStateException("unreachable, rangeUnit: " + this.rangeUnit + ", this: " + this);
         }
      }

      private ValueRange rangeByWeek(TemporalAccessor var1, TemporalField var2) {
         int var3 = this.localizedDayOfWeek(var1);
         int var4 = this.startOfWeekOffset(var1.get(var2), var3);
         ValueRange var5 = var1.range(var2);
         return ValueRange.of((long)this.computeWeek(var4, (int)var5.getMinimum()), (long)this.computeWeek(var4, (int)var5.getMaximum()));
      }

      private ValueRange rangeWeekOfWeekBasedYear(TemporalAccessor var1) {
         if (!var1.isSupported(ChronoField.DAY_OF_YEAR)) {
            return WEEK_OF_YEAR_RANGE;
         } else {
            int var2 = this.localizedDayOfWeek(var1);
            int var3 = var1.get(ChronoField.DAY_OF_YEAR);
            int var4 = this.startOfWeekOffset(var3, var2);
            int var5 = this.computeWeek(var4, var3);
            if (var5 == 0) {
               ChronoLocalDate var10 = Chronology.from(var1).date(var1);
               var10 = var10.minus((long)(var3 + 7), ChronoUnit.DAYS);
               return this.rangeWeekOfWeekBasedYear(var10);
            } else {
               ValueRange var6 = var1.range(ChronoField.DAY_OF_YEAR);
               int var7 = (int)var6.getMaximum();
               int var8 = this.computeWeek(var4, var7 + this.weekDef.getMinimalDaysInFirstWeek());
               if (var5 >= var8) {
                  ChronoLocalDate var9 = Chronology.from(var1).date(var1);
                  var9 = var9.plus((long)(var7 - var3 + 1 + 7), ChronoUnit.DAYS);
                  return this.rangeWeekOfWeekBasedYear(var9);
               } else {
                  return ValueRange.of(1L, (long)(var8 - 1));
               }
            }
         }
      }

      public String toString() {
         return this.name + "[" + this.weekDef.toString() + "]";
      }
   }
}
