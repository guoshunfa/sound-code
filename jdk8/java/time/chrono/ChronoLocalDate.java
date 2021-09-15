package java.time.chrono;

import java.time.DateTimeException;
import java.time.LocalTime;
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
import java.util.Comparator;
import java.util.Objects;

public interface ChronoLocalDate extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDate> {
   static Comparator<ChronoLocalDate> timeLineOrder() {
      return AbstractChronology.DATE_ORDER;
   }

   static ChronoLocalDate from(TemporalAccessor var0) {
      if (var0 instanceof ChronoLocalDate) {
         return (ChronoLocalDate)var0;
      } else {
         Objects.requireNonNull(var0, (String)"temporal");
         Chronology var1 = (Chronology)var0.query(TemporalQueries.chronology());
         if (var1 == null) {
            throw new DateTimeException("Unable to obtain ChronoLocalDate from TemporalAccessor: " + var0.getClass());
         } else {
            return var1.date(var0);
         }
      }
   }

   Chronology getChronology();

   default Era getEra() {
      return this.getChronology().eraOf(this.get(ChronoField.ERA));
   }

   default boolean isLeapYear() {
      return this.getChronology().isLeapYear(this.getLong(ChronoField.YEAR));
   }

   int lengthOfMonth();

   default int lengthOfYear() {
      return this.isLeapYear() ? 366 : 365;
   }

   default boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1.isDateBased();
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   default boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1.isDateBased();
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   default ChronoLocalDate with(TemporalAdjuster var1) {
      return ChronoLocalDateImpl.ensureValid(this.getChronology(), Temporal.super.with(var1));
   }

   default ChronoLocalDate with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      } else {
         return ChronoLocalDateImpl.ensureValid(this.getChronology(), var1.adjustInto(this, var2));
      }
   }

   default ChronoLocalDate plus(TemporalAmount var1) {
      return ChronoLocalDateImpl.ensureValid(this.getChronology(), Temporal.super.plus(var1));
   }

   default ChronoLocalDate plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         throw new UnsupportedTemporalTypeException("Unsupported unit: " + var3);
      } else {
         return ChronoLocalDateImpl.ensureValid(this.getChronology(), var3.addTo(this, var1));
      }
   }

   default ChronoLocalDate minus(TemporalAmount var1) {
      return ChronoLocalDateImpl.ensureValid(this.getChronology(), Temporal.super.minus(var1));
   }

   default ChronoLocalDate minus(long var1, TemporalUnit var3) {
      return ChronoLocalDateImpl.ensureValid(this.getChronology(), Temporal.super.minus(var1, var3));
   }

   default <R> R query(TemporalQuery<R> var1) {
      if (var1 != TemporalQueries.zoneId() && var1 != TemporalQueries.zone() && var1 != TemporalQueries.offset()) {
         if (var1 == TemporalQueries.localTime()) {
            return null;
         } else if (var1 == TemporalQueries.chronology()) {
            return this.getChronology();
         } else {
            return var1 == TemporalQueries.precision() ? ChronoUnit.DAYS : var1.queryFrom(this);
         }
      } else {
         return null;
      }
   }

   default Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.EPOCH_DAY, this.toEpochDay());
   }

   long until(Temporal var1, TemporalUnit var2);

   ChronoPeriod until(ChronoLocalDate var1);

   default String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   default ChronoLocalDateTime<?> atTime(LocalTime var1) {
      return ChronoLocalDateTimeImpl.of(this, var1);
   }

   default long toEpochDay() {
      return this.getLong(ChronoField.EPOCH_DAY);
   }

   default int compareTo(ChronoLocalDate var1) {
      int var2 = Long.compare(this.toEpochDay(), var1.toEpochDay());
      if (var2 == 0) {
         var2 = this.getChronology().compareTo(var1.getChronology());
      }

      return var2;
   }

   default boolean isAfter(ChronoLocalDate var1) {
      return this.toEpochDay() > var1.toEpochDay();
   }

   default boolean isBefore(ChronoLocalDate var1) {
      return this.toEpochDay() < var1.toEpochDay();
   }

   default boolean isEqual(ChronoLocalDate var1) {
      return this.toEpochDay() == var1.toEpochDay();
   }

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
