package sun.management.counter.perf;

import sun.management.counter.AbstractCounter;
import sun.management.counter.ByteArrayCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

class ByteArrayCounterSnapshot extends AbstractCounter implements ByteArrayCounter {
   byte[] value;
   private static final long serialVersionUID = 1444793459838438979L;

   ByteArrayCounterSnapshot(String var1, Units var2, Variability var3, int var4, int var5, byte[] var6) {
      super(var1, var2, var3, var4, var5);
      this.value = var6;
   }

   public Object getValue() {
      return this.value;
   }

   public byte[] byteArrayValue() {
      return this.value;
   }

   public byte byteAt(int var1) {
      return this.value[var1];
   }
}
