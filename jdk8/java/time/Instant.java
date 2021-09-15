package java.time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
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
import java.util.Objects;

public final class Instant implements Temporal, TemporalAdjuster, Comparable<Instant>, Serializable {
   public static final Instant EPOCH = new Instant(0L, 0);
   private static final long MIN_SECOND = -31557014167219200L;
   private static final long MAX_SECOND = 31556889864403199L;
   public static final Instant MIN = ofEpochSecond(-31557014167219200L, 0L);
   public static final Instant MAX = ofEpochSecond(31556889864403199L, 999999999L);
   private static final long serialVersionUID = -665713676816604388L;
   private final long seconds;
   private final int nanos;

   public static Instant now() {
      return Clock.systemUTC().instant();
   }

   public static Instant now(Clock var0) {
      Objects.requireNonNull(var0, (String)"clock");
      return var0.instant();
   }

   public static Instant ofEpochSecond(long var0) {
      return create(var0, 0);
   }

   public static Instant ofEpochSecond(long var0, long var2) {
      long var4 = Math.addExact(var0, Math.floorDiv(var2, 1000000000L));
      int var6 = (int)Math.floorMod(var2, 1000000000L);
      return create(var4, var6);
   }

   public static Instant ofEpochMilli(long var0) {
      long var2 = Math.floorDiv(var0, 1000L);
      int var4 = (int)Math.floorMod(var0, 1000L);
      return create(var2, var4 * 1000000);
   }

