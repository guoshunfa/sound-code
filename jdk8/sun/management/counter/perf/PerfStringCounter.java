package sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import sun.management.counter.StringCounter;
import sun.management.counter.Units;
import sun.management.counter.Variability;

public class PerfStringCounter extends PerfByteArrayCounter implements StringCounter {
   private static Charset defaultCharset = Charset.defaultCharset();
   private static final long serialVersionUID = 6802913433363692452L;

   PerfStringCounter(String var1, Variability var2, int var3, ByteBuffer var4) {
      this(var1, var2, var3, var4.limit(), var4);
   }

   PerfStringCounter(String var1, Variability var2, int var3, int var4, ByteBuffer var5) {
      super(var1, Units.STRING, var2, var3, var4, var5);
   }

   public boolean isVector() {
      return false;
   }

   public int getVectorLength() {
      return 0;
   }

   public Object getValue() {
      return this.stringValue();
   }

   public String stringValue() {
      String var1 = "";
      byte[] var2 = this.byteArrayValue();
      if (var2 != null && var2.length > 1) {
         int var3;
         for(var3 = 0; var3 < var2.length && var2[var3] != 0; ++var3) {
         }

         return new String(var2, 0, var3, defaultCharset);
      } else {
         return var1;
      }
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new StringCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.stringValue());
   }
}
