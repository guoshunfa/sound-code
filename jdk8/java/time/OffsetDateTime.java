package java.time;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
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
import java.time.zone.ZoneRules;
import java.util.Comparator;
import java.util.Objects;

public final class OffsetDateTime implements Temporal, TemporalAdjuster, Comparable<OffsetDateTime>, Serializable {
   public static final OffsetDateTime MIN;
   public static final OffsetDateTime MAX;
   private static final long serialVersionUID = 2287754244819255394L;
   private final LocalDateTime dateTime;
   private final ZoneOffset offset;

   public static Comparator<OffsetDateTime> timeLineOrder() {
      return OffsetDateTime::compareInstant;
   }

   private static int compareInstant(OffsetDateTime var0, OffsetDateTime var1) {
      if (var0.getOffset().equals(var1.getOffset())) {
         return var0.toLocalDateTime().compareTo((ChronoLocalDateTime)var1.toLocalDateTime());
      } else {
         int var2 = Long.compare(var0.toEpochSecond(), var1.toEpochSecond());
         if (var2 == 0) {
            var2 = var0.toLocalTime().getNano() - var1.toLocalTime().getNano();
         }

         return var2;
      }
   }

   public static OffsetDateTime now() {
      return now(Clock.systemDefaultZone());
   }

   public static OffsetDateTime now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static OffsetDateTime now(Clock var0) {
      Objects.requireNonNull(var0, (String)"clock");
      Instant var1 = var0.instant();
      return ofInstant(var1, var0.getZone().getRules().getOffset(var1));
   }

   public static OffsetDateTime of(LocalDate var0, LocalTime var1, ZoneOffset var2) {
      LocalDateTime var3 = LocalDateTime.of(var0, var1);
      return new OffsetDateTime(var3, var2);
   }

   public static OffsetDateTime of(LocalDateTime var0, ZoneOffset var1) {
      return new OffsetDateTime(var0, var1);
   }

   public static OffsetDateTime of(int var0, int var1, int var2, int var3, int var4, int var5, int var6, ZoneOffset var7) {
      LocalDateTime var8 = LocalDateTime.of(var0, var1, var2, var3, var4, var5, var6);
      return new OffsetDateTime(var8, var7);
   }

   public static OffsetDateTime ofInstant(Instant var0, ZoneId var1) {
      Objects.requireNonNull(var0, (String)"instant");
      Objects.requireNonNull(var1, (String)"zone");
      ZoneRules var2 = var1.getRules();
      ZoneOffset var3 = var2.getOffset(var0);
      LocalDateTime var4 = LocalDateTime.ofEpochSecond(var0.getEpochSecond(), var0.getNano(), var3);
      return new OffsetDateTime(var4, var3);
   }

