package sun.java2d.xr;

public class XcbRequestCounter {
   private static final long MAX_UINT = 4294967295L;
   long value;

   public XcbRequestCounter(long var1) {
      this.value = var1;
   }

   public void setValue(long var1) {
      this.value = var1;
   }

   public long getValue() {
      return this.value;
   }

   public void add(long var1) {
      this.value += var1;
      if (this.value > 4294967295L) {
         this.value = 0L;
      }

   }
}
