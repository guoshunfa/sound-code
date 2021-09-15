package java.time.temporal;

import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public interface TemporalField {
   default String getDisplayName(Locale var1) {
      Objects.requireNonNull(var1, (String)"locale");
      return this.toString();
   }

   TemporalUnit getBaseUnit();

   TemporalUnit getRangeUnit();

   ValueRange range();

   boolean isDateBased();

   boolean isTimeBased();

   boolean isSupportedBy(TemporalAccessor var1);

   ValueRange rangeRefinedBy(TemporalAccessor var1);

   long getFrom(TemporalAccessor var1);

   <R extends Temporal> R adjustInto(R var1, long var2);

   default TemporalAccessor resolve(Map<TemporalField, Long> var1, TemporalAccessor var2, ResolverStyle var3) {
      return null;
   }

   String toString();
}
