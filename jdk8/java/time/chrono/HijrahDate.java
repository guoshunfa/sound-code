package java.time.chrono;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;

public final class HijrahDate extends ChronoLocalDateImpl<HijrahDate> implements ChronoLocalDate, Serializable {
   private static final long serialVersionUID = -5207853542612002020L;
   private final transient HijrahChronology chrono;
   private final transient int prolepticYear;
   private final transient int monthOfYear;
   private final transient int dayOfMonth;

   static HijrahDate of(HijrahChronology var0, int var1, int var2, int var3) {
      return new HijrahDate(var0, var1, var2, var3);
   }

   static HijrahDate ofEpochDay(HijrahChronology var0, long var1) {
      return new HijrahDate(var0, var1);
   }

   public static HijrahDate now() {
      return now(Clock.systemDefaultZone());
   }

   public static HijrahDate now(ZoneId var0) {
      return now(Clock.system(var0));
   }

   public static HijrahDate now(Clock var0) {
      return ofEpochDay(HijrahChronology.INSTANCE, LocalDate.now(var0).toEpochDay());
   }

   public static HijrahDate of(int var0, int var1, int var2) {
      return HijrahChronology.INSTANCE.date(var0, var1, var2);
   }

   public static HijrahDate from(TemporalAccessor var0) {
      return HijrahChronology.INSTANCE.date(var0);
   }

   private HijrahDate(HijrahChronology var1, int var2, int var3, int var4) {
      var1.getEpochDay(var2, var3, var4);
      this.chrono = var1;
      this.prolepticYear = var2;
      this.monthOfYear = var3;
      this.dayOfMonth = var4;
   }

   private HijrahDate(HijrahChronology var1, long var2) {
      int[] var4 = var1.getHijrahDateInfo((int)var2);
      this.chrono = var1;
      this.prolepticYear = var4[0];
      this.monthOfYear = var4[1];
      this.dayOfMonth = var4[2];
   }

   public HijrahChronology getChronology() {
      return this.chrono;
   }

   public HijrahEra getEra() {
      return HijrahEra.AH;
   }

   public int lengthOfMonth() {
      return this.chrono.getMonthLength(this.prolepticYear, this.monthOfYear);
   }

   public int lengthOfYear() {
      return this.chrono.getYearLength(this.prolepticYear);
   }

   public ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         if (this.isSupported(var1)) {
            ChronoField var2 = (ChronoField)var1;
            switch(var2) {
            case DAY_OF_MONTH:
               return ValueRange.of(1L, (long)this.lengthOfMonth());
            case DAY_OF_YEAR:
               return ValueRange.of(1L, (long)this.lengthOfYear());
            case ALIGNED_WEEK_OF_MONTH:
               return ValueRange.of(1L, 5L);
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
         case DAY_OF_MONTH:
            return (long)this.dayOfMonth;
         case DAY_OF_YEAR:
            return (long)this.getDayOfYear();
         case ALIGNED_WEEK_OF_MONTH:
            return (long)((this.dayOfMonth - 1) / 7 + 1);
         case DAY_OF_WEEK:
            return (long)this.getDayOfWeek();
         case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            return (long)((this.getDayOfWeek() - 1) % 7 + 1);
         case ALIGNED_DAY_OF_WEEK_IN_YEAR:
            return (long)((this.getDayOfYear() - 1) % 7 + 1);
         case EPOCH_DAY:
            return this.toEpochDay();
         case ALIGNED_WEEK_OF_YEAR:
            return (long)((this.getDayOfYear() - 1) / 7 + 1);
         case MONTH_OF_YEAR:
            return (long)this.monthOfYear;
         case PROLEPTIC_MONTH:
            return this.getProlepticMonth();
         case YEAR_OF_ERA:
            return (long)this.prolepticYear;
         case YEAR:
            return (long)this.prolepticYear;
         case ERA:
            return (long)this.getEraValue();
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return var1.getFrom(this);
      }
   }

