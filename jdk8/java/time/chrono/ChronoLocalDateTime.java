package java.time.chrono;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
import java.util.Comparator;
import java.util.Objects;

public interface ChronoLocalDateTime<D extends ChronoLocalDate> extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDateTime<?>> {
   static Comparator<ChronoLocalDateTime<?>> timeLineOrder() {
      return AbstractChronology.DATE_TIME_ORDER;
   }

   static ChronoLocalDateTime<?> from(TemporalAccessor var0) {
      if (var0 instanceof ChronoLocalDateTime) {
         return (ChronoLocalDateTime)var0;
      } else {
         Objects.requireNonNull(var0, (String)"temporal");
         Chronology var1 = (Chronology)var0.query(TemporalQueries.chronology());
         if (var1 == null) {
            throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + var0.getClass());
         } else {
            return var1.localDateTime(var0);
         }
      }
   }

   default Chronology getChronology() {
      return this.toLocalDate().getChronology();
   }

   D toLocalDate();

   LocalTime toLocalTime();

   boolean isSupported(TemporalField var1);

   default boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1 != ChronoUnit.FOREVER;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   default ChronoLocalDateTime<D> with(TemporalAdjuster var1) {
      return ChronoLocalDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.with(var1));
   }

   ChronoLocalDateTime<D> with(TemporalField var1, long var2);

   default ChronoLocalDateTime<D> plus(TemporalAmount var1) {
      return ChronoLocalDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.plus(var1));
   }

   ChronoLocalDateTime<D> plus(long var1, TemporalUnit var3);

   default ChronoLocalDateTime<D> minus(TemporalAmount var1) {
      return ChronoLocalDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.minus(var1));
   }

   default ChronoLocalDateTime<D> minus(long var1, TemporalUnit var3) {
      return ChronoLocalDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.minus(var1, var3));
   }

   default <R> R query(TemporalQuery<R> var1) {
      if (var1 != TemporalQueries.zoneId() && var1 != TemporalQueries.zone() && var1 != TemporalQueries.offset()) {
         if (var1 == TemporalQueries.localTime()) {
            return this.toLocalTime();
         } else if (var1 == TemporalQueries.chronology()) {
            return this.getChronology();
         } else {
            return var1 == TemporalQueries.precision() ? ChronoUnit.NANOS : var1.queryFrom(this);
         }
      } else {
         return null;
      }
   }

   default Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.EPOCH_DAY, this.toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, this.toLocalTime().toNanoOfDay());
   }

   default String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   ChronoZonedDateTime<D> atZone(ZoneId var1);

   default Instant toInstant(ZoneOffset var1) {
      return Instant.ofEpochSecond(this.toEpochSecond(var1), (long)this.toLocalTime().getNano());
   }

   default long toEpochSecond(ZoneOffset var1) {
      Objects.requireNonNull(var1, (String)"offset");
      long var2 = this.toLocalDate().toEpochDay();
      long var4 = var2 * 86400L + (long)this.toLocalTime().toSecondOfDay();
      var4 -= (long)var1.getTotalSeconds();
      return var4;
   }

   default int compareTo(ChronoLocalDateTime<?> var1) {
      int var2 = this.toLocalDate().compareTo(var1.toLocalDate());
      if (var2 == 0) {
         var2 = this.toLocalTime().compareTo(var1.toLocalTime());
         if (var2 == 0) {
            var2 = this.getChronology().compareTo(var1.getChronology());
         }
      }

      return var2;
   }

   default boolean isAfter(ChronoLocalDateTime<?> var1) {
      long var2 = this.toLocalDate().toEpochDay();
      long var4 = var1.toLocalDate().toEpochDay();
      return var2 > var4 || var2 == var4 && this.toLocalTime().toNanoOfDay() > var1.toLocalTime().toNanoOfDay();
   }

   default boolean isBefore(ChronoLocalDateTime<?> var1) {
      long var2 = this.toLocalDate().toEpochDay();
      long var4 = var1.toLocalDate().toEpochDay();
      return var2 < var4 || var2 == var4 && this.toLocalTime().toNanoOfDay() < var1.toLocalTime().toNanoOfDay();
   }

   default boolean isEqual(ChronoLocalDateTime<?> var1) {
      return this.toLocalTime().toNanoOfDay() == var1.toLocalTime().toNanoOfDay() && this.toLocalDate().toEpochDay() == var1.toLocalDate().toEpochDay();
   }

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
