package java.time.chrono;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface Chronology extends Comparable<Chronology> {
   static Chronology from(TemporalAccessor var0) {
      Objects.requireNonNull(var0, (String)"temporal");
      Chronology var1 = (Chronology)var0.query(TemporalQueries.chronology());
      return (Chronology)(var1 != null ? var1 : IsoChronology.INSTANCE);
   }

   static Chronology ofLocale(Locale var0) {
      return AbstractChronology.ofLocale(var0);
   }

   static Chronology of(String var0) {
      return AbstractChronology.of(var0);
   }

   static Set<Chronology> getAvailableChronologies() {
      return AbstractChronology.getAvailableChronologies();
   }

   String getId();

   String getCalendarType();

   default ChronoLocalDate date(Era var1, int var2, int var3, int var4) {
      return this.date(this.prolepticYear(var1, var2), var3, var4);
   }

   ChronoLocalDate date(int var1, int var2, int var3);

   default ChronoLocalDate dateYearDay(Era var1, int var2, int var3) {
      return this.dateYearDay(this.prolepticYear(var1, var2), var3);
   }

   ChronoLocalDate dateYearDay(int var1, int var2);

   ChronoLocalDate dateEpochDay(long var1);

   default ChronoLocalDate dateNow() {
      return this.dateNow(Clock.systemDefaultZone());
   }

   default ChronoLocalDate dateNow(ZoneId var1) {
      return this.dateNow(Clock.system(var1));
   }

   default ChronoLocalDate dateNow(Clock var1) {
      Objects.requireNonNull(var1, (String)"clock");
      return this.date(LocalDate.now(var1));
   }

   ChronoLocalDate date(TemporalAccessor var1);

   default ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor var1) {
      try {
         return this.date(var1).atTime(LocalTime.from(var1));
      } catch (DateTimeException var3) {
         throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + var1.getClass(), var3);
      }
   }

   default ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor var1) {
      try {
         ZoneId var2 = ZoneId.from(var1);

         try {
            Instant var3 = Instant.from(var1);
            return this.zonedDateTime(var3, var2);
         } catch (DateTimeException var5) {
            ChronoLocalDateTimeImpl var4 = ChronoLocalDateTimeImpl.ensureValid(this, this.localDateTime(var1));
            return ChronoZonedDateTimeImpl.ofBest(var4, var2, (ZoneOffset)null);
         }
      } catch (DateTimeException var6) {
         throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + var1.getClass(), var6);
      }
   }

   default ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant var1, ZoneId var2) {
      return ChronoZonedDateTimeImpl.ofInstant(this, var1, var2);
   }

   boolean isLeapYear(long var1);

   int prolepticYear(Era var1, int var2);

   Era eraOf(int var1);

   List<Era> eras();

   ValueRange range(ChronoField var1);

   default String getDisplayName(TextStyle var1, Locale var2) {
      TemporalAccessor var3 = new TemporalAccessor() {
         public boolean isSupported(TemporalField var1) {
            return false;
         }

         public long getLong(TemporalField var1) {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }

         public <R> R query(TemporalQuery<R> var1) {
            return var1 == TemporalQueries.chronology() ? Chronology.this : TemporalAccessor.super.query(var1);
         }
      };
      return (new DateTimeFormatterBuilder()).appendChronologyText(var1).toFormatter(var2).format(var3);
   }

   ChronoLocalDate resolveDate(Map<TemporalField, Long> var1, ResolverStyle var2);

   default ChronoPeriod period(int var1, int var2, int var3) {
      return new ChronoPeriodImpl(this, var1, var2, var3);
   }

   int compareTo(Chronology var1);

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
