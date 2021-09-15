package java.time;

import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.chrono.ChronoZonedDateTime;
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
import java.util.List;
import java.util.Objects;

public final class ZonedDateTime implements Temporal, ChronoZonedDateTime<LocalDate>, Serializable {
   private static final long serialVersionUID = -6260982410461394882L;
   private final LocalDateTime dateTime;
   private final ZoneOffset offset;
   private final ZoneId zone;

   public static ZonedDateTime now() {
      return now(Clock.systemDefaultZone());
   }

   public static ZonedDateTime now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static ZonedDateTime now(Clock var0) {
      Objects.requireNonNull(var0, (String)"clock");
      Instant var1 = var0.instant();
      return ofInstant(var1, var0.getZone());
   }

   public static ZonedDateTime of(LocalDate var0, LocalTime var1, ZoneId var2) {
      return of(LocalDateTime.of(var0, var1), var2);
   }

   public static ZonedDateTime of(LocalDateTime var0, ZoneId var1) {
      return ofLocal(var0, var1, (ZoneOffset)null);
   }

   public static ZonedDateTime of(int var0, int var1, int var2, int var3, int var4, int var5, int var6, ZoneId var7) {
      LocalDateTime var8 = LocalDateTime.of(var0, var1, var2, var3, var4, var5, var6);
      return ofLocal(var8, var7, (ZoneOffset)null);
   }

   public static ZonedDateTime ofLocal(LocalDateTime var0, ZoneId var1, ZoneOffset var2) {
      Objects.requireNonNull(var0, (String)"localDateTime");
      Objects.requireNonNull(var1, (String)"zone");
      if (var1 instanceof ZoneOffset) {
         return new ZonedDateTime(var0, (ZoneOffset)var1, var1);
      } else {
         ZoneRules var3 = var1.getRules();
         List var4 = var3.getValidOffsets(var0);
         ZoneOffset var5;
         if (var4.size() == 1) {
            var5 = (ZoneOffset)var4.get(0);
         } else if (var4.size() == 0) {
            ZoneOffsetTransition var6 = var3.getTransition(var0);
            var0 = var0.plusSeconds(var6.getDuration().getSeconds());
            var5 = var6.getOffsetAfter();
         } else if (var2 != null && var4.contains(var2)) {
            var5 = var2;
         } else {
            var5 = (ZoneOffset)Objects.requireNonNull(var4.get(0), "offset");
         }

         return new ZonedDateTime(var0, var5, var1);
      }
   }

   public static ZonedDateTime ofInstant(Instant var0, ZoneId var1) {
      Objects.requireNonNull(var0, (String)"instant");
      Objects.requireNonNull(var1, (String)"zone");
      return create(var0.getEpochSecond(), var0.getNano(), var1);
   }

   public static ZonedDateTime ofInstant(LocalDateTime var0, ZoneOffset var1, ZoneId var2) {
      Objects.requireNonNull(var0, (String)"localDateTime");
      Objects.requireNonNull(var1, (String)"offset");
      Objects.requireNonNull(var2, (String)"zone");
      return var2.getRules().isValidOffset(var0, var1) ? new ZonedDateTime(var0, var1, var2) : create(var0.toEpochSecond(var1), var0.getNano(), var2);
   }

   private static ZonedDateTime create(long var0, int var2, ZoneId var3) {
      ZoneRules var4 = var3.getRules();
      Instant var5 = Instant.ofEpochSecond(var0, (long)var2);
      ZoneOffset var6 = var4.getOffset(var5);
      LocalDateTime var7 = LocalDateTime.ofEpochSecond(var0, var2, var6);
      return new ZonedDateTime(var7, var6, var3);
   }

   public static ZonedDateTime ofStrict(LocalDateTime var0, ZoneOffset var1, ZoneId var2) {
      Objects.requireNonNull(var0, (String)"localDateTime");
      Objects.requireNonNull(var1, (String)"offset");
      Objects.requireNonNull(var2, (String)"zone");
      ZoneRules var3 = var2.getRules();
      if (!var3.isValidOffset(var0, var1)) {
         ZoneOffsetTransition var4 = var3.getTransition(var0);
         if (var4 != null && var4.isGap()) {
            throw new DateTimeException("LocalDateTime '" + var0 + "' does not exist in zone '" + var2 + "' due to a gap in the local time-line, typically caused by daylight savings");
         } else {
            throw new DateTimeException("ZoneOffset '" + var1 + "' is not valid for LocalDateTime '" + var0 + "' in zone '" + var2 + "'");
         }
      } else {
         return new ZonedDateTime(var0, var1, var2);
      }
   }

