package java.time.chrono;

import java.io.Serializable;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;

abstract class ChronoLocalDateImpl<D extends ChronoLocalDate> implements ChronoLocalDate, Temporal, TemporalAdjuster, Serializable {
   private static final long serialVersionUID = 6282433883239719096L;

   static <D extends ChronoLocalDate> D ensureValid(Chronology var0, Temporal var1) {
      ChronoLocalDate var2 = (ChronoLocalDate)var1;
      if (!var0.equals(var2.getChronology())) {
         throw new ClassCastException("Chronology mismatch, expected: " + var0.getId() + ", actual: " + var2.getChronology().getId());
      } else {
         return var2;
      }
   }

   public D with(TemporalAdjuster var1) {
      return ChronoLocalDate.super.with(var1);
   }

   public D with(TemporalField var1, long var2) {
      return ChronoLocalDate.super.with(var1, var2);
   }

   public D plus(TemporalAmount var1) {
      return ChronoLocalDate.super.plus(var1);
   }

   public D plus(long var1, TemporalUnit var3) {
      if (var3 instanceof ChronoUnit) {
         ChronoUnit var4 = (ChronoUnit)var3;
         switch(var4) {
         case DAYS:
            return this.plusDays(var1);
         case WEEKS:
            return this.plusDays(Math.multiplyExact(var1, 7L));
         case MONTHS:
            return this.plusMonths(var1);
         case YEARS:
            return this.plusYears(var1);
         case DECADES:
            return this.plusYears(Math.multiplyExact(var1, 10L));
         case CENTURIES:
            return this.plusYears(Math.multiplyExact(var1, 100L));
         case MILLENNIA:
            return this.plusYears(Math.multiplyExact(var1, 1000L));
         case ERAS:
            return this.with(ChronoField.ERA, Math.addExact(this.getLong(ChronoField.ERA), var1));
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var3);
         }
      } else {
         return ChronoLocalDate.super.plus(var1, var3);
      }
   }

   public D minus(TemporalAmount var1) {
      return ChronoLocalDate.super.minus(var1);
   }

   public D minus(long var1, TemporalUnit var3) {
      return ChronoLocalDate.super.minus(var1, var3);
   }

   abstract D plusYears(long var1);

   abstract D plusMonths(long var1);

   D plusWeeks(long var1) {
      return this.plusDays(Math.multiplyExact(var1, 7L));
   }

   abstract D plusDays(long var1);

   D minusYears(long var1) {
      return var1 == Long.MIN_VALUE ? ((ChronoLocalDateImpl)this.plusYears(Long.MAX_VALUE)).plusYears(1L) : this.plusYears(-var1);
   }

   D minusMonths(long var1) {
      return var1 == Long.MIN_VALUE ? ((ChronoLocalDateImpl)this.plusMonths(Long.MAX_VALUE)).plusMonths(1L) : this.plusMonths(-var1);
   }

   D minusWeeks(long var1) {
      return var1 == Long.MIN_VALUE ? ((ChronoLocalDateImpl)this.plusWeeks(Long.MAX_VALUE)).plusWeeks(1L) : this.plusWeeks(-var1);
   }

   D minusDays(long var1) {
      return var1 == Long.MIN_VALUE ? ((ChronoLocalDateImpl)this.plusDays(Long.MAX_VALUE)).plusDays(1L) : this.plusDays(-var1);
   }

   public long until(Temporal var1, TemporalUnit var2) {
      Objects.requireNonNull(var1, (String)"endExclusive");
      ChronoLocalDate var3 = this.getChronology().date(var1);
      if (var2 instanceof ChronoUnit) {
         switch((ChronoUnit)var2) {
         case DAYS:
            return this.daysUntil(var3);
         case WEEKS:
            return this.daysUntil(var3) / 7L;
         case MONTHS:
            return this.monthsUntil(var3);
         case YEARS:
            return this.monthsUntil(var3) / 12L;
         case DECADES:
            return this.monthsUntil(var3) / 120L;
         case CENTURIES:
            return this.monthsUntil(var3) / 1200L;
         case MILLENNIA:
            return this.monthsUntil(var3) / 12000L;
         case ERAS:
            return var3.getLong(ChronoField.ERA) - this.getLong(ChronoField.ERA);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported unit: " + var2);
         }
      } else {
         Objects.requireNonNull(var2, (String)"unit");
         return var2.between(this, var3);
      }
   }

   private long daysUntil(ChronoLocalDate var1) {
      return var1.toEpochDay() - this.toEpochDay();
   }

   private long monthsUntil(ChronoLocalDate var1) {
      ValueRange var2 = this.getChronology().range(ChronoField.MONTH_OF_YEAR);
      if (var2.getMaximum() != 12L) {
         throw new IllegalStateException("ChronoLocalDateImpl only supports Chronologies with 12 months per year");
      } else {
         long var3 = this.getLong(ChronoField.PROLEPTIC_MONTH) * 32L + (long)this.get(ChronoField.DAY_OF_MONTH);
         long var5 = var1.getLong(ChronoField.PROLEPTIC_MONTH) * 32L + (long)var1.get(ChronoField.DAY_OF_MONTH);
         return (var5 - var3) / 32L;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof ChronoLocalDate) {
         return this.compareTo((ChronoLocalDate)var1) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      long var1 = this.toEpochDay();
      return this.getChronology().hashCode() ^ (int)(var1 ^ var1 >>> 32);
   }

   public String toString() {
      long var1 = this.getLong(ChronoField.YEAR_OF_ERA);
      long var3 = this.getLong(ChronoField.MONTH_OF_YEAR);
      long var5 = this.getLong(ChronoField.DAY_OF_MONTH);
      StringBuilder var7 = new StringBuilder(30);
      var7.append(this.getChronology().toString()).append(" ").append((Object)this.getEra()).append(" ").append(var1).append(var3 < 10L ? "-0" : "-").append(var3).append(var5 < 10L ? "-0" : "-").append(var5);
      return var7.toString();
   }
}
