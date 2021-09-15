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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ThaiBuddhistChronology extends AbstractChronology implements Serializable {
   public static final ThaiBuddhistChronology INSTANCE = new ThaiBuddhistChronology();
   private static final long serialVersionUID = 2775954514031616474L;
   static final int YEARS_DIFFERENCE = 543;
   private static final HashMap<String, String[]> ERA_NARROW_NAMES = new HashMap();
   private static final HashMap<String, String[]> ERA_SHORT_NAMES = new HashMap();
   private static final HashMap<String, String[]> ERA_FULL_NAMES = new HashMap();
   private static final String FALLBACK_LANGUAGE = "en";
   private static final String TARGET_LANGUAGE = "th";

   private ThaiBuddhistChronology() {
   }

   public String getId() {
      return "ThaiBuddhist";
   }

   public String getCalendarType() {
      return "buddhist";
   }

   public ThaiBuddhistDate date(Era var1, int var2, int var3, int var4) {
      return this.date(this.prolepticYear(var1, var2), var3, var4);
   }

   public ThaiBuddhistDate date(int var1, int var2, int var3) {
      return new ThaiBuddhistDate(LocalDate.of(var1 - 543, var2, var3));
   }

   public ThaiBuddhistDate dateYearDay(Era var1, int var2, int var3) {
      return this.dateYearDay(this.prolepticYear(var1, var2), var3);
   }

   public ThaiBuddhistDate dateYearDay(int var1, int var2) {
      return new ThaiBuddhistDate(LocalDate.ofYearDay(var1 - 543, var2));
   }

   public ThaiBuddhistDate dateEpochDay(long var1) {
      return new ThaiBuddhistDate(LocalDate.ofEpochDay(var1));
   }

   public ThaiBuddhistDate dateNow() {
      return this.dateNow(Clock.systemDefaultZone());
   }

   public ThaiBuddhistDate dateNow(ZoneId var1) {
      return this.dateNow(Clock.system(var1));
   }

   public ThaiBuddhistDate dateNow(Clock var1) {
      return this.date(LocalDate.now(var1));
   }

   public ThaiBuddhistDate date(TemporalAccessor var1) {
      return var1 instanceof ThaiBuddhistDate ? (ThaiBuddhistDate)var1 : new ThaiBuddhistDate(LocalDate.from(var1));
   }

   public ChronoLocalDateTime<ThaiBuddhistDate> localDateTime(TemporalAccessor var1) {
      return super.localDateTime(var1);
   }

   public ChronoZonedDateTime<ThaiBuddhistDate> zonedDateTime(TemporalAccessor var1) {
      return super.zonedDateTime(var1);
   }

   public ChronoZonedDateTime<ThaiBuddhistDate> zonedDateTime(Instant var1, ZoneId var2) {
      return super.zonedDateTime(var1, var2);
   }

   public boolean isLeapYear(long var1) {
      return IsoChronology.INSTANCE.isLeapYear(var1 - 543L);
   }

   public int prolepticYear(Era var1, int var2) {
      if (!(var1 instanceof ThaiBuddhistEra)) {
         throw new ClassCastException("Era must be BuddhistEra");
      } else {
         return var1 == ThaiBuddhistEra.BE ? var2 : 1 - var2;
      }
   }

   public ThaiBuddhistEra eraOf(int var1) {
      return ThaiBuddhistEra.of(var1);
   }

   public List<Era> eras() {
      return Arrays.asList(ThaiBuddhistEra.values());
   }

   public ValueRange range(ChronoField var1) {
      ValueRange var2;
      switch(var1) {
      case PROLEPTIC_MONTH:
         var2 = ChronoField.PROLEPTIC_MONTH.range();
         return ValueRange.of(var2.getMinimum() + 6516L, var2.getMaximum() + 6516L);
      case YEAR_OF_ERA:
         var2 = ChronoField.YEAR.range();
         return ValueRange.of(1L, -(var2.getMinimum() + 543L) + 1L, var2.getMaximum() + 543L);
      case YEAR:
         var2 = ChronoField.YEAR.range();
         return ValueRange.of(var2.getMinimum() + 543L, var2.getMaximum() + 543L);
      default:
         return var1.range();
      }
   }

   public ThaiBuddhistDate resolveDate(Map<TemporalField, Long> var1, ResolverStyle var2) {
      return (ThaiBuddhistDate)super.resolveDate(var1, var2);
   }

   Object writeReplace() {
      return super.writeReplace();
   }

   private void readObject(ObjectInputStream var1) throws InvalidObjectException {
      throw new InvalidObjectException("Deserialization via serialization delegate");
   }

   static {
      ERA_NARROW_NAMES.put("en", new String[]{"BB", "BE"});
      ERA_NARROW_NAMES.put("th", new String[]{"BB", "BE"});
      ERA_SHORT_NAMES.put("en", new String[]{"B.B.", "B.E."});
      ERA_SHORT_NAMES.put("th", new String[]{"พ.ศ.", "ปีก่อนคริสต์กาลที่"});
      ERA_FULL_NAMES.put("en", new String[]{"Before Buddhist", "Budhhist Era"});
      ERA_FULL_NAMES.put("th", new String[]{"พุทธศักราช", "ปีก่อนคริสต์กาลที่"});
   }
}
