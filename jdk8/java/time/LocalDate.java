package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Era;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneRules;
import java.util.Objects;

public final class LocalDate implements Temporal, TemporalAdjuster, ChronoLocalDate, Serializable {
   public static final LocalDate MIN = of(-999999999, 1, 1);
   public static final LocalDate MAX = of(999999999, 12, 31);
   private static final long serialVersionUID = 2942565459149668126L;
   private static final int DAYS_PER_CYCLE = 146097;
   static final long DAYS_0000_TO_1970 = 719528L;
   private final int year;
   private final short month;
   private final short day;

   public static LocalDate now() {
      return now(Clock.systemDefaultZone());
   }

   public static LocalDate now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static LocalDate now(Clock var0) {
      Objects.requireNonNull(var0, (String)"clock");
      Instant var1 = var0.instant();
      ZoneOffset var2 = var0.getZone().getRules().getOffset(var1);
      long var3 = var1.getEpochSecond() + (long)var2.getTotalSeconds();
      long var5 = Math.floorDiv(var3, 86400L);
      return ofEpochDay(var5);
   }

   public static LocalDate of(int var0, Month var1, int var2) {
      ChronoField.YEAR.checkValidValue((long)var0);
      Objects.requireNonNull(var1, (String)"month");
      ChronoField.DAY_OF_MONTH.checkValidValue((long)var2);
      return create(var0, var1.getValue(), var2);
   }

   public static LocalDate of(int var0, int var1, int var2) {
      ChronoField.YEAR.checkValidValue((long)var0);
      ChronoField.MONTH_OF_YEAR.checkValidValue((long)var1);
      ChronoField.DAY_OF_MONTH.checkValidValue((long)var2);
      return create(var0, var1, var2);
   }

