package sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.LongBuffer;
import sun.management.counter.AbstractCounter;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfLongArrayCounter extends AbstractCounter implements LongArrayCounter {
   LongBuffer lb;
   private static final long serialVersionUID = -2733617913045487126L;

   PerfLongArrayCounter(String var1, Units var2, Variability var3, int var4, int var5, LongBuffer var6) {
      super(var1, var2, var3, var4, var5);
      this.lb = var6;
   }

   public Object getValue() {
      return this.longArrayValue();
   }

   public long[] longArrayValue() {
      this.lb.position(0);
      long[] var1 = new long[this.lb.limit()];
      this.lb.get(var1);
      return var1;
   }

   public long longAt(int var1) {
      this.lb.position(var1);
      return this.lb.get();
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new LongArrayCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.getVectorLength(), this.longArrayValue());
   }
}
