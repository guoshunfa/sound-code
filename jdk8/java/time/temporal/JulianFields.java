package java.time.temporal;

import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.ResolverStyle;
import java.util.Map;

public final class JulianFields {
   private static final long JULIAN_DAY_OFFSET = 2440588L;
   public static final TemporalField JULIAN_DAY;
   public static final TemporalField MODIFIED_JULIAN_DAY;
   public static final TemporalField RATA_DIE;

   private JulianFields() {
      throw new AssertionError("Not instantiable");
   }

   static {
      JULIAN_DAY = JulianFields.Field.JULIAN_DAY;
      MODIFIED_JULIAN_DAY = JulianFields.Field.MODIFIED_JULIAN_DAY;
      RATA_DIE = JulianFields.Field.RATA_DIE;
   }

   private static enum Field implements TemporalField {
      JULIAN_DAY("JulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 2440588L),
      MODIFIED_JULIAN_DAY("ModifiedJulianDay", ChronoUnit.DAYS, ChronoUnit.FOREVER, 40587L),
      RATA_DIE("RataDie", ChronoUnit.DAYS, ChronoUnit.FOREVER, 719163L);

      private static final long serialVersionUID = -7501623920830201812L;
      private final transient String name;
      private final transient TemporalUnit baseUnit;
      private final transient TemporalUnit rangeUnit;
      private final transient ValueRange range;
      private final transient long offset;

      private Field(String var3, TemporalUnit var4, TemporalUnit var5, long var6) {
         this.name = var3;
         this.baseUnit = var4;
         this.rangeUnit = var5;
         this.range = ValueRange.of(-365243219162L + var6, 365241780471L + var6);
         this.offset = var6;
      }

      public TemporalUnit getBaseUnit() {
         return this.baseUnit;
      }

      public TemporalUnit getRangeUnit() {
         return this.rangeUnit;
      }

      public boolean isDateBased() {
         return true;
      }

      public boolean isTimeBased() {
         return false;
      }

      public ValueRange range() {
         return this.range;
      }

      public boolean isSupportedBy(TemporalAccessor var1) {
         return var1.isSupported(ChronoField.EPOCH_DAY);
      }

      public ValueRange rangeRefinedBy(TemporalAccessor var1) {
         if (!this.isSupportedBy(var1)) {
            throw new DateTimeException("Unsupported field: " + this);
         } else {
            return this.range();
         }
      }

      public long getFrom(TemporalAccessor var1) {
         return var1.getLong(ChronoField.EPOCH_DAY) + this.offset;
      }

      public <R extends Temporal> R adjustInto(R var1, long var2) {
         if (!this.range().isValidValue(var2)) {
            throw new DateTimeException("Invalid value: " + this.name + " " + var2);
         } else {
            return var1.with(ChronoField.EPOCH_DAY, Math.subtractExact(var2, this.offset));
         }
      }

      public ChronoLocalDate resolve(Map<TemporalField, Long> var1, TemporalAccessor var2, ResolverStyle var3) {
         long var4 = (Long)var1.remove(this);
         Chronology var6 = Chronology.from(var2);
         if (var3 == ResolverStyle.LENIENT) {
            return var6.dateEpochDay(Math.subtractExact(var4, this.offset));
         } else {
            this.range().checkValidValue(var4, this);
            return var6.dateEpochDay(var4 - this.offset);
         }
      }

      public String toString() {
         return this.name;
      }
   }
}
