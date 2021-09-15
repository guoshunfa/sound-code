package java.util;

import java.util.function.DoubleConsumer;

public class DoubleSummaryStatistics implements DoubleConsumer {
   private long count;
   private double sum;
   private double sumCompensation;
   private double simpleSum;
   private double min = Double.POSITIVE_INFINITY;
   private double max = Double.NEGATIVE_INFINITY;

   public void accept(double var1) {
      ++this.count;
      this.simpleSum += var1;
      this.sumWithCompensation(var1);
      this.min = Math.min(this.min, var1);
      this.max = Math.max(this.max, var1);
   }

   public void combine(DoubleSummaryStatistics var1) {
      this.count += var1.count;
      this.simpleSum += var1.simpleSum;
      this.sumWithCompensation(var1.sum);
      this.sumWithCompensation(var1.sumCompensation);
      this.min = Math.min(this.min, var1.min);
      this.max = Math.max(this.max, var1.max);
   }

   private void sumWithCompensation(double var1) {
      double var3 = var1 - this.sumCompensation;
      double var5 = this.sum + var3;
      this.sumCompensation = var5 - this.sum - var3;
      this.sum = var5;
   }

   public final long getCount() {
      return this.count;
   }

   public final double getSum() {
      double var1 = this.sum + this.sumCompensation;
      return Double.isNaN(var1) && Double.isInfinite(this.simpleSum) ? this.simpleSum : var1;
   }

   public final double getMin() {
      return this.min;
   }

   public final double getMax() {
      return this.max;
   }

   public final double getAverage() {
      return this.getCount() > 0L ? this.getSum() / (double)this.getCount() : 0.0D;
   }

   public String toString() {
      return String.format("%s{count=%d, sum=%f, min=%f, average=%f, max=%f}", this.getClass().getSimpleName(), this.getCount(), this.getSum(), this.getMin(), this.getAverage(), this.getMax());
   }
}
