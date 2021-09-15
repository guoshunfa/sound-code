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

public final class LocalTime implements Temporal, TemporalAdjuster, Comparable<LocalTime>, Serializable {
   public static final LocalTime MIN;
   public static final LocalTime MAX;
   public static final LocalTime MIDNIGHT;
   public static final LocalTime NOON;
   private static final LocalTime[] HOURS = new LocalTime[24];
   static final int HOURS_PER_DAY = 24;
   static final int MINUTES_PER_HOUR = 60;
   static final int MINUTES_PER_DAY = 1440;
   static final int SECONDS_PER_MINUTE = 60;
   static final int SECONDS_PER_HOUR = 3600;
   static final int SECONDS_PER_DAY = 86400;
   static final long MILLIS_PER_DAY = 86400000L;
   static final long MICROS_PER_DAY = 86400000000L;
   static final long NANOS_PER_SECOND = 1000000000L;
   static final long NANOS_PER_MINUTE = 60000000000L;
   static final long NANOS_PER_HOUR = 3600000000000L;
   static final long NANOS_PER_DAY = 86400000000000L;
   private static final long serialVersionUID = 6414437269572265201L;
   private final byte hour;
   private final byte minute;
   private final byte second;
   private final int nano;

   public static LocalTime now() {
      return now(Clock.systemDefaultZone());
   }

   public static LocalTime now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static LocalTime now(Clock var0) {
      Objects.requireNonNull(var0, (String)"clock");
      Instant var1 = var0.instant();
      ZoneOffset var2 = var0.getZone().getRules().getOffset(var1);
      long var3 = var1.getEpochSecond() + (long)var2.getTotalSeconds();
      int var5 = (int)Math.floorMod(var3, 86400L);
      return ofNanoOfDay((long)var5 * 1000000000L + (long)var1.getNano());
   }

   public static LocalTime of(int var0, int var1) {
      ChronoField.HOUR_OF_DAY.checkValidValue((long)var0);
      if (var1 == 0) {
         return HOURS[var0];
      } else {
         ChronoField.MINUTE_OF_HOUR.checkValidValue((long)var1);
         return new LocalTime(var0, var1, 0, 0);
      }
   }

   public static LocalTime of(int var0, int var1, int var2) {
      ChronoField.HOUR_OF_DAY.checkValidValue((long)var0);
      if ((var1 | var2) == 0) {
         return HOURS[var0];
      } else {
         ChronoField.MINUTE_OF_HOUR.checkValidValue((long)var1);
         ChronoField.SECOND_OF_MINUTE.checkValidValue((long)var2);
         return new LocalTime(var0, var1, var2, 0);
      }
   }

   public static LocalTime of(int var0, int var1, int var2, int var3) {
      ChronoField.HOUR_OF_DAY.checkValidValue((long)var0);
      ChronoField.MINUTE_OF_HOUR.checkValidValue((long)var1);
      ChronoField.SECOND_OF_MINUTE.checkValidValue((long)var2);
      ChronoField.NANO_OF_SECOND.checkValidValue((long)var3);
      return create(var0, var1, var2, var3);
   }

   public static LocalTime ofSecondOfDay(long var0) {
      ChronoField.SECOND_OF_DAY.checkValidValue(var0);
      int var2 = (int)(var0 / 3600L);
      var0 -= (long)(var2 * 3600);
      int var3 = (int)(var0 / 60L);
      var0 -= (long)(var3 * 60);
      return create(var2, var3, (int)var0, 0);
   }

   public static LocalTime ofNanoOfDay(long var0) {
      ChronoField.NANO_OF_DAY.checkValidValue(var0);
      int var2 = (int)(var0 / 3600000000000L);
      var0 -= (long)var2 * 3600000000000L;
      int var3 = (int)(var0 / 60000000000L);
      var0 -= (long)var3 * 60000000000L;
      int var4 = (int)(var0 / 1000000000L);
      var0 -= (long)var4 * 1000000000L;
      return create(var2, var3, var4, (int)var0);
   }

   public static LocalTime from(TemporalAccessor var0) {
      Objects.requireNonNull(var0, (String)"temporal");
      LocalTime var1 = (LocalTime)var0.query(TemporalQueries.localTime());
      if (var1 == null) {
         throw new DateTimeException("Unable to obtain LocalTime from TemporalAccessor: " + var0 + " of type " + var0.getClass().getName());
      } else {
         return var1;
      }
   }

   public static LocalTime parse(CharSequence var0) {
      return parse(var0, DateTimeFormatter.ISO_LOCAL_TIME);
   }