   private long getProlepticMonth() {
      return (long)this.prolepticYear * 12L + (long)this.monthOfYear - 1L;
   }

   public HijrahDate with(TemporalField var1, long var2) {
      if (var1 instanceof ChronoField) {
         ChronoField var4 = (ChronoField)var1;
         this.chrono.range(var4).checkValidValue(var2, var4);
         int var5 = (int)var2;
         switch(var4) {
         case DAY_OF_MONTH:
            return this.resolvePreviousValid(this.prolepticYear, this.monthOfYear, var5);
         case DAY_OF_YEAR:
            return this.plusDays((long)(Math.min(var5, this.lengthOfYear()) - this.getDayOfYear()));
         case ALIGNED_WEEK_OF_MONTH:
            return this.plusDays((var2 - this.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH)) * 7L);
         case DAY_OF_WEEK:
            return this.plusDays(var2 - (long)this.getDayOfWeek());
         case ALIGNED_DAY_OF_WEEK_IN_MONTH:
            return this.plusDays(var2 - this.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
         case ALIGNED_DAY_OF_WEEK_IN_YEAR:
            return this.plusDays(var2 - this.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
         case EPOCH_DAY:
            return new HijrahDate(this.chrono, var2);
         case ALIGNED_WEEK_OF_YEAR:
            return this.plusDays((var2 - this.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR)) * 7L);
         case MONTH_OF_YEAR:
            return this.resolvePreviousValid(this.prolepticYear, var5, this.dayOfMonth);
         case PROLEPTIC_MONTH:
            return this.plusMonths(var2 - this.getProlepticMonth());
         case YEAR_OF_ERA:
            return this.resolvePreviousValid(this.prolepticYear >= 1 ? var5 : 1 - var5, this.monthOfYear, this.dayOfMonth);
         case YEAR:
            return this.resolvePreviousValid(var5, this.monthOfYear, this.dayOfMonth);
         case ERA:
            return this.resolvePreviousValid(1 - this.prolepticYear, this.monthOfYear, this.dayOfMonth);
         default:
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         return (HijrahDate)super.with(var1, var2);
      }
   }

   private HijrahDate resolvePreviousValid(int var1, int var2, int var3) {
      int var4 = this.chrono.getMonthLength(var1, var2);
      if (var3 > var4) {
         var3 = var4;
      }

      return of(this.chrono, var1, var2, var3);
   }

   public HijrahDate with(TemporalAdjuster var1) {
      return (HijrahDate)super.with(var1);
   }

   public HijrahDate withVariant(HijrahChronology var1) {
      if (this.chrono == var1) {
         return this;
      } else {
         int var2 = var1.getDayOfYear(this.prolepticYear, this.monthOfYear);
         return of(var1, this.prolepticYear, this.monthOfYear, this.dayOfMonth > var2 ? var2 : this.dayOfMonth);
      }
   }

   public HijrahDate plus(TemporalAmount var1) {
      return (HijrahDate)super.plus(var1);
   }

   public HijrahDate minus(TemporalAmount var1) {
      return (HijrahDate)super.minus(var1);
   }

   public long toEpochDay() {
      return this.chrono.getEpochDay(this.prolepticYear, this.monthOfYear, this.dayOfMonth);
   }

   private int getDayOfYear() {
      return this.chrono.getDayOfYear(this.prolepticYear, this.monthOfYear) + this.dayOfMonth;
   }

   private int getDayOfWeek() {
      int var1 = (int)Math.floorMod(this.toEpochDay() + 3L, 7L);
      return var1 + 1;
   }

   private int getEraValue() {
      return this.prolepticYear > 1 ? 1 : 0;
   }

   public boolean isLeapYear() {
      return this.chrono.isLeapYear((long)this.prolepticYear);
   }

   HijrahDate plusYears(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         int var3 = Math.addExact(this.prolepticYear, (int)var1);
         return this.resolvePreviousValid(var3, this.monthOfYear, this.dayOfMonth);
      }
   }

