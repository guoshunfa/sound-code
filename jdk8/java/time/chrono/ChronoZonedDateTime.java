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
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.Objects;

public interface ChronoZonedDateTime<D extends ChronoLocalDate> extends Temporal, Comparable<ChronoZonedDateTime<?>> {
   static Comparator<ChronoZonedDateTime<?>> timeLineOrder() {
      return AbstractChronology.INSTANT_ORDER;
   }

   static ChronoZonedDateTime<?> from(TemporalAccessor var0) {
      if (var0 instanceof ChronoZonedDateTime) {
         return (ChronoZonedDateTime)var0;
      } else {
         Objects.requireNonNull(var0, (String)"temporal");
         Chronology var1 = (Chronology)var0.query(TemporalQueries.chronology());
         if (var1 == null) {
            throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + var0.getClass());
         } else {
            return var1.zonedDateTime(var0);
         }
      }
   }

   default ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 != ChronoField.INSTANT_SECONDS && var1 != ChronoField.OFFSET_SECONDS ? this.toLocalDateTime().range(var1) : var1.range();
      } else {
         return var1.rangeRefinedBy(this);
      }
   }

   default int get(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case INSTANT_SECONDS:
            throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
         case OFFSET_SECONDS:
            return this.getOffset().getTotalSeconds();
         default:
            return this.toLocalDateTime().get(var1);
         }
      } else {
         return Temporal.super.get(var1);
      }
   }

   default long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case INSTANT_SECONDS:
            return this.toEpochSecond();
         case OFFSET_SECONDS:
            return (long)this.getOffset().getTotalSeconds();
         default:
            return this.toLocalDateTime().getLong(var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   default D toLocalDate() {
      return this.toLocalDateTime().toLocalDate();
   }

   default LocalTime toLocalTime() {
      return this.toLocalDateTime().toLocalTime();
   }

   ChronoLocalDateTime<D> toLocalDateTime();

   default Chronology getChronology() {
      return this.toLocalDate().getChronology();
   }

   ZoneOffset getOffset();

   ZoneId getZone();

   ChronoZonedDateTime<D> withEarlierOffsetAtOverlap();

   ChronoZonedDateTime<D> withLaterOffsetAtOverlap();

   ChronoZonedDateTime<D> withZoneSameLocal(ZoneId var1);

   ChronoZonedDateTime<D> withZoneSameInstant(ZoneId var1);

   boolean isSupported(TemporalField var1);

   default boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1 != ChronoUnit.FOREVER;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   default ChronoZonedDateTime<D> with(TemporalAdjuster var1) {
      return ChronoZonedDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.with(var1));
   }

   ChronoZonedDateTime<D> with(TemporalField var1, long var2);

   default ChronoZonedDateTime<D> plus(TemporalAmount var1) {
      return ChronoZonedDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.plus(var1));
   }

   ChronoZonedDateTime<D> plus(long var1, TemporalUnit var3);

   default ChronoZonedDateTime<D> minus(TemporalAmount var1) {
      return ChronoZonedDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.minus(var1));
   }

   default ChronoZonedDateTime<D> minus(long var1, TemporalUnit var3) {
      return ChronoZonedDateTimeImpl.ensureValid(this.getChronology(), Temporal.super.minus(var1, var3));
   }

   default <R> R query(TemporalQuery<R> var1) {
      if (var1 != TemporalQueries.zone() && var1 != TemporalQueries.zoneId()) {
         if (var1 == TemporalQueries.offset()) {
            return this.getOffset();
         } else if (var1 == TemporalQueries.localTime()) {
            return this.toLocalTime();
         } else if (var1 == TemporalQueries.chronology()) {
            return this.getChronology();
         } else {
            return var1 == TemporalQueries.precision() ? ChronoUnit.NANOS : var1.queryFrom(this);
         }
      } else {
         return this.getZone();
      }
   }

   default String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   default Instant toInstant() {
      return Instant.ofEpochSecond(this.toEpochSecond(), (long)this.toLocalTime().getNano());
   }

   default long toEpochSecond() {
      long var1 = this.toLocalDate().toEpochDay();
      long var3 = var1 * 86400L + (long)this.toLocalTime().toSecondOfDay();
      var3 -= (long)this.getOffset().getTotalSeconds();
      return var3;
   }

   default int compareTo(ChronoZonedDateTime<?> var1) {
      int var2 = Long.compare(this.toEpochSecond(), var1.toEpochSecond());
      if (var2 == 0) {
         var2 = this.toLocalTime().getNano() - var1.toLocalTime().getNano();
         if (var2 == 0) {
            var2 = this.toLocalDateTime().compareTo(var1.toLocalDateTime());
            if (var2 == 0) {
               var2 = this.getZone().getId().compareTo(var1.getZone().getId());
               if (var2 == 0) {
                  var2 = this.getChronology().compareTo(var1.getChronology());
               }
            }
         }
      }

      return var2;
   }

   default boolean isBefore(ChronoZonedDateTime<?> var1) {
      long var2 = this.toEpochSecond();
      long var4 = var1.toEpochSecond();
      return var2 < var4 || var2 == var4 && this.toLocalTime().getNano() < var1.toLocalTime().getNano();
   }

   default boolean isAfter(ChronoZonedDateTime<?> var1) {
      long var2 = this.toEpochSecond();
      long var4 = var1.toEpochSecond();
      return var2 > var4 || var2 == var4 && this.toLocalTime().getNano() > var1.toLocalTime().getNano();
   }

   default boolean isEqual(ChronoZonedDateTime<?> var1) {
      return this.toEpochSecond() == var1.toEpochSecond() && this.toLocalTime().getNano() == var1.toLocalTime().getNano();
   }

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
