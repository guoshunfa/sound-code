package java.time;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
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
import java.util.Objects;

public final class OffsetTime implements Temporal, TemporalAdjuster, Comparable<OffsetTime>, Serializable {
   public static final OffsetTime MIN;
   public static final OffsetTime MAX;
   private static final long serialVersionUID = 7264499704384272492L;
   private final LocalTime time;
   private final ZoneOffset offset;

   public static OffsetTime now() {
      return now(Clock.systemDefaultZone());
   }

   public static OffsetTime now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static OffsetTime now(Clock var0) {
      Objects.requireNonNull(var0, (String)"clock");
      Instant var1 = var0.instant();
      return ofInstant(var1, var0.getZone().getRules().getOffset(var1));
   }

   public static OffsetTime of(LocalTime var0, ZoneOffset var1) {
      return new OffsetTime(var0, var1);
   }

   public static OffsetTime of(int var0, int var1, int var2, int var3, ZoneOffset var4) {
      return new OffsetTime(LocalTime.of(var0, var1, var2, var3), var4);
   }

   public static OffsetTime ofInstant(Instant var0, ZoneId var1) {
      Objects.requireNonNull(var0, (String)"instant");
      Objects.requireNonNull(var1, (String)"zone");
      ZoneRules var2 = var1.getRules();
      ZoneOffset var3 = var2.getOffset(var0);
      long var4 = var0.getEpochSecond() + (long)var3.getTotalSeconds();
      int var6 = (int)Math.floorMod(var4, 86400L);
      LocalTime var7 = LocalTime.ofNanoOfDay((long)var6 * 1000000000L + (long)var0.getNano());
      return new OffsetTime(var7, var3);
   }

   public static OffsetTime from(TemporalAccessor var0) {
      if (var0 instanceof OffsetTime) {
         return (OffsetTime)var0;
      } else {
         try {
            LocalTime var1 = LocalTime.from(var0);
            ZoneOffset var2 = ZoneOffset.from(var0);
            return new OffsetTime(var1, var2);
         } catch (DateTimeException var3) {
            throw new DateTimeException("Unable to obtain OffsetTime from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var3);
         }
      }
   }

   public static OffsetTime parse(CharSequence var0) {
      return parse(var0, DateTimeFormatter.ISO_OFFSET_TIME);
   }

   public static OffsetTime parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (OffsetTime)var1.parse(var0, OffsetTime::from);
   }

   private OffsetTime(LocalTime var1, ZoneOffset var2) {
      this.time = (LocalTime)Objects.requireNonNull(var1, (String)"time");
      this.offset = (ZoneOffset)Objects.requireNonNull(var2, (String)"offset");
   }

