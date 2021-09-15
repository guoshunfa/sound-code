package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.StringCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class StringCounterSnapshot extends AbstractCounter implements StringCounter {
   String value;
   private static final long serialVersionUID = 1132921539085572034L;

   StringCounterSnapshot(String var1, Units var2, Variability var3, int var4, String var5) {
      super(var1, var2, var3, var4);
      this.value = var5;
   }

   public Object getValue() {
      return this.value;
   }

   public String stringValue() {
      return this.value;
   }
}
