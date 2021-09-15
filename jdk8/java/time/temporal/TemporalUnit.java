package java.time.temporal;

import java.time.Duration;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;

public interface TemporalUnit {
   Duration getDuration();

   boolean isDurationEstimated();

   boolean isDateBased();

   boolean isTimeBased();

   default boolean isSupportedBy(Temporal var1) {
      if (var1 instanceof LocalTime) {
         return this.isTimeBased();
      } else if (var1 instanceof ChronoLocalDate) {
         return this.isDateBased();
      } else if (!(var1 instanceof ChronoLocalDateTime) && !(var1 instanceof ChronoZonedDateTime)) {
         try {
            var1.plus(1L, this);
            return true;
         } catch (UnsupportedTemporalTypeException var5) {
            return false;
         } catch (RuntimeException var6) {
            try {
               var1.plus(-1L, this);
               return true;
            } catch (RuntimeException var4) {
               return false;
            }
         }
      } else {
         return true;
      }
   }

   <R extends Temporal> R addTo(R var1, long var2);

   long between(Temporal var1, Temporal var2);

   String toString();
}
