package java.time.temporal;

public interface Temporal extends TemporalAccessor {
   boolean isSupported(TemporalUnit var1);

   default Temporal with(TemporalAdjuster var1) {
      return var1.adjustInto(this);
   }

   Temporal with(TemporalField var1, long var2);

   default Temporal plus(TemporalAmount var1) {
      return var1.addTo(this);
   }

   Temporal plus(long var1, TemporalUnit var3);

   default Temporal minus(TemporalAmount var1) {
      return var1.subtractFrom(this);
   }

   default Temporal minus(long var1, TemporalUnit var3) {
      return var1 == Long.MIN_VALUE ? this.plus(Long.MAX_VALUE, var3).plus(1L, var3) : this.plus(-var1, var3);
   }

   long until(Temporal var1, TemporalUnit var2);
}