   public static OffsetDateTime from(TemporalAccessor var0) {
      if (var0 instanceof OffsetDateTime) {
         return (OffsetDateTime)var0;
      } else {
         try {
            ZoneOffset var1 = ZoneOffset.from(var0);
            LocalDate var2 = (LocalDate)var0.query(TemporalQueries.localDate());
            LocalTime var3 = (LocalTime)var0.query(TemporalQueries.localTime());
            if (var2 != null && var3 != null) {
               return of(var2, var3, var1);
            } else {
               Instant var4 = Instant.from(var0);
               return ofInstant(var4, var1);
            }
         } catch (DateTimeException var5) {
            throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var5);
         }
      }
   }

   public static OffsetDateTime parse(CharSequence var0) {
      return parse(var0, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
   }

   public static OffsetDateTime parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (OffsetDateTime)var1.parse(var0, OffsetDateTime::from);
   }

   private OffsetDateTime(LocalDateTime var1, ZoneOffset var2) {
      this.dateTime = (LocalDateTime)Objects.requireNonNull(var1, (String)"dateTime");
      this.offset = (ZoneOffset)Objects.requireNonNull(var2, (String)"offset");
   }

   private OffsetDateTime with(LocalDateTime var1, ZoneOffset var2) {
      return this.dateTime == var1 && this.offset.equals(var2) ? this : new OffsetDateTime(var1, var2);
   }

   public boolean isSupported(TemporalField var1) {
      return var1 instanceof ChronoField || var1 != null && var1.isSupportedBy(this);
   }

   public boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1 != ChronoUnit.FOREVER;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
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
         return Temporal.super.get(var1);
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

   public OffsetDateTime withOffsetSameLocal(ZoneOffset var1) {
      return this.with(this.dateTime, var1);
   }

   public OffsetDateTime withOffsetSameInstant(ZoneOffset var1) {
      if (var1.equals(this.offset)) {
         return this;
      } else {
         int var2 = var1.getTotalSeconds() - this.offset.getTotalSeconds();
         LocalDateTime var3 = this.dateTime.plusSeconds((long)var2);
         return new OffsetDateTime(var3, var1);
      }
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

   public OffsetDateTime with(TemporalAdjuster var1) {
      if (!(var1 instanceof LocalDate) && !(var1 instanceof LocalTime) && !(var1 instanceof LocalDateTime)) {
         if (var1 instanceof Instant) {
            return ofInstant((Instant)var1, this.offset);
         } else if (var1 instanceof ZoneOffset) {
            return this.with(this.dateTime, (ZoneOffset)var1);
         } else {
            return var1 instanceof OffsetDateTime ? (OffsetDateTime)var1 : (OffsetDateTime)var1.adjustInto(this);
         }
      } else {
         return this.with(this.dateTime.with(var1), this.offset);
      }
   }

   public OffsetDateTime with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         switch(var4) {
         case INSTANT_SECONDS:
            return ofInstant(Instant.ofEpochSecond(var2, (long)this.getNano()), this.offset);
         case OFFSET_SECONDS:
            return this.with(this.dateTime, ZoneOffset.ofTotalSeconds(var4.checkValidIntValue(var2)));
         default:
            return this.with(this.dateTime.with(var1, var2), this.offset);
         }
      } else {
         return (OffsetDateTime)var1.adjustInto(this, var2);
      }
   }

   public OffsetDateTime withYear(int var1) {
      return this.with(this.dateTime.withYear(var1), this.offset);
   }

   public OffsetDateTime withMonth(int var1) {
      return this.with(this.dateTime.withMonth(var1), this.offset);
   }

   public OffsetDateTime withDayOfMonth(int var1) {
      return this.with(this.dateTime.withDayOfMonth(var1), this.offset);
   }

   public OffsetDateTime withDayOfYear(int var1) {
      return this.with(this.dateTime.withDayOfYear(var1), this.offset);
   }

   public OffsetDateTime withHour(int var1) {
      return this.with(this.dateTime.withHour(var1), this.offset);
   }

   public OffsetDateTime withMinute(int var1) {
      return this.with(this.dateTime.withMinute(var1), this.offset);
   }

   public OffsetDateTime withSecond(int var1) {
      return this.with(this.dateTime.withSecond(var1), this.offset);
   }

   public OffsetDateTime withNano(int var1) {
      return this.with(this.dateTime.withNano(var1), this.offset);
   }

   public OffsetDateTime truncatedTo(TemporalUnit var1) {
      return this.with(this.dateTime.truncatedTo(var1), this.offset);
   }

   public OffsetDateTime plus(TemporalAmount var1) {
      return (OffsetDateTime)var1.addTo(this);
   }

   public OffsetDateTime plus(long var1, TemporalUnit var3) {
      return var3 instanceof ChronoUnit ? this.with(this.dateTime.plus(var1, var3), this.offset) : (OffsetDateTime)var3.addTo(this, var1);
   }

   public OffsetDateTime plusYears(long var1) {
      return this.with(this.dateTime.plusYears(var1), this.offset);
   }

   public OffsetDateTime plusMonths(long var1) {
      return this.with(this.dateTime.plusMonths(var1), this.offset);
   }

   public OffsetDateTime plusWeeks(long var1) {
      return this.with(this.dateTime.plusWeeks(var1), this.offset);
   }

   public OffsetDateTime plusDays(long var1) {
      return this.with(this.dateTime.plusDays(var1), this.offset);
   }

   public OffsetDateTime plusHours(long var1) {
      return this.with(this.dateTime.plusHours(var1), this.offset);
   }

   public OffsetDateTime plusMinutes(long var1) {
      return this.with(this.dateTime.plusMinutes(var1), this.offset);
   }

   public OffsetDateTime plusSeconds(long var1) {
      return this.with(this.dateTime.plusSeconds(var1), this.offset);
   }

   public OffsetDateTime plusNanos(long var1) {
      return this.with(this.dateTime.plusNanos(var1), this.offset);
   }

   public OffsetDateTime minus(TemporalAmount var1) {
      return (OffsetDateTime)var1.subtractFrom(this);
   }

   public OffsetDateTime minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public OffsetDateTime minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusYears(Long.MAX_VALUE).plusYears(1L) : this.plusYears(-var1);
   }

   public OffsetDateTime minusMonths(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMonths(Long.MAX_VALUE).plusMonths(1L) : this.plusMonths(-var1);
   }

   public OffsetDateTime minusWeeks(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusWeeks(Long.MAX_VALUE).plusWeeks(1L) : this.plusWeeks(-var1);
   }

   public OffsetDateTime minusDays(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusDays(Long.MAX_VALUE).plusDays(1L) : this.plusDays(-var1);
   }

   public OffsetDateTime minusHours(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusHours(Long.MAX_VALUE).plusHours(1L) : this.plusHours(-var1);
   }

   public OffsetDateTime minusMinutes(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMinutes(Long.MAX_VALUE).plusMinutes(1L) : this.plusMinutes(-var1);
   }

   public OffsetDateTime minusSeconds(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusSeconds(Long.MAX_VALUE).plusSeconds(1L) : this.plusSeconds(-var1);
   }

   public OffsetDateTime minusNanos(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusNanos(Long.MAX_VALUE).plusNanos(1L) : this.plusNanos(-var1);
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 != TemporalQueries.offset() && var1 != TemporalQueries.zone()) {
         if (var1 == TemporalQueries.zoneId()) {
            return null;
         } else if (var1 == TemporalQueries.localDate()) {
            return this.toLocalDate();
         } else if (var1 == TemporalQueries.localTime()) {
            return this.toLocalTime();
         } else if (var1 == TemporalQueries.chronology()) {
            return IsoChronology.INSTANCE;
         } else {
            return var1 == TemporalQueries.precision() ? ChronoUnit.NANOS : var1.queryFrom(this);
         }
      } else {
         return this.getOffset();
      }
   }

   public Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.EPOCH_DAY, this.toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, this.toLocalTime().toNanoOfDay()).with(ChronoField.OFFSET_SECONDS, (long)this.getOffset().getTotalSeconds());
   }

   public long until(Temporal var1, TemporalUnit var2) {
      OffsetDateTime var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         var3 = var3.withOffsetSameInstant(this.offset);
         return this.dateTime.until(var3.dateTime, var2);
      } else {
         return var2.between(this, var3);
      }
   }

   public String format(DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return var1.format(this);
   }

   public ZonedDateTime atZoneSameInstant(ZoneId var1) {
      return ZonedDateTime.ofInstant(this.dateTime, this.offset, var1);
   }

   public ZonedDateTime atZoneSimilarLocal(ZoneId var1) {
      return ZonedDateTime.ofLocal(this.dateTime, var1, this.offset);
   }

   public OffsetTime toOffsetTime() {
      return OffsetTime.of(this.dateTime.toLocalTime(), this.offset);
   }

   public ZonedDateTime toZonedDateTime() {
      return ZonedDateTime.of(this.dateTime, this.offset);
   }

   public Instant toInstant() {
      return this.dateTime.toInstant(this.offset);
   }

   public long toEpochSecond() {
      return this.dateTime.toEpochSecond(this.offset);
   }

   public int compareTo(OffsetDateTime var1) {
      int var2 = compareInstant(this, var1);
      if (var2 == 0) {
         var2 = this.toLocalDateTime().compareTo((ChronoLocalDateTime)var1.toLocalDateTime());
      }

      return var2;
   }

   public boolean isAfter(OffsetDateTime var1) {
      long var2 = this.toEpochSecond();
      long var4 = var1.toEpochSecond();
      return var2 > var4 || var2 == var4 && this.toLocalTime().getNano() > var1.toLocalTime().getNano();
   }

   public boolean isBefore(OffsetDateTime var1) {
      long var2 = this.toEpochSecond();
      long var4 = var1.toEpochSecond();
      return var2 < var4 || var2 == var4 && this.toLocalTime().getNano() < var1.toLocalTime().getNano();
   }

   public boolean isEqual(OffsetDateTime var1) {
      return this.toEpochSecond() == var1.toEpochSecond() && this.toLocalTime().getNano() == var1.toLocalTime().getNano();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OffsetDateTime)) {
         return false;
      } else {
         OffsetDateTime var2 = (OffsetDateTime)var1;
         return this.dateTime.equals(var2.dateTime) && this.offset.equals(var2.offset);
      }
   }

   public int hashCode() {
      return this.dateTime.hashCode() ^ this.offset.hashCode();
   }

   public String toString() {
      return this.dateTime.toString() + this.offset.toString();
   }

   private Object writeReplace() {
      return new Ser((byte)10, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(ObjectOutput var1) throws IOException {
      this.dateTime.writeExternal(var1);
      this.offset.writeExternal(var1);
   }

   static OffsetDateTime readExternal(ObjectInput var0) throws IOException, ClassNotFoundException {
      LocalDateTime var1 = LocalDateTime.readExternal(var0);
      ZoneOffset var2 = ZoneOffset.readExternal(var0);
      return of(var1, var2);
   }

   static {
      MIN = LocalDateTime.MIN.atOffset(ZoneOffset.MAX);
      MAX = LocalDateTime.MAX.atOffset(ZoneOffset.MIN);
   }
}
