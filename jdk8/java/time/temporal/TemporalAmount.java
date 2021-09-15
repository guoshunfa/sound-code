package java.time.temporal;

import java.util.List;

public interface TemporalAmount {
   long get(TemporalUnit var1);

   List<TemporalUnit> getUnits();

   Temporal addTo(Temporal var1);

   Temporal subtractFrom(Temporal var1);
}
