package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.LongArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class LongArrayCounterSnapshot extends AbstractCounter implements LongArrayCounter {
   long[] value;
   private static final long serialVersionUID = 3585870271405924292L;

   LongArrayCounterSnapshot(String var1, Units var2, Variability var3, int var4, int var5, long[] var6) {
      super(var1, var2, var3, var4, var5);
      this.value = var6;
   }

   public Object getValue() {
      return this.value;
   }

   public long[] longArrayValue() {
      return this.value;
   }

   public long longAt(int var1) {
      return this.value[var1];
   }
}
