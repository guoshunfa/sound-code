package java.time.chrono;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MinguoChronology extends AbstractChronology implements Serializable {
   public static final MinguoChronology INSTANCE = new MinguoChronology();
   private static final long serialVersionUID = 1039765215346859963L;
   static final int YEARS_DIFFERENCE = 1911;

   private MinguoChronology() {
   }

   public String getId() {
      return "Minguo";
   }

   public String getCalendarType() {
      return "roc";
   }

   public MinguoDate date(Era var1, int var2, int var3, int var4) {
      return this.date(this.prolepticYear(var1, var2), var3, var4);
   }

   public MinguoDate date(int var1, int var2, int var3) {
      return new MinguoDate(LocalDate.of(var1 + 1911, var2, var3));
   }

   public MinguoDate dateYearDay(Era var1, int var2, int var3) {
      return this.dateYearDay(this.prolepticYear(var1, var2), var3);
   }

   public MinguoDate dateYearDay(int var1, int var2) {
      return new MinguoDate(LocalDate.ofYearDay(var1 + 1911, var2));
   }

   public MinguoDate dateEpochDay(long var1) {
      return new MinguoDate(LocalDate.ofEpochDay(var1));
   }

   public MinguoDate dateNow() {
      return this.dateNow(Clock.systemDefaultZone());
   }

   public MinguoDate dateNow(ZoneId var1) {
      return this.dateNow(Clock.system(var1));
   }

   public MinguoDate dateNow(Clock var1) {
      return this.date(LocalDate.now(var1));
   }

   public MinguoDate date(TemporalAccessor var1) {
      return var1 instanceof MinguoDate ? (MinguoDate)var1 : new MinguoDate(LocalDate.from(var1));
   }

   public ChronoLocalDateTime<MinguoDate> localDateTime(TemporalAccessor var1) {
      return super.localDateTime(var1);
   }

   public ChronoZonedDateTime<MinguoDate> zonedDateTime(TemporalAccessor var1) {
      return super.zonedDateTime(var1);
   }

   public ChronoZonedDateTime<MinguoDate> zonedDateTime(Instant var1, ZoneId var2) {
      return super.zonedDateTime(var1, var2);
   }

   public boolean isLeapYear(long var1) {
      return IsoChronology.INSTANCE.isLeapYear(var1 + 1911L);
   }

   public int prolepticYear(Era var1, int var2) {
      if (!(var1 instanceof MinguoEra)) {
         throw new ClassCastException("Era must be MinguoEra");
      } else {
         return var1 == MinguoEra.ROC ? var2 : 1 - var2;
      }
   }

   public MinguoEra eraOf(int var1) {
      return MinguoEra.of(var1);
   }

   public List<Era> eras() {
      return Arrays.asList(MinguoEra.values());
   }

   public ValueRange range(ChronoField var1) {
      ValueRange var2;
      switch(var1) {
      case PROLEPTIC_MONTH:
         var2 = ChronoField.PROLEPTIC_MONTH.range();
         return ValueRange.of(var2.getMinimum() - 22932L, var2.getMaximum() - 22932L);
      case YEAR_OF_ERA:
         var2 = ChronoField.YEAR.range();
         return ValueRange.of(1L, var2.getMaximum() - 1911L, -var2.getMinimum() + 1L + 1911L);
      case YEAR:
         var2 = ChronoField.YEAR.range();
         return ValueRange.of(var2.getMinimum() - 1911L, var2.getMaximum() - 1911L);
      default:
         return var1.range();
      }
   }

   public MinguoDate resolveDate(Map<TemporalField, Long> var1, ResolverStyle var2) {
      return (MinguoDate)super.resolveDate(var1, var2);
   }

   Object writeReplace() {
      return super.writeReplace();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }
}