   private OffsetTime with(LocalTime var1, ZoneOffset var2) {
      return this.time == var1 && this.offset.equals(var2) ? this : new OffsetTime(var1, var2);
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1.isTimeBased() || var1 == ChronoField.OFFSET_SECONDS;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1.isTimeBased();
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.OFFSET_SECONDS ? var1.range() : this.time.range(var1);
      } else {
         return var1.rangeRefinedBy(this);
      }
   }

   public int get(TemporalField var1) {
      return Temporal.super.get(var1);
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.OFFSET_SECONDS ? (long)this.offset.getTotalSeconds() : this.time.getLong(var1);
      } else {
         return var1.getFrom(this);
      }
   }

   public ZoneOffset getOffset() {
      return this.offset;
   }

   public OffsetTime withOffsetSameLocal(ZoneOffset var1) {
      return var1 != null && var1.equals(this.offset) ? this : new OffsetTime(this.time, var1);
   }

   public OffsetTime withOffsetSameInstant(ZoneOffset var1) {
      if (var1.equals(this.offset)) {
         return this;
      } else {
         int var2 = var1.getTotalSeconds() - this.offset.getTotalSeconds();
         LocalTime var3 = this.time.plusSeconds((long)var2);
         return new OffsetTime(var3, var1);
      }
   }

   public LocalTime toLocalTime() {
      return this.time;
   }

   public int getHour() {
      return this.time.getHour();
   }

   public int getMinute() {
      return this.time.getMinute();
   }

   public int getSecond() {
      return this.time.getSecond();
   }

   public int getNano() {
      return this.time.getNano();
   }

   public OffsetTime with(TemporalAdjuster var1) {
      if (var1 instanceof LocalTime) {
         return this.with((LocalTime)var1, this.offset);
      } else if (var1 instanceof ZoneOffset) {
         return this.with(this.time, (ZoneOffset)var1);
      } else {
         return var1 instanceof OffsetTime ? (OffsetTime)var1 : (OffsetTime)var1.adjustInto(this);
      }
   }

   public OffsetTime with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         if (var1 == ChronoField.OFFSET_SECONDS) {
            ChronoField var4 = (ChronoField)var1;
            return this.with(this.time, ZoneOffset.ofTotalSeconds(var4.checkValidIntValue(var2)));
         } else {
            return this.with(this.time.with(var1, var2), this.offset);
         }
      } else {
         return (OffsetTime)var1.adjustInto(this, var2);
      }
   }

   public OffsetTime withHour(int var1) {
      return this.with(this.time.withHour(var1), this.offset);
   }

   public OffsetTime withMinute(int var1) {
      return this.with(this.time.withMinute(var1), this.offset);
   }

   public OffsetTime withSecond(int var1) {
      return this.with(this.time.withSecond(var1), this.offset);
   }

   public OffsetTime withNano(int var1) {
      return this.with(this.time.withNano(var1), this.offset);
   }

   public OffsetTime truncatedTo(TemporalUnit var1) {
      return this.with(this.time.truncatedTo(var1), this.offset);
   }

   public OffsetTime plus(TemporalAmount var1) {
      return (OffsetTime)var1.addTo(this);
   }

   public OffsetTime plus(long var1, TemporalUnit var3) {
      return var3 instanceof ChronoUnit ? this.with(this.time.plus(var1, var3), this.offset) : (OffsetTime)var3.addTo(this, var1);
   }

   public OffsetTime plusHours(long var1) {
      return this.with(this.time.plusHours(var1), this.offset);
   }

   public OffsetTime plusMinutes(long var1) {
      return this.with(this.time.plusMinutes(var1), this.offset);
   }

   public OffsetTime plusSeconds(long var1) {
      return this.with(this.time.plusSeconds(var1), this.offset);
   }

   public OffsetTime plusNanos(long var1) {
      return this.with(this.time.plusNanos(var1), this.offset);
   }

   public OffsetTime minus(TemporalAmount var1) {
      return (OffsetTime)var1.subtractFrom(this);
   }

   public OffsetTime minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public OffsetTime minusHours(long var1) {
      return this.with(this.time.minusHours(var1), this.offset);
   }

   public OffsetTime minusMinutes(long var1) {
      return this.with(this.time.minusMinutes(var1), this.offset);
   }

   public OffsetTime minusSeconds(long var1) {
      return this.with(this.time.minusSeconds(var1), this.offset);
   }

   public OffsetTime minusNanos(long var1) {
      return this.with(this.time.minusNanos(var1), this.offset);
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 != TemporalQueries.offset() && var1 != TemporalQueries.zone()) {
         if (!(var1 == TemporalQueries.zoneId() | var1 == TemporalQueries.chronology()) && var1 != TemporalQueries.localDate()) {
            if (var1 == TemporalQueries.localTime()) {
               return this.time;
            } else {
               return var1 == TemporalQueries.precision() ? ChronoUnit.NANOS : var1.queryFrom(this);
            }
         } else {
            return null;
         }
      } else {
         return this.offset;
      }
   }

   public Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.NANO_OF_DAY, this.time.toNanoOfDay()).with(ChronoField.OFFSET_SECONDS, (long)this.offset.getTotalSeconds());
   }

   public long until(Temporal var1, TemporalUnit var2) {
      OffsetTime var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         long var4 = var3.toEpochNano() - this.toEpochNano();
         switch((ChronoUnit)var2) {
         case NANOS:
            return var4;
         case MICROS:
            return var4 / 1000L;
         case MILLIS:
            return var4 / 1000000L;
         case SECONDS:
            return var4 / 1000000000L;
         case MINUTES:
            return var4 / 60000000000L;
         case HOURS:
            return var4 / 3600000000000L;
         case HALF_DAYS:
            return var4 / 43200000000000L;
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

   public OffsetDateTime atDate(LocalDate var1) {
      return OffsetDateTime.of(var1, this.time, this.offset);
   }

   private long toEpochNano() {
      long var1 = this.time.toNanoOfDay();
      long var3 = (long)this.offset.getTotalSeconds() * 1000000000L;
      return var1 - var3;
   }

   public int compareTo(OffsetTime var1) {
      if (this.offset.equals(var1.offset)) {
         return this.time.compareTo(var1.time);
      } else {
         int var2 = Long.compare(this.toEpochNano(), var1.toEpochNano());
         if (var2 == 0) {
            var2 = this.time.compareTo(var1.time);
         }

         return var2;
      }
   }

   public boolean isAfter(OffsetTime var1) {
      return this.toEpochNano() > var1.toEpochNano();
   }

   public boolean isBefore(OffsetTime var1) {
      return this.toEpochNano() < var1.toEpochNano();
   }

   public boolean isEqual(OffsetTime var1) {
      return this.toEpochNano() == var1.toEpochNano();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OffsetTime)) {
         return false;
      } else {
         OffsetTime var2 = (OffsetTime)var1;
         return this.time.equals(var2.time) && this.offset.equals(var2.offset);
      }
   }

   public int hashCode() {
      return this.time.hashCode() ^ this.offset.hashCode();
   }

   public String toString() {
      return this.time.toString() + this.offset.toString();
   }

   private Object writeReplace() {
      return new Ser((byte)9, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(ObjectOutput var1) throws IOException {
      this.time.writeExternal(var1);
      this.offset.writeExternal(var1);
   }

   static OffsetTime readExternal(ObjectInput var0) throws IOException, ClassNotFoundException {
      LocalTime var1 = LocalTime.readExternal(var0);
      ZoneOffset var2 = ZoneOffset.readExternal(var0);
      return of(var1, var2);
   }

   static {
      MIN = LocalTime.MIN.atOffset(ZoneOffset.MAX);
      MAX = LocalTime.MAX.atOffset(ZoneOffset.MIN);
   }
}
