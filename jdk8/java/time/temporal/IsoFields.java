package java.time.temporal;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleResources;

public final class IsoFields {
   public static final TemporalField DAY_OF_QUARTER;
   public static final TemporalField QUARTER_OF_YEAR;
   public static final TemporalField WEEK_OF_WEEK_BASED_YEAR;
   public static final TemporalField WEEK_BASED_YEAR;
   public static final TemporalUnit WEEK_BASED_YEARS;
   public static final TemporalUnit QUARTER_YEARS;

   private IsoFields() {
      throw new AssertionError("Not instantiable");
   }

   static {
      DAY_OF_QUARTER = IsoFields.Field.DAY_OF_QUARTER;
      QUARTER_OF_YEAR = IsoFields.Field.QUARTER_OF_YEAR;
      WEEK_OF_WEEK_BASED_YEAR = IsoFields.Field.WEEK_OF_WEEK_BASED_YEAR;
      WEEK_BASED_YEAR = IsoFields.Field.WEEK_BASED_YEAR;
      WEEK_BASED_YEARS = IsoFields.Unit.WEEK_BASED_YEARS;
      QUARTER_YEARS = IsoFields.Unit.QUARTER_YEARS;
   }

   private static enum Unit implements TemporalUnit {
      WEEK_BASED_YEARS("WeekBasedYears", Duration.ofSeconds(31556952L)),
      QUARTER_YEARS("QuarterYears", Duration.ofSeconds(7889238L));

      private final String name;
      private final Duration duration;

      private Unit(String var3, Duration var4) {
         this.name = var3;
         this.duration = var4;
      }

      public Duration getDuration() {
         return this.duration;
      }

      public boolean isDurationEstimated() {
         return true;
      }

      public boolean isDateBased() {
         return true;
      }

      public boolean isTimeBased() {
         return false;
      }

      public boolean isSupportedBy(Temporal var1) {
         return var1.isSupported(ChronoField.EPOCH_DAY);
      }

      public <R extends Temporal> R addTo(R var1, long var2) {
         switch(this) {
         case WEEK_BASED_YEARS:
            return var1.with(IsoFields.WEEK_BASED_YEAR, Math.addExact((long)var1.get(IsoFields.WEEK_BASED_YEAR), var2));
         case QUARTER_YEARS:
            return var1.plus(var2 / 256L, ChronoUnit.YEARS).plus(var2 % 256L * 3L, ChronoUnit.MONTHS);
         default:
            throw new IllegalStateException("Unreachable");
         }
      }

      public long between(Temporal var1, Temporal var2) {
         if (var1.getClass() != var2.getClass()) {
            return var1.until(var2, this);
         } else {
            switch(this) {
            case WEEK_BASED_YEARS:
               return Math.subtractExact(var2.getLong(IsoFields.WEEK_BASED_YEAR), var1.getLong(IsoFields.WEEK_BASED_YEAR));
            case QUARTER_YEARS:
               return var1.until(var2, ChronoUnit.MONTHS) / 3L;
            default:
               throw new IllegalStateException("Unreachable");
            }
         }
      }

      public String toString() {
         return this.name;
      }
   }

