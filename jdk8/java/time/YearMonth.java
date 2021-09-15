package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
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
import java.util.Objects;

public final class YearMonth implements Temporal, TemporalAdjuster, Comparable<YearMonth>, Serializable {
   private static final long serialVersionUID = 4183400860270640070L;
   private static final DateTimeFormatter PARSER;
   private final int year;
   private final int month;

   public static YearMonth now() {
      return now(Clock.systemDefaultZone());
   }

   public static YearMonth now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static YearMonth now(Clock var0) {
      LocalDate var1 = LocalDate.now(var0);
      return of(var1.getYear(), var1.getMonth());
   }

   public static YearMonth of(int var0, Month var1) {
      Objects.requireNonNull(var1, (String)"month");
      return of(var0, var1.getValue());
   }

   public static YearMonth of(int var0, int var1) {
      ChronoField.YEAR.checkValidValue((long)var0);
      ChronoField.MONTH_OF_YEAR.checkValidValue((long)var1);
      return new YearMonth(var0, var1);
   }

   public static YearMonth from(TemporalAccessor var0) {
      if (var0 instanceof YearMonth) {
         return (YearMonth)var0;
      } else {
         Objects.requireNonNull(var0, "temporal");

         try {
            if (!IsoChronology.INSTANCE.equals(Chronology.from((TemporalAccessor)var0))) {
               var0 = LocalDate.from((TemporalAccessor)var0);
            }

            return of(((TemporalAccessor)var0).get(ChronoField.YEAR), ((TemporalAccessor)var0).get(ChronoField.MONTH_OF_YEAR));
         } catch (DateTimeException var2) {
            throw new DateTimeException("Unable to obtain YearMonth from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var2);
         }
      }
   }

   public static YearMonth parse(CharSequence var0) {
      return parse(var0, PARSER);
   }

   public static YearMonth parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (YearMonth)var1.parse(var0, YearMonth::from);
   }

   private YearMonth(int var1, int var2) {
      this.year = var1;
      this.month = var2;
   }

   private YearMonth with(int var1, int var2) {
      return this.year == var1 && this.month == var2 ? this : new YearMonth(var1, var2);
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.YEAR || var1 == ChronoField.MONTH_OF_YEAR || var1 == ChronoField.PROLEPTIC_MONTH || var1 == ChronoField.YEAR_OF_ERA || var1 == ChronoField.ERA;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1 == ChronoUnit.MONTHS || var1 == ChronoUnit.YEARS || var1 == ChronoUnit.DECADES || var1 == ChronoUnit.CENTURIES || var1 == ChronoUnit.MILLENNIA || var1 == ChronoUnit.ERAS;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      if (var1 == ChronoField.YEAR_OF_ERA) {
         return this.getYear() <= 0 ? ValueRange.of(1L, 1000000000L) : ValueRange.of(1L, 999999999L);
      } else {
         return Temporal.super.range(var1);
      }
   }

   public int get(TemporalField var1) {
      return this.range(var1).checkValidIntValue(this.getLong(var1), var1);
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case MONTH_OF_YEAR:
            return (long)this.month;
         case PROLEPTIC_MONTH:
            return this.getProlepticMonth();
         case YEAR_OF_ERA:
            return (long)(this.year < 1 ? 1 - this.year : this.year);
         case YEAR:
            return (long)this.year;
         case ERA:
            return (long)(this.year < 1 ? 0 : 1);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   private long getProlepticMonth() {
      return (long)this.year * 12L + (long)this.month - 1L;
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

   public boolean isLeapYear() {
      return IsoChronology.INSTANCE.isLeapYear((long)this.year);
   }

   public boolean isValidDay(int var1) {
      return var1 >= 1 && var1 <= this.lengthOfMonth();
   }

   public int lengthOfMonth() {
      return this.getMonth().length(this.isLeapYear());
   }

   public int lengthOfYear() {
      return this.isLeapYear() ? 366 : 365;
   }

   public YearMonth with(TemporalAdjuster var1) {
      return (YearMonth)var1.adjustInto(this);
   }

   public YearMonth with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         var4.checkValidValue(var2);
         switch(var4) {
         case MONTH_OF_YEAR:
            return this.withMonth((int)var2);
         case PROLEPTIC_MONTH:
            return this.plusMonths(var2 - this.getProlepticMonth());
         case YEAR_OF_ERA:
            return this.withYear((int)(this.year < 1 ? 1L - var2 : var2));
         case YEAR:
            return this.withYear((int)var2);
         case ERA:
            return this.getLong(ChronoField.ERA) == var2 ? this : this.withYear(1 - this.year);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return (YearMonth)var1.adjustInto(this, var2);
      }
   }

   public YearMonth withYear(int var1) {
      ChronoField.YEAR.checkValidValue((long)var1);
      return this.with(var1, this.month);
   }

   public YearMonth withMonth(int var1) {
      ChronoField.MONTH_OF_YEAR.checkValidValue((long)var1);
      return this.with(this.year, var1);
   }

   public YearMonth plus(TemporalAmount var1) {
      return (YearMonth)var1.addTo(this);
   }

   public YearMonth plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         switch((ChronoUnit)var3) {
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
         return (YearMonth)var3.addTo(this, var1);
      }
   }