   private static ZonedDateTime ofLenient(LocalDateTime var0, ZoneOffset var1, ZoneId var2) {
      Objects.requireNonNull(var0, (String)"localDateTime");
      Objects.requireNonNull(var1, (String)"offset");
      Objects.requireNonNull(var2, (String)"zone");
      if (var2 instanceof ZoneOffset && !var1.equals(var2)) {
         throw new IllegalArgumentException("ZoneId must match ZoneOffset");
      } else {
         return new ZonedDateTime(var0, var1, var2);
      }
   }

   public static ZonedDateTime from(TemporalAccessor var0) {
      if (var0 instanceof ZonedDateTime) {
         return (ZonedDateTime)var0;
      } else {
         try {
            ZoneId var1 = ZoneId.from(var0);
            if (var0.isSupported(ChronoField.INSTANT_SECONDS)) {
               long var6 = var0.getLong(ChronoField.INSTANT_SECONDS);
               int var4 = var0.get(ChronoField.NANO_OF_SECOND);
               return create(var6, var4, var1);
            } else {
               LocalDate var2 = LocalDate.from(var0);
               LocalTime var3 = LocalTime.from(var0);
               return of(var2, var3, var1);
            }
         } catch (DateTimeException var5) {
            throw new DateTimeException("Unable to obtain ZonedDateTime from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var5);
         }
      }
   }

   public static ZonedDateTime parse(CharSequence var0) {
      return parse(var0, DateTimeFormatter.ISO_ZONED_DATE_TIME);
   }

   public static ZonedDateTime parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (ZonedDateTime)var1.parse(var0, ZonedDateTime::from);
   }

   private ZonedDateTime(LocalDateTime var1, ZoneOffset var2, ZoneId var3) {
      this.dateTime = var1;
      this.offset = var2;
      this.zone = var3;
   }

   private ZonedDateTime resolveLocal(LocalDateTime var1) {
      return ofLocal(var1, this.zone, this.offset);
   }

   private ZonedDateTime resolveInstant(LocalDateTime var1) {
      return ofInstant(var1, this.offset, this.zone);
   }

   private ZonedDateTime resolveOffset(ZoneOffset var1) {
      return !var1.equals(this.offset) && this.zone.getRules().isValidOffset(this.dateTime, var1) ? new ZonedDateTime(this.dateTime, var1, this.zone) : this;
   }

   public boolean isSupported(TemporalField var1) {
      return var1 instanceof ChronoField || var1 != null && var1.isSupportedBy(this);
   }

   public boolean isSupported(TemporalUnit var1) {
      return ChronoZonedDateTime.super.isSupported(var1);
   }