   private static enum Field implements TemporalField {
      DAY_OF_QUARTER {
         public TemporalUnit getBaseUnit() {
            return ChronoUnit.DAYS;
         }

         public TemporalUnit getRangeUnit() {
            return IsoFields.QUARTER_YEARS;
         }

         public ValueRange range() {
            return ValueRange.of(1L, 90L, 92L);
         }

         public boolean isSupportedBy(TemporalAccessor var1) {
            return var1.isSupported(ChronoField.DAY_OF_YEAR) && var1.isSupported(ChronoField.MONTH_OF_YEAR) && var1.isSupported(ChronoField.YEAR) && IsoFields.Field.isIso(var1);
         }

         public ValueRange rangeRefinedBy(TemporalAccessor var1) {
            if (!this.isSupportedBy(var1)) {
               throw new UnsupportedTemporalTypeException("Unsupported field: DayOfQuarter");
            } else {
               long var2 = var1.getLong(QUARTER_OF_YEAR);
               if (var2 == 1L) {
                  long var4 = var1.getLong(ChronoField.YEAR);
                  return IsoChronology.INSTANCE.isLeapYear(var4) ? ValueRange.of(1L, 91L) : ValueRange.of(1L, 90L);
               } else if (var2 == 2L) {
                  return ValueRange.of(1L, 91L);
               } else {
                  return var2 != 3L && var2 != 4L ? this.range() : ValueRange.of(1L, 92L);
               }
            }
         }

         public long getFrom(TemporalAccessor var1) {
            if (!this.isSupportedBy(var1)) {
               throw new UnsupportedTemporalTypeException("Unsupported field: DayOfQuarter");
            } else {
               int var2 = var1.get(ChronoField.DAY_OF_YEAR);
               int var3 = var1.get(ChronoField.MONTH_OF_YEAR);
               long var4 = var1.getLong(ChronoField.YEAR);
               return (long)(var2 - IsoFields.Field.QUARTER_DAYS[(var3 - 1) / 3 + (IsoChronology.INSTANCE.isLeapYear(var4) ? 4 : 0)]);
            }
         }

         public <R extends Temporal> R adjustInto(R var1, long var2) {
            long var4 = this.getFrom(var1);
            this.range().checkValidValue(var2, this);
            return var1.with(ChronoField.DAY_OF_YEAR, var1.getLong(ChronoField.DAY_OF_YEAR) + (var2 - var4));
         }

         public ChronoLocalDate resolve(Map<TemporalField, Long> var1, TemporalAccessor var2, ResolverStyle var3) {
            Long var4 = (Long)var1.get(ChronoField.YEAR);
            Long var5 = (Long)var1.get(QUARTER_OF_YEAR);
            if (var4 != null && var5 != null) {
               int var6 = ChronoField.YEAR.checkValidIntValue(var4);
               long var7 = (Long)var1.get(DAY_OF_QUARTER);
               IsoFields.Field.ensureIso(var2);
               LocalDate var9;
               if (var3 == ResolverStyle.LENIENT) {
                  var9 = LocalDate.of(var6, 1, 1).plusMonths(Math.multiplyExact(Math.subtractExact(var5, 1L), 3L));
                  var7 = Math.subtractExact(var7, 1L);
               } else {
                  int var10 = QUARTER_OF_YEAR.range().checkValidIntValue(var5, QUARTER_OF_YEAR);
                  var9 = LocalDate.of(var6, (var10 - 1) * 3 + 1, 1);
                  if (var7 < 1L || var7 > 90L) {
                     if (var3 == ResolverStyle.STRICT) {
                        this.rangeRefinedBy(var9).checkValidValue(var7, this);
                     } else {
                        this.range().checkValidValue(var7, this);
                     }
                  }

                  --var7;
               }

               var1.remove(this);
               var1.remove(ChronoField.YEAR);
               var1.remove(QUARTER_OF_YEAR);
               return var9.plusDays(var7);
            } else {
               return null;
            }
         }

         public String toString() {
            return "DayOfQuarter";
         }
      },
      QUARTER_OF_YEAR {
         public TemporalUnit getBaseUnit() {
            return IsoFields.QUARTER_YEARS;
         }

         public TemporalUnit getRangeUnit() {
            return ChronoUnit.YEARS;
         }

         public ValueRange range() {
            return ValueRange.of(1L, 4L);
         }

         public boolean isSupportedBy(TemporalAccessor var1) {
            return var1.isSupported(ChronoField.MONTH_OF_YEAR) && IsoFields.Field.isIso(var1);
         }

         public long getFrom(TemporalAccessor var1) {
            if (!this.isSupportedBy(var1)) {
               throw new UnsupportedTemporalTypeException("Unsupported field: QuarterOfYear");
            } else {
               long var2 = var1.getLong(ChronoField.MONTH_OF_YEAR);
               return (var2 + 2L) / 3L;
            }
         }

         public <R extends Temporal> R adjustInto(R var1, long var2) {
            long var4 = this.getFrom(var1);
            this.range().checkValidValue(var2, this);
            return var1.with(ChronoField.MONTH_OF_YEAR, var1.getLong(ChronoField.MONTH_OF_YEAR) + (var2 - var4) * 3L);
         }

         public String toString() {
            return "QuarterOfYear";
         }
      },
      WEEK_OF_WEEK_BASED_YEAR {
         public String getDisplayName(Locale var1) {
            Objects.requireNonNull(var1, (String)"locale");
            LocaleResources var2 = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(var1);
            ResourceBundle var3 = var2.getJavaTimeFormatData();
            return var3.containsKey("field.week") ? var3.getString("field.week") : this.toString();
         }

         public TemporalUnit getBaseUnit() {
            return ChronoUnit.WEEKS;
         }

         public TemporalUnit getRangeUnit() {
            return IsoFields.WEEK_BASED_YEARS;
         }

         public ValueRange range() {
            return ValueRange.of(1L, 52L, 53L);
         }

         public boolean isSupportedBy(TemporalAccessor var1) {
            return var1.isSupported(ChronoField.EPOCH_DAY) && IsoFields.Field.isIso(var1);
         }

         public ValueRange rangeRefinedBy(TemporalAccessor var1) {
            if (!this.isSupportedBy(var1)) {
               throw new UnsupportedTemporalTypeException("Unsupported field: WeekOfWeekBasedYear");
            } else {
               return IsoFields.Field.getWeekRange(LocalDate.from(var1));
            }
         }

         public long getFrom(TemporalAccessor var1) {
            if (!this.isSupportedBy(var1)) {
               throw new UnsupportedTemporalTypeException("Unsupported field: WeekOfWeekBasedYear");
            } else {
               return (long)IsoFields.Field.getWeek(LocalDate.from(var1));
            }
         }

         public <R extends Temporal> R adjustInto(R var1, long var2) {
            this.range().checkValidValue(var2, this);
            return var1.plus(Math.subtractExact(var2, this.getFrom(var1)), ChronoUnit.WEEKS);
         }

         public ChronoLocalDate resolve(Map<TemporalField, Long> var1, TemporalAccessor var2, ResolverStyle var3) {
            Long var4 = (Long)var1.get(WEEK_BASED_YEAR);
            Long var5 = (Long)var1.get(ChronoField.DAY_OF_WEEK);
            if (var4 != null && var5 != null) {
               int var6 = WEEK_BASED_YEAR.range().checkValidIntValue(var4, WEEK_BASED_YEAR);
               long var7 = (Long)var1.get(WEEK_OF_WEEK_BASED_YEAR);
               IsoFields.Field.ensureIso(var2);
               LocalDate var9 = LocalDate.of(var6, 1, 4);
               if (var3 == ResolverStyle.LENIENT) {
                  long var10 = var5;
                  if (var10 > 7L) {
                     var9 = var9.plusWeeks((var10 - 1L) / 7L);
                     var10 = (var10 - 1L) % 7L + 1L;
                  } else if (var10 < 1L) {
                     var9 = var9.plusWeeks(Math.subtractExact(var10, 7L) / 7L);
                     var10 = (var10 + 6L) % 7L + 1L;
                  }

                  var9 = var9.plusWeeks(Math.subtractExact(var7, 1L)).with(ChronoField.DAY_OF_WEEK, var10);
               } else {
                  int var12 = ChronoField.DAY_OF_WEEK.checkValidIntValue(var5);
                  if (var7 < 1L || var7 > 52L) {
                     if (var3 == ResolverStyle.STRICT) {
                        IsoFields.Field.getWeekRange(var9).checkValidValue(var7, this);
                     } else {
                        this.range().checkValidValue(var7, this);
                     }
                  }

                  var9 = var9.plusWeeks(var7 - 1L).with(ChronoField.DAY_OF_WEEK, (long)var12);
               }

               var1.remove(this);
               var1.remove(WEEK_BASED_YEAR);
               var1.remove(ChronoField.DAY_OF_WEEK);
               return var9;
            } else {
               return null;
            }
         }

         public String toString() {
            return "WeekOfWeekBasedYear";
         }
      },
      WEEK_BASED_YEAR {
         public TemporalUnit getBaseUnit() {
            return IsoFields.WEEK_BASED_YEARS;
         }

         public TemporalUnit getRangeUnit() {
            return ChronoUnit.FOREVER;
         }

         public ValueRange range() {
            return ChronoField.YEAR.range();
         }

         public boolean isSupportedBy(TemporalAccessor var1) {
            return var1.isSupported(ChronoField.EPOCH_DAY) && IsoFields.Field.isIso(var1);
         }

         public long getFrom(TemporalAccessor var1) {
            if (!this.isSupportedBy(var1)) {
               throw new UnsupportedTemporalTypeException("Unsupported field: WeekBasedYear");
            } else {
               return (long)IsoFields.Field.getWeekBasedYear(LocalDate.from(var1));
            }
         }

         public <R extends Temporal> R adjustInto(R var1, long var2) {
            if (!this.isSupportedBy(var1)) {
               throw new UnsupportedTemporalTypeException("Unsupported field: WeekBasedYear");
            } else {
               int var4 = this.range().checkValidIntValue(var2, WEEK_BASED_YEAR);
               LocalDate var5 = LocalDate.from(var1);
               int var6 = var5.get(ChronoField.DAY_OF_WEEK);
               int var7 = IsoFields.Field.getWeek(var5);
               if (var7 == 53 && IsoFields.Field.getWeekRange(var4) == 52) {
                  var7 = 52;
               }

               LocalDate var8 = LocalDate.of(var4, 1, 4);
               int var9 = var6 - var8.get(ChronoField.DAY_OF_WEEK) + (var7 - 1) * 7;
               var8 = var8.plusDays((long)var9);
               return var1.with(var8);
            }
         }

         public String toString() {
            return "WeekBasedYear";
         }
      };

