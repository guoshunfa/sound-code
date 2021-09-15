package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

public final class ThaiBuddhistDate extends ChronoLocalDateImpl<ThaiBuddhistDate> implements ChronoLocalDate, Serializable {
   private static final long serialVersionUID = -8722293800195731463L;
   private final transient LocalDate isoDate;

   public static ThaiBuddhistDate now() {
      return now(Clock.systemDefaultZone());
   }

   public static ThaiBuddhistDate now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static ThaiBuddhistDate now(Clock var0) {
      return new ThaiBuddhistDate(LocalDate.now(var0));
   }

   public static ThaiBuddhistDate of(int var0, int var1, int var2) {
      return new ThaiBuddhistDate(LocalDate.of(var0 - 543, var1, var2));
   }

   public static ThaiBuddhistDate from(TemporalAccessor var0) {
      return ThaiBuddhistChronology.INSTANCE.date(var0);
   }

   ThaiBuddhistDate(LocalDate var1) {
      Objects.requireNonNull(var1, (String)"isoDate");
      this.isoDate = var1;
   }

   public ThaiBuddhistChronology getChronology() {
      return ThaiBuddhistChronology.INSTANCE;
   }

   public ThaiBuddhistEra getEra() {
      return this.getProlepticYear() >= 1 ? ThaiBuddhistEra.BE : ThaiBuddhistEra.BEFORE_BE;
   }

   public int lengthOfMonth() {
      return this.isoDate.lengthOfMonth();
   }

   public ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         if (this.isSupported(var1)) {
            ChronoField var2 = (ChronoField)var1;
            switch(var2) {
            case DAY_OF_MONTH:
            case DAY_OF_YEAR:
            case ALIGNED_WEEK_OF_MONTH:
               return this.isoDate.range(var1);
            case YEAR_OF_ERA:
               ValueRange var3 = ChronoField.YEAR.range();
               long var4 = this.getProlepticYear() <= 0 ? -(var3.getMinimum() + 543L) + 1L : var3.getMaximum() + 543L;
               return ValueRange.of(1L, var4);
            default:
               return this.getChronology().range(var2);
            }
         } else {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return var1.rangeRefinedBy(this);
      }
   }

   public long getLong(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         switch((ChronoField)var1) {
         case YEAR_OF_ERA:
            int var2 = this.getProlepticYear();
            return (long)(var2 >= 1 ? var2 : 1 - var2);
         case PROLEPTIC_MONTH:
            return this.getProlepticMonth();
         case YEAR:
            return (long)this.getProlepticYear();
         case ERA:
            return (long)(this.getProlepticYear() >= 1 ? 1 : 0);
         default:
            return this.isoDate.getLong(var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   private long getProlepticMonth() {
      return (long)this.getProlepticYear() * 12L + (long)this.isoDate.getMonthValue() - 1L;
   }

   private int getProlepticYear() {
      return this.isoDate.getYear() + 543;
   }

   public ThaiBuddhistDate with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         if (this.getLong(var4) == var2) {
            return this;
         } else {
            switch(var4) {
            case YEAR_OF_ERA:
            case YEAR:
            case ERA:
               int var5 = this.getChronology().range(var4).checkValidIntValue(var2, var4);
               switch(var4) {
               case YEAR_OF_ERA:
                  return this.with(this.isoDate.withYear((this.getProlepticYear() >= 1 ? var5 : 1 - var5) - 543));
               case PROLEPTIC_MONTH:
               default:
                  break;
               case YEAR:
                  return this.with(this.isoDate.withYear(var5 - 543));
               case ERA:
                  return this.with(this.isoDate.withYear(1 - this.getProlepticYear() - 543));
               }
            default:
               return this.with(this.isoDate.with(var1, var2));
            case PROLEPTIC_MONTH:
               this.getChronology().range(var4).checkValidValue(var2, var4);
               return this.plusMonths(var2 - this.getProlepticMonth());
            }
         }
      } else {
         return (ThaiBuddhistDate)super.with(var1, var2);
      }
   }

   public ThaiBuddhistDate with(TemporalAdjuster var1) {
      return (ThaiBuddhistDate)super.with(var1);
   }

   public ThaiBuddhistDate plus(TemporalAmount var1) {
      return (ThaiBuddhistDate)super.plus(var1);
   }

   public ThaiBuddhistDate minus(TemporalAmount var1) {
      return (ThaiBuddhistDate)super.minus(var1);
   }

   ThaiBuddhistDate plusYears(long var1) {
      return this.with(this.isoDate.plusYears(var1));
   }

   ThaiBuddhistDate plusMonths(long var1) {
      return this.with(this.isoDate.plusMonths(var1));
   }

   ThaiBuddhistDate plusWeeks(long var1) {
      return (ThaiBuddhistDate)super.plusWeeks(var1);
   }

   ThaiBuddhistDate plusDays(long var1) {
      return this.with(this.isoDate.plusDays(var1));
   }

   public ThaiBuddhistDate plus(long var1, TemporalUnit var3) {
      return (ThaiBuddhistDate)super.plus(var1, var3);
   }

   public ThaiBuddhistDate minus(long var1, TemporalUnit var3) {
      return (ThaiBuddhistDate)super.minus(var1, var3);
   }

   ThaiBuddhistDate minusYears(long var1) {
      return (ThaiBuddhistDate)super.minusYears(var1);
   }

   ThaiBuddhistDate minusMonths(long var1) {
      return (ThaiBuddhistDate)super.minusMonths(var1);
   }

   ThaiBuddhistDate minusWeeks(long var1) {
      return (ThaiBuddhistDate)super.minusWeeks(var1);
   }

   ThaiBuddhistDate minusDays(long var1) {
      return (ThaiBuddhistDate)super.minusDays(var1);
   }

   private ThaiBuddhistDate with(LocalDate var1) {
      return var1.equals(this.isoDate) ? this : new ThaiBuddhistDate(var1);
   }

   public final ChronoLocalDateTime<ThaiBuddhistDate> atTime(LocalTime var1) {
      return super.atTime(var1);
   }

   public ChronoPeriod until(ChronoLocalDate var1) {
      Period var2 = this.isoDate.until(var1);
      return this.getChronology().period(var2.getYears(), var2.getMonths(), var2.getDays());
   }

   public long toEpochDay() {
      return this.isoDate.toEpochDay();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ThaiBuddhistDate) {
         ThaiBuddhistDate var2 = (ThaiBuddhistDate)var1;
         return this.isoDate.equals(var2.isoDate);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.getChronology().getId().hashCode() ^ this.isoDate.hashCode();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   private Object writeReplace() {
      return new Ser((byte)8, this);
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeInt(this.get(ChronoField.YEAR));
      var1.writeByte(this.get(ChronoField.MONTH_OF_YEAR));
      var1.writeByte(this.get(ChronoField.DAY_OF_MONTH));
   }

   static ThaiBuddhistDate readExternal(DataInput var0) throws IOException {
      int var1 = var0.readInt();
      byte var2 = var0.readByte();
      byte var3 = var0.readByte();
      return ThaiBuddhistChronology.INSTANCE.date(var1, var2, var3);
   }
}