   public ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 != ChronoField.INSTANT_SECONDS && var1 != ChronoField.OFFSET_SECONDS ? this.dateTime.range(var1) : var1.range();
      } else {
         return var1.rangeRefinedBy(this);
      }
   }

   public int get(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case INSTANT_SECONDS:
            throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
         case OFFSET_SECONDS:
            return this.getOffset().getTotalSeconds();
         default:
            return this.dateTime.get(var1);
         }
      } else {
         return ChronoZonedDateTime.super.get(var1);
      }
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case INSTANT_SECONDS:
            return this.toEpochSecond();
         case OFFSET_SECONDS:
            return (long)this.getOffset().getTotalSeconds();
         default:
            return this.dateTime.getLong(var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   public ZoneOffset getOffset() {
      return this.offset;
   }

   public ZonedDateTime withEarlierOffsetAtOverlap() {
      ZoneOffsetTransition var1 = this.getZone().getRules().getTransition(this.dateTime);
      if (var1 != null && var1.isOverlap()) {
         ZoneOffset var2 = var1.getOffsetBefore();
         if (!var2.equals(this.offset)) {
            return new ZonedDateTime(this.dateTime, var2, this.zone);
         }
      }

      return this;
   }

   public ZonedDateTime withLaterOffsetAtOverlap() {
      ZoneOffsetTransition var1 = this.getZone().getRules().getTransition(this.toLocalDateTime());
      if (var1 != null) {
         ZoneOffset var2 = var1.getOffsetAfter();
         if (!var2.equals(this.offset)) {
            return new ZonedDateTime(this.dateTime, var2, this.zone);
         }
      }

      return this;
   }

   public ZoneId getZone() {
      return this.zone;
   }

   public ZonedDateTime withZoneSameLocal(ZoneId var1) {
      Objects.requireNonNull(var1, (String)"zone");
      return this.zone.equals(var1) ? this : ofLocal(this.dateTime, var1, this.offset);
   }

   public ZonedDateTime withZoneSameInstant(ZoneId var1) {
      Objects.requireNonNull(var1, (String)"zone");
      return this.zone.equals(var1) ? this : create(this.dateTime.toEpochSecond(this.offset), this.dateTime.getNano(), var1);
   }

   public ZonedDateTime withFixedOffsetZone() {
      return this.zone.equals(this.offset) ? this : new ZonedDateTime(this.dateTime, this.offset, this.offset);
   }

   public LocalDateTime toLocalDateTime() {
      return this.dateTime;
   }

   public LocalDate toLocalDate() {
      return this.dateTime.toLocalDate();
   }

   public int getYear() {
      return this.dateTime.getYear();
   }

   public int getMonthValue() {
      return this.dateTime.getMonthValue();
   }

   public Month getMonth() {
      return this.dateTime.getMonth();
   }

   public int getDayOfMonth() {
      return this.dateTime.getDayOfMonth();
   }

   public int getDayOfYear() {
      return this.dateTime.getDayOfYear();
   }

   public DayOfWeek getDayOfWeek() {
      return this.dateTime.getDayOfWeek();
   }

   public LocalTime toLocalTime() {
      return this.dateTime.toLocalTime();
   }

   public int getHour() {
      return this.dateTime.getHour();
   }

   public int getMinute() {
      return this.dateTime.getMinute();
   }

   public int getSecond() {
      return this.dateTime.getSecond();
   }

   public int getNano() {
      return this.dateTime.getNano();
   }

   public ZonedDateTime with(TemporalAdjuster var1) {
      if (var1 instanceof LocalDate) {
         return this.resolveLocal(LocalDateTime.of((LocalDate)var1, this.dateTime.toLocalTime()));
      } else if (var1 instanceof LocalTime) {
         return this.resolveLocal(LocalDateTime.of(this.dateTime.toLocalDate(), (LocalTime)var1));
      } else if (var1 instanceof LocalDateTime) {
         return this.resolveLocal((LocalDateTime)var1);
      } else if (var1 instanceof OffsetDateTime) {
         OffsetDateTime var3 = (OffsetDateTime)var1;
         return ofLocal(var3.toLocalDateTime(), this.zone, var3.getOffset());
      } else if (var1 instanceof Instant) {
         Instant var2 = (Instant)var1;
         return create(var2.getEpochSecond(), var2.getNano(), this.zone);
      } else {
         return var1 instanceof ZoneOffset ? this.resolveOffset((ZoneOffset)var1) : (ZonedDateTime)var1.adjustInto(this);
      }
   }

   public ZonedDateTime with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         switch(var4) {
         case INSTANT_SECONDS:
            return create(var2, this.getNano(), this.zone);
         case OFFSET_SECONDS:
            ZoneOffset var5 = ZoneOffset.ofTotalSeconds(var4.checkValidIntValue(var2));
            return this.resolveOffset(var5);
         default:
            return this.resolveLocal(this.dateTime.with(var1, var2));
         }
      } else {
         return (ZonedDateTime)var1.adjustInto(this, var2);
      }
   }

   public ZonedDateTime withYear(int var1) {
      return this.resolveLocal(this.dateTime.withYear(var1));
   }

   public ZonedDateTime withMonth(int var1) {
      return this.resolveLocal(this.dateTime.withMonth(var1));
   }

   public ZonedDateTime withDayOfMonth(int var1) {
      return this.resolveLocal(this.dateTime.withDayOfMonth(var1));
   }

   public ZonedDateTime withDayOfYear(int var1) {
      return this.resolveLocal(this.dateTime.withDayOfYear(var1));
   }

   public ZonedDateTime withHour(int var1) {
      return this.resolveLocal(this.dateTime.withHour(var1));
   }

   public ZonedDateTime withMinute(int var1) {
      return this.resolveLocal(this.dateTime.withMinute(var1));
   }

   public ZonedDateTime withSecond(int var1) {
      return this.resolveLocal(this.dateTime.withSecond(var1));
   }

   public ZonedDateTime withNano(int var1) {
      return this.resolveLocal(this.dateTime.withNano(var1));
   }

   public ZonedDateTime truncatedTo(TemporalUnit var1) {
      return this.resolveLocal(this.dateTime.truncatedTo(var1));
   }

   public ZonedDateTime plus(TemporalAmount var1) {
      if (var1 instanceof Period) {
         Period var2 = (Period)var1;
         return this.resolveLocal(this.dateTime.plus(var2));
      } else {
         Objects.requireNonNull(var1, (String)"amountToAdd");
         return (ZonedDateTime)var1.addTo(this);
      }
   }

   public ZonedDateTime plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         return var3.isDateBased() ? this.resolveLocal(this.dateTime.plus(var1, var3)) : this.resolveInstant(this.dateTime.plus(var1, var3));
      } else {
         return (ZonedDateTime)var3.addTo(this, var1);
      }
   }

   public ZonedDateTime plusYears(long var1) {
      return this.resolveLocal(this.dateTime.plusYears(var1));
   }

   public ZonedDateTime plusMonths(long var1) {
      return this.resolveLocal(this.dateTime.plusMonths(var1));
   }

   public ZonedDateTime plusWeeks(long var1) {
      return this.resolveLocal(this.dateTime.plusWeeks(var1));
   }

   public ZonedDateTime plusDays(long var1) {
      return this.resolveLocal(this.dateTime.plusDays(var1));
   }

   public ZonedDateTime plusHours(long var1) {
      return this.resolveInstant(this.dateTime.plusHours(var1));
   }

   public ZonedDateTime plusMinutes(long var1) {
      return this.resolveInstant(this.dateTime.plusMinutes(var1));
   }

   public ZonedDateTime plusSeconds(long var1) {
      return this.resolveInstant(this.dateTime.plusSeconds(var1));
   }

   public ZonedDateTime plusNanos(long var1) {
      return this.resolveInstant(this.dateTime.plusNanos(var1));
   }

   public ZonedDateTime minus(TemporalAmount var1) {
      if (var1 instanceof Period) {
         Period var2 = (Period)var1;
         return this.resolveLocal(this.dateTime.minus(var2));
      } else {
         Objects.requireNonNull(var1, (String)"amountToSubtract");
         return (ZonedDateTime)var1.subtractFrom(this);
      }
   }

   public ZonedDateTime minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public ZonedDateTime minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusYears(Long.MAX_VALUE).plusYears(1L) : this.plusYears(-var1);
   }

   public ZonedDateTime minusMonths(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMonths(Long.MAX_VALUE).plusMonths(1L) : this.plusMonths(-var1);
   }

   public ZonedDateTime minusWeeks(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusWeeks(Long.MAX_VALUE).plusWeeks(1L) : this.plusWeeks(-var1);
   }

   public ZonedDateTime minusDays(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusDays(Long.MAX_VALUE).plusDays(1L) : this.plusDays(-var1);
   }

   public ZonedDateTime minusHours(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusHours(Long.MAX_VALUE).plusHours(1L) : this.plusHours(-var1);
   }

   public ZonedDateTime minusMinutes(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMinutes(Long.MAX_VALUE).plusMinutes(1L) : this.plusMinutes(-var1);
   }

   public ZonedDateTime minusSeconds(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusSeconds(Long.MAX_VALUE).plusSeconds(1L) : this.plusSeconds(-var1);
   }

   public ZonedDateTime minusNanos(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusNanos(Long.MAX_VALUE).plusNanos(1L) : this.plusNanos(-var1);
   }

   public <R> R query(TemporalQuery<R> var1) {
      return var1 == TemporalQueries.localDate() ? this.toLocalDate() : ChronoZonedDateTime.super.query(var1);
   }

   public long until(Temporal var1, TemporalUnit var2) {
      ZonedDateTime var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         var3 = var3.withZoneSameInstant(this.zone);
         return var2.isDateBased() ? this.dateTime.until(var3.dateTime, var2) : this.toOffsetDateTime().until(var3.toOffsetDateTime(), var2);
      } else {
         return var2.between(this, var3);
      }
   }

   public String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   public OffsetDateTime toOffsetDateTime() {
      return OffsetDateTime.of(this.dateTime, this.offset);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ZonedDateTime)) {
         return false;
      } else {
         ZonedDateTime var2 = (ZonedDateTime)var1;
         return this.dateTime.equals(var2.dateTime) && this.offset.equals(var2.offset) && this.zone.equals(var2.zone);
      }
   }

   public int hashCode() {
      return this.dateTime.hashCode() ^ this.offset.hashCode() ^ Integer.rotateLeft(this.zone.hashCode(), 3);
   }

   public String toString() {
      String var1 = this.dateTime.toString() + this.offset.toString();
      if (this.offset != this.zone) {
         var1 = var1 + '[' + this.zone.toString() + ']';
      }

      return var1;
   }

   private Object writeReplace() {
      return new Ser((byte)6, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      this.dateTime.writeExternal(var1);
      this.offset.writeExternal(var1);
      this.zone.write(var1);
   }

   static ZonedDateTime readExternal(ObjectInput var0) throws IOException, ClassNotFoundException {
      LocalDateTime var1 = LocalDateTime.readExternal(var0);
      ZoneOffset var2 = ZoneOffset.readExternal(var0);
      ZoneId var3 = (ZoneId)Ser.read(var0);
      return ofLenient(var1, var2, var3);
   }
}