   public static Instant from(TemporalAccessor var0) {
      if (var0 instanceof Instant) {
         return (Instant)var0;
      } else {
         Objects.requireNonNull(var0, (String)"temporal");

         try {
            long var1 = var0.getLong(ChronoField.INSTANT_SECONDS);
            int var3 = var0.get(ChronoField.NANO_OF_SECOND);
            return ofEpochSecond(var1, (long)var3);
         } catch (DateTimeException var4) {
            throw new DateTimeException("Unable to obtain Instant from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName(), var4);
         }
      }
   }

   public static Instant parse(CharSequence var0) {
      return (Instant)DateTimeFormatter.ISO_INSTANT.parse(var0, Instant::from);
   }

   private static Instant create(long var0, int var2) {
      if ((var0 | (long)var2) == 0L) {
         return EPOCH;
      } else if (var0 >= -31557014167219200L && var0 <= 31556889864403199L) {
         return new Instant(var0, var2);
      } else {
         throw new DateTimeException("Instant exceeds minimum or maximum instant");
      }
   }

   private Instant(long var1, int var3) {
      this.seconds = var1;
      this.nanos = var3;
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1 == ChronoField.INSTANT_SECONDS || var1 == ChronoField.NANO_OF_SECOND || var1 == ChronoField.MICRO_OF_SECOND || var1 == ChronoField.MILLI_OF_SECOND;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public boolean isSupported(TemporalUnit var1) {
      if (var1 instanceof ChronoUnit) {
         return var1.isTimeBased() || var1 == ChronoUnit.DAYS;
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      return Temporal.super.range(var1);
   }

   public int get(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case NANO_OF_SECOND:
            return this.nanos;
         case MICRO_OF_SECOND:
            return this.nanos / 1000;
         case MILLI_OF_SECOND:
            return this.nanos / 1000000;
         case INSTANT_SECONDS:
            ChronoField.INSTANT_SECONDS.checkValidIntValue(this.seconds);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return this.range(var1).checkValidIntValue(var1.getFrom(this), var1);
      }
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case NANO_OF_SECOND:
            return (long)this.nanos;
         case MICRO_OF_SECOND:
            return (long)(this.nanos / 1000);
         case MILLI_OF_SECOND:
            return (long)(this.nanos / 1000000);
         case INSTANT_SECONDS:
            return this.seconds;
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   public long getEpochSecond() {
      return this.seconds;
   }

   public int getNano() {
      return this.nanos;
   }

   public Instant with(TemporalAdjuster var1) {
      return (Instant)var1.adjustInto(this);
   }

   public Instant with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         var4.checkValidValue(var2);
         int var5;
         switch(var4) {
         case NANO_OF_SECOND:
            return var2 != (long)this.nanos ? create(this.seconds, (int)var2) : this;
         case MICRO_OF_SECOND:
            var5 = (int)var2 * 1000;
            return var5 != this.nanos ? create(this.seconds, var5) : this;
         case MILLI_OF_SECOND:
            var5 = (int)var2 * 1000000;
            return var5 != this.nanos ? create(this.seconds, var5) : this;
         case INSTANT_SECONDS:
            return var2 != this.seconds ? create(var2, this.nanos) : this;
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return (Instant)var1.adjustInto(this, var2);
      }
   }

   public Instant truncatedTo(TemporalUnit var1) {
      if (var1 == ChronoUnit.NANOS) {
         return this;
      } else {
         Duration var2 = var1.getDuration();
         if (var2.getSeconds() > 86400L) {
            throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation");
         } else {
            long var3 = var2.toNanos();
            if (86400000000000L % var3 != 0L) {
               throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder");
            } else {
               long var5 = this.seconds % 86400L * 1000000000L + (long)this.nanos;
               long var7 = var5 / var3 * var3;
               return this.plusNanos(var7 - var5);
            }
         }
      }
   }

   public Instant plus(TemporalAmount var1) {
      return (Instant)var1.addTo(this);
   }

   public Instant plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         switch((ChronoUnit)var3) {
         case NANOS:
            return this.plusNanos(var1);
         case MICROS:
            return this.plus(var1 / 1000000L, var1 % 1000000L * 1000L);
         case MILLIS:
            return this.plusMillis(var1);
         case SECONDS:
            return this.plusSeconds(var1);
         case MINUTES:
            return this.plusSeconds(Math.multiplyExact(var1, 60L));
         case HOURS:
            return this.plusSeconds(Math.multiplyExact(var1, 3600L));
         case HALF_DAYS:
            return this.plusSeconds(Math.multiplyExact(var1, 43200L));
         case DAYS:
            return this.plusSeconds(Math.multiplyExact(var1, 86400L));
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var3);
         }
      } else {
         return (Instant)var3.addTo(this, var1);
      }
   }

   public Instant plusSeconds(long var1) {
      return this.plus(var1, 0L);
   }

   public Instant plusMillis(long var1) {
      return this.plus(var1 / 1000L, var1 % 1000L * 1000000L);
   }

   public Instant plusNanos(long var1) {
      return this.plus(0L, var1);
   }

   private Instant plus(long var1, long var3) {
      if ((var1 | var3) == 0L) {
         return this;
      } else {
         long var5 = Math.addExact(this.seconds, var1);
         var5 = Math.addExact(var5, var3 / 1000000000L);
         var3 %= 1000000000L;
         long var7 = (long)this.nanos + var3;
         return ofEpochSecond(var5, var7);
      }
   }

   public Instant minus(TemporalAmount var1) {
      return (Instant)var1.subtractFrom(this);
   }

   public Instant minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public Instant minusSeconds(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusSeconds(Long.MAX_VALUE).plusSeconds(1L) : this.plusSeconds(-var1);
   }

   public Instant minusMillis(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusMillis(Long.MAX_VALUE).plusMillis(1L) : this.plusMillis(-var1);
   }

   public Instant minusNanos(long var1) {
      return var1 == Long.MIN_VALUE ? this.plusNanos(Long.MAX_VALUE).plusNanos(1L) : this.plusNanos(-var1);
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 == TemporalQueries.precision()) {
         return ChronoUnit.NANOS;
      } else {
         return var1 != TemporalQueries.chronology() && var1 != TemporalQueries.zoneId() && var1 != TemporalQueries.zone() && var1 != TemporalQueries.offset() && var1 != TemporalQueries.localDate() && var1 != TemporalQueries.localTime() ? var1.queryFrom(this) : null;
      }
   }

   public Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.INSTANT_SECONDS, this.seconds).with(ChronoField.NANO_OF_SECOND, (long)this.nanos);
   }

   public long until(Temporal var1, TemporalUnit var2) {
      Instant var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         ChronoUnit var4 = (ChronoUnit)var2;
         switch(var4) {
         case NANOS:
            return this.nanosUntil(var3);
         case MICROS:
            return this.nanosUntil(var3) / 1000L;
         case MILLIS:
            return Math.subtractExact(var3.toEpochMilli(), this.toEpochMilli());
         case SECONDS:
            return this.secondsUntil(var3);
         case MINUTES:
            return this.secondsUntil(var3) / 60L;
         case HOURS:
            return this.secondsUntil(var3) / 3600L;
         case HALF_DAYS:
            return this.secondsUntil(var3) / 43200L;
         case DAYS:
            return this.secondsUntil(var3) / 86400L;
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var2);
         }
      } else {
         return var2.between(this, var3);
      }
   }

   private long nanosUntil(Instant var1) {
      long var2 = Math.subtractExact(var1.seconds, this.seconds);
      long var4 = Math.multiplyExact(var2, 1000000000L);
      return Math.addExact(var4, (long)(var1.nanos - this.nanos));
   }

   private long secondsUntil(Instant var1) {
      long var2 = Math.subtractExact(var1.seconds, this.seconds);
      long var4 = (long)(var1.nanos - this.nanos);
      if (var2 > 0L && var4 < 0L) {
         --var2;
      } else if (var2 < 0L && var4 > 0L) {
         ++var2;
      }

      return var2;
   }

   public OffsetDateTime atOffset(ZoneOffset var1) {
      return OffsetDateTime.ofInstant(this, var1);
   }

   public ZonedDateTime atZone(ZoneId var1) {
      return ZonedDateTime.ofInstant(this, var1);
   }

   public long toEpochMilli() {
      long var1;
      if (this.seconds < 0L && this.nanos > 0) {
         var1 = Math.multiplyExact(this.seconds + 1L, 1000L);
         long var3 = (long)(this.nanos / 1000000 - 1000);
         return Math.addExact(var1, var3);
      } else {
         var1 = Math.multiplyExact(this.seconds, 1000L);
         return Math.addExact(var1, (long)(this.nanos / 1000000));
      }
   }

   public int compareTo(Instant var1) {
      int var2 = Long.compare(this.seconds, var1.seconds);
      return var2 != 0 ? var2 : this.nanos - var1.nanos;
   }

   public boolean isAfter(Instant var1) {
      return this.compareTo(var1) > 0;
   }

   public boolean isBefore(Instant var1) {
      return this.compareTo(var1) < 0;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Instant)) {
         return false;
      } else {
         Instant var2 = (Instant)var1;
         return this.seconds == var2.seconds && this.nanos == var2.nanos;
      }
   }

   public int hashCode() {
      return (int)(this.seconds ^ this.seconds >>> 32) + 51 * this.nanos;
   }

   public String toString() {
      return DateTimeFormatter.ISO_INSTANT.format(this);
   }

   private Object writeReplace() {
      return new Ser((byte)2, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeLong(this.seconds);
      var1.writeInt(this.nanos);
   }

   static Instant readExternal(DataInput var0) throws IOException {
      long var1 = var0.readLong();
      int var3 = var0.readInt();
      return ofEpochSecond(var1, (long)var3);
   }
}