   public static LocalDate ofYearDay(int var0, int var1) {
      ChronoField.YEAR.checkValidValue((long)var0);
      ChronoField.DAY_OF_YEAR.checkValidValue((long)var1);
      boolean var2 = IsoChronology.INSTANCE.isLeapYear((long)var0);
      if (var1 == 366 && !var2) {
         throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + var0 + "' is not a leap year");
      } else {
         Month var3 = Month.of((var1 - 1) / 31 + 1);
         int var4 = var3.firstDayOfYear(var2) + var3.length(var2) - 1;
         if (var1 > var4) {
            var3 = var3.plus(1L);
         }

         int var5 = var1 - var3.firstDayOfYear(var2) + 1;
         return new LocalDate(var0, var3.getValue(), var5);
      }
   }

   public static LocalDate ofEpochDay(long var0) {
      long var2 = var0 + 719528L;
      var2 -= 60L;
      long var4 = 0L;
      long var6;
      if (var2 < 0L) {
         var6 = (var2 + 1L) / 146097L - 1L;
         var4 = var6 * 400L;
         var2 += -var6 * 146097L;
      }

      var6 = (400L * var2 + 591L) / 146097L;
      long var8 = var2 - (365L * var6 + var6 / 4L - var6 / 100L + var6 / 400L);
      if (var8 < 0L) {
         --var6;
         var8 = var2 - (365L * var6 + var6 / 4L - var6 / 100L + var6 / 400L);
      }

      var6 += var4;
      int var10 = (int)var8;
      int var11 = (var10 * 5 + 2) / 153;
      int var12 = (var11 + 2) % 12 + 1;
      int var13 = var10 - (var11 * 306 + 5) / 10 + 1;
      var6 += (long)(var11 / 10);
      int var14 = ChronoField.YEAR.checkValidIntValue(var6);
      return new LocalDate(var14, var12, var13);
   }

   public static LocalDate from(TemporalAccessor var0) {
      Objects.requireNonNull(var0, (String)"temporal");
      LocalDate var1 = (LocalDate)var0.query(TemporalQueries.localDate());
      if (var1 == null) {
         throw new DateTimeException("Unable to obtain LocalDate from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName());
      } else {
         return var1;
      }
   }

   public static LocalDate parse(CharSequence var0) {
      return parse(var0, DateTimeFormatter.ISO_LOCAL_DATE);
   }

   public static LocalDate parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (LocalDate)var1.parse(var0, LocalDate::from);
   }

   private static LocalDate create(int var0, int var1, int var2) {
      if (var2 > 28) {
         int var3 = 31;
         switch(var1) {
         case 2:
            var3 = IsoChronology.INSTANCE.isLeapYear((long)var0) ? 29 : 28;
         case 3:
         case 5:
         case 7:
         case 8:
         case 10:
         default:
            break;
         case 4:
         case 6:
         case 9:
         case 11:
            var3 = 30;
         }

         if (var2 > var3) {
            if (var2 == 29) {
               throw new DateTimeException("Invalid date 'February 29' as '" + var0 + "' is not a leap year");
            }

            throw new DateTimeException("Invalid date '" + Month.of(var1).name() + " " + var2 + "'");
         }
      }

      return new LocalDate(var0, var1, var2);
   }

   private static LocalDate resolvePreviousValid(int var0, int var1, int var2) {
      switch(var1) {
      case 2:
         var2 = Math.min(var2, IsoChronology.INSTANCE.isLeapYear((long)var0) ? 29 : 28);
      case 3:
      case 5:
      case 7:
      case 8:
      case 10:
      default:
         break;
      case 4:
      case 6:
      case 9:
      case 11:
         var2 = Math.min(var2, 30);
      }

      return new LocalDate(var0, var1, var2);
   }

   private LocalDate(int var1, int var2, int var3) {
      this.year = var1;
      this.month = (short)var2;
      this.day = (short)var3;
   }

   public boolean isSupported(TemporalField var1) {
      return ChronoLocalDate.super.isSupported(var1);
   }

   public boolean isSupported(TemporalUnit var1) {
      return ChronoLocalDate.super.isSupported(var1);
   }

   public ValueRange range(TemporalField var1) {
      if (!(var1 instanceof ChronoField)) {
         return var1.rangeRefinedBy(this);
      } else {
         ChronoField var2 = (ChronoField)var1;
         if (var2.isDateBased()) {
            switch(var2) {
            case DAY_OF_MONTH:
               return ValueRange.of(1L, (long)this.lengthOfMonth());
            case DAY_OF_YEAR:
               return ValueRange.of(1L, (long)this.lengthOfYear());
            case ALIGNED_WEEK_OF_MONTH:
               return ValueRange.of(1L, this.getMonth() == Month.FEBRUARY && !this.isLeapYear() ? 4L : 5L);
            case YEAR_OF_ERA:
               return this.getYear() <= 0 ? ValueRange.of(1L, 1000000000L) : ValueRange.of(1L, 999999999L);
            default:
               return var1.range();
            }
         } else {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      }
   }

   public int get(TemporalField var1) {
      return var1 instanceof ChronoField ? this.get0(var1) : ChronoLocalDate.super.get(var1);
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         if (var1 == ChronoField.EPOCH_DAY) {
            return this.toEpochDay();
         } else {
            return var1 == ChronoField.PROLEPTIC_MONTH ? this.getProlepticMonth() : (long)this.get0(var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   private int get0(TemporalField var1) {
      switch((ChronoField)var1) {
      case DAY_OF_MONTH:
         return this.day;
      case DAY_OF_YEAR:
         return this.getDayOfYear();
      case ALIGNED_WEEK_OF_MONTH:
         return (this.day - 1) / 7 + 1;
      case YEAR_OF_ERA:
         return this.year >= 1 ? this.year : 1 - this.year;
      case DAY_OF_WEEK:
         return this.getDayOfWeek().getValue();
      case ALIGNED_DAY_OF_WEEK_IN_MONTH:
         return (this.day - 1) % 7 + 1;
      case ALIGNED_DAY_OF_WEEK_IN_YEAR:
         return (this.getDayOfYear() - 1) % 7 + 1;
      case EPOCH_DAY:
         throw new UnsupportedTemporalTypeException("Invalid field 'EpochDay' for get() method, use getLong() instead");
      case ALIGNED_WEEK_OF_YEAR:
         return (this.getDayOfYear() - 1) / 7 + 1;
      case MONTH_OF_YEAR:
         return this.month;
      case PROLEPTIC_MONTH:
         throw new UnsupportedTemporalTypeException("Invalid field 'ProlepticMonth' for get() method, use getLong() instead");
      case YEAR:
         return this.year;
      case ERA:
         return this.year >= 1 ? 1 : 0;
      default:
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      }
   }

   private long getProlepticMonth() {
      return (long)this.year * 12L + (long)this.month - 1L;
   }

   public IsoChronology getChronology() {
      return IsoChronology.INSTANCE;
   }

   public Era getEra() {
      return ChronoLocalDate.super.getEra();
   }

   public int getYear() {
      return this.year;
   }

   public int getMonthValue() {
      return this.month;
   }

   public Month getMonth() {
      return Month.of(this.month);
   }

   public int getDayOfMonth() {
      return this.day;
   }

   public int getDayOfYear() {
      return this.getMonth().firstDayOfYear(this.isLeapYear()) + this.day - 1;
   }

   public DayOfWeek getDayOfWeek() {
      int var1 = (int)Math.floorMod(this.toEpochDay() + 3L, 7L);
      return DayOfWeek.of(var1 + 1);
   }

   public boolean isLeapYear() {
      return IsoChronology.INSTANCE.isLeapYear((long)this.year);
   }

   public int lengthOfMonth() {
      switch(this.month) {
      case 2:
         return this.isLeapYear() ? 29 : 28;
      case 3:
      case 5:
      case 7:
      case 8:
      case 10:
      default:
         return 31;
      case 4:
      case 6:
      case 9:
      case 11:
         return 30;
      }
   }

   public int lengthOfYear() {
      return this.isLeapYear() ? 366 : 365;
   }

   public LocalDate with(TemporalAdjuster var1) {
      return var1 instanceof LocalDate ? (LocalDate)var1 : (LocalDate)var1.adjustInto(this);
   }

   public LocalDate with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         var4.checkValidValue(var2);
         switch(var4) {
         case DAY_OF_MONTH:
            return this.withDayOfMonth((int)var2);
         case DAY_OF_YEAR:
            return this.withDayOfYear((int)var2);
         case ALIGNED_WEEK_OF_MONTH:
            return this.plusWeeks(var2 - this.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
         case YEAR_OF_ERA:
            return this.withYear((int)(this.year >= 1 ? var2 : 1L - var2));
         case DAY_OF_WEEK:
            return this.plusDays(var2 - (long)this.getDayOfWeek().getValue());
         case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            return this.plusDays(var2 - this.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
         case ALIGNED_DAY_OF_WEEK_IN_YEAR:
            return this.plusDays(var2 - this.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
         case EPOCH_DAY:
            return ofEpochDay(var2);
         case ALIGNED_WEEK_OF_YEAR:
            return this.plusWeeks(var2 - this.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
         case MONTH_OF_YEAR:
            return this.withMonth((int)var2);
         case PROLEPTIC_MONTH:
            return this.plusMonths(var2 - this.getProlepticMonth());
         case YEAR:
            return this.withYear((int)var2);
         case ERA:
            return this.getLong(ChronoField.ERA) == var2 ? this : this.withYear(1 - this.year);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return (LocalDate)var1.adjustInto(this, var2);
      }
   }

   public LocalDate withYear(int var1) {
      if (this.year == var1) {
         return this;
      } else {
         ChronoField.YEAR.checkValidValue((long)var1);
         return resolvePreviousValid(var1, this.month, this.day);
      }
   }

   public LocalDate withMonth(int var1) {
      if (this.month == var1) {
         return this;
      } else {
         ChronoField.MONTH_OF_YEAR.checkValidValue((long)var1);
         return resolvePreviousValid(this.year, var1, this.day);
      }
   }

   public LocalDate withDayOfMonth(int var1) {
      return this.day == var1 ? this : of(this.year, this.month, var1);
   }

   public LocalDate withDayOfYear(int var1) {
      return this.getDayOfYear() == var1 ? this : ofYearDay(this.year, var1);
   }

   public LocalDate plus(TemporalAmount var1) {
      if (var1 instanceof Period) {
         Period var2 = (Period)var1;
         return this.plusMonths(var2.toTotalMonths()).plusDays((long)var2.getDays());
      } else {
         Objects.requireNonNull(var1, (String)"amountToAdd");
         return (LocalDate)var1.addTo(this);
      }
   }

   public LocalDate plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         ChronoUnit var4 = (ChronoUnit)var3;
         switch(var4) {
         case DAYS:
            return this.plusDays(var1);
         case WEEKS:
            return this.plusWeeks(var1);
         case MONTHS:
            return this.plusMonths(var1);
         case YEARS:
            return this.plusYears(var1);
         case DECADES:
            return this.plusYears(Math.multiplyExact(var1, 10L));
         case CENTURIES:
            return this.plusYears(Math.multiplyExact(var1, 100L));
         case MILLENNIA:
            return this.plusYears(Math.multiplyExact(var1, 1000L));
         case ERAS:
            return this.with(ChronoField.ERA, Math.addExact(this.getLong(ChronoField.ERA), var1));
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var3);
         }
      } else {
         return (LocalDate)var3.addTo(this, var1);
      }
   }

   public LocalDate plusYears(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         int var3 = ChronoField.YEAR.checkValidIntValue((long)this.year + var1);
         return resolvePreviousValid(var3, this.month, this.day);
      }
   }

   public LocalDate plusMonths(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         long var3 = (long)this.year * 12L + (long)(this.month - 1);
         long var5 = var3 + var1;
         int var7 = ChronoField.YEAR.checkValidIntValue(Math.floorDiv(var5, 12L));
         int var8 = (int)Math.floorMod(var5, 12L) + 1;
         return resolvePreviousValid(var7, var8, this.day);
      }
   }

   public LocalDate plusWeeks(long var1) {
      return this.plusDays(Math.multiplyExact(var1, 7L));
   }

   public LocalDate plusDays(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         long var3 = Math.addExact(this.toEpochDay(), var1);
         return ofEpochDay(var3);
      }
   }

   public LocalDate minus(TemporalAmount var1) {
      if (var1 instanceof Period) {
         Period var2 = (Period)var1;
         return this.minusMonths(var2.toTotalMonths()).minusDays((long)var2.getDays());
      } else {
         Objects.requireNonNull(var1, (String)"amountToSubtract");
         return (LocalDate)var1.subtractFrom(this);
      }
   }

   public LocalDate minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public LocalDate minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusYears(Long.MAX_VALUE).plusYears(1L) : this.plusYears(-var1);
   }

   public LocalDate minusMonths(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMonths(Long.MAX_VALUE).plusMonths(1L) : this.plusMonths(-var1);
   }

   public LocalDate minusWeeks(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusWeeks(Long.MAX_VALUE).plusWeeks(1L) : this.plusWeeks(-var1);
   }

   public LocalDate minusDays(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusDays(Long.MAX_VALUE).plusDays(1L) : this.plusDays(-var1);
   }

   public <R> R query(TemporalQuery<R> var1) {
      return var1 == TemporalQueries.localDate() ? this : ChronoLocalDate.super.query(var1);
   }

   public Temporal adjustInto(Temporal var1) {
      return ChronoLocalDate.super.adjustInto(var1);
   }

   public long until(Temporal var1, TemporalUnit var2) {
      LocalDate var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         switch((ChronoUnit)var2) {
         case DAYS:
            return this.daysUntil(var3);
         case WEEKS:
            return this.daysUntil(var3) / 7L;
         case MONTHS:
            return this.monthsUntil(var3);
         case YEARS:
            return this.monthsUntil(var3) / 12L;
         case DECADES:
            return this.monthsUntil(var3) / 120L;
         case CENTURIES:
            return this.monthsUntil(var3) / 1200L;
         case MILLENNIA:
            return this.monthsUntil(var3) / 12000L;
         case ERAS:
            return var3.getLong(ChronoField.ERA) - this.getLong(ChronoField.ERA);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var2);
         }
      } else {
         return var2.between(this, var3);
      }
   }

   long daysUntil(LocalDate var1) {
      return var1.toEpochDay() - this.toEpochDay();
   }

   private long monthsUntil(LocalDate var1) {
      long var2 = this.getProlepticMonth() * 32L + (long)this.getDayOfMonth();
      long var4 = var1.getProlepticMonth() * 32L + (long)var1.getDayOfMonth();
      return (var4 - var2) / 32L;
   }

   public Period until(ChronoLocalDate var1) {
      LocalDate var2 = from(var1);
      long var3 = var2.getProlepticMonth() - this.getProlepticMonth();
      int var5 = var2.day - this.day;
      if (var3 > 0L && var5 < 0) {
         --var3;
         LocalDate var6 = this.plusMonths(var3);
         var5 = (int)(var2.toEpochDay() - var6.toEpochDay());
      } else if (var3 < 0L && var5 > 0) {
         ++var3;
         var5 -= var2.lengthOfMonth();
      }

      long var9 = var3 / 12L;
      int var8 = (int)(var3 % 12L);
      return Period.of(Math.toIntExact(var9), var8, var5);
   }

   public String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   public LocalDateTime atTime(LocalTime var1) {
      return LocalDateTime.of(this, var1);
   }

   public LocalDateTime atTime(int var1, int var2) {
      return this.atTime(LocalTime.of(var1, var2));
   }

   public LocalDateTime atTime(int var1, int var2, int var3) {
      return this.atTime(LocalTime.of(var1, var2, var3));
   }

   public LocalDateTime atTime(int var1, int var2, int var3, int var4) {
      return this.atTime(LocalTime.of(var1, var2, var3, var4));
   }

   public OffsetDateTime atTime(OffsetTime var1) {
      return OffsetDateTime.of(LocalDateTime.of(this, var1.toLocalTime()), var1.getOffset());
   }

   public LocalDateTime atStartOfDay() {
      return LocalDateTime.of(this, LocalTime.MIDNIGHT);
   }

   public ZonedDateTime atStartOfDay(ZoneId var1) {
      Objects.requireNonNull(var1, (String)"zone");
      LocalDateTime var2 = this.atTime(LocalTime.MIDNIGHT);
      if (!(var1 instanceof ZoneOffset)) {
         ZoneRules var3 = var1.getRules();
         ZoneOffsetTransition var4 = var3.getTransition(var2);
         if (var4 != null && var4.isGap()) {
            var2 = var4.getDateTimeAfter();
         }
      }

      return ZonedDateTime.of(var2, var1);
   }

   public long toEpochDay() {
      long var1 = (long)this.year;
      long var3 = (long)this.month;
      long var5 = 0L;
      var5 += 365L * var1;
      if (var1 >= 0L) {
         var5 += (var1 + 3L) / 4L - (var1 + 99L) / 100L + (var1 + 399L) / 400L;
      } else {
         var5 -= var1 / -4L - var1 / -100L + var1 / -400L;
      }

      var5 += (367L * var3 - 362L) / 12L;
      var5 += (long)(this.day - 1);
      if (var3 > 2L) {
         --var5;
         if (!this.isLeapYear()) {
            --var5;
         }
      }

      return var5 - 719528L;
   }

   public int compareTo(ChronoLocalDate var1) {
      return var1 instanceof LocalDate ? this.compareTo0((LocalDate)var1) : ChronoLocalDate.super.compareTo(var1);
   }

   int compareTo0(LocalDate var1) {
      int var2 = this.year - var1.year;
      if (var2 == 0) {
         var2 = this.month - var1.month;
         if (var2 == 0) {
            var2 = this.day - var1.day;
         }
      }

      return var2;
   }

   public boolean isAfter(ChronoLocalDate var1) {
      if (var1 instanceof LocalDate) {
         return this.compareTo0((LocalDate)var1) > 0;
      } else {
         return ChronoLocalDate.super.isAfter(var1);
      }
   }

   public boolean isBefore(ChronoLocalDate var1) {
      if (var1 instanceof LocalDate) {
         return this.compareTo0((LocalDate)var1) < 0;
      } else {
         return ChronoLocalDate.super.isBefore(var1);
      }
   }

   public boolean isEqual(ChronoLocalDate var1) {
      if (var1 instanceof LocalDate) {
         return this.compareTo0((LocalDate)var1) == 0;
      } else {
         return ChronoLocalDate.super.isEqual(var1);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof LocalDate) {
         return this.compareTo0((LocalDate)var1) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.year;
      short var2 = this.month;
      short var3 = this.day;
      return var1 & -2048 ^ (var1 << 11) + (var2 << 6) + var3;
   }

   public String toString() {
      int var1 = this.year;
      short var2 = this.month;
      short var3 = this.day;
      int var4 = Math.abs(var1);
      StringBuilder var5 = new StringBuilder(10);
      if (var4 < 1000) {
         if (var1 < 0) {
            var5.append(var1 - 10000).deleteCharAt(1);
         } else {
            var5.append(var1 + 10000).deleteCharAt(0);
         }
      } else {
         if (var1 > 9999) {
            var5.append('+');
         }

         var5.append(var1);
      }

      return var5.append(var2 < 10 ? "-0" : "-").append((int)var2).append(var3 < 10 ? "-0" : "-").append((int)var3).toString();
   }

   private Object writeReplace() {
      return new Ser((byte)3, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeInt(this.year);
      var1.writeByte(this.month);
      var1.writeByte(this.day);
   }

   static LocalDate readExternal(DataInput var0) throws IOException {
      int var1 = var0.readInt();
      byte var2 = var0.readByte();
      byte var3 = var0.readByte();
      return of(var1, var2, var3);
   }
}