   public static LocalTime parse(CharSequence var0, DateTimeFormatter var1) {
      Objects.requireNonNull(var1, (String)"formatter");
      return (LocalTime)var1.parse(var0, LocalTime::from);
   }

   private static LocalTime create(int var0, int var1, int var2, int var3) {
      return (var1 | var2 | var3) == 0 ? HOURS[var0] : new LocalTime(var0, var1, var2, var3);
   }

   private LocalTime(int var1, int var2, int var3, int var4) {
      this.hour = (byte)var1;
      this.minute = (byte)var2;
      this.second = (byte)var3;
      this.nano = var4;
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         return var1.isTimeBased();
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
      return Temporal.super.range(var1);
   }

   public int get(TemporalField var1) {
      return var1 instanceof ChronoField ? this.get0(var1) : Temporal.super.get(var1);
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         if (var1 == ChronoField.NANO_OF_DAY) {
            return this.toNanoOfDay();
         } else {
            return var1 == ChronoField.MICRO_OF_DAY ? this.toNanoOfDay() / 1000L : (long)this.get0(var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   private int get0(TemporalField var1) {
      switch((ChronoField)var1) {
      case NANO_OF_SECOND:
         return this.nano;
      case NANO_OF_DAY:
         throw new UnsupportedTemporalTypeException("Invalid field 'NanoOfDay' for get() method, use getLong() instead");
      case MICRO_OF_SECOND:
         return this.nano / 1000;
      case MICRO_OF_DAY:
         throw new UnsupportedTemporalTypeException("Invalid field 'MicroOfDay' for get() method, use getLong() instead");
      case MILLI_OF_SECOND:
         return this.nano / 1000000;
      case MILLI_OF_DAY:
         return (int)(this.toNanoOfDay() / 1000000L);
      case SECOND_OF_MINUTE:
         return this.second;
      case SECOND_OF_DAY:
         return this.toSecondOfDay();
      case MINUTE_OF_HOUR:
         return this.minute;
      case MINUTE_OF_DAY:
         return this.hour * 60 + this.minute;
      case HOUR_OF_AMPM:
         return this.hour % 12;
      case CLOCK_HOUR_OF_AMPM:
         int var2 = this.hour % 12;
         return var2 % 12 == 0 ? 12 : var2;
      case HOUR_OF_DAY:
         return this.hour;
      case CLOCK_HOUR_OF_DAY:
         return this.hour == 0 ? 24 : this.hour;
      case AMPM_OF_DAY:
         return this.hour / 12;
      default:
         throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
      }
   }

   public int getHour() {
      return this.hour;
   }

   public int getMinute() {
      return this.minute;
   }

   public int getSecond() {
      return this.second;
   }

   public int getNano() {
      return this.nano;
   }

   public LocalTime with(TemporalAdjuster var1) {
      return var1 instanceof LocalTime ? (LocalTime)var1 : (LocalTime)var1.adjustInto(this);
   }

   public LocalTime with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         var4.checkValidValue(var2);
         switch(var4) {
         case NANO_OF_SECOND:
            return this.withNano((int)var2);
         case NANO_OF_DAY:
            return ofNanoOfDay(var2);
         case MICRO_OF_SECOND:
            return this.withNano((int)var2 * 1000);
         case MICRO_OF_DAY:
            return ofNanoOfDay(var2 * 1000L);
         case MILLI_OF_SECOND:
            return this.withNano((int)var2 * 1000000);
         case MILLI_OF_DAY:
            return ofNanoOfDay(var2 * 1000000L);
         case SECOND_OF_MINUTE:
            return this.withSecond((int)var2);
         case SECOND_OF_DAY:
            return this.plusSeconds(var2 - (long)this.toSecondOfDay());
         case MINUTE_OF_HOUR:
            return this.withMinute((int)var2);
         case MINUTE_OF_DAY:
            return this.plusMinutes(var2 - (long)(this.hour * 60 + this.minute));
         case HOUR_OF_AMPM:
            return this.plusHours(var2 - (long)(this.hour % 12));
         case CLOCK_HOUR_OF_AMPM:
            return this.plusHours((var2 == 12L ? 0L : var2) - (long)(this.hour % 12));
         case HOUR_OF_DAY:
            return this.withHour((int)var2);
         case CLOCK_HOUR_OF_DAY:
            return this.withHour((int)(var2 == 24L ? 0L : var2));
         case AMPM_OF_DAY:
            return this.plusHours((var2 - (long)(this.hour / 12)) * 12L);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return (LocalTime)var1.adjustInto(this, var2);
      }
   }

   public LocalTime withHour(int var1) {
      if (this.hour == var1) {
         return this;
      } else {
         ChronoField.HOUR_OF_DAY.checkValidValue((long)var1);
         return create(var1, this.minute, this.second, this.nano);
      }
   }

   public LocalTime withMinute(int var1) {
      if (this.minute == var1) {
         return this;
      } else {
         ChronoField.MINUTE_OF_HOUR.checkValidValue((long)var1);
         return create(this.hour, var1, this.second, this.nano);
      }
   }

   public LocalTime withSecond(int var1) {
      if (this.second == var1) {
         return this;
      } else {
         ChronoField.SECOND_OF_MINUTE.checkValidValue((long)var1);
         return create(this.hour, this.minute, var1, this.nano);
      }
   }

   public LocalTime withNano(int var1) {
      if (this.nano == var1) {
         return this;
      } else {
         ChronoField.NANO_OF_SECOND.checkValidValue((long)var1);
         return create(this.hour, this.minute, this.second, var1);
      }
   }

   public LocalTime truncatedTo(TemporalUnit var1) {
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
               long var5 = this.toNanoOfDay();
               return ofNanoOfDay(var5 / var3 * var3);
            }
         }
      }
   }

   public LocalTime plus(TemporalAmount var1) {
      return (LocalTime)var1.addTo(this);
   }

   public LocalTime plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         switch((ChronoUnit)var3) {
         case NANOS:
            return this.plusNanos(var1);
         case MICROS:
            return this.plusNanos(var1 % 86400000000L * 1000L);
         case MILLIS:
            return this.plusNanos(var1 % 86400000L * 1000000L);
         case SECONDS:
            return this.plusSeconds(var1);
         case MINUTES:
            return this.plusMinutes(var1);
         case HOURS:
            return this.plusHours(var1);
         case HALF_DAYS:
            return this.plusHours(var1 % 2L * 12L);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var3);
         }
      } else {
         return (LocalTime)var3.addTo(this, var1);
      }
   }

   public LocalTime plusHours(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         int var3 = ((int)(var1 % 24L) + this.hour + 24) % 24;
         return create(var3, this.minute, this.second, this.nano);
      }
   }

   public LocalTime plusMinutes(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         int var3 = this.hour * 60 + this.minute;
         int var4 = ((int)(var1 % 1440L) + var3 + 1440) % 1440;
         if (var3 == var4) {
            return this;
         } else {
            int var5 = var4 / 60;
            int var6 = var4 % 60;
            return create(var5, var6, this.second, this.nano);
         }
      }
   }

   public LocalTime plusSeconds(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         int var3 = this.hour * 3600 + this.minute * 60 + this.second;
         int var4 = ((int)(var1 % 86400L) + var3 + 86400) % 86400;
         if (var3 == var4) {
            return this;
         } else {
            int var5 = var4 / 3600;
            int var6 = var4 / 60 % 60;
            int var7 = var4 % 60;
            return create(var5, var6, var7, this.nano);
         }
      }
   }

   public LocalTime plusNanos(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         long var3 = this.toNanoOfDay();
         long var5 = (var1 % 86400000000000L + var3 + 86400000000000L) % 86400000000000L;
         if (var3 == var5) {
            return this;
         } else {
            int var7 = (int)(var5 / 3600000000000L);
            int var8 = (int)(var5 / 60000000000L % 60L);
            int var9 = (int)(var5 / 1000000000L % 60L);
            int var10 = (int)(var5 % 1000000000L);
            return create(var7, var8, var9, var10);
         }
      }
   }

   public LocalTime minus(TemporalAmount var1) {
      return (LocalTime)var1.subtractFrom(this);
   }

   public LocalTime minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   public LocalTime minusHours(long var1) {
      return this.plusHours(-(var1 % 24L));
   }

   public LocalTime minusMinutes(long var1) {
      return this.plusMinutes(-(var1 % 1440L));
   }

   public LocalTime minusSeconds(long var1) {
      return this.plusSeconds(-(var1 % 86400L));
   }

   public LocalTime minusNanos(long var1) {
      return this.plusNanos(-(var1 % 86400000000000L));
   }

   public <R> R query(TemporalQuery<R> var1) {
      if (var1 != TemporalQueries.chronology() && var1 != TemporalQueries.zoneId() && var1 != TemporalQueries.zone() && var1 != TemporalQueries.offset()) {
         if (var1 == TemporalQueries.localTime()) {
            return this;
         } else if (var1 == TemporalQueries.localDate()) {
            return null;
         } else {
            return var1 == TemporalQueries.precision() ? ChronoUnit.NANOS : var1.queryFrom(this);
         }
      } else {
         return null;
      }
   }

   public Temporal adjustInto(Temporal var1) {
      return var1.with(ChronoField.NANO_OF_DAY, this.toNanoOfDay());
   }

   public long until(Temporal var1, TemporalUnit var2) {
      LocalTime var3 = from(var1);
      if (var2 instanceof ChronoUnit) {
         long var4 = var3.toNanoOfDay() - this.toNanoOfDay();
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

   public LocalDateTime atDate(LocalDate var1) {
      return LocalDateTime.of(var1, this);
   }

   public OffsetTime atOffset(ZoneOffset var1) {
      return OffsetTime.of(this, var1);
   }

   public int toSecondOfDay() {
      int var1 = this.hour * 3600;
      var1 += this.minute * 60;
      var1 += this.second;
      return var1;
   }

   public long toNanoOfDay() {
      long var1 = (long)this.hour * 3600000000000L;
      var1 += (long)this.minute * 60000000000L;
      var1 += (long)this.second * 1000000000L;
      var1 += (long)this.nano;
      return var1;
   }

   public int compareTo(LocalTime var1) {
      int var2 = Integer.compare(this.hour, var1.hour);
      if (var2 == 0) {
         var2 = Integer.compare(this.minute, var1.minute);
         if (var2 == 0) {
            var2 = Integer.compare(this.second, var1.second);
            if (var2 == 0) {
               var2 = Integer.compare(this.nano, var1.nano);
            }
         }
      }

      return var2;
   }

   public boolean isAfter(LocalTime var1) {
      return this.compareTo(var1) > 0;
   }

   public boolean isBefore(LocalTime var1) {
      return this.compareTo(var1) < 0;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LocalTime)) {
         return false;
      } else {
         LocalTime var2 = (LocalTime)var1;
         return this.hour == var2.hour && this.minute == var2.minute && this.second == var2.second && this.nano == var2.nano;
      }
   }

   public int hashCode() {
      long var1 = this.toNanoOfDay();
      return (int)(var1 ^ var1 >>> 32);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(18);
      byte var2 = this.hour;
      byte var3 = this.minute;
      byte var4 = this.second;
      int var5 = this.nano;
      var1.append(var2 < 10 ? "0" : "").append((int)var2).append(var3 < 10 ? ":0" : ":").append((int)var3);
      if (var4 > 0 || var5 > 0) {
         var1.append(var4 < 10 ? ":0" : ":").append((int)var4);
         if (var5 > 0) {
            var1.append('.');
            if (var5 % 1000000 == 0) {
               var1.append(Integer.toString(var5 / 1000000 + 1000).substring(1));
            } else if (var5 % 1000 == 0) {
               var1.append(Integer.toString(var5 / 1000 + 1000000).substring(1));
            } else {
               var1.append(Integer.toString(var5 + 1000000000).substring(1));
            }
         }
      }

      return var1.toString();
   }

   private Object writeReplace() {
      return new Ser((byte)4, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      if (this.nano == 0) {
         if (this.second == 0) {
            if (this.minute == 0) {
               var1.writeByte(~this.hour);
            } else {
               var1.writeByte(this.hour);
               var1.writeByte(~this.minute);
            }
         } else {
            var1.writeByte(this.hour);
            var1.writeByte(this.minute);
            var1.writeByte(~this.second);
         }
      } else {
         var1.writeByte(this.hour);
         var1.writeByte(this.minute);
         var1.writeByte(this.second);
         var1.writeInt(this.nano);
      }

   }

   static LocalTime readExternal(DataInput var0) throws IOException {
      int var1 = var0.readByte();
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;
      if (var1 < 0) {
         var1 = ~var1;
      } else {
         var2 = var0.readByte();
         if (var2 < 0) {
            var2 = ~var2;
         } else {
            var3 = var0.readByte();
            if (var3 < 0) {
               var3 = ~var3;
            } else {
               var4 = var0.readInt();
            }
         }
      }

      return of(var1, var2, var3, var4);
   }

   static {
      for(int var0 = 0; var0 < HOURS.length; ++var0) {
         HOURS[var0] = new LocalTime(var0, 0, 0, 0);
      }

      MIDNIGHT = HOURS[0];
      NOON = HOURS[12];
      MIN = HOURS[0];
      MAX = new LocalTime(23, 59, 59, 999999999);
   }
}
