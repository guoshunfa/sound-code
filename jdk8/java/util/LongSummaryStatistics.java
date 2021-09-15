package java.util;

import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class LongSummaryStatistics implements LongConsumer, IntConsumer {
   private long count;
   private long sum;
   private long min = Long.MAX_VALUE;
   private long max = Long.MIN_VALUE;

   public void accept(int var1) {
      this.accept((long)var1);
   }

   public void accept(long var1) {
      ++this.count;
      this.sum += var1;
      this.min = Math.min(this.min, var1);
      this.max = Math.max(this.max, var1);
   }

   public void combine(LongSummaryStatistics var1) {
      this.count += var1.count;
      this.sum += var1.sum;
      this.min = Math.min(this.min, var1.min);
      this.max = Math.max(this.max, var1.max);
   }

   public final long getCount() {
      return this.count;
   }

   public final long getSum() {
      return this.sum;
   }

   public final long getMin() {
      return this.min;
   }

   public final long getMax() {
      return this.max;
   }

   public final double getAverage() {
      return this.getCount() > 0L ? (double)this.getSum() / (double)this.getCount() : 0.0D;
   }

   public String toString() {
      return String.format("%s{count=%d, sum=%d, min=%d, average=%f, max=%d}", this.getClass().getSimpleName(), this.getCount(), this.getSum(), this.getMin(), this.getAverage(), this.getMax());
   }
}