   public YearMonth plusYears(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         int var3 = ChronoField.YEAR.checkValidIntValue((long)this.year + var1);
         return this.with(var3, this.month);
      }
   }

   public YearMonth plusMonths(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         long var3 = (long)this.year * 12L + (long)(this.month - 1);
         long var5 = var3 + var1;
         int var7 = ChronoField.YEAR.checkValidIntValue(Math.floorDiv(var5, 12L));
         int var8 = (int)Math.floorMod(var5, 12L) + 1;
         return this.with(var7, var8);
      }
   }

   public YearMonth minus(TemporalAmount var1) {
      return (YearMonth)var1.subtractFrom(this);
   }

   public YearMonth minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public YearMonth minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusYears(Long.MAX_VALUE).plusYears(1L) : this.plusYears(-var1);
   }

   public YearMonth minusMonths(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMonths(Long.MAX_VALUE).plusMonths(1L) : this.plusMonths(-var1);
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 == TemporalQueries.chronology()) {
         return IsoChronology.INSTANCE;
      } else {
         return var1 == TemporalQueries.precision() ? ChronoUnit.MONTHS : Temporal.super.query(var1);
      }
   }

   public Temporal adjustInto(Temporal var1) {
      if (!Chronology.from(var1).equals(IsoChronology.INSTANCE)) {
         throw new DateTimeException("Adjustment only supported on ISO date-time");
      } else {
         return var1.with(ChronoField.PROLEPTIC_MONTH, this.getProlepticMonth());
      }
   }

   public long until(Temporal var1, TemporalUnit var2) {
      YearMonth var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         long var4 = var3.getProlepticMonth() - this.getProlepticMonth();
         switch((ChronoUnit)var2) {
         case MONTHS:
            return var4;
         case YEARS:
            return var4 / 12L;
         case DECADES:
            return var4 / 120L;
         case CENTURIES:
            return var4 / 1200L;
         case MILLENNIA:
            return var4 / 12000L;
         case ERAS:
            return var3.getLong(ChronoField.ERA) - this.getLong(ChronoField.ERA);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var2);
         }
      } else {
         return var2.between(this, var3);
      }
   }

   public String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   public LocalDate atDay(int var1) {
      return LocalDate.of(this.year, this.month, var1);
   }

   public LocalDate atEndOfMonth() {
      return LocalDate.of(this.year, this.month, this.lengthOfMonth());
   }

   public int compareTo(YearMonth var1) {
      int var2 = this.year - var1.year;
      if (var2 == 0) {
         var2 = this.month - var1.month;
      }

      return var2;
   }

   public boolean isAfter(YearMonth var1) {
      return this.compareTo(var1) > 0;
   }

   public boolean isBefore(YearMonth var1) {
      return this.compareTo(var1) < 0;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof YearMonth)) {
         return false;
      } else {
         YearMonth var2 = (YearMonth)var1;
         return this.year == var2.year && this.month == var2.month;
      }
   }

   public int hashCode() {
      return this.year ^ this.month << 27;
   }

   public String toString() {
      int var1 = Math.abs(this.year);
      StringBuilder var2 = new StringBuilder(9);
      if (var1 < 1000) {
         if (this.year < 0) {
            var2.append(this.year - 10000).deleteCharAt(1);
         } else {
            var2.append(this.year + 10000).deleteCharAt(0);
         }
      } else {
         var2.append(this.year);
      }

      return var2.append(this.month < 10 ? "-0" : "-").append(this.month).toString();
   }

   private Object writeReplace() {
      return new Ser((byte)12, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeInt(this.year);
      var1.writeByte(this.month);
   }

   static YearMonth readExternal(DataInput var0) throws IOException {
      int var1 = var0.readInt();
      byte var2 = var0.readByte();
      return of(var1, var2);
   }

   static {
      PARSER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).toFormatter();
   }
}
