package java.time.chrono;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public interface ChronoPeriod extends TemporalAmount {
   static ChronoPeriod between(ChronoLocalDate var0, ChronoLocalDate var1) {
      Objects.requireNonNull(var0, (String)"startDateInclusive");
      Objects.requireNonNull(var1, (String)"endDateExclusive");
      return var0.until(var1);
   }

   long get(TemporalUnit var1);

   List<TemporalUnit> getUnits();

   Chronology getChronology();

   default boolean isZero() {
      Iterator var1 = this.getUnits().iterator();

      TemporalUnit var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (TemporalUnit)var1.next();
      } while(this.get(var2) == 0L);

      return false;
   }

   default boolean isNegative() {
      Iterator var1 = this.getUnits().iterator();

      TemporalUnit var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (TemporalUnit)var1.next();
      } while(this.get(var2) >= 0L);

      return true;
   }

   ChronoPeriod plus(TemporalAmount var1);

   ChronoPeriod minus(TemporalAmount var1);

   ChronoPeriod multipliedBy(int var1);

   default ChronoPeriod negated() {
      return this.multipliedBy(-1);
   }

   ChronoPeriod normalized();

   Temporal addTo(Temporal var1);

   Temporal subtractFrom(Temporal var1);

   boolean equals(Object var1);

   int hashCode();

   String toString();
}
