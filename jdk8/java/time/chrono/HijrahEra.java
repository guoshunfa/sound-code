package java.time.chrono;

import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;

public enum HijrahEra implements Era {
   AH;

   public static HijrahEra of(int var0) {
      if (var0 == 1) {
         return AH;
      } else {
         throw new DateTimeException("Invalid era: " + var0);
      }
   }

   public int getValue() {
      return 1;
   }

   public ValueRange range(TemporalField var1) {
      return var1 == ChronoField.ERA ? ValueRange.of(1L, 1L) : Era.super.range(var1);
   }
}
