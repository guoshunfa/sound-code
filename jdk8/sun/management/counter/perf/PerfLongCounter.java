package sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.LongBuffer;
import sun.management.counter.AbstractCounter;
import sun.management.counter.LongCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfLongCounter extends AbstractCounter implements LongCounter {
   LongBuffer lb;
   private static final long serialVersionUID = 857711729279242948L;

   PerfLongCounter(String var1, Units var2, Variability var3, int var4, LongBuffer var5) {
      super(var1, var2, var3, var4);
      this.lb = var5;
   }

   public Object getValue() {
      return new Long(this.lb.get(0));
   }

   public long longValue() {
      return this.lb.get(0);
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new LongCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.longValue());
   }
}