   HijrahDate plusMonths(long var1) {
      if (var1 == 0L) {
         return this;
      } else {
         long var3 = (long)this.prolepticYear * 12L + (long)(this.monthOfYear - 1);
         long var5 = var3 + var1;
         int var7 = this.chrono.checkValidYear(Math.floorDiv(var5, 12L));
         int var8 = (int)Math.floorMod(var5, 12L) + 1;
         return this.resolvePreviousValid(var7, var8, this.dayOfMonth);
      }
   }

   HijrahDate plusWeeks(long var1) {
      return (HijrahDate)super.plusWeeks(var1);
   }

   HijrahDate plusDays(long var1) {
      return new HijrahDate(this.chrono, this.toEpochDay() + var1);
   }

   public HijrahDate plus(long var1, TemporalUnit var3) {
      return (HijrahDate)super.plus(var1, var3);
   }

   public HijrahDate minus(long var1, TemporalUnit var3) {
      return (HijrahDate)super.minus(var1, var3);
   }

   HijrahDate minusYears(long var1) {
      return (HijrahDate)super.minusYears(var1);
   }

   HijrahDate minusMonths(long var1) {
      return (HijrahDate)super.minusMonths(var1);
   }

   HijrahDate minusWeeks(long var1) {
      return (HijrahDate)super.minusWeeks(var1);
   }

   HijrahDate minusDays(long var1) {
      return (HijrahDate)super.minusDays(var1);
   }

   public final ChronoLocalDateTime<HijrahDate> atTime(LocalTime var1) {
      return super.atTime(var1);
   }

   public ChronoPeriod until(ChronoLocalDate var1) {
      HijrahDate var2 = this.getChronology().date(var1);
      long var3 = (long)((var2.prolepticYear - this.prolepticYear) * 12 + (var2.monthOfYear - this.monthOfYear));
      int var5 = var2.dayOfMonth - this.dayOfMonth;
      if (var3 > 0L && var5 < 0) {
         --var3;
         HijrahDate var6 = this.plusMonths(var3);
         var5 = (int)(var2.toEpochDay() - var6.toEpochDay());
      } else if (var3 < 0L && var5 > 0) {
         ++var3;
         var5 -= var2.lengthOfMonth();
      }

      long var9 = var3 / 12L;
      int var8 = (int)(var3 % 12L);
      return this.getChronology().period(Math.toIntExact(var9), var8, var5);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HijrahDate)) {
         return false;
      } else {
         HijrahDate var2 = (HijrahDate)var1;
         return this.prolepticYear == var2.prolepticYear && this.monthOfYear == var2.monthOfYear && this.dayOfMonth == var2.dayOfMonth && this.getChronology().equals(var2.getChronology());
      }
   }

   public int hashCode() {
      int var1 = this.prolepticYear;
      int var2 = this.monthOfYear;
      int var3 = this.dayOfMonth;
      return this.getChronology().getId().hashCode() ^ var1 & -2048 ^ (var1 << 11) + (var2 << 6) + var3;
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   private Object writeReplace() {
      return new Ser((byte)6, this);
   }

   void writeExternal(ObjectOutput var1) throws IOException {
      var1.writeObject(this.getChronology());
      var1.writeInt(this.get(ChronoField.YEAR));
      var1.writeByte(this.get(ChronoField.MONTH_OF_YEAR));
      var1.writeByte(this.get(ChronoField.DAY_OF_MONTH));
   }

   static HijrahDate readExternal(ObjectInput var0) throws IOException, ClassNotFoundException {
      HijrahChronology var1 = (HijrahChronology)var0.readObject();
      int var2 = var0.readInt();
      byte var3 = var0.readByte();
      byte var4 = var0.readByte();
      return var1.date(var2, var3, var4);
   }
}
