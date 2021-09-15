package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;

final class ChronoLocalDateTimeImpl<D extends ChronoLocalDate> implements ChronoLocalDateTime<D>, Temporal, TemporalAdjuster, Serializable {
   private static final long serialVersionUID = 4556003607393004514L;
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
   private final transient D date;
   private final transient LocalTime time;

   static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> of(R var0, LocalTime var1) {
      return new ChronoLocalDateTimeImpl(var0, var1);
   }

   static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> ensureValid(Chronology var0, Temporal var1) {
      ChronoLocalDateTimeImpl var2 = (ChronoLocalDateTimeImpl)var1;
      if (!var0.equals(var2.getChronology())) {
         throw new ClassCastException("Chronology mismatch, required: " + var0.getId() + ", actual: " + var2.getChronology().getId());
      } else {
         return var2;
      }
   }

   private ChronoLocalDateTimeImpl(D var1, LocalTime var2) {
      Objects.requireNonNull(var1, (String)"date");
      Objects.requireNonNull(var2, (String)"time");
      this.date = var1;
      this.time = var2;
   }

   private ChronoLocalDateTimeImpl<D> with(Temporal var1, LocalTime var2) {
      if (this.date == var1 && this.time == var2) {
         return this;
      } else {
         ChronoLocalDate var3 = ChronoLocalDateImpl.ensureValid(this.date.getChronology(), var1);
         return new ChronoLocalDateTimeImpl(var3, var2);
      }
   }

   public D toLocalDate() {
      return this.date;
   }

   public LocalTime toLocalTime() {
      return this.time;
   }