      private static final int[] QUARTER_DAYS = new int[]{0, 90, 181, 273, 0, 91, 182, 274};

      private Field() {
      }

      public boolean isDateBased() {
         return true;
      }

      public boolean isTimeBased() {
         return false;
      }

      public ValueRange rangeRefinedBy(TemporalAccessor var1) {
         return this.range();
      }

      private static boolean isIso(TemporalAccessor var0) {
         return Chronology.from(var0).equals(IsoChronology.INSTANCE);
      }

      private static void ensureIso(TemporalAccessor var0) {
         if (!isIso(var0)) {
            throw new DateTimeException("Resolve requires IsoChronology");
         }
      }

      private static ValueRange getWeekRange(LocalDate var0) {
         int var1 = getWeekBasedYear(var0);
         return ValueRange.of(1L, (long)getWeekRange(var1));
      }

      private static int getWeekRange(int var0) {
         LocalDate var1 = LocalDate.of(var0, 1, 1);
         return var1.getDayOfWeek() != DayOfWeek.THURSDAY && (var1.getDayOfWeek() != DayOfWeek.WEDNESDAY || !var1.isLeapYear()) ? 52 : 53;
      }

      private static int getWeek(LocalDate var0) {
         int var1 = var0.getDayOfWeek().ordinal();
         int var2 = var0.getDayOfYear() - 1;
         int var3 = var2 + (3 - var1);
         int var4 = var3 / 7;
         int var5 = var3 - var4 * 7;
         int var6 = var5 - 3;
         if (var6 < -3) {
            var6 += 7;
         }

         if (var2 < var6) {
            return (int)getWeekRange(var0.withDayOfYear(180).minusYears(1L)).getMaximum();
         } else {
            int var7 = (var2 - var6) / 7 + 1;
            if (var7 == 53 && var6 != -3 && (var6 != -2 || !var0.isLeapYear())) {
               var7 = 1;
            }

            return var7;
         }
      }

      private static int getWeekBasedYear(LocalDate var0) {
         int var1 = var0.getYear();
         int var2 = var0.getDayOfYear();
         int var3;
         if (var2 <= 3) {
            var3 = var0.getDayOfWeek().ordinal();
            if (var2 - var3 < -2) {
               --var1;
            }
         } else if (var2 >= 363) {
            var3 = var0.getDayOfWeek().ordinal();
            var2 = var2 - 363 - (var0.isLeapYear() ? 1 : 0);
            if (var2 - var3 >= 0) {
               ++var1;
            }
         }

         return var1;
      }

      // $FF: synthetic method
      Field(Object var3) {
         this();
      }
   }
}
