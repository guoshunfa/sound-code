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

public final class Year implements Temporal, TemporalAdjuster, Comparable<Year>, Serializable {
   public static final int MIN_VALUE = -999999999;
   public static final int MAX_VALUE = 999999999;
   private static final long serialVersionUID = -23038383694477807L;
   private static final DateTimeFormatter PARSER;
   private final int year;

   public static Year now() {
      return now(Clock.systemDefaultZone());
   }

   public static Year now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static Year now(Clock var0) {
      LocalDate var1 = LocalDate.now(var0);
      return of(var1.getYear());
   }

   public static Year of(int var0) {
      ChronoField.YEAR.checkValidValue((long)var0);
      return new Year(var0);
   }

   public static Year from(TemporalAccessor var0) {
      if (var0 instanceof Year) {
         return (Year)var0;
      } else {
         Objects.requireNonNull(var0, "temporal");

         try {
            if (!IsoChronology.INSTANCE.equals(Chronology.from((TemporalAccessor)var0))) {
               var0 = LocalDate.from((TemporalAccessor)var0);
            }

            return of(((TemporalAccessor)var0).get(ChronoField.YEAR));
         } catch (DateTimeException var2) {
            throw new DateTimeException("Unable to obtain Year from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var2);
         }
      }
   }

   public static Year parse(CharSequence var0) {
      return parse(var0, PARSER);
   }

   public static Year parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (Year)var1.parse(var0, Year::from);
   }

   public static boolean isLeap(long var0) {
      return (var0 & 3L) == 0L && (var0 % 100L != 0L || var0 % 400L == 0L);
   }

   private Year(int var1) {
      this.year = var1;
   }

   public int getValue() {
      return this.year;
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.YEAR || var1 == ChronoField.YEAR_OF_ERA || var1 == ChronoField.ERA;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1 == ChronoUnit.YEARS || var1 == ChronoUnit.DECADES || var1 == ChronoUnit.CENTURIES || var1 == ChronoUnit.MILLENNIA || var1 == ChronoUnit.ERAS;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      if (var1 == ChronoField.YEAR_OF_ERA) {
         return this.year <= 0 ? ValueRange.of(1L, 1000000000L) : ValueRange.of(1L, 999999999L);
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

   public boolean isLeap() {
      return isLeap((long)this.year);
   }

   public boolean isValidMonthDay(MonthDay var1) {
      return var1 != null && var1.isValidYear(this.year);
   }

   public int length() {
      return this.isLeap() ? 366 : 365;
   }

   public Year with(TemporalAdjuster var1) {
      return (Year)var1.adjustInto(this);
   }

   public Year with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         var4.checkValidValue(var2);
         switch(var4) {
         case YEAR_OF_ERA:
            return of((int)(this.year < 1 ? 1L - var2 : var2));
         case YEAR:
            return of((int)var2);
         case ERA:
            return this.getLong(ChronoField.ERA) == var2 ? this : of(1 - this.year);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return (Year)var1.adjustInto(this, var2);
      }
   }

   public Year plus(TemporalAmount var1) {
      return (Year)var1.addTo(this);
   }

   public Year plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         switch((ChronoUnit)var3) {
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
         return (Year)var3.addTo(this, var1);
      }
   }

   public Year plusYears(long var1) {
      return var1 == 0L ? this : of(ChronoField.YEAR.checkValidIntValue((long)this.year + var1));
   }

   public Year minus(TemporalAmount var1) {
      return (Year)var1.subtractFrom(this);
   }

   public Year minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public Year minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusYears(Long.MAX_VALUE).plusYears(1L) : this.plusYears(-var1);
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 == TemporalQueries.chronology()) {
         return IsoChronology.INSTANCE;
      } else {
         return var1 == TemporalQueries.precision() ? ChronoUnit.YEARS : Temporal.super.query(var1);
      }
   }

   public Temporal adjustInto(Temporal var1) {
      if (!Chronology.from(var1).equals(IsoChronology.INSTANCE)) {
         throw new DateTimeException("Adjustment only supported on ISO date-time");
      } else {
         return var1.with(ChronoField.YEAR, (long)this.year);
      }
   }

   public long until(Temporal var1, TemporalUnit var2) {
      Year var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         long var4 = (long)var3.year - (long)this.year;
         switch((ChronoUnit)var2) {
         case YEARS:
            return var4;
         case DECADES:
            return var4 / 10L;
         case CENTURIES:
            return var4 / 100L;
         case MILLENNIA:
            return var4 / 1000L;
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
      return LocalDate.ofYearDay(this.year, var1);
   }

   public YearMonth atMonth(Month var1) {
      return YearMonth.of(this.year, var1);
   }

   public YearMonth atMonth(int var1) {
      return YearMonth.of(this.year, var1);
   }

   public LocalDate atMonthDay(MonthDay var1) {
      return var1.atYear(this.year);
   }

   public int compareTo(Year var1) {
      return this.year - var1.year;
   }

   public boolean isAfter(Year var1) {
      return this.year > var1.year;
   }

   public boolean isBefore(Year var1) {
      return this.year < var1.year;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof Year) {
         return this.year == ((Year)var1).year;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.year;
   }

   public String toString() {
      return Integer.toString(this.year);
   }

   private Object writeReplace() {
      return new Ser((byte)11, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeInt(this.year);
   }

   static Year readExternal(DataInput var0) throws IOException {
      return of(var0.readInt());
   }

   static {
      PARSER = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).toFormatter();
   }
}
