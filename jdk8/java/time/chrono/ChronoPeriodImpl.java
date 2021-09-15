package java.time.chrono;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class ChronoPeriodImpl implements ChronoPeriod, Serializable {
   private static final long serialVersionUID = 57387258289L;
   private static final List<TemporalUnit> SUPPORTED_UNITS;
   private final Chronology chrono;
   final int years;
   final int months;
   final int days;

   ChronoPeriodImpl(Chronology var1, int var2, int var3, int var4) {
      Objects.requireNonNull(var1, (String)"chrono");
      this.chrono = var1;
      this.years = var2;
      this.months = var3;
      this.days = var4;
   }

   public long get(TemporalUnit var1) {
      if (var1 == ChronoUnit.YEARS) {
         return (long)this.years;
      } else if (var1 == ChronoUnit.MONTHS) {
         return (long)this.months;
      } else if (var1 == ChronoUnit.DAYS) {
         return (long)this.days;
      } else {
         throw new UnsupportedTemporalTypeException("Unsupported unit: " + var1);
      }
   }

   public List<TemporalUnit> getUnits() {
      return SUPPORTED_UNITS;
   }

   public Chronology getChronology() {
      return this.chrono;
   }

   public boolean isZero() {
      return this.years == 0 && this.months == 0 && this.days == 0;
   }

   public boolean isNegative() {
      return this.years < 0 || this.months < 0 || this.days < 0;
   }

   public ChronoPeriod plus(TemporalAmount var1) {
      ChronoPeriodImpl var2 = this.validateAmount(var1);
      return new ChronoPeriodImpl(this.chrono, Math.addExact(this.years, var2.years), Math.addExact(this.months, var2.months), Math.addExact(this.days, var2.days));
   }

   public ChronoPeriod minus(TemporalAmount var1) {
      ChronoPeriodImpl var2 = this.validateAmount(var1);
      return new ChronoPeriodImpl(this.chrono, Math.subtractExact(this.years, var2.years), Math.subtractExact(this.months, var2.months), Math.subtractExact(this.days, var2.days));
   }

   private ChronoPeriodImpl validateAmount(TemporalAmount var1) {
      Objects.requireNonNull(var1, (String)"amount");
      if (!(var1 instanceof ChronoPeriodImpl)) {
         throw new DateTimeException("Unable to obtain ChronoPeriod from TemporalAmount: " + var1.getClass());
      } else {
         ChronoPeriodImpl var2 = (ChronoPeriodImpl)var1;
         if (!this.chrono.equals(var2.getChronology())) {
            throw new ClassCastException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + var2.getChronology().getId());
         } else {
            return var2;
         }
      }
   }

   public ChronoPeriod multipliedBy(int var1) {
      return !this.isZero() && var1 != 1 ? new ChronoPeriodImpl(this.chrono, Math.multiplyExact(this.years, var1), Math.multiplyExact(this.months, var1), Math.multiplyExact(this.days, var1)) : this;
   }

   public ChronoPeriod normalized() {
      long var1 = this.monthRange();
      if (var1 > 0L) {
         long var3 = (long)this.years * var1 + (long)this.months;
         long var5 = var3 / var1;
         int var7 = (int)(var3 % var1);
         return var5 == (long)this.years && var7 == this.months ? this : new ChronoPeriodImpl(this.chrono, Math.toIntExact(var5), var7, this.days);
      } else {
         return this;
      }
   }

   private long monthRange() {
      ValueRange var1 = this.chrono.range(ChronoField.MONTH_OF_YEAR);
      return var1.isFixed() && var1.isIntValue() ? var1.getMaximum() - var1.getMinimum() + 1L : -1L;
   }

   public Temporal addTo(Temporal var1) {
      this.validateChrono(var1);
      if (this.months == 0) {
         if (this.years != 0) {
            var1 = var1.plus((long)this.years, ChronoUnit.YEARS);
         }
      } else {
         long var2 = this.monthRange();
         if (var2 > 0L) {
            var1 = var1.plus((long)this.years * var2 + (long)this.months, ChronoUnit.MONTHS);
         } else {
            if (this.years != 0) {
               var1 = var1.plus((long)this.years, ChronoUnit.YEARS);
            }

            var1 = var1.plus((long)this.months, ChronoUnit.MONTHS);
         }
      }

      if (this.days != 0) {
         var1 = var1.plus((long)this.days, ChronoUnit.DAYS);
      }

      return var1;
   }

   public Temporal subtractFrom(Temporal var1) {
      this.validateChrono(var1);
      if (this.months == 0) {
         if (this.years != 0) {
            var1 = var1.minus((long)this.years, ChronoUnit.YEARS);
         }
      } else {
         long var2 = this.monthRange();
         if (var2 > 0L) {
            var1 = var1.minus((long)this.years * var2 + (long)this.months, ChronoUnit.MONTHS);
         } else {
            if (this.years != 0) {
               var1 = var1.minus((long)this.years, ChronoUnit.YEARS);
            }

            var1 = var1.minus((long)this.months, ChronoUnit.MONTHS);
         }
      }

      if (this.days != 0) {
         var1 = var1.minus((long)this.days, ChronoUnit.DAYS);
      }

      return var1;
   }

   private void validateChrono(TemporalAccessor var1) {
      Objects.requireNonNull(var1, (String)"temporal");
      Chronology var2 = (Chronology)var1.query(TemporalQueries.chronology());
      if (var2 != null && !this.chrono.equals(var2)) {
         throw new DateTimeException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + var2.getId());
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChronoPeriodImpl)) {
         return false;
      } else {
         ChronoPeriodImpl var2 = (ChronoPeriodImpl)var1;
         return this.years == var2.years && this.months == var2.months && this.days == var2.days && this.chrono.equals(var2.chrono);
      }
   }

   public int hashCode() {
      return this.years + Integer.rotateLeft(this.months, 8) + Integer.rotateLeft(this.days, 16) ^ this.chrono.hashCode();
   }

   public String toString() {
      if (this.isZero()) {
         return this.getChronology().toString() + " P0D";
      } else {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.getChronology().toString()).append(' ').append('P');
         if (this.years != 0) {
            var1.append(this.years).append('Y');
         }

         if (this.months != 0) {
            var1.append(this.months).append('M');
         }

         if (this.days != 0) {
            var1.append(this.days).append('D');
         }

         return var1.toString();
      }
   }

   protected Object writeReplace() {
      return new Ser((byte)9, this);
   }

   private void readObject(ObjectInputStream var1) throws ObjectStreamException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   void writeExternal(DataOutput var1) throws IOException {
      var1.writeUTF(this.chrono.getId());
      var1.writeInt(this.years);
      var1.writeInt(this.months);
      var1.writeInt(this.days);
   }

   static ChronoPeriodImpl readExternal(DataInput var0) throws IOException {
      Chronology var1 = Chronology.of(var0.readUTF());
      int var2 = var0.readInt();
      int var3 = var0.readInt();
      int var4 = var0.readInt();
      return new ChronoPeriodImpl(var1, var2, var3, var4);
   }

   static {
      SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS));
   }
}
