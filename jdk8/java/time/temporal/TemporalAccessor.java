package java.time.temporal;

import java.time.DateTimeException;
import java.util.Objects;

public interface TemporalAccessor {
   boolean isSupported(TemporalField var1);

   default ValueRange range(TemporalField var1) {
      if (var1 instanceof ChronoField) {
         if (this.isSupported(var1)) {
            return var1.range();
         } else {
            throw new UnsupportedTemporalTypeException("Unsupported field: " + var1);
         }
      } else {
         Objects.requireNonNull(var1, (String)"field");
         return var1.rangeRefinedBy(this);
      }
   }

   default int get(TemporalField var1) {
      ValueRange var2 = this.range(var1);
      if (!var2.isIntValue()) {
         throw new UnsupportedTemporalTypeException("Invalid field " + var1 + " for get() method, use getLong() instead");
      } else {
         long var3 = this.getLong(var1);
         if (!var2.isValidValue(var3)) {
            throw new DateTimeException("Invalid value for " + var1 + " (valid values " + var2 + "): " + var3);
         } else {
            return (int)var3;
         }
      }
   }

   long getLong(TemporalField var1);

   default <R> R query(TemporalQuery<R> var1) {
      return var1 != TemporalQueries.zoneId() && var1 != TemporalQueries.chronology() && var1 != TemporalQueries.precision() ? var1.queryFrom(this) : null;
   }
}
