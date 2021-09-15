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
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

public final class MonthDay implements TemporalAccessor, TemporalAdjuster, Comparable<MonthDay>, Serializable {
   private static final long serialVersionUID = -939150713474957432L;
   private static final DateTimeFormatter PARSER;
   private final int month;
   private final int day;

   public static MonthDay now() {
      return now(Clock.systemDefaultZone());
   }

   public static MonthDay now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static MonthDay now(Clock var0) {
      LocalDate var1 = LocalDate.now(var0);
      return of(var1.getMonth(), var1.getDayOfMonth());
   }

   public static MonthDay of(Month var0, int var1) {
      Objects.requireNonNull(var0, (String)"month");
      ChronoField.DAY_OF_MONTH.checkValidValue((long)var1);
      if (var1 > var0.maxLength()) {
         throw new DateTimeException("Illegal value for DayOfMonth field, value " + var1 + " is not valid for month " + var0.name());
      } else {
         return new MonthDay(var0.getValue(), var1);
      }
   }

   public static MonthDay of(int var0, int var1) {
      return of(Month.of(var0), var1);
   }

   public static MonthDay from(TemporalAccessor var0) {
      if (var0 instanceof MonthDay) {
         return (MonthDay)var0;
      } else {
         try {
            if (!IsoChronology.INSTANCE.equals(Chronology.from((TemporalAccessor)var0))) {
               var0 = LocalDate.from((TemporalAccessor)var0);
            }

            return of(((TemporalAccessor)var0).get(ChronoField.MONTH_OF_YEAR), ((TemporalAccessor)var0).get(ChronoField.DAY_OF_MONTH));
         } catch (DateTimeException var2) {
            throw new DateTimeException("Unable to obtain MonthDay from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var2);
         }
      }
   }

   public static MonthDay parse(CharSequence var0) {
      return parse(var0, PARSER);
   }

   public static MonthDay parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (MonthDay)var1.parse(var0, MonthDay::from);
   }

   private MonthDay(int var1, int var2) {
      this.month = var1;
      this.day = var2;
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.MONTH_OF_YEAR || var1 == ChronoField.DAY_OF_MONTH;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      if (var1 == ChronoField.MONTH_OF_YEAR) {
         return var1.range();
      } else {
         return var1 == ChronoField.DAY_OF_MONTH ? ValueRange.of(1L, (long)this.getMonth().minLength(), (long)this.getMonth().maxLength()) : TemporalAccessor.super.range(var1);
      }
   }

   public int get(TemporalField var1) {
      return this.range(var1).checkValidIntValue(this.getLong(var1), var1);
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case DAY_OF_MONTH:
            return (long)this.day;
         case MONTH_OF_YEAR:
            return (long)this.month;
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return var1.getFrom(this);
      }
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

   public boolean isValidYear(int var1) {
      return this.day != 29 || this.month != 2 || Year.isLeap((long)var1);
   }

   public MonthDay withMonth(int var1) {
      return this.with(Month.of(var1));
   }

   public MonthDay with(Month var1) {
      Objects.requireNonNull(var1, (String)"month");
      if (var1.getValue() == this.month) {
         return this;
      } else {
         int var2 = Math.min(this.day, var1.maxLength());
         return new MonthDay(var1.getValue(), var2);
      }
   }

   public MonthDay withDayOfMonth(int var1) {
      return var1 == this.day ? this : of(this.month, var1);
   }

   public <R> R query(TemporalQuery<R> var1) {
      return var1 == TemporalQueries.chronology() ? IsoChronology.INSTANCE : TemporalAccessor.super.query(var1);
   }

   public Temporal adjustInto(Temporal var1) {
      if (!Chronology.from(var1).equals(IsoChronology.INSTANCE)) {
         throw new DateTimeException("Adjustment only supported on ISO date-time");
      } else {
         var1 = var1.with(ChronoField.MONTH_OF_YEAR, (long)this.month);
         return var1.with(ChronoField.DAY_OF_MONTH, Math.min(var1.range(ChronoField.DAY_OF_MONTH).getMaximum(), (long)this.day));
      }
   }

   public String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   public LocalDate atYear(int var1) {
      return LocalDate.of(var1, this.month, this.isValidYear(var1) ? this.day : 28);
   }

   public int compareTo(MonthDay var1) {
      int var2 = this.month - var1.month;
      if (var2 == 0) {
         var2 = this.day - var1.day;
      }

      return var2;
   }

   public boolean isAfter(MonthDay var1) {
      return this.compareTo(var1) > 0;
   }

   public boolean isBefore(MonthDay var1) {
      return this.compareTo(var1) < 0;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MonthDay)) {
         return false;
      } else {
         MonthDay var2 = (MonthDay)var1;
         return this.month == var2.month && this.day == var2.day;
      }
   }

   public int hashCode() {
      return (this.month << 6) + this.day;
   }

   public String toString() {
      return (new StringBuilder(10)).append("--").append(this.month < 10 ? "0" : "").append(this.month).append(this.day < 10 ? "-0" : "-").append(this.day).toString();
   }

   private Object writeReplace() {
      return new Ser((byte)13, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeByte(this.month);
      var1.writeByte(this.day);
   }

   static MonthDay readExternal(DataInput var0) throws IOException {
      byte var1 = var0.readByte();
      byte var2 = var0.readByte();
      return of(var1, var2);
   }

   static {
      PARSER = (new DateTimeFormatterBuilder()).appendLiteral("--").appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).toFormatter();
   }
}