   public boolean isSupported(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isDateBased() || var2.isTimeBased();
      } else {
         return var1 != null && var1.isSupportedBy(this);
      }
   }

   public ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isTimeBased() ? this.time.range(var1) : this.date.range(var1);
      } else {
         return var1.rangeRefinedBy(this);
      }
   }

   public int get(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isTimeBased() ? this.time.get(var1) : this.date.get(var1);
      } else {
         return this.range(var1).checkValidIntValue(this.getLong(var1), var1);
      }
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         ChronoField var2 = (ChronoField)var1;
         return var2.isTimeBased() ? this.time.getLong(var1) : this.date.getLong(var1);
      } else {
         return var1.getFrom(this);
      }
   }

   public ChronoLocalDateTimeImpl<D> with(TemporalAdjuster var1) {
      if (var1 instanceof ChronoLocalDate) {
         return this.with((ChronoLocalDate)var1, this.time);
      } else if (var1 instanceof LocalTime) {
         return this.with(this.date, (LocalTime)var1);
      } else {
         return var1 instanceof ChronoLocalDateTimeImpl ? ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl)var1) : ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl)var1.adjustInto(this));
      }
   }

   public ChronoLocalDateTimeImpl<D> with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         return var4.isTimeBased() ? this.with(this.date, this.time.with(var1, var2)) : this.with(this.date.with(var1, var2), this.time);
      } else {
         return ensureValid(this.date.getChronology(), var1.adjustInto(this, var2));
      }
   }

   public ChronoLocalDateTimeImpl<D> plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         ChronoUnit var4 = (ChronoUnit)var3;
         switch(var4) {
         case NANOS:
            return this.plusNanos(var1);
         case MICROS:
            return this.plusDays(var1 / 86400000000L).plusNanos(var1 % 86400000000L * 1000L);
         case MILLIS:
            return this.plusDays(var1 / 86400000L).plusNanos(var1 % 86400000L * 1000000L);
         case SECONDS:
            return this.plusSeconds(var1);
         case MINUTES:
            return this.plusMinutes(var1);
         case HOURS:
            return this.plusHours(var1);
         case HALF_DAYS:
            return this.plusDays(var1 / 256L).plusHours(var1 % 256L * 12L);
         default:
            return this.with(this.date.plus(var1, var3), this.time);
         }
      } else {
         return ensureValid(this.date.getChronology(), var3.addTo(this, var1));
      }
   }

   private ChronoLocalDateTimeImpl<D> plusDays(long var1) {
      return this.with(this.date.plus(var1, ChronoUnit.DAYS), this.time);
   }

   private ChronoLocalDateTimeImpl<D> plusHours(long var1) {
      return this.plusWithOverflow(this.date, var1, 0L, 0L, 0L);
   }

   private ChronoLocalDateTimeImpl<D> plusMinutes(long var1) {
      return this.plusWithOverflow(this.date, 0L, var1, 0L, 0L);
   }

   ChronoLocalDateTimeImpl<D> plusSeconds(long var1) {
      return this.plusWithOverflow(this.date, 0L, 0L, var1, 0L);
   }

   private ChronoLocalDateTimeImpl<D> plusNanos(long var1) {
      return this.plusWithOverflow(this.date, 0L, 0L, 0L, var1);
   }

   private ChronoLocalDateTimeImpl<D> plusWithOverflow(D var1, long var2, long var4, long var6, long var8) {
      if ((var2 | var4 | var6 | var8) == 0L) {
         return this.with(var1, this.time);
      } else {
         long var10 = var8 / 86400000000000L + var6 / 86400L + var4 / 1440L + var2 / 24L;
         long var12 = var8 % 86400000000000L + var6 % 86400L * 1000000000L + var4 % 1440L * 60000000000L + var2 % 24L * 3600000000000L;
         long var14 = this.time.toNanoOfDay();
         var12 += var14;
         var10 += Math.floorDiv(var12, 86400000000000L);
         long var16 = Math.floorMod(var12, 86400000000000L);
         LocalTime var18 = var16 == var14 ? this.time : LocalTime.ofNanoOfDay(var16);
         return this.with(var1.plus(var10, ChronoUnit.DAYS), var18);
      }
   }

   public ChronoZonedDateTime<D> atZone(ZoneId var1) {
      return ChronoZonedDateTimeImpl.ofBest(this, var1, (ZoneOffset)null);
   }

   public long until(Temporal var1, TemporalUnit var2) {
      Objects.requireNonNull(var1, (String)"endExclusive");
      ChronoLocalDateTime var3 = this.getChronology().localDateTime(var1);
      if (var2 instanceof ChronoUnit) {
         if (var2.isTimeBased()) {
            long var6 = var3.getLong(ChronoField.EPOCH_DAY) - this.date.getLong(ChronoField.EPOCH_DAY);
            switch((ChronoUnit)var2) {
            case NANOS:
               var6 = Math.multiplyExact(var6, 86400000000000L);
               break;
            case MICROS:
               var6 = Math.multiplyExact(var6, 86400000000L);
               break;
            case MILLIS:
               var6 = Math.multiplyExact(var6, 86400000L);
               break;
            case SECONDS:
               var6 = Math.multiplyExact(var6, 86400L);
               break;
            case MINUTES:
               var6 = Math.multiplyExact(var6, 1440L);
               break;
            case HOURS:
               var6 = Math.multiplyExact(var6, 24L);
               break;
            case HALF_DAYS:
               var6 = Math.multiplyExact(var6, 2L);
            }

            return Math.addExact(var6, this.time.until(var3.toLocalTime(), var2));
         } else {
            ChronoLocalDate var4 = var3.toLocalDate();
            if (var3.toLocalTime().isBefore(this.time)) {
               var4 = var4.minus(1L, ChronoUnit.DAYS);
            }

            return this.date.until(var4, var2);
         }
      } else {
         Objects.requireNonNull(var2, (String)"unit");
         return var2.between(this, var3);
      }
   }

   private Object writeReplace() {
      return new Ser((byte)2, this);
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeObject(this.date);
      var1.writeObject(this.time);
   }

   static ChronoLocalDateTime<?> readExternal(ObjectInput var0) throws IOException, ClassNotFoundException {
      ChronoLocalDate var1 = (ChronoLocalDate)var0.readObject();
      LocalTime var2 = (LocalTime)var0.readObject();
      return var1.atTime(var2);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ChronoLocalDateTime) {
         return this.compareTo((ChronoLocalDateTime)var1) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.toLocalDate().hashCode() ^ this.toLocalTime().hashCode();
   }

   public String toString() {
      return this.toLocalDate().toString() + 'T' + this.toLocalTime().toString();
   }
}
